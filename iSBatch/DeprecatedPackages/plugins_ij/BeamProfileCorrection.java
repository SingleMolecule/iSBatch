package plugins_ij;

import java.io.IOException;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class BeamProfileCorrection implements PlugIn{
	
	ImagePlus impBackground;
	ImagePlus impDarkCount;
	ImageProcessor ip;
	

public static void main(String[] args) throws IOException {
		
		new BeamProfileCorrection().run("");
		
		
	}
	public void run(String arg0) {
		ImagePlus imp2 = new ImagePlus("D:\\UmuC-DnaX\\set2\\TestArea\\DnaX-YPet-UmuC-mKate2_dinB-_001_Acquisition2.tif");
		ImagePlus imp = new ImagePlus("D:\\UmuC-DnaX\\set2\\TestArea\\Corrected_Stack[BF].tif");
		impBackground = new ImagePlus("D:\\UmuC-DnaX\\set2\\TestArea\\Acquisition_568_BeamProfile.tif");
		impDarkCount =  new ImagePlus("D:\\UmuC-DnaX\\set2\\TestArea\\OffSet[568].tif");
		
		ImageStack stack = imp2.getStack();
		int slices = stack.getSize();

		System.out.println("Stack size: " + stack.getSize());
		
		
		System.out.println(imp2.getNSlices() + "|" + impBackground.getNSlices() + "|" + impDarkCount.getNSlices());
		
		
		
		
		ImageProcessor ip = stack.getProcessor(1);
		ImageProcessor ip2 = stack.getProcessor(2);
		
		//System.out.println("Current Name: " + stack.getShortSliceLabel(2));
		
		System.out.println("Pixel count : " + ip.getPixelCount());
		int MaxValue = 0;
		
		for (int x=0; x<2; x++) {
			for (int y=0; y<2; y++) {
				System.out.println(ip.getPixel(x, y) + " | " + ip2.getPixel(x, y));
				if(ip.getPixel(x, y)>= MaxValue){
					MaxValue = ip.getPixel(x, y);
				}
			}
			
			
			}
		
		System.out.println("Max : " + MaxValue);

		
		
		
				
	}
	
	

}
