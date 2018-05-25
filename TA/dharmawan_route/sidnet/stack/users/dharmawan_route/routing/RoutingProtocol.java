/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sidnet.stack.users.dharmawan_route.routing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import sidnet.core.gui.TopologyGUI;
import sidnet.core.misc.NCS_Location2D;
import sidnet.stack.users.dharmawan_route.colorprofile.ColorProfileDharmawan;
import sidnet.core.misc.Node;
import sidnet.core.misc.NodeEntry;
import sidnet.core.misc.Reason;
import sidnet.stack.users.dharmawan_route.app.MessageDataValue;
import sidnet.stack.users.dharmawan_route.app.MessageQuery;

/**
 *
 * @author dharmawan
 */
public class RoutingProtocol implements RouteInterface.DharmawanRoute {
    public static final byte ERROR = -1;
    public static final byte SUCCESS = 0;

    private static final long INTERVAL_TIMING_SEND = 5 * Constants.SECOND;
    
    private static final int LIMIT_PACKET_ID_SIZE = 500;

    private final Node myNode; // The SIDnet handle to the node representation 
    
    private boolean netQueueFULL = false;
    
    // entity hook-up (network stack)
    /** Network entity. */
    private NetInterface netEntity;
   
    /** Self-referencing proxy entity. */
    private RouteInterface self; 
    
    /** The proxy-entity for this application interface */
    private AppInterface appInterface;
    
    // DO NOT MAKE THIS STATIC
    private ColorProfileDharmawan colorProfile = new ColorProfileDharmawan(); 
    
    //Showing topology
    public static TopologyGUI topologyGUI = null;
    
    //node entry discover hashmap, keperluan penentuan my cluster head
    private HashMap<NetAddress, NodeEntryDiscovery> listTetangga = new HashMap<NetAddress, NodeEntryDiscovery>();
    
    //anti-duplicate list
    ArrayList<String> receivedDataId = new ArrayList<String>();
    
    //list dari node ini proses query apa aj mana saja
    private ArrayList<Integer> queryProcessed = new ArrayList<Integer>();
    
    //hashmap key:queryid item:sinkIP,sinkLocation
    private class DestinationSink {
        public NetAddress sinkIP;
        public NCS_Location2D sinkLocation;
        public int regionProcessed;

        public DestinationSink (NetAddress sinkIP, NCS_Location2D sinkLocation, int regionProcessed) {
            this.sinkIP = sinkIP;
            this.sinkLocation = sinkLocation;
            this.regionProcessed = regionProcessed;
        }
    }
    private HashMap<Integer, DestinationSink> detailQueryProcessed = new HashMap<Integer, DestinationSink>();
    
    /*Pool received, key hashmap "QUERYID-P:PRIORITY-TIPESENSOR"
     * contoh QUERYID = 10; TIPESENSOR=SENSOR-SUHU
     * jadinya 10-SENSOR-SUHU (STRING tipe)
     */
    
    private class poolReceivedItem {
        public String tipeSensor;
        public double maxValue;
        public double minValue;
        public double averageValue;
        public int queryID;
        public int fromRegion;

        public poolReceivedItem() {
            this.tipeSensor = "UNKNOWN";
            this.maxValue = -9999;
            this.minValue = -9999;
            this.averageValue = -9999;
            this.queryID = 0;
            this.fromRegion = 0;
        }

        public void putMaxValue (double MaxValue) {
            if (this.maxValue < MaxValue) {
                this.maxValue = MaxValue;
                return;
            }
            if (this.maxValue == -9999) {
                this.maxValue = MaxValue;
                return;
            }
        }

        public void putMinValue (double MinValue) {
            if (this.minValue > MinValue) {
                this.minValue = MinValue;
                return;
            }

            if (this.minValue == -9999) {
                this.minValue = MinValue;
                return;
            }
        }
    }
    
    private Map<String, poolReceivedItem> rcvPool = new HashMap<String, poolReceivedItem>();
    private ArrayList<poolReceivedItem> lstItemPool = new ArrayList<poolReceivedItem>();
    
