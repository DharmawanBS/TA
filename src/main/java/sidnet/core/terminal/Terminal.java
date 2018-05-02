/*
 * Terminal.java
 *  
 * @author  Oliviu C. Ghica, Northwestern University
 * @version 1.0.1
 */
package sidnet.core.terminal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComboBox;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.Region;
import sidnet.core.query.Query;
import jist.swans.Constants;
import jist.runtime.JistAPI;
import sidnet.core.misc.LocationContext;
import sidnet.core.interfaces.SIDnetTerminalAccessible;
import sidnet.core.gui.RegionDrawingTool;
import sidnet.core.interfaces.NodeHardwareInterface;
import sidnet.core.interfaces.TerminalCallbackInterface;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import sidnet.core.query.LogicalOp;
import sidnet.core.query.PredefinedOperator;
import sidnet.core.query.Predicate;
import sidnet.core.query.WhereClause;

public class Terminal 
extends javax.swing.JFrame
implements SIDnetTerminalAccessible{
    
    private LocationContext locationContext;
    
    boolean regDef; // true while the regions is currently being drawn
    private Region currentlyDrawingRegion;
    private static int regionContor = 1;
    private TerminalDataSet terminalDataSet;
    private NodeHardwareInterface hostNode;
    private TerminalCallbackInterface hostNodeCallback;
    
    /** Creates new form Terminal */
    public Terminal() {
        regDef = false;
        initComponents();
        
        locationContext = new LocationContext(jRegionPanel.getWidth(),
        									  jRegionPanel.getHeight());
        
        //updateRegionComboBox();
        setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);
        
        jProgressBar1.setMinimum(0);
        jProgressBar1.setMaximum(100);
        
        terminalDataSet = null;
        
        setTitle("Terminal v1.0.1");        
    }
    
   public void dataExchange(TerminalDataSet terminalDataSet,
		   					NodeHardwareInterface hostNode,
		   					TerminalCallbackInterface hostNodeCallback) {
       if (this.terminalDataSet == null)
            initConnection(terminalDataSet, hostNode, hostNodeCallback);
       else
            if (terminalDataSet.getID() == this.terminalDataSet.getID())
                repaint();  
            else
                JOptionPane.showMessageDialog(null,
                	"Terminal already connected to a different node!" +
                	" Close terminal first!", 
                	"Terminal Warning", JOptionPane.ERROR_MESSAGE);
   }
   
   public boolean appendConsoleText(TerminalDataSet terminalDataSet, String s) {
       if (this.terminalDataSet != null)
           if (this.terminalDataSet.getID() == terminalDataSet.getID())
           {
               this.terminalDataSet.appendConsoleText(s);
               jTextArea1.setText(terminalDataSet.getConsoleText());
               jTextArea1.setCaretPosition(jTextArea1.getText().length());
               repaint();
               return true;
           }
       return false;
   }
    
    
    private void initConnection(TerminalDataSet terminalDataSet,
    							NodeHardwareInterface hostNode,
    							TerminalCallbackInterface hostNodeCallback) {
        this.terminalDataSet = terminalDataSet;
        this.hostNode = hostNode;
        this.hostNodeCallback = hostNodeCallback;
        
        currentlyDrawingRegion = null;
        regDef = false;
        
        // Build the default region, if it does not exist already
        if (terminalDataSet.getRegionList().size() == 0)
            terminalDataSet.getRegionList().add(
            		new Region(0, 5, 5, locationContext.getWidth()-10,
            							locationContext.getHeight()-10,
            							locationContext));
        updateRegionComboBox();
        
        this.setVisible(true); 
        this.repaint();
        
        // Set the nodeID
        jTextField1.setText(new String(""+terminalDataSet.getID()));
        
        // Load the terminal console data-text
        jTextArea1.setText(terminalDataSet.getConsoleText());
        
        jProgressBar1.setValue((int)hostNode.getEnergyManagement().getBattery().getPercentageEnergyLevel());
        jProgressBar1.setStringPainted(true);
        if (hostNode.getEnergyManagement().getBattery().getPercentageEnergyLevel() > 50)
            jProgressBar1.setForeground(Color.green);
        else
            if (hostNode.getEnergyManagement().getBattery().getPercentageEnergyLevel() > 20)
                jProgressBar1.setForeground(Color.orange);
            else
                jProgressBar1.setForeground(Color.red);
        
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

    	// terminal window should have fixed size
    	this.setResizable(false);
    	
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jTextField1 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jComboBox17 = new javax.swing.JComboBox();
        jSeparator2 = new javax.swing.JSeparator();
        jRegionPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButtonSubmitQuery = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxWHEREopA1 = new javax.swing.JComboBox();
        jComboBoxWHERElogA = new javax.swing.JComboBox();
        jComboBoxWHEREopB1 = new javax.swing.JComboBox();
        jComboBoxWHERElogB = new javax.swing.JComboBox();
        jComboBoxWHEREopC1 = new javax.swing.JComboBox();
        jComboBoxWHERElogC = new javax.swing.JComboBox();
        jComboBoxWHEREmodB = new javax.swing.JComboBox();
        jComboBoxWHEREmodC = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jComboBox15 = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jComboBox16 = new javax.swing.JComboBox();
        jTextFieldWHEREopA2 = new javax.swing.JTextField();
        jTextFieldWHEREopB2 = new javax.swing.JTextField();
        jTextFieldWHEREopC2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                OnWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Stats"));

        jLabel1.setText("Node #:");

        jLabel2.setText("Battery:");

        jTextField1.setEditable(false);
        jTextField1.setFocusable(false);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(124, 124, 124)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(294, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel1)
                        .add(jLabel2)
                        .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Sensor Field"));

        jButton3.setText("Begin Region Definition");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Delete Region");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jComboBox17.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox17ActionPerformed(evt);
            }
        });

        jRegionPanel.setBackground(new java.awt.Color(153, 153, 153));
        jRegionPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jRegionPanel.setMinimumSize(new java.awt.Dimension(600, 600));
        jRegionPanel.setPreferredSize(new java.awt.Dimension(600, 600));
        jRegionPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRegionPanelMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jRegionPanelLayout = new org.jdesktop.layout.GroupLayout(jRegionPanel);
        jRegionPanel.setLayout(jRegionPanelLayout);
        jRegionPanelLayout.setHorizontalGroup(
            jRegionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 613, Short.MAX_VALUE)
        );
        jRegionPanelLayout.setVerticalGroup(
            jRegionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 526, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRegionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 617, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(jButton3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 166, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 86, Short.MAX_VALUE)
                        .add(jComboBox17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 225, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jButton4)
                        .add(39, 39, 39))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton3)
                    .add(jButton4)
                    .add(jComboBox17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jRegionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 530, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Query Area"));

        jButtonSubmitQuery.setText("Submit Query");
        jButtonSubmitQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSubmitQueryActionPerformed(evt);
            }
        });

        jButton2.setText("Cancel Selected Query");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Submitted Queries");

        jLabel5.setText("SELECT");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL", "AVG", "MIN", "MAX" }));

        jLabel6.setText("FROM");

        jLabel7.setText("WHERE");

        jComboBoxWHEREopA1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Measure_A", "Measure_B", "Measure_C" }));

        jComboBoxWHERElogA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "=", "#", ">", "<", ">=", "<=" }));

        jComboBoxWHEREopB1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Measure_A", "Measure_B", "Measure_C" }));

        jComboBoxWHERElogB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "=", "#", ">", "<", ">=", "<=" }));

        jComboBoxWHEREopC1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Measure_A", "Measure_B", "Measure_C" }));

        jComboBoxWHERElogC.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "=", "#", ">", "<", ">=", "<=" }));

        jComboBoxWHEREmodB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "AND", "OR" }));

        jComboBoxWHEREmodC.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "AND", "OR" }));

        jLabel8.setText("FOR");

        jComboBox15.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Time Unit", "Seconds", "Minutes", "Hours" }));

        jLabel9.setText("SI (Sampling Interval)");

        jComboBox16.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Time Unit", "Seconds", "Minutes", "Hours" }));

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel5)
                                    .add(jLabel6))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(jComboBox2, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jComboBox3, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel8)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                                        .add(10, 10, 10)
                                        .add(jLabel9))
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                                            .add(jLabel7)
                                            .add(30, 30, 30)
                                            .add(jComboBoxWHEREopA1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                                            .add(10, 10, 10)
                                            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                .add(jComboBoxWHEREmodB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .add(jComboBoxWHEREmodC, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                            .add(11, 11, 11)
                                            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                                .add(jComboBoxWHEREopB1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .add(jComboBoxWHEREopC1, 0, 87, Short.MAX_VALUE)))))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                        .add(jTextField3)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jComboBoxWHERElogC, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jComboBoxWHERElogB, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jComboBoxWHERElogA, 0, 52, Short.MAX_VALUE))
                                    .add(jTextField4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(jTextFieldWHEREopA2)
                                    .add(jTextFieldWHEREopB2)
                                    .add(jTextFieldWHEREopC2)
                                    .add(jComboBox15, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jComboBox16, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jButtonSubmitQuery)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel3))
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jComboBox1, 0, 265, Short.MAX_VALUE)
                            .add(jButton2))
                        .add(34, 34, 34))))
        );

        jPanel3Layout.linkSize(new java.awt.Component[] {jButton2, jButtonSubmitQuery}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel3Layout.linkSize(new java.awt.Component[] {jTextField3, jTextField4}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(jComboBox2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(jComboBox3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(jComboBoxWHEREopA1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboBoxWHERElogA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextFieldWHEREopA2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBoxWHEREmodB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboBoxWHEREopB1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboBoxWHERElogB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextFieldWHEREopB2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBoxWHEREmodC, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboBoxWHEREopC1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboBoxWHERElogC, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextFieldWHEREopC2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(jComboBox15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(jComboBox16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextField4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonSubmitQuery)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton2)
                .add(55, 55, 55))
        );

        jLabel4.setText("Terminal");

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setRows(5);
        jTextArea1.setMaximumSize(new java.awt.Dimension(8000, 8000));
        jScrollPane1.setViewportView(jTextArea1);

        jButton5.setText("Clear Terminal");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("CLOSE TERMINAL ...");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(jButton5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 78, Short.MAX_VALUE)
                        .add(jButton6))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel4))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 346, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jButton5)
                            .add(jButton6))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OnWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_OnWindowClosing
        // TODO add your handling code here:
        closeTerminal();
    }//GEN-LAST:event_OnWindowClosing

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
       closeTerminal();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void closeTerminal()
    {
        if (terminalDataSet != null)
        {
            terminalDataSet.setConsoleText(jTextArea1.getText());
            hostNodeCallback.dataExchange(terminalDataSet);
        }
        terminalDataSet = null;
        
        this.setVisible(false);
    }
    
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // "Clear Terminal" Button
        jTextArea1.setText("");
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButtonSubmitQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmitQueryActionPerformed
        boolean syntaxOK = true;
        
        String queryString = new String();
        queryString="SELECT ";
        queryString += (String)jComboBox2.getSelectedItem();
        
        if (jComboBox3.getSelectedIndex() > 0)
            queryString += "\n   FROM " + (String)jComboBox3.getSelectedItem();
        else
            syntaxOK = false;
        
        if (jComboBoxWHEREopA1.getSelectedIndex() > 0 && jComboBoxWHERElogA.getSelectedIndex() > 0 && !((String)jTextFieldWHEREopA2.getText()).equals(""))
            queryString += "\n   WHERE " + (String)jComboBoxWHEREopA1.getSelectedItem() + " " + (String)jComboBoxWHERElogA.getSelectedItem() + " " + jTextFieldWHEREopA2.getText();
        
        if (jComboBoxWHEREmodB.getSelectedIndex() > 0 && jComboBoxWHEREopB1.getSelectedIndex() > 0 && jComboBoxWHEREopB1.getSelectedIndex() > 0 && !((String)jTextFieldWHEREopB2.getText()).equals(""))
            queryString += "\n   " + (String)jComboBoxWHEREmodB.getSelectedItem() + " " + (String)jComboBoxWHEREopB1.getSelectedItem() + " " + (String)jComboBoxWHERElogB.getSelectedItem() + " " + jTextFieldWHEREopB2.getText();
        
        if (jComboBoxWHEREmodC.getSelectedIndex() > 0 && jComboBoxWHEREopC1.getSelectedIndex() > 0 && jComboBoxWHERElogC.getSelectedIndex() > 0 && !((String)jTextFieldWHEREopC2.getText()).equals(""))
            queryString += "\n   " + (String)jComboBoxWHEREmodC.getSelectedItem() + " " + (String)jComboBoxWHEREopC1.getSelectedItem() + " " + (String)jComboBoxWHERElogC.getSelectedItem() + " " + jTextFieldWHEREopC2.getText();
        
        if (!((String)jTextField3.getText()).equals("") && jComboBox15.getSelectedIndex() > 0)
            queryString += "\n      FOR " + jTextField3.getText() + " " + (String)jComboBox15.getSelectedItem();
        else
            syntaxOK = false;
        
        if (!((String)jTextField4.getText()).equals("") && jComboBox16.getSelectedIndex() > 0)
            queryString += "\n      SI " + jTextField4.getText() + " " + (String)jComboBox16.getSelectedItem();
        else
            syntaxOK = false;
        
        queryString +="\n";
        
        if (!syntaxOK)
        {
            queryString = "SYNTAX ERROR\n";
            jTextArea1.append(queryString);
            return;
        }
        
        
        //jComboBox15.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Time Unit", "Seconds", "Minutes", "Hours" }));

        
        //jComboBox16.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Time Unit", "Seconds", "Minutes", "Hours" }));
        
        //Calculate the run time.  
        long runTime = 0;
        if((String)jComboBox15.getSelectedItem()=="Seconds")
        {
            runTime = Long.parseLong(jTextField3.getText())*Constants.SECOND;
        }
        if((String)jComboBox15.getSelectedItem()=="Minutes")
        {
            runTime = Long.parseLong(jTextField3.getText())*Constants.MINUTE;
        }
        if((String)jComboBox15.getSelectedItem()=="Hours")
        {
            runTime = Long.parseLong(jTextField3.getText())*Constants.HOUR;
        }      
        
        
        
        //Calculate the sampling frequency
        long sampleFreq = 0;
        if((String)jComboBox16.getSelectedItem()=="Seconds")
        {
            sampleFreq = Long.parseLong(jTextField4.getText())*Constants.SECOND;
        }
        if((String)jComboBox16.getSelectedItem()=="Minutes")
        {
            sampleFreq = Long.parseLong(jTextField4.getText())*Constants.MINUTE;
        }
        if((String)jComboBox16.getSelectedItem()=="Hours")
        {
            sampleFreq = Long.parseLong(jTextField4.getText())*Constants.HOUR;
        }  
        
        
        // Parse the query (panel) dimensions to (field) dimensions
        Region region = (Region)terminalDataSet.getRegionList().get(jComboBox3.getSelectedIndex()-1);
        
        // Parse the WHERE clause
        WhereClause whereClause = null;
        
        Predicate predicateA = createPredicate(jComboBoxWHEREopA1, jComboBoxWHERElogA, jTextFieldWHEREopA2);
        Predicate predicateB = createPredicate(jComboBoxWHEREopB1, jComboBoxWHERElogB, jTextFieldWHEREopB2);
        Predicate predicateC = createPredicate(jComboBoxWHEREopC1, jComboBoxWHERElogC, jTextFieldWHEREopC2);
        LogicalOp lopB = LogicalOp.AND;
        if (jComboBoxWHEREmodB.getSelectedItem().toString().equals("OR"));
            lopB = LogicalOp.OR;
        if (jComboBoxWHEREmodB.getSelectedItem().toString().equals("AND"));
            lopB = LogicalOp.AND;    
        LogicalOp lopC = null;
        if (jComboBoxWHEREmodC.getSelectedItem().toString().equals("OR"));
            lopC = LogicalOp.OR;
        if (jComboBoxWHEREmodC.getSelectedItem().toString().equals("AND"));
            lopC = LogicalOp.AND;
            
         if (predicateA != null)
         {
             whereClause = new WhereClause();
             whereClause.addPredicate(predicateA);
             if (predicateB != null)
                 whereClause.addPredicate(lopB, predicateB);
             if (predicateC != null)
                 whereClause.addPredicate(lopC, predicateC);
         }
            
        // Create a Query Object
        terminalDataSet.add(new Query(hostNode.getIP(), hostNode.getGPS().getNCS_Location2D(), null, (byte)jComboBox2.getSelectedIndex(), sampleFreq, JistAPI.getTime() , JistAPI.getTime()  + runTime, region, whereClause));
        
        jTextArea1.append(queryString);
        
        // Signal the node about the update
        hostNodeCallback.dataExchange(terminalDataSet);
    }//GEN-LAST:event_jButtonSubmitQueryActionPerformed

    private Predicate createPredicate(JComboBox jBox1, JComboBox jBox2, JTextField jText)
    {
        if (jBox1.getSelectedItem().toString().startsWith("Measure") &&
            jBox2.getSelectedItem().toString().length() > 0 &&
            jText.getText().length() > 0)
        {
            PredefinedOperator pop = null;
            if (jBox1.getSelectedItem().toString().endsWith("A"))
                pop = PredefinedOperator.MEASUREMENT_A;
            if (jBox1.getSelectedItem().toString().endsWith("B"))
                pop = PredefinedOperator.MEASUREMENT_B;
            if (jBox1.getSelectedItem().toString().endsWith("C"))
                pop = PredefinedOperator.MEASUREMENT_C;
            
            LogicalOp lop = null;
            if (jBox2.getSelectedItem().toString().equals(">="))
                lop = LogicalOp.GREATER_OR_EQUAL;
            if (jBox2.getSelectedItem().toString().equals(">"))
                lop = LogicalOp.GREATER;
            if (jBox2.getSelectedItem().toString().equals("="))
                lop = LogicalOp.EQUAL;
            if (jBox2.getSelectedItem().toString().equals("<"))
                lop = LogicalOp.LESS;
            if (jBox2.getSelectedItem().toString().equals("<="))
                lop = LogicalOp.LESS_OR_EQUAL;
            if (jBox2.getSelectedItem().toString().equals("<>"))
                lop = LogicalOp.NOT_EQUAL;
            
            double nop = 0.0d;
            nop = Double.parseDouble(jText.getText());

            if (pop != null && lop != null)
                return new Predicate(pop, lop, nop);
        }
        return null;
    }
    
    private void jComboBox17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox17ActionPerformed
        // TODO add your handling code here:
        if (terminalDataSet == null)
            System.out.println("[ERROR: Terminal] - no host node identified for this terminal; Perform a connection Handshake first");
        
        repaint();
        
    }//GEN-LAST:event_jComboBox17ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // DELETE REGION
        if (terminalDataSet == null)
            System.out.println("[ERROR: Terminal] - no host node identified for this terminal; Perform a connection Handshake first");
        else
        {
            // we don't want to delete the default region, which is the entire sensor field
            if (jComboBox17.getSelectedIndex() == 1)
                return;
            Region region = terminalDataSet.getRegionList().get(jComboBox17.getSelectedIndex()-1);
            if (region != null)
            {
                terminalDataSet.getRegionList().remove(jComboBox17.getSelectedIndex()-1);    
                updateRegionComboBox();
            }
            repaint();
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // ADD REGION
         if (terminalDataSet == null)
         {
              System.out.println("[ERROR: Terminal] - no host node identified for this terminal; Perform a connection Handshake first");
              return;
         }
        
        regDef=!regDef;
        if (regDef)
            jButton3.setText("End Region Definition");
        else
        {
            if (currentlyDrawingRegion!=null)
            {
                terminalDataSet.getRegionList().add(currentlyDrawingRegion.getCopy());
                currentlyDrawingRegion = null;
            }
            updateRegionComboBox();
            jButton3.setText("Start Region Definition");
            repaint();
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    
    private void updateRegionComboBox()
    {
        int index = 1;
        
        if (terminalDataSet == null)
        {
           System.out.println("[ERROR: Terminal] - no host node identified for this terminal; Perform a connection Handshake first");
           return;
        }
        
        if (terminalDataSet.getRegionList()!= null && terminalDataSet.getRegionList().size() > 0)
        {
            String[] s = new String[terminalDataSet.getRegionList().size()+1];
            s[0] ="";
            for (Region tempRegion:terminalDataSet.getRegionList())
            {
                s[index] = "Region " + tempRegion.getID();
                index++;
            }     
            
            jComboBox17.setModel(new javax.swing.DefaultComboBoxModel(s));
            jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(s));  
            jComboBox3.setSelectedIndex(index-1);
        }
        else
            {
                String[] s = new String[1];
                s[0] = "";
                jComboBox17.setModel(new javax.swing.DefaultComboBoxModel(s));
                jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(s));
                jComboBox3.setSelectedIndex(1);
            }
        
        repaint();
    }
    
    private void jRegionPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRegionPanelMouseClicked
