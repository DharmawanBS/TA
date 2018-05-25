package sidnet.utilityviews.energymap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

import sidnet.core.misc.FileUtils;
import sidnet.core.misc.NCS_Location2D;
import jist.runtime.JistAPI;

public class EnergyCSVWriter 
implements JistAPI.DoNotRewrite {
	
	public static void saveAsCSV(NCS_Location2D[] locArray, int[] energyArray) {
		// open file
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setFileFilter(new FileUtils.SufixFileFilter(".csv"));
		jFileChooser.setSelectedFile(new File("*.csv"));
		jFileChooser.showSaveDialog(null);		
		
		// append text
		File file = jFileChooser.getSelectedFile();
		
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO handle this better
			e.printStackTrace();
			return;
		}
		
		BufferedWriter output = null;
		
		try {
			output = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			// TODO handle this better
			e.printStackTrace();
			return;
		}
		
		for (int i = 0; i < locArray.length; i++) {
			String str = locArray[i].getX() + ", " +
						 locArray[i].getY() + ", " +
						 energyArray[i];
			if (i != locArray.length - 1)
				str += ", ";
			try {
				output.append(str);
				output.newLine();
			} catch (IOException e) {
				// TODO handle this better
				e.printStackTrace();
				return;
			}			
		}
		
		// close file
		try {
			output.close();
		} catch (IOException e) {
			// TODO handle this error better;
			e.printStackTrace();
		}
	}
}
