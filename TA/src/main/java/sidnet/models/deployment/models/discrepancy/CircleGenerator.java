package sidnet.models.deployment.models.discrepancy;

import java.util.Random;

import jist.swans.misc.Location;

public class CircleGenerator implements ShapeGenerator {
	private int width;
	private int length;
	private int side;
	private int count = 0;
	private Location center;
	private int MAX_NUM = 10;
	private Random rand;
	
	public CircleGenerator(int width, int length, long seed) {
		this.width = width;
		this.length = length;
		side = Math.min(width, length);
		center = new Location.Location2D(width/2, length/2);
		rand = new Random(seed);
	}
	
	public Shape getNext() {
		count++;
		return new Circle(center, (int) (count * side / MAX_NUM / 2));
		//return new Circle(center, rand.nextInt(side / MAX_NUM / 2));
	}

	public boolean hasNext() {
		return count < MAX_NUM;
	}

	public void reset() {
		count = 0;		
	}
}
