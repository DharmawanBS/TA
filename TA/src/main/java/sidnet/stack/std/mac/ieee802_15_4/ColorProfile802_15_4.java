/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import java.awt.Color;
import java.util.HashMap;
import sidnet.core.interfaces.ColorProfile;

/**
 *
 * @author Oliver
 */
public class ColorProfile802_15_4 extends ColorProfile {
    public static final String RADIO_SLEEPS = "RADIO_SLEEPS";    
    
    public ColorProfile802_15_4()
    {
        super();
        register(new ColorBundle(RADIO_SLEEPS, Color.GRAY, Color.GRAY));
    }
}
