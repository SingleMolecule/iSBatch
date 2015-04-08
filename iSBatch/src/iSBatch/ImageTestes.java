package iSBatch;

import java.io.File;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class ImageTestes {
	public static void main(String[] args) {
		File file = new File("D:\\ExampleDB\\AVG_BF.Tiff");
		System.out.println(file.getAbsolutePath());
		
		ImagePlus imp = new ImagePlus("D:\\ExampleDB\\AVG_BF.Tif");
		ImageStack stack = imp.getStack();
		ImageProcessor ip = imp.getProcessor();
		
		
		System.out.println(ip.getMax());
	
	}
}
