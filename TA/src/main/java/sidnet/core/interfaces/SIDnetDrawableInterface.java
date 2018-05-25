/*
 * GuiInterface.java
 *
 * Created on October 4, 2007, 2:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import javax.swing.JPanel;
/**
 *
 * @author Oliver
 */
public interface SIDnetDrawableInterface{
    
    public void configureGUI(JPanel hostPanel); 
    
    public void setVisibleGUI(boolean visible);
    
    public void repaintGUI();
}
