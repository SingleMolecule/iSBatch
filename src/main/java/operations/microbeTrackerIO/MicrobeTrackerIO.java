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

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import filters.GenericFilter;
import gui.LogPanel;

import java.util.HashMap;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import model.parameters.Channel;
import model.parameters.NodeType;
import operations.Operation;
import test.TreeGenerator;
import utils.EnumUtils;
import utils.ImageStackUtils;
import utils.StringUtils;

public class MicrobeTrackerIO implements Operation {
	private MicrobeTrackerIOGui dialog;
	private String channel;
	private String customFilter;
	private String BFFIleInputPath;
	private Object imageType;
	private ArrayList<String> imageTag;
	private boolean isTimeLapse = false;
	private StringUtils strUtils = new StringUtils(false);

	public MicrobeTrackerIO(DatabaseModel treeModel) {

	}

	public String[] getContext() {
		// Set context to all unless Root.
		return new String[] { NodeType.EXPERIMENT.toString(),
				NodeType.SAMPLE.toString(), NodeType.FOV.toString(),
				NodeType.FILE.toString() };
	}

	@Override
	public String getName() {
		return "MicrobeTracker I/O";
	}

	@Override
	public boolean setup(Node node) {
		dialog = new MicrobeTrackerIOGui(node);
		if (dialog.isCanceled())
			return false;

		// Get information from the dialog
		// From panel1
		this.channel = dialog.getChannel();
		if (channel.equalsIgnoreCase(null)) {
			LogPanel.log("No channel selected. Operation cancelled.");
			return false;
		}
		this.imageType = dialog.getImageType();
		this.customFilter = dialog.getCustomFilter();
		this.imageTag = dialog.getImageTag();
		// From panel 2
		this.BFFIleInputPath = dialog.BFFIleInputPath;
		return true;
	}

	public void finalize(Node node) {
		System.out.println("--- End ----");

		System.out.println("MicrobeTracker IO Operation finalized");
	}

	public void visit(Root root) {
	}

	public void visit(Experiment experiment) {
		setExperimentType(experiment);
		run(experiment);
	}

	private void setExperimentType(Experiment experiment) {
		if (experiment.getProperty("type") == "Time Lapse")
			this.isTimeLapse = true;
	}

	private void run(Node node) {

		File matFile = new File(dialog.getMatFilePath());
		File BFmt = new File(dialog.BFFIleInputPath);

		if (matFile.exists() && BFmt.exists()) {
			if (isTimeLapse) {
				importFiles(node, matFile);
			} else {
				importFilesTL(node, matFile);
			}

		} else {
			getStackForMT(node);
		}

	}

