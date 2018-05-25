/*
 * Location2D.java
 * @version 1.0
 *
 * Created on July 27, 2005, 11:12 AM
 */

/**
 *
 * @author Oliviu Ghica, Northwestern University
 * @version 1.0
 * @version 1.1 (02/06/2009)
 * Changed the x, y to "double" data types from "int"
 *
 * POJO for keeping the location information of a sensor node in a single object. Easy to expand to 3D
 */
package sidnet.core.misc;

public class Location2D {
    private double x;  /** the X-coordinate */
    private double y;  /** the Y-coordinate */
    
    /** Creates a new instance of Location */
    public Location2D(double xpos, double ypos) {
        this.x = xpos;
        this.y = ypos;
    }
    
    public Location2D(Location2D fromLocation, LocationContext fromLocationContext, LocationContext toLocationContext)
    {
        Location2D convLoc = fromLocation.toNCS(fromLocationContext).fromNCS(toLocationContext);
        this.x = convLoc.getX();
        this.y = convLoc.getY();
    }
    
    /** setter method. Sets the X-coordinate */
    public void setX(double x)
    {
        this.x = x;
    }
    
    /** setter method. Sets the Y-coordinate */
    public void setY(double y)
    {
        this.y = y;
    }
    
    /** getter method. Gets the X-coordinate */
    public double getX()
    {
        return x;
    }
    
    /** getter method. gets the Y-coordinate */
    public double getY()
    { 
        return y;
    }
    
    /** 
     * Converts the current represented locations to NCS (Normalized Coordinate System)
     * <p>
     * @param LocationContext       the locationContext based on which the NCS can be obtained
     */
    public NCS_Location2D toNCS(LocationContext locationContext) {
        return new NCS_Location2D((double)x/locationContext.getWidth(), (double)y/locationContext.getHeight());
    }
    
    /** Converts the indicated location according to the target locationContext */
    public static Location2D convertTo(Location2D loc, LocationContext sourceLocationContext, LocationContext targetLocationContext)
    {
        return loc.toNCS(sourceLocationContext).fromNCS(targetLocationContext);
    }
    
    /** Converts the current location according to the target locationContext */
    public Location2D convertTo( LocationContext sourceLocationContext, LocationContext targetLocationContext) {
        return (new Location2D(this.x, this.y)).toNCS(sourceLocationContext).fromNCS(targetLocationContext);
    }
    
    /** Calculates the distance between the current Location2D and the specified Location2D */
    public double distanceTo(Location2D toLocation) {
         return Math.sqrt((x-toLocation.getX())*(x-toLocation.getX()) + (y-toLocation.getY())*(y-toLocation.getY()));
    }
    
    /** Returns the angle [in radians] made in between [p1-p2] & [p2-p3] segments */
    public static double angleRad(Location2D p1, Location2D p2, Location2D p3)
    {
        double m1; // slope of the [p1-p2] line segment
        double m2; // slope of the [p2-p3] line segment
        //double theta; // angle theta = atan[(m1-m2)/(1+m1*m2)]
        
        m1 = slope(p1, p2);
        m2 = slope(p2, p3);
        return Math.atan(Math.abs(( m1 - m2 )/(1 + m1 * m2)));
    }
    
    /** Returns the angle [in degrees -180 : + 180] made in between [p1-p2] & [p2-p3] segments */
    public static double angleDeg(Location2D p1, Location2D p2, Location2D p3)
    {
        double angle = angleRad(p1, p2, p3) * 180 / Math.PI;
        if (angle > 180)
            angle = angle - 360;
        if (angle < -180)
            angle = angle + 360;
        return angle;
    }
    
    /** Returns the slope of the line segment [p1-p2] */
    public static double slope(Location2D p1, Location2D p2)
    {
        if (p1.getX() == p2.getX())
            return 0;
        return ((double)(p1.getY() - p2.getY())) / ((double)(p1.getX() - p2.getX()));
    }
    
    /** Returns euclidean distance from origin */
    public double norm()
    {
        return distanceTo(new Location2D(0,0));
    }
}
