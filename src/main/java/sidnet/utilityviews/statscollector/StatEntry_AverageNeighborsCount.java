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
public class StatEntry_AverageNeighborsCount
extends StatEntry
implements NodeBasedStatEntry {
    private static final String TAG = "AverageNeighborsCount";
    private int averageNeighborsCount = 0;
    
    public StatEntry_AverageNeighborsCount(String key) {
        super(key, TAG); // default header
    }
            
    
    /**
     * @inheridoc
     */
    public String getValueAsString() {
        return "" + averageNeighborsCount;
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes) {
       averageNeighborsCount = 0;
       for (int i = 0; i < nodes.length; i++)
            averageNeighborsCount += nodes[i].neighboursList.size();
       averageNeighborsCount /= nodes.length;
   }
}
