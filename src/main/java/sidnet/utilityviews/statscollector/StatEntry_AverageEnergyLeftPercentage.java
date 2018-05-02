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
public class StatEntry_AverageEnergyLeftPercentage extends ExclusionStatEntry{
    private static final String TAG = "AverageEnergyLeftPercentage";
    private double averageEnergyLeftPercentage = 0;
    
    public StatEntry_AverageEnergyLeftPercentage(String key) {
        this(key, null, null); // default header
    }

    public StatEntry_AverageEnergyLeftPercentage(String key, Region region, TYPE regionType)
    {
        super(key, TAG, region, regionType);
    }
    
    
    /**
     * @inheridoc
     */
    public String getValueAsString()
    {
        return "" + averageEnergyLeftPercentage;
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes) 
   {
       super.update(nodes);
       averageEnergyLeftPercentage = 0;
       for (int i = 0; i < nodes.length; i++)
           if (included(nodes[i]))
                averageEnergyLeftPercentage += nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel();
       averageEnergyLeftPercentage /= inclusionContor;
   }

}
