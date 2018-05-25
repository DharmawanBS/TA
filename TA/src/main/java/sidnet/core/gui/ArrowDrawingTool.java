/*
 * ArrowDrawingTool.java
 *
 *
 * @version 1.0.1 (November 28, 2007)
 *  - fixed the display problem when nodes where on the same x-axis
 *
 * @version 1.0 
 *
 * @author Oliviu Ghica, Northwestern University
 */


package sidnet.core.gui;

import sidnet.core.gui.Arrow;
import java.awt.*;
import sidnet.core.misc.Location2D;


/*
 * Tool that can drow an Arrow on the screen
 *
 */
public class ArrowDrawingTool {
  public static final int SIDE_LEAD=0,
                          SIDE_TRAIL=1,
                          SIDE_BOTH=2,
                          SIDE_NONE=3;
  
  public ArrowDrawingTool () { ; }
  
  /*
   * Draws an arrow on the screen given an Arrow object 
   * <p>
   * @param g       graphic context in which the Arrow will be drawn
   * @param arrow   an Arrow object
   *
   */ 
  
  public static void drawArrow(Graphics g, Arrow arrow)
  {
      drawArrow(g,
                arrow.getFromPoint(),
                arrow.getToPoint(), 
                arrow.getColor(),
                arrow.getArrowHead());
  }
  
  /*
   * Draws an arrow on the screen given using the end-points location of the arrow. 
   * <p>
   * @param g          graphic context in which the Arrow will be drawn
   * @param fromPoint  the origin of the arrow
   * @param toPoint    the end-point of the arrow
   * @param color      the color of the arrow
   * @param arrowHead  the type of arrow head: LEAD(end-point arrow), TRAIL (start-point arrow), BOTH(both-sides arrow marker), NONE (straight line, no marker)
   */
  public static void drawArrow(Graphics g, Location2D fromPoint, Location2D toPoint)
  {
      if (fromPoint.getX() == toPoint.getX() &&
          fromPoint.getY() == toPoint.getY())
          return; // do not draw since this has length ZERO
      double angle = Math.atan((double)(fromPoint.getY() - toPoint.getY())/(double)(fromPoint.getX() - toPoint.getX()));
      
      if (fromPoint.getX() >= toPoint.getX())
          angle = angle + Math.PI;
      
      if (angle == Double.NaN)
          return;
      
      drawArrow(g,
                fromPoint.getX(),
                fromPoint.getY(),
                angle,
                fromPoint.distanceTo(toPoint),
                Color.BLACK,
                SIDE_LEAD);
  }
  
  /*
   * Draws an arrow on the screen given using the end-points location of the arrow. 
   * <p>
   * @param g          graphic context in which the Arrow will be drawn
   * @param fromPoint  the origin of the arrow
   * @param toPoint    the end-point of the arrow
   * @param color      the color of the arrow
   * @param arrowHead  the type of arrow head: LEAD(end-point arrow), TRAIL (start-point arrow), BOTH(both-sides arrow marker), NONE (straight line, no marker)
   */
  public static void drawArrow(Graphics g, Location2D fromPoint, Location2D toPoint, Color color, int arrowHead)
  {
       if (fromPoint.getX() == toPoint.getX() &&
          fromPoint.getY() == toPoint.getY())
          return; // do not draw since this has length ZERO
       
      double angle = Math.atan((double)(fromPoint.getY() - toPoint.getY())/(double)(fromPoint.getX() - toPoint.getX()));
      
      if (angle == Double.NaN)
          return;
      
      if (fromPoint.getX() >= toPoint.getX())
          angle = angle + Math.PI;
      
      drawArrow(g,
                fromPoint.getX(),
                fromPoint.getY(),
                angle,
                fromPoint.distanceTo(toPoint),
                color,
                arrowHead);
  }

  /*
   * Draws an arrow on the screen given using polar representations 
   * <p>
   * @param g          graphic context in which the Arrow will be drawn
   * @param x          the X-coordinate of the origin of the arrow
   * @param y          the y-coordinate of the origin of the arrow
   * @param theta      the angle of the arrow expressed in radians
   * @param length     the length of the arrow
   * @param color      the color of the arrow
   * @param arrowHead  the type of arrow head: LEAD(end-point arrow), TRAIL (start-point arrow), BOTH(both-sides arrow marker), NONE (straight line, no marker)
   */
  public static void drawArrow(Graphics g,
                        double x, 
                        double y,
                        double theta, 
                        double length,
                        Color color,
                        int arrowHead) {
    try {
      if (length < 0) { 
        theta+=Math.PI;
        length*=-1;
      }
      
      Graphics2D g2d = (Graphics2D)g;
      
      int x1, y1;
      x1=(int)Math.ceil(x + length*Math.cos(theta));
      y1=(int)Math.ceil(y + length*Math.sin(theta));
      g2d.setColor(color);
      g2d.drawLine((int)x,(int)y,(int)x1,(int)y1);

      switch (arrowHead) {
        case SIDE_LEAD :
          drawArrow(g,x1,y1,theta+5*Math.PI/4,5,color,SIDE_NONE);
          drawArrow(g,x1,y1,theta+3*Math.PI/4,5,color,SIDE_NONE);
          break;
        case SIDE_TRAIL :
          drawArrow(g,x,y,theta-Math.PI/4,5,color,SIDE_NONE);
          drawArrow(g,x,y,theta+Math.PI/4,5,color,SIDE_NONE);
          break;
        case SIDE_BOTH:
          drawArrow(g,x,y,theta-Math.PI/4,5,color,SIDE_NONE);
          drawArrow(g,x,y,theta+Math.PI/4,5,color,SIDE_NONE);
          drawArrow(g,x1,y1,theta+5*Math.PI/4,5,color,SIDE_NONE);
          drawArrow(g,x1,y1,theta+3*Math.PI/4,5,color,SIDE_NONE);
          break;
        case SIDE_NONE :
          break;
        default:
          throw new IllegalArgumentException();
      }
    }
    catch (IllegalArgumentException iae) {
      System.out.println("Invalid value for variable arrowHead.");
    }
  }
}

