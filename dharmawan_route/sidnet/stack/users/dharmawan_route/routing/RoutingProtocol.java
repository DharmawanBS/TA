/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sidnet.stack.users.dharmawan_route.routing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
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
import sidnet.stack.users.dharmawan_route.app.DropperNotifyAppLayer;
import sidnet.stack.users.dharmawan_route.app.MessageDataValue;
import sidnet.stack.users.dharmawan_route.app.MessageQuery;

/**
 *
 * @author dharmawan
 */
public class RoutingProtocol implements RouteInterface.DharmawanRoute {
    
    //Showing topology
    public static TopologyGUI topologyGUI = null;
    
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
    private ColorProfileDharmawan colorProfileGeneric = new ColorProfileDharmawan();

    //anti-duplicate list
    ArrayList<String> receivedDataId = new ArrayList<String>();

    //hashmap hitung maksimum retry
    HashMap<Long, Integer> dataRetry = new HashMap<Long, Integer>();
    
    private HashMap<Integer, destinationSink> detailQueryProcessed = new HashMap<Integer, destinationSink>();

    //node entry discover hashmap, keperluan penentuan my cluster head
    private HashMap<NetAddress, NodeEntryDiscovery> listTetangga = new HashMap<NetAddress, NodeEntryDiscovery>();
    
    private Map<String, poolReceivedItem> rcvPool = new HashMap<String, poolReceivedItem>();
    private boolean rcvPoolIsLock = false; //jika rcvPool dikunci, tidak boleh diakses
    private ArrayList<poolReceivedItem> lstItemPool = new ArrayList<poolReceivedItem>();
    
    private static final int LIMIT_PACKET_ID_SIZE = 200;
    private static final long INTERVAL_TIMING_SEND = 5 * Constants.SECOND;
    private static final int MAXIMUM_RETRY_SEND_MESSAGE = 0;
    private static final long INTERVAL_WAITING_BEFORE_RETRY = 10 * Constants.SECOND;
    
    //list dari node ini proses query apa aj mana saja
    private ArrayList<Integer> queryProcessed = new ArrayList<Integer>();
    
    public RoutingProtocol (Node myNode) {
        this.myNode = myNode;
        
        self = (RouteInterface.DharmawanRoute)JistAPI.proxy(this, RouteInterface.DharmawanRoute.class);

        this.rcvPoolIsLock = false;
    }
    
    /*
     * SWANS Network Hooks up
     *
     */
    public void setNetEntity(NetInterface netEntity)
    {
        if(!JistAPI.isEntity(netEntity)) throw new IllegalArgumentException("expected entity");
        if(this.netEntity!=null) throw new IllegalStateException("net entity already set");

        this.netEntity = netEntity;
    }
    public RouteInterface getProxy()
    {
        return self;
    }
    public void setAppInterface(AppInterface appInterface)
    {
        this.appInterface = appInterface;
    }

    @Override
    public void timingSend(long interval) {
        JistAPI.sleepBlock(interval);

        if (!rcvPool.isEmpty()) {
            
            lstItemPool.clear();

            Comparator priorityComp = new Comparator<poolReceivedItem>(){
                public int compare(poolReceivedItem o1, poolReceivedItem o2) {
                    return o2.priorityLevel - o1.priorityLevel;
                }
            };
            
            Iterator i = rcvPool.entrySet().iterator();

            while (i.hasNext()) {
               Entry item = (Entry) i.next();
               poolReceivedItem pri = (poolReceivedItem)item.getValue();
               
               i.remove();
               lstItemPool.add(pri);
            }

            for (poolReceivedItem pri : lstItemPool) {
                MessageDataDharmawanValue madv = new MessageDataDharmawanValue();
                madv.from = myNode.getNCS_Location2D();
                madv.nodeId = myNode.getID();
                
                madv.sinkIP = detailQueryProcessed.get(pri.queryID).sinkIP;
                madv.to = detailQueryProcessed.get(pri.queryID).sinkLocation;
                
                madv.dataValue = pri.dataValue;
                madv.queryId = pri.queryID;

                ProtocolMessageWrapper pmw = new ProtocolMessageWrapper(madv);
                String unikID = String.valueOf(myNode.getID()) + String.valueOf(JistAPI.getTime());
                pmw.setS_seq(Long.valueOf(unikID));

                NetMessage.Ip nmip = new NetMessage.Ip(pmw, myNode.getIP(), detailQueryProcessed.get(madv.queryId).sinkIP, Constants.NET_PROTOCOL_INDEX_1, Constants.NET_PRIORITY_NORMAL, (byte)100);
                NetAddress nextHop = getNextHopToSink(madv.sinkIP, madv.to);
                sendToLinkLayer(nmip, nextHop);

            }

        }
        
        ((RouteInterface.DharmawanRoute)self).timingSend(interval);
    }

