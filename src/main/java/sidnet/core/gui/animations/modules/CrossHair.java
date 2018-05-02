package sidnet.core.gui.animations.modules;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import sidnet.core.gui.animations.AnimationModule;

public class CrossHair extends AnimationModule{	
	
	private static final BufferedImage image 
		= new BufferedImage(20, 20, BufferedImage.TRANSLUCENT); 
	
	static {
		Graphics2D g2 = image.createGraphics();
		g2.setColor(Color.BLACK);
		g2.draw(new Ellipse2D.Double(3, 3, 12, 12));
		g2.drawLine(0, 8, 15, 8);
		g2.drawLine(8, 0, 8, 15);
		g2.dispose();
	}
		
	public void run() {		
		hostPanel.setVisible(true);
		Graphics2D g2d = (Graphics2D)hostPanel.getGraphics();
		if (g2d == null)
			System.out.println("g2d is null");
		g2d.drawImage(image, (int)(loc.getX() - 5),
							 (int)( loc.getY() - 5),
					  hostPanel);	
	}	
}
