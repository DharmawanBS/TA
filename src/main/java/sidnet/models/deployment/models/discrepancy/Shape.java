package sidnet.models.deployment.models.discrepancy;

import java.util.List;

import jist.swans.misc.Location;

public interface Shape {

	boolean isInside(Location loc);
	
	int countPointsInside(List<Location> locations);
	
	double area();
}
