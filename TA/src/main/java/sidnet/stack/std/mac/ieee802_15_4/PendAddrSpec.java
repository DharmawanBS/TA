/*
 * PendAddrSpec.java
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
class PendAddrSpec
{
    PendAddrFields fields;

    byte numShortAddr;	//num of short addresses pending
    byte numExtendedAddr;	//num of extended addresses pending

    //for constructing the fields
    public byte addShortAddr(int sa)
    {
            int i;

            if (numShortAddr + numExtendedAddr >= 7)
                    return (byte)((numShortAddr + numExtendedAddr));
            //only unique address added
            for (i=0;i<numShortAddr;i++)
            if (fields.addrList[i] == sa)
                    return (byte)((numShortAddr + numExtendedAddr));
            fields.addrList[numShortAddr] = sa;
            numShortAddr++;
            return (byte)(numShortAddr + numExtendedAddr);
    }
    public byte addExtendedAddr(int /*IE3ADDR*/ ea)
    {
            int i;

            if (numShortAddr + numExtendedAddr >= 7)
                    return (byte)(numShortAddr + numExtendedAddr);
            //only unique address added
            for (i=6;i>6-numExtendedAddr;i--)
            if (fields.addrList[i] == ea)
                    return (byte)(numShortAddr + numExtendedAddr);
            //save the extended address in reverse order
            fields.addrList[6-numExtendedAddr] = ea;
            numExtendedAddr++;
            return (byte)(numShortAddr + numExtendedAddr);
    }
    public void format()
    {
            //realign the addresses
            int i;
            int /*IE3ADDR*/ tmpAddr;
            //restore the order of extended addresses
            for (i=0;i<numExtendedAddr;i++)
            {
                    if ((7 - numExtendedAddr + i) < (6 - i))
                    {
                            tmpAddr = fields.addrList[7 - numExtendedAddr + i];
                            fields.addrList[7 - numExtendedAddr + i] = fields.addrList[6 - i];
                            fields.addrList[6 - i] = tmpAddr;
                    }
            }
            //attach the extended addresses to short addresses
            for (i=0;i<numExtendedAddr;i++)
                    fields.addrList[numShortAddr + i] = fields.addrList[7 - numExtendedAddr + i];
            //update address specification
            fields.spec = (byte)(((numShortAddr) << 5) + (numExtendedAddr << 1));
    }
    //for parsing the received fields
    public void parse()
    {
            numShortAddr = (byte)((fields.spec & 0xe0) >> 5);
            numExtendedAddr = (byte)((fields.spec & 0x0e) >> 1);
    }

    public int size()
    {
            parse();
            return (1 + numShortAddr * 2 + numExtendedAddr * 8);
    }
};
