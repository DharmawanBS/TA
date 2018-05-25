/*
 * TransientDataGrid.java
 * @version 1.0
 *
 * @version 1.0.1
 * ==> Added support for bounding the min/max value that are randomly generated
 *
 * Created on April 20, 2007, 3:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


/*
 * A transient data grid represent an intermediate state of a DataGrid as it evolves, temporarly, from a given START grid to a given TARGET grid
 * Both grids must have the same dimension
 */

package sidnet.core.misc;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import sidnet.core.interfaces.SIDnetDrawableInterface;
import sidnet.core.interfaces.SIDnetTimeDependable;
import jist.runtime.JistAPI;
import jist.swans.Constants;
import sidnet.core.interfaces.DataExchangeReadableOnly;
import sidnet.core.interfaces.SIDnetMenuInterface;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import sidnet.models.senseable.phenomena.PhenomenaConfigurationFrame;

/**
 *
 * @author Oliver
 */
public class TransientDataGrid 
implements SIDnetDrawableInterface, SIDnetTimeDependable, DataExchangeReadableOnly, SIDnetMenuInterface{
    private DataGrid startGrid, targetGrid;     // same size grids
    private DataGrid currentGrid;
    private long epochTimeInterval;
    private long startEpochTimestamp;
    private long currentTimestamp;
    private long lastRefreshTimestamp;
    
    
    private double temporalIndex;               // [0 ... 1]
    private int spatialInterpolationMethod;     // See DataGrid for possible methods
    
    private PhenomenaConfigurationFrame configurationFrame;
    
    // Menus
    JMenuItem menuItemConfigure = new JMenuItem("Configure ...");
    
    
    public void configureGUI(JPanel hostPanel)
    {
        currentGrid.configureGUI(hostPanel);
    }
    
    public void repaintGUI()
    {
        currentGrid.repaintGUI();
    }
    
    public void setVisibleGUI(boolean visible)
    {
        currentGrid.setVisibleGUI(visible);
    }
    
    
    public void configureMenu(JPopupMenu hostPopupMenu)
    { 
        currentGrid.configureMenu(hostPopupMenu);
        menuItemConfigure.addActionListener(this);
        hostPopupMenu.add(menuItemConfigure);
    }
    
    public void enableUI()
    {
        menuItemConfigure.setVisible(true);
        currentGrid.enableUI();
    }
    
    public void disableUI()
    {
        menuItemConfigure.setVisible(false);
        currentGrid.disableUI();
    }
    
    public void passMenuActionEvent(ActionEvent menuEvent)
    {
        currentGrid.passMenuActionEvent(menuEvent);
    }
    
    public void updateSimulationTimeToCurrent()
    {
        this.update(JistAPI.getTime()/Constants.MILLI_SECOND);
    }
  
    
    /** Creates a new instance of TransientDataGrid */
    public TransientDataGrid(int rowNum, int colNum, int renderingResolution, long epochTimeInterval, int spatialInterpolationMethod) {
        startGrid = new DataGrid(renderingResolution, DataGrid.SHADE, rowNum, colNum);
        startGrid.seedGrid();

        currentGrid = new DataGrid(renderingResolution, DataGrid.SHADE, rowNum, colNum);
        
        targetGrid = new DataGrid(renderingResolution, DataGrid.SHADE, rowNum, colNum);
        targetGrid.seedGrid();
        
        this.epochTimeInterval = epochTimeInterval;
        this.spatialInterpolationMethod = spatialInterpolationMethod;
        
        this.configurationFrame = new PhenomenaConfigurationFrame();
    }
    
    public int getTemporalInterpolationPointAt(int x, int y)
     {
        return getTemporalInterpolationPointAt(x, y, temporalIndex);
     }
    
    public int getTemporalInterpolationPointAt(int x, int y, double temporalIndex)
     {
         if (temporalIndex >= 0 && temporalIndex <=1)
         {
            double p_prev = startGrid.getInterpolatedPointAt(x, y, spatialInterpolationMethod);
            double p_next = targetGrid.getInterpolatedPointAt(x, y, spatialInterpolationMethod);
         
            return (int)(temporalIndex * p_next + (1 - temporalIndex) * p_prev);
         }
         else
             System.out.println("ERROR: Temporal Index must be between 0 and 1");
         
         return 0;
     }
   
    
    /* computes the currentGrid from the startGrid and targetGrid at the time given by temporalIndex */
    public void updateTemporalInterpolationGrid()
    {
        for (int i = 0; i < currentGrid.getRowCount(); i++)
            for (int j = 0; j < currentGrid.getColumnCount(); j++)
                currentGrid.setElementAt(i, j, (int)(temporalIndex * targetGrid.getElementAt(i,j) + (1 - temporalIndex) * startGrid.getElementAt(i,j)));
    }
    
    public void nextEpoch()
    {
        startEpochTimestamp = currentTimestamp;
        startGrid.copyDataFrom(targetGrid);
        targetGrid.seedGrid(configurationFrame.minSensingValue, configurationFrame.maxSensingValue);
    }
    
    public double readDataAt(Location2D location, LocationContext locationContext) {
        return currentGrid.readDataAt(location, locationContext);
    }
    
    public Map<String, Object> getSensorReadings(Location2D physicalLocation,
			LocationContext physicalLocationContext) {
		return currentGrid.getSensorReadings(physicalLocation, physicalLocationContext);
	}
    
    public int readGridValueAt(int x, int y, long timestamp)
    {
        update(timestamp);
        return getTemporalInterpolationPointAt(x, y);
    }
    
    public void update(long timestamp)
    {
        assert(timestamp >= currentTimestamp);//"GenericDynamicPhenomena.TransientDataGrid.update(): ERROR! Attempt to update to an earlier timestamp!");
        
        if (timestamp == currentTimestamp)
            return;
        
        // if outside the scope of the current epoch, then generate a new one
        if (timestamp - startEpochTimestamp > epochTimeInterval)
        {
            currentTimestamp = timestamp;
            nextEpoch();
        }
        else // update the temporal index
        {
            currentTimestamp = timestamp;
            temporalIndex = (double)(timestamp - startEpochTimestamp)/epochTimeInterval;
            updateTemporalInterpolationGrid();
        }       
    }
    
    public DataGrid getCurrentDataGrid()
    {
        return currentGrid;
    }
    
     public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Configure ...")
        {
            if (!configurationFrame.isVisible())
                configurationFrame.setVisible(true);
            else
                JOptionPane.showMessageDialog(null,"Configuration window already opened", "Configuration Warning", JOptionPane.ERROR_MESSAGE);
        }
        
        passMenuActionEvent(e);
     }

	public Map<String, Object> getSensorProperties() {
		// TODO Auto-generated method stub
		return null;
	}
}
