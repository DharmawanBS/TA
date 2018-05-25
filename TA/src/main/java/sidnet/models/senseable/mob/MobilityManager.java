package sidnet.models.senseable.mob;

import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class MobilityManager {
	static Logger logger;	
	static {
		 logger = Logger.getLogger("GaussMarkovMobilityModelLogger");
		 logger.setLevel((Level) Level.DEBUG);		 
	}
	
	protected List<MobilityModel> mobs = new LinkedList<MobilityModel>();

	public List<MobilityModel> getMobilityModelsList() {
		return mobs;
	}

	public void activateMobilityModel(MobilityModel mob) {
		logger.debug("Mobility Model Activated!");
		mobs.add(mob);		
	}
	
	public void deactivateMobilityModel(MobilityModel mob) {
		logger.debug("Mobility Model De-activated!");
		mobs.remove(mob);		
	}
	
	public int getMobCount() {
		return mobs.size();
	}
}
