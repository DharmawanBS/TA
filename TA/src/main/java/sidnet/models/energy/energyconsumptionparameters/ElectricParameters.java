/*
 * ElectricParameters.java
 *
 * Created on July 27, 2005, 10:55 AM
 */

/**
 *
 * @author  Oliviu Ghica
 */
package sidnet.models.energy.energyconsumptionparameters;

public class ElectricParameters {
    public double ProcessorCurrentDrawn_ActiveMode;
    public double ProcessorCurrentDrawn_SleepMode;
    public double RadioCurrentDrawn_TransmitMode;
    public double RadioCurrentDrawn_ListenMode;
    public double RadioCurrentDrawn_ReceiveMode;
    public double RadioCurrentDrawn_SleepMode;
    public double SensorCurrentDrawn_ActiveMode;
    public double SensorCurrentDrawn_SleepMode;
    public double Coefficient;          // We need to scale from hour (mAh) to milisenconds
    /** Creates a new instance of ElectricParameters */
    public ElectricParameters(  double PCD_AM, 
                                double PCD_SM,
                                double RCD_TM, 
                                double RCD_RM,
                                double RCD_LM,
                                double RCD_SM,
                                double SCD_AM,
                                double SCD_PM) {
        ProcessorCurrentDrawn_ActiveMode = PCD_AM;
        ProcessorCurrentDrawn_SleepMode  = PCD_SM;
        RadioCurrentDrawn_TransmitMode   = RCD_TM;
        RadioCurrentDrawn_ReceiveMode    = RCD_RM;
        RadioCurrentDrawn_ListenMode     = RCD_LM;
        RadioCurrentDrawn_SleepMode      = RCD_SM;
        SensorCurrentDrawn_ActiveMode   = SCD_AM;
        SensorCurrentDrawn_SleepMode    = SCD_PM;
        
        Coefficient                     = 60*60*1000;  // 1h = 60 m; 1m = 60s; 1s = 1000 ms                                  
    }
    
}
