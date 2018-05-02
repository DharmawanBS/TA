/*
 * p802_15_4sscs.java
 *
 * Created on July 3, 2008, 5:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import jist.runtime.JistAPI;
import jist.swans.mac.MacAddress;


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
class sscsTaskPending
{
    //----------------
    public boolean	startPANCoord;
    public boolean	startPANCoord_isCluster_Tree;
    public byte         startPANCoord_STEP;
    public boolean	startPANCoord_txBeacon;
    public byte         startPANCoord_BO;
    public byte         startPANCoord_SO;
    public byte         startPANCoord_Channel;
    //----------------
    public boolean	startDevice;
    public boolean	startDevice_isCluster_Tree;
    public byte         startDevice_STEP;
    public boolean	startDevice_isFFD;
    public boolean	startDevice_assoPermit;
    public boolean	startDevice_txBeacon;
    public byte         startDevice_BO;
    public byte         startDevice_SO;
    public byte 	startDevice_Channel;
    public PAN_ELE      startDevice_panDes;
    //----------------
    public sscsTaskPending()
    {
        init();
    }
    
    public void init()
    {
        startPANCoord = false;
        startPANCoord_STEP = 0;
        startDevice = false;
        startDevice_STEP = 0;
    }

    /* NS:2 
    public boolean taskStatus(byte task)
    {
        switch (task)
        {
            case Def.sscsTP_startPANCoord:
                    return startPANCoord;
            case Def.sscsTP_startDevice:
                    return startDevice;
            default:
                    assert(false);
        }
    }*/
    
    // SIDnet version
    public void taskStatus(byte task, boolean value)
    {
        switch (task)
        {
            case Def.sscsTP_startPANCoord:
                    startPANCoord = value;
                    //return startPANCoord;
            case Def.sscsTP_startDevice:
                    startDevice = value;
                    //return startDevice;
            default:
                    assert(false);
        }
    }
    
    public boolean taskStatus(byte task)
    {
        switch (task)
        {
            case Def.sscsTP_startPANCoord:
                    return startPANCoord;
            case Def.sscsTP_startDevice:
                    return startDevice;
            default:
                    assert(false);
        }
        return false; //???
    }

    /* NS-2 version
     public byte taskStep(byte task)
    {
        switch (task)
        {
            case Def.sscsTP_startPANCoord:
                    return startPANCoord_STEP;
            case Def.sscsTP_startDevice:
                    return startDevice_STEP;
            default:
                    assert(false);
        }
    } 
     */
    
    /* SIDnet version */
    public void setTaskStep(byte task, byte value)
    {
        switch (task)
        {
            case Def.sscsTP_startPANCoord:
                    //return startPANCoord_STEP;
                 startPANCoord_STEP  = value;
            case Def.sscsTP_startDevice:
                    //return startDevice_STEP;
                 startDevice_STEP = value;
            default:
                    assert(false);
        }
    } 
    
    public void taskStepIncrement(byte task)
    {
        switch (task)
        {
            case Def.sscsTP_startPANCoord:
                    //return startPANCoord_STEP;
                 startPANCoord_STEP ++;
            case Def.sscsTP_startDevice:
                    //return startDevice_STEP;
                 startDevice_STEP ++;
            default:
                    assert(false);
        }
    } 
    
    public byte taskStep(byte task)
    {
        switch (task)
        {
            case Def.sscsTP_startPANCoord:
                    return startPANCoord_STEP;
            case Def.sscsTP_startDevice:
                    return startDevice_STEP;
            default:
                    assert(false);
        }
        return -1; // ???
    } 
    
    

    
};

public class SSCS802_15_4
{
    // Not member in NS-2
    //public NET_SYSTEM_CONFIG NetSystemConfig; ???
    
     String[] sscsTaskName = {"NONE",
                             "startPANCoord",
                             "startDevice"};
    
    protected boolean t_isCT,t_txBeacon,t_isFFD,t_assoPermit;
    protected byte t_BO,t_SO;
    //for cluster tree
    protected int rt_myDepth;
    protected int rt_myNodeID;
    protected int rt_myParentNodeID;

    public static int ScanChannels;
    public boolean neverAsso;			

    private Mac802_15_4Impl mac;
    //private ZBR zbr;			 // ??? For ZigBee
    private TimerInterface802_15_4 assoH;//private SSCS802_15_4Timer assoH;
    private sscsTaskPending sscsTaskP;

    //--- store results returned from MLME_SCAN_confirm() ---
    private int	T_UnscannedChannels;
    private byte	T_ResultListSize;
    private byte[]	T_EnergyDetectList;
    private PAN_ELE[]	T_PANDescriptorList;
    private byte	Channel;
	//-------------------------------------------------------

    private HLISTLINK hlistLink1;
    private HLISTLINK hlistLink2;
    
