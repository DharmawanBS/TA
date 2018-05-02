/*
 * XMLLoaderGUI.java
 *
 * Created on November 7, 2007, 1:26 PM
 * 
 * @author  Oliviu C. Ghica, Northwestern University
 * @version 1.0.1
 */

package sidnet.core.gui;

import sidnet.core.interfaces.XMLLoader;
import sidnet.core.interfaces.XMLLoaderListener;
import sidnet.core.misc.FileUtils;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;



import org.w3c.dom.Document;
import sidnet.models.senseable.mob.Mob;

public class XMLLoaderGUI extends javax.swing.JFrame implements XMLLoader{
    
    /* CONFIGURATION */
    private static final String MOB_CLASS_PACKAGE = "sidnet.models.senseable.mob";
    
    // Menu
    private JPopupMenu hostPopupMenu;
    private JMenuItem configMenuItem;
    
    // List Models
    private DefaultListModel modelFound = new DefaultListModel();
    private Map<String, File> fileMap = new HashMap();
    private DefaultListModel modelActive = new DefaultListModel();
    
    // Files
    private File currentDir;
    private File xsdFile;
    
    private Document docu;
    private Map<String, Object> pojoList = new HashMap<String, Object>();;
    private Class pojoProfile;
    
    private XMLLoaderListener xmlListener;
    
    /**
     * Creates new form XMLLoaderGUI
     */
    public XMLLoaderGUI() {
        initComponents();
        this.setVisible(false);

        jList1.setModel(modelFound);
        jList1.setCellRenderer(new CustomCellRenderer());
        jList2.setModel(modelActive);
    }
    
    public void enableLoaderFromXML(String xmlSchemaDirectory,
    								String xsdSchemaFilename,
    								String frameTitle,
    								Class pojoProfile,
    								XMLLoaderListener xmlListener) {
        if (this.isVisible() || pojoProfile == null || xmlListener == null)
            return;
                
        this.setTitle(frameTitle);
        this.setVisible(true);
        
        this.pojoProfile = pojoProfile;
        
        this.xmlListener = xmlListener;
        
        // Designate the directory in which to look for files
        currentDir = new File(System.getProperty("user.dir"));

        if (xsdSchemaFilename != null)
            xsdFile = new File(currentDir, xsdSchemaFilename);
        else
            xsdFile = null;             
        
        // Build an XML filter to rule out non-xml files and xml-files not following the MobSchema.xsd
        /*FilenameFilter xmlFilter = new FilenameFilter() {
            public boolean accept(File dir, String fileName) {
                if (fileName.endsWith(".xml"))
                    return xmlValidWithSchema(xsdFile, new File(dir, fileName));
                return false;
            }
        };
        
        if (modelFound.size() > 0)
            return;
        
        String[] fileNameList = currentDir.list();
        
        if (fileNameList != null)        
	        for (int i = 0; i < fileNameList.length; i++) {
	            System.out.println("current directory files = " + fileNameList[i]);
	            
	            if (fileNameList[i].endsWith(".xml")) {
	                if (!xmlFilter.accept(currentDir, fileNameList[i]))
	                    modelFound.addElement(fileNameList[i] + " ");
	                else
	                    modelFound.addElement(fileNameList[i]);
	            }
	        }
	        */
    }
    
    private boolean xmlValidWithSchema(File schemaFile, File xmlFile) {
        if (schemaFile == null)
            return true;
        if (xmlFile == null)
            return false;
        
        DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
        
        try {
            factory.newDocumentBuilder().parse(xmlFile);
        } catch (Exception e) {// catch all the other exceptions
            return false;
        }
        return true;
    }
    
    private Object xmlParser(File xmlFile) {
        Object pojo = null;
        
        try{
            JAXBContext context = JAXBContext.newInstance(MOB_CLASS_PACKAGE) ;

            Unmarshaller unmarshaller = context.createUnmarshaller() ;

            pojo = (Mob)unmarshaller.unmarshal(xmlFile) ;
        }
        catch (Exception e){e.printStackTrace(); } ;
        
        return pojo;
    }
    

