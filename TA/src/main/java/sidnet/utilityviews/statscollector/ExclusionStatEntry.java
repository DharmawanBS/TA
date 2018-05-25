/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.utilityviews.statscollector;
import sidnet.core.misc.Node;
import sidnet.core.misc.Region;
import jist.runtime.JistAPI;

/**
 *
 * @author Oliver
 */
public abstract class ExclusionStatEntry 
extends StatEntry 
implements JistAPI.Timeless{
    /* ********************************************** */
    public static final boolean DEBUG = false;
    /* ********************************************** */
    
    public enum STATUS {UNKNOWN, INCLUDED, EXCLUDED};
    public enum TYPE {EXCLUSION_REGION, INCLUSION_REGION};
    private Region region = null;
    private TYPE type;
    protected STATUS inclusionNodesMap[] = null;
    protected int inclusionContor=0;
    private boolean initialized = false;   
   
     /**
     * Must be called from extending classes
     * Each data value that is being collected is refereed to by means of a Header (or tag) (e.q. NumberOfNodesDead, AverageEnergy, etc)
     * @param header
     */
    public ExclusionStatEntry(String key, String tag) {
        this(key, tag, null, null);
    }
    
    public ExclusionStatEntry(String key, String tag, Region region, TYPE regionType) {
        super(key, tag);
        this.region = region;
        this.type   = regionType;     
    }
    
    public void initialize(int numberOfNodes) {
        inclusionNodesMap = new STATUS[numberOfNodes];
        for (int i = 0; i < inclusionNodesMap.length; i++)
            inclusionNodesMap[i] = STATUS.UNKNOWN;
    }
    
    public void excludeFromMonitoring(int id) {
        if (inclusionNodesMap[id] == STATUS.INCLUDED)
                inclusionContor--;
        inclusionNodesMap[id] = STATUS.EXCLUDED;        
    }
     
    public void includeInMonitoring(int id) {
        if (inclusionNodesMap[id] == STATUS.EXCLUDED || 
            inclusionNodesMap[id] == STATUS.UNKNOWN)
                inclusionContor++;
        inclusionNodesMap[id] = STATUS.INCLUDED;        
    }
    
    /*
     * A node is included IF it is inside an inclusion region and it was not explicitly excluded
     *                            
     */
    public boolean included(Node node) {
        if (region != null) {            
            if (type == TYPE.INCLUSION_REGION) { // nodes that are inside the region are the ones considered
                if (region.isInside(node.getNCS_Location2D())) {
                    if (inclusionNodesMap[node.getID()] == STATUS.UNKNOWN) {
                        inclusionNodesMap[node.getID()] = STATUS.INCLUDED;
                        inclusionContor++;
                    }
                    return true && !(inclusionNodesMap[node.getID()] == STATUS.EXCLUDED); /* inside and not explicitly excluded */
                }
                return false || inclusionNodesMap[node.getID()] == STATUS.INCLUDED; /* if outside but explicitly included */
            } else { // nodes that are outside the region are the ones considered
                if (!region.isInside(node.getNCS_Location2D())) { // the exterior is now the inclusion
                    if (inclusionNodesMap[node.getID()] == STATUS.UNKNOWN) {
                        inclusionNodesMap[node.getID()] = STATUS.INCLUDED;
                        inclusionContor++;
                    }                
                    return true && !(inclusionNodesMap[node.getID()] == STATUS.EXCLUDED); /* outside and not explicitly excluded */
                }
                return false || inclusionNodesMap[node.getID()] == STATUS.INCLUDED; /* if inside but explicitly included */
            }
        }
        else
            return inclusionNodesMap[node.getID()] != STATUS.EXCLUDED;
    }
    
    /**
     * User-defined processing (update of inner statistical values) based on the informations comprised in the Node
     * The implementation must call this method first
     */
   public void update(Node[] nodes) {
       if (!initialized) {
           initialized = true;          
           for (int i = 0; i < inclusionNodesMap.length; i++) {
               if (inclusionNodesMap[i] == STATUS.UNKNOWN) {
                   if (region == null) {
                        inclusionNodesMap[i] = STATUS.INCLUDED;
                        inclusionContor++;
                   }
                   else {
                        if (region.isInside(nodes[i].getNCS_Location2D()) && type == TYPE.INCLUSION_REGION ||
                           !region.isInside(nodes[i].getNCS_Location2D()) && type == TYPE.EXCLUSION_REGION) {
                            if (DEBUG)
                                System.out.println("[StatsCollector][ExclusionStatEntry].Update() - Node[" + i + "] - included " + nodes[i].getNCS_Location2D().getX() + "," + nodes[i].getNCS_Location2D().getY());
                            inclusionNodesMap[i] = STATUS.INCLUDED;
                            inclusionContor++;
                        }
                        else {
                            if (DEBUG)
                                System.out.println("[StatsCollector][ExclusionStatEntry].Update() - Node[" + i + "] - excluded " + nodes[i].getNCS_Location2D().getX() + "," + nodes[i].getNCS_Location2D().getY());
                            inclusionNodesMap[i] = STATUS.EXCLUDED;
                        }
                   }
               }
           }
       }
   }
}
