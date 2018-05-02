/*
 * SuperframeSpec.java
 *
 * Created on July 8, 2008, 11:16 AM
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
class SuperframeSpec			//refer to Figures 40,59
{
    int SuperSpec;		//(MSDU) Superframe Specification (Figure 40)
                                    // --(0123):	Beacon order
                                    // --(4567):	Superframe order
                                    // --(89ab):	Final CAP slot
                                    // --(c):	Battery life extension
                                    // --(d):	Reserved
                                    // --(e):	PAN Coordinator
                                    // --(f):	Association permit
    byte BO;
    int BI;
    byte SO;
    int SD;
    int sd;
    byte FinCAP;
    boolean BLE;
    boolean PANCoor;
    boolean AssoPmt;

    public void parse()
    {
            BO = (byte)((SuperSpec & 0xf000) >> 12);
            BI = (byte)(Const.aBaseSuperframeDuration * (1 << BO));
            SO = (byte)((SuperSpec & 0x0f00) >> 8);
            SD = Const.aBaseSuperframeDuration * (1 << SO);	//superframe duration
            sd = Const.aBaseSlotDuration * (1 << SO);		//slot duration
            FinCAP = (byte)((SuperSpec & 0x00f0) >> 4);
            BLE = ((SuperSpec & 0x0008) == 0)    ? false : true;
            PANCoor = ((SuperSpec & 0x0002) == 0)? false : true;
            AssoPmt = ((SuperSpec & 0x0001) == 0)? false : true;
    }

    public void setBO(byte bo)
    {
            BO = bo;
            BI = Const.aBaseSuperframeDuration * (1 << BO);
            SuperSpec =  ((SuperSpec & 0x0fff) + (bo << 12));
    }
    public void setSO(byte so)
    {
            SO = so;
            SD = Const.aBaseSuperframeDuration * (1 << SO);
            sd = Const.aBaseSlotDuration * (1 << SO);
            SuperSpec =  ((SuperSpec & 0xf0ff) + (so << 8));
    }
    public void setFinCAP(byte fincap)
    {
            FinCAP = fincap;
            SuperSpec =  ((SuperSpec & 0xff0f) + (fincap << 4));
    }
    public void setBLE(boolean ble)
    {
            BLE = ble;
            SuperSpec =  (SuperSpec & 0xfff7);
            if (ble) SuperSpec += 8;
    }
    public void setPANCoor(boolean pancoor)
    {
            PANCoor = pancoor;
            SuperSpec =  (SuperSpec & 0xfffd);
            if (pancoor) SuperSpec += 2;
    }
    public void setAssoPmt(boolean assopmt)
    {
            AssoPmt = assopmt;
            SuperSpec =  (SuperSpec & 0xfffe);
            if (assopmt) SuperSpec += 1;
    }
};
