package plugins_ij;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;

import tools.ImageOperations;
import tools.iSBOps;

public class Fluorescence_Correction_IJ implements PlugIn{
		private static final int DOES_16 = 0;
		static ResultsTable table;
		static File file;
		private static String channel;
	//public static void main(String[] args){
		public static void main(String[] args) {
			new Fluorescence_Correction_IJ().run("");
			
				}

	private ImagePlus imp;	
	public int setup(String arg, ImagePlus imp) {
			this.imp = imp;
			return DOES_16;
			}
		
	public void run(String arg0) {
		
		String csvFilename =  IJ.getFilePath("Provide ControlFile.CSV");	
		if (csvFilename==null) return;	
		//Load table
		loadTable(csvFilename);
    	
    	
    	GenericDialog gd = new GenericDialog("Fluorescence Channel");
		gd.addStringField("Channel", channel);
		gd.showDialog();
        	if (gd.wasCanceled()){
        		return;
        	}
        	channel = gd.getNextString();
	
    	//List of BF Images
    	List<String> listchannel = getSubFileList(table, "Channel",channel, "WorkingFile");
    		
    	List<String> uniques = getUniqueTags("Channel", table);
		String[] labels = iSBOps.CheckBoxLabes(uniques);
    	
    	correctImage(listchannel);
        	
    	System.out.println("---------Done!-------------");
    	

		
		IJ.showMessage("Done - Fluorescence Correction of channel: " +channel );
		java.awt.Toolkit.getDefaultToolkit().beep(); 
		
		
	}
	private List<String> getUniqueTags(String collum, ResultsTable table) {
		List<String> list = getAllTags(collum, table);
		HashSet<String> repeated = new HashSet<String>();
		repeated.addAll(list);
		list.clear();
		list.addAll(repeated);
		return list;
		
	}
	private List<String> getAllTags(String collum, ResultsTable table)
	{
		
		List<String> list = new ArrayList<>();
		
		
		for (int i=0; i<table.getCounter(); i++){
			String tag = table.getStringValue(collum, i);
			list.add(tag);
			}
			
				
		return list;
		
	}
	
	
	private List<String> getSubFileList(ResultsTable resultstable, String colNameToCheckMatch, String stringToMatch, String colNameGetValuesFrom) {
		List<String> list = new ArrayList<>();
		
		for (int row = 0; row<resultstable.getCounter(); row++){
			if(resultstable.getStringValue(colNameToCheckMatch, row).equalsIgnoreCase(stringToMatch)){
				list.add(resultstable.getStringValue(colNameGetValuesFrom, row));
			}
			
			
		}
		
		
		
	
		return list;
	}

