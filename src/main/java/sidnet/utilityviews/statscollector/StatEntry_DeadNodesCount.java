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
public class StatEntry_DeadNodesCount 
extends ExclusionStatEntry
implements NodeBasedStatEntry {
	
    private static final String TAG = "DeadNodesCount";
    private int deadNodesCount = 0;
    private int energyPercentageThreshold;

    public StatEntry_DeadNodesCount(String key, int energyPercentageThreshold) {
        super(key, TAG);
        this.energyPercentageThreshold = energyPercentageThreshold;
    }
    
    public StatEntry_DeadNodesCount(String key, int energyPercentageThreshold, Region region, TYPE regionType) {
        super(key, TAG, region, regionType);
        this.energyPercentageThreshold = energyPercentageThreshold;
    }
    
    public void initialize(int numberOfNodes) {
        inclusionNodesMap = new STATUS[numberOfNodes];
        for (int i = 0; i < inclusionNodesMap.length; i++)
            inclusionNodesMap[i] = STATUS.INCLUDED;
    }
            
    
    /**
     * @inheridoc
     */
    public String getValueAsString() {
        return "" + deadNodesCount;
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes) {
       super.update(nodes);
       deadNodesCount = 0;
       for (int i = 0; i < nodes.length; i++)
           if (included(nodes[i]))
             if (nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel() < energyPercentageThreshold)
                   deadNodesCount++;
   }
}
