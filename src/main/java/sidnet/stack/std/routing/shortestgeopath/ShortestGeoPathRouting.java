/*
 * ShortestGeographicalPathRouting.java
 *
 * Created on April 15, 2008, 11:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.routing.shortestgeopath;

import java.util.LinkedList;
import jist.runtime.JistAPI;
import jist.swans.Constants;
import sidnet.core.interfaces.AppInterface;
import jist.swans.mac.MacAddress;
import jist.swans.misc.Message;
import jist.swans.net.NetAddress;
import jist.swans.net.NetInterface;
import jist.swans.net.NetMessage;
import jist.swans.route.RouteInterface;
import sidnet.colorprofiles.ColorProfileGeneric;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.Node;
import sidnet.core.misc.NodeEntry;
import sidnet.core.misc.Reason;
import sidnet.core.misc.Region;

/**
 *
 * @author Oliviu C. Ghica, Northwestern University
 */
public class ShortestGeoPathRouting implements RouteInterface {
    public static final byte ERROR = -1;
    public static final byte SUCCESS = 0;

    private final Node myNode; // The SIDnet handle to the node representation 
    
    // entity hook-up (network stack)
    /** Network entity. */
    private NetInterface netEntity;
   
    /** Self-referencing proxy entity. */
    private RouteInterface self; 
    
    /** The proxy-entity for this application interface */
    private AppInterface appInterface;
    
    // DO NOT MAKE THIS STATIC
    private ColorProfileGeneric colorProfileGeneric = new ColorProfileGeneric(); 
    
    /** Creates a new instance of ShortestGeographicalPathRouting
     *
     * @param Node    the SIDnet node handle to access 
     * 				  its GUI-primitives and shared environment
     */
    public ShortestGeoPathRouting(Node myNode) {
        this.myNode = myNode;
        
        /** Create a proxy for the application layer of this node */
        self = (RouteInterface)JistAPI.proxy(this, RouteInterface.class);
    }
    
    /** SWANS legacy. We no longer enable this */
    public void peek(NetMessage msg, MacAddress lastHopMac) {
       // no peeking
    }
    
    /** 
     *  Receive a message from the network layer
     *  This method is typically called when this node is the ultimate 
     *  	destination of an incoming data-message (the sink) 
     * 
     * @param Message   the incomming message
     * @param NetAddress the original source of the message
     * @param MacAddress the MAC address 1-hop neighbor from which this 
     * 					 nodes received this message
     * @param macId     the macId interface through which 
     * 					this message was received
     * @param NetAddress the IP address of the ultimate node destination (this)
     * @param priority  the priority of the incoming message
     * @param ttl    Time To Leave
     */
    public void receive(Message msg, 
    					NetAddress src, 
    					MacAddress lastHop, 
    					byte macId, NetAddress dst, byte priority, byte ttl) 
    {  
        if (myNode.getEnergyManagement()
        		  .getBattery()
        		  .getPercentageEnergyLevel()< 2)
            return;
        
        // Provide a basic visual feedback on the fact that 
        //this node has received a message */
        myNode.getNodeGUI().colorCode.mark(colorProfileGeneric,
        								   ColorProfileGeneric.RECEIVE, 20);  
        
        // A message may come in a format that you define based
        // on your implementation needs
        // You must extract that format and act upon
        if (msg instanceof SGPWrapperMessage) {
        	SGPWrapperMessage msgNZ = (SGPWrapperMessage)msg;
            sendToAppLayer(msgNZ.getPayload(), null);
        }
         
        // otherwise, it is a format not recognized by SGP, so we ignore it.
    }
    
