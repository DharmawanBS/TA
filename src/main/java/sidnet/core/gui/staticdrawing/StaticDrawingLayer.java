package sidnet.core.gui.staticdrawing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JPanel;

import jist.runtime.JistAPI;
import sidnet.core.interfaces.SIDnetDrawableInterface;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;
import sidnet.core.misc.NCS_Location2D;

public class StaticDrawingLayer 
extends JPanel
implements SIDnetDrawableInterface, JistAPI.DoNotRewrite {

	private static JPanel hostPanel;
	
	private static BipListManager bipListManager;
	
	private static Location2D origin;
	
	private static boolean globalVisible = true;
	
	static {
		bipListManager = new BipListManager();
		origin = new Location2D(0,0);
	}
	
	/** 
	  * Since this is a GUI, 
	  * it is important to know the dimension information of the area 
	  * in which we draw to be able to convert location information from NCS 
	  */
    private LocationContext screenLocationContext = null;

    private PolygonDrawingTool polygonDrawingTool;
        
    public void plot(List<NCS_Location2D> vertexList, boolean closePolygon,
    				 Color color, int lineWidth, int dashLengthPx,
    				 long duration, boolean showOnlyVerteces) {
    	polygonDrawingTool.plot(vertexList, closePolygon,
    							color, lineWidth, dashLengthPx, duration, showOnlyVerteces);
    	
    	repaint();
    }
    
    public void plot(BufferedImageProvider bip,
    		 	     Location2D location2D,
    				 LocationContext locationContext,
    				 long duration) {
    	plot(bip.getClass().getName(), bip,
    		 location2D, locationContext, duration);    	
    }
    
    public void plot(String bipTag,
    				 BufferedImageProvider bip,
    				 Location2D location2D,
    				 LocationContext locationContext,
    				 long duration) {
    	bipListManager.add(bip.getClass().getName(), bip,
    				       location2D, locationContext, duration);  
    	if (bipListManager.getList().size() > 0 && globalVisible)
    		this.setVisible(true);
    	else
    		this.setVisible(false);
    }
    
    public void plot(BufferedImageProvider bip,
	 	     		 Location2D location2D,
	 	     		 LocationContext locationContext) {

    	plot(bip.getClass().getName(), bip,
    		 location2D, locationContext);    
    }
    
    public void plot(String bipTag, 
    				 BufferedImageProvider bip,
    				 Location2D location2D,
    				 LocationContext locationContext) {

    	bipListManager.add(bipTag, bip,
    					   location2D, locationContext);   
    	
    	if (bipListManager.getList().size() > 0 && globalVisible)
    		this.setVisible(true);
    	else
    		this.setVisible(false);
    }    
    
    public void add(BufferedImageProvider bip) {
    	add(bip.getClass().getName(), bip);
    }
    
    public void add(String bipTag,
	        		BufferedImageProvider bip) {
    	bipListManager.add(bipTag, bip, null, null);
    	if (bipListManager.getList().size() > 0 && globalVisible)
    		this.setVisible(true);
    	else
    		this.setVisible(false);
    }    
    
    public void remove(BufferedImageProvider bip) {
    	remove(bip.getClass().getName());    	
    }
    
    public void remove(String bipTag) {
    	bipListManager.remove(bipTag);
    	if (bipListManager.getList().size() > 0 && globalVisible)
    		this.setVisible(true);
    	else
    		this.setVisible(false);
    }
    
	public void configureGUI(JPanel hostPanel) {
		
		this.hostPanel = hostPanel;
		
		screenLocationContext = new LocationContext(hostPanel.getWidth(),
				 									hostPanel.getHeight());
		
		// register module
		polygonDrawingTool = new PolygonDrawingTool(screenLocationContext);
		bipListManager.add(PolygonDrawingTool.class.getName(),
						   polygonDrawingTool, null, null);
		
		this.setOpaque(false);
	    this.setBackground(Color.black);
	    hostPanel.add(this);
	    	        
	    this.setSize(hostPanel.getSize());
	}
	
	public void repaintGUI() {
		repaint();
	}
	
	public void setVisibleGUI(boolean visible) {
		globalVisible = visible;
		this.setVisible(visible);		
	}
	
	public synchronized void paintComponent(Graphics g) {		
		
		Graphics2D g2d = (Graphics2D)g;
		
		List<BipEntry> bips = bipListManager.getList();
		
		Location2D screenLoc;		
		
		for (BipEntry bip: bips) {
			if (bip.location != null) { 
				screenLoc = bip.location.convertTo(bip.locationContext,
							 					   screenLocationContext);				
			}
			else
				screenLoc = origin;
						
			
			g2d.drawImage(bip.bip.getImage(), 
						 (int)screenLoc.getX(),
						 (int)screenLoc.getY(),
						 hostPanel);
		}			
    }
	
	 protected void clear(Graphics g) {
	        //super.paintComponent(g); 
	    }   
}