/*
 * IdealBattery.java
 *
 * Created on February 9, 2008, 6:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.models.energy.batteries;

import jist.runtime.JistAPI;

/**
 *
 * @author Oliver
 *
 * An IdealBattery is insensitive to temperature, internal drainage, memory effects (for recharge).
 * It acts as a finite container of energy.
 *
 */
public class IdealBattery implements Battery, BatteryControl
{    
    public final double capacity;       //[mJ]
    public double remainingEnergy;      //[mJ]
    public double voltage;              //[V]
    
    /*
     * Creates a new instance of IdealBattery
     * <p>
     * @params capacity the capacity of the battery expressed in milliJoules [mJ]
     * @params voltage    the operational voltage of the battery, which is needed to convert [mAh] in [mJ]
     */
    public IdealBattery(double capacityMJ, double voltage)
    {
        this.voltage = voltage;
        if (capacityMJ < 0 && capacityMJ != INF)
            capacityMJ = 0;
        this.capacity = capacityMJ;
        remainingEnergy = capacityMJ;
    }
    
    public void depleteAmount(double amount)
    {
        if (capacity != INF && remainingEnergy != INF)
            remainingEnergy -= amount;
        
        if (remainingEnergy < 0)
            remainingEnergy = 0;
    }
    
    /* Get Energy Level
     * <p>
     * @return double      the level of the battery [mJ]
     *
     */
    public double getEnergyLevel_mJ()
    {
        return remainingEnergy;
    }
        
    /* Get Energy Level as a percentage
     * <p>
     * @return double     the energy level of the battery [%]
     *
     */
    public double getPercentageEnergyLevel()
    {
         if (remainingEnergy == 0)
            return 0;
        
        if (remainingEnergy != INF)
            return (remainingEnergy * 100 / capacity);
        else
            return 100; /* % */
    }
    
    /* Get the capacity of the battery
     * <p>
     * @return double     the capacity of the battery [mAh]
     */
    public double getCapacity_mJ()
    {
        return capacity;
    }
    
    /* 
     * Deplete a battery. A battery that has infinite capacity cannot be depleted.
     */
    public void deplete()
    {
        if (capacity != INF)
            remainingEnergy = 0;
    }
    
    /* 
     * Recharge a battery to the designated level [mAh]s. A battery that has infinite capacity cannot be recharged.
     */
    public void recharge(double amount)
    {
        if (amount == INF)
        {
            remainingEnergy = INF;
            return;
        }
        
        if (capacity != INF)
            remainingEnergy = BatteryUtils.mAhToMJ(amount, voltage);
        
        if (remainingEnergy > capacity)
            remainingEnergy = capacity;
    }
    
     /*
     * Returns the specified voltage of this battery
     */
    public double getVoltage()
    {
        return voltage;
    }
}
