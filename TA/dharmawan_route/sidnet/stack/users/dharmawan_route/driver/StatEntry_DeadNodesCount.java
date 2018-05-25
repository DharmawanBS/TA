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
