/*
 * MovingObjectsPanel.java
 *
 * Created on November 21, 2007, 2:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.models.senseable.mob;

import sidnet.core.interfaces.SIDnetDrawableInterface;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import jist.runtime.JistAPI;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;
import sidnet.core.misc.NCS_Location2D;

/**
 *
 * @author Oliver
 */
@SuppressWarnings("serial")
public class MovingObjectsPanel 
extends JPanel
implements SIDnetDrawableInterface {
	
	private static final int ICON_SIZE = 20;
	
	private static BufferedImage image;
	
    /* DEBUG */
    private static final boolean DEBUG = false;
    
    private List<Location2D> mobs;
    
    /** Since this is a GUI, it is important to know the dimension information of the area in which we draw to be able to convert location information from NCS */
    private LocationContext panelLocationContext;
    
    static {
    	image = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.BITMASK);
    	Graphics2D g2 = image.createGraphics();
		g2.setColor(Color.red);
		g2.fillRect(0, 0, ICON_SIZE, ICON_SIZE);
		g2.dispose();
    }
    
    /** Creates a new instance of MovingObjectsPanel */
    public MovingObjectsPanel() {
        mobs = new LinkedList<Location2D>();
    }
    
    public void configureGUI(JPanel hostPanel)
    {
         if (hostPanel == null)
             return;
         
        this.setOpaque(false);
        this.setBackground(Color.black);
        hostPanel.add(this);

        this.setBounds(hostPanel.getBounds());
        this.setSize(hostPanel.getSize());
        //this.setBounds(0,0, 0, 0);
	    //this.setSize(0,0);

        /* We choose to enable this by default */
        this.setVisible(false); 

         if (hostPanel == null)
             return;
         
       
        panelLocationContext = new LocationContext(hostPanel.getWidth(), hostPanel.getHeight());
    }
    
    public void repaintGUI() {
    	repaint();
    }
    
    public void setVisibleGUI(boolean visibility) {
        this.setVisible(visibility);
    }
    
    public synchronized void updateMobs(List<Location2D> mobs, LocationContext actualLocationContext) {
        if (DEBUG) System.out.println("[DEBUG][" + JistAPI.getTime() + "][MovingObjectsPanel.updateMobs()]");

        if (mobs == null)
            return;
        this.mobs.clear();
        for (Location2D loc: mobs) {
        	if (loc == null) // TODO this should not happen, but it happens with GIS09_DEMO_RQ and two moving objects
        		continue;
            this.mobs.add(loc.toNCS(actualLocationContext).fromNCS(panelLocationContext));  
            repaint((int)loc.getX(), (int)loc.getY(), ICON_SIZE, ICON_SIZE);
        }
    }
    
    public synchronized void paintComponent(Graphics g) {    	
        Graphics2D g2d = (Graphics2D)g;
      
        if (DEBUG) System.out.println("[DEBUG][" + JistAPI.getTime() + "][MovingObjectsPanel.paintComponent()]");
      
        //System.out.println("clip mob -> " + g2d.getClip());
        
        for (Location2D loc:mobs) {
        	if (g2d.getClipBounds().intersects((int)loc.getX() - 2*ICON_SIZE/2, (int)loc.getY()-2*ICON_SIZE/2, ICON_SIZE, ICON_SIZE))
      	    g2d.drawImage(image, (int)loc.getX() - 2*ICON_SIZE/2, (int)loc.getY() - 2*ICON_SIZE/2, this);
        }
    }

    protected void clear(Graphics g) {
        super.paintComponent(g);
    }    
}
