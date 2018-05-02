/*
 * NCS_Location2D.java
 *
 * Created on October 23, 2007, 6:15 PM
 * @version 1.0
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

import java.lang.Exception;

/**
 *
 * @author Oliviu Ghica, Northwestern University
 *
 * Normalized Coordinate System (location information in interval [0, 1]
 * Similar with Location2D with some restrictions added.
 * POJO for keeping the location information of a sensor node in a single object. Easy to expand to 3D
 * The location information must be in the interval [0, 1]
 */
public class NCS_Location2D{
    double x, y;
    
    /** Creates a new instance of NCS_Location2D */
    public NCS_Location2D(double x, double y){
        if (x < -50 || x > 50 || y < -50 || y > 50) {
            Exception e = new Exception("[NCS_Location2D] - ERROR: coordinates <" + x + ", " + y + "> not compatible with the Normalized Coordinate System in NCS_Location2D");
            e.printStackTrace();
            //throw e;
        }
      
        this.x = x;
        this.y = y;
    }
      
    /** getter method. gets the X-coordinate in NCS representation */
    public double getX()
    {
        return x;
    }
    
    /** getter method. gets the Y-coordinate in NCS representation */
    public double getY()
    { 
        return y;
    }
    
    /** 
     * Converts the current NCS-represented locations to a real location given the LocationContext (contains the dimensions in which the location will be converted to) 
     * <p>
     * @param LocationContext       the locationContext based on which the real location coordintates can be obtained
     */
    public Location2D fromNCS(LocationContext locationContext) {
        return new Location2D((x*locationContext.getWidth()), (y*locationContext.getHeight()));
    }
    
    /** Calculates the distance between the current Location2D and the specified Location2D */
    public double distanceTo(NCS_Location2D toLocation) {
         return (double)Math.sqrt((x-toLocation.getX())*(x-toLocation.getX()) + (y-toLocation.getY())*(y-toLocation.getY()));
    }
}
