/*
 * TopologyGUI.java
 *
 * Created on October 24, 2007, 6:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 * 
 * @version 1.0.1
 * @author Oliver
 */
package sidnet.core.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.LinkedList;
import javax.swing.JPanel;
import sidnet.core.misc.*;
import sidnet.core.interfaces.SIDnetDrawableInterface;
import sidnet.core.interfaces.SIDnetMenuInterface;
import sidnet.core.interfaces.SIDnetRegistrable;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import jist.swans.net.NetAddress;
import sidnet.stack.users.dharmawan_route.routing.Konstanta;

/**
 * Helper class, designed to integrate with SIDnet simulator,
 * maintains and draws a list of links (lines) on the screen
 * Can be used to maintain a visual representation of a topology 
 * (routing structure, for example)
 */
public final class TopologyGUI 
extends JPanel
implements SIDnetDrawableInterface, SIDnetMenuInterface, SIDnetRegistrable{
	
    //DEBUG
    public static final boolean DEBUG = false;

    public static enum HeadType  {LEAD_ARROW, // default
                                  TRAIL_ARROW,
                                  BOTH_ARROW,
                                  NO_ARROW}; // no arrow
    
    private Node[] nodeList = null;
    private LinkedList<TopologyGroup> topologyGroupList;
    
    /* Since this is a GUI, it is important to know the dimension information
     * of the area in which we draw
     * to be able to convert location information from NCS */
    private LocationContext locationContext = null;
     
    /* Toggle graphics on/off */
    private boolean show = true;
    
    private boolean changed = false;
    private BufferedImage image = null;
    
    /* Menu */
    private JMenuItem menuItemShowPhysics;
    
    /**
     * Creates a new instance of TopologyGUI
     */
    public TopologyGUI() {
        topologyGroupList = new LinkedList<TopologyGroup>();
        
        // add the default group
        topologyGroupList.add(new TopologyGroup(-1, Color.BLACK));
    }
    
    public void setNodeList(Node[] nodeList) {
        this.nodeList = nodeList;
        
        //set_cakupan_node(nodeList);
    }
    
    /*private void set_cakupan_node(Node[] nodeList) {
        for(int i=0;i<nodeList.length;i++) {
            for(int j=i+1;j<nodeList.length;j++) {
                nodeList[i].cakupan_node = new HashMap<NetAddress, Location2D>();
                
                if (nodeList[i].getLocation2D().distanceTo(nodeList[j].getLocation2D()) <= Konstanta.RADIUS) {
                    nodeList[i].cakupan_node.put(nodeList[j].getIP(), nodeList[j].getLocation2D());
                    nodeList[j].cakupan_node.put(nodeList[i].getIP(), nodeList[i].getLocation2D());
                }
            }
        }
    }*/
    
    /** 
     * Adds a link to the topology viewer 
     * <p>
     * @param fromNodeWithID     
     * 		  the id (NOT IP) of the node from which to draw the link (arrow)
     * @param toNodeWithID       
     * 		  the id (NOT IP) of the node to which do draw the link
     * 		 (tip of the arrow)
     * @param color              
     * 		  the color of the link.
     * 		  It may be use as a mean of identification
     * 		  if two arrows share the same locations
     */
    public void addLink(int fromNodeWithID, int toNodeWithID,
    					int groupId, Color groupColor) {
        addLink(fromNodeWithID, toNodeWithID,
        		groupId, groupColor, HeadType.LEAD_ARROW);
    }
    
    /** 
     * Adds a link to the topology viewer
     * <p>
     * @param fromNodeWithID     
     * 		  the id (NOT IP) of the node from which to draw the link (arrow)
     * @param toNodeWithID       
     * 		  the id (NOT IP) of the node to which do draw the link
     * 		  (tip of the arrow)
     * @param color              
     * 		  the color of the link.
     * 		  It may be use as a mean of identification
     * 		  if two arrows share the same locations
     */
    public void addLink(int fromNodeWithID, int toNodeWithID,
    					int groupId, Color groupColor, HeadType headType) {
        addLink(nodeList[fromNodeWithID].getNCS_Location2D(),
        		nodeList[toNodeWithID].getNCS_Location2D(),
        		groupId, groupColor, headType);
    }
    
    public void addLink(NCS_Location2D fromPoint, NCS_Location2D toPoint,
    					int groupId, Color groupColor) {
        addLink(fromPoint, toPoint, groupId, groupColor, HeadType.LEAD_ARROW);
    }
    
    public synchronized void addLink(NCS_Location2D fromPoint,
    							     NCS_Location2D toPoint,
    							     int groupId, 
    							     Color groupColor, HeadType headType) {
        TopologyGroup foundTopologyGroup = getTopologyGroup(groupId);
        
        // If group already defined
        if (foundTopologyGroup != null)
            foundTopologyGroup.addLink(fromPoint, toPoint, headType);
        else {
            TopologyGroup newTopologyGroup 
            	= new TopologyGroup(groupId, groupColor);
            newTopologyGroup.addLink(fromPoint, toPoint, headType);
            topologyGroupList.add(newTopologyGroup);
        }
        
        changed = true;
        repaint();
    }
    
    public void removeGroup(int groupId) {
        TopologyGroup foundTopologyGroup = getTopologyGroup(groupId);
        if (foundTopologyGroup != null) {
            topologyGroupList.remove(foundTopologyGroup);
            foundTopologyGroup.removeAll();
        }
    }
    
    public void removeLink(NCS_Location2D fromPoint,
    					   NCS_Location2D toPoint, int groupId) {
         TopologyGroup foundTopologyGroup = getTopologyGroup(groupId);
         
         foundTopologyGroup.removeLink(fromPoint, toPoint);
         
         changed = true;
         repaint();
    }
    
     public void removeLink(int fromNodeWithID, int toNodeWithID, int groupId) {
         removeLink(nodeList[fromNodeWithID].getNCS_Location2D(),
        		 	nodeList[toNodeWithID].getNCS_Location2D(), groupId);
     }
    
    private synchronized TopologyGroup getTopologyGroup(int groupId) {
        // Retrieve the group specified by groupId, if any
        for (TopologyGroup topologyGroup: topologyGroupList)
            if (topologyGroup.getGroupId() == groupId)
               return topologyGroup;
        
        return null;
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
        hostPanel.add(this);

        this.setSize(hostPanel.getSize());

        locationContext = new LocationContext(hostPanel.getWidth(),
        									  hostPanel.getHeight());
    }
    
    /** Force the screen to redraw */
    public void repaintGUI() { /*if (show) repaint();*/}
    
    /** Turns the display on/off */
    public void setVisibleGUI(boolean visible) {
        if (DEBUG) System.out.println("[DEBUG][TopologyGUI.setVisibleGUI()");
        if (show)
            this.setVisible(visible);
    }
    
    /* ****************** *
     * SIDnetMenuInterface *
     * ****************** */
    
    public void configureMenu(JPopupMenu hostPopupMenu)
    {
        menuItemShowPhysics = new JMenuItem("Show/Hide Topology Visualization");
        menuItemShowPhysics.addActionListener(this);
        hostPopupMenu.add(menuItemShowPhysics);
    }
    
    public void disableUI() { /* do nothing */ }
    
    public void enableUI() { /* do nothing */ }
    
    public void passMenuActionEvent(ActionEvent e) { /* do nothing */ }
       
    public void actionPerformed(ActionEvent e) {
         /* Toggle this visual tool graphics on/off */
         if (e.getActionCommand() == "Show/Hide Topology Visualization") {
             if (DEBUG) System.out.println("[DEBUG][TopologyGUI.SHOW/Hide]");
             show = !show;
             this.setVisible(show);
         }
    }
    
    /* ************************* *
     * SIDnetRegistrableInterface *
     * ************************* */
    public void terminate() { /* do nothing */ }
    
    public synchronized void paintComponent(Graphics g) {
       if (changed) {
             changed = false;
             image = new BufferedImage(this.getWidth(),
            		 				   this.getHeight(),
            		 				   BufferedImage.BITMASK);
             Graphics2D g2 = image.createGraphics();
             g2.setBackground(this.getBackground());
           
             for (TopologyGroup topologyGroup:topologyGroupList)
                  topologyGroup.repaint(image.getGraphics());
             
             g2.dispose();
       }
       if (image != null)
            g.drawImage(image, 0, 0, this);
    }
     
    protected void clear(Graphics g) {
       //super.paintComponent(g); 
    }  
    
    private class TopologyGroup
    {
        private int groupId;
        private Color groupColor;
        
        /* Links are represented as a set of Arrows */
        private LinkedList<Arrow> arrowSet = null;  
        private Graphics g = null;
        
        public TopologyGroup(int groupId) {
            this.groupId = groupId;
            arrowSet = new LinkedList<Arrow>();
        }
                
        public TopologyGroup(int groupId, Color groupColor) {
            this.groupId = groupId;
            
            arrowSet = new LinkedList<Arrow>();
            if (groupColor != null)
                this.groupColor = groupColor;
            else {
                // randomly generate a RGB color
                int r = ((0xC0 & groupId) >> 6) * 64;
               	int g = ((0x38 & groupId) >> 3) * 32;
              	int b = (0x7   & groupId) * 32;

                this.groupColor = new Color(r, g, b);
            }               
        }
        
        public int getGroupId() {
            return groupId;
        }

        public void setGroupColor(Color groupColor) {            
            this.groupColor = groupColor;
        }       
        
        public synchronized void repaint(Graphics g) {  
            this.g = g;
            if (arrowSet != null) {
                //System.out.println("Panah false");
                for(Arrow arrow: arrowSet)
                     ArrowDrawingTool.drawArrow(g, arrow);
            }
            //else System.out.println("Panah true");
        }
        
        /** 
         * Adds a link to the topology viewer
         * <p>
         * @param fromPoint     the origin of the link given as NCS
         * @param toPoint       the end-point of the link as NCS
         * @param color         the color of the link. It may be use as a
         * 					    mean of identification if two arrows 
         * 						share the same locations
         * @param headType      
         */
        public synchronized void addLink(NCS_Location2D fromPoint,
        								 NCS_Location2D toPoint,
        								 HeadType headType) {
            // if an arrow with same location parameters 
        	// and same color exists already, we do nothing and return 
            if (containsLink(fromPoint, toPoint))
                return;

            if (locationContext == null) {
                Exception e = 
                	new Exception(
                		"[ERROR][TopologyGUI] - " +
                		"locationContext not initialized." +
                		"\n\nDid you forgot to write\n\n" +
                        "\tsimManager.registerAndRun(topologyGUI, " +
                        "simGUI.getSensorsPanelContext());\n" +
                        "\ttopologyGUI.setNodeList(myNode);\n\n" +
                        "in the driver file?\n\n");
                e.printStackTrace();
                System.exit(1);
            }
            
            arrowSet.add(new Arrow(fromPoint.fromNCS(locationContext),
            					     toPoint.fromNCS(locationContext),
            					     groupColor,
            					     computeArrowHeadType(headType)));       
            
            if (g != null)
                repaint(g);
        }
        
        private int computeArrowHeadType(HeadType headType) {
            int arrowHead;
            
            switch(headType) {
                case LEAD_ARROW:  arrowHead = 0;break;
                case TRAIL_ARROW: arrowHead = 1;break;
                case BOTH_ARROW:  arrowHead = 2;break;
                case NO_ARROW:    arrowHead = 3;break;
                default: arrowHead = 0;
            }
            
            return arrowHead;
        }

       
        
        /** 
         * Test the existence of a link with the same location and color
         * <p>
         * @param fromPoint     the origin of the link given as NCS
         * @param toPoint       the end-point of the link as NCS
         * @param color         the color (seen as identification to
         * 						distinguish between arrows 
         * 						that share the same locations)
         * @return              TRUE if found; FALSE otherwise
         */
        public synchronized boolean containsLink(NCS_Location2D fromPoint,
        										 NCS_Location2D toPoint) {
            for (Arrow arrow: arrowSet)
                if (arrow.getFromPoint().getX() 
                		== fromPoint.fromNCS(locationContext).getX() &&
                    arrow.getFromPoint().getY() 
                    	== fromPoint.fromNCS(locationContext).getY() &&
                    arrow.getToPoint().getX() 
                    	== toPoint.fromNCS(locationContext).getX()     &&
                    arrow.getToPoint().getY() 
                    	== toPoint.fromNCS(locationContext).getY())
                        return true;

            return false;
        }
        
        public synchronized void removeAll() {
            arrowSet.clear();
        }
        
        /** 
         * Remove a link from the topological view
         * <p>
         * @param fromPoint     the origin of the link given as NCS
         * @param toPoint       the end-point of the link as NCS
         * @param color         the color (seen as identification 
         * 						to distinguish between arrows 
         * 						that share the same locations)
         */
        public synchronized void removeLink(NCS_Location2D fromPoint,
        									NCS_Location2D toPoint) {
            for (Arrow arrow: arrowSet) {
                if (arrow.getFromPoint() 
                		== fromPoint.fromNCS(locationContext) &&
                    arrow.getToPoint() 
                    	== toPoint.fromNCS(locationContext)) {
                        arrowSet.remove(arrow);
                        break;
                    } 
            }
            if (g != null)
                repaint(g);
        }
    }
}
