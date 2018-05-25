/*
 * Driver.java
 *
 * Created on August 18, 2005, 12:02 PM
 */

/**
 *
 * @author  Oliviu Ghica
 */
package sidnet.core.gui;

public class Driver {
    // ** Driver class **
    //
    //    Contains all the set-up parameters of the simulation, including
    //      - graphic object dimensions
    //      - sensor nodes characteristics
    //      - simulation time, resolution, and so on ...
    
    // Graphics Parameters
    public static final int AREA_WIDTH                  = 600;
    public static final int AREA_HEIGHT                 = 600;
    public static final int ENERGY_AREA_WIDTH           = 300;
    public static final int ENERGY_AREA_HEIGHT          = 300;
    public static final int WINDOW_WIDTH                = 725;
    public static final int WINDOW_HEIGHT               = 645;
    public static final int PERCENT_PROGRESS_BAR_WIDTH  = AREA_WIDTH;
    public static final int PERCENT_PROGRESS_BAR_HEIGHT = 20;
    public static final int EVENT_BAR_WIDTH             = AREA_WIDTH;
    public static final int EVENT_BAR_HEIGHT            = 20;
    
    // Setting up Simulations Params
    static final int    N                        = 1000;     // The number of sensors to be deployed;
    static final int    TIME_RESOLUTION          = 1;       // [miliseconds] - used by the Event-Driven Core
    static final double SIMULATION_END_TIME      = 1000000; // [miliseconds]
    static       int    SIMULATION_MODE;                    // '1' RUN all protocols
                                                            // '0' RUN only selected protocol
    static final int    QUERIES_NO               = 50;
    static final int    QUERY_INTERVAL           = 2000;    // [ms]       
    static int          ROUTING_PROTOCOL_TYPE;              // '1' - RandomizedRouting Protocol
                                                            // '2' - ShortestPathRandomizedRouting Protocol
                                                            // '3' - SpreadConstrainedRoute Protocol
                                                            // '4' - DD (Directed Diffusion)
    static final int   ROUTING_PROTOCOLS_COUNT   = 3;      // The number of routing protocols implemented yet.
    static int          MAC_PROTOCOL_TYPE;                  // '1' - Dummy Protocol - does mostly nothing just purely sends packets
    
    // RndRouting Specific Parameters
    static final int DEADLIST_EXPIRE_PERIOD       = 10000;  // [ms]
    
    // Setting up the Sensor Node Params (following are from the MICA MPR300CB
    public static double ProcessorCurrentDraw_ActiveMode = 5.5;    // [mA]
    public static double ProcessorCurrentDraw_SleepMode  = 0.02;   // [mA]
    public static double RadioCurrentDraw_TransmitMode   = 12000;  // [mA] // ????? it was 12
    public static double RadioCurrentDraw_ReceiveMode    = 1.8;    // [mA]
    public static double RadioCurrentDraw_ListenMode     = 1.8;    // [mA]
    public static double RadioCurrentDraw_SleepMode      = 0.001;  // [mA]
    public static double VOLTAGE                         = 3;      // [Volts]
    public static int    NUMBER_OF_BATTERIES             = 2;
    public static double BATTERY_CAPACITY                = 28;     // [mAh] each battery
    public static double RADIO_RANGE                     = 40;     // [m]  70
    public static int    RADIO_DATA_RATE                 = 40000;  // [bits/sec]
    public static int    PACKET_LENGTH                   = 30*8;   // [bits] = [# bytes * 8]
    
    // Sensing parameters
    public static int lowerVal                           = 80;
    public static int upperVal                           = 100;
    public static int interval                           = 10000;    // Interval of time in [ms] to modify the sensing value
    
    
    // For SpreadConstrainedRoutingProtocol only
    // - Bezier parameters
    static final double[] xSetLeft = {300, 800, 300, 300};
    static final double[] ySetLeft = {500, 500, 250, 100};
    static final double[] xSetRight = {300, 800, 300, 300};
    static final double[] ySetRight = {500, -200, 250, 100};
    static final int degree    = 4;
    static int maxHopCount = 10;
    
    
    /** Creates a new instance of Driver */
    public Driver(int SimulationMode, int RoutingProtocolType, int MACProtocolType) {
        SIMULATION_MODE       = SimulationMode;
        ROUTING_PROTOCOL_TYPE = RoutingProtocolType;
        MAC_PROTOCOL_TYPE     = MACProtocolType;
    }
    
    public void setProtocol(int RoutingProtocolType)
    {
        ROUTING_PROTOCOL_TYPE = RoutingProtocolType;
    }
}
