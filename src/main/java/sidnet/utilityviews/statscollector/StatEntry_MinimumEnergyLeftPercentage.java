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
public class StatEntry_MinimumEnergyLeftPercentage extends ExclusionStatEntry {
    private static final String TAG = "MinimumEnergyLeftPercentage";
    private double minimumEnergyLeftPercentage = 0;


    public StatEntry_MinimumEnergyLeftPercentage(String key)
    {
        this(key, null, null);
    }
    
    public StatEntry_MinimumEnergyLeftPercentage(String key, Region region, TYPE regionType)
    {
        super(key, TAG, region, regionType);
    }
    
    /**
     * @inheridoc
     */
    public String getValueAsString()
    {
        return "" + minimumEnergyLeftPercentage;
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes)
   {
       super.update(nodes);
       minimumEnergyLeftPercentage = 100;
       for (int i = 0; i < nodes.length; i++)
           if (included(nodes[i]))
               if (nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel() < minimumEnergyLeftPercentage)
                   minimumEnergyLeftPercentage = nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel();
   }

}
