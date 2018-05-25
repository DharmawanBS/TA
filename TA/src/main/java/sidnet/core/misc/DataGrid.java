/*
 * DataGrid.java
 * @version 1
 *
 * @version 1.0.1
 * Changes: added support for bounding the min/max value that are randomly generated
 *
 * Created on April 19, 2007, 4:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/*
 * A DataGrid is an array of integers with that can range from 0 to 100
 * DataGrid extends the JPanel class to allow itself to draw the content on a parent JComponent
 * It has two configurable drawing options
 *      1. Draw a GRID, with each value of the cell plotted in the graphical interface
 *      2. SHADE, which is also a grid, but each cell is a filled rectangle colored through a color mapping function 'jet'
 *
 * It provides methods for obtaining interpolated data from the geographical region it covers
 * In the current implementation, only BINOMIAL and FLAT methods are provided.
 */
package sidnet.core.misc;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import sidnet.core.interfaces.SIDnetDrawableInterface;
import sidnet.core.interfaces.SIDnetMenuInterface;
import sidnet.core.interfaces.DataExchangeReadableOnly;
import java.awt.event.ActionEvent;
import jist.runtime.JistAPI;

/**
 *
 * @author Oliviu Ghica
 */
public class DataGrid 
extends JPanel
implements SIDnetDrawableInterface, SIDnetMenuInterface, DataExchangeReadableOnly{
    // DEBUG
    private static final boolean DEBUG = false;
    
    // consts
    public static final int FLAT        = 0; // the value of a point is the value of the cell it is located in
    public static final int BILINEAR    = 1; // bilinear interpolation
    public static final int POLYNOMIAL  = 2; // polynomial interpolation
    public static final int GRID        = 1; // Draw the grid lines and add display cell values
    public static final int SHADE       = 2; // Draw the colormap representation of the values within the grid

    // vars
    private int[][] array;
    private int colNum;     // Indicate the number of columns of the grid
    private int rowNum;     // Indicate the number of rows of the grid
   
    private int rowResolutionNum, colResolutionNum; // nr cols/rows for the extended grid due to resolution - for GUI only
    private double cellWidth, cellHeight; // the dimensions of a CELL in JPanel dimension space - for GUI only
    private double cellResWidth, cellResHeight; //  the dimensions of a CELL in JPanel dimension space considering the resolution  - for GUI only
    private JPanel hostPanel;
    private int renderingResolution;
    
    private int drawType;
    
    private LocationContext myLocationContext;
    
    // Menus
    JMenuItem menuItemShadeDisplay = new JMenuItem("SHADE Display");
    JMenuItem menuItemGridDisplay = new JMenuItem("GRID Display");
    JSeparator menuSeparator = new JSeparator();
    
    public void configureGUI(JPanel hostPanel)
    {    
        if (hostPanel != null)
        {
            this.setOpaque(false);
            this.setBackground(Color.black);
            hostPanel.add(this);
            // this should be at the bottom of the screen
            hostPanel.setComponentZOrder(this, hostPanel.getComponentCount() - 1);
            
            this.setBounds(0,0, hostPanel.getWidth(), hostPanel.getHeight());
            
            this.setPreferredSize(hostPanel.getSize());
            
            myLocationContext = new LocationContext(this.getWidth(), this.getHeight());
            
            this.setVisible(false);
        }
        
        if (drawType == GRID)
            updateResolution(1);
        else
            updateResolution(renderingResolution);         
    }
    
   public void repaintGUI()
   {
       if (this.isVisible())
            this.repaint();
   }
   
   public void setVisibleGUI(boolean visible)
   {
       this.setVisible(visible);
   }
    
    public void configureMenu(JPopupMenu hostPopupMenu)
    {
        menuItemShadeDisplay.addActionListener(this);
        menuItemGridDisplay.addActionListener(this);
        
        hostPopupMenu.add(menuSeparator);
        hostPopupMenu.add(menuItemShadeDisplay);
        hostPopupMenu.add(menuItemGridDisplay);
    }
    
    public void passMenuActionEvent(ActionEvent menuEvent)
    {
        // TODO
    }
    
    public void enableUI()
    {
        menuSeparator.setVisible(true);
        menuItemShadeDisplay.setVisible(true);
        menuItemGridDisplay.setVisible(true);
    }
   
    public void disableUI()
    {
        menuSeparator.setVisible(false);
        menuItemShadeDisplay.setVisible(false);
        menuItemGridDisplay.setVisible(false);
    }
    
   public void actionPerformed(ActionEvent e) {
        
        if (e.getActionCommand() == "SHADE Display")
        {
            drawType = SHADE;
            updateResolution(renderingResolution);
            repaint();
        }
        
        if (e.getActionCommand() == "GRID Display")
        {
            drawType = GRID;
            updateResolution(1);
            repaint();
        }

     }
    
    /* Constructor */
    public DataGrid(int renderingResolution/* > 0 (2x - double the number of cells of startGrid, for example)*/,
                    int drawType,
                    int rowNum, int colNum)
    {
        this.rowNum = rowNum;
        this.colNum = colNum;
       
        this.renderingResolution = renderingResolution;
        
        array = new int[rowNum][colNum];
        
        this.drawType = drawType;
    }
    
    /* Randomly initializes each cell of the array */
    public void seedGrid()
    {
        Random rnd = new Random();
        for (int i = 0; i < rowNum; i++)
            for (int j = 0; j < colNum; j++)
                array[i][j] = rnd.nextInt(100);  // The colormapper expects this value to be max; otherwise it will not map colors properly
    }
    
    /* Randomly initializes each cell of the array */
    public void seedGrid(int minValue, int maxValue)
    {
        Random rnd = new Random();
        for (int i = 0; i < rowNum; i++)
            for (int j = 0; j < colNum; j++)
                array[i][j] = minValue + rnd.nextInt(maxValue-minValue);  // The colormapper expects this value to be max; otherwise it will not map colors properly
    }
    
    /* Returns the index of the column in which xCoord is located */
    public int getColumnIndex(double xCoord)
    {
        return (int) java.lang.Math.floor((double)xCoord / cellWidth);
    }
    
    /* Returns the index of the row in which yCoord is located */
    public int getRowIndex(double yCoord)
    {
        return (int) java.lang.Math.floor((double)yCoord / cellHeight);
    }
    
    /* Returns the center of the cells indexed by [rowIndex][colndex] */
    public Location2D getCellCoords(int rowIndex, int colIndex)
    {
        return new Location2D(colIndex * (int)cellWidth + (int)cellWidth/2, rowIndex * (int)cellHeight + (int)cellHeight/2);
    }

    /* Returns the value stored at [rowIndex][colIndex] */
    public int getElementAt(int rowIndex, int colIndex)
    {
        return array[rowIndex][colIndex];
    }
    
    /* Sets the value stored at [rowIndex][colIndex] */
    public void setElementAt(int rowIndex, int colIndex, int data)
    {
        array[rowIndex][colIndex] = data;
    }
    
    /* Returns the physical center of a cell given through the [rowIndex][colIndex] */
    private Point2D.Double getCenterOfCell(int rowIndex, int colIndex)
     {
         return new Point2D.Double(colIndex * cellWidth + cellWidth/2, rowIndex * cellHeight + cellHeight/2);
     }
    
    /* Populate this grid from another, given, data grid, based on the specified interpolation method */
     public void populate(DataGrid grid, int method)
     {
         if (grid != null)
             for (int i = 0; i < rowNum; i ++)
                 for (int j = 0; j < colNum; j++)
                     array[i][j] = grid.getInterpolatedPointAt(j * (int)cellWidth + (int)cellWidth/2,  i * (int)cellHeight + (int)cellHeight/2, method);
     }
     
     /* Imports the data from another grid that has the same size */
     public void copyDataFrom(DataGrid grid)
     {
         if (grid.getRowCount() == rowNum && grid.getColumnCount() == colNum)
         {
             for (int i = 0; i < rowNum; i ++)
                 for (int j = 0; j < colNum; j++)
                     array[i][j] = grid.getElementAt(i, j);
         }
     }
     
     /* Returns the number of rows of the grid */
     public int getRowCount()
     {
         return rowNum;
     }
     
     /* Returns the number of columns of the grid */
     public int getColumnCount()
     {
         return colNum;
     }
     
    public double readDataAt(Location2D location, LocationContext locationContext) {
        if (location == null || myLocationContext == null)
            return -1.0;
        Location2D adaptedLocation = locationContext.adapt(location, myLocationContext);
        //System.out.println("Read from  location ( " + location.getX() + " ," +  location.getY() + " )");
        //System.out.println("Read from adapted location ( " + adaptedLocation.getX() + " ," +  adaptedLocation.getY() + " )");
        return (double) getInterpolatedPointAt((int)adaptedLocation.getX() , (int)adaptedLocation.getY(), BILINEAR);
    }
    
    public Map<String, Object> getSensorReadings(Location2D physicalLocation,
			LocationContext physicalLocationContext) {
		return (Map<String, Object>) (new HashMap<String, Object>().put("ANALOG_VALUE", readDataAt(physicalLocation, physicalLocationContext)));
	}
    
    /* Returns the value at location x, y obtain from the given array through the interpolation method */
    public int getInterpolatedPointAt(int x, int y, int method) // x and y specified in panel-dimensions (NOT field dimensions)
     {
         switch(method)
         {
             case FLAT: return getFlatInterpolatedPointAt(x, y);
             case BILINEAR: return getBilinearInterpolatedPointAt(x, y);
         }
         return 0;
     }
     
     /* Specialized function that performs the interpolated value through FLAT (no) interpolation */
     private int getFlatInterpolatedPointAt(int x, int y)
     {
         int row, col;
         
         row = (int)(Math.floor(y / cellHeight));
         col = (int)(Math.floor(x / cellWidth));
         
         return array[row][col];
     }
    
     /* Specialized function that returns the interpolated value of the matrix evaluated at point (x, y) through bilinear interpolation */
     private int getBilinearInterpolatedPointAt(int xx, int yy)
     {
         int row, col;
         Point2D center, c11, c12, c21, c22;
         double x = (int)xx;
         double y = (int)yy;
         
         // Four quadrans
         //
         //     12      |    22
         //    ------------------
         //     11      |    21
         //
         
         int x11, x12, x21, x22;
         int y11, y12, y21, y22;
         int a11, a12, a21, a22;
         int row11 = 0, row12 = 0, row21 = 0, row22 = 0;
         int col11 = 0, col12 = 0, col21 = 0, col22 = 0;
         
         // Identify the cell where this points maps in
         row = (int)(Math.floor(y / cellHeight));
         col = (int)(Math.floor(x / cellWidth));
         center=getCenterOfCell(row, col);
         
         /* Some bounds check to make sure we do not address outside the matrix bounds */
         if ( x < center.getX() && y < center.getY())
         {
             row11 = row;     col11 = col - 1;
             row12 = row - 1; col12 = col - 1;
             row22 = row - 1; col22 = col;
             row21 = row;     col21 = col;
         }
         if ( x < center.getX() && y >= center.getY())
         {
             row11 = row + 1; col11 = col - 1;
             row12 = row    ; col12 = col - 1;
             row22 = row    ; col22 = col;
             row21 = row + 1; col21 = col;
             
             
         }
         if ( x >= center.getX() && y >= center.getY())
         {
             row11 = row + 1; col11 = col;
             row12 = row    ; col12 = col;
             row22 = row    ; col22 = col + 1;
             row21 = row + 1; col21 = col + 1;
         }
         if ( x >= center.getX() && y < center.getY())
         {
             row11 = row    ; col11 = col;
             row12 = row - 1; col12 = col;
             row22 = row - 1; col22 = col + 1;
             row21 = row    ; col21 = col + 1;
         }
         
         if (row12 < 0 || row22 < 0)
         {
             row12 = 0;
             row22 = 0;
             row11 ++;
             row21 ++;
         }
         if (row11 >= rowNum || row21 >= rowNum)
         {
             row11 = rowNum-1;
             row21 = rowNum-1;
             row12 --;
             row22 --;
         }
         //*******************
         if (row12 >= rowNum || row22 >= rowNum)
         {
             row12 = rowNum-1;
             row22 = rowNum-1;
             row11 --;
             row21 --;
         }
         if (col12 < 0 || col22 < 0)
         {
             col12 = 0;
             col22 = 0;
             col11 ++;
             col21 ++;
         }
         if (col11 >= colNum || col12 >= colNum)
         {
             col11 = colNum-1;
             col12 = colNum-1;
             col21 --;
             col22 --;
         }
         
         //*******************
         if (col11 < 0 || col12 < 0)
         {
             col11 = 0;
             col12 = 0;
             col21 ++;
             col22 ++;
         }
         if (col21 >= colNum || col22 >= colNum)
         {
             col21 = colNum-1;
             col22 = colNum-1;
             col11 --;
             col12 --;
         }

         /* Retrieve the values of the four orthogonal-neighboring cells */
         
         
         a11 = array[row11][col11];
         a12 = array[row12][col12];
         a21 = array[row21][col21];
         a22 = array[row22][col22];
         
         /* and the physical center of these cells */
         c11 = getCenterOfCell(row11, col11);
         c12 = getCenterOfCell(row12, col12);
         c21 = getCenterOfCell(row21, col21);
         c22 = getCenterOfCell(row22, col22);
         
         // X-direction linear interpolation
         double x1 = c11.getX();
         double x2 = c21.getX();
         double y1 = c12.getY();
         double y2 = c11.getY();

         if (x < x1)
             x = x1;
         if (x > x2)
             x = x2;
         if (y < y1)
             y = y1;
         if (y > y2)
             y = y2;
         
         double R1_X = (x2 - x)/(x2 - x1) * a11 + (x - x1)/(x2 - x1) * a21;
         double R2_X = (x2 - x)/(x2 - x1) * a12 + (x - x1)/(x2 - x1) * a22;

         double P    = (y2 - y)/(y2 - y1) * R2_X + (y - y1)/(y2 - y1) * R1_X;
         
         return (int)P;
     }
     
     public void updateResolution(int newResolution)
     {
          rowResolutionNum = rowNum * newResolution;
          colResolutionNum = colNum * newResolution;

          cellResWidth = (double)this.getWidth() / colResolutionNum;
          cellResHeight = (double)this.getHeight() / rowResolutionNum;
          
          cellWidth = this.getWidth() / colNum;
          cellHeight = this.getHeight() / rowNum;
          
          //myLocationContext = new LocationContext(colResolutionNum, rowResolutionNum);
     }
     
    /* GRAPHICS */
    
    /* Overwrite method of parent component */
     public void paintComponent(Graphics g) { 
        if (JistAPI.getTime() == 0)
            return;
         
        if (DEBUG) System.out.println("[DEBUG][DataGrid.paintComponent(g)");
        
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.black);
                
        double offsetX = cellResWidth /2;
        double offsetY = cellResHeight /2;
        
        int min = 100;
        int max = 0;
        double sum = 0;
   
        if (drawType == GRID)
        {
            AlphaComposite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
            for (int i = 0; i < rowNum; i++)
            {
                for (int j = 0; j < colNum; j++)
                {                     
                     g2d.setColor(getMappedColor(array[i][j]));
                     g2d.fillRect(j * (int)cellResWidth + (int)cellWidth / 3, i * (int)cellResHeight + (int)cellHeight / 3 , (int)cellResWidth / 3, (int)cellResHeight / 3);

                     g2d.setColor(Color.black);
                     g2d.drawString(new String(""+array[i][j]), j * (int)cellResWidth  + (int)offsetX, i * (int)cellResHeight + (int)offsetY);
                     
                     /* Plot the vertical lines of the grid */
                     if (i == 0)  // draw the lines only once
                     {
                        Line2D.Double vertLine = new Line2D.Double(j * (int)cellResWidth, 0, j * (int)cellResWidth, this.getHeight());
                        g2d.draw(vertLine);  
                     }
                }
                /* Draw horizontal line of the grid */
                Line2D.Double horzLine = new Line2D.Double(0, i * (int)cellHeight , (int)this.getWidth(), i * (int)cellHeight);
                g2d.draw(horzLine);
            }
            
        }
        
        if (drawType == SHADE)
        {
            AlphaComposite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
	    g2d.setComposite(alphaComp);
      
            
            for (double i = 0; i < rowResolutionNum; i = i + 1)
                for (double j = 0; j < colResolutionNum; j = j + 1)
                {
                    int interpVal = getInterpolatedPointAt((int)(i * cellResWidth + offsetX), (int) (j * cellResHeight + offsetY), BILINEAR);
                    if (interpVal < min)
                         min = interpVal;
                     if (interpVal > max)
                         max = interpVal;
                     sum += interpVal;
                     
                    g2d.setColor(getMappedColor(interpVal));
                    g2d.fillRect((int)(i * cellResWidth), (int)(j * cellResHeight), (int)cellResWidth+2, (int)cellResHeight+2);
                }
            
            g2d.setColor(Color.BLACK);
            g2d.drawString("Min: " + min, 5, myLocationContext.getHeight() - 25);
            g2d.drawString("Max: " + max, 5, myLocationContext.getHeight() - 15);
            g2d.drawString("Avg: " + (int)(sum/(colResolutionNum * rowResolutionNum)), 5, myLocationContext.getHeight() - 5);
        }
        
     }
     
    public void clear(Graphics g) {
       super.paintComponent(g);
    }   
 
     /* The function maps a value in interval 0 ... 100 to a color following the 'jet' color representation: 0: blue - yellow - red : 100
      * The function is explicitly hardcoded *
      */
     public Color getMappedColor(int i)
     {
         // We implement the 'jet' colormap
         double R = 0, G = 0, B = 0.54;
         
         // Set-up the reds
         if (i > 37)
             R += (i-37)*0.04;
         if (R > 1)
             R = 1;
         if (i > 88)
             R -= (i-88)*0.04;
         
         // Set-up the greens
         if (i > 11)
             G += (i-11)*0.04;
         if (G > 1)
             G = 1;
         if (i > 62)
             G -= (i-62)*0.04;
         if (G < 0)
             G = 0;
         
         // Set-up the blues
         B += i*0.04;
         if (B > 1)
             B = 1;
         if (i > 37)
             B -= (i-37)*0.04;
         if (B < 0)
             B = 0;
         
         //System.out.println("R = " + R*255 + " G = " + G*255 + " B = " + B*255);
         return new Color((int)(R * 255), (int)(G * 255), (int)(B * 255));
     }

	public Map<String, Object> getSensorProperties() {
		// TODO Auto-generated method stub
		return null;
	}
     
}
