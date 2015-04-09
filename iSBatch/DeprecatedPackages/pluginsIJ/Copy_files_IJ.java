package pluginsIJ;

import ij.IJ;
import ij.plugin.PlugIn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;


public class Copy_files_IJ implements PlugIn{
		static String logpath;
		
		public static void main(String[] args) {
			new Copy_files_IJ().run("");
			System.out.println("Copy_files_IJ");
		}
		public void run(String arg0){
			
		//Get file
		String csvFilename =  IJ.getFilePath("Provide the input.CSV");
			if (csvFilename==null) return;	
			
		//String csvFilename = "H:\\TestOutput\\20140506_InputFile_Fast.csv";
		File tempFile = new File(csvFilename);
		
		logpath = tempFile.getParent() + File.separator+"ControlFile.csv";
		try {
			new FileOutputStream(logpath, true).close();
		} catch (FileNotFoundException e6) {
			// TODO Auto-generated catch block
			e6.printStackTrace();
		} catch (IOException e6) {
			// TODO Auto-generated catch block
			e6.printStackTrace();
		}
		
		
		long startTime = System.currentTimeMillis();
    	LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new FileReader(csvFilename));
		} catch (FileNotFoundException e6) {
			// TODO Auto-generated catch block
			e6.printStackTrace();
		}
    	int nLines = 0;
		try {	
			nLines = countLines(reader);
		} catch (IOException e6) {
			// TODO Auto-generated catch block
			e6.printStackTrace();
		}
    	try {
			reader = new LineNumberReader(new FileReader(csvFilename));
		} catch (FileNotFoundException e6) {
			// TODO Auto-generated catch block
			e6.printStackTrace();
		}
    	print(nLines);
    	
    	
    	
    	
    	reader.setLineNumber(0);
    	
    	
    	//Store the headers in a array
    	reader.setLineNumber(0);
    	String[] Headers = null;
		try {
			Headers = splitCSVLine(reader.readLine());
		} catch (IOException e5) {
			// TODO Auto-generated catch block
			e5.printStackTrace();
		}
  
    	
    	//Get index for Source Directory
    	
    	int SourceCol = getCol("LoadFromPath", Headers);
    	int DestCol = getCol("SaveToPath", Headers);
    	int indexCol = getCol("Index",Headers);
    	int ChannelCol = getCol("Channel", Headers);
    	int FolderCol = getCol("FolderIndex", Headers);
    	try {
			WriteArray(getHeaders(), logpath);
		} catch (IOException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}
    	
    	
        for(int i=1; i<nLines; i++){
    		reader.setLineNumber(i);
	       	String currentLine = null;
			try {
				currentLine = reader.readLine();
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
	       	String[] items = currentLine.split(",");
	       	print(i + " Move from: " + items[SourceCol]);
	       	print("to ------> " + items[DestCol]);
	       	CopyFile(items[SourceCol],items[DestCol]);
	      /**
	       	try {
				AppendLogLine(items[indexCol], logpath);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	       	try {
				//AppendLogLine(items[DestCol], logpath);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	       	try {
				//AppendLogLine(items[ChannelCol].substring(0, items[ChannelCol].lastIndexOf('.')), logpath);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	       	try {
				NewLogLine(items[FolderCol], logpath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	      */
	             	
	       	
	       	
        }  
    	
    	long endTime   = System.currentTimeMillis();
		long totalTime = (endTime - startTime);
		System.out.println("Done in "+ totalTime +" ms.");	
		IJ.showMessage("Done Copying Files");
		java.awt.Toolkit.getDefaultToolkit().beep(); 
    }
		
		
		
		
		private static int getCol(String string, String[] headers) {
			int col = 0;
			for(int i=0; i<headers.length; i++){
				
				if( headers[i].equalsIgnoreCase(string)){
					col = i;
				}
			}
			
			return col;
		}

		private static String[] splitCSVLine(String string) {
			String[] parts = string.split(",");
			return parts;
		}

		private static void WriteArray(String[] headers, String logpath2) throws IOException {
			for (int i=0; i<headers.length; i++){
				if(i==headers.length-1){
					NewLogLine(headers[i], logpath2);
					}
				
				else{
					AppendLogLine(headers[i], logpath2);
				}
				
				}
				
				
			}
		private static String[] getHeaders() {
			String[] Headers = new String[9];
			
			
				Headers[0]="Index";
				Headers[1]="Input";
				Headers[2]="Channel";
				Headers[3]="Trimm";
				Headers[4]="OffSet_Removed";
				Headers[5]="BG_BP_Corrected";
				Headers[6]="Proccess_1";
				Headers[7]="Proccess_2";
				Headers[8]="FolderIndex";
		





			return Headers;
		}
		public static void NewLogLine( String string, String logpath) throws IOException{
			BufferedWriter output;
			output = new BufferedWriter(new FileWriter(logpath, true));
			
			output.append(string);
			output.append("\n");
			output.close();
			
			//System.out.println(string);
			}
		
		public static void AppendLogLine( String string, String logpath) throws IOException{
			BufferedWriter output;
			output = new BufferedWriter(new FileWriter(logpath, true));
			
			output.append(string);
			output.append(",");
			output.close();
			
			//System.out.println(string);
			}
		
		public static int GetNumberOfLines(BufferedReader cSVFile) throws IOException{
			int lines = 0;
			while (cSVFile.readLine() != null) lines++;
			return lines;
			
			
		}
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
		private static void CopyFile(String Origin, String Destination) {
			
			File file = new File(Origin);
			File saveFileat = new File(Destination);
			
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
		public static void print(String string){
			System.out.println(string);
		}
		public static void print(int string){
			System.out.println(string);
		}




	
		
}	
