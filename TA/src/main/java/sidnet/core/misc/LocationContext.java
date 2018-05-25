/*
 * LocationContext.java
 * @version 1.0
 *
 * Created on October 23, 2007, 1:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

/**
 *
 * @author Oliviu Ghica, Northwestern University
 *
 * POJO that holds the dimensions of the field in which Location2D are valid/compatible/represented relative to
 */
public class LocationContext {
    private int width, height;      /** dimensions of a field, panel, etc */

    /** Constructor. Creates a new instance of LocationContext */
    public LocationContext(int width, int height) {
        this.width  = width;
        this.height = height;
    }
    
    /** Copy Constructor. Creates a new instance of a LocationContext */
    public LocationContext(LocationContext locationContext)
    {
        this.width = locationContext.getWidth();
        this.height = locationContext.getHeight();
    }
    
    /** Setter method. Sets the width of the panel/field */
    public void setWidth(int width) {
        this.width = width;
    }  
               
    /** Setter method. Sets the height of the panel/field */
    public void setHeight(int height) {
        this.height = height;
    }  
    
    /** Getter method. Gets the width of the panel/field */
    public int getWidth()
    {
        return width;
    }
    
    /** Getter method. Gets the height of the panel/field */
    public int getHeight()
    {
        return height;
    }

    /** 
     * Converts a Location2D information represented relative to one locationContext to a new Location2D represented relative to "thisLocationContext"
     * without having to convert it to NCS first
     */
    public Location2D adapt(Location2D thisLocation, LocationContext thisLocationContext)
    {
        int x, y;
        
        x = (int)((long)thisLocation.getX() * (long)thisLocationContext.getWidth() / width );
        y = (int)((long)thisLocation.getY() * (long)thisLocationContext.getHeight() / height) ;
        
        return new Location2D(x, y);
    }
}
