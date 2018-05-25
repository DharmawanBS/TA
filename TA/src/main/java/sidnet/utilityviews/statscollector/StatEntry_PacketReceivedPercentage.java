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
public class StatEntry_PacketReceivedPercentage extends StatEntry implements EventBasedStatEntry{
    private static final String TAG = "PacketReceivedPercentage";
    private EventMonitor packetMonitor = null;
    
    
    public StatEntry_PacketReceivedPercentage(String key)
    {
        super(key, TAG); // default                
    }
    
    public void setEventMonitor(EventMonitor packetMonitor)
    {
        this.packetMonitor = packetMonitor;
    }
    
    
     /**
     * @inheridoc
     */
    public String getValueAsString()
    {
        return "" + (long)Math.ceil((double)packetMonitor.getEventDetectedCount(key) * 100 / packetMonitor.getEventOccurredCount(key));
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes)
   {
       // DO NOTHING
   }
}
