/*
 * GroupSelectionTool.java
 *
 * Created on November 8, 2007, 3:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 * 
 *  @author Oliviu C. Ghica, Northwestern University
 *  @version 1.0.1 - Enabled Snapping to sensor network GUI edges 
 */

package sidnet.core.gui;

import sidnet.core.interfaces.SIDnetDrawableInterface;
import sidnet.core.interfaces.SIDnetRegistrable;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

/**
 *
 *
 */
public class GroupSelectionTool 
extends JPanel 
implements SIDnetDrawableInterface, MouseListener, MouseInputListener, SIDnetRegistrable{
    // DEBUG
    private static final boolean DEBUG = false;
    
    private int xStartRect, yStartRect, xEndRect, yEndRect;
    protected sidnet.core.misc.Node[] nodeList;
    protected LinkedList<sidnet.core.misc.Node> individualGroupNodeList;
    private boolean visibleRect = false;
    
    /** Creates a new instance of GroupSelectionTool */
    public GroupSelectionTool(sidnet.core.misc.Node[] nodeList) {
        xStartRect = -1;
        yStartRect = -1;
        xEndRect = -1;
        yEndRect = -1;
        individualGroupNodeList = new LinkedList<sidnet.core.misc.Node>();
        this.nodeList = nodeList;
    }
    
    public void configureGUI(JPanel hostPanel)
    {
        if (hostPanel == null)
             return;
         
        this.setOpaque(false);
        this.setBackground(Color.black);
        hostPanel.add(this);

        this.setSize(hostPanel.getSize());
        
        this.setVisible(false);
        
        hostPanel.addMouseListener(this);
        hostPanel.addMouseMotionListener(this);   
    }
    
    public boolean groupExists()
    {
        return individualGroupNodeList.size() >= 2;
    }

    public void repaintGUI()
    {
        // DO NOTHING
    }
    
    public void setVisibleGUI(boolean visibility)
    {
        // DO NOTHING
    }
    
    public void terminate()
    {
        // DO NOTHING
    }
    
    
    public void mouseMoved(MouseEvent ev)
    {
        // NOTHING
    }
    
    public void mouseClicked(MouseEvent ev)
    {
        if (ev.getButton() != MouseEvent.BUTTON1)
            return;
        
        this.setVisible(true);
        
        xStartRect = -1;
        yStartRect = -1;
        xEndRect = -1;
        yEndRect = -1;
        
        for(int i = 0; i < nodeList.length; i++)
            nodeList[i].getNodeGUI().markSelected(false);
        
        if(individualGroupNodeList.size() > 0)
            individualGroupNodeList.clear();
    }
    
    public void mouseEntered(MouseEvent ev){};
    public void mouseExited(MouseEvent ev){};    
    public void mousePressed(MouseEvent ev){
        mouseClicked(ev);
    }
    public void mouseReleased(MouseEvent ev)
    {
        int tmp;

        if (xStartRect == -1)
            return;

        if (DEBUG) System.out.println("[DEBUG][GroupSelectionTool.mouseReleased()]");
        
        if (xStartRect > xEndRect)
        {
            tmp = xStartRect;
            xStartRect = xEndRect;
            xEndRect = tmp;
        }
        
        if (yStartRect > yEndRect)
        {
            tmp = yStartRect;
            yStartRect = yEndRect;
            yEndRect = tmp;
        }

        
        // Mark all affected nodes
        for(int i = 0; i < nodeList.length; i++)
        {
            if (nodeList[i].getNodeGUI().getPanelLocation2D().getX() >= xStartRect && 
                nodeList[i].getNodeGUI().getPanelLocation2D().getX() <= xEndRect   &&
                nodeList[i].getNodeGUI().getPanelLocation2D().getY() >= yStartRect &&
                nodeList[i].getNodeGUI().getPanelLocation2D().getY() <= yEndRect)
                {
                    notifySelected(nodeList[i], true);
                    nodeList[i].getNodeGUI().markSelected(true);
                }
        }
        
        visibleRect = false;
        //this.repaint();
        this.setVisible(false);
    };
    
    public boolean isGroupMember(sidnet.core.misc.Node thisNode)
    {
        return individualGroupNodeList.contains(thisNode);
    }
    
    public void notifyActionPerformed(sidnet.core.misc.Node fromNode, ActionEvent e)
    {
         // Signal all affected nodes

        if (individualGroupNodeList.size() >= 2)
        {
            for (sidnet.core.misc.Node currentNode: individualGroupNodeList)
                currentNode.getNodeGUI().handleAction(e);
            return;
        }
    }
    
    public void notifySelected(sidnet.core.misc.Node node, boolean selected)
    {
        node.getNodeGUI().markSelected(selected);
        if (selected && !individualGroupNodeList.contains(node))
            individualGroupNodeList.add(node);
        if (!selected)
            individualGroupNodeList.remove(node);
    }
    
    
    public void mouseDragged(MouseEvent ev)
    { 
        
        if (DEBUG) System.out.println("[DEBUG][GroupSelectionTool.mouseDragged()");
        if (xStartRect == -1)
            xStartRect = ev.getX();
        
        xEndRect = ev.getX();
        
        if (yStartRect == -1)
            yStartRect = ev.getY();
        
        yEndRect = ev.getY();
        
        if (xStartRect < 20)
            xStartRect = 0;
        if (yStartRect < 20)
            yStartRect = 0;
        if (xEndRect < 20)
            xEndRect = 0;
        if (yEndRect < 20)
            yEndRect = 0;
        
        if (xStartRect > this.getWidth()-20)
            xStartRect = this.getWidth();
        if (yStartRect > this.getHeight()-20)
            yStartRect = this.getHeight();
        if (xEndRect > this.getWidth()-20)
            xEndRect = this.getWidth();
        if (yEndRect > this.getHeight()-20)
            yEndRect = this.getHeight();
        
        visibleRect = true;
        
        //super.repaint();
        repaint();
    }
     
      public void paintComponent(Graphics g) {
        if (DEBUG) System.out.println("[DEBUG][GroupSelectionTool.paintComponent(g)]");
        
        if (xStartRect == -1 || !visibleRect)
            return;
  
        Graphics2D g2d = (Graphics2D)g; 
        
        g2d.setColor(Color.yellow);
        g2d.setStroke(new BasicStroke(
			          2, 
			          BasicStroke.CAP_BUTT,
			          BasicStroke.JOIN_MITER,
			          50,
			          new float[] {9}, 
			          0));
        
        
        System.out.println("xEndRect" + xEndRect);
        
        g2d.drawRect(xStartRect > xEndRect ? xEndRect : xStartRect, yStartRect > yEndRect ? yEndRect : yStartRect,
                     Math.abs(xStartRect - xEndRect), Math.abs(yStartRect - yEndRect));
    }
      
    protected void clear(Graphics g) {
       //super.paintComponent(g);
    }    
}