    /* Not member in NS-2 */
    String statusName(MACenum status)
    {
	switch(status)
	{
		case m_SUCCESS:
			return "SUCCESS";
		case m_PAN_at_capacity:
			return "PAN_at_capacity";
		case m_PAN_access_denied:
			return "PAN_access_denied";
		case m_BEACON_LOSS:
			return "BEACON_LOSS";
		case m_CHANNEL_ACCESS_FAILURE:
			return "CHANNEL_ACCESS_FAILURE";
		case m_DENIED:
			return "DENIED";
		case m_DISABLE_TRX_FAILURE:
			return "DISABLE_TRX_FAILURE";
		case m_FAILED_SECURITY_CHECK:
			return "FAILED_SECURITY_CHECK";
		case m_FRAME_TOO_LONG:
			return "FRAME_TOO_LONG";
		case m_INVALID_GTS:
			return "INVALID_GTS";
		case m_INVALID_HANDLE:
			return "INVALID_HANDLE";
		case m_INVALID_PARAMETER:
			return "INVALID_PARAMETER";
		case m_NO_ACK:
			return "NO_ACK";
		case m_NO_BEACON:
			return "NO_BEACON";
		case m_NO_DATA:
			return "NO_DATA";
		case m_NO_SHORT_ADDRESS:
			return "NO_SHORT_ADDRESS";
		case m_OUT_OF_CAP:
			return "OUT_OF_CAP";
		case m_PAN_ID_CONFLICT:
			return "PAN_ID_CONFLICT";
		case m_REALIGNMENT:
			return "REALIGNMENT";
		case m_TRANSACTION_EXPIRED:
			return "TRANSACTION_EXPIRED";
		case m_TRANSACTION_OVERFLOW:
			return "TRANSACTION_OVERFLOW";
		case m_TX_ACTIVE:
			return "TX_ACTIVE";
		case m_UNAVAILABLE_KEY:
			return "UNAVAILABLE_KEY";
		case m_UNSUPPORTED_ATTRIBUTE:
			return "UNSUPPORTED_ATTRIBUTE";
		case m_UNDEFINED:
		default:
			return "UNDEFINED";
	}
    }
    
    /* ??? friend class Mac802_15_4Impl;
    friend class SSCS802_15_4Timer; */
    public SSCS802_15_4(Mac802_15_4Impl m)
    {
        //Tcl& tcl = Tcl::instance();
	//Node *node;
	//MobileNode* mnode;

        assoH = new SSCS802_15_4Timer(m.getProxy(), m.localAddr.hashCode()).getProxy();
        
	mac = m;
	neverAsso = true;
        //if (Def.ZigBeeIF) ??? OLIVER: we disable support for zigbee for now
          //  zbr = getZBRLink(mac.localAddr); ???
        
	hlistLink1 = null;
	hlistLink2 = null;
    }
    