     /**
      * Send a message
      * This method is being called when a message, 
      * coming from either the application layer or the mac layer,
      * needs to be forwarded
      *
      * @param  NetMessage  the 'NetMessage' wrapped Message
      */
     public void send(NetMessage msg) 
     {        
    	// process only if there are energy reserves
        if (myNode.getEnergyManagement()
        		  .getBattery()
        		  .getPercentageEnergyLevel()< 2)
            return;
        
        // update visuals        
    	myNode.getNodeGUI().colorCode.mark(colorProfileGeneric,
    									   ColorProfileGeneric.RECEIVE, 2000);
      
        // If this message comes from App Layer
        if (!(((NetMessage.Ip)msg).getPayload() instanceof SGPWrapperMessage))
        	return; // ignore non-specific messages
        
        // extract message
        Location2D targetLocation = null;
        SGPWrapperMessage msgSGP 
        	= (SGPWrapperMessage)((NetMessage.Ip)msg).getPayload();

        // Seek destination information (Location or Region based)
        if (msgSGP.getTargetRegion() != null)
        	targetLocation = extractClosestVertex(msgSGP.getTargetRegion());
                
        if (msgSGP.getTargetLocation() != null)
        	targetLocation = msgSGP.getTargetLocation();
        
        if (targetLocation != null)	// TODO FIXME occasionally this turns to be null
        	handleWithTargetLocation(targetLocation, msg);
     }
     
     private void handleWithTargetLocation(Location2D targetLocation,
    		 								NetMessage msg) {
    	 SGPWrapperMessage msgSGP 
    	 	= (SGPWrapperMessage)((NetMessage.Ip)msg).getPayload();
    	 
    	 // Retrieve the IP address of the 1-hop neighbor
    	 // closest to the area of interest */
         NetAddress nextHopIP = getThroughShortestPath(targetLocation);
         
    	 // If there is no node closer to the area of interest than this node, 
    	 // then this node will get the message
         if (nextHopIP.hashCode() == myNode.getIP().hashCode())
        	 sendToAppLayer(msgSGP.getPayload(), null);
         else { // keep forwarding
        	  // first, make a copy of the message
              NetMessage.Ip copyOfMsg 
              	= new NetMessage.Ip(msgSGP,
					   ((NetMessage.Ip)msg).getSrc(),
                       ((NetMessage.Ip)msg).getDst(),
                       ((NetMessage.Ip)msg).getProtocol(),
                       ((NetMessage.Ip)msg).getPriority(), 
                       ((NetMessage.Ip)msg).getTTL(),
                       ((NetMessage.Ip)msg).getId(),
                       ((NetMessage.Ip)msg).getFragOffset());
              
             sendToLinkLayer(copyOfMsg, nextHopIP);
         }
     }
     
     /** Find the closest vertex. 
	  * A query may contain a region of interest.
	  * Since this is SGP routing, 
	  * we look for the closest vertex of that region 
	  */
     private Location2D extractClosestVertex(Region targetRegion) {    
         targetRegion.resetIterator();
             
         Location2D nextLoc 
         	= targetRegion.getNext()
         				  .convertTo(targetRegion.getLocationContext(),
         						     myNode.getLocationContext());;
         Location2D closestVertex = nextLoc;
            
         double distMin 
         	= nextLoc.distanceTo(myNode.getLocation2D());
             
         while (targetRegion.hasNext()) {
             nextLoc = targetRegion.getNext();
             Location2D actualLoc 
             	= nextLoc.convertTo(targetRegion.getLocationContext(),
             						myNode.getLocationContext());
             if (actualLoc.distanceTo(myNode.getLocation2D()) < distMin) {
                 distMin = actualLoc.distanceTo(myNode.getLocation2D());
                 closestVertex = actualLoc;
             }
         }         
         return closestVertex;
     }
    
     
    public void sendToAppLayer(Message msg, NetAddress src)
    {
    	// ignore if not enough energy
        if (myNode.getEnergyManagement()
        		  .getBattery()
        		  .getPercentageEnergyLevel()< 2)
            return;

        appInterface.receive(msg, src, null, (byte)-1,
        					 NetAddress.LOCAL, (byte)-1, (byte)-1);
    }
    
