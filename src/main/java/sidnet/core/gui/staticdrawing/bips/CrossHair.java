package sidnet.core.gui.staticdrawing.bips;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import sidnet.core.gui.staticdrawing.BufferedImageProvider;

public class CrossHair 
implements BufferedImageProvider {
	
	private static final Map<Color, BufferedImage> imageMap 
	    = new HashMap<Color, BufferedImage>();
	
	private Color color;
	
	public CrossHair() {
		this(Color.BLACK);
	}
	
	public CrossHair(Color color) {
		this.color = color;
		if (!imageMap.containsKey(color))
			imageMap.put(color, createImage(color));
	}

	public BufferedImage getImage() {
		return imageMap.get(color);
	}
	
	private BufferedImage createImage(Color color) {
		BufferedImage image = new BufferedImage(20, 20, BufferedImage.TRANSLUCENT);
		Graphics2D g2 = image.createGraphics();
		g2.setColor(color);
		g2.draw(new Ellipse2D.Double(3, 3, 12, 12));
		g2.drawLine(0, 8, 15, 8);
		g2.drawLine(8, 0, 8, 15);
		g2.dispose();
		return image;
	}
}
