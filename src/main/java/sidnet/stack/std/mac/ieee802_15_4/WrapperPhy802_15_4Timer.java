/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import java.util.LinkedList;
import java.util.List;
import jist.runtime.JistAPI;
import sidnet.models.energy.energyconsumptionmodels.EnergyConsumptionModel;
import sidnet.models.energy.energyconsumptionmodels.EnergyManagement;

/**
 *
 * @author Oliver
 */
public class WrapperPhy802_15_4Timer implements TimerInterface802_15_4{
    private List<TimerInterface802_15_4> timerList;
    private long availableTimers = 0;
    
    private EnergyManagement energyManagementUnit;
    private EnergyConsumptionModel energyConsumptionModel; 
    private int type;
    private int id = 0; // for debugging purposes only
    
    private MacMessage_802_15_4 p = null;
    
    // PROXIES------------------------------------
    private Phy802_15_4 phy;  
    TimerInterface802_15_4 toSleepTimerEntity;
    TimerInterface802_15_4 self;
    
    
    /**
     * ToSleepTimer is for energyConsumptionModel. It schedules the node's radio in the OFF mode
     */
    public WrapperPhy802_15_4Timer(Phy802_15_4 p, int tp, int id, EnergyManagement energyManagementUnit, TimerInterface802_15_4 toSleepTimerEntity)
    {
        if(!JistAPI.isEntity(p)) throw new IllegalArgumentException("expected entity");
        self = (TimerInterface802_15_4)JistAPI.proxy(this, TimerInterface802_15_4.class);
        timerList = new LinkedList<TimerInterface802_15_4>();
        availableTimers = timerList.size();
                
        phy = p;
        type = tp;
        
        this.id = id;
        this.energyManagementUnit = energyManagementUnit;
        if (energyManagementUnit != null)
            this.energyConsumptionModel = energyManagementUnit.getEnergyConsumptionModel();
        
        this.toSleepTimerEntity = toSleepTimerEntity;
    }
    
    private synchronized TimerInterface802_15_4 getFreshTimer()
    {
        //if (id == 332)
          //  System.out.println("timerbase size = " + timerList.size());
        int cleanIndex = findAvailableTimers(); 
        if (cleanIndex != -1)
        {
            if (cleanIndex != 0)
                timerList.add(0, timerList.remove(cleanIndex));
        }
        else // create a new timer
        {
            TimerInterface802_15_4 newTimer = new Phy802_15_4Timer(phy, type, id, energyManagementUnit, toSleepTimerEntity);
            timerList.add(0, newTimer); // insert the timer last
        }
        return timerList.get(0);
    }
    
    private synchronized int findAvailableTimers()
    {
        int contor = 0;
        for (TimerInterface802_15_4 timer: timerList)
        {
            if (!((Phy802_15_4Timer)timer).getProxy().bussy())
            {
                ((Phy802_15_4Timer)timer).getProxy().resetTimer();
                return contor;
            }
            contor++;
        }
        return -1;
    }
    
    public synchronized void startTimer(double wtime, MacMessage_802_15_4 p)
    {          
        TimerInterface802_15_4 newTimer = getFreshTimer();
        ((Phy802_15_4Timer)newTimer).p = p;
        
        ((Phy802_15_4Timer)newTimer).getProxy().startTimer(wtime);
        //this.startTimer(wtime);
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
        // DO NOT IMPLEMENT
    }
    
    public void cancel()
    {
        ((Phy802_15_4Timer)timerList.get(0)).getProxy().cancel();
    }
    
    public void timeout(long currentTimerSequence)
    {
        //
    }
    // ------------------------------------
    
    public void resetTimer()
    {
       ((Phy802_15_4Timer)timerList.get(0)).getProxy().resetTimer();
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
        
    }
    
    
    
    public boolean canceled() throws JistAPI.Continuation
    {
        return  ((Phy802_15_4Timer)timerList.get(0)).getProxy().canceled();
    }
    
    public boolean bussy() throws JistAPI.Continuation
    {
        return ((Phy802_15_4Timer)timerList.get(0)).getProxy().bussy();
    }
    
    public boolean paused() throws JistAPI.Continuation
    {
        return ((Phy802_15_4Timer)timerList.get(0)).getProxy().paused();
    }
       
    /** 
     * Pause timer
     */
    public void pause()
    {
        ((Phy802_15_4Timer)timerList.get(0)).getProxy().pause();
    }
    
    /** 
     * Resume timer
     */
    public void resume()
    {
        ((Phy802_15_4Timer)timerList.get(0)).getProxy().resume();
    }
    
    public void stopTimerr()
    {
        ((Phy802_15_4Timer)timerList.get(0)).getProxy().stopTimerr();
    }
    
     public TimerInterface802_15_4 getProxy()
    {
        return self;
    }
}
