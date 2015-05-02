/*
 * 
 */
package utils;


import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// TODO: Auto-generated Javadoc
/**
 * The Class ListContentsOfZipFile.
 */
public class ListContentsOfZipFile {
		
		/** The zip file. */
		ZipFile zipFile = null;
		
		/** The zip. */
		File zip;
	
	/**
	 * Instantiates a new list contents of zip file.
	 *
	 * @param file the file
	 */
	public ListContentsOfZipFile(File file){
		this.zip = file;
		
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		ZipFile zipFile = null;

		try {
			
			// open a zip file for reading
			zipFile = new ZipFile("c:/archive.zip");

			// get an enumeration of the ZIP file entries
			Enumeration<? extends ZipEntry> e = zipFile.entries();

			while (e.hasMoreElements()) {
				
				ZipEntry entry = e.nextElement();

				// get the name of the entry
				String entryName = entry.getName();
				
				System.out.println("ZIP Entry: " + entryName);

			}

		}
		catch (IOException ioe) {
			System.out.println("Error opening zip file" + ioe);
		}
		 finally {
			 try {
				 if (zipFile!=null) {
					 zipFile.close();
				 }
			 }
			 catch (IOException ioe) {
					System.out.println("Error while closing zip file" + ioe);
			 }
		 }
		
	}

}