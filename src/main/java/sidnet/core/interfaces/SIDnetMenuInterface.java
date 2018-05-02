/*
 * MenuInterface.java
 *
 * Created on October 4, 2007, 2:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 *
 * @author Oliver
 */
public interface SIDnetMenuInterface extends ActionListener{
    
    public void configureMenu(JPopupMenu hostPopupMenu);
    
    public void passMenuActionEvent(ActionEvent menuEvent);
    
    public void enableUI();
    
    public void disableUI();
}
