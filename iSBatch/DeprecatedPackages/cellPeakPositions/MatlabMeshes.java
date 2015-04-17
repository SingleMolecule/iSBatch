package cellPeakPositions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLNumericArray;
import com.jmatio.types.MLStructure;

public class MatlabMeshes {
	
	public static ArrayList<Mesh> getMeshes(String filename) throws IOException {

		ArrayList<Mesh> meshes = new ArrayList<Mesh>();
		
		MatFileReader reader = new MatFileReader(filename);
		
		Map<String, MLArray> content = reader.getContent();
		MLCell cellList = (MLCell)content.get("cellList");
		if( cellList != null){
			
		
		// each slice has a cell list
		for (int slice = 0; slice < cellList.getSize(); slice++) {
			
			MLCell cells = (MLCell)cellList.get(slice);
			
			for (int cell = 0; cell < cells.getSize(); cell++) {
				

				// http://intra.csb.ethz.ch/javadoc/metabolic/com/jmatio/types/MLArray.html
				MLArray a = cells.get(cell);

				if (a.getType() == 2) {
				
					MLStructure struct = (MLStructure)a;
					@SuppressWarnings("unchecked")
					MLNumericArray<Double> mesh = (MLNumericArray<Double>)struct.getField("mesh");
					
					//double length = ((MLDouble)struct.getField("length")).get(0);
					double area = ((MLDouble)struct.getField("area")).get(0);
					double volume = ((MLDouble)struct.getField("volume")).get(0);
					
					Mesh cellMesh = new Mesh(slice + 1, cell + 1, area, volume);
					
					int m = mesh.getM();	// number of rows
					ArrayList<Line> lines = new ArrayList<Line>();
					
					for (int k = 0; k < m; k++) {
						
						double x0 = mesh.get(k, 0).doubleValue();
						double y0 = mesh.get(k, 1).doubleValue();
						double x1 = mesh.get(k, 2).doubleValue();
						double y1 = mesh.get(k, 3).doubleValue();
						
						lines.add(new Line(new Point(x0, y0), new Point(x1, y1)));
					}
					
					for (int k = 0; k < lines.size() - 1; k++)
						cellMesh.addSegment(new Segment(lines.get(k), lines.get(k + 1)));
					
					meshes.add(cellMesh);
				}
				
			}
			
		}}
		
		return meshes;
	}
	
}
