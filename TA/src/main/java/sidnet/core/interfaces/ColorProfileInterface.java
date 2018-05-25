/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import sidnet.core.interfaces.ColorProfile.ColorBundle;

/**
 *
 * @author Oliver
 */
public interface ColorProfileInterface {
    public ColorBundle getColorBundle(String tag);
}
