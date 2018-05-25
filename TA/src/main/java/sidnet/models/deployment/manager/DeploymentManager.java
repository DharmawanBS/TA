/*
 * DeploymentManager.java
 *
 * Created on April 4, 2008, 1:50 PM
 */

package sidnet.models.deployment.manager;

import sidnet.models.deployment.interfaces.DeploymentModel;
import sidnet.models.deployment.models.xml.*;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import jist.runtime.JistAPI;
import jist.swans.field.Placement;
import sidnet.core.gui.SimGUI;

/**
 *
 * @author  Oliver
 */
public class DeploymentManager 
extends javax.swing.JFrame
implements JistAPI.DoNotRewrite
{
    //DEBUG
    private final static boolean DEBUG = false;
    
    //private static final String DEPLOYMENT_MODELS_PATH = System.getenv("SIDNETDIR") + "\\sidnet\\deployment\\models";
    private static final String DEPLOYMENT_MODELS_PACKAGE = "sidnet.models.deployment.models";
    private boolean deployCommandSubmitted = false;
    private Placement placement = null;
    private DeploymentModel deploymentModel = null;
    private boolean externalCall = false;
    private int fieldLength;
    private int fieldWidth;
    private SimGUI simGUI;
    
    private DeploymentXMLLoader deploymentXMLLoader;
    
    /** Creates new form DeploymentManager */
    public DeploymentManager(int fieldLength, int fieldWidth, SimGUI simGUI) {
        initComponents();
        jButtonDeploy.setEnabled(false);
        this.setVisible(true);
        this.fieldLength = fieldLength;
        this.fieldWidth = fieldWidth;
        this.simGUI = simGUI;
        deploymentXMLLoader = new DeploymentXMLLoader(this, fieldWidth, fieldLength);
        
        initComboBoxModel();
    }
    
    public void deploy() {
    	jButtonDeployMouseClicked(null);
    }
    
    public void initComboBoxModel()
    {
        jComboBoxModel.removeAllItems();
        
        // get a list of java classes in the DEPLOYMENT_MODELS_PACKAGE
        if (DEBUG) System.out.println("Get the classList");
        
        Class[] classList = null;
        try
        {
            classList = getClasses(DEPLOYMENT_MODELS_PACKAGE);
        }catch(Exception e){jTextModelWarning1.setText("Error retrieving available deployment models"); System.err.println("[DeploymentManager]<Error> Error retrieving available deployment models"); e.printStackTrace();};
        
        if (classList == null || classList.length == 0)
        {
            jTextModelWarning1.setText("No deployment models have been found!");
            jTextModelWarning2.setText("Make sure the ''DEPLOYMENTMODELSDIR'' environment variable is properly set");
            jComboBoxModel.setEnabled(false);
            return;
        }
        if (DEBUG) System.out.println("done");
        
        int contor = 0;
        // open each class and check if it is a valid model
        try{
            for (int i = 0; i < classList.length; i++)
            {
                if(DEBUG) System.out.println("classList[" + i +"] = " + classList[i].getName());
                Type[] interfaces = classList[i].getGenericInterfaces();
                for (int j = 0; j < interfaces.length; j++)
                {
                    if(DEBUG) System.out.println("Interface[" + j + "] = " + interfaces[j]);
                    
                    try{
                        
                        if (interfaces[j].equals(Class.forName("sidnet.models.deployment.interfaces.DeploymentModel"))) // only classes which implement the DeploymentModel interface are considered
                        {
                            jComboBoxModel.addItem(classList[i].getSimpleName());
                            if(DEBUG) System.out.println("matching class");
                            contor++;
                        }
                    }
                    catch(Exception e){/* DO NOTHING */ if (!(e instanceof ClassNotFoundException)) e.printStackTrace();};
                }
            }
        }catch(Exception e){/* DO NOTHING */ e.printStackTrace();};
        if (contor == 0)
        {
            jTextModelWarning1.setText("No deployment models have been found!");
            jTextModelWarning2.setText("No class to match sidnet.models.deployment.models");
            jComboBoxModel.setEnabled(false);
        }
    }
    
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }
    
    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if(DEBUG) System.out.println("[DEBUG][DeploymentManager.findClasses()] - directory = " + directory.getName() + " packageName = " + packageName);
        if (!directory.exists()) {
            // it could be the %20 problem
            directory = new File(directory.getPath().replace("%20", " "));
            if (!directory.exists())
                return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                // TODO assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                if(DEBUG) System.out.println("[DEBUG][DeploymentManager.findClasses()] - file.getName() = " + file.getName());
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        jTabbedModelLoader = new javax.swing.JTabbedPane();
        jPanelXML = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButtonXMLBrowse = new javax.swing.JButton();
        jButtonDeploy = new javax.swing.JButton();
        jTextFilename = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextCompatibilityCheck = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxModel = new javax.swing.JComboBox();
        jButtonModelDeploy = new javax.swing.JButton();
        jTextModelWarning1 = new javax.swing.JTextField();
        jTextModelWarning2 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SIDnet Deployment Manager v1.0");
        setResizable(false);
        jLabel1.setText("Specify XML file that contains deployment information");

        jButtonXMLBrowse.setText("BROWSE ... ");
        jButtonXMLBrowse.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonXMLBrowseActionPerformed(evt);
            }
        });

        jButtonDeploy.setText("Deploy");
        jButtonDeploy.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jButtonDeployMouseClicked(evt);
            }
        });

        jTextFilename.setEditable(false);

        jLabel2.setText("Filename:");

        jLabel3.setText("Compatibility Check:");

        jTextCompatibilityCheck.setEditable(false);

        org.jdesktop.layout.GroupLayout jPanelXMLLayout = new org.jdesktop.layout.GroupLayout(jPanelXML);
        jPanelXML.setLayout(jPanelXMLLayout);
        jPanelXMLLayout.setHorizontalGroup(
            jPanelXMLLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelXMLLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelXMLLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jPanelXMLLayout.createSequentialGroup()
                        .add(jPanelXMLLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanelXMLLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jTextFilename)
                            .add(jTextCompatibilityCheck, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelXMLLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jButtonDeploy, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButtonXMLBrowse, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelXMLLayout.setVerticalGroup(
            jPanelXMLLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelXMLLayout.createSequentialGroup()
                .add(13, 13, 13)
                .add(jPanelXMLLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jButtonXMLBrowse))
                .add(jPanelXMLLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelXMLLayout.createSequentialGroup()
                        .add(9, 9, 9)
                        .add(jPanelXMLLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(jTextFilename, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanelXMLLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(jTextCompatibilityCheck, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanelXMLLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonDeploy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jTabbedModelLoader.addTab("XML Loader", jPanelXML);

        jLabel4.setText("Deployment Model");

        jComboBoxModel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButtonModelDeploy.setText("Deploy");
        jButtonModelDeploy.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jButtonModelDeployMouseClicked(evt);
            }
        });

        jTextModelWarning1.setEditable(false);
        jTextModelWarning1.setForeground(new java.awt.Color(255, 0, 0));
        jTextModelWarning1.setAutoscrolls(false);
        jTextModelWarning1.setBorder(null);

        jTextModelWarning2.setEditable(false);
        jTextModelWarning2.setForeground(new java.awt.Color(255, 0, 0));
        jTextModelWarning2.setAutoscrolls(false);
        jTextModelWarning2.setBorder(null);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextModelWarning1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jComboBoxModel, 0, 171, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonModelDeploy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(jTextModelWarning2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(jComboBoxModel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonModelDeploy))
                .add(7, 7, 7)
                .add(jTextModelWarning1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextModelWarning2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jTabbedModelLoader.addTab("Model Based Loader", jPanel2);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedModelLoader, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedModelLoader, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 127, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonModelDeployMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jButtonModelDeployMouseClicked
    {//GEN-HEADEREND:event_jButtonModelDeployMouseClicked
        // TODO add your handling code here:
        if (jButtonModelDeploy.isEnabled())
        {
            System.out.println(jComboBoxModel.getSelectedItem());
            try
            {
                //System.out.println("deployment.models." + jComboBoxModel.getSelectedItem().toString());
                Object[] arguments = new Object[3];
                arguments[0] = fieldLength;
                arguments[1] = fieldWidth;
                arguments[2] = simGUI;
                
                
                deploymentModel = ((DeploymentModel)(Class.forName(DEPLOYMENT_MODELS_PACKAGE +"." + jComboBoxModel.getSelectedItem().toString().toLowerCase() + "." + jComboBoxModel.getSelectedItem().toString()).getConstructors()[0].newInstance(arguments)));
            }catch(Exception e){e.printStackTrace();};
            deployCommandSubmitted = true;
            if (!externalCall) {
                placement = deploymentModel.getPlacement();
                this.dispose();
            }
        }
        
    }//GEN-LAST:event_jButtonModelDeployMouseClicked

    private void jButtonDeployMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jButtonDeployMouseClicked
    {//GEN-HEADEREND:event_jButtonDeployMouseClicked
        if (jButtonDeploy.isEnabled())
        {
            deployCommandSubmitted = true;
            //this.dispose();
        }
    }//GEN-LAST:event_jButtonDeployMouseClicked

    private void jButtonXMLBrowseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonXMLBrowseActionPerformed
    {//GEN-HEADEREND:event_jButtonXMLBrowseActionPerformed
        deploymentXMLLoader.browse();
        //deploymentXMLLoader.setVisible(true);
    }//GEN-LAST:event_jButtonXMLBrowseActionPerformed
    
    /**
     * For testing purposes only
     * @return
     */
    public DeploymentXMLLoader getDeploymentXMLLoader() {
        return deploymentXMLLoader;
    }
    
    public void jXMLLoaderCompletes(String filename, Boolean compatibilityPassed, Placement placement)
    {
        this.placement = placement;
        this.jTextFilename.setText(filename);
        if (compatibilityPassed)
        {
            if (placement == null)
            {
                this.jTextCompatibilityCheck.setForeground(Color.RED);
                this.jTextCompatibilityCheck.setText("passed, but no location information has been found and loaded!");
                jButtonDeploy.setEnabled(false);
            }
            else
            {
                this.jTextCompatibilityCheck.setForeground(Color.BLACK);
                this.jTextCompatibilityCheck.setText("passed");
                jButtonDeploy.setEnabled(true);
            }
        }
        else
        {
            this.jTextCompatibilityCheck.setForeground(Color.RED);
            this.jTextCompatibilityCheck.setText("failed!");
            jButtonDeploy.setEnabled(false);
        }
    }
    
    public Placement getPlacement() {
        externalCall = true;
        
        while (!deployCommandSubmitted || (!deployCommandSubmitted && deploymentModel == null ))
            try { // busy wait
                Thread.sleep(500);
            } catch(Exception e){e.printStackTrace();}
        
        this.dispose();
        
        if (deploymentModel != null)
            return deploymentModel.getPlacement();
        else
            return placement;
    }    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new DeploymentManager(100,100, null).setVisible(true);
            }
        });
    }
    
    /**
     * For testing only
     * @return
     */
    public javax.swing.JButton getJButtonXMLBrowse()
    {
        return jButtonXMLBrowse;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDeploy;
    private javax.swing.JButton jButtonModelDeploy;
    private javax.swing.JButton jButtonXMLBrowse;
    private javax.swing.JComboBox jComboBoxModel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelXML;
    private javax.swing.JTabbedPane jTabbedModelLoader;
    private javax.swing.JTextField jTextCompatibilityCheck;
    private javax.swing.JTextField jTextFilename;
    private javax.swing.JTextField jTextModelWarning1;
    private javax.swing.JTextField jTextModelWarning2;
    // End of variables declaration//GEN-END:variables
    
}
