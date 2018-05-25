/*
 *
 * @author Oliver, Northwestern University
 * 
 * @version 1.1 (2009/10/13)
 * 		- added support for storing energy-map as a CSV file
 * 
 * @version 1.0
 */

package sidnet.utilityviews.energymap;

import sidnet.core.interfaces.GroupSemantics;
import sidnet.core.interfaces.SIDnetMenuInterface;
import sidnet.core.interfaces.SimulationTimeRunnable;

import javax.swing.*;           // For JPanel, etc.

import java.awt.*;              // For Graphics, etc.
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import jist.runtime.JistAPI;
import jist.swans.Constants;
import sidnet.core.interfaces.UtilityView;
import sidnet.core.misc.FileUtils;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;
import sidnet.core.misc.NCS_Location2D;
import sidnet.core.misc.Node;
import sidnet.utilityviews.commons.StatLogger;
import sidnet.utilityviews.commons.StatLoggerImpl;
import sidnet.utilityviews.statscollector.ExperimentData;

public class EnergyMap 
extends UtilityView 
implements GroupSemantics, SIDnetMenuInterface, SimulationTimeRunnable {
	/** CONSTANTS */
	private static final String SAVE_AS_CSV_MENU = "Save Map As ... CSV";
	
    /** CONFIGURATION */
    private static final Color FULL_COLOR = Color.white;    // The color to simbolize full (100%) energy
    private static final Color EMPTY_COLOR = Color.black;   // The color to simbolize empty  (0%) energy 
    private static final int REFRESH_SLOWDOWN_FACTOR   =   100;
    private static final int RESOLUTION_DEFAULT = 30; // [%, 0 means EnergyMap off] 
    private static final int RADIANCE = 100; // in # of cells all around, >= 1
    
    private static int refreshContor = 0;
    public class EnergyItem {
        public int energyLevel;
        public int x, y;
    }
    
    private LocationContext locationContext;
    
    private Node[] myNode;
    
    // Data-Map Logging
    private StatLogger mapLogger = null;
    private long loggingInterval;
    private Object self; /** self-referencing proxy entity. */
    
    private int rowCellCount, columnCellCount;   // the number of rows/columns of the energy map matrix
    
    /* Constants for the EnergyMap's meaning */
    private static final int EMAP_MAX   = 1;
    private static final int EMAP_MIN   = 2;
    private static final int EMAP_AVG   = 3;
    private static int EMapMeaning      = EMAP_AVG;
    
    private static int EnergyMapMatrix[][] = null;
    private static int SmoothEnergyMapMatrix[][] = null;
    private static int tempSmoothEnergyMapMatrix[][] = null;
    
    private static int width, height;   // The size of the panel
    private static int fieldWidth, fieldHeight;
    
    private JPopupMenu hostPopupMenu;
    
    /** Creates a new instance of EnergyMap 
     * 
     */
    public EnergyMap(Node[] myNode) {  
        this.myNode = myNode;
        hostPopupMenu = new JPopupMenu();
        this.configureMenu(hostPopupMenu);
    }

    /** Creates a new instance of EnergyMap 
     *  and enables data-map time-lapse logging
     */
    public EnergyMap(Node[] myNode, ExperimentData optionalExperimentData, long loggingInterval) {
    	this(myNode);
    	this.loggingInterval = loggingInterval;
    	
    	if (optionalExperimentData != null) {
    		mapLogger = new StatLoggerImpl();
    		mapLogger.configureLogger("EnergyMap",
    								  optionalExperimentData.getInt(optionalExperimentData.RUN_ID),
    								  optionalExperimentData.getInt(optionalExperimentData.REPEAT_INDEX),
    								  optionalExperimentData.getInt(optionalExperimentData.EXPERIMENT_ID),
    								  optionalExperimentData.getString(optionalExperimentData.EXPERIMENTS_TARGET_DIRECTORY), 
    								  optionalExperimentData.getString(optionalExperimentData.EXPERIMENT_TAG), 
    								  optionalExperimentData);
        	
    		mapLogger.appendToDataTableHeader("Time[h]");
        	for (int i = 0; i < myNode.length; i++)
        		mapLogger.appendToDataTableHeader("#" + myNode[i].getID());        	
        	mapLogger.commitHeaderLog();
    	}
    	
    	self = JistAPI.proxy(this, SimulationTimeRunnable.class);
    	((SimulationTimeRunnable)self).run();
    }
        
    public void configureGroupMenu(JPopupMenu hostGroupPopupMenu) {
        // N/A
    }
    
    public void enableGroupUI() {
        // N/A
    }
    
    public void disableGroupUI() {
        // N/A
    }
    public void run() {
    	logMapSnapshot();

    	// reschedule
    	JistAPI.sleep(loggingInterval);
    	((SimulationTimeRunnable)self).run();
	} 
    
    private void logMapSnapshot() {
    	if (mapLogger == null) // logging disabled
    		return;
    	
    	String dataRow = "\t" + JistAPI.getTime() / Constants.HOUR + "\t";
    	for (int i = 0; i < myNode.length; i++)
    		dataRow += "" + myNode[i].getEnergyManagement().getBattery().getPercentageEnergyLevel() + "\t";
    	
    	mapLogger.appendDataRow(dataRow);
    }
    
    public void repaintGUI() {        
        if (refreshContor < REFRESH_SLOWDOWN_FACTOR)
        {
            refreshContor ++;
            return ;
        }
         
        refreshContor = 0;
        this.repaint();
    }
    
    public void configureGUI(JPanel hostPanel) {
        this.setOpaque(true);
        
        this.setVisible(true);
    
        hostPanel.add(this);
        
        this.setBounds(0,0, hostPanel.getWidth(), hostPanel.getHeight());
         
        EnergyMap.width = hostPanel.getWidth();
        EnergyMap.height = hostPanel.getHeight();
        
        //System.out.println("EnergyMap width = " + width + " height = " + height);
        
        this.locationContext = new LocationContext(width, height);
        
        UpdateResolution(RESOLUTION_DEFAULT); 
    }
  
    public void UpdateResolution(int resolution) {
        if (resolution < 0 || resolution > 100)
        {
            System.out.println("[ERROR - EnergyMap](UpdateResolution) - Invalid resolution value! Must be between 0% - 100%");
            return;
        }
        rowCellCount = (int)(height * resolution / 100);
        columnCellCount = (int)(width * resolution / 100);
        
        EnergyMapMatrix = new int[rowCellCount][columnCellCount];
    }
    
    public void RefreshGraphics() {
        //System.out.println("Energy map RefreshGraphics");
        switch(EMapMeaning)
        {
            case EMAP_MIN: ComputeEnergyMapMatrix_MIN(); break;
            case EMAP_MAX: ComputeEnergyMapMatrix_MAX(); break;
            case EMAP_AVG: ComputeEnergyMapMatrix_AVG(); break;
        }
    }
    
    public void ComputeEnergyMapMatrix_MIN() {
        int columnIndex, rowIndex;
        for (int r = 0; r < rowCellCount; r++)
            for (int c = 0; c < columnCellCount; c++)
                EnergyMapMatrix[r][c] = 0;
        for (int i = 0; i < myNode.length; i++) {
            columnIndex = (int)Math.floor((double)myNode[i].getLocation2D().toNCS(myNode[i].getLocationContext()).fromNCS(locationContext).getX() / ((double)fieldWidth / (double)columnCellCount));
            rowIndex    = (int)Math.floor((double)myNode[i].getLocation2D().toNCS(myNode[i].getLocationContext()).fromNCS(locationContext).getY() / ((double)fieldHeight / (double)rowCellCount));
            if (EnergyMapMatrix[rowIndex][columnIndex] == 0 || 
                EnergyMapMatrix[rowIndex][columnIndex] >  0 && EnergyMapMatrix[rowIndex][columnIndex] > myNode[i].getEnergyManagement().getBattery().getPercentageEnergyLevel())
                EnergyMapMatrix[rowIndex][columnIndex] = (int)myNode[i].getEnergyManagement().getBattery().getPercentageEnergyLevel();
        }
    }
    
    public void ComputeEnergyMapMatrix_MAX() {
        int columnIndex, rowIndex;
        for (int r = 0; r < rowCellCount; r++)
            for (int c = 0; c < columnCellCount; c++)
                EnergyMapMatrix[r][c] = 0;
        for (int i = 0; i < myNode.length; i++)
        {
            columnIndex = (int)Math.floor((double)myNode[i].getLocation2D().toNCS(myNode[i].getLocationContext()).fromNCS(locationContext).getX() / ((double)fieldWidth / (double)columnCellCount));
            rowIndex    = (int)Math.floor((double)myNode[i].getLocation2D().toNCS(myNode[i].getLocationContext()).fromNCS(locationContext).getY() / ((double)fieldHeight / (double)rowCellCount));
            if (EnergyMapMatrix[rowIndex][columnIndex] == 0 || 
                EnergyMapMatrix[rowIndex][columnIndex] >  0 && EnergyMapMatrix[rowIndex][columnIndex] < myNode[i].getEnergyManagement().getBattery().getPercentageEnergyLevel())
                EnergyMapMatrix[rowIndex][columnIndex] = (int)myNode[i].getEnergyManagement().getBattery().getPercentageEnergyLevel();
        }
    }
    
    public void ComputeEnergyMapMatrix_AVG()
    {
        if (JistAPI.getTime() == 0)
            return;
        int columnIndex = 0, rowIndex = 0;
        int EnergyMapCopy[][] = new int[rowCellCount][columnCellCount];
        for (int r = 0; r < rowCellCount; r++)
            for (int c = 0; c < columnCellCount; c++)
            {
                EnergyMapCopy[r][c] = EnergyMapMatrix[r][c];
                EnergyMapMatrix[r][c] = 0;
            }
        int contor[][] = new int[rowCellCount][columnCellCount];
//        for (int i = 0; i < myNode.length; i++)
//        {
//            columnIndex = (int)Math.floor((double)myNode[i].getLocation2D().toNCS(myNode[i].getLocationContext()).fromNCS(locationContext).getX() / (width / (double)columnCellCount));
//            rowIndex    = (int)Math.floor((double)myNode[i].getLocation2D().toNCS(myNode[i].getLocationContext()).fromNCS(locationContext).getY() / (height / (double)rowCellCount));
//            EnergyMapMatrix[rowIndex][columnIndex] += (int)myNode[i].getBattery().getPercentageEnergyLevel();
//            contor[rowIndex][columnIndex] ++;
//        }
//        
//        for (int r = 0; r < rowCellCount; r++)
//            for (int c = 0; c < columnCellCount; c++)
//                if (EnergyMapMatrix[r][c] == 0)
//                    EnergyMapMatrix[r][c] = EnergyMapCopy[r][c];
//        
//        /* Compute the average */
//        for (int r = 0; r < rowCellCount; r++)
//            for (int c = 0; c < columnCellCount; c++)
//                if (contor[r][c] != 0)
//                    EnergyMapMatrix[r][c] = EnergyMapMatrix[r][c] / contor[r][c];       
        
        for (int i = 0; i < myNode.length; i++) {
            columnIndex = 
            	(int)Math.floor((double)myNode[i]
            	        .getLocation2D()
            			.toNCS(myNode[i].getLocationContext())
            			.fromNCS(locationContext).getX() /
            			(width / (double)columnCellCount));
            rowIndex    = (int)Math.floor((double)myNode[i]
                        .getLocation2D()
                        .toNCS(myNode[i]
                        .getLocationContext())
                        .fromNCS(locationContext).getY() /
                        (height / (double)rowCellCount));
            
            EnergyMapMatrix[rowIndex][columnIndex] = 
            	(int)myNode[i].getEnergyManagement()
            				  .getBattery()
            				  .getPercentageEnergyLevel();
        }        
        
       makeSmooth(); 
       //makeSmooth2();
    }
    
    public void makeSmooth2()
    {
        for (int rad = 0; rad < RADIANCE; rad++)
            for (int r = 0; r < rowCellCount; r++)
                for (int c = 0; c < columnCellCount; c++)
                {
                    //
                }
    }
    
    public void makeSmooth()
    {
         if (tempSmoothEnergyMapMatrix == null)
            tempSmoothEnergyMapMatrix = new int[rowCellCount][columnCellCount];
        
        if (SmoothEnergyMapMatrix == null)
            SmoothEnergyMapMatrix = new int[rowCellCount][columnCellCount];
        else
            for (int r = 0; r < rowCellCount; r++)
                for (int c = 0; c < columnCellCount; c++)
                {
                    SmoothEnergyMapMatrix[r][c] = EnergyMapMatrix[r][c];
                    tempSmoothEnergyMapMatrix[r][c] = EnergyMapMatrix[r][c];
                }
        
       
        
        for (int rad = 0; rad < RADIANCE; rad++)
        {
            for (int r = 0; r < rowCellCount; r++)
                 for (int c = 0; c < columnCellCount; c++)
                {                    
                    if (SmoothEnergyMapMatrix[r][c] == 0)
                    {
                        int cnt = 0;
                        if (r - 1 >= 0)
                        {
                            if (tempSmoothEnergyMapMatrix[r-1][c] != 0)
                                cnt++;
                            SmoothEnergyMapMatrix[r][c] += tempSmoothEnergyMapMatrix[r-1][c];
                            
                        }
                        if (c - 1 >= 0)
                        {
                            if (tempSmoothEnergyMapMatrix[r][c-1] != 0)
                                cnt++;
                            
                            SmoothEnergyMapMatrix[r][c] += tempSmoothEnergyMapMatrix[r][c-1];
                        }
                        if (r + 1 < rowCellCount)
                        {
                            if (tempSmoothEnergyMapMatrix[r+1][c] != 0)
                                cnt++;
                            SmoothEnergyMapMatrix[r][c] += tempSmoothEnergyMapMatrix[r+1][c];
                        }
                        if (c + 1 < columnCellCount)
                        {
                            if (tempSmoothEnergyMapMatrix[r][c+1] != 0)
                                cnt++;
                            SmoothEnergyMapMatrix[r][c] += tempSmoothEnergyMapMatrix[r][c+1];
                        }
                        
                        SmoothEnergyMapMatrix[r][c] = (int)(SmoothEnergyMapMatrix[r][c] * ((double)(RADIANCE - rad) / ((double)RADIANCE + 1)));
                        
                        if (cnt >= 2)
                        {
                            SmoothEnergyMapMatrix[r][c] = SmoothEnergyMapMatrix[r][c] / (cnt);
                            if (SmoothEnergyMapMatrix[r][c] > 100)
                                SmoothEnergyMapMatrix[r][c] = 100;
                            if (SmoothEnergyMapMatrix[r][c] < 0)
                                SmoothEnergyMapMatrix[r][c] = 0;
                        }
                      
                    }
                }    
               for (int r = 0; r < rowCellCount; r++)
                 for (int c = 0; c < columnCellCount; c++)
                     tempSmoothEnergyMapMatrix[r][c] = SmoothEnergyMapMatrix[r][c];
        }
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (JistAPI.getTime() == 0)
            return; 
        Graphics2D g2d = (Graphics2D)g; 
        
        RefreshGraphics();
        Rectangle cell = null;
        if (rowCellCount != 0 && columnCellCount != 0)
            cell = new Rectangle((int)(width / rowCellCount), (int)( height / columnCellCount));
        else
        {
            String s = "Energy Map initialization ...";
            g2d.drawString(s, 12, 9);
        }
        
        float colorVal;
        
        if (cell != null)
        {
            for (int r = 0; r < rowCellCount; r++)
                for (int c = 0; c < columnCellCount; c++)
                {
                   // if (SmoothEnergyMapMatrix != null)
                        colorVal = ((float)SmoothEnergyMapMatrix[r][c])/(float)100;
                    //else
                       // colorVal = ((float)EnergyMapMatrix[r][c])/(float)100;
                                      
                    g2d.setColor(new Color(colorVal, colorVal, colorVal));
                    
                    cell.setLocation(c * width / columnCellCount, r * height / rowCellCount);
                    g2d.draw(cell);
                    g2d.fill(cell);
                }
        
        }
        
    }
 
     protected void clear(Graphics g) {
        //super.paintComponent(g);
    }

	public void configureMenu(JPopupMenu hostPopupMenu) {
		JMenuItem saveMapAsCSV = new JMenuItem();
		saveMapAsCSV.setText(SAVE_AS_CSV_MENU);
		saveMapAsCSV.addActionListener(this);
		hostPopupMenu.add(saveMapAsCSV);	
	}

	public void disableUI() {
		hostPopupMenu.setEnabled(false);
	}

	public void enableUI() {
		hostPopupMenu.setEnabled(true);	
	}

	public void passMenuActionEvent(ActionEvent menuEvent) {
		// TODO Auto-generated method stub		
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == SAVE_AS_CSV_MENU) {
			NCS_Location2D[] locArray = new NCS_Location2D[myNode.length];
			int[] energyArray = new int[myNode.length];
			
			for (int i = 0; i < myNode.length; i++) {
				locArray[i] = new NCS_Location2D(
								myNode[i].getNCS_Location2D().getX(),
								myNode[i].getNCS_Location2D().getY());
				energyArray[i] = (int) myNode[i].getEnergyManagement()
												.getBattery()
												.getPercentageEnergyLevel();				
			}
			
			EnergyCSVWriter.saveAsCSV(locArray, energyArray);
		}
	}	
}

 