/*
 * macBeaconRxTimer.java
 *
 * Created on July 9, 2008, 4:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import jist.runtime.JistAPI;
import jist.swans.Constants;
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
public class macBeaconRxTimer implements TimerInterface802_15_4
{
    protected boolean started;
    protected boolean busy;
    protected boolean paused;
    protected boolean canceled;
    protected boolean terminated;
    protected double stime = 0.0;		//start time
    protected double wtime = 0.0;		//waiting time
    protected double lastMomentOfPause = 0.0;
    protected double totalPauseTime = 0.0;
    private double lastTime; // NS-2 had this as DOUBLE
    private int id;
    private long timerSequence = 0;
    
    // PROXIES-------------------------------------------
    private Mac802_15_4	macEntity;
    private Phy802_15_4 phyEntity;
    private TimerInterface802_15_4 self;
    

    public macBeaconRxTimer(Mac802_15_4 macEntity, Phy802_15_4 phyEntity, int id)// macBeaconRxTimer(Mac802_15_4Impl *m) : 
    {
        if(!JistAPI.isEntity(macEntity)) throw new IllegalArgumentException("expected entity");
        if(!JistAPI.isEntity(phyEntity)) throw new IllegalArgumentException("expected entity");
        this.macEntity = macEntity;
        this.phyEntity = phyEntity;
        self = (TimerInterface802_15_4)JistAPI.proxy(this, TimerInterface802_15_4.class);
        lastTime = 0; // not 0.0 since in Jist time is double
        this.id = id;
    }
    
    
     public void	start(boolean reset, boolean fortx, double wt)
    {
        // DO NOT IMPLEMENT> Only for macBcnTxTimer
    }
     
     public void startTimer(double wtime, MacMessage_802_15_4 p)
    {
        // DO NOT IMPLEMENT
    }

    public void	start()
    {
        double BI,bcnRxTime,now,len12s,wtime;
        double tmpf;

        BI = (Const.aBaseSuperframeDuration * (1 << macEntity.getMacBeaconOrder2())) / phyEntity.getRate_BitsPerSecond('s');
        bcnRxTime = macEntity.getMacBcnRxTime() / phyEntity.getRate_BitsPerSecond('s');
        now = JistAPI.getTime()/Constants.SECOND;
        while (now - bcnRxTime > BI)
                bcnRxTime += BI;
        len12s = 12 / phyEntity.getRate_BitsPerSecond('s');

        /* Linux floating number compatibility
        wtime = BI - (now - bcnRxTime);
        */
        {
        tmpf = (now - bcnRxTime);;
        wtime = BI - tmpf;
        }

        if (wtime >= len12s)
                wtime -= len12s;

        /* Linux floating number compatibility
        if (now + wtime - lastTime < BI - len12s)
        */
        tmpf = now + wtime;
        if (tmpf - lastTime < BI - len12s)
        {
                tmpf = 2 * BI;
                tmpf = tmpf - now;
                tmpf = tmpf + bcnRxTime;
                wtime = tmpf - len12s;
                //wtime = 2* BI - (now - bcnRxTime) - len12s;
        }
        lastTime = now + wtime;
        self.startTimer(wtime);
    }
    
    public void	start(double time, boolean onlycap)
    {
        // DO NOT IMPLEMENT> Only for macExtractTimer
    }

    public void	timeout(long currentTimerSequence)
    {
        if (currentTimerSequence == timerSequence)
            if (!canceled && !terminated && !paused)
            {
                resetTimer();
                terminated = true;
                macEntity.beaconRxHandler();             
            }
    }
    
    // -------------------------------------------
      public void resetTimer()
    {
        started    = false;
        busy       = false;
        paused     = false;
        canceled   = false;
        terminated = false;
        stime = 0;
        wtime = 0;
        timerSequence++;
    }
    
   
    /**
     * Start timer
     */
    public void startTimer(double time)
    {
        assert(!busy && !started);
	busy = true;
	stime = ((double)JistAPI.getTime())/Constants.SECOND;
	wtime = time;
	assert(wtime >= 0);
	//s.schedule(this, /* & */event, wtime);
        timerSequence++;
        long currentTimerSequence = timerSequence;
        JistAPI.sleep((long)(wtime * Constants.SECOND));
        self.timeout(currentTimerSequence);
    }
    
    /**
     * Cancel timer
     */ 
    public void cancel()
    {
    	//assert(started);   commented on 2010-03-15
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
    
    private double expire()
    {
        return ((double)JistAPI.getTime())/Constants.SECOND - stime - totalPauseTime > wtime ? 0 : wtime - (((double)JistAPI.getTime())/Constants.SECOND - stime - totalPauseTime);
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
