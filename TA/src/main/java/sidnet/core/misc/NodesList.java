/*
 * DeadEndList.java
 *
 * Created on November 3, 2005, 5:41 PM
 */

package sidnet.core.misc;

/**
 *
 * @author  Oliviu Ghica
 */

import jist.swans.net.NetAddress;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import jist.swans.mac.MacAddress;


public class NodesList{

    private HashMap list = null;
    private int size;
    
    
    /** Creates a new instance of DeadEndList */
    public NodesList() {
        list   = new HashMap();
        size = 0;
    }
    
    public void add(NetAddress ip, NodeEntry entry)
    {
        /* Check to see if the object already exist */
        NodeEntry n = (NodeEntry)list.get(ip);
        if(n==null)
        {
            list.put(ip, entry);
            size ++;
        }
    }
    
    public void add(NodeEntry n)
    {
        if (n.ip != null)
            add(n.ip, n);
    }
    
    public void add(NetAddress ip)
    {
        add(ip, new NodeEntry(null, ip, null)); 
    }
    
    public void remove(int id)
    {
        remove(new NetAddress(id));
    }
    
    public void remove(NetAddress ip)
    {
        //System.out.println("remove node from neighboring list");
        Iterator it = list.values().iterator();
        while(it.hasNext()) 
        {
            NodeEntry n = (NodeEntry)it.next();
     
            if (n.ip.hashCode() == ip.hashCode())
            {
                it.remove();
                size --;
            }
        }
    }
    
    public void remove(NodeEntry n)
    {
        if (n.ip != null)
            remove(n.ip);
    }
    
    public void printList()
    {
        System.out.println("NodeList : ");
        Iterator it = list.values().iterator();
        while(it.hasNext()) 
        {
            NodeEntry n = (NodeEntry)it.next();
            System.out.println("        IP: " + n.ip + "        MAC: " + n.mac + "        Location: (" + n.getNCS_Location2D().getX() + ", " + n.getNCS_Location2D().getY() + ")");
        }
    }
    
    public boolean contains(int nodeId)
    {
        return contains(new NetAddress(nodeId));
    }
    
    public boolean contains(NetAddress ip)
    {
        if (ip == null)
            return false;
        Iterator it = list.values().iterator();
        while(it.hasNext()) 
        {
            NodeEntry n = (NodeEntry)it.next();
     
            if (n.ip.hashCode() == ip.hashCode())
                return true;  
        }
        return false;
    }
    
    public boolean contains(MacAddress mac)
    {
        Iterator it = list.values().iterator();
        while(it.hasNext()) 
        {
            NodeEntry n = (NodeEntry)it.next();
     
            if (n.mac.equals(mac))
                return true;  
        }
        return false;
    }
    
    public NodeEntry get(NetAddress ip)
    {
        if (contains(ip))
            return (NodeEntry)list.get(ip);
        else
            return null;
    }
    
    public NodeEntry getElementAt(int index)
    {
        NodeEntry n = null;
        
        Iterator it = list.values().iterator();
        while(it.hasNext() && index >= 0) 
        {
            n = (NodeEntry)it.next();
            index--;
        }
        return n;
    }
    
    public LinkedList<NodeEntry> getAsLinkedList()
    {
        LinkedList<NodeEntry> neighboursList = new LinkedList<NodeEntry>();
        
        Iterator it = list.values().iterator();
        while(it.hasNext()) 
            neighboursList.add((NodeEntry)it.next());
        
        return neighboursList;
    }
    
    public int size()
    {
        return size;
    }
    
    public double getFurthestNodeDistanceNCS(NCS_Location2D fromLocation)
    {
        LinkedList<NodeEntry> nodeList = getAsLinkedList();
        double max = 0;
        for(NodeEntry entry:nodeList)
        {
            if (max < fromLocation.distanceTo(entry.getNCS_Location2D()))
                max = fromLocation.distanceTo(entry.getNCS_Location2D());
        }
        return max;
    }
    
    /* Returns the NodeEntry of the node that is closest to the targetLocation (NCS)*/
    public NodeEntry getClosestNodeToNCS(NCS_Location2D targetLocation)
    {
        LinkedList<NodeEntry> nodeList = getAsLinkedList();
        NodeEntry bestMatch = null;
        double min = 1;
        for (NodeEntry entry: nodeList)
        {
            if (min > entry.getNCS_Location2D().distanceTo(targetLocation))
            {
                min = entry.getNCS_Location2D().distanceTo(targetLocation);
                bestMatch = entry;
            }
        }
        return bestMatch;
    }
    
    // This function analyzes all the nodes in the list and returns the furthest distance from the reference location
    public double maxCoverage(Location2D referenceLocation, LocationContext referenceLocationContext)
    {
        double coverage = 0;
        // calculate maximum coverage of this node
        for (NodeEntry node: getAsLinkedList())
        {
            double distance = referenceLocation.distanceTo(node.getNCS_Location2D().fromNCS(referenceLocationContext));
            if (coverage < distance)
                coverage = distance;
        }
        return coverage;
    }
}
