/*
 * 
 */
package tools;
//Collection of pieces of code to help file handling while developing imageJ codes. This should be well commented to have educational and reference purposes
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

	// TODO: Auto-generated Javadoc
/**
	 * The Class Shortcut.
	 */
	public class Shortcut {
		
		/**
		 *  public means that the method is visible and can be called from other objects of other types. 
		 *  Other alternatives are private, protected, package and package-private. See here for more details.
		 * 
		 * 			static means that the method is associated with the class, not a specific instance (object) of that class. 
		 * 			This means that you can call a static method without creating an object of the class.
		 * 
		 * 			void means that the method has no return value. If the method returned an int you would write int instead of void.
		 *
		 * @param inputPath the input path
		 * @return the file list
		 */
		public static File[] getFileList(String inputPath){ // Public = everyone can see. static = no need to create and object
			File INPUT_DIR = new File(inputPath);           //Create a File from a input String
			File[] fListINPUT_DIR = INPUT_DIR.listFiles(); //Get the lost of files in that folder
				if (fListINPUT_DIR==null) return null;   //If empty, return empty
			return fListINPUT_DIR;						//otherwise, return a File[] with all files and directories under the input string path
			
		}
		
		
		/**
		 * Gets the tag.
		 *
		 * @param FileName the file name
		 * @return the tag
		 */
		public static String getTag(String FileName){
			
			 Pattern pattern = Pattern.compile("\\[BF\\]|\\[514\\]|\\[568\\]|\\[Acquisition\\]");
			 Matcher matcher = pattern.matcher(FileName);
			   if (matcher.find()) {
			       //IJ.log(matcher.group(0));
			       String index = matcher.group(0);
			       
			       return index;
			   }
			  return null;
			
		}
		
		/**
		 * Gets the time.
		 *
		 * @param FileName the file name
		 * @return the time
		 */
		public static String getTime(String FileName){
			
			 Pattern pattern = Pattern.compile("min\\d{3}|day\\d{3}|min\\d{2}|day\\d{2}");
			 Matcher matcher = pattern.matcher(FileName);
			   if (matcher.find()) {
			       //IJ.log(matcher.group(0));
			       String index = matcher.group(0);
			       return index;
			   }
			  return null;
			
		}
		
		/**
		 * Gets the number.
		 *
		 * @param FileName the file name
		 * @return the number
		 */
		public static String getNumber(String FileName){
			
			 Pattern pattern = Pattern.compile("\\d{3}|\\d{2}");
			 Matcher matcher = pattern.matcher(FileName);
			   if (matcher.find()) {
			       //IJ.log(matcher.group(0));
			       String index = matcher.group(0);
			       return index;
			   }
			  return null;
			
		}
		
		/**
		 * Gets the time value.
		 *
		 * @param FileName the file name
		 * @return the time value
		 */
		public static String getTimeValue(String FileName){
			
			 Pattern pattern = Pattern.compile("min\\d{3}|day\\d{3}|min\\d{2}|day\\d{2}");
			 Matcher matcher = pattern.matcher(FileName);
			   if (matcher.find()) {
			       //IJ.log(matcher.group(0));
			       String number = getNumber( matcher.group(0));
			       return number;
			   }
			  return null;
			
		}
		
		/**
		 * Gets the time stamp.
		 *
		 * @param FileName the file name
		 * @return the time stamp
		 */
		public static String getTimeStamp(String FileName){
			
			 Pattern pattern = Pattern.compile("min|day");
			 Matcher matcher = pattern.matcher(FileName);
			   if (matcher.find()) {
			       //IJ.log(matcher.group(0));
				   String index = matcher.group(0);
			       return index;
			   }
			  return null;
		}
		
		/**
		 * Gets the sample value.
		 *
		 * @param FileName the file name
		 * @return the sample value
		 */
		public static String getSampleValue(String FileName){
			
			 Pattern pattern = Pattern.compile("spl\\d{3}");
			 Matcher matcher = pattern.matcher(FileName);
			   if (matcher.find()) {
			       //IJ.log(matcher.group(0));
			       String number = getNumber( matcher.group(0));
			       return number;
			   }
			  return null;
			
		}

		/**
		 * Populate.
		 *
		 * @param pathToCSV the path to csv
		 * @return the string[][]
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static String[][] Populate (String pathToCSV) throws IOException{
			
			 BufferedReader CSVFile = new BufferedReader(new FileReader(pathToCSV));
			 //Check number of rows
			 String dataRow = CSVFile.readLine();
		     //check number of columns and get separator
			 int length = geCSVLenght(CSVFile);
	 		 String[] separator = getSeparator(dataRow);
	 		 CSVFile.close();
	 
	 		 
	 		 //----------------------------------------------------//
			 BufferedReader CSVFile1 = new BufferedReader(new FileReader(pathToCSV));
			 String dataRow1 = CSVFile1.readLine();
	 		 String dataRow2 = dataRow1.replace(separator[0], ";");
		     int counterY= 0;//number of rows
			 int realSize = dataRow2.split(";").length;
			 int ExpandedSize =  realSize+2;
			 
			 String[][] MyTable = new String[ExpandedSize][length+1];

		
			 
			 while (dataRow1 != null){
				 	String dataRow3 = dataRow1.replace(separator[0], ";");
				 	String[] dataArray = dataRow3.split(";");
				 	for (int counterX =0; counterX<dataArray.length; counterX++){
						
						MyTable[counterX][counterY] = dataArray[counterX];
				 		
				 	}
		            dataRow1 = CSVFile1.readLine(); // Read next line of data.
		            counterY++;
		        }
			 CSVFile1.close();
			 
			 //return MyTable;
		     return MyTable;
		}	
		
		/**
		 * Ge csv lenght.
		 *
		 * @param csvFile the csv file
		 * @return the int
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static int geCSVLenght(BufferedReader csvFile) throws IOException {
			 int length=0;
			 String dataRow = csvFile.readLine();
		     while (dataRow != null){
		       	length++;
		            dataRow = csvFile.readLine(); // Read next line of data
		     }
		     
			return length;
		}
		
		/**
		 * Gets the separator.
		 *
		 * @param dataRow the data row
		 * @return the separator
		 */
		private static String[] getSeparator(String dataRow) {
			String[] outputs = new String[2];
			
			
			String[] separators = {"\t", ",", ".",};
			outputs[1] = null;
			outputs[0] = null;
			int size = 0;
			for (String item:separators){
				String[] sTab = dataRow.split(item);
				int size2 = sTab.length;
				
				if (size2>size){
					size = size2;
					outputs[0] = item;
					outputs[1] = Integer.toString(size);
				}
				
			}
			
			
			
			return outputs;
			
		
			
		}
		
		/**
		 * Gets the main name.
		 *
		 * @param Filename the filename
		 * @return the main name
		 */
		public static String getMainName(String Filename){
			String[] parts = Filename.split("\\[");
			return  parts[0];
			
		}
		
		/**
		 * Gets the main name and time.
		 *
		 * @param Filename the filename
		 * @return the main name and time
		 */
		public static String getMainNameAndTime(String Filename){
			String[] parts = Filename.split("\\[");
			
			return  parts[0]+"[" +parts[1];
			
		}
		
	
	
}
