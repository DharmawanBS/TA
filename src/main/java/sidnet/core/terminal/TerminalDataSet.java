/*
 * TerminalDataSet.java
 *
 * Created on October 23, 2007, 8:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.terminal;

import java.util.LinkedList;
import sidnet.core.query.Query;
import sidnet.core.misc.Region;

/**
 *
 * @author Oliver
 */
public class TerminalDataSet{
    private static final int MAX_STRING = 2000;
    
    private final int dataSetID;
    private String consoleText;
    private LinkedList<Region> regionList;
    private LinkedList<Query> queryList;
   
    
    /**
     * Creates a new instance of TerminalData
     */
    public TerminalDataSet(int dataSetID) 
    {
        this.dataSetID = dataSetID;
        regionList = new LinkedList<Region>();
        queryList = new LinkedList<Query>();
        consoleText = new String();
    }
    
    public int getID()
    {
        return dataSetID;
    }
   
    // print some data to the terminal's console
    public void appendConsoleText(String s)
    {
        consoleText = consoleText + s + "\n";
        if (consoleText.length() > MAX_STRING)
            consoleText = consoleText.substring(consoleText.length() - MAX_STRING);
    }
    
    public void setConsoleText(String s)
    {
        consoleText = s;
    }
    
    public String getConsoleText()
    {
        return consoleText;
    }
    
    public LinkedList<Region> getRegionList()
    {
        return regionList;
    }
    
    public void add(Region region)
    {
        regionList.add(region);
    }
    
       public LinkedList<Query> getQueryList()
    {
        return queryList;
    }
    
    public void add(Query query)
    {
        queryList.add(query);
    }
}
