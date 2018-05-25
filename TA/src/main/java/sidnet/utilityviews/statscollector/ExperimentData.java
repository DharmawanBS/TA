/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.utilityviews.statscollector;

import java.util.LinkedHashMap;
import java.util.Map;

import jist.runtime.JistAPI;
import jist.swans.Constants;

/**
 * @author Oliver
 */
public abstract class ExperimentData 
implements ExperimentDataInterface, JistAPI.DoNotRewrite {
    /* *********************** */
    /* Most often, the same experiment should be repeated several times under a different deployment (random)
     * For that, when you run SIDnetCSVRunner, specify the argument repeat=50 (for 50 repetitions)
     * The SIDnetCSVRunner will iterate from 1 ... 50 and pass that value as argument [0] through command line
     * That needs to be stored locally under the following variable.
     * It will be used to generate experimentId's for subsequent repetitions
     * It should also be used in the driver to generate multiple deployment's */
        
    // furnished implicitly by the batching mechanism
    public static final String REPEAT_INDEX     = "repeatIndex",
    						   EXPERIMENTS_TARGET_DIRECTORY = "experimentsTargetDirectory",
    						   RUN_ID			= "runId",
    						   EXPERIMENT_ID    = "experimentId";
    
    // explicitly by user
    public static final String EXPERIMENT_TAG   = "experimentTag",    						  
    						   NODES_COUNT      = "nodesCount",
    						   FIELD_LENGTH     = "fieldLength",
    						   SIM_TIMEOUT      = "simTimeout",
    						   LOGGING_INTERVAL = "statsLoggingInterval[s]";
    						                 
	protected Map<String, String> configurationData = new LinkedHashMap<String, String>();
    
    public ExperimentData() {       
    	extractData(REPEAT_INDEX);
    	extractData(EXPERIMENTS_TARGET_DIRECTORY);
    	extractData(RUN_ID);
    	extractData(EXPERIMENT_ID);
    	extractData(EXPERIMENT_TAG);
    	extractData(NODES_COUNT );
    	extractData(FIELD_LENGTH);
    	extractData(SIM_TIMEOUT);
    	extractData(LOGGING_INTERVAL);
    	configurationData.put(LOGGING_INTERVAL, "" + getLong(LOGGING_INTERVAL) * Constants.SECOND);
    }
    
    public int getInt(String tag) {
    	if (configurationData.containsKey(tag))
    		return Integer.parseInt(configurationData.get(tag));
    	else
    		fail(tag);
    	
    	return -1;
    }
    
    public int getInt(String tag, int defaultValue) {
    	if (configurationData.containsKey(tag))
    		return Integer.parseInt(configurationData.get(tag));
    	else {
    		warnDefaults(tag, defaultValue);
    		return defaultValue;
    	}
    }
    
    public long getLong(String tag) {
    	if (configurationData.containsKey(tag))
    		return Long.parseLong(configurationData.get(tag));
    	else
    		fail(tag);
    	
    	return -1;
    }
    
    public long getLong(String tag, long defaultValue) {
    	if (configurationData.containsKey(tag))
    		return Long.parseLong(configurationData.get(tag));
    	else {
    		warnDefaults(tag, defaultValue);
    		return defaultValue;
    	}
    }
    
    public double getDouble(String tag) {
    	if (configurationData.containsKey(tag))
    		return Double.parseDouble(configurationData.get(tag));
    	else
    		fail(tag);
    	
    	return -1;
    }
    
    public double getDouble(String tag, double defaultValue) {
    	if (configurationData.containsKey(tag))    		
    		return Double.parseDouble(configurationData.get(tag));
    	else {
    		warnDefaults(tag, defaultValue);
    		return defaultValue;
    	}
    }
    
    public boolean getBoolean(String tag) {
    	if (configurationData.containsKey(tag))
    		return Boolean.parseBoolean(configurationData.get(tag));
    	else
    		fail(tag);
    	
    	return false;
    }
    
    public boolean getBoolean(String tag, boolean defaultValue) {
    	if (configurationData.containsKey(tag))    		
    		return Boolean.parseBoolean(configurationData.get(tag));
    	else {
    		warnDefaults(tag, defaultValue);
    		return defaultValue;
    	}
    }
    
    public String getString(String tag) {
    	if (configurationData.containsKey(tag))
    		return configurationData.get(tag);
    	else
    		fail(tag);
    	
    	return null;
    }
    
    public String getString(String tag, String defaultValue) {
    	if (configurationData.containsKey(tag))
    		return configurationData.get(tag);
    	else {
    		warnDefaults(tag, defaultValue);
    		return defaultValue;
    	}
    }
    
    private void fail(String tag) {
    	throw new RuntimeException("Configuration: \"" + tag + "\" not found!" );
    }
    
    private void warnDefaults(String tag, Object defaultValue) {
    	System.out.println("[ExperimentData] WARNING - Running with default value: [" + tag + "] = " + defaultValue);
    }
    
    protected void extractData(String tag) {
    	String property = System.getProperty(tag);
    	if (property != null)
    		configurationData.put(tag, System.getProperty(tag));    	
    }
    
    public String getDataSummary() {
        String s = "";
        s += "\nSIDnet-SWANS Simulation Log File";
        s += "\n\nGroupBy parameters:\n";
        
        for (String key: configurationData.keySet())
        	s += "\n<" + key + ">\t\t" + configurationData.get(key);
        
        return s;
    }
}
