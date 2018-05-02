package sidnet.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sidnet.core.misc.FileUtils;

public class FileUtil {
	public static List<File> buildInputLogFileList(File rootPath) 
    throws FileNotFoundException {
		
	   System.out.println("\nScanning source files ... ");
		
       FileUtils.validateDirectory(rootPath);
       List<File> result = new ArrayList<File>();

       File[] filesAndDirs = rootPath.listFiles();
       List<File> filesDirs = Arrays.asList(filesAndDirs);
       for(File file : filesDirs) {
           if ((file.getName().startsWith(Constants.FILE_PREFIX_NAME1) ||
        	    file.getName().startsWith(Constants.FILE_PREFIX_NAME2))&& 
        	    file.getName().endsWith(Constants.FILE_EXTENSION))
               result.add(file); //always add, even if directory
           if ( !file.isFile() ) {
               //must be a directory
               //recursive call!
               //List<File> deeperList = buildInputLogFileList(file);
               //result.addAll(deeperList);
           }
       }
       return result;
    }  
	
	public static String grabExtension(String fileName) {
		int indexofdot = fileName.indexOf(".");
		
		return fileName.substring(indexofdot, fileName.length());
	}
	
	public static String grabBaseFileName(String fileName) {
		int indexofdot = fileName.indexOf(".");
		
		return fileName.substring(0, indexofdot);
	}
	
	public static void write(File file, Object[][] data)
	throws IOException {
		FileWriter fileWriter = null;
		BufferedWriter bw = null;
		try {
			 fileWriter = new FileWriter(file, false); // false - overwrite mode
	         bw = new BufferedWriter(fileWriter);
	         for (int i = 0; i < data.length; i++) {
	        	 String row = "";
	        	 for (int j = 0; j < data[i].length; j++)       		                  
	                 row += data[i][j] + Constants.OUTPUT_DELIMITER;                     
	         	commit(bw, row);
	         } 
	         bw.flush();
	 		 bw.close();
	 		 fileWriter.close();
         } finally {                       // always close the file
            if (bw != null) try {
               bw.close();
               fileWriter.close();
            } catch (IOException ioe2) {
               // just ignore it
            }
        } // end try/catch/finally
         
	}
	
	public static void commit(BufferedWriter bw, String row) 
	throws IOException {
		bw.write(row);
        bw.newLine();
	}
}
