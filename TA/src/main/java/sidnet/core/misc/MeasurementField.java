/*
 * MeasurementField.java
 *
 * Created on September 22, 2005, 10:26 AM
 */

package sidnet.core.misc;

/**
 *
 * @author  Oliviu Ghica
 */
import java.util.*;

public class MeasurementField
{
    int minVal;
    int maxVal;
    double interval;            // [ms] between the modification of the values in the field
    double intervalDeviation;   // [%] specifies the amount (percentage) of deviation in time of the interval
    double lastMeasureTimeStamp;
    
    int measure;
    
    double actualInterval;
    double measurementDynamics; // [%] Each new measurement is generated randomly. User may want to control the amount of
                                // deviation between the new value and the old value; 100% means total randomness, while
                                // 0% means the value will never change from the initial value set by the constructor
    Random rndGenerator;     
    
    /** Creates a new instance of MeasurementField */
    public MeasurementField(int minVal, int maxVal, int interval, int intervalDeviation, int measurementDynamics, long seed) {
        this.minVal   = minVal;
        this.maxVal   = maxVal;
        this.interval = interval;
        if (intervalDeviation > 100 || intervalDeviation < 0)
            System.out.println("ERROR: [MeasurementField] - intervalDeviation MUST be a percentage value!");
        if (intervalDeviation > 100)
            intervalDeviation = 100;
        if (intervalDeviation < 0)
            intervalDeviation = 0;
        this.intervalDeviation = intervalDeviation;
       
        this.actualInterval = interval;
        this.lastMeasureTimeStamp = 0;
        
            
         if (measurementDynamics > 100 || measurementDynamics < 0)
            System.out.println("ERROR: [MeasurementField] - measurementDynamics MUST be a percentage value!");
        if (measurementDynamics > 100)
            measurementDynamics = 100;
        if (measurementDynamics < 0)
            measurementDynamics = 0;
        
        this.measurementDynamics = measurementDynamics;
       
        measure = (int)(maxVal - minVal)/2;
        
        rndGenerator = new Random();
        rndGenerator.setSeed(seed);
    }
    
    public int aquireMeasure(long timeStamp)
    {
        updateMeasure(timeStamp);
        return measure;
    }
    
    public void updateMeasure(long timeStamp)
    {
        if (timeStamp - lastMeasureTimeStamp > actualInterval)
        {
            double realIntervalDeviation = intervalDeviation*interval/100;
            actualInterval = interval - realIntervalDeviation/2 + rndGenerator.nextInt((int)(realIntervalDeviation));
            
            double realMeasurementDynamics = measurementDynamics*(maxVal - minVal)/100;
            measure = (int)((double)measure - realMeasurementDynamics/2 + (double)rndGenerator.nextInt((int)realMeasurementDynamics));
            
            if (measure < minVal)
                measure = minVal;
             if (measure > maxVal)
                measure = maxVal;      
            
            lastMeasureTimeStamp = timeStamp;
        }      
    }
}
