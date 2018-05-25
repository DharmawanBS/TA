/*
 * MessageDataP2P.java
 *
 * Created on December 18, 2007, 3:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.users.dharmawan_route.app;

import jist.swans.misc.Message;

/**
 *
 * @author Oliver
 */
public class MessageDataValue implements Message {
public final double dataValue;
    public final int queryId;
    public final long sequenceNumber;
    public int producerNodeId;

    public String tipeSensor;

    public int fromRegion;
    
    public MessageDataValue(double dataValue) {
        this.dataValue = dataValue;
        queryId        = -1;
        sequenceNumber = -1;
    }
    
    public MessageDataValue(double dataValue, int queryId,
    					  long sequenceNumber, int producerNodeId) {
        this.dataValue = dataValue;
        this.queryId   = queryId;
        this.sequenceNumber = sequenceNumber;
        this.producerNodeId = producerNodeId;
    }
    
    /** {@inheritDoc} */
    public int getSize() 
    { 
      int size = 0;
      size += 4; // double dataValue;
      size += 2; // double sequenceNumber;
      size += 4; // double sequenceNumber;
      size += 2; // int producerNodeId;
  
      return size;
    }
    /** {@inheritDoc} */
    public void getBytes(byte[] b, int offset)
   {
      throw new RuntimeException("not implemented");
    }
  } // class: MessageP2P