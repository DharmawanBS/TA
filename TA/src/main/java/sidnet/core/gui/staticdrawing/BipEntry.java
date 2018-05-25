package sidnet.core.gui.staticdrawing;

import jist.runtime.JistAPI;
import sidnet.core.gui.staticdrawing.bips.CrossHair;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;

public class BipEntry {
	
	public static final long NONE = -1;
	
	public BufferedImageProvider bip;
	public Location2D location;
	public LocationContext locationContext;
	public long expirationTimestamp;
	
	public BipEntry(BufferedImageProvider bip,
					Location2D location,
					LocationContext locationContext,
					long duration) {		
		this.bip = bip;
		this.location = location;
		this.locationContext = locationContext;
		this.expirationTimestamp = JistAPI.getTime() + duration;
	}
	
	public BipEntry(BufferedImageProvider bip,
					Location2D location,
					LocationContext locationContext) {
		
		this.bip = bip;
		this.location = location;
		this.locationContext = locationContext;
		this.expirationTimestamp = NONE;
	}
}
