package tools;

import java.awt.Rectangle;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Measurements;
import ij.plugin.ZProjector;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

public class ImageOperations implements PlugInFilter {
	@Override
	public int setup(String arg, ImagePlus imp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run(ImageProcessor ip) {
		// TODO Auto-generated method stub
		
	}
	
public static ImagePlus testDiv(ImagePlus imp, ImagePlus imp2){
		
		
		ImageStack stack = imp.getImageStack();
		int sSize = stack.getSize();
		ImageProcessor ip2 = imp2.getProcessor();
		int M = ip2.getPixelCount();
		
		//Create image to store results
		ImagePlus imp3 = IJ.createImage("", imp.getWidth(), imp.getHeight(), imp.getNSlices(), 16);// 512x512 px
		
		ImageStack stack3 = imp3.getStack();
		
		
		for (int i=1;i<=sSize; i++){
			ImageProcessor ip = stack.getProcessor(i);
			ImageProcessor ip3 = stack3.getProcessor(i);
			
			for (int j = 0; j < M; j++) {
				double value = ip.getf(j);
				double value2 = ip2.getf(j);
				value /=   value2;	// subtract electronic offset
				//System.out.println(j+" of  "+ M +"||"+ value+"||"+ value2+ "||");
				
				ip3.setf(j, (float) value);
			}
		}
		imp.close();
		imp2.close();
		
		return imp3;
	}

public static ImagePlus testDivAddPx(ImagePlus imp, ImagePlus imp2){
	
	
	ImageStack stack = imp.getImageStack();
	int sSize = stack.getSize();
	ImageProcessor ip2 = imp2.getProcessor();
	int M = ip2.getPixelCount();
	
	//Create image to store results
	ImagePlus imp3 = IJ.createImage("", imp.getWidth(), imp.getHeight(), imp.getNSlices(), 16);// 512x512 px
	ImageStack stack3 = imp3.getStack();
	
	for (int i=1;i<=sSize; i++){
		ImageProcessor ip = stack.getProcessor(i);
		ImageProcessor ip3 = stack3.getProcessor(i);
		
		for (int j = 0; j < M; j++) {
			double value = ip.getf(j);
			double value2 = ip2.getf(j);
			value /=   value2;	// subtract electronic offset
			//System.out.println(j+" of  "+ M +"||"+ value+"||"+ value2+ "||");
			
			ip3.setf(j, (float) value);
		}
	}
	imp.close();
	imp2.close();
	
	return imp3;
}

public static ImagePlus divide2Images(ImagePlus imp, ImagePlus imp2){
	ImageProcessor beamProfile = imp2.getProcessor();
			
	//Get Data image
	ImageStack DataStack = imp.getStack();
	
	// create new 32-bit stack to store results 
	ImagePlus flattenedImp = IJ.createImage("", beamProfile.getWidth(), beamProfile.getHeight(), imp.getNSlices(), 16);// 512x512 px
	ImageStack flattenedStack = flattenedImp.getStack();
	
	// The resulting stack should be the same size as the input
	
	int M = beamProfile.getPixelCount();
	int Slices = DataStack.getSize();
	
	
	
	//Loop over the data slices
	for (int slice = 1; slice <= Slices; slice++) {
		ImageProcessor dataip = DataStack.getProcessor(slice);		//get current slice of Data
		ImageProcessor ip2 = flattenedStack.getProcessor(slice);	//get current slice of result image - now empty
		
		for (int i = 0; i < M; i++) {
			double value = dataip.get(i);
			
			value /=   beamProfile.getf(i);	// subtract electronic offset
			ip2.setf(i, (float) value);
		}
	
	}
			
	return flattenedImp;
	
}

public static void BeamProfileCorrection(ImagePlus impInput, ImageProcessor ipDarkCount, ImageProcessor ipBeamProfile) {
	/**
	 * Corrected_Image = (Specimen - Darkfield) / (Brightfield - Darkfield) * 255
	 */
	System.out.println("StartLoop");	
	ImageStack DataStack = impInput.getImageStack();
	
	double MaxOffset = 0;
	
	int M = ipBeamProfile.getPixelCount();
	int Slices = DataStack.getSize();
	
	for (int j = 0; j < M; j++) {
		if (MaxOffset<= ipDarkCount.getf(j)){
			MaxOffset = ipDarkCount.getf(j);
		}
		
	}	
	System.out.println(MaxOffset);
	//Loop over the data slices
	for (int slice = 1; slice <= Slices; slice++) {
		ImageProcessor dataip = DataStack.getProcessor(slice);		//get current slice of Data
		
		
		for (int i = 0; i < M; i++) {
			double value = dataip.get(i);
			
			double value1 =  value -  ipDarkCount.getf(i);
	
			double value2 = value1/ ipBeamProfile.getf(i);
			dataip.setf(i, (float) value2);
			
			
		}
		System.out.println("Finishing Loop");
	
	}
	

}

public static void Stack_Deflicker(ImagePlus imp){
	int Fram=-1;
	/* Adapted from: Stack_Deflicker.java
	 * ImageJ plugin to remove flickering from movies by J. S. Pedersen. 
	 * version 1 2008-12-30 Initial version based on Stack_Normalizer plugin by Joachim Walter
	 *
	 * version 2 2010-09-09 Uses ImageJ functions to measure mean and multiply frames in stead of getPixelValue and putPixel
	 *                      These changes make the plugin about 4 times faster than version 1.
	*/
	/** The Stack_Deflicker calculates the average grey value for each frame and normalizes all frames so that they have same average grey level as a specified frame of the stack
	 * This plugin is very useful to remove flickering in movies caused by frame rates different from the frequency of 50/60Hz AC power used for the light-source that illuminate the scene.
	 * An input value of -1 corresponds to the brightest frame while an input value of zero corresponds to the faintest frame.
	 * If a ROI is selected the average frame intensity will be calculated from this region, but the whole scene will be normalized.
	 */


	ImageStack stack = imp.getStack();
	int size = stack.getSize();
	ImageProcessor ip = imp.getProcessor();
	Rectangle roi = ip.getRoi();

	// Find min and max

	double roiAvg[] = new double[size+1];
	double fMin = Double.MAX_VALUE;
	double fMax = -Double.MAX_VALUE;
	int maxF = 1;
	int minF = 1; 
					
	for (int slice=1; slice<=size; slice++) {
		IJ.showStatus("Calculating: "+slice+"/"+size);
		IJ.showProgress((double)slice/size);
		ip = stack.getProcessor(slice);
	roiAvg[slice]=0;
	
	ip.setRoi(roi);
	ImageStatistics is = ImageStatistics.getStatistics(ip, Measurements.MEAN, imp.getCalibration());
	
	roiAvg[slice]=is.mean; 
	
	if (roiAvg[slice]>fMax) {
	maxF= slice ;
	fMax=roiAvg[slice];
	}
	if (roiAvg[slice]<fMin) {
	minF = slice;
	fMin=roiAvg[slice];
	}
		
	}
	if (Fram<0) Fram=maxF;
	else if (Fram<1) Fram=minF;

	
	for (int slice=1; slice<=size; slice++) {
		IJ.showStatus("Normalizing: "+slice+"/"+size);
		IJ.showProgress((double)slice/size);
		ip = stack.getProcessor(slice);
		ip.multiply(roiAvg[Fram]/roiAvg[slice]);  
	}
	
	
	
	
	
}

public static ImagePlus trimmer(ImagePlus dataImage, int SlicesBegin, int SlicesEnd){
	
	ImageStack myStack = dataImage.getStack();
	
	
	if (myStack.getSize()>=SlicesEnd+SlicesBegin+1){
		
	
	for (int countEnd1 = 0; countEnd1<=SlicesEnd; countEnd1++) {
		myStack.deleteLastSlice();
		countEnd1++;
	for (int countbegin = 0; countbegin<=SlicesBegin+1; countbegin++) {
		myStack.deleteSlice(1);
		countbegin++;
		}
		}
	return dataImage;
	 
	 
	}
	return dataImage;
	
	
	
}

public static ImagePlus getStack(List<String> listBF) {
	 ImagePlus ip = IJ.openImage(listBF.get(1));
	
	
	
	ImagePlus imp2 = IJ.createImage("MTinput", ip.getWidth(), ip.getHeight(), listBF.size()-1, 16);
	ImageStack stack = imp2.getStack();
	
	for (int i=1; i<listBF.size(); i++){
		System.out.println(listBF.get(i));
		ImagePlus imp = IJ.openImage(listBF.get(i));
		ImageProcessor ip2 = imp.getProcessor();
		String ImageName = imp.getShortTitle();
		stack.setProcessor(ip2, i);			
		stack.setSliceLabel(ImageName, i);
						
		
		
		
	}
	return imp2;
	
	
}	

public static ImagePlus getStackProjection(List<String> listBF) {
	 ImagePlus ip = IJ.openImage(listBF.get(1));
	
	
	
	ImagePlus averageImp = IJ.createImage("MTinput", ip.getWidth(), ip.getHeight(), listBF.size(), 16);
	ImageStack stack = averageImp.getStack();
	
	for (int i=1; i<listBF.size(); i++){
		System.out.println(listBF.get(i));
		ImagePlus imp = IJ.openImage(listBF.get(i));
		String ImageName = imp.getShortTitle();
		
		ZProjector projector = new ZProjector(imp);
		projector.setMethod(ZProjector.AVG_METHOD);
		
		projector.doProjection();
		ImagePlus averaged = projector.getProjection();
		
		
		
		stack.setProcessor(averaged.getProcessor(), i+1);
		stack.setSliceLabel(ImageName, i+1);
						
		
		
		
	}
	return averageImp;
	
	
}	

public static ImagePlus doAverage1(ImagePlus imp) {
	

		
		ZProjector projector = new ZProjector(imp);
		projector.setMethod(ZProjector.AVG_METHOD);
		projector.doProjection();
		ImagePlus averaged = projector.getProjection();

	return averaged;
}

public static ImagePlus doAverage(ImagePlus imp) {
	

		
		ZProjector projector = new ZProjector(imp);
		projector.setMethod(ZProjector.AVG_METHOD);
		projector.doProjection();
		ImagePlus averaged = projector.getProjection();

	return averaged;
}

public static void BeamProfileCorrection(ImagePlus impInput, double offset,
		ImagePlus impBackGround) {
	/**
	 * Corrected_Image = (Specimen - Darkfield) / (Brightfield - Darkfield) * 255
	 */
		
	ImageStack DataStack = impInput.getImageStack();
	
	double MaxOffset = 0;
	ImageProcessor ipBackGround = impBackGround.getProcessor();
	int M = ipBackGround.getPixelCount();
	int Slices = DataStack.getSize();
	//Get maximum value for normalization
	for (int j = 0; j < M; j++) {
		if (MaxOffset<= ipBackGround.getf(j)){
			MaxOffset = ipBackGround.getf(j);
		}
		
	}	
	//Loop over the data slices
	for (int slice = 1; slice <= Slices; slice++) {
		ImageProcessor dataip = DataStack.getProcessor(slice);		//get current slice of Data
		
		for (int i = 0; i < M; i++) {
			double value = dataip.get(i);
			double value1 =  value -  offset;
			double norMBG = (ipBackGround.getf(i) - offset)/(MaxOffset-offset);
			double value2 = value1/ norMBG;
			dataip.setf(i, (float) value2);
			
			
		}
	
	}
	

	
}

public static void DivideNormalize(ImagePlus impInput, ImagePlus backgroundimp) {
	/**
	 * Corrected_Image = (Specimen - Darkfield) / (Brightfield - Darkfield) * 255
	 */
		
	ImageStack DataStack = impInput.getImageStack();
	
	double MaxOffset = 0;
	ImageProcessor ipBackGround = backgroundimp.getProcessor();
	int M = ipBackGround.getPixelCount();
	int Slices = DataStack.getSize();
	//Get maximum value for normalization
	for (int j = 0; j < M; j++) {
		if (MaxOffset<= ipBackGround.getf(j)){
			MaxOffset = ipBackGround.getf(j);
		}
		
	}	
	//Loop over the data slices
	for (int slice = 1; slice <= Slices; slice++) {
		ImageProcessor dataip = DataStack.getProcessor(slice);		//get current slice of Data
		
		for (int i = 0; i < M; i++) {
			double norMBG = (ipBackGround.getf(i))/(MaxOffset);
			double value2 = dataip.get(i)/ norMBG;
			dataip.setf(i, (float) value2);
			
			
		}
	
	}
	
	
}

public static void Subtract_Image1_Image2(ImagePlus bFStackImp, ImagePlus DKimp) {
	ImageStack DataStack = bFStackImp.getImageStack();
	
	ImageProcessor ipBackGround = DKimp.getProcessor();
	int M = ipBackGround.getPixelCount();
	int Slices = DataStack.getSize();
	//Get maximum value for normalization

	//Loop over the data slices
	for (int slice = 1; slice <= Slices; slice++) {
		ImageProcessor dataip = DataStack.getProcessor(slice);		//get current slice of Data
		
		for (int i = 0; i < M; i++) {
			double value2 = dataip.get(i)- ipBackGround.getf(i);
			dataip.setf(i, (float) value2);
						
		}
	
	}
	
}

public static void Subtract_Image_double(ImagePlus bFStackImp, double offset) {
	ImageStack DataStack = bFStackImp.getImageStack();
	
	int M = DataStack.getProcessor(1).getPixelCount();
	int Slices = DataStack.getSize();
	//Get maximum value for normalization

	//Loop over the data slices
	for (int slice = 1; slice <= Slices; slice++) {
		ImageProcessor dataip = DataStack.getProcessor(slice);		//get current slice of Data
		
		for (int i = 0; i < M; i++) {
			double value2 = dataip.get(i)- offset;
			dataip.setf(i, (float) value2);
						
		}
	
	}
	
}

public static ImagePlus doAveragelarge(List<String> listchannel) {
	//Save all data in a array and divide
	//Future versions would have a better way to do that 
	//get the last 20% of the last 
	
	//Create the array to store
	ImagePlus imp = IJ.openImage(listchannel.get(1));
	ImageProcessor ip = imp.getProcessor();
	
	int heigth = ip.getHeight();
	int width = ip.getWidth();
	
	int NumberOfSlices = 0;
	//create array to store vaues
	long[] totalSum = new long[heigth * width];
	
	for (int listposition = 1; listposition<listchannel.size(); listposition++){
		//Open Image
		ImagePlus impTemp = new ImagePlus(listchannel.get(listposition));
		ImageProcessor ipStackTemp;
		ImageStack stack =impTemp.getImageStack();	//get Number of Slices
		int totalSlices = stack.getSize();
		int subStackSize = (int) Math.ceil (totalSlices*0.2);
		//Will just calculate the rage
		for (int j=subStackSize; subStackSize<totalSlices;subStackSize++ ){
			ipStackTemp = stack.getProcessor(subStackSize);
			addPixelValuestoArray(impTemp,totalSum,subStackSize,totalSlices);
			NumberOfSlices +=totalSlices;
			NumberOfSlices -=subStackSize;
			
		}
		
	}
	
	//After summing all, divide the background file
	for (int i=0; i<heigth*width;i++){
		totalSum[i] /= NumberOfSlices;
	}
	
	ImagePlus ArtificialBG = convertArraytoImp(totalSum,width, heigth );
	
	return ArtificialBG;
}
public static void addPixelValuestoArray(ImagePlus imp, long[] array){
	ImageStack stack =imp.getStack();
	
	for (int j = 1; j <= stack.getSize(); j++) {
		
		ImageProcessor ip = stack.getProcessor(j);
		
		for (int k = 0; k < ip.getPixelCount(); k++)
			array[k] += ip.get(k);
	}
}

public static void addPixelValuestoArray(ImagePlus imp, long[] array, int start, int end){

	ImageStack stackTemp = imp.getImageStack();

	for (int j = start; j <= end; j++) {
		
		ImageProcessor ip = stackTemp.getProcessor(j);
		
		for (int k = 0; k < ip.getPixelCount(); k++)
			array[k] += ip.get(k);
	}
}

public static ImagePlus convertArraytoImp(long[] array, int width, int heigth){

	ImagePlus imp = IJ.createImage("MTinput", width, heigth, 1, 16);
	ImageProcessor ip = imp.getProcessor();
	
	for (int index = 0; index < array.length ; index++) {
			
		long value = array[index];
		ip.setf(index, (float)value);
	}
	
			
	return imp;
	
}


public static float getMaxPixelValue(ImageProcessor ip){ 
	
	//get boundaries
	int width = ip.getWidth();
	int height = ip.getHeight();
	float max = 0;
	
	//iterate over all coordinates
	for(int u = 0; u < width ; u++){
		for (int v = 0; v<height; v++){
		float  pixel = ip.getPixel(u,v);
			if (pixel>=max) 
				max= pixel;
			
		}
	}

	return max;
}


public static float getMaxPixelValue(ImagePlus imp){ 
	
	ImageStack stack = imp.getImageStack();
	
	//get boundaries
	ImageProcessor ip = imp.getProcessor();
	
	int width = ip.getWidth();
	int height = ip.getHeight();
	float max =0;
	
	
	
	for (int i=1; i<= stack.getSize();i++){
		ImageProcessor ip2 = stack.getProcessor(1);
		//iterate over all coordinates
		for(int u=0; u<width;u++){
			for (int v=0; v<height; v++){
				float  pixel = Float.intBitsToFloat(ip2.getPixel(u,v));
				
				if (pixel >=max) max= pixel;
				
			}
		}
	
				
	}
	return max;
}

public static ImagePlus createArtificialDarkCount(double offset, int width,
		int height, int slices) {

	ImagePlus flatImp = IJ.createImage("Flat Image", width, height, slices, 16);
	ImageProcessor flatIp = flatImp.getChannelProcessor();
	
	for(int u = 0; u < width; u++){
		for(int v = 0; v < height; v++){
			flatIp.putPixelValue(u,v, offset);
		}
	}
	
	
	return flatImp;
}


public static ImagePlus ImageCorrection(ImagePlus imp, ImagePlus impBackground, ImagePlus impDarkCount){
	
	ImageProcessor ipDarkCount = impDarkCount.getProcessor();
	ImageProcessor ipBackground = impBackground.getProcessor();
	
	//Basic information about the input image
	int Slices = imp.getNSlices();
	int width = imp.getWidth();
	int height = imp.getHeight();
	
	//Create Image to store the results - 16bits
		
	double MaxValue = ipBackground.getMax();
	//float MaxValue = getMaxPixelValue(ipBackground);
	
	double v1;//Input Image
	double v2;//DarkCount / Offset
	double v3;//Background
	
	ImageStack DataStack = imp.getImageStack();
		
	for (int slice = 1; slice <= Slices; slice++) {

		ImageProcessor dataip = DataStack.getProcessor(slice);		//
				
		
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				v1 = dataip.getPixelValue(x,y);
				v2 = ipDarkCount.getPixelValue(x, y);
				v3 = ipBackground.getPixelValue(x, y);
				
			
				
				double value = (v1-v2)*MaxValue/(v3-v2);
				if (value >= 65535){
					value = 65535;
				}
				dataip.putPixel(x, y, (int)value);
								
				}
								
			}  
		
		//ResultStack.setProcessor(Outputip, slice);
		
		}

	
	return imp;
}

