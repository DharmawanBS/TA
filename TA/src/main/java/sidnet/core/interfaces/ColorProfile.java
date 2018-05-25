/*
 * ColorProfile.java
 *
 * Created on May 16, 2006, 5:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import sidnet.colorprofiles.ColorPair;
import sidnet.core.gui.NodeGUIimpl;

import java.awt.Color;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Oliviu Ghica, Northwestern University
 *
 * Interface for defining the meaning and type of colors a visual node can paint itself into
 */
public abstract class ColorProfile 
implements ColorProfileInterface {
	
     /** Event marker, indicates an indefinite duration for which the last specified color will be applied */
    public static final int FOREVER   = -1;
    /** Duration marker, clear the last set color */
    public static final int CLEAR     =  0;
    
    /** The tag for dead nodes */
    public static final String DEAD     = "DEAD";
    /** The tag for selected nodes */
    public static final String SELECTED = "SELECTED";
    public static final String DEFAULT  = "DEFAULT";
      
    private HashMap<String, ColorBundle> colorBundleMap;
 
    public boolean changed = false;
    
    private Color innerCol = null;
    private Color outerCol = null;
    private Set<String> tagSet = null;
    private long lowestImplicitPriority = -1;       // TODO should be static, but if done so causes the screen not to refresh completely (when limited time coloring is used).
    
    private NodeGUIimpl nodeCallback = null;
    
    /**
     * Constructor function in which you must define the assignment of colors to the array
     */
    public ColorProfile() {
        colorBundleMap = new HashMap<String, ColorBundle>();
        register(new ColorBundle(DEAD    , Color.black, Color.black));
        register(new ColorBundle(SELECTED, Color.MAGENTA, Color.MAGENTA));
        ColorBundle defaultColor = new ColorBundle(DEFAULT , Color.white, Color.black);
        defaultColor.priority = Integer.MAX_VALUE-1;
        defaultColor.expiration = FOREVER;
        register(defaultColor);
        changed = true;
        tagSet = colorBundleMap.keySet();
    }
    
    public void setNodeGUICallback(NodeGUIimpl nodeCallback) {
    	this.nodeCallback = nodeCallback;
    }
   
    public synchronized void register(ColorBundle colorBundle) {
        if (!colorBundleMap.containsKey(colorBundle.tag)) {
            // check to see if it has any priority specified
            if (colorBundle.priority == -1) {            	
                lowestImplicitPriority ++;
                colorBundle.priority = lowestImplicitPriority;
            }
            colorBundleMap.put(colorBundle.tag, colorBundle);
        }
    }
    
    
    /** 
     * Mark a given node
     * <p>
     * @param event     event marker
     * @param duration  either a marker, or an integer value.
     * 				    The latter indicates the amount of time,
     * 				    in [ms]-simulation time, the mark will last
     */
    public synchronized void mark(ColorProfile clientColorProfile,
    							  String tag, long duration_ms) {          
        // Do we have the tag registered already? 
    	//    If not, register it from the supplied Class
    	// You cannot alter the DEFAULT colors
        if (clientColorProfile == null || tag == DEFAULT)
            return;
        try {
            if (!colorBundleMap.containsKey(tag)) {
                ColorBundle colorBundle = clientColorProfile.getColorBundle(tag);
                if (colorBundle != null) {
                    colorBundleMap.put(tag, colorBundle);
                    tagSet = colorBundleMap.keySet();
                    changed = true;
                }
            }
        } catch(Exception e){e.printStackTrace(); System.exit(-1);};
        
        // change the expiration of the current bundle
        ColorBundle colorBundle = colorBundleMap.get(tag);   
        if (duration_ms != FOREVER && duration_ms != CLEAR)
            colorBundle.expiration = System.currentTimeMillis() + duration_ms;
        else
            colorBundle.expiration = duration_ms;
            //colorBundleMap.put(tag, colorBundle);        
        changed = true;  
        nodeCallback.updateNodeImage();
    }
    
    /** Returns the ColorPair associated with a given 
     *  instance of an implementation function */
    public synchronized ColorPair getColorSet()
    {
    	if (getTimeToColorChange() == 0)
    		changed = true;
    	
        if (changed)
        {
            long innerColPriority = Long.MAX_VALUE;
            long outerColPriority = Long.MAX_VALUE;
            
            changed = false;
            innerCol = null;
            outerCol = null;

            // scan all color bundles and use the ones with highes priority (lowest number)            
            for (String tag: tagSet)
            {                
                ColorBundle colorBundle = colorBundleMap.get(tag);
                
                updateResidualTime(colorBundle);
                purgeIfExpired(colorBundle);               
                
                if (colorBundle.expiration != CLEAR)
                {
                    if (colorBundle.innerColor != null)
                        if (innerColPriority > colorBundle.priority)
                        {
                            innerCol = colorBundle.innerColor;
                            innerColPriority = colorBundle.priority;
                        }
                    if (colorBundle.outerColor != null)
                        if (outerColPriority > colorBundle.priority)
                        {
                            outerCol = colorBundle.outerColor;
                            outerColPriority = colorBundle.priority;
                        }
                }
            }
        }
        
        return new ColorPair(innerCol, outerCol);
    } 
    
    private void purgeIfExpired(ColorBundle colorBundle) {    	    	
    	if (colorBundle.expiration < System.currentTimeMillis() &&
    		colorBundle.expiration != FOREVER && 
    		colorBundle.expiration != CLEAR)
    	colorBundle.expiration = CLEAR;	
    }
    
     /** Get remaining (simulation) time before the last paint is cleared */
    public synchronized long getTimeToColorChange()
    {
        long minTime = 0;
        if (tagSet == null)
            return 0;
        //for (int i = 0; i < innerColorList.length - 1; i++)
        for (String tag: tagSet)
        {
            ColorBundle colorBundle = colorBundleMap.get(tag);
            
            // clear expired items
            if (!tag.equals(DEFAULT) && colorBundle.expiration != CLEAR &&
            							colorBundle.expiration != FOREVER) 
            {            	
                purgeIfExpired(colorBundle);                
                changed = true;                       
                minTime = updateResidualTime(colorBundle);
            }            
        }
        return minTime;
    }
    
    private long updateResidualTime(ColorBundle colorBundle) {
    	long minTime = 0;
    	
    	if (minTime < Math.max(colorBundle.expiration-System.currentTimeMillis(), 0))
            minTime = Math.max(colorBundle.expiration-System.currentTimeMillis(), 0);
    	
    	return minTime;
    }
    
    public ColorBundle getColorBundle(String tag) {
       return colorBundleMap.get(tag);
    }
    
    public class ColorBundle
    {
        public String tag;
        public Color innerColor;
        public Color outerColor;
        private long expiration;
        public long priority; // DO NOT SET PRIORITY EXPLICITLY YOURSELF. SIDNET will associate one
        
        public ColorBundle(String tag, Color innerColor, Color outerColor)
        {
            this.tag        = tag;
            this.innerColor = innerColor;
            this.outerColor = outerColor;
            this.expiration = 0;
            this.priority   = -1;
        }
    }
}
