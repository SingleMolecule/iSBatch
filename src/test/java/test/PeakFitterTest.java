///*
// * 
// */
//package test;
//
//import java.io.File;
//
//import java.util.ArrayList;
//
//import java.util.Hashtable;
//
//
//
//
//
//
//import ij.gui.Roi;
//
//
//
//
//import ij.plugin.frame.RoiManager;
//
///**
// * The Class PeakFitterTest.
// */
//public class PeakFitterTest {
//	
//	/** The roi. */
//	Roi roi;
//	
//	/** The files. */
//	static ArrayList<File> files;
//	
//	/**
//	 * The main method.
//	 *
//	 * @param args the arguments
//	 */
//	public static void main(String[] args ) {
////		File image = new File("D:\\TestFolderIsbatch\\image.TIF");
//		File rois = new File("D:\\TestFolderIsbatch\\rois.zip");
//		File cells = new File("D:\\TestFolderIsbatch\\cellRois.zip");
//		
//		
//		
////		ArrayList<Roi> myRois = new ArrayList<Roi>();
//		//myRois = getRois(rois);
//		RoiManager roimanager = new RoiManager(true);
//		roimanager.runCommand("Open", rois.getAbsolutePath());
//
////		Hashtable<String, Roi> table = roimanager.getROIs();   
//		
////		for (String label : table.keySet()) {
////			System.out.println(label);
////			
////		}
//		System.out.println(table.size());
//		System.out.println("==================================");
//		roimanager.close();
//		roimanager = new RoiManager(true);
//		roimanager.runCommand("Open", cells.getAbsolutePath());
////		table = roimanager.getROIs();  
////		for (String label : table.keySet()) {
////			System.out.println(label);
////			
////		}
//		System.out.println(table.size());
//		System.out.println("---------------");
//	}
//	
//
//		
//		
//	}
//	
//
