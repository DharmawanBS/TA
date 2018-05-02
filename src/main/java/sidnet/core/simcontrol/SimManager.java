/*
 * SimManager.java
 *
 * Created on June 3, 2007, 11:09 AM
 * 
 * @author Oliviu C. Ghica, Northwestern University
 * @version 1.0.1
 */

package sidnet.core.simcontrol;

import java.util.Hashtable;
import javax.swing.JLabel;
import jist.runtime.JistAPI;
import jist.swans.Constants;
import sidnet.core.gui.PanelContext;
import sidnet.core.gui.SIDnetProgressBar;
import sidnet.core.misc.Sleeper;
import java.util.LinkedList;
import javax.swing.JPopupMenu;
import sidnet.core.interfaces.*;
import java.awt.Graphics;
import sidnet.core.gui.SimGUI;
import sidnet.core.misc.Node;
import sidnet.core.misc.NodeFinder;
import sidnet.utilityviews.statscollector.StatsCollector;

/**
 *
 * @author  Oliver
 */
public class SimManager extends javax.swing.JPanel implements SimulationTimeRunnable, SimControl{
    public static byte DEMO = 0;
    public static byte EXPERIMENT = 1;
    
    /** self-referencing proxy entity. */
    public static final long serialVersionUID = 24362462L;
    
    private static byte runMode = 0;
    
    private SimulationTimeRunnable self;
    
    public static byte PAUSED   = 0;
    public static byte X1       = 1;        //REAL TIME
    public static byte X10      = 2;
    public static byte X100     = 3;
    public static byte X1000    = 4;
    public static byte ACCEL    = 5;
    public static byte MAX      = 6;
    
    private LinkedList<SIDnetRegistrable> registeredLayers;
    private Node[] nodesList = null;
    private LinkedList<PanelContext> panelContextSet;
    
    SIDnetProgressBar progressBar = null;
    
    JPopupMenu fieldPopupMenu = null;
    
    public long runnerCount = 0;
    
    private static SimGUI simGUI = null;
    
    private static StatsCollector statistics = null;
    
    // internals
    private boolean registeredCoreElements = false;
    
    public void register(Node[] nList)
    {
        this.nodesList = nList;
        if (!registeredCoreElements)
            registerAndRunCoreElements(nList);
    }
    
    public void registerAndRun(SIDnetRegistrable registrableLayer,
    						   PanelContext panelContext) {               
        registeredLayers.add(registrableLayer);
        
        if (registrableLayer instanceof StatsCollector)
            this.statistics = (StatsCollector)registrableLayer;
        
        if (registrableLayer instanceof SIDnetDrawableInterface) {
            if (panelContext != null)
                ((SIDnetDrawableInterface)registrableLayer).configureGUI(panelContext.getPanelGUI());
            else
                ((SIDnetDrawableInterface)registrableLayer).configureGUI(null);
        }
        
        if (registrableLayer instanceof SIDnetMenuInterface) {
            ((SIDnetMenuInterface)registrableLayer).configureMenu(panelContext.getMenu());
        }               
    }
    
    private void registerAndRunCoreElements(Node[] nList) {
        registeredCoreElements = true;
        registerAndRun(new NodeFinder(nList), panelContextSet.getFirst()); // first is the fieldPanel 
    }
    
    /**
     * Creates new form SimManager
     */
    public SimManager(SimGUI simGUI, SIDnetProgressBar progressBar, byte runMode) {
    	
    	this.simGUI = simGUI;
    	
        initComponents();
        
        this.runMode = runMode;
        
        registeredLayers = new LinkedList<SIDnetRegistrable>();
        panelContextSet = new LinkedList<PanelContext>();
        
        this.setSize(simGUI.getSimControlPanel().getSize());
        
        simGUI.getSimControlPanel().add(this);
        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(PAUSED), new JLabel("PAUSED"));
        labelTable.put(new Integer(X1), new JLabel("1X"));
        labelTable.put(new Integer(X10), new JLabel("10X"));
        labelTable.put(new Integer(X100), new JLabel("100X"));
        labelTable.put(new Integer(X1000), new JLabel("1000X"));
        labelTable.put(new Integer(ACCEL), new JLabel("Accel"));
        labelTable.put(new Integer(MAX), new JLabel("MAX"));  // user should cut out graphics. This is for very high speed simulations
        jSlider1.setLabelTable(labelTable);
        jSlider1.setPaintLabels(true);
        this.setBounds(10,7, 300, 100);
        this.setVisible(true);  
        this.setOpaque(false);
        
        this.progressBar = progressBar;
        this.registerAndRun(progressBar, null);
        
        managePanelContext(simGUI.getSensorsPanelContext());  
        managePanelContext(simGUI.getUtilityPanelContext1()); 
        managePanelContext(simGUI.getUtilityPanelContext2());
        
