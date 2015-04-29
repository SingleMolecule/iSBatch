package plugins_ij;

import ij.IJ;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class Create_inputCSV implements PlugIn{

	static String logpath;
	static String OUT_DIR;
		
	public static void main(String[] args) throws IOException {
		
		new Create_inputCSV().run("");
		IJ.log("Files ready to be Copied!");
		
	}
	
	
	

	public void run(String arg0){
		
		
		String INPUT_DIR =  IJ.getDirectory("Select the Main directory with all data to be analysed");	
		if (INPUT_DIR==null) return;

		String OUT_DIR =  IJ.getDirectory("Indicate a directory to keep the Analysed files");	
			if (OUT_DIR==null) return;
		
			
		ResultsTable table = new ResultsTable();
		
		tools.iSBOps.checkCreateDir(OUT_DIR);

		String ImagesFolder = tools.iSBOps.checkCreateDir(OUT_DIR+File.separator+"Images");

		
		
		long startTime = System.currentTimeMillis();

		// Creating file to save all data from files
		
		String strDate = new SimpleDateFormat("yyyyMMdd").format(new Date(startTime));
		
		String logpath2 = OUT_DIR + File.separator+ strDate+"AnalysisFile.csv";
	
		
		// Get the list of folders
		File[] Directories = tools.iSBOps.getFileList(INPUT_DIR);
		int ExperimentIndexCount = 1;
		
		for (int i=0; i< Directories.length; i++){
			System.out.println("*  " + Directories[i].getAbsolutePath());
			
			
			
			// Getting list of Images inside the folder
			File[] temFiles = tools.iSBOps.getImageList(Directories[i].getAbsolutePath());
			
			// Executing file by file
			for (int j=0; j< temFiles.length; j++){
//			System.out.println(temFiles[j].getName());
			
			table.incrementCounter();
			
			table.addValue("OriginaFile", temFiles[j].getAbsolutePath());
			table.addValue("WorkingFile", ImagesFolder + File.separator+Directories[i].getName()+"_"+temFiles[j].getName() );	
			table.addValue("SampleID",temFiles[j].getName());
			
			//Field of View
			String FoV = Directories[i].getName().substring(Directories[i].getName().lastIndexOf('_') + 1); 
			table.addValue("FieldOfView",FoV );
			
			
			
			//get name of the file in the folder, i.e. the channel
			
			String channel = temFiles[j].getName().substring(0, temFiles[j].getName().indexOf('.'));
			//System.out.println(channel);
			table.addValue("Channel", channel);
			
			table.addValue("FolderIndex", i+1);
			
			// Adding ExperimentIndex
			
			if (j==0){
				if (i==0){
					table.addValue("ExperimentIndex", ExperimentIndexCount );
				}
				else if (i !=0) {
					//Check previous name without the endnumber
					String previousName = Directories[i-1].getName().substring(0,Directories[i-1].getName().lastIndexOf("_"));
					String CurrentName = Directories[i].getName().substring(0,Directories[i].getName().lastIndexOf("_"));
					//System.out.println("Counter: " + i);
					if (CurrentName.equalsIgnoreCase(previousName)){
						table.addValue("ExperimentIndex", ExperimentIndexCount );
					}
					else {
							ExperimentIndexCount++;
							table.addValue("ExperimentIndex", ExperimentIndexCount );
					}
					
					
				}
			}
			if(j!=0){
				table.addValue("ExperimentIndex", ExperimentIndexCount );
				
				
			}
			
			
			
			
			//Adding Extra attributes
			List<String> parts = getParts(Directories[i].getName());

			removeFromParts(parts, FoV);
	
			//Check Date
			String date = getDate(parts);
			if(checkCondition(date)){
				table.addValue("Date", date);
				removeFromParts(parts, date);
			}
			String SampleName = parts.get(0);
			removeFromParts(parts, SampleName);
			table.addValue("SampleID", SampleName);
			
			//Check TimeStamp
			//Get TimeStamp
			String TimeStamp[] = getTimeStamp(parts);
			if(checkCondition(TimeStamp[0])){
				table.addValue("TimePoint", TimeStamp[0]);
				table.addValue("TimeUnit", TimeStamp[1]);
				removeFromParts(parts, TimeStamp[0]+TimeStamp[1]);
			}
			
			//Add Damage Agent
			String DamageInfo = getDamagerData(parts);
			if(checkCondition(DamageInfo)){
				String[] damageInfo = splitTags(DamageInfo);
				table.addValue("Damage Agent",damageInfo[0]);
				table.addValue("DamageDose_n",damageInfo[1]);
				table.addValue("DamageDose_s",damageInfo[2]);
							removeFromParts(parts, DamageInfo);
				
			}
			
			//Add Inducer
			//get Inducer
			String InducerInfo[] = getInducerData(parts);
			if(checkCondition(InducerInfo[0])){
				table.addValue("InducerName",InducerInfo[0]);
				table.addValue("InducerConcentration",InducerInfo[1]);
				table.addValue("InducerUnit",InducerInfo[2]);
				removeFromParts(parts, InducerInfo[0]+InducerInfo[1]+InducerInfo[2]);
			}
			
		
					
		
					
					
					
					
					
				
				
				
			}
			
		}
		System.out.println("Saving at: "+ logpath2);
		try {
			table.saveAs(logpath2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		long endTime   = System.currentTimeMillis();
		long totalTime = (endTime - startTime);
		System.out.println("Done in "+ totalTime +" ms.");
		IJ.showMessage("Input created - Fast Mode");
		java.awt.Toolkit.getDefaultToolkit().beep(); 
			
	}

	
	
private void removeFromParts(List<String> parts, String foV) {
		for(int i=0; i<parts.size(); i++){
			String string = parts.get(i);
			
			if (string.equalsIgnoreCase(foV)){
				parts.remove(i);
			}
			
		}
		
	}




//----------------------------------------------------------------------------------------------------------------------//
//----------------------------------------------------------------------------------------------------------------------//

	

	

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
			 //System.out.println(index);
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


			
}

	

