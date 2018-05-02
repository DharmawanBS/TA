/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import sidnet.core.interfaces.Header;

/**
 *
 * @author Oliver
 */
public class hdr_mac implements Header, Serializable
{

    
	MacFrameType ftype_;	// frame type
	/* int */ int macSA_;		// source MAC address // ??? OLIVER: everywhere the address is represented on 16-bit
	/* int */ int macDA_;		// destination MAC address // ??? OLIVER: everywhere the address is represented on 16-bit
	int hdr_type_;        // mac_hdr type

	double txtime_;		// transmission time
	double sstime_;		// slot start time

	int padding_;

	public void set(MacFrameType ft, /* int */ short sa, /* int */ short da /* =-1 ???*/) // ??? OLIVER: everywhere the address is represented on 16-bit
        {
		ftype_ = ft;
		macSA_ = sa;
		if (da != -1)  macDA_ = da;
	}
	public MacFrameType ftype() { return ftype_; }
	public /* int */ int macSA() { return macSA_; } // ??? OLIVER: everywhere the address is represented on 16-bit
	public /* int */ int macDA() { return macDA_; } // ??? OLIVER: everywhere the address is represented on 16-bit
	public int hdr_type() {return hdr_type_; }

        public double txtime() { return txtime_; }
	double sstime() { return sstime_; }

	// Header access methods
	//static int offset_;
	//inline static int& offset() { return offset_; }
	//inline static hdr_mac* access(const Packet* p) {
	//	return (hdr_mac*) p->access(offset_);
	//}
        
        public hdr_mac copy()
        {
            hdr_mac copy = new hdr_mac();
            
            copy.ftype_     = ftype_;       // frame type
            copy.macSA_     = macSA_;       // source MAC address
            copy.macDA_     = macDA_;       // destination MAC address
            copy.hdr_type_  = hdr_type_;    // mac_hdr type

            copy.txtime_    = txtime_;      // transmission time
            copy.sstime_    = sstime_;      // slot start time

            copy.padding_   = padding_;
            
            return copy;
        }
        
    public void getBytes(byte[] hdr, int offset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getSize() {
        return 
                4 + // ftype_;	// frame type
                2 + // macSA_;		// source MAC address // ??? OLIVER: everywhere the address is represented on 16-bit
                2 + // macDA_;		// destination MAC address // ??? OLIVER: everywhere the address is represented on 16-bit
                2 ; // hdr_type_;        // mac_hdr type
    }
        
    public char[] serialize()
        {   
            hdr_mac object = this;
            
              try {
                    // Serialize to a byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bos) ;
                    out.writeObject(object);
                    out.close();

                    // Get the bytes of the serialized object
                    byte[] buf = bos.toByteArray();
                    char[] cbuf = new char[buf.length];
                    
                    for (int i = 0; i < buf.length; i++)
                        cbuf[i] = (char)buf[i];
                    
                    return cbuf;
                } 
                catch (IOException e) 
                {
                }
              return null;
        }
        
        public static hdr_mac unserialize(byte[] stream)
        {
            // Serialization
            class FixedStream extends InputStream
            {
                private byte[] stream;
                private int index = -1;

                public FixedStream(byte[] stream)
                {
                    this.stream = stream;
                }
                public int available()
                {
                    return stream.length;
                }
                public int read()
                {
                    index ++;
                    return (int)stream[index];
                }
            }
            
            ObjectInputStream objIn = null;
            try
            {
                objIn = new ObjectInputStream(new FixedStream(stream));
            } catch (IOException ex)
            {
                ex.printStackTrace();
            } 
                
            if (objIn != null)
            {
                try
                {
                    return (hdr_mac) objIn.readObject();
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex)
                {
                    ex.printStackTrace();
                }
            }
            else
                System.err.println("[SIDNET][MacMessage_802_15_4]<ERROR>: Unable to unserialize");

            return null;
         };
};
