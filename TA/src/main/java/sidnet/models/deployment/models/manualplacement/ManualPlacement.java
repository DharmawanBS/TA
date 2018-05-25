/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.models.deployment.models.manualplacement;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.swing.JMenuItem;
import jist.swans.field.Placement;
import sidnet.models.deployment.interfaces.DeploymentModel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputListener;
import jist.runtime.JistAPI;
import jist.swans.misc.Location.Location2D;
import sidnet.core.gui.SimGUI;
import sidnet.core.interfaces.SIDnetMenuInterface;
import sidnet.models.deployment.utils.PlacementList;



/**
 *
 * @author Oliver
 */
public class ManualPlacement extends JPanel implements DeploymentModel, MouseInputListener, SIDnetMenuInterface, JistAPI.DoNotRewrite{    
    private PlacementList placementList;
    private boolean manualSessionCompleted;
    private int fieldWidth, fieldHeight;
    private int contor = 0; // used also for node ID
    private static final int EPSILON = 15;
    private Location2D nearbyFieldLoc = null; // needed when performing node relocation on the screen
    
    private String originalTitle = null;
    
    // GUI
    private SimGUI simGUI;
    private JPanel hostPanel;
    
    // Menu
    private JPopupMenu hostPopupMenu;
    private JMenuItem menuItemFinishManualDeployment; 
    public String textMenuItemFinishManualDeployment = "I'm DONE with Manual Deployment";
    private JMenuItem menuItemRemoveLastPlacedNode; 
    public String textMenuItemRemoveLastPlacedNode = "Remove Last Placed Node";
    
    public ManualPlacement(int fieldHeight, int fieldWidth, SimGUI simGUI)
    {
        this(fieldHeight, fieldWidth, simGUI.getSensorsPanelContext().getPanelGUI(), simGUI.getSensorsPanelContext().getMenu());
        this.simGUI = simGUI;
        originalTitle = simGUI.getTitle();
        simGUI.appendTitle("Manual Deployment v1.0");
    }
    
    private ManualPlacement(int fieldHeight, int fieldWidth, JPanel hostPanel, JPopupMenu hostPopupMenu)
    {
        this.fieldHeight       = fieldHeight;
        this.fieldWidth        = fieldWidth;
        this.hostPanel         = hostPanel;
        
        placementList          = new PlacementList();
        manualSessionCompleted = false;
        
        this.setBounds(hostPanel.getBounds());
        this.setOpaque(true);
        this.setLocation(0,0);
        this.setSize(hostPanel.getSize());
        this.setBackground(Color.GRAY);
        hostPanel.addMouseListener(this);
        hostPanel.addMouseMotionListener(this);
        hostPanel.add(this);
        
        this.configureMenu(hostPopupMenu);
        this.enableUI();
    }
    
    public Placement getPlacement() {
        while(!manualSessionCompleted) // to avoid using a callback method
        {
            try{ // OLIVER: a poor, but fast way to get this going.
                Thread.sleep(500);
            }catch(Exception e){e.printStackTrace();};
        }
        return placementList;
    }
    
    public void mouseClicked(MouseEvent e) {
        Location2D newLocation = new Location2D(e.getX() * fieldWidth / hostPanel.getWidth(), e.getY() * fieldHeight / hostPanel.getHeight());
        if (getNearbyFieldLocation(new Location2D(e.getX(), e.getY()), EPSILON, null) != null)
            return;
        placementList.add(newLocation);
        //super.repaint(e.getY() * fieldWidth / hostPanel.getY(), e.getX() * fieldHeight / hostPanel.getX(), 10, 10);
        super.repaint();
        contor++;
    }
   

    public void mouseEntered(MouseEvent e) {
        // NOT implemented
    }

    public void mouseExited(MouseEvent e) {
        // NOT implemented
    }

    public void mousePressed(MouseEvent e) {
        // get the node that is within Epsilon of the point the mouse was pressed
        nearbyFieldLoc = getNearbyFieldLocation(new Location2D(e.getX(), e.getY()), EPSILON, null);
        
        super.repaint();
    }

    public void mouseReleased(MouseEvent e) {
        // NOT implemented
        
        nearbyFieldLoc = null;
        super.repaint();
    }
    
