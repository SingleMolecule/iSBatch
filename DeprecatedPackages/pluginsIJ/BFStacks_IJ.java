/*
 * 
 */
package pluginsIJ;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

// TODO: Auto-generated Javadoc
/**
 * The Class BFStacks_IJ.
 */
public class BFStacks_IJ implements PlugIn{

		/** The file. */
		File file;
		
		/**
		 * The main method.
		 *
		 * @param args the arguments
		 */
		public static void main(String[] args) {
			new BFStacks_IJ().run("");
			System.out.println("Create BF");
		}
		
		
		
		
		
		
		
		
		
		
		
	//public static void main(String[] args){

	/**
	 * Run.
	 *
	 * @param arg0 the arg0
	 */
	public void run(String arg0) {
		
		//Get file
		String csvFilename =  IJ.getFilePath("Provide the input.CSV");
			if (csvFilename==null) return;	
		file = new File(csvFilename);
		long startTime = System.currentTimeMillis();
    	String[][] CSVContent = null;
		
    	try {
			CSVContent = tools.iSBOps.getCSVContent2(csvFilename);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	//List of BF Images
    	List<String> listBF =tools.iSBOps.getSubFileList(CSVContent, "BF");
    	correctImage(listBF);
    	
		long endTime   = System.currentTimeMillis();
		long totalTime = (endTime - startTime);
		System.out.println("It took "+ totalTime +" ms.");
		//IJ.showMessage("Done");
		java.awt.Toolkit.getDefaultToolkit().beep(); 
	}

	/**
	 * Correct image.
	 *
	 * @param listBF the list bf
	 */
	private void correctImage(List<String> listBF) {
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
		
    	    	
    	if(Selection<=1){
    		//Save BF Stack
    		ImagePlus BFStackImp = tools.ImageOperations.getStack(listBF);
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
    		
    	ImagePlus BFStackImp = tools.ImageOperations.getStack(listBF);
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
		
	}

	/**
	 * Ask back ground.
	 *
	 * @return the int
	 */
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

	/**
	 * Ask offset.
	 *
	 * @return the double
	 */
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

	/**
	 * Ask dark count.
	 *
	 * @return the int
	 */
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

}

