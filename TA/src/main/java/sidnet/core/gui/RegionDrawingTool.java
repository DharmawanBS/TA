/*
 * RegionDrawTool.java
 *
 * Created on October 25, 2007, 4:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.Region;

/**
 *
 * @author Oliver
 */
public class RegionDrawingTool {
    
    /** Creates a new instance of RegionDrawTool */
    public RegionDrawingTool() {
    }
    
    public static void draw(Region region, Graphics2D g2d, Color color)
    {
        region.resetIterator();
        g2d.setColor(color);
        Location2D point = region.getNext();
        Location2D firstPoint = point;
        Location2D lastPoint = null;
        Location2D lastlastPoint = null;
        
        int contor = 0;
        
        while (point != null)
        {
            g2d.drawRect((int)point.getX(), (int)point.getY(), region.WIDTH, region.HEIGHT);
            g2d.drawString(""+region.getID(), (int)point.getX()+5, (int)point.getY());
            
            if (lastPoint != null)
                g2d.drawLine((int)lastPoint.getX(), (int)lastPoint.getY(), (int)point.getX(), (int)point.getY());
            lastlastPoint = lastPoint;
            lastPoint = point;
            point = region.getNext();
           // contor ++;
        }
        if (firstPoint != null)
            g2d.drawLine((int)lastPoint.getX(), (int)lastPoint.getY(), (int)firstPoint.getX(), (int)firstPoint.getY());
        //if (lastlastPoint != null && contor > 3)
        //{
         //   g2d.setColor(g2d.getBackground());
          //  g2d.drawLine((int)lastlastPoint.getX(), (int)lastlastPoint.getY(), (int)firstPoint.getX(), (int)firstPoint.getY());
   // }
    }
    
}