    public void mouseDragged(MouseEvent e) {
        // NOT implemented
        if (nearbyFieldLoc != null)
        if (getNearbyFieldLocation(new Location2D(e.getX(), e.getY()), EPSILON, nearbyFieldLoc) == null)
        {
            nearbyFieldLoc.setX(e.getX() * fieldWidth  / hostPanel.getWidth());
            nearbyFieldLoc.setY(e.getY() * fieldHeight / hostPanel.getHeight());
        }
        
        super.repaint();
    }

    public void mouseMoved(MouseEvent e) {
        // NOT implemented
    }
    
    private Location2D getNearbyFieldLocation(Location2D referencePanelLoc, int epsilon, Location2D exceptingLocation)
    {
        LinkedList<Location2D> locationList = placementList.getAsLinkedList();
        Location2D bestFieldLoc = null;
        Location2D referenceFieldLoc = new Location2D(referencePanelLoc.getX() * fieldWidth / hostPanel.getWidth(), referencePanelLoc.getY() * fieldHeight / hostPanel.getHeight());
        for (Location2D fieldLoc: locationList)
        {
            if (exceptingLocation != null && fieldLoc == exceptingLocation)
                continue;
            if (referenceFieldLoc.distance(fieldLoc) < epsilon)
            {
                if (bestFieldLoc == null)
                    bestFieldLoc = fieldLoc;
                else
                    if (referencePanelLoc.distance(fieldLoc) < referencePanelLoc.distance(bestFieldLoc))
                        bestFieldLoc = fieldLoc;
            }
        }
        return bestFieldLoc;
    }
    
    public void configureMenu(JPopupMenu hostPopupMenu) {
        this.hostPopupMenu = hostPopupMenu;
        
        menuItemFinishManualDeployment = new JMenuItem(textMenuItemFinishManualDeployment);
        menuItemFinishManualDeployment.addActionListener(this);
        
        menuItemRemoveLastPlacedNode = new JMenuItem(textMenuItemRemoveLastPlacedNode);
        menuItemRemoveLastPlacedNode.addActionListener(this);
    }

    public void disableUI() {
        menuItemFinishManualDeployment.setVisible(false);
        hostPopupMenu.remove(menuItemFinishManualDeployment);
        
        menuItemRemoveLastPlacedNode.setVisible(false);
        hostPopupMenu.remove(menuItemRemoveLastPlacedNode);
    }

    public void enableUI() {
        hostPopupMenu.add(menuItemFinishManualDeployment);
        hostPopupMenu.add(menuItemRemoveLastPlacedNode);
        
        menuItemFinishManualDeployment.setVisible(true);        
        menuItemRemoveLastPlacedNode.setVisible(true);
        
        hostPopupMenu.setVisible(true);
    }

    public void passMenuActionEvent(ActionEvent menuEvent) {
        // DO NOTHING
    }
    
    public void actionPerformed(ActionEvent e) {
       if (e.getActionCommand().equals(textMenuItemFinishManualDeployment))
       {
           this.disableUI();
           // remove this from the GUI since we won't need it again
           hostPanel.remove(this);
            
           // restore the original title
           simGUI.setTitle(originalTitle);
           manualSessionCompleted = true;    
       }
       if (e.getActionCommand().equals(textMenuItemRemoveLastPlacedNode))
       {
            if (placementList.getAsLinkedList() != null && placementList.getAsLinkedList().size() > 0)
                placementList.getAsLinkedList().removeLast();
            super.repaint();
       }
    }
    
   @Override 
   public void paintComponent(Graphics g)
   {
        super.paintComponent(g);
        Graphics2D g2d  = (Graphics2D)g;
        
        LinkedList<Location2D> locationList = placementList.getAsLinkedList();
        
        int localcontor = 0;
        for (Location2D loc: locationList)
        {
            g2d.setColor(Color.WHITE);
            g2d.drawString("O", loc.getX() * hostPanel.getWidth() / fieldWidth , loc.getY() * hostPanel.getHeight() / fieldHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawString("" + localcontor, loc.getX() * hostPanel.getWidth() / fieldWidth + 15, loc.getY() * hostPanel.getHeight() / fieldHeight);
            localcontor++;
        }
        if (nearbyFieldLoc != null)
        {
            g2d.setColor(Color.RED);
            g2d.drawString("O", nearbyFieldLoc.getX() * hostPanel.getWidth() / fieldWidth , nearbyFieldLoc.getY() * hostPanel.getHeight() / fieldHeight);
        }
   }
   
    protected void clear(Graphics g) {
       //super.paintComponent(g);
    }    
}
 