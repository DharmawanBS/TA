/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sidnet.stack.users.dharmawan_route.routing;

import jist.swans.misc.Message;
import jist.swans.net.NetAddress;
import sidnet.core.misc.NCS_Location2D;

/**
 *
 * @author dharmawan
 */
public class MessageDharmawanDataValue implements Message {

    public String tipeSensor;

    public double maxValue;
    public double minValue;
    public double averageValue;

    public long totalValueAggregated;

    public int priorityLevel;

    public boolean is_from_CH;

    public boolean is_from_group;

    public int aggregatorNodeID;
    public NCS_Location2D aggregatorNodeLocation;

    public NetAddress sinkIP;
    public NCS_Location2D sinkLocation;

    public MessageDharmawanDataValue() {
    }

    public int getSize() {
        return 17;
    }

    public void getBytes(byte[] msg, int offset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

