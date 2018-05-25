package sidnet.core.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    Process p = null;
    Logger log = null;
    boolean terminated = false;
    

    public StreamGobbler(InputStream is, String type, Process p, Logger log)
    {
        this.is = is;
        this.type = type;
        this.p = p;
        this.log = log;
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
                     if (log != null)
                        log.error(line); 
                     if (!br.ready())
                        p.destroy();
                }
            }
            } catch (IOException ioe)
              {
                    if (log != null)
                        log.error(ioe);
                    ioe.printStackTrace();
                    p.destroy();
              }
            terminated = true;  
    }
}