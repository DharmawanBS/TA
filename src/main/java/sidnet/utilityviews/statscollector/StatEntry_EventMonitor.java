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
public class StatEntry_EventMonitor extends StatEntry{
    private static final String TAG = "EventMonitor";
    private int contor = 0;

    public StatEntry_EventMonitor(String key)
    {
        super(key, TAG); // default header
    }

          
    public void increment()
    {
        contor++;
    }
    
    /**
     * @inheridoc
     */
    public String getValueAsString()
    {
        return "" + contor;
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes)
   {
       // do nothing;
   }

}
