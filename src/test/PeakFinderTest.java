/*
 * 
 */
package test;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import analysis.PeakFinder;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.io.RoiEncoder;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

// TODO: Auto-generated Javadoc
/**
 * The Class PeakFinderTest.
 */
public class PeakFinderTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		File image = new File("D:\\TestFolderIsbatch\\image.TIF");
		
		if(image.exists()){
			System.out.println("S");
		}
		ImagePlus imp = IJ.openImage(image.getAbsolutePath());
		
		process.DiscoidalAveragingFilter filter1 = new process.DiscoidalAveragingFilter(imp.getWidth(), 1, 3);
	
		
		//PeakFinder peakFinder = new PeakFinder(true, filter, 0, PeakFinderThreshold, 3);
		PeakFinder finder = new PeakFinder(false, filter1, 6, 0, 3);
		ArrayList<Roi> rois = findPeaks(finder, imp);
		
		
		try {
			saveRoisAsZip(rois, image.getParent() + File.separator + "rois.zip");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	/**
	 * Run plug in filter.
	 *
	 * @param filter the filter
	 * @param imp the imp
	 */
	public static void runPlugInFilter(PlugInFilter filter, ImagePlus imp) {
		
		ImageStack stack = imp.getImageStack();
		
		for (int slice = 1; slice <= stack.getSize(); slice++)
			runPlugInFilter(filter, stack.getProcessor(slice));
	}
	
	/**
	 * Run plug in filter.
	 *
	 * @param filter the filter
	 * @param ip the ip
	 */
	public static void runPlugInFilter(PlugInFilter filter, ImageProcessor ip) {
		filter.run(ip);
	}
	
	/**
	 * Find peaks.
	 *
	 * @param finder the finder
	 * @param imp the imp
	 * @return the array list
	 */
	public static ArrayList<Roi> findPeaks(PeakFinder finder, ImagePlus imp) {
		
		ArrayList<Roi> allPeaks = new ArrayList<Roi>();
		ImageStack stack = imp.getImageStack();
		
		for (int slice = 1; slice <= stack.getSize(); slice++) {
			ImageProcessor ip = stack.getProcessor(slice);
			
			for (Point p: finder.findPeaks(ip)) {
				PointRoi roi = new PointRoi(p.x, p.y);
				roi.setPosition(slice);
				allPeaks.add(roi);
			}
			
		}
		
		return allPeaks;
	}
	
	/**
	 * Save rois as zip.
	 *
	 * @param rois the rois
	 * @param filename the filename
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void saveRoisAsZip(ArrayList<Roi> rois, String filename) throws IOException {
		File file = new File(filename);
		if(file.exists()){
			file.delete();
		}
		
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(filename));
		
		int i = 0;
		
		for (Roi roi: rois) {
			byte[] b = RoiEncoder.saveAsByteArray(roi);
			zos.putNextEntry(new ZipEntry(i + ".roi"));
			zos.write(b, 0, b.length);
			i++;
		}
		
		zos.close();
		
	}
	
	
}
