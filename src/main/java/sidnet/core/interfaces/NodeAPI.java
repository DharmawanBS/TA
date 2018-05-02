/*
 * NodeAPI.java
 * @version 1.0
 * Created on October 25, 2007, 3:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import sidnet.models.energy.energyconsumptionmodels.EnergyManagement;
import sidnet.core.gui.NodeGUIimpl;
import java.util.List;
import jist.swans.net.NetAddress;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;
import sidnet.core.misc.NCS_Location2D;
import sidnet.core.query.Query;
import sidnet.models.energy.energyconsumptionmodels.EnergyManagement;
import sidnet.core.gui.NodeGUIimpl;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;
import sidnet.core.misc.NCS_Location2D;
import sidnet.core.query.Query;

/**
 *
 * @author Oliver
 *
 * Defines the main interface between SWANS and SIDnet
 */
public interface NodeAPI {
    /** 
     * Gets the unique ID that has been associated with the node. It is the integer representation of the IP address
     */
    public int getID();
    
    /** 
     * Gets the IP address that has been associated with the node 
     */
    public NetAddress getIP();
    
    /**
     * Get the TYPE of the node. Assigning nodes types can be useful when dealing with heterogeneous networks
     */
    public byte getType();
    
    /** 
     * Set the TYPE of the node. Assigning nodes types can be useful when dealing with heterogeneous networks to be able to distinguish between the nodes
     */
    public void setType(byte type);
    
     /* Get the energymanagement
     * <p>
     * @return EnergyManagement   the energy management handle
     */
    public EnergyManagement getEnergyManagement();
    
    /**
     * Returns the list of queries that have been posted by the client through this node. It is update by the Terminal 
     */
    public List<Query> getQueryList();
    
    /**
     * Adds a query to the query list. This is used to set the query without automatically
     */
    public void addQuery(Query query);
    
    /**
     * Return the SimControl handle
     */
    public SimControl getSimControl();
    
    /**
     * Get the geographical location of the sensor node in real metric units, not in screen coordinates
     * <p>
     * @returns Location2D
     */
    public Location2D getLocation2D();
    
    /** Returns the location of this node as an NCS (Normalized Coordinate System) */
    public NCS_Location2D getNCS_Location2D();

    /** Returns the LocationContext in which the getLocation2D returns the measurements */
    public LocationContext getLocationContext();
    
    /** 
     * Reads sensor data
     * <p>
     * @param sensorIndex   indexes through the registered sensors, in case a node has more than one sensing capabilities. Otherwise, set to 0
     * @returns double      numerical value between 0 ... 100 
     */
    public double readAnalogSensorData(int sensorIndex);
    
    /**
     * Appends the supplied string to the console associated with the Terminal of the calling node 
     * <p>
     * @param String 
     */       
    public void appendTerminalText(String s);
    
    /**
     * Gets the handle over the NodeGUIimpl which contains functions related to GUI
     */
    public NodeGUIimpl getNodeGUI();
    
    /* Establishes a meaning of sending events from SIDnet modules to the SWANS application layer
     * <p> 
     * @param CallbackInterface   the callback interface of the app that implements the CallbackInterface 
     */
    public void setAppCallback(CallbackInterface appCallback);
    
     /* Retrieves the callback of the class that implements the CallbackInterface
     * <p> 
     * @returns CallbackInterface   the callback interface of the app that implements the CallbackInterface 
     */
    public CallbackInterface getAppCallback();
}
