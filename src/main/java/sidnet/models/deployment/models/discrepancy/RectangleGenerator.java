package sidnet.models.deployment.models.discrepancy;

import java.util.Random;

import org.w3c.dom.css.Rect;

import jist.swans.misc.Location;

public class RectangleGenerator implements ShapeGenerator{
	private int width;
	private int length;
	private int levels;
	private int currentLevel = 0;
	private int count = 0;
	private int MAX_RECT = 100;
	private Random rand;
	
	public RectangleGenerator(int width, int length, int seed) {
		this.width = width;
		this.length = length;
		rand = new Random(seed);
	}
	
	public void reset() {
		currentLevel = 0;
		count = 0;
	}
	
	public boolean hasNext() {
		return count < MAX_RECT;
	}
	/*public boolean hasNext() {
		if (currentLevel < levels)
		 	return true;
		
		if (currentLevel == levels && count < numRectsInLevel(currentLevel))
			return true;
		
		return false;
	}*/
	
	public Shape getNext() {
		Rectangle rect = new Rectangle();
		rect.a = rect.b = 0;
		int numCols = (int)Math.sqrt(MAX_RECT);
		int numRows = numCols;
		
		rect.c = (int)(count % numCols + 0.5) * width / numCols;
		rect.d = (int)(count % numRows + 0.5) * length / numRows;
		
		count++;
		
		return rect;
	}	
	
/*	public Rectangle getNext() {
		Rectangle rect = new Rectangle();
		rect.c = rand.nextInt(width);
		if (rect.c < 100)
			rect.c = 100;
		rect.a = rand.nextInt(rect.c);
		if (rect.c - rect.a < 100)
			rect.a = rect.c - 100;
		rect.d = rand.nextInt(length);
		if (rect.d < 100)
			rect.d = 100;
		rect.b = rand.nextInt(rect.d);
		if (rect.d - rect.b < 100)
			rect.b = rect.d - 100;
		count++;
		return rect;
	}*/
	
	/*public Rectangle getNext() {		
		if (count >= numRectsInLevel(currentLevel)) {
			count = 0;
			currentLevel++;
		}
		
		int numCols = numRectsInLevel(currentLevel) == 1 ? 1 : numRectsInLevel(currentLevel) / 2;
		int numRows = numCols;		
		
		Rectangle rect = new Rectangle();
		rect.a = (count % numCols) * width / numCols;
		rect.b = (count % numRows) * length / numRows;
		rect.c = rect.a + width / numCols;
		rect.d = rect.b + length / numRows;
		
		count++;
		
		return rect;
	}*/
	
	public int numRectsInLevel(int levelNum) {
		return (int)Math.pow(2, 2 * levelNum);
	}
}
