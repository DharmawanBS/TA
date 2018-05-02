/*
 * DataExchangeReadableOnly.java
 *
 * Created on October 23, 2007, 1:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import java.util.Map;

import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;



/**
 *
 * @author Oliver
 */
public interface DataExchangeReadableOnly {
	/**
	 * @deprecated  As of version 1.5.7, replaced by
	 * getSensorReadings(...)
	 * @see #getSensorReadings(Location2D, LocationContext)
	 */
    public double readDataAt(Location2D physicalLocation, LocationContext physicalLocationContext);
    public Map<String, Object> getSensorReadings(Location2D physicalLocation, LocationContext physicalLocationContext);
    public Map<String, Object> getSensorProperties();
}
