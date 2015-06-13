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
import operations.microbeTrackerIO.Point2;

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
		System.out.println("Does not apply to root.JrebelS");
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

		// WindowManager.
		// IJ.renameResults("green");
		ResultsTable green = ResultsTable.getResultsTable();
		//
		IJ.open("D:\\ImageTest\\red.csv");
		IJ.run("Results Sorter", "column=slice group=[no grouping] ascending");
		// IJ.renameResults("red");
		ResultsTable red = Analyzer.getResultsTable();

		// for (String string : WindowManager.getImageTitles()) {
		// LogPanel.log(string);
		// }
		//
		// for (String string : WindowManager.getNonImageTitles()) {
		// LogPanel.log(string);
		// }

		LogPanel.log(green.getCounter());
		for (String string : green.getHeadings()) {
			LogPanel.log(string);
		}
		;

		// for(int i = 0; i<green.getCounter(); i++){

		for (int i = 0; i < green.getCounter(); i++) {
			LogPanel.log(i);
			LogPanel.log(green.getValue("x", i));

			double x = green.getValue("x", i);
			double y = green.getValue("y", i);
			double slice = green.getValue("slice", i);
			Point2 p = new Point2(x, y);

			LogPanel.log(x);

			// get Closest peak
			Point2 closest = getClosestPoint(p, getSubTable(red, "slice", slice));
			
			if(p.distanceTo(closest)<=minDistance){
				green.setValue("distance", i, p.distanceTo(closest));
				green.setValue("x2", i, closest.x);
				green.setValue("y2", i, closest.y);
				green.setValue("distance2", i, p.distanceTo(closest));
			}
			else{
				green.setValue("distance", i, -1);
				green.setValue("x2", i, closest.x);
				green.setValue("y2", i, closest.y);
				green.setValue("distance2", i, p.distanceTo(closest));
			}
		}

		green.save("D:\\ImageTest\\greenColoS.csv");
		
		LogPanel.log("Done");

	}

	private Point2 getClosestPoint(Point2 p,
			ResultsTable currentComparableTable) {
		
		Point2 p3 = new Point2(0, 0);
		double distance = 0;
		for (int j = 0; j < currentComparableTable.getCounter(); j++) {
				double dx = currentComparableTable.getValue("x", j);
				double dy = currentComparableTable.getValue("y", j);
				Point2 p2 = new Point2(dx, dy);
				LogPanel.log("Point "+ dx + " | " + dy);
				
				//calculate distance
				double currentDistance = p.distanceTo(p2);
				LogPanel.log(currentDistance);

				if(j==0){
					distance = currentDistance;
					p3 = p2;
				}
				else if (currentDistance<distance){
					distance = currentDistance;
					p3 = p2;
				}
			}
		return p3;
		}
		

	/**
	 * This method returns a ResultsTable containing only the rows with the
	 * selected filter.
	 *
	 * @param table
	 *            ResultsTable to be filtered
	 * @param colName
	 *            Column name to get the value from
	 * @param value
	 *            Condition to match for filtering.
	 * @return A new ResultsTable
	 */
	public static ResultsTable getSubTable(ResultsTable table, String colName,
			double value) {
		ResultsTable results = new ResultsTable();
		// results.incrementCounter();
		LogPanel.log(table.getCounter());

		for (int i = 0; i < table.getCounter(); i++) {

			if (table.getValue(colName, i) == value) {
				// add to the new table.
				results.incrementCounter();
				for (String string : table.getHeadings()) {
					double currentValue = table.getValue(string, i);
					results.addValue(string, currentValue);
				}
				// results.incrementCounter();
			}
		}
		return results;
	}

}
