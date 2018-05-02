/*
 * GTSSpec.java
 *
 * Created on July 23, 2008, 10:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

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
class GTSSpec {
    GTSFields fields;

    byte count;		//GTS descriptor count
    boolean permit;		//GTS permit
    boolean[] recvOnly = new boolean[7];	//reception only
    byte[] slotStart   = new byte[7];	//starting slot
    byte[] length      = new byte[7];	//length in slots

    public void parse() {
            int i;
            count = (byte)((fields.spec & 0xe0) >> 5);
            permit = ((fields.spec & 0x01) != 0) ? true : false;
            for (i=0;i<count;i++) {
                    recvOnly[i] = ((fields.dir & (1<<(7-i))) != 0);
                    slotStart[i] = (byte)((fields.list[i].slotSpec & 0xf0) >> 4);
                    length[i] = (byte)((fields.list[i].slotSpec & 0x0f));
            }
    }

    public void setCount(byte cnt) {
            count = cnt;
            fields.spec = (byte)((fields.spec & 0x1f) + (cnt << 5));
    }
    
    public void setPermit(boolean pmt) {
            permit = pmt;
            fields.spec = (byte)((fields.spec & 0xfe));
            if (pmt) fields.spec += 1;
    }
    
    public void setRecvOnly(byte ith,boolean rvonly) {
            recvOnly[ith] = rvonly;
            fields.dir = (byte)(fields.dir & ((1<<(7-ith))^0xff));
            if (rvonly) fields.dir += (1<<(7-ith));
    }
    
    public void setSlotStart(byte ith,byte st) {
            slotStart[ith] = st;
            slotStart[ith] = (byte)((fields.list[ith].slotSpec & 0x0f) + (st << 4));
    }
    
    public void setLength(byte ith,byte len) {
            length[ith] = len;
            length[ith] = (byte)((fields.list[ith].slotSpec & 0xf0) + len);
    }
    
    public int size() {
            count = (byte)((fields.spec & 0xe0) >> 5);
            return (1 + 1 + 3 * count);
    }
};
