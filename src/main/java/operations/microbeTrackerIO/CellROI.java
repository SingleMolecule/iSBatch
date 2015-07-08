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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ij.gui.Roi;

public class CellROI{
	Roi roi;
	ArrayList<Point> points;
	static File temp;
	public CellROI(ArrayList<Point> points){
		this.points = points;

		parseROI();
	}
	private void parseROI() {
		//List of XY coordinates
		writeToCsv(points);
		
	}
	
	private static void writeToCsv(ArrayList<Point> points) {
		 //Delimiter used in CSV file
		    final String COMMA_DELIMITER = ",";
		    final String NEW_LINE_SEPARATOR = "\n";
		    FileWriter fileWriter = null;
		    try {
		    	temp = File.createTempFile("temp-file-name", ".tmp"); 
				fileWriter = new FileWriter(temp);

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
	
	

}
