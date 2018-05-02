package sidnet.models.deployment.models.discrepancy;

import java.util.List;

import jist.swans.misc.Location;

public class Circle implements Shape{
	private Location center;
	private int radix;
	
	public Circle(Location center, int radix) {
		this.center = center;
		this.radix = radix;
	}
	
	public double area() {		
		return Math.PI * radix * radix;
	}

	public int countPointsInside(List<Location> locations) {
		int n = 0;
		
		for (Location loc: locations) {
			if (isInside(loc))
				n++;
		}
		return n;
	}

	public boolean isInside(Location loc) {
		return loc.distance(center) <= radix;
	}
}
