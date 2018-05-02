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
public class StatEntry_MessagesContor extends StatEntry{
    private static final String TAG = "MessagesContor";
    private int contor = 0;

    public StatEntry_MessagesContor(String key)
    {
        super(key, TAG);
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
