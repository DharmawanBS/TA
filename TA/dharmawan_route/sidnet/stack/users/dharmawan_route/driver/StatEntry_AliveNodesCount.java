/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.users.dharmawan_route.driver;

import sidnet.utilityviews.statscollector.ExclusionStatEntry;
import sidnet.utilityviews.statscollector.NodeBasedStatEntry;
import sidnet.core.misc.Node;

/**
 *
 * @author Oliver
 */
public class StatEntry_AliveNodesCount extends ExclusionStatEntry implements NodeBasedStatEntry {
    private static final String TAG = "AliveNodesCount";
    private int aliveNodesCount = 0;
    private int energyPercentageThreshold;

    public StatEntry_AliveNodesCount(String key, int energyPercentageThreshold)
    {
        super(key, TAG);
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
