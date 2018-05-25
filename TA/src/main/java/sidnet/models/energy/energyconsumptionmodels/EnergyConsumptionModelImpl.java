/*
 * EnergyModel.java
 *
 * Created on May 10, 2006, 10:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/* 
 * Implementation details:
 *  At the first sight, it is a cumbersome way of computing the energyLevel. In a nutshell, during simulation time we record the total amount of time a functional entity
 *  (Radio, CPU or sensor) is staying in a given state, and the actual energyLevel is computed upon request by subtracting from the battery capacity the costs associated
 *  with each state. 
 *
 */

package sidnet.models.energy.energyconsumptionmodels;

import sidnet.models.energy.energyconsumptionparameters.EnergyConsumptionParameters;
import sidnet.models.energy.batteries.Battery;
import jist.runtime.JistAPI;
import jist.swans.Constants;
/**
 *
 * @author Oliviu Ghica
 */
public class EnergyConsumptionModelImpl implements EnergyConsumptionModel, EnergyConsumptionModelAccessible{
    // DEBUG
    private static final boolean DEBUG = false;
    private static final int DEBUG_NODEID = 921;  // -1 for all
    
    // CONSTANTS
    public static final boolean OK         = true;
    public static final boolean ERROR      = false;
    public static final long NA            = -1;
    
    // Events
    public static final int RADIO_TRANSMIT = 3;
    public static final int RADIO_RECEIVE  = 4;
    
    // States of the functional units in a node. They also act as triggering events
    public static final int RADIO_ON       = 5;
    public static final int RADIO_SLEEP    = 6;
    
    private int cpuDutyCycle = 100;     // percentage; 100% - fully loaded; 0% - idling; default: 100
    
    private static EnergyConsumptionParameters eCostParam;
    
    /* Radio energy */
    private long lastRadioTransmitTime, totalRadioTransmitDuration; // all expressed in [ms]
    private long lastRadioReceiveTime, totalRadioReceiveDuration;
    private long lastRadioOnTime, totalRadioListenDuration;
    private long totalRadioON_duration, lastRadioON_timestamp;
    private long lastRadioSleepTime, totalRadioSleepDuration;
    private long lastRadioStartsReceiving = NA;
    private long lastRadioStartsTransmitting = NA;
    
    private int numberReceivingInitialized = 0;
    
    /* CPU energy */
    private long lastCPUEventTime, totalCPUActiveDuration;  // in [ms]
    
    /* Sensing energy */
    private long lastSensorActiveTime, totalSensorActiveDuration; // in [ms]

    private int radioState;              // The previous state of the Radio
    private int CPUState;                // The previous state of the CPU
    
    private int id;                     // for debugging purposes only
    
    private Battery battery;
    
    /** Creates a new instance of EnergyModel */
    public EnergyConsumptionModelImpl(EnergyConsumptionParameters eCostParam, Battery battery) {
       this.eCostParam = eCostParam;   
       radioState  = RADIO_ON;

       this.id = id;
       this.battery = battery;
    }
   
    
    public void setID(int id) {
        this.id = id;
    }
    
    public int getID() {
        return id;
    }
    
    public int getRadioState() {
        return radioState;
    }
    
