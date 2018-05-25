/*
 * NodeGUIimpl.java
 *
 * Created on April 27, 2007, 12:34 PM
 * 
 * @author  Oliviu C. Ghica, Northwestern University
 * @version 1.0.1
 */

package sidnet.core.gui;

import sidnet.core.gui.GroupSelectionTool;
import sidnet.core.gui.staticdrawing.BufferedImageProvider;
import sidnet.core.gui.staticdrawing.bips.StringPlotter;
import sidnet.colorprofiles.ColorPair;
import sidnet.models.energy.batteries.BatteryControl;
import sidnet.core.interfaces.ColorProfile;
import sidnet.core.interfaces.NodeGUI;
import sidnet.core.interfaces.TerminalCallbackInterface;
import sidnet.core.terminal.Terminal;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.Timer;

import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;
import sidnet.core.misc.Node;
import sidnet.core.terminal.TerminalDataSet;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.event.MouseInputListener;
import jist.runtime.JistAPI;
import jist.swans.Constants;
import jist.swans.field.Field;
import jist.swans.misc.Location;
import sidnet.colorprofiles.ColorProfileGeneric;
import sidnet.core.misc.NodeEntry;
import sidnet.core.misc.NodesList;
import sidnet.core.simcontrol.SimManager;


public class NodeGUIimpl 
extends JPanel
implements ActionListener, MouseListener, MouseInputListener,
           TerminalCallbackInterface, NodeGUI, JistAPI.DoNotRewrite {
	
	private static long repaintContor = 0;
	
	private static final boolean DEBUG = false;
        
    private static SimGUI simGUI;
      
    private static final Ellipse2D.Double MarkSendSymbol = new Ellipse2D.Double(2, 2, 6, 6);
    private static final Ellipse2D.Double symbol2D3rd = new Ellipse2D.Double(2, 2, 6, 6);        // Small circle

    public ColorProfile colorCode = null;
    private boolean selectionMarked = false;
    private boolean previousSelectionMarked = false;
    
    public Location2D panelLocation;
    
    private static JProgressBar progressBar;
  
    public Node node;
    private Field field = null; // used for radio-relocation; if non-null, the relocation is enabled
    
    private static PanelContext hostPanelContext;
    private static Graphics2D hostG2D;
    
    public TerminalDataSet localTerminalDataSet;
    
    // Built-in menus
    private JPopupMenu nodePopupMenu;
    
    private JMenuItem menuItemTerminal;
    private JMenuItem menuItemClearAll;
    private  JMenuItem menuItemIP;
    private  JMenuItem menuItemLocation;
    private  JMenuItem menuItemEnergy;
    private  JMenuItem menuItemKill;
    private JMenuItem menuItemNeighbors;
    private JMenuItem menuItemUdefData1;
    private JMenuItem menuItemUdefData2;
    private JMenuItem menuItemUdefData3;
    
    private String userDefinedData1 = "";
    private String userDefinedData2 = "";
    private String userDefinedData3 = "";
    
    private boolean displayIP             = false;
    private boolean displayLocation       = false;
    private boolean displayEnergy         = false;
    private boolean displayUdefData1      = false;
    private boolean displayUdefData2      = false;
    private boolean displayUdefData3      = false;
    
    private int displayItemCount = 0;
    
    // Terminal 
    private static Terminal terminal;    
    private static LocationContext locationContext;    
    private static NeighborsPanel neighborsPanel = null;
    private boolean displayNeighbors = false;
    
    private BufferedImage nodeImage = null;
    
    private boolean nodeImageChanged = true;
    
    // Node movement vars
    private boolean moving = false;
    private boolean originalDisplayLocation = false;
    private int origSimSpeed = 0;
    
    // for testing
    public long refreshAttemptContor = 0;
    public long refreshPerformedContor = 0;
    
    private static NodeImageManager nodeImageManager;
    private BufferedImage lastPaintedImage = null;
    
    private Timer swingTimer;
    
    static {
    	nodeImageManager = new NodeImageManager();
    }
    
    public void setSimGUI(SimGUI simGUI) {
    	this.simGUI = simGUI;
    }
    
    private long lifetime = 0;
    
    // Group Interaction
    private static GroupSelectionTool groupSelectionTool = null;
    
    /**
     * Creates new form NodeGUIimpl
     */
    public Graphics2D getHostGUIPanel() {
        return hostG2D;
    }
    
    public NodeGUIimpl(PanelContext hostPanelContext, 
    				   ColorProfile colorCode, Node node) {
    	
        initComponents();
        
        swingTimer = new Timer(10000, this);
        swingTimer.setRepeats(false);
        swingTimer.setCoalesce(true);        
        
        NodeGUIimpl.hostPanelContext = hostPanelContext;
        
        hostPanelContext.getPanelGUI().add(this);
        
        // make sure that nodes are on TOP
        hostPanelContext.getPanelGUI().setComponentZOrder(this, 0);
        
        this.node = node;
        
        if (neighborsPanel == null)
        	neighborsPanel = new NeighborsPanel(hostPanelContext);        	
        
        this.colorCode = colorCode;
        this.colorCode.setNodeGUICallback(this);
        this.setSize(11,11);
        //this.setVisible(true);        
        
        this.setBackground(Color.gray);
        this.setOpaque(false);
        
        locationContext = new LocationContext(hostPanelContext.getPanelGUI().getWidth(), hostPanelContext.getPanelGUI().getHeight());
        
        hostG2D = (Graphics2D)hostPanelContext.getPanelGUI().getGraphics();
        if (hostG2D != null)
        	hostG2D.setBackground(Color.GRAY);
        
        if (terminal == null)
            terminal = new Terminal();
        
        // Create the default pop-up menus (to view its energy, location)
        menuItemTerminal = new JMenuItem("Connect Terminal to ...");
        
        menuItemIP        = new JMenuItem("Show/Hide Node ID");
        menuItemLocation  = new JMenuItem("Show/Hide Location Coordinates");
        menuItemEnergy    = new JMenuItem("Show/Hide Energy Levels");
        menuItemNeighbors = new JMenuItem("Show/Hide Discovered Neighbors");
        menuItemUdefData1 = new JMenuItem("Show/Hide User Defined Data 1");
        menuItemUdefData2 = new JMenuItem("Show/Hide User Defined Data 2");
        menuItemUdefData3 = new JMenuItem("Show/Hide User Defined Data 3");
        menuItemKill      = new JMenuItem("Kill Node");
        menuItemClearAll  = new JMenuItem("Clear all ...");
        
        nodePopupMenu = new JPopupMenu();
        
        menuItemTerminal.addActionListener( this );
        
        menuItemIP.addActionListener(this);
        menuItemLocation.addActionListener( this );
        menuItemEnergy.addActionListener( this );
        menuItemNeighbors.addActionListener( this );
        menuItemUdefData1.addActionListener( this );
        menuItemUdefData2.addActionListener( this );
        menuItemUdefData3.addActionListener( this );
        menuItemKill.addActionListener( this );
        menuItemClearAll.addActionListener( this );
        
        
        nodePopupMenu.add(menuItemTerminal);
        nodePopupMenu.addSeparator();
        nodePopupMenu.add(menuItemIP);
        nodePopupMenu.add(menuItemLocation);
        nodePopupMenu.add(menuItemEnergy);
        nodePopupMenu.add(menuItemNeighbors);
        nodePopupMenu.add(menuItemUdefData1);
        nodePopupMenu.add(menuItemUdefData2);
        nodePopupMenu.add(menuItemUdefData3);
        nodePopupMenu.add(menuItemKill);
        nodePopupMenu.addSeparator();
        nodePopupMenu.add(menuItemClearAll);
        
        addMouseMotionListener( this );
        addMouseListener( this );   
        
    	lastPaintedImage = nodeImageManager.getImageFor(colorCode.getColorSet());

    }
    
    public void enableRelocation(Field field) {
        this.field = field;
    }
    
    public PanelContext getPanelContext() {
        return hostPanelContext;
    }
   
    public void setPanelLocation2D(Location2D panelLocation) {
        // Expects a location expressed in screen coordinates (pixels)
        
        ((JPanel)this).setBounds((int)panelLocation.getX(),
        						 (int)panelLocation.getY(), 13, 13);
        if (panelLocation == null) {
            System.out.println("Null panel location reported at node ID: " +
            					node.getID());
            System.exit(1);
        }
        this.panelLocation = panelLocation;
        this.setVisible(true); 
    }

    
     /* **************** *
     * NodeGUI interface *
     * ***************** */
    public Location2D getPanelLocation2D() {
        return panelLocation;
    }
    
    public LocationContext getLocationContext() {
        return locationContext;
    }
    
    public Terminal getTerminal() {
        return terminal;
    }
    
    /**
     * Returns the ColorProfile associated with this node
     * <p>
     * @returns ColorProfile
     */
    public ColorProfile getColorProfile() {
        return colorCode;
    }
    
    public void setGroupSelectionTool(GroupSelectionTool groupSelectionTool) {
        this.groupSelectionTool = groupSelectionTool;
    }
    
    public void markSelected(boolean marked) {
        if (DEBUG) System.out.println("[DEBUG][NodeGUIImpl.markSelected()");
        this.selectionMarked = marked;
        if (previousSelectionMarked != marked) {
            if (marked)
                colorCode.mark(new ColorProfileGeneric(),
                				   ColorProfile.SELECTED,
                				   ColorProfile.FOREVER);
            else
                colorCode.mark(new ColorProfileGeneric(),
                				   ColorProfile.SELECTED,
                				   ColorProfile.CLEAR);  
            updateNodeImage();
            previousSelectionMarked = marked;
        }
    }
    
    public void dataExchange(TerminalDataSet terminalDataSet) {
        localTerminalDataSet = terminalDataSet;
        node.setQueryList(terminalDataSet.getQueryList());
        node.getAppCallback().signalUserRequest();
    }
        
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        jPopupMenu = new javax.swing.JPopupMenu();

        addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                formMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                formMouseEntered(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseEntered(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseEntered
    {//GEN-HEADEREND:event_formMouseEntered
         setToolTipText("<html>NodeID: " + node.getID() +
                        "<br>Enrg [%]: " +
                        (int)node.getEnergyManagement().getBattery().getPercentageEnergyLevel() +
                        "<br>Coverage [m]: " + (long)node.getEffectiveCoverage_ft() +
                        "<br>Location [m]: (" + (int)node.getLocation2D().getX() + "," + (int)node.getLocation2D().getY() + ")</html>");
         
    }//GEN-LAST:event_formMouseEntered

    private void formMouseClicked(java.awt.event.MouseEvent evt) {
    }//GEN-LAST:event_formMouseClicked
    
    public void addJMenuItem(JMenuItem jMenuItem) {
        jPopupMenu.add(jMenuItem);
    }
    
    // Turn on-off GUI drawing
    public void setVisibility(boolean visibility) {
        this.setVisible(visibility);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu jPopupMenu;
    // End of variables declaration//GEN-END:variables
    
    public void updateNodeImage() {
    	if(node != null &&
           node.getSimControl() != null &&
           node.getSimControl().getSpeed() == SimManager.MAX)
           return;   
    	 
    	if (colorCode != null) {
            lifetime = colorCode.getTimeToColorChange();  
            ColorPair colorPair = colorCode.getColorSet();
            if (colorPair != null) {
            	BufferedImage newImage = nodeImageManager.getImageFor(colorPair);
            	if (newImage != lastPaintedImage) {
            		lastPaintedImage = newImage;
            		repaint();
            	} 
            	if (lifetime > 0) {            		
            		swingTimer.setInitialDelay((int) lifetime);
            		swingTimer.restart();
            	}
            }           
    	}
    }
    
    
    /* GRAPHICS */
     public synchronized void paintComponent(Graphics g) {        	    	
    	  refreshPerformedContor++;
          
          Graphics2D g2d = (Graphics2D)g;            

          g2d.drawImage(lastPaintedImage, 0, 0, this);                       
     }

    protected void clear(Graphics g) {
        //super.paintComponent(g); 
    }           
    
    public void addPopupMenus(JPopupMenu extraNodePopupMenus) {
        nodePopupMenu.add(extraNodePopupMenus);
    }
    
    public void actionPerformed(ActionEvent e) {
    	if (e.getActionCommand() == null) {
    		updateNodeImage();
    		return;
    	}
        nodeImageChanged = true;
        if (groupSelectionTool != null && groupSelectionTool.groupExists() && groupSelectionTool.isGroupMember(this.node))
            groupSelectionTool.notifyActionPerformed(node, e);
        else
            handleAction(e);
     }
    
    public void setUserDefinedData1Visible(boolean visibility)
    {
        if (displayUdefData1 == visibility)
            return;
        
        if (visibility)
            displayItemCount++;
        else
            displayItemCount--;
            
        displayUdefData1 = visibility;
        
        customRepaint(true);
    }
    
    public void setUserDefinedData2Visible(boolean visibility)
    {
        if (displayUdefData2 == visibility)
            return;
        
        if (visibility)
            displayItemCount++;
        else
            displayItemCount--;
            
        displayUdefData2 = visibility;
              
        customRepaint(true);
    }
    
    public void setUserDefinedData3Visible(boolean visibility)
    {
       if (displayUdefData3 == visibility)
            return;
        
        if (visibility)
            displayItemCount++;
        else
            displayItemCount--;
            
        displayUdefData3 = visibility;
        
        customRepaint(true);
    }
    
    public void setUserDefinedData1(String data) {
    	boolean needsRepaint = true;
    	if (this.userDefinedData1.equals(data))
    		needsRepaint = false;
    	
        this.userDefinedData1 = data;
        
        if (needsRepaint)
        	customRepaint(true);
    }
    public void setUserDefinedData1(int data) {
    	setUserDefinedData1("" + data);
    }
    
    public void setUserDefinedData2(String data) {
    	boolean needsRepaint = true;
    	if (this.userDefinedData2.equals(data))
    		needsRepaint = false;
    	
        this.userDefinedData2 = data;
               
        if (needsRepaint)
        	customRepaint(true);
    }
    
    public void setUserDefinedData2(int data) {
    	setUserDefinedData2("" + data);
    }

    public void setUserDefinedData3(String data) {
    	boolean needsRepaint = true;
    	if (this.userDefinedData3.equals(data))
    		needsRepaint = false;
    	
        this.userDefinedData3 = data;
        
        if(needsRepaint)
        	customRepaint(true);  	
    }
    public void setUserDefinedData3(int data) {
        setUserDefinedData3("" + data);
    }

    public void handleAction(ActionEvent e) {
        boolean needsParentRepaint = false;
        
        if (e.getActionCommand() == "Connect Terminal to ...") {
            terminal.dataExchange(localTerminalDataSet, node, this);
        }

        if (e.getActionCommand() == "Clear all ...") {
            displayIP = false;
            displayLocation = false;
            displayEnergy = false;
            displayNeighbors = false;
            neighborsPanel.remove(this);
            displayUdefData1 = false;
            displayUdefData2 = false;
            displayUdefData3 = false;
            displayItemCount = 0;
            updateCustomNodeData();
        }

        if (e.getActionCommand() == "Show/Hide Node ID") {
            displayIP = !displayIP;  
            if (displayIP)
                displayItemCount++;
            else
                displayItemCount--;
        }
        
        if (e.getActionCommand() == "Show/Hide Location Coordinates") {
            displayLocation = !displayLocation;
            if (displayLocation)
                displayItemCount++;
            else
                displayItemCount--;
        }
        
        if (e.getActionCommand() == "Show/Hide Energy Levels") {
            displayEnergy = !displayEnergy;
            if (displayEnergy)
                displayItemCount++;
            else
                displayItemCount--;
        }

        if (e.getActionCommand() == "Show/Hide Discovered Neighbors") {
            displayNeighbors = !displayNeighbors;  
            needsParentRepaint = true;
        }

        if (e.getActionCommand() == "Show/Hide User Defined Data 1") {
            displayUdefData1 = !displayUdefData1;
            if (displayUdefData1)
                displayItemCount++;
            else
                displayItemCount--;
        }

        if (e.getActionCommand() == "Show/Hide User Defined Data 2") {
            displayUdefData2 = !displayUdefData2;
            if (displayUdefData2)
                displayItemCount++;
            else
                displayItemCount--;
        }

        if (e.getActionCommand() == "Show/Hide User Defined Data 3") {
            displayUdefData3 = !displayUdefData3;
            if (displayUdefData3)
                displayItemCount++;
            else
                displayItemCount--;
        }

        if (e.getActionCommand() == "Kill Node") {
            ((BatteryControl)node.getEnergyManagement().getBattery()).deplete();
            colorCode.mark(new ColorProfileGeneric(), 
            				   ColorProfile.DEAD, ColorProfile.FOREVER);    
        }        
        
        if (displayItemCount > 0)
        	needsParentRepaint = true;
        
        customRepaint(needsParentRepaint);
    }
    
    public void updateLocation2D(Location2D panelLocation)
    {        
        if (field == null)
            return;
       
        // check bounds. We don't want the node to get out of the visible area
        int newLocX = (int)panelLocation.getX();
        int newLocY = (int)panelLocation.getY();

        if (newLocX < 0)
            newLocX = 0;
        if (newLocX > hostPanelContext.getPanelGUI().getBounds().getHeight() - this.getBounds().getHeight())
            newLocX = (int)(hostPanelContext.getPanelGUI().getBounds().getHeight() - this.getBounds().getHeight());

        if (newLocY < 0)
            newLocY = 0;
        if (newLocY > hostPanelContext.getPanelGUI().getBounds().getHeight() - this.getBounds().getHeight())
            newLocY = (int)(hostPanelContext.getPanelGUI().getBounds().getHeight() - this.getBounds().getHeight());

        this.setLocation(newLocX, newLocY);   

        // Update the local panel location
        panelLocation.setX(newLocX);
        panelLocation.setY(newLocY);

        field.moveRadio(node.getID(), new Location.Location2D((int)node.getLocation2D().getX(), (int)node.getLocation2D().getY()));
        
        // Update the field Location
        node.setLocation2D(
        	panelLocation.toNCS(hostPanelContext.getLocationContext())
        	             .fromNCS(node.getLocationContext()));

        customRepaint(true);
    }
    
    private void customRepaint(boolean needsParentRepaint) {
        // calculate extraWidth
        int extraWidth = 13;
        if (displayIP)
            extraWidth = extraWidth < 30 ? 30 : extraWidth;
        if (displayLocation)
            extraWidth = extraWidth < 80 ? 80 : extraWidth;
        if (displayEnergy)
            extraWidth = extraWidth < 40 ? 40 : extraWidth;
        if (displayUdefData1 || displayUdefData2 || displayUdefData3)
            extraWidth = extraWidth < 50 ? 50 : extraWidth;
        
	    if (displayNeighbors)
	    	neighborsPanel.add(this);	    	
	    else
	    	neighborsPanel.remove(this);

	    node.getSimManager()
			.getSimGUI()
			.staticDrawingLayer.add(neighborsPanel);
	    
        nodeImageChanged = true;
        
        if (needsParentRepaint) {
        	updateCustomNodeData();
        	node.getSimManager().getSimGUI().staticDrawingLayer.repaint();
        	needsParentRepaint = false;
        } else
        	updateNodeImage();
    }
    
    private void updateCustomNodeData() {
    	 // update the node status information
        String str = "";
        
        if (displayIP)
      	  str += ((Integer)node.getID()).toString() + "\n";

        if (displayEnergy)
      	  str += ((Integer)(int)node.getEnergyManagement()
      			  					.getBattery()
      			  					.getPercentageEnergyLevel())
      			  					.toString() + " %" + "\n";

        if (displayLocation)
      	  str += ((Integer)(int)node.getLocation2D().getX()).toString() +
      	  		 " x " +
      	  		 ((Integer)(int)node.getLocation2D().getY()).toString() + "\n";

        if (displayUdefData1)
      	  str += "" + userDefinedData1 + "\n";

      	if (displayUdefData2) 
      	  str += "" + userDefinedData2 + "\n"; 

      	if (displayUdefData3)
      	  str += "" + userDefinedData3 + "\n";
        
        if (str.length() > 0)
        	node.getSimManager()
        		.getSimGUI()
        		.staticDrawingLayer
        		.plot("node-" + node.getID() + "-custom-string", 
        			  new StringPlotter(str, Color.BLACK),
        			  node.getLocation2D(), 
        			  node.getLocationContext());
        else
        	node.getSimManager()
    			.getSimGUI()
    			.staticDrawingLayer
    			.remove("node-" + node.getID() + "-custom-string"); 
    }
    
     public void mouseClicked(MouseEvent e) {   
        /* If right-click, pop-up the menu */
        if (e.getButton() == e.BUTTON3)
           if (e.getX() <= 13)
                nodePopupMenu.show(this, 20,20);
          
        
        /* If left-click, select/deselect the node */
        if (e.getButton() == e.BUTTON1)
            if (groupSelectionTool != null)
                groupSelectionTool.notifySelected(this.node, !selectionMarked);
        
     }
    
    public void mouseEntered(MouseEvent e) {}
    
    public void mouseExited(MouseEvent e) {}
    
    public void mousePressed(MouseEvent e) 
    {
        if (field == null)
            return;
        originalDisplayLocation = displayLocation;
        displayLocation = true;
        origSimSpeed = node.getSimControl().getSpeed();
        node.getSimControl().setSpeed(0); // PAUSE
        moving = true;
    }
    
    public void mouseReleased(MouseEvent e) {
        if (field == null)
            return;
       
        displayLocation = originalDisplayLocation;
        moving = false;
        field.moveRadio(node.getID(), new Location.Location2D((int)node.getLocation2D().getX(), (int)node.getLocation2D().getY()));
        node.getSimControl().setSpeed(origSimSpeed); // RESUME
        customRepaint(true);
    }
    
    public void mouseDragged(MouseEvent ev)
    { 
        if (field == null)
            return;
        
        if (moving)
            updateLocation2D(new Location2D(panelLocation.getX() + ev.getX(), panelLocation.getY() + ev.getY()));
    }
    
    public void mouseMoved(MouseEvent ev) {
        // NOTHING
    }    
}
