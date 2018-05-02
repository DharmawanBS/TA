package sidnet.utilityviews.commons;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatLoggerUtils {
	  public static String getDateTime() {
	        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss");
	        Date date = new Date();
	        return dateFormat.format(date);
	  }
	  
	  public static String buildDataTableHeader(String key, String tag) {
	    	String header = "";
	    	if (key != null && key.length() > 0)
	    		header += key + "_";
	    	header += tag;
	    	
	    	return header;
	  }
}
