/*
 * 
 */
package test;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.io.RoiEncoder;
import ij.plugin.frame.RoiManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.jmatio.io.MatFileReader;

import operations.microbeTrackerIO.MatlabMeshes;
import operations.microbeTrackerIO.Mesh;
import operations.microbeTrackerIO.Point;

// TODO: Auto-generated Javadoc
/**
 * The Class MatFileToROI.
 */
public class MatFileToROI {
	
	/** The manager. */
	static RoiManager manager;
	
	/** The rois. */
	static ArrayList<Roi> rois = new ArrayList<Roi>();
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		
		File mat = new File("D:\\TestFolderIsbatch\\DnaQ.mat");
		ImagePlus imp = IJ.openImage("D:\\TestFolderIsbatch\\BFTeste.tif");
		
		ArrayList<Mesh> meshes = MatlabMeshes.getMeshes(mat);
		manager = new RoiManager(true);
		MatFileReader reader = new MatFileReader(mat);
		
//		Map<String, MLArray> content = reader.getContent();

		//Create a place to store the list
		ArrayList<ArrayList<Roi>> placeHolder= new ArrayList<ArrayList<Roi>>();
		for (int i=1; i<=imp.getStackSize(); i++){
			RoiManager currentManager = new RoiManager(true);
			System.out.println(i);

			for (Mesh m : meshes){
				int stackPosition = m.getSlice();
				
				
				
				
				imp.setSlice(stackPosition); // Set slice in the stack
				
				if(i==stackPosition){
					Roi roi = getRoi(m);
					roi.setPosition(stackPosition);
					currentManager.addRoi(roi);	
				}
				
			}
			
			currentManager.runCommand("Save", "D:\\TestFolderIsbatch\\TesteRoiSaver_"+ i +".zip");
		
		}
//		for (Mesh m : meshes){
//			System.out.println(m.getSlice());
//			int stackPosition = m.getSlice();
//			imp.setSlice(stackPosition); // Set slice in the stack
//			Roi roi = getRoi(m);
//			roi.setPosition(stackPosition);
//			manager.addRoi(roi);
//			
//			placeHolder.get(stackPosition).add(roi);
//			
//			
//			
//			
//		}
//		
		
		System.out.println(placeHolder.size());

//		saveRoisAsZip(rois, "D:\\TestFolderIsbatch\\TesteRoiSaver.zip");
//		
//		
//		File roiz = new File("D:\\TestFolderIsbatch\\TesteRoiSaver2.zip");
//		if (roiz.exists()){
//			roiz.delete();
//		}
//		
//		manager.runCommand("Save", "D:\\TestFolderIsbatch\\TesteRoiSaver2.zip");
////			System.out.println("saveAs(\"Selection\", \"D:\\TestFolderIsbatch\\TesteRoiSaver.roi\"");
//
//		System.out.println("Done");
	}

	/**
	 * Gets the roi.
	 *
	 * @param m the m
	 * @return the roi
	 */
	private static Roi getRoi(Mesh m) {
		ArrayList<Point> points = m.getOutline();
		
		int height = points.size();
		int[] x = new int[height];
		int[] y = new int[height];
		
		for (int i=0; i<points.size(); i++) {
			x[i] = (int)Math.round(points.get(i).x);
			y[i] = (int)Math.round(points.get(i).y);
		}
		
		Roi roi = new PolygonRoi(x, y, height, null, Roi.FREEROI);
		if (roi.getLength()/x.length>10)
			roi = new PolygonRoi(x, y, height, null, Roi.POLYGON); // use "handles"
		
		return roi;
	}

	/**
	 * Write to csv.
	 *
	 * @param points the points
	 */
	private static void writeToCsv(ArrayList<Point> points) {
		 //Delimiter used in CSV file
		    final String COMMA_DELIMITER = ",";
		    final String NEW_LINE_SEPARATOR = "\n";
		    FileWriter fileWriter = null;
		    try {
				fileWriter = new FileWriter("D:\\TestFolderIsbatch\\tempROI.csv");

				//Write the CSV file header
//				fileWriter.append(FILE_HEADER.toString());
				
				//Add a new line separator after the header
//				fileWriter.append(NEW_LINE_SEPARATOR);
				
				//Write a new student object list to the CSV file
				for (Point point : points) {
					fileWriter.append(String.valueOf(point.x));
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(point.y));
					fileWriter.append(NEW_LINE_SEPARATOR);
				}

				
				
				System.out.println("CSV file was created successfully !!!");
				
			} catch (Exception e) {
				System.out.println("Error in CsvFileWriter !!!");
				e.printStackTrace();
			} finally {
				
				try {
					fileWriter.flush();
					fileWriter.close();
				} catch (IOException e) {
					System.out.println("Error while flushing/closing fileWriter !!!");
	                e.printStackTrace();
				}
				
			}
		    

		
	}

	/**
	 * Save rois as zip.
	 *
	 * @param rois the rois
	 * @param filename the filename
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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
	
//	class Run_Macro implements PlugIn {
//	    @Override
//	    public void run(final String arg) {
//	        final String macro =
//	            "run(\"Clown (14K)\");\n" +
//	            "run(\"Make Binary\");\n";
//	        IJ.runMacro(macro);
//	    }
//	
//}
}
