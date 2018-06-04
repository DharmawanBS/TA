/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sidnet.stack.users.dharmawan_route.app;

import java.util.LinkedList;
import java.util.List;
import jist.runtime.JistAPI;
import jist.swans.Constants;
import jist.swans.mac.MacAddress;
import jist.swans.misc.Message;
import jist.swans.net.NetAddress;
import jist.swans.net.NetInterface;
import sidnet.colorprofiles.ColorProfileGeneric;
import sidnet.core.gui.TopologyGUI;
import sidnet.core.interfaces.AppInterface;
import sidnet.core.interfaces.CallbackInterface;
import sidnet.core.interfaces.ColorProfile;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.Node;
import sidnet.core.query.Query;
import sidnet.core.simcontrol.SimManager;
import sidnet.stack.std.routing.heartbeat.MessageHeartbeat;
import sidnet.stack.std.routing.shortestgeopath.SGPWrapperMessage;
import sidnet.stack.users.dharmawan_route.routing.MessagePoolDataValue;
import sidnet.stack.users.dharmawan_route.routing.ProtocolMessageWrapper;
import sidnet.utilityviews.statscollector.StatEntry_EnergyLeftPercentage;
import sidnet.utilityviews.statscollector.StatsCollector;

/**
 *
 * @author dharmawan
 */
public class AppLayer implements AppInterface, CallbackInterface {
    private final Node myNode; // The SIDnet handle to the node representation 
    
    public static TopologyGUI topologyGUI = null;
    
    /** network entity. */ 
    private NetInterface netEntity;
    
    /** self-referencing proxy entity. */
    private Object self;
    
    /** flag to mark if a heartbean protocol has been initialized */
    private boolean heartbeatInitiated = false;
    
    private static boolean flag = false;
    
    private boolean signaledUserRequest = false;
    
    private final short routingProtocolIndex;
    
    private StatsCollector stats = null;
    
    private boolean startedSensing = false;
    
    // do not make this static
    private ColorProfileGeneric colorProfileGeneric = new ColorProfileGeneric(); 
    
    /** Creates a new instance of the AppP2P */
    public AppLayer(Node myNode, 
    					short routingProtocolIndex,
    					StatsCollector stats)
    {
        this.self = JistAPI.proxyMany(this, new Class[] { AppInterface.class });
        this.myNode = myNode;
        
        // To allow the upper layer (user's terminal) 
        //to signal any updates to this node */
        this.myNode.setAppCallback(this);
  
        this.routingProtocolIndex = routingProtocolIndex;

        this.stats = stats;
    }
    
    
    
