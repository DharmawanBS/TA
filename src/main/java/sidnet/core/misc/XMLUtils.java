/*
 * XMLUtils.java
 *
 * Created on April 4, 2008, 4:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 * 
 * @author Oliviu C. Ghica, Northwestern University
 * @version 1.0.1
 */

package sidnet.core.misc;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import jist.runtime.JistAPI;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLUtils 
implements JistAPI.DoNotRewrite {   
    static final MyErrorHandler myErrorHandler = new MyErrorHandler();
    public static boolean xmlValidation(File xmlFile) 
    throws Exception {        
        if (xmlFile == null)
            return false;
        try {
        	// get the SIDnet-SWANS root path
        	String sidnetDir = System.getenv("SIDNETSWANSDIR") == null ?
        					   System.getProperty("user.dir")          :
        					   System.getenv("SIDNETSWANSDIR");        	
        	
        	System.out.println("XSD current dir = " + sidnetDir);
        					
            // Create a new XML parser
            XMLReader reader = XMLReaderFactory.createXMLReader();
            // Request validation
            reader.setFeature(
            	"http://xml.org/sax/features/validation",true);
            reader.setFeature(
            	"http://apache.org/xml/features/validation/schema",true);
            reader.setFeature(
            	"http://apache.org/xml/features/validation" +
            	"/schema-full-checking", true);
                    
            reader.setProperty( 
            	"http://apache.org/xml/properties/schema/" +
            	"external-noNamespaceSchemaLocation",
            	"file:///" + sidnetDir +
            	"/src/main/java/sidnet/core/misc/deploymentSchema.xsd");
           
            // Register the error handler
            reader.setErrorHandler(myErrorHandler);
            
            String xmlFilePath = xmlFile.getAbsolutePath();
            
            // This line is necessary to force SAX recognize this as a file
            // otherwise an 'unknown protocol: d' exception may be thrown
            if (!xmlFilePath.startsWith("file:///"))
            	xmlFilePath = "file:///" + xmlFilePath;
            
            // Parse the file as the first argument on the command-line
            System.out.println("parse file: " + xmlFilePath);
            reader.parse(xmlFilePath);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
        return true;
    }
    
    public static Object xmlParser(String targetClassPackageName, 
    							   File xmlFile) {
       
    	Object pojo = null;
        
        try {
            JAXBContext context 
            	= JAXBContext.newInstance(targetClassPackageName) ;

            Unmarshaller unmarshaller = context.createUnmarshaller() ;

            pojo = unmarshaller.unmarshal(xmlFile);
        }
        catch (Exception e){e.printStackTrace(); } ;
        
        return pojo;
    }
}

 class MyErrorHandler implements ErrorHandler {
    public void warning(SAXParseException exception) throws SAXException {
        // Bring things to a crashing halt
        System.out.println("**Parsing Warning**" +
                           "  Line:    " + 
                              exception.getLineNumber() + "" +
                           "  URI:     " + 
                              exception.getSystemId() + "" +
                           "  Message: " + 
                              exception.getMessage());        
        throw new SAXException("Warning encountered");
    }
    public void error(SAXParseException exception) throws SAXException {
        // Bring things to a crashing halt
        System.out.println("**Parsing Error**" +
                           "  Line:    " + 
                              exception.getLineNumber() + "" +
                           "  URI:     " + 
                              exception.getSystemId() + "" +
                           "  Message: " + 
                              exception.getMessage());        
        throw new SAXException("Error encountered");
    }
    public void fatalError(SAXParseException exception) throws SAXException {
        // Bring things to a crashing halt
        System.out.println("**Parsing Fatal Error**" +
                           "  Line:    " + 
                              exception.getLineNumber() + "" +
                           "  URI:     " + 
                              exception.getSystemId() + "" +
                           "  Message: " + 
                              exception.getMessage());        
        throw new SAXException("Fatal Error encountered");
    }
}