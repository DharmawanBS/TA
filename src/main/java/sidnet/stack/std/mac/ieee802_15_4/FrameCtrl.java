/*
 * FrameCtrl.java
 *
 * Created on July 23, 2008, 10:46 AM
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
public class FrameCtrl
{
    int FrmCtrl;		//(PSDU/MPDU) Frame Control (Figure 35)
                                    // --leftmost bit numbered 0 and transmitted first
                                    // --(012):	Frame type (Table 65)
                                    //		 --(210)=000:		Beacon
                                    //		 --(210)=001:		Data
                                    //		 --(210)=010:		Ack
                                    //		 --(210)=110:		MAC command
    							    //       --(210)=100:       Data
                                    //		 --(210)=others:	Reserved
                                    // --(3):	Security enabled
                                    // --(4):	Frame pending
                                    // --(5):	Ack. req.
                                    // --(6):	Intra PAN
                                    // --(789):	Reserved
                                    // --(ab):	Dest. addressing mode (Table 66)
                                    //		 --(ba)=00:	PAN ID and Addr. field not present
                                    //		 --(ba)=01:	Reserved
                                    //		 --(ba)=10:	16-bit short address
                                    //		 --(ba)=11:	64-bit extended address
                                    // --(cd):	Reserved
                                    // --(ef):	Source addressing mode (see Dest. addressing mode)
    byte frmType;
    boolean secu;
    boolean frmPending;
    boolean ackReq;
    boolean intraPan;
    byte dstAddrMode;
    byte srcAddrMode;
    
    public String getType() {
    	
    	switch(frmType) {
    		case Const.defFrmCtrl_Type_Beacon : return "[Beacon]";
    		case Const.defFrmCtrl_Type_Data : return "[DATA]";
    		case Const.defFrmCtrl_Type_Ack : return "[ACK]";
    		case Const.defFrmCtrl_Type_MacCmd : return "[mac command]";
    		default : return "[others - reserved]";
    	}
    }

    public void parse() {
        frmType = (byte)((FrmCtrl & 0xe000) >> 13);
        secu = ((FrmCtrl & 0x1000) == 0)?false:true;
        frmPending = ((FrmCtrl & 0x0800) == 0)?false:true;
        ackReq = ((FrmCtrl & 0x0400) == 0)?false:true;
        intraPan = ((FrmCtrl & 0x0200) == 0)?false:true;
        dstAddrMode = (byte)((FrmCtrl & 0x0030) >> 4);
        srcAddrMode = (byte)((FrmCtrl & 0x0003));
    }

    public void setFrmType(byte frmtype) {
            frmType = frmtype;
            FrmCtrl =  ((FrmCtrl & 0x1fff) + (frmtype << 13));
    }
    
    public void setSecu(boolean sc) {
            secu = sc;
            FrmCtrl =  (FrmCtrl & 0xefff);
            if (sc) FrmCtrl += 0x1000;
    }
    
    public void setFrmPending(boolean pending) {
            frmPending = pending;
            FrmCtrl =  (FrmCtrl & 0xf7ff);
            if (pending) FrmCtrl += 0x0800;
    }
    
    public void setAckReq(boolean ack) {
            ackReq = ack;
            FrmCtrl =  (FrmCtrl & 0xfbff);
            if (ack) FrmCtrl += 0x0400;
    }
    
    public void setIntraPan(boolean intrapan) {
            intraPan = intrapan;
            FrmCtrl =  (FrmCtrl & 0xfdff);
            if (intrapan) FrmCtrl += 0x0200;
    }
    
    public void setDstAddrMode(byte dstmode) {
            dstAddrMode = dstmode;
            FrmCtrl =  ((FrmCtrl & 0xffcf) + (dstmode << 4));
    }
    
    public void setSrcAddrMode(byte srcmode) {
            srcAddrMode = srcmode;
            FrmCtrl =  ((FrmCtrl & 0xfffc) + srcmode);
    }
};