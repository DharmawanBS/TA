/*
 * GenericDynamicPhenomenon.java
 * @version 1.0
 *
 * Created on October 4, 2007, 2:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.models.senseable.phenomena;

import sidnet.core.misc.Location2D;
import sidnet.core.misc.TransientDataGrid;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import jist.swans.Constants;
import sidnet.core.misc.DataGrid;
import sidnet.core.misc.LocationContext;
/**
 *
 * @author Oliver
 */
public class GenericDynamicPhenomenon extends PhenomenaLayerInterface{
    private static final int DEFAULT_ROW_COUNT    = 8;
    private static final int DEFAULT_COLUMN_COUNT = 8;
    private static final int MAX_ROW_COUNT        = 32;
    private static final int MAX_COLUMN_COUNT     = 32;
    private static final int DEFAULT_RESOLUTION   = 8; //X  MUST BE A POWER OF TWO
    private static       long DEFAULT_EPOCH_TIME_INTERVAL = 1*60*1000; // 1 minute [ms]
    
    private int core_row_count, core_column_count;
    private int interp_row_count, interp_column_count;
    private int renderingResolution;
    private int spatialInterpolationMethod;
    private long epochTimeInterval;

    private TransientDataGrid coreGrid;
    
    private boolean show = false;
   
    // Menu
    private JPopupMenu hostPopupMenu;
    private JMenuItem menuItemShowPhysics;
    
    public GenericDynamicPhenomenon(long timeBase)
    {
         this.core_row_count = DEFAULT_ROW_COUNT;
         this.core_column_count = DEFAULT_COLUMN_COUNT;
         this.renderingResolution = DEFAULT_RESOLUTION;
         this.epochTimeInterval = timeBase / Constants.MILLI_SECOND;
         
         spatialInterpolationMethod = DataGrid.BILINEAR;
         
         coreGrid = new TransientDataGrid(core_row_count, core_column_count, renderingResolution, epochTimeInterval, spatialInterpolationMethod);
    }
    
    /** Creates a new instance of GenericDynamicPhenomenon */
    public GenericDynamicPhenomenon() {       
         this(DEFAULT_EPOCH_TIME_INTERVAL * Constants.MILLI_SECOND);
    };
    
    public void configureGUI(JPanel hostPanel)
    {
        coreGrid.configureGUI(hostPanel);
    }
    
   public void repaintGUI()
   {
       if (show)
            coreGrid.repaintGUI();
   }
   
   public void setVisibleGUI(boolean visible)
   {
       if (show == true)
           coreGrid.setVisibleGUI(visible); 
   }
    
    public void configureMenu(JPopupMenu hostPopupMenu)
    {
        this.hostPopupMenu = hostPopupMenu;
        menuItemShowPhysics = new JMenuItem("Show/Hide Phenomena Layer");
        menuItemShowPhysics.addActionListener(this);
        hostPopupMenu.add(menuItemShowPhysics);
        
        coreGrid.configureMenu(hostPopupMenu);
        coreGrid.disableUI();
    }
    
    public void enableUI()
    {
        coreGrid.enableUI();
    }
    
    public void disableUI()
    {
        coreGrid.disableUI();
    }
    
    public void passMenuActionEvent(ActionEvent menuEvent)
    {
        coreGrid.passMenuActionEvent(menuEvent);
    }
    
    public void terminate(){};
    
    public void init(){};
    
    public void updateSimulationTimeToCurrent()
    {
        coreGrid.updateSimulationTimeToCurrent();
    }
    
   public double readDataAt(Location2D location, LocationContext locationContext) {
       return coreGrid.readDataAt(location, locationContext);
   }
   
   public Map<String, Object> getSensorReadings(Location2D physicalLocation,
			LocationContext physicalLocationContext) {	   
		return coreGrid.getSensorReadings(physicalLocation, physicalLocationContext);
	}
    
    public void actionPerformed(ActionEvent e) {
        
        if (e.getActionCommand() == "Show/Hide Phenomena Layer")
        {
            show = !show;
            
            if (show)
            {
                coreGrid.updateSimulationTimeToCurrent();
                coreGrid.setVisibleGUI(show);
                coreGrid.enableUI();
            }
            else
            {
                coreGrid.setVisibleGUI(false);
                coreGrid.disableUI();
            }
        }
     }

	public Map<String, Object> getSensorProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}