    public synchronized double getEnergyLevel_mJ(){
        double energyLevel = battery.getEnergyLevel_mJ();
        long now = JistAPI.getTime()/Constants.MILLI_SECOND;
        
        if (eCostParam == null) {
            System.out.println("[ERROR][EnergyConsumptionModelImpl.getEnergyLevel_mJ] - no EnergyConsumptionParameters have been specified");
            System.exit(-1);
        }
        
        
        if (battery.getCapacity_mJ() != battery.INF) {   
            /* Radio energy toll */
            energyLevel -= totalRadioTransmitDuration * eCostParam.get_Radio_Transmitting_Cost();
            energyLevel -= totalRadioReceiveDuration * eCostParam.get_Radio_Receiving_Cost();
            if (radioState == RADIO_ON) {
                totalRadioListenDuration = now - totalRadioTransmitDuration
                                               - totalRadioReceiveDuration
                                               - totalRadioSleepDuration;
                energyLevel -= totalRadioSleepDuration  * eCostParam.get_Radio_Sleeping_Cost();
                energyLevel -= totalRadioListenDuration * eCostParam.get_Radio_Listening_Cost();
            }
            else {    /* RadioState == RADIO_SLEEP */
                totalRadioSleepDuration = now - totalRadioTransmitDuration
                                              - totalRadioReceiveDuration
                                              - totalRadioListenDuration;
                energyLevel -= totalRadioListenDuration * eCostParam.get_Radio_Listening_Cost();
                energyLevel -= totalRadioSleepDuration  * eCostParam.get_Radio_Sleeping_Cost();
            }
            
            /* CPU toll */
            UpdateCPUEnergy();
            if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
                System.out.println("totalCPUActiveDuration * eCostParam.CPU_Processing_Cost = " + totalCPUActiveDuration + " * " + eCostParam.get_CPU_Processing_Cost());
            energyLevel -= totalCPUActiveDuration * eCostParam.get_CPU_Processing_Cost();                                                     /* Cost due to processing period */
            energyLevel -= (now - totalCPUActiveDuration) * eCostParam.get_CPU_Idling_Cost();            /* Cost due to idling period */

            /* Sensor toll */
            energyLevel -= totalSensorActiveDuration * eCostParam.get_Sensor_Active_Cost();                                                   /* Cost due to active period */
            energyLevel -= (now - totalSensorActiveDuration) * eCostParam.get_Sensor_Passive_Cost();     /* Cost due to passive period */
            
            if (energyLevel < 0)
                energyLevel = 0;
        }
               
        return energyLevel;
    }
    
    public double get_CPU_Active_Cost_mJ() {
        return totalCPUActiveDuration * eCostParam.get_CPU_Processing_Cost();
    }
    
    public void simulateSensing(long duration) {
        long eventTime = JistAPI.getTime()/Constants.MILLI_SECOND;
        duration = duration / Constants.MILLI_SECOND;
        
        /* Consistency check */
        if (eventTime < lastSensorActiveTime)
        	outOfOrderEventsFailure(eventTime, lastSensorActiveTime);
        
        /* Update durations */
        totalSensorActiveDuration += duration;
        lastSensorActiveTime = eventTime + duration;
    }
    
    private void outOfOrderEventsFailure(long eventTime, long lastComparingTime) {
    	 String errMsg = "[" + JistAPI.getTime()/Constants.MILLI_SECOND+"][CRITICAL ERROR: energyModel node] - Out of order timestamps in simulateSensing()" +
         				 "eventTime = " + eventTime + " < lastComparingTime = " + lastComparingTime;
    	 throw new RuntimeException(errMsg);
    }
    
    public synchronized void setCPUDutyCycle(int cpuDutyCycle) {
        UpdateCPUEnergy(JistAPI.getTime());
        this.cpuDutyCycle = cpuDutyCycle;
    }    
    
    public synchronized void simulateCPUActivity(long duration, int cpuDutyCycle) {
        setCPUDutyCycle(cpuDutyCycle); 
        JistAPI.sleepBlock(duration);
        setCPUDutyCycle(0); 
    }
    
    private synchronized void UpdateCPUEnergy() {
        UpdateCPUEnergy(JistAPI.getTime());
    }
    
    private synchronized void UpdateCPUEnergy(long eventTime) {
        if (eventTime == 0) // the enery map calls this at 0 and fails ... we must prevent this at 0, but no error, obviously, since this is not caused by nodes' activity
            return;
        eventTime = eventTime / Constants.MILLI_SECOND;
        if (eventTime < lastCPUEventTime) {
        	//outOfOrderEventsFailure(eventTime, lastCPUEventTime); // FIXME
        	return;
        }

        if (eventTime < totalCPUActiveDuration)
        	outOfOrderEventsFailure(eventTime, totalCPUActiveDuration);
        
        totalCPUActiveDuration += cpuDutyCycle  * (eventTime - lastCPUEventTime) / 100;
        lastCPUEventTime = eventTime;   
        // totalCPUIdleDuration is computed by subtracting totalCPUActiveDuration from the total simulation time 
    }
    
