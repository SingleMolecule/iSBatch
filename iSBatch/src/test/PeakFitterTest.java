package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.RoiDecoder;
import ij.io.RoiEncoder;
import ij.plugin.RoiReader;
import ij.plugin.filter.RoiWriter;
import ij.plugin.frame.RoiManager;

public class PeakFitterTest {
	Roi roi;
	static ArrayList<File> files;
	public static void main(String[] args ) {
		File image = new File("D:\\TestFolderIsbatch\\image.TIF");
		File rois = new File("D:\\TestFolderIsbatch\\rois.zip");
		File cells = new File("D:\\TestFolderIsbatch\\cellRois.zip");
		
		
		
//		ArrayList<Roi> myRois = new ArrayList<Roi>();
		//myRois = getRois(rois);
		RoiManager roimanager = new RoiManager(true);
		roimanager.runCommand("Open", rois.getAbsolutePath());

		Hashtable<String, Roi> table = (Hashtable<String, Roi>)roimanager.getROIs();   
		
//		for (String label : table.keySet()) {
//			System.out.println(label);
//			
//		}
		System.out.println(table.size());
		System.out.println("==================================");
		roimanager.close();
		roimanager = new RoiManager(true);
		roimanager.runCommand("Open", cells.getAbsolutePath());
		table = (Hashtable<String, Roi>)roimanager.getROIs();  
//		for (String label : table.keySet()) {
//			System.out.println(label);
//			
//		}
		System.out.println(table.size());
		System.out.println("---------------");
	}
	

		
		
	}
	

