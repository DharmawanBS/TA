/*
 * ConstantTimeDrainingBattery.java
 *
 * Created on February 9, 2008, 7:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.models.energy.batteries;

import jist.runtime.JistAPI;
import jist.swans.Constants;

/**
 *
 * @author Oliver
 * This battery model improves the ideal battery model taking into account the fact that a battery has an internal drainage (leak) which is independent of 
 * wether the battery is being used or not
 */
public class LinearTimeDrainingBattery extends IdealBattery
{
    private final double energyLossAmountPerMinute;
    private long lastTimestampMinutes = 0;
    
    /** 
     * Creates a new instance of ConstantTimeDrainingBattery
     * <p>
     * @param capacity  the capacity of the battery expressed in milliammpere-hours [mJ]
     * @params voltage    the operational voltage of the battery, which is needed to convert [mAh] in [mJ]
     * @param energyLossAmountPerSecond the amount of energy lost [mJ] per unit of second
     */
    public LinearTimeDrainingBattery(double capacityMJ, double voltage, double energyLossAmountPerMinute)
    {
        super(capacityMJ, voltage);
        this.energyLossAmountPerMinute = BatteryUtils.mAhToMJ(energyLossAmountPerMinute, voltage);
    }
    
    public double getEnergyLevel_mJ()
    {
        update();
        return super.getEnergyLevel_mJ();
    }
    
    public double getPercentageEnergyLevel()
    {
        update();
        return super.getPercentageEnergyLevel();
    }
    
    private void update()
    {
        long timestampMinutes = JistAPI.getTime() / Constants.MINUTE;
        if (timestampMinutes > lastTimestampMinutes)
            remainingEnergy -= (timestampMinutes - lastTimestampMinutes) * energyLossAmountPerMinute;
        else
            return;
        lastTimestampMinutes = timestampMinutes;
    }
  
}
