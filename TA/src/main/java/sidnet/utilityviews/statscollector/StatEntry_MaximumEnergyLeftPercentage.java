/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.utilityviews.statscollector;

import java.util.LinkedList;
import java.util.List;
import sidnet.core.misc.Node;
import sidnet.core.misc.Region;

/**
 *
 * @author Oliver
 */
public class StatEntry_MaximumEnergyLeftPercentage extends ExclusionStatEntry{
    private static final String TAG = "MinimumEnergyLeftPercentage";
    private double maximumEnergyLeftPercentage = 0;  

    public StatEntry_MaximumEnergyLeftPercentage(String key)
    {
        this(key, null, null);
    }
    
    public StatEntry_MaximumEnergyLeftPercentage(String key, Region region, TYPE regionType)
    {
        super(key, TAG, region, regionType);
    }
    
    /**
     * @inheridoc
     */
    public String getValueAsString()
    {
        return "" + maximumEnergyLeftPercentage;
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes)
   {
       super.update(nodes);
       maximumEnergyLeftPercentage = 0;
       for (int i = 0; i < nodes.length; i++)
           if (included(nodes[i]))
               if (nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel() > maximumEnergyLeftPercentage)
                   maximumEnergyLeftPercentage = nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel();
   }

}
