package sidnet.models.deployment.models.discrepancy;

import java.util.List;

import jist.swans.misc.Location;

public class Rectangle implements Shape{
	public int a, b, c, d;
	
	public boolean isInside(Location loc) {
		return loc.getX() >= a && loc.getX() <= c &&
			   loc.getY() >= b && loc.getY() <= d ;
	}

	public int countPointsInside(List<Location> locations) {
		int n = 0;
		
		for (Location loc: locations) {
			if (isInside(loc))
				n++;
		}
		return n;
	}
	
	public double area() {
		return Math.abs((c - a) * (d - b));
	}
}