    /** Creates a new instance
     *
     * @param Node    the SIDnet node handle to access 
     * 				  its GUI-primitives and shared environment
     */
    public RoutingProtocol(Node myNode) {
        this.myNode = myNode;
        
        /** Create a proxy for the application layer of this node */
        self = (RouteInterface.DharmawanRoute)JistAPI.proxy(this, RouteInterface.DharmawanRoute.class);
    }

    public void timingSend(long interval) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        JistAPI.sleepBlock(interval);

        if (!rcvPool.isEmpty()) {
            
            System.out.println("pool isi");
            
            lstItemPool.clear();
            
            Iterator i = rcvPool.entrySet().iterator();

            while (i.hasNext()) {
                Entry item = (Entry) i.next();
                poolReceivedItem pri = (poolReceivedItem)item.getValue();

                i.remove();
                lstItemPool.add(pri);
            }

            for (poolReceivedItem pri : lstItemPool) {
                MessageDharmawanDataValue mddv = new MessageDharmawanDataValue();
                mddv.fromRegion = pri.fromRegion;
                mddv.queryID = pri.queryID;
                mddv.tipeSensor = pri.tipeSensor;

                mddv.sinkIP = detailQueryProcessed.get(pri.queryID).sinkIP;
                mddv.sinkLocation = detailQueryProcessed.get(pri.queryID).sinkLocation;

                ProtocolMessageWrapper pmw = new ProtocolMessageWrapper(mddv);
                String unikID = String.valueOf(myNode.getID()) + String.valueOf(JistAPI.getTime());
                pmw.setS_seq(Long.valueOf(unikID));

                NetMessage.Ip nmip = new NetMessage.Ip(pmw, myNode.getIP(), detailQueryProcessed.get(mddv.queryID).sinkIP, Constants.NET_PROTOCOL_INDEX_1, Constants.NET_PRIORITY_NORMAL, (byte)100);
                NetAddress nextHop = getNextHopToSink(mddv.sinkIP, mddv.sinkLocation);
                System.out.println("Next Hop To Sink = "+nextHop.toString());
                sendToLinkLayer(nmip, nextHop);
            }
        }
        
