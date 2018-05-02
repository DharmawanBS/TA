/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.utilityviews.statscollector;

import java.util.HashMap;
import java.util.LinkedList;

import sidnet.core.misc.Node;
import jist.swans.net.NetAddress; 
import sidnet.core.misc.Region;
import jist.runtime.JistAPI;
/**
 *
 * @author Oliver
 */
public class StatEntry_EnergyLeftPercentage 
extends ExclusionStatEntry 
implements NodeBasedStatEntry, JistAPI.Timeless {
    private static final String TAG = "EnergyLeftPercentage";
    public enum MODE {MIN, MAX, AVG, STDEV};
    private double energyLeftPercentage = 0.0;
    private double energyImbalance = 0.0;
    private HashMap nodeListMap = new HashMap();
    
    private MODE mode;

    public StatEntry_EnergyLeftPercentage(String key, MODE mode) {
        this(key, mode, null, null);
    }
    
    public StatEntry_EnergyLeftPercentage(String key, MODE mode, Region region, TYPE regionType) {
        super(key, TAG + "_" + mode, region, regionType);
        this.mode = mode;
    }
            
    
    /**
     * @inheridoc
     */
    public String getValueAsString() {       
        switch(mode) {
            case MIN:
            case MAX:
            case AVG: return "" + Utils.roundTwoDecimals(energyLeftPercentage);
            case STDEV: return "" + Utils.roundTwoDecimals(energyImbalance);
        }
        return "-1";
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes) {
       super.update(nodes);
       switch(mode) {
           case MIN:   updateMin(nodes); break;
           case MAX:   updateMax(nodes); break;
           case AVG:   updateAvg(nodes); break;
           case STDEV: updateSTDEV(nodes); break;
       }       
   }
   
   private void updateMin(Node[] nodes)
   {
       super.update(nodes);       
       energyLeftPercentage = 100;
       for (int i = 0; i < nodes.length; i++)
       {          
           if (included(nodes[i]))
           {                
                if (nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel() < energyLeftPercentage)
                        energyLeftPercentage = nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel();
           }
       }
   }
   
   private void updateMax(Node[] nodes)
   {
       super.update(nodes);
       energyLeftPercentage = 0;
       for (int i = 0; i < nodes.length; i++)
           if (included(nodes[i]))
                if (nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel() > energyLeftPercentage)
                       energyLeftPercentage = nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel();
   }
   
   private void updateAvg(Node[] nodes)
   {
       super.update(nodes);
       energyLeftPercentage = 0;
       int contor = 0;
       for (int i = 0; i < nodes.length; i++)
           if (included(nodes[i]))
           {
                 double val = nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel();
                 // OLIVER: it may, very rarely, throw > 100 due to out-of-time between proxies, which is normal.
                 // but we don't want those outliers to get into the stats.
                 // This problem actually happens only when using Sector-based inclusion
                 if (val <= 100) 
                 {
                    energyLeftPercentage += val;
                    contor ++;
                 }
           }
       if (energyLeftPercentage != 0 && inclusionContor == 0)
       {
           Exception e = new Exception("[ERROR][StatEntry_EnergyLeftPercentage].updateAvg() - inclusionContor = 0 but found at least one node in the inclusion list. Quitting");
           e.printStackTrace();
           System.exit(1);
       }
      
       if (inclusionContor != 0)
            energyLeftPercentage = energyLeftPercentage/contor;
       else
            energyLeftPercentage = 0;
   }
   
   private void updateSTDEV(Node[] nodes) {
       double[] stdevNeigh = new double[nodes.length];
       double avgStdev = 0;       
       
       for (int i = 0; i < nodes.length; i++) {           
            if (!nodeListMap.containsKey(nodes[i].getID()))
                nodeListMap.put(nodes[i].getID(), nodes[i].physicalNeighboursList.getAsLinkedList());
            double stdev = 0;
            LinkedList nodeListt = (LinkedList<sidnet.core.misc.NodeEntry>)nodes[i].physicalNeighboursList.getAsLinkedList();
            
            int contor = 0;
            for (sidnet.core.misc.NodeEntry nodeEntry: (LinkedList<sidnet.core.misc.NodeEntry>)nodeListt) {
                if (included(nodes[nodeEntry.ip.hashCode()]))  {
                    contor++;
                    double diff = nodes[i].getEnergyManagement().getBattery().getPercentageEnergyLevel() - ((Node)retrieveNodeForIP(nodes, nodeEntry.ip)).getEnergyManagement().getBattery().getPercentageEnergyLevel();
                    stdev += diff * diff;
                }
            }
            if (contor != 0) {
                stdev = Math.sqrt(stdev/contor);            
                avgStdev += stdev;
            }
        }
       if (avgStdev != 0 && inclusionContor == 0)
       {
           Exception e = new Exception("[ERROR][StatEntry_EnergyLeftPercentage].updateSTDEV() - inclusionContor = 0 but found at least one node in the inclusion list. Quitting");
           e.printStackTrace();
           System.exit(1);
       }
       if (inclusionContor != 0)
            energyImbalance =  avgStdev / inclusionContor;
       else
            energyImbalance = 0;
   }
   
   private Node retrieveNodeForIP(Node[] nodes, NetAddress ip)
   {
        for (int i = 0; i < nodes.length; i++)
            if (nodes[i].getIP().equals(ip))
                return nodes[i];
        return null;
   }

}
