/*
 * NodeHardwareInterface.java
 * @version 1.0
 *
 * Created on October 25, 2007, 4:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import sidnet.models.energy.energyconsumptionmodels.EnergyManagement;
import jist.swans.net.NetAddress;

/**
 *
 * @author Oliviu Ghica, Northwestern University
 *
 * Interface that should "abstract" the main/common component entities of a node 
 */
public interface NodeHardwareInterface {
    /* Sets the IP address of the node
     * <p>
     * @param NetAddress ip  IP address represented as a NetAddress
     */
    public void setIP(NetAddress ip);
    
    /* Get the IP address of the node
     * <p>
     * @return NetAddress   IP address represented as a NetAddress
     */
    public NetAddress getIP();
    
      /* Get the energymanagement
     * <p>
     * @return EnergyManagement   the energy management handle
     */
    public EnergyManagement getEnergyManagement();
    
    /* Add a GPS to the node
     * <p>
     * @param GPS   a GPS object
     */
    public void setGPS(GPS gps);
    public GPS getGPS();
    
    /* Add a Sensor to the node. A node can have multiple sensors
     * <p>
     * @param Sensor   a Sensor object
     */
    public void addSensor(Sensor sensor);
    public Sensor getSensor(int sensorIndex);
}