    @Override
    public void selfMonitor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void peek(NetMessage msg, MacAddress lastHop) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void send(NetMessage msg) {
        //Reject pesan jika energy yang tersisa kurang dari 2%
        if (myNode.getEnergyManagement()
        		  .getBattery()
        		  .getPercentageEnergyLevel()< 2)
            return;

        //Reject semua pesan yang diterima jika tipe pesan tidak dikenali
        if (!(((NetMessage.Ip)msg).getPayload() instanceof ProtocolMessageWrapper))
            return;

        //node fail system
        if (this.netQueueFULL)
            return;

        //set visual warna node
        myNode.getNodeGUI().colorCode.mark(colorProfileGeneric,ColorProfileDharmawan.RECEIVE, 2);
        System.out.println(""+myNode.getID());

        //Extract pesan ketipe wrapper
        ProtocolMessageWrapper sndMsg = (ProtocolMessageWrapper)((NetMessage.Ip)msg).getPayload();
        
        handleMessageDataValue((MessageDataValue)sndMsg.getPayload());
    }
    
    private void handleMessageDataValue(MessageDataValue msg) {
        //mendapatkan pesan dengan jenis MessageDataValue terdapat 2 kemungkinan
        //jika pesan berasal dari layer atas, maka akan dikirimkan ke cluster head
        //jika pesan berasal dari node lain, maka data value ditampung pada pool

        if (msg.producerNodeId == myNode.getID()) {

            //cari cluster head
            NetAddress myClusterHead = getMyClusterHead(msg.queryID);
            
            //jika cluster headnya diri sendiri maka masukan ke pool agar diteruskan
            //ke sink dengan timingSend()
            if (myClusterHead.hashCode() == myNode.getIP().hashCode()) {
                System.out.println("Node " + myNode.getID() + " memasukan data value sendiri ke pool");
                poolHandleMessageDataValue(msg);
            }
            //jika tidak maka kirim ke cluster head
            else {
                System.out.println("Node " + myNode.getID() + " mengirim data ke clusterhead " + myClusterHead);
                ProtocolMessageWrapper pmw = new ProtocolMessageWrapper(msg);
                String unikID = String.valueOf(myNode.getID()) + String.valueOf(JistAPI.getTime());
                pmw.setS_seq(Long.valueOf(unikID));
                NetMessage.Ip nmip = new NetMessage.Ip(pmw, myNode.getIP(), detailQueryProcessed.get(msg.queryID).sinkIP, Constants.NET_PROTOCOL_INDEX_1, Constants.NET_PRIORITY_NORMAL, (byte)100);
                sendToLinkLayer(nmip, myClusterHead);
            }
        }
        else {
            topologyGUI.addLink(msg.producerNodeId, myNode.getID(), 0, Color.RED, TopologyGUI.HeadType.LEAD_ARROW);
            myNode.getNodeGUI().colorCode.mark(colorProfileGeneric, ColorProfileDharmawan.CLUSTERHEAD, 5000);
            
            //System.out.println("Node " + myNode.getID() + " memasukan data value node:" + msg.producerNodeId + " ke pool");

            poolHandleMessageDataValue(msg);
        }
    }
    
    private void poolHandleMessageDataValue(MessageDataValue msg) {
        //buat keyHashMap
        String hashMapKey = String.valueOf(msg.queryID) + "-P:" + msg.tipeSensor;

        //cek jika sudah terdapat
        if (rcvPool.containsKey(hashMapKey)) {
            //sudah terdapat key yang sama, lakukan aggregate dengan data sebelumnya

            //System.out.println("Node:" + myNode.getID() + " aggregated hashmapkey:" + hashMapKey);

            poolReceivedItem priNew = new poolReceivedItem();
            poolReceivedItem priLast = rcvPool.get(hashMapKey);

            priNew.tipeSensor = priLast.tipeSensor;
            priNew.fromRegion = priLast.fromRegion;
            priNew.priorityLevel = priLast.priorityLevel;
            priNew.queryID = priLast.queryID;

            priNew.putAverageValue(priLast.averageValue);
            priNew.putMaxValue(priLast.maxValue);
            priNew.putMinValue(priLast.minValue);

            priNew.putAverageValue(msg.dataValue);
            priNew.putMaxValue(msg.dataValue);
            priNew.putMinValue(msg.dataValue);

            rcvPool.put(hashMapKey, priNew);
        }
        else {
            //tidak terdapat key
            //bikin entry queue langsung masukan

            //System.out.println("Node:" + myNode.getID() + " create new hashmapkey:" + hashMapKey);

            poolReceivedItem priNew = new poolReceivedItem();
            priNew.tipeSensor = msg.tipeSensor;
            priNew.fromRegion = msg.fromRegion;
            priNew.queryID = msg.queryID;

            priNew.putAverageValue(msg.dataValue);
            priNew.putMaxValue(msg.dataValue);
            priNew.putMinValue(msg.dataValue);

            rcvPool.put(hashMapKey, priNew);
            return;
        }
    }

