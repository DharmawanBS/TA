/*
 * SIDnetProgressBar.java
 *
 * Created on November 19, 2007, 6:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.gui;
import sidnet.core.interfaces.SIDnetDrawableInterface;
import sidnet.core.interfaces.SIDnetRegistrable;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import jist.runtime.JistAPI;

/**
 *
 * @author Oliver
 */
public class SIDnetProgressBar implements SIDnetDrawableInterface, SIDnetRegistrable{
    
    private JProgressBar progressBar;
    private long startTime;
    private long duration;
    private long timeUnit;
    private String string;
    private boolean temporalProgress = false;
    
    /**
     * Creates a new instance of SIDnetProgressBar
     */
    public SIDnetProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }
    
    public JProgressBar getInnerProgressBar()
    {
        return progressBar;
    }
    
    
    public void configureTemporalProgress(long startTime, long duration, long timeUnit, String s)
    {
        this.startTime = startTime;
        this.duration  = duration;
        this.timeUnit  = timeUnit;
        this.string = s;
        this.temporalProgress = true;
    }
    
    public void terminate(){ /* not implemented */ }
    
    public void repaintGUI()
    {
        if (temporalProgress)
        {
            int value = 0;
            if (duration != 0)
                value = (int)(((double)(JistAPI.getTime()/timeUnit - startTime))/((double)duration) * 100);
            if (value < 0)
                value = 0;
            if (value >= 100)
            {
                value = 0;
                progressBar.setString("");
                temporalProgress = false;
                progressBar.setValue(0);
                return;
            }

            progressBar.setValue(value);
            if (string != null)
                progressBar.setString(string + " " + value + " %");
            else
                progressBar.setString(value + " %");
        }
        
    }
    
    public void configureGUI(JPanel hostPanel){ /* not implemented */ };
    
    
    public void setVisibleGUI(boolean visible ){ /* not implemented */ };   
}
