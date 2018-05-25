package sidnet.core.gui.animations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;

import javax.swing.JPanel;

import sidnet.core.interfaces.SIDnetDrawableInterface;
import sidnet.core.misc.LocationContext;
import sidnet.core.misc.NCS_Location2D;
import jist.runtime.JistAPI;

public class AnimationDrawingLayer 
extends JPanel
implements SIDnetDrawableInterface, JistAPI.DoNotRewrite{
	
	private static JPanel hostPanel;
	private boolean configured = false;
	private static final String modulesPackageName = 
		"sidnet.core.gui.animations.modules.";
		
	 /** 
	  * Since this is a GUI, 
	  * it is important to know the dimension information of the area 
	  * in which we draw to be able to convert location information from NCS 
	  */
    private LocationContext screenLocationContext = null;
	
	public void animate(String animationModuleClassName, NCS_Location2D ncsLoc) {
		
		if (!configured)
			configure();
		
		Class animationModuleClass = null;
		
		try {
			animationModuleClass 
				= Class.forName( modulesPackageName + 
								 animationModuleClassName);
		} catch (ClassNotFoundException e1) {
			throw new RuntimeException(
					"Module: \"" + modulesPackageName + "" + animationModuleClassName +
					"\" does not exist or cannot be found." +
					" SIDnet is looking for these modules under " +
					modulesPackageName);
		}
		
		AnimationModule am = null;
		
		try {			
			am = (AnimationModule) animationModuleClass.newInstance();
		} catch (InstantiationException e) {e.printStackTrace();
		} catch (IllegalAccessException e) {e.printStackTrace();}
		
		if (am != null) {		
			if(!am.maxedOut()) {				
				am.animateOn(ncsLoc.fromNCS(screenLocationContext), this);
				new Thread(am).start();
			}
		}
		
	}

	public void configureGUI(JPanel hostPanel) {
		this.hostPanel = hostPanel;	
	}
	
	private void configure() {
		configured = true;
		
		this.setOpaque(false);
	    this.setBackground(Color.black);
	    hostPanel.add(this);
	    
	    this.setBounds(hostPanel.getBounds());
	    this.setSize(hostPanel.getSize());

	    /* We choose to enable this by default */
	    this.setVisible(true); 
	    
	    screenLocationContext = new LocationContext(hostPanel.getWidth(),
	    									  hostPanel.getHeight());
	}

	public void repaintGUI() {
		// not used		
	}

	public void setVisibleGUI(boolean visible) {
		this.setVisible(visible);		
	}
	
	 public synchronized void paintComponent(Graphics g) {
		 super.paintComponent(g);
	 }
}
