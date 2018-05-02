/*
 * AppInterface.java
 *
 * Created on February 7, 2008, 3:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.timers;

import jist.runtime.JistAPI;

/**
 *
 * @author Oliver
 */
public interface JistTimerInterface 
extends JistAPI.Proxiable {
    public void startTimer(long wtime);
    public void cancelAndReset() throws JistAPI.Continuation; 
    public boolean isBusy() throws JistAPI.Continuation;
    public boolean expired() throws JistAPI.Continuation;
}
