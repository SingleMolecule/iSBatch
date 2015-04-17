package cellPeakPositions;

import java.io.IOException;
import java.util.ArrayList;

import ij.IJ;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;

public class Main implements PlugIn {

	//private String matFilename;
	//private String tableFilename;
	private double maxDistance = 12;
	
	@Override
	public void run(String arg0) {
		String matFilename =  IJ.getFilePath("Load meshes file, BF.mat");
		if (matFilename==null) return;
		String tableFilename =  IJ.getFilePath("Load meshes table with peaks");
		if (tableFilename==null) return;
		
		try {
			
				
			ArrayList<Mesh> meshes = MatlabMeshes.getMeshes(matFilename);
			

			
			
			ResultsTable table = ResultsTable.open(tableFilename);
			ResultsTable table2 = new ResultsTable();
			
			for (int row = 0; row < table.getCounter(); row++) {
				
				int slice = (int)table.getValue("BFslice", row);
				double x = table.getValue("x", row);
				double y = table.getValue("y", row);
				
				//System.out.println(slice +","+ x + ","+ y);
				if (Double.isNaN(x) || Double.isNaN(y))
					continue;

				// find closest mesh
				Point p = new Point(x, y);
				Mesh closestMesh = null;
				double minDistance = maxDistance;
				
				for (Mesh m: meshes) {
					
					if (m.getSlice() == slice) {
						
						double distance = m.distanceTo(p);
						
						if (distance < minDistance) {
							
							minDistance = distance;
							closestMesh = m;
							
						}
						
					}
					
				}
				
				if (closestMesh != null) {
					
					Point projection = closestMesh.projectionOf(p);
					
					table2.incrementCounter();
					table2.addValue("BFSlice", slice);
					table2.addValue("x", x);
					table2.addValue("y", y);
					table2.addValue("L", projection.x);
					table2.addValue("D", projection.y);
					table2.addValue("L_normalized", projection.x / closestMesh.getTotalLength());
					table2.addValue("cell", (int)closestMesh.getCell());
					table2.addValue("length", closestMesh.getTotalLength());
					table2.addValue("area", closestMesh.getArea());
					table2.addValue("volume", closestMesh.getVolume());
					
					
				}
				
			}
			
			table2.saveAs(tableFilename + ".result.txt");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new Main().run("");
		System.out.println("All peaks localized");
	}
}
