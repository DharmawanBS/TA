/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

/**
 *
 * @author Oliver
 */
/*#define plme_set_trx_state_request(state) \
set_trx_state_request(state,__FILE__,__FUNCTION__,__LINE__)
#define resetTRX() \
reset_TRX(__FILE__,__FUNCTION__,__LINE__)*/




//task pending (callback)
/*#define TP_mcps_data_request		1
#define TP_mlme_associate_request	2
#define TP_mlme_associate_response	3
#define TP_mlme_disassociate_request	4
#define TP_mlme_orphan_response		5
#define TP_mlme_reset_request		6
#define TP_mlme_rx_enable_request	7
#define TP_mlme_scan_request		8
#define TP_mlme_start_request		9
#define TP_mlme_sync_request		10
#define TP_mlme_poll_request		11
#define TP_CCA_csmaca			12
#define TP_RX_ON_csmaca			13*/

class taskPending
{
    public static final byte TP_mcps_data_request = 1;
    public static final byte TP_mlme_associate_request = 2;
    public static final byte TP_mlme_associate_response = 3;
    public static final byte TP_mlme_disassociate_request = 4;
    public static final byte TP_mlme_orphan_response = 5;
    public static final byte TP_mlme_reset_request = 6;
    public static final byte TP_mlme_rx_enable_request = 7;
    public static final byte TP_mlme_scan_request = 8;
    public static final byte TP_mlme_start_request = 9;
    public static final byte TP_mlme_sync_request = 10;
    public static final byte TP_mlme_poll_request = 11;
    public static final byte TP_CCA_csmaca = 12;
    public static final byte TP_RX_ON_csmaca = 13;
    public taskPending() {
    	init();
    }
    public void init()
    {
        mcps_data_request = false;
        mcps_data_request_STEP = 0;
        mlme_associate_request = false;
        mlme_associate_request_STEP = 0;
        mlme_associate_response = false;
        mlme_associate_response_STEP = 0;
        mlme_disassociate_request = false;
        mlme_disassociate_request_STEP = 0;
        mlme_orphan_response = false;
        mlme_orphan_response_STEP = 0;
        mlme_reset_request = false;
        mlme_reset_request_STEP = 0;
        mlme_rx_enable_request = false;
        mlme_rx_enable_request_STEP = 0;
        mlme_scan_request = false;
        mlme_scan_request_STEP = 0;
        mlme_start_request = false;
        mlme_start_request_STEP = 0;
        mlme_sync_request = false;
        mlme_sync_request_STEP = 0;
        mlme_sync_request_tracking = false;
        mlme_poll_request = false;
        mlme_poll_request_STEP = 0;
        CCA_csmaca = false;
        CCA_csmaca_STEP = 0;
        RX_ON_csmaca = false;
        RX_ON_csmaca_STEP = 0;
    }

    public boolean taskStatus(byte task)
    {
        switch (task)
        {
                case TP_mcps_data_request:
                        return mcps_data_request;
                case TP_mlme_associate_request:
                        return mlme_associate_request;
                case TP_mlme_associate_response:
                        return mlme_associate_response;
                case TP_mlme_disassociate_request:
                        return mlme_disassociate_request;
                case TP_mlme_orphan_response:
                        return mlme_orphan_response;
                case TP_mlme_reset_request:
                        return mlme_reset_request;
                case TP_mlme_rx_enable_request:
                        return mlme_rx_enable_request;
                case TP_mlme_scan_request:
                        return mlme_scan_request;
                case TP_mlme_start_request:
                        return mlme_start_request;
                case TP_mlme_sync_request:
                        return mlme_sync_request;
                case TP_mlme_poll_request:
                        return mlme_poll_request;
                case TP_CCA_csmaca:
                        return CCA_csmaca;
                case TP_RX_ON_csmaca:
                        return RX_ON_csmaca;
                default:
                        assert(false);
                        return false; // Not in NS-2
        }
    }
    
