package sidnet.core.interfaces;

import sidnet.core.gui.SIDnetProgressBar;
import sidnet.utilityviews.statscollector.StatsCollector;
/*
 * SimControl.java
 *
 * Created on October 30, 2007, 5:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Oliver
 */
public interface SimControl {
     public int getSpeed();
     public void setSpeed(int speed);
     public SIDnetProgressBar getSIDnetProgressBar();
     public boolean isExperimentMode();
}
