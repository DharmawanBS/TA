/*
 * StatEntry.java
 *
 * Created on April 30, 2008, 6:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.utilityviews.statscollector;

/**
 *
 * @author Oliver
 */
public abstract class StatEntry {
    protected String tag = "Not Specified. Please Specify one in your StatEntry extending class";
    protected String key;    
    
    /**
     * Must be called from extending classes
     * Each data value that is being collected is referred to by means of a Header (or tag) (e.q. NumberOfNodesDead, AverageEnergy, etc)
     * @param header
     */
    public StatEntry(String key, String tag) {
        this.key = key;
        this.tag = tag;
    }
    
    /** 
     * Each data value that is being collected is referred to by means of a Header (or tag) (e.q. NumberOfNodesDead, AverageEnergy, etc)
     * @return header
     */
    public String getHeader() {
        if (key == null || key.length() == 0)
            return tag;
        else
            return key + "_" + tag;
    }
    
    public String getKey() {
        return key;
    }
        
    /**
     * Returns the current recorded value of the data that is being monitored, as String
     * @return data value
     */
    public abstract String getValueAsString();       
}
