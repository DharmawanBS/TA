/*
 * Const.java
 *
 * Created on July 1, 2008, 4:31 PM
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
public class Const
{
    //---PHY layer constants (Table 18)---
    public static final byte aMaxPHYPacketSize            = 127;        //max PSDU size (in bytes) the PHY shall be able to receive
    public static final byte aTurnaroundTime              = 12;		//Rx-to-Tx or Tx-to-Rx max turnaround time (in symbol period)

    //---PHY_PIB default values---
    //All the default values are not given in the draft.
    //They are chosen for sake of a value
    public static final int def_phyCurrentChannel           = 11;       // ???? int ????
    public static final long def_phyChannelsSupported       = 134217727;    // ???? long ???? 0x07ffffff; ????
    public static final int def_phyTransmitPower            = 0;        // ???? int ????
    public static final int def_phyCCAMode                  = 1;        // ???? int ????

    //---MAC sublayer constants (Table 70)---
    public static final byte aNumSuperframeSlots          = 16;		//# of slots contained in a superframe
    public static final byte aBaseSlotDuration            = 60;		//# of symbols comprising a superframe slot of order 0
    public static final short aBaseSuperframeDuration     = aBaseSlotDuration * aNumSuperframeSlots; //# of symbols comprising a superframe of order 0
    //aExtendedAddress                                      = ;		//64-bit (IEEE) address assigned to the device (device specific)
    public static final byte aMaxBE                       = 5;		//max value of the backoff exponent in the CSMA-CA algorithm
    public static final  byte aMaxBeaconOverhead          = 75;		//max # of octets added by the MAC sublayer to the payload of its beacon frame
    public static final  byte aMaxBeaconPayloadLength     = aMaxPHYPacketSize - aMaxBeaconOverhead; //max size, in octets, of a beacon payload
    public static final  byte aGTSDescPersistenceTime     = 4;		//# of superframes that a GTS descriptor exists in the beacon frame of a PAN coordinator
    public static final  byte aMaxFrameOverhead           = 25;		//max # of octets added by the MAC sublayer to its payload w/o security.
    public static final  short aMaxFrameResponseTime      = 1220;		//max # of symbols (or CAP symbols) to wait for a response frame
    public static final  byte aMaxFrameRetries            = 3;		//max # of retries allowed after a transmission failures
    public static final  byte aMaxLostBeacons             = 4;		//max # of consecutive beacons the MAC sublayer can miss w/o declaring a loss of synchronization
    public static final  byte aMaxMACFrameSize            = aMaxPHYPacketSize - aMaxFrameOverhead; //max # of octets that can be transmitted in the MAC frame payload field
    public static final  byte aMaxSIFSFrameSize           = 18;		//max size of a frame, in octets, that can be followed by a SIFS period
    public static final  short aMinCAPLength              = 440;		//min # of symbols comprising the CAP
    public static final  byte aMinLIFSPeriod              = 40;		//min # of symbols comprising a LIFS period
    public static final  byte aMinSIFSPeriod              = 12;		//min # of symbols comprising a SIFS period
    public static final  short aResponseWaitTime          = 32 * aBaseSuperframeDuration;		//max # of symbols a device shall wait for a response command following a request command
    public static final  byte aUnitBackoffPeriod          = 20;		//# of symbols comprising the basic time period used by the CSMA-CA algorithm

    //---MAC_PIB default values (Tables 71,72)---
    //attributes from Table 71
    public static final byte def_macAckWaitDuration         = 54; // ??? int ???	//22(ack) + 20(backoff slot) + 12(turnaround); propagation delay ignored?
    public static final boolean def_macAssociationPermit    = false;
    public static final boolean def_macAutoRequest          = true;
    public static final boolean def_macBattLifeExt          = false;
    public static final byte def_macBattLifeExtPeriods      = 6;  // ??? int ???
    public static final byte[] def_macBeaconPayload         = new byte[1]; //'\0';// \0 means empty character in Java // ??? char ???
    public static final byte def_macBeaconPayloadLength     = 0 ; // ??? int ???
    public static final byte def_macBeaconOrder             = 15; // ??? int ???
    public static final int def_macBeaconTxTime             = 0x000000;
    //#define def_macBSN				Random.random() % 0x100
    //#define def_macCoordExtendedAddress		0xffffffffffffffffLL
    public static final short def_macCoordExtendedAddress   = (short)0xffff;			
                                                                            //not defined in draft
    public static final short def_macCoordShortAddress      = (short)0xffff;
    //#define def_macDSN				Random.random() % 0x100
    public static final boolean def_macGTSPermit            = true;
    public static final byte def_macMaxCSMABackoffs         = 4; // ??? int ???
    public static final byte def_macMinBE                   = 3; // ??? int ???
    public static final short def_macPANId                  = (short) 0xffff;
    public static final boolean def_macPromiscuousMode      = false;
    public static final boolean def_macRxOnWhenIdle         = false;
    public static final short def_macShortAddress           = (short) 0xffff;
    public static final byte def_macSuperframeOrder         = 15; 
    public static final short def_macTransactionPersistenceTime	= 500; // ??? short ??? 0x01f4
    //attributes from Table 72 (security attributes)
    public static final MAC_ACL def_macACLEntryDescriptorSet = null; // ??? Object ??? null
    public static final byte def_macACLEntryDescriptorSetSize = (byte)0x00;
    public static final boolean def_macDefaultSecurity      = false;
    public static final byte def_macACLDefaultSecurityMaterialLength = (byte)0x15;
    public static final Byte def_macDefaultSecurityMaterial = null; // ??? Object ??? null
    public static final byte def_macDefaultSecuritySuite    = (byte)0x00; 
    public static final byte def_macSecurityMode            = (byte)0x00; 
    
    //--MAC frame control field (leftmost bit numbered 0)---
    //types (3 bits) -- we reverse the bit order for convenient operation
    public static final byte defFrmCtrl_Type_Beacon		= (byte)0x00;
    public static final byte defFrmCtrl_Type_Data		= (byte)0x04;
    public static final byte defFrmCtrl_Type_Ack		= (byte)0x02;
    public static final byte defFrmCtrl_Type_MacCmd		= (byte)0x06;
    //dest/src addressing mode (2 bits) -- we reverse the bit order for convenient operation
    public static final byte defFrmCtrl_AddrModeNone    = (byte)0x00;
    public static final byte defFrmCtrl_AddrMode16		= (byte)0x01;
    public static final byte defFrmCtrl_AddrMode64		= (byte)0x03;
    
    // Transac
    public static final int maxNumTransactions = 	70;
    
    public static final double max_pDelay = 100.0/200000000.0;	//maximum propagation delay
}
/* END *** CONSTANTS *** */
