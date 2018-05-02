/*
 * BatteryControl.java
 *
 * Created on March 20, 2008, 11:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.models.energy.batteries;

/**
 *
 * @author Oliver
 */
public interface BatteryControl
{
  /* Deplete the indicated amount
     * <p>
     * @param amount      amount of energy [mJ] that should be depleted
     *
     */
    public void depleteAmount(double amount);
    /* Get Energy Level
     * <p>
     * @return double      the level of the battery [mJ]
     *
     */
    
     /* 
     * Deplete a battery. A battery that has infinite capacity cannot be depleted.
     */
    public void deplete();
    
    /* 
     * Recharge a battery to the designated level [mAh]s. A battery that has infinite capacity cannot be recharged.
     */
    public void recharge(double levelMAH);
}
