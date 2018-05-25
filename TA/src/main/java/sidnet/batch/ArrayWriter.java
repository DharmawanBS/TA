package sidnet.batch;

import java.io.File;
import java.io.IOException;
import java.util.Map;

// Writes an array of objects to a specified file
public class ArrayWriter {
	
	public static void write(String baseFilePath, String fileName, Map<String, Object[][]> dataTables)
	throws IOException {
		for (String tableTag: dataTables.keySet()) {			
			String extension = FileUtil.grabExtension(fileName);
			String newBaseFileName = FileUtil.grabBaseFileName(fileName);
			String newFileName = newBaseFileName.toString();
			
			if (tableTag.length() > 0)
				newFileName += "_" + tableTag;
			
			if (extension != null)
				newFileName += extension;
			else
				newFileName += Constants.DEFAULT_FILE_EXTENSION;							
			
			FileUtil.write(new File(baseFilePath, newFileName), dataTables.get(tableTag));
		}
	}
}