    public void simulatePacketReceive(long eventTime, long duration) {         
//        if (radioState == RADIO_SLEEP)
//        {
//            System.out.println("[ERROR][EnergyConsumptionModelImpl][" + JistAPI.getTime() + "] - Invalid state detected. Seems that the node attempts receiving a packet while its radio is turned off");
//            System.exit(1);
//        }
//        
//        if (radioState == RADIO_TRANSMIT)
//        {
//            System.out.println("[ERROR][EnergyConsumptionModelImpl][" + JistAPI.getTime() + "] - Invalid state detected. Seems that the node attempts receiving a packet while its radio is TRANSMITTING");
//            System.exit(1);
//        }
//        
//        radioState = RADIO_RECEIVE; // because we supposingly end up the receiving here
//        
//        eventTime = eventTime / Constants.MILLI_SECOND;
//        duration = duration / Constants.MILLI_SECOND;
//        
//        if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
//            System.out.println("[" + JistAPI.getTime() + "] - SimulatePacketReceive" + duration + "ms");
//        
//        /* Consistency check */
//        if (eventTime < lastRadioReceiveTime)
//    		  outOfOrderEventsFailure(eventTime, lastRadioReceiveTime);
//    	  if (eventTime < lastRadioTransmitTime)
//    		  outOfOrderEventsFailure(eventTime, lastRadioTransmitTime);
//        
//        /* Update durations */
//        totalRadioReceiveDuration += duration;
//        lastRadioReceiveTime = eventTime + duration;
    }
    
    public void simulatePacketStartsReceiving() {
        if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
            System.out.println("[DEBUG][#" +id + "][" + JistAPI.getTime() + "] - SimulatePacketStartsReceiving");
        if (radioState == RADIO_SLEEP) {
            System.out.println("[ERROR][EnergyConsumptionModelImpl][" + JistAPI.getTime() + "] - Invalid state detected. Seems that the node attempts receiving a packet while its radio is turned off");
            System.exit(1);
        }
        
        if (radioState == RADIO_TRANSMIT) {
            //System.out.println("[ERROR][EnergyConsumptionModelImpl][" + JistAPI.getTime() + "].simulatePacketStartsReceiving - Invalid state detected. Seems that the node attempts receiving a packet while its radio is TRANSMITTING");
            //System.exit(1);
            return;
        }
        
        radioState = RADIO_RECEIVE;
        
        if (lastRadioStartsReceiving != NA)
        {
            // OLIVER: Actually, it can happen if multiple nodes transmit to one node (collision)
            //System.out.println("[ERROR][EnergyConsumptionModelImpl][" + JistAPI.getTime() + "] - Invalid state detected. Seems that the nodes starts receiving while a previous receive mode has not finished.");
            //System.exit(1);
            
        }
        else
            lastRadioStartsReceiving = JistAPI.getTime(); // correct withou the MILLISECOND... look below
        numberReceivingInitialized ++;
    }
    
    public void simulatePacketEndsReceiving() {
        if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
            System.out.println("[NOTE][#" +id + "][" + JistAPI.getTime() + "] - SimulatePacketEndsReceiving");
        
        radioState = RADIO_ON;
        
        if (lastRadioStartsReceiving == NA) {
            //System.out.println("[ERROR][EnergyConsumptionModelImpl][#" +id + "][" + JistAPI.getTime() + "] - Invalid state detected. Seems that the node stops receiving while a previous receive mode has NOT been initialized");
            //System.exit(1);
            return;
        }
        else {
            numberReceivingInitialized--;
            if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
                System.out.println("[DEBUG][" + JistAPI.getTime() + "][numberReceivingInitiated left = " + numberReceivingInitialized);
            if (numberReceivingInitialized <= 0) {
                 if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
                    System.out.println("[" + JistAPI.getTime() + "] - totalRadioReceiveDuration = " + totalRadioReceiveDuration + " + duration (" + (JistAPI.getTime() - lastRadioStartsReceiving) / Constants.MILLI_SECOND + ")");
                totalRadioReceiveDuration += (JistAPI.getTime() - lastRadioStartsReceiving) / Constants.MILLI_SECOND; 
                lastRadioStartsReceiving = NA;
            }
        }
        lastRadioReceiveTime = JistAPI.getTime();
    }
    
