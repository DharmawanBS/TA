/*
 * EnergyCostParameters.java
 *
 * Created on May 12, 2006, 9:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.models.energy.energyconsumptionparameters;


/**
 *
 * @author Oliviu Ghica
 */
public class EnergyConsumptionParameters {
    // CONSTANT
    private static final double C = 0.001;           // Comes as PART OF the conversion of hour in ms
    
    // Energy parameters
    private static double CPU_Processing_Cost;       // Energy drawn by an active CPU [mJ/ms]
    private static double CPU_Idling_Cost;           // Energy drawn by an idling CPU [mJ/ms]
    private static double Radio_Transmitting_Cost;   // Energy drawn to transmit [mJ/ms]
    private static double Radio_Receiving_Cost;      // Energy drawn to receive  [mJ/ms]
    private static double Radio_Listening_Cost;      // Energy drawn to listen the channel [mJ/ms]
    private static double Radio_Sleeping_Cost;       // Energy drawn to listen the channel [mJ/ms]
    private static double Sensor_Active_Cost;        // Energy drawn by a sigle measurement, on average. [mJ/ms]
    private static double Sensor_Passive_Cost;       // Energy drawn by an idling sensor [mJ/ms]
    
    /** Creates a new instance of EnergyCostParameters */
    public EnergyConsumptionParameters(ElectricParameters eParam, double voltage) {
        CPU_Processing_Cost     = C   * eParam.ProcessorCurrentDrawn_ActiveMode * voltage;
        CPU_Idling_Cost         = C   * eParam.ProcessorCurrentDrawn_SleepMode  * voltage;
        Radio_Transmitting_Cost = C   * eParam.RadioCurrentDrawn_TransmitMode   * voltage;
        Radio_Receiving_Cost    = C   * eParam.RadioCurrentDrawn_ReceiveMode    * voltage;
        Radio_Listening_Cost    = C   * eParam.RadioCurrentDrawn_ListenMode     * voltage;
        Radio_Sleeping_Cost     = C   * eParam.RadioCurrentDrawn_SleepMode      * voltage;
        Sensor_Active_Cost      = C   * eParam.SensorCurrentDrawn_ActiveMode    * voltage;
        Sensor_Passive_Cost     = C   * eParam.SensorCurrentDrawn_SleepMode     * voltage;
    }
    
    public EnergyConsumptionParameters(double CPU_Processing_Cost,
                                double CPU_Idling_Cost,
                                double Radio_Transmitting_Cost,
                                double Radio_Receiving_Cost,
                                double Radio_Listening_Cost,
                                double Radio_Sleeping_Cost,
                                double Sensor_Active_Cost,
                                double Sensor_Passive_Cost)
    {
        this.CPU_Processing_Cost     = CPU_Processing_Cost;
        this.CPU_Idling_Cost         = CPU_Idling_Cost;
        this.Radio_Transmitting_Cost = Radio_Transmitting_Cost;
        this.Radio_Receiving_Cost    = Radio_Receiving_Cost;
        this.Radio_Listening_Cost    = Radio_Listening_Cost;
        this.Radio_Sleeping_Cost     = Radio_Sleeping_Cost;
        this.Sensor_Active_Cost      = Sensor_Active_Cost;
        this.Sensor_Passive_Cost     = Sensor_Passive_Cost;   
    }
    
    public double get_CPU_Processing_Cost(){ return CPU_Processing_Cost;}       // Energy drawn by an active CPU [mJ/ms]
    public double get_CPU_Idling_Cost(){ return CPU_Idling_Cost;};              // Energy drawn by an idling CPU [mJ/ms]
    public double get_Radio_Transmitting_Cost(){ return Radio_Transmitting_Cost;};   // Energy drawn to transmit [mJ/ms]
    public double get_Radio_Receiving_Cost(){ return Radio_Receiving_Cost;};     // Energy drawn to receive  [mJ/ms]
    public double get_Radio_Listening_Cost(){ return Radio_Listening_Cost;};     // Energy drawn to listen the channel [mJ/ms]
    public double get_Radio_Sleeping_Cost(){ return Radio_Sleeping_Cost;};       // Energy drawn to listen the channel [mJ/ms]
    public double get_Sensor_Active_Cost(){ return Sensor_Active_Cost;};         // Energy drawn by a sigle measurement, on average. [mJ/ms]
    public double get_Sensor_Passive_Cost(){ return Sensor_Passive_Cost;};       // Energy drawn by an idling sensor [mJ/ms]
    
    public void DisplayParameters()
    {
        System.out.println("Energy Cost Parameters:");
        System.out.println("CPU_Processing_Cost: \t\t"     + CPU_Processing_Cost     + " mJ/ms");
        System.out.println("CPU_Idling_Cost: \t\t"         + CPU_Idling_Cost         + " mJ/ms");
        System.out.println("Radio_Transmitting_Cost: \t\t" + Radio_Transmitting_Cost + " mJ/ms");
        System.out.println("Radio_Receiving_Cost: \t\t"    + Radio_Receiving_Cost    + " mJ/ms");
        System.out.println("Radio_Listening_Cost: \t\t"    + Radio_Listening_Cost    + " mJ/ms");
        System.out.println("Radio_Sleeping_Cost: \t\t"     + Radio_Sleeping_Cost     + " mJ/ms");
        System.out.println("Sensor_Active_Cost: \t\t"      + Sensor_Active_Cost      + " mJ/ms");
        System.out.println("Sensor_Passive_Cost: \t\t"     + Sensor_Passive_Cost     + " mJ/ms");
    }
}
