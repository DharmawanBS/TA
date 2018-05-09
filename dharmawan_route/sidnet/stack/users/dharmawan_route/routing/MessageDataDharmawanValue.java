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
public class MessageDataDharmawanValue implements Message {
    public NCS_Location2D from;
    public int nodeId;
    
    public NetAddress sinkIP;

    public NCS_Location2D to;
    
    public double dataValue;
    public int queryId;
    
    public MessageDataDharmawanValue() {
        
    }
    
    /** {@inheritDoc} */
    public int getSize() { 
        int size = 0;
        size += 4; // double dataValue;
        
        return size;
    }
    /** {@inheritDoc} */
    public void getBytes(byte[] b, int offset) {
        throw new RuntimeException("not implemented");
    }
}