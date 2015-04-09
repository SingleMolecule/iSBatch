package plugins_ij;
/** 
 * Victor Caldas
 * This work released under the terms of the General Public License in its latest edition. 
 * */


import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

/** This code takes Bright Field images and combine them in one single stack.
 * As Input, yous should have a control File with all paths.
 * The operantiosn performed are:
 * 1 - Subract the offset value from each pixel;
 * 2 - Divide by the background image, also corrected by the offset value;
 * 3 - Save a stack with all images, keeping the proper names of the files.
 * 
 * @author Victor Caldas	
 *
 */
public class MicrobeTracker_BF_Input implements PlugIn{
	static String logpath;
	static ResultsTable table;
	static String ImagesFolder;
	

	public static void main(String[] args) {
		new MicrobeTracker_BF_Input().run("");
		IJ.log("MicrobeTracker input created!");
		
	}

	public void run(String arg0) {
		
		//Get file
		String csvFilename =  IJ.getFilePath("Provide ControlFile.CSV");
		if (csvFilename==null) return;	
		File file = new File(csvFilename);
		//Load table
		ImagesFolder = tools.iSBOps.checkCreateDir(file.getParent()+File.separator+"Projections");
		loadTable(csvFilename);
			
				//Get Unique tags related to the possible channels
	  
				
		
	  	
    	//List of BF Images
    	List<String> listBF =	getSubFileList(table, "Channel","BF", "WorkingFile");
    	
    	
    	//Ask User 
    	Object[] options = {"Create raw BF Stack", "Create raw BF Stack + Deflicker", "Create Corrected BF Stack"};
    	Component frame = null;
    	int Selection= JOptionPane.showOptionDialog(frame,
    			"Choose the action to be taken:",
    			"BF Stack Creator",
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.QUESTION_MESSAGE,
    			null,     //do not use a custom Icon
    			options,  //the titles of buttons
    			options[0]); //default button title
		
    	ImagePlus BFStackImp = tools.ImageOperations.getStack(listBF);    	
    	
    	if(Selection<=1){
    		//Save BF Stack
    		
    		if( Selection ==0){
    			IJ.saveAsTiff(BFStackImp, file.getParent()+File.separator+"RawStack[BF]");
    		}
    		else if(Selection ==1){
    	
    			tools.ImageOperations.Stack_Deflicker(BFStackImp);
    			IJ.saveAsTiff(BFStackImp, file.getParent()+File.separator+"RawStack_Deflicker[BF]");
    			    		}
			BFStackImp.close();
			
    	}
    	
    	
    	
    	
    	
    	
    	////////User choose to provide BG and DK////////////////////////////////////////
    	////////////////////////////////////////////////////////////////////////////////
    	else if (Selection==2){
    		
    	tools.ImageOperations.Stack_Deflicker(BFStackImp);
    	
    	int  BGSelection = askBackGround();
    	
        
        	
        if(BGSelection==0){
        	String Background =  IJ.getFilePath("Provide the Background Image for the BF channel");
        	ImagePlus Backgroundimp = IJ.openImage(Background);
			
			
			//Choose action with DK
			int DKSelection = askDarkCount();
	        
	        if(DKSelection==0){
	        	String Darkcount =  IJ.getFilePath("Provide the DarkCount Image for the BF channel");
	        	ImagePlus DKimp = IJ.openImage(Darkcount);
	        	ImageProcessor DKip = DKimp.getProcessor();
	        	ImageProcessor Backgroundip = Backgroundimp.getProcessor();
				tools.ImageOperations.BeamProfileCorrection(BFStackImp, DKip, Backgroundip);
				
				
				IJ.saveAsTiff(BFStackImp, file.getParent()+File.separator+"Corrected_Stack[BF]");
				System.out.println("Finishing Loop2");
				BFStackImp.close();
	        }
	        
	        else if(DKSelection==1){
	        	double offset = askOffset();
	        	tools.ImageOperations.BeamProfileCorrection(BFStackImp, offset, Backgroundimp);
				IJ.saveAsTiff(BFStackImp, file.getParent()+File.separator+"Corrected_Stack[BF]");
				BFStackImp.close();
	        }
	        else if (DKSelection == 2){
	        	
	        	tools.ImageOperations.DivideNormalize(BFStackImp, Backgroundimp);
				IJ.saveAsTiff(BFStackImp, file.getParent()+File.separator+"Corrected_Stack[BF]");
				BFStackImp.close();
	        }
			
       }
        //User Choose to calculcate Background from average
        else if(BGSelection==1){
        	ImagePlus Backgroundimp = tools.ImageOperations.doAverage(BFStackImp);
        	
        	IJ.saveAsTiff(Backgroundimp, file.getParent()+File.separator+"CreatedBG[BF]");
			//Choose action with DK
			int DKSelection = askDarkCount();
	        
	        if(DKSelection==0){
	        	String Darkcount =  IJ.getFilePath("Provide the DarkCount Image for the BF channel");
	        	ImagePlus DKimp = IJ.openImage(Darkcount);
	        	ImageProcessor DKip = DKimp.getProcessor();
	        	ImageProcessor Backgroundip = Backgroundimp.getProcessor();
				tools.ImageOperations.BeamProfileCorrection(BFStackImp, DKip, Backgroundip);
				//tools.ImageOperations.BeamProfileCorrection(BFStackImp, DKimp, Backgroundimp);
				IJ.saveAsTiff(BFStackImp, file.getParent()+File.separator+"Corrected_Stack[BF]");
				BFStackImp.close();
	        }
	        
	        else if(DKSelection==1){
	        	double offset = askOffset();
	        	tools.ImageOperations.BeamProfileCorrection(BFStackImp, offset, Backgroundimp);
				IJ.saveAsTiff(BFStackImp, file.getParent()+File.separator+"Corrected_Stack[BF]");
				BFStackImp.close();
	        }
	        else if (DKSelection == 2){
	        	
	        	tools.ImageOperations.DivideNormalize(BFStackImp, Backgroundimp);
				IJ.saveAsTiff(BFStackImp, file.getParent()+File.separator+"Corrected_Stack[BF]");
				BFStackImp.close();
	        }
        }
        	
        else if(BGSelection==2){// The user will not use a background. Will just subtrack offset
          	System.out.println("Background not Selected");	 	
                 	
          
            
        	int DKSelection = askDarkCount();
	        
	        if(DKSelection==0){
	        	String Darkcount =  IJ.getFilePath("Provide the DarkCount Image for the BF channel");
	        	ImagePlus DKimp = IJ.openImage(Darkcount);
	        	tools.ImageOperations.Subtract_Image1_Image2(BFStackImp, DKimp);
				IJ.saveAsTiff(BFStackImp, file.getParent()+File.separator+"Corrected_Stack[BF]");
				BFStackImp.close();
	        }
	        
	        else if(DKSelection==1){
	        	double offset = askOffset();
	        	tools.ImageOperations.Subtract_Image_double(BFStackImp, offset);
				IJ.saveAsTiff(BFStackImp, file.getParent()+File.separator+"Corrected_Stack[BF]");
				BFStackImp.close();
	        }
	        else if (DKSelection == 2){
				BFStackImp.close();
	        }
        	
        	
        	
        }
        
        	
        }
        
        
  
        	
    	System.out.println("---------Done!-------------");
    	
		

		IJ.showMessage("MicrobeTracker BF Input created!");
		java.awt.Toolkit.getDefaultToolkit().beep(); 
		
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

