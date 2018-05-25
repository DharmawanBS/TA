package sidnet.models.senseable.mob;

import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;

public interface MobilityModel {
	public Location2D nextLocation(long currentTimestamp, LocationContext actualTargetFieldLocationContext);
}