    public byte sendToLinkLayer(NetMessage.Ip ipMsg, NetAddress nextHopDestIP)
    {
        if (myNode.getEnergyManagement()
        		  .getBattery()
        		  .getPercentageEnergyLevel()< 2)
            return 0;
     
        /*myNode.getSimManager().getSimGUI()
		  .getAnimationDrawingTool()
		 	  .animate("ExpandingFadingCircle",
				       myNode.getNCS_Location2D());*/         
        
        if (myNode.getID() == 164)
            System.out.println("route packet to " + nextHopDestIP);

        if (nextHopDestIP == null)
            System.err.println("NULL nextHopDestIP");
        if (nextHopDestIP == NetAddress.ANY)
            netEntity.send(ipMsg, Constants.NET_INTERFACE_DEFAULT, MacAddress.ANY);
        else
        {
            NodeEntry nodeEntry = myNode.neighboursList.get(nextHopDestIP);
            if (nodeEntry == null)
            {
                 System.err.println("Node #" + myNode.getID() + ": Destination IP (" + nextHopDestIP + ") not in my neighborhood. Please re-route! Are you sending the packet to yourself?");
                 System.err.println("Node #" + myNode.getID() + "has + " + myNode.neighboursList.size() + " neighbors");
                 new Exception().printStackTrace();
                 return ERROR; 
            }
            MacAddress macAddress = nodeEntry.mac;
            if (macAddress == null)
            {
                 System.err.println("Node #" + myNode.getID() + ": Destination IP (" + nextHopDestIP + ") not in my neighborhood. Please re-route! Are you sending the packet to yourself?");
                 System.err.println("Node #" + myNode.getID() + "has + " + myNode.neighboursList.size() + " neighbors");
                 return ERROR;
            }
            myNode.getNodeGUI().colorCode.mark(colorProfileGeneric, ColorProfileGeneric.TRANSMIT, 2);
            netEntity.send(ipMsg, Constants.NET_INTERFACE_DEFAULT, macAddress);
        }
        
        return SUCCESS;
    }
   
       
     public NetAddress getThroughShortestPath(Location2D destLocation) {        
        double closestDist = myNode.getLocation2D().distanceTo(destLocation);
        NetAddress closestNode = myNode.getIP();
        LinkedList<NodeEntry> neighboursLinkedList 
        	= myNode.neighboursList.getAsLinkedList();
        
        for(NodeEntry nodeEntry: neighboursLinkedList) {
             /* Get the location coordinates of the neighbour 'i' */
            double neighbourDistance 
            	= nodeEntry.getNCS_Location2D()
            	  		   .fromNCS(myNode.getLocationContext())
            			   .distanceTo(destLocation);
            if ( neighbourDistance < closestDist ) {
                closestDist = neighbourDistance;
                closestNode = nodeEntry.ip;
            }
        }
        
        return closestNode;
    }   
       
     
    // *** USER CODE FUNCTIONS *** //
    public void dropNotify(Message msg, MacAddress nextHopMac, Reason reason) {
    	System.out.println("Packet dropped notify");
    	if (reason == Reason.PACKET_SIZE_TOO_LARGE) {
    		System.out.println("WARNING: Packet size too large - unable to transmit");
    		throw new RuntimeException("Packet size too large - unable to transmit");
    	}
        if (reason == Reason.NET_QUEUE_FULL) {
            System.out.println("WARNING: Net Queue full");
            throw new RuntimeException("Net Queue Full");
        }
        if (reason == Reason.UNDELIVERABLE || reason == Reason.MAC_BUSY)
            System.out.println("WARNING: Cannot relay packet to the destination node " + nextHopMac);
        
        // wait 5 seconds and try again
        JistAPI.sleepBlock(500 * Constants.MILLI_SECOND);
        //netEntity.send((NetMessage.Ip)msg, Constants.NET_INTERFACE_DEFAULT, nextHopMac);
        this.send((NetMessage)msg);
    }

       
   /* **************************************** *
    * SWANS network's stack hook-up interfaces *
    * **************************************** */
    
    public RouteInterface getProxy()
    {
        return self;
    }
   
    
    public void setNetEntity(NetInterface netEntity)
    {
        if(!JistAPI.isEntity(netEntity)) throw new IllegalArgumentException("expected entity");
        if(this.netEntity!=null) throw new IllegalStateException("net entity already set");
        
        this.netEntity = netEntity;
    }

    
    public void setAppInterface(AppInterface appInterface)
    {
        this.appInterface = appInterface;
    }
        
   public void start()
   {
        //nothing
   }
    
}
