/*
 * DummyRoute.java
 *
 * Created on July 8, 2008, 5:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.routing.dummyroute;

import jist.runtime.JistAPI;
import jist.swans.Constants;
import sidnet.colorprofiles.ColorProfileGeneric;
import sidnet.core.interfaces.AppInterface;
import jist.swans.mac.MacAddress;
import jist.swans.misc.Message;
import jist.swans.net.NetAddress;
import jist.swans.net.NetInterface;
import jist.swans.net.NetMessage;
import jist.swans.route.RouteInterface;
import sidnet.core.misc.Node;
import sidnet.core.misc.Reason;

/**
 *
 * @author Oliver
 */
public class DummyRoute implements RouteInterface
{
    // DEBUG
    private static final boolean DEBUG = false;
    
    // INTERNALS (do not change):  entity hookup 
    /** Network entity. */
    private NetInterface netEntity;
    /** Self-referencing proxy entity. */
    private RouteInterface self; 
    private AppInterface appInterface;
    // INTERNALS//
    
    private Node myNode;
    
    private long packetCount = 0;
    
    // for tests in Mac802_15_4test.java
    public int contorUndeliverable = 0;
    public MacAddress undeliverableToMacAddress = null;
    
    private ColorProfileGeneric colorProfileGeneric = new ColorProfileGeneric(); // DO NOT MAKE THIS STATIC
    
    /**
     * Notification mechanism for packet dropped due to various "Reasons"
     *
     * 
     */
    // *** USER CODE FUNCTIONS *** //
    public void dropNotify(Message msg, MacAddress nextHopMac, Reason reason) {
        if (reason == Reason.NET_QUEUE_FULL) {
            System.out.println("WARNING: Net Queue full at node #" + myNode.getID());
            throw new RuntimeException("Net Queue full"); //???
        }
        if (reason == Reason.UNDELIVERABLE) {
            if (DEBUG)
                System.out.println("WARNING: Cannot relay packet to the destination node " + nextHopMac);
            contorUndeliverable++;
            undeliverableToMacAddress = nextHopMac;
        }       
    }
      
     /** 
      * This function is called when the App Layer transmits a packet (other than broadcast (NetAddress.ANY) packet) 
      * or a message is received from the MAC layer, but, since this node is not the FINAL destination of the message, needs to be forwarded
      * The mechanism is explained in the SIDnet manual. On short: When the app-layer sends a packet down (or link layer sends it UP) to Network layer (unicast)
      * the network layer calls the send(NetMessage) of the routing protocol "indexed" by the packet. Here you decide how to route or what
      * to do with the packet
      * If it is a broadcast packet, the Network layer will immediatelly push the packet DOWN to the Link layer, bypassing the routing protocol
      */
     public void send(NetMessage msg) {
        //  ... write your code here if you want to make DummyRoute smarter.
        // For now, it has no clue what to do with the packets, so it assumes (dumbly) that the dest address is 
        // right in this node's neighborhood. It just forwards the message to MAC.
        // However, broadcast-packets are sent to mac by default as it bypasses the send function
        
        if (DEBUG) System.out.println("[DEBUG][DummyRoute].send()");
        // Since this is a really DUMB implementation, the routing layer does nothing for now, just forwards the message down ... 
        // Your code must be put here 
        sendToLinkLayer((NetMessage.Ip)msg, ((NetMessage.Ip)msg).getDst());
        return; 
     }
     
     /** 
      * Receive a message from the Link layer 
      * This function is called whenever the link layer receives a message whose final destination is THIS node. 
      */
     public void receive(Message msg, NetAddress src, MacAddress lastHop, byte macId, NetAddress dst, byte priority, byte ttl) {
          // Since this is a really DUMB implementation, the routing layer does nothing for now, just forwards the message UP to the app layer ... change this.
          // Your code must be put here 
         
    	 myNode.getNodeGUI().colorCode.mark(colorProfileGeneric, ColorProfileGeneric.RECEIVE, 20);  
    	 
         NetAddress myParent = new NetAddress(lastHop.hashCode());
         
         if (DEBUG)
        	 System.out.println("[DEBUG][#" + myNode.getID() +"][DummyRoute.receive()] - packet number (" + (++packetCount) + ")");
         
         sendToAppLayer(msg, src);
     }
     
     public void peek(NetMessage msg, MacAddress lastHopMac) {
        // NOT AVAILABLE ANYMORE. YOU CANNOT PEEK PACKETS. DISREGARD. ONLY FOR BACKWARDS COMPATIBILITY
     }
          
    /** convenience function to send data DOWN ("to link layer") */ 
    public byte sendToLinkLayer(NetMessage.Ip ipMsg, NetAddress nextHopDestIP) {
        if (DEBUG) System.out.println("[DEBUG][DummyRoute].sendToLinkLayer()");
        if (nextHopDestIP == null)
            System.err.println("NULL nextHopDestIP");
        
        //System.out.println("time = " + JistAPI.getTime());
        if (nextHopDestIP == NetAddress.ANY)
            netEntity.send(ipMsg, Constants.NET_INTERFACE_DEFAULT, MacAddress.ANY);
        else
            netEntity.send(ipMsg, Constants.NET_INTERFACE_DEFAULT, new MacAddress(nextHopDestIP.hashCode()));  
        
        return 0;
    }
     
    
    /** convenience function to send data UP ("to app layer") */ 
    public void sendToAppLayer(Message msg, NetAddress src) {
        //System.out.println("Node #" + myNode.getID() + ": Send to app layer");
        appInterface.receive(msg, src, new MacAddress(src.hashCode()), (byte)-1, NetAddress.LOCAL, (byte)-1, (byte)-1);
    }
    
   
    
    
    
    
    
    
    
    
    
    
    // ******* SETTING UP FUNCTIONS (INTERNALS) - do not change *********
    /** Creates a new instance of DummyRoute */
    public DummyRoute(Node myNode) {
        this.myNode = myNode; // this is the way (API) to the node's GUI
      
        /** internals, do not change */
        self = (RouteInterface)JistAPI.proxy(this, RouteInterface.class);
    }
    
    /** internals - for hooking up with App-Layer */
    public void setAppInterface(AppInterface appInterface) {
        this.appInterface = appInterface;
    }
    
    /** internals - for hooking up with the Network-Layer */
    public void setNetEntity(NetInterface netEntity)
    {
        if(!JistAPI.isEntity(netEntity)) throw new IllegalArgumentException("expected entity");
        if(this.netEntity!=null) throw new IllegalStateException("net entity already set");
        
        this.netEntity = netEntity;
    }
    
    /** internals */
    public RouteInterface getProxy() {
        return self;
    }
    // *** END OF SETTING UP FUNCTIONS *** //
    
}