    class CustomCellRenderer extends DefaultListCellRenderer {
        
        
        public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean hasFocus) {
            JLabel label =
                    (JLabel)super.getListCellRendererComponent(list,
                    value,
                    index,
                    isSelected,
                    hasFocus);
            
            if(((String)value).endsWith(" "))
                label.setForeground(Color.red);
            else
                label.setForeground(Color.black);
            
            return(label);
        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("XML Profile Loader");
        setResizable(false);

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList2);

        jLabel1.setText("Available (.XML)  Profiles");

        jLabel2.setText("Selected Profiles");

        jButton1.setText("/\\");
            jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    jButton1MouseClicked(evt);
                }
            });

            jButton2.setText("\\/");           
            jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    jButton2MouseClicked(evt);
                }
            });

            jButton3.setText("Cancel");
            jButton3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });

            jButton4.setText("OK / Close");
            jButton4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });

            jButton5.setText("Load XML Mobility Profiles ...");
            jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    jButton5MouseClicked(evt);
                }
            });

            org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                        .add(layout.createSequentialGroup()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(layout.createSequentialGroup()
                                    .add(jLabel2)
                                    .add(231, 231, 231)
                                    .add(jButton2)
                                    .add(29, 29, 29))
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                    .add(jButton4)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jButton3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(layout.createSequentialGroup()
                                    .add(20, 20, 20)
                                    .add(jButton1))))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                        .add(layout.createSequentialGroup()
                            .add(jLabel1)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 139, Short.MAX_VALUE)
                            .add(jButton5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 212, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(19, 19, 19))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .addContainerGap()
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jLabel1)
                        .add(jButton5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel2)
                        .add(jButton2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jButton3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jButton4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        pojoProfile = null;     
        
        this.setVisible(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    	List<Object> list = new LinkedList<Object>();
    	    	
    	for (int i = 0; i < modelActive.size(); i++) {
    		if (pojoList.get(modelActive.elementAt(i)) != null)
    			list.add(pojoList.get(modelActive.elementAt(i)));
    	}
    	
        xmlListener.handleParsedObjects(list);
        
        this.setVisible(false);
    }//GEN-LAST:event_jButton4ActionPerformed
    
    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        System.out.println("Number of selected values for removal is = " + jList2.getSelectedValues().length);
        LinkedList removeList = new LinkedList();
        for (int i = 0; i < jList2.getSelectedValues().length; i++) {
            //modelActive.removeElement(jList2.getSelectedValues()[i]);
            removeList.add(jList2.getSelectedValues()[i]);
            System.out.println("removing i = " + i);
        }
        
        for (Object element: removeList)
            modelActive.removeElement(element);
    }//GEN-LAST:event_jButton1MouseClicked
    
    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
         for (int i = 0; i < jList1.getSelectedValues().length; i++) {
            if (!modelActive.contains(jList1.getSelectedValues()[i]))
                     modelActive.addElement(jList1.getSelectedValues()[i]);
         }
    }//GEN-LAST:event_jButton2MouseClicked

	private void jButton5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseClicked
	
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setFileFilter(new FileUtils.SufixFileFilter(".xml"));
		jFileChooser.setMultiSelectionEnabled(true);
		jFileChooser.showOpenDialog(this);
		File[] files = jFileChooser.getSelectedFiles();
			
		for (int i = 0; i < files.length; i++)
		    processLoadedXMLProfile(files[i]);		
		
	}//GEN-LAST:event_jButton5MouseClicked	
	
	public void processLoadedXMLProfile(File file) {
		if (!modelFound.contains(file.getName())) 
			modelFound.addElement(file.getName());		
		fileMap.put(file.getName(), file);	
		
		Object pojo = xmlParser(fileMap.get(file.getName()));
        if (pojo != null)
            pojoList.put(file.getName(), pojo);
	}
	
	public void activateXMLProfile(File file) {
		jList1.setSelectedValue(file.getName(), true);
		jButton2MouseClicked(null);
	}
	
	public void hitOK() {
		jButton4ActionPerformed(null);
	}
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables    
}
