/*
 * Phy802_15_4Timer.java
 *
 * Created on July 23, 2008, 11:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import jist.runtime.JistAPI;
import jist.swans.Constants;
import jist.swans.mac.MacAddress;
import sidnet.models.energy.energyconsumptionmodels.EnergyConsumptionModel;
import sidnet.models.energy.energyconsumptionmodels.EnergyManagement;

/**
 *
 * @author Oliver
 * Java adaptation after NS-2 C++ implementation
 */ 
/*
 * Copyright (c) 2003-2004 Samsung Advanced Institute of Technology and
 * The City University of New York. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the Joint Lab of Samsung 
 *      Advanced Institute of Technology and The City University of New York.
 * 4. Neither the name of Samsung Advanced Institute of Technology nor of 
 *    The City University of New York may be used to endorse or promote 
 *    products derived from this software without specific prior written 
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE JOINT LAB OF SAMSUNG ADVANCED INSTITUTE
 * OF TECHNOLOGY AND THE CITY UNIVERSITY OF NEW YORK ``AS IS'' AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN 
 * NO EVENT SHALL SAMSUNG ADVANCED INSTITUTE OR THE CITY UNIVERSITY OF NEW YORK 
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class Phy802_15_4Timer implements TimerInterface802_15_4
{
    // DEBUG
    private static final boolean DEBUG = false;
    
    protected boolean started;
    protected boolean busy;
    protected boolean paused;
    protected boolean canceled;
    protected boolean terminated;
    protected double stime = 0.0;		//start time
    protected double wtime = 0.0;		//waiting time
    protected double lastMomentOfPause = 0.0;
    protected double totalPauseTime = 0.0;
    protected int id = 0; // for debugging purposes only
    protected long timerSequence = 0;
    
    private EnergyConsumptionModel energyConsumptionModel; 
    private EnergyManagement energyManagementUnit;
    private int type;
    private boolean active;
    MacMessage_802_15_4 p = null;
    
    // PROXIES------------------------------------
    private Phy802_15_4 phy;  
    TimerInterface802_15_4 toSleepTimerEntity;
    TimerInterface802_15_4 self;
    
    private String getType()
    {
        switch(type)
        {
            case 1: return "CCAH"; 
            case 2: return "EDHT"; 
            case 3: return "TRXHT"; 
            case 4: return "phyRecvOverH"; 
            case 5: return "phySendOverH";
            default: return "INVALID"; 
        }
    }
    
    /**
     * ToSleepTimer is for energyConsumptionModel. It schedules the node's radio in the OFF mode
     */
    public Phy802_15_4Timer(Phy802_15_4 p, int tp, int id, EnergyManagement energyManagementUnit, TimerInterface802_15_4 toSleepTimerEntity)
    {
        if(!JistAPI.isEntity(p)) throw new IllegalArgumentException("expected entity");
        self = (TimerInterface802_15_4)JistAPI.proxy(this, TimerInterface802_15_4.class);
        //super();
        phy = p;
        type = tp;
        active = false;
        this.id = id;
        this.energyManagementUnit = energyManagementUnit;
        if (energyManagementUnit != null)
            this.energyConsumptionModel = energyManagementUnit.getEnergyConsumptionModel();
        
        this.toSleepTimerEntity = toSleepTimerEntity;
        
       
    }
    
    public void startTimer(double wtime, MacMessage_802_15_4 p)
    {   
        if (Def.DEBUG802_15_4_timer && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(id) ))
            System.out.println("["+JistAPI.getTime()+"][" + id + "][Phy802_15_4Timer." + getType() + ".startTimer( _ , _ )]");
        
	active = true;
        this.p = p;
        this.startTimer(wtime);
    }
    
     public void start()
    {
        // DO NOT IMPLEMENTD
    }        
    
    public void	start(double time, boolean onlycap)
    {
        // DO NOT IMPLEMENT> Only for macExtractTimer
    }
    
    public void	start(boolean reset, boolean fortx, double wt)
    {
        // DO NOT IMPLEMENT> Only for macBcnTxTimer
    }
    
    public void cancel()
    {
        if (!canceled && !terminated)
        {
            //System.out.println("["+JistAPI.getTime()+"][" + id + "]Phy802_15_4Timer (" + getType() + ") canceled");
            // --- OLIVER: NOT IN NS2. Needed for energy actualization
            //if (getType().equals("phySendOverH") && energyConsumptionModel != null)
            //    energyConsumptionModel.simulatePacketEndsTransmitting();
            //if (getType().equals("phyRecvOverH") && energyConsumptionModel != null)
            //    energyConsumptionModel.simulatePacketEndsReceiving();
            // schedule the power-off
            //toSleepTimerEntity.startTimer(0.0 /* value will be ignored on sleep timer*/);
            // ---
        }
        //cancelTimer();
        canceled = true;
        active = false;
        busy= false;
        
    }
    
    public void timeout(long currentTimerSequence)
    {
        if (currentTimerSequence == timerSequence)
            if (!canceled /* && !terminated*/ && !paused)
            {
        
                if (energyConsumptionModel != null && energyManagementUnit.getBattery().getPercentageEnergyLevel() < 1)
                {
                    if (Def.DEBUG802_15_4_timer)
                        System.out.println("["+JistAPI.getTime()+"][" + id + "][Phy802_15_4Timer." + getType() + ".startTimer( _ , _ )] - cannot continue execution since the node's battery is depleted.");
                    return;
                }



                busy = false;

                //timeout();
                if (Def.DEBUG802_15_4_timer && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(id) ))
                    System.out.println("["+JistAPI.getTime()+"][#" + id + "][Phy802_15_4Timer." + getType() + ".timeout()]");

                active = false;
                // Rey: only call handler if timer wasn't canceled in the meanwhile
                if (!canceled) {
                    if (type == Phy802_15_4Impl.phyCCAHType)
                            phy.CCAHandler();
                    else if(type == Phy802_15_4Impl.phyEDHType)
                            phy.EDHandler();
                    else if(type == Phy802_15_4Impl.phyTRXHType)
                            phy.TRXHandler();
                    else if(type == Phy802_15_4Impl.phyRecvOverHType)
                    {
                        //if (energyConsumptionModel != null)
                          //  energyConsumptionModel.simulatePacketEndsReceiving();
                        if (Def.DEBUG802_15_4_timer && id == Def.DEBUG802_15_4_nodeid.hashCode())
                            System.out.println("["+JistAPI.getTime()+"][#" + id + "][recvOverTimer." + getType() + ".timeout()]");
                        //energyConsumptionModel.simulateRadioGoesToSleep();
                        phy.recvOverHandler(/*(MacMessage_802_15_4 )e ??? */ p);

                        //toSleepTimerEntity.startTimer(0.0);
                    }
                    else if(type == Phy802_15_4Impl.phySendOverHType)
                    {

                        phy.sendOverHandler(); // MAC can call this as well!!! 
                        //toSleepTimerEntity.startTimer(0.0);
                    }
                    else	
                        assert(false);
                }
                terminated = true;
            }
    }
    // ------------------------------------
    
    public void resetTimer() throws JistAPI.Continuation
    {
        busy       = false;
        paused     = false;
        canceled   = false;
        terminated = false;
        stime = 0;
        wtime = 0;
    }
    
    
    public void setNewSleepTimer(TimerInterface802_15_4 timerEntity)
    {
        
    }
    
    /**
     * Handle timer expiration.
     */
    /*public void timeout()
    {
        // user code to be executed at timer expiration
    }*/
    
    /**
     * Start timer
     */
    public void startTimer(double time)
    {
        if (energyConsumptionModel != null && energyManagementUnit.getBattery().getPercentageEnergyLevel() < 1)
        {
            if (Def.DEBUG802_15_4_timer)
                System.out.println("["+JistAPI.getTime()+"][" + id + "][Phy802_15_4Timer." + getType() + ".startTimer( _ , _ )] - cannot start the timer since the node's battery is depleted.");
            return;
        }
        
        //assert(!busy && !started); ???
	
        busy = true;
	// Rey: reset canceled flag on timer restart
	canceled = false;
	stime = ((double)JistAPI.getTime())/Constants.SECOND;
	wtime = time;
        
        if (getType().equals("phySendOverH") && energyConsumptionModel != null)
        {
            //if (DEBUG)
            //if (id == 588) 
                //System.out.println("588[DEBUG][phySendOverH.Timer] - startTimer(_)]");
            //energyConsumptionModel.simulateRadioWakes();
            //phy.markWakes();
            //energyConsumptionModel.simulateRadioWakes();
            //energyConsumptionModel.simulatePacketStartsTransmitting();            
            //energyConsumptionModel.simulateRadioGoesToSleep();
            //toSleepTimerEntity.cancel();
            //toSleepTimerEntity.start();
        }
        if (getType().equals("phyRecvOverH") && energyConsumptionModel != null)
        {
            //if (DEBUG) 
            //if (id == 921)  System.out.println("921[DEBUG][phyRecvOverH.Timer] - startTimer(_)]");
            //phy.markSleeps();
            //phy.markWakes();
            //energyConsumptionModel.simulateRadioWakes();
            //energyConsumptionModel.simulatePacketStartsReceiving();
            //if (id == 588)
              //   System.out.println("ToSleepTimer2.timeout");
            //energyConsumptionModel.simulateRadioGoesToSleep();
            //toSleepTimerEntity.cancel();
            //toSleepTimerEntity.start();
        }
        
		assert(wtime >= 0);
		if (wtime < 0)
			throw new RuntimeException("Attempt to set a negative time in timer: " + wtime);
		
		//s.schedule(this, /* & */event, wtime);
	        if (Def.DEBUG802_15_4_timer && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(id) ))
	        {
	            System.out.println("wtime = " + wtime);
	            System.out.println("Constants.SECOND = " + Constants.SECOND);
	            System.out.println("wtime * Constants.SECOND = " + (wtime * Constants.SECOND));
	            System.out.println("[" + JistAPI.getTime() + "][" + id + "][Phy802_15_4Timer." + getType() + ".startTimer()]. Timer scheduled to expire at " + (long)(JistAPI.getTime() + wtime * Constants.SECOND) + " (wtime = " + wtime);
	        }
	        timerSequence++;
	        long currentTimerSequence = timerSequence;
	        JistAPI.sleep((long)(wtime * Constants.SECOND));
	        self.timeout(currentTimerSequence);
    }
    
    /**
     * Cancel timer
     */ 
    private void cancelTimer()
    {
        assert(started);
        canceled = true;
        busy = false;
    }
    
    public boolean canceled() throws JistAPI.Continuation
    {
        return canceled;
    }
    
    public boolean bussy() throws JistAPI.Continuation
    {
        return busy;
    }
    
    public boolean paused() throws JistAPI.Continuation
    {
        return paused;
    }
    
    private double expire()
    {
        return ((double)JistAPI.getTime())/Constants.SECOND - stime - totalPauseTime > wtime ? 0 : wtime - (((double)JistAPI.getTime())/Constants.SECOND - stime - totalPauseTime);
    }
   
    /** 
     * Pause timer
     */
    public void pause()
    {
        assert(started && !canceled && !paused);
        paused = true;
        lastMomentOfPause = ((double)JistAPI.getTime())/Constants.SECOND;
    }
    
    /** 
     * Resume timer
     */
    public void resume()
    {
        assert(started && !canceled);
        totalPauseTime += ((double)JistAPI.getTime())/Constants.SECOND - lastMomentOfPause;
        paused = false;
        busy = true;
        // sleep for the remaining amount of time in the timer
        long currentTimerSequence = timerSequence;
        JistAPI.sleep((long)(expire() * Constants.SECOND));       
        self.timeout(currentTimerSequence);
    }
    
    public void stopTimerr()
    {
         //System.out.println("["+JistAPI.getTime()+"][" + id + "]Phy802_15_4Timer (" + getType() + ") stopTimerr");
         cancel();
	 resetTimer();
    }
    
     public TimerInterface802_15_4 getProxy()
    {
        return self;
    }
}
