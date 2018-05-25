package sidnet.core.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import sidnet.colorprofiles.ColorPair;

public class NodeImageManager {
	
    private static final Ellipse2D.Double innerCircle = new Ellipse2D.Double(0, 0, 10, 10);      // Create a visual symbol for a sensor node
    private static final Ellipse2D.Double outerCircle = new Ellipse2D.Double(0, 0, 11, 11);      // Create a visual symbol for a sensor node
	
	private static Map<Color, Map<Color, BufferedImage>> nodeImageDoubleMap;

	static {
		nodeImageDoubleMap = new HashMap<Color, Map<Color, BufferedImage>>();
	}
	
	public BufferedImage getImageFor(ColorPair colorPair) {
		
		Map<Color, BufferedImage> firstIndex = nodeImageDoubleMap.get(colorPair.innerColor);
		if (firstIndex == null) { 
			nodeImageDoubleMap.put(colorPair.innerColor, new HashMap<Color, BufferedImage>());
			firstIndex = nodeImageDoubleMap.get(colorPair.innerColor);
		}
		
		BufferedImage image = firstIndex.get(colorPair.outerColor);
		if (image == null) {
			image = new BufferedImage(12, 12, BufferedImage.TRANSLUCENT);
			Graphics2D g2 = image.createGraphics();
			if (colorPair.outerColor != null) {
                g2.setColor(colorPair.outerColor);
                g2.draw(outerCircle);
            }
            if (colorPair.innerColor != null) {
                g2.setColor(colorPair.innerColor);
                g2.fill(innerCircle);
            }
			g2.dispose();
			firstIndex.put(colorPair.outerColor, image);
		}
		
		return image;
	}
}
