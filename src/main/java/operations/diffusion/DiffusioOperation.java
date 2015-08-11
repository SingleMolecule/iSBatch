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
package operations.diffusion;

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
import utils.IJUtils;
import utils.MultiFilter;
import utils.StringOperations;
import utils.TreeGenerator;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;

public class DiffusioOperation implements Operation, PlugIn {
	private DiffusionOperationGUI dialog;
	private String channel;
	private ResultsTable table;
	int NUMBER_OF_OPERATIONS;
	int currentCount;
	private String customSearch;
	ArrayList<String> tags = new ArrayList<String>();
	private ArrayList<Node> filenodes;
	private boolean insideCells;
	private Object lookAhead;
	private Object maxStepSize;
	Node currentNode;
	public DiffusioOperation() {
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
		dialog = new DiffusionOperationGUI(node);
		if (dialog.isCanceled())
			return false;
		this.channel = dialog.getChannel();
		if (channel.equalsIgnoreCase(null)) {
			LogPanel.log("No channel selected. Operation cancelled.");
			return false;
		}
		this.lookAhead = dialog.lookAhead;
		this.maxStepSize = dialog.maxStepSize;
		
		this.customSearch = dialog.getCustomSearch();
		this.tags = dialog.getTags();
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
		String extension = null;

		this.filenodes = node.getDescendents(new GenericFilter(channel, tags,
				extension, customSearch));
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

	@SuppressWarnings("static-access")
	@Override
	public void run(String arg0) {
		IJUtils.emptyAll();

		String pluginName = "Peak Fitter";
		String arguments = "";
		int size = filenodes.size();

		for (int i = 0; i < size; i++) {
			currentNode = filenodes.get(i);
			ResultsTable table = Analyzer.getResultsTable();
			table.open2(currentNode.getProperty("PeakTable"));
			File f = new File(currentNode.getOutputFolder() + File.separator);

			IJ.run("Particle Tracker", "slice_to_look_ahead="+ lookAhead + " max_step_size="+maxStepSize+ " show_trajectories");
			table = Analyzer.getResultsTable();
			table.save(f.getAbsolutePath() + "["+channel+"]PeaksAndTracks.csv");	
			

			String path = f.getAbsolutePath()+ File.separator + currentNode.getChannel() + ".FittedPeaks.csv";
			table = Analyzer.getResultsTable();
			currentNode.getProperties().put("fitted peaks", path);
			try {
				table.saveAs(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
//			IJ.run(imp, pluginName, arguments);
			}
			
			
			
			

//			//Check if it is inside cells{
//			
//			if(insideCells){
//				//Load Cell Manager
//				RoiManager cellsManager = new RoiManager(true);
//				cellsManager.runCommand("Open", currentNode.getCellROIPath());
//				//Add property Channel_cellPeaks to the node
//				currentNode.getProperties().put("fitted peaks cells", path);
//				try {
//					System.out.println(path);
//					MultiFilter.getTableRowsInsideCells(currentNode, path,f);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
				
			}
			
//			IJUtils.emptyAll();
			
			
//		}

	
	public static void main(String[] args) {
		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 2);
		DiffusionOperationGUI dialog = new DiffusionOperationGUI(model.getRoot());

		System.out.println("Channel: " + dialog.getChannel());

		System.out.println("Custom search: " + dialog.getCustomSearch());

		for (String string : dialog.getTags()) {
			System.out.println("Tag: " + string);
		}


	}
	
}
