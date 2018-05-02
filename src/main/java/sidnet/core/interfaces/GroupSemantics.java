/*
 * GroupSemantics.java
 *
 * Created on November 8, 2007, 3:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import javax.swing.JPopupMenu;

/**
 *
 * @author Oliver
 */
public interface GroupSemantics {
    public void configureGroupMenu(JPopupMenu hostPopupMenu);
     
    public void enableGroupUI();
    
    public void disableGroupUI();
}
