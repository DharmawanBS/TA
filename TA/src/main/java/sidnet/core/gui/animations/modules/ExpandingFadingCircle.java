package sidnet.core.gui.animations.modules;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import sidnet.core.gui.animations.AnimationModule;

public class ExpandingFadingCircle
extends AnimationModule {
	
	private static final int STEPS = 5;
	private static final int DIST_FACTOR = 3;
	
	public void run() {		
		COUNT_INSTANCES--;
		double lastX = 0, lastY = 0;		
		
		Graphics2D g2d = (Graphics2D)hostPanel.getGraphics();
		BufferedImage image = new BufferedImage(40, 40, BufferedImage.TRANSLUCENT);
		
		for (int i = 0; i < STEPS; i++) {

			Graphics2D g2 = image.createGraphics();
			if (i > 0) {
				g2.setColor(Color.LIGHT_GRAY);
				g2.draw(new Ellipse2D.Double(lastX,
											  lastY,
											  15 + (i-1)*DIST_FACTOR,
											  15 + (i-1)*DIST_FACTOR));
			}
			if (i < STEPS-1)
			g2.setColor(Color.GREEN);
			g2.draw(new Ellipse2D.Double( 5-i*DIST_FACTOR/2,
										  5-i*DIST_FACTOR/2,
										  15 + i*DIST_FACTOR,
										  15 + i*DIST_FACTOR));
			lastX = 5 - i*DIST_FACTOR/2;
			lastY = 5 - i*DIST_FACTOR/2;
			
			g2.dispose();
			
			g2d.drawImage(image, (int)(loc.getX() - 5 - DIST_FACTOR), (int)(loc.getY()- 5 - DIST_FACTOR), hostPanel);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {;}			
		}
		COUNT_INSTANCES++;
	}
	
}
