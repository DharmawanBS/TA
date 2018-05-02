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
public class StatEntry_DeadNodesPercentage extends ExclusionStatEntry{
    private static final String TAG = "DeadNodesCount";
    private double deadNodesPercentage = 0;
    private int energyPercentageThreshold;
         
    public StatEntry_DeadNodesPercentage(String key, int energyPercentageThreshold) {
        this(key, energyPercentageThreshold, null, null);
    }
    
    public StatEntry_DeadNodesPercentage(String key, int energyPercentageThreshold, Region region, TYPE regionType) {
        super(key, TAG, region, regionType);
        this.energyPercentageThreshold = energyPercentageThreshold;
    }
        
    /**
     * @inheridoc
     */
    public String getValueAsString()
    {
        return "" + deadNodesPercentage;
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes)
   {
       super.update(nodes);
       int deadNodesCount = 0;
       for (int i = 0; i < nodes.length; i++)
       {
           if (included(nodes[i]))
                if (nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel() < energyPercentageThreshold)
                    deadNodesCount++;
       }
       deadNodesPercentage = deadNodesCount / inclusionContor * 100;
   }

}
