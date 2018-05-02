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
public class StatEntry_AliveNodesCount extends ExclusionStatEntry {
    private static final String TAG = "AliveNodesCount";
    private int aliveNodesCount = 0;
    private int energyPercentageThreshold;

    public StatEntry_AliveNodesCount(String key, int energyPercentageThreshold) {
        this(key, energyPercentageThreshold, null, null); // default header
    }

    public StatEntry_AliveNodesCount(String key, int energyPercentageThreshold, Region region, TYPE regionType)
    {
        super(key, TAG, region, regionType);
        this.energyPercentageThreshold = energyPercentageThreshold;
    }
            
    
    /**
     * @inheridoc
     */
    public String getValueAsString()
    {
        return "" + aliveNodesCount;
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes)
   {
       super.update(nodes);
       aliveNodesCount = 0;
       for (int i = 0; i < nodes.length; i++)
           if (included(nodes[i]))
                if (nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel() >= energyPercentageThreshold)
                    aliveNodesCount++;
   }

}
