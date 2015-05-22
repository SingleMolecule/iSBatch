package operations.peakFitter;

import java.io.File;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

public class CropSaveImage {
	
	public static void main(String[] args) {
		ImagePlus imp = null;
		File image = new File("D:\\ImageTest\\AVG_514_flat.tif");
		if (image.exists()){
			System.out.println("Load image");
			imp =  IJ.openImage(image.getAbsolutePath());
		}
		
		ImageProcessor ip = imp.getProcessor();
		ip.setRoi(313 - 3, 110 - 3, 7, 7);
		
		ImagePlus imp2 =new ImagePlus();
		imp2.setProcessor(ip.crop());
		IJ.saveAsTiff(imp2, image.getParent() + File.separator + "newImage");
		IJ.saveAs(imp2, "Text Image", image.getParent() + File.separator + "peak");
		System.out.println("----- End --------");
		
	}

}
