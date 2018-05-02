/*
 * TransmitReceiveFX.java
 *
 * Created on December 12, 2007, 11:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.gui;

import sidnet.core.interfaces.SIDnetDrawableInterface;
import sidnet.core.interfaces.SIDnetMenuInterface;
import sidnet.core.interfaces.SIDnetRegistrable;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;
import sidnet.core.misc.NCS_Location2D;

/**
 *
 * @author Oliver
 */
public class TransmitReceiveFX extends JPanel implements SIDnetDrawableInterface, SIDnetMenuInterface, SIDnetRegistrable{
     /** Since this is a GUI, it is important to know the dimension information of the area in which we draw to be able to convert location information from NCS */
    private LocationContext locationContext;
     
    /* Toggle graphics on/off */
    private boolean show = true;
    private int delay = 100;
    
    /* Menu */
    private JMenuItem menuItemShowTransmitReceiveFX;
    
    Graphics2D g2d = null;
    
    /** Creates a new instance of TransmitReceiveFX */
    public TransmitReceiveFX() {
         super();
    }
    
    public void setDelay(int delay)
    {
        this.delay = delay;
    }
    
       /* ********************** *   
     * SIDnetDrawableInterface *
     * ********************** */

    /** Gives the panel handle on which the data will be plot */
    public void configureGUI(JPanel hostPanel)
    {
        if (hostPanel == null)
             return;
         
        this.setOpaque(false);
        this.setBackground(Color.black);
        hostPanel.setLocation(10,10);
        hostPanel.add(this);

        this.setBounds(hostPanel.getBounds());

        this.setSize(hostPanel.getSize());

        /* We choose to enable this by default */
        this.setVisible(false); 

        locationContext = new LocationContext(hostPanel.getWidth(), hostPanel.getHeight());
        
        this.g2d = (Graphics2D)this.getGraphics();
    }
    
    /** Force the screen to redraw */
    public void repaintGUI() { /*if (show) repaint();*/ System.out.println("repaint");}
    
    /** Turns the display on/off */
    public void setVisibleGUI(boolean visible)
    {
        if (show)
            this.setVisible(visible);
    }
    
    /* ****************** *
     * SIDnetMenuInterface *
     * ****************** */
    
    public void configureMenu(JPopupMenu hostPopupMenu)
    {
        menuItemShowTransmitReceiveFX = new JMenuItem("Toggle Transmit/Receive FX");
        menuItemShowTransmitReceiveFX.addActionListener(this);
        hostPopupMenu.add(menuItemShowTransmitReceiveFX);
    }
    
    public void disableUI() { /* do nothing */ }
    
    public void enableUI() { /* do nothing */ }
    
    public void passMenuActionEvent(ActionEvent e) { /* do nothing */ }
       
    public void actionPerformed(ActionEvent e)
    {
         /* Toggle this visual tool graphics on/off */
         if (e.getActionCommand() == "Toggle Transmit/Receive FX")
         {
             System.out.println("SHOW");
             show = !show;
             this.setVisible(show);
         }
    }
    
    /* ************************* *
     * SIDnetRegistrableInterface *
     * ************************* */
    public void terminate() { /* do nothing */ }
    
    public synchronized void transmit(NCS_Location2D fromNCSLocation, NCS_Location2D toNCSLocation, int userValue)
    {  
         if (fromNCSLocation == null || toNCSLocation == null || locationContext == null)
            return;
        Location2D fromLocation = fromNCSLocation.fromNCS(locationContext);
        Location2D toLocation = toNCSLocation.fromNCS(locationContext);
        if (fromLocation.getX() == toLocation.getX() && fromLocation.getY() == toLocation.getY())
            return;
        
        PacketFX packetFX = new PacketFX(fromLocation, toLocation, PacketFX.TRANSMIT, g2d, userValue);
        packetFX.setDelay(delay);
        packetFX.run();
    }
    
    public synchronized void receive(NCS_Location2D fromNCSLocation, NCS_Location2D toNCSLocation)
    {
        if (fromNCSLocation == null || toNCSLocation == null || locationContext == null)
            return;
        Location2D fromLocation = fromNCSLocation.fromNCS(locationContext);
        Location2D toLocation = toNCSLocation.fromNCS(locationContext);
        if (fromLocation.getX() == toLocation.getX() && fromLocation.getY() == toLocation.getY())
            return;
        
        PacketFX packetFX = new PacketFX(fromLocation, toLocation, PacketFX.RECEIVE, g2d);
        packetFX.setDelay(delay);
        packetFX.run();
    }
    
    public synchronized void repaint(Graphics g)
    {  
        /* NOTHING */
    }
    
    protected void clear(Graphics g) {
        //super.paintComponent(g);
    }    
}
