/*
 * RoutingTable.java
 *
 * Created on June 14, 2006, 2:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.routing.heartbeat;


import java.util.LinkedList;
import java.util.ListIterator;
import jist.swans.mac.MacAddress;
import jist.swans.net.NetAddress;
/**
 *
 * @author Oliviu Ghica
 *
 * The routing table has two columns:
 *    Destination Node IP Address |  NextHopNode MAC address
 *    ------------------------------------------------------
 *              ...               |           ....
 *              ...               |           ....
 *
 *  We model each column as a LinkedList, and we match the proper destination node entry with it's stored NextHopNode through the index in the list
 */

public class RoutingTable {
    private LinkedList DestColumn;
    private LinkedList NextHopColumn;
    
    /** Creates a new instance of RoutingTable */
    public RoutingTable() {
        DestColumn    = new LinkedList();
        NextHopColumn = new LinkedList();
    }
    
    public boolean existDestinationEntry(NetAddress destIP)
    {
        return DestColumn.contains(destIP);
    }
    
    public void add(NetAddress destIP, MacAddress nextHopMac, int nodeId)
    {
        if (existDestinationEntry(destIP))
            System.out.println("[WARNING: RoutingTable @ node " +nodeId+"] - Routing Entry " + destIP + " coming from " + nextHopMac + " already exist for destination node! Consider erasing the existing entries first!");
        if (destIP == null || nextHopMac == null)
        {
            System.out.println("[WARNING: RoutingTable @ " + nodeId+" ] - NULL entries are not allowed in the RoutingTable!");
            return;
        }
        DestColumn.add(destIP);
        NextHopColumn.add(nextHopMac);
    }
    
    public void update(NetAddress destIP, MacAddress nextHopMac, int nodeId)
    {
         if (!existDestinationEntry(destIP))
         {
            System.out.println("[WARNING: RoutingTable @ node " +nodeId+"] - Update required on non-existing entry!");
            return;
         }
         eraseDestinationEntries(destIP);
         add(destIP, nextHopMac, nodeId);
    }
    
    public void replaceNextHopMac(MacAddress oldAddress, MacAddress newAddress)
    {
        ListIterator itNHop = NextHopColumn.listIterator(0);
        int index = 0;
        while (itNHop.hasNext())
        {
            if ((MacAddress)itNHop.next() == oldAddress)
                NextHopColumn.set(index, newAddress);
            index++;                
        }
    }
    
    public void deleteNextHopMac(MacAddress deleteableAddress)
    {
        ListIterator itNHop = NextHopColumn.listIterator(0);
        int index = 0;
        while(itNHop.hasNext())
        {
            if ((MacAddress)itNHop.next() == deleteableAddress)
            {
                NextHopColumn.remove(index);
                DestColumn.remove(index);
                itNHop = NextHopColumn.listIterator(0);
                index  = 0;
            }
            else
                index++;
        }
    }
    
    /* Erases all entries in the table that have the destination field = destIP */
    public void eraseDestinationEntries(NetAddress destIP)
    {
        int index;
        do
        {
            index = DestColumn.indexOf(destIP);     /* Will return -1 if destIP is not found */
            if (index != -1)
            {
                DestColumn.remove(index);
                NextHopColumn.remove(index);
            }
        }
        while (index != -1);
    } 
    
    public MacAddress getNextHop(NetAddress destIP)
    {
        int index = DestColumn.indexOf(destIP);     /* Will return -1 if destIP is not found */
        if (index != -1)
            return (MacAddress)NextHopColumn.get(index);
        else
            return null;
    }
    
    /* Returns a copy of this table. It does not make a copy of Address objects */
    public RoutingTable getCopy()
    {
        RoutingTable routingTableCopy = new RoutingTable();
        ListIterator itDest = DestColumn.listIterator(0);
        ListIterator itNHop = NextHopColumn.listIterator(0);
        
        while (itDest.hasNext() && itNHop.hasNext())
            routingTableCopy.add((NetAddress)itDest.next(), (MacAddress)itNHop.next(), -1);
        
        return routingTableCopy;
    }
    
    public NetAddress getDestByIndex(int index)
    {
        if (index < 0 || index >= DestColumn.size())
            return null;
        return (NetAddress)DestColumn.get(index);
    }
    
    public MacAddress getNextHopByIndex(int index)
    {
        if (index < 0 || index >= NextHopColumn.size())
            return null;
        return (MacAddress)NextHopColumn.get(index);
    }
    
    public int getSize()
    {
        return NextHopColumn.size();
    }
    
    public void print()
    {
        System.out.println(" DestIP       |   NextHopIP");
        ListIterator itDest = DestColumn.listIterator(0);
        ListIterator itNHop = NextHopColumn.listIterator(0);
        while(itNHop.hasNext() && itDest.hasNext())
            System.out.println(" " + itDest.next() + "  |  " + itNHop.next());
        System.out.flush();
    }
}