        this.self = (SimulationTimeRunnable)JistAPI.proxy(this, SimulationTimeRunnable.class );
    }
    
    public SimGUI getSimGUI() {
    	return simGUI;
    }
    
    private void managePanelContext(PanelContext panelContext) {
        panelContextSet.add(panelContext); 
    }
    
    public SIDnetProgressBar getSIDnetProgressBar() {
        return progressBar;
    }
    
    public int getSpeed() {
        return jSlider1.getValue();
    }
    
    public void setSpeed(int val) {
        jSlider1.setValue(val);
    }
    
    public long getSpeedUp() {
        if (jSlider1.getValue() == X10)
            return 10;
        if (jSlider1.getValue() == X100)
            return 100;
        if (jSlider1.getValue() == X1000)
            return 1000;
        if (jSlider1.getValue() == ACCEL)
            return 1800000;     // every 30 minutes
        if (jSlider1.getValue() == MAX)
            return 14400000;    // every 4 hours
        return 1;
    }
    
    public long getBaseTime() {
        if (jSlider1.getValue() == X10)
            return 2000;
        if (jSlider1.getValue() == X100)
            return 4000;
        if (jSlider1.getValue() == X1000)
            return 8000;
        if (jSlider1.getValue() == ACCEL)
            return 8000;     // every 30 minutes
        if (jSlider1.getValue() == MAX)
            return 16000;    // every 4 hours
        return 200;
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jSlider1 = new javax.swing.JSlider();

        jSlider1.setMajorTickSpacing(1);
        jSlider1.setMaximum(6);
        jSlider1.setMinorTickSpacing(1);
        jSlider1.setPaintTicks(true);
        jSlider1.setSnapToTicks(true);
        jSlider1.setValue(0);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jSlider1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 279, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jSlider1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        // TODO add your handling code here:
        for (SIDnetRegistrable layer: registeredLayers)
        {
            if (layer instanceof SIDnetTimeDependable)
                ((SIDnetTimeDependable)layer).updateSimulationTimeToCurrent();
            
              if (layer instanceof SIDnetDrawableInterface)
                  if(getSpeed() == MAX)
                      ((SIDnetDrawableInterface)layer).setVisibleGUI(false);
                  else
                  {
                      ((SIDnetDrawableInterface)layer).setVisibleGUI(true);           
                      //((SIDnetDrawableInterface)layer).repaintGUI();
                  }
        }
        
        if (nodesList != null)
            for (int i = 0; i < nodesList.length; i++)
            {
                if ( getSpeed() == MAX )
                {
                    if( nodesList[i].getNodeGUI().isVisible())
                        nodesList[i].getNodeGUI().setVisible(false);
                }
                else
                    if( !nodesList[i].getNodeGUI().isVisible())
                        nodesList[i].getNodeGUI().setVisible(true);        
            }
    }//GEN-LAST:event_jSlider1StateChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider jSlider1;
    // End of variables declaration//GEN-END:variables
 
    public void scheduleNextRun() throws JistAPI.Continuation
    {
        long time = System.currentTimeMillis();
        if (getSpeed() == SimManager.MAX)
            JistAPI.sleepBlock(getBaseTime() * Constants.MILLI_SECOND);
        else
            if (getSpeed() == SimManager.ACCEL)
                JistAPI.sleepBlock(getBaseTime() * Constants.MILLI_SECOND);
            else 
                if (getSpeed() == SimManager.X1 || 
                    getSpeed() == SimManager.X10 ||
                    getSpeed() == SimManager.X100 ||
                    getSpeed() == SimManager.X1000) 
                {
                        //long time = System.currentTimeMillis();
                        Sleeper.Sleep(getBaseTime() / getSpeedUp() );
                        JistAPI.sleepBlock(getBaseTime()* Constants.MILLI_SECOND);
                }
                else
                    if (getSpeed() == SimManager.PAUSED)
                        Sleeper.Sleep(1000);       
    }
    
  public long getSleepPeriod() {
      if (getSpeed() == MAX)
          return 1000;
      if (getSpeed() == ACCEL)
          return 1000;
      if (getSpeed() == PAUSED)
          return 1000;
      if (getSpeed() == X1)
    	  return 100;
      return 1000;
  }
  
  public void repaintGUINow() {
	  for (SIDnetRegistrable layer: registeredLayers) {       
          if (layer instanceof SIDnetTimeDependable)
              ((SIDnetTimeDependable)layer).updateSimulationTimeToCurrent(); // this refreshes the phenomena layer
          
          if (layer instanceof SIDnetDrawableInterface) 
              if(getSpeed() != SimManager.MAX &&
                   getSpeed() != SimManager.PAUSED) 
                  ((SIDnetDrawableInterface)layer).repaintGUI(); 
      }
  }
    
    public void run() {
       repaintGUINow();
                
       scheduleNextRun();
       
       self.run();
    }
    
    protected void clear(Graphics g) {
       // super.paintComponent(g);
    }  
     
     
    public boolean isExperimentMode() {
        return runMode == EXPERIMENT;
    }
     
    public SimulationTimeRunnable getProxy() {
        return (SimulationTimeRunnable)self;
    }    
}
