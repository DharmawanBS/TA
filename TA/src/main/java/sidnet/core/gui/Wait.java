/*
 * Wait.java
 *
 * Created on July 28, 2005, 1:15 PM
 */

/**
 *
 * @author  Oliviu Ghica
 */
package sidnet.core.gui;

public class Wait {
    
    /** Creates a new instance of Wait */
     public static void WaitASecond(double s) {
        try {
           Thread.currentThread().sleep((long)(s * 1000));
        }
        catch (InterruptedException e) {
           e.printStackTrace();
        }  
    }
}
