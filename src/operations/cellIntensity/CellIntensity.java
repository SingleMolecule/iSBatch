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
package operations.cellIntensity;

import gui.FileSelectionDialog;
import gui.LogPanel;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;

import java.io.File;
import java.util.ArrayList;
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
import utils.IJUtils;
import utils.RoiUtils;

public class CellIntensity implements Operation, PlugIn {
	private FileSelectionDialog dialog;
	private ArrayList<Node> filenodes;
	
	public CellIntensity(DatabaseModel treeModel) {
	}

//	private ResultsTable traces = new ResultsTable();
//	private ResultsTable tracesCorrected = new ResultsTable();


	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	@Override
	public String getName() {
		return "Cell Intensity";
	}

	@Override
	public boolean setup(Node node) {
		dialog = new FileSelectionDialog(node);
		if (dialog.isCanceled())
			return false;
		this.filenodes = dialog.getFileNodes();
//		this.channel = dialog.getChannel();
//		this.customSearch = dialog.getCustomSearch();
//		this.tags = dialog.getTags();

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
		IJUtils.emptyAll();
		RoiManager manager = new RoiManager(true);
		ResultsTable table;
		
		int size = filenodes.size();
		
		for (int i = 0; i < size; i++) {
			LogPanel.log("Measuring on file " + i + " of " + size);
			Node currentNode = filenodes.get(i);
			
			File f = new File(currentNode.getOutputFolder()+ File.separator + "CellData");
			f.mkdirs();
			
			// For this node. Load image and RoiManager with Cell Rois
			ImagePlus imp = IJ.openImage(currentNode.getPath());
			manager = new RoiManager(true);
			manager.runCommand("Open", currentNode.getCellROIPath());
			
			IJ.run("Set Measurements...", "mean integrated display redirect=None decimal=3");
			IJ.run(imp, "Select All", "");
			table = manager.multiMeasure(imp);
			
			String path = f.getAbsolutePath() + File.separator
					+ currentNode.getChannel() + currentNode.getName() + ".CellMeasurements.csv";
			table.save(path);
			
			table.reset();
			
			
			
			
			
			//Get localBackground
			
			
			
			
			//Get manager
			//count rois
			RoiManager manager2 = RoiUtils.getRoiBand(imp.getWidth(), imp.getHeight(), manager);
			currentNode.getProperties().put("supportRoi", f.getAbsolutePath()+ File.separator + "supportRoi.zip");
			manager2.runCommand("Save", f.getAbsolutePath()+ File.separator + "supportRoi.zip");
			
			IJ.run("Set Measurements...", "mean integrated display redirect=None decimal=3");
			IJ.run(imp, "Select All", "");
			
			table = manager2.multiMeasure(imp);
			
			path = f.getAbsolutePath() + File.separator
					+ currentNode.getChannel() + currentNode.getName() + ".SupportMeasurements.csv";
			table.save(path);
			
			
			
			IJUtils.emptyAll();
		}
		manager.close();

	}



//	private void calculateCellIntensities(Node currentNode) {
//		FileNode fNode = (FileNode) currentNode;
//		ImagePlus imp = fNode.getImage();
//
//		RoiManager cellsManager = new RoiManager(true);
//		cellsManager.runCommand("Open", currentNode.getCellROIPath());
//
//		ImageStack stack = imp.getStack();
//		int stackSize = stack.getSize(); //
//
//		ResultsTable SUM = new ResultsTable();
//		ResultsTable AVERAGE = new ResultsTable();
//		ResultsTable AREA = new ResultsTable();
//		ResultsTable STDDEV = new ResultsTable();
//		ResultsTable MAX = new ResultsTable();
//		ResultsTable MIN = new ResultsTable();
//		traces.reset();
//	}
	
//	private void run(Node node) {
//
//		String extention = null;
//
//		ArrayList<Node> filenodes = node.getDescendents(new GenericFilter(
//				channel, tags, extention, customSearch));
//		// Generate Averages
//		for (Node currentNode : filenodes) {
//			System.out.println(currentNode.getFieldOfViewName());
//			if (!node.getCellROIPath().isEmpty()) {
//				// Make Output
//				calculateCellIntensities(currentNode);
//			}
//		}
//	}
}
