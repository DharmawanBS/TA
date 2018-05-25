/*
 * ColorPair.java
 *
 * Created on May 16, 2006, 6:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.colorprofiles;

import java.awt.Color;
/**
 *
 * @author Oliviu Ghica
 */
public class ColorPair {
    public Color innerColor;
    public Color outerColor;
    /** Creates a new instance of ColorPair */
    public ColorPair(Color innerColor, Color outerColor) {
        this.innerColor = innerColor;
        this.outerColor = outerColor;
    }
    
}
