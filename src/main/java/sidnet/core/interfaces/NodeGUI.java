/*
 * NodeGUI.java
 *
 * Created on October 30, 2007, 7:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import javax.swing.JProgressBar;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;
import sidnet.core.terminal.Terminal;

/**
 *
 * @author Oliver
 */
public interface NodeGUI {
     /**
     * Get the location of the sensor node on the screen/panel display, in pixels
     * <p>
     * @returns Location2D  in pixels
     */
    public Location2D getPanelLocation2D();
    
     /** Returns the LocationContext corresponding to the panel the sensor is drawed (screen/panel dimensions) */
    public LocationContext getLocationContext();
    
    /** Get a handle to the associated Terminal */
    public Terminal getTerminal();
    
    /**
     * Gets the ColorProfile
     */
    public ColorProfile getColorProfile();
    
    /** Contract: mark the given node distinctivly, to visually indicate that it was selected within a group */
    public void markSelected(boolean marked);
}
