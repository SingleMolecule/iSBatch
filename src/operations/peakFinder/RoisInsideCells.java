package operations.peakFinder;

import iSBatch.iSBatchPreferences;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.io.RoiEncoder;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import process.DiscoidalAveragingFilter;
import analysis.PeakFinder;

public class RoisInsideCells {

	public static void main(String[] args) {
		
		ImagePlus  imp = IJ.openImage("D:\\ImageTest\\514_flat.tif");
		System.out.println("Detect within cells");
		
		
		 PeakFinder peakFinder = new PeakFinder(true, new DiscoidalAveragingFilter(
				imp.getWidth(), "1",
				"3"),
				6,
				0,
				8);

		ArrayList<Roi> rois = findPeaks(peakFinder, imp);

		System.out.println("Saving peak Rois @ " + "D:\\ImageTest\\detected.zip");
		
		try {
			System.out.println("Detected " + rois.size() + " raw peaks.");
			saveListOfRoisAsZip(rois, "D:\\ImageTest\\detected.zip");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		RoiManager allPeaksManager = new RoiManager(true);
		allPeaksManager.runCommand("Open", "D:\\ImageTest\\detected.zip");
		
		
		RoiManager cellsManager = new RoiManager(true);
		cellsManager.runCommand("Open", "D:\\ImageTest\\cellRoi.zip");
		
		RoiManager peaksInsideCells = getPeaksInsideCells(allPeaksManager,
				cellsManager);

		System.out.println(peaksInsideCells.getCount());

		peaksInsideCells.runCommand("Save", "D:\\ImageTest\\cellRoiPeaks.zip");
		
		
	}
	public static ArrayList<Roi> findPeaks(PeakFinder finder, ImagePlus imp) {

		ArrayList<Roi> detectedPeaks = new ArrayList<Roi>();
		ImageStack stack = imp.getImageStack();

		for (int slice = 1; slice <= stack.getSize(); slice++) {
			ImageProcessor ip = stack.getProcessor(slice);

			for (Point p : finder.findPeaks(ip)) {
				PointRoi roi = new PointRoi(p.x, p.y);
				roi.setPosition(slice);
				detectedPeaks.add(roi);
			}

		}

		return detectedPeaks;
	}
	
	public static void saveListOfRoisAsZip(ArrayList<Roi> rois, String filename)
			throws IOException {
		ZipOutputStream zos = new ZipOutputStream(
				new FileOutputStream(filename));

		int i = 0;

		for (Roi roi : rois) {
			byte[] b = RoiEncoder.saveAsByteArray(roi);
			zos.putNextEntry(new ZipEntry(i + ".roi"));
			zos.write(b, 0, b.length);
			i++;
		}

		zos.close();
	}
	
	private static RoiManager getPeaksInsideCells(RoiManager allPeaksManager,
			RoiManager cellsManager) {
		
		RoiManager peaksInsideCells = new RoiManager(true);
		
		long startTime = System.nanoTime();
		
		@SuppressWarnings("unchecked")
		Hashtable<String, Roi> listOfCells = (Hashtable<String, Roi>) cellsManager
				.getROIs();
		@SuppressWarnings("unchecked")
		Hashtable<String, Roi> listOfPeaks = (Hashtable<String, Roi>) allPeaksManager
				.getROIs();
		long endTime = System.nanoTime();

		long duration = (endTime - startTime); 
		
		System.out.println("Hash " + duration);
		startTime = System.nanoTime();
		
		for (String label : listOfCells.keySet()) {
			ShapeRoi currentCell = new ShapeRoi(listOfCells.get(label));

			for (String peakLabel : listOfPeaks.keySet()) {
				Roi currentPeak = listOfPeaks.get(peakLabel);

				if (currentCell.contains(currentPeak.getBounds().x,
						currentPeak.getBounds().y)) {
					peaksInsideCells.addRoi(currentPeak);
					continue;
				}
			}

		}
		endTime = System.nanoTime();

		duration = (endTime - startTime); 
		
		System.out.println("Loop " + duration);
		return peaksInsideCells;
	}
}
