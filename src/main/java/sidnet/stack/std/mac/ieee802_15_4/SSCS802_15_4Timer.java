/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import jist.runtime.JistAPI;
import jist.swans.Constants;

/**
 *
 * @author Oliver
 */
//public class SSCS802_15_4Timer extends p802_15_4Timer /*Handler*/
public class SSCS802_15_4Timer implements TimerInterface802_15_4
{
    //protected SSCS802_15_4 sscs;
    protected boolean started;
    protected boolean canceled;
    protected boolean terminated;
    private Mac802_15_4 macEntity;
    private boolean active;
    protected boolean paused;
    protected boolean busy;
    protected double stime = 0.0;		//start time
    protected double wtime = 0.0;		//waiting time
    
     protected double lastMomentOfPause = 0.0;
    protected double totalPauseTime = 0.0;
    private int id;
    private long timerSequence = 0;
    
    // PROXIES -----------
    private TimerInterface802_15_4 self;
    
    
    
    // ??? friend class SSCS802_15_4;
    public SSCS802_15_4Timer(Mac802_15_4 macEntity, int id)
    {
        super();
        if(!JistAPI.isEntity(macEntity)) throw new IllegalArgumentException("expected entity");
        self = (TimerInterface802_15_4)JistAPI.proxy(this, TimerInterface802_15_4.class);
        this.macEntity = macEntity;
        active = false;
        this.id = id;
    }
    
     public void resetTimer()
    {
        started    = false;
        busy       = false;
        paused     = false;
        canceled   = false;
        terminated = false;
        stime = 0;
        wtime = 0;
    }
     
     public void start()
    {
        // DO NOT IMPLEMENT
    } 
     
      public void	start(double time, boolean onlycap)
    {
        // DO NOT IMPLEMENT> Only for macExtractTimer
    }
     
       public void	start(boolean reset, boolean fortx, double wt)
    {
        // DO NOT IMPLEMENT> Only for macBcnTxTimer
    }
       
         public void startTimer(double wtime, MacMessage_802_15_4 p)
    {
        // DO NOT IMPLEMENT
    }
       
        /**
     * Start timer
     */
    public void startTimer(double time)
    {
        assert(!busy && !started);
	busy = true;
	stime =((double)JistAPI.getTime())/Constants.SECOND;
	wtime = time;
	assert(wtime >= 0);
	//s.schedule(this, /* & */event, wtime);
        timerSequence++;
        long currentTimerSequence = timerSequence;
        JistAPI.sleep((long)(wtime * Constants.SECOND));
        self.timeout(currentTimerSequence);
    }
    
    public void start(long wtime)
    {
        assert(!active);
	active = true;
        timerSequence++;
	long currentTimerSequence = timerSequence;
        JistAPI.sleep(wtime);
        self.timeout(currentTimerSequence);
    }
    
     public boolean canceled() throws JistAPI.Continuation
    {
        return canceled;
    }
    
    public boolean paused() throws JistAPI.Continuation
    {
        return paused;
    }
    
     public boolean bussy() throws JistAPI.Continuation
    {
        return busy;
    }
     
    /** 
     * Pause timer
     */
    public void pause()
    {
        assert(started && !terminated && !canceled && !paused);
        paused = true;
        lastMomentOfPause = ((double)JistAPI.getTime())/Constants.SECOND;
    }
    
    public void cancel()
    {
        active = false;
    }
   
    public void timeout(long currentTimerSequence)
    {
        if (currentTimerSequence == timerSequence)
            if (!canceled && !terminated && !paused)
            {
                terminated = true;
                if (!active)
                    return; // probably canceled
                active = false;
                if (macEntity.sscs_neverAsso())
                    macEntity.startDevice();
            }
    }
    
     private double expire()
    {
        return ((double)JistAPI.getTime())/Constants.SECOND - stime - totalPauseTime > wtime ? 0 : wtime - (((double)JistAPI.getTime())/Constants.SECOND - stime - totalPauseTime);
    }
    
     /** 
     * Resume timer
     */
    public void resume()
    {
        assert(started && !terminated && !canceled);
        totalPauseTime += ((double)JistAPI.getTime())/Constants.SECOND - lastMomentOfPause;
        paused = false;
        // sleep for the remaining amount of time in the timer
        long currentTimerSequence = timerSequence;
        JistAPI.sleep((long)(expire() * Constants.SECOND));
        self.timeout(currentTimerSequence);
    }
    
     public void stopTimerr()
    {
         self.cancel();
	 self.resetTimer();
    }
    
    public TimerInterface802_15_4 getProxy()
    {
        return self;
    }
}
