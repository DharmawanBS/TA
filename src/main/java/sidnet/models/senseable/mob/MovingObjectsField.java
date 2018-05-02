/*
 * MovingObjectsField.java
 *
 * Created on November 7, 2007, 1:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.models.senseable.mob;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import jist.runtime.JistAPI;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;
import sidnet.core.misc.NCS_Location2D;
import sidnet.models.senseable.phenomena.PhenomenaLayerInterface;

/**
 *
 * @author Oliver
 */
public class MovingObjectsField 
extends PhenomenaLayerInterface {

	public static enum MANAGERS {MODELBASED, TRACEBASED}
	
    /** Creates a new instance of MovingObjectsField */    
    private int sensingRangeFt;
    
    // GUI
    private boolean show = true;
    private MovingObjectsPanel mobPanel;    
    private List<MobilityManager> mobilityManagers;
    private List<Location2D> mobs;
    
    // Menu
    private JMenuItem menuItemShowMob, xmlLoaderMenuItem;
    
    // Sensor Properties
    private HashMap<String, Object> sensorProperties = new HashMap<String, Object>();
        
    private LocationContext actualFieldLocationContext;
    
    /**
     * 
     * @param sensingRangeFt
     * @param fieldLocationContext
     * @param forceWithinNetworkBounds - if "true", forces the moving object to stay within the bounds of the sensor network
     *                                     by either preventing or mirroring all locations that fall outside the network bounds, inside. 
     */
    public MovingObjectsField(int sensingRangeFt, LocationContext fieldLocationContext, boolean forceWithinNetworkBounds) {        
        this.sensingRangeFt = sensingRangeFt;
    	mobPanel = new MovingObjectsPanel();
        
        sensorProperties.put("SENSING_RANGE", sensingRangeFt);
        
        mobs = new LinkedList<Location2D>();
        
        this.actualFieldLocationContext = fieldLocationContext;
        
        mobilityManagers = new LinkedList<MobilityManager>();
        mobilityManagers.add(new ModelBasedMobilityManager());
        mobilityManagers.add(new TraceBasedMobilityManager(fieldLocationContext, forceWithinNetworkBounds));       
    }
    
    public void init() {
        // not used
    }
    
    public void terminate() {
        // not used
    }
    
    public void updateSimulationTimeToCurrent(){
        // not used
    }
    
    public double readDataAt(Location2D location,
    						 LocationContext locationContext) {
    	this.updateMobs(); // for painting performance purposes
    	
    	for (Location2D mob: mobs)
    		if (mob != null) // TODO should not happen
    			if ( mob.distanceTo(location) < sensingRangeFt)
    				return 1 - mob.distanceTo(location) / sensingRangeFt;           

    	return 0.0;
    }
    
    public Map<String, Object> getSensorReadings(Location2D location,
			LocationContext locationContext) {

    	this.updateMobs(); // for painting performance purposes
    	
		Map<String, Object> measurementData = new HashMap<String, Object>();
		
		double range = 0.0;
		Location2D absoluteLocation = null;
		
		for (Location2D mob: mobs)
    		if (mob != null) // TODO should not happen
    			if ( mob.distanceTo(location) < sensingRangeFt) {
    				range = 1 - mob.distanceTo(location) / sensingRangeFt;
    				absoluteLocation = mob;
    			}

    	measurementData.put("RANGE_MEASUREMENT", range);
    	measurementData.put("ABSOLUTE_LOCATION", absoluteLocation);
		
		return measurementData;
	}
    
    public Location2D getLocationOfClosestMob(Location2D location, LocationContext locationContext) {
    	for (Location2D mob: mobs)
    		if ( mob.distanceTo(location) < sensingRangeFt)           	 
    			return mob; 	
    	return null;
    }
    
    public void configureGUI(JPanel hostPanel) {
        mobPanel.configureGUI(hostPanel);
    }
    
    public void repaintGUI() {
        this.updateMobs(); 
        mobPanel.updateMobs(mobs, actualFieldLocationContext);
        mobPanel.repaintGUI();
    }
    
    public void setVisibleGUI(boolean visible) {
        mobPanel.setVisibleGUI(visible);
    }
    
    public void configureMenu(JPopupMenu hostPopupMenu) {
        menuItemShowMob = new JMenuItem("Show/Hide Moving Object Field");
        xmlLoaderMenuItem = new JMenuItem("Load Moving Object Profiles");
        xmlLoaderMenuItem.setVisible(false);
        menuItemShowMob.addActionListener(this);
        xmlLoaderMenuItem.addActionListener(this);
        hostPopupMenu.add(menuItemShowMob);
        hostPopupMenu.add(xmlLoaderMenuItem);
        
        // enable by default
        actionPerformed(new ActionEvent(this, 1, "Show/Hide Moving Object Field"));
    }
     
    public void enableUI() {
        // nothing
    }
    
    public void disableUI() {
        // nothing
    }
    
    public void passMenuActionEvent(ActionEvent menuEvent) {
        ((TraceBasedMobilityManager)getMobilityManager(MANAGERS.TRACEBASED)).actionPerformed(menuEvent);
    }     
     
    public void updateMobs() {             	
         mobs.clear();
         for (MobilityManager MM: mobilityManagers)
     		for (MobilityModel mm : MM.getMobilityModelsList()) {
     			Location2D mob = mm.nextLocation(JistAPI.getTime(), actualFieldLocationContext);
                mobs.add(mob);                    
         }
     }
 
    public int getMobCount() {
    	 int sum = 0;
    	 for (MobilityManager MM: mobilityManagers)
    		 sum += MM.getMobCount();
    	 return sum;
    }
    
    public MobilityManager getMobilityManager(MANAGERS managerTag) {    	
    	if (managerTag == MANAGERS.MODELBASED)
    		return mobilityManagers.get(0);
    	if (managerTag == MANAGERS.TRACEBASED)
    		return mobilityManagers.get(1);    	
    	return null;
    }
 

	public final Map<String, Object> getSensorProperties() {
		return sensorProperties;
	}

	public void actionPerformed(ActionEvent e) {        
        if (e.getActionCommand() == "Show/Hide Moving Object Field") {
            show = !show;
            xmlLoaderMenuItem.setVisible(show);
        }   
        passMenuActionEvent(e);
    }
}
