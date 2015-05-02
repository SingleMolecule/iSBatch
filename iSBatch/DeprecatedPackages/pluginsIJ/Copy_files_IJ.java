/*
 * 
 */
package plugins_ij;


import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;



// TODO: Auto-generated Javadoc
/**
 * The Class Copy_files_IJ.
 */
public class Copy_files_IJ implements PlugIn{
		
		/** The logpath. */
		static String logpath;
		
		
		/**
		 * The main method.
		 *
		 * @param args the arguments
		 */
		public static void main(String[] args) {
			new Copy_files_IJ().run("");
			IJ.log("All files copied");
		}
		
		/**
		 * Run.
		 *
		 * @param arg0 the arg0
		 */
		public void run(String arg0){
			
		//Get file
		String csvFilename =  IJ.getFilePath("Provide ControlFile.CSV");
			if (csvFilename==null) return;	
		long startTime = System.currentTimeMillis();	
		
		
		try {
			CopyAllFiles(csvFilename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		long endTime   = System.currentTimeMillis();
		long totalTime = (endTime - startTime);
		System.out.println("Done in "+ totalTime +" ms.");	
		IJ.showMessage("Done Copying Files");
		java.awt.Toolkit.getDefaultToolkit().beep(); 
	       	
        }  
    	
    	
    
		
		
		
		
		/**
		 * Copy all files.
		 *
		 * @param csvFilename the csv filename
		 * @throws IOException Signals that an I/O exception has occurred.
		 * @throws NoSuchAlgorithmException the no such algorithm exception
		 */
		private void CopyAllFiles(String csvFilename) throws IOException, NoSuchAlgorithmException {
			ResultsTable table = ResultsTable.open(csvFilename);
			
			int size = table.getCounter();
			for(int i=0; i<size; i++){
				String source = table.getStringValue("OriginaFile", i);
				String destination = table.getStringValue("WorkingFile", i);
				print(i+1 + " Move from: " + source + " to ------> " + destination);
				//print("to ------> " + destination);
					//imp =  new ImagePlus(source);
					//IJ.save(imp, destination);
					CopyFile(source,destination);
					StringBuffer sb1 = CheckSum(source);
					StringBuffer sb2 = CheckSum( destination);
					//CheckSum(source);
					//check SHA1
					writeChecksum(sb1, sb2, table,i);
					
					
					
			}
			table.saveAs(csvFilename);
			
			
		}
		
		/**
		 * Write checksum.
		 *
		 * @param sb1 the sb1
		 * @param sb2 the sb2
		 * @param table the table
		 * @param row the row
		 */
		private void writeChecksum(StringBuffer sb1, StringBuffer sb2, ResultsTable table, int row) {
			if (sb1.toString().equals(sb2.toString())){
				
				System.out.println("Checksum Match!" + sb1.toString());
				//table.addValue("CheckSum", "Safe");
				table.setValue("Checksum", row ,  sb1.toString());
				//table.addValue("CheckSum", sb1.toString());
				//table.incrementCounter();
			}
			else {
				
			//	System.out.println("Checksum Match! Image may be damaged!"+ " : " + t);
				//i = i-1;
				//table.addValue("CheckSum", "Damaged");
				table.setValue("Checksum", row ,  "Damaged");
				//table.incrementCounter();
			}
			
		}

		/**
		 * Check sum.
		 *
		 * @param source the source
		 * @return the string buffer
		 * @throws NoSuchAlgorithmException the no such algorithm exception
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public StringBuffer  CheckSum (String source) throws NoSuchAlgorithmException, IOException{
			    //MessageDigest md = MessageDigest.getInstance("MD5");
			    // Change MD5 to SHA1 to get SHA checksum
			    MessageDigest md = MessageDigest.getInstance("SHA1");
			 
			    FileInputStream fis = new FileInputStream(source);
			    byte[] dataBytes = new byte[4096];
			    int nread = 0; 
			 
			    while ((nread = fis.read(dataBytes)) != -1) {
			      md.update(dataBytes, 0, nread);
			    };
			 
			    byte[] mdbytes = md.digest();
			 
			    //convert the byte to hex format
			    StringBuffer sb = new StringBuffer("");
			    for (int i = 0; i < mdbytes.length; i++) {
			    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			    }
			    
			    
			  //  System.out.println("Checksum");
			 
			  //  System.out.println(sb.toString() + " " + source);
			   
			//    System.out.println("------------------");
			    fis.close();
			    
			    return sb;
			    
			 }
			

		
		/**
		 * New log line.
		 *
		 * @param string the string
		 * @param logpath the logpath
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static void NewLogLine( String string, String logpath) throws IOException{
			BufferedWriter output;
			output = new BufferedWriter(new FileWriter(logpath, true));
			
			output.append(string);
			output.append("\n");
			output.close();
			
			//System.out.println(string);
			}
		
		/**
		 * Append log line.
		 *
		 * @param string the string
		 * @param logpath the logpath
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static void AppendLogLine( String string, String logpath) throws IOException{
			BufferedWriter output;
			output = new BufferedWriter(new FileWriter(logpath, true));
			
			output.append(string);
			output.append(",");
			output.close();
			
			//System.out.println(string);
			}
		
		/**
		 * Gets the number of lines.
		 *
		 * @param cSVFile the c sv file
		 * @return the int
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static int GetNumberOfLines(BufferedReader cSVFile) throws IOException{
			int lines = 0;
			while (cSVFile.readLine() != null) lines++;
			return lines;
			
			
		}
		
		/**
		 * Count lines.
		 *
		 * @param reader the reader
		 * @return the int
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static int countLines(LineNumberReader reader) throws IOException {
		   
		    try {
		        while ((reader.readLine()) != null);
		        return reader.getLineNumber();
		    } catch (Exception ex) {
		        return -1;
		    } finally { 
		        if(reader != null) 
		            reader.close();
		    }
		}
		
		/**
		 * Copy file2.
		 *
		 * @param source the source
		 * @param destination the destination
		 */
		public static void CopyFile2(String source, String destination){
		
		   		 
		   	InputStream inStream = null;
			OutputStream outStream = null;
		 
		    	try{
		 
		    	    File afile =new File(source);
		    	    File bfile =new File(destination);
		 
		    	    inStream = new FileInputStream(afile);
		    	    outStream = new FileOutputStream(bfile);
		 
		    	    byte[] buffer = new byte[1024];
		 
		    	    int length;
		    	    //copy the file content in bytes 
		    	    while ((length = inStream.read(buffer)) > 0){
		 
		    	    	outStream.write(buffer, 0, length);
		 
		    	    }
		 
		    	    inStream.close();
		    	    outStream.close();
		 
		    	    System.out.println("File is copied successful!");
		 
		    	}catch(IOException e){
		    		e.printStackTrace();
		    	}
		    }
		
		/**
		 * Copy file.
		 *
		 * @param Origin the origin
		 * @param Destination the destination
		 */
		private static void CopyFile(String Origin, String Destination) {
			
			File file = new File(Origin);
			File saveFileat = new File(Destination);
			
			if(!saveFileat.exists() && !saveFileat.isDirectory()){
				
			
			
			try{
				 
	    	    
	 
	    	    FileInputStream inStream = new FileInputStream(file);
	    	    FileOutputStream outStream = new FileOutputStream(saveFileat);
	 
	    	    byte[] buffer = new byte[1024];
	    	    int length;
	    	    //copy the file content in bytes 
	    	    while ((length = inStream.read(buffer)) > 0){
	 
	    	    	outStream.write(buffer, 0, length);
	 
	    	    }
			
	    	    	
	    	    inStream.close();
	    	    outStream.close();
	 	    	    			
				}catch(IOException e){
					e.printStackTrace();
			
					
				}	
			}
			
		}
		
		/**
		 * Prints the.
		 *
		 * @param string the string
		 */
		public static void print(String string){
			System.out.println(string);
		}
		
		/**
		 * Prints the.
		 *
		 * @param string the string
		 */
		public static void print(int string){
			System.out.println(string);
		}



}

	
		
	
