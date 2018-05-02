/*
 * hdr_cmn.java
 *
 * Created on July 21, 2008, 2:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import sidnet.core.interfaces.Header;

/**
 *
 * @author Oliver
 * Java adaptation after NS-2 C++ implementation
 */
/*
 * Copyright (c) 2003-2004 Samsung Advanced Institute of Technology and
 * The City University of New York. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the Joint Lab of Samsung 
 *      Advanced Institute of Technology and The City University of New York.
 * 4. Neither the name of Samsung Advanced Institute of Technology nor of 
 *    The City University of New York may be used to endorse or promote 
 *    products derived from this software without specific prior written 
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE JOINT LAB OF SAMSUNG ADVANCED INSTITUTE
 * OF TECHNOLOGY AND THE CITY UNIVERSITY OF NEW YORK ``AS IS'' AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN 
 * NO EVENT SHALL SAMSUNG ADVANCED INSTITUTE OR THE CITY UNIVERSITY OF NEW YORK 
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class hdr_cmn implements Header
{
    // NS-2: enum dir_t {DOWN = -1, NONE = 0, UP = 1}
    enum dir_t{ DOWN, NONE, UP };
    //public static final byte DOWN = -1;
    //public static final byte NONE = 0;
    //public static final byte UP   = 1;
    
    public Packet_t ptype_;	// packet type (see above)
    public int size_;       // simulated packet size
    public int uid_;		// unique id
    public int error_;		// error flag
    public int iface_;		// receiving interface (label)
    public int lastHopAddr;       // OLIVER: not in NS-2
    
    dir_t	direction_;	// direction: 0=none, 1=up, -1=down
    
    public boolean xmit_failure_; // sinals failed transmission
    public short xmit_reason_; // reason for failure.
    
    // filled in by GOD on first transmission, used for trace analysis
    public int num_forwards_;	// how many times this pkt was forwarded
    
    // tx time for this packet in sec
    private double txtime_;
    
    //Monarch extn begins
    public /*nsaddr_t*/ int next_hop_;	// next hop for this packet

    public Packet_t ptype()
    {
        return ptype_;
    }
    
    public void setPtype(Packet_t ptype)
    {
        this.ptype_ = ptype;
    }
    
    public int size()
    {
        return size_;
    }
    
    public void setSize( int size)
    {
        size_ = size;
    }
    
    public int uid()
    {
        return uid_;
    }
    
    public void setUid(int uid)
    {
        uid_ = uid;
    }
    
    public int error()
    {
        return error_;
    }
    
    public void setError(int error)
    {
        error_ = error;
    }
    
    public dir_t direction()
    {
        return direction_;
    }
    
    public void setDirection(dir_t direction)
    {
        direction_ = direction;
    }
    
    public double txtime()
    {
        return txtime_;
    }
    
    public void setTxtime(double txtime)
    {
        txtime_ = txtime;
    }

    public hdr_cmn copy()
    {
        hdr_cmn copy = new hdr_cmn();

        copy.ptype_     = ptype_;
        copy.size_      = size_;
        copy.uid_       = uid_;
        copy.error_     = error_;
        copy.iface_     = iface_;
        copy.lastHopAddr = lastHopAddr;
        copy.direction_      = direction_;
        copy.num_forwards_ = num_forwards_;
        copy.next_hop_  = next_hop_;
        
        // tx time for this packet in sec
        copy.txtime_    = txtime_;
                
        return copy;
    }
    
       public void getBytes(byte[] hdr, int offset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getSize() {
        return             
            4 + // uid_;		// unique id
            4 + // error_;		// error flag
            4 + // iface_;		// receiving interface (label)
            2 + // lastHopAddr;       // OLIVER: not in NS-2
            2 ; // next_hop_;	// next hop for this packet      
    }
}
