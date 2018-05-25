/*
 * Sleeper.java
 *
 * Created on June 6, 2007, 4:06 PM
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
public class Sleeper implements JistAPI.DoNotRewrite{
    
    /** Creates a new instance of Sleeper */
    public Sleeper() {
    }
    
    public static void Sleep(long ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ie){/*do nothing */};
    }
    
}
