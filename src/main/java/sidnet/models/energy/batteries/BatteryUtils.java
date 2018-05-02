/*
 * BatteryUtilities.java
 *
 * Created on March 19, 2008, 8:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.models.energy.batteries;

/**
 *
 * @author Oliver
 */
public abstract class BatteryUtils
{    
    public static double mAhToMJ(double mAh, double voltage)
    {
        return mAh * voltage * 3600;
    }
    
    public static double MjTomAh(double mJ, double voltage)
    {
        return mJ / (voltage * 3600);
    }
}
