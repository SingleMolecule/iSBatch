package tools;

import ij.IJ;
import ij.measure.ResultsTable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author vcaldas
 *
 */
public class iSBOps {
	
	public static int getCol(String string, String[] headers) {
		int col = -1;
		for(int i=0; i<headers.length; i++){
			System.out.println(headers[i]);
			if( headers[i].equalsIgnoreCase(string)){
				col = i; 
				return col;
			}
		}
		
		return col;
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
	public static String[][] getCSVContent2(String csvFilename) throws IOException {
		//OpenFile
		String delimiter = ",";
		LineNumberReader CSVreader = new LineNumberReader(new FileReader(csvFilename));
		int cols = CSVreader.readLine().split(delimiter).length;
		
		int rows = countLines(CSVreader);
		String[][] Content = new String[rows][cols];
		
		
		File file = new File(csvFilename);
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(file);
		
		


		for (int index = 0; index<rows; index++){
			
	    String line = sc.nextLine();
	       String[] splits = line.split(delimiter);
	    for(int j=0; j<cols; j++){
	    	if(j<splits.length){
	    		
	    		Content[index][j] = splits[j];
	    	  	}
	    	else {
	    		Content[index][j] = "";
	    	}
	    }
	    CSVreader.close();
		
	
	}
		return Content;
}
		
	public	static void main (String[] arg) throws IOException{
	 
		String INPUT_DIR = "D:\\DevFolder";
		long startTime = System.currentTimeMillis();
		String SETUP_DIR = "H:\\SetUpFiles";
		
		System.out.println("------ Starting Point------");	
//Create output dir	
		
		File fINPUT_DIR = new File(INPUT_DIR);
		System.out.println("Input Folder: "+ INPUT_DIR);	
		String OUTPUT_DIR = fINPUT_DIR.getAbsolutePath()+"_Ready";
		
		String READY_DIR = checkCreateDir(OUTPUT_DIR);
	
		System.out.println("------ Organizing Files!------" );	

		
		Organize(getFileList(INPUT_DIR), SETUP_DIR, READY_DIR);		
		
//Get list of all Folders
		
		File[] FilesInFolder = getFileList(INPUT_DIR);
		for (int i=0; i< FilesInFolder.length; i++){
			System.out.println(FilesInFolder[i].getAbsolutePath());
			File[] temFiles = getFileList(FilesInFolder[i].getAbsolutePath());
			for (int j=0; j< temFiles.length; j++){
				System.out.println(temFiles[j].getAbsolutePath());
				
				
				System.out.println(stdFileName(temFiles[j].getAbsoluteFile()));
				
			}
		}
		
		
		long endTime   = System.currentTimeMillis();
		long totalTime = (endTime - startTime);
		System.out.println("Done in "+ totalTime +"ms");
	}

	private static void Organize(File[] fileList, String supportFiles, String READY_DIR) {
			//get offsetlist of files
			for (File targetFile:fileList){
			File[] filelist2 =  getfListTypeFilter(targetFile.getAbsolutePath(),"tif");
			for (@SuppressWarnings("unused") File dummy2:filelist2){
			}	
		}
		
	}
	
	public static String stdFileName(File dummy2) {
		/**
		 * 1 - get parental folder name
		 * 2 - get sample entry
		 * 3 - check if has min, hour value and store it
		 * 
		 */
		
		String piece1 = dummy2.getParentFile().getName();
		String[] parts = piece1.split("_");
		String channel = dummy2.getName().replace(".","].");
		
		
		//Modify here for longer names
		String name = parts[0]+"["+ parts[parts.length -2 ]+"]"+"[spl"+ parts[parts.length -1 ]+"]"+"["+ channel;
		
		return name;
	}

	public static String getTag(String FileName){
		
		 Pattern pattern = Pattern.compile("\\[BF\\]|\\[514\\]|\\[568\\]|\\[Acquisition\\]|\\[TAMRA1\\]|\\[GFP\\]");
		 Matcher matcher = pattern.matcher(FileName);
		   if (matcher.find()) {
		       //IJ.log(matcher.group(0));
		       String index = matcher.group(0);
		       
		       return index;
		   }
		  return null;
		
	}
	
	public static String getNewName(File oldName, File SourceFolder){
		
		String NewName = "["+oldName.getName().replace(".","].");
		NewName = SourceFolder.getName() + NewName;
        //NewName = NewName.replace("TIF", ".TIF");
		
		return NewName;
		
	}	
	
	public static File fishfromPool(File[] arFile, String bait){
		for (File dummy:arFile){
			if (dummy.getName().contains(bait)){
				return dummy.getAbsoluteFile();
			}
		}
		
		return null;
	}
	
	public static File[] getFileList(String inputPath){ // Public = everyone can see. static = no need to create and object
		File INPUT_DIR = new File(inputPath);           //Create a File from a input String
		File[] directories = INPUT_DIR.listFiles(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
		});
			
		
		return directories;
		
		
		
		
		
		
		
		
		
		
		}
	
