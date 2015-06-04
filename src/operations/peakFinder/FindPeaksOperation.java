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
package operations.peakFinder;

import filters.GenericFilter;
import gui.FileSelectionDialog;
import gui.LogPanel;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.plugin.frame.Recorder;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import analysis.PeakFinder;
import operations.Operation;
import test.TreeGenerator;
import utils.FileNames;
import utils.IJUtils;
import utils.StringOperations;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;

public class FindPeaksOperation implements Operation, PlugIn {
	private FileSelectionDialog dialog;
	// private DatabaseModel model;
	PeakFinder peakFinder;
	RoiManager roiManager;
	int NUMBER_OF_OPERATIONS;
	int currentCount;
	String operationName = "Peak Finder";
	private ArrayList<Node> filenodes;

	public FindPeaksOperation() {
		// this.model = treeModel;
	}

	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	@Override
	public String getName() {
		return operationName;
	}

	@Override
	public boolean setup(Node node) {
		dialog = new FileSelectionDialog(node, operationName);
		if (dialog.isCanceled())
			return false;
		return true;
	}

	@Override
	public void finalize(Node node) {
		LogPanel.log("-------------Peak Finder finalized---------------");
	}

	@Override
	public void visit(Root root) {
		System.out.println("Not applicable to root. ");
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

	private void runNode(Node node) throws IOException {
		String extention = null;

		this.filenodes =node.getDescendents(new GenericFilter(dialog.getChannel(), dialog
				.getTags(), extention, dialog.getCustomSearch()));
		run(null);
		// }
	}

	public static void main(String[] args) {
		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 2);
		FileSelectionDialog dialog = new FileSelectionDialog(model.getRoot());
		System.out.println(dialog.getChannel());
		System.out.println("Use cells :" + dialog.useCells);
		for (String string : dialog.getTags()) {
			System.out.println(string);
		}
		System.out.println(dialog.getCustomSearch());
		System.out.println("--------");
	}

	@Override
	public void visit(OperationNode operationNode) {
	}

	@Override
	public Node[] getCreatedNodes() {
		return null;
	}

	@Override
	public HashMap<String, String> getParameters() {
		return null;
	}

	public static ArrayList<Roi> findPeaks(PeakFinder finder, ImagePlus imp) {

		ArrayList<Roi> allPeaks = new ArrayList<Roi>();
		ImageStack stack = imp.getImageStack();

		for (int slice = 1; slice <= stack.getSize(); slice++) {
			ImageProcessor ip = stack.getProcessor(slice);

			for (Point p : finder.findPeaks(ip)) {
				PointRoi roi = new PointRoi(p.x, p.y);
				roi.setPosition(slice);
				allPeaks.add(roi);
			}
		}

		return allPeaks;
	}

	@Override
	public void run(String arg0) {
		IJUtils.emptyAll();

		/**
		 * Plugin : Peak Fitter
		 */
		
		// Create output directory
		

		String pluginName = "Peak Finder";
		String arguments = "";
		int size = filenodes.size();
		
		for (int i = 0; i < size; i++) {
			Node currentNode = filenodes.get(i);
			
			ImagePlus imp = IJ.openImage(currentNode.getPath());
			File f = new File(currentNode.getOutputFolder()+ File.separator + "PeakFinder");
			f.mkdirs();
			if (i == 0) {
				
				imp.show();
				Recorder recorder = new Recorder(false);
				Recorder.record = true;
				Recorder.recordInMacros = true;
				IJ.run(imp, pluginName, arguments);
				IJ.runPlugIn(imp, pluginName, arguments);
				LogPanel.log(arguments);
				String command = recorder.getText();
				recorder.close();
				arguments = StringOperations.getArguments(pluginName, command);
			} else {
				IJ.run(imp, pluginName, arguments);
			}

			String nameToSave = FileNames.getOutputFilename(currentNode,
					"PeakROI", ".zip");

			// peak finder does not return a table
			// Just returns a ROI Manager

			String path = f.getAbsolutePath() + File.separator
					+ currentNode.getChannel() + ".FittedPeaks.csv";
			
			RoiManager currentManager = RoiManager.getInstance2();
			currentManager.runCommand("Save", f.getAbsolutePath()
					+ File.separator + nameToSave);
			
			
			// Check if it is inside cells{

//			if (dialog.useCells) {
//				RoiManager cellsManager = new RoiManager(true);
//				cellsManager.runCommand("Open", currentNode.getCellROIPath());
//				// Add property Channel_cellPeaks to the node
//				try {
//					System.out.println(path);
//					MultiFilter.getTableRowsInsideCells(currentNode, path);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
			currentManager.removeAll();
			currentManager.close();
			IJUtils.emptyAll();
			

		}

	}

}
