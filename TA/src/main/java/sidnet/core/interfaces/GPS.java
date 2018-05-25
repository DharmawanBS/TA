/*
 * GPS.java
 *
 * Created on October 25, 2007, 4:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import sidnet.core.misc.LocationContext;
import sidnet.core.misc.NCS_Location2D;

/**
 *
 * @author Oliviu Ghica, Northwestern University
 *
 * Interface that should "abstract" the meaning of a GPS
 */

public interface GPS {
    /* Configures the GPS
     * <p>
     * @param LocationContext   the location context from which the GPS is returning location coordinates
     */
    public void configure(LocationContext locationContext);
    
    /* Get current location as NCS
     * <p>
     * @return NCS_Location2D       the current location of the hosting object. The location is converted to NCS relative to the LocationContext
     */
    public NCS_Location2D getNCS_Location2D();
}
