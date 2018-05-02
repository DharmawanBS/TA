/*
 * EnergyManagement.java
 *
 * Created on March 20, 2008, 10:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.models.energy.energyconsumptionmodels;

import sidnet.models.energy.batteries.Battery;

/**
 *
 * @author Oliver
 */
public interface EnergyManagement
{
    public Battery getBattery();
    
    public EnergyConsumptionModel getEnergyConsumptionModel();
}
