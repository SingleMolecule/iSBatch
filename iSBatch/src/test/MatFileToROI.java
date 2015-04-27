package test;

import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.io.RoiEncoder;
import ij.plugin.frame.RoiManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;

import operations.microbeTrackerIO.MatlabMeshes;
import operations.microbeTrackerIO.Mesh;
import operations.microbeTrackerIO.Point;
import operations.microbeTrackerIO.ROIBuilder;
import operations.microbeTrackerIO.Segment;

public class MatFileToROI {
	
	public static void main(String[] args) throws IOException {
		
		File mat = new File("D:\\TestFolderIsbatch\\DnaQ.mat");
		ArrayList<Mesh> meshes = MatlabMeshes.getMeshes(mat.getAbsolutePath());
		RoiManager manager = new RoiManager(true);
		MatFileReader reader = new MatFileReader(mat);
		
		Map<String, MLArray> content = reader.getContent();
		MLCell cellList = (MLCell)content.get("cellList");
//		for (Mesh m : meshes){
//			
//				System.out.println("Slice: " + m.getSlice());// Slice of the cell
//				System.out.println(m.getCell());// Number of the cell in the slice
////				System.out.println(m.getSegments());
//					
//			
//		}
		System.out.println("Total cells: "+ meshes.size());
		Mesh m = meshes.get(0);
		System.out.println("Slice: "+ m.getSlice());
		System.out.println("Cell: "+ m.getCell());	
		System.out.println("Area: " + m.getArea());
		ArrayList<Point> points = m.getOutline();

		int maxX = 0;
		int maxY = 0;
		
		ArrayList<Integer> tempx = new ArrayList<Integer>();
		ArrayList<Integer> tempy = new ArrayList<Integer>();
		
		for(Point point : points){
//			System.out.println("X: " + point.x + "|" + point.y);; 
			if (point.x >= maxX){
				maxX = (int)Math.round(point.x);
			}
			
			if (point.y >= maxY){
				maxY = (int)Math.round(point.y);
			}
			
			tempx.add((int) point.x);
			tempy.add((int) point.y);
			
			
		}
		int[]x = new int[tempx.size()];
		tempx.toArray();
		
		int[]y = new int[tempy.size()];
		tempy.toArray();
		
		
		System.out.println(maxX + " " + maxY);
		
		System.out.print(points.size());
//		ROIBuilder roi = new ROIBuilder(points);
		ArrayList<Roi> rois = new ArrayList<Roi>();
		
		@SuppressWarnings("deprecation")
		Roi roi  = new PolygonRoi(x, y, points.size(), null, Roi.FREEROI);
		if (roi.getLength() / x.length > 10)
			roi = new PolygonRoi(x, y, points.size(), null, Roi.POLYGON); 
		
		
		rois.add(roi);
		


		saveRoisAsZip(rois, "D:\\TestFolderIsbatch\\TesteRoiSaver.zip");
		
		
		
		
		
		
		
		
//		manager.runCommand("saveAs(\"Selection\", \"D:\\TestFolderIsbatch\\TesteRoiSaver.roi");
//			System.out.println("saveAs(\"Selection\", \"D:\\TestFolderIsbatch\\TesteRoiSaver.roi\"");

		System.out.println("Done");
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
	
}
