/*
 * SIDnetCSVRunner.java
 *
 * Created on March 3, 2008, 7:38 PM
 */

package sidnet.batch;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.*;
import sidnet.core.misc.FileUtils;

/**
 *
 * @deprecated "Use SIDnetBatchRunner Instead"
 *
 * @author  Oliver
 * version 1.1 (02-27-2009) Oliver
 *      Added support for "-repeat"
 *      Enforced syntax
 * version 1.2 (03-20-2009) Oliver
 *      Disabling rewrite r caching in linux since it can create some file system problems
 * version 1.2.1 (03-20-2009) Oliver
 *      Correcting resource over-usage at the last experiments (infinite loop)
 * version 1.3 (10-07-2009) Oliver
 *      Add support for passing arguments via system-properties
 */

@Deprecated
public class SIDnetCSVRunner 
extends javax.swing.JFrame {
    /** VERSIONING */
    private static final String VERSION = "v1.3";
    
    /** FILE NAMES */
    private static final String DETAILED_LOG_FILENAME_PREFIX = "DetailedExperimentReport";
    private static final String SUMMARY_LOG_FILENAME_PREFIX  = "SummaryExperimentReport";
    private static final String EXPERIMENT_CONTOR_FILE       = "runcontor.cnt";   
    
    /** ARGUMENTS */
    private static final String LINUX_KEY                    = "-linux";
    private static final String RUNID_KEY                    = "-runid=";
    private static final String EXPERIMENTID_KEY             = "-experimentid=";
    private static final String PARALLELISM_KEY              = "-parallelism=";
    private static final String REPEAT_KEY                   = "-repeat=";
    private static final String FIRST_REPEAT_KEY             = "-firstrepeat=";    
        
    /** DEFAULTS */
    private static final long    RUNID_DEFAULT                = -1; // run all
    private static final long    EXPERIMENTID_DEFAULT         = -1; // run all
    private static final long    PARALLELISM_DEFAULT          = 1;
    private static final int     REPEAT_COUNT_DEFAULT         = 1;
    private static final int     REPEAT_INDEX_BEGIN_DEFAULT   = 1;
    private static final boolean LINUX_DEFAULT                = false;
    private static final String DEFAULT_WORKING_DIRECTORY    = ".";
    
    /** OTHERS */
    private static final long UNDEFINED  = -1;
    
    /** LOCALS */    
    private static Logger detailedLog;
    private static Logger summaryLog;
    private static String[] header = null;
    
    private static Process p = null; 
    
    private static List<Integer> experimentsMatrix = new LinkedList<Integer>();
    private static LinkedList<Integer> launchedExperiments = new LinkedList<Integer>();
    private static LinkedList<Long> launchedExperimentsIds = new LinkedList<Long>();

    private static String experimentsStartDateAndTime = getDateTime();
    private static long startTime;
    private static long runId = 0;
    private static int  REPEAT_COUNT = 0;
    private static int  REPEAT_INDEX_BEGIN = -1;
    private static int  repeatIndex = 1;
    private static int degreeOfParallelism = 1;
    private static int instanceContor = 0;
    private static LinkedList<SIDnetInstanceLauncher> SIDnetInstanceList;
    private static long lastCommitedInstanceIndex = 0;
    private static long userExperimentId    = -1;
    private static long userRunId           = -1;
    private static long current_CSV_ExperimentId = -1;
    private static long current_LOG_ExperimentId = -1;
    public static String workingDirectory = "";
    public static String experimentsTargetDirectory   = "";
    
    public static boolean started = false;
    private static boolean quitted = false;
    
    
    private static boolean linuxEnv = LINUX_DEFAULT;
    
    private static int totalNumberAttemptedRuns_LOG = 0;
    private static int totalNumberAbortedRuns_LOG = 0;
    private static int totalNumberOfExperiments_LOG = 0;
    private static int passedExperimentsCount_LOG = 0;
    private static int failedExperimentsCount_LOG = 0;
    private static int remainingExperimentsCount_LOG = 0;
    
    private static int totalNumberAttemptedRuns = 0;
    private static int totalNumberAbortedRuns = 0;
    private static int totalNumberOfExperiments = 0;
    private static int passedExperimentsCount = 0;
    private static int failedExperimentsCount = 0;
    private static int remainingExperimentsCount = 0;

  
    /**
     * Creates new form SIDnetCSVRunner
     */
    public SIDnetCSVRunner() {
        initComponents();
        jButtonQuit.setEnabled(false);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMainPanel = new javax.swing.JPanel();
        jButtonStart = new javax.swing.JButton();
        jButtonQuit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jRunID_Text = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextDegreeParallelism = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jRepeeatIndex_Label = new javax.swing.JLabel();
        jLOG_ExperimentID_Label = new javax.swing.JLabel();
        jLOG_ExperimentID_Text = new javax.swing.JTextField();
        jRepeatIndex_Text = new javax.swing.JTextField();
        jRepeeatIndex_Label1 = new javax.swing.JLabel();
        jLOG_ExperimentID_MAX_Text = new javax.swing.JTextField();
        jRepeeatIndex_Label3 = new javax.swing.JLabel();
        jNumberRepeats_Text = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jExperimentsCount_LOG = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPassedExperimentsCount_LOG = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jFailedExperimentsCount_LOG = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jRemainingExperimentsCount_LOG = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jRemainingExperimentsCount = new javax.swing.JTextField();
        jFailedExperimentsCount = new javax.swing.JTextField();
        jPassedExperimentsCount = new javax.swing.JTextField();
        jExperimentsCount = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jCurrentRepeat_ProgressBar = new javax.swing.JProgressBar();
        jOverallProgress_Label1 = new javax.swing.JLabel();
        jCSV_ExperimentID_Label = new javax.swing.JLabel();
        jCSV_ExperimentID_Text = new javax.swing.JTextField();
        jRepeeatIndex_Label2 = new javax.swing.JLabel();
        jCSV_ExperimentID_MAX_Text = new javax.swing.JTextField();
        jOverallProgress_Label2 = new javax.swing.JLabel();
        jOverall_ProgressBar = new javax.swing.JProgressBar();
        jLabel4 = new javax.swing.JLabel();
        jTextElapsedTime = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SIDnet CSV Runner " + VERSION);
        setResizable(false);

        jMainPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonStart.setText("START");
        jButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartActionPerformed(evt);
            }
        });

        jButtonQuit.setText("Safe Interrupt");
        jButtonQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonQuitActionPerformed(evt);
            }
        });

        jLabel1.setText("Run ID");

        jRunID_Text.setEditable(false);
        jRunID_Text.setText("jTextField1");
        jRunID_Text.setBorder(null);

        jLabel3.setText("Degree of parallelism");

        jTextDegreeParallelism.setEditable(false);
        jTextDegreeParallelism.setText("jTextField3");
        jTextDegreeParallelism.setBorder(null);

        jRepeeatIndex_Label.setText("RepeatIndex:");

        jLOG_ExperimentID_Label.setText("LOG-ExperimentID:");

        jLOG_ExperimentID_Text.setEditable(false);
        jLOG_ExperimentID_Text.setText("N/A");
        jLOG_ExperimentID_Text.setBorder(null);

        jRepeatIndex_Text.setEditable(false);
        jRepeatIndex_Text.setText("N/A");
        jRepeatIndex_Text.setBorder(null);

        jRepeeatIndex_Label1.setText("/");

        jLOG_ExperimentID_MAX_Text.setEditable(false);
        jLOG_ExperimentID_MAX_Text.setText("N/A");
        jLOG_ExperimentID_MAX_Text.setBorder(null);

        jRepeeatIndex_Label3.setText("/");

        jNumberRepeats_Text.setEditable(false);
        jNumberRepeats_Text.setText("jTextField1");
        jNumberRepeats_Text.setBorder(null);

        jLabel8.setText("No. of Experiments");

        jExperimentsCount_LOG.setEditable(false);
        jExperimentsCount_LOG.setText("N/A");
        jExperimentsCount_LOG.setAutoscrolls(false);
        jExperimentsCount_LOG.setBorder(null);

        jLabel9.setText("#Passed");

        jPassedExperimentsCount_LOG.setEditable(false);
        jPassedExperimentsCount_LOG.setText("N/A");
        jPassedExperimentsCount_LOG.setBorder(null);

        jLabel10.setText("#Failed");

        jFailedExperimentsCount_LOG.setEditable(false);
        jFailedExperimentsCount_LOG.setText("N/A");
        jFailedExperimentsCount_LOG.setBorder(null);

        jLabel11.setText("#Remaining");

        jRemainingExperimentsCount_LOG.setEditable(false);
        jRemainingExperimentsCount_LOG.setText("N/A");
        jRemainingExperimentsCount_LOG.setBorder(null);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jRepeeatIndex_Label)
                            .add(jLOG_ExperimentID_Label))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jLOG_ExperimentID_Text)
                            .add(jRepeatIndex_Text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(4, 4, 4)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(jRepeeatIndex_Label1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLOG_ExperimentID_MAX_Text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(jRepeeatIndex_Label3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jNumberRepeats_Text, 0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel8)
                                    .add(jLabel9)
                                    .add(jLabel10))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jPassedExperimentsCount_LOG, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                                    .add(jRemainingExperimentsCount_LOG, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                                    .add(jFailedExperimentsCount_LOG, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                                    .add(jExperimentsCount_LOG, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)))
                            .add(jLabel11))
                        .add(20, 20, 20))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jRepeeatIndex_Label)
                            .add(jRepeatIndex_Text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLOG_ExperimentID_Label)
                            .add(jLOG_ExperimentID_Text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jRepeeatIndex_Label3)
                            .add(jNumberRepeats_Text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jRepeeatIndex_Label1)
                            .add(jLOG_ExperimentID_MAX_Text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .add(18, 18, 18)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel9)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel11))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jExperimentsCount_LOG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPassedExperimentsCount_LOG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jFailedExperimentsCount_LOG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRemainingExperimentsCount_LOG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setText("No. of Experiments");

        jLabel6.setText("#Passed");

        jLabel7.setText("#Failed");

        jRemainingExperimentsCount.setEditable(false);
        jRemainingExperimentsCount.setText("N/A");
        jRemainingExperimentsCount.setBorder(null);

        jFailedExperimentsCount.setEditable(false);
        jFailedExperimentsCount.setText("N/A");
        jFailedExperimentsCount.setBorder(null);

        jPassedExperimentsCount.setEditable(false);
        jPassedExperimentsCount.setText("N/A");
        jPassedExperimentsCount.setBorder(null);

        jExperimentsCount.setEditable(false);
        jExperimentsCount.setText("N/A");
        jExperimentsCount.setAutoscrolls(false);
        jExperimentsCount.setBorder(null);

        jLabel2.setText("#Remaining");

        jCurrentRepeat_ProgressBar.setFocusable(false);

        jOverallProgress_Label1.setText("Current Repeat Progress");

        jCSV_ExperimentID_Label.setText("CSV-ExperimentID:");

        jCSV_ExperimentID_Text.setEditable(false);
        jCSV_ExperimentID_Text.setText("N/A");
        jCSV_ExperimentID_Text.setBorder(null);

        jRepeeatIndex_Label2.setText("/");

        jCSV_ExperimentID_MAX_Text.setEditable(false);
        jCSV_ExperimentID_MAX_Text.setText("N/A");
        jCSV_ExperimentID_MAX_Text.setBorder(null);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jCurrentRepeat_ProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .add(jOverallProgress_Label1)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jCSV_ExperimentID_Label)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jCSV_ExperimentID_Text, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRepeeatIndex_Label2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jCSV_ExperimentID_MAX_Text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel5)
                            .add(jLabel6)
                            .add(jLabel7))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPassedExperimentsCount, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                            .add(jRemainingExperimentsCount, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                            .add(jFailedExperimentsCount, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                            .add(jExperimentsCount, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)))
                    .add(jLabel2))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jOverallProgress_Label1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCurrentRepeat_ProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jCSV_ExperimentID_Label)
                        .add(jCSV_ExperimentID_Text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jCSV_ExperimentID_MAX_Text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jRepeeatIndex_Label2)))
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel6)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jExperimentsCount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPassedExperimentsCount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jFailedExperimentsCount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRemainingExperimentsCount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jOverallProgress_Label2.setText("Overall Progress");

        jOverall_ProgressBar.setFocusable(false);

        jLabel4.setText("Elapsed Time");

        jTextElapsedTime.setEditable(false);
        jTextElapsedTime.setText("N/A");
        jTextElapsedTime.setBorder(null);

        org.jdesktop.layout.GroupLayout jMainPanelLayout = new org.jdesktop.layout.GroupLayout(jMainPanel);
        jMainPanel.setLayout(jMainPanelLayout);
        jMainPanelLayout.setHorizontalGroup(
            jMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jMainPanelLayout.createSequentialGroup()
                        .add(jMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jButtonStart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 214, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jMainPanelLayout.createSequentialGroup()
                                .add(jLabel1)
                                .add(26, 26, 26)
                                .add(jRunID_Text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jMainPanelLayout.createSequentialGroup()
                                .add(jLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jTextDegreeParallelism, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jButtonQuit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)))
                    .add(jMainPanelLayout.createSequentialGroup()
                        .add(jMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jMainPanelLayout.createSequentialGroup()
                                .add(jLabel4)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jTextElapsedTime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))
                            .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jOverallProgress_Label2)
                    .add(jOverall_ProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jMainPanelLayout.setVerticalGroup(
            jMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jRunID_Text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(jTextDegreeParallelism, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonStart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonQuit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jOverallProgress_Label2)
                .add(5, 5, 5)
                .add(jOverall_ProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jMainPanelLayout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(23, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jMainPanelLayout.createSequentialGroup()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel4)
                            .add(jTextElapsedTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(36, 36, 36))))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jMainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jMainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonStartActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonStartActionPerformed
    {//GEN-HEADEREND:event_jButtonStartActionPerformed
        started = true;
        jButtonStart.setEnabled(false);
        jButtonQuit.setEnabled(true);
        
    }//GEN-LAST:event_jButtonStartActionPerformed

    private void jButtonQuitActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonQuitActionPerformed
    {//GEN-HEADEREND:event_jButtonQuitActionPerformed
        quitted = true;
        jButtonQuit.setEnabled(false);
    }//GEN-LAST:event_jButtonQuitActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonQuit;
    private javax.swing.JButton jButtonStart;
    private javax.swing.JLabel jCSV_ExperimentID_Label;
    private javax.swing.JTextField jCSV_ExperimentID_MAX_Text;
    private javax.swing.JTextField jCSV_ExperimentID_Text;
    private javax.swing.JProgressBar jCurrentRepeat_ProgressBar;
    private javax.swing.JTextField jExperimentsCount;
    private javax.swing.JTextField jExperimentsCount_LOG;
    private javax.swing.JTextField jFailedExperimentsCount;
    private javax.swing.JTextField jFailedExperimentsCount_LOG;
    private javax.swing.JLabel jLOG_ExperimentID_Label;
    private javax.swing.JTextField jLOG_ExperimentID_MAX_Text;
    private javax.swing.JTextField jLOG_ExperimentID_Text;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jMainPanel;
    private javax.swing.JTextField jNumberRepeats_Text;
    private javax.swing.JLabel jOverallProgress_Label1;
    private javax.swing.JLabel jOverallProgress_Label2;
    private javax.swing.JProgressBar jOverall_ProgressBar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jPassedExperimentsCount;
    private javax.swing.JTextField jPassedExperimentsCount_LOG;
    private javax.swing.JTextField jRemainingExperimentsCount;
    private javax.swing.JTextField jRemainingExperimentsCount_LOG;
    private javax.swing.JTextField jRepeatIndex_Text;
    private javax.swing.JLabel jRepeeatIndex_Label;
    private javax.swing.JLabel jRepeeatIndex_Label1;
    private javax.swing.JLabel jRepeeatIndex_Label2;
    private javax.swing.JLabel jRepeeatIndex_Label3;
    private javax.swing.JTextField jRunID_Text;
    private javax.swing.JTextField jTextDegreeParallelism;
    private javax.swing.JTextField jTextElapsedTime;
    // End of variables declaration//GEN-END:variables
    
    private static void checkSyntaxQuitIfError(String[] cmdLineArgs)
    {
        if (cmdLineArgs.length != 0)
            return;
        
        String errmsg1 = "<SIDnetCSVRunner>[ERROR]: Invalid number of arguments specified";
        String errmsg2 = "syntax: SIDnetCSVRunner <fileName>.csv [-runid=#] [-experimentid=#] [-linux] [ -demo | -experiment ] [-parallelism=#] [-repeat=# [-initialrepeat=#]]\n";
               errmsg2+= "                                                                                                                       \n";
               errmsg2+= "\t-parallelism: specify the number of simulations to be run in parallel (OPTIONAL)                                     \n";
               errmsg2+= "\t              Rule of thumb value: number of cores your CPU has if Simulation runs at MAX                            \n";
               errmsg2+= "\t                                   number of cores / 2 if the simulation runs between X1 - ACCEL                     \n";
               errmsg2+= "\t              DEFAULT: 1                                                                                             \n";
               errmsg2+= "\t-repeat: specify the number of time the simulations will be repeated. (OPTIONAL)                                     \n";
               errmsg2+= "\t              If > 1, then the simulationId will be computed as follows:                                             \n";
               errmsg2+= "\t                      Actual simulationId = .csv's simulationId * repeat                                             \n";
               errmsg2+= "\t              It is supplied as args[0] in the command line to the driver                                            \n";
               errmsg2+= "\t              It can be used in the Driver to generate a different random deployment                                 \n";
               errmsg2+= "\t              DEFAULT: 1                                                                                             \n";
               errmsg2+= "\t-firstrepeat: specify which repeat Number to start with (OPTIONAL)                                                 \n";
               errmsg2+= "\t              This is useful when attempting to split the repeats across multiple computers, say                     \n";
               errmsg2+= "\t              one machine is doing repeat 1 through 30, another 31 through 50. So, for this example                  \n";
               errmsg2+= "\t              Machine 1: -repeat=30 -firstrepeat=1                                                                 \n";
               errmsg2+= "\t              Machine 2: -repeat=31 -firstrepeat=20                                                                \n";
               errmsg2+= "\t              DEFAULT: 1                                                                                             \n";
        System.out.println(errmsg1);
        System.out.println(errmsg2);
        JOptionPane.showMessageDialog(null,
                                      errmsg1 + " \n\n " + errmsg2,
                                      "ERROR", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
    
    private static void checkExperimentFileSpecificationQuitIfError(String[] cmdLineArgs) {
        if(cmdLineArgs[0].endsWith(".csv"))
            return;
        
        String errmsg1 = "<SIDnetCSVRunner>[ERROR]: Invalid filename: \"" + (cmdLineArgs[0]) + "\";   Only .csv files supported. The filename must have the .csv extension";
        String errmsg2 = "syntax: SIDnetCSVRunner <fileName>.csv [-runid=#] [experimentid=#] [ -demo | -experiment ]";
        System.out.println(errmsg1);
        System.out.println(errmsg2);
        JOptionPane.showMessageDialog(null,
                                      errmsg1 + " \n\n " + errmsg2,
                                      "ERROR", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
                   
    }        
            
    private static void parseCommandLineArguments(String[] cmdLineArgs) 
    throws Exception {
        checkSyntaxQuitIfError(cmdLineArgs);
        checkExperimentFileSpecificationQuitIfError(cmdLineArgs);
         
        // Parse command line arguments        
        degreeOfParallelism = (int)extract(cmdLineArgs, PARALLELISM_KEY  , PARALLELISM_DEFAULT        ); 
        userExperimentId    =      extract(cmdLineArgs, EXPERIMENTID_KEY , EXPERIMENTID_DEFAULT       );
        userRunId           =      extract(cmdLineArgs, RUNID_KEY        , RUNID_DEFAULT              );
        REPEAT_COUNT        = (int)extract(cmdLineArgs, REPEAT_KEY       , REPEAT_COUNT_DEFAULT       );
        REPEAT_INDEX_BEGIN  = (int)extract(cmdLineArgs, FIRST_REPEAT_KEY , REPEAT_INDEX_BEGIN_DEFAULT );
        linuxEnv            =      extract(cmdLineArgs, LINUX_KEY);        
        
        repeatIndex = REPEAT_INDEX_BEGIN;
        
        if (userRunId == -1)
            runId = retrieveRunId();
        else
            runId = userRunId;  
        
        // working directory (new File(workingDirectory)).getAbsolutePath()
        File file = new File(cmdLineArgs[0]);
        String path = file.getCanonicalPath();
        String filename = file.getName();
        String[] split = path.split(filename);
        if (split.length > 0)
        {
            workingDirectory = split[0];
            // get rid of trailing '/' or  '\'
            workingDirectory = workingDirectory.substring(0, workingDirectory.length()-1);            
        }
        else
            workingDirectory = "";
        
        if (workingDirectory.equals(""))
            if (!linuxEnv)
                workingDirectory = DEFAULT_WORKING_DIRECTORY;
    }
    
    private static void updateGUI(SIDnetCSVRunner runnerInterface) {
        // Update GUI 
        runnerInterface.jRunID_Text.setText("" + runId);
        runnerInterface.jNumberRepeats_Text.setText("" + REPEAT_COUNT);
        runnerInterface.jTextDegreeParallelism.setText("" + degreeOfParallelism);
       
        runnerInterface.jExperimentsCount.setText("" + totalNumberOfExperiments);
        runnerInterface.jCSV_ExperimentID_MAX_Text.setText("" + totalNumberOfExperiments);
        runnerInterface.jLOG_ExperimentID_MAX_Text.setText("" + totalNumberOfExperiments * REPEAT_COUNT);
        runnerInterface.jExperimentsCount_LOG.setText("" + totalNumberOfExperiments_LOG);

        runnerInterface.jTextElapsedTime.setText("0s");
        runnerInterface.jCurrentRepeat_ProgressBar.setMaximum((int)totalNumberOfExperiments);
        runnerInterface.jCurrentRepeat_ProgressBar.setString("CSV Experiment Count");
        runnerInterface.jCurrentRepeat_ProgressBar.setStringPainted(true);
        runnerInterface.jOverall_ProgressBar.setMaximum((int)totalNumberOfExperiments * REPEAT_COUNT);
        runnerInterface.jOverall_ProgressBar.setString("Overall (LOG) experiment count");
        runnerInterface.jOverall_ProgressBar.setStringPainted(true);

        // Overall
        runnerInterface.jPassedExperimentsCount_LOG.setText("0");

        runnerInterface.jPassedExperimentsCount.setText("0");
        runnerInterface.jFailedExperimentsCount.setText("0");
        runnerInterface.jRemainingExperimentsCount.setText("" + totalNumberOfExperiments);
    }
    
    private static void createDirStructure_And_DisplaySIDnetCSVRunnerStartConfirmation() {
        System.out.println("\n\nSIDnetCSVRunner started!");
        System.out.println("**************************************************************************************");
        if (linuxEnv)
            System.out.println("*\n* Operating System Mode: Linux");
        else
            System.out.println("*\n* Operating System Mode: Windows (For Linux users, add -linux)");
        System.out.println("*\n* Working/Current directory: " + workingDirectory);
        
        prepareExperimentsDirectory();
        
        System.out.println("*\n* Degree of parallelism: " + degreeOfParallelism);
        System.out.println("*\n**************************************************************************************");
    }
    
    private static void configureLoggers() {
        //configure loggers
        configureSummaryLogger("run" + runId + "-" + SUMMARY_LOG_FILENAME_PREFIX);
        configureDetailedLogger("run" + runId + "-" + DETAILED_LOG_FILENAME_PREFIX);
    }
    
    private static void prepareExperimentsDirectory() {
        // Create a dedicated directory
        if (!linuxEnv)
        {
            if (workingDirectory.equals(""))
                experimentsTargetDirectory =                    ".\\run" + runId + "\\";
            else
                experimentsTargetDirectory = workingDirectory + "\\run" + runId + "\\";
        }
        else
            experimentsTargetDirectory = workingDirectory + "/" +
                    "run" + runId + "/";
        
        // replace '\' with '\\'
        experimentsTargetDirectory = experimentsTargetDirectory.replace("\\", "\\\\");
        
        // Create the experiments target directory, if not existant
        if (new File(experimentsTargetDirectory).exists())
            System.out.println("*\n* Target directory: " + experimentsTargetDirectory + " already exists. Only experiments associated to the missing experiments log files will be run!");
        else
        {
            boolean success = (new File(experimentsTargetDirectory)).mkdir();
            if (success) {
              System.out.println("*\n* New target directory: " + experimentsTargetDirectory + " succesfully created");
            }    
            else 
            {
                System.out.println("*\n* Cannot create experiment target directory: " + experimentsTargetDirectory);
                System.exit(1);
            }
        }
    }
    
    private static void preScanCSVFile(String[] cmdLineArgs) 
    throws Exception {
        CSVReader reader = openCSVFile(cmdLineArgs);
        
        writeDetailedLogHeader(cmdLineArgs);
        
        header = parseCSVFileAndReturnHeader(reader);
    }        
        
    private static String[] parseCSVFileAndReturnHeader(CSVReader reader)
    throws Exception {
        String [] nextLine;
        String[] header = null;
        int lineNumber = -1; // 0 is header

        SIDnetInstanceList = new LinkedList<SIDnetInstanceLauncher>();
        if (userExperimentId == UNDEFINED)
        {
            // determine the number of experiments
            lineNumber=-1;
            while ((nextLine = reader.readNext()) != null && nextLine[1].length() > 0)
            {
                lineNumber ++;
                if (lineNumber == 0) {
                    //continue; // skip the header
                    if (header == null)
                    	header = new String[nextLine.length];
                    for (int i = 0; i < nextLine.length; i++)
                    	header[i] = nextLine[i];
                    continue;
                }
                // determine if this particular experiment needs to be performed or not
                //System.out.println("lineNumber = " + lineNumber);
                //for (int i = 0; i < nextLine.length; i++)
                    //System.out.println(nextLine[i]);
                if (1 > nextLine.length) {
                    System.err.println("Invalid content found at the end of the .csv file. Please check and erase the possible extra lines at the end of the .csv file");
                    System.exit(1);
                }
                current_CSV_ExperimentId = Long.parseLong(nextLine[1]);
                File logFile = new File(experimentsTargetDirectory, "err-rpt" + repeatIndex + "-exp" + current_CSV_ExperimentId + ".tmp");
                if (!logFile.exists())
                    totalNumberOfExperiments++;
            }
        }
        else
            totalNumberOfExperiments = 1;
        
        totalNumberOfExperiments_LOG = totalNumberOfExperiments * REPEAT_COUNT;
        
        return header;
    }
    
    private static CSVReader openCSVFile(String[] cmdLineArgs) {
        CSVReader reader = null;
        try{
             reader = new CSVReader(new FileReader(cmdLineArgs[0]));
        }catch(IOException e){
            if (e instanceof FileNotFoundException) {
                if (cmdLineArgs[0].length() == 0)
                    JOptionPane.showMessageDialog(null,
                                      "No CSV file (.csv) has been specified as command line argument\n\n" +
                                      " Quitting ...",
                                      "ERROR", JOptionPane.ERROR_MESSAGE);
                else
                    JOptionPane.showMessageDialog(null,
                                      "The following CSV File:\n\n \"" + cmdLineArgs[0] + "\"\n\n could not be found in the default working directory" +
                                      "\n\nQuitting ...",
                                      "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            System.err.println("[SIDnetCSVRunner][openCSVFile] - quitting");
            System.exit(1);
        }
        return reader;
    }
    
    private static void writeDetailedLogHeader(String[] cmdLineArgs) {
         /* ************** Detailed logger *************** */
        detailedLog.info("Detailed Report for SIDnet Experiments Run #" + runId +"\n\n");
        detailedLog.info("Date/Time                       : " + experimentsStartDateAndTime);
        detailedLog.info("CSV file                        : " + cmdLineArgs[0]);
        detailedLog.info("********************************************************** ");
    }
    
    private static void awaitUserSelection() 
    throws Exception {
        // await for the user to press that START button (or the QUIT button)          
         while (!started && !quitted)
            Thread.sleep(100);
         
         //if (quitted)
           // return quitted;
    }
  
    private static void commitSummaryLogger(String[] cmdLineArgs) {
          /* ************** Summary logger *************** */
        long elapsedTime = System.currentTimeMillis() - startTime;
        summaryLog.info("Summary Report for SIDnet Experiments Run #" + runId + "\n\n");
        summaryLog.info("Date/Time                       : " + experimentsStartDateAndTime);
        summaryLog.info("CSV file                        : " + cmdLineArgs[0]);
        summaryLog.info("Total Number Attempted Runs     : " + totalNumberAttemptedRuns);
        summaryLog.info("Total Number Aborted Runs       : " + totalNumberAbortedRuns);
        if (totalNumberAttemptedRuns != 0)
            summaryLog.info("Percentage successful runs      : " + (totalNumberAttemptedRuns - totalNumberAbortedRuns) * 100 / totalNumberAttemptedRuns + " %");
        else
            summaryLog.info("Percentage successful runs      : 0 %");
        summaryLog.info("Experiments ended at            : " + getDateTime());
        summaryLog.info("Experiment duration             : " + elapsedFormatedTime(elapsedTime));
        if (totalNumberAttemptedRuns != 0)
            summaryLog.info("Average time per run            : " + elapsedFormatedTime(elapsedTime/totalNumberAttemptedRuns));
        else
            summaryLog.info("Average time per run            : N/A");
        summaryLog.info("\n\nExperiments Matrix");
        String str="";
        int count = 0;
        int interval = 20;
        // build horizontal ruller
        for (int i = 0; i < interval; i++ )
            if (i < 10)
                str += i + "  ";
            else
                str += i + " ";

        summaryLog.info("\t\t" + str);
        str = "";

        for (Integer errorCode: experimentsMatrix)
        {
            if (count % interval == 0)
            {
                if (count != 0)
                {
                    String padding = "";
                    String header = "[" + (count/interval-1)*interval + " ... " + (count/interval)*interval + "]: ";
                    for (int i = 0; i < 16 - header.length(); i++)
                        padding +=" ";
                    summaryLog.info( header + padding + str);
                }
                str = "";
            }
            if (errorCode == 0)
                str += ".  ";
            else
            {
                if (errorCode < 10)
                    str += errorCode + "  ";
                else
                    str += errorCode + " ";
            }
            count++;
        }
        if ((count-1) % interval != 0)
        {
            String padding = "";
            String header = "[" + (count/interval)*interval + " ... " + (count/interval+1)*interval + "]: ";
            for (int i = 0; i < 16 - header.length(); i++)
                 padding +=" ";
            summaryLog.info(header + padding + str);
        }
    }
    
    private static void displayEndExperimentsReport(int lineNumber)
    {
        if (totalNumberAttemptedRuns == 0)
        {
            if (lineNumber <= 1)
                System.out.println("[WARNING] The indicated .csv file does not contain any experiments parameters");
            else
                if (userExperimentId != -1)
                    System.out.println("[WARNING] The indicated experiment ID# ["  + userExperimentId + "] cannot be found in the .csv");
        }
    }
    
    private static void handleTrailingExperiments() throws Exception
    {
        while(SIDnetInstanceList.size() > 0) // this must not be checked for quitted
        {
            SIDnetInstanceLauncher removable = null;

            // check for SIDnet instance terminations
            for(SIDnetInstanceLauncher SIDnetInstance: SIDnetInstanceList)
            {
                if (SIDnetInstance.isAlive() && SIDnetInstance.terminated())                             
                {                        
                    removable = SIDnetInstance;
                    if (SIDnetInstance.failedExperiment())
                    {
                        failedExperimentsCount++;
                        failedExperimentsCount_LOG++;
                    }
                    else
                    {
                        passedExperimentsCount++;
                        passedExperimentsCount_LOG++;
                    }
                    SIDnetInstance.kill();

                    // commit temp error logs
                    //commitTempErrorFile(experimentsTargetDirectory, "err-Exp" + launchedExperiments.getFirst() + ".tmp", consecutiveExperimentsNumber, launchedExperimentsIds.getFirst());
                    //launchedExperiments.removeFirst();
                    //launchedExperimentsIds.removeFirst();
                    FileUtils.appendToFilesWithPrefix(experimentsTargetDirectory, "run" + SIDnetInstance.getRunNumber() + "-rpt" + SIDnetInstance.getRepeatIndex() + "-exp" + SIDnetInstance.getExperimentId(), "\n<elapsedTimeMinutes>" + (System.currentTimeMillis() - SIDnetInstance.getStartTimeMillis())/60000 + "</minutes>\n");
                    break;       
                } 
                else
                    if (quitted)
                        SIDnetInstance.quit();
            }
            if (removable != null)
                SIDnetInstanceList.remove(removable);             
             Thread.sleep(1000);
        }
    }
    
    private static int executeMainExperimentsLoop(SIDnetCSVRunner runnerInterface, String[] cmdLineArgs)
    throws Exception {
        int lineNumber = -1;
        
        long last_CSV_ExperimentId = UNDEFINED;
        startTime = System.currentTimeMillis();
        while(repeatIndex < REPEAT_INDEX_BEGIN + REPEAT_COUNT) {
            totalNumberAttemptedRuns = 0;
            remainingExperimentsCount = 0;
            passedExperimentsCount = 0;
            failedExperimentsCount = 0;
            
            // Update GUI
            runnerInterface.jRepeatIndex_Text.setText("" + repeatIndex);
                    
            // reopen the reader
            CSVReader reader = null;
            try{
                 reader = new CSVReader(new FileReader(cmdLineArgs[0]));
            }catch(IOException e){e.printStackTrace();}

            // MAIN LOOP to execute one set of experiments, once
            lineNumber = -1;
            String [] nextLine;
            while ((nextLine = reader.readNext()) != null && !quitted) {                
                lineNumber++;      
                if (lineNumber == 0)
                    System.out.println("<"+cmdLineArgs[0] + "> Header read succesfully");
                else {         
                    // Check for terminated instances of simulator.
                    while(SIDnetInstanceList.size() >= degreeOfParallelism && !quitted) {
                        SIDnetInstanceLauncher removable = null;
                        // check for SIDnet instance terminations
                        for(SIDnetInstanceLauncher SIDnetInstance: SIDnetInstanceList) {
                            if (!SIDnetInstance.isAlive()) {
                                removable = SIDnetInstance;
                                FileUtils.appendToFilesWithPrefix(experimentsTargetDirectory, "run" + SIDnetInstance.getRunNumber() + "-rpt" + SIDnetInstance.getRepeatIndex() + "-exp" + SIDnetInstance.getExperimentId(), "\n<elapsedTimeMinutes>   " + (System.currentTimeMillis() - SIDnetInstance.getStartTimeMillis())/60000 + "   </elapsedTimeMinutes>\n");
                                break;
                            }
                             // Check to see if run-time errors occurr
                            if (SIDnetInstance.isAlive() && SIDnetInstance.terminated()) {
                                if (SIDnetInstance.failedExperiment()) {
                                    failedExperimentsCount++;
                                    failedExperimentsCount_LOG++;     
                                }
                                else {
                                    passedExperimentsCount++;
                                    passedExperimentsCount_LOG++;
                                }
                                System.out.println("TERMINATE");
                                SIDnetInstance.kill();
                                break;
                            }
                        }
                        if (removable != null)
                            SIDnetInstanceList.remove(removable);
                        try{
                            runnerInterface.jTextElapsedTime.setText(elapsedFormatedTime(System.currentTimeMillis() - startTime));
                            Thread.sleep(1000);
                        }catch(Exception e){e.printStackTrace();};
                    }

                    if (nextLine[1].equals(""))
                        break;

                    current_CSV_ExperimentId = Long.parseLong(nextLine[1]);
                    // Check the requirement that experimentId are specified in increasing monotonic order
                    // Also, their ids needs to be consecutive
                    if (last_CSV_ExperimentId != UNDEFINED && last_CSV_ExperimentId != current_CSV_ExperimentId - 1) {
                            Exception e = new Exception("[ERROR][SIDnetCSVRunner] - current_CSV_ExperimentId " + current_CSV_ExperimentId + "\n violates requirement that experimentsIDs must be consecutive, increasing monotonically. The first experimentId must be >=1. Quitting");
                            e.printStackTrace();
                            System.exit(1);
                        }
                    last_CSV_ExperimentId = current_CSV_ExperimentId;
                    
                    if (userExperimentId != UNDEFINED && userExperimentId != current_CSV_ExperimentId /*expId*/)
                        continue;

                    if (quitted)
                        break;

                
                    File logFile = new File(experimentsTargetDirectory, "err-rpt" + repeatIndex + "-exp" + current_CSV_ExperimentId + ".tmp");
                    if (logFile.exists()) {
                        System.out.println("\n---------------------\n Skipping SIDnet [" + runId +"," + repeatIndex + "," + current_CSV_ExperimentId + "] because experiments results already exist in target directory for run# " + runId + " --------------------------------------------\n---------------------\n");
                        // check to see if the log file is empty or not
                        if (FileUtils.isEmpty(logFile)) {
                            passedExperimentsCount++;
                            passedExperimentsCount_LOG++;
                        } else {
                            failedExperimentsCount++;
                            failedExperimentsCount_LOG++;
                        }
                        continue;
                    } else {
                        // remove any existing log file for this runid/experimentid
                        FileUtils.deleteFilesWithPrefix(experimentsTargetDirectory, "run" + runId + "-rpt" + repeatIndex + "-exp" + current_CSV_ExperimentId);
                    }

                    totalNumberAttemptedRuns++;
                    totalNumberAttemptedRuns_LOG++;
                    
                    current_LOG_ExperimentId = totalNumberAttemptedRuns_LOG;

                    remainingExperimentsCount = totalNumberOfExperiments - totalNumberAttemptedRuns;
                    remainingExperimentsCount_LOG = totalNumberOfExperiments_LOG - totalNumberAttemptedRuns_LOG;

                    runnerInterface.jPassedExperimentsCount_LOG.setText("" + passedExperimentsCount_LOG);
                    runnerInterface.jFailedExperimentsCount_LOG.setText("" + failedExperimentsCount_LOG);
                    runnerInterface.jRemainingExperimentsCount_LOG.setText("" + remainingExperimentsCount_LOG);
                    
                    runnerInterface.jPassedExperimentsCount.setText("" + passedExperimentsCount);
                    runnerInterface.jFailedExperimentsCount.setText("" + failedExperimentsCount);
                    runnerInterface.jRemainingExperimentsCount.setText("" + remainingExperimentsCount);


                    System.out.println("\n---------------------\n Launching SIDnet [RunId: " + runId + ",repeatIndex:" + repeatIndex + ",CSV_ExperimentId:" + current_CSV_ExperimentId + "] --------------------------------------------\n---------------------\n");
              
                    String arguments = "";
                    String[] argumentArray = new String[nextLine.length];
                    argumentArray[0] = nextLine[0];
                    for (int i = 1; i < nextLine.length; i++) {
                        arguments += nextLine[i] + " ";
                        argumentArray[i] = nextLine[i];
                    }

                    // Update the progress bar of the current repeat
                    runnerInterface.jCurrentRepeat_ProgressBar.setValue(lineNumber);
                    runnerInterface.jCurrentRepeat_ProgressBar.setString("#"+lineNumber);
                    
                    // Update the progress bar of the overall process
                    runnerInterface.jOverall_ProgressBar.setValue(totalNumberAttemptedRuns_LOG);
                    runnerInterface.jOverall_ProgressBar.setString("#"+ totalNumberAttemptedRuns_LOG);
                    
                    // Update other GUI
                    runnerInterface.jCSV_ExperimentID_Text.setText("" + current_CSV_ExperimentId);
                    runnerInterface.jLOG_ExperimentID_Text.setText("" + current_LOG_ExperimentId);

                    String[] extendedHeaderArray = new String[header.length + 4];
                    String[] extendedArgumentArray = new String[argumentArray.length + 4];
                    
                    for (int i = 0; i < header.length; i++) {
                    	extendedHeaderArray[i] = header[i];
                    	extendedArgumentArray[i] = argumentArray[i];
                    }
                    
                    // add extra info
                    extendedHeaderArray[header.length] = "runId";
                    extendedArgumentArray[header.length] = "" + runId;
                    extendedHeaderArray[header.length + 1] = "repeatIndex";
                    extendedArgumentArray[header.length + 1] = "" + repeatIndex;
                    extendedHeaderArray[header.length + 2] = "current_CSV_ExperimentId";
                    extendedArgumentArray[header.length + 2] = "" + current_CSV_ExperimentId;
                    extendedHeaderArray[header.length + 3] = "experimentsTargetDirectory";
                    extendedArgumentArray[header.length + 3] = "\"" + experimentsTargetDirectory + "\"";
                    	
                    String systemProperties = buildSystemProperties(extendedHeaderArray, extendedArgumentArray);
                    String command = "java -Xms256m -Xmx512m " + systemProperties + " jist.runtime.Main ";
                    if (linuxEnv)
                        command += "--nocache "; // disable caching in linux environments
                    command += "jist.swans.Main " + nextLine[0] + " " + repeatIndex + " " + experimentsTargetDirectory + " " + runId + " " + arguments;
                    System.out.println("Command Line:\n> " + command + "\n");
                    SIDnetInstanceList.add(new SIDnetInstanceLauncher(command, runId, repeatIndex, current_CSV_ExperimentId, experimentsTargetDirectory));
                    try{
                           // runnerInterface.jTextElapsedTime.setText(elapsedFormatedTime(System.currentTimeMillis() - startTime));
                           Thread.sleep(10);
                    }catch(Exception e){e.printStackTrace();};
                    
                    SIDnetInstanceList.getLast().start();
                }            
            } // FOR EVERY RUN OF ALL EXPERIMENTS IN .CSV            
            last_CSV_ExperimentId = UNDEFINED;
            repeatIndex++;
        } // FOR EVERY EXPERIMENT
        
        return lineNumber;
    }
    
    protected static String buildSystemProperties(String[] headerArray, String[] argumentArray) {
    	if (headerArray.length != argumentArray.length) {
    		throw new RuntimeException(
    				"Mismatched argument/header array lengths:" +
    				" headerArray -> " + headerArray.length + ";" +
    				" argumentArray = " + argumentArray.length);
    	}
    	
    	System.out.println("System Properties: ");
    	
    	String systemProperties = " ";    	
    	for (int i = 0; i < headerArray.length; i++) {
    		String newProperty = "-D" + headerArray[i] + "=" + argumentArray[i] + " ";
    		systemProperties += newProperty;
    		System.out.println("\t " + newProperty);
    	}
    	
    	System.out.println("");
    	
    	return systemProperties;
    }
    
    public static void quit() // for testing only
    {
        quitted = true;
    }
    
    /**
     * @param args the command line arguments
     */
     public static void main(String[] cmdLineArgs) throws Exception 
    {
         SIDnetCSVRunner runnerInterface = new SIDnetCSVRunner();
         runnerInterface.setVisible(true);

         parseCommandLineArguments(cmdLineArgs);        
    
         createDirStructure_And_DisplaySIDnetCSVRunnerStartConfirmation();

         configureLoggers();

         preScanCSVFile(cmdLineArgs);
         
         updateGUI(runnerInterface);
         
         awaitUserSelection(); // either START or QUIT. If QUIT, program terminates
         
         if (quitted)
             return;
         
         // If START, then ...
         // Take each experiment and run-it. One-per-core.
         // This terminates either by QUIT-user selection or by exhausting the experiments specified in .CSV
         int lineNumber = executeMainExperimentsLoop(runnerInterface, cmdLineArgs);
        
         // await and properly handle for the last remaining experiments after all CSV-experiments have been launched 
         handleTrailingExperiments();
        
         displayEndExperimentsReport(lineNumber);

         commitSummaryLogger(cmdLineArgs);                 
    }

    public static long extract(String cmdLineArgs[], String KEY, long defaultValue)
    {
        for (int i = 1; i < cmdLineArgs.length; i++)
        {
            if (cmdLineArgs[i].toLowerCase().startsWith(KEY))
            {   
                String substring;
                try{
                    substring = cmdLineArgs[i].split("=", 2)[1];
                }catch(Exception e)
                {
                    i++;
                    substring = cmdLineArgs[i];
                }

                return Integer.parseInt(substring);
            }                    
        }
        return defaultValue;
    }
    
    public static boolean extract(String cmdLineArgs[], String KEY)
    {
        for (int i = 1; i < cmdLineArgs.length; i++)
            if (cmdLineArgs[i].toLowerCase().equals(KEY))
                return true;            
        return false;
    }
    
    public static void commitTempErrorFile(String experimentsTargetDirectory, String filename, long runNumber, long runId)
    {
        int MAX_LENGTH = 30;
        
        detailedLog.info("\n---------------\n Experiment #" + runId + " --------------------------------------------------\n---------------\n");
         
        File tmpFile = new File(experimentsTargetDirectory + filename);
        String nextLine;
        
        try{
            // If the file does not exists, create one
            if (!tmpFile.exists())
            {
                System.out.println("[FATAL ERROR]<SIDnetCSVRunner> - cannot locate temporary error log files \"err-Exp#.tmp\" which should have been created automatically by now");
                detailedLog.info("[FATAL ERROR]<SIDnetCSVRunner> - cannot locate temporary error log files \"err-Exp#.tmp\" which should have been created automatically by now");        
            }
            else
            {
                // Here BufferedInputStream is added for fast reading.
                BufferedReader input = new BufferedReader(new FileReader(tmpFile));
                
                while((nextLine = input.readLine()) != null)
                    detailedLog.info(nextLine);

                tmpFile.delete();
            }
        }catch(Exception e){e.printStackTrace();detailedLog.info("[EXCEPTION]<SIDnetCSVRunner>(CommitTempErrorFile)");};
    }
    
    public static String elapsedFormatedTime(long elapsedTimeMillis)
    {
        String str = "";
        long hours, minutes, seconds, milliseconds;
        
        hours = elapsedTimeMillis/(1000*60*60);
        elapsedTimeMillis -= hours * (1000*60*60);
        
        minutes = elapsedTimeMillis/(1000*60);
        elapsedTimeMillis -= minutes * (1000*60);
        
        seconds = elapsedTimeMillis/(1000);
        elapsedTimeMillis -= seconds * (1000);
        
        milliseconds       = elapsedTimeMillis;
        
        str = "" + hours + "h " + minutes + "m " + seconds + "s " + milliseconds +"ms";
        
        return str;
    }
    
    private static void configureSummaryLogger(String fileNamePrefix)
    {
        // Configure the summary report logger
        String filename = fileNamePrefix + getDateTime() + ".log";
        
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

        summaryLog = Logger.getLogger(filename);
        summaryLog.addAppender(appender);
        summaryLog.setLevel((Level)Level.INFO);      
        summaryLog.setAdditivity(false);
    }

    private static void configureDetailedLogger(String fileNamePrefix)
    {
        // Configure the summary report logger
        String filename = fileNamePrefix  + getDateTime() + ".log";
        
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

        detailedLog = Logger.getLogger(filename);
        detailedLog.addAppender(appender);
        detailedLog.setLevel((Level)Level.INFO);      
        detailedLog.setAdditivity(false);
    }
    
    
    
    public static long retrieveRunId()
    {
        long experimentSuiteIdNumber = 1;
        // Generate the experimentSuite#
        File experimentSuiteFile = new File(EXPERIMENT_CONTOR_FILE);
        
        try{
            // If the file does not exists, create one
            if (!experimentSuiteFile.exists())
            {
                System.out.println("*\n* Experiment contor tracker file (" + EXPERIMENT_CONTOR_FILE + ") does not exists. Creating one that starts at #1");

                experimentSuiteFile.createNewFile();

                FileOutputStream fout = new FileOutputStream(experimentSuiteFile);
                new PrintStream(fout).println(experimentSuiteIdNumber);
                fout.close();
            }
            else
            {
                // Here BufferedInputStream is added for fast reading.
                BufferedReader input = new BufferedReader(new FileReader(experimentSuiteFile));

                // dis.available() returns 0 if the file does not have more lines.
                long lastExperimentSuiteId = Long.parseLong((String)input.readLine());
                experimentSuiteIdNumber = lastExperimentSuiteId + 1;

                // write back the experiment suite id
                System.out.println("*\n* Experiment contor tracker file found. Executing experiment suite #" + experimentSuiteIdNumber);

                experimentSuiteFile.delete();

                experimentSuiteFile.createNewFile();

                FileOutputStream fout = new FileOutputStream(experimentSuiteFile);
                new PrintStream(fout).println(experimentSuiteIdNumber);
                fout.close();
                input.close();
            }
        }catch(Exception e){e.printStackTrace();};
        return experimentSuiteIdNumber;
    }

    
     public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
