package Deprecated;

import ij.IJ;
import ij.plugin.PlugIn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Create_inputCSV_full_IJ implements PlugIn {

	static String logpath;
	static String OUT_DIR;
	
	public static void main(String[] args) {
		new Create_inputCSV_full_IJ().run("");
		System.out.println("Create_inputCSV_full_IJ");
	}
	public void run(String arg0){
		
		//Get Main Folder that contains all subfolders from Olympus Excellence Software
		//String INPUT_DIR =  "H:\\DevFolder";
		String INPUT_DIR =  IJ.getDirectory("Indicate the main directory to be organized");	
		if (INPUT_DIR==null) return;
		
		//Get the Output
		//OUT_DIR =  "H:\\TestOutput";
		String OUT_DIR =  IJ.getDirectory("Indicate the main directory to be organized");	
		if (OUT_DIR==null) return;
		tools.iSBOps.checkCreateDir(OUT_DIR);
		tools.iSBOps.checkCreateDir(OUT_DIR+File.separator+"Images");
		//Check or Create log file

		
		
		long startTime = System.currentTimeMillis();
		// Creating file to save all data from files
		String strDate = new SimpleDateFormat("yyyyMMdd").format(new Date(startTime));
		logpath = OUT_DIR + File.separator+ strDate+"_InputFile.csv";
		try {
			new FileOutputStream(logpath, true).close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Write file headers
		String Headers[] = getHeaders();
		try {
			WriteArray(Headers,logpath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Get the list of folders
		File[] FilesInFolder = tools.iSBOps.getFileList(INPUT_DIR);
		int CountingFiles = 1;
		for (int i=0; i< FilesInFolder.length; i++){
			System.out.println("*  " + FilesInFolder[i].getAbsolutePath());
			
			// Getting list of Images inside the folder
			File[] temFiles = tools.iSBOps.getImageList(FilesInFolder[i].getAbsolutePath());
			
			// Executing file by file
			for (int j=0; j< temFiles.length; j++){
					
				
				String[] ParsedInfo = null;
				try {
					ParsedInfo = ParseName(temFiles[j].getName(),FilesInFolder[i].getName());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ParsedInfo[1] = FilesInFolder[i].getAbsolutePath();
				ParsedInfo[0] = Integer.toString(CountingFiles);
				CountingFiles++;
				try {
					WriteArray(ParsedInfo, logpath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				
			}
		}
		long endTime   = System.currentTimeMillis();
		long totalTime = (endTime - startTime);
		System.out.println("Done in "+ totalTime +" ms.");
		IJ.showMessage("Input created - Full Mode");
		java.awt.Toolkit.getDefaultToolkit().beep(); 

			
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
		String[] Headers = new String[17];
		
		
			Headers[0]="Index";
			Headers[1]="Original_Address";
			Headers[2]="ID";
			Headers[3]="New_Address";
			Headers[4]="Date";
			Headers[5]="Sample";
			Headers[6]="Damage Agent";
			Headers[7]="DamageDose_n";
			Headers[8]="DamageDose_s";
			Headers[9]="Time";
			Headers[10]="TimeStamp";
			Headers[11]="InducerName";
			Headers[12]="Inducer_Concentration";
			Headers[13]="Inducer Unit";
			Headers[14]="Replicate";
			Headers[15]="Channel";
			Headers[16]="OD";





		return Headers;
	}

	static String[] ParseName(String fileName, String FolderName) throws IOException {
		String[] sOutput = new String[17];
				
		//Get image tag, e.g. [BF], [568]...
		sOutput[15] =  fileName;
		
		
		
		
		//Check for extra attributes
		List<String> parts = getParts(FolderName);

			
		//Having the parts, it's time to identify each of them accordingly
		
		//Check date 
		String date = getDate(parts);
		boolean hasDate = checkCondition(date);	
		
		
		
		//Get Sample Name - not safe
		String SampleName =  parts.get(0);
		parts.remove(0);
		sOutput[2] = SampleName;
		
		
		//Get Sample Replicate
		String ReplicateNumber = getReplicate(parts);
		boolean hasReplicateNumber = checkCondition(ReplicateNumber);
		sOutput[14] = ReplicateNumber;
		
		//Get TimeStamp
		String TimeStamp[] = getTimeStamp(parts);
		boolean hasTimeStamp = checkCondition(TimeStamp[0]);	

		
		
		//get Inducer
		String InducerInfo[] = getInducerData(parts);
		boolean hasInducer = checkCondition(InducerInfo[0]);
		

		
		
		
		//get Damage
		String DamageInfo = getDamagerData(parts);
		boolean hasDamageAgent = checkCondition(DamageInfo);
		
		//get Plasmid
		String PlasmidInfo = getPlasmidData(parts);
		boolean hasPlasmid = checkCondition(PlasmidInfo);
		
		//get OD
		//String ODInfo = getODData(parts);
		boolean hasOD = checkCondition(PlasmidInfo);
		
		String FPInfo = getFPData(parts);
		boolean hasFP = checkCondition(FPInfo);
		
		
		//Save the information in a TXT/CSV File
		
				
		
		//NewFileName
		String NewFileName = SampleName; 
		if (hasDate == true){
			sOutput[4] = date;
			NewFileName  += "["+date+"]";
		}
		if (hasTimeStamp == true){
			sOutput[10]=TimeStamp[1];
			sOutput[9]=TimeStamp[0];
			NewFileName  += "["+TimeStamp[1]+TimeStamp[0]+"]";
		}
		if (hasDamageAgent == true){
			String[] damageInfo = splitTags(DamageInfo);
			sOutput[6]=damageInfo[0];
			sOutput[7]=damageInfo[1];
			sOutput[8]=damageInfo[2];
			NewFileName  += "["+damageInfo[0]+damageInfo[1]+"]";
			
		}
		if (hasInducer == true){
			sOutput[11]=InducerInfo[0];
			sOutput[12]=InducerInfo[1];
			sOutput[13]=InducerInfo[2];
			NewFileName  += "["+InducerInfo[0]+InducerInfo[1]+"]";
		}
		if (hasPlasmid == true){
			if (hasFP == true){
				NewFileName  += PlasmidInfo+"["+FPInfo+"]" ;
			}
			else{
				NewFileName  += PlasmidInfo;
			}
			
		}
		if(hasOD == true){
			//NewFileName += ODInfo;
		}
		if (hasReplicateNumber == true){
			NewFileName  += "[rep"+ ReplicateNumber+ "]";
		}
		sOutput[3] =OUT_DIR + File.separator +NewFileName+ fileName;
		sOutput[2] = NewFileName +=fileName.substring(0,fileName.lastIndexOf('.'));
		
		return sOutput;

	}
	
	private static String getFPData(List<String> parts) {
		Pattern pattern = Pattern.compile("^\\b[mkate2]|^\\b[ypet]", Pattern.CASE_INSENSITIVE);
		String index = null;
		for (int i=0; i< parts.size(); i++){
		
		Matcher matcher = pattern.matcher(parts.get(i));
		
		if (matcher.find()) {
						
			 index = parts.get(0);
		     parts.remove(i);		
			 
		   }
		   
		 
		}
		return index;	
	}

/**
	private static String getODData(List<String> parts) {
		// TODO Auto-generated method stub
		return null;
	}

*/
	private static String[] splitTags(String tag){
						
		/**
		 * tags[0] = Name; e.g. Ara, min, UVL, IPTG....
		 * tags[1] = value of concentration or dilution. Eg. 10, 10.5 etc..
		 * tags[2] = un; mM, uM, nM, 
		 */
		String[] tags = new String[3];
		tags[0]=tag;//in case that it was not identified or able to split
		tags[1] = tag;
		tags[2]= tag; 
		
		//getting the name
		Pattern pattern = Pattern.compile("^\\D{1,4}");
		Matcher matcher = pattern.matcher(tag);
		 if (matcher.find()) {
			 tags[0] = matcher.group(0);
			 tag = tag.replace(matcher.group(0), "");
			
		
		   }
		
		 /** The number structure could be in the following options
		  * 100
		  * 100m
		  * 100m43
		  */
		 
		Pattern pattern1 = Pattern.compile("\\d{1,4}");
		Matcher matcher1 = pattern1.matcher(tag);
		 if (matcher1.find()) {
			 tags[1] = matcher1.group(0);
			 tag = tag.replace(matcher1.group(0), "");
			
		 }
		 
		 
		Pattern pattern2 = Pattern.compile("[dmun]");
			Matcher matcher2 = pattern2.matcher(tag);
			 if (matcher2.find()) {
				
				 if ( matcher2.group(0).equals("m")){
					 tags[2] = matcher2.group(0)+"M";
					 tag = tag.replace(matcher2.group(0), "");
					 
					 matcher1 = pattern1.matcher(tag);
					 if (matcher1.find()) {
						 tags[1] = tags[1]+ "."+ matcher1.group(0);
						
					 }
					 
					 
					 
				 }
				 else if ( matcher2.group(0).equals("u")){
					 tags[2] = matcher2.group(0)+"M";
					 tag = tag.replace(matcher2.group(0), "");
					 matcher1 = pattern1.matcher(tag);
					 if (matcher1.find()) {
						 
						 tags[1] = tags[1]+ "."+ matcher1.group(0);
						
					 }
				 }
				 else if ( matcher2.group(0).equals("n")){
					 tags[2] = matcher2.group(0)+"M";
					 tag = tag.replace(matcher2.group(0), "");
					 matcher1 = pattern1.matcher(tag);
					 if (matcher1.find()) {
						 tags[1] = tags[1]+ "."+ matcher1.group(0);
						
					 }
				 }
				 else if ( matcher2.group(0).equalsIgnoreCase("x")){
					 tags[2] = matcher2.group(0);
					 tag = tag.replace(matcher2.group(0), "");
					 matcher1 = pattern1.matcher(tag);
					 
				 }
				 
				 
				 else{
					 tags[2] = matcher2.group(0);
					 tag = tag.replace(matcher2.group(0), "");
				 }
				 	
			     
			   }
			 
		
		return tags;
	}

	private static String getPlasmidData(List<String> parts) {
		Pattern pattern = Pattern.compile("\\b[pBAD]", Pattern.CASE_INSENSITIVE);
		String index = null;
		for (int i=0; i< parts.size(); i++){
		
		Matcher matcher = pattern.matcher(parts.get(i));
		
		if (matcher.find()) {
						
			 index = parts.get(0);
			 print(index);
		     parts.remove(i);		
			 
		   }
		   
		 
		}
		return index;	
	}
	
	private static String getDamagerData(List<String> parts) {
		Pattern pattern = Pattern.compile("\\b[UVL]{2,4}|\\b[NFZ]{2,4}", Pattern.CASE_INSENSITIVE);
		String index = null;
		for (int i=0; i< parts.size(); i++){
		
		Matcher matcher = pattern.matcher(parts.get(i));
				
		if (matcher.find()) {
						
			 index = parts.get(i) ;
		     parts.remove(i);		
			 
		   }
		   
		 
		}
		return index;	
	}

	private static String[] getInducerData(List<String> parts) {
		
		//Pattern pattern = Pattern.compile("\\b[Ara]{2,4}", Pattern.CASE_INSENSITIVE);
		Pattern pattern = Pattern.compile("\\b[Ara]{2,4}|\\b[IPTG]{2,4}", Pattern.CASE_INSENSITIVE);
		String[] index = new String[3];
		for (int i=0; i< parts.size(); i++){
		
		Matcher matcher = pattern.matcher(parts.get(i));
				
		if (matcher.find()) {
			 index = splitTags(parts.get(i));
			
		    parts.remove(i);		
			
		   }
		   
		 
		}
		return index;	
	}

	private static String[] getTimeStamp(List<String> parts) {
		
		Pattern pattern = Pattern.compile("\\b[min]{2,4}", Pattern.CASE_INSENSITIVE);
		String index[] = new String[2];
		for (int i=0; i< parts.size(); i++){
		
		Matcher matcher = pattern.matcher(parts.get(i));
				
		if (matcher.find()) {
			//Have to separate string from the int; //min15
			index = SeparateStringFromInt(parts.get(i));
			//index 0 - int
			//index 1 - string
			parts.remove(i);

		   }
		   
		 
		}
		return index;	
	}

	private static String[] SeparateStringFromInt(String string) {
		String[] tags = new String[2];
		
		Pattern pattern = Pattern.compile("\\D{2,4}", Pattern.CASE_INSENSITIVE);
		
		Matcher matcher = pattern.matcher(string);
		if (matcher.find()) {
			tags[1] = matcher.group(0);
		}
		
		Pattern pattern2 = Pattern.compile("\\d{2,4}", Pattern.CASE_INSENSITIVE);
		
		Matcher matcher2 = pattern2.matcher(string);
		if (matcher2.find()) {
			tags[0] = matcher2.group(0);
		}
		
		return tags;
	}

	private static String getReplicate(List<String> parts) {
		Pattern pattern = Pattern.compile("^\\d{3}");
		String index = null;
		for (int i=0; i< parts.size(); i++){
		
		Matcher matcher = pattern.matcher(parts.get(i));
		 if (matcher.find()) {
			 index = matcher.group(0);
		     parts.remove(i);		
		         
		   }
		   
		 
		}
		return index;	
	}

	private static boolean checkCondition(String date) {
		boolean condition;
		if (date==null){
			condition = false;
				}
		else{
			condition = true;
		}
		return condition;
	}

	private static String getDate(List<String> parts) {
		Pattern pattern = Pattern.compile("\\d{6,8}");
		String index = null;
		for (int i=0; i< parts.size(); i++){
		
		Matcher matcher = pattern.matcher(parts.get(i));
		 if (matcher.find()) {
			 index = matcher.group(0);
		      parts.remove(i);
		         
		   }
		   
		 
		}
		return index;		
	}
	
	public static List<String> getParts(String Folderdame){
		String[] parts = Folderdame.split("_");
		List<String> myList = new LinkedList<String>();
		for (int i=0; i<parts.length; i++){
			myList.add(parts[i]);
		}
		
		
		return myList;
				
	}
	
//----------------------------------------------------------------------------------------------------------------------//
//----------------------------------------------------------------------------------------------------------------------//
	public static void print(String string){
		System.out.println(string);
	}
	public static void print(int string){
		System.out.println(string);
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

	
			
}

	