	public static File[] getImageList(String inputPath){ // Public = everyone can see. static = no need to create and object
		File INPUT_DIR = new File(inputPath);           //Create a File from a input String
		
		
		File[] fListINPUT_DIR = INPUT_DIR.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".tif");
		    }
		});
		
			if (fListINPUT_DIR==null) return null;   //If empty, return empty
		return fListINPUT_DIR;						//otherwise, return a File[] with all files and directories under the input string path
		}
	
	public static String checkCreateSubDir(String INPUT_DIR, String SubfolderName){
		String OUTPUT_DIR = INPUT_DIR + File.separator + SubfolderName;
			
		File theDir = new File(OUTPUT_DIR);
		//IJ.log("Checking if the directory " + theDir + " exist...");
		IJ.log("Creating directory: " + theDir);
		  if (!theDir.exists()) {
		   //IJ.log("Creating directory: " + theDir);
		    boolean result = theDir.mkdir();
		    if(result) {    
		       //IJ.log("Done!");
		       return theDir.getAbsolutePath();
		     }
		  }
		
		return theDir.getAbsolutePath();
	}

	public static String checkCreateDir(String INPUT_DIR){

				
		File theDir = new File(INPUT_DIR);
		
		IJ.log("Checking if the directory " + theDir + " exist...");
		
		 if (!theDir.exists()) {
		    IJ.log("Creating directory: " + theDir);
		    boolean result = theDir.mkdir();
		    if(result) {    
		       //IJ.log("Done!");
		       return theDir.getAbsolutePath();
		     }
		  }
		 else{
			 IJ.log("Directory exist: " + theDir);
		 }
		return theDir.getAbsolutePath();
	}
	

	public static File[] getfListTypeFilter(String inputPath, final String filetype){ // Public = everyone can see. static = no need to create and object
		File INPUT_DIR = new File(inputPath);           //Create a File from a input String
			
		File[] fListINPUT_DIR = INPUT_DIR.listFiles(new FilenameFilter(){
			public boolean accept(File INPUT_DIR, String name){
				return name.toLowerCase().endsWith(filetype);
				//return name.toLowerCase().endsWith(filetype);
			}}); //Get the list of files in that folder
			if (fListINPUT_DIR==null) return null;   //If empty, return empty
		return fListINPUT_DIR;						//otherwise, return a File[] with all files and directories under the input string path+
	}
		
	public static File[] getfListTagFilter(String inputPath, final String filetype){ // Public = everyone can see. static = no need to create and object
		filetype.toLowerCase();
		
		File INPUT_DIR = new File(inputPath);           //Create a File from a input String
			
		File[] fListINPUT_DIR = INPUT_DIR.listFiles(new FilenameFilter(){
			public boolean accept(File INPUT_DIR, String name){
				return name.toLowerCase().contains(filetype);
			}}); //Get the lost of files in that folder
			if (fListINPUT_DIR==null) return null;   //If empty, return empty
		return fListINPUT_DIR;						//otherwise, return a File[] with all files and directories under the input string path+
	}
	
	public static List<String> getSubFileList(File[] myList, String tag){ // Public = everyone can see. static = no need to create and object
		int size = myList.length;
		System.out.println(size);
		List<String> list = new ArrayList<>();
			
		for (int i=0; i<size; i++){
			System.out.println(myList[i]);
			System.out.println(getTag(myList[i].getName()));
			
			if (myList[i].isFile()){
				if (getTag(myList[i].getName()).equals(null)){
					continue;
				}	
				else if(getTag(myList[i].getName()).equals(tag)){
					list.add(myList[i].getAbsolutePath());
				}
			}
		}		
		return list;
	           
	}
	
	public static List<File> getListofFiles(File directory){ // Public = everyone can see. static = no need to create and object
		File[] tempArray = directory.listFiles();
		int size = tempArray.length;
		List<File> list = new ArrayList<>();
			
		for (int i=0; i<size; i++){
			if (tempArray[i].isFile()){
				list.add(tempArray[i]);
				
			}
		}		
		return list;
	           
	}
	
	public static List<File> getListofFiles(File directory,String FileExtention){ // Public = everyone can see. static = no need to create and object
		FileExtention.toLowerCase();
		File[] tempArray = directory.listFiles();
		int size = tempArray.length;
		List<File> list = new ArrayList<>();
			
		for (int i=0; i<size; i++){
			if (tempArray[i].isFile()){
				if(tempArray[i].getAbsolutePath().toLowerCase().endsWith(FileExtention)){
					list.add(tempArray[i]);
				}
			}
		}		
		return list;
	           
	}

	public static void CopyFile(File file, File saveFileat) {
		
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
    	    IJ.log("File: " + file.getName() + "  copied to: "+ saveFileat.getAbsolutePath());
		
			}
		catch(IOException e){
				e.printStackTrace();
		}	
	}
	
	public static String getCoreName(String string){
		
		String output = string.replace(getTag(string), "");
		output = output.replace(".tif", "");
		output = output.replace(".TIF", "");
		return output;
		
		
		
	}
	public static String getTargetFile(String INPUT_DIR, String channel, String threshold){
		String outFile = INPUT_DIR+ File.separator+ "Peaks"+ "["+ channel+ "]"+"_"+threshold +".csv";;
		return outFile;
		
	}
	public static List<String> getSubFileList(String[][] cSVContent, String tag) {
		
		int inputCol = 	getCol("Input",cSVContent[0]);
		int channelCol=	getCol("channel", cSVContent[0]);
		int rows = 		cSVContent.length;
		

		
		List<String> imagesList = new ArrayList<>();
		
	
		imagesList.add(cSVContent[0][inputCol]);
		
		for (int i=1; i<rows; i++){
			if (cSVContent[i][channelCol].equalsIgnoreCase(tag)){

				imagesList.add( cSVContent[i][inputCol]);
			}
		}
		return imagesList;
	}
	public static List<String> getSubFileList2(String[][] cSVContent, String tag) {
		
		
		int inputCol = 	getCol("Input",cSVContent[0]);
		int channelCol=	getCol("channel", cSVContent[0]);
		int rows = 		cSVContent.length;
		

		
		List<String> imagesList = new ArrayList<>();
		
	
		imagesList.add(cSVContent[0][inputCol]);
		
		for (int i=1; i<rows; i++){
			if (cSVContent[i][channelCol].equalsIgnoreCase(tag)){

				imagesList.add( cSVContent[i][inputCol]);
			}
		}
		
		
		return imagesList;
	}
	public static List<String> getSubFileListUniques(String[][] cSVContent,
			String string) {
		//get header index
		List<String> index = getSubFileList(cSVContent, string);
		int inputCol = 	getCol("Channel",cSVContent[0]);
		
		
		System.out.println(index.size());
		System.out.println(inputCol);
		
		
		
		
		
		
		
		return null;
	}
	public static List<String> getCollumData(String[][] cSVContent,
			int index) {
		
		List<String> ItemsList = new ArrayList<>();
		ItemsList.add(cSVContent[0][index]);
		int rows = 		cSVContent.length;
		
		for (int i=1; i<rows; i++){
			ItemsList.add(cSVContent[i][index]);
			}
		
		// TODO Auto-generated method stub
		return ItemsList;
	}	
	public static List<String> getCollumDataNoHeader(String[][] cSVContent,
			int index) {
		
		List<String> ItemsList = new ArrayList<>();
		//ItemsList.add(cSVContent[0][index]);
		int rows = 		cSVContent.length;
		
		for (int i=1; i<rows; i++){
			ItemsList.add(cSVContent[i][index]);
			}
		
		// TODO Auto-generated method stub
		return ItemsList;
	}
	
	
	
	
	public static List<String> getCollumDataUniques(String[][] cSVContent,
			String string) {
		int inputCol = 	getCol(string,cSVContent[0]);
		
		List<String> newList = new ArrayList<String>(new HashSet<String>(getCollumData(cSVContent,inputCol)));
		
		return newList;
	}
	
	public static List<String> getCollumDataUniquesNoHeader(String[][] cSVContent,
			String string) {
		int inputCol = 	getCol(string,cSVContent[0]);
		
		List<String> newList = new ArrayList<String>(new HashSet<String>(getCollumDataNoHeader(cSVContent,inputCol)));
		
		return newList;
	}
	
	public static List<String> getCollumData(String[][] cSVContent,
			String string) {
		int inputCol = 	getCol("Channel",cSVContent[0]);
		List<String> Results = getCollumData(cSVContent,inputCol);
		
		
		return Results;
	}
	public static String[] CheckBoxLabes(List<String> listchannel) {
		//listchannel.remove(0);
		java.util.Collections.sort(listchannel);
		String[] array = listchannel.toArray(new String[listchannel.size()]);
		return array;
	}
	public static List<String> getBFPosition(String[][] cSVContent,
			List<String> listchannel) {
		
		List<String> imagesList = new ArrayList<>();
		imagesList.add("BFIndex");
		
		
		
		
		
		return null;
	}
	public static int getCol(String string, String[][] controlTableArray) {
		int inputCol = getCol(string, controlTableArray[0]);
		
		return inputCol;
	}
	public static String[][] getCSVContent2TAB(String csvFilename) throws IOException {
		//OpenFile
		String delimiter = "\t";
		LineNumberReader CSVreader = new LineNumberReader(new FileReader(csvFilename));
		int cols = CSVreader.readLine().split(delimiter).length;
		
		int rows = countLines(CSVreader);
		String[][] Content = new String[rows][cols];
		
		
		File file = new File(csvFilename);
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(file);
		
		


		for (int index = 0; index<rows; index++){
			
	    String line = sc.nextLine();
	       String[] splits = line.split(delimiter);
	    for(int j=0; j<cols; j++){
	    	if(j<splits.length){
	    		
	    		Content[index][j] = splits[j];
	    	  	}
	    	else {
	    		Content[index][j] = "";
	    	}
	    }
	    CSVreader.close();
		
	
	}
		return Content;
}
		
}