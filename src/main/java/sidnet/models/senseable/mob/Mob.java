/*
 * Mob.java
 *
 * Created on November 7, 2007, 3:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.models.senseable.mob;

import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlRootElement;

import jist.swans.Constants;

import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;

/**
 *
 * @author Oliver
 */

@XmlRootElement
public class Mob 
implements MobilityModel {
    
    public String mobProfileName;
    
    // dimensions
    public FieldDimensions fieldDimensions;
    
    public int radiance;
    
    public String timeUnit;
    
    public Path path;
    
    public boolean mirrored = false;
    public boolean relativeTiming;
    public double baseTimestamp, rootTimestamp;
    
    private Iterator<LocationInTime> literator = null;
    private LocationInTime lit;
    private double periodicity = 0;
    
    // Find the proper time-segment
    private LocationInTime lastLocationInTime = null;
    private LocationInTime nextLocationInTime = null;  
    
    /** Creates a new instance of Mob */
    public Mob() {
    	relativeTiming = true;
    	baseTimestamp = -1;
    	rootTimestamp = -1;
    }
    
    public static class FieldDimensions {    	    	
        public int length;
        public int width;
        
        // required by jaxb
        public FieldDimensions(){;}
        
        public FieldDimensions(int length, int width) {
        	this.length = length;
        	this.width = width;
        }
    }
    
    public static class LocationInTime {
        public long timeStamp;
        public double xCoord;
        public double yCoord;
        
        // required by jaxb
        public LocationInTime(){;}
        
        public LocationInTime(long timeStamp, double xCoord, double yCoord) {
        	this.timeStamp = timeStamp;
        	this.xCoord = xCoord;
        	this.yCoord = yCoord;
        }
    }
    
    public static class Path {
        public LinkedList<LocationInTime> locationInTime;
    }
    
    public void printComponents() {
        System.out.println("mobProfileName: " + mobProfileName);
        if (fieldDimensions != null) {
        	System.out.println("fieldDimensions:");
        	System.out.println("\tlength: " + fieldDimensions.length);
        	System.out.println("\twidth: " + fieldDimensions.width);
        } else
        	System.out.println("fieldDimensions: unspecified. Assume absolute positioning");
        System.out.println("radiance: " + radiance);
        System.out.println("timeUnits: " + timeUnit);
        /*System.out.println("path:");
        for(LocationInTime lit: path.locationInTime) {
            System.out.println("\tlocationInTime: ");
            System.out.println("\t\ttimeStamp: " + lit.timeStamp);
            System.out.println("\t\txCoord: " + lit.xCoord);
            System.out.println("\t\tyCoord: " + lit.yCoord);
        }*/
    }

	public Location2D nextLocation(long currentTimestamp, LocationContext actualTargetFieldLocationContext) {
		
		double currentTime = ((double)currentTimestamp)/getJistTimeConstant(timeUnit);

		if (rootTimestamp == -1)
			if (relativeTiming)
				rootTimestamp = currentTime;
			else
				rootTimestamp = 0;

		if (periodicity == 0)
			periodicity = path.locationInTime.getLast().timeStamp;
		
		currentTime = currentTime - rootTimestamp;
		
		long numberOfLoops = (long) Math.floor(currentTime/periodicity);
		baseTimestamp = (long) (periodicity * numberOfLoops);
      
		currentTime = currentTime - baseTimestamp;
		
		double residualTime = 0;
        
		if (literator == null)
			literator = path.locationInTime.iterator();
		
		if (lit == null)
			lit = literator.next();
		
		int loops = 0;
		            
        do {        	
            if (lastLocationInTime == null)
                lastLocationInTime = lit;
            else {
                if (lastLocationInTime.timeStamp <= currentTime && currentTime <= lit.timeStamp) {
                    nextLocationInTime = lit;
                    residualTime = lit.timeStamp - currentTime;
                    break;
                }
                else
                    lastLocationInTime = lit;
            }
            if (!literator.hasNext()) {
            	literator = path.locationInTime.iterator();
            	loops++;
            }
            lit = literator.next();
        } while (loops < 2);

        double x, y;
        double timeFraction = 1-((double)residualTime)/(nextLocationInTime.timeStamp - lastLocationInTime.timeStamp);
        
        if (timeFraction < 0)
            timeFraction = 0;
        if (timeFraction > 1)
            timeFraction = 1;
        
        if (lastLocationInTime.xCoord > nextLocationInTime.xCoord)
            x = (nextLocationInTime.xCoord + (lastLocationInTime.xCoord - nextLocationInTime.xCoord) * (1-timeFraction));
        else
            x = (lastLocationInTime.xCoord + (- lastLocationInTime.xCoord + nextLocationInTime.xCoord) * timeFraction);
        
       if (lastLocationInTime.yCoord > nextLocationInTime.yCoord)
            y = (nextLocationInTime.yCoord + (lastLocationInTime.yCoord - nextLocationInTime.yCoord) * (1-timeFraction));
        else
            y = (lastLocationInTime.yCoord + (- lastLocationInTime.yCoord + nextLocationInTime.yCoord) * timeFraction);                              
        
       Location2D loc;
       
        // relative positioning with field scaling
       if (fieldDimensions != null) { 
               /* get the segment (locationInTime) where the object is at current time */
               loc = new Location2D(x, y).toNCS(new LocationContext(fieldDimensions.length, fieldDimensions.width))
                                          .fromNCS(actualTargetFieldLocationContext);
       } else
    	   loc = new Location2D(x, y);
       
       if (mirrored)
    	   loc = mirror(loc, actualTargetFieldLocationContext);
       
       return loc;
	}
	
	 private long getJistTimeConstant(String timeUnit) {
        if (timeUnit.equals("millisecond"))
             return Constants.MILLI_SECOND;
        if (timeUnit.equals("second"))
             return Constants.SECOND;
        if (timeUnit.equals("minute"))
             return Constants.MINUTE;
        if (timeUnit.equals("hour"))
             return Constants.HOUR;

        return -1;
     }
	 
	 public void setMirrored(boolean mirrored) {
		 this.mirrored = mirrored;
	 }
	    
     private static Location2D mirror(Location2D loc, LocationContext locationContext) {
	   	 double x, y;
	   	 
	   	 int flipCountX = (int) Math.floor(loc.getX() / locationContext.getWidth());
	   	 int flipCountY = (int) Math.floor(loc.getY() / locationContext.getHeight());
	   	 
	   	 // on even cases
	   	 if (flipCountX % 2 == 0)
	   		 x = loc.getX() % locationContext.getWidth();
	   	 else // on odd cases
	   		 x = locationContext.getWidth() - loc.getX() % locationContext.getWidth();
	   	 
	   	// on even cases
	   	 if (flipCountY % 2 == 0)
	   		 y = loc.getY() % locationContext.getHeight();
	   	 else // on odd cases
	   		 y = locationContext.getHeight() - loc.getY() % locationContext.getHeight();
	   	 
	   	 return new Location2D(x, y);
     }
}
