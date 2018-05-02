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
public class StatEntry_MaximumNeighborsCount extends ExclusionStatEntry{
    private static final String TAG = "MaximumNeighborsCount";
    private int maximumNeighborsCount = 0;


    public StatEntry_MaximumNeighborsCount(String key)
    {
        this(key, null, null);
    }
            
    public StatEntry_MaximumNeighborsCount(String key, Region region, TYPE regionType)
    {
        super(key, TAG, region, regionType);
    }
    
    /**
     * @inheridoc
     */
    public String getValueAsString()
    {
        return "" + maximumNeighborsCount;
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes)
   {
       super.update(nodes);
       maximumNeighborsCount = 0;
       for (int i = 0; i < nodes.length; i++)
           if (included(nodes[i]))
                if (maximumNeighborsCount < nodes[i].neighboursList.size())
                      maximumNeighborsCount = nodes[i].neighboursList.size();
   }

}
