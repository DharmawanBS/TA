package sidnet.batch;

import sidnet.core.misc.FileUtils;

public class ExperimentFileFinder {
	public static boolean existsFileContaining(String dirPath, String experimentKey) {
		String[] filenames = FileUtils.getFileList(dirPath, new FileUtils.BodyFilter(experimentKey));
		return filenames != null && filenames.length > 0;
	}
}