	private static void correctImage(List<String> listchannel) {
		//Ask User 
		// Ask the Background File/Create
    	
    	int  BGSelection = askBackGround();
    	
        
        //Provide Image	
        if(BGSelection==0){
        	String Background =  IJ.getFilePath("Provide the Background Image for the BF channel");
        	ImagePlus Backgroundimp =new ImagePlus(Background);
			
			
			//Choose action with DK
			int DKSelection = askDarkCount();
			//Provide Image	
	        if(DKSelection==0){
	        	String Darkcount =  IJ.getFilePath("Provide the DarkCount Image for the BF channel");
	        	ImagePlus DKimp =new ImagePlus(Darkcount);
	        	
	        	for(int i=0; i<listchannel.size(); i++){
	        		String pathToFile = listchannel.get(i);
	        		
	        		ImagePlus impInput =new ImagePlus(pathToFile);
	        		System.out.println(pathToFile);
	        		//ImageOperations.ImageCorrection(impInput, Backgroundimp, DKimp);        		
	        		ImageProcessor ipBackground = Backgroundimp.getProcessor();
	        		ImageProcessor ipDarkCount = DKimp.getProcessor();
	        		
	        		int Slices = impInput.getNSlices();
	        		int width = impInput.getWidth();
	        		int height = impInput.getHeight();
	        		
	        		double MaxValue = ipBackground.getMax();

	        		double v1;//Input Image
	        		double v2;//DarkCount / Offset
	        		double v3;//Background
	        		
	        		ImageStack DataStack = impInput.getImageStack();
	        		
	        		for (int slice = 1; slice <= Slices; slice++) {

	        			ImageProcessor dataip = DataStack.getProcessor(slice);
	        			//impInput.setPosition(slice);//
	        			//ImageProcessor dataip = impInput.getProcessor();		
	        			
	        			for (int x=0; x<width; x++) {
	        				for (int y=0; y<height; y++) {
	        					
	        					v1 = dataip.getPixelValue(x,y);
	        					v2 = ipDarkCount.getPixelValue(x, y);
	        					v3 = ipBackground.getPixelValue(x, y);
	        					
	        					double value = (v1-v2)*(MaxValue-v2)/(v3-v2);
	        					dataip.putPixelValue(x, y, value);
	        				
	        				}
	        									
	        				}  
	        			
	        			        			
	        			}
	        			
	        		
	        		IJ.saveAsTiff(impInput, pathToFile);
	        		IJ.log("Finish and saving: " + pathToFile);
	        		impInput.close();
	        	
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        	}
	        	
	        	      	
	        	
				
	        }
	      //Provide Value
	        else if(DKSelection==1){
	        	double offset = askOffset();
	        	IJ.log("Offset : "+ offset);
	        	
	        	ImagePlus impInput =new ImagePlus(listchannel.get(1));
	        	// ArtificialDarkCount = ImageOperations.createArtificialDarkCount(offset, impInput.getWidth(), impInput.getHeight(), 1);
	        	
	        	
	        	for(int i=1; i<listchannel.size(); i++){
	        		impInput =new ImagePlus(listchannel.get(i));
	        		
	        		impInput = ImageOperations.ImageCorrection(impInput, Backgroundimp, offset);        		
	        		IJ.saveAsTiff(impInput, listchannel.get(i));
	        		IJ.log("Finish and saving: " + listchannel.get(i));
	        		impInput.close();
	        		
	        		
	        	}
	        	
	        }
	      //Provide Image	Ignore DarkCount
	        else if (DKSelection == 2){
	        	//Divide without removing offset
	        	IJ.log("Under Development. Send a request to v.e.a.caldas@rug.nl");
	        	
	        	
	        	
	        	
	        	
	        }
			
       }
        //User Choose to calculcate Background from average
        else if(BGSelection==1){
        	ImagePlus Backgroundimp = tools.ImageOperations.doAveragelarge(listchannel);
        	
        	IJ.saveAsTiff(Backgroundimp, file.getParent()+File.separator+"Artificial_FP+["+channel+"]");
			//Choose action with DK
			int DKSelection = askDarkCount();
	        
	        if(DKSelection==0){
	        	String Darkcount =  IJ.getFilePath("Provide the DarkCount Image for the BF channel");
	        	ImagePlus DKimp =new ImagePlus(Darkcount);
	        	
	        	for(int i=0; i<listchannel.size(); i++){
	        		ImagePlus impInput =new ImagePlus(listchannel.get(i));
	        			
	        		impInput = ImageOperations.ImageCorrection(impInput, Backgroundimp, DKimp);        		
	        		IJ.saveAsTiff(impInput, listchannel.get(i));
	        		IJ.log("Finish and saving: " + listchannel.get(i));
	        		impInput.close();
	        		
	        	}
	        	
	        	
				

	        }
	        
	        else if(DKSelection==1){
	        	
	        		double offset = askOffset();
	        	
	        		ImagePlus impInput =new ImagePlus(listchannel.get(1));
	        		ImagePlus ArtificialDarkCount = ImageOperations.createArtificialDarkCount(offset, impInput.getWidth(), impInput.getHeight(), 1);
	        	
	        		
	        		for(int i=1; i<listchannel.size(); i++){
	        		impInput =new ImagePlus(listchannel.get(i));
	        		
	        		impInput = ImageOperations.ImageCorrection(impInput, Backgroundimp, ArtificialDarkCount);        		
	        		IJ.saveAsTiff(impInput, listchannel.get(i));
	        		IJ.log("Finish and saving: " + listchannel.get(i));
	        		
	        		}
	        	
	        		impInput.close();
	        	
	        }
	        else if (DKSelection == 2){
	        	
	        	//Just remove Offset
	        	IJ.log("Under Development. Send a request to v.e.a.caldas@rug.nl");

	        	
	        	
	        	
	        }
        }
        	
        
        else if(BGSelection==2){// The user will not use a background. Will just subtrack offset
          IJ.log("Background not Selected");	 	
                 	
          
            
        	int DKSelection = askDarkCount();
	        
	        if(DKSelection==0){
	        	IJ.log("Under Development. Send a request to v.e.a.caldas@rug.nl");

	        	
	        	
	        }
	        
	        else if(DKSelection==1){
	        	IJ.log("Under Development. Send a request to v.e.a.caldas@rug.nl");

	        	
	        	
	        }
	        else if (DKSelection == 2){
	        	IJ.log("Under Development. Send a request to v.e.a.caldas@rug.nl");
	        }
        	
        	
        	
        }
    }
		


	private static int askBackGround() {
		Object[] options1 = {"Load BackGround Image", "Create Background from AVG", "Ignore Background"};
        Component frame1 = null;
        int BGSelection= JOptionPane.showOptionDialog(frame1,
        		"Choose the action to be taken:",
        		"Background Correction",
        		JOptionPane.YES_NO_OPTION,
        		JOptionPane.QUESTION_MESSAGE,
        		null,     //do not use a custom Icon
        		options1,  //the titles of buttons
        		options1[0]); //default button title
		return BGSelection;
	}

	private static double askOffset() {
		GenericDialog gd = new GenericDialog("Offset Value");
		double offset = 0;
		gd.addNumericField("Offset: ", offset, 0);
		gd.showDialog();
        	if (gd.wasCanceled()){
        		return offset=0 ;
        	}
        offset = (double)gd.getNextNumber();
		return offset;
	}


	
	
	private static int askDarkCount() {
		Object[] options2 = {"Load DarkCount Image", "Create DarkCount Image", "Ignore DarkCount"};
        Component frame2 = null;
        int DKSelection= JOptionPane.showOptionDialog(frame2,
        		"Choose the action to be taken:",
        		"Dark Count Correction",
        		JOptionPane.YES_NO_OPTION,
        		JOptionPane.QUESTION_MESSAGE,
        		null,     //do not use a custom Icon
        		options2,  //the titles of buttons
        		options2[0]); //default button title
	return DKSelection;
}
	
	private void loadTable(String csvFilename) {
		try {
			table = ResultsTable.open(csvFilename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

