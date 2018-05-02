/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.utilityviews.statscollector;

import sidnet.core.misc.Node;

/**
 *
 * @author Oliver
 */
public class StatEntry_PacketDeliveryLatency 
extends StatEntry
implements EventBasedStatEntry {
	
    private static final String TAG = "PacketDeliveryLatency";
    public enum MODE{AVG, MIN, MAX};
    private MODE mode;
            
    private EventMonitor packetMonitor = null;
       
    public StatEntry_PacketDeliveryLatency(String key, MODE mode) {
        super(key, TAG + "_" + mode); // default                
        this.mode = mode;
    }
    
    public void setEventMonitor(EventMonitor packetMonitor) {
        this.packetMonitor = packetMonitor;
    }
    
    
     /**
     * @inheridoc
     */
    public String getValueAsString() {
        if (mode == MODE.AVG)
            return "" + packetMonitor.getAverageDeliveryLatency(key);
        else if (mode == MODE.MAX)
            return "" + packetMonitor.getMaximumDeliveryLatency(key);
        else if (mode == MODE.MIN)
            return "" + packetMonitor.getMinimumDeliveryLatency(key);
        else
            return "" + "-1";
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes) {
       // DO NOTHING
   }
}
