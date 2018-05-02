/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.core.misc;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import jist.runtime.JistAPI;
import sidnet.core.interfaces.SIDnetDrawableInterface;
import sidnet.core.interfaces.SIDnetMenuInterface;
import sidnet.core.interfaces.SIDnetRegistrable;

/**
 *
 * @author Oliver
 */
public class NodeFinder 
extends JPanel 
implements SIDnetDrawableInterface, SIDnetRegistrable, SIDnetMenuInterface, JistAPI.DoNotRewrite{
  
    private Node[] nodes;
    
     // Menu
    private JPopupMenu hostPopupMenu;
    private JMenuItem menuItemNodeFinder;
    
    public NodeFinder(Node[] nodes) {
        this.nodes = nodes;
    }
    
    public void configureGUI(JPanel hostPanel) {
        if (hostPanel == null)
             return;
         
        this.setOpaque(true);
        hostPanel.add(this);

        //this.setBounds(hostPanel.getBounds());
        //this.setSize(hostPanel.getSize());
        this.setBounds(0,0, 0, 0);
	    this.setSize(0,0);

        /* We choose to enable this by default */
        this.setVisible(true);
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
    
    public void enableUI()
    {
       // DO NOTHING
    }
    
    public void disableUI()
    {
        // DO NOTHING
    }
    
    public void passMenuActionEvent(ActionEvent menuEvent)
    {
        // TODO
    }
    
    public void configureMenu(JPopupMenu hostPopupMenu)
    {
        this.hostPopupMenu = hostPopupMenu;
        menuItemNodeFinder = new JMenuItem("Node Finder ... ");
        menuItemNodeFinder.addActionListener(this);
        hostPopupMenu.add(menuItemNodeFinder);
    }
    
    public void actionPerformed(ActionEvent e) {
        
        if (e.getActionCommand() == "Node Finder ... ")
        {
            // Pop the user for node ID
            int nodeId = getNodeID();
            
            if (nodeId < 0 && nodeId > nodes.length - 1)
                return;
            
            nodes[nodeId].getNodeGUI().markSelected(true);
            nodes[nodeId].getNodeGUI().actionPerformed(new ActionEvent(this, nodeId, "Show/Hide Node ID"));
            
            System.out.println("Node Finder ... " + nodeId);                   
        }
     }
    
    private int getNodeID()
    {        
          NodeFinderDialog dialog = new NodeFinderDialog(new JFrame(), true);  
          dialog.setVisible(true);
          dialog.setAlwaysOnTop(true);   
          
          while(dialog.isVisible())
          {
              try{
                Thread.sleep(200);
              }
              catch (Exception e){;};
          }
          
          return dialog.nodeID;
    }
    
    public void paintComponent(Graphics g) {
    	if (false)
    		System.out.println("test");
    }
    
    protected void clear(Graphics g) {
       //super.paintComponent(g);
    }       
}