    public void MCPS_DATA_confirm(byte msduHandle,MACenum status)
    {
       
    }
    public void MCPS_DATA_indication(byte SrcAddrMode,int SrcPANId,/* IE3ADDR */ int SrcAddr,
				  byte DstAddrMode,int DstPANId,/* IE3ADDR */ int DstAddr,
				  byte msduLength,/* Packet */MacMessage_802_15_4 msdu,byte mpduLinkQuality,
				  boolean SecurityUse,byte ACLEntry)
    {
        // DO NOTHING
        // NS-2: Packet::free(msdu);
    }
    public void MCPS_PURGE_confirm(byte msduHandle,MACenum status)
    {
        // DO NOTHING
    }
    public void MLME_ASSOCIATE_indication(/* IE3ADDR */ int DeviceAddress,byte CapabilityInformation,boolean SecurityUse,byte ACLEntry)
    {
        	//we assign the cluster tree address as the MAC short address
	int child_num,logAddr;
	boolean noCapacity;

        if(Def.ZigBeeIF)
        {
            /*if (t_isCT)			//need to assign a cluster tree logic address
            {
                    assertZBR();
                    noCapacity = false;
                    if (zbr.myDepth >= NetSystemConfig.Lm)
                            noCapacity = true;
                    else
                    {
                            child_num = 1;
                            logAddr = zbr.myNodeID + 1;				
                            while (!updateCTAddrLink(zbr_oper_est,logAddr))
                            {
                                    if (getIpAddrFrLogAddr(logAddr,false) == (UINT_16)DeviceAddress)
                                            break; 
                                    logAddr += ZBR.c_skip(zbr.myDepth);		
                                    child_num++;
                                    if (child_num > NetSystemConfig.Cm)
                                            break;
                            }
                            if (child_num > NetSystemConfig.Cm)
                                    noCapacity = true;
                    }
                    if (noCapacity)						//no capacity
                    {
                            if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                            zbr.sscs_nb_insert((UINT_16)DeviceAddress,NEIGHBOR);
                            mac.MLME_ASSOCIATE_response(DeviceAddress,0,m_PAN_at_capacity,false);
                    }
                    else
                    {
                            chkAddCTAddrLink(logAddr,DeviceAddress);
                            chkAddDeviceLink( &mac.deviceLink1,&mac.deviceLink2, DeviceAddress, CapabilityInformation);
                            zbr.sscs_nb_insert((UINT_16)DeviceAddress,CHILD);
                            mac.MLME_ASSOCIATE_response(DeviceAddress,logAddr,m_SUCCESS,false);
                    }
            }
            else	//just assign the IP address as the MAC short address for non-cluster tree
            {
                    chkAddDeviceLink(mac.deviceLink1, mac.deviceLink2, DeviceAddress, CapabilityInformation);
                    mac.MLME_ASSOCIATE_response(DeviceAddress,(UINT_16)DeviceAddress,m_SUCCESS,false);
            } OLIVER: WE DO NOT ENABLE SUPPORT FOR ZIGBEE BY DEFAULT*/
        }
        else
        {
            DEVICELINK.chkAddDeviceLink(/* & */mac.deviceLink1, /* & */mac.deviceLink2,DeviceAddress,CapabilityInformation);
            mac.MLME_ASSOCIATE_response(DeviceAddress, DeviceAddress ,MACenum.m_SUCCESS,false);
        }
    }
    public void MLME_ASSOCIATE_confirm(int AssocShortAddress,MACenum status)
    {
        MAC_PIB t_mpib = new MAC_PIB();

	if (status == MACenum.m_SUCCESS)
	{
		rt_myNodeID = AssocShortAddress;
		/*
		if (!sscsTaskP.startDevice_isCluster_Tree)
			t_mpib.macShortAddress = AssocShortAddress;
		else
		*/
		t_mpib.macShortAddress = mac.localAddr.hashCode();		//don't use cluster tree logic address
		mac.MLME_SET_request(MPIBAenum.macShortAddress,/* & */t_mpib);
	}
	dispatch(status,"MLME_ASSOCIATE_confirm");
    }
    
    public void MLME_DISASSOCIATE_confirm(MACenum status)
    {
        // DO NOTHING
    }
    public void MLME_BEACON_NOTIFY_indication(byte BSN,PAN_ELE PANDescriptor,byte PendAddrSpec,/* IE3ADDR */ int[] AddrList,byte sduLength,byte[] sdu)
    {
        // DO NOTHING
    }
    public void MLME_GET_confirm(MACenum status,MPIBAenum PIBAttribute,MAC_PIB PIBAttributeValue)
    {
        // DO NOTHING
    }
    public void MLME_ORPHAN_indication(/*IE3ADDR*/ int OrphanAddress,boolean SecurityUse,byte ACLEntry)
    {
        if (DEVICELINK.updateDeviceLink(Def.tr_oper_est,/* & */mac.deviceLink1,/* & */mac.deviceLink2,OrphanAddress) == 0)
		mac.MLME_ORPHAN_response(OrphanAddress,/*(UINT_16)*/ OrphanAddress,true,false);
	else
		mac.MLME_ORPHAN_response(OrphanAddress,(short)0,false,false);
    }
    
    public void MLME_RESET_confirm(MACenum status)
    {
        // DO NOTHING
    }
    public void MLME_RX_ENABLE_confirm(MACenum status)
    {
        // DO NOTHING
    }
    public void MLME_SET_confirm(MACenum status,MPIBAenum PIBAttribute)
    {
        // DO NOTHING
    }
    public void MLME_SCAN_confirm(MACenum status,byte ScanType,int UnscannedChannels,
                           byte ResultListSize,/* UINT_8 * */ byte[] EnergyDetectList,
                           PAN_ELE[] PANDescriptorList)
    {
         MAC_PIB t_mpib = new MAC_PIB();

	T_UnscannedChannels = UnscannedChannels;
	T_ResultListSize = ResultListSize;
	T_EnergyDetectList = EnergyDetectList;
	T_PANDescriptorList = PANDescriptorList;
//	if (ScanType == 0x01)
		dispatch(status,"MLME_SCAN_confirm");
//	if (ScanType == 0x03)
//	if (status == MACenum.m_SUCCESS)
//	{
//		System.out.println("["+ JistAPI.getTime()  + "](node" + mac.localAddr + ") coordinator relocation successful, begin to re-synchronize with the coordinator");
//		//re-synchronize with the coordinator
//		mac.phyEntity.PLME_GET_request(PPIBAenum.phyCurrentChannel);
//		mac.MLME_SYNC_request(mac.tmp_ppib.phyCurrentChannel,true);
//	}
//	else
//	{
//		boolean isCoord = mac.capability.FFD && ( DEVICELINK.numberDeviceLink(/* & */mac.deviceLink1) > 0);
//		System.out.println("[" + JistAPI.getTime() + "](node " + mac.localAddr + ") coordinator relocation failed " + ((isCoord) ? "." : " -. try to reassociate ..."));
// 		if (!isCoord)		//I am not a coordinator
// 		{
//	 		t_mpib.macShortAddress = (short)0xffff;
//			mac.MLME_SET_request(MPIBAenum.macShortAddress,/* & */t_mpib);
//	 		t_mpib.macCoordExtendedAddress = Const.def_macCoordExtendedAddress;
//			mac.MLME_SET_request(MPIBAenum.macCoordExtendedAddress,/* & */t_mpib);
//			startDevice(t_isCT,t_isFFD,t_assoPermit,t_txBeacon,t_BO,t_SO,true, MACenum.m_SUCCESS);
//		}
//	}
    }
    public void MLME_COMM_STATUS_indication(int PANId,byte SrcAddrMode,/* IE3ADDR */ int SrcAddr,
                                     byte DstAddrMode,/* IE3ADDR */ int DstAddr,MACenum status)
    {
        // DO NOTHING
    }
    public void MLME_START_confirm(MACenum status)
    {
         dispatch(status,"MLME_START_confirm");
    }
    
