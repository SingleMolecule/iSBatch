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
		LogPanel.log("There are " + WindowManager.getWindowCount() + " windows open.");
		String pluginName = "Particle Tracker";
		String arguments = "";
		int[] IDS = WindowManager.getIDList();


		for(String string : WindowManager.getImageTitles()){
			LogPanel.log(string);
		}
		
		for(String string :WindowManager.getNonImageTitles()){
			LogPanel.log(string);
		}
		

		IJ.open("D:\\ImageTest\\Results.csv");
		IJ.run(pluginName, arguments);
		ResultsTable table = Analyzer.getResultsTable();
		
		table.save("D:\\ImageTest\\ResultsTrack.csv");
		
		

	}


}