    // Not in NS-2
    public void setTaskStatus(byte task, boolean value)
    {
           switch (task)
        {
                case TP_mcps_data_request:
                        mcps_data_request = value; break;
                case TP_mlme_associate_request:
                        mlme_associate_request = value; break;
                case TP_mlme_associate_response:
                        mlme_associate_response = value; break;
                case TP_mlme_disassociate_request:
                        mlme_disassociate_request = value; break;
                case TP_mlme_orphan_response:
                        mlme_orphan_response = value; break;
                case TP_mlme_reset_request:
                        mlme_reset_request = value; break;
                case TP_mlme_rx_enable_request:
                        mlme_rx_enable_request = value; break;
                case TP_mlme_scan_request:
                        mlme_scan_request = value; break;
                case TP_mlme_start_request:
                        mlme_start_request = value; break;
                case TP_mlme_sync_request:
                        mlme_sync_request = value; break;
                case TP_mlme_poll_request:
                        mlme_poll_request = value; break;
                case TP_CCA_csmaca:
                        CCA_csmaca = value; break;
                case TP_RX_ON_csmaca:
                        RX_ON_csmaca = value; break;
                default:
                        assert(false);
        }
    }
    
    // Not in NS-2
     public void taskStepIncrement(byte task)
    {
        switch (task)
        {
                case TP_mcps_data_request:
                        mcps_data_request_STEP ++; break;
                case TP_mlme_associate_request:
                        mlme_associate_request_STEP++; break;
                case TP_mlme_associate_response:
                        mlme_associate_response_STEP++; break;
                case TP_mlme_disassociate_request:
                        mlme_disassociate_request_STEP++; break;
                case TP_mlme_orphan_response:
                        mlme_orphan_response_STEP++; break;
                case TP_mlme_reset_request:
                        mlme_reset_request_STEP++; break;
                case TP_mlme_rx_enable_request:
                        mlme_rx_enable_request_STEP++; break;
                case TP_mlme_scan_request:
                        mlme_scan_request_STEP++; break;
                case TP_mlme_start_request:
                        mlme_start_request_STEP++; break;
                case TP_mlme_sync_request:
                        mlme_sync_request_STEP++; break;
                case TP_mlme_poll_request:
                        mlme_poll_request_STEP++; break;
                case TP_CCA_csmaca:
                        CCA_csmaca_STEP++; break;
                case TP_RX_ON_csmaca:
                        RX_ON_csmaca_STEP++; break;
                default:
                        assert(false);
        }
    }
     
    // SIDnet version
    public void setTaskStep(byte task, byte value)
    {
        switch (task)
        {
                case TP_mcps_data_request:
                        mcps_data_request_STEP = value; break;
                case TP_mlme_associate_request:
                       mlme_associate_request_STEP = value; break;
                case TP_mlme_associate_response:
                        mlme_associate_response_STEP = value; break;
                case TP_mlme_disassociate_request:
                        mlme_disassociate_request_STEP = value; break;
                case TP_mlme_orphan_response:
                        mlme_orphan_response_STEP = value; break;
                case TP_mlme_reset_request:
                        mlme_reset_request_STEP = value; break;
                case TP_mlme_rx_enable_request:
                        mlme_rx_enable_request_STEP = value; break;
                case TP_mlme_scan_request:
                        mlme_scan_request_STEP = value; break;
                case TP_mlme_start_request:
                        mlme_start_request_STEP = value; break;
                case TP_mlme_sync_request:
                        mlme_sync_request_STEP = value; break;
                case TP_mlme_poll_request:
                        mlme_poll_request_STEP = value; break;
                case TP_CCA_csmaca:
                        CCA_csmaca_STEP = value; break;
                case TP_RX_ON_csmaca:
                        RX_ON_csmaca_STEP = value; break;
                default:
                        assert(false);
        }
    } 