        ((RouteInterface.DharmawanRoute)self).timingSend(interval);
    }
    
    private NetAddress getNextHopToSink(NetAddress sinkIP, NCS_Location2D sinkLocation) {

        double shortestNodeDistance = -1;
        NetAddress nextHopAddress = null;
        NCS_Location2D nextHopLocation = myNode.getNCS_Location2D();

        //ambil list tetangga yang dibuat oleh heartbeat
        LinkedList<NodeEntry> neighboursLinkedList
        	= myNode.neighboursList.getAsLinkedList();

        for(NodeEntry nodeEntry: neighboursLinkedList) {
            if (shortestNodeDistance == -1) {
                shortestNodeDistance = nodeEntry.getNCS_Location2D().distanceTo(sinkLocation);
                nextHopAddress = nodeEntry.ip;
                nextHopLocation = nodeEntry.getNCS_Location2D();
            } else if (shortestNodeDistance > nodeEntry.getNCS_Location2D().distanceTo(sinkLocation)) {
                shortestNodeDistance = nodeEntry.getNCS_Location2D().distanceTo(sinkLocation);
                nextHopAddress = nodeEntry.ip;
                nextHopLocation = nodeEntry.getNCS_Location2D();
            }
        }

        topologyGUI.addLink(myNode.getNCS_Location2D(), nextHopLocation, 1, Color.RED, TopologyGUI.HeadType.LEAD_ARROW);

        return nextHopAddress;
    }

    public void selfMonitor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void peek(NetMessage msg, MacAddress lastHop) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void send(NetMessage msg) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        // process only if there are energy reserves
        if (myNode.getEnergyManagement()
        		  .getBattery()
        		  .getPercentageEnergyLevel()< 2)
            return;
        
        if (this.netQueueFULL)
            return;
      
        //  jika pesan tidak dikenali, tolak
        if (!(((NetMessage.Ip)msg).getPayload() instanceof ProtocolMessageWrapper)) {
            return; // ignore non-specific messages
        }
        
        // update visuals        
    	myNode.getNodeGUI().colorCode.mark(colorProfile,ColorProfileDharmawan.RECEIVE, 2000);
        
        //Extract pesan ketipe wrapper
        ProtocolMessageWrapper sndMsg = (ProtocolMessageWrapper)((NetMessage.Ip)msg).getPayload();

        if (sndMsg.getPayload() instanceof MessageDataValue) {
            //tipe pesan ini diteruskan ke fungsi handleMessageDataValue
            //if (!receivedDataId.contains(String.valueOf(sndMsg.getS_seq()))) {
            //    receivedDataId.add(String.valueOf(sndMsg.getS_seq()));
            //    memoryControllerPacketID();
            //handleMessageDataValue(msg);
            handleMessageDataValue((MessageDataValue)sndMsg.getPayload());
        }
        else if (sndMsg.getPayload() instanceof MessageQuery) {
            //tipe pesan query, cek apakah pesan query sudah pernah diterima sebelumnya
            //jika belum berikan ke fungsi handleMessageQuery
            //jika sudah abaikan
            if (!receivedDataId.contains(String.valueOf(sndMsg.getS_seq()))) {
                receivedDataId.add(String.valueOf(sndMsg.getS_seq()));
                memoryControllerPacketID();
                handleMessageQuery(sndMsg);
            }
        }
        else if (sndMsg.getPayload() instanceof MessageDharmawanDataValue) {
            //tipe pesan aggregate diteruskan ke fungsi handleMessageAggregatedDataValue
            //if (!receivedDataId.contains(String.valueOf(sndMsg.getS_seq()))) {
            //    receivedDataId.add(String.valueOf(sndMsg.getS_seq()));
            //    memoryControllerPacketID();
                //System.out.println("boom");
                handleMessageDharmawan(msg);
            //}
        }
    }
    
    private void handleMessageDharmawan(NetMessage msg) {
        //Extract pesan ketipe wrapper
        ProtocolMessageWrapper sndMsg = (ProtocolMessageWrapper)((NetMessage.Ip)msg).getPayload();

        //extract ke tipe madv
        MessageDharmawanDataValue madv = (MessageDharmawanDataValue)sndMsg.getPayload();

        if (queryProcessed.contains(madv.queryID)) {
            //pesan diterima oleh node yang mengerjakan query yang sama
            //next?
            //sementara di send next hop aja

            NetMessage.Ip nmip = new NetMessage.Ip(sndMsg, myNode.getIP(), madv.sinkIP, Constants.NET_PROTOCOL_INDEX_1, Constants.NET_PRIORITY_NORMAL, (byte)100);
            NetAddress nextHop = getNextHopToSink(madv.sinkIP, madv.sinkLocation);
            sendToLinkLayer(nmip, nextHop);
        } else {
            //node tidak melakukan process query ID yang sama
            //send ke next hop
            NetMessage.Ip nmip = new NetMessage.Ip(sndMsg, myNode.getIP(), madv.sinkIP, Constants.NET_PROTOCOL_INDEX_1, Constants.NET_PRIORITY_NORMAL, (byte)100);
            NetAddress nextHop = getNextHopToSink(madv.sinkIP, madv.sinkLocation);
            sendToLinkLayer(nmip, nextHop);
        }
    }
    
    private void handleMessageQuery(ProtocolMessageWrapper msg) {
        /*
         * Ketika mendapatkan query message, node memeriksa apakah dia masuk
         * didalam region tersebut, jika iya maka node akan broadcast
         * ke tetangga bahwa ia masuk dalam region baru
         */

        MessageQuery query = (MessageQuery)msg.getPayload();

        if (query.getQuery().getSinkIP().hashCode() != myNode.getIP().hashCode())
            if (query.getQuery().getRegion().isInside(myNode.getNCS_Location2D())) {
                //System.out.println("Node " + myNode.getID() + " is inside region " + String.valueOf(query.getQuery().getRegion().getID()));

                //start timing send
                ((RouteInterface.DharmawanRoute)self).timingSend(this.INTERVAL_TIMING_SEND);

                this.queryProcessed.add(query.getQuery().getID());
                DestinationSink ds = new DestinationSink(query.getQuery().getSinkIP(), query.getQuery().getSinkNCSLocation2D(), query.getQuery().getRegion().getID());
                this.detailQueryProcessed.put(query.getQuery().getID(), ds);

                MessageNodeDiscover mnd = new MessageNodeDiscover(myNode.getID(), myNode.getIP(), myNode.neighboursList.size(), myNode.getEnergyManagement().getBattery().getPercentageEnergyLevel());                
                mnd.addQueryProcessed(this.queryProcessed);

                ProtocolMessageWrapper pmw = new ProtocolMessageWrapper(mnd);
                String unikID = String.valueOf(myNode.getID()) + String.valueOf(JistAPI.getTime());
                pmw.setS_seq(Long.valueOf(unikID));
                NetMessage.Ip nmip = new NetMessage.Ip(pmw, myNode.getIP(), NetAddress.ANY, Constants.NET_PROTOCOL_INDEX_1, Constants.NET_PRIORITY_NORMAL, (byte)100);
                sendToLinkLayer(nmip, NetAddress.ANY);

                //setelah di broadcast, query diteruskan ke app layer
                sendToAppLayer(query, null);
            }
        
        //give a breath
        //JistAPI.sleepBlock(2 * Constants.SECOND);
        
        //sebarkan query
        //System.out.println("Node " + myNode.getID() + " broadcasting query.");
        NetMessage.Ip nmip = new NetMessage.Ip(msg, myNode.getIP(), NetAddress.ANY, Constants.NET_PROTOCOL_INDEX_1, Constants.NET_PRIORITY_NORMAL, (byte)100);
        sendToLinkLayer(nmip, NetAddress.ANY);
    }
    
    private void handleMessageDataValue(MessageDataValue msg) {

        //cari cluster head
        //NetAddress myClusterHead = myNode.current_CH;
        
        //cari cluster head
        NetAddress myClusterHead = getMyClusterHead();
        
        //jika cluster headnya diri sendiri
        if (myClusterHead.hashCode() == myNode.getIP().hashCode()) {
            //cluster headnya diri sendiri, cari tujuan lain
            System.out.println("CH diri sendiri");
            
            poolHandleMessageDataValue(msg);
        }
        //jika tidak maka kirim ke cluster head
        else {
            //System.out.println("Node " + myNode.getID() + " mengirim data ke clusterhead " + myClusterHead);
            System.out.println("CH "+ myClusterHead.toString());
            
            ProtocolMessageWrapper pmw = new ProtocolMessageWrapper(msg);
            String unikID = String.valueOf(myNode.getID()) + String.valueOf(JistAPI.getTime());
            pmw.setS_seq(Long.valueOf(unikID));
            NetMessage.Ip nmip = new NetMessage.Ip(pmw, myNode.getIP(), detailQueryProcessed.get(msg.queryId).sinkIP, Constants.NET_PROTOCOL_INDEX_1, Constants.NET_PRIORITY_NORMAL, (byte)100);
            sendToLinkLayer(nmip, myClusterHead);
        
            //  teruskan pesan ke cluster head
            topologyGUI.addLink(msg.producerNodeId, myNode.getID(), 0, Color.BLACK, TopologyGUI.HeadType.LEAD_ARROW);
        }
    }
    
    private void poolHandleMessageDataValue(MessageDataValue msg) {
        //buat keyHashMap
        String hashMapKey = String.valueOf(msg.queryId) + "-" + msg.tipeSensor;

        //cek jika sudah terdapat
        if (rcvPool.containsKey(hashMapKey)) {
            
            poolReceivedItem priNew = new poolReceivedItem();
            poolReceivedItem priLast = rcvPool.get(hashMapKey);

            priNew.tipeSensor = priLast.tipeSensor;
            priNew.fromRegion = priLast.fromRegion;
            priNew.queryID = priLast.queryID;

            priNew.putMaxValue(priLast.maxValue);
            priNew.putMinValue(priLast.minValue);

            priNew.putMaxValue(msg.dataValue);
            priNew.putMinValue(msg.dataValue);

            rcvPool.put(hashMapKey, priNew);
            return;
        } else {
            //tidak terdapat key
            //bikin entry queue langsung masukan

            //System.out.println("Node:" + myNode.getID() + " create new hashmapkey:" + hashMapKey);

            poolReceivedItem priNew = new poolReceivedItem();
            priNew.tipeSensor = msg.tipeSensor;
            priNew.fromRegion = msg.fromRegion;
            priNew.queryID = msg.queryId;

            priNew.putMaxValue(msg.dataValue);
            priNew.putMinValue(msg.dataValue);

            rcvPool.put(hashMapKey, priNew);
            return;
        }
    }
    
    private NetAddress getMyClusterHead() {
        
        List<NetAddress> lstNetAddr = new ArrayList<NetAddress>();

        //awalnya node akan mengganggap dirinya adalah cluster head, memiliki
        //banyak tetangga
        int maxDiscoveredNode = myNode.neighboursList.size();
        double energy = myNode.getEnergyManagement().getBattery().getPercentageEnergyLevel();
        //double point = maxDiscoveredNode/200 + energy/100;
        double point = maxDiscoveredNode/200;
        NetAddress selectedClusterHead = myNode.getIP();
        //System.out.println("ululululu\n"+myNode.getIP()+" "+myNode.getID()+" "+maxDiscoveredNode+" "+energy);

        //ambil list tetangga yang dibuat oleh heartbeat
        LinkedList<NodeEntry> neighboursLinkedList = myNode.neighboursList.getAsLinkedList();

        //periksa setiap tetangga yang memproses queryID yang sama
        //jika ada yang sama kumpulkan di ListNetAddress
        for(NodeEntry nodeEntry: neighboursLinkedList) {
            if (listTetangga.containsKey(nodeEntry.ip))
                lstNetAddr.add(nodeEntry.ip);
        }

        //cari tetangga yang memiliki tetangga paling banyak
        if (!lstNetAddr.isEmpty())
            for (NetAddress na: lstNetAddr) {;
                int tmp_tetangga = listTetangga.get(na).totalDiscoveredNode;
                double tmp_baterry = listTetangga.get(na).energyLeft;
                //double tmp_point = tmp_tetangga/200 + tmp_baterry/100;
                //System.out.println(na.toString()+" 0 "+maxDiscoveredNode+" "+energy);
                double tmp_point = tmp_tetangga/200;
                if (tmp_point > point) {
                    selectedClusterHead = na;
                    point = tmp_point;
                }
            }
        System.out.println(selectedClusterHead.toString());
        return selectedClusterHead;
    }

    public void receive(Message msg, NetAddress src, MacAddress lastHop, byte macId, NetAddress dst, byte priority, byte ttl) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        if (myNode.getEnergyManagement()
        		  .getBattery()
        		  .getPercentageEnergyLevel()< 2)
            return;
        
        if (this.netQueueFULL)
            return;

        //Reject semua pesan yang diterima jika tipe pesan tidak dikenali
        if (!(msg instanceof ProtocolMessageWrapper)) {
            return;
        }
        
        // Provide a basic visual feedback on the fact that 
        //this node has received a message */
        myNode.getNodeGUI().colorCode.mark(colorProfile,ColorProfileDharmawan.RECEIVE, 20);
        
        //Extract pesan ketipe wrapper
        ProtocolMessageWrapper rcvMsg = (ProtocolMessageWrapper)msg;

        //Operasi sesuai dengan tipe pesan
        if (rcvMsg.getPayload() instanceof MessageNodeDiscover) {
            //tipe pesan discovery, berikan ke fungsi handle MessageNodeDiscover
            System.out.println("receive discover Node " + myNode.getID() + " got node info from Node " + ((MessageNodeDiscover)rcvMsg.getPayload()).nodeID);
            handleMessageNodeDiscover((MessageNodeDiscover)rcvMsg.getPayload());
        }
        else if (rcvMsg.getPayload() instanceof MessageQuery) {
            //tipe pesan query, cek apakah pesan query sudah pernah diterima sebelumnya
            //jika belum berikan ke fungsi handleMessageQuery
            //jika sudah abaikan
            if (!receivedDataId.contains(String.valueOf(rcvMsg.getS_seq()))) {
                receivedDataId.add(String.valueOf(rcvMsg.getS_seq()));
                memoryControllerPacketID();
                handleMessageQuery(rcvMsg);
            }
            //System.out.println("receive query");
        }
        else if (rcvMsg.getPayload() instanceof MessageDharmawanDataValue) {
            //jika pesan aggregate duplicate, abaikan
            if (this.receivedDataId.contains(String.valueOf(rcvMsg.getS_seq())))
                return;
            receivedDataId.add(String.valueOf(rcvMsg.getS_seq()));
            memoryControllerPacketID();

            //tipe pesan data value yang sudah diaggregate
            //bagian ini biasanya dipanggil jika pesan ini diterima pada sink node
            //lempar ke layer app
            //sendToAppLayer(rcvMsg.getPayload(), src);
            
            sendToAppLayer((MessageDharmawanDataValue)rcvMsg.getPayload(), null);
        }
    }
    
    private void handleMessageNodeDiscover(MessageNodeDiscover msg) {
        NodeEntryDiscovery ned = new NodeEntryDiscovery(msg.nodeID, msg.ipAddress, msg.totalDiscoveredNode, msg.energyLeft);
        ned.addQueryProcessed(msg.queryProcessed);
        listTetangga.put(msg.ipAddress, ned);
    }

    @Override
    public void dropNotify(Message msg, MacAddress nextHopMac, Reason reason) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        System.out.println("Packet dropped notify");
    	if (reason == Reason.PACKET_SIZE_TOO_LARGE) {
    		System.out.println("WARNING: Packet size too large - unable to transmit");
    		throw new RuntimeException("Packet size too large - unable to transmit");
    	}
        if (reason == Reason.NET_QUEUE_FULL) {
            if (!this.netQueueFULL) {
                this.netQueueFULL = true;
                System.out.println("ERROR: Net Queue full node" + myNode.getID() + " TIME (SEC): " + (JistAPI.getTime() / Constants.SECOND));
                myNode.getNodeGUI().colorCode.mark(new ColorProfileGeneric(), ColorProfileGeneric.DEAD, ColorProfileGeneric.FOREVER);
                //throw new RuntimeException("Net Queue Full");
            }
        }
        if (reason == Reason.UNDELIVERABLE || reason == Reason.MAC_BUSY)
            System.out.println("WARNING: Cannot relay packet to the destination node " + nextHopMac);
        
        // wait 5 seconds and try again
        JistAPI.sleepBlock(500 * Constants.MILLI_SECOND);
        netEntity.send((NetMessage.Ip)msg, Constants.NET_INTERFACE_DEFAULT, nextHopMac);
        this.send((NetMessage)msg);
    }

    public void start() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
     
        myNode.getSimManager().getSimGUI()
		  .getAnimationDrawingTool()
		 	  .animate("ExpandingFadingCircle",
				       myNode.getNCS_Location2D());
        
//        if (myNode.getID() == 164)
//            System.out.println("route packet to " + nextHopDestIP);

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
            myNode.getNodeGUI().colorCode.mark(colorProfile, ColorProfileDharmawan.TRANSMIT, 2);
            netEntity.send(ipMsg, Constants.NET_INTERFACE_DEFAULT, macAddress);
        }
        
        return SUCCESS;
    }
    
    public RouteInterface getProxy()
    {
        return self;
    }
    
    private void memoryControllerPacketID() {
        if (this.receivedDataId.size() >= LIMIT_PACKET_ID_SIZE) {
            this.receivedDataId.remove(0);
        }
    }
}