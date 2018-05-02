/*
 * AppInterface.java
 *
 * Created on February 7, 2008, 3:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

import jist.runtime.JistAPI;

/**
 *
 * @author Oliver
 */
@Deprecated
public interface TimerInterface 
extends JistAPI.Proxiable {
    public void startTimer(double wtime);
    public void cancel();
    public void timeout(); 
    public boolean isReady() throws JistAPI.Continuation;
}
