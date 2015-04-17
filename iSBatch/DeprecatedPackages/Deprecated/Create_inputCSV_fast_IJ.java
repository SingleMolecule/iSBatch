package Deprecated;

import ij.IJ;
import ij.measure.ResultsTable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;




public class Create_inputCSV_fast_IJ {

	static String logpath;
	static String OUT_DIR;
		
	public static void main(String[] args) throws IOException {
		
		new Create_inputCSV_fast_IJ().run("");
		System.out.println("Files ready to be Copied!");
		
	}
	
	
	

	public void run(String arg0) throws IOException{
		
		
		String INPUT_DIR =  IJ.getDirectory("Select the Main directory with all data to be analysed");	
		if (INPUT_DIR==null) return;

		String OUT_DIR =  IJ.getDirectory("Indicate the Output Directory");	
			if (OUT_DIR==null) return;
			
		ResultsTable table = new ResultsTable();
		
		tools.iSBOps.checkCreateDir(OUT_DIR);

		String ImagesFolder = tools.iSBOps.checkCreateDir(OUT_DIR+File.separator+"Images");

		
		
		long startTime = System.currentTimeMillis();

		// Creating file to save all data from files
		
		String strDate = new SimpleDateFormat("yyyyMMdd").format(new Date(startTime));
		logpath = OUT_DIR + File.separator+ strDate+"_InputFile.csv";
		String logpath2 = OUT_DIR + File.separator+ strDate+"Results.csv";
		
	
			
			
			try {
				new FileOutputStream(logpath, true).close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	
			
		String Headers[] = getHeaders();
		
		
		
		
		
		// Get the list of folders
		File[] Directories = tools.iSBOps.getFileList(INPUT_DIR);
		
		int CountFiles = 1;
		
		for (int i=0; i< Directories.length; i++){
			System.out.println("*  " + Directories[i].getAbsolutePath());
			
			// Getting list of Images inside the folder
			File[] temFiles = tools.iSBOps.getImageList(Directories[i].getAbsolutePath());
			
			// Executing file by file
			for (int j=0; j< temFiles.length; j++){
					
					//AppendLogLine(Integer.toString(CountFiles), logpath);
					//AppendLogLine(temFiles[j].getAbsolutePath(), logpath);
					//AppendLogLine(Directories[i].getName()+"_"+ temFiles[j].getName().substring(0,temFiles[j].getName().lastIndexOf('.')), logpath);
					//AppendLogLine(ImagesFolder + File.separator+Directories[i].getName()+"_"+temFiles[j].getName() , logpath);
				
					AppendLogLine(temFiles[j].getName(), logpath);
			
					NewLogLine(Integer.toString(i+1), logpath);
					
					
					
					
					
					
					
					
					table.incrementCounter(); // this will appear naturally, i guess
					table.addValue("Index", CountFiles); // this will appear naturally, i guess
					table.addValue("LoadFromPath", temFiles[j].getAbsolutePath());
					table.addValue("SaveToPath", ImagesFolder + File.separator+Directories[i].getName()+"_"+temFiles[j].getName() );	
					table.addValue("SampleID",Directories[i].getName()+"_"+ temFiles[j].getName().substring(0,temFiles[j].getName().lastIndexOf('.')));
					table.addValue("FieldOfView", j);
					table.addValue("Channel", (Directories[i].getName()+"_"+ temFiles[j].getName().substring(0,temFiles[j].getName().lastIndexOf('.'))));
					table.addValue("FolderIndex", i);
					CountFiles++;
					
					
					
					
					
					
					
					
					
				
				
				
			}
		}
		table.saveAs(logpath2);
		
	
		
		long endTime   = System.currentTimeMillis();
		long totalTime = (endTime - startTime);
		System.out.println("Done in "+ totalTime +" ms.");
		IJ.showMessage("Input created - Fast Mode");
		java.awt.Toolkit.getDefaultToolkit().beep(); 
			
	}

	
	
	
	private static String[] getHeaders() {

		String[] Headers = new String[7];
		
		Headers[0] = "Index";
		Headers[1] = "LoadFromPath";
		Headers[2] = "SaveToPath";
		Headers[3] = "SampleID";
		Headers[4] = "FieldOfView";
		Headers[5] = "Channel";
		Headers[6] = "FolderIndex";
		
		return Headers;
	}


				
		
	



		

	
	
//----------------------------------------------------------------------------------------------------------------------//
//----------------------------------------------------------------------------------------------------------------------//

	
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
	
	public static String getPath(String question){
		String string = IJ.getDirectory(question);	
			if (string==null){
				getPath(question);
				}
			else return string;
		
		return null;
		
	}
	
	public File StringToFile(String string){
		File file = new File(string);
		return file;
				
	}


			
}

	

