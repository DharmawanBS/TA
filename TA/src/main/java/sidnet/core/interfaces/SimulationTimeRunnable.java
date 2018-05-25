/*
 * SimulationTimeRunnable.java
 *
 * Created on October 22, 2007, 8:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import jist.runtime.JistAPI;

/**
 *
 * @author Oliver
 */
public interface SimulationTimeRunnable extends JistAPI.Proxiable{
    
    void run();
}