    public byte taskStep(byte task)
    {
        switch (task)
        {
                case TP_mcps_data_request:
                        return mcps_data_request_STEP;
                case TP_mlme_associate_request:
                        return mlme_associate_request_STEP;
                case TP_mlme_associate_response:
                        return mlme_associate_response_STEP;
                case TP_mlme_disassociate_request:
                        return mlme_disassociate_request_STEP;
                case TP_mlme_orphan_response:
                        return mlme_orphan_response_STEP;
                case TP_mlme_reset_request:
                        return mlme_reset_request_STEP;
                case TP_mlme_rx_enable_request:
                        return mlme_rx_enable_request_STEP;
                case TP_mlme_scan_request:
                        return mlme_scan_request_STEP;
                case TP_mlme_start_request:
                        return mlme_start_request_STEP;
                case TP_mlme_sync_request:
                        return mlme_sync_request_STEP;
                case TP_mlme_poll_request:
                        return mlme_poll_request_STEP;
                case TP_CCA_csmaca:
                        return CCA_csmaca_STEP;
                case TP_RX_ON_csmaca:
                        return RX_ON_csmaca_STEP;
                default:
                        assert(false);
                        return (byte)0; // Not in NS-2
        }
    }
    
    public void setTaskFrFunc(byte task, String frFunc)
    {
         switch (task)
        {
                case TP_mcps_data_request:
                        mcps_data_request_frFunc = new String(frFunc); break;
                case TP_mlme_associate_request:
                        mlme_associate_request_frFunc = new String(frFunc); break;
                case TP_mlme_associate_response:
                        mlme_associate_response_frFunc = new String(frFunc); break;
                case TP_mlme_disassociate_request:
                        mlme_disassociate_request_frFunc = new String(frFunc); break;
                case TP_mlme_orphan_response:
                        mlme_orphan_response_frFunc = new String(frFunc); break;
                case TP_mlme_reset_request:
                        mlme_reset_request_frFunc = new String(frFunc); break;
                case TP_mlme_rx_enable_request:
                        mlme_rx_enable_request_frFunc = new String(frFunc); break;
                case TP_mlme_scan_request:
                        mlme_scan_request_frFunc = new String(frFunc); break;
                case TP_mlme_start_request:
                        mlme_start_request_frFunc = new String(frFunc); break;
                case TP_mlme_sync_request:
                        mlme_sync_request_frFunc = new String(frFunc); break;
                case TP_mlme_poll_request:
                        mlme_poll_request_frFunc = new String(frFunc); break;
                default:
                        assert(false);
        }
    }

    public String taskFrFunc(byte task)
    {
        switch (task)
        {
                case TP_mcps_data_request:
                        return mcps_data_request_frFunc;
                case TP_mlme_associate_request:
                        return mlme_associate_request_frFunc;
                case TP_mlme_associate_response:
                        return mlme_associate_response_frFunc;
                case TP_mlme_disassociate_request:
                        return mlme_disassociate_request_frFunc;
                case TP_mlme_orphan_response:
                        return mlme_orphan_response_frFunc;
                case TP_mlme_reset_request:
                        return mlme_reset_request_frFunc;
                case TP_mlme_rx_enable_request:
                        return mlme_rx_enable_request_frFunc;
                case TP_mlme_scan_request:
                        return mlme_scan_request_frFunc;
                case TP_mlme_start_request:
                        return mlme_start_request_frFunc;
                case TP_mlme_sync_request:
                        return mlme_sync_request_frFunc;
                case TP_mlme_poll_request:
                        return mlme_poll_request_frFunc;
                default:
                        assert(false);
                        return ""; // Not in NS-2
        }
    }

    //----------------
    boolean	mcps_data_request;
    byte	mcps_data_request_STEP;
    //char[]	mcps_data_request_frFunc = new char[81];
    String      mcps_data_request_frFunc = new String();
    byte	mcps_data_request_TxOptions;
    MacMessage_802_15_4	mcps_data_request_pendPkt;
    //----------------
    boolean	mlme_associate_request;
    byte	mlme_associate_request_STEP;
    //char[]	mlme_associate_request_frFunc = new char[81];
    String      mlme_associate_request_frFunc = new String();
    boolean	mlme_associate_request_SecurityEnable;
    byte	mlme_associate_request_CoordAddrMode;
    MacMessage_802_15_4	mlme_associate_request_pendPkt;
    //----------------
    boolean	mlme_associate_response;
    byte	mlme_associate_response_STEP;
    //char[]	mlme_associate_response_frFunc = new char[81];
    String      mlme_associate_response_frFunc = new String();
    /* IE3ADDR */ int	mlme_associate_response_DeviceAddress;
    MacMessage_802_15_4	mlme_associate_response_pendPkt;
    //----------------
    boolean	mlme_disassociate_request;
    byte	mlme_disassociate_request_STEP;
    //char[]	mlme_disassociate_request_frFunc = new char[81];
    String      mlme_disassociate_request_frFunc = new String();
    boolean	mlme_disassociate_request_toCoor;
    MacMessage_802_15_4	mlme_disassociate_request_pendPkt;
    //----------------
    boolean	mlme_orphan_response;
    byte	mlme_orphan_response_STEP;
    //char[]	mlme_orphan_response_frFunc = new char[81];
    String mlme_orphan_response_frFunc = new String();
    
