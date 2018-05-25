/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.core.misc;

import jist.runtime.JistAPI;

/**
 *
 * @author Oliver
 */
public interface ProxiableTimerInterface extends JistAPI.Proxiable
{
    public void timeout();
    public void startTimer(double wtime);
    public void cancel();
}