public static ImagePlus ImageCorrection(ImagePlus imp, ImageProcessor ipBackground, double doubleDarkCount){
	ImagePlus impArtificialDarkCout = createArtificialDarkCount(doubleDarkCount,imp.getWidth(),imp.getHeight(),1);
	ImageProcessor ipArtificialDarkCount = impArtificialDarkCout.getProcessor();
	ImagePlus impResults = ImageCorrection(imp, ipBackground, ipArtificialDarkCount);
	return impResults;
}

public static ImagePlus ImageCorrection(ImagePlus imp, ImagePlus impBackground, double doubleDarkCount){
	ImageProcessor ipBackground = impBackground.getProcessor();
	ImagePlus impArtificialDarkCout = createArtificialDarkCount(doubleDarkCount,imp.getWidth(),imp.getHeight(),1);
	ImageProcessor ipArtificialDarkCount = impArtificialDarkCout.getProcessor();
	ImagePlus impResults = ImageCorrection(imp, ipBackground, ipArtificialDarkCount);
	return impResults;
}


/**
public static void BeamProfileCorrection(ImagePlus impInput, ImagePlus impDarkCount, ImagePlus impBeamProfile) {
	/**
	 * Corrected_Image = (Specimen - Darkfield) / (Brightfield - Darkfield) * 255
	
	System.out.println("I'm here dumb");
	ImageConverter Converter = new ImageConverter(impInput);
	Converter.convertToGray32();
	ImageProcessor ipBeamProfile = impBeamProfile.getProcessor();
	ImageStack DataStack = impInput.getImageStack();
	
	//get Maximun value of the DarkCount
	double MaxOffset=0;
	ImageProcessor ipDarkCount = impDarkCount.getProcessor();
	int ArrayOffset = ipDarkCount.getPixelCount();
	for (int i = 0; i < ArrayOffset; i++) {
		if (MaxOffset <= ipDarkCount.get(i)){
			MaxOffset = ipDarkCount.get(i);
		}
	}
	
	
	int M = ipDarkCount.getPixelCount();
	int Slices = DataStack.getSize();
	
	//Loop over the data slices
	for (int slice = 1; slice <= Slices; slice++) {
		ImageProcessor dataip = DataStack.getProcessor(slice);		//get current slice of Data
		
		
		for (int i = 0; i < M; i++) {
			double value = dataip.get(i);
			
			
			value -=   ipDarkCount.getf(i);
			//System.out.println(value);
			value /= ipBeamProfile.getf(i);
			//System.out.println(value+ ","+ ipBeamProfile.getf(i)+ "--" + (float)value);
			dataip.setf(i, (float) value);
			
		}
	
	}

	//Converter.convertToGray16();
*/








}