    public void simulatePacketReceiveForcedTermination() {
        if (lastRadioStartsReceiving != NA) {
            simulatePacketEndsReceiving();
            lastRadioReceiveTime = JistAPI.getTime()/Constants.MILLI_SECOND;
            return;
        }
        
        radioState = RADIO_ON;
        
        if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
            System.out.println("[DEBUG][#" +id + "][" + JistAPI.getTime() + "] - SimulatePacketReceiveForcedTermination");
        long overestimatedReceiveTime = lastRadioReceiveTime - JistAPI.getTime()/Constants.MILLI_SECOND;
        if (overestimatedReceiveTime < 0)
            overestimatedReceiveTime = 0;
        totalRadioReceiveDuration -= overestimatedReceiveTime;
        lastRadioReceiveTime = JistAPI.getTime() / Constants.MILLI_SECOND;
    }
     
    public void simulateRadioForcedToIdle() {
//        if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
//            System.out.println("[DEBUG][#" +id + "][" + JistAPI.getTime() + "] - SimulatePacketForcedToIdle");
//        
//        radioState = RADIO_ON;
//        
//        simulatePacketReceiveForcedTermination();
//        simulatePacketTransmitForcedTermination();
//        simulateRadioWakes(); // if already awaken, then it is no problem, this has no effect;
    }
    
    public void simulatePacketTransmit(long duration) {
//        long eventTime = JistAPI.getTime()/Constants.MILLI_SECOND;
//        duration = duration / Constants.MILLI_SECOND;
//       
//        if (radioState == RADIO_SLEEP)
//        {
//            System.out.println("[ERROR][EnergyConsumptionModelImpl][#" +id + "][" + JistAPI.getTime() + "] - Invalid state detected. Seems that the node attempts to transmit while its transceiver is OFF");
//            System.exit(1);
//            
//        }
//        
//        radioState = RADIO_ON; // as we assume that the transmision ends
//        
//        if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
//            System.out.println("[DEBUG][#" +id + "] - simulatePacketTransmit(" + duration + "ms)");
//        
//        /* Consistency check */
//        if (eventTime < lastRadioReceiveTime)
//    		  outOfOrderEventsFailure(eventTime, lastRadioReceiveTime);
//        if(eventTime < lastRadioTransmitTime)
//    		  outOfOrderEventsFailure(eventTime, lastRadioTransmitTime);
//        
//        /* Update durations */
//        totalRadioTransmitDuration += duration;
//        lastRadioTransmitTime = eventTime + duration;
    }
    
    public void simulatePacketTransmitForcedTermination() {
//        long overestimatedTransmitTime = lastRadioTransmitTime - JistAPI.getTime()/Constants.MILLI_SECOND;
//        if (overestimatedTransmitTime < 0)
//            overestimatedTransmitTime = 0;
//        
//        radioState = RADIO_ON;
//        
//        totalRadioTransmitDuration -= overestimatedTransmitTime;
//        lastRadioTransmitTime = JistAPI.getTime() / Constants.MILLI_SECOND;
    }
    
    public void simulatePacketStartsTransmitting() {
        if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
            System.out.println("[DEBUG][#" +id + "][" + JistAPI.getTime() + "] - SimulatePacketStartsTransmitting");
        
        if (radioState == RADIO_SLEEP) {
            System.out.println("[ERROR][EnergyConsumptionModelImpl][#" +id + "][" + JistAPI.getTime() + "] - Invalid state detected. Seems that the node attempts transmitting a packet while its radio is turned off");
            System.exit(1);
        }
        
        if (radioState == RADIO_RECEIVE) {
            System.out.println("[ERROR][EnergyConsumptionModelImpl][" + JistAPI.getTime() + "].simulatePacketStartsReceiving - Invalid state detected. Seems that the node attempts receiving a packet while its radio is RECEIVING");
            System.exit(1);
        }
        
        radioState = RADIO_TRANSMIT;
        
        if (lastRadioStartsTransmitting != NA) {
            //System.out.println("[ERROR][EnergyConsumptionModelImpl][#" +id + "][" + JistAPI.getTime() + "] - Invalid state detected. Seems that the nodes starts transmitting while a previous transmit mode has not finished.");
            //System.exit(1);
            return;
        }
        lastRadioStartsTransmitting = JistAPI.getTime();
    }
    
