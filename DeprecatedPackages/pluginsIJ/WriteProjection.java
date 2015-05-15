/*
 * 
 */
package plugins;


import tools.Shortcut;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Class WriteProjection.
 */
public class WriteProjection {

	/**
	 * The main method.
	 *
	 * @param arg the arguments
	 * @throws Exception the exception
	 */
	@SuppressWarnings("resource")
	public static void main(String[] arg) throws Exception {
		
		//BASIC INPUT!!!!!
		
		String whereisCSV = IJ.getFilePath("Indicate the CSV File with filtered peaks");
       new File(whereisCSV);
        BufferedReader CSVFile = new BufferedReader(new FileReader(whereisCSV));
        String whereisCellData = IJ.getFilePath("Indicate the CSV File with CellData - MT Export2xls");       
       new BufferedReader(new FileReader(whereisCellData));
        File DATA_DIRECTORY = new File(whereisCellData);
        
        //String INPUT_DIR =  IJ.getDirectory("Organizer: Indicate  your source folder...");	
        //String INPUT_DIR = "I:\\20131015\\Ready";
        String BFFile = IJ.getFilePath("Indicate the Bright Field Stack of files");
        //String BFFile = "E:\\Dev\\BF_mtrackerInput.tif";
        	//if (BFFile==null) return;
		long startTime = System.currentTimeMillis();

		print("File " + whereisCSV + " loaded!" );
		print("File " + whereisCellData + " loaded!" );
		print("File " + BFFile + " loaded!" );
		
		//File[] ListOfFiles = iSBTools.Detect_Peaks.getFileList(INPUT_DIR);	
		//List<String> listBF = iSBTools.Detect_Peaks.getSubFileList(ListOfFiles, "[BF]");

       //-----------------Files loaded--------------------------------
		
		
       //Defining positions
        String[] headers = GetHead(whereisCSV);
       
        int Sampleindex = getIndex(headers, "Sample");
        getIndex(headers, "SampleNumber");
        getIndex(headers, "Height");
        getIndex(headers, "volume");
        getIndex(headers, "x");
        getIndex(headers, "y");
        getIndex(headers, "cell");
        getIndex(headers, "L_normalized");
        getIndex(headers, "L");
        getIndex(headers, "D");
        int Sliceindex =  getIndex(headers, "Slice");

        System.out.println("----------Start----------");
   
        
        int CSVLength = GetCSVlenght(whereisCSV);
        System.out.println("CSV total length: "+ CSVLength);
    	
    	// Preparing All Arrays to be used for future analysis - Yes. A lot of checks, re-checks, redundancy and stuff
       
    	String[][] Alldata = Shortcut.Populate(whereisCSV);
    	String[][] CellData =Shortcut.Populate(whereisCellData);
    	String[][] BFRef = AllExperiments(BFFile);
    	String[][] BFRefShort =  BFRefShortener(BFRef);
    	getUniqueDatasetNames(Alldata[Sampleindex]);
    	
    	getUniqueSubDatasetNames(Alldata[Sampleindex], Alldata[Sliceindex]);
    	
    	/**
    	 * CONTAINS SAMPLE NAME -NO SAMPLE NUMBER - AND BOUNDARIES FOR THE SPOTS TABLE. BOUNDARIES RELATED TO LINES!!!
    	 * BoundariesSpots[0] = name, e.g PolIV[min000]
    	 * BoundariesSpots[1] = start
    	 * BoundariesSpots[2] = end
    	 * BoundariesSpots[3] = Spot count
    	 */
    	String[][] BoundariesSpots = boundariesSpots(Alldata,0);
    	
    	/** This string will be hard coded since it contais all required information related to the basis of analysis. Extra columsn can be added latet.
    	 * Report[0][0]  =  Main; PolIV
    	 * Report[0][1]  =  MainTime; PolIV[min010]
    	 * Report[0][2]  =  timePoint; 010
    	 * Report[0][3]  =  BF reference Start; according to Microbetracker
    	 * Report[0][4]  =  BF reference End; according to Microbetracker
    	 * Report[0][5]  =  SpotStart;
    	 * Report[0][6]  =  SpotEnd;
    	 * Report[0][7]  =  ncells;
    	 * Report[0][8]  =  n samples; 
    	 * Report[0][9]  = 	spots
    	 * Report[0][10]  =  average cell length
    	 * Report[0][11] =  sgma avg cell length
    	 * Report[0][12] =  avg cell witdh
    	 * Report[0][13] =  sigma cell witdh
    	 * Report[0][14] =  avg cell area
    	 * Report[0][15] = 	sigma cell area
    	 * Report[0][16] = Empty
    	 * Report[0][17] = Empty
    	 * Report[0][18] = Empty
    	 * Report[0][19] = Empty
    	 * Report[0][20]  = Empty 
    	 */

    	String[][] Report = new String[BFRefShort.length][21];
    	//Writing all headers
    	Report[0][0]  =  "Main";
   	 	Report[0][1]  =  "MainTime";
   	 	Report[0][2]  =  "timePoint";
   	 	Report[0][3]  =  "BFStart";
   	 	Report[0][4]  =  "BFEnd"; 
   	 	Report[0][5]  =  "SpotStart";
   	 	Report[0][6]  =  "SpotEnd";
   	 	Report[0][7]  =  "ncells";
   	 	Report[0][8]  =  "nsamples"; 
   	 	Report[0][9]  =  "nspots";
   	 	Report[0][10] =  "avgCellLength";
   	 	Report[0][11] =  "+/-";
   	 	Report[0][12] =  "avgCellWitdh";
   	 	Report[0][13] =  "+/-";
   	 	Report[0][14] =  "avgCellArea";
   	 	Report[0][15] =  "+/-";
   	 	Report[0][16] =  "Empty";
   	 	Report[0][17] =  "Empty";
   	 	Report[0][18] =  "Empty";
   	 	Report[0][19] =  "Empty";
   	 	Report[0][20] =  "Empty";
   	 
    	//Let's populate! EXTRA CARE WITH INDEX!!!
   	 	//BFRef already have several itens;
		for (int i=1; i<BFRefShort.length; i++){//Skip the header! 
			//print(BFRef[i][0] +"-|-" + BFRef[i][1]+"-|-"+BFRef[i][2]+"-|-"+BFRef[i][3]+"-|-"+BFRef[i][4]);
			Report[i][0] = Shortcut.getMainName(BFRefShort[i][0]);// Parsing Main
			Report[i][1] = Shortcut.getMainNameAndTime(BFRefShort[i][0]);// Parsing Main Time
			Report[i][2] = BFRefShort[i][2];//Parsing timePoint
			Report[i][3] =  BFRefShort[i][3];
			Report[i][4] =  BFRefShort[i][4];
			Report[i][8] =   Integer.toString(Integer.parseInt(BFRefShort[i][4]) - Integer.parseInt(BFRefShort[i][3]) +1); 
		}
		//Adding information about Spots
		print(BFRefShort.length);
		for (int i=1; i<BFRefShort.length; i++){//Skip the header! 
				print("Line: "+ i + " of " +BFRefShort.length );
					Report[i][5] =	BoundariesSpots[i][1];// = start
				Report[i][6] = 	BoundariesSpots[i][2];// = end
				Report[i][9] = 	BoundariesSpots[i][3];// = Spot count
				
				print("Spot count: "+ Report[i][5]+ "-"+ BoundariesSpots[i][2]);
		}
		
		//Adding CellStans
		/** Cell Data Structure
		 * frame	cell	length	area	volume	max width
			0		1		2		3		4		5
		 */
		
		for (int i=1; i<BFRefShort.length; i++){//Skip the header!
			int  Start = 	Integer.parseInt(Report[i][3]); //Get lower boundary;
			int  End = Integer.parseInt(Report[i][4]); //Upper Boundary
			int Cellcount = 0;
			
			ArrayList<Float> CellLengthList = new ArrayList<Float>();
			ArrayList<Float> CellWidthist = new ArrayList<Float>();
			ArrayList<Float> CellAreaList = new ArrayList<Float>();
			
			for (int j=1; j<CellData[0].length; j++){
				int Frame = Integer.parseInt(CellData[0][j]);
				
				if ( Start<=Frame && Frame<=End){
					Cellcount++;
					CellLengthList.add(Float.parseFloat(CellData[2][j]));
					
					CellWidthist.add(Float.parseFloat(CellData[5][j]));
					
					CellAreaList .add(Float.parseFloat(CellData[3][j]));
				}
			}
				
			Report[i][7]  =  Integer.toString(Cellcount);	
			Report[i][10] =  averageAsString(CellLengthList);
	   	 	Report[i][12] =  averageAsString(CellWidthist);
	   	 	Report[i][14] =  averageAsString(CellAreaList);
	   	 	Report[i][11] =   stdDev(CellLengthList);
	   	 	Report[i][13] =   stdDev(CellWidthist);
	   	 	Report[i][15] =   stdDev(CellAreaList);
			}
			
		for (int i=1; i<BFRefShort.length; i++){//Skip the header!
			int  Start = 	Integer.parseInt(Report[i][3]); //Get lower boundary;
			int  End = Integer.parseInt(Report[i][4]); //Upper Boundary
			ArrayList<Float> CellLengthList = new ArrayList<Float>();
			ArrayList<Float> CellWidthist = new ArrayList<Float>();
			ArrayList<Float> CellAreaList = new ArrayList<Float>();
			
			for (int j=1; j<CellData[0].length; j++){
				int Frame = Integer.parseInt(CellData[0][j]);
				
				if ( Start<=Frame && Frame<=End){
					CellLengthList.add(Float.parseFloat(CellData[2][j]));
					
					CellWidthist.add(Float.parseFloat(CellData[5][j]));
					
					CellAreaList .add(Float.parseFloat(CellData[3][j]));
				}
			}
			}
		
		print("proj_x and proj_y saved!");
		print("Report Array populated!");
		
		
		
			
		//write in a CSV
		SaveFile(Report, "Report", DATA_DIRECTORY);
        print("Report file saved!");

	   	print("Creating ImageJ InputTable!");
		print("Alldata Lenght:"+ Alldata[0].length);
	   
	   	String[][] InputImageJ = new String[Alldata[0].length][8];
	   	print(InputImageJ.length);
	   	print(InputImageJ[0].length);
	   	
	   	print(Alldata[0][0]);
	   	print(Alldata[4][0]);
	   	print(Alldata[20][0]);
	   	print(Alldata[21][0]);
	   	print(Alldata[22][0]);

	   	//Writing headers
	   	InputImageJ[0][0] = Alldata[0][0];
   		InputImageJ[0][1] = Alldata[4][0];
   		InputImageJ[0][2] = Alldata[21][0];
   		InputImageJ[0][3] = Alldata[25][0];
   		InputImageJ[0][4] = Alldata[24][0];
   		InputImageJ[0][5] = "proj_x";
   		InputImageJ[0][6] = "proj_y";
   		InputImageJ[0][7] = "Average";
   		
   		for (int i=1; i<Alldata[0].length; i++){
   			print(i+ " of " + Alldata[0].length);
   			InputImageJ[i][0] = Alldata[0][i];//sample
   	   		InputImageJ[i][1] = Alldata[4][i];//y
   	   		InputImageJ[i][2] = Alldata[21][i];//L
   	   		InputImageJ[i][3] = Alldata[25][i];//length
   	   		InputImageJ[i][4] = Alldata[19][i];//
   	   		
   	   		//get AverageCell Length
   	   		int SliceNumber = Integer.parseInt(Alldata[19][i]);
   	   		float AverageCellLength =0;
   	   		for (int j=1; j<Report.length; j++){//1 to skip headers
   	   				int Start = Integer.parseInt(Report[j][3]);
   	   				int End = Integer.parseInt(Report[j][4]);
   	   				
   	   				if( SliceNumber>=Start && SliceNumber<=End){
   	   					AverageCellLength = Float.parseFloat(Report[j][10]);
   	   				}
   	   			 			
   	   		InputImageJ[i][5] = Float.toString(Float.parseFloat(Alldata[23][i])*AverageCellLength);	//proj_x
   	   		InputImageJ[i][7] = Float.toString(AverageCellLength);//average
   	   		
   	   		print(i+ " of " + Alldata[0].length  );
   	   		}
   	   		
   	   		InputImageJ[i][6] = Alldata[22][i];//proj_y
   		}
   		
	   	SaveFile(InputImageJ, "InputImageJ", DATA_DIRECTORY);
    	
    	print ("InputImageJ file saved!");
    	print("Creating macro command file");
    	//getting min and max X and Y values
    	
    	String[] X_values = getColumnValues(InputImageJ, 5);
    	float min_x = 0;
    	float max_x = 0;
    	float min_y = 0;
    	float max_y = 0;
    	
    	for(int i=1; i<X_values.length; i++){
    		float current_x = Float.parseFloat(InputImageJ[i][5]);
    		float current_y = Float.parseFloat(InputImageJ[i][6]);
    		if( current_x>max_x){ max_x = current_x;}
    		if( current_y>max_y){ max_y = current_y;}
    		if( current_y<min_y){ min_y = current_y;}
    		if( current_x<min_x){ min_x = current_x;}
    	}
    	
    	String Proj_minx = (Float.toString(Math.round(min_x - 10)));
    	String Proj_miny = (Float.toString(Math.round(min_y - 5 )));
    	String Proj_maxx =  (Float.toString(Math.round(max_x + 10)));
    	String Proj_maxy =  (Float.toString(Math.round(max_y + 5 )));
    	
    	// Writing Macro Command!
    	
		 try (
	                PrintStream output = new PrintStream(DATA_DIRECTORY.getParentFile() + File.separator + "LocationMapsMacro.txt");
	            ){

	            for(int i =1;i<Report.length;i++){
	            	output.println("run(\"draw data\", \"x_column=proj_x y_column=proj_y slice_column=slice slice_from="+Report[i][3]+ " slice_to="+ Report[i][4]
	            			+ " x_min="+Proj_minx
	            			+ " y_min="+Proj_miny
	            			+ " x_max="+Proj_maxx
	            			+ " y_max="+Proj_maxy
	            			+ " pixel_size=0.10 height=1 standard=0.80"+"\")"+";");
	            	output.println("run(\"Duplicate...\", \"title="+Report[i][1]+"\""+")"+";");
	            	output.println("selectWindow"+"("+"\"Image\""+ ")"+";");   
	            	output.println("close"+"();");
	                }
				output.close();
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
		 try (
	                PrintStream output = new PrintStream(DATA_DIRECTORY.getParentFile() + File.separator + "LocationMapsMacroGaussianNormalized.txt");
	            ){

	            for(int i =1;i<Report.length;i++){
	            	output.println("run(\"draw data\", \"x_column=proj_x y_column=proj_y slice_column=slice slice_from="+Report[i][3]+ " slice_to="+ Report[i][4]
	            			+ " x_min="+Proj_minx
	            			+ " y_min="+Proj_miny
	            			+ " x_max="+Proj_maxx
	            			+ " y_max="+Proj_maxy
	            			+ " pixel_size=0.10 height=1 standard=0.80"+"\")"+";");
	            	output.println("run(\"Duplicate...\", \"title="+Report[i][1]+"\""+")"+";");
	            	output.println("selectWindow"+"("+"\"Image\""+ ")"+";");   
	            	output.println("close"+"();");
	            	output.println("selectWindow"+"("+"\"" + Report[i][1] + "\""+ ")" +";");
	            	output.println("run"+"("+"\"Gaussian Blur...\""+","+ " \"sigma=5\""+ ")"+ ";");
	            	output.println("run"+"("+"\"Divide...\"" +"," + " \"value="+ Report[i][7] + "\"" +")"+ ";");
	                }
	                    
				output.close();

	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
    	
    	System.out.println("----------Done----------");
    	IJ.log("Location Maps done!");
		long endTime   = System.currentTimeMillis();
		long totalTime = (endTime - startTime);
		System.out.println("Done in "+ totalTime +"ms");
    	
    	CSVFile.close();
      } //main()
	
	
	/**
	 * Save file.
	 *
	 * @param Array the array
	 * @param outputName the output name
	 * @param DIRECTORY the directory
	 */
	public static void SaveFile(String[][] Array, String outputName, File DIRECTORY){
		
		 try (
	                PrintStream output = new PrintStream(DIRECTORY.getParentFile() + File.separator + outputName+ ".csv");
	            ){

	            for(int i =0;i<Array.length;i++){
	            	String sc ="";
	                for (int j=0;j<Array[i].length;j++){
	                	sc+=Array[i][j]+",";
	                }
	                output.println(sc);
	                //output.println(Array[i][0] +"," + Array[i][1]+","+Array[i][2]+","+Array[i][3]+","+Array[i][4]+","+Array[i][5]+","+Array[i][6]+","+Array[i][7]+","+Array[i][8]+","+Array[i][9]+","+Array[i][10]+","+Array[i][11]+","+Array[i][12]+","+Array[i][13]+","+Array[i][14]+","+Array[i][15]+","+Array[i][16]+","+Array[i][17]+","+Array[i][18]+","+Array[i][19]+","+Array[i][20]);
	            }
	            output.close();

	        } catch (FileNotFoundException e) {

	            e.printStackTrace();
	        }
	}
	
	/**
	 * Average as string.
	 *
	 * @param m the m
	 * @return the string
	 */
	private static String averageAsString(ArrayList<Float> m) {
		 float sum = 0;
		    for(int i = 0; i < m.size(); i++)
		    {
		        sum += m.get(i);
		    }
		    
		    float avg = sum/m.size();
		    return Float.toString(avg);

	}
	
	/**
	 * Average as float.
	 *
	 * @param m the m
	 * @return the float
	 */
	private static Float averageAsFloat(ArrayList<Float> m) {
		 float sum = 0;
		    for(int i = 0; i < m.size(); i++)
		    {
		        sum += m.get(i);
		    }
		    		    
		    return sum/m.size();

	}
	
	/**
	 * Std dev.
	 *
	 * @param m the m
	 * @return the string
	 */
	private static String stdDev(ArrayList<Float> m) {
		float mean = averageAsFloat(m);
		 float sum = 0;
		    for(int i = 0; i < m.size(); i++)
		    {
		        sum += (m.get(i)-mean)* (m.get(i)-mean);
		    }
		    
		    
		    float avg = (float) Math.sqrt(sum/m.size());
		    
		    return Float.toString(avg);

	}
		
	/**
	 * All experiments.
	 *
	 * @param BFPath the BF path
	 * @return the string[][]
	 */
	public static String[][] AllExperiments(String BFPath){
		ImagePlus imp = IJ.openImage(BFPath);
		ImageStack stack = imp.getStack();
		String[][] all = new String[stack.getSize()+1][5];
		all[0][0] = "Sample";
		all[0][1] = "BFIndex";
		all[0][2] = "SampleTime";
		all[0][3] = "MainName";
		all[0][4] = "MainNameTime";
		
		
		for (int i=1; i<=stack.getSize(); i++){
			all[i][0] = stack.getShortSliceLabel(i);
			all[i][1] = Integer.toString(i);
			all[i][2] = Shortcut.getTimeValue(stack.getShortSliceLabel(i));
			all[i][3] = Shortcut.getMainName(stack.getShortSliceLabel(i));
			all[i][4] = Shortcut.getMainNameAndTime((stack.getShortSliceLabel(i)));
		}
		
		

		
		imp.close();
		
		return all;
		
	}
	
	/**
	 * BF ref shortener.
	 *
	 * @param bFRef the b f ref
	 * @return the string[][]
	 */
	public static String[][] BFRefShortener(String[][] bFRef){
		String[][] all = bFRef;
		String[] UniqueNames = OneOfEach(getColumnValues(all, 4));
		for (int i=0; i<UniqueNames.length; i++){
			//print(all[i][0] +"-|-" + all[i][1]+"-|-"+all[i][2]+"-|-"+all[i][3]+"-|-"+all[i][4]);
		}
		String[][] NewTable = new String[UniqueNames.length][5];
		NewTable[0][0] = UniqueNames[0];
		NewTable[0][1] = "MainName";
		NewTable[0][2] = "TimePoint";
		NewTable[0][3] = "BFStart";
		NewTable[0][4] = "BFEnd";
		for (int i=1; i<UniqueNames.length; i++){
			String dummy = UniqueNames[i];
			NewTable[i][0] = dummy;
			NewTable[i][1] = Shortcut.getMainName(dummy);
			NewTable[i][2] = Shortcut.getTimeValue(dummy);
			
			

			NewTable[i][3] = StartEnd(dummy, all)[0];
			NewTable[i][4] = StartEnd(dummy, all)[1];
									
			
		}
		for (int i=0; i<NewTable.length; i++){
			//print(NewTable[i][0] +"-|-" + NewTable[i][1]+"-|-"+NewTable[i][2]+"-|-"+NewTable[i][3]+"-|-"+NewTable[i][4]);
		}
		print(NewTable.length);
		return NewTable;
		
	}
	
	/**
	 * Start end.
	 *
	 * @param dummy the dummy
	 * @param all the all
	 * @return the string[]
	 */
	public static String[] StartEnd(String dummy,String[][] all){
		int[] index = new int[2];
		index[0] = (all.length);
		index[1]=0;
		for (int j=1; j<all.length; j++){
			
			if (dummy.equals(all[j][4])){
				int position = Integer.parseInt(all[j][1]);
				if (index[0]>position){
					index[0]=position;
				}
				if (index[1]<position){
					index[1]=position;
				}
			}
		}
		
		String[] Output = new String[2];
		Output[0] = Integer.toString(index[0]);
		Output[1] = Integer.toString(index[1]);
		
		
		return Output;
		
		
	}

	/**
	 * Gets the column values.
	 *
	 * @param AllData the all data
	 * @param Column the column
	 * @return the column values
	 */
	public static String[] getColumnValues(String[][] AllData, int Column){
		String[] column = new String[AllData.length];
		column[0] = AllData[0][Column];
		for (int i=0; i<AllData.length; i++)
			column[i] = AllData[i][Column];
		return column;
	}

	/**
	 * One of each.
	 *
	 * @param AllData the all data
	 * @return the string[]
	 */
	public static String[] OneOfEach(String[] AllData){
		List<String> UniqueList = new ArrayList<String>();
		UniqueList.add(AllData[0]);
		for(int i =1; i<AllData.length; i++){
			String item2 = AllData[i];
			//System.out.print(item2 + "\n" );
				boolean add = checkMatch(item2, UniqueList);
				//System.out.println(add);
				if (add==false){
					UniqueList.add(item2);
					
					//System.out.println(UniqueList.size()+ "added " + item2);
			}
			
		}
		String[] results = new String[UniqueList.size()];
		
		for(int i=0; i<UniqueList.size();i++){
			results[i] = UniqueList.get(i);
			//print(results[i]);
		}
		
		
		return results;
		
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

	/**
	 * Boundaries spots.
	 *
	 * @param alldata the alldata
	 * @param Sample the sample
	 * @return the string[][]
	 */
	public static String[][] boundariesSpots(String[][] alldata, int Sample) {
		List<String> SubDatasetsTime = getUniqueSubDatasetTimeStamp(alldata[Sample]);
		String[][] BoundariesFix = new String[SubDatasetsTime.size()][2];
		String[][] BoundariesFixComplete = new String[SubDatasetsTime.size()][4];
		
		for (int j=0; j<SubDatasetsTime.size(); j++){
			String item = SubDatasetsTime.get(j);
			int counter = 0;

			for(int i=1; i<alldata[Sample].length;i++){
				String[] CompleteSampleName = alldata[Sample][i].split("\\[");
				String SampleNameWithTime = CompleteSampleName[0]+"["+CompleteSampleName[1];
				if (item.equals(SampleNameWithTime)){
					counter++;
			}
						
		}
			BoundariesFix[j][0] = SubDatasetsTime.get(j);
			BoundariesFix[j][1] = Integer.toString(counter);
		}
		
		for (int i=0;i< BoundariesFix.length; i++){
			if (i==0){//that's just the header
				BoundariesFixComplete[i][0] =BoundariesFix[i][0];
				BoundariesFixComplete[i][1] =Integer.toString(Integer.parseInt(BoundariesFix[i][1]) );
				BoundariesFixComplete[i][2] =Integer.toString(Integer.parseInt(BoundariesFix[i][1]) );
				BoundariesFixComplete[i][3] = " ";
			}

			else{
				BoundariesFixComplete[i][0] =BoundariesFix[i][0];
				BoundariesFixComplete[i][1] =Integer.toString(Integer.parseInt(BoundariesFixComplete[i-1][2]) + 1);
				BoundariesFixComplete[i][2] =Integer.toString(Integer.parseInt(BoundariesFixComplete[i-1][2]) +Integer.parseInt(BoundariesFix[i][1]));
				BoundariesFixComplete[i][3] =  BoundariesFix[i][1];
			}
			
			System.out.println(BoundariesFixComplete[i][0]+"---"+  BoundariesFixComplete[i][1]+"---"+BoundariesFixComplete[i][2] + "---"  +BoundariesFixComplete[i][3] );
			
		}
	

		return BoundariesFixComplete;
	}

	/**
	 * Gets the unique sub dataset time stamp.
	 *
	 * @param AllData the all data
	 * @return the unique sub dataset time stamp
	 */
	public static List<String> getUniqueSubDatasetTimeStamp(String[] AllData){
		List<String> UniqueList = new ArrayList<String>();
		//System.out.println(UniqueList.size());
		UniqueList.add(AllData[0]);
		//System.out.println(AllData[0]);
		for(int i =1; i<AllData.length; i++){
			String[] item3 = AllData[i].split("\\[");
			String item2 = item3[0]+"["+item3[1];
			
			//System.out.print(item2 + "\n" );
				boolean add = checkMatch(item2, UniqueList);
				//System.out.println(add);
				if (add==false){
					UniqueList.add(item2);
					
					//System.out.println(UniqueList.size()+ "added " + item2);
			}
			
		}
		
		return UniqueList;
	}
	
	/**
	 * Check match split.
	 *
	 * @param toCheck the to check
	 * @param ListOfValues the list of values
	 * @return true, if successful
	 */
	public static boolean checkMatchSplit(String toCheck, List<String> ListOfValues){
		for (int i=0; i<ListOfValues.size(); i++){
			String[] parts = ListOfValues.get(i).split("\\[");
			
			if (toCheck.equals(parts[0])){
				return true;
			}
				
		}
		
		return false;
		
		
	}	
	
	/**
	 * Gets the unique dataset names.
	 *
	 * @param AllData the all data
	 * @return the unique dataset names
	 */
	public static List<String> getUniqueDatasetNames(String[] AllData){
		List<String> UniqueList = new ArrayList<String>();
		UniqueList.add(AllData[0]);
		
		for(int i =1; i<AllData.length; i++){
			String item2 = AllData[i];
			String[] parts = item2.split("\\[");
			String item3 = parts[0];
			
				boolean add = checkMatchSplit(item3, UniqueList);
				//System.out.println(add);
				if (add==false){
					UniqueList.add(item3);
			}
			
		}
		/**
		for (int w= 0; w<UniqueList.size(); w++){
			System.out.println(UniqueList.get(w));
		}
		*/
		return UniqueList;
	}
	
	/**
	 * Check match.
	 *
	 * @param toCheck the to check
	 * @param ListOfValues the list of values
	 * @return true, if successful
	 */
	public static boolean checkMatch(String toCheck, List<String> ListOfValues){
		//System.out.println("Im here");
		for (int i=0; i<ListOfValues.size(); i++){
			//System.out.println("Compare: "+ toCheck+ " ----=----" + ListOfValues);

			if (toCheck.equals(ListOfValues.get(i))){
				//System.out.println("Compare: "+ toCheck+ " ----=----" + ListOfValues);
				return true;
			}
				
		}
		
		return false;
		
		
	}

	/**
	 * Gets the unique sub dataset names.
	 *
	 * @param AllData the all data
	 * @return the unique sub dataset names
	 */
	public static List<String> getUniqueSubDatasetNames(String[] AllData){
		List<String> UniqueList = new ArrayList<String>();

		UniqueList.add(AllData[0]);
		for(int i =1; i<AllData.length; i++){
			String item2 = AllData[i];
			//System.out.print(AllData[i] + "\n");
				boolean add = checkMatch(item2, UniqueList);
				//System.out.println(add);
				if (add==false){
					UniqueList.add(AllData[i]);

			}
			
		}
		/**
		for (int w= 0; w<UniqueList.size(); w++){
			System.out.println(UniqueList.get(w));
		}
		*/
		return UniqueList;
	}
	
	/**
	 * Gets the unique sub dataset names.
	 *
	 * @param AllData the all data
	 * @param alldata2 the alldata2
	 * @return the unique sub dataset names
	 */
	public static List<String> getUniqueSubDatasetNames(String[] AllData, String[] alldata2){
		List<String> UniqueList = new ArrayList<String>();
		List<String> UniqueListwithSlice = new ArrayList<String>();

		UniqueList.add(AllData[0]);
		UniqueListwithSlice.add(AllData[0]);
		for(int i =1; i<AllData.length; i++){
			String item2 = AllData[i];
			//System.out.print(AllData[i] + "\n");
				boolean add = checkMatch(item2, UniqueList);
				//System.out.println(add);
				if (add==false){
					UniqueList.add(AllData[i]);
					UniqueListwithSlice.add(AllData[i]+","+alldata2[i]);

			}
			
		}
		/**
		for (int w= 0; w<UniqueList.size(); w++){
			System.out.println(UniqueList.get(w));
		}
		*/
		return UniqueListwithSlice;
	}
	
	/**
	 * Gets the index.
	 *
	 * @param inputString the input string
	 * @param ColumnName the column name
	 * @return the index
	 */
	public static int getIndex(String[] inputString, String ColumnName){
		int length = inputString.length;
		for (int i=0; i<length; i++){
			if (inputString[i].toLowerCase().equals(ColumnName.toLowerCase())){
				return i;
			}
		}
				
		
		return 0;	
		
	}
	
	/**
	 * Gets the head.
	 *
	 * @param pathToCSV the path to csv
	 * @return the string[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String[] GetHead(String pathToCSV) throws IOException{
		 BufferedReader CSVFile = new BufferedReader(new FileReader(pathToCSV));

	        String dataRow = CSVFile.readLine(); // Read the first line of data.
	        // The while checks to see if the data is null. If it is, we've hit
	        //  the end of the file. If not, process the data.
	        	dataRow.replace("\\t", ",");
	            String[] dataArray = dataRow.split(";");
	           return dataArray;
	           //CSVFile.close();
		
	}
	
	/**
	 * Gets the cs vlenght.
	 *
	 * @param CSVFilePath the CSV file path
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static int GetCSVlenght(String CSVFilePath) throws IOException{
        BufferedReader CSVFile = new BufferedReader(new FileReader(CSVFilePath));
	    String dataRow = CSVFile.readLine();
        int length=0;
        while (dataRow != null){
        	length++;
            dataRow = CSVFile.readLine(); // Read next line of data
          }
        CSVFile.close();
	return length;
}
	

	}
	

