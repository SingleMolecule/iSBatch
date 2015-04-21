package test;

import java.awt.Point;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import process.DiscoidalAveragingFilter;
import analysis.PeakFinder;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.io.RoiEncoder;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class TestPlugInFilter {
	
	public static double gaussian(double x, double y, double A, double s) {
		return A * Math.exp(-(x * x + y * y) / (2 * s * s));
	}
	
	public static void drawGaussian(ImageProcessor ip, int x0, int y0, double A, double s) {
		
		for (int y1 = -50; y1 <= 50; y1++) {
			for (int x1 = -50; x1 <= 50; x1++)
				ip.putPixelValue(x0 + x1, y0 + y1, ip.getPixelValue(x0 + x1, y0 + y1) + gaussian(x1, y1, A, s));
		}
		
	}
	
	public static void drawRandomGaussians(ImagePlus imp, int n, double A, double s) {
		
		ImageStack stack = imp.getImageStack();
		
		for (int slice = 1; slice <= stack.getSize(); slice++) {
			
			ImageProcessor ip = stack.getProcessor(slice);
			
			for (int i = 0; i < n; i++) {
				
				double A1 = Math.random() * A;
				double s1 = Math.random() * s;
				int x = (int)(Math.random() * ip.getWidth());
				int y = (int)(Math.random() * ip.getHeight());
				
				drawGaussian(ip, x, y, A1, s1);
			}
			
		}
		
	}
	
	public static void runPlugInFilter(PlugInFilter filter, ImagePlus imp) {
		
		ImageStack stack = imp.getImageStack();
		
		for (int slice = 1; slice <= stack.getSize(); slice++)
			runPlugInFilter(filter, stack.getProcessor(slice));
	}
	
	public static void runPlugInFilter(PlugInFilter filter, ImageProcessor ip) {
		filter.run(ip);
	}
	
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
	
	public static void saveRoisAsZip(ArrayList<Roi> rois, String filename) throws IOException {
		
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
	
	public static void main(String[] args) {
		
		ImagePlus imp = IJ.createImage("test image", 512, 512, 10, 16);
		drawRandomGaussians(imp, 100, 1000, 4);
		
		DiscoidalAveragingFilter filter = new DiscoidalAveragingFilter(imp.getWidth(), 1, 3);
		PeakFinder finder = new PeakFinder(true, filter, 3, 0, 8);
		ArrayList<Roi> rois = findPeaks(finder, imp);
		
		try {
			saveRoisAsZip(rois, "C:\\Users\\p262597\\Desktop\\rois.zip");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		IJ.saveAsTiff(imp, "C:\\Users\\p262597\\Desktop\\image.tif");
		
	}
}