    @Override
    public void receive(Message msg, NetAddress src, MacAddress lastHop, byte macId, NetAddress dst, byte priority, byte ttl) {
        //Reject pesan jika energy yang tersisa kurang dari 2%
        if (myNode.getEnergyManagement()
        		  .getBattery()
        		  .getPercentageEnergyLevel()< 2)
            return;

        //Reject semua pesan yang diterima jika tipe pesan tidak dikenali
        if (!(msg instanceof ProtocolMessageWrapper))
            return;

        //node fail system
        if (this.netQueueFULL)
            return;

        //set visual warna node
        myNode.getNodeGUI().colorCode.mark(colorProfileGeneric,ColorProfileDharmawan.RECEIVE, 2);

        //Extract pesan ketipe wrapper
        ProtocolMessageWrapper rcvMsg = (ProtocolMessageWrapper)msg;

        //Operasi sesuai dengan tipe pesan
        if (rcvMsg.getPayload() instanceof MessageNodeDiscover) {
            //tipe pesan discovery, berikan ke fungsi handle MessageNodeDiscover
            //System.out.println("Node " + myNode.getID() + " got node info from Node " + ((MessageNodeDiscover)rcvMsg.getPayload()).nodeID);
            handleMessageNodeDiscover((MessageNodeDiscover)rcvMsg.getPayload());
        } else if (rcvMsg.getPayload() instanceof MessageQuery) {
            //tipe pesan query, cek apakah pesan query sudah pernah diterima sebelumnya
            //jika belum berikan ke fungsi handleMessageQuery
            //jika sudah abaikan
            if (!receivedDataId.contains(String.valueOf(rcvMsg.getS_seq()))) {
                receivedDataId.add(String.valueOf(rcvMsg.getS_seq()));
                memoryControllerPacketID();
                handleMessageQuery(rcvMsg);
            }
        } else if (rcvMsg.getPayload() instanceof MessageDataValue) {
            //jika pesan aggregate duplicate, abaikan
            if (this.receivedDataId.contains(String.valueOf(rcvMsg.getS_seq())))
                return;
            receivedDataId.add(String.valueOf(rcvMsg.getS_seq()));
            memoryControllerPacketID();

            //tipe pesan data value yang sudah diaggregate
            //bagian ini biasanya dipanggil jika pesan ini diterima pada sink node
            //lempar ke layer app
            //sendToAppLayer(rcvMsg.getPayload(), src);

            sendToAppLayer((MessageDataValue)rcvMsg.getPayload(), null);
        }
    }

