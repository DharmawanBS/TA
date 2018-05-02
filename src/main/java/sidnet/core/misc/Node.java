/*
 * Node.java
 *
 * @version 1.0.1
 *
 * Created on April 27, 2007, 2:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

import sidnet.core.query.Query;
import jist.swans.net.NetAddress;
import jist.swans.route.RouteInterface;
import sidnet.core.terminal.TerminalDataSet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jist.swans.Constants;
import jist.swans.field.Field;
import sidnet.models.energy.energyconsumptionmodels.EnergyManagement;
import sidnet.core.gui.NodeGUIimpl;
import sidnet.core.gui.PanelContext;
import sidnet.core.interfaces.CallbackInterface;
import sidnet.core.interfaces.ColorProfile;
import sidnet.core.interfaces.GPS;
import sidnet.core.interfaces.NodeAPI;
import sidnet.core.interfaces.NodeHardwareInterface;
import sidnet.core.interfaces.Sensor;
import sidnet.core.interfaces.SimControl;
import sidnet.core.simcontrol.SimManager;

/**
 *
 * @author Oliviu Ghica, Nortwhestern University
 * 
 * @version 1.0 
 * @version 1.0.1 fixed the queryList not being updated by the terminal
 *
 * Placeholder for various non-graphical information. Acts as an interface between the SIDnet and SWANS
 * The QueryList hosted in this object represents the list of queries posted through the associated terminal (root)
 */
public class Node implements NodeAPI, NodeHardwareInterface{
    private EnergyManagement energyManagement;
    private List<Sensor> sensorList;
    private GPS gps;
    private static LocationContext fieldContext;         // real field units, not screen dimensions
    private Location2D fieldLocation;                   // real field units, not screen dimensions
    
    private int id;
    private NetAddress ip;
   
    // Application running with this
    private RouteInterface routing;
    
    private List<Query> queryList = null;          // The set of queries that were posted through this node's terminal
    
    private static SimManager simControl;
    
    // some other data
    public long packetReceivedCount = 0;
    public long packetSentCount = 0;
    
    /** list of neighbours. */
    public NodesList neighboursList;        // for users, keeps an updated list of neighbors (list may shrink if nodes die)
    public NodesList physicalNeighboursList; // for statistics only, keeps the list of topological neighbors (list will not shrink if nodes die)
    
    private NodeGUIimpl nodeGUI;
    
    private CallbackInterface appCallback;
    
    /** The type of a node. Node type can be useful to distinguish the nodes in a heterogeneous network */
    private byte type;
   
    private boolean faulty = false;
    
    /** Creates a new instance of Node */
    public Node(int id, EnergyManagement energyManagement, PanelContext hostingPanelContext, LocationContext fieldContext, ColorProfile colorCode, SimManager simControl){
        this.id = id;
        this.energyManagement = energyManagement;

        this.fieldContext = fieldContext;
        
        nodeGUI = new NodeGUIimpl(hostingPanelContext, colorCode, this);
        this.simControl = simControl;
        queryList = new LinkedList<Query>();
        sensorList = new LinkedList<Sensor>();
        nodeGUI.localTerminalDataSet = new TerminalDataSet(id);
        
        neighboursList = new NodesList();   
        physicalNeighboursList = new NodesList();
    }
    
    
    /* *********************************************************************** */
    
    
    
    /* ******* *
     * NodeAPI *
     * ******* */
    public List<Query> getQueryList()
    {
        return queryList;
    }
    
    public void addQuery(Query query)
    {
        if (queryList == null)
            queryList = new LinkedList<Query>();
        queryList.add(query);
    }
    
    /** Not member of NodeAPI */
    public void setQueryList(LinkedList<Query> terminalQueryList)
    {
        this.queryList = terminalQueryList;
    }
    
    public int getID() {
        return id;
    }
    
    /** Get the IP address of the node
     * <p>
     * @return NetAddress   IP address represented as a NetAddress
     */
    public NetAddress getIP()
    {
        return ip;
    }
    
    /** Set the TYPE of a node
     * <p>
     * @param short type   The type, as a short, numerical value, to identify a node's type in a heterogeneous network
     */
    public void setType(byte type)
    {
        this.type = type;
    }

