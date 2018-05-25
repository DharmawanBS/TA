/*
 * PercentualTimeDrainingBattery.java
 *
 * Created on February 9, 2008, 7:59 PM
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
 * A more realistic battery that takes into account the internal leakage, which is independent of wether the battery is being used or not, which is proportional
 * to the remaining energy level
 */
public class PercentualTimeDrainingBattery extends IdealBattery
{
    private double percentageLossPerMinute;
    private long lastTimestampMinutes;
    
    /** 
     * Constructor
     * <p>
     * @param capacity  the capacity of the battery expressed in milliammpere-hours [mJ]
     * @param voltage    the operational voltage of the battery, which is needed to convert [mAh] in [mJ]
     * @param percentageLossPerMinute the amount of energy lost [mJ] per unit of minute relative to the capacity of the battery
     */
    public PercentualTimeDrainingBattery(double capacityMJ, double voltage, double percentageLossPerMinute)
    {
        super(capacityMJ, voltage);
        if (percentageLossPerMinute < 0)
            percentageLossPerMinute = 0;
        if (percentageLossPerMinute > 100)
            percentageLossPerMinute = 100;
        this.percentageLossPerMinute = percentageLossPerMinute;
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
            remainingEnergy -= (timestampMinutes - lastTimestampMinutes) * percentageLossPerMinute / 100 * capacity;
        else
            return;
        lastTimestampMinutes = timestampMinutes;
    }
    
}