    @Override
    public void dropNotify(Message msg, MacAddress nextHopMac, Reason reason) {
        if (reason == Reason.PACKET_SIZE_TOO_LARGE) {
    		System.out.println("NODE:" + myNode.getID() +" WARNING: Packet size too large - unable to transmit");
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
        if (reason == Reason.UNDELIVERABLE || reason == Reason.MAC_BUSY) {
            ProtocolMessageWrapper xMsg = (ProtocolMessageWrapper)((NetMessage.Ip)msg).getPayload();
            System.out.println("NODE:" + myNode.getID() + " WARNING: Cannot relay packet " + xMsg.getS_seq() + " to the destination node " + nextHopMac);
            
            //cek apakah batas retry masih bisa atau tidak
            if (isThisRetryAgain(xMsg.getS_seq())) {

                increaseThisRetry(xMsg.getS_seq());
                System.out.println("NODE:" + myNode.getID() + " Retrying(" + dataRetry.get(xMsg.getS_seq()) + ") PID:" + xMsg.getS_seq() + " send to node " + nextHopMac);

                //sleep before retry
                JistAPI.sleepBlock(this.INTERVAL_WAITING_BEFORE_RETRY);

                NetMessage.Ip nmip = (NetMessage.Ip) msg;

                if (xMsg.getPayload() instanceof MessageDataValue) {
                    //beritahu app layer agar menambah window aggregation
                    DropperNotifyAppLayer dnal = new DropperNotifyAppLayer(false, true);
                    sendToAppLayer(dnal, myNode.getIP());
                }
                NetAddress retryTo = convertMacToIP(nextHopMac);
                if (retryTo != null)
                    sendToLinkLayer(nmip, retryTo);
                else {
                    
                }
            } else {
                System.out.println("NODE:" + myNode.getID() + " Drop packet " + xMsg.getS_seq() + " after retry(" + dataRetry.get(xMsg.getS_seq()) + ") to node " + nextHopMac);
                deleteThisRetry(xMsg.getS_seq());
            }

        }

        if (reason == Reason.PACKET_DELIVERED) {
            ProtocolMessageWrapper xMsg = (ProtocolMessageWrapper)((NetMessage.Ip)msg).getPayload();
            if (xMsg.getPayload() instanceof MessageDataValue) {
                //beritahu app layer agar menngurangi window aggregation
                DropperNotifyAppLayer dnal = new DropperNotifyAppLayer(true, false);
                sendToAppLayer(dnal, myNode.getIP());
            }
            deleteThisRetry(xMsg.getS_seq());
        }
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private NetAddress getMyClusterHead(int queryID) {

        List<NetAddress> lstNetAddr = new ArrayList<NetAddress>();

        //awalnya node akan mengganggap dirinya adalah cluster head, memiliki
        //banyak tetangga
        int maxDiscoveredNode = myNode.neighboursList.size();
        double energyLeft = myNode.getEnergyManagement().getBattery().getPercentageEnergyLevel();
        
        double poin = maxDiscoveredNode/200 + energyLeft/100;
        
        NetAddress selectedClusterHead = myNode.getIP();

        //ambil list tetangga yang dibuat oleh heartbeat
        LinkedList<NodeEntry> neighboursLinkedList
        	= myNode.neighboursList.getAsLinkedList();

        //periksa setiap tetangga yang memproses queryID yang sama
        //jika ada yang sama kumpulkan di ListNetAddress
        for(NodeEntry nodeEntry: neighboursLinkedList) {
            if (listTetangga.containsKey(nodeEntry.ip))
                if (listTetangga.get(nodeEntry.ip).queryProcessed.contains(queryID))
                    lstNetAddr.add(nodeEntry.ip);
        }

        //ubah ini untuk penentuan clusterhead
        //cari tetangga yang memiliki tetangga paling banyak
        if (!lstNetAddr.isEmpty())
            for (NetAddress na: lstNetAddr) {;
                int tempDiscoveredNode = listTetangga.get(na).totalDiscoveredNode;
                double tempEnergyLeft = listTetangga.get(na).energyLeft;
                
                if (poin < tempDiscoveredNode/200 + tempEnergyLeft/100) {
                    selectedClusterHead = na;
                }
            }

        System.out.println("Node => " +myNode.getID()+ "Cluster Head => " +selectedClusterHead.toString());
        
        return selectedClusterHead;
    }
    
    private NetAddress getNextHopToSink(NetAddress sinkIP, NCS_Location2D sinkLocation) {

        double shortestNodeDistance = -1;
        NetAddress nextHopAddress = null;
        NCS_Location2D nextHopLocation = myNode.getNCS_Location2D();

        //ambil list tetangga yang dibuat oleh heartbeat
        LinkedList<NodeEntry> neighboursLinkedList = myNode.neighboursList.getAsLinkedList();

        for(NodeEntry nodeEntry: neighboursLinkedList) {
            if (shortestNodeDistance == -1) {
                shortestNodeDistance = nodeEntry.getNCS_Location2D().distanceTo(sinkLocation);
                nextHopAddress = nodeEntry.ip;
                nextHopLocation = nodeEntry.getNCS_Location2D();
            }
            else if (shortestNodeDistance > nodeEntry.getNCS_Location2D().distanceTo(sinkLocation)) {
                shortestNodeDistance = nodeEntry.getNCS_Location2D().distanceTo(sinkLocation);
                nextHopAddress = nodeEntry.ip;
                nextHopLocation = nodeEntry.getNCS_Location2D();
            }
        }

        topologyGUI.addLink(myNode.getNCS_Location2D(), nextHopLocation, 1, Color.BLACK, TopologyGUI.HeadType.LEAD_ARROW);

        return nextHopAddress;
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
    
    public void sendToLinkLayer(NetMessage.Ip ipMsg, NetAddress nextHopDestIP)
    {
        if (myNode.getEnergyManagement().getBattery().getPercentageEnergyLevel()< 2)
            System.out.println("sendToLinkLayer ==> batery level < 2");

        /*myNode.getSimManager().getSimGUI()
		  .getAnimationDrawingTool()
		 	  .animate("ExpandingFadingCircle",
				       myNode.getNCS_Location2D());*/
        ProtocolMessageWrapper pmw = (ProtocolMessageWrapper)ipMsg.getPayload();
        NetMessage.Ip copyMsg = new NetMessage.Ip(pmw,
			       ((NetMessage.Ip)ipMsg).getSrc(),
                               ((NetMessage.Ip)ipMsg).getDst(),
                               ((NetMessage.Ip)ipMsg).getProtocol(),
                               ((NetMessage.Ip)ipMsg).getPriority(),
                               ((NetMessage.Ip)ipMsg).getTTL(),
                               ((NetMessage.Ip)ipMsg).getId(),
                               ((NetMessage.Ip)ipMsg).getFragOffset());
        ipMsg = null;

        if (nextHopDestIP == null)
            System.err.println("NULL nextHopDestIP");
        if (nextHopDestIP == NetAddress.ANY) {
            netEntity.send(copyMsg, Constants.NET_INTERFACE_DEFAULT, MacAddress.ANY);
            registerIDToHashmapRetry(pmw.getS_seq());
        }
        else
        {
            NodeEntry nodeEntry = myNode.neighboursList.get(nextHopDestIP);
            if (nodeEntry == null)
            {
                 System.err.println("Node #" + myNode.getID() + ": Destination IP (" + nextHopDestIP + ") not in my neighborhood. Please re-route! Are you sending the packet to yourself?");
                 System.err.println("Node #" + myNode.getID() + "has + " + myNode.neighboursList.size() + " neighbors");
                 new Exception().printStackTrace();
                 System.out.println("sendToLinkLayer ==> error");
            }
            MacAddress macAddress = nodeEntry.mac;
            if (macAddress == null)
            {
                 System.err.println("Node #" + myNode.getID() + ": Destination IP (" + nextHopDestIP + ") not in my neighborhood. Please re-route! Are you sending the packet to yourself?");
                 System.err.println("Node #" + myNode.getID() + "has + " + myNode.neighboursList.size() + " neighbors");
                 System.out.println("sendToLinkLayer ==> error");
            }
            myNode.getNodeGUI().colorCode.mark(colorProfileGeneric, ColorProfileDharmawan.TRANSMIT, 2);

            netEntity.send(copyMsg, Constants.NET_INTERFACE_DEFAULT, macAddress);
            registerIDToHashmapRetry(pmw.getS_seq());
        }

        System.out.println("sendToLinkLayer ==> success");
    }
    
    private void registerIDToHashmapRetry(long s_Seq) {
        if (!dataRetry.containsKey(s_Seq)) {
            dataRetry.put(s_Seq, 0);
        }
    }
    
    private void handleMessageNodeDiscover(MessageNodeDiscover msg) {
        NodeEntryDiscovery ned = new NodeEntryDiscovery(msg.nodeID, msg.ipAddress, msg.totalDiscoveredNode, msg.energyLeft);
        ned.addQueryProcessed(msg.queryProcessed);
        listTetangga.put(msg.ipAddress, ned);
    }
    
    private void memoryControllerPacketID() {
        if (this.receivedDataId.size() >= LIMIT_PACKET_ID_SIZE) {
            this.receivedDataId.remove(0);
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
                destinationSink ds = new destinationSink(query.getQuery().getSinkIP(), query.getQuery().getSinkNCSLocation2D(), query.getQuery().getRegion().getID());
                this.detailQueryProcessed.put(query.getQuery().getID(),ds);

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
 
    private void increaseThisRetry(long s_Seq) {
        int x = dataRetry.get(s_Seq) + 1;
        dataRetry.put(s_Seq, x);
    }

    private void deleteThisRetry(long s_Seq) {
        dataRetry.remove(s_Seq);
    }
    
    private boolean isThisRetryAgain(long s_Seq) {
        if (dataRetry.containsKey(s_Seq)) {
            return dataRetry.get(s_Seq) < this.MAXIMUM_RETRY_SEND_MESSAGE;
        } else {
            System.out.println("Packet " + s_Seq + " not registered, dropped!");
            return false;
        }
    }
    
    private NetAddress convertMacToIP(MacAddress macAddr) {
        //ambil list tetangga yang dibuat oleh heartbeat
        LinkedList<NodeEntry> neighboursLinkedList
        	= myNode.neighboursList.getAsLinkedList();

        for(NodeEntry nodeEntry: neighboursLinkedList) {
            if (nodeEntry.mac.hashCode() == macAddr.hashCode())
                return nodeEntry.ip;
        }

        return null;
    }
}