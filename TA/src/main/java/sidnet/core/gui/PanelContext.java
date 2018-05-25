/*
 * PanelContext.java
 *
 * Created on October 15, 2007, 6:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import sidnet.core.misc.LocationContext;



/**
 *
 * @author Oliver
 */
public final class PanelContext {
    private JPanel contextPanel;
    private JPopupMenu contextPopupMenu;
    
    public PanelContext(JPanel panel, JPopupMenu popupMenu)
    {
        this.contextPanel = panel;
        this.contextPopupMenu = popupMenu;
        
        contextPanel.addMouseListener(new PopupListener());
    }
        
    public JPanel getPanelGUI()
    {
        return contextPanel;
    }
    
    public JPopupMenu getMenu()
    {
        return contextPopupMenu;
    }
    
    public LocationContext getLocationContext()
    {
        return new LocationContext(contextPanel.getWidth(), contextPanel.getHeight());
    }
    
    class PopupListener extends MouseAdapter 
    {

        /**
         * Checks for context popup calls in mouse released events.
         */
    	public void mouseReleased(MouseEvent e) 
        {
			checkPopup(e);
             super.mouseReleased(e);
        }

        /**
         * Checks for context popup calls in mouse pressed events.
         */
		public void mousePressed(MouseEvent e) {
			checkPopup(e);
			super.mousePressed(e);
		}
        
		/**
		 * Checks if mouse event is calling for a popup and displays the context menu if necessary.
		 * This check should be performed both on mouse press and release events.
		 * 
		 * @see MouseEvent.isPopupTrigger()
		 * 
		 * @param e the mouse event
		 */
        private void checkPopup(MouseEvent e) {
            if (e.isPopupTrigger()) 
            {
               contextPopupMenu.show(e.getComponent(),
                          e.getX(), e.getY());
            }        	
        }
        
    }
}
