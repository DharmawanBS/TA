/*
 * Grid.java
 *
 * Created on May 15, 2006, 10:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

import sidnet.core.misc.Location2D;

/**
 *
 * @author Oliviu Ghica
 */
public class Grid extends JPanel{
    private int cellWidth, cellHeight;
    
    private int colNum;     // Indicate the number of columns of the grid
    private int rowNum;     // Indicate the number of rows of the grid
   
    // Graphics
    Color lineColor = Color.gray;
    public double scale_factor_vert, scale_factor_horz;
    
    /** Creates a new instance of Grid */ 
    public Grid(int fieldWidth, int fieldHeight, int cellWidth, int cellHeight) {
        colNum = (int) java.lang.Math.ceil(((double)fieldWidth  / cellWidth));
        rowNum = (int) java.lang.Math.ceil(((double)fieldHeight / cellHeight));

        this.cellWidth = 600/rowNum;
        this.cellHeight = 600/colNum;
        
        scale_factor_vert = ((double)cellWidth * colNum) / 600;
        scale_factor_horz = ((double)cellHeight * rowNum) / 600;
        
        this.setBounds(0,0,600,600);
        this.setOpaque(false);
        this.setVisible(true);
    }

    public int getColumnID(Location2D loc, LocationContext locationContext)    {
        double xCoord = loc.convertTo(locationContext, new LocationContext(600, 600)).getX();
        return getColumnID(xCoord);
    }
    
    public int getRowID(Location2D loc, LocationContext locationContext)    {
        double yCoord = loc.convertTo(locationContext, new LocationContext(600, 600)).getY();
        return getRowID(yCoord);
    }
    
    private int getColumnID(double xCoord)
    {
        return (int) java.lang.Math.floor((double)xCoord / cellWidth);
    }
    
    public int getRowID(double yCoord)
    {
        return (int) java.lang.Math.floor((double)yCoord / cellHeight);
    }
    
    public Location2D getCellCoords(int rowID, int colID)
    {
        return new Location2D(colID * cellWidth, rowID * cellHeight);
    }
    
    public int getCellWidth()
    {
        return cellWidth;
    }
    
    public int getCellHeight()
    {
        return cellHeight;
    }
    
    public void refresh()
    {
        repaint();
    } 
    
    protected void clear(Graphics g) {
       super.paint(g);
    }  
        
     public void paintComponent(Graphics g) { 
        Graphics2D g2d = (Graphics2D)g;
        this.setLocation(0,0); 
        g2d.setColor(lineColor);
        
        // Plot the vertical lines of the grid
        for (int i = 0; i <= colNum; i++)
        {
            Line2D.Double vertLine = new Line2D.Double(i * cellHeight , 0, i * cellHeight , this.getHeight());
            g2d.draw(vertLine);
        }
        // Plot the horizontal lies of the grid
        for (int i = 0; i <= rowNum; i++)
        {
            Line2D.Double horzLine = new Line2D.Double(0, i * cellWidth , this.getWidth(), i * cellWidth);
            g2d.draw(horzLine);
        }
     }
}