// TODO add your handling code here:
        if (terminalDataSet == null)
        {
            System.out.println("[ERROR: Terminal] - no host node identified for this terminal; Perform a connection Handshake first");
            return;
        }
        
        if (regDef)
        {
            if (currentlyDrawingRegion == null)
            {
                currentlyDrawingRegion = new Region(regionContor++, locationContext);
                repaint();
                //terminalDataSet.regionListGUI.incrementContor();
            }
            
            Location2D point = new Location2D((int)evt.getPoint().getX(), (int)evt.getPoint().getY());
            currentlyDrawingRegion.add(point);
            repaint();  
            
        }
    }//GEN-LAST:event_jRegionPanelMouseClicked
    
    
    public void paint(Graphics g) {
        String s = "+";
        super.paint(g);
        
       
        for (Region region: terminalDataSet.getRegionList())
        {
            int id = region.getID() % 5;
            Color color;
            switch(id)
            {
                case(0): color = Color.PINK;  break;
                case(1): color = Color.YELLOW;break;
                case(2): color = Color.GREEN; break;
                case(3): color = Color.RED;   break;
                case(4): color = Color.ORANGE;break;
                default: color = Color.BLACK; break;
            }
            RegionDrawingTool.draw(region, (Graphics2D)jRegionPanel.getGraphics(), color);
        }
        
        if (currentlyDrawingRegion != null)
            RegionDrawingTool.draw(currentlyDrawingRegion, (Graphics2D)jRegionPanel.getGraphics(), Color.WHITE);
        
        jTextArea1.setCaretPosition(jTextArea1.getText().length());
        
        Graphics2D g2d = (Graphics2D)jRegionPanel.getGraphics();
        g2d.setColor(Color.white);
        Location2D terminalPanelLocation = hostNode.getGPS().getNCS_Location2D().fromNCS(locationContext);
        g2d.drawString(s, (int)terminalPanelLocation.getX(), (int)terminalPanelLocation.getY());
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Terminal().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButtonSubmitQuery;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox15;
    private javax.swing.JComboBox jComboBox16;
    private javax.swing.JComboBox jComboBox17;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBoxWHERElogA;
    private javax.swing.JComboBox jComboBoxWHERElogB;
    private javax.swing.JComboBox jComboBoxWHERElogC;
    private javax.swing.JComboBox jComboBoxWHEREmodB;
    private javax.swing.JComboBox jComboBoxWHEREmodC;
    private javax.swing.JComboBox jComboBoxWHEREopA1;
    private javax.swing.JComboBox jComboBoxWHEREopB1;
    private javax.swing.JComboBox jComboBoxWHEREopC1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JPanel jRegionPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextFieldWHEREopA2;
    private javax.swing.JTextField jTextFieldWHEREopB2;
    private javax.swing.JTextField jTextFieldWHEREopC2;
    // End of variables declaration//GEN-END:variables
    
}
