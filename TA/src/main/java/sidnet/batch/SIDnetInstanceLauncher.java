package sidnet.batch;

import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import sidnet.core.misc.FileUtils;

public class SIDnetInstanceLauncher 
extends Thread {
    private String command;
    private Logger errorLog;
    private Process p = null;
    private long runNumber;
    private long repeatIndex;
    private long experimentId;
    private String experimentsTargetDirectory;
    private boolean quitted = false;
    private boolean terminated = false;
    private long startTimeMillis;
    private boolean failed = false;
    private boolean killed = false;
    
    public SIDnetInstanceLauncher(String command, long runNumber, long repeatIndex, long experimentId, String experimentsTargetDirectory)
    {
        this.command = command;
        this.runNumber = runNumber;
        this.repeatIndex = repeatIndex;
        this.experimentId = experimentId;
        this.experimentsTargetDirectory = experimentsTargetDirectory;
        errorLog = configureTempErrorLogger(experimentsTargetDirectory, "err-rpt" + repeatIndex + "-exp" + experimentId);
    }
         
    public void run()
    {
         try{
           startTimeMillis = System.currentTimeMillis();
           p = Runtime.getRuntime().exec(command, null);

           // any error message?
           StreamGobbler errorGobbler = new 
               StreamGobbler(p.getErrorStream(), "ERROR", p, errorLog);            

           // any output?
           StreamGobbler outputGobbler = new 
               StreamGobbler(p.getInputStream(), "OUTPUT", p, null);

           // kick them off
           errorGobbler.start();  
           outputGobbler.start();

           int exitValue = p.waitFor();

           terminated = true;
           
           errorGobbler.stopGlobber();
           outputGobbler.stopGlobber();

           //experimentsMatrix.add(new Integer(exitValue));

           switch(exitValue)
           {
               case 0 : System.out.println("\tSIDnet terminated normally!"); failed = false; break;
               default: System.out.println("\t! ! ! SIDnet terminated abnormally. Exit code: " + exitValue); failed = true;  break;
           }
           if (quitted)
           {
                System.out.println("SIDnet experimentId#" + experimentId + " quitted. Remove associated log files");
                errorLog.removeAllAppenders();
                FileUtils.deleteFilesWithPrefix(experimentsTargetDirectory, "run" + runNumber + "-rpt" + repeatIndex + "-exp" + experimentId);
                FileUtils.deleteFilesWithPrefix(experimentsTargetDirectory, "err-rpt" + repeatIndex + "-exp" + experimentId);
           }
           
           // wait for main to kill this process
           while (!killed)
           {
               try{
                   sleep(100);
                   
               }catch(Exception e){;};
           }
         }
           catch(Throwable e)
           {
               //totalNumberAbortedRuns++;
               System.out.println("Aborting current experiment due to run-time errors");
               e.printStackTrace();
               failed = false;
               terminated = true;
           }
    }
         
     public void quit()
     {
         quitted = true;
         p.destroy();
     }   
     
    public boolean terminated()
    {
        return terminated;
    }
    
    public void kill()
    {
        p.destroy();
        killed = true;
    }
    
    public boolean failedExperiment()
    {
        return failed;
    }
    
    public long getRunNumber()
    {
        return runNumber;
    }
    
    public long getRepeatIndex()
    {
        return repeatIndex;
    }
    
    public long getExperimentId()
    {
        return experimentId;
    }
    
    public long getStartTimeMillis()
    {
        return startTimeMillis;
    }
    
   private Logger configureTempErrorLogger(String experimentsTargetDirectory, String fileNamePrefix)
   {
       // Configure the summary report logger
       Logger errorLog;
       String filename = fileNamePrefix  + ".tmp";
       
       FileAppender appender; 
       try
       {
           PatternLayout layout = new PatternLayout("%m %n");
           appender = new FileAppender(layout, experimentsTargetDirectory + filename, true);
       }
       catch(IOException e)
       {
           e.printStackTrace();
           throw new RuntimeException("Unable to configure loggin property");
       }

       errorLog = Logger.getLogger(filename);
       errorLog.addAppender(appender);
       errorLog.setLevel((Level)Level.ERROR);      
       errorLog.setAdditivity(false);
       
       return errorLog;
   }
}
