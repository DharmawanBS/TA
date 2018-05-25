/*
 * FileUtils.java
 *
 * Created on April 8, 2008, 10:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.filechooser.FileFilter;

import java.io.*;


/**
 *
 * @author Oliver
 */
 public class FileUtils
 {
	 /**
	  * Check to see if the indicated file exists
	  * 
	  * @param fileNameWithPath
	  * @return
	  */
	 public static boolean exists(String fileNameWithPath) {
		 File file = new File(fileNameWithPath);
		 return file.exists();
	 }
	 
	 /**
	  * Checks whether the 'searchedString' can be found within the indicated file
	  * 
	  * @param fileNameWithPath
	  * @param searchedString
	  * 
	  * @return
	  */
	 public static boolean contains(String fileNameWithPath, String searchedString) {
		 File file = new File(fileNameWithPath);
		 
		 try {
		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
		      BufferedReader input =  new BufferedReader(new FileReader(file));
		      try {
		        String line = null; //not declared within while loop
		        /*
		        * readLine is a bit quirky :
		        * it returns the content of a line MINUS the newline.
		        * it returns null only for the END of the stream.
		        * it returns an empty String if two newlines appear in a row.
		        */
		        while (( line = input.readLine()) != null) {
		        	if (line.contains(searchedString))
		        		return true;
		        }
		      }
		      finally {
		        input.close();
		      }
		    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    
	    return false;
	 }
	 
     /*
      * Check to see if a file is empty or not 
      */
     public static boolean isEmpty(File file)
     {
         BufferedReader input = null;
         try{
             input = new BufferedReader(new FileReader(file));
         } catch(FileNotFoundException e){e.printStackTrace();System.exit(1);};
         
         String nextLine = "";
         try{
            nextLine = input.readLine();
            if (input != null)
                input.close();
         }catch(IOException ioe){ioe.printStackTrace(); System.exit(1);};
         
         if (nextLine != null && nextLine.length() > 0)
             return false;
         else
             return true;
     }
      
      /**
       * Directory is valid if it exists, does not represent a file, and can be read.
       */
      public static void validateDirectory (File dir) 
      throws FileNotFoundException {
          if (dir == null) 
              throw new IllegalArgumentException("Directory should not be null.");

          if (!dir.exists()) 
              throw new FileNotFoundException("Directory does not exist: " + dir);

          if (!dir.isDirectory()) 
              throw new IllegalArgumentException("Is not a directory: " + dir);
          if (!dir.canRead()) 
              throw new IllegalArgumentException("Directory cannot be read: " + dir);
      }
      
     public static void deleteFilesWithPrefix( String dirPath, String filenamePrefix ) {
          PrefixFilter filter = new PrefixFilter(filenamePrefix);
          
          delete(dirPath, filenamePrefix, filter);
     }
          
     public static void deleteFilesWithBody( String dirPath, String filenameBody) {
    	 BodyFilter filter = new BodyFilter(filenameBody);
    	 
    	 delete(dirPath, filenameBody, filter);
     }
     
     private static void delete(String dirPath, String filenameSearchString, FilenameFilter filter) {
    	 File dir = new File(dirPath);

         String[] list = dir.list(filter);
         File file;
         
         System.out.println(">> Target = " + dirPath);
         System.out.println(">> Look for = " + filenameSearchString);
         System.out.println(">> Found " + list.length + " deletable files.");
         
         if (list.length == 0) return;

         for (int i = 0; i < list.length; i++) {
            file = new File(dirPath, list[i]);
            if (file.isDirectory()) { // recursive
            	delete(file.getAbsolutePath(), filenameSearchString, filter);
            	continue;
            }
            try {
                new FileWriter(file).close();
            }
            catch(Exception e){e.printStackTrace();};
            boolean isdeleted =   file.delete();
            System.out.print(file);
            if (isdeleted)
                System.out.println( "  deleted!");
            else
                System.out.println(" could not be deleted");
        }
     }
     
     public static void appendToFilesWithPrefix(String dirPath, String filenamePrefix, String text) {
    	 PrefixFilter filter = new PrefixFilter(filenamePrefix);
    	 
    	 append(dirPath, filenamePrefix, filter, text);
     }
        
     public static void appendToFilesWithBody(String dirPath, String filenameSearchBody, String text) {
    	 BodyFilter filter = new BodyFilter(filenameSearchBody);
    	 
    	 append(dirPath, filenameSearchBody, filter, text);
     }
     
     private static void append(String dirPath, String filenamePrefix, FilenameFilter filter, String text) {
    	 System.out.println(" +++++ Commit " + text + " to " + filenamePrefix);         
         
         File dir = new File(dirPath);
         
         String[] list = dir.list(filter);
         if (list.length == 0)
             return;
         
         System.out.println(" +++++ Commit " + text + " to " + list[0]);
         
         FileWriter fileWriter = null;
         BufferedWriter bw = null;
         
         try{
             File file = new File(dirPath, list[0]); // there should be normally only one file of this kind
             fileWriter = new FileWriter(file, true); // true- append mode
             bw = new BufferedWriter(fileWriter);
             bw.write(text);
             bw.newLine();
             bw.flush();
             bw.close();
             fileWriter.close();
         } catch (IOException ioe) {
             ioe.printStackTrace();
         } finally {                       // always close the file
             if (bw != null) try {
                bw.close();
                fileWriter.close();
             } catch (IOException ioe2) {
                // just ignore it
             }
         } // end try/catch/finally
     }
       
   public static String[] getFileList(String dirPath, FilenameFilter optionalFilenameFilter) {
          File dir = new File(dirPath);
          
          String[] list = null;
          if (optionalFilenameFilter != null)
              list = dir.list(optionalFilenameFilter);
          else
              list = dir.list();
          
          return list;
   }
     

   public static class BodyFilter 
   implements FilenameFilter {
         private String body;         
         public BodyFilter( String body ) {
             this.body = body;             
         }
         public boolean accept(File dir, String name) {
             return name.contains(body);
         }
   }
   
   public static class PrefixFilter 
   implements FilenameFilter {
         private String prefix;
         public PrefixFilter( String prefix ) {
             this.prefix = prefix;             
         }
         public boolean accept(File dir, String name) {
             return name.startsWith(prefix);
         }
   }
   
   public static class SufixFilter 
   implements FilenameFilter {
         private String sufix;
         public SufixFilter( String sufix ) {
             this.sufix = sufix;             
         }
         public boolean accept(File dir, String name) {
             return name.endsWith(sufix);
         }
   }
   
   public static class SufixFileFilter extends FileFilter
   {
	   private String sufix; // ie. ".xml"
	   
	   public SufixFileFilter(String sufix) {
		   this.sufix = sufix;
	   }
	   
       public boolean accept(File filename)
       {
           if (filename.isDirectory() || filename.getName().endsWith(sufix))
               return true;
           return false;
       }

       public String getDescription()
       {
           return "*" + sufix; // i.e. *.xml
       }
   }
   
   public static boolean deleteDirectory(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
   }

   
   public static void moveFile(File file, String dstDirPath) 
   throws IOException {	    
	    // Destination directory
	    File dir = new File(dstDirPath);
	    if (!dir.exists()){
	    	boolean success = dir.mkdir();
	    	if (!success)
	    		System.out.println("Unable to create directory " + dstDirPath);
	    }
	    
	    // Move file to new directory
	    File newFile = new File(dir.getParent(), file.getName());
	    boolean success = file.renameTo(newFile);
	    if (!success) {
	    	System.out.println("Unable to move file " + file.getAbsolutePath() + " to " + newFile.getAbsolutePath());
	    }
   }
   
   public static void copyFile(File srFile, File dtFile) {
	   try {		     
		      InputStream in = new FileInputStream(srFile);
		      if (!dtFile.exists())
		    	  dtFile.createNewFile();
		      //For Append the file.
	   	      //OutputStream out = new FileOutputStream(f2,true);

		      //For Overwrite the file.
		      OutputStream out = new FileOutputStream(dtFile);

		      byte[] buf = new byte[1024];
		      int len;
		      while ((len = in.read(buf)) > 0){
		        out.write(buf, 0, len);
		      }
		      in.close();
		      out.close();
		      System.out.println("File copied.");
		    }
		    catch(FileNotFoundException ex){
		      System.out.println(ex.getMessage() + " in the specified directory.");
		      System.exit(0);
		    }
		    catch(IOException e){
		      System.out.println(e.getMessage());      
		    }
   }
   
   public static void copyfile(String srFile, String dtFile) {
	    copyFile(new File(srFile), new File(dtFile));
   }
 }