    /* 
     * This is your main execution loop at the Application Level. Here you design the application functionality. It is simulation-time driven
     * The first call to this function is made automatically upon starting the simulation, from the Driver
     */
    public void run(String[] args) 
    {       
         /* At time 0, set the simulation speed to x1000 to get over the heartbeat node identification phase fast */
          if (JistAPI.getTime() == 0)  // this is how to get the simulation time, by the way
               myNode.getSimControl().setSpeed(SimManager.X1000);
     
          //if (myNode.getID() != 2) return;  // ???
          /* This is a one-time phase. We'll allow a one-hour warm-up in which each node identifies its neighbors (The Heartbeat Protocol) */
          if (JistAPI.getTime() > 0 && !heartbeatInitiated)
          {
                //System.out.println("["+(myNode.getID() * 5 * Constants.MINUTE) +"] Node " + myNode.getID() + " broadcasts a heartbeat message");
               
                myNode.getNodeGUI().colorCode.mark(colorProfileGeneric, ColorProfileGeneric.TRANSMIT, 500); 
               
                /* To avoid all nodes to transmit in the same time */
                JistAPI.sleepBlock(myNode.getID() * 5 * Constants.SECOND); 
                
                MessageHeartbeat msg = new MessageHeartbeat();
                msg.setNCS_Location(myNode.getNCS_Location2D());
                               
                myNode.getNodeGUI().colorCode.mark(colorProfileGeneric, ColorProfileGeneric.TRANSMIT, 500); 
                
                /* Send the heartbeat message. The heartbeat protocol will handle these messages and continue according to the protocol*/
                netEntity.send(msg, NetAddress.ANY, Constants.NET_PROTOCOL_HEARTBEAT, Constants.NET_PRIORITY_NORMAL, (byte)100);  // TTL 100
                
                heartbeatInitiated = true;
          }
         
         /* Wait 1 hour for the heartbeat-bootstrap to finish, then slow down to allow users to interact in real-time*/
         if (JistAPI.getTime()/Constants.HOUR >= 1 && !flag) {
              myNode.getSimControl().setSpeed(SimManager.X1);
              flag = true;
         }
          
          if (JistAPI.getTime()/Constants.MINUTE < 60) {
              /*if (myNode.getID() == 0)
              {
                  topologyGUI.addLink(new NCS_Location2D(0,0), new NCS_Location2D(1,1), 0 , Color.blue);
                  topologyGUI.addLink(3, 5, 0, Color.green);
              }*/
              
               JistAPI.sleep(5000*Constants.MILLI_SECOND);  // 5000 milliseconds
              
              /* this is to schedule the next run(args) */
              ((AppInterface)self).run(null);  /* !!! Pay attention to the way we re-run the app-layer code. We don't use a while loop, but rather let JiST call this again and again */
              
              return;
          }
      }
    
    
    public void run() {
        //Location currentLoc = field.getRadioData(new Integer(nodenum)).getLocation();
        JistAPI.sleep(2 + (long)((1000-2)*Constants.random.nextFloat())); 
        run(null);
    }
    
    
    
    
    /* Sensing the phenomena is most likely a periodic process. We wrote a procedure to do so.
     * Since the sensing() takes place at various simulation-time, this function should be called through a proxy reference, rather than directly to avoid
     * an infinite starvation loop */
      public void sensing(List params)
      {
           long samplingInterval  = (Long)params.get(0);
           long endTime           = (Long)params.get(1);
           int  queryId           = (Integer)params.get(2);
           long sequenceNumber    = (Long)params.get(3);
           NetAddress sinkAddress = (NetAddress)params.get(4);
           Location2D sinkLocation= (Location2D)params.get(5);
                     
           JistAPI.sleepBlock(samplingInterval);
        
           double sensedValue = myNode.readAnalogSensorData(0);
           
           myNode.getNodeGUI().colorCode.mark(colorProfileGeneric,ColorProfileGeneric.SENSE, 5);
           
           MessageDataValue msgDataValue = new MessageDataValue(sensedValue,queryId,sequenceNumber,myNode.getID());
           
           
           //stats.markPacketSent("DATA", sequenceNumber);
           
           //Wrap pesan ke protokol pengiriman
           ProtocolMessageWrapper msgValue 
           	= new ProtocolMessageWrapper(msgDataValue, sinkLocation,
           							0, JistAPI.getTime());
           
           netEntity.send(msgValue, 
        		   		  sinkAddress,
        		   		  routingProtocolIndex,
        		   		  Constants.NET_PRIORITY_NORMAL, (byte)40); 
           
           stats.incrementValue("AV_Created", 1);
           
           myNode.getNodeGUI().colorCode.mark(colorProfileGeneric,
        		   							  ColorProfileGeneric.TRANSMIT, 5);
           
           if (JistAPI.getTime() < endTime)
           {
                sequenceNumber++;
                
                params.set(0, samplingInterval);
                params.set(1, endTime);
                params.set(2, queryId);
                params.set(3, sequenceNumber);
                params.set(4, sinkAddress);
                params.set(5, sinkLocation);
                
                // this is to schedule the next run(args). 
                //DO NOT use WHILE loops to do this, 
                // nor call the function directly. Let JiST handle it 
                ((AppInterface)self).sensing(params);
           } else {
               myNode.getSimControl().setSpeed(SimManager.PAUSED);
           }
      }
      
