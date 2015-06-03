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
package iSBatch;

import gui.LogPanel;
import ij.IJ;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;

import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operations.Operation;

public class DebugProperties implements Operation, PlugIn {

	public DebugProperties(DatabaseModel treeModel) {
	}

	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	@Override
	public String getName() {
		return "Debug";
	}

	@Override
	public boolean setup(Node node) {
		return true;
	}

	@Override
	public void finalize(Node node) {
	}

	@Override
	public Node[] getCreatedNodes() {
		return null;
	}

	@Override
	public HashMap<String, String> getParameters() {
		return null;
	}

	@Override
	public void visit(Root root) {
		System.out.println("Does not apply to root");
	}

	@Override
	public void visit(Experiment experiment) {
		runNode(experiment);
	}

	@Override
	public void visit(Sample sample) {
		runNode(sample);
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		runNode(fieldOfView);
	}

	@Override
	public void visit(FileNode fileNode) {
		runNode(fileNode);
	}

	private void runNode(Node node) {
		run(null);
	}

	@Override
	public void visit(OperationNode operationNode) {
	}

	@Override
	public void run(String arg0) {
		LogPanel.log("There are " + WindowManager.getWindowCount()
				+ " windows open.");
		String pluginName = "Particle Tracker";
		String arguments = "";
		double minDistance = 2.0;
		int[] IDS = WindowManager.getIDList();

		IJ.open("D:\\ImageTest\\green.csv");
		IJ.run("Results Sorter", "column=slice group=[no grouping] ascending");
		
//		WindowManager.
//		IJ.renameResults("green");
		ResultsTable green = ResultsTable.getResultsTable();
//
		IJ.open("D:\\ImageTest\\red.csv");
		IJ.run("Results Sorter", "column=slice group=[no grouping] ascending");
//		IJ.renameResults("red");
		ResultsTable red = Analyzer.getResultsTable();

//		for (String string : WindowManager.getImageTitles()) {
//			LogPanel.log(string);
//		}
//
//		for (String string : WindowManager.getNonImageTitles()) {
//			LogPanel.log(string);
//		}
		
		
		for (int i = 1; i <= green.getCounter(); i++) {
			double x = green.getValue("x", i);
			double y = green.getValue("y", i);
			double slice = green.getValue("slice", i);
			operations.microbeTrackerIO.Point p = new operations.microbeTrackerIO.Point(
					x, y);

			
			LogPanel.log(x);
//			ResultsTable peaks = getSubTable(red, "slice", slice);
//			peaks.save("D:\\ImageTest\\green_" + slice + ".csv");
//			for (int j = 0; j < red.getCounter(); j++) {
//				double dslice = red.getValue("slice", j);
//				
//				if (dslice == slice) {
//					double dx = red.getValue("x", j);
//					double dy = red.getValue("y", j);
//					operations.microbeTrackerIO.Point p2 = new operations.microbeTrackerIO.Point(
//							x, y);
//
//					double distance = p.distanceTo(p2);
//					if (distance <= minDistance) {
//						green.setValue("distance", i, distance);
//						green.setValue("x2", i, dx);
//						green.setValue("y2", i, dy);
//					} else {
//
//						green.setValue("distance", i, -1);
//						green.setValue("x2", i, 0);
//						green.setValue("y2", i, 0);
//					}
//				}
//			}
		}

		green.save("D:\\ImageTest\\greenColoS.csv");

	}

	/**
	 * This method returns a ResultsTable containing only the rows with the selected filter.
	 *
	 * @param table ResultsTable to be filtered
	 * @param colName Column name to get the value from
	 * @param value Condition to match for filtering. 
	 * @return A new ResultsTable
	 */
//	public static ResultsTable getSubTable(ResultsTable table, String colName, double value){
//		ResultsTable results = new ResultsTable();
//		results.incrementCounter();
//		LogPanel.log(table.getCounter());
//		for (int i=0; i<=table.getCounter(); i++){
//			if(table.getValue(colName, i)==value){
//				//add to the new table.
//				//
//				for(String string : table.getHeadings()){
//					double currentValue = table.getValue(string, i);
//					results.addValue(string, currentValue);
//				}
//			results.incrementCounter();
//			}
//		}
//		return results;
//	}
	
}
