/*
 * UtilityView.java
 *
 * Created on April 10, 2008, 4:35 PM
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
@SuppressWarnings("serial")
public abstract class UtilityView
extends JPanel
implements SIDnetDrawableInterface, SIDnetRegistrable {
    public void configureGUI(JPanel hostPanel) {
        this.setOpaque(true);
        hostPanel.add(this);

        this.setVisible(true);
        
        this.setBounds(0,0, hostPanel.getWidth(), hostPanel.getHeight());
    }
    
    public void setVisibleGUI(boolean visibility) {
        this.setVisible(visibility);
    }
    
    public void repaintGUI() {
        repaint();
    }
     
    public void terminate() {
        this.setVisible(false);
        this.setEnabled(false);
    }
}
