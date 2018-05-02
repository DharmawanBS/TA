/*
 * Battery.java
 *
 * Created on October 25, 2007, 3:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Oliviu Ghica, Northwestern University
 *
 * Interface that should declare the functionality of a battery
 */

package sidnet.models.energy.batteries;

public interface Battery {
    public static final int INF            = -1;
    public static final int EMPTY          = 0;
    public static final int FULL           = 100;
    
  
    public double getEnergyLevel_mJ();
    
    /* Get Energy Level as a percentage
     * <p>
     * @return double     the energy level of the battery [%]
     *
     */
    public double getPercentageEnergyLevel();
    
    /* Get the capacity of the battery
     * <p>
     * @return double     the capacity of the battery [mJ]
     */
    public double getCapacity_mJ();
    
   
    
    /*
     * Returns the specified voltage of this battery
     */
    public double getVoltage();
}