    /** Callback registered with the terminal,
     * The terminal will call this function whenever the user posts a new query or just closes the terminal window
     * <p>
     * You should inspect the myNode.localTerminalDataSet.getQueryList() to check for new posted queries that your node must act upon
     * Have a look at the TerminalDataSet.java for the available data that is exchanged between this node and the terminal
     */
    public void signalUserRequest()
    {
        /* We'll assume that the node through which the user has posted a query becomes a sink node */
        if (myNode.getQueryList().size() > 0 )
        {     
            Query query = ((LinkedList<Query>)myNode.getQueryList()).getLast();
            myNode.getNodeGUI().colorCode.mark(colorProfileGeneric, ColorProfileGeneric.SINK, ColorProfile.FOREVER); // to make easier to you to see the node you've posted the query through (the sink node)
          
            if (!query.isDispatched()) {        
                myNode.getNodeGUI().colorCode.mark(colorProfileGeneric, ColorProfileGeneric.SINK, ColorProfileGeneric.FOREVER);
                
                int[] rootIDArray = new int[1];
                rootIDArray[0] = myNode.getID();
                
                MessageQuery msgQuery = new MessageQuery(query);

                // wrap the MessageQuery as a SGP message
                /*SGPWrapperMessage msgSGP 
                	= new SGPWrapperMessage(msgQuery, query.getRegion(),
                							0, JistAPI.getTime());*/
                
                // wrap the MessageQuery as a SGP message
                String unikID = String.valueOf(myNode.getID()) + String.valueOf(JistAPI.getTime());

                ProtocolMessageWrapper msgSGP
                	= new ProtocolMessageWrapper(msgQuery, query.getRegion(),
                                                Long.parseLong(unikID), JistAPI.getTime());
                
                netEntity.send(msgSGP, 
                		       null/*unknown Dest IP, only its approx location*/,
                		       routingProtocolIndex /* (see Driver) */,
                		       Constants.NET_PRIORITY_NORMAL, (byte)100);                  
                
                stats.monitor(new StatEntry_EnergyLeftPercentage("REGION-" + String.valueOf(query.getRegion().getID()), StatEntry_EnergyLeftPercentage.MODE.AVG, query.getRegion(), StatEntry_EnergyLeftPercentage.TYPE.INCLUSION_REGION));
                            
                query.dispatched(true);
            }
        }
    }
    
    
    /**
     * Message has been received. 
     * This node must be the either the sink or the source nodes 
     */
    public void receive(Message msg, NetAddress src, MacAddress lastHop, byte macId, NetAddress dst, byte priority, byte ttl) 
    {   
        //System.out.println("receive app_layer");
        if (myNode.getEnergyManagement().getBattery().getPercentageEnergyLevel() < 5)
            return;
        
        //stats.incrementValue("AV_Received", 1);
        
        if (msg instanceof MessageQuery) { /* This is a source node. It receives the query request, and not it prepares to do the periodic sensing/sampling */
             MessageQuery msgQuery = (MessageQuery)msg;
             //System.out.println("Node " + myNode.getID() + " got query, processing query " + msgQuery.getQuery().getID());
         
             if (msgQuery.getQuery() != null) { /* a query init message */
                if (!startedSensing) { /* To avoid creating duplicated sensing tasks due to duplicated requests, which may happen */
                    myNode.getNodeGUI().colorCode.mark(colorProfileGeneric, ColorProfileGeneric.SOURCE, ColorProfileGeneric.FOREVER); 
                    
                    startedSensing = true;
                
                    LinkedList params = new LinkedList();
                    params.add(msgQuery.getQuery().getSamplingInterval());   /* sampling interval */
                    params.add(JistAPI.getTime()/Constants.MILLI_SECOND + msgQuery.getQuery().getEndTime()); /* endTime */
                    params.add(msgQuery.getQuery().getID());
                    params.add((long)0);
                    params.add(msgQuery.getQuery().getSinkIP());
                    params.add(msgQuery.getQuery()
                    		           .getSinkNCSLocation2D()
                    		           .fromNCS(myNode.getLocationContext()));
                    
                    JistAPI.sleepBlock(msgQuery.getQuery().getSamplingInterval());
                    
                    sensing(params);
                }
             }
             
             if (msg instanceof MessagePoolDataValue) {
                /*MessagePoolDataValue xmsg = (MessagePoolDataValue) msg;
                int minute = new Long(JistAPI.getTime() / Constants.MINUTE).intValue();
                int hour = minute / 60;
                minute = minute - (hour * 60);
                int second = new Long(JistAPI.getTime() / Constants.SECOND).intValue();
                second = second - ((minute * 60) + (hour * 3600));*/

                stats.updateCommonStats();
             }
        }
        
        // it is a data message, 
        // which means this node is the sink (consumer node)
        if (msg instanceof MessageDataValue) {  
        	 MessageDataValue msgData = (MessageDataValue)msg;  
             stats.markPacketReceived("DATA", msgData.sequenceNumber);
             myNode.getNodeGUI().setUserDefinedData1((int)msgData.dataValue);
             myNode.getNodeGUI().setUserDefinedData2((int)msgData.sequenceNumber);
             
             // Connecting a terminal to this node, at run time,
             // allows the user to visualize the result of the posted query 
             myNode.getNodeGUI()
                   .getTerminal()
                   .appendConsoleText(myNode.getNodeGUI().localTerminalDataSet,
            		                  "Sample #" +
            		                  msgData.sequenceNumber +
            		                  " | val: " + msgData.dataValue);             
        }
    }
 
    
    
    /* **************************************** *
     * SWANS network's stack hook-up interfaces *
     * **************************************** */
    
    /**
     * Set network entity.
     *
     * @param netEntity network entity
     */
     public void setNetEntity(NetInterface netEntity) {
       this.netEntity = netEntity;
     } 
    
     /**
      * Return self-referencing APPLICATION proxy entity.
      *
      * @return self-referencing APPLICATION proxy entity
      */
     public AppInterface getAppProxy() {
        return (AppInterface)self;
     } 
}
