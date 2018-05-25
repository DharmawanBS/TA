/*
 * Trace.java
 *
 * Created on July 1, 2008, 3:25 PM
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
public class Trace
{
    /* Not members in NS-2 */
    public static final byte MAC_Subtype_Command_AssoReq =  0x01;
    public static final byte MAC_Subtype_Command_AssoRsp =  0x02;
    public static final byte MAC_Subtype_Command_DAssNtf =  0x03;
    public static final byte MAC_Subtype_Command_DataReq =  0x04;
    public static final byte MAC_Subtype_Command_PIDCNtf =  0x05;
    public static final byte MAC_Subtype_Command_OrphNtf =  0x06;
    public static final byte MAC_Subtype_Command_BconReq =  0x07;
    public static final byte MAC_Subtype_Command_CoorRea =  0x08;
    public static final byte MAC_Subtype_Command_GTSReq  =  0x09;
    
    public static /* int */ int p802_15_4macSA(MacMessage_802_15_4 p) // ??? OLIVER: everywhere the address is represented on 16-bit
    {
        hdr_mac mh = p.HDR_MAC();
        return mh.macSA();
    }
    public static /* int */ int p802_15_4macDA(MacMessage_802_15_4 p)  // ??? OLIVER: everywhere the address is represented on 16-bit
    {
        hdr_mac mh = p.HDR_MAC();
        return mh.macDA();
    }
    
    public static int p802_15_4hdr_type(MacMessage_802_15_4 p)
    {
        hdr_mac mh = p.HDR_MAC();
        return mh.hdr_type();
    }
    
    public static int p802_15_4hdr_dst(/*char*/ char[] hdrc,  int  dst /*= -2*/) // char* // ??? OLIVER: everywhere the address is represented on 16-bit
    {
        byte[] hdr = new byte[hdrc.length];
        for (int i = 0; i < hdrc.length; i++)
            hdr[i] = (byte)hdrc[i];
        
        //hdr_mac mh = (hdr_mac) hdr;
        hdr_mac mh = hdr_mac.unserialize(hdr);
	if(dst > -2)
		mh.macDA_ = (short)dst; // ??? carefull
	return mh.macDA();
    }
    
    public static int p802_15_4hdr_src(/*char*/ char[] hdrc , int src /*= -2*/) // char* // ??? OLIVER: everywhere the address is represented on 16-bit
    {
        byte[] hdr = new byte[hdrc.length];
        for (int i = 0; i < hdrc.length; i++)
            hdr[i] = (byte)hdrc[i];
        
        //hdr_mac mh = (hdr_mac) hdr;
        hdr_mac mh = hdr_mac.unserialize(hdr);
	if(src > -2)
		mh.macSA_ = (short)src; // carefull
	return mh.macSA();
    }
    
    public static int p802_15_4hdr_type(/*char*/ char[] hdrc, int type /*= 0*/) // char*
    {
        byte[] hdr = new byte[hdrc.length];
        for (int i = 0; i < hdrc.length; i++)
            hdr[i] = (byte)hdrc[i];
         
        //hdr_mac mh = (hdr_mac) hdr;
        hdr_mac mh = hdr_mac.unserialize(hdr);
	if (type == 1) // NS-2: if (type)
		mh.hdr_type_ = type;
	return mh.hdr_type();
    }
    public static void p802_15_4hdrDATA(MacMessage_802_15_4 p)
    {
        ;	//nothing to do
    }
    public static void p802_15_4hdrACK(MacMessage_802_15_4 p)
    {
        ;	//nothing to do
    }
    public static void p802_15_4hdrBeacon(MacMessage_802_15_4 p)
    {
        ;	//nothing to do
    }
    public static void p802_15_4hdrCommand(MacMessage_802_15_4 p, short type)
    {	
        ;	//nothing to do
    }
    
    public static Packet_t /* char */ wpan_pName(MacMessage_802_15_4 p)
    {
        hdr_cmn ch = p.HDR_CMN();
	/* hdr_mac802_11 mh = p.HDR_MAC802_11();
	return
	((ch.ptype() == PT_MAC) ? (
	  (mh.dh_fc.fc_subtype == MAC_Subtype_Beacon) ? "M_BEACON"  :			//Beacon
	  (mh.dh_fc.fc_subtype == MAC_Subtype_Command_AssoReq) ? "M_CM1_AssoReq"  :	//CMD: Association request
	  (mh.dh_fc.fc_subtype == MAC_Subtype_Command_AssoRsp) ? "M_CM2_AssoRsp"  :	//CMD: Association response
	  (mh.dh_fc.fc_subtype == MAC_Subtype_Command_DAssNtf) ? "M_CM3_DisaNtf"  :	//CMD: Disassociation notification
	  (mh.dh_fc.fc_subtype == MAC_Subtype_Command_DataReq) ? "M_CM4_DataReq"  :	//CMD: Data request
	  (mh.dh_fc.fc_subtype == MAC_Subtype_Command_PIDCNtf) ? "M_CM5_PidCNtf"  :	//CMD: PAN ID conflict notification
	  (mh.dh_fc.fc_subtype == MAC_Subtype_Command_OrphNtf) ? "M_CM6_OrphNtf"  :	//CMD: Orphan notification
	  (mh.dh_fc.fc_subtype == MAC_Subtype_Command_BconReq) ? "M_CM7_Bcn-Req"  :	//CMD: Beacon request
	  (mh.dh_fc.fc_subtype == MAC_Subtype_Command_CoorRea) ? "M_CM8_CoorRal"  :	//CMD: Coordinator realignment
	  (mh.dh_fc.fc_subtype == MAC_Subtype_Command_GTSReq)  ?  "M_CM9_GTS-Req"  :	//CMD: GTS request
	  (mh.dh_fc.fc_subtype == MAC_Subtype_ACK) ? "M_ACK"  :			//Acknowledgement
	  "M_UNKN"
	  ) : packet_info.name(ch.ptype())); */
        return ch.ptype();
    }
}
/* END *** TRACE *** */
