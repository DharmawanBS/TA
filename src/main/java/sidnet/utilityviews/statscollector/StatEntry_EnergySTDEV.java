/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.utilityviews.statscollector;

import java.util.HashMap;
import java.util.LinkedList;
import jist.swans.net.NetAddress;
import sidnet.core.misc.Node;
import sidnet.core.misc.Region;

/**
 *
 * @author Oliver
 */
public class StatEntry_EnergySTDEV extends ExclusionStatEntry{
    private static final String TAG = "EnergySTDEV";
    private double energyImbalance = 0;
    private HashMap nodeListMap = new HashMap();

    public StatEntry_EnergySTDEV(String key) {
        this(key, null, null); // default header
    }

    public StatEntry_EnergySTDEV(String key, Region region, TYPE regionType) {
        super(key, TAG, region, regionType);
    }
            
    
    /**
     * @inheridoc
     */
    public String getValueAsString() {
        return "" + energyImbalance;
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes) {
       double[] stdevNeigh = new double[nodes.length];
       double avgStdev = 0;
       
       long contor = 0;
       
       super.update(nodes);
       for (int i = 0; i < nodes.length; i++)
        {
           /* if (nodes[i].getID() != 390 &&
                nodes[i].getID() != 363 &&
                nodes[i].getID() != 251 &&
                nodes[i].getID() != 101 &&
                nodes[i].getID() != 196 &&
                nodes[i].getID() != 319 &&
                nodes[i].getID() != 70 &&
                nodes[i].getID() != 10 &&
                nodes[i].getID() != 450)
                continue;*/
            if (included(nodes[i])) {
                if (!nodeListMap.containsKey(nodes[i].getID()))
                    nodeListMap.put(nodes[i].getID(), nodes[i].physicalNeighboursList.getAsLinkedList());
                double stdev = 0;
                LinkedList nodeListt = (LinkedList<sidnet.core.misc.NodeEntry>)nodes[i].physicalNeighboursList.getAsLinkedList();
                //LinkedList nodeListt = (LinkedList<misc.NodeEntry>)nodeListMap.get(nodes[i].getID());
                for (sidnet.core.misc.NodeEntry nodeEntry: (LinkedList<sidnet.core.misc.NodeEntry>)nodeListt) {
                    if (included(nodes[nodeEntry.ip.hashCode()])) {
                        double diff = nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel() - ((Node)retrieveNodeForIP(nodes, nodeEntry.ip)).getEnergyManagement().getBattery().getPercentageEnergyLevel();
                        stdev += diff * diff;
                        contor++;
                    }
                }
                stdev = Math.sqrt(stdev/((LinkedList<sidnet.core.misc.NodeEntry>)nodeListt).size());

                avgStdev += stdev;
            }
        }
        energyImbalance =  avgStdev / contor;
        
        System.out.println("STDev contor = " + contor);
   }
   
   private Node retrieveNodeForIP(Node[] nodes, NetAddress ip) {
        for (int i = 0; i < nodes.length; i++)
            if (nodes[i].getIP().equals(ip))
                return nodes[i];
        return null;
   }
}
