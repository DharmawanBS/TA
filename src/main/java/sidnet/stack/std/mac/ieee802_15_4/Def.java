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
package sidnet.stack.std.mac.ieee802_15_4;

import jist.swans.mac.MacAddress;

/**
 *
 * @author Oliver
 * Java adaptation after NS-2 C++ implementation
 */


public class Def {
    // Enabling debuggin
    public static final boolean DEBUG802_15_4 = false;
    public static final boolean DEBUG802_15_4_packetdrop = false;
    public static final boolean DEBUG802_15_4_rxTotNum = false;
    public static final boolean DEBUG802_15_4_err = false;
    public static final boolean DEBUG802_15_4_timer = false;
    public static final boolean DEBUG802_15_4_ack = false;
    public static final boolean DEBUG802_15_4_packetsize = false;
    public static final boolean DEBUG802_15_4_transmissiontime = false;
    public static final boolean DEBUG802_15_4_pump = false;
    //public static final MacAddress DEBUG802_15_4_nodeid =  new MacAddress(332);
    public static final MacAddress DEBUG802_15_4_nodeid =  MacAddress.NULL;
    
    
    // Enable support for ZigBee. By default, this is disabled
    public static final boolean ZigBeeIF = false;
    
    //---Frequency bands and data rates (Table 1)---
    public static final byte BR_868M	= (byte)20;	//20 kbit/s	-- ch 0
    public static final byte BR_915M	= (byte)40;	//40 kbit/s	-- ch 1,2,3,...,10
    public static final byte BR_2_4G	= (byte)250;//250 kbit/s	-- ch 11,12,13,...,26
    public static final byte SR_868M	= 20;		//20 ks/s
    public static final byte SR_915M	= 40;		//40 ks/s
    public static final double SR_2_4G	= 62.5;		//62.5 ks/s

    //---PHY header---
    public static final int  defSHR_PreSeq     = 0x00000000;
    public static final byte defSHR_SFD        = (byte)0xe5;
    public static final byte defPHY_HEADER_LEN = 6;
    
    // Transactions
    public static final int tr_oper_del	= 1;
    public static final int tr_oper_est	= 2;
    public static final int tr_oper_EST	= 3;
    
    // SSCS
    public static final byte sscsTP_startPANCoord = 1;
    public static final byte sscsTP_startDevice = 2;
    
    // HLIST
    public static final int hl_oper_del = 1;
    public static final int hl_oper_est = 2;
    public static final int hl_oper_rpl = 3;
    
    // FAIL
    public static final int fl_oper_del = 1;
    public static final int fl_oper_est = 2;
    
    // MAC (NS-2)
    public static final int MAC_BROADCAST = ((int) 0xffffffff);
    
    // MAC802_15_4 task pending (callback)
    /*public static final byte TP_mcps_data_request	 =	(byte)1;
    public static final byte TP_mlme_associate_request	 =      (byte)2;
    public static final byte TP_mlme_associate_response	 =      (byte)3;
    public static final byte TP_mlme_disassociate_request = 	(byte)4;
    public static final byte TP_mlme_orphan_response      =	(byte)5;
    public static final byte TP_mlme_reset_request	 =	(byte)6;
    public static final byte TP_mlme_rx_enable_request	 =      (byte)7;
    public static final byte TP_mlme_scan_request	 = 	(byte)8;
    public static final byte TP_mlme_start_request	 = 	(byte)9;
    public static final byte TP_mlme_sync_request	 = 	(byte)10;
    public static final byte TP_mlme_poll_request	 =	(byte)11;
    public static final byte TP_CCA_csmaca		 = 	(byte)12;
    public static final byte TP_RX_ON_csmaca		 = 	(byte)13;*/
};











/* END *** DEFINITIONS *** */
