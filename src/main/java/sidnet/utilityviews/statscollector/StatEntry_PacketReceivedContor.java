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
public class StatEntry_PacketReceivedContor
extends StatEntry
implements EventBasedStatEntry{
    private static final String TAG = "PacketReceivedContor";
    private EventMonitor packetMonitor = null;
    
    
    public StatEntry_PacketReceivedContor(String key)
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
        return "" + packetMonitor.getEventDetectedCount(key);
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes)
   {
       // DO NOTHING
   }
}
