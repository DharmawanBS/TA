/*
 * hdr_lrwpan.java
 *
 * Created on July 17, 2008, 11:43 AM
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
public class hdr_lrwpan 
implements Header {
    //---PHY header---
    int	        SHR_PreSeq;
    byte		SHR_SFD;
    int 		PHR_FrmLen;

    //---MAC header---
    int 		MHR_FrmCtrl;
    byte		MHR_BDSN;
    panAddrInfo         MHR_DstAddrInfo;
    panAddrInfo         MHR_SrcAddrInfo;

    //---PHY layer---
    byte	ppduLinkQuality;
    double rxTotPower;

    //---MAC sublayer---
    int         	MFR_FCS;
    int  		MSDU_SuperSpec;
    GTSFields           MSDU_GTSFields;
    PendAddrFields	MSDU_PendAddrFields;
    byte		MSDU_CmdType;
    byte		MSDU_PayloadLen;
    int 		pad;	
    
    
    byte[]		MSDU_Payload = new byte[Const.aMaxMACFrameSize];		//MSDU_BeaconPL/MSDU_DataPL/MSDU_CmdPL
    boolean		SecurityUse;
    byte		ACLEntry;

    //---SSCS entity---
    byte		msduHandle;

    //---Other---
    boolean		setSN;		//SN already been set
    byte		phyCurrentChannel;
    boolean		indirect;	//this is a pending packet (indirect transmission)
    int		uid;		//for debug purpose
    int 		clusTreeDepth;
    int 		clusTreeParentNodeID;
   


    //---Packet header access functions---
    //static int offset_;
    //inline static int& offset() {return offset_;}
    //inline static hdr_lrwpan* access(const Packet* p)
    //{
    //	return (hdr_lrwpan*) p->access(offset_);
    //}
    
    public hdr_lrwpan() {
         MHR_DstAddrInfo = new panAddrInfo();
         MHR_SrcAddrInfo = new panAddrInfo();
         MSDU_GTSFields  = new GTSFields();
         MSDU_PendAddrFields = new PendAddrFields();
         
    }
    
    public hdr_lrwpan copy() {
        hdr_lrwpan copy = new hdr_lrwpan();
        
         //---PHY header---
        copy.SHR_PreSeq           = SHR_PreSeq;
        copy.SHR_SFD              = SHR_SFD;
        copy.PHR_FrmLen           = PHR_FrmLen;

        //---MAC header---
        copy.MHR_FrmCtrl          = MHR_FrmCtrl;
        copy.MHR_BDSN             = MHR_BDSN;
        copy.MHR_DstAddrInfo      = MHR_DstAddrInfo;
        copy.MHR_SrcAddrInfo      = MHR_SrcAddrInfo;

        //---PHY layer---
        copy.ppduLinkQuality      = ppduLinkQuality;
        copy.rxTotPower           = rxTotPower;

        //---MAC sublayer---
        copy.MFR_FCS              = MFR_FCS;
        copy.MSDU_SuperSpec       = MSDU_SuperSpec;
        copy.MSDU_GTSFields       = MSDU_GTSFields;
        copy.MSDU_PendAddrFields  = MSDU_PendAddrFields;
        copy.MSDU_CmdType         = MSDU_CmdType;
        copy.MSDU_PayloadLen      = MSDU_PayloadLen;
        copy.pad                  = pad;
        for (int i = 0; i < MSDU_Payload.length; i++)
            copy.MSDU_Payload[i]  = MSDU_Payload[i];
        copy.SecurityUse          = SecurityUse;
        copy.ACLEntry             = ACLEntry;

        //---SSCS entity---
        copy.msduHandle           = msduHandle;

        //---Other---
        copy.setSN                = setSN;
        copy.phyCurrentChannel    = phyCurrentChannel;
        copy.indirect             = indirect;
        copy.uid                  = uid;
        copy.clusTreeDepth        = clusTreeDepth;
        copy.clusTreeParentNodeID = clusTreeParentNodeID;
        return copy;
    }
    
    public void getBytes(byte[] hdr, int offset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getSize() {
        return 
                4 + // SHR_PreSeq
                1 + // SHR_SFD
                1 + // PHR_FrmLen
                2 + // MHR_FrmCtrl
                1 + // MHR_BDSN
                (MHR_DstAddrInfo == null ? 0 : MHR_DstAddrInfo.getSize()) + 
                (MHR_SrcAddrInfo == null ? 0 : MHR_SrcAddrInfo.getSize()) +
                1 + // ppduLinkQuality
                4 + // rxTotPower
                2 + // MFR_FCS;
                2 + // MSDU_SuperSpec;
                (MSDU_GTSFields == null ? 0 : MSDU_GTSFields.getSize()) +
                (MSDU_PendAddrFields == null ? 0 : MSDU_PendAddrFields.getSize()) + 
                1 + //MSDU_CmdType;
                1 + //MSDU_PayloadLen;
                2 + //pad;	
                (MSDU_Payload == null ? 0 :MSDU_Payload.length) + //
                1 + // boolean		SecurityUse; setSN, indirect
                1 + // ACLEntry;

                    // ---SSCS entity---
                1 + // msduHandle;

                    //---Other---                                 
                1 + // phyCurrentChannel;                       
                2 + // clusTreeDepth;
                2 ; // clusTreeParentNodeID;
    }
}
    