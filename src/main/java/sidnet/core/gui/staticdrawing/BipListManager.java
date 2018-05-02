package sidnet.core.gui.staticdrawing;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;

import jist.runtime.JistAPI;

public class BipListManager {
	private static final int MAX_ELEMENTS = 100;
	
	private Map<String, BipEntry> permanentBipMap;
	private Map<String, BipEntry> temporarBipMap;
	
	
	public BipListManager() {
		permanentBipMap = new HashMap<String, BipEntry>();
		temporarBipMap = new HashMap<String, BipEntry>();		
	}
		
	public synchronized void add(String bipTag, 
					BufferedImageProvider bip,
					Location2D location,
					LocationContext locationContext,
					long duration) {
		purgeOld();
		
		if (temporarBipMap.size() < MAX_ELEMENTS)
			temporarBipMap.put(bipTag,
							   new BipEntry(bip,
									        location, locationContext,
									        duration));	
	}
	
	public synchronized void add(String bipTag, 
			BufferedImageProvider bip,
			Location2D location,
			LocationContext locationContext) {
		
		purgeOld();
		
		if (permanentBipMap.size() < MAX_ELEMENTS)
			permanentBipMap.put(bipTag,
							   new BipEntry(bip,
									        location, locationContext));	
	}
	
	public synchronized void remove(String bipTag) {
		permanentBipMap.remove(bipTag);
		temporarBipMap.remove(bipTag);
	}
	
	public synchronized List<BipEntry> getList() {
		List<BipEntry> cummulatedList = new LinkedList<BipEntry>();
		Collection<BipEntry> permo = permanentBipMap.values();
		Collection<BipEntry> tempo = temporarBipMap.values();
		
		for (BipEntry bip: permo)
			cummulatedList.add(bip);
		for (BipEntry bip: tempo)
			cummulatedList.add(bip);
		
		return cummulatedList;
	}
	
	private synchronized void purgeOld() {
		
		Set<String> tempo = temporarBipMap.keySet();
		List<String> removableTags = new LinkedList<String>();
	
		for(String tag: tempo)
			if (temporarBipMap.get(tag).expirationTimestamp != BipEntry.NONE &&
			    temporarBipMap.get(tag).expirationTimestamp < JistAPI.getTime())
			
				removableTags.add(tag);
		
		for(String tag: removableTags)
			temporarBipMap.remove(tag);
	}
}