    /** Get the TYPE of a node
     * <p>
     * @return short type   The type, as a short, numerical value, to identify a node's type in a heterogeneous network
     */

    public byte getType()
    {
        return type;
    }
    
    public void enableRelocation(Field field)
    {
        nodeGUI.enableRelocation(field);
    }
    
    public SimManager getSimManager() {
    	return simControl;
    }
    
    public SimControl getSimControl()
    {
        return simControl;
    }
    
    public Location2D getLocation2D()
    {
        return fieldLocation;
    }
    
    public NCS_Location2D getNCS_Location2D()
    {
        return fieldLocation.toNCS(fieldContext);
    }
    
    public LocationContext getLocationContext()
    {
        return fieldContext;
    }
    
    public void malfunctioned()
    {
        faulty = true;
    }
    
    public boolean isFaulty()
    {
        return faulty;
    }
     
    public double readAnalogSensorData(int sensorIndex){
        energyManagement.getEnergyConsumptionModel().simulateSensing(50 * Constants.MILLI_SECOND);
        return (double) getSensor(sensorIndex).readDataAt(getLocation2D(), getLocationContext());
        //return (double) getSensor(sensorIndex).getSensorReadings(getLocation2D(), getLocationContext()).get();
    };
    
    public Map<String, Object> getSensorReadings(int sensorIndex) {	   
		return getSensor(sensorIndex).getSensorReadings(getLocation2D(), getLocationContext());
	}
      
    public void appendTerminalText(String s)
    {
        boolean success;
        success = nodeGUI.getTerminal().appendConsoleText(nodeGUI.localTerminalDataSet, s);
        if (!success)
            nodeGUI.localTerminalDataSet.appendConsoleText(s);
    }
    
    public NodeGUIimpl getNodeGUI()
    {
        return nodeGUI;
    }
    
    
    /* *********************************************************************** */
    
    
    
    /* ********************* *
     * NodeHardwareInterface *
     * ********************* */
    public void setIP(NetAddress ip)
    {
        this.ip = ip;
    }
    
    public EnergyManagement getEnergyManagement()
    {
        return energyManagement;
    }
    
    public void setGPS(GPS gps)
    {
        this.gps = gps;
        this.setLocation2D(gps.getNCS_Location2D().fromNCS(fieldContext));
        this.nodeGUI.setPanelLocation2D(gps.getNCS_Location2D().fromNCS(nodeGUI.getLocationContext()));
    }
    
    public GPS getGPS() {
        return gps;
    }
    
    public void addSensor(Sensor sensor) {
        if (sensor != null)
            sensorList.add(sensor);
    }
    
    public Sensor getSensor(int sensorIndex) {
        if (sensorIndex > sensorList.size() - 1 || sensorIndex < 0)
            return null;
        
        return sensorList.get(sensorIndex);
    }
    
    
    /* *********************************************************************** */ 
    
   
    
    /* some extras for internal hook-up between the app-layer and the Terminal */
    public void setAppCallback(CallbackInterface appCallback)
    {
        this.appCallback = appCallback;
    }
    
    public CallbackInterface getAppCallback()
    {
        return appCallback;
    }
    
    /** Should be called at runtime if node placement changes programmatically
     *  Will not work if the node relocation is not enabled (aka, the nodeGUI.field is null
     */
    public void updateLocation2D(Location2D fieldLocation)
    {        
        nodeGUI.updateLocation2D(fieldLocation.convertTo(fieldContext, nodeGUI.getLocationContext()));        
    }
    
    public void setLocation2D(Location2D fieldLocation)
    {       
        this.fieldLocation = fieldLocation;

        nodeGUI.setPanelLocation2D(fieldLocation.toNCS(fieldContext).fromNCS(nodeGUI.getLocationContext()));
    }
    
    public double getEffectiveCoverage_ft()
    {
        LinkedList<NodeEntry> nodesList = neighboursList.getAsLinkedList();
        double maxDist = 0;
        
        for (NodeEntry nodeEntry: nodesList)
        {
            double dist = 0;
            if (nodeEntry != null && nodeEntry.getNCS_Location2D() != null)
                dist = nodeEntry.getNCS_Location2D().fromNCS(fieldContext).distanceTo(this.fieldLocation);
            if (dist > maxDist)
                maxDist = dist;
        }
        return maxDist;
    }
}
