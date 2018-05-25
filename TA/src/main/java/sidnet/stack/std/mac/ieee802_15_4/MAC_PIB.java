/*
 * MAC_PIB.java
 *
 * Created on July 14, 2008, 1:54 PM
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

class MAC_PIB
{
     //attributes from Table 71
    byte	macAckWaitDuration;
    boolean	macAssociationPermit;
    boolean	macAutoRequest;
    boolean	macBattLifeExt;
    byte	macBattLifeExtPeriods;
    /*
    byte	macBeaconPayload[aMaxPHYPacketSize-(6+9+2+1)+1];	//beacon length in octets (w/o payload):
                                                                    //	max: 6(phy) + 15(mac) + 23 (GTSs) + 57 (pending addresses)
                                                                    //	min: 6(phy) + 9(mac) + 2 (GTSs) + 1 (pending addresses)
    */
    byte[]	macBeaconPayload = new byte[Const.aMaxPHYPacketSize-(6+9+2+1)+1];
    byte	macBeaconPayloadLength;
    byte	macBeaconOrder;
    int	macBeaconTxTime; // int	macBeaconTxTime:24; ???
    byte	macBSN;
    /* IE3ADDR */ int	macCoordExtendedAddress;
    int 	macCoordShortAddress;
    byte	macDSN;
    boolean	macGTSPermit;
    byte	macMaxCSMABackoffs;
    byte	macMinBE;
    int 	macPANId;
    boolean	macPromiscuousMode;
    boolean	macRxOnWhenIdle;
    int 	macShortAddress;
    byte	macSuperframeOrder;
    short	macTransactionPersistenceTime;
    //attributes from Table 72 (security attributes)
    MAC_ACL	macACLEntryDescriptorSet;
    byte	macACLEntryDescriptorSetSize;
    boolean	macDefaultSecurity;
    byte	macACLDefaultSecurityMaterialLength;
    Byte	macDefaultSecurityMaterial;
    byte	macDefaultSecuritySuite;
    byte	macSecurityMode;
    
    public MAC_PIB()
    {
        // Configure with default settings;
        MPIB();
    };
    
    public MAC_PIB( byte	macAckWaitDuration,
                    boolean	macAssociationPermit,
                    boolean	macAutoRequest,
                    boolean	macBattLifeExt,
                    byte	macBattLifeExtPeriods,
                    /*
                    byte	macBeaconPayload[aMaxPHYPacketSize-(6+9+2+1)+1];	//beacon length in octets (w/o payload):
                                                                                    //	max: 6(phy) + 15(mac) + 23 (GTSs) + 57 (pending addresses)
                                                                                    //	min: 6(phy) + 9(mac) + 2 (GTSs) + 1 (pending addresses)
                    */
                    byte[]	macBeaconPayload,
                    byte	macBeaconPayloadLength,
                    byte	macBeaconOrder,
                    int	macBeaconTxTime, // int	macBeaconTxTime:24; ???
                    byte	macBSN,
                    /* IE3ADDR */ short	macCoordExtendedAddress,
                    short	macCoordShortAddress,
                    byte	macDSN,
                    boolean	macGTSPermit,
                    byte	macMaxCSMABackoffs,
                    byte	macMinBE,
                    short	macPANId,
                    boolean	macPromiscuousMode,
                    boolean	macRxOnWhenIdle,
                    short	macShortAddress,
                    byte	macSuperframeOrder,
                    short	macTransactionPersistenceTime,
                    //attributes from Table 72 (security attributes)
                    MAC_ACL	macACLEntryDescriptorSet,
                    byte	macACLEntryDescriptorSetSize,
                    boolean	macDefaultSecurity,
                    byte	macACLDefaultSecurityMaterialLength,
                    Byte	macDefaultSecurityMaterial,
                    byte	macDefaultSecuritySuite,
                    byte	macSecurityMode
                  )
    {
        this.macAckWaitDuration = macAckWaitDuration;
        this.macAssociationPermit = macAssociationPermit;
        this.macAutoRequest = macAutoRequest;
        this.macBattLifeExt = macBattLifeExt;
        this.macBattLifeExtPeriods = macBattLifeExtPeriods;
        this.macBeaconPayload = macBeaconPayload;
        this.macBeaconPayloadLength = macBeaconPayloadLength;
        this.macBeaconOrder = macBeaconOrder;
        this.macBeaconTxTime = macBeaconTxTime; // int	macBeaconTxTime:24; ???
        this.macBSN = macBSN;
        this.macCoordExtendedAddress = macCoordExtendedAddress;
        this.macCoordShortAddress = macCoordShortAddress;
        this.macDSN = macDSN;
        this.macGTSPermit = macGTSPermit;
        this.macMaxCSMABackoffs = macMaxCSMABackoffs;
        this.macMinBE = macMinBE;
        this.macPANId = macPANId;
        this.macPromiscuousMode = macPromiscuousMode;
        this.macRxOnWhenIdle = macRxOnWhenIdle;
        this.macShortAddress = macShortAddress;
        this.macSuperframeOrder = macSuperframeOrder;
        this.macTransactionPersistenceTime = macTransactionPersistenceTime;
        //attributes from Table 72 (security attributes)
        this.macACLEntryDescriptorSet = macACLEntryDescriptorSet;
        this.macACLEntryDescriptorSetSize = macACLEntryDescriptorSetSize;
        this.macDefaultSecurity = macDefaultSecurity;
        this.macACLDefaultSecurityMaterialLength = macACLDefaultSecurityMaterialLength;
        this.macDefaultSecurityMaterial = macDefaultSecurityMaterial;
        this.macDefaultSecuritySuite = macDefaultSecuritySuite;
        this.macSecurityMode = macSecurityMode;
    }
    
    public void MPIB()
    {
        this.MAC_PIB2(Const.def_macAckWaitDuration,	Const.def_macAssociationPermit,
                Const.def_macAutoRequest,		Const.def_macBattLifeExt,
                Const.def_macBattLifeExtPeriods,	Const.def_macBeaconPayload,
                Const.def_macBeaconPayloadLength,	Const.def_macBeaconOrder,
                Const.def_macBeaconTxTime,		(byte)0/*Const.def_macBSN*/,
                Const.def_macCoordExtendedAddress,	Const.def_macCoordShortAddress,
                (byte)0/*Const.def_macDSN*/,            Const.def_macGTSPermit,
                Const.def_macMaxCSMABackoffs,		Const.def_macMinBE,
                Const.def_macPANId,			Const.def_macPromiscuousMode,
                Const.def_macRxOnWhenIdle,		Const.def_macShortAddress,
                Const.def_macSuperframeOrder,		Const.def_macTransactionPersistenceTime,
                Const.def_macACLEntryDescriptorSet,	Const.def_macACLEntryDescriptorSetSize,
                Const.def_macDefaultSecurity,		Const.def_macACLDefaultSecurityMaterialLength,
                Const.def_macDefaultSecurityMaterial,	Const.def_macDefaultSecuritySuite,
                Const.def_macSecurityMode);
    }
    
     private void MAC_PIB2( byte	macAckWaitDuration,
                    boolean	macAssociationPermit,
                    boolean	macAutoRequest,
                    boolean	macBattLifeExt,
                    byte	macBattLifeExtPeriods,
                    /*
                    byte	macBeaconPayload[aMaxPHYPacketSize-(6+9+2+1)+1];	//beacon length in octets (w/o payload):
                                                                                    //	max: 6(phy) + 15(mac) + 23 (GTSs) + 57 (pending addresses)
                                                                                    //	min: 6(phy) + 9(mac) + 2 (GTSs) + 1 (pending addresses)
                    */
                    byte[]	macBeaconPayload,
                    byte	macBeaconPayloadLength,
                    byte	macBeaconOrder,
                    int	macBeaconTxTime, // int	macBeaconTxTime:24; ???
                    byte	macBSN,
                    /* IE3ADDR */ short	macCoordExtendedAddress,
                    short	macCoordShortAddress,
                    byte	macDSN,
                    boolean	macGTSPermit,
                    byte	macMaxCSMABackoffs,
                    byte	macMinBE,
                    short	macPANId,
                    boolean	macPromiscuousMode,
                    boolean	macRxOnWhenIdle,
                    short	macShortAddress,
                    byte	macSuperframeOrder,
                    short	macTransactionPersistenceTime,
                    //attributes from Table 72 (security attributes)
                    MAC_ACL	macACLEntryDescriptorSet,
                    byte	macACLEntryDescriptorSetSize,
                    boolean	macDefaultSecurity,
                    byte	macACLDefaultSecurityMaterialLength,
                    Byte	macDefaultSecurityMaterial,
                    byte	macDefaultSecuritySuite,
                    byte	macSecurityMode
                  )
    {
        this.macAckWaitDuration = macAckWaitDuration;
        this.macAssociationPermit = macAssociationPermit;
        this.macAutoRequest = macAutoRequest;
        this.macBattLifeExt = macBattLifeExt;
        this.macBattLifeExtPeriods = macBattLifeExtPeriods;
        this.macBeaconPayload = macBeaconPayload;
        this.macBeaconPayloadLength = macBeaconPayloadLength;
        this.macBeaconOrder = macBeaconOrder;
        this.macBeaconTxTime = macBeaconTxTime; // int	macBeaconTxTime:24; ???
        this.macBSN = macBSN;
        this.macCoordExtendedAddress = macCoordExtendedAddress;
        this.macCoordShortAddress = macCoordShortAddress;
        this.macDSN = macDSN;
        this.macGTSPermit = macGTSPermit;
        this.macMaxCSMABackoffs = macMaxCSMABackoffs;
        this.macMinBE = macMinBE;
        this.macPANId = macPANId;
        this.macPromiscuousMode = macPromiscuousMode;
        this.macRxOnWhenIdle = macRxOnWhenIdle;
        this.macShortAddress = macShortAddress;
        this.macSuperframeOrder = macSuperframeOrder;
        this.macTransactionPersistenceTime = macTransactionPersistenceTime;
        //attributes from Table 72 (security attributes)
        this.macACLEntryDescriptorSet = macACLEntryDescriptorSet;
        this.macACLEntryDescriptorSetSize = macACLEntryDescriptorSetSize;
        this.macDefaultSecurity = macDefaultSecurity;
        this.macACLDefaultSecurityMaterialLength = macACLDefaultSecurityMaterialLength;
        this.macDefaultSecurityMaterial = macDefaultSecurityMaterial;
        this.macDefaultSecuritySuite = macDefaultSecuritySuite;
        this.macSecurityMode = macSecurityMode;
    }
    
   
    
    // Not in NS-2 - does a memcpy() equiv
    public static byte[] copy(byte[] src, int payloadLength)
    {
        byte[] dst = new byte[payloadLength];
        for (int i = 0; i < payloadLength; i++)
            dst[i] = src[i];
        
        return dst;
    }
};




