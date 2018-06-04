/*
 * MultiTreeRouting.java
 *
 * Created on March 20, 2007, 11:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.routing.heartbeat;


import java.util.LinkedList;
import jist.swans.misc.Message;
import jist.swans.net.NetAddress;
import jist.swans.net.NetInterface;
import jist.swans.net.NetMessage;
import jist.swans.mac.MacAddress;
import jist.swans.Constants;
import sidnet.colorprofiles.ColorProfileGeneric;
import sidnet.core.misc.NodeEntry;


import sidnet.core.gui.*;

import jist.runtime.JistAPI;
import jist.swans.app.AppInterface;
import jist.swans.route.RouteInterface;
import org.w3c.dom.NodeList;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.Node;
import sidnet.core.misc.NodesList;
import sidnet.core.misc.Reason;

public class HeartbeatProtocol implements RouteInterface.HeartbeatProtocol{

    // DEBUG
    private static final boolean DEBUG = false;
    private static final int DEBUG_NODE_ID = -1;
    
    private NetAddress localAddr;
    
    // entity hookup 
    /** Network entity. */
    private NetInterface netEntity;
    
    /** Self-referencing proxy entity. */
    private RouteInterface.HeartbeatProtocol self; 
    
    private AppInterface appInterface;
    
    private Node myNode;
    
    private RoutingTable routingTable;
    
    private boolean wakeAndBeatStarted = false;
    private boolean unregistered = false;
    
    private long beatInterval;
    
    public static TopologyGUI topologyGUI = null;
    
    /** Creates a new instance of HeartbeatProtocol */
    public HeartbeatProtocol(NetAddress localAddr, Node myNode, PanelContext hostPanelContext, long beatInterval) {
        this.localAddr = localAddr;
        this.myNode = myNode;
      
        self = (RouteInterface.HeartbeatProtocol)JistAPI.proxy(this, RouteInterface.HeartbeatProtocol.class);
        this.beatInterval = beatInterval;
    }
    
    // *** USER CODE FUNCTIONS *** //
    public void dropNotify(Message msg, MacAddress nextHopMac, Reason reason) {
        if (reason == Reason.NET_QUEUE_FULL)
            System.out.println("WARNING<Heartbeat Protocol>: Net Queue full");
        if (reason == Reason.UNDELIVERABLE || reason == Reason.MAC_BUSY) {
            System.out.println("[WARNING][HeartbeatProtocol]: Cannot relay packet + " + msg + " to the destination node " + nextHopMac);
             
            // remove the node from the neighboring list
            myNode.neighboursList.remove(nextHopMac.hashCode());
        }
    }
    
     public void peek(NetMessage msg, MacAddress lastHopMac) {
        // DO NOTHING
     }
    
     public void send(NetMessage msg) 
     {
        // DO NOTHING
     }
     
     
    public synchronized void wakeAndBeat(long beatInterval, boolean wakeAndBeatStarted)
    {   
        if (DEBUG &&
            (DEBUG_NODE_ID < 0 ||
             DEBUG_NODE_ID == myNode.getID()))
             System.out.println("[DEBUG][" + JistAPI.getTime() + "][HeartbeatPrototocol.wakeAndBeat(_,_)] at node " + myNode.getID());
        if (myNode.getEnergyManagement().getBattery().getPercentageEnergyLevel() <= 1 && unregistered)
        {
            myNode.getNodeGUI().colorCode.mark(new ColorProfileGeneric(), ColorProfileGeneric.DEAD, ColorProfileGeneric.FOREVER);
            return;
        }
        if (myNode.getEnergyManagement().getBattery().getPercentageEnergyLevel() < 5 && !unregistered)
        {
            unregistered = true;
            MessageHeartbeat messageHeartbeat = new MessageHeartbeat(MessageHeartbeat.UNREGISTER);
            myNode.getNodeGUI().colorCode.mark(new ColorProfileGeneric(), ColorProfileGeneric.TRANSMIT, 200);
            JistAPI.sleepBlock(Constants.random.nextInt(100) * 100 * Constants.MILLI_SECOND); 
            netEntity.send(messageHeartbeat, NetAddress.ANY, Constants.NET_PROTOCOL_HEARTBEAT, Constants.NET_PRIORITY_NORMAL, (byte)100);  // TTL 100'
            
            return;
        }
        
        topologyGUI.removeGroup(0);
        topologyGUI.removeGroup(1);
        
        if (!this.wakeAndBeatStarted || wakeAndBeatStarted)
        {   
            wakeAndBeatStarted = true;
            JistAPI.sleepBlock(beatInterval);
            if (myNode.getEnergyManagement().getBattery().getPercentageEnergyLevel() < 20)
                beatInterval = 5 * Constants.MINUTE;
            ((RouteInterface.HeartbeatProtocol)self).wakeAndBeat(beatInterval, true);
        }
    }
     
    /* Receive a message from the network layer */
    public void receive(Message msg, NetAddress src, MacAddress lastHop, byte macId, NetAddress dst, byte priority, byte ttl) 
    {   
        if (DEBUG) System.out.println("Node " + myNode.getID() + " received message from " + src); 
        
        if (myNode.getEnergyManagement().getBattery().getPercentageEnergyLevel() <= 1)
        {
            myNode.getNodeGUI().colorCode.mark(new ColorProfileGeneric(), ColorProfileGeneric.DEAD, ColorProfileGeneric.FOREVER);
            return;
        }
        
        
        
        // Pass all the non-heartbeat specific messages straight to the App layer
        if (! (msg instanceof MessageHeartbeat))
        {
            System.out.println("<WARNING>[HeartbeatProtocol] : receiving packets that are not primarely designed for the Heartbeat Protocol. Make sure you transmit your message to the proper routing algorithm (implementation)");
            return;
        }
         
        NodeEntry newEntry = new NodeEntry( lastHop,
                                            src,
                                            ((MessageHeartbeat)msg).getNCS_Location()
                                          );
        if (((MessageHeartbeat)msg).isUnregistering())
            myNode.neighboursList.remove(src);
        else
            if (!myNode.neighboursList.contains(lastHop))
                myNode.neighboursList.add(src, newEntry);
        myNode.getNodeGUI().colorCode.mark(new ColorProfileGeneric(), ColorProfileGeneric.RECEIVE, 200);
        if (!wakeAndBeatStarted)
            wakeAndBeat(beatInterval, wakeAndBeatStarted);
    }
    
    public RouteInterface.HeartbeatProtocol getProxy()
    {
        return self;
    }
   
    
    public void setNetEntity(NetInterface netEntity)
    {
        if(!JistAPI.isEntity(netEntity)) throw new IllegalArgumentException("expected entity");
        if(this.netEntity!=null) throw new IllegalStateException("net entity already set");
        
        this.netEntity = netEntity;
    }
   
    
   public void start()
   {
        //DO NOTHING
   }
}