    public void simulatePacketEndsTransmitting() {
        if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
            System.out.println("[DEBUG][#" +id + "][" + JistAPI.getTime() + "] - SimulatePacketEndsTransmitting");
        
        if (radioState == RADIO_SLEEP) {
            System.err.println("[ERROR][EnergyConsumptionModelImpl][#" +id + "][" + JistAPI.getTime() + "] - Invalid state detected. Seems that the node ends transmitting a packet while its radio is turned off");
            new Exception().printStackTrace();
            System.exit(1);
            return;
        }
        
        if (lastRadioStartsTransmitting == NA) {
            //System.out.println("[ERROR][EnergyConsumptionModelImpl][#" +id + "][" + JistAPI.getTime() + "] - Invalid state detected. Seems that the node stops transmitting while a previous transmit mode has NOT been initialized");
            //System.exit(1);
            return;
        }
        
        radioState = RADIO_ON;
        
        totalRadioTransmitDuration += (JistAPI.getTime() - lastRadioStartsTransmitting) / Constants.MILLI_SECOND; 
        lastRadioStartsTransmitting = NA;
        lastRadioTransmitTime = JistAPI.getTime();
    }
    
   
    
    public void simulateRadioGoesToSleep() {
        if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
            System.out.println("[DEBUG][#" +id + "][" + JistAPI.getTime() + "] - SimulateRadioGoesToSleep");
        
        long eventTime = JistAPI.getTime();
        
        // Lan Bai: 10/05/2009 (TODO monitor if this breaks stuff)
        // I did this because I found that such trace exists: 
        // ... startTransmit -> startRecv -> endRecv -> GotoSleep -> endTransmit ...
        // In the original code, the second endTransmit never got counted 
        // because the radio is in sleep state when it occurs. 
        // As a result, it overestimated the transmitting time since 
        // it thinks this transmission ends much later than it was.
        // So what I did is to not allow the radio to go to sleep 
        // if last transmitting hasn't ended yet.        
        if (lastRadioStartsTransmitting != NA ) {
            if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
                System.out.println("Last transmit not finished yet.");
            return;
        }
        
        if (radioState == RADIO_SLEEP) {
            if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
                System.out.println("Node already sleeps. Do nothing ...");
            return;         /* Node already in the sleep mode. Do nothing */
        }
        
        if (radioState == RADIO_TRANSMIT || radioState == RADIO_RECEIVE) {
            ///System.out.println("[" + JistAPI.getTime() + "][CRITICAL ERROR: energyModel #"+id+"] - Radio is TRANSMIT/RECEIVE mode when attempting to mark it OFF");
            //Exception e = new Exception("[" + JistAPI.getTime() + "]CRITICAL ERROR: energyModel #"+id+"] - Radio is TRANSMIT/RECEIVE mode when attempting to mark it OFF");
            //e.printStackTrace();
            //System.exit(1);
            return;
        }
        
        radioState = RADIO_SLEEP;
        
        /* Consistency check */
        if (eventTime < lastRadioSleepTime)
        	outOfOrderEventsFailure(eventTime, lastRadioSleepTime);
        if (eventTime < lastRadioON_timestamp)
        	outOfOrderEventsFailure(eventTime, lastRadioON_timestamp);
        if (eventTime < lastRadioTransmitTime)
        	outOfOrderEventsFailure(eventTime, lastRadioTransmitTime);
        if (eventTime < lastRadioReceiveTime)
        	outOfOrderEventsFailure(eventTime, lastRadioReceiveTime);
        
        totalRadioON_duration += (eventTime - lastRadioON_timestamp)/Constants.MILLI_SECOND;
        lastRadioSleepTime = eventTime;
        
        /* Update durations */
        totalRadioListenDuration = totalRadioON_duration - totalRadioTransmitDuration
                                                         - totalRadioReceiveDuration;
    }
    
