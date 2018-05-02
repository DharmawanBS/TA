/*
 * DummyPhenomenon.java
 *
 * Created on March 4, 2008, 3:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.models.senseable.phenomena;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;

/**
 *
 * @author Oliver
 */
public class DummyPhenomenon
extends PhenomenaLayerInterface {
    private double dummyValue = 0;
    
    /** Creates a new instance of DummyPhenomenon */
    public DummyPhenomenon() {
        // NOTHING TO DO
    }
    
    public void configureGUI(JPanel hostPanel) {
        // NOT USING GUI
    }
    
   public void repaintGUI() {
       // NOT USING A GUI
   }
   
   public void setVisibleGUI(boolean visible) {
       // NOT USING A GUI
   }
    
    public void configureMenu(JPopupMenu hostPopupMenu) {
        // NOT USING MENUS
    }
    
    public void enableUI() {
        // NOT USING A GUI
    }
    
    public void disableUI() {
        // NOT USING A GUI
    }
    
    public void passMenuActionEvent(ActionEvent menuEvent) {
        // NOT USING A MENU
    }
    
    public void terminate(){};
    
    public void init(){};
    
    public void updateSimulationTimeToCurrent()
    {
        // NOT TIME DEPENDENT
    }
    
   public double readDataAt(Location2D location, LocationContext locationContext) {
       return dummyValue; // flat dummy value returned
   }
   
   public Map<String, Object> getSensorReadings(Location2D physicalLocation,
			LocationContext physicalLocationContext) {	   
		return (Map<String, Object>)(new HashMap<String, Object>().put("ANALOG_VALUE", dummyValue));
	}
    
    public void actionPerformed(ActionEvent e) {   
        // NOT LISTENING TO ANY ACTION
    }

	public Map<String, Object> getSensorProperties() {
		return null;
	}
}
