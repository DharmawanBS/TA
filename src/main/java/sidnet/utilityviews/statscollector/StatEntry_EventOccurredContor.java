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
public class StatEntry_EventOccurredContor 
extends StatEntry
implements EventBasedStatEntry {
    private static final String TAG = "EventOccurredContor";
    private EventMonitor eventMonitor = null;
    
    public StatEntry_EventOccurredContor(String key)
    {
        super(key, TAG); // default                
    }
    
    public void setEventMonitor(EventMonitor eventMonitor)
    {
        this.eventMonitor = eventMonitor;
    }
    
     /**
     * @inheridoc
     */
    public String getValueAsString()
    {
        return "" + eventMonitor.getEventOccurredCount(key);
    }
        
    /**
     * @inheridoc
     */
   public void update(Node[] nodes)
   {
       // DO NOTHING
   }
}