    /* IE3ADDR */ int	mlme_orphan_response_OrphanAddress;
    //----------------
    boolean	mlme_reset_request;
    byte	mlme_reset_request_STEP;
    //char[]	mlme_reset_request_frFunc = new char[81];
    String      mlme_reset_request_frFunc = new String();
    boolean	mlme_reset_request_SetDefaultPIB;
    //----------------
    boolean	mlme_rx_enable_request;
    byte	mlme_rx_enable_request_STEP;
    //char[]	mlme_rx_enable_request_frFunc = new char[81];
    String      mlme_rx_enable_request_frFunc = new String();
    
    int	mlme_rx_enable_request_RxOnTime;
    int	mlme_rx_enable_request_RxOnDuration;
    double	mlme_rx_enable_request_currentTime;
    //----------------
    boolean	mlme_scan_request;
    byte	mlme_scan_request_STEP;
    //char[]	mlme_scan_request_frFunc = new char[81];
    String      mlme_scan_request_frFunc = new String();
    byte	mlme_scan_request_ScanType;
    byte	mlme_scan_request_orig_macBeaconOrder;
    byte	mlme_scan_request_orig_macBeaconOrder2;
    byte	mlme_scan_request_orig_macBeaconOrder3;
    int 	mlme_scan_request_orig_macPANId;
    int	mlme_scan_request_ScanChannels;
    byte	mlme_scan_request_ScanDuration;
    byte	mlme_scan_request_CurrentChannel;
    byte	mlme_scan_request_ListNum;
    byte[]	mlme_scan_request_EnergyDetectList = new byte[27];
    PAN_ELE[]	mlme_scan_request_PANDescriptorList = new PAN_ELE[27];
    //----------------
    boolean	mlme_start_request;
    byte	mlme_start_request_STEP;
    //char[]	mlme_start_request_frFunc = new char[81];
    String      mlme_start_request_frFunc = new String();
    byte	mlme_start_request_BeaconOrder;
    byte	mlme_start_request_SuperframeOrder;
    boolean	mlme_start_request_BatteryLifeExtension;
    boolean	mlme_start_request_SecurityEnable;
    boolean	mlme_start_request_PANCoordinator;
    int 	mlme_start_request_PANId;
    byte	mlme_start_request_LogicalChannel;
    //----------------
    boolean	mlme_sync_request;
    byte	mlme_sync_request_STEP;
    //char[]	mlme_sync_request_frFunc = new char[81];
    String      mlme_sync_request_frFunc = new String();
    byte	mlme_sync_request_numSearchRetry;
    boolean	mlme_sync_request_tracking;
    //----------------
    boolean	mlme_poll_request;
    byte	mlme_poll_request_STEP;
    //char[]	mlme_poll_request_frFunc = new char[81];
    String      mlme_poll_request_frFunc = new String();
    byte	mlme_poll_request_CoordAddrMode;
    int 	mlme_poll_request_CoordPANId;
    /* IE3ADDR */ int	mlme_poll_request_CoordAddress;
    boolean	mlme_poll_request_SecurityEnable;
    boolean	mlme_poll_request_autoRequest;
    boolean	mlme_poll_request_pending;
    //----------------
    boolean	CCA_csmaca;
    byte	CCA_csmaca_STEP;
    //----------------
    boolean	RX_ON_csmaca;
    byte	RX_ON_csmaca_STEP;
    //----------------
};
