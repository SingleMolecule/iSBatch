package test;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;

import java.io.File;

public class MultiMeasure {
	
	public static void main(String[] args) {
		File image = new File("D:\\ImageTest\\514_flat.TIF");
		File rois = new File("D:\\ImageTest\\rois.zip");
		File cells = new File("D:\\ImageTest\\cellRoi.zip");
		
		//open image
		
		ImagePlus imp = IJ.openImage(image.getAbsolutePath());
		
		RoiManager manager = new RoiManager(true);
		manager.runCommand("Open", cells.getAbsolutePath());
		IJ.run(imp, "Select All", "");
		ResultsTable rt = new ResultsTable();
		rt = manager.multiMeasure(imp);
		
		rt.save("D:\\ImageTest\\ResultsTableR.csv");
		
				
		
		
		System.out.println("Finish");
		
		
		
		
	}

}