    public void MLME_SYNC_LOSS_indication(MACenum LossReason)
    {
       	System.out.println("[" + JistAPI.getTime() + "](node " + mac.localAddr + ") synchronization loss");
	mac.MLME_SCAN_request((byte)0x03,SSCS802_15_4.ScanChannels,(byte)0);
    }
    public void MLME_POLL_confirm(MACenum status)
    {
        // DO NOTHING
    }

    //--------------------------------------------------------------------------
    
    protected void checkTaskOverflow(byte task)
    {
        if (sscsTaskP.taskStatus(task))
	{
		System.out.println("[SSCS][" + JistAPI.getTime() + "](node " + mac.localAddr + ") task overflow: " + sscsTaskName[task]);
		System.exit(1);
	}
	else
		//sscsTaskP.taskStep(task) = 0;
                sscsTaskP.setTaskStep(task, (byte)0);
    }
    
    protected void dispatch(MACenum status, /* char * */String frFunc)
    {
        if (frFunc.equals("MLME_SCAN_confirm"))
	{
//		if (sscsTaskP.taskStatus(Def.sscsTP_startPANCoord))
			startPANCoord(sscsTaskP.startPANCoord_isCluster_Tree,sscsTaskP.startPANCoord_txBeacon,sscsTaskP.startPANCoord_BO,sscsTaskP.startPANCoord_SO,false,status);
//		else if (sscsTaskP.taskStatus(Def.sscsTP_startDevice))
//			startDevice(sscsTaskP.startDevice_isCluster_Tree,sscsTaskP.startDevice_isFFD,sscsTaskP.startDevice_assoPermit,sscsTaskP.startDevice_txBeacon,sscsTaskP.startDevice_BO,sscsTaskP.startDevice_SO,false,status);
	}
//	else if (frFunc.equals("MLME_START_confirm"))
//	{
//		if(sscsTaskP.taskStatus(Def.sscsTP_startPANCoord))
//			startPANCoord(sscsTaskP.startPANCoord_isCluster_Tree,sscsTaskP.startPANCoord_txBeacon,sscsTaskP.startPANCoord_BO,sscsTaskP.startPANCoord_SO,false,status);
//		else if (sscsTaskP.taskStatus(Def.sscsTP_startDevice))
//			startDevice(sscsTaskP.startDevice_isCluster_Tree,sscsTaskP.startDevice_isFFD,sscsTaskP.startDevice_assoPermit,sscsTaskP.startDevice_txBeacon,sscsTaskP.startDevice_BO,sscsTaskP.startDevice_SO,false,status);
//		else	//default handling
//		{
//			if (mac.mpib.macBeaconOrder == 15)
//				System.out.println("[" + JistAPI.getTime() + "](node " + mac.localAddr + ") beacon transmission stopped [channel:" + mac.tmp_ppib.phyCurrentChannel + "] [PAN_ID:" + mac.mpib.macPANId + "]");
//			else if (status == MACenum.m_SUCCESS)
//				System.out.println("[" + JistAPI.getTime() + "](node " + mac.localAddr + ") beacon transmission successful [channel:" + mac.tmp_ppib.phyCurrentChannel + "] [PAN_ID:" + mac.mpib.macPANId + "]");
//			else
//				System.out.println("<!>[" + JistAPI.getTime() + "](node " + mac.localAddr + ") failed to transmit beacons . " + statusName(status) + " [channel:" + mac.tmp_ppib.phyCurrentChannel + "] [PAN_ID:" + mac.mpib.macPANId + "]");
//		}
//	}
//	else if (frFunc.equals("MLME_ASSOCIATE_confirm"))
//	{
//		if(sscsTaskP.taskStatus(Def.sscsTP_startDevice))
//			startDevice(sscsTaskP.startDevice_isCluster_Tree,sscsTaskP.startDevice_isFFD,sscsTaskP.startDevice_assoPermit,sscsTaskP.startDevice_txBeacon,sscsTaskP.startDevice_BO,sscsTaskP.startDevice_SO,false,status);
//	}
    }
    protected void startPANCoord(boolean isClusterTree,boolean txBeacon,byte BO,byte SO,boolean firsttime,MACenum status /*= m_SUCCESS */) // ???
    {
        byte step;
	MAC_PIB t_mpib = new MAC_PIB();
	PHY_PIB t_ppib = new PHY_PIB();
	int i;
		
	if (firsttime) checkTaskOverflow(Def.sscsTP_startPANCoord);

	step = sscsTaskP.taskStep(Def.sscsTP_startPANCoord);
	switch(step)
	{
		case 0:
			System.out.println("--- startPANCoord [ " + mac.localAddr + " ] ---");
			//sscsTaskP.taskStatus(Def.sscsTP_startPANCoord) = true;
                        sscsTaskP.taskStatus(Def.sscsTP_startPANCoord, true);
			sscsTaskP.taskStepIncrement(Def.sscsTP_startPANCoord);
			sscsTaskP.startPANCoord_isCluster_Tree = isClusterTree;
			sscsTaskP.startPANCoord_txBeacon = txBeacon;
			sscsTaskP.startPANCoord_BO = BO;
			sscsTaskP.startPANCoord_SO = SO;
			//must be an FFD
			mac.capability.setFFD(true);
			//assign a short address for myself
                        if (Def.ZigBeeIF)
                        {
                            /* ??? for ZigBee
                            if (isClusterTree)
                            {
                                    assertZBR(); 
                                    zbr.myDepth = 0;
                                    zbr.myNodeID = 0;			//assign logic address 0 for myself
                                    zbr.myParentNodeID = 0;		//no parent, assign my own ID
                                    chkAddCTAddrLink(zbr.myNodeID,mac.index_);
                                    activateCTAddrLink(zbr.myNodeID,mac.index_);
                            } */
                        }
			t_mpib.macShortAddress = (short)mac.localAddr.hashCode(); 
			mac.MLME_SET_request(MPIBAenum.macShortAddress,/* & */t_mpib);
			//scan the channels
			System.out.println("["+ JistAPI.getTime()+"](node "+mac.localAddr+") performing active channel scan");
			mac.MLME_SCAN_request((byte)0x01,SSCS802_15_4.ScanChannels,BO);
			break;
		case 1:
			if (status != MACenum.m_SUCCESS)
			{
				System.out.println("<!>[" + JistAPI.getTime() + "](node " + mac.localAddr + ") unable to start as a PAN coordinator: active channel scan failed . " + statusName(status));
				//sscsTaskP.taskStatus(Def.sscsTP_startPANCoord) = false;
                                sscsTaskP.taskStatus(Def.sscsTP_startPANCoord, false);
				return;
			}
			//select a channel and a PAN ID (for simplicity, we just use the IP address as the PAN ID)
			//(it's not an easy task to select a channel and PAN ID in implementation!)
			for (i=11;i<27;i++)		//we give priority to 2.4G
			if ((T_UnscannedChannels & (1 << i)) == 0)
				break;
			if (i >= 27)
			for (i=0;i<11;i++)
			if ((T_UnscannedChannels & (1 << i)) == 0)
				break;
			sscsTaskP.startPANCoord_Channel = (byte)i;
			//permit association
			t_mpib.macAssociationPermit = true;
			mac.MLME_SET_request(MPIBAenum.macAssociationPermit,/* & */t_mpib);
			if (txBeacon)
			{
				//sscsTaskP.taskStep(Def.sscsTP_startPANCoord)++;
                                sscsTaskP.taskStepIncrement(Def.sscsTP_startPANCoord);
				System.out.println("[" + JistAPI.getTime() + "](node " + mac.localAddr + ") begin to transmit beacons");
				mac.MLME_START_request((short)mac.localAddr.hashCode(),(byte)i,BO,SO,true,false,false,false);
			}
//			else
//			{
//				mac.isPanCoor(true);
//				t_mpib.macCoordExtendedAddress = (short)mac.localAddr.hashCode();
//				mac.MLME_SET_request(MPIBAenum.macCoordExtendedAddress,/* & */t_mpib);
//				t_ppib.phyCurrentChannel = (byte)i;
//				mac.phyEntity.PLME_SET_request(PPIBAenum.phyCurrentChannel,/* & */t_ppib);
//				//sscsTaskP.taskStatus(Def.sscsTP_startPANCoord) = false;
//                                sscsTaskP.taskStatus(Def.sscsTP_startPANCoord, false);
//				System.out.println("[" + JistAPI.getTime() + "](node " + mac.localAddr + ") successfully started a new PAN (non-beacon enabled) [channel:" + sscsTaskP.startPANCoord_Channel + "] [PAN_ID:" + mac.localAddr + "]");
//				t_mpib.macPANId = (short)mac.localAddr.hashCode();
//				mac.MLME_SET_request(MPIBAenum.macPANId,/* & */t_mpib);
//				t_mpib.macBeaconOrder = 15;
//				mac.MLME_SET_request(MPIBAenum.macBeaconOrder,/* & */t_mpib);
//				t_mpib.macSuperframeOrder = 15;
//				mac.MLME_SET_request(MPIBAenum.macSuperframeOrder,/* & */t_mpib);
//			}
//                        if(Def.ZigBeeIF)
//                        {
//                            /* For ZigBee ???
//                            if (isClusterTree)
//                            {
//                                    assertZBR();
//                                    zbr.dRate = mac.phy.getRate('d');
//                            }
//                             */
//                        }
//
//			break;
//		case 2:
//			//sscsTaskP.taskStatus(Def.sscsTP_startPANCoord) = false;
//                        sscsTaskP.taskStatus(Def.sscsTP_startPANCoord, false);
//			if (status == MACenum.m_SUCCESS)
//				System.out.println("[" + JistAPI.getTime() + "](node " + mac.localAddr + ") successfully started a new PAN (beacon enabled) [channel:" + sscsTaskP.startPANCoord_Channel + "] [PAN_ID: " + mac.localAddr + "]");
//			else
//				System.out.println("<!>[" + JistAPI.getTime() + "](node " + mac.localAddr + ") failed to transmit beacons . " + statusName(status) + " [channel: " + sscsTaskP.startPANCoord_Channel + "] [PAN_ID: " + mac.localAddr + "]");
//			break;
//		default:
//			break;
	}
    }
    
    
    public void startDevice(boolean isClusterTree,boolean isFFD,boolean assoPermit,boolean txBeacon,byte BO,byte SO,boolean firsttime,MACenum status /*= m_SUCCESS*/) // ???
    {
        byte step,scan_BO;
	MAC_PIB t_mpib = new MAC_PIB();
	SuperframeSpec sfSpec = new SuperframeSpec();
	byte ch,fstChannel,fstChannel2_4G;
	
        //char[] tmpstr = new char[30];
        String tmpstr = new String();
   
	int i = 0,k = 0,l = 0,m,n,depth;
		
	if (firsttime) checkTaskOverflow(Def.sscsTP_startDevice);

	step = sscsTaskP.taskStep(Def.sscsTP_startDevice);
//	switch(step)
//	{
//		case 0:
			System.out.println("--- startDevice [" + mac.localAddr + "] ---");
			//sscsTaskP.taskStatus(Def.sscsTP_startDevice) = true;
                        sscsTaskP.taskStatus(Def.sscsTP_startDevice, true);
			//sscsTaskP.taskStep(Def.sscsTP_startDevice)++;
                        sscsTaskP.taskStepIncrement(Def.sscsTP_startPANCoord);
			mac.capability.setFFD(isFFD);
			sscsTaskP.startDevice_isCluster_Tree = isClusterTree;
			sscsTaskP.startDevice_isFFD = isFFD;
			sscsTaskP.startDevice_assoPermit = assoPermit;
			sscsTaskP.startDevice_txBeacon = txBeacon;
			sscsTaskP.startDevice_BO = BO;
			sscsTaskP.startDevice_SO = SO;
			scan_BO = (byte)(sscsTaskP.startDevice_BO + (byte)1);
			//set FFD
			mac.capability.setFFD(isFFD);
			//scan the channels
			System.out.println("[" + JistAPI.getTime() + "](node" + mac.localAddr + ") performing active channel scan ... ");			
                        mac.MLME_SCAN_request((byte)0x01,SSCS802_15_4.ScanChannels,scan_BO);
//			break;
//		case 1:
//			if (status != MACenum.m_SUCCESS)
//			{
//				//sscsTaskP.taskStatus(Def.sscsTP_startDevice) = false;
//                                sscsTaskP.taskStatus(Def.sscsTP_startDevice, false);
//				System.out.println("<!>[" + JistAPI.getTime() + "](node " + mac.localAddr + ") unable to start as a device: active channel scan failed . " + statusName(status));
//				assoH.startTimer(Config.assoRetryInterval);
//				return;
//			}
//			//select a PAN and a coordinator to join
//			fstChannel = (byte)0xff;
//			fstChannel2_4G = (byte)0xff;
//			for (i=0;i<T_ResultListSize;i++)
//			{
//				sfSpec.SuperSpec = T_PANDescriptorList[i].SuperframeSpec;
//				sfSpec.parse();
//				n = HLISTLINK.updateHListLink(Def.hl_oper_est,/* & */hlistLink1,/* & */hlistLink2,(short)T_PANDescriptorList[i].CoordAddress_64, (byte)0);
//				if ((!sfSpec.AssoPmt)||(/*!n ??? */ n != 0))
//					continue;
//				else
//				{
//					if (T_PANDescriptorList[i].LogicalChannel < 11)
//					{
//						if (fstChannel == 0xff)
//						{
//							fstChannel = T_PANDescriptorList[i].LogicalChannel;
//							k = i;
//						}
//					}
//					else
//					{
//						if (fstChannel2_4G == 0xff)
//						{
//							fstChannel2_4G = T_PANDescriptorList[i].LogicalChannel;
//							l = i;
//						}
//					}
//				}
//			}
//			if (fstChannel2_4G != 0xff)
//			{
//				ch = fstChannel2_4G;
//				i = l;
//			}
//			else
//			{
//				ch = fstChannel;
//				i = k;
//			}
//			if (ch == 0xff)		//cannot find any coordinator for association
//			{
//				//sscsTaskP.taskStatus(Def.sscsTP_startDevice) = false;
//                                sscsTaskP.taskStatus(Def.sscsTP_startDevice, false);
//				System.out.println("<!>[" + JistAPI.getTime() + "](node " + mac.localAddr + ") no coordinator found for association.");
//				assoH.startTimer(Config.assoRetryInterval);
//				return;
//			}
//			else
//			{
//				//select the least depth for cluster tree association
//                                if(Def.ZigBeeIF)
//                                {
//                                    /* For ZigBee ???
//                                    if (isClusterTree)
//                                    {
//                                            depth = T_PANDescriptorList[i].clusTreeDepth;
//                                            for (m=0;m<T_ResultListSize;m++)
//                                            {
//                                                    n = updateHListLink(hl_oper_est,hlistLink1,hlistLink2,(short)T_PANDescriptorList[m].CoordAddress_64);
//                                                    if ((ch == T_PANDescriptorList[m].LogicalChannel)&&(n))
//                                                    if (T_PANDescriptorList[m].clusTreeDepth < depth)
//                                                    {
//                                                            depth = T_PANDescriptorList[m].clusTreeDepth;
//                                                            i = m;
//                                                    }
//                                            }
//                                    }*/
//                                }
//                                
//				//If the coordinator is in beacon-enabled mode, we may begin to track beacons now.
//				//But this is only possible if the network is a one-hop star; otherwise we don't know
//				//which coordinator to track, since there may be more than one beaconing coordinators
//				//in a device's neighborhood and MLME-SYNC.request() has no parameter telling which 
//				//coordinator to track. As this is an optional step, we will not track beacons here.
//				t_mpib.macAssociationPermit = assoPermit;
//				mac.MLME_SET_request(MPIBAenum.macAssociationPermit,/* & */t_mpib);
//				sscsTaskP.startDevice_Channel = ch;
//				System.out.println("[" + JistAPI.getTime() + "](node " + mac.localAddr + ") sending association request to [channel: " + ch + "] [PAN_ID: " + T_PANDescriptorList[i].CoordPANId +"] [CoordAddr: " + T_PANDescriptorList[i].CoordAddress_64 + "] ... ");
//				//sscsTaskP.taskStep(Def.sscsTP_startDevice)++;
//                                sscsTaskP.taskStepIncrement(Def.sscsTP_startDevice);
//				sscsTaskP.startDevice_panDes = T_PANDescriptorList[i];
//				mac.MLME_ASSOCIATE_request(ch,T_PANDescriptorList[i].CoordAddrMode,T_PANDescriptorList[i].CoordPANId,T_PANDescriptorList[i].CoordAddress_64,mac.capability.cap,false);
//			}
//			break;
//		case 2:
//			sfSpec.SuperSpec = sscsTaskP.startDevice_panDes.SuperframeSpec;
//			sfSpec.parse();
//			if (sfSpec.BO != 15)
//				tmpstr = "beacon enabled";
//			else
//				tmpstr  = "non-beacon enabled";
//			if (status != MACenum.m_SUCCESS)
//			{
//				//reset association permission
//				t_mpib.macAssociationPermit = false;
//				mac.MLME_SET_request(MPIBAenum.macAssociationPermit,/* & */t_mpib);
//                                System.out.println("<!>[" + JistAPI.getTime() + "](node " + mac.localAddr + ") association failed " + statusName(status) + " ("+ tmpstr + ") [channel: " + sscsTaskP.startDevice_panDes.LogicalChannel + "] [PAN_ID: " + sscsTaskP.startDevice_panDes.CoordPANId +"] [CoordAddr: " + T_PANDescriptorList[i].CoordAddress_64 + "]");
//				
//				assoH.startTimer(Config.assoRetryInterval);
//				//sscsTaskP.taskStatus(Def.sscsTP_startDevice) = false;
//                                sscsTaskP.taskStatus(Def.sscsTP_startDevice, false);
//                                if(Def.ZigBeeIF)
//                                {
//                                    /* For ZigBee ???
//                                    if (isClusterTree)
//                                    {
//                                            assertZBR();
//                                            zbr.sscs_nb_insert(sscsTaskP.startDevice_panDes.CoordAddress_64,NEIGHBOR);
//                                            if (status == m_PAN_at_capacity)
//                                                    chkAddUpdHListLink(hlistLink1,hlistLink2,(short)sscsTaskP.startDevice_panDes.CoordAddress_64,0);
//                                    }*/
//                                }
//			}
//			else
//			{
//				neverAsso = false;
//				System.out.println("[" + JistAPI.getTime() + "](node " + mac.localAddr + ") association successful (" + tmpstr + ") [channel: " + sscsTaskP.startDevice_panDes.LogicalChannel + "] [PAN_ID: " + sscsTaskP.startDevice_panDes.CoordPANId + "] [CoordAddr: " + sscsTaskP.startDevice_panDes.CoordAddress_64 + "]");
//                                if(Def.ZigBeeIF)
//                                {
//                                    /* for ZigBee ???
//                                    if (isClusterTree)
//                                    {
//                                            assertZBR();
//                                            zbr.myDepth = rt_myDepth;
//                                            zbr.myNodeID = rt_myNodeID;
//                                            zbr.myParentNodeID = rt_myParentNodeID;
//                                            zbr.sscs_nb_insert(sscsTaskP.startDevice_panDes.CoordAddress_64,PARENT);
//                                            //chkAddCTAddrLink(zbr.myNodeID,mac.index_);	//too late -- may result in assigning duplicated addresses
//                                            activateCTAddrLink(zbr.myNodeID,mac.index_);
//                                            emptyHListLink(hlistLink1, hlistLink2);
//                                    }*/
//                                }
//
//				if (sfSpec.BO != 15)
//				{
//					System.out.println("[" + JistAPI.getTime() + "](node " + mac.localAddr + ") begin to synchronize with the coordinator");
//					mac.MLME_SYNC_request(sscsTaskP.startDevice_panDes.LogicalChannel,true);
//					//sscsTaskP.taskStatus(Def.sscsTP_startDevice) = false;
//                                        sscsTaskP.taskStatus(Def.sscsTP_startDevice, false);
//				}
//				if (isFFD && txBeacon)
//				{
//					//sscsTaskP.taskStep(Def.sscsTP_startDevice)++;
//                                        sscsTaskP.taskStepIncrement(Def.sscsTP_startDevice);
//					System.out.println("[" + JistAPI.getTime() + "](node " + mac.localAddr + ") begin to transmit beacons");
//					mac.MLME_START_request(mac.mpib.macPANId,sscsTaskP.startDevice_Channel,BO,SO,false,false,false,false);
//				}
//				else
//					//sscsTaskP.taskStatus(Def.sscsTP_startDevice) = false;
//                                        sscsTaskP.taskStatus(Def.sscsTP_startDevice, false);
//                                if(Def.ZigBeeIF)
//                                {
//                                    /* For ZigBee ???
//                                    if (isClusterTree)
//                                            zbr.dRate = mac.phy.getRate('d');
//                                     */
//                                }
//
//			}
//			break;
//		case 3:
//			//sscsTaskP.taskStatus(Def.sscsTP_startDevice) = false;
//                        sscsTaskP.taskStatus(Def.sscsTP_startDevice, false);
//			if (status == MACenum.m_SUCCESS)
//				System.out.println("[" + JistAPI.getTime() + "](node " + mac.localAddr + ") beacon transmission successful [channel: " + sscsTaskP.startDevice_Channel + "] [PAN_ID: " + mac.mpib.macPANId + "]");
//			else
//                                System.out.println("<!>[" + JistAPI.getTime() + "](node " + mac.localAddr + ") failed to transmit beacons . " + statusName(status) + " [channel: " + sscsTaskP.startDevice_Channel + "] [PAN_ID: " + mac.mpib.macPANId + "]");
//			break;
//		default:
//			break;
//	}
    }
    //protected int command(int argc, char[][] argv); // const char*const* argv ??? NS-2 this is TCL stuff

    // for cluster tree ??? For ZigBee only. 
    //protected void assertZBR();
    //protected int RNType();
    //protected void setGetClusTreePara(char setGet,Packet p);

   
};
/* END *** SSCS *** */
