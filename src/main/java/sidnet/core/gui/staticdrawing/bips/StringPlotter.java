/**
 * @author Oliviu C. Ghica, Northwestern University
 * @version 1.0.1
 */

package sidnet.core.gui.staticdrawing.bips;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import sidnet.core.gui.staticdrawing.BufferedImageProvider;

public class StringPlotter 
implements BufferedImageProvider {
	
	private final BufferedImage image; 		

	public StringPlotter(String str, Color color) {
		String[] stringTokens = str.split("\n");
		image = new BufferedImage(12 + maxRowLength(stringTokens) * 12,
								  10 + stringTokens.length * 10,
								  BufferedImage.TRANSLUCENT); 
		Graphics2D g2 = image.createGraphics();
		g2.setColor(color);
		for (int i = 0; i < stringTokens.length; i++)
			g2.drawString(stringTokens[i], 12, 12 + 12 * i);
		g2.dispose();
	}

	public BufferedImage getImage() {
		return image;
	}
	
	private int maxRowLength(String[] strArray) {
		int maxRowLength = 0;
		for (int i = 0; i < strArray.length; i++)
			if (strArray[i].length() > maxRowLength)
				maxRowLength = strArray[i].length();
		
		return maxRowLength;
	}
}
