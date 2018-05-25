package sidnet.models.senseable.mob.mobilitymodels;

import java.io.IOException;

import jist.swans.Constants;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;
import sidnet.core.misc.NCS_Location2D;
import sidnet.models.probabilistic.Gaussian;
import sidnet.models.senseable.mob.MobilityModel;

public class GaussMarkovMobilityModel
implements MobilityModel{
	static Logger logger;	
	static {
		 logger = Logger.getLogger("GaussMarkovMobilityModelLogger");
		 logger.setLevel((Level) Level.INFO);		 
	}
	
	private Location2D nextLocation;	
	private Location2D lastLocation;
	private Location2D currentLocation;
	private double s_mps, d_rps;
	private double alpha;
	private long n_interval_MS; // time interval
	private Gaussian sG, dG;
	private double edgeProximityLimitFactor;
	private LocationContext locationContext;
	private double LB, RB, UB, BB; // left bound, right bound, up bound, bottom bound
	
	private boolean strictBounds;
	
	private long lastTimestamp_ms, nextTimestamp_ms;
	private long n;
	private double sn, dn, sn_1, dn_1;
	
	private double sum_dn;
	private double sum_sx;
	private double sum_dx;
	
	/**
	 * 
	 * @param expectedMeanSpeed_MPS				[meters per second]
	 * @param expectedMeanAngularDeviation_RPS  [radians per second]
	 * @param reevaluationTimeInterval        
	 * @param alpha
	 * @param regionBoundX_m
	 * @param regionBoundY_m
	 * @param edgeProximityLimitPercentage
	 * @param speedDistribution
	 * @param directionDistribution
	 * @param strictBounds - if true, the object will never leave the bounds. Otherwise, the object
	 *                       may occasionally leave the bounds (WARNING - if movement is very random
	 *                       the object might have difficulty returning into the bounds)
	 */
	public GaussMarkovMobilityModel(double expectedMeanSpeed_MPS,
									double expectedMeanAngularDeviation_RPS,
									long reevaluationTimeInterval,
									double alpha,
									Location2D initialLocation,
									final LocationContext locationContext,
									int edgeProximityLimitPercentage,
									Gaussian speedDistribution,
									Gaussian directionDistribution,
									boolean strictBounds) {
		
		this.d_rps = expectedMeanAngularDeviation_RPS;
		this.n_interval_MS = reevaluationTimeInterval / Constants.MILLI_SECOND;
		this.s_mps = (expectedMeanSpeed_MPS * n_interval_MS) / 1000;
		logger.info("n_interval = " + n_interval_MS + " [ms]");
		
		this.alpha = alpha;
		this.edgeProximityLimitFactor = ((double)edgeProximityLimitPercentage)/100;
		//this.edgeProximityLimitFactor = ((double)(s_mps * n_intervalS)) / Math.min(locationContext.getHeight(), locationContext.getWidth());
		//logger.info("EdgeProximityFactor = " + this.edgeProximityLimitFactor);
		this.locationContext = locationContext;
		LB = ((double)locationContext.getWidth()) * edgeProximityLimitFactor;
		RB = ((double)locationContext.getWidth()) - LB;
		UB = ((double)locationContext.getHeight()) * edgeProximityLimitFactor;
		BB = ((double)locationContext.getHeight()) - UB;
		
		logger.info("LB = " + LB + " RB = " + RB + " UB = " + UB + " BB = " + BB);

		//nextLocation = new Location2D(locationContext.getWidth()/2, locationContext.getHeight()/2);
		nextLocation = initialLocation;
		
		sn_1 = s_mps;
		dn_1 = d_rps;
		
		this.sG = speedDistribution;
		this.dG = directionDistribution;	
		this.strictBounds = strictBounds;
		
		try {
			logger.addAppender(new FileAppender(new SimpleLayout(), "GaussMarkovTrace.log", false));
		 } catch (IOException e) {
		 	e.printStackTrace();
		 }
		 logger.debug("GaussMarkovTrace Invoked");
		
		// initialize
		executeGMMMStep(0);
	}
	
	public Location2D nextLocation(long curTimestamp, LocationContext locationContext) {		
					
		long currentTimestamp_ms = curTimestamp / Constants.MILLI_SECOND;
		
		sanityCheck(currentTimestamp_ms);
	
		long n_increment = 0;
		
		if (currentTimestamp_ms >= nextTimestamp_ms)
			n_increment = (long)((currentTimestamp_ms - lastTimestamp_ms) / n_interval_MS);
		
		// Fast forward
		while (n_increment > 0) {
			executeGMMMStep(currentTimestamp_ms);
			n_increment--;
		}		
		
		currentLocation
			= liniarInterpolation(lastLocation, nextLocation,
								  lastTimestamp_ms, nextTimestamp_ms, currentTimestamp_ms);

		if (currentLocation.getX() < LB || currentLocation.getX() > RB ||
			currentLocation.getY() < UB || currentLocation.getY() > BB) {
					
			executeGMMMStep(currentTimestamp_ms); // this will detect the network bound conditions and switch direction
					
			currentLocation 
			= liniarInterpolation(lastLocation, nextLocation,
								  lastTimestamp_ms, nextTimestamp_ms, currentTimestamp_ms);
		}		
		
		return currentLocation;
	}

	private void executeGMMMStep(long currentTimestamp_ms) {
		n++;	
		double sx_n_1 = sG.nextValue();
		sum_sx += sx_n_1; 
		double dx_n_1 = dG.nextValue();
		sum_dx += dx_n_1;
		sn = alpha * sn_1 + (1-alpha)*s_mps + Math.sqrt(1-alpha * alpha)*sx_n_1;
		dn = alpha * dn_1 + (1-alpha)*d_rps + Math.sqrt(1-alpha * alpha)*dx_n_1;
		if (currentLocation == null)
			currentLocation = new Location2D(nextLocation.getX(), nextLocation.getY());
		lastLocation = new Location2D(nextLocation.getX(), nextLocation.getY());
		// xn = xn_1 + sn_1 cos dn_1
		nextLocation.setX( lastLocation.getX() + sn_1 * Math.cos(dn_1));
		// yn = yn_1 + sn_1 sin dn_1
		nextLocation.setY( lastLocation.getY() + sn_1 * Math.sin(dn_1));
		
		logger.debug("n = " + n + " sn = " + sn + " dn = " + dn);
		logger.debug(nextTimestamp_ms + "\t" + nextLocation.getX() + "\t" + nextLocation.getY());	
		
		// Make sure the object does not get outside the network's bounds
		boundaryConditioning();
		
		sum_dn = sum_dn + dn;
		
		sn_1 = sn;
		dn_1 = dn;	
		
		//lastTimestamp = nextTimestamp;
		lastTimestamp_ms = currentTimestamp_ms;
		//nextTimestamp = nextTimestamp + n_intervalS;			
		nextTimestamp_ms = currentTimestamp_ms + n_interval_MS;
	}
	
	private void boundaryConditioning(){
		double old_d = d_rps;
		double newValue = -999;
		if (nextLocation.getX() < LB) {
			if (nextLocation.getY() < UB)
				newValue = toRadians(45);
			else if (nextLocation.getY() > BB)
				newValue = toRadians(-45);
			else
				newValue = toRadians(0);
		} else
			if (nextLocation.getX() > RB) {
				if (nextLocation.getY() < UB)
					newValue = toRadians(135);
				else if (nextLocation.getY() > BB)
					newValue = toRadians(-135);
				else
					newValue = toRadians(180);
			}
			else if (nextLocation.getY() < UB)
				newValue = toRadians(90);
			else if (nextLocation.getY() > BB)
				newValue = toRadians(-90);
		
		
		if (newValue != -999) {			
			d_rps = newValue;
			logger.debug("\tDirection change from " + old_d + " to " + d_rps + " due to boundary being hit (X = " + nextLocation.getX() + ", Y = " + nextLocation.getY() + ")");
			if (strictBounds) {
				dn_1 = d_rps;
				dn = d_rps;
			}
		}
	}
	
	private static double toRadians(double degree) {
		return Math.PI * degree / 180; 
	}
	
	private Location2D liniarInterpolation(Location2D lastLoc,
										   Location2D nextLoc,
										   long lastTimestamp, 
										   long nextTimestamp,
										   long currentTimestamp) {
		
		sanityCheck(lastTimestamp, nextTimestamp);
		sanityCheck(currentTimestamp);
		
		double factor = ((double)(currentTimestamp - lastTimestamp)) / (nextTimestamp - lastTimestamp);
		double x = lastLoc.getX() * (1 - factor) + nextLoc.getX() * factor;
		double y = lastLoc.getY() * (1 - factor) + nextLoc.getY() * factor;
		Location2D currentLoc = new Location2D(x, y);
		
		//if (nextLocation.getX() > -512 && nextLocation.getX() < -510)
		
		return currentLoc;
	}
	
	public double getExhibitedMeanDirection(){
		return sum_dn / n;
	}
		
	private void sanityCheck(long currentTimestamp_ms) {
		if (currentTimestamp_ms < lastTimestamp_ms)
			throw new RuntimeException("[GaussMarkovMobilityModel][ERROR]Out of order timings");		
	}
	
	private void sanityCheck(long lastTimestamp_ms, long nextTimestamp_ms) {
		if (nextTimestamp_ms - lastTimestamp_ms != n_interval_MS)
			throw new RuntimeException("[GaussMarkovMobilityModel][ERROR] Incorrect correlation on the 'last' and 'next' timestamps");	
	}
}
