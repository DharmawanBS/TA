package sidnet.core.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import sidnet.core.gui.staticdrawing.BufferedImageProvider;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;
import sidnet.core.misc.NodeEntry;
import sidnet.core.misc.NodesList;

public class NeighborsPanel implements BufferedImageProvider {
    
    private BufferedImage image; 
    
    private LinkedList<NodeGUIimpl> neighborsOfNeighborsList 
    	= new LinkedList<NodeGUIimpl>();
    
    private LocationContext screenLocationContext;   
    
    private boolean needsUpdate = false;
    
    // for testing
    private boolean updated;
    public long updateCount = 0;
    
    public NeighborsPanel(PanelContext screenPanelContext) {
    	this.screenLocationContext = screenPanelContext.getLocationContext();
    }
    
    public void add(NodeGUIimpl nodeGui) {
    	if (neighborsOfNeighborsList.contains(nodeGui))
    		neighborsOfNeighborsList.remove(nodeGui);
    	
        neighborsOfNeighborsList.add(nodeGui);        
        needsUpdate = true;
    }
    
    public void remove(NodeGUIimpl nodeGui) {
        neighborsOfNeighborsList.remove(nodeGui);        
        needsUpdate = true;
    }
    
    public synchronized void updateImage() {   
    	
    	updateCount++;
    	needsUpdate = false;
    	
    	updated = true; // for testing purposes only
    	
        image = new BufferedImage(screenLocationContext.getWidth(), 
        					      screenLocationContext.getHeight(),
        						  BufferedImage.TRANSLUCENT);
        
        Graphics2D g2 = image.createGraphics();

        if (neighborsOfNeighborsList != null &&
        	neighborsOfNeighborsList.size() > 0) {
           for (NodeGUIimpl nodeGui: neighborsOfNeighborsList) {
              NodesList neighborsList = nodeGui.node.neighboursList;

              if (neighborsList != null && neighborsList.size() > 0) {
                  for (NodeEntry nodeEntry:neighborsList.getAsLinkedList()){
                 
                      Location2D neighLoc 
                      	= new Location2D(
                      		nodeEntry.getNCS_Location2D()
                      		  	     .fromNCS(nodeGui.getLocationContext())
                      				 .getX(),
                            nodeEntry.getNCS_Location2D()
                              		 .fromNCS(nodeGui.getLocationContext())
                              		 .getY());
                      
                      Location2D myLoc 
                      	= new Location2D(nodeGui.getPanelLocation2D()
                      							.getX(), 
                                         nodeGui.getPanelLocation2D()
                                         		.getY());

                      // do some adjustments
                      // quadran I 
                      if (neighLoc.getX() >= myLoc.getX() &&
                    	  neighLoc.getY() <= myLoc.getY())
                          
                    	  neighLoc.setY(neighLoc.getY() + 10.0);
                      
                      // quadran II
                      else if (neighLoc.getX() <= myLoc.getX() &&
                    		   neighLoc.getY() <= myLoc.getY()) {
                    	  
                          neighLoc.setX(neighLoc.getX() + 10.0);
                          neighLoc.setY(neighLoc.getY() + 10);
                          
                      }// quadran III
                      else if (neighLoc.getX() <= myLoc.getX() &&
                    		   neighLoc.getY() >= myLoc.getY())
                    	  
                          neighLoc.setX(neighLoc.getX() + 10);
                      // quadran IV
                      else if (neighLoc.getX() >= myLoc.getX() &&
                    		   neighLoc.getY() >= myLoc.getY()) {
                          // no adjustment
                      }

                      myLoc.setX(myLoc.getX() + 5);
                      myLoc.setY(myLoc.getY() + 5);

                      ArrowDrawingTool.drawArrow(
                    		  g2, 
                              myLoc,
                              neighLoc,
                              Color.gray,
                              ArrowDrawingTool.SIDE_LEAD);
                  }
               }
          }
        }
               
        g2.dispose();
    }  
  
    // Testing
	public BufferedImage getImage() {	
		if (needsUpdate)
			updateImage();
		return image;
	}   
	
	public boolean wasUpdated() {
    	boolean was = updated;
    	updated = false;
    	return was;
    }
	    
    public int size() {
    	return neighborsOfNeighborsList.size();
    }
}
