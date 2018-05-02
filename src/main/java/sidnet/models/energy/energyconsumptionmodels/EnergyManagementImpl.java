/*
 * EnergyManagementImpl.java
 *
 * Created on March 20, 2008, 10:41 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.models.energy.energyconsumptionmodels;

import jist.runtime.JistAPI;
import sidnet.models.energy.batteries.Battery;
import sidnet.models.energy.batteries.BatteryControl;

/**
 *
 * @author Oliver
 */
public class EnergyManagementImpl implements EnergyManagement, JistAPI.Timeless
{
    // DEBUG
    private static final boolean DEBUG = false;
    
    private EnergyConsumptionModel energyConsumptionModel;
    private Battery battery;
    private Battery stubBattery;
    
    /** Creates a new instance of EnergyManagementImpl */
    public EnergyManagementImpl(EnergyConsumptionModel energyConsumptionModel, Battery battery)
    {
        this.energyConsumptionModel = energyConsumptionModel;
        this.battery     = battery;
        stubBattery = new StubBattery(battery);
    }
    
    public Battery getBattery()
    {
        return stubBattery; // we never allow the user to interact with the real battery
    }
    
    public EnergyConsumptionModel getEnergyConsumptionModel()
    {
        return energyConsumptionModel;
    }
    
    public class StubBattery implements Battery, BatteryControl
    {
        Battery battery;
        
        public StubBattery (Battery battery)
        {
            this.battery = battery;
        }
        
        /* Get Energy Level
         * <p>
         * @return double      the level of the battery [mJ]
         *
         */
        public double getEnergyLevel_mJ()
        {
            if (DEBUG) System.out.println("[DEBUG][EnergyManagementImpl.StubBattery.getEnergyLevel_mJ]");
            if (battery == null || battery.getEnergyLevel_mJ() == battery.INF)
            {
                if (DEBUG) System.out.println("[DEBUG][EnergyManagementImpl.StubBattery.getEnergyLevel_mJ] - battery.capacity = INF");
                return battery.INF;
            }
            
            double energyLostDueToActivity = battery.getCapacity_mJ() - ((EnergyConsumptionModelAccessible)energyConsumptionModel).getEnergyLevel_mJ(); 
            double remainingEnergy =  battery.getEnergyLevel_mJ() - energyLostDueToActivity;
            if (remainingEnergy < 0)
                remainingEnergy = 0;
            if (DEBUG) System.out.println("[DEBUG][EnergyManagementImpl.StubBattery.getEnergyLevel_mJ] - energyLostDueToActivity = " + energyLostDueToActivity);
            if (DEBUG) System.out.println("[DEBUG][EnergyManagementImpl.StubBattery.getEnergyLevel_mJ] - remainingEnergy" + remainingEnergy);
            return remainingEnergy;
        }

        /* Get Energy Level as a percentage
         * <p>
         * @return double     the energy level of the battery [%]
         *
         */
        public double getPercentageEnergyLevel()
        {
            double remainingEnergy = getEnergyLevel_mJ();
         
            if (remainingEnergy != INF)
                return (remainingEnergy * 100 / battery.getCapacity_mJ());
            else
                return 100; /* % */
        }

        /* Get the capacity of the battery
         * <p>
         * @return double     the capacity of the battery [mJ]
         */
        public double getCapacity_mJ()
        {
            return battery.getCapacity_mJ();
        }

        /* 
         * Deplete a battery. A battery that has infinite capacity cannot be depleted.
         */
        public void deplete()
        {
             ((BatteryControl)battery).deplete();
        }
        
        public void depleteAmount(double amount)
        {
            // battery.depleteAmount(); NOT USED. NOT DESIGNED TO BE USED THIS WAY.
        }
        

        /* 
         * Recharge a battery to the designated level [mAh]s. A battery that has infinite capacity cannot be recharged.
         */
        public void recharge(double amount)
        {
            ((BatteryControl)battery).recharge(amount);
        }

         /*
         * Returns the specified voltage of this battery
         */
        public double getVoltage()
        {
            return battery.getVoltage();
        }
    }
    
}
