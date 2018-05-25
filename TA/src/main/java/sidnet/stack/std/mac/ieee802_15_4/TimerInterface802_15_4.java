/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import jist.runtime.JistAPI;

/**
 *
 * @author Oliver
 */
public interface TimerInterface802_15_4 extends JistAPI.Proxiable{
    public void startTimer(double wtime, MacMessage_802_15_4 p);
    public void startTimer(double wtime);
    public void	start(double time, boolean onlycap);
    public void	start(boolean reset, boolean fortx, double wt);
    public void start();
    public void stopTimerr();
    public void cancel();
    public void timeout(long currentTimerSequence);
    public void pause();
    public void resume();
    public void resetTimer() throws JistAPI.Continuation;
   
    
    boolean canceled() throws JistAPI.Continuation;
    boolean bussy() throws JistAPI.Continuation;
    boolean paused() throws JistAPI.Continuation;
    
}
