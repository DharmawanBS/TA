/*
 * GPSimpl.java
 *
 * Created on October 25, 2007, 5:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

import sidnet.core.interfaces.GPS;


/**
 *
 * @author Oliver
 */
public class GPSimpl implements GPS{
    private static LocationContext locationContext;
    private Location2D location2d;
    
    /** Creates a new instance of GPSimpl */
    public GPSimpl(Location2D staticLocation2d) {
        locationContext = null;
        this.location2d = staticLocation2d;
    }
    
    public void configure(LocationContext locationContext)
    {
        this.locationContext = locationContext;
    }
    
    public NCS_Location2D getNCS_Location2D()
    {
        if (locationContext == null)
        {
            System.err.println("[GPSimpl] WARNING: GPS not configured properly. Unknown location context. Call GPSimpl.configure(LocationContext)");
            System.exit(-1);
        }
        return location2d.toNCS(locationContext); // not implemented yet
    }
}
