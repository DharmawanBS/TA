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
public class StatEntry_EventMissedRatio extends StatEntry implements EventBasedStatEntry{
    private static final String TAG = "EventMissedRatio";
    private EventMonitor eventMonitor = null;
    
    public StatEntry_EventMissedRatio(String key)
    {
        super(key, TAG);        
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
        return "" + eventMonitor.eventMissedRatio(key);
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes)
   {
       // DO NOTHING
   }
}
