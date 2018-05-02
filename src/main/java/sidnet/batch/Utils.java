/*
 * Utils.java
 *
 * Created on July 24, 2008, 3:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Oliver
 */
public class Utils
{
    public static boolean testPass(String command) {
        InstanceLauncher sidRun = new InstanceLauncher(command);
        sidRun.start();
        int exitValue = sidRun.exitValue();
        if (exitValue != 0)
           return false;
        return true;
    }
    
 static class InstanceLauncher extends Thread
 {
     private String command;
     private Process p;
     private boolean quitted = false;
     private int exitValue = 0;
     
     public InstanceLauncher(String command)
     {
         this.command = command;
     }
     
     public void run()
     {
          try{
            p = Runtime.getRuntime().exec(command, null);

            // any error message?
            StreamGobbler errorGobbler = new 
                StreamGobbler(p.getErrorStream(), "ERROR", p);            

            // any output?
            StreamGobbler outputGobbler = new 
                StreamGobbler(p.getInputStream(), "OUTPUT", p);

            // kick them off
            errorGobbler.start();  
            outputGobbler.start();

            exitValue = p.waitFor();
            quitted = true;
            errorGobbler.stopGlobber();
            outputGobbler.stopGlobber();

            //experimentsMatrix.add(new Integer(exitValue));

            switch(exitValue)
            {
                case 0: /*System.out.println("\tSIDnet terminated normally!");*/break;
                case 1: System.out.println("\t! ! ! SIDnet terminated abnormally. Exit code: " + exitValue);break;
            }
          }
            catch(Throwable e)
            {
                quitted = true;
                //totalNumberAbortedRuns++;
                System.out.println("Aborting current run due to run-time errors");
                e.printStackTrace();
                p.destroy();
            }
     }
     public int exitValue()
     {
         while (!quitted)
         {
             try{
                this.sleep(1000);
             }catch (Exception e){};
         }
         return exitValue;
     }
          
      public void quit()
      {
          quitted = true;
          p.destroy();
      }
 }
 
static class StreamGobbler extends Thread
    {
        InputStream is;
        String type;
        Process p = null;
        boolean terminated = false;
        

        StreamGobbler(InputStream is, String type, Process p)
        {
            this.is = is;
            this.type = type;
            this.p = p;
        }
        
        public void stopGlobber()
        {
            while(!terminated)
            {
                try{
                    this.sleep(100);
                }catch(Exception e){e.printStackTrace();};
            }
        }

        public void run()
        {
            try
            {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line=null;
                while ( (line = br.readLine()) != null)
                {   
                    System.out.println(line);    
                    if (type.equals("ERROR") && p != null)
                    {
                         if (!br.ready())
                            p.destroy();
                    }
                }
                } catch (IOException ioe)
                  {
                        ioe.printStackTrace();
                        p.destroy();
                  }
                terminated = true;  
        }
}
    
}
