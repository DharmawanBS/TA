/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.std.mac;

import java.awt.Color;
import java.util.HashMap;
import sidnet.core.interfaces.ColorProfile;

/**
 *
 * @author Oliver
 */
public class ColorProfileMac802_11_4 extends ColorProfile {
    public static final String RADIO_SLEEPS = "RADIO_SLEEPS";    
    
    public ColorProfileMac802_11_4()
    {
        super();
        register(new ColorBundle(RADIO_SLEEPS, null, Color.GRAY));
    }
}
