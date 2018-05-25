/*
 * Mac802_15_4Impl.java
 *
 * Created on June 30, 2008, 11:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import jist.runtime.JistAPI;
import jist.swans.Constants;
import jist.swans.mac.MacAddress;
import jist.swans.misc.Message;
import jist.swans.net.NetInterface;
import jist.swans.radio.RadioInfo;
import sidnet.core.misc.Node;
import sidnet.core.misc.Reason;
import sidnet.core.timers.TimerCallbackInterface;
import sidnet.models.energy.energyconsumptionmodels.EnergyManagement;
import sidnet.models.energy.energyconsumptionmodels.EnergyConsumptionModelImpl;


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

public class Mac802_15_4Impl 
implements Mac802_15_4,
TimerCallbackInterface { /* extends Mac */
    // Not members in NS-2, ??? 
    public static final byte TxOp_Acked = 1;      // 0x01
    public static final byte TxOp_GTS = 2;        // 0x02
    public static final byte TxOp_Indirect = 4;   // 0x04
    public static final byte TxOp_SecEnabled = 8; // 0x08
    
     /* Not members in NS-2  ???*/
    public static final byte macTxBcnCmdDataHType = 1;
    public static final byte macIFSHType = 2;
    public static final byte macBackoffBoundType = 3;
    
    static Logger packetFlowLogger, resetLogger, taskPLogger,
                  mcpsLogger, packetInfoLogger, traceACKLogger, traceDATALogger,
                  pdLogger, plmeLogger;
	static {
		 resetLogger = Logger.getLogger("Mac802_15_4.resetLogger");
		 resetLogger.setLevel(Level.INFO);
		 resetLogger.setAdditivity(false);
		 resetLogger.addAppender(new ConsoleAppender(new PatternLayout("\t%-5p %c *** %m \n\n")));
		 
		 taskPLogger = Logger.getLogger("Mac802_15_4.taskPLogger");
		 taskPLogger.setLevel(Level.INFO);
		 taskPLogger.setAdditivity(false);
		 taskPLogger.addAppender(new ConsoleAppender(new PatternLayout("\t\t%-5p %c *** %m \n")));
		 
		 mcpsLogger = Logger.getLogger("Mac802_15_4.mcpsLogger");
		 mcpsLogger.setLevel(Level.INFO);
		 mcpsLogger.setAdditivity(false);
		 mcpsLogger.addAppender(new ConsoleAppender(new PatternLayout("%-5p %c *** %m")));
		 
		 packetFlowLogger = Logger.getLogger("Mac802_15_4.packetFlowLogger");
		 packetFlowLogger.setLevel(Level.INFO);
		 packetFlowLogger.setAdditivity(false);
		 packetFlowLogger.addAppender(new ConsoleAppender(new PatternLayout("\t\t%-5p %c *** %m \n")));
		 
		 packetInfoLogger = Logger.getLogger("Mac802_15_4.packetInfoLogger");
		 packetInfoLogger.setLevel(Level.INFO);
		 packetInfoLogger.setAdditivity(false);
		 packetInfoLogger.addAppender(new ConsoleAppender(new PatternLayout("\t\t%-5p %c *** %m \n")));
		 
		 traceACKLogger = Logger.getLogger("Mac802_15_4.traceACKLogger");
		 traceACKLogger.setLevel(Level.INFO);
		 traceACKLogger.setAdditivity(false);
		 traceACKLogger.addAppender(new ConsoleAppender(new PatternLayout("\t\t%-5p %c *** %m \n")));
		 
		 traceDATALogger = Logger.getLogger("Mac802_15_4.traceDATALogger");
		 traceDATALogger.setLevel(Level.INFO);
		 traceDATALogger.setAdditivity(false);
		 traceDATALogger.addAppender(new ConsoleAppender(new PatternLayout("\t\t%-5p %c *** %m \n")));
		 
		 pdLogger = Logger.getLogger("Mac802_15_4.pdLogger");
		 pdLogger.setLevel(Level.INFO);
		 pdLogger.setAdditivity(false);
		 pdLogger.addAppender(new ConsoleAppender(new PatternLayout("\t\t\t%-5p %c *** %m \n")));
		 
		 plmeLogger = Logger.getLogger("Mac802_15_4.plmeLogger");
	     plmeLogger.setLevel(Level.INFO);
	     plmeLogger.setAdditivity(false);
	     plmeLogger.addAppender(new ConsoleAppender(new PatternLayout("\t\t\t%-5p %c *** %m \n")));
	}
	
	long debugNodeID = 2;

    
    //////////////////////////////////////////////////
    // locals
    //
    
    /** mac address of this interface. */
    public MacAddress localAddr;
    
    /** NS-2 */
    public static boolean verbose = false;
    public static byte txOption = 0;		//0x02=GTS; 0x04=Indirect; 0x00=Direct (only for 802.15.4-unaware upper layer app. packet)
    public static boolean ack4data = true;
    public static byte callBack = 1;		//0=no call back; 1=call back for failures; 2=call back for failures and successes
    public static int DBG_UID = 0;

    public taskPending taskP;
    public MAC_PIB mpib = new MAC_PIB(	Const.def_macAckWaitDuration,		Const.def_macAssociationPermit,
                                                Const.def_macAutoRequest,		Const.def_macBattLifeExt,
                                                Const.def_macBattLifeExtPeriods,	Const.def_macBeaconPayload,
                                                Const.def_macBeaconPayloadLength,	Const.def_macBeaconOrder,
                                                Const.def_macBeaconTxTime,		(byte)0/*def_macBSN*/,
                                                Const.def_macCoordExtendedAddress,	Const.def_macCoordShortAddress,
                                                (byte)0/*def_macDSN*/,                        Const.def_macGTSPermit,
                                                Const.def_macMaxCSMABackoffs,		Const.def_macMinBE,
                                                Const.def_macPANId,			Const.def_macPromiscuousMode,
                                                Const.def_macRxOnWhenIdle,		Const.def_macShortAddress,
                                                Const.def_macSuperframeOrder,		Const.def_macTransactionPersistenceTime,
                                                Const.def_macACLEntryDescriptorSet,	Const.def_macACLEntryDescriptorSetSize,
                                                Const.def_macDefaultSecurity,		Const.def_macACLDefaultSecurityMaterialLength,
                                                Const.def_macDefaultSecurityMaterial,	Const.def_macDefaultSecuritySuite,
                                                Const.def_macSecurityMode);
    
    
    public PHY_PIB tmp_ppib;
    public DevCapability capability = new DevCapability();		//device capability (refer to Figure 49)

	//--- for beacon ---
	//(most are temp. variables which should be populated before being used)
    private boolean secuBeacon;
    public SuperframeSpec sfSpec,sfSpec2,sfSpec3;	//superframe specification
    
    private GTSSpec gtsSpec,gtsSpec2;		//GTS specification
    private PendAddrSpec pendAddrSpec;		//pending address specification
    public byte beaconPeriods,beaconPeriods2;	//# of backoff periods it takes to transmit the beacon
    public PAN_ELE	panDes,panDes2;			//PAN descriptor
    public MacMessage_802_15_4 rxBeacon;			//the beacon packet just received
    public double	macBcnTxTime;			//the time last beacon sent (in symbol) (we use this double variable instead of integer mpib.macBeaconTxTime for accuracy reason)
    public double	macBcnRxTime;			//the time last beacon received from within PAN (in symbol)
    
    
    public double	macBcnOtherRxTime;		//the time last beacon received from outside PAN (in symbol)
	//To support beacon enabled mode in multi-hop envirionment, we use {<mpib.macBeaconOrder>,<mpib.macSuperframeOrder>}
	//for coordinators (transmitting beacons) and the following two parameters for devices (receiving beacons). Note that,
	//in such an environment, a node can act as a coordinator and a device at the same time. More complicated algorithm 
	//is required for slotted CSMA-CA in this case.
	//(does 802.15.4 have this in mind?) 
    public byte	macBeaconOrder2;
   
    
    private byte	macSuperframeOrder2;
    public  byte	macBeaconOrder3;
    private byte	macSuperframeOrder3;
    private boolean	oneMoreBeacon;			
    private byte	numLostBeacons;			//# of beacons lost in a row
	//------------------
    private int	rt_myNodeID;			

    private byte energyLevel;			

	//for association and transaction
    public DEVICELINK deviceLink1;
    public DEVICELINK deviceLink2;
    private TRANSACLINK transacLink1;
    private TRANSACLINK transacLink2;

    private /*IE3ADDR*/ int aExtendedAddress;

    //timers
    private TimerInterface802_15_4 txOverT = null;           //private macTxOverTimer txOverT;
    private TimerInterface802_15_4 txT     = null;               //private macTxTimer txT;
    private TimerInterface802_15_4 extractT = null;          //private macExtractTimer extractT;
    private TimerInterface802_15_4 assoRspWaitT = null;      //private macAssoRspWaitTimer assoRspWaitT;
    private TimerInterface802_15_4 dataWaitT = null;         //private macDataWaitTimer dataWaitT;
    private TimerInterface802_15_4 rxEnableT = null;         //private macRxEnableTimer rxEnableT;
    private TimerInterface802_15_4 scanT = null;             //private macScanTimer scanT;
    private TimerInterface802_15_4 bcnTxT = null;            //public macBeaconTxTimer bcnTxT;		//beacon transmission timer
    public TimerInterface802_15_4 bcnRxT = null;            //public macBeaconRxTimer bcnRxT;		//beacon reception timer
    private TimerInterface802_15_4 bcnSearchT = null;        //private macBeaconSearchTimer bcnSearchT;	//beacon search timer

    //handlers
    private Mac802_15_4Handler txCmdDataH;
    private Mac802_15_4Handler IFSH;
    private Mac802_15_4Handler backoffBoundH;

    private boolean isPANCoor;			//is a PAN coordinator?
    private CsmaCA802_15_4 csmaca;
    private SSCS802_15_4 sscs;
    //private Nam802_15_4 nam;  // ??? nam
    private PHYenum trx_state_req;		//tranceiver state required: TRX_OFF/TX_ON/RX_ON
    private boolean inTransmission;		//in the middle of transmission
    private boolean beaconWaiting;		//it's about time to transmit beacon (suppress all other transmissions)
    private MacMessage_802_15_4 txBeacon;		//beacon packet to be transmitted (w/o using CSMA-CA)
    public MacMessage_802_15_4 txAck;			//ack. packet to be transmitted (no waiting)
    private MacMessage_802_15_4 txBcnCmd;		//beacon or command packet waiting for transmission (using CSMA-CA) -- triggered by receiving a packet
    private MacMessage_802_15_4 txBcnCmd2;		//beacon or command packet waiting for transmission (using CSMA-CA) -- triggered by upper layer
    private MacMessage_802_15_4 txData;			//data packet waiting for transmission (using CSMA-CA)
    private MacMessage_802_15_4 txCsmaca;		//for which packet (txBcnCmd/txBcnCmd2/txData) is CSMA-CA performed
    private MacMessage_802_15_4 txPkt;			//packet (any type) currently being transmitted
    private MacMessage_802_15_4 rxData;			//data packet received from phy (waiting for passing up)
    private MacMessage_802_15_4 rxCmd;			//command packet received (will be handled after the transmission of ack.)
    private int	txPkt_uid;		//for debug purpose
    private double rxDataTime;		//the time when data packet received by MAC
    private boolean waitBcnCmdAck;		//only used if (txBcnCmd != null): waiting for an ack. or not
    private boolean waitBcnCmdAck2;		//only used if (txBcnCmd2 != null): waiting for an ack. or not
    private boolean waitDataAck;		//only used if (txData != null): waiting for an ack. or not
    public byte backoffStatus;		//0=no backoff yet;1=backoff successful;2=backoff failed;99=in the middle of backoff
    private byte numDataRetry;		//# of retries (retransmissions) for data packet
    private byte numBcnCmdRetry;		//# of retries (retransmissions) for beacon or command packet
    private byte numBcnCmdRetry2;		//# of retries (retransmissions) for beacon or command packet

    //private NsObject logtarget_;

    //packet duplication detection
    private HLISTLINK hlistBLink1;
    private HLISTLINK hlistBLink2;
    private HLISTLINK hlistDLink1;
    private HLISTLINK hlistDLink2;
    // <-- end of NS-2
    
    /* START ::: OSI Stack Hookup methods +++++++++++++++++++ */

    /** Network upcall entity interface. */
    private NetInterface netEntity;
    
    /** network interface number. */
    private byte netId;
    
    /** Self-referencing mac entity reference. */
    private Mac802_15_4 self = null;    
    
    /** Physical downcall entity interface. */
    public Phy802_15_4 phyEntity;
    
    
    
    
    /**
     * Hook up with the network entity.
     *
     * @param net network entity
     * @param netid network interface number
     */
    public void setNetEntity(NetInterface net, byte netid)
    {
       if(!JistAPI.isEntity(net)) throw new IllegalArgumentException("expected entity");
       this.netEntity = net;
       this.netId = netid;
    }
    
    
    public int get_sfSpec2_sd() throws JistAPI.Continuation
    {
        return sfSpec2.sd;
    } 
    
    public byte get_sfSpec2_FinCAP() throws JistAPI.Continuation
    {
        return sfSpec2.FinCAP;
    }
    
    public byte getMacBeaconOrder2() throws JistAPI.Continuation
    {
        return macBeaconOrder2;
    }    
    
    public double getMacBcnRxTime() throws JistAPI.Continuation
    {
        return macBcnRxTime;
    }
    
    public byte getMpibMacBeaconOrder() throws JistAPI.Continuation
    { 
        return mpib.macBeaconOrder;
    }
    
    public boolean sscs_neverAsso() throws JistAPI.Continuation
    {
        return sscs.neverAsso;
    }
    
    // called from SSCS802_15_4Timer
    public void startDevice() 
    {
        sscs.startDevice(sscs.t_isCT,sscs.t_isFFD,sscs.t_assoPermit,sscs.t_txBeacon,sscs.t_BO,sscs.t_SO,true, MACenum.m_SUCCESS);
    }
    
    
    /**
     * Return proxy entity of this mac.
     *
     * @return self-referencing proxy entity.
     */
    public Mac802_15_4 getProxy() {
      return self;
    }
    
   
   /**
    * Hook down with the Physical entity.
    *
    * @param phy physical entity
    */
   public void setPhyEntity(Phy802_15_4 phy) {
       if(!JistAPI.isEntity(phy)) 
    	   throw new IllegalArgumentException("expected entity");
       this.phyEntity = phy;
       //initTimers();
       txOverT = new macTxOverTimer(self, localAddr.hashCode()).getProxy();
       txT = new macTxTimer(self, myNode.getID()).getProxy();
       extractT = new macExtractTimer(self, phyEntity, localAddr.hashCode()).getProxy();
       assoRspWaitT = new macAssoRspWaitTimer(self, localAddr.hashCode()).getProxy();
       dataWaitT = new macDataWaitTimer(self, localAddr.hashCode()).getProxy();
       rxEnableT = new macRxEnableTimer(self, localAddr.hashCode()).getProxy();
       scanT = new macScanTimer(self, localAddr.hashCode()).getProxy();
       bcnTxT = new macBeaconTxTimer(self, phyEntity, localAddr.hashCode()).getProxy();
       bcnRxT = new macBeaconRxTimer(self, phyEntity, localAddr.hashCode()).getProxy();
       bcnSearchT = new macBeaconSearchTimer(self, localAddr.hashCode()).getProxy();
       csmaca = new CsmaCA802_15_4(phyEntity, this, localAddr);
   }
   
   /* END ::: OSI Stack Hookup methods +++++++++++++++++++ */

    // properties

    /** link bandwidth (units: bytes/second). */
    private final int bandwidth;
    
    private EnergyManagement energyManagementUnit;  
    
    private Node myNode = null;
    
     
    public Mac802_15_4Impl(
            MacAddress addr,
            RadioInfo.RadioInfoShared radioInfo,
            EnergyManagement energyManagement,
            Node myNode)
    {
        this(addr, radioInfo, energyManagement, myNode, new MAC_PIB());
    }
    
    
    public Mac802_15_4Impl(MacAddress addr,
                           RadioInfo.RadioInfoShared radioInfo,
                           EnergyManagement energyManagement,
                           Node myNode,
                           MAC_PIB mp) {   
        
        // proxy
        self = (Mac802_15_4)JistAPI.proxyMany(this, new Class[]{Mac802_15_4.class, TimerCallbackInterface.class}); 
        
        localAddr = addr;
        
        //super();
        txCmdDataH = new Mac802_15_4Handler(self, macTxBcnCmdDataHType);
        IFSH = new Mac802_15_4Handler(self,macIFSHType);
        backoffBoundH = new Mac802_15_4Handler(this,macBackoffBoundType);
        bandwidth = radioInfo.getBandwidth() / 8;           // byte per second conversion
        
        
        
        capability.cap = (byte)0xc1;	//alterPANCoor = true
					//FFD = true
					//mainsPower = false
					//recvOnWhenIdle = false
					//secuCapable = false
					//alloShortAddr = true
		capability.parse();
		aExtendedAddress =  addr.hashCode();
		oneMoreBeacon = false;
		isPANCoor = false;
		inTransmission = false;
		mpib = mp;
	        mpib.macBSN = (byte)Constants.random.nextInt(256); //mpib.macBSN = Random.random() % 0x100;
	        mpib.macDSN = (byte)Constants.random.nextInt(256); //mpib.macDSN = Random.random() % 0x100;
		macBeaconOrder2 = 15;
		macSuperframeOrder2 = Const.def_macBeaconOrder;
		macBeaconOrder3 = 15;
		macSuperframeOrder3 = Const.def_macBeaconOrder;
		if (mpib.macBeaconOrder == 15)		//non-beacon mode
			mpib.macRxOnWhenIdle = true;	//default is false, but should be true in non-beacon mode
		numLostBeacons = 0;
		
		sscs = new SSCS802_15_4(this);
		assert(sscs != null);
		//nam = new Nam802_15_4((isPANCoor)?Nam802_15_4.def_PANCoor_clr:"black","black",this); ??? nam
		//assert(nam); ??? nam
	
		//chkAddMacLink(localAddr,this); ??? nam
	
		init(true); //??? NS-2 calling super MAC
	        
	        this.myNode = myNode;
	        
	        
	        this.energyManagementUnit = energyManagement;
    }
    
   
    
  
    private void initTimers()
    {
        //if (txOverT == null || txOverT.bussy())
          //  txOverT = new macTxOverTimer(self, localAddr.hashCode()).getProxy();
	assert(txOverT != null);
	//txT = new macTxTimer(this, myNode.getID());
        //if (txT == null || txT.bussy())
            txT = new macTxTimer(self, myNode.getID()).getProxy();
	assert(txT != null);
	//extractT = new macExtractTimer(this);
//        if (extractT == null || extractT.bussy())
//            extractT = new macExtractTimer(self, phyEntity, localAddr.hashCode()).getProxy();
//	assert(extractT != null);
//	//assoRspWaitT = new macAssoRspWaitTimer(this);
//        if (assoRspWaitT == null || assoRspWaitT.bussy())
//            assoRspWaitT = new macAssoRspWaitTimer(self, localAddr.hashCode()).getProxy();
//	assert(assoRspWaitT != null);
//	//dataWaitT = new macDataWaitTimer(this);
//        if (dataWaitT == null || dataWaitT.bussy())
//            dataWaitT = new macDataWaitTimer(self, localAddr.hashCode()).getProxy();
//	assert(dataWaitT != null);
//	//rxEnableT = new macRxEnableTimer(this);
//        if (rxEnableT == null || rxEnableT.bussy())
//            rxEnableT = new macRxEnableTimer(self, localAddr.hashCode()).getProxy();
//	assert(rxEnableT != null);
//	//scanT = new macScanTimer(this);
//        if (scanT == null || scanT.bussy())
//            scanT = new macScanTimer(self, localAddr.hashCode()).getProxy();
//	assert(scanT != null);
//	//bcnTxT = new macBeaconTxTimer(this);
//        if (bcnTxT == null || bcnTxT.bussy())
//            bcnTxT = new macBeaconTxTimer(self, phyEntity, localAddr.hashCode()).getProxy();
//	assert(bcnTxT != null);
//	//bcnRxT = new macBeaconRxTimer(this);
//        if (bcnRxT == null || bcnRxT.bussy())
//            bcnRxT = new macBeaconRxTimer(self, phyEntity, localAddr.hashCode()).getProxy();
//	assert(bcnRxT != null);
//	//bcnSearchT = new macBeaconSearchTimer(this);
//        if (bcnSearchT == null || bcnSearchT.bussy())
//            bcnSearchT = new macBeaconSearchTimer(self, localAddr.hashCode()).getProxy();
//	assert(bcnSearchT != null);        
    }
    
    public void reset() {
        if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
        	resetLogger.debug("MAC [#" + localAddr + "] RESET/INIT ");        

         //txBeacon = null;		//beacon packet to be transmitted (w/o using CSMA-CA)
         //txAck = null;			//ack. packet to be transmitted (no waiting)
         //txBcnCmd = null;		//beacon or command packet waiting for transmission (using CSMA-CA) -- triggered by receiving a packet
         //txBcnCmd2 = null;		//beacon or command packet waiting for transmission (using CSMA-CA) -- triggered by upper layer
         //txData = null;			//data packet waiting for transmission (using CSMA-CA)
         //txCsmaca = null;		//for which packet (txBcnCmd/txBcnCmd2/txData) is CSMA-CA performed
         //txPkt = null;			//packet (any type) currently being transmitted
         //rxData = null;			//data packet received from phy (waiting for passing up)
         //rxCmd = null;		
         resetTimers();
         taskP.init();
    }
     
    public void resetTimers() {
    	csmaca.reset(); // oliver 2010-04-20
        txOverT.cancel();
        txOverT.resetTimer();
        txT.cancel();
        txT.resetTimer();
        extractT.cancel();
        extractT.resetTimer();
        assoRspWaitT.cancel();
        assoRspWaitT.resetTimer();
        dataWaitT.cancel();
        dataWaitT.resetTimer();
        rxEnableT.cancel();
        rxEnableT.resetTimer();
        scanT.cancel();
        scanT.resetTimer();
        bcnTxT.cancel();
        bcnTxT.resetTimer();
         //if (bcnRxT.bussy())
         //   bcnRxT = new macBeaconRxTimer(self, phyEntity, localAddr.hashCode()).getProxy();
        bcnRxT.cancel();
        bcnRxT.resetTimer();
        bcnSearchT.cancel();
        bcnSearchT.resetTimer();
        initTimers();
    }
    
    // relay function for proxies
    public void csmaca_backoffHandler() {
        csmaca.backoffHandler();
    }
    
    public void csmaca_bcnOtherHandler() {
        csmaca.bcnOtherHandler();
    }
    
    public void csmaca_deferCCAHandler() {
    	csmaca.deferCCAHandler();
    }
    
    public void init(boolean reset /* = false */) {
        secuBeacon = false;
		beaconWaiting = false;
		txBeacon = null;
		txAck = null;
		txBcnCmd = null;
		txBcnCmd2 = null;
		txData = null;
		rxData = null;
		rxCmd = null;
	
		if (reset) {
	            HLISTLINK.emptyHListLink(hlistBLink1,hlistBLink2);
	            HLISTLINK.emptyHListLink(hlistDLink1,hlistDLink2);
	            DEVICELINK.emptyDeviceLink(deviceLink1,deviceLink2);
	            TRANSACLINK.emptyTransacLink(transacLink1,transacLink2);
		} else { 
	            hlistBLink1 = null;
	            hlistBLink2 = null;
	            hlistDLink1 = null;
	            hlistDLink2 = null;
	            deviceLink1 = null;
	            deviceLink2 = null;
	            transacLink1 = null;
	            transacLink2 = null;
		}
	
	    taskP = new taskPending();
		taskP.init();
    }
    
    
    
    //interfaces between MAC and PHY
    public void PD_DATA_confirm(PHYenum status) {
        String __FUNCTION__ = "PD_DATA_confirm";
        inTransmission = false;
        
        if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
        	pdLogger.debug("[" +JistAPI.getTime()/Constants.MILLI_SECOND + "ms]["+localAddr+"][MAC.PD_DATA_confirm(" + status + ")]");
        
        if (txOverT.bussy())
            txOverT.stopTimerr();
        
		if (backoffStatus == 1)
	            backoffStatus = 0;	
	        
		if (status == PHYenum.p_SUCCESS) {
	            dispatch(status,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
		} else if (txPkt == txBeacon) {
	            beaconWaiting = false;
	            //MacMessage_802_15_4.free(txBeacon);
	            txBeacon = null;
		} else if (txPkt == txAck) {
			//MacMessage_802_15_4.free(txAck);
			txAck = null;
		}
		else	//RX_ON/TRX_OFF -- possible if the transmisstion is terminated by a FORCE_TRX_OFF or change of channel, or due to energy depletion
		{	//nothing to do -- it is the process that terminated the transmisstion to provide a way to resume the transmission
	        if (Def.DEBUG802_15_4_pump && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
	            System.out.println("[" + JistAPI.getTime() + "][MAC].PD_DATA_confirm() -> pump()");
	        
		}
    }
    
    public void PLME_CCA_confirm(PHYenum status) {
    	
    	if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
    		plmeLogger.debug("[" +JistAPI.getTime()/Constants.MILLI_SECOND + "]["+localAddr+"][PHY.PLME_CCA_confirm()] - Channel is " + status);
         
        if (taskP.taskStatus(taskPending.TP_CCA_csmaca)) {
	            taskP.setTaskStatus(taskPending.TP_CCA_csmaca, false);
	            csmaca.CCA_confirm(status);
		}
    }
    
    
    public void PLME_ED_confirm(PHYenum status,byte EnergyLevel)
    {
        String __FUNCTION__ = "PLME_ED_confirm";
        
        energyLevel = EnergyLevel;
	dispatch(status,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
    }
    
    public void PLME_GET_confirm(PHYenum status,PPIBAenum PIBAttribute,PHY_PIB PIBAttributeValue)
    {
        if (status == PHYenum.p_SUCCESS)
            switch(PIBAttribute)
            {
		case phyCurrentChannel:
                    tmp_ppib.phyCurrentChannel = PIBAttributeValue.phyCurrentChannel;
                    break;
		case phyChannelsSupported:
                    tmp_ppib.phyChannelsSupported = PIBAttributeValue.phyChannelsSupported;
                    break;
		case phyTransmitPower:
                    tmp_ppib.phyTransmitPower = PIBAttributeValue.phyTransmitPower;
                    break;
		case phyCCAMode:
                    tmp_ppib.phyCCAMode = PIBAttributeValue.phyCCAMode;
                    break;
		default:
                    break;
            }
    }
    public void PLME_SET_TRX_STATE_confirm(PHYenum status)
    {
    	if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
    		System.out.println("[" + JistAPI.getTime() + "][MAC.PLME_SET_TRX_STATE_confirm]");
        
        String __FUNCTION__ = "PLME_SET_TRX_STATE_confirm";
        
        hdr_lrwpan wph;
	FrameCtrl frmCtrl = new FrameCtrl();
	double delay;

	if (status == PHYenum.p_SUCCESS) status = trx_state_req;

	if (backoffStatus == 99)
	{
            if (trx_state_req == PHYenum.p_RX_ON)
            {
                if (taskP.taskStatus(taskPending.TP_RX_ON_csmaca))
                {
                    //taskP.taskStatus(taskPending.TP_RX_ON_csmaca) = false;
                    taskP.setTaskStatus(taskPending.TP_RX_ON_csmaca, false);
                    csmaca.RX_ON_confirm(status);
                }
            }
	}
	else
            dispatch(status,__FUNCTION__,trx_state_req, MACenum.m_SUCCESS);

	if (status != PHYenum.p_TX_ON) 
            return;

	//transmit the packet
	if (beaconWaiting)
	{
            /* to synchronize better, we don't transmit the beacon here
            #ifdef DEBUG802_15_4
            fprintln(stdout,"[%s.%s][%f](node %d) transmit BEACON to %d: SN = %d, uid = %d, mac_uid = %ld\n",__FILE__,__FUNCTION__,((double)JistAPI.getTime()/Constants.SECOND),((int)localAddr.hashCode()),Trace.p802_15_4macDA(txBeacon),HDR_LRWPAN(txBeacon).MHR_BDSN,txBeacon.HDR_CMN().uid(),HDR_LRWPAN(txBeacon).uid);
            #endif
            if (!taskP.taskStatus(taskPending.TP_mlme_start_request))	//not first beacon
                    assert((txAck == null)&&(!txCsmaca));		//all tasks should be done before next beacon
            txPkt = txBeacon;
            txBeacon.HDR_CMN().direction() = hdr_cmn.DOWN;
            sendDown(txBeacon.refcopy(),this);
            */
	}
	else if (txAck != null)
	{
            //although no CSMA-CA required for the transmission of ack., 
            //but we still need to locate the backoff period boundary if beacon enabled
            //(refer to page 157, line 25-31)
            if ((mpib.macBeaconOrder == 15)&&(macBeaconOrder2 == 15))	//non-beacon enabled
                //delay = 0.0;
                delay = 0;
            else								//beacon enabled
                delay  = locateBoundary((Trace.p802_15_4macDA(txAck) == mpib.macCoordShortAddress),/* 0.0 */ 0);
            if (/*delay == 0.0*/ delay == 0)
                backoffBoundHandler();
            else
                //Scheduler.instance().schedule(backoffBoundH, backoffBoundH.nullEvent, delay);
                backoffBoundH.executeLater(delay);
	}
	else
            transmitCmdData();  
    }
    public void PLME_SET_confirm(PHYenum status,PPIBAenum PIBAttribute)
    {
        String __FUNCTION__ = "PLME_SET_confirm";
        if ((PIBAttribute == PPIBAenum.phyCurrentChannel)&&(status == PHYenum.p_SUCCESS))
        dispatch(status,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
    }

	//interfaces between MAC and SSCS (or some other upper layer)
    public void MCPS_DATA_request(byte SrcAddrMode,int SrcPANId,/* IE3ADDR */ int SrcAddr,
			       byte DstAddrMode,int DstPANId,/* IE3ADDR */ int DstAddr,
			       int msduLength,MacMessage_802_15_4 msdu,byte msduHandle,byte TxOptions) {
        mcps_data_request(SrcAddrMode,SrcPANId,SrcAddr,DstAddrMode,DstPANId,DstAddr,msduLength,msdu,msduHandle,TxOptions,true, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
    }
    
    public void MCPS_DATA_indication(byte SrcAddrMode,int SrcPANId,/* IE3ADDR */ int SrcAddr,
				  byte DstAddrMode,int DstPANId,/* IE3ADDR */ int DstAddr,
				  byte msduLength,MacMessage_802_15_4 msdu,byte mpduLinkQuality,
				  boolean SecurityUse,byte ACLEntry) {
        msdu.HDR_CMN().num_forwards_ += 1;

		if (msdu.HDR_LRWPAN().msduHandle != 0)	//from peer SSCS
		{
	            //log(msdu.refcopy()); ???
	            sscs.MCPS_DATA_indication(SrcAddrMode,SrcPANId,SrcAddr,DstAddrMode,DstPANId,DstAddr,msduLength,msdu,mpduLinkQuality,SecurityUse,ACLEntry);
		}
		else
	        {
	            //uptarget_.recv(msdu,(Handler) 0); // OLIVER: Send to NET layer
	            sendUp(msdu); // OLIVER: I have added the sendUp(m) method to extract further information from the package
	        }
    }
    
    // Not in NS-2
    public void sendUp(MacMessage_802_15_4 msg) {        
        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) )) {
            System.out.println("************************************************************");        
            System.out.println("* MAC #" + localAddr + " RECEIVED MESSAGE FROM " + msg.HDR_CMN().lastHopAddr + " AND PASSES UP TO NET");
            System.out.println("************************************************************");        
        }
      
        // ray
        //netEntity.receive(msg.getPayload(), new MacAddress(msg.HDR_CMN().lastHopAddr), /*byte macId*/ netId, /* boolean promiscuous */ false, /* boolean broadcast */ (Trace.p802_15_4macDA(msg) == Def.MAC_BROADCAST) );
        // oliver: for sidnet format        
        netEntity.receive(msg.getPayload(), new MacAddress(msg.HDR_CMN().lastHopAddr), /*byte macId*/ netId, /* boolean promiscuous */ false );               
        // OLIVER: 03/01/2009        
        //netEntity.pump(netId);
    }
    
    public void MCPS_PURGE_request(byte msduHandle)
    {
        int i;
	MACenum t_status;

	i = TRANSACLINK.updateTransacLinkByPktOrHandle(Def.tr_oper_del,transacLink1,transacLink2,null,msduHandle);
	t_status = (i == 0) ? MACenum.m_SUCCESS : MACenum.m_INVALID_HANDLE;
	sscs.MCPS_PURGE_confirm(msduHandle,t_status);
    }
    public void MLME_ASSOCIATE_request(byte LogicalChannel,byte CoordAddrMode,int CoordPANId,/* IE3ADDR */ int CoordAddress,
				    byte CapabilityInformation,boolean SecurityEnable)
    {
//        mlme_associate_request(LogicalChannel,CoordAddrMode,CoordPANId,CoordAddress,CapabilityInformation,SecurityEnable,true, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
    }
    public void MLME_ASSOCIATE_response(/*IE3ADDR*/ int DeviceAddress,int AssocShortAddress,MACenum status,boolean SecurityEnable)
    {
        mlme_associate_response(DeviceAddress,AssocShortAddress,status,SecurityEnable,true, PHYenum.p_SUCCESS);
    }
    public void MLME_DISASSOCIATE_request(IE3ADDR DeviceAddress,byte DisassociateReason,boolean SecurityEnable)
    {
        mlme_disassociate_request(DeviceAddress,DisassociateReason,SecurityEnable,true, PHYenum.p_SUCCESS);
    }
    public void MLME_DISASSOCIATE_indication(/* IE3ADDR */ int DeviceAddress,byte DisassociateReason,boolean SecurityUse,byte ACLEntry)
    {
        // DO NOTHING
    }
    public void MLME_GET_request(MPIBAenum PIBAttribute)
    {
        MACenum t_status;
	
	switch(PIBAttribute)
	{
            case macAckWaitDuration:
            case macAssociationPermit:
            case macAutoRequest:
            case macBattLifeExt:
            case macBattLifeExtPeriods:
            case macBeaconPayload:
            case macBeaconPayloadLength:
            case macBeaconOrder:		
            case macBeaconTxTime:
            case macBSN:
            case macCoordExtendedAddress:
            case macCoordShortAddress:
            case macDSN:
            case macGTSPermit:
            case macMaxCSMABackoffs:
            case macMinBE:
            case macPANId:
            case macPromiscuousMode:
            case macRxOnWhenIdle:
            case macShortAddress:
            case macSuperframeOrder:
            case macTransactionPersistenceTime:
            case macACLEntryDescriptorSet:
            case macACLEntryDescriptorSetSize:
            case macDefaultSecurity:
            case macACLDefaultSecurityMaterialLength:
            case macDefaultSecurityMaterial:
            case macDefaultSecuritySuite:
            case macSecurityMode:
                    t_status = MACenum.m_SUCCESS;
                    break;
            default:
                    t_status = MACenum.m_UNSUPPORTED_ATTRIBUTE;
                    break;
	}
	sscs.MLME_GET_confirm(t_status,PIBAttribute,mpib);
    }
/*TBD*/	public void MLME_GTS_request(byte GTSCharacteristics,boolean SecurityEnable) {};
/*TBD*/	public void MLME_GTS_confirm(byte GTSCharacteristics,MACenum status) {};
/*TBD*/	public void MLME_GTS_indication(int DevAddress,byte GTSCharacteristics,
				 boolean SecurityUse, byte ACLEntry) {};
    public void MLME_ORPHAN_response(/* IE3ADDR */ int OrphanAddress,int ShortAddress,boolean AssociatedMember,boolean SecurityEnable)
    {
        mlme_orphan_response(OrphanAddress,ShortAddress,AssociatedMember,SecurityEnable,true, PHYenum.p_SUCCESS);
    }
    public void MLME_RESET_request(boolean SetDefaultPIB)
    {
        mlme_reset_request(SetDefaultPIB,true, PHYenum.p_SUCCESS);
    }
    public void MLME_RX_ENABLE_request(boolean DeferPermit,int RxOnTime,int RxOnDuration)
    {
        mlme_rx_enable_request(DeferPermit,RxOnTime,RxOnDuration,true, PHYenum.p_SUCCESS);
    }
    public void MLME_SCAN_request(byte ScanType,int ScanChannels,byte ScanDuration)
    {
        mlme_scan_request(ScanType,ScanChannels,ScanDuration,true, PHYenum.p_SUCCESS);
    }
    public void MLME_SET_request(MPIBAenum PIBAttribute,MAC_PIB PIBAttributeValue)
    {
        PHYenum p_state;
	MACenum t_status;

	t_status = MACenum.m_SUCCESS;
	switch(PIBAttribute)
	{
            case macAckWaitDuration:
                    phyEntity.PLME_GET_request(PPIBAenum.phyCurrentChannel);	//value will be returned in tmp_ppib
                    if ((tmp_ppib.phyCurrentChannel <= 10)&&(PIBAttributeValue.macAckWaitDuration != 120)
                     || (tmp_ppib.phyCurrentChannel > 10)&&(PIBAttributeValue.macAckWaitDuration != 54))
                            t_status = MACenum.m_INVALID_PARAMETER;
                    else
                            mpib.macAckWaitDuration = PIBAttributeValue.macAckWaitDuration;
                    break;
            case macAssociationPermit:
                            mpib.macAssociationPermit = PIBAttributeValue.macAssociationPermit;
                    break;
            case macAutoRequest:
                            mpib.macAutoRequest = PIBAttributeValue.macAutoRequest;
                    break;
            case macBattLifeExt:
                            mpib.macBattLifeExt = PIBAttributeValue.macBattLifeExt;
                    break;
            case macBattLifeExtPeriods:
                    phyEntity.PLME_GET_request(PPIBAenum.phyCurrentChannel);	//value will be returned in tmp_ppib
                    if ((tmp_ppib.phyCurrentChannel <= 10)&&(PIBAttributeValue.macBattLifeExtPeriods != 8)
                     || (tmp_ppib.phyCurrentChannel > 10)&&(PIBAttributeValue.macBattLifeExtPeriods != 6))
                            t_status = MACenum.m_INVALID_PARAMETER;
                    else
                            mpib.macBattLifeExtPeriods = PIBAttributeValue.macBattLifeExtPeriods;
                    break;
            case macBeaconPayload:
                            //<macBeaconPayloadLength> should be set first
                            //memcpy(mpib.macBeaconPayload,PIBAttributeValue.macBeaconPayload,mpib.macBeaconPayloadLength);
                            mpib.macBeaconPayload = MAC_PIB.copy(PIBAttributeValue.macBeaconPayload, mpib.macBeaconPayloadLength);
                    break;
            case macBeaconPayloadLength:
                    if (PIBAttributeValue.macBeaconPayloadLength > Const.aMaxBeaconPayloadLength)
                            t_status = MACenum.m_INVALID_PARAMETER;
                    else
                            mpib.macBeaconPayloadLength = PIBAttributeValue.macBeaconPayloadLength;
                    break;
            case macBeaconOrder:
                    if (PIBAttributeValue.macBeaconOrder > 15)
                            t_status = MACenum.m_INVALID_PARAMETER;
                    else
                            mpib.macBeaconOrder = PIBAttributeValue.macBeaconOrder;
                    break;
            case macBeaconTxTime:
                    mpib.macBeaconTxTime = PIBAttributeValue.macBeaconTxTime;
                    break;
            case macBSN:
                    mpib.macBSN = PIBAttributeValue.macBSN;
                    break;
            case macCoordExtendedAddress:
                    mpib.macCoordExtendedAddress = PIBAttributeValue.macCoordExtendedAddress;
                    break;
            case macCoordShortAddress:
                    mpib.macCoordShortAddress = PIBAttributeValue.macCoordShortAddress;
                    break;
            case macDSN:
                    mpib.macDSN = PIBAttributeValue.macDSN;
                    break;
            case macGTSPermit:
                    mpib.macGTSPermit = PIBAttributeValue.macGTSPermit;
                    break;
            case macMaxCSMABackoffs:
                    if (PIBAttributeValue.macMaxCSMABackoffs > 5)
                            t_status = MACenum.m_INVALID_PARAMETER;
                    else
                            mpib.macMaxCSMABackoffs = PIBAttributeValue.macMaxCSMABackoffs;
                    break;
            case macMinBE:
                    if (PIBAttributeValue.macMinBE > 3)
                            t_status = MACenum.m_INVALID_PARAMETER;
                    else
                            mpib.macMinBE = PIBAttributeValue.macMinBE;
                    break;
            case macPANId:
                    mpib.macPANId = PIBAttributeValue.macPANId;
                    break;
            case macPromiscuousMode:
                    mpib.macPromiscuousMode = PIBAttributeValue.macPromiscuousMode;
                    //some other operations (refer to sec. 7.5.6.6)
                    mpib.macRxOnWhenIdle = PIBAttributeValue.macPromiscuousMode;
                    p_state = mpib.macRxOnWhenIdle ? PHYenum.p_RX_ON : PHYenum.p_TRX_OFF;
                    phyEntity.PLME_SET_TRX_STATE_request(p_state);
                    break;
            case macRxOnWhenIdle:
                    mpib.macRxOnWhenIdle = PIBAttributeValue.macRxOnWhenIdle;
                    break;
            case macShortAddress:
                    mpib.macShortAddress = PIBAttributeValue.macShortAddress;
                    break;
            case macSuperframeOrder:
                    if (PIBAttributeValue.macSuperframeOrder > 15)
                            t_status = MACenum.m_INVALID_PARAMETER;
                    else
                            mpib.macSuperframeOrder = PIBAttributeValue.macSuperframeOrder;
                    break;
            case macTransactionPersistenceTime:
                    mpib.macTransactionPersistenceTime = PIBAttributeValue.macTransactionPersistenceTime;
                    break;
            case macACLEntryDescriptorSet:
            case macACLEntryDescriptorSetSize:
            case macDefaultSecurity:
            case macACLDefaultSecurityMaterialLength:
            case macDefaultSecurityMaterial:
            case macDefaultSecuritySuite:
            case macSecurityMode:
                    break;		//currently security ignored in simulation
            default:
                    t_status = MACenum.m_UNSUPPORTED_ATTRIBUTE;
                    break;
	}
	sscs.MLME_SET_confirm(t_status,PIBAttribute);
    }
    public void MLME_START_request(int PANId,byte LogicalChannel,byte BeaconOrder,
				byte SuperframeOrder,boolean PANCoordinator,boolean BatteryLifeExtension,
				boolean CoordRealignment,boolean SecurityEnable)
    {
        mlme_start_request(PANId,LogicalChannel,BeaconOrder,SuperframeOrder,PANCoordinator,BatteryLifeExtension,CoordRealignment,SecurityEnable,true, PHYenum.p_SUCCESS);
    }
    public void MLME_SYNC_request(byte LogicalChannel, boolean TrackBeacon)
    {
        mlme_sync_request(LogicalChannel,TrackBeacon,true, PHYenum.p_SUCCESS);
    }
    public void MLME_POLL_request(byte CoordAddrMode,int CoordPANId,/* IE3ADDR */ int CoordAddress,boolean SecurityEnable)
    {
        mlme_poll_request(CoordAddrMode,CoordPANId,CoordAddress,SecurityEnable,false,true, PHYenum.p_SUCCESS);
    }

    public int hdr_dst(char[] hdr,int dst /*= -2*/ )
    {
        return Trace.p802_15_4hdr_dst(hdr,dst);
    }
    public int hdr_src(char[] hdr,int src)
    {
        return Trace.p802_15_4hdr_src(hdr,src /* = -2 */);
    }
    
    public int hdr_type(char[] hdr, int type /* = 0 */)
    {
        return Trace.p802_15_4hdr_type(hdr,type);
    }

    //public Tap tap() ???
    /*public int tap();
    {
        // ===================================================================
        // Objects that want to promiscously listen to the packets before
        // address filtering must inherit from class Tap in order to plug into
        // the tap
        // =================================================================
        
        //return tap_; ???
        return -1;
    }*/
    
    //////////////////////////////////////////////////
    // radio mode
    //

    // MacInterface
    public void setRadioMode(byte mode) {
        throw new RuntimeException("not implemented");
    }
  
    
    //////////////////////////////////////////////////
    // send-related functions
    //

    // MacInterface interface
    public void send(Message msg, MacAddress nextHop) {
    	if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
    		packetFlowLogger.debug("[" +JistAPI.getTime()/Constants.MILLI_SECOND + "ms]["+localAddr+"][MAC.send()] - NWK to MAC - send message to " + nextHop);        
        
        taskP.init();
        resetTimers();
        
        MacMessage_802_15_4 macMsg = new MacMessage_802_15_4();
        // OLIVER: Build the HDR_CMN
        hdr_cmn hdrcmn = new hdr_cmn();
        hdrcmn.direction_ = hdr_cmn.dir_t.DOWN; // packet is to be sent to another node
        hdrcmn.next_hop_ = nextHop.hashCode();
        hdrcmn.lastHopAddr = localAddr.hashCode();
        //hdrcmn.ptype_ = Packet_t.PT_MAC;
        macMsg.setHDR_CMN(hdrcmn);
        
        // OLIVER: Build the HDR_MAC
        hdr_mac hdrmac = new hdr_mac();
        hdrmac.macDA_ = nextHop.hashCode();
        hdrmac.macSA_ = localAddr.hashCode();
        macMsg.setHDR_MAC(hdrmac);
        
        // OLIVER: Build the HDR_LRWPAN
        hdr_lrwpan hdrlrwpan = new hdr_lrwpan();
   
        macMsg.setHDR_LRWPAN(hdrlrwpan);
        
        // OLIVER: Set payload
        macMsg.setPayload(msg); // also, internally, sets the payload size, in bytes, to hdr_cmn
        
        // OLIVER: now pass it to the 802_15_4 handling function
        recv(macMsg);
    }
  
    //////////////////////////////////////////////////
    // receive-related functions
    //

    // MacInterface
     public void peek(Message msg) {
         throw new RuntimeException("not implemented");
     }
    
    public void receive(Message m) {// not in NS-2
        recv((MacMessage_802_15_4)m);
    }
    	
    public void recv(MacMessage_802_15_4 p /* , Handler h */) 
    {
		hdr_lrwpan wph = p.HDR_LRWPAN();
		hdr_cmn ch = p.HDR_CMN();
		boolean noAck;
		int i;
		byte txop;
		FrameCtrl frmCtrl = new FrameCtrl();
		SuperframeSpec t_sfSpec = new SuperframeSpec();
	
	if(ch.direction() == hdr_cmn.dir_t.DOWN) {	// OUTGOING PACKET (from NWK to PHY)
		
		frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
		frmCtrl.parse();
		
		if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) {
			packetFlowLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.recv()] - MAC to PHY - " + frmCtrl.getType());   
    		packetInfoLogger.debug("type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
		}
	    
        //System.out.println("[" + /*__FILE__ + */"."+ /*__FUNCTION__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") outgoing pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = ??, size = " + ch.size());
		
        //-- Notes for power-saving:
		//   It turns out to be very difficult to apply sleeping model in 802.15.4.
		//   First, a node shouldn't go to sleep if peer2peer transmission mode is
		//   used. Non-peer2peer means that a node only communicates with its parent 
		//   and/or children, which requests that pure tree routing be used.
		//   Second, even pure tree routing is used, a node can only go to sleep
		//   if it satisfies both the sleeping condition as a parent (to its children)
		//   and that as a child (to its parent) in a multi-hop environment. To 
		//   satisfy both conditions requires efficient scheduling scheme.
		//   Since ns2, by default, treats the power consumption in idle mode same 
		//   as that in sleeping mode, it makes no difference at this moment whether
		//   we set sleeping mode or not.

		//wake up the node if it is in sleep mode (only for legacy applications)
       

		if (energyManagementUnit != null)
		{
                    if (energyManagementUnit.getBattery().getPercentageEnergyLevel() < 1)                        
                    {
                        drop(p,"ENE"); // not enough energy to transmit this packet
                        if ((Def.DEBUG802_15_4_packetdrop || Def.DEBUG802_15_4) && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                            System.out.println("<NOTE>[Mac802_15_4] - Packet drop by node " + localAddr + " due to insufficient energy at node");
                        return;
                    }
                    if (energyManagementUnit.getEnergyConsumptionModel().getRadioState() == EnergyConsumptionModelImpl.RADIO_SLEEP)
                    {
                        //em.set_node_sleepBlock(0);
                        //em.set_node_state(EnergyModel.INROUTE);
                       
                    }
		}
		/* SSCS should call MCPS_DATA_request() directly
		if (from SSCS)
		{
			MCPS_DATA_request(wph.SrcAddrMode,wph.SrcPANId,wph.SrcAddr,
					  wph.DstAddrMode,wph.DstPANId,wph.DstAddr,
					  ch.size(),p,wph.msduHandle,wph.TxOptions);
		}
		else	//802.15.4-unaware upper layer app. packet
		*/
		{
                    //callback_ = h; ??? handler
                    if (Trace.p802_15_4macDA(p) == Def.MAC_BROADCAST)
                        txop = 0;
                    else
                    {
                        if (Mac802_15_4Impl.ack4data)
                            txop = TxOp_Acked;
                        else
                            txop = 0;
                        txop = (byte)(txop | Mac802_15_4Impl.txOption);
                    }
                    wph.msduHandle = 0;
                    MCPS_DATA_request((byte)0,(byte)0,(byte)0,Const.defFrmCtrl_AddrMode16,mpib.macPANId,Trace.p802_15_4macDA(p), /* OLIVER: ??? carefull ??? */ ch.size(),p,(byte)0,txop);	//direct transmission w/o security
		}
		return;
	} else {	// INCOMING PACKET
	    
		resetCounter(Trace.p802_15_4macSA(p)); // ???

		//if during ED scan, discard all frames received over the PHY layer data service (sec. 7.5.2.1.1)
		//if during Active/Passive scan, discard all frames received over the PHY layer data service that are not beacon frames (sec. 7.5.2.1.2/7.5.2.1.3)
		//if during Orphan scan, discard all frames received over the PHY layer data service that are not coordinator realignment command frames (sec. 7.5.2.1.4)
		frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
		frmCtrl.parse();
		
		if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) {
			packetFlowLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.recv()] - PHY to MAC - " + frmCtrl.getType() + " from PHY");   
			packetInfoLogger.debug("type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
		}
		
		if (taskP.taskStatus(taskPending.TP_mlme_scan_request))
		if (taskP.mlme_scan_request_ScanType == 0x00) {	//ED scan            
            
			if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
				taskPLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.recv()][D][ED-scan]");
                    
			drop(p,"ED"); 
			return;
		}
		else if (((taskP.mlme_scan_request_ScanType == 0x01)	//Active scan
		        ||(taskP.mlme_scan_request_ScanType == 0x02))	//Passive scan
		     && (frmCtrl.frmType != Const.defFrmCtrl_Type_Beacon)) {
            //if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            //System.out.println("[D][APS]" + /*__FILE__ +*/"."+ /*__FUNCTION__ + */"." + /*__LINE__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
			
			if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
				taskPLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.recv()][D][APS - active scan]");
			
			drop(p,"APS"); 
			return;
		}
		else if ((taskP.mlme_scan_request_ScanType == 0x03)	//Orphan scan
		     && ((frmCtrl.frmType != Const.defFrmCtrl_Type_MacCmd)||(wph.MSDU_CmdType != 0x08))) {
            //if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
              //          System.out.println("[D][OPH]" + /*__FILE__ +*/"."+ /*__FUNCTION__ + */ "." + /*__LINE__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());

			if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
				taskPLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.recv()][D][OPH - orphan scan]");
                    
			drop(p,"OPH"); 
			return;
		}

		//drop the packet if corrupted
		if (ch.error() == 1) {
                 //if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                   //  System.out.println("[D][ERR]" + /* __FILE__ + */"."+ /* __FUNCTION__ + */"." + /*__LINE__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
			
			 if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
				 taskPLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.recv()][D][ERR - corrupted]");
           	
             drop(p,"ERR"); 
		     return;
		}
		//drop the packet if the link quality is too bad (basically, collisions)
		if ((wph.rxTotPower-p.txinfo_.RxPr) > 0.0)
		if (p.txinfo_.RxPr/(wph.rxTotPower-p.txinfo_.RxPr) < p.txinfo_.CPThresh) {
             if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                System.out.println("[D][LQI]" + /*__FILE__ +*/"."+ /*__FUNCTION__ + */"." + /*__LINE__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());

            //if (!wph.colFlag)	 ??? nam
            //        nam.flashNodeColor(((double)JistAPI.getTime()/Constants.SECOND)); // ??? nam
            
            if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
            	taskPLogger.debug("[" +JistAPI.getTime()/Constants.MILLI_SECOND + "ms]["+localAddr+"][MAC.recv()][D][LQI - link quality]");
             
            drop(p,"LQI"); 
            return;
		}

		if (frmCtrl.frmType == Const.defFrmCtrl_Type_Beacon) {
			t_sfSpec.SuperSpec = wph.MSDU_SuperSpec;
			t_sfSpec.parse();
			if (t_sfSpec.BO != 15) {
				//update superframe specification
				sfSpec3 = t_sfSpec;
				//calculate the time when we received the first bit of the beacon
				macBcnOtherRxTime = (((double)JistAPI.getTime())/Constants.SECOND - phyEntity.trxTime(p, false)) * phyEntity.getRate_BitsPerSecond('s');
				//update beacon order and superframe order
				macBeaconOrder3 = sfSpec3.BO;
				macSuperframeOrder3 = sfSpec3.SO;
			}
		}

		//---perform filtering (refer to sec. 7.5.6.2)---
		//drop the packet if FCS is not correct (ignored in simulation)
		if (ch.ptype() == Packet_t.PT_MAC)	//perform further filtering only if it is an 802.15.4 packet
		if (!mpib.macPromiscuousMode) {	//perform further filtering only if the PAN is currently not in promiscuous mode
			//check packet type
			if ((frmCtrl.frmType != Const.defFrmCtrl_Type_Beacon)
			  &&(frmCtrl.frmType != Const.defFrmCtrl_Type_Data)
			  &&(frmCtrl.frmType != Const.defFrmCtrl_Type_Ack)
			  &&(frmCtrl.frmType != Const.defFrmCtrl_Type_MacCmd)) {
                     if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                        System.out.println("[D][TYPE]" + /*__FILE__ +*/"."+ /*__FUNCTION__ + */"." + /*__LINE__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());

                    drop(p,"TYPE"); // ??? drop
                    return;
			}
			//check source PAN ID for beacon frame
			if ((frmCtrl.frmType == Const.defFrmCtrl_Type_Beacon)
			  &&(mpib.macPANId != 0xffff)
			  &&(wph.MHR_SrcAddrInfo.panID != mpib.macPANId))
			{
                             if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                System.out.println("[D][PAN]" + /*__FILE__ + */ "."+ /*__FUNCTION__ +*/ "." + /*__LINE__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());

                            drop(p,"PAN"); // ??? drop
                            return;
			}
			//check dest. PAN ID if it is included
			if ((frmCtrl.dstAddrMode == Const.defFrmCtrl_AddrMode16)
			  ||(frmCtrl.dstAddrMode == Const.defFrmCtrl_AddrMode64))
			if ((wph.MHR_DstAddrInfo.panID != 0xffff)
			  &&(wph.MHR_DstAddrInfo.panID != mpib.macPANId))
			{
                             if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                System.out.println("[D][PAN]" + /*__FILE__ + */"."+ /* __FUNCTION__ + */"." + /*__LINE__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());

                            drop(p,"PAN");
                            return;
			}
			//check dest. address if it is included
			if (frmCtrl.dstAddrMode == Const.defFrmCtrl_AddrMode16)
			{
				if ((wph.MHR_DstAddrInfo.addr_16 != 0xffff)
				 && (wph.MHR_DstAddrInfo.addr_16 != mpib.macShortAddress))
				{
                                    if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                           System.out.println("[D][ADR]" + /*__FILE__ +*/ "."+ /*__FUNCTION__ + */"." + /*__LINE__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());

                                    drop(p,"ADR");
                                    return;
				}

			}
			else if (frmCtrl.dstAddrMode == Const.defFrmCtrl_AddrMode64)
			{
				if (wph.MHR_DstAddrInfo.addr_64 != aExtendedAddress)
				{
                                      if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                            System.out.println("[D][ADR]" + /*__FILE__ +*/"."+ /*__FUNCTION__ + */"." + /*__LINE__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());

				      drop(p,"ADR");
				      return;
				}
			}
			//check for Data/MacCmd frame only w/ source address
			if ((frmCtrl.frmType == Const.defFrmCtrl_Type_Data)
			  ||(frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd))
			if (frmCtrl.dstAddrMode == Const.defFrmCtrl_AddrModeNone)
			{
				if (((!capability.FFD)||(DEVICELINK.numberDeviceLink(deviceLink1) == 0))	//I am not a coordinator
				  ||(wph.MHR_SrcAddrInfo.panID != mpib.macPANId))
				{
                                    if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                        System.out.println("[D][PAN]" + /*__FILE__ + */"."+ /*__FUNCTION__ + */"." + /*__LINE__ +*/ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());

                                    drop(p,"PAN");
                                    return;
				}
			}
			//we need to add one more filter for supporting multi-hop beacon enabled mode (not in the draft)
			if (frmCtrl.frmType == Const.defFrmCtrl_Type_Beacon)
			if (wph.MHR_DstAddrInfo.panID != 0xffff)
			if ((mpib.macCoordExtendedAddress != wph.MHR_SrcAddrInfo.addr_64)	//ok even for int address (in simulation)
			&&  (mpib.macCoordExtendedAddress != Const.def_macCoordExtendedAddress))
			{
                              if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                    System.out.println("[D][COO]" + /*__FILE__ +*/"."+ /*__FUNCTION__ + */"." + /*__LINE__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());

				drop(p,"COO");
				return;
			}
		}	//---filtering done---

		// NOTE: perform security task if required (ignored in simulation)

		// Rey: causes busy conflict on ack send - busy checks should be done before sending acks, so i'm moving this code further down
		/*
		// send an acknowledgement if needed (no matter this is a duplicated packet or not)
		if ((frmCtrl.frmType == Const.defFrmCtrl_Type_Data)
		  ||(frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd))
		if (frmCtrl.ackReq)	//acknowledgement required
		{
			//association request command will be ignored under following cases
			if ((frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)
			 && (wph.MSDU_CmdType == (byte)0x01))
			if ((!capability.FFD)			//not an FFD
			 || (mpib.macShortAddress == (int)0xffff)	//not yet joined any PAN
			 || (!mpib.macAssociationPermit))		//association not permitted
			{
				//MacMessage_802_15_4.free(p);
                            p = null;
                            return;
			}
		
			noAck = false;
			if (frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)
			if ((rxCmd != null )||(txBcnCmd != null))
				noAck = true;
			if (!noAck)
			{
				constructACK(p);
				//stop CSMA-CA if it is pending (it will be restored after the transmission of ACK)
				if (backoffStatus == 99)
				{
					backoffStatus = 0;
					csmaca.cancel();
				}
				plme_set_trx_state_request(PHYenum.p_TX_ON);
			}
		}
		else
			resetTRX();
            */

		if (frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)
			if ((rxCmd != null)||(txBcnCmd != null)) {
	             if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) )) {
	                 System.out.println("[D][BSY]" + /*__FILE__ +*/ "."+ /*__FUNCTION__ + */"." + /*__LINE__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
		 			 if (rxCmd != null)
					  	 System.out.println("\trxCmd pkt: type = " + Trace.wpan_pName(rxCmd) + ", src = " + Trace.p802_15_4macSA(rxCmd) + ", dst = " + Trace.p802_15_4macDA(rxCmd) + ", uid =" + rxCmd.HDR_CMN().uid() + ", mac_uid = " + rxCmd.HDR_LRWPAN().uid + ", size = " + rxCmd.HDR_CMN().size());
					 if (txBcnCmd != null)
		                 System.out.println("\ttxBcnCmd pkt: type = " + Trace.wpan_pName(txBcnCmd) + ", src = " + Trace.p802_15_4macSA(txBcnCmd) + ", dst = " + Trace.p802_15_4macDA(txBcnCmd) + ", uid =" + txBcnCmd.HDR_CMN().uid() + ", mac_uid = " + txBcnCmd.HDR_LRWPAN().uid + ", size = " + txBcnCmd.HDR_CMN().size());
	             }  
	
                 drop(p,"BSY"); 
                 if (Def.DEBUG802_15_4_packetdrop) 
                	 System.out.println("Packet dropped (2) at node #" + myNode.getID());
                 return;
			}

		if (frmCtrl.frmType == Const.defFrmCtrl_Type_Data) {
			// Rey: if the packet is received while we're waiting for on ack, we need to drop it as well		
			//if (rxData != null || txData != null) 			
	                    // oliver: put this back otherwise it will break test_ChainUnicast_1_2_3x12
	        if (rxData != null) { 
	            if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
	                System.out.println("[D][BSY]" + /*__FILE__ +*/"."+ /*__FUNCTION__ + */ "." + /*__LINE__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
	
	            if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
	            	packetFlowLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.recv()] - packet received while awaiting for ACK, so we need to drop");
	            
	            drop(p,"BSY"); // packet received while waiting for ACK	            
	            return;
			}
		}
	        
		// Rey: acks are now sent here, after busy checks have been made
        // Oliver: fixes test_ChainUnicast_1_2_3x12
		if ((frmCtrl.frmType == Const.defFrmCtrl_Type_Data) ||
		    (frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd))
				if (frmCtrl.ackReq) {	//acknowledgement required
					//association request command will be ignored under following cases
					if ((frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd) &&
						(wph.MSDU_CmdType == (byte)0x01))
						if ((!capability.FFD)			//not an FFD
						    || (mpib.macShortAddress == 0xffff)	//not yet joined any PAN
						    || (!mpib.macAssociationPermit)) {	//association not permitted
							//MacMessage_802_15_4.free(p);
			                p = null;
			                return;
						}
				
					noAck = false;
					if (frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)
						if ((rxCmd != null )||(txBcnCmd != null))
							noAck = true;
					
					if (!noAck) {
						constructACK(p);
						//stop CSMA-CA if it is pending (it will be restored after the transmission of ACK)
						if (backoffStatus == 99) {
							backoffStatus = 0;
							csmaca.cancel();
						}
						
						plme_set_trx_state_request(PHYenum.p_TX_ON); // preparing to transmit
					}
				}
				else
					resetTRX();
		
		//check duplication -- must be performed AFTER all drop's
		if (frmCtrl.frmType == Const.defFrmCtrl_Type_Beacon)
			i = HLISTLINK.chkAddUpdHListLink(hlistBLink1,hlistBLink2,Trace.p802_15_4macSA(p),wph.MHR_BDSN);
		else if (frmCtrl.frmType != Const.defFrmCtrl_Type_Ack)
			i = HLISTLINK.chkAddUpdHListLink(hlistDLink1,hlistDLink2,Trace.p802_15_4macSA(p),wph.MHR_BDSN);
		else {//Acknowledgement
			//assert(txPkt != null);
            if (txPkt == null) {
                System.err.println("<ERROR>[MAC.recv()] - Null txPkt");
                System.exit(1);
            }
             
            // check ack sequence number to see if it matches the sseq of the
            // txPkt
			if (wph.MHR_BDSN != txPkt.HDR_LRWPAN().MHR_BDSN)
                i = 2;
			else i = 0;
		}
		
		if (frmCtrl.frmType != Const.defFrmCtrl_Type_Ack) // Oliver: 2010-04-19
		if (i == 2) {
			drop(p,"DUP");
			return;
		}
		
		//handle the beacon packet
		if (frmCtrl.frmType == Const.defFrmCtrl_Type_Beacon)
            recvBeacon(p);

		//handle the ack. packet
		else if (frmCtrl.frmType == Const.defFrmCtrl_Type_Ack)
            recvAck(p);

		//handle the command packet
		else if (frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)
            recvCommand(p);
	
		//handle the data packet
		else if (frmCtrl.frmType == Const.defFrmCtrl_Type_Data)
            recvData(p);
	}
    }
    
    public void drop(MacMessage_802_15_4 p, String reason) {
        
    	FrameCtrl frmCtrl = new FrameCtrl();
    	hdr_lrwpan wph = p.HDR_LRWPAN();
    	hdr_cmn ch = p.HDR_CMN();    	
    	frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
		frmCtrl.parse();
    	
		if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
			packetFlowLogger.debug("\t[" +JistAPI.getTime() + "]["+localAddr+"][MAC.drop()] - DROP - drop " + frmCtrl.getType() + " due to " + reason);    	
    	
    	if (frmCtrl.frmType == Const.defFrmCtrl_Type_Ack) // oliver 2010-04-19    
    		return;
    	
        if (!reason.equals("BSY")) { 
           //reset(); 
           //resetTimers(); 
           //taskP.init(); 
           //phyEntity.reset(); 
           if (p != null && !reason.equals("DUP") && !reason.equals("BSY")) {    		
               if (reason.equals("ENE"))
                   netEntity.dropNotify(p.getPayload(), new MacAddress(p.HDR_CMN().next_hop_), Reason.LOW_ENERGY);                            
               else
            		netEntity.dropNotify(p.getPayload(), new MacAddress(p.HDR_CMN().next_hop_), Reason.UNDELIVERABLE);               
           }
    		
  		   netEntity.pump(netId);          
        }
    }
    
    public void recvBeacon(MacMessage_802_15_4 p)
    {
        String __FUNCTION__ = "recvBeacon";
        
        hdr_lrwpan wph = p.HDR_LRWPAN();
	FrameCtrl frmCtrl = new FrameCtrl();
	PendAddrSpec pendSpec = new PendAddrSpec();
	boolean pending;
	double txtime;
	byte ifs;
	int i;
	//update superframe specification
	sfSpec2.SuperSpec = wph.MSDU_SuperSpec;
	sfSpec2.parse();
        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
        {
            hdr_cmn ch = p.HDR_CMN();
            System.out.println("[" + /* __FILE__ + */"."+ /*__FUNCTION__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") M_BEACON [B0: " + sfSpec2.BO + "][S0: " + sfSpec2.SO + "] received: from = " + Trace.p802_15_4macSA(p) + ", dst =" + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size() + ", SN = " + wph.MHR_BDSN);
        }

	//calculate the time when we received the first bit of the beacon
	txtime = phyEntity.trxTime(p, false);

	/* Linux floating number compatibility
	macBcnRxTime = (((double)JistAPI.getTime()/Constants.SECOND) - txtime) * phyEntity.getRate('s');
	*/
	{
	double tmpf;
	tmpf = (double)(JistAPI.getTime())/Constants.SECOND - txtime;
	macBcnRxTime = tmpf * phyEntity.getRate_BitsPerSecond('s');
	}

	//calculate <beaconPeriods2>
	if (p.HDR_CMN().size() <= Const.aMaxSIFSFrameSize)
		ifs = Const.aMinSIFSPeriod;
	else
		ifs = Const.aMinLIFSPeriod;

	/* Linux floating number compatibility
	beaconPeriods2 = (byte)((txtime * phyEntity.getRate('s') + ifs) / aUnitBackoffPeriod);
	*/
	double tmpf;
	tmpf = txtime * phyEntity.getRate_BitsPerSecond('s');
	tmpf += ifs;
	beaconPeriods2 = (byte)(tmpf / Const.aUnitBackoffPeriod);

	/* Linux floating number compatibility
	if (fmod((txtime * phyEntity.getRate('s')+ ifs) ,aUnitBackoffPeriod) > 0.0)
	*/
	if (/*mod(tmpf ,aUnitBackoffPeriod)*/ tmpf % Const.aUnitBackoffPeriod> 0.0)
		beaconPeriods2++;
	//update PAN descriptor
	frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
	frmCtrl.parse();
	panDes2.CoordAddrMode = frmCtrl.srcAddrMode;
	panDes2.CoordPANId = wph.MHR_SrcAddrInfo.panID;
	panDes2.CoordAddress_64 = wph.MHR_SrcAddrInfo.addr_64;		//ok even it is a 16-bit address
	panDes2.LogicalChannel = wph.phyCurrentChannel;
	panDes2.SuperframeSpec = wph.MSDU_SuperSpec;
	gtsSpec2.fields = wph.MSDU_GTSFields;
	gtsSpec2.parse();
	panDes2.GTSPermit = gtsSpec2.permit;
	panDes2.LinkQuality = wph.ppduLinkQuality;
	panDes2.TimeStamp = (int)macBcnRxTime;
	panDes2.SecurityUse = wph.SecurityUse;
	panDes2.ACLEntry = wph.ACLEntry;
	panDes2.SecurityFailure = false;				//ignored in simulation
	panDes2.clusTreeDepth = wph.clusTreeDepth;
	//handle active and passive channel scans
	if ((taskP.taskStatus(taskPending.TP_mlme_scan_request))
	 || (taskP.taskStatus(taskPending.TP_mlme_rx_enable_request)))
	{
		rxBeacon = p;
		dispatch(PHYenum.p_SUCCESS,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
	}

	if ((mpib.macPANId == 0xffff)
	|| (mpib.macPANId != panDes2.CoordPANId)
	|| (taskP.taskStatus(taskPending.TP_mlme_associate_request)))
	{
		//MacMessage_802_15_4.free(p);
		return;		
	}
	numLostBeacons = 0;
	//nam.flashNodeMark(((double)JistAPI.getTime())/Constants.SECOND); ??? nam
	macBeaconOrder2 = sfSpec2.BO;
	macSuperframeOrder2 = sfSpec2.SO;
	//populate <macCoordShortAddress> if needed
	if (mpib.macCoordShortAddress == Const.def_macCoordShortAddress)
	if (frmCtrl.srcAddrMode == Const.defFrmCtrl_AddrMode16)
		mpib.macCoordShortAddress = wph.MHR_SrcAddrInfo.addr_16;
	dispatch(PHYenum.p_SUCCESS,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
	//resume extraction timer if needed
	extractT.resume();
	//CSMA-CA may be waiting for the new beacon
	if (wph.MHR_SrcAddrInfo.panID == mpib.macPANId)
	if (backoffStatus == 99)
		csmaca.newBeacon(CHAR.r/*'r'*/);
	
	//check if need to notify the upper layer
	if ((!mpib.macAutoRequest)||(wph.MSDU_PayloadLen > 0))
		sscs.MLME_BEACON_NOTIFY_indication(wph.MHR_BDSN,/* & */panDes2,wph.MSDU_PendAddrFields.spec,wph.MSDU_PendAddrFields.addrList,wph.MSDU_PayloadLen,wph.MSDU_Payload);
	if (mpib.macAutoRequest)
	{
		//handle the pending packet
		pendSpec.fields = wph.MSDU_PendAddrFields;
		pendSpec.parse();
		pending = false;
		for (i=0;i<pendSpec.numShortAddr;i++)
		{
			if (pendSpec.fields.addrList[i] == mpib.macShortAddress)
			{
				pending = true;
				break;
			}
		}
		if (!pending)
		for (i=0;i<pendSpec.numExtendedAddr;i++)
		{
			if (pendSpec.fields.addrList[pendSpec.numShortAddr + i] == aExtendedAddress)
			{
				pending = true;
				break;
			}
		}

		if (pending)
		{
			//frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
			//frmCtrl.parse();
			mlme_poll_request(frmCtrl.srcAddrMode,wph.MHR_SrcAddrInfo.panID,wph.MHR_SrcAddrInfo.addr_64,capability.secuCapable,true,true, PHYenum.p_SUCCESS);
		}

		log(p);
	}
    }
    
    public void recvAck(MacMessage_802_15_4 p) {
                
        String __FUNCTION__ = "recvAck";
        
        hdr_lrwpan wph = p.HDR_LRWPAN();
		hdr_cmn ch = p.HDR_CMN();
		FrameCtrl frmCtrl = new FrameCtrl();
		frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
		frmCtrl.parse();
		
		if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
			traceACKLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.recvAck()] - handle " + frmCtrl.getType() + " (sseq " + wph.MHR_BDSN + ")");   
		
		if ((txBcnCmd == null)&&(txBcnCmd2 == null)&&(txData == null)) {
			//MacMessage_802_15_4.free(p);
			return;
		}	
	
		// check the sequence number in the ack,
		// to see if it matches that in the <txPkt>
		//if (wph.MHR_BDSN != txPkt.HDR_LRWPAN().MHR_BDSN) { // oliver: 2010-04-23
		if (wph.MHR_BDSN != txData.HDR_LRWPAN().MHR_BDSN) {
			//MacMessage_802_15_4.free(p);
			if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
				traceACKLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.recvAck()]" +
					" - unmatched sequence number of ACK (" + wph.MHR_BDSN + ") " +
					"for DATA (" + txPkt.HDR_LRWPAN().MHR_BDSN + "). Ignoring ... "); 
			return;
		}

		// Rey: in the rare case that the ack is received before phyEntity.sendOverH expires, we need to reset the timer and immediately handle the send over event
		if (phyEntity.isSendOverTimerBusy()/*phyEntity.sendOverH.busy()*/) {
			//phyEntity.sendOverH.cancel();
	                phyEntity.cancelSendOverTimer();
	                //System.out.println("MAC calls sendOverHandler");
			phyEntity.sendOverHandler();
		}
		if (txT.bussy())
			txT.stopTimerr();
		else {
	            if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
	                System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") LATE ACK received: from = " + Trace.p802_15_4macSA(p) + ", SN = " + wph.MHR_BDSN + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid);
	            
	            //only handle late ack. for data packet
	            if (txPkt != txData) {
	            	 if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
	 	            	traceACKLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.recvAck()] - LATE ACK received");
	                //MacMessage_802_15_4.free(p);
	                //return;
	            	// this can cause it to get stuck if we send an ack to an incoming DATA, since txPkt is now the ACK
	            	// however, we know this is the ack for the last sent data, because the sequence number matching
	            	// is perform prior calling recvAck(). So, we finalize that task rather then ignoring it
	            	// If we ignore, the previously sent DATA never receives the ACK and gets stuck permanently
	            	 resetTRX(); // oliver: 2010-04-23
	 				taskSuccess(CHAR.d/*'d'*/, true); // oliver: 2010-04-23
	            	return;
	            }
	
	            if (backoffStatus == 99) {
	                backoffStatus = 0;
	                csmaca.cancel();
	            }
		}

		//set pending flag for data polling
		if (txPkt == txBcnCmd2)
		if ((taskP.taskStatus(taskPending.TP_mlme_poll_request))
		 && (taskP.taskFrFunc(taskPending.TP_mlme_poll_request).equals(__FUNCTION__))) // ???
		{
			frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
			frmCtrl.parse();
			taskP.mlme_poll_request_pending = frmCtrl.frmPending;
		}
		
		dispatch(PHYenum.p_SUCCESS,__FUNCTION__,PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
	
		log(p);
    }
    
    public void recvCommand(MacMessage_802_15_4 p) {
        hdr_lrwpan wph;
		FrameCtrl frmCtrl = new FrameCtrl();
		boolean ackReq;

        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
        {
            wph = p.HDR_LRWPAN();
            hdr_cmn ch = p.HDR_CMN();
            System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") " + Trace.wpan_pName(p) + " received: from = " + Trace.p802_15_4macSA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ",size = " + ch.size() + ", SN = " + wph.MHR_BDSN);
        }

	ackReq = false;
	switch(p.HDR_LRWPAN().MSDU_CmdType)
	{
		case 0x01:	//Association request
			//recv() is in charge of sending ack.
			//MLME-ASSOCIATE.indication() will be passed to upper layer after the transmission of ack.
			assert(rxCmd == null);
			rxCmd = p;
			ackReq = true;
			break;
		case 0x02:	//Association response
			//recv() is in charge of sending ack.
			//MLME-ASSOCIATE.confirm will be passed to upper layer after the transmission of ack.
			assert(rxCmd == null);
			rxCmd = p;
			ackReq = true;
			wph = p.HDR_LRWPAN();
			rt_myNodeID = (wph.MSDU_Payload[0]); /// ??? not sure
                        
                        //if (Def.ZigBeeIF) // #ifdef ZigBeeIF
                          //  sscs.setGetClusTreePara('g',p); ??? ZigBee
                        
    			break;
		case 0x03:	//Disassociation notification
			break;
		case 0x04:	//Data request
			//recv() is in charge of sending ack.
			//pending packet will be sent after the transmission of ack.
			assert(rxCmd == null);
			rxCmd = p;
			ackReq = true;
			break;
		case 0x05:	//PAN ID conflict notification
			break;
		case 0x06:	//Orphan notification
			wph = p.HDR_LRWPAN();
			sscs.MLME_ORPHAN_indication(wph.MHR_SrcAddrInfo.addr_64,false,(byte)0);
			break;
		case 0x07:	//Beacon request
			if (capability.FFD						//I am an FFD
			 && (mpib.macAssociationPermit)					//association permitted
			 && (mpib.macShortAddress != 0xffff)				//allow to send beacons
			 && (mpib.macBeaconOrder == 15))				//non-beacon enabled mode
			{
				//send a beacon using unslotted CSMA-CA
                                if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                    System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ")  before alloc txBcnCmd:\n\t\ttxBeacon\t= " + txBeacon + "\n\t\ttxAck   \t= " + txAck + " \n\t\ttxBcnCmd\t= " + txBcnCmd + "\n\t\ttxBcnCmd2\t= " + txBcnCmd2 + " \n\t\ttxData  \t= " + txData);

                                assert(txBcnCmd == null) ; // !txBcnCmd
				//txBcnCmd = MacMessage_802_15_4.alloc();
				if (txBcnCmd == null) break; // !txBcnCmd
				wph = txBcnCmd.HDR_LRWPAN();
				frmCtrl.FrmCtrl = 0;
				frmCtrl.setFrmType(Const.defFrmCtrl_Type_Beacon);
				frmCtrl.setSecu(secuBeacon);
				frmCtrl.setFrmPending(false);
				frmCtrl.setAckReq(false);
				frmCtrl.setDstAddrMode(Const.defFrmCtrl_AddrModeNone);
				if (mpib.macShortAddress == 0xfffe)
				{
					frmCtrl.setSrcAddrMode(Const.defFrmCtrl_AddrMode64);
					wph.MHR_SrcAddrInfo.panID = mpib.macPANId;
					wph.MHR_SrcAddrInfo.addr_64 = aExtendedAddress;
				}
				else
				{
					frmCtrl.setSrcAddrMode(Const.defFrmCtrl_AddrMode16);
					wph.MHR_SrcAddrInfo.panID = mpib.macPANId;
					wph.MHR_SrcAddrInfo.addr_16 = mpib.macShortAddress;
				}
				sfSpec.SuperSpec = 0;
				sfSpec.setBO((byte)15);
				sfSpec.setBLE(mpib.macBattLifeExt);
				sfSpec.setPANCoor(isPANCoor);
				sfSpec.setAssoPmt(mpib.macAssociationPermit);
				wph.MSDU_GTSFields.spec = 0;
				wph.MSDU_PendAddrFields.spec = 0;
				wph.MSDU_PayloadLen = 0;
                                //if (Def.ZigBeeIF) // #ifdef ZigBeeIF
                                  //  sscs.setGetClusTreePara('s',txBcnCmd); // ??? ZigBee

				constructMPDU((byte)4,txBcnCmd,frmCtrl.FrmCtrl,mpib.macBSN++,wph.MHR_DstAddrInfo,wph.MHR_SrcAddrInfo,sfSpec.SuperSpec,(byte)0,(byte)0);
				//hdr_dst((char[])txBcnCmd.HDR_MAC().serialize(),Trace.p802_15_4macSA(p));
                                 hdr_dst(txBcnCmd.HDR_MAC(), Trace.p802_15_4macSA(p));
                                 hdr_src(txBcnCmd.HDR_MAC(), localAddr.hashCode());
				//hdr_src((char[])txBcnCmd.HDR_MAC().serialize(), (localAddr.hashCode()));
				//txBcnCmd.HDR_CMN().ptype() = Packet_t.PT_MAC;
                                txBcnCmd.HDR_CMN().setPtype(Packet_t.PT_MAC);
   				//for trace
				txBcnCmd.HDR_CMN().next_hop_ = Trace.p802_15_4macDA(txBcnCmd);		//nam needs the nex_hop information
				Trace.p802_15_4hdrBeacon(txBcnCmd);
				//csmacaBegin('c');
                                csmacaBegin(CHAR.c);
			}
			break;
		case 0x08:	//Coordinator realignment
			wph = p.HDR_LRWPAN();
			frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
			frmCtrl.parse();
			if (frmCtrl.dstAddrMode == Const.defFrmCtrl_AddrMode64)		//directed to an orphan device
			{
				//recv() is in charge of sending ack.
				//further handling continues after the transmission of ack.
				assert(rxCmd == null);
				rxCmd = p;
				ackReq = true;
			}
			else								//broadcasted realignment command
			if ((wph.MHR_SrcAddrInfo.addr_64 == mpib.macCoordExtendedAddress)
			&& (wph.MHR_SrcAddrInfo.panID == mpib.macPANId))
			{
				//no specification in the draft as how to handle this packet, so use our discretion
				mpib.macPANId = (wph.MSDU_Payload[0]); //???
				mpib.macCoordShortAddress = ((wph.MSDU_Payload[1] /*+ 2*/));
				tmp_ppib.phyCurrentChannel = wph.MSDU_Payload[4];
				phyEntity.PLME_SET_request(PPIBAenum.phyCurrentChannel,/* & */tmp_ppib);
			}
			break;
		case 0x09:	//GTS request
			break;
		default:
			assert(false);
			break;
	}

	if (!ackReq)
		log(p);
	else	
        {
		//log(p.refcopy());
            ;
        }
    }
    public void recvData(MacMessage_802_15_4 p) {
        hdr_lrwpan wph;
		hdr_cmn ch;
		FrameCtrl frmCtrl = new FrameCtrl();
		byte ifs;

		//pass the data packet to upper layer
		//(we need some time to process the packet -- so delay SIFS/LIFS symbols from now or after finishing sending the ack.)
		//(refer to Figure 60 for details of SIFS/LIFS)
		assert(rxData == null);
		
		rxData = p; // set the receiving data
		
		wph = p.HDR_LRWPAN();
		ch = p.HDR_CMN();
        
        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
              System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") DATA (" + Trace.wpan_pName(p) + ") received: from = " + Trace.p802_15_4macSA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ",size = " + ch.size() + ", SN = " + wph.MHR_BDSN);            

		frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
		frmCtrl.parse();
		rxDataTime = ((double)JistAPI.getTime())/Constants.SECOND;
		if (!frmCtrl.ackReq) {
	        if (Def.DEBUG802_15_4_ack && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
	            System.out.println("[" + JistAPI.getTime() + "][" + localAddr + "][MAC.recvData()] - no ACK required, so dispatching it after IFSH");
			if (ch.size() <= Const.aMaxSIFSFrameSize)
				ifs = Const.aMinSIFSPeriod;
			else
				ifs = Const.aMinLIFSPeriod;
			//Scheduler.instance().schedule(/* & */IFSH, /* & */(IFSH.nullEvent), ifs/phyEntity.getRate('s'));
	        IFSH.executeLater(ifs/phyEntity.getRate_BitsPerSecond('s'));
		}
		else {	//schedule and dispatch after finishing ack. transmission
	          if (Def.DEBUG802_15_4_ack && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
	             System.out.println("[" + JistAPI.getTime() + "][" + localAddr + "][MAC.recvData()] - but dispatching DATA only AFTER finishing the ACK transmission");
		}
	}

    public boolean toParent(MacMessage_802_15_4 p)
    {
        hdr_lrwpan wph = p.HDR_LRWPAN();
	FrameCtrl frmCtrl = new FrameCtrl();

	frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
	frmCtrl.parse();
	if (((frmCtrl.dstAddrMode == Const.defFrmCtrl_AddrMode16)&&(wph.MHR_DstAddrInfo.addr_16 == mpib.macCoordShortAddress))
	||  ((frmCtrl.dstAddrMode == Const.defFrmCtrl_AddrMode64)&&(wph.MHR_DstAddrInfo.addr_64 == mpib.macCoordExtendedAddress)))
		return true;
	else
		return false;
    }

    private void set_trx_state_request(PHYenum state, String frFile, String frFunc,int line)
    {
       
        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
        {
             System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + "): " +
                ((state == PHYenum.p_RX_ON)?"RX_ON":
                (state == PHYenum.p_TX_ON)?"TX_ON":
                (state == PHYenum.p_TRX_OFF)?"TRX_OFF":
                (state == PHYenum.p_FORCE_TRX_OFF)?"FORCE_TRX_OFF":"???") +
                " request from [" + frFile + " : " + frFunc + " : " + line + "]\n");
        }
	trx_state_req = state;
	phyEntity.PLME_SET_TRX_STATE_request(state);
    }
    
    public double locateBoundary(boolean parent, double wtime) {
        //In the case that a node acts as both a coordinator and a device, 
		//transmission of beacons is preferablly to be aligned with reception 
		//of beacons to achieve the best results -- but we cannot control this.
		//For example, the parent may originally work in non-beacon enabled mode
		//and later on begin to work in beacon enabled mode; the parent will
		//not align with the child since it is not supposed to handle the beacons
		//from the child.
		//So the alignment is specifically w.r.t. either transmission of beacons
		//(as a coordinator) or reception of beacons (as a device), but there is
		//no guarantee to satisfy both.
		
		int align;			
		double bcnTxRxTime,bPeriod;
		double newtime = 0.0;
		
		if ((mpib.macBeaconOrder == 15)&&(macBeaconOrder2 == 15))
			return wtime;		
	
		if (parent)			
			align = (macBeaconOrder2 == 15)?1:2;
		else				
			align = (mpib.macBeaconOrder == 15)?2:1;
		
		bcnTxRxTime = (align == 1)?(macBcnTxTime / phyEntity.getRate_BitsPerSecond('s')):(macBcnRxTime / phyEntity.getRate_BitsPerSecond('s'));
		bPeriod = Const.aUnitBackoffPeriod / phyEntity.getRate_BitsPerSecond('s');
	
		/* Linux floating number compatibility
		newtime = fmod(((double)JistAPI.getTime()/Constants.SECOND) + wtime - bcnTxRxTime, bPeriod);
		*/
		{
		double tmpf;
		tmpf = ((double)JistAPI.getTime())/Constants.SECOND + wtime;
		tmpf -= bcnTxRxTime;
		newtime = /* fmod(tmpf, bPeriod); */ tmpf % bPeriod;
		}
	
	        //if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
	          //   System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") delay = bPeriod - fmod = " + bPeriod + "-" + newtime + " = " + bPeriod - newtime);
	
	        if(newtime > 0.0)
		{
	            /* Linux floating number compatibility
	            newtime = wtime + (bPeriod - newtime);
	            */
	            {
	                double tmpf;
	                tmpf = bPeriod - newtime;
	                newtime = wtime + tmpf;
	            }
		}
		else
			newtime = wtime;

	return newtime;
    }
    
    public void txOverHandler()		//transmission over timer handler
    {
        assert(txPkt != null);
	PD_DATA_confirm(PHYenum.p_UNDEFINED);
    }
    
    public void txHandler()			//ack expiration timer handler
    {
        String __FUNCTION__ = "txHandler";
        
        //assert(txBcnCmd != null || txBcnCmd2 != null || txData != null);
        if (txBcnCmd == null && txBcnCmd2 == null && txData == null)
        {
            if (Def.DEBUG802_15_4_err && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                System.out.println("["+localAddr+"][MAC802_15_4]<ERROR> - null txBcnCmd, txBcnCmd2 and txData, which is unexpected");
            //System.exit(1);  //????
            return;
        }

	MacMessage_802_15_4 p;
	hdr_lrwpan wph;
	hdr_cmn ch;
	byte t_numRetry;

	if (txBcnCmd != null)
            p = txBcnCmd;
	else if (txBcnCmd2 != null)
            p = txBcnCmd2;
	else 
            p = txData;
	wph = p.HDR_LRWPAN();
	ch = p.HDR_CMN();

	if (txBcnCmd != null)
            t_numRetry = numBcnCmdRetry;
	else if (txBcnCmd2 != null) 
            t_numRetry = numBcnCmdRetry2;
	else t_numRetry = numDataRetry;
	t_numRetry++;
        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
        	if (t_numRetry > Const.aMaxFrameRetries) 
        		System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") No ACK - giving up: type = " + Trace.wpan_pName(p) + " src = " + Trace.p802_15_4macSA(p) + ", dst = " + Trace.p802_15_4macDA(p) +", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ",size = " + ch.size());
        	else
        		System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") No ACK - retransmitting: type = " + Trace.wpan_pName(p) + " src = " + Trace.p802_15_4macSA(p) + ", dst = " + Trace.p802_15_4macDA(p) +", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ",size = " + ch.size());
        //if (true) return; // ??????    
	//if (t_numRetry > Const.aMaxFrameRetries) ??? nam
	  //  nam.flashLinkFail(((double)JistAPI.getTime())/Constants.SECOND,Trace.p802_15_4macDA(p)); ??? nam

	dispatch(PHYenum.p_BUSY,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);	//the status p_BUSY will be ignored
    }
    public void extractHandler()		//data extraction timer handler
    {
        String __FUNCTION__ = "extractHandler";
        
        if (taskP.taskStatus(taskPending.TP_mlme_associate_request))
            //taskP.setTaskFrFunc(taskPending.TP_mlme_associate_request),__FUNCTION__);
            taskP.setTaskFrFunc(taskPending.TP_mlme_associate_request, __FUNCTION__);
            
	dispatch(PHYenum.p_BUSY,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
    }
    public void assoRspWaitHandler()		//association response wait timer handler
    {
        String __FUNCTION__ = "assoRspWaitHandler";
        
        dispatch(PHYenum.p_BUSY,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
    }
    public void dataWaitHandler()		//data wait timer handler (for indirect transmission)
    {
        String __FUNCTION__ = "dataWaitHandler";
        
        dispatch(PHYenum.p_BUSY,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
    }
    public void rxEnableHandler()		//receiver enable timer handler
    {
        String __FUNCTION__ = "rxEnableHandler";
        
        dispatch(PHYenum.p_SUCCESS,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
    }
    public void scanHandler()			//scan done for current channel
    {
        String __FUNCTION__ = "scanHandler";
        
        if (taskP.mlme_scan_request_ScanType == (byte)0x01)
            //taskP.taskStep(taskPending.TP_mlme_scan_request)++;	
            taskP.taskStepIncrement(taskPending.TP_mlme_scan_request);
        
	dispatch(PHYenum.p_SUCCESS,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
    }
    public void beaconTxHandler(boolean forTX)	//periodic beacon transmission
    {
        hdr_lrwpan wph;
	FrameCtrl frmCtrl = new FrameCtrl();
	TRANSACLINK tmp;
	int i;

	if ((mpib.macBeaconOrder != 15)		//beacon enabled
	|| (oneMoreBeacon))
	if (forTX)
	{
		if (capability.FFD/*&&(numberDeviceLink(&deviceLink1) > 0)*/)
		{
			//enable the transmitter
			beaconWaiting = true;
			plme_set_trx_state_request(PHYenum.p_FORCE_TRX_OFF);	//finish your job before this!
			//assert(txAck == 0);	//It's not true, for the reason that packets can arrive 
						//at any time if the source is in non-beacon mode.
						//This could also happen if a device loses synchronization
						//with its coordinator, or if a coordinator changes beacon
						//order in the middle.
			if (txAck != null)
			{
                                if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                    if (DEVICELINK.updateDeviceLink(Def.tr_oper_est, /* & */deviceLink1, /* & */deviceLink2, Trace.p802_15_4macDA(txAck)) != 0)	//this ACK is for my child                                    
                                       System.out.println("[" + JistAPI.getTime() + "]<MAC>(node " + localAddr + ") outgoing ACK truncated by beacon: src = " + Trace.p802_15_4macSA(txAck) + ", dst = " + Trace.p802_15_4macDA(txAck) + ", uid = " + txAck.HDR_CMN().uid() + ", mac_uid = " + txAck.HDR_LRWPAN().uid + ", size = " + txAck.HDR_CMN().size() + "\n");
	
				//MacMessage_802_15_4.free(txAck);
				txAck = null;
			}
			plme_set_trx_state_request(PHYenum.p_TX_ON);
		}
		else
			assert(false);
	}
	else
	{
		if (capability.FFD/*&&(numberDeviceLink(&deviceLink1) > 0)*/)	//send a beacon here
		{
			//beaconWaiting = false;				
			if ((taskP.taskStatus(taskPending.TP_mlme_start_request))		
			&&  (mpib.macBeaconOrder != 15))
			{
				if (txAck!= null || backoffStatus == 1)
				{
					beaconWaiting = false;
					bcnTxT.start();
					return;
				}
			}
                        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                              System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") before alloc txBeacon:\n\t\ttxBeacon\t= " + txBeacon + "\n\t\ttxAck   \t=" + txAck + "\n\t\ttxBcnCmd\t= %ld\n\t\ttxBcnCmd2\t=" + txBcnCmd + "\n\t\ttxData  \t=" + txBcnCmd2 + " \n = "+  txData );

                        if ( NFAILLINK.updateNFailLink(Def.fl_oper_est,(localAddr.hashCode())) == 0)
			{
				if (txBeacon != null)
				{
					//MacMessage_802_15_4.free(txBeacon);
					txBeacon = null;
				}
				beaconWaiting = false;
				bcnTxT.start();
				return;
			}
			assert(txBeacon == null); // !txBeacon
			//txBeacon = MacMessage_802_15_4.alloc();
			if (/*!txBeacon*/ txBeacon == null)
			{
				bcnTxT.start();		//try to restore the transmission of beacons next time
				return;
			}
			wph = txBeacon.HDR_LRWPAN();
			frmCtrl.FrmCtrl = 0;
			frmCtrl.setFrmType(Const.defFrmCtrl_Type_Beacon);
			frmCtrl.setSecu(secuBeacon);
			frmCtrl.setAckReq(false);
			frmCtrl.setDstAddrMode(Const.defFrmCtrl_AddrModeNone);
			if (mpib.macShortAddress == 0xfffe)
			{
				frmCtrl.setSrcAddrMode(Const.defFrmCtrl_AddrMode64);
				wph.MHR_SrcAddrInfo.panID = mpib.macPANId;
				wph.MHR_SrcAddrInfo.addr_64 = aExtendedAddress;
			}
			else
			{
				frmCtrl.setSrcAddrMode(Const.defFrmCtrl_AddrMode16);
				wph.MHR_SrcAddrInfo.panID = mpib.macPANId;
				wph.MHR_SrcAddrInfo.addr_16 = mpib.macShortAddress;
			}
			sfSpec.SuperSpec = 0;
			sfSpec.setBO(mpib.macBeaconOrder);
			sfSpec.setSO(mpib.macSuperframeOrder);
			sfSpec.setFinCAP((byte)(Const.aNumSuperframeSlots - 1));		//TBD: may be less than <aNumSuperframeSlots> when considering GTS
			sfSpec.setBLE(mpib.macBattLifeExt);
			sfSpec.setPANCoor(isPANCoor);
			sfSpec.setAssoPmt(mpib.macAssociationPermit);
			//populate the GTS fields -- more TBD when considering GTS
			gtsSpec.fields.spec = 0;
			gtsSpec.setPermit(mpib.macGTSPermit);
			wph.MSDU_GTSFields = gtsSpec.fields;
			//--- populate the pending address list ---
			pendAddrSpec.numShortAddr = 0;
			pendAddrSpec.numExtendedAddr = 0;
			TRANSACLINK.purgeTransacLink(/* & */transacLink1, /* & */transacLink2);
			tmp = transacLink1;
			i = 0;
			while (tmp != null)
			{
				if (tmp.pendAddrMode == Const.defFrmCtrl_AddrMode16)
				{
					if (DEVICELINK.updateDeviceLink(Def.tr_oper_est,/* & */deviceLink1,/* & */deviceLink2,tmp.pendAddr64) == 0)
						i = pendAddrSpec.addShortAddr(tmp.pendAddr16);		//duplicated address filtered out
				}
				else
				{
					if (DEVICELINK.updateDeviceLink(Def.tr_oper_est,/* & */deviceLink1,/* & */deviceLink2,tmp.pendAddr64) == 0)
						i = pendAddrSpec.addExtendedAddr(tmp.pendAddr64);	//duplicated address filtered out
				}
				if (i >= 7) break;
				tmp = tmp.next;
			}
			pendAddrSpec.format();
			wph.MSDU_PendAddrFields = pendAddrSpec.fields;
			frmCtrl.setFrmPending(i>0);
			//To populate the beacon payload field, <macBeaconPayloadLength> and <macBeaconPayload>
			//should be set first, in that order (use primitive MLME_SET_request).
			wph.MSDU_PayloadLen = mpib.macBeaconPayloadLength;
			//memcpy(wph.MSDU_Payload,mpib.macBeaconPayload,mpib.macBeaconPayloadLength); // ???
                        wph.MSDU_Payload = MAC_PIB.copy(mpib.macBeaconPayload, mpib.macBeaconPayloadLength);
			//-----------------------------------------
                        //if (Def.ZigBeeIF)  // ??? ZigBee
                            //sscs.setGetClusTreePara('s',txBeacon); // ??? ZigBee
                        
			constructMPDU((byte)(2 + gtsSpec.size() + pendAddrSpec.size() + mpib.macBeaconPayloadLength),txBeacon,frmCtrl.FrmCtrl,mpib.macBSN++,wph.MHR_DstAddrInfo,wph.MHR_SrcAddrInfo,sfSpec.SuperSpec,(byte)0,0);
			//hdr_src((char[])txBeacon.HDR_MAC().serialize(), (localAddr.hashCode()));
			//hdr_dst((char[])txBeacon.HDR_MAC().serialize(),Def.MAC_BROADCAST);
                         hdr_src(txBeacon.HDR_MAC(), localAddr.hashCode());
                         hdr_dst(txBeacon.HDR_MAC(), Def.MAC_BROADCAST);
			//txBeacon.HDR_CMN().ptype() = Packet_t.PT_MAC;
                        txBeacon.HDR_CMN().setPtype(Packet_t.PT_MAC);
			txBeacon.HDR_CMN().next_hop_ = Trace.p802_15_4macDA(txBeacon);		//nam needs the nex_hop information
			Trace.p802_15_4hdrBeacon(txBeacon);
                        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                            System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") transmit BEACON to " + Trace.p802_15_4macDA(txBeacon) + ": SN = " + txBeacon.HDR_LRWPAN().MHR_BDSN + ", uid = " + txBeacon.HDR_CMN().uid() + ", mac_uid = " + txBeacon.HDR_LRWPAN().uid + "\n");
			txPkt = txBeacon;
			//txBeacon.HDR_CMN().direction() = hdr_cmn.DOWN;
                        txBeacon.HDR_CMN().setDirection(hdr_cmn.dir_t.DOWN);
			//nam.flashNodeMark(((double)JistAPI.getTime()/Constants.SECOND));
			sendDown(txBeacon.copy()/*, this*/);
                        
			mpib.macBeaconTxTime = (int)(((double)JistAPI.getTime())/Constants.SECOND * phyEntity.getRate_BitsPerSecond('s'));
                    macBcnTxTime = ((double)JistAPI.getTime())/Constants.SECOND * phyEntity.getRate_BitsPerSecond('s');	//double used for accuracy
			oneMoreBeacon = false;
		}
		else
			assert(false);
	}
	bcnTxT.start();	//don't disable this even beacon not enabled (beacon may be temporarily disabled like in channel scan, but it will be enabled again)
    }
    public void beaconRxHandler()		//periodic beacon reception
    {
        if (macBeaconOrder2 != 15)		//beacon enabled (do nothing if beacon not enabled)
	{
            if (txAck != null)
            {
                    //MacMessage_802_15_4.free(txAck); // ???
                    txAck = null;
            }
            //enable the receiver
            plme_set_trx_state_request(PHYenum.p_RX_ON);
            if (taskP.mlme_sync_request_tracking)	
            {
                    //a better way is using another timer to detect <numLostBeacons> right after the header of superframe
                    if (numLostBeacons > Const.aMaxLostBeacons)
                    {
                            //char[] label = new char[11];
                            //String label;
                            //label[0] = 0;		
                            //strcpy(label,"\" \"");
                            //label = "\" \"";
                            //if (ZigBeeIF)  /// ??? ZigBee
                              //   if (sscs.t_isCT) /// ??? ZigBee
                                    //label = "\\" + ((sscs.RNType()) ? "+" : "-") + "\\";

                            //nam.changeLabel(((double)JistAPI.getTime()/Constants.SECOND),label); // ???
                            //changeNodeColor(((double)JistAPI.getTime()/Constants.SECOND),Nam802_15_4.def_Node_clr); ???
                            sscs.MLME_SYNC_LOSS_indication(MACenum.m_BEACON_LOSS);
                            numLostBeacons = 0;
                    }
                    else
                    {
                            numLostBeacons++;
                            bcnRxT.start();
                    }
		}
	}
    }
    public void beaconSearchHandler()		//beacon searching times out during synchronization
    {
        String __FUNCTION__ = "beaconSearchHandler";
        
        dispatch(PHYenum.p_BUSY,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
    }
    
    public void isPanCoor(boolean isPC)
    {
        	if (isPANCoor == isPC)
		return;

	//if (isPC) \\ ??? nam
          //  changeNodeColor(((double)JistAPI.getTime())/Constants.SECOND, Nam802_15_4.def_PANCoor_clr); // ??? nam
	//else if (isPANCoor) ??? nam
          //  changeNodeColor(((double)JistAPI.getTime())/Constants.SECOND,   // ??? nam
            //   (mpib.macPANId == (byte)0xffff) ? Nam802_15_4.def_Node_clr : ??? nam
              // (mpib.macAssociationPermit) ? Nam802_15_4.def_Coor_clr : Nam802_15_4.def_Dev_clr); // ??? nam
	isPANCoor = isPC;
    }
    
    //-------------------------------------------------------------------------------------

    String[] taskName = {   "NONE",
                            "MCPS-DATA.request",
                            "MLME-ASSOCIATE.request",
                            "MLME-ASSOCIATE.response",
                            "MLME-DISASSOCIATE.request",
                            "MLME-ORPHAN.response",
                            "MLME-RESET.request",
                            "MLME-RX-ENABLE.request",
                            "MLME-SCAN.request",
                            "MLME-START.request",
                            "MLME-SYNC.request",
                            "MLME-POLL.request",
                            "CCA_csmaca",
                            "RX_ON_csmaca"};
    
    private void checkTaskOverflow(byte task)
    {
        //Though we assume the upper layer should know what it is doing -- should send down requests one by one.
	//But we'd better check again (we have no control over upper layer and we don't know who is operating on the upper layer)
	if (taskP.taskStatus(task))
	{
            System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") task overflow: " + taskName[task] + "\n");
            System.exit(1);
	}
	else
	{
            //taskP.taskStep(task) = 0;
            taskP.setTaskStep(task, (byte)0);
            //(taskP.taskFrFunc(task))[0] = 0;
            String tmp = taskP.taskFrFunc(task);
            char[] ctmp = tmp.toCharArray();
            if (ctmp.length == 0)
                ctmp = new char[1];  
            ctmp[0] = '0';
            taskP.setTaskFrFunc(task, ctmp.toString());
            
	}
    }
    
    private void dispatch(PHYenum status, String frFunc,PHYenum req_state /*= PHYenum.p_SUCCESS*/,MACenum mStatus /*= MACenum.m_SUCCESS*/) // ???  
    {
        hdr_lrwpan wph;
	hdr_cmn ch;
	FrameCtrl frmCtrl = new FrameCtrl();
	byte ifs;
	int i;
        
        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            System.out.println("[" + JistAPI.getTime() + "]<MAC>(node" + localAddr + ") dispatch status = " + status + " frFunc = " + frFunc + "reqState = " + req_state + "mStatus = " + mStatus);
	

	if (frFunc.equals("csmacaCallBack"))
	{
		if (txCsmaca == txBcnCmd2)
		{
			if (taskP.taskStatus(taskPending.TP_mlme_scan_request)
			&& (taskP.taskFrFunc(taskPending.TP_mlme_scan_request).equals(frFunc)))
			{
				if ((taskP.mlme_scan_request_ScanType == (byte)0x01)	//active scan
				||  (taskP.mlme_scan_request_ScanType == (byte)0x03)){}	//orphan scan
					mlme_scan_request(taskP.mlme_scan_request_ScanType,taskP.mlme_scan_request_ScanChannels,taskP.mlme_scan_request_ScanDuration,false,status);
			}
			else if (taskP.taskStatus(taskPending.TP_mlme_start_request)
			&& (taskP.taskFrFunc(taskPending.TP_mlme_start_request).equals(frFunc)))
				mlme_start_request(taskP.mlme_start_request_PANId,taskP.mlme_start_request_LogicalChannel,taskP.mlme_start_request_BeaconOrder,taskP.mlme_start_request_SuperframeOrder,taskP.mlme_start_request_PANCoordinator,taskP.mlme_start_request_BatteryLifeExtension,false,taskP.mlme_start_request_SecurityEnable,false,status);
			else if (taskP.taskStatus(taskPending.TP_mlme_associate_request)
			&& (taskP.taskFrFunc(taskPending.TP_mlme_associate_request).equals(frFunc)))
				mlme_associate_request((byte)0,(byte)0,(int)0,(int)0,(byte)0,taskP.mlme_associate_request_SecurityEnable,false,status, MACenum.m_SUCCESS);
			else if (taskP.taskStatus(taskPending.TP_mlme_poll_request)
			&& (taskP.taskFrFunc(taskPending.TP_mlme_poll_request).equals(frFunc)))
				mlme_poll_request(taskP.mlme_poll_request_CoordAddrMode,taskP.mlme_poll_request_CoordPANId,taskP.mlme_poll_request_CoordAddress,taskP.mlme_poll_request_SecurityEnable,taskP.mlme_poll_request_autoRequest,false,status);
			else	//default handling for txBcnCmd2
			{
				if (status == PHYenum.p_IDLE)
					plme_set_trx_state_request(PHYenum.p_TX_ON);
				else {
					//freePkt(txBcnCmd2 != null);
					txBcnCmd2 = null;
					csmacaResume();		//other packet may be waiting
				}
			}
		}
		else if (txCsmaca == txData) {
			assert(taskP.taskStatus(taskPending.TP_mcps_data_request)
			&& (taskP.taskFrFunc(taskPending.TP_mcps_data_request).equals(frFunc)));

			if ((taskP.mcps_data_request_TxOptions & TxOp_GTS) == (byte)1)		//GTS transmission
			{
				;	//TBD
			}
			else if (((taskP.mcps_data_request_TxOptions & TxOp_Indirect) == 1)	//indirect transmission
			&& (capability.FFD && (DEVICELINK.numberDeviceLink(deviceLink1) > 0)))	//I am a coordinator
			{
				if (status != PHYenum.p_IDLE)
					mcps_data_request((byte)0,(int)0,(int)0,(byte)0,(int)0,(int)0,(byte)0,null,(byte)0,taskP.mcps_data_request_TxOptions,false,PHYenum.p_BUSY,MACenum.m_CHANNEL_ACCESS_FAILURE);
				else
				{
					//taskP.setTaskFrFunc(taskPending.TP_mcps_data_request),"PD_DATA_confirm");
                                        taskP.setTaskFrFunc(taskPending.TP_mcps_data_request, "PD_DATA_confirm");
					plme_set_trx_state_request(PHYenum.p_TX_ON);
				}
			}
			else		//direct transmission: in this case, let mcps_data_request() take care of everything
				mcps_data_request((byte)0,(int)0,(int)0,(byte)0,(int)0,(int)0,(byte)0,null,(byte)0,taskP.mcps_data_request_TxOptions,false,status, MACenum.m_SUCCESS);
		}
		else if (txCsmaca == txBcnCmd)	//default handling for txBcnCmd
		{
			wph = txBcnCmd.HDR_LRWPAN();
			frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
			frmCtrl.parse();
			if (status == PHYenum.p_IDLE)
			{
				if ((frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)		//command packet
				&& (wph.MSDU_CmdType == (byte)0x02))				//association response packet
					//taskP.setTaskFrFunc(taskPending.TP_mlme_associate_response),"PD_DATA_confirm");
                                        taskP.setTaskFrFunc(taskPending.TP_mlme_associate_response,"PD_DATA_confirm");
				else if ((frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)	//command packet
				&& (wph.MSDU_CmdType == 0x08))				//coordinator realignment response packet
					//taskP.setTaskFrFunc(taskPending.TP_mlme_orphan_response),"PD_DATA_confirm");
                                        taskP.setTaskFrFunc(taskPending.TP_mlme_orphan_response,"PD_DATA_confirm");
				plme_set_trx_state_request(PHYenum.p_TX_ON);
			}
			else
			{
				if ((frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)		//command packet
				&& (wph.MSDU_CmdType == (byte)0x02))				//association response packet
					mlme_associate_response(taskP.mlme_associate_response_DeviceAddress,(int)0,MACenum.m_CHANNEL_ACCESS_FAILURE,false,false,PHYenum.p_BUSY);	//status returned in MACenum rather than in PHYenum
				else if ((frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)	//command packet
				&& (wph.MSDU_CmdType == 0x08))				//coordinator realignment response packet
					mlme_orphan_response(taskP.mlme_orphan_response_OrphanAddress,(int)0,true,false,false,PHYenum.p_BUSY);
				else
				{
					//freePkt(txBcnCmd != null);
					txBcnCmd = null;
					csmacaResume();		//other packets may be waiting
				}
			}
		}
		//else		//may be purged from pending list
	}
	else if (frFunc.equals("PD_DATA_confirm"))
	{
		if (txPkt == txBeacon)
		{
			if (taskP.taskStatus(taskPending.TP_mlme_start_request)
			&& (taskP.taskFrFunc(taskPending.TP_mlme_start_request).equals(frFunc)))
				mlme_start_request(taskP.mlme_start_request_PANId,taskP.mlme_start_request_LogicalChannel,taskP.mlme_start_request_BeaconOrder,taskP.mlme_start_request_SuperframeOrder,taskP.mlme_start_request_PANCoordinator,taskP.mlme_start_request_BatteryLifeExtension,false,taskP.mlme_start_request_SecurityEnable,false,status);
			else	//default handling
			{
				resetTRX();
				taskSuccess(CHAR.b/*'b'*/, true);
			}
		}
		else if (txPkt == txAck)
		{
			if (rxCmd != null)
			{
				ch = rxCmd.HDR_CMN();
				if (ch.size() <= Const.aMaxSIFSFrameSize)
					ifs = Const.aMinSIFSPeriod;
				else
					ifs = Const.aMinLIFSPeriod;
				//Scheduler.instance().schedule(/* & */IFSH, /* & */(IFSH.nullEvent), ifs/phyEntity.getRate('s'));
                                
				resetTRX();
				taskSuccess(CHAR.a/*'a'*/, true);
                                
                                //JistAPI.sleepBlock((long)(ifs/phyEntity.getRate('s') * Constants.SECOND));
                                IFSH.executeLater(ifs/phyEntity.getRate_BitsPerSecond('s'));
			}
			else if (rxData != null)	//default handling (virtually the only handling needed) for <rxData>
			{
				ch = rxData.HDR_CMN();
				if (ch.size() <= Const.aMaxSIFSFrameSize)
					ifs = Const.aMinSIFSPeriod;
				else
					ifs = Const.aMinLIFSPeriod;
				//Scheduler.instance().schedule(/* & */IFSH, /* & */(IFSH.nullEvent), ifs/phyEntity.getRate('s'));
				resetTRX();
				taskSuccess(CHAR.a/*'a'*/, true);
                                
                                JistAPI.sleepBlock((long)(ifs/phyEntity.getRate_BitsPerSecond('s') * Constants.SECOND));
                                IFSH.executeLater(ifs/phyEntity.getRate_BitsPerSecond('s'));
			}
			else	//ack. for duplicated packet
			{
				resetTRX();
				taskSuccess(CHAR.a/*'a'*/, true);
			}
		}
		else if (txPkt == txBcnCmd)
		{
			//default handling -- should be replaced once a specific task will handle this
			wph = txBcnCmd.HDR_LRWPAN();
			ch = txBcnCmd.HDR_CMN();
			frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
			frmCtrl.parse();
			if (frmCtrl.ackReq)	//ack. required
			{
				//enable the receiver
				plme_set_trx_state_request(PHYenum.p_RX_ON);
				txT.startTimer(mpib.macAckWaitDuration/phyEntity.getRate_BitsPerSecond('s'));
				waitBcnCmdAck = true;
			}
			else		//assume success if ack. not required
			{
				resetTRX();
				taskSuccess(CHAR.c/*'c'*/, true);
			}
		}
		else if (txPkt == txBcnCmd2)
		{
			if (taskP.taskStatus(taskPending.TP_mlme_scan_request)
			&& ((taskP.mlme_scan_request_ScanType == 0x01)		//active scan
			  ||(taskP.mlme_scan_request_ScanType == 0x03))		//orphan scan
			&& (taskP.taskFrFunc(taskPending.TP_mlme_scan_request).equals(frFunc)))
				mlme_scan_request(taskP.mlme_scan_request_ScanType,taskP.mlme_scan_request_ScanChannels,taskP.mlme_scan_request_ScanDuration,false,status);
			else if (taskP.taskStatus(taskPending.TP_mlme_start_request)
			&& (taskP.taskFrFunc(taskPending.TP_mlme_start_request).equals(frFunc)))
				mlme_start_request(taskP.mlme_start_request_PANId,taskP.mlme_start_request_LogicalChannel,taskP.mlme_start_request_BeaconOrder,taskP.mlme_start_request_SuperframeOrder,taskP.mlme_start_request_PANCoordinator,taskP.mlme_start_request_BatteryLifeExtension,false,taskP.mlme_start_request_SecurityEnable,false,status);
			else if (taskP.taskStatus(taskPending.TP_mlme_associate_request)
			&& (taskP.taskFrFunc(taskPending.TP_mlme_associate_request).equals(frFunc)))
				mlme_associate_request((byte)0,(byte)0,(int)0,(int)0,(byte)0,taskP.mlme_associate_request_SecurityEnable,false,status, MACenum.m_SUCCESS);
			else if (taskP.taskStatus(taskPending.TP_mlme_poll_request)
			&& (taskP.taskFrFunc(taskPending.TP_mlme_poll_request).equals(frFunc)))
				mlme_poll_request(taskP.mlme_poll_request_CoordAddrMode,taskP.mlme_poll_request_CoordPANId,taskP.mlme_poll_request_CoordAddress,taskP.mlme_poll_request_SecurityEnable,taskP.mlme_poll_request_autoRequest,false,status);
			else	//default handling
			{
				wph = txBcnCmd2.HDR_LRWPAN();
				ch = txBcnCmd2.HDR_CMN();
				frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
				frmCtrl.parse();
				if (frmCtrl.ackReq)	//ack. required
				{
					//enable the receiver
					plme_set_trx_state_request(PHYenum.p_RX_ON);
					txT.startTimer(mpib.macAckWaitDuration/phyEntity.getRate_BitsPerSecond('s'));
					waitBcnCmdAck2 = true;
				}
				else		//assume success if ack. not required
				{
					resetTRX();
					taskSuccess(CHAR.C/*'C'*/, true);
				}
			}
		}
		else if (txPkt == txData)
		{
			assert((taskP.taskStatus(taskPending.TP_mcps_data_request))
			&& (taskP.taskFrFunc(taskPending.TP_mcps_data_request).equals(frFunc)));

			wph = txData.HDR_LRWPAN();
			ch = txData.HDR_CMN();
			frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
			frmCtrl.parse();
			if (taskP.taskStatus(taskPending.TP_mcps_data_request))
			{
				if ((taskP.mcps_data_request_TxOptions & TxOp_GTS) == 1)		//GTS transmission
				{
					;	//TBD
				}
				else if (((taskP.mcps_data_request_TxOptions & TxOp_Indirect) == 1)	//indirect transmission
				&& (capability.FFD && (DEVICELINK.numberDeviceLink(/* & */deviceLink1) > 0)))	//I am a coordinator
				{
					if (!frmCtrl.ackReq)	//ack. not required
						mcps_data_request((byte)0,(int)0,(int)0,(byte)0,(int)0,(int)0,(byte)0,null,(byte)0,taskP.mcps_data_request_TxOptions,false,PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
					else
					{
						//taskP.setTaskFrFunc(taskPending.TP_mcps_data_request),"recvAck");
                                                taskP.setTaskFrFunc(taskPending.TP_mcps_data_request,"recvAck");
						//enable the receiver
						plme_set_trx_state_request(PHYenum.p_RX_ON);
						txT.startTimer(mpib.macAckWaitDuration/phyEntity.getRate_BitsPerSecond('s'));
						waitDataAck = true;
					}
				}
				else		//direct transmission: in this case, let mcps_data_request() take care of everything
					mcps_data_request((byte)0,(int)0,(int)0,(byte)0,(int)0,(int)0,(byte)0,null,(byte)0,taskP.mcps_data_request_TxOptions,false,status,  MACenum.m_SUCCESS);
			}
			else	//default handling (seems impossible)
			{
				if (frmCtrl.ackReq)	//ack. required
				{
					//enable the receiver
					plme_set_trx_state_request(PHYenum.p_RX_ON);
					txT.startTimer(mpib.macAckWaitDuration/phyEntity.getRate_BitsPerSecond('s'));
					waitDataAck = true;
				}
				else		//assume success if ack. not required
				{
					resetTRX();
					taskSuccess(CHAR.d/*'d'*/, true);
				}
			}
		}
		//else		//may be purged from pending list
	}
        
	else if (frFunc.equals("recvAck")) {		//always check the task status if the dispatch comes from recvAck()
		if (txPkt == txData) {
			if ((taskP.taskStatus(taskPending.TP_mcps_data_request))
		 	 && (taskP.taskFrFunc(taskPending.TP_mcps_data_request).equals(frFunc)))
				mcps_data_request((byte)0,(int)0,(int)0,(byte)0,(int)0,(int)0,(byte)0,null,(byte)0,taskP.mcps_data_request_TxOptions,false,PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
			else	//default handling for <txData>
			{
				if (taskP.taskStatus(taskPending.TP_mcps_data_request))	//seems late ACK received
					//taskP.taskStatus(taskPending.TP_mcps_data_request) = false;
                    taskP.setTaskStatus(taskPending.TP_mcps_data_request, false);
				resetTRX();
				taskSuccess(CHAR.d/*'d'*/, true);
			}
		}
		else if (txPkt == txBcnCmd2)
		{
			if (taskP.taskStatus(taskPending.TP_mlme_associate_request)
		 	&& (taskP.taskFrFunc(taskPending.TP_mlme_associate_request).equals(frFunc)))
				mlme_associate_request((byte)0,(byte)0,(int)0,(int)0,(byte)0,taskP.mlme_associate_request_SecurityEnable,false,PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
			else if (taskP.taskStatus(taskPending.TP_mlme_poll_request)
		 	&& (taskP.taskFrFunc(taskPending.TP_mlme_poll_request).equals(frFunc)))
				mlme_poll_request(taskP.mlme_poll_request_CoordAddrMode,taskP.mlme_poll_request_CoordPANId,taskP.mlme_poll_request_CoordAddress,taskP.mlme_poll_request_SecurityEnable,taskP.mlme_poll_request_autoRequest,false,PHYenum.p_SUCCESS);
			else	//default handling for <txBcnCmd2>
				taskSuccess(CHAR.C/*'C'*/, true);
		}
		else if	(txPkt == txBcnCmd)	//default handling for <txBcnCmd>
		{
			wph = txBcnCmd.HDR_LRWPAN();
			frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
			frmCtrl.parse();
			if ((frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)			//command packet
			 && (wph.MSDU_CmdType == 0x02))				//association response packet
				mlme_associate_response(taskP.mlme_associate_response_DeviceAddress,(int)0,MACenum.m_SUCCESS,false,false,PHYenum.p_SUCCESS);
			else if ((frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)		//command packet
			&& (wph.MSDU_CmdType == 0x08))					//coordinator realignment response packet
				mlme_orphan_response(taskP.mlme_orphan_response_OrphanAddress,(int)0,true,false,false, PHYenum.p_SUCCESS);
			else
				taskSuccess(CHAR.c/*'c'*/, true);
		}
		//else		//may be purged from pending list
	}
	else if (frFunc.equals("txHandler"))	//always check the task status if the dispatch comes from a timer
	{
		if (txPkt == txData)
		{
			if ((!taskP.taskStatus(taskPending.TP_mcps_data_request))
		 	 || (!taskP.taskFrFunc(taskPending.TP_mcps_data_request).equals("recvAck") )) // != 0
		 	 	return;

			if (taskP.taskStatus(taskPending.TP_mcps_data_request))
			if ((taskP.mcps_data_request_TxOptions & TxOp_GTS) == 1)		//GTS transmission
			{
				;	//TBD
			}
			else if (((taskP.mcps_data_request_TxOptions & TxOp_Indirect) == 1)	//indirect transmission
			&& (capability.FFD && (DEVICELINK.numberDeviceLink(/* & */deviceLink1) > 0)))	//I am a coordinator
			{
				//there is contradiction in the draft:
				//page 156, line 16: (for transaction, i.e., indirect transmission) "all subsequent retransmissions shall be transmitted using CSMA-CA"
				//page 158, line 14-16:
				//	"if a single transmission attempt has failed and the transmission was indirect, the coordinator shall not
				//	retransmit the data or MAC command frame. Instead, the frame shall remain in the transaction queue of the
				//	coordinator."
				//the description on page 158 is more reasonable (though we already proceeded according to page 156)
				/* now follow page 158
				numDataRetry++;
				if (numDataRetry <= aMaxFrameRetries)
				{
					/* no need to check if the packet has been purged -- if purged, then taskFailed() should have set txData = 0
					wph = txData.HDR_LRWPAN();
					frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
					frmCtrl.parse();
					i = updateTransacLinkByPktOrHandle(Def.tr_oper_est,&transacLink1,&transacLink2,txData);
					if (i != 0)	//already purged from pending list
					{
						MacMessage_802_15_4.free(txData != null);
						txData = 0;
						return;
					}
					* / -- don't end here, but afte 'else'
					waitDataAck = false;
					csmacaResume();
				}
				else
				*/
					mcps_data_request((byte)0,(int)0,(int)0,(byte)0,(int)0,(int)0,(byte)0,null,(byte)0,taskP.mcps_data_request_TxOptions,false,PHYenum.p_BUSY,MACenum.m_NO_ACK);
			}
			else		//direct transmission: in this case, let mcps_data_request() take care of everything
				mcps_data_request((byte)0,(int)0,(int)0,(byte)0,(int)0,(int)0,(byte)0,null,(byte)0,taskP.mcps_data_request_TxOptions,false,PHYenum.p_BUSY, MACenum.m_SUCCESS);	//status can be anything but p_SUCCESS
		}
		else if (txPkt == txBcnCmd2) {
			if (taskP.taskStatus(taskPending.TP_mlme_associate_request)
		 	&& (taskP.taskFrFunc(taskPending.TP_mlme_associate_request).equals("recvAck")))
				mlme_associate_request((byte)0,(byte)0,(int)0,(int)0,(byte)0,taskP.mlme_associate_request_SecurityEnable,false,PHYenum.p_BUSY, MACenum.m_SUCCESS);	//status can anything but p_SUCCESS
			else if (taskP.taskStatus(taskPending.TP_mlme_poll_request)
		 	&& (taskP.taskFrFunc(taskPending.TP_mlme_poll_request).equals("recvAck")))
				mlme_poll_request(taskP.mlme_poll_request_CoordAddrMode,taskP.mlme_poll_request_CoordPANId,taskP.mlme_poll_request_CoordAddress,taskP.mlme_poll_request_SecurityEnable,taskP.mlme_poll_request_autoRequest,false,PHYenum.p_BUSY);		//status can anything but p_SUCCESS
			else	//default handling for <txBcnCmd2>
			{
				numBcnCmdRetry2++;
				if (numBcnCmdRetry2 <= Const.aMaxFrameRetries)
					waitBcnCmdAck2 = false;
				else
				{
					//freePkt(txBcnCmd2);
					txBcnCmd2 = null;
				}
				csmacaResume();		//other packets may be waiting
			}
		}
		else if (txPkt == txBcnCmd)
		{
			wph = txBcnCmd.HDR_LRWPAN();
			frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
			frmCtrl.parse();
			if ((frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)		//command packet
			 && (wph.MSDU_CmdType == 0x02))			//association response packet
			{
				//different from data packet, association response packet
				//should be retransmitted though it uses indirect transmission
				//(refer to page 67, line 28-32)
				numBcnCmdRetry++;
				if (numBcnCmdRetry <= Const.aMaxFrameRetries) {
					/* no need to check if the packet has been purged -- if purged, then taskFailed() should have set txBcnCmd = 0
					if (wph.indirect)				//indirect transmission
					{
						frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
						frmCtrl.parse();
						i = updateTransacLinkByPktOrHandle(Def.tr_oper_est,&transacLink1,&transacLink2,txBcnCmd);
						if (i != 0)	//already purged from pending list
						{
							MacMessage_802_15_4.free(txBcnCmd != null);
							txBcnCmd = null;
							return;
						}
					}
					*/
					//taskP.setTaskFrFunc(taskPending.TP_mlme_associate_response),"csmacaCallBack");
                                        taskP.setTaskFrFunc(taskPending.TP_mlme_associate_response,"csmacaCallBack");
					waitBcnCmdAck = false;
					csmacaResume();
				}
				else
					mlme_associate_response(taskP.mlme_associate_response_DeviceAddress,(int)0,MACenum.m_NO_ACK,false,false,PHYenum.p_BUSY);	//status returned in MACenum rather than in PHYenum
			}
			else if ((frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)		//command packet
			&& (wph.MSDU_CmdType == 0x08))					//coordinator realignment response packet
			{
				numBcnCmdRetry++;
				if (numBcnCmdRetry <= Const.aMaxFrameRetries)
				{
					//taskP.setTaskFrFunc(taskPending.TP_mlme_orphan_response),"csmacaCallBack");
                                        taskP.setTaskFrFunc(taskPending.TP_mlme_orphan_response,"csmacaCallBack");
					waitBcnCmdAck = false;
					csmacaResume();
				}
				else
					mlme_orphan_response(taskP.mlme_orphan_response_OrphanAddress,(int)0,true,false,false,PHYenum.p_BUSY);
			}
			else		//default handling for <txBcnCmd>
			{
				//freePkt(txBcnCmd );
				txBcnCmd = null;
				csmacaResume();		//other packets may be waiting
			}
		}
		//else		//may be purged from the pending list
		
	}
	else if (frFunc.equals("PLME_SET_TRX_STATE_confirm"))
	{
		//handle TRX_OFF
		if (req_state == PHYenum.p_TRX_OFF)
		if (taskP.taskStatus(taskPending.TP_mlme_reset_request)
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_reset_request).equals(frFunc)))
			mlme_reset_request(taskP.mlme_reset_request_SetDefaultPIB,false,status);
		//handle RX_ON
		if (req_state == PHYenum.p_RX_ON)
		if (taskP.taskStatus(taskPending.TP_mlme_scan_request)
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_scan_request).equals(frFunc)))
			mlme_scan_request(taskP.mlme_scan_request_ScanType,taskP.mlme_scan_request_ScanChannels,taskP.mlme_scan_request_ScanDuration,false,status);
		else if (taskP.taskStatus(taskPending.TP_mlme_rx_enable_request)
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_rx_enable_request).equals(frFunc)))
			mlme_rx_enable_request(false,taskP.mlme_rx_enable_request_RxOnTime,taskP.mlme_rx_enable_request_RxOnDuration,false, PHYenum.p_SUCCESS);
	}
	else if (frFunc.equals("PLME_SET_confirm"))
	{
		if (taskP.taskStatus(taskPending.TP_mlme_scan_request)
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_scan_request).equals(frFunc)))
			mlme_scan_request(taskP.mlme_scan_request_ScanType,taskP.mlme_scan_request_ScanChannels,taskP.mlme_scan_request_ScanDuration,false,status);
	}
	else if (frFunc.equals("PLME_ED_confirm"))
	{
		if (taskP.taskStatus(taskPending.TP_mlme_scan_request)
		&& (taskP.mlme_scan_request_ScanType == 0x00)		//ED scan
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_scan_request).equals(frFunc)))
			mlme_scan_request(taskP.mlme_scan_request_ScanType,taskP.mlme_scan_request_ScanChannels,taskP.mlme_scan_request_ScanDuration,false,status);
	}
	else if (frFunc.equals("recvBeacon"))
	{
		if (taskP.taskStatus(taskPending.TP_mlme_scan_request)
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_scan_request).equals(frFunc)))
			mlme_scan_request(taskP.mlme_scan_request_ScanType,taskP.mlme_scan_request_ScanChannels,taskP.mlme_scan_request_ScanDuration,false,PHYenum.p_SUCCESS);
		else if (taskP.taskStatus(taskPending.TP_mlme_rx_enable_request)
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_rx_enable_request).equals(frFunc)))
			mlme_rx_enable_request(false,taskP.mlme_rx_enable_request_RxOnTime,taskP.mlme_rx_enable_request_RxOnDuration,false, PHYenum.p_SUCCESS);
		else if (taskP.taskStatus(taskPending.TP_mlme_sync_request)
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_sync_request).equals(frFunc)))
			mlme_sync_request((byte)0,taskP.mlme_sync_request_tracking,false,PHYenum.p_SUCCESS);
	}
	else if (frFunc.equals("scanHandler"))	//always check the task status if the dispatch comes from a timer
	{
		if (taskP.taskStatus(taskPending.TP_mlme_scan_request))
			mlme_scan_request(taskP.mlme_scan_request_ScanType,taskP.mlme_scan_request_ScanChannels,taskP.mlme_scan_request_ScanDuration,false,PHYenum.p_BUSY);
	}
	else if (frFunc.equals("extractHandler"))	//always check the task status if the dispatch comes from a timer
	{
		if (taskP.taskStatus(taskPending.TP_mlme_associate_request)
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_associate_request).equals(frFunc)))
	 	{
			mlme_associate_request((byte)0,(byte)0,(int)0,(int)0,(byte)0,taskP.mlme_associate_request_SecurityEnable,false,PHYenum.p_BUSY, MACenum.m_SUCCESS);	//status ignored in case 4, but should set to any value but p_SUCCESS in case 7 -- PHYenum.p_BUSY will be ok anyway
		}
		else if (taskP.taskStatus(taskPending.TP_mlme_poll_request)
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_poll_request).equals("IFSHandler") ))
			mlme_poll_request(taskP.mlme_poll_request_CoordAddrMode,taskP.mlme_poll_request_CoordPANId,taskP.mlme_poll_request_CoordAddress,taskP.mlme_poll_request_SecurityEnable,taskP.mlme_poll_request_autoRequest,false,PHYenum.p_BUSY);
	}
	else if (frFunc.equals("assoRspWaitHandler"))	//always check the task status if the dispatch comes from a timer
	{
		if (taskP.taskStatus(taskPending.TP_mlme_associate_response))
		{
			//taskP.taskStep(taskPending.TP_mlme_associate_response) = 2;
                        taskP.setTaskStep(taskPending.TP_mlme_associate_response,(byte)2);
			mlme_associate_response(taskP.mlme_associate_response_DeviceAddress,(int)0,MACenum.m_SUCCESS,false,false,PHYenum.p_BUSY);	//status ignored
		}
	}
	else if (frFunc.equals("dataWaitHandler"))	//always check the task status if the dispatch comes from a timer
	{
		if (taskP.taskStatus(taskPending.TP_mcps_data_request))
		{
			//taskP.taskStep(taskPending.TP_mcps_data_request) = 2;
                        taskP.setTaskStep(taskPending.TP_mcps_data_request, (byte)2);
			mcps_data_request((byte)0,(int)0,(int)0,(byte)0,(int)0,(int)0,(byte)0,null,(byte)0,taskP.mcps_data_request_TxOptions,false,PHYenum.p_BUSY, MACenum.m_SUCCESS);	//status ignored
		}
	}
	else if (frFunc.equals("IFSHandler"))	//always check the task status if the dispatch comes from a timer
	{
		if (taskP.taskStatus(taskPending.TP_mlme_associate_request)
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_associate_request).equals(frFunc)))
			mlme_associate_request((byte)0,(byte)0,(int)0,(int)0,(byte)0,taskP.mlme_associate_request_SecurityEnable,false,PHYenum.p_SUCCESS,mStatus);
		else if (taskP.taskStatus(taskPending.TP_mlme_poll_request)
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_poll_request).equals(frFunc)))
			mlme_poll_request(taskP.mlme_poll_request_CoordAddrMode,taskP.mlme_poll_request_CoordPANId,taskP.mlme_poll_request_CoordAddress,taskP.mlme_poll_request_SecurityEnable,taskP.mlme_poll_request_autoRequest,false,PHYenum.p_SUCCESS);
		else if (taskP.taskStatus(taskPending.TP_mlme_scan_request)
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_scan_request).equals(frFunc)))
			mlme_scan_request(taskP.mlme_scan_request_ScanType,taskP.mlme_scan_request_ScanChannels,taskP.mlme_scan_request_ScanDuration,false,PHYenum.p_SUCCESS);
	}
	else if (frFunc.equals("rxEnableHandler"))
	{
		//if (taskP.taskStatus(taskPending.TP_mlme_rx_enable_request))	//we don't check the task status (it may be reset)
	 	if (taskP.taskFrFunc(taskPending.TP_mlme_rx_enable_request).equals(frFunc))
			mlme_rx_enable_request(false,taskP.mlme_rx_enable_request_RxOnTime,taskP.mlme_rx_enable_request_RxOnDuration,false, PHYenum.p_SUCCESS);
	}
	else if (frFunc.equals("beaconSearchHandler"))	//always check the task status if the dispatch comes from a timer
	{
		if (taskP.taskStatus(taskPending.TP_mlme_sync_request)
	 	&& (taskP.taskFrFunc(taskPending.TP_mlme_sync_request).equals("recvBeacon")))
			mlme_sync_request((byte)0,taskP.mlme_sync_request_tracking,false,PHYenum.p_BUSY);	//status can anything but p_SUCCESS
	}
    }
    
    private void sendDown(MacMessage_802_15_4 p/*,Handler h*/)
    {
        if (energyManagementUnit != null && energyManagementUnit.getBattery().getPercentageEnergyLevel() < 1)
	{
            PD_DATA_confirm(PHYenum.p_UNDEFINED);
            return;
	}
        
         if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            System.out.println("[MAC.sendDown()]");
         
        if ( NFAILLINK.updateNFailLink(Def.fl_oper_est,(localAddr.hashCode())) == 0)
	{
		if (txBeacon != null)
		{
			beaconWaiting = false;
			//MacMessage_802_15_4.free(txBeacon);
			txBeacon = null;
		}
		return;
	}
	else if ( LFAILLINK.updateLFailLink(Def.fl_oper_est,(localAddr.hashCode()),Trace.p802_15_4macDA(p)) == 0)
	{
		dispatch(PHYenum.p_UNDEFINED,"PD_DATA_confirm", PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
		return;
	}

	inTransmission = true;

	//double trx_time = phyEntity.trxTime(p,false);
	/* Linux floating number compatibility
	txOverT.start(trx_time + 1/phyEntity.getRate('s'));
	*/
	//{
	//double tmpf;
	//tmpf = 1/phyEntity.getRate('s');		
	//txOverT.start(trx_time + tmpf);
	//}
	//EnergyModel *em = netif_.node().energy_model();
	//if (em)
	//if (em.energy() <= 0)       

	//downtarget_.recv(p, h); 
        phyEntity.transmit(p);//
    }
    
    private void mcps_data_request(byte SrcAddrMode,int SrcPANId,/* IE3ADDR */ int SrcAddr,
			           byte DstAddrMode,int DstPANId,/* IE3ADDR */ int DstAddr,
			           int msduLength,MacMessage_802_15_4 msdu,byte msduHandle,byte TxOptions,
			           boolean frUpper /* = false */,PHYenum status /*= PHYenum.p_SUCCESS*/,MACenum mStatus /*= MACenum.m_SUCCESS */) { // ???   

    byte step,task;
	hdr_lrwpan wph;
	hdr_cmn ch;
	FrameCtrl frmCtrl = new FrameCtrl();
	double kpTime = 0.0;
	int i;
        
         if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            System.out.println("[MAC.mcps_data_request]");

	task = taskPending.TP_mcps_data_request;
	if (frUpper) checkTaskOverflow(task);	
        
	step = taskP.taskStep(task);
	if (step == 0) {
		//check if parameters valid or not
		ch = msdu.HDR_CMN();
		if (ch.ptype() == Packet_t.PT_MAC)	//we only check for 802.15.4 packets (let other packets go through -- must be changed in implementation)
		if ((SrcAddrMode > 0x03)
		||(DstAddrMode > 0x03)
		||(msduLength > Const.aMaxMACFrameSize)
		||(TxOptions > 0x0f))
		{
			sscs.MCPS_DATA_confirm(msduHandle,MACenum.m_INVALID_PARAMETER);
			return;
		}

		//taskP.setTaskStatus(task, true);
                taskP.setTaskStatus(task, true);
		taskP.mcps_data_request_TxOptions = TxOptions;
		//---construct a MPDU packet (not really a new packet in simulation, but still <msdu>)---
		frmCtrl.FrmCtrl = 0;
		frmCtrl.setFrmType(Const.defFrmCtrl_Type_Data);	//data type
		if ((TxOptions & TxOp_Acked) == 1)
			frmCtrl.setAckReq(true);
		if (SrcPANId == DstPANId)
			frmCtrl.setIntraPan(true);		//Intra PAN
		frmCtrl.setDstAddrMode(DstAddrMode);		//we reverse the bit order -- note to use the required order in implementation
		frmCtrl.setSrcAddrMode(SrcAddrMode);		//we reverse the bit order -- note to use the required order in implementation
		wph = msdu.HDR_LRWPAN();
		wph.MHR_DstAddrInfo.panID = DstPANId;
		wph.MHR_DstAddrInfo.addr_64 = DstAddr;		//it doesn't matter if this is actually a 16-bit address
		wph.MHR_SrcAddrInfo.panID = SrcPANId;
		wph.MHR_SrcAddrInfo.addr_64 = SrcAddr;		//it doesn't matter if this is actually a 16-bit address
		//ignore FCS in simulation
		constructMPDU(msduLength,msdu,frmCtrl.FrmCtrl,mpib.macDSN++,wph.MHR_DstAddrInfo,wph.MHR_SrcAddrInfo,0,(byte)0,0);
		//for trace
		Trace.p802_15_4hdrDATA(msdu);
		//---------------------------------------------------------------------------------------------------

		//perform security task if required (ignored in simulation)
	}

	if((TxOptions & TxOp_GTS) == 1)	{ //GTS transmission
		switch(step) {
			//other cases: TBD
			default:
				break;
		}
	}
	else if (((TxOptions & TxOp_Indirect) == 1)				//indirect transmission
             && (capability.FFD&&(DEVICELINK.numberDeviceLink(/* & */deviceLink1) > 0)))	//I am a coordinator
	{
		switch(step)
		{
			case 0:
				taskP.taskStepIncrement(task);
				taskP.mcps_data_request_pendPkt = msdu;
				if ((DstAddrMode == Const.defFrmCtrl_AddrMode16)		//16-bit address available
				|| (DstAddrMode == Const.defFrmCtrl_AddrMode64))		//64-bit address available
				{
					/* Linux floating number compatibility
					kpTime = mpib.macTransactionPersistenceTime * (Const.aBaseSuperframeDuration * (1 << mpib.macBeaconOrder) / phyEntity.getRate('s'));
					*/
					{
					double tmpf = 0.0;
					tmpf = (Const.aBaseSuperframeDuration * (1 << mpib.macBeaconOrder) / phyEntity.getRate_BitsPerSecond('s'));
					kpTime = mpib.macTransactionPersistenceTime * tmpf;		
					}

					TRANSACLINK.chkAddTransacLink(/* & */transacLink1,/* & */transacLink2,DstAddrMode,DstAddr,msdu,msduHandle,kpTime);
				}
				taskP.taskFrFunc(task).equals("csmacaCallBack");
				dataWaitT.startTimer(kpTime);		
				break;
			case 1:
				if (!taskP.taskStatus(task))	
					break;
				if (status == PHYenum.p_SUCCESS)	//data packet transmitted and, if required, ack. received 
				{
					dataWaitT.stopTimerr();
					taskP.setTaskStatus(task, false);
					resetTRX();
					taskSuccess(CHAR.d/*'d'*/, true);
				}
				else				//data packet transmission failed
				{
					//leave the packet in the queue waiting for next polling
					taskP.taskFrFunc(task).equals("csmacaCallBack");	//wait for next polling
					//status return in MACenum, either CHANNEL_ACCESS_FAILURE or NO_ACK
					//taskP.setTaskStatus(task, false);	
					resetTRX();
                                        //taskFailed('d',mStatus);
					taskFailed(CHAR.d/*'d'*/,mStatus, true);
				}
				break;
			case 2:
				if (!taskP.taskStatus(task))		
					break;
				taskP.setTaskStatus(task, false);
				//check if the transaction still pending -- actually no need to check (it must be pending if case 1 didn't happen), but no harm
				i = TRANSACLINK.updateTransacLinkByPktOrHandle(Def.tr_oper_est,/* & */transacLink1,/* & */transacLink2,taskP.mcps_data_request_pendPkt, (byte)0);	//don't use <txData>, since assignment 'txData = msdu' only happens if a data request command received
				if (i == 0)	{//still pending
					//get a copy of the packet for taskFailed()
					if (txData == null)
						txData = taskP.mcps_data_request_pendPkt.copy();
					//delete the packet from the transaction list immediately -- prevent the packet from being transmitted at the last moment
					TRANSACLINK.updateTransacLinkByPktOrHandle(Def.tr_oper_del,/* & */transacLink1,/* & */transacLink2,taskP.mcps_data_request_pendPkt, (byte)0);
					resetTRX();
					taskFailed(CHAR.d/*'d'*/,MACenum.m_TRANSACTION_EXPIRED, true);
					return;
				} else {	//being successfully extracted
					resetTRX();
					taskFailed(CHAR.d/*'d'*/,MACenum.m_SUCCESS, true);
					return;
				}
				//break;

			default:
				break;
		}
	}
	else {				//direct transmission
		switch(step) {
			case 0:
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task, "csmacaCallBack");
				assert(txData == null);
				txData = msdu;
				csmacaBegin(CHAR.d/*'d'*/);
				break;
			case 1:
				if (status == PHYenum.p_IDLE) {
					taskP.taskStepIncrement(task);
					taskP.setTaskFrFunc(task, "PD_DATA_confirm");
					//enable the transmitter
					plme_set_trx_state_request(PHYenum.p_TX_ON);
				} else {
					wph = txData.HDR_LRWPAN();
					ch = txData.HDR_CMN();
					if (wph.msduHandle == 1) { //from SSCS
						//let the upper layer handle the failure (no retry)
						taskP.setTaskStatus(task, false);
						resetTRX();
						taskFailed(CHAR.d/*'d'*/,MACenum.m_CHANNEL_ACCESS_FAILURE, true);
					} else
						csmacaResume();
				}
				break;
			case 2:
				wph = txData.HDR_LRWPAN();
				ch = txData.HDR_CMN();
				frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
				frmCtrl.parse();
				if (frmCtrl.ackReq)	//ack. required
				{
					taskP.taskStepIncrement(task);
					taskP.setTaskFrFunc(task,"recvAck");
					//enable the receiver
					plme_set_trx_state_request(PHYenum.p_RX_ON);
					txT.startTimer(mpib.macAckWaitDuration/phyEntity.getRate_BitsPerSecond('s'));
					waitDataAck = true;
				} else {		//assume success if ack. not required
					taskP.setTaskStatus(task, false);
					resetTRX();
					taskSuccess(CHAR.d/*'d'*/, true);
				}
				break;
			case 3:
				if (status == PHYenum.p_SUCCESS) {	//ack. received
					taskP.setTaskStatus(task, false);
					resetTRX();
					taskSuccess(CHAR.d/*'d'*/, true);
				} else {				//time out when waiting for ack.
					numDataRetry++;
					if (numDataRetry <= Const.aMaxFrameRetries) {
						if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
							traceACKLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.mcps_data_request()] - timeout waiting for ACK. Retry " + numDataRetry + "/" + Const.aMaxFrameRetries);						
                        taskP.setTaskStep(task, (byte)1);	//important 
						taskP.setTaskFrFunc(task,"csmacaCallBack");
						waitDataAck = false;
						csmacaResume();
					} else {
						if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
							traceACKLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.mcps_data_request()] - timeout waiting for ACK. Number of retries maxed out");
						resetTRX();
						taskFailed(CHAR.d/*'d'*/,MACenum.m_NO_ACK, true);
					}
				}
				break;
			default:
				break;
		}
	}
    }
    
    private void mlme_associate_request(byte LogicalChannel,byte CoordAddrMode,int CoordPANId, /* IE3ADDR */ int CoordAddress,
				    byte CapabilityInformation,boolean SecurityEnable,
				    boolean frUpper /*= false*/,PHYenum status /*= PHYenum.p_SUCCESS*/,MACenum mStatus /*= MACenum.m_SUCCESS*/) // ???
    {
        	//refer to Figure 25 for association details
	byte step,task;
	FrameCtrl frmCtrl = new FrameCtrl();
	hdr_lrwpan wph;

	task = taskPending.TP_mlme_associate_request;
	if (frUpper) checkTaskOverflow(task);

	step = taskP.taskStep(task);
	switch(step)
	{
		case 0:
			//check if parameters valid or not
			if ((!phyEntity.channelSupported(LogicalChannel))
			|| ((CoordAddrMode != Const.defFrmCtrl_AddrMode16)&&(CoordAddrMode != Const.defFrmCtrl_AddrMode64)))
			{
				sscs.MLME_ASSOCIATE_confirm(0,MACenum.m_INVALID_PARAMETER);
				return;
			}

			//assert(mpib.macShortAddress == 0xffff);		//not associated yet

			//we may optionally track beacons if beacon enabled (here we don't)

			tmp_ppib.phyCurrentChannel = LogicalChannel;
			phyEntity.PLME_SET_request(PPIBAenum.phyCurrentChannel,/* & */tmp_ppib);
			mpib.macPANId = CoordPANId;
			mpib.macCoordExtendedAddress = CoordAddress;
			taskP.setTaskStatus(task, true);
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task,"csmacaCallBack");
			taskP.mlme_associate_request_CoordAddrMode = CoordAddrMode;
			taskP.mlme_associate_request_SecurityEnable = SecurityEnable;
			//--- send an association request command ---
                        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                            System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") before alloc txBcnCmd2:\n\t\ttxBeacon\t= " + txBeacon + " \n\t\ttxAck   \t= " + txAck + " \n\t\ttxBcnCmd\t= " + txBcnCmd + " \n\t\ttxBcnCmd2\t= " + txBcnCmd2 + " \n\t\ttxData  \t= " + txData);
			assert(txBcnCmd2 == null);
			//txBcnCmd2 = MacMessage_802_15_4.alloc();
			assert(txBcnCmd2 != null);
			wph = txBcnCmd2.HDR_LRWPAN();
			constructCommandHeader(txBcnCmd2,/* & */frmCtrl,(byte)0x01,CoordAddrMode,CoordPANId,CoordAddress,Const.defFrmCtrl_AddrMode64,(int)0xffff,aExtendedAddress,SecurityEnable,false,true);
			wph.MSDU_Payload[0] = capability.cap;
			constructMPDU((byte)2,txBcnCmd2,frmCtrl.FrmCtrl,mpib.macDSN++,wph.MHR_DstAddrInfo,wph.MHR_SrcAddrInfo,0,(byte)0x01,0);
			csmacaBegin(CHAR.C/*'C'*/);
			//------------------------------------
			break;
		case 1:
			if (status == PHYenum.p_IDLE)
			{
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task,"PD_DATA_confirm");
				if (Mac802_15_4Impl.verbose)
					System.out.println("[" + JistAPI.getTime()+ "]<MAC>(node " + (localAddr.hashCode() + ") sending association request command ..."));
				plme_set_trx_state_request(PHYenum.p_TX_ON);
				break;
			}
			else
			{
				taskP.setTaskStatus(task, false);
				//freePkt(txBcnCmd2);
				txBcnCmd2 = null;
				//restore default values
				mpib.macPANId = Const.def_macPANId;
				mpib.macCoordExtendedAddress = Const.def_macCoordExtendedAddress;
				sscs.MLME_ASSOCIATE_confirm(0,MACenum.m_CHANNEL_ACCESS_FAILURE);
				csmacaResume();
				return;
			}
			//break;
		case 2:
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task,"recvAck");
			plme_set_trx_state_request(PHYenum.p_RX_ON);	//waiting for ack.
			txT.startTimer(mpib.macAckWaitDuration/phyEntity.getRate_BitsPerSecond('s'));
			waitBcnCmdAck2 = true;
			break;
		case 3:
			if (status == PHYenum.p_SUCCESS)	//ack. received
			{
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task,"extractHandler");
				plme_set_trx_state_request(PHYenum.p_TRX_OFF);		//we don't want to receive any packet at this moment
				if (Mac802_15_4Impl.verbose)
					System.out.println("[" + JistAPI.getTime()+ "]<MAC>(node " + (localAddr.hashCode()) + ") ack for association request command received");
				taskSuccess(CHAR.C/*'C'*/,false);
				extractT.start(Const.aResponseWaitTime/phyEntity.getRate_BitsPerSecond('s'),false);
			}
			else				//time out when waiting for ack.
			{
				numBcnCmdRetry2++;
				if (numBcnCmdRetry2 <= Const.aMaxFrameRetries)
				{
					taskP.setTaskStep(task, (byte)1);	//important
					taskP.setTaskFrFunc(task, "csmacaCallBack");
					waitBcnCmdAck2 = false;
					csmacaResume();
				}
				else
				{
					taskP.setTaskStatus(task, false);
					resetTRX();
					//freePkt(txBcnCmd2);
					txBcnCmd2 = null;
					//restore default values
					mpib.macPANId = Const.def_macPANId;
					mpib.macCoordExtendedAddress = Const.def_macCoordExtendedAddress;
					sscs.MLME_ASSOCIATE_confirm(0,MACenum.m_NO_ACK);
					csmacaResume();
					return;
				}
			}
			break;
		case 4:
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task, "PD_DATA_confirm");
			//-- send a data request command to extract the response ---
                        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                            System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") before alloc txBcnCmd2:\n\t\ttxBeacon\t= " + txBeacon + " \n\t\ttxAck   \t= " + txAck + " \n\t\ttxBcnCmd\t= " + txBcnCmd + " \n\t\ttxBcnCmd2\t= " + txBcnCmd2 + " \n\t\ttxData  \t= " + txData);
			assert(txBcnCmd2 == null);
			//txBcnCmd2 = MacMessage_802_15_4.alloc();
			assert(txBcnCmd2 != null);
			wph = txBcnCmd2.HDR_LRWPAN();
			if ((mpib.macShortAddress == 0xfffe)||(mpib.macShortAddress == 0xffff))
			{
				frmCtrl.setSrcAddrMode(Const.defFrmCtrl_AddrMode64);
				wph.MHR_SrcAddrInfo.addr_64 = aExtendedAddress;
			}
			else
			{
				frmCtrl.setSrcAddrMode(Const.defFrmCtrl_AddrMode16);
				wph.MHR_SrcAddrInfo.addr_16 = mpib.macShortAddress;
			}
			constructCommandHeader(txBcnCmd2,/* & */frmCtrl,(byte)0x04,taskP.mlme_associate_request_CoordAddrMode,mpib.macPANId,mpib.macCoordExtendedAddress,frmCtrl.srcAddrMode,mpib.macPANId,wph.MHR_SrcAddrInfo.addr_64,SecurityEnable,false,true);
			constructMPDU((byte)1,txBcnCmd2,frmCtrl.FrmCtrl,mpib.macDSN++,wph.MHR_DstAddrInfo,wph.MHR_SrcAddrInfo,0,(byte)0x04,0);
			waitBcnCmdAck2 = false;		//command packet not yet transmitted
			numBcnCmdRetry2 = 0;
			if (Mac802_15_4Impl.verbose)
				System.out.println("[" + JistAPI.getTime()+ "]<MAC>(node " + (localAddr.hashCode()) + ") sending data request command ...");
			txCsmaca = txBcnCmd2;
			plme_set_trx_state_request(PHYenum.p_TX_ON);
			//------------------------------------
			break;
		case 5:
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task, "recvAck");
			//enable the receiver
			plme_set_trx_state_request(PHYenum.p_RX_ON);
			txT.startTimer(mpib.macAckWaitDuration/phyEntity.getRate_BitsPerSecond('s'));
			waitBcnCmdAck2 = true;
			break;
		case 6:
			if (status == PHYenum.p_SUCCESS)	//ack. received
			{
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task, "IFSHandler");
				plme_set_trx_state_request(PHYenum.p_RX_ON);		//wait for response
				if (Mac802_15_4Impl.verbose)
					System.out.println("[" + JistAPI.getTime()+ "]<MAC>(node " + (localAddr.hashCode()) + ") ack for data request command received");
				taskSuccess(CHAR.C/*'C'*/,false);
				extractT.start(Const.aResponseWaitTime/phyEntity.getRate_BitsPerSecond('s'),false);	//compare: for normal data, wait for <aMaxFrameResponseTime> symbols (or CAP symbols if beacon enabled) (see page 156, line 1-3)
			}
			else				//time out when waiting for ack.
			{
				//No retransmission required in general (just wait for next beacon and poll again, see page 156, line 20-24),
				//but we need to retransmit here, since the node will not handle the pending list before it has associated
				//with the coordinator.
				numBcnCmdRetry2++;
				if (numBcnCmdRetry2 <= Const.aMaxFrameRetries)
				{
					taskP.setTaskStep(task, (byte)5);	//important
					taskP.setTaskFrFunc(task, "PD_DATA_confirm");
					waitBcnCmdAck2 = false;
					txCsmaca = txBcnCmd2;
					plme_set_trx_state_request(PHYenum.p_TX_ON);
				}
				else
				{
					taskP.setTaskStatus(task, false);
					resetTRX();
					//freePkt(txBcnCmd2);
					txBcnCmd2 = null;
					//restore default values
					mpib.macPANId = Const.def_macPANId;
					mpib.macCoordExtendedAddress = Const.def_macCoordExtendedAddress;
					sscs.MLME_ASSOCIATE_confirm(0,MACenum.m_NO_DATA);	//assume no DATA
					csmacaResume();
					return;
				}
			}
			break;
		case 7:
			taskP.setTaskStatus(task, false);
			resetTRX();
			if (status == PHYenum.p_SUCCESS)		//response received
			{
				if (mStatus == MACenum.m_SUCCESS)
				{
					//changeNodeColor(((double)JistAPI.getTime()/Constants.SECOND),(mpib.macAssociationPermit)?Nam802_15_4.def_Coor_clr:Nam802_15_4.def_Dev_clr); ??? nam
					//char[] label = new label[31]; // nam
					//sprintf(label,"[%d]",mpib.macCoordExtendedAddress); // nam
                                        //if (ZigBeeIF) // ??? ZigBee
                                            //if (sscs.t_isCT) 
						//sprintf(label,"\"%s%d (%d.%d)\"",(sscs.RNType())?"+":"-",rt_myNodeID,sscs.rt_myParentNodeID,sscs.rt_myDepth);

					//nam.changeLabel(((double)JistAPI.getTime()/Constants.SECOND),label); // ??? nam
				}
				else
				{
					//restore default values
					mpib.macPANId = Const.def_macPANId;
					mpib.macCoordExtendedAddress = Const.def_macCoordExtendedAddress;
				}
				//stop the timer
				if (Mac802_15_4Impl.verbose)
					System.out.println("[" + JistAPI.getTime()+ "]<MAC>(node " + (localAddr.hashCode()) + ") association response command received");
				extractT.stopTimerr();
				sscs.MLME_ASSOCIATE_confirm(rt_myNodeID,mStatus);
			}
			else					//time out when waiting for response
			{
				//restore default values
				mpib.macPANId = Const.def_macPANId;
				mpib.macCoordExtendedAddress = Const.def_macCoordExtendedAddress;
				sscs.MLME_ASSOCIATE_confirm(0,MACenum.m_NO_DATA);
			}
			csmacaResume();
		default:
			break;
	}
    }
    
    private void mlme_associate_response(/* IE3ADDR */ int DeviceAddress, int AssocShortAddress,MACenum Status,boolean SecurityEnable,
				     boolean frUpper /*= false*/,PHYenum status /*= PHYenum.p_SUCCESS*/) // ???
    {
        FrameCtrl frmCtrl = new FrameCtrl();
	hdr_lrwpan wph;
	MacMessage_802_15_4 rspPkt = new MacMessage_802_15_4();
	double kpTime;
	byte step,task;
	int i;
	task = taskPending.TP_mlme_associate_response;
	//checkTaskOverflow(task);	
	if (frUpper)
	{
		if (taskP.taskStatus(task))	//overflow
		{
			sscs.MLME_COMM_STATUS_indication(mpib.macPANId,Const.defFrmCtrl_AddrMode64,aExtendedAddress,Const.defFrmCtrl_AddrMode64,DeviceAddress,MACenum.m_TRANSACTION_OVERFLOW);
			return;
		}
		taskP.setTaskStep(task, (byte)0);
		//(taskP.taskFrFunc(task))[0] = 0;
                String tmp = taskP.taskFrFunc(task);
                char[] ctmp = tmp.toCharArray();
                ctmp[0] = 0;
                taskP.setTaskFrFunc(task, ctmp.toString());
                
	}
	step = taskP.taskStep(task);
	switch(step)
	{
		case 0:
			//check if parameters valid or not
			if ((Status != MACenum.m_SUCCESS)&&(Status != MACenum.m_PAN_at_capacity)&&(Status != MACenum.m_PAN_access_denied))
			{
				sscs.MLME_COMM_STATUS_indication(mpib.macPANId,Const.defFrmCtrl_AddrMode64,aExtendedAddress,Const.defFrmCtrl_AddrMode64,DeviceAddress,MACenum.m_INVALID_PARAMETER);
				return;
			}
			taskP.setTaskStatus(task, true);
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task, "csmacaCallBack");
			taskP.mlme_associate_response_DeviceAddress = DeviceAddress;
			//--- construct an association response command packet and put it in the pending list ---
			//rspPkt = MacMessage_802_15_4.alloc();
			assert(rspPkt != null);
			wph = rspPkt.HDR_LRWPAN();

                        //if (ZigBeeIF)
                            //sscs.setGetClusTreePara('s',rspPkt);

                        constructCommandHeader(rspPkt,/* & */frmCtrl,(byte)0x02,Const.defFrmCtrl_AddrMode64,mpib.macPANId,DeviceAddress,Const.defFrmCtrl_AddrMode64,mpib.macPANId,aExtendedAddress,SecurityEnable,false,true);
			///* * */((int /* * */)wph.MSDU_Payload) = AssocShortAddress; ??? 
			///* * */((MACenum /* * */)(wph.MSDU_Payload + 2)) = Status; ???
                        MSDU_Util.storeAt(wph.MSDU_Payload, 0, new Integer(AssocShortAddress));
                        MSDU_Util.storeAt(wph.MSDU_Payload, 2, Status);
			constructMPDU((byte)4,rspPkt,frmCtrl.FrmCtrl,mpib.macDSN++,wph.MHR_DstAddrInfo,wph.MHR_SrcAddrInfo,(int)0,(byte)0x02,(int)0);
			kpTime = (2 * Const.aResponseWaitTime) / phyEntity.getRate_BitsPerSecond('s');
			i = TRANSACLINK.chkAddTransacLink(/* & */transacLink1,/* & */transacLink2,Const.defFrmCtrl_AddrMode64,DeviceAddress,rspPkt,(byte)0,kpTime);
			if (i != 0)	//overflow or failed
			{
                            if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                            {
                                hdr_cmn ch = rspPkt.HDR_CMN();
                                System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") task overflow or failed: type = " + Trace.wpan_pName(rspPkt) + ", src = " + Trace.p802_15_4macSA(rspPkt) + ", dst = " + Trace.p802_15_4macDA(rspPkt) + ", uid = " + wph.uid + ", mac_uid = ??, size = " + ch.size());
                            }
                            taskP.setTaskStatus(task, false);
                            //MacMessage_802_15_4.free(rspPkt);
                            sscs.MLME_COMM_STATUS_indication(mpib.macPANId,Const.defFrmCtrl_AddrMode64,aExtendedAddress,Const.defFrmCtrl_AddrMode64,DeviceAddress,MACenum.m_TRANSACTION_OVERFLOW);
                            return;
			}
			//----------------------------------------------------------------------
			assoRspWaitT.startTimer(kpTime);
			taskP.mlme_associate_response_pendPkt = rspPkt;
			break;
		case 1:
			if (!taskP.taskStatus(task))	
				break;
			taskP.setTaskStatus(task, false);
			assoRspWaitT.stopTimerr();
			if (status == PHYenum.p_SUCCESS)	//response packet transmitted and ack. received
			{
				sscs.MLME_COMM_STATUS_indication(mpib.macPANId,Const.defFrmCtrl_AddrMode64,aExtendedAddress,Const.defFrmCtrl_AddrMode64,DeviceAddress,MACenum.m_SUCCESS);
				taskSuccess(CHAR.c/*'c'*/, true);
			}
			else				//response packet transmission failed
			{
				//be careful, we use MACenum to return the status, either CHANNEL_ACCESS_FAILURE or NO_ACK
				sscs.MLME_COMM_STATUS_indication(mpib.macPANId,Const.defFrmCtrl_AddrMode64,aExtendedAddress,Const.defFrmCtrl_AddrMode64,DeviceAddress,Status);
				//freePkt(txBcnCmd);
				txBcnCmd = null;
			}
			break;
		case 2:
			if (!taskP.taskStatus(task))	
				break;
			taskP.setTaskStatus(task, false);
			//check if the transaction still pending -- actually no need to check (it must be pending if case 1 didn't happen), but no harm
			i = TRANSACLINK.updateTransacLinkByPktOrHandle(Def.tr_oper_est,/* & */transacLink1,/* & */transacLink2,taskP.mlme_associate_response_pendPkt, (byte)0);	//don't use <txBcnCmd>, since assignment 'txBcnCmd = rspPkt' only happens if a data request command received
			if (i == 0)	//still pending
			{
				//delete the packet from the transaction list immediately -- prevent the packet from being transmitted at the last moment
				TRANSACLINK.updateTransacLinkByPktOrHandle(Def.tr_oper_del,/* & */transacLink1,/* & */transacLink2,taskP.mlme_associate_response_pendPkt, (byte)0);
				sscs.MLME_COMM_STATUS_indication(mpib.macPANId,Const.defFrmCtrl_AddrMode64,aExtendedAddress,Const.defFrmCtrl_AddrMode64,DeviceAddress,MACenum.m_TRANSACTION_EXPIRED);
				return;
			}
			else	//being successfully extracted
			{
				sscs.MLME_COMM_STATUS_indication(mpib.macPANId,Const.defFrmCtrl_AddrMode64,aExtendedAddress,Const.defFrmCtrl_AddrMode64,DeviceAddress,MACenum.m_SUCCESS);
				return;
			}
			//break;
		default:
			break;
	}
    }
    
    private void mlme_disassociate_request(IE3ADDR DeviceAddress,byte DisassociateReason,boolean SecurityEnable,boolean frUpper /*= false*/,PHYenum status /*= PHYenum.p_SUCCESS*/) // ???
    {
        	/*
	FrameCtrl frmCtrl = new FrameCtrl();
	hdr_lrwpan* wph;
	double kpTime;
	byte step,task;
	int i;

	task = taskPending.TP_mlme_disassociate_request;
	if (frUpper) checkTaskOverflow(task);

	step = taskP.taskStep(task);
	switch(step)
	{
		case 0:
			//check if parameters valid or not
			if (DeviceAddress != mpib.macCoordExtendedAddress)		//send to a device
			if ((!capability.FFD)||(DEVICELINK.numberDeviceLink(&deviceLink1) == 0))	//I am not a coordinator

			{
				sscs.MLME_DISASSOCIATE_confirm(MACenum.m_INVALID_PARAMETER);
				return;
			}
			taskP.mlme_disassociate_request_toCoor = (DeviceAddress == mpib.macCoordExtendedAddress);
			//--- construct a disassociation notification command packet ---
#ifdef DEBUG802_15_4
			fprintf(stdout,"[%s.%s][%f](node %d) before alloc txBcnCmd2:\n\t\ttxBeacon\t= %ld\n\t\ttxAck   \t= %ld\n\t\ttxBcnCmd\t= %ld\n\t\ttxBcnCmd2\t= %ld\n\t\ttxData  \t= %ld\n",__FILE__,__FUNCTION__,((double)JistAPI.getTime()/Constants.SECOND),((int)localAddr.hashCode()),txBeacon,txAck,txBcnCmd,txBcnCmd2,txData);
#endif
			assert(txBcnCmd2 == null);
			txBcnCmd2 = MacMessage_802_15_4.alloc();
			assert(txBcnCmd2 != null);
			wph = txBcnCmd2.HDR_LRWPAN();
			if (!taskP.mlme_disassociate_request_toCoor)
				wph.MHR_DstAddrInfo.addr_64 = DeviceAddress;
			else
				wph.MHR_DstAddrInfo.addr_64 = mpib.macCoordExtendedAddress;
			constructCommandHeader(txBcnCmd2,&frmCtrl,0x03,Const.defFrmCtrl_AddrMode64,mpib.macPANId,wph.MHR_DstAddrInfo.addr_64,Const.defFrmCtrl_AddrMode64,mpib.macPANId,aExtendedAddress,SecurityEnable,false,true);
			*((byte *)wph.MSDU_Payload) = (taskP.mlme_disassociate_request_toCoor)?0x02:0x01;
			constructMPDU(2,txBcnCmd2,frmCtrl.FrmCtrl,mpib.macDSN++,wph.MHR_DstAddrInfo,wph.MHR_SrcAddrInfo,0,0x03,0);
			//----------------------------------------------------------------------
			taskP.setTaskStatus(task, true);
			taskP.taskStepIncrement(task);
			taskP.mlme_disassociate_request_pendPkt = txBcnCmd2;
			if (!taskP.mlme_disassociate_request_toCoor)		//indirect transmission should be used
			{
				// Linux floating number compatibility
				//kpTime = mpib.macTransactionPersistenceTime * (Const.aBaseSuperframeDuration * (1 << mpib.macBeaconOrder) / phyEntity.getRate('s'));
				{
				double tmpf;
				tmpf = (Const.aBaseSuperframeDuration * (1 << mpib.macBeaconOrder) / phyEntity.getRate('s'));
				kpTime = mpib.macTransactionPersistenceTime * tmpf;
				}

				i = chkAddTransacLink(&transacLink1,&transacLink2,Const.defFrmCtrl_AddrMode64,wph.MHR_DstAddrInfo.addr_64,txBcnCmd2,0,kpTime);
				if (i != 0)	//overflow or failed
				{
					taskP.setTaskStatus(task, false);
					MacMessage_802_15_4.free(txBcnCmd2 != null);
					txBcnCmd2 = null;
					sscs.MLME_DISASSOCIATE_confirm(MACenum.m_TRANSACTION_OVERFLOW);
					return;
				}
				extractT.start(kpTime,false);
			}
			else
				csmacaBegin('C');
			break;
		case 1:
			if (!taskP.mlme_disassociate_request_toCoor)		//indirect transmission
			{
				//check if the transaction still pending
				wph = txBcnCmd2.HDR_LRWPAN();
				i = updateTransacLinkByPktOrHandle(Def.tr_oper_est,&transacLink1,&transacLink2,taskP.mlme_disassociate_request_pendPkt);	//don't use <txBcnCmd2>, since it may be null if a data request command not received
				if (i == 0)	//still pending
				{
					//delete the packet from the transaction list immediately -- prevent the packet from being transmitted at the last moment
					updateTransacLinkByPktOrHandle(Def.tr_oper_del,&transacLink1,&transacLink2,taskP.mlme_disassociate_request_pendPkt);
					taskP.setTaskStatus(task, false);
					sscs.MLME_DISASSOCIATE_confirm(MACenum.m_TRANSACTION_EXPIRED);
					return;
				}
				else	//being successfully extracted
				{
					taskP.setTaskStatus(task, false);
					sscs.MLME_COMM_STATUS_indication(mpib.macPANId,Const.defFrmCtrl_AddrMode64,aExtendedAddress,Const.defFrmCtrl_AddrMode64,DeviceAddress,MACenum.m_SUCCESS);
					return;
				}
			}
			else
			{
			}
			break;
		default:
			break;
	}
	*/
    }
    
    
    private void mlme_orphan_response(/* IE3ADDR */ int OrphanAddress,int ShortAddress,boolean AssociatedMember,boolean SecurityEnable,boolean frUpper /*= false */,PHYenum status /*= PHYenum.p_SUCCESS*/) // ???
    {
        hdr_lrwpan wph;
	FrameCtrl frmCtrl = new FrameCtrl();
	byte step,task;

	task = taskPending.TP_mlme_orphan_response;
	if (frUpper) checkTaskOverflow(task);

	switch(taskP.taskStep(task))
	{
		case 0:
			if (AssociatedMember)
			{
				//send a coordinator realignment command
                                if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                      System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") before alloc txBcnCmd:\n\t\ttxBeacon\t= " + txBeacon + " \n\t\ttxAck   \t= " + txAck + " \n\t\ttxBcnCmd\t= " + txBcnCmd + " \n\t\ttxBcnCmd2\t= " + txBcnCmd2 + " \n\t\ttxData  \t= " + txData);
                                
				taskP.setTaskStatus(task, true);
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task, "csmacaCallBack");
				taskP.mlme_orphan_response_OrphanAddress = OrphanAddress;
				assert(txBcnCmd == null);
				//txBcnCmd = MacMessage_802_15_4.alloc();
				assert(txBcnCmd != null);
				wph = txBcnCmd.HDR_LRWPAN();
				constructCommandHeader(txBcnCmd,/* & */frmCtrl,(byte)0x08,Const.defFrmCtrl_AddrMode64,(int)0xffff,OrphanAddress,Const.defFrmCtrl_AddrMode64,mpib.macPANId,aExtendedAddress,SecurityEnable,false,true);
				///* * */((int /* * */)wph.MSDU_Payload) = mpib.macPANId;
				///* * */((int /* * */)wph.MSDU_Payload + 2) = mpib.macShortAddress;
                                MSDU_Util.storeAt(wph.MSDU_Payload, 0, new Integer(mpib.macPANId));
                                MSDU_Util.storeAt(wph.MSDU_Payload, 2, new Integer(mpib.macShortAddress));
				phyEntity.PLME_GET_request(PPIBAenum.phyCurrentChannel);
				///* * */((byte /* * */)wph.MSDU_Payload + 4) = tmp_ppib.phyCurrentChannel;
				///* * */((int /* * */)wph.MSDU_Payload + 5) = ShortAddress;
                                MSDU_Util.storeAt(wph.MSDU_Payload, 4, new Byte(tmp_ppib.phyCurrentChannel));
                                MSDU_Util.storeAt(wph.MSDU_Payload, 5, new Integer(ShortAddress));
				constructMPDU((byte)8,txBcnCmd,frmCtrl.FrmCtrl,mpib.macDSN++,wph.MHR_DstAddrInfo,wph.MHR_SrcAddrInfo,(int)0,(byte)0x08,(int)0);
				csmacaBegin(CHAR.c/*'c'*/);
			}
			break;
		case 1:
			taskP.setTaskStatus(task, false);
			if (status == PHYenum.p_SUCCESS)	//response packet transmitted and ack. received
			{
				sscs.MLME_COMM_STATUS_indication(mpib.macPANId,Const.defFrmCtrl_AddrMode64,aExtendedAddress,Const.defFrmCtrl_AddrMode64,OrphanAddress,MACenum.m_SUCCESS);
				taskSuccess(CHAR.c/*'c'*/, true);
			}
			else				//response packet transmission failed
			{
				sscs.MLME_COMM_STATUS_indication(mpib.macPANId,Const.defFrmCtrl_AddrMode64,aExtendedAddress,Const.defFrmCtrl_AddrMode64,OrphanAddress,MACenum.m_CHANNEL_ACCESS_FAILURE);
				//freePkt(txBcnCmd);
				txBcnCmd = null;
				csmacaResume();		//other packets may be waiting
			}
			break;
		default:
			break;
	}
    }
    
    private void mlme_reset_request(boolean SetDefaultPIB,boolean frUpper /*= false*/,PHYenum status /*= PHYenum.p_SUCCESS*/) // ???
    {
        byte step,task;

	task = taskPending.TP_mlme_reset_request;
	if (frUpper) checkTaskOverflow(task);

	switch(taskP.taskStep(task))
	{
		case 0:
			taskP.setTaskStatus(task, true);
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task, "PLME_SET_TRX_STATE_confirm");
			taskP.mlme_reset_request_SetDefaultPIB = SetDefaultPIB;
			plme_set_trx_state_request(PHYenum.p_TRX_OFF);
			break;
		case 1:
			taskP.setTaskStatus(task, false);
			init(true);
			if (SetDefaultPIB)
                                //mpib = MPIB;
                                mpib.MPIB();
                        
			if (status == PHYenum.p_TRX_OFF)
				sscs.MLME_RESET_confirm(MACenum.m_SUCCESS);
			else
				sscs.MLME_RESET_confirm(MACenum.m_DISABLE_TRX_FAILURE);
			break;
		default:
			break;
	}
    }
    
    private void mlme_rx_enable_request(boolean DeferPermit,int RxOnTime,int RxOnDuration,boolean frUpper /* = false */,PHYenum status /*= PHYenum.p_SUCCESS*/) // ???
    {
        byte step,task;
	int t_CAP;
	double cutTime,tmpf;

	task = taskPending.TP_mlme_rx_enable_request;
	if (frUpper) checkTaskOverflow(task);

	step = taskP.taskStep(task);

	if (step == 0)
	if (RxOnDuration == 0)
	{
		sscs.MLME_RX_ENABLE_confirm(MACenum.m_SUCCESS);
		plme_set_trx_state_request(PHYenum.p_TRX_OFF);
		return;
	}
	
	if (macBeaconOrder2 != 15)		//beacon enabled
	{
		switch(step)
		{
			case 0:
				taskP.mlme_rx_enable_request_RxOnTime = RxOnTime;
				taskP.mlme_rx_enable_request_RxOnDuration = RxOnDuration;
				if (RxOnTime + RxOnDuration >= sfSpec2.BI)
				{
					sscs.MLME_RX_ENABLE_confirm(MACenum.m_INVALID_PARAMETER);
					return;
				}
				t_CAP = (sfSpec2.FinCAP + 1) * sfSpec2.sd;

				/* Linux floating number compatibility
				*/
				tmpf = ((double)JistAPI.getTime()/Constants.SECOND) * phyEntity.getRate_BitsPerSecond('s');

				if ((RxOnTime - Const.aTurnaroundTime) > t_CAP)
				{
					sscs.MLME_RX_ENABLE_confirm(MACenum.m_OUT_OF_CAP);
					return;
				}
				/* Linux floating number compatibility
				else if ((((double)JistAPI.getTime()/Constants.SECOND) * phyEntity.getRate('s') - macBcnRxTime) < (RxOnTime - aTurnaroundTime))
				*/
				else if ((tmpf - macBcnRxTime) < (RxOnTime - Const.aTurnaroundTime))
				{
					//can proceed in current superframe
					taskP.setTaskStatus(task, true);
					taskP.taskStepIncrement(task);
					//just fall through case 1
				}
				else if (DeferPermit)
				{
					//need to defer until next superframe
					taskP.setTaskStatus(task, true);
					taskP.taskStepIncrement(task);
					taskP.setTaskFrFunc(task, "recvBeacon");
					break;
				}
				else
				{
					sscs.MLME_RX_ENABLE_confirm(MACenum.m_OUT_OF_CAP);
					return;
				}
			case 1:
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task, "rxEnableHandler");
				/* Linux floating number compatibility
				rxEnableT.start(RxOnTime / phyEntity.getRate('s') - (((double)JistAPI.getTime()/Constants.SECOND) - macBcnRxTime / phyEntity.getRate('s')));
				*/
				{
				double tmpf2;
				tmpf = macBcnRxTime / phyEntity.getRate_BitsPerSecond('s');
				tmpf = ((double)JistAPI.getTime()/Constants.SECOND) - tmpf;
				tmpf2 = RxOnTime / phyEntity.getRate_BitsPerSecond('s');
				tmpf = tmpf2 - tmpf;
				rxEnableT.startTimer(tmpf);
				}
				break;
			case 2:
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task, "PLME_SET_TRX_STATE_confirm");
				taskP.mlme_rx_enable_request_currentTime = ((double)JistAPI.getTime()/Constants.SECOND);
				plme_set_trx_state_request(PHYenum.p_RX_ON);
				break;
			case 3:
				taskP.setTaskStatus(task, false);	
				taskP.setTaskFrFunc(task, "rxEnableHandler");
				taskP.taskStepIncrement(task);
				if (status == PHYenum.p_TX_ON)
					sscs.MLME_RX_ENABLE_confirm(MACenum.m_TX_ACTIVE);
				else
					sscs.MLME_RX_ENABLE_confirm(MACenum.m_SUCCESS);
				//turn off the receiver before the CFP so as not to disturb it, and we see no reason to turn it on again after the CFP (i.e., inactive port of the superframe)
				t_CAP = (sfSpec2.FinCAP + 1) * sfSpec2.sd;
				cutTime = (RxOnTime + RxOnDuration - t_CAP) / phyEntity.getRate_BitsPerSecond('s');

				/* Linux floating number compatibility
				rxEnableT.start(RxOnDuration / phyEntity.getRate('s') - (((double)JistAPI.getTime()/Constants.SECOND) - taskP.mlme_rx_enable_request_currentTime) - cutTime);
				*/
				{
				tmpf = RxOnDuration / phyEntity.getRate_BitsPerSecond('s');
				tmpf -= ((double)JistAPI.getTime()/Constants.SECOND);
				tmpf += taskP.mlme_rx_enable_request_currentTime;
				tmpf -= cutTime;
				rxEnableT.startTimer(tmpf);
				}
				break;
			case 4:
				taskP.setTaskFrFunc(task, "");
				plme_set_trx_state_request(PHYenum.p_TRX_OFF);
				break;
			default:
				break;
		}
	}
	else
	{
		switch(step)
		{
			case 0:
				taskP.setTaskStatus(task, true);
				taskP.setTaskFrFunc(task, "PLME_SET_TRX_STATE_confirm");
				taskP.taskStepIncrement(task);
				taskP.mlme_rx_enable_request_RxOnDuration = RxOnDuration;
				taskP.mlme_rx_enable_request_currentTime = ((double)JistAPI.getTime()/Constants.SECOND);
				plme_set_trx_state_request(PHYenum.p_RX_ON);
				break;
			case 1:
				taskP.setTaskStatus(task, false);	
				taskP.setTaskFrFunc(task, "rxEnableHandler");
				taskP.taskStepIncrement(task);
				if (status == PHYenum.p_TX_ON)
					sscs.MLME_RX_ENABLE_confirm(MACenum.m_TX_ACTIVE);
				else
					sscs.MLME_RX_ENABLE_confirm(MACenum.m_SUCCESS);
				/* Linux floating number compatibility
				rxEnableT.start(RxOnDuration / phyEntity.getRate('s') - (((double)JistAPI.getTime()/Constants.SECOND) - taskP.mlme_rx_enable_request_currentTime));
				*/
				{
				tmpf = RxOnDuration / phyEntity.getRate_BitsPerSecond('s');
				tmpf -= ((double)JistAPI.getTime()/Constants.SECOND);
				tmpf += taskP.mlme_rx_enable_request_currentTime;
				rxEnableT.startTimer(tmpf);
				}
				break;
			case 2:
				taskP.setTaskFrFunc(task, "");
				plme_set_trx_state_request(PHYenum.p_TRX_OFF);
				break;
			default:
				break;
		}
	}
    }
    
    private void mlme_scan_request(byte ScanType,int ScanChannels,byte ScanDuration,boolean frUpper /*= false*/,PHYenum status /*= PHYenum.p_SUCCESS*/)// ??? 
    {
        int t_chanPos;
	byte step,task;
	FrameCtrl frmCtrl = new FrameCtrl();
	hdr_lrwpan wph;
	int i;

	task = taskPending.TP_mlme_scan_request;
	if (frUpper) checkTaskOverflow(task);

	step = taskP.taskStep(task);

	if (step == 0)
	{
		if ((ScanType > 3)
		||((ScanType != 3)&&(ScanDuration > 14)))
		{
			sscs.MLME_SCAN_confirm(MACenum.m_INVALID_PARAMETER,ScanType,ScanChannels,(byte)0,null,null);
			return;
		}
		//disable the beacon
		taskP.mlme_scan_request_orig_macBeaconOrder = mpib.macBeaconOrder;
		taskP.mlme_scan_request_orig_macBeaconOrder2 = macBeaconOrder2;
		taskP.mlme_scan_request_orig_macBeaconOrder3 = macBeaconOrder3;
		mpib.macBeaconOrder = 15;
		macBeaconOrder2 = 15;
		macBeaconOrder3 = 15;
		//stop the CSMA-CA if it is running
		if (backoffStatus == 99)
		{
			backoffStatus = 0;
			csmaca.cancel();
		}
		taskP.mlme_scan_request_ScanType = ScanType;
	}

	if (ScanType == 0x00)		//ED scan
	switch (step)
	{
		case 0:
			phyEntity.PLME_GET_request(PPIBAenum.phyChannelsSupported);	//value will be returned in tmp_ppib
			taskP.mlme_scan_request_ScanChannels = ScanChannels;
			if ((taskP.mlme_scan_request_ScanChannels & tmp_ppib.phyChannelsSupported) == 0)
			{
				//restore the beacon order
				mpib.macBeaconOrder = taskP.mlme_scan_request_orig_macBeaconOrder;
				macBeaconOrder2 = taskP.mlme_scan_request_orig_macBeaconOrder2;
				macBeaconOrder3 = taskP.mlme_scan_request_orig_macBeaconOrder3;
				sscs.MLME_SCAN_confirm(MACenum.m_SUCCESS,ScanType,ScanChannels,(byte)0,null,null);	//SUCCESS or INVALID_PARAMETER?
				csmacaResume();
				return;
			}
			taskP.setTaskStatus(task, true);
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task, "PLME_SET_confirm");
			taskP.mlme_scan_request_CurrentChannel = 0;
			taskP.mlme_scan_request_ListNum = 0;
			t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
			while((t_chanPos & taskP.mlme_scan_request_ScanChannels) == 0
			||(t_chanPos & tmp_ppib.phyChannelsSupported) == 0)
			{
				taskP.mlme_scan_request_CurrentChannel++;
				t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
			}
			tmp_ppib.phyCurrentChannel = taskP.mlme_scan_request_CurrentChannel;
			phyEntity.PLME_SET_request(PPIBAenum.phyCurrentChannel,/* & */tmp_ppib);
			break;
		case 1:
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task, "PLME_SET_TRX_STATE_confirm");
			plme_set_trx_state_request(PHYenum.p_RX_ON);
			break;
		case 2:
			if (status == PHYenum.p_RX_ON)
			{
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task, "PLME_ED_confirm");
				phyEntity.PLME_ED_request();
				break;
			}
			//else	//fall through case 4
		case 3:
			if (step == 3)	//note that case 2 needs to fall through case 4 via here
			{
				if (status == PHYenum.p_SUCCESS)
				{
					t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
					taskP.mlme_scan_request_ScanChannels &= (t_chanPos^0xffffffff);
					taskP.mlme_scan_request_EnergyDetectList[taskP.mlme_scan_request_ListNum] = energyLevel;
					taskP.mlme_scan_request_ListNum++;
				}
			}
			//fall through
		case 4:
			if ((taskP.mlme_scan_request_ScanChannels & tmp_ppib.phyChannelsSupported) == 0)
			{
				//restore the beacon order
				mpib.macBeaconOrder = taskP.mlme_scan_request_orig_macBeaconOrder;
				macBeaconOrder2 = taskP.mlme_scan_request_orig_macBeaconOrder2;
				macBeaconOrder3 = taskP.mlme_scan_request_orig_macBeaconOrder3;
				taskP.setTaskStatus(task, false);
				sscs.MLME_SCAN_confirm(MACenum.m_SUCCESS,ScanType,taskP.mlme_scan_request_ScanChannels,taskP.mlme_scan_request_ListNum,taskP.mlme_scan_request_EnergyDetectList,null);
				csmacaResume();
				return;
			}
			taskP.setTaskStep(task, (byte)1);	//important
			taskP.setTaskFrFunc(task, "PLME_SET_confirm");
			taskP.mlme_scan_request_CurrentChannel++;
			t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
			while((t_chanPos & taskP.mlme_scan_request_ScanChannels) == 0
			||(t_chanPos & tmp_ppib.phyChannelsSupported) == 0)
			{
				taskP.mlme_scan_request_CurrentChannel++;
				t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
			}
			tmp_ppib.phyCurrentChannel = taskP.mlme_scan_request_CurrentChannel;
			phyEntity.PLME_SET_request(PPIBAenum.phyCurrentChannel,/* & */tmp_ppib);
			break;
		default:
			break;
	}

	else if ((ScanType == 0x01)	//active scan
	||	 (ScanType == 0x02))	//passive scan
	switch (step)
	{
		case 0:
			phyEntity.PLME_GET_request(PPIBAenum.phyChannelsSupported);	//value will be returned in tmp_ppib
			taskP.mlme_scan_request_ScanChannels = ScanChannels;
			if ((taskP.mlme_scan_request_ScanChannels & tmp_ppib.phyChannelsSupported) == 0)
			{
				mpib.macBeaconOrder = taskP.mlme_scan_request_orig_macBeaconOrder;
				macBeaconOrder2 = taskP.mlme_scan_request_orig_macBeaconOrder2;
				macBeaconOrder3 = taskP.mlme_scan_request_orig_macBeaconOrder3;
				sscs.MLME_SCAN_confirm(MACenum.m_SUCCESS,ScanType,ScanChannels,(byte)0,null,null);	//SUCCESS or INVALID_PARAMETER?
				csmacaResume();
				return;
			}
			taskP.setTaskStatus(task, true);
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task, "PLME_SET_confirm");
			taskP.mlme_scan_request_orig_macPANId = mpib.macPANId;
			mpib.macPANId = (int)0xffff;
			taskP.mlme_scan_request_ScanDuration = ScanDuration;
			taskP.mlme_scan_request_CurrentChannel = 0;
			taskP.mlme_scan_request_ListNum = 0;
			t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
			while((t_chanPos & taskP.mlme_scan_request_ScanChannels) == 0
			||(t_chanPos & tmp_ppib.phyChannelsSupported) == 0)
			{
				taskP.mlme_scan_request_CurrentChannel++;
				t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
			}
			tmp_ppib.phyCurrentChannel = taskP.mlme_scan_request_CurrentChannel;
			phyEntity.PLME_SET_request(PPIBAenum.phyCurrentChannel,/* & */tmp_ppib);
			break;
		case 1:
			if (Mac802_15_4Impl.verbose)
				System.out.println("[" + JistAPI.getTime()+ "]<MAC>(node " + ((int)localAddr.hashCode()) + ") scanning channel " + taskP.mlme_scan_request_CurrentChannel);
			if (ScanType == 0x01)		//active scan
			{
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task, "csmacaCallBack");
				//--- send a beacon request command ---
                                if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                      System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") before alloc txBcnCmd2:\n\t\ttxBeacon\t= " + txBeacon + " \n\t\ttxAck   \t= " + txAck + " \n\t\ttxBcnCmd\t= " + txBcnCmd + " \n\t\ttxBcnCmd2\t= " + txBcnCmd2 + " \n\t\ttxData  \t= " + txData);

				assert(txBcnCmd2 == null);
				//txBcnCmd2 = MacMessage_802_15_4.alloc();
				assert(txBcnCmd2 != null);
				wph = txBcnCmd2.HDR_LRWPAN();
				constructCommandHeader(txBcnCmd2,/* & */frmCtrl,(byte)0x07,Const.defFrmCtrl_AddrMode16,(int)0xffff,(int)0xffff,Const.defFrmCtrl_AddrModeNone,(int)0,(int)0,false,false,false);
				constructMPDU((byte)1,txBcnCmd2,frmCtrl.FrmCtrl,mpib.macDSN++,wph.MHR_DstAddrInfo,wph.MHR_SrcAddrInfo,(int)0,(byte)0x07,(int)0);
				csmacaBegin(CHAR.C/*'C'*/);
				//------------------------------------
			}
			else
			{
				//taskP.taskStep(task) = 4;	//skip the steps only for active scan
                                taskP.setTaskStep(task, (byte)4);
				taskP.setTaskFrFunc(task, "PLME_SET_TRX_STATE_confirm");
				plme_set_trx_state_request(PHYenum.p_RX_ON);
			}
			break;
		case 2:
			if (status == PHYenum.p_IDLE)
			{
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task, "PD_DATA_confirm");
				plme_set_trx_state_request(PHYenum.p_TX_ON);
				break;
			}
			else
			{
				//freePkt(txBcnCmd2 );	//actually we can keep <txBcnCmd2> for next channel
				txBcnCmd2 = null;
				//fall through case 7
			}
		case 3:
			if (step == 3)
			{
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task, "PLME_SET_TRX_STATE_confirm");
				taskSuccess(CHAR.C/*'C'*/,false);
				plme_set_trx_state_request(PHYenum.p_RX_ON);
				break;
			}
		case 4:
			if (step == 4)
			{
				if (status == PHYenum.p_RX_ON)
				{
					taskP.taskStepIncrement(task);
					taskP.setTaskFrFunc(task, "recvBeacon");
					//schedule for next channel
					scanT.startTimer((Const.aBaseSuperframeDuration * ((1 << taskP.mlme_scan_request_ScanDuration) + 1)) / phyEntity.getRate_BitsPerSecond('s'));
					break;
				}
				//else	//fall through case 7
			}
		case 5:
			if (step == 5)
			{
				//beacon received
				//record the PAN descriptor if it is a new one
				assert(rxBeacon != null);
				wph = rxBeacon.HDR_LRWPAN();
				frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
				frmCtrl.parse();
				for (i=0;i<taskP.mlme_scan_request_ListNum;i++)
				if ((taskP.mlme_scan_request_PANDescriptorList[i].LogicalChannel == taskP.mlme_scan_request_CurrentChannel)
				&& (taskP.mlme_scan_request_PANDescriptorList[i].CoordAddrMode == frmCtrl.srcAddrMode)
				&& (taskP.mlme_scan_request_PANDescriptorList[i].CoordPANId == wph.MHR_SrcAddrInfo.panID)		//but (page 146, line 4-5) implies not checking PAN ID
				&& (((frmCtrl.srcAddrMode == Const.defFrmCtrl_AddrMode16)&&(taskP.mlme_scan_request_PANDescriptorList[i].CoordAddress_16 == (wph.MHR_SrcAddrInfo.addr_16))
				||((frmCtrl.srcAddrMode == Const.defFrmCtrl_AddrMode64)&&(taskP.mlme_scan_request_PANDescriptorList[i].CoordAddress_64 == wph.MHR_SrcAddrInfo.addr_64)))))
				 	break;
				if (i >= taskP.mlme_scan_request_ListNum)	//unique beacon
				{
					taskP.mlme_scan_request_PANDescriptorList[taskP.mlme_scan_request_ListNum] = panDes2;
					taskP.mlme_scan_request_ListNum++;
					if (taskP.mlme_scan_request_ListNum >= 27)
					{
						//stop the timer
						scanT.stopTimerr();
						//fall through case 7
					}
					else
						break;
				}
				else
					break;
			}
		case 6:
			if (step == 6)
			{
				t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
				taskP.mlme_scan_request_ScanChannels &= (t_chanPos^0xffffffff);
				//fall through case 7
			}
		case 7:
			if (((taskP.mlme_scan_request_ScanChannels & tmp_ppib.phyChannelsSupported) == 0)
			   ||(taskP.mlme_scan_request_ListNum >= 27))
			{
				mpib.macPANId = taskP.mlme_scan_request_orig_macPANId;
				mpib.macBeaconOrder = taskP.mlme_scan_request_orig_macBeaconOrder;
				macBeaconOrder2 = taskP.mlme_scan_request_orig_macBeaconOrder2;
				macBeaconOrder3 = taskP.mlme_scan_request_orig_macBeaconOrder3;
				taskP.setTaskStatus(task, false);
				sscs.MLME_SCAN_confirm(MACenum.m_SUCCESS,ScanType,taskP.mlme_scan_request_ScanChannels,taskP.mlme_scan_request_ListNum,null,taskP.mlme_scan_request_PANDescriptorList);
				csmacaResume();
				return;
			}
			taskP.setTaskStep(task, (byte)1);	//important
			taskP.setTaskFrFunc(task, "PLME_SET_confirm");
			taskP.mlme_scan_request_CurrentChannel++;
			t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
			while((t_chanPos & taskP.mlme_scan_request_ScanChannels) == 0
			||(t_chanPos & tmp_ppib.phyChannelsSupported) == 0)
			{
				taskP.mlme_scan_request_CurrentChannel++;
				t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
			}
			tmp_ppib.phyCurrentChannel = taskP.mlme_scan_request_CurrentChannel;
			phyEntity.PLME_SET_request(PPIBAenum.phyCurrentChannel,/* & */tmp_ppib);
			break;
		default:
			break;
	}

	else //if (ScanType == 0x03)	//orphan scan
	switch (step)
	{
		case 0:
			phyEntity.PLME_GET_request(PPIBAenum.phyChannelsSupported);	//value will be returned in tmp_ppib
			taskP.mlme_scan_request_ScanChannels = ScanChannels;
			if ((taskP.mlme_scan_request_ScanChannels & tmp_ppib.phyChannelsSupported) == 0)
			{
				mpib.macBeaconOrder = taskP.mlme_scan_request_orig_macBeaconOrder;
				//macBeaconOrder2 = taskP.mlme_scan_request_orig_macBeaconOrder2;
				macBeaconOrder2 = 15;
				macBeaconOrder3 = taskP.mlme_scan_request_orig_macBeaconOrder3;
				sscs.MLME_SCAN_confirm(MACenum.m_INVALID_PARAMETER,ScanType,ScanChannels,(byte)0,null,null);
				csmacaResume();
				return;
			}
			taskP.setTaskStatus(task, true);
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task, "PLME_SET_confirm");
			taskP.mlme_scan_request_CurrentChannel = 0;
			t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
			while((t_chanPos & taskP.mlme_scan_request_ScanChannels) == 0
			||(t_chanPos & tmp_ppib.phyChannelsSupported) == 0)
			{
				taskP.mlme_scan_request_CurrentChannel++;
				t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
			}
			tmp_ppib.phyCurrentChannel = taskP.mlme_scan_request_CurrentChannel;
			phyEntity.PLME_SET_request(PPIBAenum.phyCurrentChannel,/* & */tmp_ppib);
			break;
		case 1:
			if (Mac802_15_4Impl.verbose)
				System.out.println("[" + JistAPI.getTime()+ "]<MAC>(node " + ((int)localAddr.hashCode()) + ") orphan-scanning channel " + taskP.mlme_scan_request_CurrentChannel );
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task, "csmacaCallBack");
			//--- send an orphan notification command ---
                        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                            System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") before alloc txBcnCmd2:\n\t\ttxBeacon\t= " + txBeacon + " \n\t\ttxAck   \t= " + txAck + " \n\t\ttxBcnCmd\t= " + txBcnCmd + " \n\t\ttxBcnCmd2\t= " + txBcnCmd2 + " \n\t\ttxData  \t= " + txData);

			assert(txBcnCmd2 == null);
			//txBcnCmd2 = MacMessage_802_15_4.alloc();
			assert(txBcnCmd2 != null);
			wph = txBcnCmd2.HDR_LRWPAN();
			constructCommandHeader(txBcnCmd2,/* & */frmCtrl,(byte)0x06,Const.defFrmCtrl_AddrMode64,mpib.macPANId,mpib.macCoordExtendedAddress,Const.defFrmCtrl_AddrMode64,mpib.macPANId,aExtendedAddress,false,false,false);
			constructMPDU((byte)1,txBcnCmd2,frmCtrl.FrmCtrl,mpib.macDSN++,wph.MHR_DstAddrInfo,wph.MHR_SrcAddrInfo,(int)0,(byte)0x06,(int)0);
			csmacaBegin(CHAR.C/*'C'*/);
			//------------------------------------
			break;
		case 2:
			if (status == PHYenum.p_IDLE)
			{
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task, "PD_DATA_confirm");
				plme_set_trx_state_request(PHYenum.p_TX_ON);
				break;
			}
			else
			{
				//freePkt(txBcnCmd2);
				txBcnCmd2 = null;
				//fall through case 6
			}
		case 3:
			if (step == 3)
			{
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task, "PLME_SET_TRX_STATE_confirm");
				taskSuccess(CHAR.C/*'C'*/,false);
				plme_set_trx_state_request(PHYenum.p_RX_ON);
				break;
			}
		case 4:
			if (step == 4)
			{
				if (status == PHYenum.p_RX_ON)
				{
					taskP.taskStepIncrement(task);
					taskP.setTaskFrFunc(task, "IFSHandler");
					scanT.startTimer(Const.aResponseWaitTime / phyEntity.getRate_BitsPerSecond('s'));
					break;
				}
				//else	//fall through case 6
			}
		case 5:
			if (step == 5)
			{
				if (status == PHYenum.p_SUCCESS)	//coordinator realignment command received
				{
					scanT.stopTimerr();
					mpib.macBeaconOrder = taskP.mlme_scan_request_orig_macBeaconOrder;
					macBeaconOrder2 = taskP.mlme_scan_request_orig_macBeaconOrder2;
					macBeaconOrder3 = taskP.mlme_scan_request_orig_macBeaconOrder3;
					taskP.setTaskStatus(task, false);
					//changeNodeColor(((double)JistAPI.getTime()/Constants.SECOND),(mpib.macAssociationPermit)?Nam802_15_4.def_Coor_clr:Nam802_15_4.def_Dev_clr); ??? nam
					t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
					taskP.mlme_scan_request_ScanChannels &= (t_chanPos^0xffffffff);
					sscs.MLME_SCAN_confirm(MACenum.m_SUCCESS,ScanType,taskP.mlme_scan_request_ScanChannels,(byte)0,null,null);
					csmacaResume();
					break;
				}
				else	//time out
				{
					t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
					taskP.mlme_scan_request_ScanChannels &= (t_chanPos^0xffffffff);
					//fall through case 6
				}
			}
		case 6:
			if ((taskP.mlme_scan_request_ScanChannels & tmp_ppib.phyChannelsSupported) == 0)
			{
				mpib.macBeaconOrder = taskP.mlme_scan_request_orig_macBeaconOrder;
				//macBeaconOrder2 = taskP.mlme_scan_request_orig_macBeaconOrder2;
				macBeaconOrder2 = 15;
				macBeaconOrder3 = taskP.mlme_scan_request_orig_macBeaconOrder3;
				taskP.setTaskStatus(task, false);
				sscs.MLME_SCAN_confirm(MACenum.m_NO_BEACON,ScanType,taskP.mlme_scan_request_ScanChannels,(byte)0,null,null);
				csmacaResume();
				return;
			}
			taskP.setTaskStep(task, (byte)1);	//important
			taskP.setTaskFrFunc(task, "PLME_SET_confirm");
			taskP.mlme_scan_request_CurrentChannel++;
			t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
			while((t_chanPos & taskP.mlme_scan_request_ScanChannels) == 0
			||(t_chanPos & tmp_ppib.phyChannelsSupported) == 0)
			{
				taskP.mlme_scan_request_CurrentChannel++;
				t_chanPos = (1<<taskP.mlme_scan_request_CurrentChannel);
			}
			tmp_ppib.phyCurrentChannel = taskP.mlme_scan_request_CurrentChannel;
			phyEntity.PLME_SET_request(PPIBAenum.phyCurrentChannel,/* & */tmp_ppib);
			break;
		default:
			break;
	}
    }
    
    private void mlme_start_request(int PANId,byte LogicalChannel,byte BeaconOrder,
				byte SuperframeOrder,boolean PANCoordinator,boolean BatteryLifeExtension,
				boolean CoordRealignment,boolean SecurityEnable,
				boolean frUpper /*= false*/,PHYenum status /*= PHYenum.p_SUCCESS*/)// ???
    {
        FrameCtrl frmCtrl = new FrameCtrl();
	hdr_lrwpan wph;
	byte origBeaconOrder;
	byte step,task;

	task = taskPending.TP_mlme_start_request;
	if (frUpper) checkTaskOverflow(task);

	step = taskP.taskStep(task);
	switch (step)
	{
		case 0:
			if (mpib.macShortAddress == 0xffff)
			{
				sscs.MLME_START_confirm(MACenum.m_NO_SHORT_ADDRESS);
				return;
			}
			else if ((!phyEntity.channelSupported(LogicalChannel))
			|| (BeaconOrder > 15)
			|| ((SuperframeOrder > BeaconOrder)&&(SuperframeOrder != 15)))
			{
				sscs.MLME_START_confirm(MACenum.m_INVALID_PARAMETER);
				return;
			}
			else if (!capability.FFD)
			{
				sscs.MLME_START_confirm(MACenum.m_UNDEFINED);
				return;
			}
			taskP.setTaskStatus(task, true);
			if (CoordRealignment)		//send a realignment command before changing configuration that affects the command
			{
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task, "csmacaCallBack");
				//broadcast a realignment command
                                if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                      System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") before alloc txBcnCmd2:\n\t\ttxBeacon\t= " + txBeacon + " \n\t\ttxAck   \t= " + txAck + " \n\t\ttxBcnCmd\t= " + txBcnCmd + " \n\t\ttxBcnCmd2\t= " + txBcnCmd2 + " \n\t\ttxData  \t= " + txData);

				assert(txBcnCmd2 == null);
				//txBcnCmd2 = MacMessage_802_15_4.alloc();
				assert(txBcnCmd2 != null);
				wph = txBcnCmd2.HDR_LRWPAN();
				constructCommandHeader(txBcnCmd2,/* & */frmCtrl,(byte)0x08,Const.defFrmCtrl_AddrMode16,(int)0xffff,(int)0xffff,Const.defFrmCtrl_AddrMode64,mpib.macPANId,aExtendedAddress,false,false,false);
				//--- payload (refer to Figure 56) ---
				wph.MSDU_PayloadLen = 7;
				//* * */((int /* * */)wph.MSDU_Payload) = PANId;			//PAN identifier // ??? 
				//* * */((int /* * */)(wph.MSDU_Payload + 2)) = mpib.macShortAddress;	//Coor. int address // ???
                                MSDU_Util.storeAt(wph.MSDU_Payload, 0, new Integer(PANId));
                                MSDU_Util.storeAt(wph.MSDU_Payload, 2, new Integer(mpib.macShortAddress));
                                
				wph.MSDU_Payload[4] = LogicalChannel;				//Logical channel
				///* * */((int /* * */)(wph.MSDU_Payload + 5)) = (int)0xffff;			//int address; be the assigned address if directed to an orphaned device //???
                                MSDU_Util.storeAt(wph.MSDU_Payload, 5, new Integer((int)0xffff));
				constructMPDU((byte)8,txBcnCmd2,frmCtrl.FrmCtrl,mpib.macDSN++,wph.MHR_DstAddrInfo,wph.MHR_SrcAddrInfo,(int)0,(byte)0x08,(int)0);
				csmacaBegin(CHAR.C/*'C'*/);
				//------------------------------------
				//change the configuration and begin to transmit beacons after the transmission of the realignment command
				taskP.mlme_start_request_BeaconOrder = BeaconOrder;
				taskP.mlme_start_request_SuperframeOrder = SuperframeOrder;
				taskP.mlme_start_request_BatteryLifeExtension = BatteryLifeExtension;
				taskP.mlme_start_request_SecurityEnable = SecurityEnable;
				taskP.mlme_start_request_PANCoordinator = PANCoordinator;
				taskP.mlme_start_request_PANId = PANId;
				taskP.mlme_start_request_LogicalChannel = LogicalChannel;
				break;
			}
			else
			{
				//taskP.taskStep(task) = 2;
                                taskP.setTaskStep(task, (byte)2);
				step = 2;
				//fall through case 2
			}
		case 1:
			if (step == 1)
			{
				if (status == PHYenum.p_IDLE)
				{
					taskP.taskStepIncrement(task);
					taskP.setTaskFrFunc(task, "PD_DATA_confirm");
					plme_set_trx_state_request(PHYenum.p_TX_ON);
					break;
				}
				else
				{
					//freePkt(txBcnCmd2);	//actually we can keep <txBcnCmd2> for next channel
					txBcnCmd2 = null;
					//fall through case case 2 -- ignore the failure and continue to transmit beacons
					//taskP.taskStep(task) = 2;
                                        taskP.setTaskStep(task, (byte)2);
				}
			}
		case 2:
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task, "PD_DATA_confirm");	//for beacon
			resetTRX();
			if (CoordRealignment)
				taskSuccess(CHAR.C/*'C'*/,false);
			//change the configuration
			origBeaconOrder = mpib.macBeaconOrder;
			mpib.macBeaconOrder = BeaconOrder;
			if (BeaconOrder == 15)
				mpib.macSuperframeOrder = 15;
			else
				mpib.macSuperframeOrder = SuperframeOrder;
			mpib.macBattLifeExt = BatteryLifeExtension;
			secuBeacon = SecurityEnable;
			if (isPANCoor != PANCoordinator)
                        {
				// changeNodeColor(((double)JistAPI.getTime()/Constants.SECOND),PANCoordinator?Nam802_15_4.def_PANCoor_clr:Nam802_15_4.def_Coor_clr); // nam
                        }
			isPANCoor = PANCoordinator;
			if (PANCoordinator)
			{
				mpib.macPANId = PANId;
				mpib.macCoordExtendedAddress = aExtendedAddress;	//I'm the coordinator of myself
				tmp_ppib.phyCurrentChannel = LogicalChannel;
				phyEntity.PLME_SET_request(PPIBAenum.phyCurrentChannel,/* & */tmp_ppib);
			}
			if (origBeaconOrder == BeaconOrder)
			{
				taskP.setTaskStatus(task, false);
				sscs.MLME_START_confirm(MACenum.m_SUCCESS);
				csmacaResume();
			}
			else if ((origBeaconOrder == 15)&&(BeaconOrder < 15))
			{
				//transmit beacon immediately
				if (bcnTxT.bussy())		//the timer may still be looping there
					bcnTxT.stopTimerr();
				bcnTxT.start(true,true,0.0);
			}
			else if ((origBeaconOrder < 15)&&(BeaconOrder == 15))
				oneMoreBeacon = true;
			break;
		case 3:
			taskP.setTaskStatus(task, false);
			sscs.MLME_START_confirm(MACenum.m_SUCCESS);
			taskSuccess(CHAR.b/*'b'*/, true);
			break;
		default:
			break;
	}
    }
    
    private void mlme_sync_request(byte LogicalChannel, boolean TrackBeacon,boolean frUpper /*= false*/,PHYenum status /*= PHYenum.p_SUCCESS*/) // ???
    {
        byte step,task,BO;

	task = taskPending.TP_mlme_sync_request;
	if (frUpper)
	{
		//checkTaskOverflow(task);	//overlapping allowed
		//stop the beacon receiving timer if it is running
		if (bcnRxT.bussy())
			bcnRxT.stopTimerr();
		//taskP.taskStep(task) = 0;
                taskP.setTaskStep(task, (byte)0);
		//(taskP.taskFrFunc(task))[0] = 0;
                String tmp = taskP.taskFrFunc(task);
                char[] ctmp = tmp.toCharArray();
                ctmp[0] = 0;
                taskP.setTaskFrFunc(task, ctmp.toString());
	}

	step = taskP.taskStep(task);
	switch(step)
	{
		case 0:
			//no validation check required in the draft, but it's better to check it
			if ((!phyEntity.channelSupported(LogicalChannel))	//channel not supported
			 || (mpib.macPANId == 0xffff)			//broadcast PAN ID
			 //|| (macBeaconOrder2 == 15)			//non-beacon mode or <macBeaconOrder2> not yet populated
			 )
			{
				sscs.MLME_SYNC_LOSS_indication(MACenum.m_UNDEFINED);
				return;
			}
			taskP.setTaskStatus(task, true);
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task, "recvBeacon");
			taskP.mlme_sync_request_numSearchRetry = 0;
			taskP.mlme_sync_request_tracking = TrackBeacon;
			//set current channel
			tmp_ppib.phyCurrentChannel = LogicalChannel;
			phyEntity.PLME_SET_request(PPIBAenum.phyCurrentChannel,/* & */tmp_ppib);
			//enable the receiver
			plme_set_trx_state_request(PHYenum.p_RX_ON);
			BO = (macBeaconOrder2 == 15)?14:macBeaconOrder2;
			if (bcnSearchT.bussy())
				bcnSearchT.stopTimerr();
			bcnSearchT.startTimer(Const.aBaseSuperframeDuration*((1 << BO)+1) / phyEntity.getRate_BitsPerSecond('s'));
			break;
		case 1:
			if (status == PHYenum.p_SUCCESS)	//beacon received
			{
				//no confirm primitive for the success - it's better to have one
				taskP.setTaskStatus(task, false);
				//continue to track the beacon if required
				if (TrackBeacon)
				{
					//reset <numSearchRetry> (so that tracking can work properly)
					taskP.mlme_sync_request_numSearchRetry = 0;
					if(!bcnRxT.bussy())
						bcnRxT.start();
				}
				csmacaResume();
			}
			else				//time out when waiting for beacon
			{
				taskP.mlme_sync_request_numSearchRetry++;
				if (taskP.mlme_sync_request_numSearchRetry <= Const.aMaxLostBeacons)
				{
					plme_set_trx_state_request(PHYenum.p_RX_ON);
					BO = (macBeaconOrder2 == 15)?14:macBeaconOrder2;
					bcnSearchT.startTimer(Const.aBaseSuperframeDuration*((1 << BO)+1) / phyEntity.getRate_BitsPerSecond('s'));
				}
				else
				{
					taskP.setTaskStatus(task, false);
					//changeNodeColor(((double)JistAPI.getTime()/Constants.SECOND),Nam802_15_4.def_Node_clr); // nam
					sscs.MLME_SYNC_LOSS_indication(MACenum.m_BEACON_LOSS);
					/*If the initial beacon location fails, no need to track the beacon even it is required
					 *Note that not tracking does not mean the device will not be able to receive beacons --
					 *but the reception may be not so reliable since there is no synchronization.
					 */
					taskP.mlme_sync_request_tracking = false;
					csmacaResume();
					return;
				}
			}
			break;
		default:
			break;
	}
    }
    
    private void mlme_poll_request(byte CoordAddrMode,int CoordPANId,/* IE3ADDR */ int CoordAddress,boolean SecurityEnable,
			       boolean autoRequest /*= false*/,boolean firstTime /*= false*/,PHYenum status /*= PHYenum.p_SUCCESS*/) // ???
    {
        byte step,task;
	FrameCtrl frmCtrl = new FrameCtrl();
	hdr_lrwpan wph;

	task = taskPending.TP_mlme_poll_request;
	if (firstTime)
	{
		if (taskP.taskStatus(task))
			return;
		else
		{
			//taskP.taskStep(task) = 0;
                        taskP.setTaskStep(task, (byte)0);
			//(taskP.taskFrFunc(task))[0] = 0;
                        String tmp = taskP.taskFrFunc(task);
                        char[] ctmp = tmp.toCharArray();
                        ctmp[0] = 0;
                        taskP.setTaskFrFunc(task, ctmp.toString());
		}
	}

	step = taskP.taskStep(task);
	switch(step)
	{
		case 0:
			//check if parameters valid or not
			if (((CoordAddrMode != Const.defFrmCtrl_AddrMode16)&&(CoordAddrMode != Const.defFrmCtrl_AddrMode64))
			 || (CoordPANId == 0xffff))
			{
				if (!autoRequest)
					sscs.MLME_POLL_confirm(MACenum.m_INVALID_PARAMETER);
				return;
			}
			taskP.setTaskStatus(task, true);
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task, "csmacaCallBack");
			taskP.mlme_poll_request_CoordAddrMode = CoordAddrMode;
			taskP.mlme_poll_request_CoordPANId = CoordPANId;
			taskP.mlme_poll_request_CoordAddress = CoordAddress;
			taskP.mlme_poll_request_SecurityEnable = SecurityEnable;
			taskP.mlme_poll_request_autoRequest = autoRequest;
			//-- send a data request command ---
                        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                              System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") before alloc txBcnCmd:\n\t\ttxBeacon\t= " + txBeacon + " \n\t\ttxAck   \t= " + txAck + " \n\t\ttxBcnCmd\t= " + txBcnCmd + " \n\t\ttxBcnCmd2\t= " + txBcnCmd2 + " \n\t\ttxData  \t= " + txData);

			assert(txBcnCmd2 == null);
			//txBcnCmd2 = MacMessage_802_15_4.alloc(); //???
			assert(txBcnCmd2 != null);
			wph = txBcnCmd2.HDR_LRWPAN();
			if ((mpib.macShortAddress == 0xfffe)||(mpib.macShortAddress == 0xffff))
			{
				frmCtrl.setSrcAddrMode(Const.defFrmCtrl_AddrMode64);
				wph.MHR_SrcAddrInfo.addr_64 = aExtendedAddress;
			}
			else
			{
				frmCtrl.setSrcAddrMode(Const.defFrmCtrl_AddrMode16);
				wph.MHR_SrcAddrInfo.addr_16 = mpib.macShortAddress;
			}
			if (sfSpec2.PANCoor)
				frmCtrl.setDstAddrMode(Const.defFrmCtrl_AddrModeNone);
			else
				frmCtrl.setDstAddrMode(CoordAddrMode);
			constructCommandHeader(txBcnCmd2,/* & */frmCtrl,(byte)0x04,frmCtrl.dstAddrMode,CoordPANId,CoordAddress,frmCtrl.srcAddrMode,mpib.macPANId,wph.MHR_SrcAddrInfo.addr_64,SecurityEnable,false,true);
			constructMPDU((byte)1,txBcnCmd2,frmCtrl.FrmCtrl,mpib.macDSN++,wph.MHR_DstAddrInfo,wph.MHR_SrcAddrInfo,(int)0,(byte)0x04,(int)0);
			csmacaBegin(CHAR.C/*'C'*/);
			//------------------------------------
			break;
		case 1:
			if (status == PHYenum.p_IDLE)
			{
				taskP.taskStepIncrement(task);
				taskP.setTaskFrFunc(task, "PD_DATA_confirm");
				plme_set_trx_state_request(PHYenum.p_TX_ON);
				break;
			}
			else
			{
				taskP.setTaskStatus(task, false);
				if (!autoRequest)
					sscs.MLME_POLL_confirm(MACenum.m_CHANNEL_ACCESS_FAILURE);
				resetTRX();
				taskFailed(CHAR.C/*'C'*/,MACenum.m_CHANNEL_ACCESS_FAILURE, true);
				return;
			}
			//break;
		case 2:
			taskP.taskStepIncrement(task);
			taskP.setTaskFrFunc(task, "recvAck");
			//enable the receiver
			plme_set_trx_state_request(PHYenum.p_RX_ON);
			txT.startTimer(mpib.macAckWaitDuration/phyEntity.getRate_BitsPerSecond('s'));
			waitBcnCmdAck2 = true;
			break;
		case 3:
			if (status == PHYenum.p_SUCCESS)	//ack. received
			{
				if (!taskP.mlme_poll_request_pending)
				{
					taskP.setTaskStatus(task, false);
					if (!autoRequest)
						sscs.MLME_POLL_confirm(MACenum.m_NO_DATA);
					resetTRX();
					taskSuccess(CHAR.C/*'C'*/, true);
					return;
				}
				else
				{
					taskP.taskStepIncrement(task);
					taskP.setTaskFrFunc(task, "IFSHandler");
					plme_set_trx_state_request(PHYenum.p_RX_ON);		//wait for data
					taskSuccess(CHAR.C/*'C'*/,false);
					extractT.start(Const.aMaxFrameResponseTime/phyEntity.getRate_BitsPerSecond('s'),true);	//wait for <aMaxFrameResponseTime> symbols (or CAP symbols if beacon enabled) (see page 156, line 1-3)
				}
			}
			else				//time out when waiting for ack.
			{
				numBcnCmdRetry2++;
				if (numBcnCmdRetry2 <= Const.aMaxFrameRetries)
				{
					taskP.setTaskStep(task, (byte)1);	//important
					taskP.setTaskFrFunc(task, "csmacaCallBack");
					waitBcnCmdAck2 = false;
					csmacaResume();
				}
				else
				{
					taskP.setTaskStatus(task, false);
					if (!autoRequest)
						sscs.MLME_POLL_confirm(MACenum.m_NO_ACK);
					resetTRX();
					taskFailed(CHAR.C/*'C'*/,MACenum.m_NO_ACK, true);
					return;
				}
			}
			break;
		case 4:
			taskP.setTaskStatus(task, false);
			if (status == PHYenum.p_SUCCESS)		//data received
			{
				//stop the timer
				extractT.stopTimerr();
				if (!autoRequest)
					sscs.MLME_POLL_confirm(MACenum.m_SUCCESS);
				//another step is to issue DATA.indication() which has been done in IFSHandler()

				//poll again to see if there are more packets pending -- note that, for each poll request, more than one confirm could be passed to upper layer
				mlme_poll_request(CoordAddrMode,CoordPANId,CoordAddress,SecurityEnable,autoRequest,true, PHYenum.p_SUCCESS);
			}
			else					//time out when waiting for response
			{
				if (!autoRequest)
					sscs.MLME_POLL_confirm(MACenum.m_NO_DATA);
				resetTRX();
				csmacaResume();
			}
			break;
		default:
			break;
	}
    }
    
    //-------------------------------------------------------------------------------------

    private void csmacaBegin(CHAR pktType)
    {
        
         if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            System.out.println("[MAC.csmacaBegin()]");
         
        if (pktType == CHAR.c/*'c'*/)		//txBcnCmd
	{
		waitBcnCmdAck = false;			//beacon packet not yet transmitted
		numBcnCmdRetry = 0;
		if (backoffStatus == 99)		//backoffing for data packet
		{
			backoffStatus = 0;
			csmaca.cancel();
		}
		csmacaResume();
	}
	else if (pktType == CHAR.C/*'C'*/)	//txBcnCmd2
	{
		waitBcnCmdAck2 = false;			//command packet not yet transmitted
		numBcnCmdRetry2 = 0;
		if ((backoffStatus == 99)&&(txCsmaca != txBcnCmd))	//backoffing for data packet
		{
			backoffStatus = 0;
			csmaca.cancel();
		}
		csmacaResume();

	}
	else //if (pktType == CHAR.d/*'d'*/)	//txData
	{
		waitDataAck = false;			//data packet not yet transmitted
		numDataRetry = 0;
		csmacaResume();
	}
    }
    
    private void csmacaResume() {
        FrameCtrl frmCtrl = new FrameCtrl();

		if ((backoffStatus != 99)			//not during backoff
		&&  (!inTransmission))				//not during transmission
		if ((txBcnCmd != null)&&(!waitBcnCmdAck)) {
			backoffStatus = 99;
			frmCtrl.FrmCtrl = txBcnCmd.HDR_LRWPAN().MHR_FrmCtrl;
			frmCtrl.parse();
			txCsmaca = txBcnCmd;
			csmaca.start(true,txBcnCmd,frmCtrl.ackReq);
		}
		else if ((txBcnCmd2 != null)&&(!waitBcnCmdAck2)) {
			backoffStatus = 99;
			frmCtrl.FrmCtrl = txBcnCmd2.HDR_LRWPAN().MHR_FrmCtrl;
			frmCtrl.parse();
			txCsmaca = txBcnCmd2;
			csmaca.start(true,txBcnCmd2,frmCtrl.ackReq);
		}
		else if ((txData != null)&&(!waitDataAck)) {
			taskP.setTaskFrFunc(taskPending.TP_mcps_data_request,"csmacaCallBack");	//the transmission may be interrupted and need to backoff again
			//taskP.taskStep(taskPending.TP_mcps_data_request) = 1;				//also set the step
	        taskP.setTaskStep(taskPending.TP_mcps_data_request, (byte)1);				//also set the step
			backoffStatus = 99;
			frmCtrl.FrmCtrl = txData.HDR_LRWPAN().MHR_FrmCtrl;
			frmCtrl.parse();
			txCsmaca = txData;
			csmaca.start(true,txData,frmCtrl.ackReq);
		}
    }
    
    public void csmacaCallBack(PHYenum status)
    {
         if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            System.out.println("[MAC.csmacaCallBack()]");
        
        String __FUNCTION__ = "csmacaCallBack";
        if (((txBcnCmd == null)||(waitBcnCmdAck))
          &&((txBcnCmd2 == null)||(waitBcnCmdAck2))
          &&((txData == null)||(waitDataAck)))
        return;

	backoffStatus = (status == PHYenum.p_IDLE) ? (byte)1 : (byte)2;

        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
        {
            hdr_cmn ch = txCsmaca.HDR_CMN();
            if (status != PHYenum.p_IDLE)
                System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") backoff failed: type = " + Trace.wpan_pName(txCsmaca) + ", src =" +  Trace.p802_15_4macSA(txCsmaca) + ", dst = " + Trace.p802_15_4macDA(txCsmaca) + ", uid = " +  ch.uid() + ", mac_uid = " + txCsmaca.HDR_LRWPAN().uid + ", size = " + ch.size());
        }
	
	dispatch(status,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
    }
    
    public int getBattLifeExtSlotNum()
    {
        phyEntity.PLME_GET_request(PPIBAenum.phyCurrentChannel);
	return (tmp_ppib.phyCurrentChannel<=10) ? 8 : 6;
    }
    public double getCAP(boolean small)
    {
        double bcnTxTime,bcnRxTime,bcnOtherRxTime,bPeriod;
	double sSlotDuration,sSlotDuration2,sSlotDuration3,BI2,BI3,t_CAP = 0.0,t_CAP2 = 0.0,t_CAP3 = 0.0;
	double now,oneDay,tmpf;
	
	now = ((double)JistAPI.getTime()/Constants.SECOND);
	oneDay = now + 24.0*3600;

	if ((mpib.macBeaconOrder == 15)&&(macBeaconOrder2 == 15)				//non-beacon enabled
	&&(macBeaconOrder3 == 15))								//no beacons from outside PAN
		return oneDay;									//transmission can always go ahead
	
	bcnTxTime = macBcnTxTime / phyEntity.getRate_BitsPerSecond('s');
	bcnRxTime = macBcnRxTime / phyEntity.getRate_BitsPerSecond('s');
	bcnOtherRxTime = macBcnOtherRxTime / phyEntity.getRate_BitsPerSecond('s');
	bPeriod = Const.aUnitBackoffPeriod / phyEntity.getRate_BitsPerSecond('s');
	sSlotDuration = sfSpec.sd / phyEntity.getRate_BitsPerSecond('s');
	sSlotDuration2 = sfSpec2.sd / phyEntity.getRate_BitsPerSecond('s');
	sSlotDuration3 = sfSpec3.sd / phyEntity.getRate_BitsPerSecond('s');
	BI2 = (sfSpec2.BI / phyEntity.getRate_BitsPerSecond('s'));
	BI3 = (sfSpec3.BI / phyEntity.getRate_BitsPerSecond('s'));
	if (mpib.macBeaconOrder != 15)
	{
		if (sfSpec.BLE)
		{
			/* Linux floating number compatibility
			t_CAP = (bcnTxTime + (beaconPeriods + getBattLifeExtSlotNum()) * aUnitBackoffPeriod);
			*/
			{
			tmpf = (beaconPeriods + getBattLifeExtSlotNum()) * Const.aUnitBackoffPeriod;
			t_CAP = bcnTxTime + tmpf;
			}
		}
		else
		{
			/* Linux floating number compatibility
			t_CAP = (bcnTxTime + (sfSpec.FinCAP + 1) * sSlotDuration);
			*/
			{
			tmpf = (sfSpec.FinCAP + 1) * sSlotDuration;
			t_CAP = bcnTxTime + tmpf;
			}
		}
	}
	if (macBeaconOrder2 != 15)
	{
		if (sfSpec2.BLE)
		{
			/* Linux floating number compatibility
			t_CAP2 = (bcnRxTime + (beaconPeriods2 + getBattLifeExtSlotNum()) * aUnitBackoffPeriod);
			*/
			{
			tmpf = (beaconPeriods2 + getBattLifeExtSlotNum()) * Const.aUnitBackoffPeriod;
			t_CAP2 = bcnRxTime + tmpf;
			}
		}
		else
		{
			/* Linux floating number compatibility
			t_CAP2 = (bcnRxTime + (sfSpec2.FinCAP + 1) * sSlotDuration2);
			*/
			{
			tmpf = (sfSpec2.FinCAP + 1) * sSlotDuration2;
			t_CAP2 = bcnRxTime + tmpf;
			}
		}

		/* Linux floating number compatibility
		if ((t_CAP2 < now)&&(t_CAP2 + aMaxLostBeacons * BI2 >= now))
		*/
		tmpf = Const.aMaxLostBeacons * BI2;
		if ((t_CAP2 < now)&&(t_CAP2 + tmpf >= now))	//no more than <aMaxLostBeacons> beacons missed
		while (t_CAP2 < now)
			t_CAP2 += BI2;
	}
	if (macBeaconOrder3 != 15)
	{
		//no need to handle option <macBattLifeExt> here
		/* Linux floating number compatibility
		t_CAP3 = (bcnOtherRxTime + (sfSpec3.FinCAP + 1) * sSlotDuration3);
		*/
		{
		tmpf = (sfSpec3.FinCAP + 1) * sSlotDuration3;
		t_CAP3 = bcnOtherRxTime + tmpf;
		}

		/* Linux floating number compatibility
		if ((t_CAP3 < now)&&(t_CAP3 + aMaxLostBeacons * BI3 >= now))
		*/
		tmpf = Const.aMaxLostBeacons * BI3;
		if ((t_CAP3 < now)&&(t_CAP3 + tmpf >= now))	//no more than <aMaxLostBeacons> beacons missed
		while (t_CAP3 < now)
			t_CAP3 += BI3;
	}

	if ((mpib.macBeaconOrder == 15)&&(macBeaconOrder2 == 15))
	{
		if (t_CAP3 >= now)
			return t_CAP3;
		else
			return oneDay;
	}
	else if (mpib.macBeaconOrder == 15)
	{
		if (t_CAP2 >= now)
			return t_CAP2;
		else
			return oneDay;
	}
	else if (macBeaconOrder2 == 15)
	{
		if (t_CAP >= now)
			return t_CAP;
		else
			return oneDay;
	}
	else
	{
		if (t_CAP2 < now)
			return t_CAP;

		if ((small)
		&&  (t_CAP > t_CAP2))
			t_CAP = t_CAP2;
		if ((!small)
		&&  (t_CAP < t_CAP2))
			t_CAP = t_CAP2;

		return t_CAP;
	}
    }
    
    public double getCAPbyType(int type)
    {
        double bcnTxTime,bcnRxTime,bcnOtherRxTime,bPeriod;
	double sSlotDuration,sSlotDuration2,sSlotDuration3,BI2,BI3,t_CAP,t_CAP2,t_CAP3;
	double now,oneDay = 0.0,tmpf;
	
	now = ((double)JistAPI.getTime()/Constants.SECOND);
	oneDay = now + 24.0*3600;

	if ((mpib.macBeaconOrder == 15)&&(macBeaconOrder2 == 15)				//non-beacon enabled
	&&(macBeaconOrder3 == 15))								//no beacons from outside PAN
		return oneDay;									//transmission can always go ahead
	
	bcnTxTime = macBcnTxTime / phyEntity.getRate_BitsPerSecond('s');
	bcnRxTime = macBcnRxTime / phyEntity.getRate_BitsPerSecond('s');
	bcnOtherRxTime = macBcnOtherRxTime / phyEntity.getRate_BitsPerSecond('s');
	bPeriod = Const.aUnitBackoffPeriod / phyEntity.getRate_BitsPerSecond('s');
	sSlotDuration = sfSpec.sd / phyEntity.getRate_BitsPerSecond('s');
	sSlotDuration2 = sfSpec2.sd / phyEntity.getRate_BitsPerSecond('s');
	sSlotDuration3 = sfSpec3.sd / phyEntity.getRate_BitsPerSecond('s');
	BI2 = (sfSpec2.BI / phyEntity.getRate_BitsPerSecond('s'));
	BI3 = (sfSpec3.BI / phyEntity.getRate_BitsPerSecond('s'));

	if (type == 1)
	if (mpib.macBeaconOrder != 15)
	{
		if (sfSpec.BLE)
		{
			/* Linux floating number compatibility
			t_CAP = (bcnTxTime + (beaconPeriods + getBattLifeExtSlotNum()) * aUnitBackoffPeriod);
			*/
			{
			tmpf = (beaconPeriods + getBattLifeExtSlotNum()) * Const.aUnitBackoffPeriod;
			t_CAP = bcnTxTime + tmpf;
			}
		}
		else
		{
			/* Linux floating number compatibility
			t_CAP = (bcnTxTime + (sfSpec.FinCAP + 1) * sSlotDuration);
			*/
			{
			tmpf = (sfSpec.FinCAP + 1) * sSlotDuration;
			t_CAP = bcnTxTime + tmpf;
			}
		}
		return (t_CAP>=now)?t_CAP:oneDay;
	}
	else
		return oneDay;

	if (type == 2)
	if (macBeaconOrder2 != 15)
	{
		if (sfSpec2.BLE)
		{
			/* Linux floating number compatibility
			t_CAP2 = (bcnRxTime + (beaconPeriods2 + getBattLifeExtSlotNum()) * aUnitBackoffPeriod);
			*/
			{
			tmpf = (beaconPeriods2 + getBattLifeExtSlotNum()) * Const.aUnitBackoffPeriod;
			t_CAP2 = bcnRxTime + tmpf;
			}
		}
		else
		{
			/* Linux floating number compatibility
			t_CAP2 = (bcnRxTime + (sfSpec2.FinCAP + 1) * sSlotDuration2);
			*/
			{
			tmpf = (sfSpec2.FinCAP + 1) * sSlotDuration2;
			t_CAP2 = bcnRxTime + tmpf;
			}
		}

		/* Linux floating number compatibility
		if ((t_CAP2 < now)&&(t_CAP2 + aMaxLostBeacons * BI2 >= now))
		*/
		tmpf = Const.aMaxLostBeacons * BI2;
		if ((t_CAP2 < now)&&(t_CAP2 + tmpf >= now))	//no more than <aMaxLostBeacons> beacons missed
		while (t_CAP2 < now)
			t_CAP2 += BI2;
		return (t_CAP2>=now)?t_CAP2:oneDay;
	}
	else
		return oneDay;
	
	if (type == 3)
	if (macBeaconOrder3 != 15)
	{
		//no need to handle option <macBattLifeExt> here
		/* Linux floating number compatibility
		t_CAP3 = (bcnOtherRxTime + (sfSpec3.FinCAP + 1) * sSlotDuration3);
		*/
		{
		tmpf = (sfSpec3.FinCAP + 1) * sSlotDuration3;
		t_CAP3 = bcnOtherRxTime + tmpf;
		}

		/* Linux floating number compatibility
		if ((t_CAP3 < now)&&(t_CAP3 + aMaxLostBeacons * BI3 >= now))
		*/
		tmpf = Const.aMaxLostBeacons * BI3;
		if ((t_CAP3 < now)&&(t_CAP3 + tmpf >= now))	//no more than <aMaxLostBeacons> beacons missed
		while (t_CAP3 < now)
			t_CAP3 += BI3;
		return (t_CAP3>=now)?t_CAP3:oneDay;
	}
	else
		return oneDay;

	return oneDay;
    }
    
    public boolean canProceedWOcsmaca(MacMessage_802_15_4 p)	//can we proceed w/o CSMA-CA?
    {
        	//this function checks whether there is enough time in the CAP of current superframe to finish a transaction (transmit a pending packet to a device)
	//(in the case the node acts as both a coordinator and a device, both the superframes from and to this node should be taken into account)
	double wtime,t_IFS,t_transacTime,t_CAP,tmpf;
	FrameCtrl frmCtrl = new FrameCtrl();
	int type;

	if ((mpib.macBeaconOrder == 15)&&(macBeaconOrder2 == 15)				
	&&(macBeaconOrder3 == 15))								
		return true;									
	else
	{
		frmCtrl.FrmCtrl = p.HDR_LRWPAN().MHR_FrmCtrl;
		frmCtrl.parse();
		wtime = 0.0;
		//there is no need to consider <macBattLifeExt>, since the device polling the data 
		//should be waiting rather than go to sleep after the first 6 CAP backoff perios.
		if (p.HDR_CMN().size() <= Const.aMaxSIFSFrameSize)
			t_IFS = Const.aMinSIFSPeriod;
		else
			t_IFS = Const.aMinLIFSPeriod;
		t_IFS /= phyEntity.getRate_BitsPerSecond('s');
		t_transacTime  = locateBoundary(toParent(p),wtime) - wtime;			//boundary location time
		t_transacTime += phyEntity.trxTime(p, false);						//packet transmission time
		if (frmCtrl.ackReq)
		{
			t_transacTime += mpib.macAckWaitDuration/phyEntity.getRate_BitsPerSecond('s');		//ack. waiting time
			t_transacTime += 2 * Const.max_pDelay;						//round trip propagation delay (802.15.4 ignores this, but it should be there even though it is very small)
			t_transacTime += t_IFS;							//IFS time -- not only ensure that the sender can finish the transaction, but also the receiver
			t_CAP = getCAP(true);

			/* Linux floating number compatibility
			if (((double)JistAPI.getTime()/Constants.SECOND) + wtime + t_transacTime > t_CAP)
			*/
			tmpf = ((double)JistAPI.getTime()/Constants.SECOND) + wtime;
			tmpf += t_transacTime;
			if (tmpf > t_CAP)
				return false;
			else
				return true;
		}
		else
		{
			//in this case, we need to handle individual CAP 
			t_CAP = getCAPbyType(1);

			/* Linux floating number compatibility
			if (((double)JistAPI.getTime()/Constants.SECOND) + wtime + t_transacTime > t_CAP)
			*/
			tmpf = ((double)JistAPI.getTime()/Constants.SECOND) + wtime;
			tmpf += t_transacTime;
			if (tmpf > t_CAP)
				return false;
			t_CAP = getCAPbyType(2);
			t_transacTime += Const.max_pDelay;						//one-way trip propagation delay (802.15.4 ignores this, but it should be there even though it is very small)
			t_transacTime += 12/phyEntity.getRate_BitsPerSecond('s');					//transceiver turn-around time (receiver may need to do this to transmit next beacon)
			t_transacTime += t_IFS;							//IFS time -- not only ensure that the sender can finish the transaction, but also the receiver

			/* Linux floating number compatibility
			if (((double)JistAPI.getTime()/Constants.SECOND) + wtime + t_transacTime > t_CAP)
			*/
			tmpf = ((double)JistAPI.getTime()/Constants.SECOND) + wtime;
			tmpf += t_transacTime;
			if (tmpf > t_CAP)
				return false;
			t_CAP = getCAPbyType(3);
			t_transacTime -= t_IFS;							//the third node does not need to handle the transaction

			/* Linux floating number compatibility
			if (((double)JistAPI.getTime()/Constants.SECOND) + wtime + t_transacTime > t_CAP)
			*/
			tmpf = ((double)JistAPI.getTime()/Constants.SECOND) + wtime;
			tmpf += t_transacTime;
			if (tmpf > t_CAP)
				return false;

			return true;
                    
		}
	}

    }
    
    private void transmitCmdData()
    {
        double delay;

	if ((mpib.macBeaconOrder != 15)||(macBeaconOrder2 != 15))	//beacon enabled -- slotted
	{
		delay =locateBoundary(toParent(txCsmaca),0.0);
		if(delay > 0.0)
		{
			//Scheduler.instance().schedule(/* & */txCmdDataH, /* & */(txCmdDataH.nullEvent), delay); // ???
                        txCmdDataH.executeLater(delay);
			return;
		}
	}

	//transmit immediately
	txBcnCmdDataHandler();
    }
    
    private void reset_TRX(String frFile,String frFunc,int line) // ??? char *frFile 
    {
        double t_CAP;
	PHYenum t_state;

	if ((mpib.macBeaconOrder != 15)||(macBeaconOrder2 != 15))	//beacon enabled
	{
		//according to the draft, <macRxOnWhenIdle> only considered during idle periods of the CAP if beacon enabled
		t_CAP = getCAP(false);
		if (((double)JistAPI.getTime()/Constants.SECOND) < t_CAP)
			t_state = mpib.macRxOnWhenIdle ? PHYenum.p_RX_ON : PHYenum.p_TRX_OFF;
		else
			t_state = PHYenum.p_RX_ON;	//(not considered ==> RX_ON)?
	}
	else
		t_state = mpib.macRxOnWhenIdle ? PHYenum.p_RX_ON : PHYenum.p_TRX_OFF;
        
        if (Def.DEBUG802_15_4 && Def.DEBUG802_15_4_nodeid.hashCode() == localAddr.hashCode())
        {
            System.out.println("[MAC][reset_TRX()] - mpib.macRxOnWhenIdle = " + mpib.macRxOnWhenIdle);
            System.out.println("[MAC][reset_TRX()] - to t_state = " + t_state);
        }
        
	set_trx_state_request(t_state,frFile,frFunc,line);
    }
    
    private void taskSuccess(CHAR task, boolean csmacaRes /*= true*/) { // ???
        hdr_cmn ch;
		hdr_lrwpan wph;
		int t_CAP;
		byte ifs = (byte)0;
		double tmpf;
		
		if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
			taskPLogger.debug("[" +JistAPI.getTime()/Constants.MILLI_SECOND + "ms]["+localAddr+"][MAC.taskSuccess()] - Task_[" + task + "]_SUCCESS");

        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
              System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") task '" + task + " successful: txBcnCmd:\n\t\ttxBeacon\t= " + txBeacon + " \n\t\ttxAck   \t= " + txAck + " \n\t\ttxBcnCmd\t= " + txBcnCmd + " \n\t\ttxBcnCmd2\t= " + txBcnCmd2 + " \n\t\ttxData  \t= " + txData);

		if (task == CHAR.b /*'b'*/)	{//beacon
			if (txBeacon == null) {
				assert(txBcnCmd2 != null);
				txBeacon = txBcnCmd2;
				txBcnCmd2 = null;
			}
			//--- calculate CAP ---
			sfSpec.parse();		
			if (txBeacon.HDR_CMN().size() <= Const.aMaxSIFSFrameSize)
				ifs = Const.aMinSIFSPeriod;
			else
				ifs = Const.aMinLIFSPeriod;
	
			/* Linux floating number compatibility
			beaconPeriods = (byte)((phyEntity.trxTime(txBeacon) * phyEntity.getRate('s') + ifs) / aUnitBackoffPeriod);
			*/
			
            tmpf = phyEntity.trxTime(txBeacon, false) * phyEntity.getRate_BitsPerSecond('s');
            tmpf += ifs;
            beaconPeriods = (byte)(tmpf / Const.aUnitBackoffPeriod);

	
			/* Linux floating number compatibility
			if (fmod((phyEntity.trxTime(txBeacon) * phyEntity.getRate('s')+ ifs) ,aUnitBackoffPeriod) > 0.0)
			*/
			tmpf = phyEntity.trxTime(txBeacon, false) * phyEntity.getRate_BitsPerSecond('s');
			tmpf += ifs;
			if (/*fmod(tmpf,Const.aUnitBackoffPeriod)*/ tmpf % Const.aUnitBackoffPeriod > 0.0)
				beaconPeriods++;
	
			t_CAP =  ((sfSpec.FinCAP + 1) * (sfSpec.sd / Const.aUnitBackoffPeriod) - beaconPeriods); // ??? careful with conversion
	
			if (t_CAP == 0)	{
				csmacaRes = false;
				plme_set_trx_state_request(PHYenum.p_TRX_OFF);
			}
			else
				plme_set_trx_state_request(PHYenum.p_RX_ON);
			//CSMA-CA may be waiting for the new beacon
			if (backoffStatus == 99)
				csmaca.newBeacon(CHAR.t/*'t'*/);
			//----------------------
			beaconWaiting = false;
			//MacMessage_802_15_4.free(txBeacon);
			txBeacon = null;
			/*
			//send out delayed ack.
			if (txAck)
			{
				csmacaRes = false;
				plme_set_trx_state_request(PHYenum.p_TX_ON);
			}
			*/
		}
		else if (task == CHAR.a/*'a'*/)	{ //ack.
			assert(txAck != null);
			//MacMessage_802_15_4.free(txAck);
			txAck = null;
		}
		else if (task == CHAR.c/*'c'*/)	//command
		{
			assert(txBcnCmd != null);
			//if it is a pending packet, delete it from the pending list
			TRANSACLINK.updateTransacLinkByPktOrHandle(Def.tr_oper_del,/* & */transacLink1,/* & */transacLink2,txBcnCmd, (byte)0);
			//freePkt(txBcnCmd);
			txBcnCmd = null;
		}
		else if (task == CHAR.C/*'C'*/)	//command
		{
			assert(txBcnCmd2 != null);
			freePkt(txBcnCmd2);
			txBcnCmd2 = null;
		}
		else if (task == CHAR.d/*'d'*/)	{//data
			
			assert(txData != null);
	
			ch = txData.HDR_CMN();
			wph = txData.HDR_LRWPAN();
	
			MacMessage_802_15_4 p = txData.copy();
			
			txData = null;
			
			if (ch.ptype() == Packet_t.PT_MAC) {
				assert(wph.msduHandle != 0);
				sscs.MCPS_DATA_confirm(wph.msduHandle,MACenum.m_SUCCESS);
			} else {
				/* ??? Exists in NS-2
	                        Uif (Mac802_15_4Impl.callBack == 2)
				if (ch.xmit_failure_)
				if (Trace.p802_15_4macDA(p) != MAC_BROADCAST)
				{
					ch.size() -= macHeaderLen(wph.MHR_FrmCtrl);
					ch.xmit_reason_ = 1;
					ch.xmit_failure_(p.refcopy(),ch.xmit_failure_data_);
				}
				if (callback_)	
				{
					Handler *h = callback_;
					callback_ = 0;
					h.handle((Event) 0);
				}*/
			}
			//if it is a pending packet, delete it from the pending list
			TRANSACLINK.updateTransacLinkByPktOrHandle(Def.tr_oper_del,/* & */transacLink1,/* & */transacLink2, p, (byte)0);
			freePkt(p);

            JistAPI.sleepBlock(1 * Constants.MILLI_SECOND);
            reset();
            netEntity.pump(netId);
		}
		else
			assert(false);
	
		if (csmacaRes)
			csmacaResume();
    }
    
    private void taskFailed(CHAR task, MACenum status, boolean csmacaRes /*= true*/) { // ???
        hdr_cmn ch;
		hdr_lrwpan wph;
	
		if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
			taskPLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.taskFailed()] - Task_[" + task + "]_FAILED - ");   

        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") task '" + task + " failed: txBcnCmd:\n\t\ttxBeacon\t= " + txBeacon + " \n\t\ttxAck   \t= " + txAck + " \n\t\ttxBcnCmd\t= " + txBcnCmd + " \n\t\ttxBcnCmd2\t= " + txBcnCmd2 + " \n\t\ttxData  \t= " + txData);
	
		if ((task == CHAR.b/*'b'*/)	//beacon
		|| (task == CHAR.a/*'a'*/)	//ack.
		|| (task == CHAR.c/*'c'*/))	//command
	        
		{
			assert(false);	//we don't handle the above failures here
		}
		else if (task == CHAR.C/*'C'*/)	{ //command
			//freePkt(txBcnCmd2);
			txBcnCmd2 = null;
		}
		else if (task == CHAR.d/*'d'*/)	{ //data
			wph = txData.HDR_LRWPAN();
			ch = txData.HDR_CMN();
	                if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
	                      System.out.println("[D][FAIL][" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "." + /*__LINE__ + */"][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") failure: type " + Trace.wpan_pName(txData) + ", src = " + Trace.p802_15_4macSA(txData) + ", dst = " + Trace.p802_15_4macDA(txData) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
	
			MacMessage_802_15_4 p = txData.copy();
			txData = null;
			if (wph.msduHandle == (byte)1)	//from SSCS
				sscs.MCPS_DATA_confirm(wph.msduHandle,status);
			else {
				//if (callback_			
				//&& (!dataWaitT.busy()))	
				//	{
				//		Handler *h = callback_;
				//		callback_ = 0;
				//		h.handle((Event) 0);
				//	}
	                    
				/* Exists in NS-2
	                         if (Mac802_15_4Impl.callBack)
				if (ch.xmit_failure_)
				{
					wph.setSN = true;		
					ch.size() -= macHeaderLen(wph.MHR_FrmCtrl);
					ch.xmit_reason_ = 0;
					ch.xmit_failure_(p.refcopy(),ch.xmit_failure_data_);
				}
				if (callback_			
				&& (!dataWaitT.busy()))	
				{
					Handler *h = callback_;
					callback_ = 0;
					h.handle((Event) 0);
				}*/
			}
			//freePkt(p);
			// Rey: notify upper layer of failed transmission
            netEntity.dropNotify(p.getPayload(), new MacAddress(p.HDR_CMN().next_hop_), Reason.UNDELIVERABLE);
            if (Def.DEBUG802_15_4_packetdrop) 
            	System.out.println("Packet dropped at node #" + myNode.getID());
            freePkt(p);
	                
		}
		else
			assert(false);
	
		if (csmacaRes)
			csmacaResume();
	        if (task == CHAR.d/*'d'*/) {
	            reset();
	            netEntity.pump(netId); // get the next packet in queue
	        }
    }
    
    private void freePkt(MacMessage_802_15_4 pkt) {
        /*
		if (HDR_LRWPAN(pkt).indirect)
			return;			//the packet will be automatically deleted when expired
		else
			MacMessage_802_15_4.free(pkt);
		*/
		//MacMessage_802_15_4.free(pkt);		//now same operation for directly transmitted and indirectly transmitted packets
	    pkt = null;
    }
    
    private byte macHeaderLen_Bytes(int FrmCtrl)  // in bytes
    {
        String __FUNCTION__ = "macHeaderLen";
        
        //calculate the MAC sublayer header (also including footer) length
	byte macHLen = (byte)0;
	FrameCtrl frmCtrl = new FrameCtrl();

	frmCtrl.FrmCtrl = FrmCtrl;
	frmCtrl.parse();
	
	macHLen = 0;
	macHLen += 2		//FrmCtrl
		  +1		//BSN/DSN
		  +2;		//FCS
	if (frmCtrl.frmType == Const.defFrmCtrl_Type_Beacon)		//Beacon
	{
		if (frmCtrl.srcAddrMode == Const.defFrmCtrl_AddrMode16)
			macHLen += 2;
		else if (frmCtrl.srcAddrMode == Const.defFrmCtrl_AddrMode64)
			macHLen += 8;
	}
	else if ((frmCtrl.frmType == Const.defFrmCtrl_Type_Data)	//Data
		||(frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd))	//Mac Command
	{
		if (frmCtrl.dstAddrMode == Const.defFrmCtrl_AddrMode16)
			macHLen += 2;
		else if (frmCtrl.dstAddrMode == Const.defFrmCtrl_AddrMode64)
			macHLen += 8;
		if (frmCtrl.srcAddrMode == Const.defFrmCtrl_AddrMode16)
			macHLen += 2;
		else if (frmCtrl.srcAddrMode == Const.defFrmCtrl_AddrMode64)
			macHLen += 8;
	}
	else if (frmCtrl.frmType == Const.defFrmCtrl_Type_Ack)	//Ack.
	{
		;//nothing to do
	}
	else
		System.out.println("[" + __FUNCTION__ + "][" + JistAPI.getTime()+ "]<MAC>(node " + localAddr + ") Invalid frame type!");

	return macHLen;
    }
    
    public int hdr_dst(/*char * */ hdr_mac hdr, int dst)
    {
        return p802_15_4hdr_dst(hdr, dst);
    }
    
    public int p802_15_4hdr_dst(/*char */ hdr_mac hdr, int dst)
    {
        if(dst > -2)
            hdr.macDA_ = (int)dst;
        return hdr.macDA_;
    }
    
    public int hdr_src(/*char * */ hdr_mac hdr, int src)
    {
        return p802_15_4hdr_src(hdr, src);
    }
    
    public int p802_15_4hdr_src(/*char * */ hdr_mac hdr, int src)
    {
        if (src > -2)
            hdr.macSA_ = (int)src;
        return hdr.macSA_;
    }
    
    public int hdr_type(/*char * */ hdr_mac hdr, int type)
    {
        return p802_15_4hdr_type(hdr, type);
    }
    
    public int p802_15_4hdr_type(/*char * */ hdr_mac hdr, int type)
    {
        if (type == (int)1)
            hdr.hdr_type_ = type;
        return hdr.hdr_type();
    }
    
    private void constructACK(MacMessage_802_15_4 p)
    {      
        boolean intraPan;
	byte origFrmType;
	FrameCtrl frmCtrl = new FrameCtrl();
	//MacMessage_802_15_4 *ack = MacMessage_802_15_4.alloc();
        MacMessage_802_15_4 ack = new MacMessage_802_15_4();
	hdr_lrwpan wph1 = p.HDR_LRWPAN();
	hdr_lrwpan wph2 = ack.HDR_LRWPAN();
	hdr_cmn ch1 = p.HDR_CMN();
	hdr_cmn ch2 = ack.HDR_CMN();
	int i;
        
        if (Def.DEBUG802_15_4_ack && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            System.out.println("[" + JistAPI.getTime() + "][" + localAddr + "][MAC.constructACK]  for packet with SN: " + wph1.MHR_BDSN);
	
	//hdr_dst((char[])ack.HDR_MAC().serialize(),Trace.p802_15_4macSA(p)); ???
        hdr_dst(ack.HDR_MAC(), Trace.p802_15_4macSA(p));
        hdr_src(ack.HDR_MAC(), localAddr.hashCode());
	//hdr_src((char[])ack.HDR_MAC().serialize(),((int)localAddr.hashCode())); ???
        
        
	frmCtrl.FrmCtrl = wph1.MHR_FrmCtrl;
	frmCtrl.parse();
	intraPan = frmCtrl.intraPan;
	origFrmType = frmCtrl.frmType;
	frmCtrl.FrmCtrl = 0;
	frmCtrl.setFrmType(Const.defFrmCtrl_Type_Ack);
	frmCtrl.setSecu(false);
	//if it is a data request command, then need to check if there is any packet pending.
	//In implementation, we may not have enough time to check if packets pending. If this is the case,
	//then the pending flag in the ack. should be set to 1, and then send a zero-length data packet
	//if later it turns out there is no packet actually pending.
	//In simulation, we assume having enough time to determine the pending status -- so zero-length packet will never be sent.
	//(refer to page 155, line 46-50)
	if ((origFrmType == Const.defFrmCtrl_Type_MacCmd)		//command packet
	&& (wph1.MSDU_CmdType == 0x04))			//data request command
	{
		i = TRANSACLINK.updateTransacLink(Def.tr_oper_est,/* & */transacLink1,/* & */transacLink2,frmCtrl.srcAddrMode,wph1.MHR_SrcAddrInfo.addr_64);
		frmCtrl.setFrmPending(i==0);
	}
	else
		frmCtrl.setFrmPending(false);
	frmCtrl.setAckReq(false);
	frmCtrl.setIntraPan(intraPan);
	frmCtrl.setDstAddrMode(Const.defFrmCtrl_AddrModeNone);
	frmCtrl.setSrcAddrMode(Const.defFrmCtrl_AddrModeNone);
	wph2.MHR_FrmCtrl = frmCtrl.FrmCtrl;
	wph2.MHR_BDSN = wph1.MHR_BDSN;	//copy the SN from Data/MacCmd packet
	wph2.uid = wph1.uid;			//for debug
	//wph2.MFR_FCS

	ch2.uid_ = 0;
	ch2.ptype_ = Packet_t.PT_MAC;
	ch2.iface_ = -2;
	ch2.error_ = 0;
	ch2.size_ = 5;
	ch2.uid_ = ch1.uid();		//copy the uid

	ch2.next_hop_ = Trace.p802_15_4macDA(ack);	//nam needs the nex_hop information
	Trace.p802_15_4hdrACK(ack);

        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
              System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") before alloc txAck:\n\t\t txBeacon\t= " + txBeacon + " \n\t\ttxAck   \t= " + txAck + " \n\t\ttxBcnCmd\t= " + txBcnCmd + " \n\t\ttxBcnCmd2\t= " + txBcnCmd2 + " \n\t\ttxData  \t= " + txData);

	assert(txAck == null);		//it's impossilbe to receive the second packet before
				//the Ack has been sent out.
	txAck = ack;
    }
    
    private void constructMPDU(int msduLength,MacMessage_802_15_4 msdu, int FrmCtrl,byte BDSN,panAddrInfo DstAddrInfo,
			    panAddrInfo SrcAddrInfo,int SuperSpec,byte CmdType,int FCS)
    {
        hdr_lrwpan wph = msdu.HDR_LRWPAN();
	hdr_cmn ch = msdu.HDR_CMN();
	FrameCtrl frmCtrl = new FrameCtrl();

	//fill out fields
	wph.MHR_FrmCtrl = (int)FrmCtrl;
	if (!wph.setSN)		
		wph.MHR_BDSN = BDSN;
	else
		wph.setSN = false;	
	if (wph.uid == 0) // !wph.uid
		wph.uid = Mac802_15_4Impl.DBG_UID++;
	wph.MHR_DstAddrInfo = DstAddrInfo;
	wph.MHR_SrcAddrInfo = SrcAddrInfo;
	wph.MSDU_SuperSpec = (int)SuperSpec;
	wph.MSDU_CmdType = CmdType;
	wph.MFR_FCS = FCS;

	//update packet length
        if (Def.DEBUG802_15_4_packetsize && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
        {
            System.out.println("[" + JistAPI.getTime() + "][#" + localAddr + "][Mac802_15_4.constructMPDU] - payload size (bytes): " + ch.size_);
            System.out.println("[" + JistAPI.getTime() + "][#" + localAddr + "][Mac802_15_4.constructMPDU] - header size (bytes): " + macHeaderLen_Bytes((int)FrmCtrl));
        }
	ch.size_ = msduLength + macHeaderLen_Bytes((int)FrmCtrl); // msduLength is the payload size, in bytes
        if (Def.DEBUG802_15_4_packetsize && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            System.out.println("[" + JistAPI.getTime() + "][#" + localAddr + "][Mac802_15_4.constructMPDU] - TOTAL (MAC) packet size (bytes): " + ch.size_);

    }
    
    private void constructCommandHeader(MacMessage_802_15_4 p,FrameCtrl frmCtrl,byte CmdType,
				   byte dstAddrMode,int dstPANId,/* IE3ADDR */ int dstAddr,
				   byte srcAddrMode,int srcPANId,/* IE3ADDR */ int srcAddr,
				   boolean secuEnable,boolean pending,boolean ackreq)
    {
        hdr_lrwpan wph = p.HDR_LRWPAN();

	frmCtrl.FrmCtrl = 0;
	frmCtrl.setFrmType(Const.defFrmCtrl_Type_MacCmd);
	frmCtrl.setDstAddrMode(dstAddrMode);
	wph.MHR_DstAddrInfo.panID = dstPANId;
	wph.MHR_DstAddrInfo.addr_64 = dstAddr;
	hdr_src((char[])p.HDR_MAC().serialize(),((int)localAddr.hashCode()));
	if (dstAddr == 0xffff)
		//hdr_dst((char[])p.HDR_MAC().serialize(),Def.MAC_BROADCAST);
                 hdr_dst(p.HDR_MAC(), Def.MAC_BROADCAST);
        
	else
		//hdr_dst((char[])p.HDR_MAC().serialize(),dstAddr);
                hdr_dst(p.HDR_MAC(), dstAddr);
        frmCtrl.setSrcAddrMode(srcAddrMode);
	wph.MHR_SrcAddrInfo.panID = srcPANId;
	wph.MHR_SrcAddrInfo.addr_64 = srcAddr;
	frmCtrl.setSecu(secuEnable);
	frmCtrl.setFrmPending(pending);
	frmCtrl.setAckReq(ackreq);

	p.HDR_CMN().ptype_ = Packet_t.PT_MAC;

	//for trace purpose
	p.HDR_CMN().next_hop_ = Trace.p802_15_4macDA(p);		//nam needs the nex_hop information
	Trace.p802_15_4hdrCommand(p,CmdType);
    }
    
     private void log(MacMessage_802_15_4 p)
    {
        // OLIVER: no logging
        //logtarget_.recv(p, (Handler) 0);
    }
    
    private void resetCounter(int dst)
    {
        if (txBcnCmd != null)
        if (Trace.p802_15_4macDA(txBcnCmd) == dst)
		numBcnCmdRetry = 0;

	if (txBcnCmd2 != null)
	if (Trace.p802_15_4macDA(txBcnCmd2) == dst)
		numBcnCmdRetry2 = 0;

	if (txData != null)
	if (Trace.p802_15_4macDA(txData) == dst)
		numDataRetry = 0;
    }
    
    //private int command(int argc, /* char ** *//*String[] argv*/) // ??? const*char*const // TCL configuration
    /*{
        	if (argc == 3)
	{
		//if (strcmp(argv[1], "log-target") == 0)
                if (argv[1].equals("log-target"))
		{
			logtarget_ = (NsObject) TclObject.lookup(argv[2]);
			if(logtarget_ == 0)
				return TCL_ERROR;
			return TCL_OK;
		}
	}

	if (strcmp(argv[1], "NodeClr") == 0)
	{
		changeNodeColor(((double)JistAPI.getTime()/Constants.SECOND),argv[2]);
		return (TCL_OK);
	}

	if (strcmp(argv[1], "NodeLabel") == 0)
	{
		char[] label = new label[81];
		int i;
		strcpy(label,"\"");
		strcat(label,argv[2]);
		i = 3;
		while (i < argc)
		{
			if (strlen(label) + strlen(argv[i]) < 78)
			{
				strcat(label," ");
				strcat(label,argv[i]);
			}
			else
				break;
			i++;
		}
		strcat(label,"\"");
		nam.changeLabel(((double)JistAPI.getTime()/Constants.SECOND),label);
		return (TCL_OK);
	}

	if (strcmp(argv[1], "node-down") == 0)
	{
		chkAddNFailLink(((int)localAddr.hashCode()));
		changeNodeColor(((double)JistAPI.getTime()/Constants.SECOND),Nam802_15_4.def_NodeFail_clr);
		if (txAck)
		{
			MacMessage_802_15_4.free(txAck);
			txAck = null;
		}
		if (txBcnCmd != null)
		{
			freePkt(txBcnCmd != null);
			txBcnCmd = null;
		}
		if (txBcnCmd2 != null)
		{
			freePkt(txBcnCmd2 != null);
			txBcnCmd2 = null;
		}
		if (txData != null)
		{
			freePkt(txData != null);
			txData = 0;
		}
		if (phyEntity.rxPacket())
			HDR_CMN(phyEntity.rxPacket()).error() = 1;
		init(true);		//reset
		return (TCL_OK);
	}
	if (strcmp(argv[1], "node-up") == 0)
	{
		updateNFailLink(fl_oper_del,((int)localAddr.hashCode()));
		nam.changeBackNodeColor(((double)JistAPI.getTime()/Constants.SECOND));
		init(true);		//reset
		if (callback_)
		{
			Handler *h = callback_;
			callback_ = 0;
			h.handle((Event) 0);
		}
		return (TCL_OK);
	}

	//check if this is actually a SSCS command
	if ((argc >= 3)&&(strcmp(argv[1],"sscs") == 0))
		return sscs.command(argc,argv);

	int rt = Mac.command(argc, argv);
	
	//check if Mac.command has already populated netif_
	if (netif_)
	if (phy == null)	//only execute once
	{
		phy = (Phy802_15_4Impl)netif_;
		phyEntity.macObj(this);
		csmaca = new CsmaCA802_15_4(phy,this);
		assert(csmaca);
	}

	return rt;
    }*/
    
    
    // private void changeNodeColor(double atTime,char[] newColor,boolean save /*= true*/); // ??? char * ??? nam
    /*{
        nam.changeNodeColor(atTime,newColor,save);
	nam.changeNodeColor(atTime+0.030001,newColor,false);	
    }*/
    
    public void txBcnCmdDataHandler() {
         if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            System.out.println("[#" + localAddr+"][MAC.txBcnCmdDataHandler()]");
         
        int i;

		if (taskP.taskStatus(taskPending.TP_mlme_scan_request))
		if (txBcnCmd2 != txCsmaca)
			return;			//terminate all other transmissions (will resume afte channel scan)
	
		if (txCsmaca.HDR_LRWPAN().indirect) {
			i = TRANSACLINK.updateTransacLinkByPktOrHandle(Def.tr_oper_est,/* & */transacLink1,/* & */transacLink2,txCsmaca, (byte)0);
			if (i != 0)	{ //transaction expired
				resetTRX();
				if (txBcnCmd == txCsmaca)
					txBcnCmd = null;
				else if (txBcnCmd2 == txCsmaca)
					txBcnCmd2 = null;
				else if (txData == txCsmaca)
					txData = null;
				//MacMessage_802_15_4.free(txCsmaca);	//don't do this, since the packet will be automatically deleted when expired
				csmacaResume();
				return;
			}
		}
	
		if (txBcnCmd == txCsmaca) {
			txPkt = txBcnCmd;
			
			if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
				traceACKLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.txBcnCmdDataHandler()]" +
					" - sendDown CSMACA(sseq " + txPkt.HDR_LRWPAN().MHR_BDSN + ")");
			
			txBcnCmd.HDR_CMN().direction_ = hdr_cmn.dir_t.DOWN;
			sendDown(txBcnCmd.copy()/*, this*/);		
		}
		else if (txBcnCmd2 == txCsmaca) {
			txPkt = txBcnCmd2;
			txBcnCmd2.HDR_CMN().direction_ = hdr_cmn.dir_t.DOWN;
			
			if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
				traceACKLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.txBcnCmdDataHandler()]" +
					" - sendDown Beacon(sseq " + txData.HDR_LRWPAN().MHR_BDSN + ")");
			
			sendDown(txBcnCmd2.copy()/*, this*/);		
		}
		else if (txData == txCsmaca) {
			txPkt = txData;
			txData.HDR_CMN().direction_ = hdr_cmn.dir_t.DOWN;
			
			if (debugNodeID == -1 || debugNodeID == localAddr.hashCode()) 
				traceACKLogger.debug("[" +JistAPI.getTime() + "]["+localAddr+"][MAC.txBcnCmdDataHandler()]" +
					" - sendDown DATA(sseq " + txPkt.HDR_LRWPAN().MHR_BDSN + ") to node " + txData.HDR_CMN().next_hop_);			
	        
			sendDown(txData.copy()/*, this*/);		
		}
    }
    
    public void IFSHandler()
    {
        String __FUNCTION__ = "IFSHandler";
        hdr_lrwpan wph = null;
	hdr_cmn ch;
	FrameCtrl frmCtrl = new FrameCtrl();
	MacMessage_802_15_4 pendPkt = null;
	MACenum status = MACenum.m_SUCCESS; // OLIVER: default value
	int i = 0;

	assert(rxData != null || rxCmd != null);

	if (rxCmd != null)
	{
		wph = rxCmd.HDR_LRWPAN();
		frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
		frmCtrl.parse();

		if (wph.MSDU_CmdType == 0x01)		//Association request
			sscs.MLME_ASSOCIATE_indication(wph.MHR_SrcAddrInfo.addr_64,wph.MSDU_Payload[0],frmCtrl.secu,(byte)0);	//ACL ignored in simulation
		else if (wph.MSDU_CmdType == 0x02)	//Association response
		{
			//status = (/* * */(MACenum /* * */)(wph.MSDU_Payload + 2));

//(??REFLECTION??)                        status = (MACenum)MSDU_Util.loadFrom(wph.MSDU_Payload, 2, MACenum.class); 
			//save the int address (if association successful)
			if (status == MACenum.m_SUCCESS)
				//mpib.macShortAddress = /* * */((int /* * */)wph.MSDU_Payload);
//(??REFLECTION??)                        mpib.macShortAddress = (int)MSDU_Util.loadFrom(wph.MSDU_Payload, 0, int.class);
			dispatch(PHYenum.p_SUCCESS,__FUNCTION__,PHYenum.p_SUCCESS,status);
              }
		else if (wph.MSDU_CmdType == 0x04)	//Data request
		{
			//Continue to send pending packet (an ack. already sent).
			//In implementation, we may not have enough time to check if packets pending. If this is the case,
			//then the pending flag in the ack. should be set to 1, and then send a zero-length data packet
			//if later it turns out there is no packet actually pending.
			//In simulation, we assume having enough time to determine the pending status -- so zero-length packet will never be sent.
			//(refer to page 155, line 46-50)
			i = TRANSACLINK.updateTransacLink(Def.tr_oper_EST, /* & */transacLink1,/* & */transacLink2,frmCtrl.srcAddrMode,wph.MHR_SrcAddrInfo.addr_64);
			if (i != 0)
			{
				i = TRANSACLINK.updateTransacLink(Def.tr_oper_est,/* & */transacLink1,/* & */transacLink2,frmCtrl.srcAddrMode,wph.MHR_SrcAddrInfo.addr_64);
				i = (i == 0)?1:0;
			}
			else	//more than one packet pending
			{
				i = 2;
			}
			if (i > 0)	//packet(s) pending
			{
				pendPkt = TRANSACLINK.getPktFrTransacLink(/* & */transacLink1,frmCtrl.srcAddrMode,wph.MHR_SrcAddrInfo.addr_64);
				wph = pendPkt.HDR_LRWPAN();
				wph.indirect = true;
				frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
				frmCtrl.parse();
				frmCtrl.setFrmPending(i>1);		//more packet pending?
				wph.MHR_FrmCtrl = frmCtrl.FrmCtrl;
				pendPkt.HDR_CMN().direction_ = hdr_cmn.dir_t.DOWN;
				if (frmCtrl.frmType == Const.defFrmCtrl_Type_MacCmd)
				{
					if (txBcnCmd == pendPkt)	//it's being processed
					{
						//MacMessage_802_15_4.free(rxCmd);	//we logged the command packet before, here just free it
                                                rxCmd = null;
						return;
					}
                                        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                            System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") before assign txBcnCmd:\n\t\t txBeacon\t= " + txBeacon + " \n\t\ttxAck   \t= " + txAck + " \n\t\ttxBcnCmd\t= " + txBcnCmd + " \n\t\ttxBcnCmd2\t= " + txBcnCmd2 + " \n\t\ttxData  \t= " + txData);

					assert(txBcnCmd == null);		//we couldn't receive the data request command if we are processing txBcnCmd
					//txBcnCmd = pendPkt.refcopy();	keeps track of the number of packets references this
                                        txBcnCmd = pendPkt;
					waitBcnCmdAck = false;
					numBcnCmdRetry = 0;

				}
				else if (frmCtrl.frmType == Const.defFrmCtrl_Type_Data)
				{
					if (txData == pendPkt)		//it's being processed
					{
						//MacMessage_802_15_4.free(rxCmd);	//we logged the command packet before, here just free it
						rxCmd = null;
						return;
					}
                                        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                            System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") before assign txData:\n\t\t txBeacon\t= " + txBeacon + " \n\t\ttxAck   \t= " + txAck + " \n\t\ttxBcnCmd\t= " + txBcnCmd + " \n\t\ttxBcnCmd2\t= " + txBcnCmd2 + " \n\t\ttxData  \t= " + txData);

					assert(txData == null);		//we couldn't receive the data request command if we are processing txData
					//txData = pendPkt.refcopy();	
                                        txData = pendPkt;
					waitDataAck = false;
					numDataRetry = 0;
				}
				//there are two ways to transmit the pending packet for the first time (refer to page 156), w/o or w/ CSMA-CA,
				if (canProceedWOcsmaca(pendPkt))
				{
					//change task field "frFunc"
					if(taskP.taskStatus(taskPending.TP_mcps_data_request))
					{
						 if (taskP.taskFrFunc(taskPending.TP_mcps_data_request).equals("csmacaCallBack"))
						 	taskP.setTaskFrFunc(taskPending.TP_mcps_data_request,"PD_DATA_confirm");
					}
					else if (taskP.taskStatus(taskPending.TP_mlme_associate_response))
					{
						 if (taskP.taskFrFunc(taskPending.TP_mlme_associate_response).equals("csmacaCallBack"))
						 	taskP.setTaskFrFunc(taskPending.TP_mlme_associate_response,"PD_DATA_confirm");
						 //else		//other commands using indirect transmission (may  be none): TBD
					}
					txCsmaca = pendPkt;
					plme_set_trx_state_request(PHYenum.p_TX_ON);
				}
				else
				{
					csmacaResume();
				}
			}
			//else		//may need to send a zero-length packet in implementation
		}
		else if (wph.MSDU_CmdType == 0x08)	//Coordinator realignment
		{
			//mpib.macPANId = /* * */((int /* * */)wph.MSDU_Payload);
//(??REFLECTION??)                        mpib.macPANId = (int)MSDU_Util.loadFrom(wph.MSDU_Payload, 0, int.class);
			//mpib.macCoordShortAddress = /* * */((int /* * */)wph.MSDU_Payload + 2);
//(??REFLECTION??)                        mpib.macCoordShortAddress = (int)MSDU_Util.loadFrom(wph.MSDU_Payload, 2, int.class);
			tmp_ppib.phyCurrentChannel = wph.MSDU_Payload[4];
			phyEntity.PLME_SET_request(PPIBAenum.phyCurrentChannel,/* & */tmp_ppib);
			//mpib.macShortAddress = /* * */((int /* * */)wph.MSDU_Payload + 5);
//(??REFLECTION??)                        mpib.macShortAddress = (int)MSDU_Util.loadFrom(wph.MSDU_Payload, 5, int.class);
			dispatch(PHYenum.p_SUCCESS,__FUNCTION__, PHYenum.p_SUCCESS, MACenum.m_SUCCESS);
		}
		//MacMessage_802_15_4.free(rxCmd);	//we logged the command packet before, here just free it
		rxCmd = null;
	}
	else if (rxData != null)
	{
		wph = rxData.HDR_LRWPAN();
		ch = rxData.HDR_CMN();
		frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
		frmCtrl.parse();

		if (taskP.taskStatus(taskPending.TP_mlme_poll_request))
			dispatch(PHYenum.p_SUCCESS,__FUNCTION__,PHYenum.p_SUCCESS,status);
		//else	//do nothing
		//the data waiting timer in data polling expired and the upper layer confirmed
		//with a status NO_DATA -- but we see no reason not to continue passing this data
		//packet to upper layer (note that the ack. has already been sent to data source -- 
		//we shouldn't drop the data packet here).

		//strip off the MAC sublayer header
		ch.size_ -= macHeaderLen_Bytes(wph.MHR_FrmCtrl);
		MCPS_DATA_indication(frmCtrl.srcAddrMode,wph.MHR_SrcAddrInfo.panID,wph.MHR_SrcAddrInfo.addr_64,
			     	frmCtrl.dstAddrMode,wph.MHR_DstAddrInfo.panID,wph.MHR_DstAddrInfo.addr_64,
			     	(byte)ch.size(),rxData,wph.ppduLinkQuality,
			     	wph.SecurityUse,wph.ACLEntry);
		rxData = null;
	}
    }
    
    public void backoffBoundHandler() {
        if (!beaconWaiting)
			if (txAck != null) {		//<txAck> may have been cancelled by <macBeaconRxTimer>
	             if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
	                 System.out.println("[" + /* __FILE__ + */"."+ /* __FUNCTION__ + */ "][" + JistAPI.getTime() /* ((double)JistAPI.getTime()/Constants.SECOND) */ + "]<MAC>(node " + myNode.getID() /* ((int)localAddr.hashCode()) */ + ") transmit M_ACK to " + Trace.p802_15_4macDA(txAck) + ": SN = " + txAck.HDR_LRWPAN().MHR_BDSN + ", uid = " + txAck.HDR_CMN().uid() + ", mac_uid = " + txAck.HDR_LRWPAN().uid);
	
	            txPkt = txAck; 
	            txAck.HDR_CMN().direction_ = hdr_cmn.dir_t.DOWN;
	            sendDown(/*txAck.refcopy()*/ txAck.copy()/*, this */);
			}
    }
    
    /* the following not members in NS-2 but #define */
    public void plme_set_trx_state_request(PHYenum state) { // ??? datatype
        String __FILE__ = "Mac802_15_4Impl";
        String __FUNCTION__ = "plme_set_trx_state_request";
        int __LINE__ = -1;
        set_trx_state_request(state,__FILE__,__FUNCTION__,__LINE__);
    }
    
    public void resetTRX() {
        String __FILE__ = "Mac802_15_4Impl";
        String __FUNCTION__ = "resetTRX";
        int __LINE__ = -1;
        reset_TRX(__FILE__,__FUNCTION__,__LINE__);
    }


	public void timeout(long timerID) {
		switch((int)timerID) {
			case CsmaCA802_15_4.CSMACA_BACKOFF_TIMER:      csmaca_backoffHandler();  break;
			case CsmaCA802_15_4.CSMACA_BEACON_OTHER_TIMER: csmaca_bcnOtherHandler(); break;
			case CsmaCA802_15_4.CSMACA_DEFER_CCAT_TIMER:   csmaca_deferCCAHandler(); break;
		}
	}
};
/* END *** MAC *** */

