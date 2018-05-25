/*
 * DataExchangeInterface.java
 *
 * Created on October 23, 2007, 1:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;

/**
 *
 * @author Oliver
 */
public interface DataExchangeInterface {
    public double readDataAt(Location2D physicalLocation, LocationContext physicalLocationContext);
    public void writeDataAt(double data, Location2D physicalLocation, LocationContext physicalLocationContext);
}
