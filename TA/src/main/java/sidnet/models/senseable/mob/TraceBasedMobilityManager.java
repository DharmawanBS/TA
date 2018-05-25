package sidnet.models.senseable.mob;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import jist.runtime.JistAPI;

import sidnet.core.gui.XMLLoaderGUI;
import sidnet.core.interfaces.XMLLoaderListener;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.LocationContext;
import sidnet.core.misc.NCS_Location2D;
import sidnet.models.senseable.mob.Mob.FieldDimensions;

public class TraceBasedMobilityManager 
extends MobilityManager
implements XMLLoaderListener{
	
	/* CONFIGURATION */
    private XMLLoaderGUI profileLoader;
    private static String MOBXMLDIR = System.getenv("MOBXMLDIR");;
    private String xsdSchemaFilename = "MobSchema.xsd";
    private String frameTitle = "Trace Based Mobility Manager";
	
    private List<Location2D> mobLocs = new LinkedList<Location2D>();
    
    private LocationContext actualTargetFieldLocationContext;
    
    // for testing purposes
    private boolean loaderEnabled = false; 
    
    private boolean mirrored;
    
    /**
     * 
     * @param actualTargetFieldLocationContext
     * @param mirrored - if "true", forces the moving object to stay within the bounds of the sensor network
     *                   by mirroring all locations that fall outside the network bounds, inside. 
     */
    public TraceBasedMobilityManager(LocationContext actualTargetFieldLocationContext, boolean mirrored) {
    	profileLoader = new XMLLoaderGUI();
    	this.actualTargetFieldLocationContext = actualTargetFieldLocationContext;
    	this.mirrored = mirrored;
    }
    
    public void openWindow() {
   	 System.out.println("Enable XML Loader");
        loaderEnabled = true;
        profileLoader.enableLoaderFromXML(
        		MOBXMLDIR, xsdSchemaFilename, frameTitle,
        		new Mob().getClass(), this);
    }
    
    public void loadProfile(String xmlFilePath) {
   	 	profileLoader.processLoadedXMLProfile(new File(xmlFilePath));
    }
    
    public void activateProfile(String xmlFilePath, boolean enable) {
   	 if (enable)
   		 profileLoader.activateXMLProfile(new File(xmlFilePath));
   	 else {
   		 // TODO
   	 }
    }
    
    public void closeWindow() {
   	 	profileLoader.hitOK();
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Load Moving Object Profiles") {
            openWindow();
        }   
     }
     
     public void handleParsedObjects(List<Object> parsedPojoList) {
         if (parsedPojoList == null) {
             mobs = null;
             return;
         }
         
         mobs = new LinkedList<MobilityModel>();
         for (Object pojo: parsedPojoList) {
        	 ((Mob)pojo).setMirrored(mirrored);
             mobs.add((Mob)pojo);
             ((Mob)pojo).printComponents();
         }
     }
     
     public boolean isLoaderEnabled() {
    	 return loaderEnabled;
     }

     public void updateMobs() {         
          /* get object periodicity */
          if (mobs == null)
              return;
          
          mobLocs.clear();
          for (MobilityModel mob:mobs)
        	  mobLocs.add(mob.nextLocation(JistAPI.getTime(), actualTargetFieldLocationContext));                   
     }
}
