/*
 * DevCapability.java
 *
 * Created on July 23, 2008, 10:43 AM
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
public class DevCapability
{
    byte cap;		//refer to Figure 49
                            // --(0):	alternate PAN coordinator
                            // --(1):	device type (1=FFD,0=RFD)
                            // --(2):	power source (1=mains powered,0=non mains powered)
                            // --(3):	receiver on when idle
                            // --(45):	reserved
                            // --(6):	security capability
                            // --(7):	allocate address (asking for allocation of a short address during association)

    boolean alterPANCoor;
    boolean FFD;
    boolean mainsPower;
    boolean recvOnWhenIdle;
    boolean secuCapable;
    boolean alloShortAddr;

    public void parse()
    {
            alterPANCoor = ((cap & 0x80) != 0)?true:false;
            FFD = ((cap & 0x40) != 0)?true:false;
            mainsPower = ((cap & 0x20) != 0)?true:false;
            recvOnWhenIdle = ((cap & 0x10) != 0)?true:false;
            secuCapable = ((cap & 0x02) != 0)?true:false;
            alloShortAddr = ((cap & 0x01) != 0)?true:false;
    }

    public void setAlterPANCoor(boolean alterPC)
    {
            alterPANCoor = alterPC;
            cap = (byte)(cap & 0x7f);
            if (alterPC) cap += 0x80;
    }
    public void setFFD(boolean ffd)
    {
            FFD = ffd;
            cap = (byte)(cap & 0xbf);
            if (ffd) cap += 0x40;
    }
    public void setMainPower(boolean mp)
    {
            mainsPower = mp;
            cap = (byte)(cap & 0xdf);
            if (mp) cap += 0x20;
    }
    public void setRecvOnWhenIdle(boolean onidle)
    {
            recvOnWhenIdle = onidle;
            cap = (byte)(cap & 0xef);
            if (onidle) cap += 0x10;
    }
    public void setSecuCapable(boolean sc)
    {
            secuCapable = sc;
            cap = (byte)(cap & 0xfd);
            if (sc) cap += 0x02;
    }
    public void setAlloShortAddr(boolean alloc)
    {
            alloShortAddr = alloc;
            cap = (byte)(cap & 0xfe);
            if (alloc) cap += 0x01;
    }
}
