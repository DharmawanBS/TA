/*
 * HexGrid.java
 *
 * Created on April 18, 2007, 11:09 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

import java.awt.geom.Point2D;

/**
 *
 * @author Oliver
 */
public class HexGrid {
    private int rows, columns;
    private HexCell[][] hexGrid;
    /** Creates a new instance of HexGrid */
    public HexGrid(int rows, int columns) {
        hexGrid = new HexCell[rows][columns];
    }
    
    public int getContentOf(int row, int column)
    {
        return hexGrid[row][column].getContent();
    }
    
    
    public class HexCell
    {   
        Point2D[] v;        // Vertexes of the triangles in the geographic space
        int content;        // The value carried by each cell; We might make this Object to be more general
        
        public void HexCell(Point2D v1, Point2D v2, Point2D v3)
        {
            v = new Point2D[3];
            v[1] = v1;
            v[2] = v2;
            v[3] = v3;
        }
        
        public void setContent(int content)
        {
            this.content = content;
        }
        
        public int getContent()
        {
            return content;
        }
        
        public boolean isInside(Point2D p)
        {
            // This method is patterned after [Franklin, 2000]
            int count = 0;
          
            // Loop through all the edges of a polygon
            for (int i = 0; i < 3; i++)
            {
                if ((v[i].getY() <= p.getY() && v[(i+1)%3].getY() > p.getY())        // an upward crossing
                    || (v[i].getY() > p.getY() && v[(i+1)%3].getY() <= p.getY()))           // a downward crossing
                {
                        // compute the actual edge-ray intersect x-coordinate
                        float vt = (float)((p.getY() - v[i].getY())/(v[(i+1)%3].getY() - v[i].getY()));
                        if ((p.getX() < v[i].getX() + vt*(v[(i+1)%3].getX() - v[i].getX())))        // p.x < intersect
                            ++count;        // a valid crossing of y = p.y right of p.x
                }
            }
            if (count % 2 == 0)
                return false;
            else
                return true;
        }
    }
    
}
