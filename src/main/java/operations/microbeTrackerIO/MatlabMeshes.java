/************************************************************************
 * 				iSBatch  Copyright (C) 2015  							*
 *		Victor E. A. Caldas -  v.e.a.caldas at rug.nl					*
 *		C. Michiel Punter - c.m.punter at rug.nl						*
 *																		*
 *	This program is distributed in the hope that it will be useful,		*
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of		*
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the		*
 *	GNU General Public License for more details.						*
 *	You should have received a copy of the GNU General Public License	*
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************/
package operations.microbeTrackerIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLEmptyArray;
import com.jmatio.types.MLNumericArray;
import com.jmatio.types.MLStructure;

/**
 * The Class MatlabMeshes.
 */
public class MatlabMeshes {
	
	/**
	 * Gets the meshes.
	 *
	 * @param file the file
	 * @return the meshes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ArrayList<Mesh> getMeshes(File file) throws IOException{
		return getMeshes(file.getAbsolutePath());
	}
	
	/**
	 * Gets the meshes.
	 *
	 * @param filename the filename
	 * @return the meshes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ArrayList<Mesh> getMeshes(String filename) throws IOException {

		ArrayList<Mesh> meshes = new ArrayList<Mesh>();
		
		MatFileReader reader = new MatFileReader(filename);
		
		Map<String, MLArray> content = reader.getContent();
		MLCell cellList = (MLCell)content.get("cellList");
		if( cellList != null ){
			
		// each slice has a cell list
		for (int slice = 0; slice < cellList.getSize(); slice++) {
			System.out.println(slice);
			//check size of cellList
			//Empty causes errors
		if (cellList.get(slice) instanceof MLEmptyArray == false){
//			System.out.println(cellList.get(slice).getClass().toString());
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
					ArrayList<Point> outline = getPoints(struct);
					
					Mesh cellMesh = new Mesh(slice + 1, cell + 1, area, volume, outline);
					
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
			}
		}}
		
		return meshes;
	}

	/**
	 * Gets the points.
	 *
	 * @param struct the struct
	 * @return the points
	 */
	private static ArrayList<Point> getPoints(MLStructure struct) {
		ArrayList<Point> points = new ArrayList<Point>();
		
		@SuppressWarnings("unchecked")
		MLNumericArray<Double> mesh = (MLNumericArray<Double>)struct.getField("model");
		int m = mesh.getM();
		
		for (int k = 0; k < m; k++) {
			
			double x0 = mesh.get(k, 0).doubleValue();
			double y0 = mesh.get(k, 1).doubleValue();
			
			points.add(new Point(x0, y0));
		}
		
		
		
		return points;
	}
	
}
