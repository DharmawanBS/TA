/*
 * Packet_t.java
 *
 * Created on July 9, 2008, 2:50 PM
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
public enum Packet_t // as a struct in NS-2
{
     PT_TCP,// = 0;
     PT_UDP,// = 1;
     PT_CBR,// = 2;
     PT_AUDIO,// = 3;
     PT_VIDEO,// = 4;
     PT_ACK,// = 5;
     PT_START,// = 6;
     PT_STOP,// = 7;
     PT_PRUNE,// = 8;
     PT_GRAFT,// = 9;
     PT_GRAFTACK,// = 10;
     PT_JOIN,// = 11;
     PT_ASSERT,// = 12;
     PT_MESSAGE,// = 13;
     PT_RTCP,// = 14;
     PT_RTP,// = 15;
     PT_RTPROTO_DV,// = 16;
     PT_CtrMcast_Encap,// = 17;
     PT_CtrMcast_Decap,// = 18;
     PT_SRM,// = 19;
            /* simple signalling messages */
     PT_REQUEST,// = 20;
     PT_ACCEPT,// = 21;
     PT_CONFIRM,// = 22;
     PT_TEARDOWN,// = 23;
     PT_LIVE,// = 24;   // packet from live network
     PT_REJECT,// = 25;

     PT_TELNET,// = 26; // not needed: telnet use TCP
     PT_FTP,// = 27;
     PT_PARETO,// = 28;
     PT_EXP,// = 29;
     PT_INVAL,// = 30;
     PT_HTTP,// = 31;

            /* new encapsulator */
     PT_ENCAPSULATED,// = 32;
     PT_MFTP,// = 33;

            /* CMU/Monarch's extnsions */
     PT_ARP,// = 34;
     PT_MAC,// = 35;
     PT_TORA,// = 36;
     PT_DSR,// = 37;
     PT_AODV,// = 38;
     PT_IMEP,// = 39;

            // RAP packets
     PT_RAP_DATA,// = 40;
     PT_RAP_ACK,// = 41;

     PT_TFRC,// = 42;
     PT_TFRC_ACK,// = 43;
     PT_PING,// = 44;

     PT_PBC,// = 45;
            // Diffusion packets - Chalermek
     PT_DIFF,// = 46;

            // LinkState routing update packets
     PT_RTPROTO_LS,// = 47;

            // MPLS LDP header
     PT_LDP,// = 48;

            // GAF packet
     PT_GAF,// = 49;

            // ReadAudio traffic
     PT_REALAUDIO,// = 50;

            // Pushback Messages
     PT_PUSHBACK,// = 51;

     // #ifdef HAVE_STL ???
     //       // Pragmatic General Multicast
     //PT_PGM = 52;
     // #endif //STL
            // LMS packets
     PT_LMS,// = 53;
     PT_LMS_SETUP,// = 54;

     PT_SCTP,// = 55;
     PT_SCTP_APP1,// = 56;

            // SMAC packet
     PT_SMAC,// = 57;
            // XCP packet
     PT_XCP,// = 58;

            // HDLC packet
     PT_HDLC,// = 59;

            // Bell Labs Traffic Trace Type (PackMime OL)
     PT_BLTRACE,// = 60;

            // insert new packet types here
     PT_NTYPE,// = 61; // This MUST be the LAST one
};
