/*
 * DeadEndListEntry.java
 *
 * Created on July 29, 2005, 11:39 AM
 */

/**
 *
 * @author  Oliviu Ghica
 */
package sidnet.core.misc;

import jist.runtime.JistAPI;
import jist.swans.net.NetAddress;

public class DeadEndListEntry {
    public NetAddress nodeIP;         // The IP of the node that will enter in the list
    long packetID;             // The ID for which the node is entered in the list
    long timeStamp;             // The Time at which the node entered the list. 
    NetAddress destIP;          // The Destination of the packet that has packetID entered
    int status;                 // '0' - Entry is in the Monitoring/History Mode
                                // '1' - Entry is Blocked/Active in the DeadEndList
    
    /** Creates a new instance of DeadEndListEntry */
    public DeadEndListEntry(NetAddress nodeIP, long packetID, NetAddress destIP) {
        this.nodeIP       = nodeIP;
        this.packetID     = packetID;
        timeStamp         = JistAPI.getTime();
        this.destIP       = destIP;
        status            = 0;
    }
    
    public void Block(long time){ status = 1; timeStamp = time; }
}
