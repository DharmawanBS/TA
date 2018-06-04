/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.users.dharmawan_route.routing;

import java.util.ArrayList;
import java.util.Iterator;
import jist.swans.mac.MacAddress;
import jist.swans.misc.Message;
import jist.swans.net.NetAddress;
import sidnet.core.misc.NCS_Location2D;

/**
 *
 * @author invictus
 */
public class MessageNodeDiscover implements Message {
    public int nodeID;
    public NetAddress ipAddress;
    public int totalDiscoveredNode;
    public double energyLeft;
    public NCS_Location2D position;

    public double myPoint;

    public ArrayList<Integer> queryProcessed = new ArrayList<Integer>();
    
    public MessageNodeDiscover(int nodeID, NetAddress ipAddress, int totalDiscoveredNode, double energyLeft, NCS_Location2D position) {
        this.nodeID = nodeID;
        this.ipAddress = ipAddress;
        this.totalDiscoveredNode = totalDiscoveredNode;
        this.energyLeft = energyLeft;
        this.position = position;
        this.myPoint = 0;
    }

    public MessageNodeDiscover(int nodeID, NetAddress ipAddress, int totalDiscoveredNode, double energyLeft, double myPoint) {
        this.nodeID = nodeID;
        this.ipAddress = ipAddress;
        this.totalDiscoveredNode = totalDiscoveredNode;
        this.energyLeft = energyLeft;
        this.myPoint = myPoint;
    }

    public void addQueryProcessed(ArrayList<Integer> qp) {
        if (!qp.isEmpty())
            for (int x: qp) {
                this.queryProcessed.add(x);
            }
    }

    public int getSize() {
        return (6 + (this.queryProcessed.size()));
    }

    public void getBytes(byte[] msg, int offset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
