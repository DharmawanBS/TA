/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sidnet.stack.users.dharmawan_route.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import jist.runtime.JistAPI;
import jist.swans.Constants;
import jist.swans.mac.MacAddress;
import jist.swans.misc.Message;
import jist.swans.net.NetAddress;
import jist.swans.net.NetInterface;
import jist.swans.net.NetMessage;
import jist.swans.route.RouteInterface;
import sidnet.core.gui.TopologyGUI;
import sidnet.core.interfaces.AppInterface;
import sidnet.core.misc.NCS_Location2D;
import sidnet.core.misc.Node;
import sidnet.core.misc.Reason;
import sidnet.stack.users.dharmawan_route.colorprofile.ColorProfileDharmawan;

/**
 *
 * @author dharmawan
 */
public class RoutingProtocol implements RouteInterface.DharmawanRoute {

    public static final byte ERROR = -1;
    public static final byte SUCCESS = 0;

    /*
     * Konstanta variable pengaturan routing
     * modifikasi value disini aja
     */
    private static final long MINIMUM_AGGREGATE_DATA_PRIORITY_1 = 5;
    private static final long MINIMUM_AGGREGATE_DATA_PRIORITY_2 = 3;
    private static final long MINIMUM_AGGREGATE_DATA_PRIORITY_3 = 1;
    private static final long INTERVAL_TIMING_SEND = 5 * Constants.SECOND;

    private static final int MAXIMUM_RETRY_SEND_MESSAGE = 0;
    private static final long INTERVAL_WAITING_BEFORE_RETRY = 10 * Constants.SECOND;

    private static final int LIMIT_PACKET_ID_SIZE = 500;

    private final Node myNode; // The SIDnet handle to the node representation

    private boolean netQueueFULL = false;

    //Showing topology
    public static TopologyGUI topologyGUI = null;

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

    //node entry discover hashmap, keperluan penentuan my cluster head
    private HashMap<NetAddress, NodeEntryDiscovery> listTetangga = new HashMap<NetAddress, NodeEntryDiscovery>();

    /*Pool received, key hashmap "QUERYID-P:PRIORITY-TIPESENSOR"
     * contoh QUERYID = 10; PRIORITY=3; TIPESENSOR=SENSOR-SUHU
     * jadinya 10-P:3-SENSOR-SUHU (STRING tipe)
     */
    
    private class poolReceivedItem {
        public String tipeSensor;
        public double maxValue;
        public double minValue;
        public double averageValue;
        public long totalValueAggregated;
        public int priorityLevel;
        public boolean is_from_CH;
        public boolean is_from_group;

        public poolReceivedItem() {
            this.tipeSensor = "UNKNOWN";
            this.maxValue = -9999;
            this.minValue = -9999;
            this.averageValue = -9999;
            this.totalValueAggregated = 0;
            this.priorityLevel = 0;
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

        public void putAverageValue (double AverageValue) {
            if (this.averageValue == -9999) {
                this.averageValue = AverageValue;
                return;
            } else {
                double tmpAvg = this.averageValue + AverageValue;
                tmpAvg = tmpAvg / 2;
                this.averageValue = tmpAvg;
            }
        }

        public void putTotalAggregatedValue(long Total) {
            this.totalValueAggregated = this.totalValueAggregated + Total;
        }
    }
    private Map<String, poolReceivedItem> rcvPool = new HashMap<String, poolReceivedItem>();
    private boolean rcvPoolIsLock = false; //jika rcvPool dikunci, tidak boleh diakses
    private ArrayList<poolReceivedItem> lstItemPool = new ArrayList<poolReceivedItem>();
    
    /*
     * Inisialisasi Routing protocol
     */
    public RoutingProtocol(Node myNode) {
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

               if ((pri.priorityLevel == 1) &&
                       (pri.totalValueAggregated >= this.MINIMUM_AGGREGATE_DATA_PRIORITY_1)) {
                   i.remove();
                   lstItemPool.add(pri);
               } else if ((pri.priorityLevel == 2) &&
                       (pri.totalValueAggregated >= this.MINIMUM_AGGREGATE_DATA_PRIORITY_2)) {
                   i.remove();
                   lstItemPool.add(pri);
               } else if ((pri.priorityLevel == 3) &&
                       (pri.totalValueAggregated >= this.MINIMUM_AGGREGATE_DATA_PRIORITY_3)) {
                   i.remove();
                   lstItemPool.add(pri);
               }
            }

            //System.out.println("UNSORTED QUEUE");

            Collections.sort(lstItemPool, priorityComp);

            //System.out.println("SORTED QUEUE");

            for (poolReceivedItem pri : lstItemPool) {
                MessageDharmawanDataValue madv = new MessageDharmawanDataValue();
                madv.averageValue = pri.averageValue;
                madv.is_from_CH = pri.is_from_CH;
                madv.maxValue = pri.maxValue;
                madv.minValue = pri.minValue;
                madv.priorityLevel = pri.priorityLevel;
                madv.is_from_group = pri.is_from_group;
                madv.tipeSensor = pri.tipeSensor;
                madv.totalValueAggregated = pri.totalValueAggregated;

                madv.aggregatorNodeID = myNode.getID();
                madv.aggregatorNodeLocation = myNode.getNCS_Location2D();

                //madv.sinkIP = detailQueryProcessed.get(pri.queryID).sinkIP;
                //madv.sinkLocation = detailQueryProcessed.get(pri.queryID).sinkLocation;
                
                

                ProtocolMessageWrapper pmw = new ProtocolMessageWrapper(madv);
                String unikID = String.valueOf(myNode.getID()) + String.valueOf(JistAPI.getTime());
                pmw.setS_seq(Long.valueOf(unikID));

                NetMessage.Ip nmip = new NetMessage.Ip(pmw, myNode.getIP(), detailQueryProcessed.get(madv.queryID).sinkIP, Constants.NET_PROTOCOL_INDEX_1, Constants.NET_PRIORITY_NORMAL, (byte)100);
                NetAddress nextHop = getNextHopToSink(madv.sinkIP, madv.sinkLocation);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void receive(Message msg, NetAddress src, MacAddress lastHop, byte macId, NetAddress dst, byte priority, byte ttl) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dropNotify(Message msg, MacAddress nextHopMac, Reason reason) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
