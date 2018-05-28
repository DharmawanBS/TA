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

    public int priorityLevel;

    public int queryID;

    public int fromRegion;

    public NetAddress sinkIP;
    public NCS_Location2D sinkLocation;
    
    public MessageDharmawanDataValue() {
        
    }
    
    /** {@inheritDoc} */
    public int getSize() { 
        return 17;
    }
    /** {@inheritDoc} */
    public void getBytes(byte[] b, int offset) {
        throw new RuntimeException("not implemented");
    }
}