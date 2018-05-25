/*
 * Arrow.java
 *
 * Created on October 24, 2007, 6:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.gui;

import sidnet.core.gui.*;
import sidnet.core.misc.Location2D;
import java.awt.*;

/**
 *
 * @author Oliver
 */
public class Arrow{
    private final static Color defaultColor = Color.BLACK;
    private Location2D fromPoint;
    private Location2D toPoint;
    private double angle;
    private double length;
    private Color color;
    private final int headType;
    
    
    /** Creates a new instance of Arrow */
    public Arrow(Location2D fromPoint, Location2D toPoint) {
        this.fromPoint = fromPoint;
        this.toPoint   = toPoint;
        angle = Math.atan((double)(fromPoint.getY() - toPoint.getY())/(double)(fromPoint.getX() - toPoint.getX()));
        if (fromPoint.getX() > toPoint.getX())
            angle = angle + Math.PI;
        length = fromPoint.distanceTo(toPoint);
        this.color = defaultColor;
        headType = ArrowDrawingTool.SIDE_LEAD;
    }
    
    /** Creates a new instance of Arrow */
    public Arrow(Location2D fromPoint, Location2D toPoint, Color color) {
        this.fromPoint = fromPoint;
        this.toPoint   = toPoint;
        angle = Math.atan((double)(fromPoint.getY() - toPoint.getY())/(double)(fromPoint.getX() - toPoint.getX()));
        if (fromPoint.getX() > toPoint.getX())
            angle = angle + Math.PI;
        length = fromPoint.distanceTo(toPoint);
        if (color != null)
            this.color = color;
        else
            this.color = defaultColor;
        headType = ArrowDrawingTool.SIDE_LEAD;
    }
    
    /** Creates a new instance of Arrow with custom arrow head*/
    public Arrow(Location2D fromPoint, Location2D toPoint, Color color, int headType) {
        this.fromPoint = fromPoint;
        this.toPoint   = toPoint;
        if (fromPoint.getX() != toPoint.getX())
            angle = Math.atan((double)(fromPoint.getY() - toPoint.getY())/(double)(fromPoint.getX() - toPoint.getX()));
        else
            angle = Math.PI/2;
          
        length = fromPoint.distanceTo(toPoint);
        if (color != null)
            this.color = color;
        else
            this.color = defaultColor;
        this.headType = headType;
    }
    
    public Location2D getFromPoint()
    {
        return fromPoint;
    }
    
    public Location2D getToPoint()
    {
        return toPoint;
    }
    
    public Color getColor()
    {
        return color;
    }
    
   
    public int getArrowHead()
    {
        return headType;
    }
    
    public double getAngle360()
    {
        return angle;
    }
        
}
