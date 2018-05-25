/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.users.dharmawan_route.routing;

import jist.swans.misc.Message;

import sidnet.core.misc.Location2D;
import sidnet.core.misc.Region;

/**
 *
 * @author invictus
 */
public class ProtocolMessageWrapper implements Message {
    private Message payload;

    // if IP of the source is now known, you can provide target location
    // or a target area (surrounded by a polygon/region)
    private Location2D targetLocation;
    private Region 	   targetRegion;

    // for tracking
    private long s_seq;      // sequence number to filter duplicates
    private long timeSent;

    public ProtocolMessageWrapper() {
    	payload 	   = null;
    	targetLocation = null;
    	s_seq          = -1;
    	timeSent       = 0;
    }

    public ProtocolMessageWrapper(Message payload) {
    	this.payload 	   = payload;
    	targetLocation = null;
    	s_seq          = -1;
    	timeSent       = 0;
    }

    public ProtocolMessageWrapper(Message payload, Location2D targetLocation,
    						 long s_seq, long timeSent) {
    	this.payload  	    = payload;
    	this.targetLocation = targetLocation;
    	this.s_seq     		= s_seq;
        this.timeSent  		= timeSent;
    }

    public ProtocolMessageWrapper(Message payload, Region targetRegion,
    						 long s_seq, long timeSent) {
    	this.payload  	    = payload;
    	this.targetLocation = null;
    	this.targetRegion   = targetRegion;
    	this.s_seq     		= s_seq;
    	this.timeSent  		= timeSent;
}

    public Message    getPayload()		 { return payload;       }
    public Location2D getTargetLocation(){ return targetLocation;}
    public Region     getTargetRegion()  { return targetRegion;  }
    public void setTargetLocation(Location2D targetLocation) {
    	this.targetLocation = targetLocation;
    };

    /** {@inheritDoc} */
    public int getSize() {
        int size = 0;
        if (payload != null)
            size += payload.getSize();
        if (targetRegion != null)
            size += targetRegion.getAsMessageSize();
        if (targetLocation != null)
            size += 4;
        size += 4;  // long s_seq;
        size += 4;  // long timeSent

        return size;
    }

    /** {@inheritDoc} */
    public void getBytes(byte[] b, int offset) {
        throw new RuntimeException("not implemented");
    }

    public ProtocolMessageWrapper copy () {
    	ProtocolMessageWrapper newMsg = new ProtocolMessageWrapper();
        newMsg.payload = payload;
        newMsg.targetLocation = targetLocation;
        newMsg.targetRegion = targetRegion;
        newMsg.setS_seq(getS_seq());
        newMsg.timeSent = timeSent;

        return newMsg;
    }

    /**
     * @return the s_seq
     */
    public long getS_seq() {
        return s_seq;
    }

    /**
     * @param s_seq the s_seq to set
     */
    public void setS_seq(long s_seq) {
        this.s_seq = s_seq;
    }
}