    public void simulateRadioWakes() {
        if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
            System.out.println("[DEBUG][#" +id + "][" + JistAPI.getTime() + "] - SimulatePacketRadioWakes");
        
        if (DEBUG && (id == 748 || id == 588)) {
            System.out.println("["+ JistAPI.getTime() + "]#" + id + " = WAKES");
            println();
        }
        
        if (radioState == RADIO_ON) {
            if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
                System.out.println("Node already awaken. Do nothing ...");
            return;         /* Node already in the listening/wake mode. Do nothing */
        }
        
        if (radioState != RADIO_ON && radioState != RADIO_SLEEP) {
            //System.out.println("[CRITICAL ERROR: energyModel #"+id+"] - Radio is TRANSMIT/RECEIVE mode when attempting to mark it ON");
            //Exception e = new Exception("[CRITICAL ERROR: energyModel #"+id+"] - Radio is TRANSMIT/RECEIVE mode when attempting to mark it ON");
            //e.printStackTrace();
            //System.exit(1);
            return;
        }
        
        radioState = RADIO_ON;
        
        long eventTime = JistAPI.getTime();
        
        /* Consistency check */
        if (eventTime < lastRadioSleepTime)
        	outOfOrderEventsFailure(eventTime, lastRadioSleepTime);
        if (eventTime < lastRadioOnTime)
        	outOfOrderEventsFailure(eventTime, lastRadioOnTime);
        if (eventTime < lastRadioTransmitTime)
        	outOfOrderEventsFailure(eventTime, lastRadioTransmitTime);
        if (eventTime < lastRadioReceiveTime)
        	outOfOrderEventsFailure(eventTime, lastRadioReceiveTime);       
        
        /* Update durations */
        //totalRadioSleepDuration = JistAPI.getTime()/Constants.MILLI_SECOND - totalRadioTransmitDuration
        //                                                                    - totalRadioReceiveDuration
        //                                                                    - totalRadioListenDuration;
        
        totalRadioSleepDuration += (eventTime - lastRadioSleepTime)/Constants.MILLI_SECOND;
        
        lastRadioON_timestamp = eventTime;
        
         if (DEBUG && (DEBUG_NODEID == id || DEBUG_NODEID == -1))
            println();
    }
    
    public EnergyConsumptionParameters getEnergyConsumptionParameters() {
        return eCostParam;
    }
    
    /**
     * For testing only
     * @return
     */
    public int getNumberReceivingInitialized() {
        return numberReceivingInitialized;
    }
    
    /**
     * For testing only
     * @return
     */
    public long getTotalRadioReceiveDuration() {
        return totalRadioReceiveDuration;
    }
    
    /**
     * For testing only
     * @return
     */
    public long getTotalRadioListenDuration() {
        return totalRadioListenDuration;
    }
    
    /**
     * For testing only
     * @return
     */
    public long getTotalRadioTransmitDuration() {
        return totalRadioTransmitDuration;
    }
    
    /**
     * For testing only
     * @return
     */
    public long getTotalRadioSleepDuration() {
        return totalRadioSleepDuration;
    }
      
    public void println() {
        UpdateCPUEnergy();
        System.out.println("Node = " + id);
        System.out.println("totalRadioON_Duration           (ms) : " + totalRadioON_duration);
        System.out.println("\ttotalRadioTransmitDuration      (ms) : " + totalRadioTransmitDuration);
        System.out.println("\ttotalRadioReceiveDuration       (ms) : " + totalRadioReceiveDuration);
        System.out.println("\ttotalRadioListenDuration        (ms) : " + totalRadioListenDuration);
        System.out.println("totalRadioSleepDuration         (ms) : " + totalRadioSleepDuration);    
        System.out.println("totalCPUEffectiveActiveDuration (ms) : " + totalCPUActiveDuration);
    }
}