	private void importFilesTL(Node node, File matFile) {
		ImagePlus referenceImp = IJ.openImage(BFFIleInputPath);
		ImageStack referenceStack = referenceImp.getStack();

		ArrayList<FieldOfView> nodes = node.getFieldOfView();

		FieldOfView currentFov = null;

		ArrayList<String> uniqueFoVNames = ImageStackUtils
				.getUniqueFOVNames(referenceImp);

		try {
			ArrayList<Mesh> meshes = MatlabMeshes.getMeshes(matFile);
			for (Node node1 : nodes) {
				RoiManager currentManager = new RoiManager(true);
				System.out.println("FoV name " + node1.getName());
				currentFov = (FieldOfView) node1;

				for (int stackPosition = 1; stackPosition <= referenceStack
						.getSize(); stackPosition++) {

					if (referenceStack.getShortSliceLabel(stackPosition)
							.startsWith(node1.getName())) {

						for (Mesh m : meshes) {

							int meshStackPosition = m.getSlice();
							// referenceImp.setSlice(stackPosition);

							if (meshStackPosition == stackPosition) {
								Roi roi = getRoi(m);
								roi.setPosition(strUtils
										.getCurrentStackfromAssigment(referenceStack
												.getShortSliceLabel(stackPosition)));
								currentManager.addRoi(roi);
							}
						}

					}

				}

				System.out.println(currentFov.getOutputFolder()
						+ File.separator + "cellRoi.zip");
				currentFov.setCellROIPath(currentFov.getOutputFolder()
						+ File.separator + "cellRoi.zip");
				currentFov.getProperties().put("CellRoi",
						currentFov.getCellROIPath());

				if (currentManager.getCount() != 0) {
					String currentPath =currentFov.getOutputFolder() + File.separator
							+ "cellRoi.zip";
					currentManager.runCommand("Save",
							currentFov.getOutputFolder() + File.separator
									+ "cellRoi.zip");
					
					Lineage lineage = new Lineage(currentPath, referenceImp);
					lineage.assingn();
					node.getProperties().put("LineageFolder", lineage.OUTPUT_FOLDER.getAbsolutePath());
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void importFiles(Node node, File matFile) {
		// Get the MicrobeTracker Reference Image
		ImagePlus referenceImp = IJ.openImage(BFFIleInputPath);
		ArrayList<FieldOfView> nodes = node.getFieldOfView();

		// By the way that it acts, get the parent is the folder to Save the
		// ROI. The list of nodes
		// contain imagePaths based on the filter tag. Sure this has to be
		// improved later, but provides enough control now
		// This is a temporary solution.
		FieldOfView currentFov = null;
		try {
			ArrayList<Mesh> meshes = MatlabMeshes.getMeshes(matFile);

			for (int i = 1; i <= referenceImp.getStackSize(); i++) {
				// get the matching FOV
				for (Node node1 : nodes) {
					if (node1.getName().equalsIgnoreCase(
							referenceImp.getStack().getSliceLabel(i))) {
						currentFov = (FieldOfView) node1;
						continue;
					}
				}

				RoiManager currentManager = new RoiManager(true);

				for (Mesh m : meshes) {
					int stackPosition = m.getSlice();
					referenceImp.setSlice(stackPosition); // Set slice in the
															// stack

					if (i == stackPosition) {
						Roi roi = getRoi(m);
						currentManager.addRoi(roi);
					}
				}
				// Save all Rois in that folder
				System.out.println(referenceImp.getStack().getSliceLabel(i));
				System.out.println(currentFov.getName());
				System.out.println(currentFov.getOutputFolder()
						+ File.separator + "cellRoi.zip");
				currentFov.setCellROIPath(currentFov.getOutputFolder()
						+ File.separator + "cellRoi.zip");
				currentFov.getProperties().put("CellRoi",
						currentFov.getCellROIPath());
				currentManager.runCommand("Save", currentFov.getOutputFolder()
						+ File.separator + "cellRoi.zip");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void getStackForMT(Node node) {

		// Get list of nodes to be the input.

		ArrayList<Node> filenodes = node.getDescendents(new GenericFilter(
				channel, imageTag, null, null));

		MicrobeTrackerInputStack MTImputStack = new MicrobeTrackerInputStack(
				filenodes, isTimeLapse, "MTInput");
		ImagePlus imp = MTImputStack.getImagePlus();

		// Convert image to 15 bit
		// IJ.run(imp, "Enhance Contrast", "saturated=0.35");
		// IJ.run(imp, "16-bit", "");

		System.out.println(node.getOutputFolder() + File.separator
				+ imp.getTitle());
		IJ.saveAsTiff(imp,
				node.getOutputFolder() + File.separator + imp.getTitle());
		imp.close();

		// Now, finally get this list of files and create a combined

	}

	@Override
	public void visit(Sample sample) {
		run(sample);
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		run(fieldOfView);
	}

	@Override
	public void visit(FileNode fileNode) {
		run(fileNode);
	}

	public static void main(String[] args) {

		/**
		 * 
		 * Testing the funtions
		 */
		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 4);
		MicrobeTrackerIOGui dialog = new MicrobeTrackerIOGui(model.getRoot());
		System.out.println(dialog.getChannel());

		for (String string : dialog.getImageTag()) {
			System.out.println("Tag: " + string);
		}
		System.out.println(dialog.getImageType());
		System.out.println(dialog.getCustomFilter());

		// From panel 2
		System.out.println("MatFile: " + dialog.getMatFilePath());
		System.out.println("BFFile: " + dialog.BFFIleInputPath);

		File f = new File(dialog.getMatFilePath());
		File f2 = new File(dialog.BFFIleInputPath);

		if (f.exists() && f2.exists()) {
			System.out.println("Going to import");
		} else {
			System.out.println("Going to create input");
		}

		if (EnumUtils.contains(Channel.values(), dialog.getChannel())) {
			System.out.println("Contain channel " + dialog.getChannel());
		} else {
			System.out.println("Contain no Channel. Abort");
		}

		if (EnumUtils.contains(Channel.values(), dialog.getImageType())) {
			System.out.println("Contain type " + dialog.getImageType());
		} else {
			System.out.println("Contain no type. Set to Raw");
		}

		if (!f.exists() || !f2.exists()) {
			System.out.println("Missing one of the files. Will abort");
		}

		System.out.println(dialog.getCustomFilter());

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

	@SuppressWarnings("deprecation")
	private static Roi getRoi(Mesh m) {
		ArrayList<Point> points = m.getOutline();

		int height = points.size();
		int[] x = new int[height];
		int[] y = new int[height];

		for (int i = 0; i < points.size(); i++) {
			x[i] = (int) Math.round(points.get(i).x);
			y[i] = (int) Math.round(points.get(i).y);
		}

		Roi roi = new PolygonRoi(x, y, height, null, Roi.FREEROI);
		if (roi.getLength() / x.length > 10)
			roi = new PolygonRoi(x, y, height, null, Roi.POLYGON); // use
																	// "handles"

		return roi;
	}

}
