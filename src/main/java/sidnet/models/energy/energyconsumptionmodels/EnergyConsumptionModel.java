/*
 * EnergyModel.java
 *
 * Created on March 19, 2008, 8:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.models.energy.energyconsumptionmodels;

import sidnet.models.energy.energyconsumptionparameters.EnergyConsumptionParameters;

/**
 *
 * @author Oliver
 */
public interface EnergyConsumptionModel
{
   public void setID(int id); 
    
   public void simulateSensing(long requiredTime);
   
   public void setCPUDutyCycle(int cpuDutyCycle);
   
   public void simulateCPUActivity(long duration, int cpuDutyCycle);
 
   public void simulatePacketReceive(long startTime, long duration);
   
   public void simulatePacketStartsReceiving();
   
   public void simulatePacketEndsReceiving();
   
   public void simulatePacketReceiveForcedTermination();
   
   public void simulateRadioForcedToIdle();
   
   public void simulatePacketTransmit(long requiredTime);
   
   public void simulatePacketTransmitForcedTermination();
   
   public void simulatePacketStartsTransmitting();
   
   public void simulatePacketEndsTransmitting();
   
   public void simulateRadioGoesToSleep();
   
   public void simulateRadioWakes(); 
   
   public int getRadioState();
   
   public EnergyConsumptionParameters getEnergyConsumptionParameters();
}
