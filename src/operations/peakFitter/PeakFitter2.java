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
package operations.peakFitter;

import filters.GenericFilter;
import gui.LogPanel;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.Recorder;
import ij.plugin.frame.RoiManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import operations.Operation;
import test.TreeGenerator;
import utils.IJUtils;
import utils.MultiFilter;
import utils.StringOperations;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;

public class PeakFitter2 implements Operation, PlugIn {
	private FitPeaksOperationGUI dialog;
	private String channel;
	private ResultsTable table;
	int NUMBER_OF_OPERATIONS;
	int currentCount;
	private String customSearch;
	ArrayList<String> tags = new ArrayList<String>();
	private ArrayList<Node> filenodes;
	private boolean insideCells;
	public PeakFitter2() {
	}

	public static final double SIGMA_TO_FWHM = 2.0 * Math.sqrt(2.0 * Math
			.log(2));

	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	@Override
	public String getName() {
		return "Peak Fitter";
	}

	@Override
	public boolean setup(Node node) {
		dialog = new FitPeaksOperationGUI(node);
		if (dialog.isCanceled())
			return false;
		this.channel = dialog.getChannel();
		if (channel.equalsIgnoreCase(null)) {
			LogPanel.log("No channel selected. Operation cancelled.");
			return false;
		}
		this.customSearch = dialog.getCustomSearch();
		this.tags = dialog.getTags();
		this.insideCells = dialog.useCells();
		return true;
	}

	@Override
	public void finalize(Node node) {
		LogPanel.log("-------------Peak Fitter finalized---------------");
	}

	@Override
	public void visit(Root root) {
		System.out.println("Not applicable to root. ");
	}

	private void runNode(Node node) throws IOException {
		String extention = null;

		this.filenodes = node.getDescendents(new GenericFilter(channel, tags,
				extention, customSearch));
		run(null);
		// }
	}

	@Override
	public void visit(Experiment experiment) {
		try {
			runNode(experiment);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(Sample sample) {
		try {
			runNode(sample);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		try {
			runNode(fieldOfView);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(FileNode fileNode) {
		try {
			runNode(fileNode);
		} catch (IOException e) {
			e.printStackTrace();
		}

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
	public void visit(OperationNode operationNode) {

	}

	@Override
	public void run(String arg0) {
		IJUtils.emptyAll();

		/**
		 * Plugin : Peak Fitter
		 * 
		 */

		String pluginName = "Peak Fitter";
		String arguments = "";
		int size = filenodes.size();

		for (int i = 0; i < size; i++) {
			Node currentNode = filenodes.get(i);
			ImagePlus imp = IJ.openImage(currentNode.getPath());
			File f = new File(currentNode.getOutputFolder()+ File.separator + "PeakFitter");
			f.mkdirs();
			
			if (i == 0) {
				imp.show();
				Recorder recorder = new Recorder(false);
				Recorder.record = true;
				Recorder.recordInMacros = true;
				IJ.run(imp, pluginName, arguments);
				String command = recorder.getText();
				recorder.close();
				arguments = StringOperations.getArguments(pluginName, command);
			}
			else{
				IJ.run(imp, pluginName, arguments);
			}
			
			
			
			String path = f.getAbsolutePath()+ File.separator + currentNode.getChannel() + ".FittedPeaks.csv";
			table = Analyzer.getResultsTable();
			try {
				table.saveAs(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			

			//Check if it is inside cells{
			
			if(insideCells){
				//Load Cell Manager
				RoiManager cellsManager = new RoiManager(true);
				cellsManager.runCommand("Open", currentNode.getCellROIPath());
				//Add property Channel_cellPeaks to the node
				try {
					System.out.println(path);
					MultiFilter.getTableRowsInsideCells(currentNode, path,f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
			
			IJUtils.emptyAll();
			
			
		}

	}
	
	public static void main(String[] args) {
		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 2);
		FitPeaksOperationGUI dialog = new FitPeaksOperationGUI(model.getRoot());

		System.out.println("Channel: " + dialog.getChannel());

		System.out.println("Custom search: " + dialog.getCustomSearch());

		for (String string : dialog.getTags()) {
			System.out.println("Tag: " + string);
		}

		System.out.println("Discoidal: " + dialog.useDiscoidal);
		System.out.println("Cells: " + dialog.useCells);

	}
	
}
