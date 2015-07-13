package utils;

import java.io.File;
import java.io.IOException;

import gui.LogPanel;

public class FileUtils {
	
	/**
	 * This method was taken from http://www.mkyong.com/java/how-to-delete-directory-in-java/
	 * and modified by C.M. Punter
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();
				LogPanel.log("Directory " + file.getAbsolutePath() + " was deleted");

			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					LogPanel.log("Directory " + file.getAbsolutePath() + " was deleted");
				}
			}

		} else {
			// if file, then delete it
			file.delete();
			LogPanel.log("File " + file.getAbsolutePath() + " was deleted");
		}
	}
}
