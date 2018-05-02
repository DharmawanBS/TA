/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import jist.runtime.JistAPI;
import jist.swans.Constants;
import jist.swans.mac.MacAddress;

/**
 *
 * @author Oliver
 */
public class ToSleepTimer2 
implements TimerInterface802_15_4 {
    // DEBUG
    private static final boolean DEBUG = false;
    
    private double toSleepTime_s;
    
    protected boolean active;
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
    
    
    // PROXIES-----------------------------------
    Phy802_15_4 phyEntity;
    TimerInterface802_15_4 self;
    
    /**
     * 
     * @param id
     * @param toSleepTime - the amount of time elapsed before the radio is turned off in [MS]
     * @param energyConsumptionModel
     */
    public ToSleepTimer2(int id, double toSleepTime_s, Phy802_15_4 phyEntity)
    {
        //super();
        self = (TimerInterface802_15_4)JistAPI.proxy(this, TimerInterface802_15_4.class);
        active = false;
        this.id = id;
        this.toSleepTime_s = toSleepTime_s;
        this.phyEntity = phyEntity;
        
        
    }
    
    public void startTimer(double wtime, MacMessage_802_15_4 p)
    {
        // DO NOT IMPLEMENT
    }
    
      public void start()
    {
       startTimer();
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
             canceled = true;
        }
        //this.cancelTimer();
        active = false;
    }
    
    public void timeout(long currentTimerSequence)
    {
        if (currentTimerSequence == timerSequence )
            if (!canceled /* && !terminated*/ && !paused) //???
            {
                if (Def.DEBUG802_15_4_timer && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.hashCode() == id ))
                    System.out.println("["+JistAPI.getTime()+"][#" + id + "][ToSleepTimer.timeout()]");

                active = false;
                // Rey: only call handler if timer wasn't canceled in the meanwhile
                if (!canceled && !terminated)
                {
                     terminated = true;
                     busy = false;
                     //if (id == 588)
                       //  System.out.println("ToSleepTimer2.timeout");
                     phyEntity.markSleeps();
                }
                else	
                     assert(false);
            }

        
    }
    // ------------------------------------
    
    public void resetTimer()
    {
        busy       = false;
        paused     = false;
        canceled   = false;
        terminated = false;
        stime = 0;
        wtime = 0;
    }
    
    /**
     * Handle timer expiration.
     */
    /*public void timeout()
    {
        // user code to be executed at timer expiration
    }*/
    
    public void startTimer(double wtime)
    {
        startTimer();
    }
    
    /**
     * Start timer
     */
    public void startTimer()
    {
        if (Def.DEBUG802_15_4_timer && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.hashCode() == id ))
            System.out.println("["+JistAPI.getTime()+"][" + id + "][ToSleepTimer.startTimer( _ , _ )]");
        
        active = true;
        
        assert(!busy && !started);
	busy = true;
	// Rey: reset canceled flag on timer restart
	canceled = false;
	stime = ((double)JistAPI.getTime())/Constants.SECOND;
	wtime = toSleepTime_s;
        
        
	assert(wtime >= 0);
	//s.schedule(this, /* & */event, wtime);
        if (Def.DEBUG802_15_4_timer && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.hashCode() == id ))
        {
            System.out.println("wtime = " + wtime / Constants.MILLI_SECOND + " ms");
            System.out.println("wtime * Constants.MILI_SECOND = " + (wtime));
            System.out.println("[" + JistAPI.getTime() + "][" + id + "][ToSleepTimer.startTimer()]. Timer scheduled to expire at " + (long)(JistAPI.getTime() + wtime * Constants.SECOND) + " (wtime = " + wtime);
        }
        timerSequence++;
        long currentTimerSequence = timerSequence;
        JistAPI.sleep((long)(wtime*Constants.SECOND));
        self.timeout((long)currentTimerSequence);
    }
    
    /**
     * Cancel timer
     */ 
    private void cancelTimer()
    {
        assert(started);
        canceled = true;
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
    
    public double expire()
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
        // sleep for the remaining amount of time in the timer
        long currentTimerSequence = timerSequence;
        JistAPI.sleep((long)(expire() * Constants.SECOND));
        timeout((long)currentTimerSequence);
    }
    
    public void stopTimerr()
    {
         if (DEBUG) System.out.println("["+JistAPI.getTime()+"][" + id + "]ToSleepTimer.stopTimerr");
         cancel();
	 resetTimer();
    }
    
     public TimerInterface802_15_4 getProxy()
    {
        return self;
    }
}
