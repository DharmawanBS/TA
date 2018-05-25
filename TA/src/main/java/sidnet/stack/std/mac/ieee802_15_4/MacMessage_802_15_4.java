/*
 * MacMessage_802_15_4.java
 *
 * Created on July 1, 2008, 2:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import jist.swans.misc.Message;
import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;


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
public class MacMessage_802_15_4 implements Message
{
    // Headers
    private hdr_lrwpan headerLRWPAN;
    private hdr_cmn headerCMN;
    private hdr_mac headerMAC;
    
    // the pkt stamp carries all info about how/where the pkt
    // was sent needed for a receiver to determine if it correctly
    // receives the pkt
    PacketStamp	txinfo_;  
    
    // Not in NS-2
    private Message payload;
    
    /** Creates a new instance of MacMessage_802_15_4 */
    public MacMessage_802_15_4()
    {
        txinfo_ = new PacketStamp();
        headerLRWPAN = new hdr_lrwpan();
        headerCMN    = new hdr_cmn();
        headerMAC    = new hdr_mac();
    }
    
    // Not in NS-2
    public void setPayload(Message msg)
    {
        this.payload = msg;
        
        // setting the packet (payload) size
        headerCMN.size_ = msg.getSize(); // bytes
    }
    
    // Not in NS-2
    public Message getPayload()
    {
        return payload;
    }
    
    public void setHDR_LRWPAN(hdr_lrwpan headerLRWPAN)
    {
        this.headerLRWPAN = headerLRWPAN;
    }
    
    public hdr_lrwpan HDR_LRWPAN()
    {
        return headerLRWPAN;
    }
    
    public void setHDR_CMN(hdr_cmn headerCMN)
    {
        this.headerCMN = headerCMN;
    }
    
    public hdr_cmn HDR_CMN()
    {
        return headerCMN;
    }
    
    public void setHDR_MAC(hdr_mac headerMAC)
    {
        this.headerMAC = headerMAC;
    }
    
    public hdr_mac HDR_MAC()
    {
        return headerMAC;
    }
    
    public MacMessage_802_15_4 copy() {
        MacMessage_802_15_4 copy = new MacMessage_802_15_4();
        copy.payload             = payload;
        copy.headerLRWPAN        = headerLRWPAN.copy();
        copy.headerCMN           = headerCMN.copy();
        copy.headerMAC           = headerMAC.copy();
        
        return copy;
    }
    
    /** {@inheritDoc} SWANS*/
    public void getBytes(byte[] b, int offset)
    {
        throw new RuntimeException("not implemented");
    }
    
     public int getSize()        // returns the size in [bits]
     {
         return  
                 (headerLRWPAN == null ? 0 : headerLRWPAN.getSize()) + 
                 (headerCMN    == null ? 0 : headerCMN.getSize()) + 
                 (headerMAC    == null ? 0 : headerMAC.getSize()) + 
                 (payload      == null ? 0 : payload.getSize());
     }
};














    
