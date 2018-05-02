/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.utilityviews.statscollector;

import sidnet.core.misc.Node;
import sidnet.core.misc.Region;

/**
 *
 * @author Oliver
 */
public class StatEntry_MinimumNeighborsCount extends ExclusionStatEntry{
    private static final String TAG = "MinimumNeighborsCount";
    private int minimumNeighborsCount = 0;


    public StatEntry_MinimumNeighborsCount(String key)
    {
        this(key, null, null);
    }
    
    public StatEntry_MinimumNeighborsCount(String key, Region region, TYPE regionType)
    {
        super(key, TAG, region, regionType);
    }
            
    
    /**
     * @inheridoc
     */
    public String getValueAsString()
    {
        return "" + minimumNeighborsCount;
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes)
   {
       super.update(nodes);
       minimumNeighborsCount = 10000;
       for (int i = 0; i < nodes.length; i++)
           if (included(nodes[i]))
                if (minimumNeighborsCount > nodes[i].neighboursList.size())
                        minimumNeighborsCount = nodes[i].neighboursList.size();
   }

}
