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
import ij.measure.Measurements;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import imageOperations.NodeToImageStack;

import java.awt.Rectangle;
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

public class MicrobeTrackerIO implements Operation {
	private MicrobeTrackerIOGui dialog;
	private String channel;
	private String customFilter;
	private String BFFIleInputPath;
	private Object imageType;
	private ArrayList<String> imageTag;
	
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

	/**
	 * Setup.
	 *
	 * @param node
	 *            the node
	 * @return true, if successful
	 */
	@Override
	public boolean setup(Node node) {
		dialog = new MicrobeTrackerIOGui(node);
		if (dialog.isCanceled())
			return false;

		// Get information from the dialog
		// From panel1
		this.channel = dialog.getChannel();
		if(channel.equalsIgnoreCase(null)){
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
		System.out.println("MicrobeTracker IO Operation finalized");
	}

	public void visit(Root root) {
	}

	public void visit(Experiment experiment) {
		System.out.println(experiment.getProperty("type"));
		run(experiment);
	}

	private void run(Node node) {

		File matFile = new File(dialog.getMatFilePath());
		File BFmt = new File(dialog.BFFIleInputPath);

		if (matFile.exists() && BFmt.exists()) {
			importFiles(node, matFile);
		} else {
			getStackForMT(node);
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
						roi.setPosition(stackPosition);
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
		System.out.println("--- Start ----");
		
//		ArrayList<Node> nodes = node.getDescendents(filter(channel));
		ArrayList<Node> filenodes = node.getDescendents(new GenericFilter(
				channel, imageTag, null, null));
		// Create BF File
		
		NodeToImageStack temp = new NodeToImageStack(filenodes, channel, "MTinput");
		ImagePlus imp = temp.getImagePlus();
		
		Stack_Deflicker(imp);
		System.out.println("Filters to use");
		System.out.println("Channel: " + channel);
		System.out.println("Type: " + imageType);
		System.out.println("Custom filter" + customFilter);

		// save Image
		System.out.println(node.getOutputFolder() + File.separator
				+ imp.getTitle());
		IJ.saveAsTiff(imp,
				node.getOutputFolder() + File.separator + imp.getTitle());
		imp.close();
		// Now, finally get this list of files and create a combined

		System.out.println("--- End ----");

	}

//	private int getStackSize(ArrayList<Mesh> meshes) {
//		int size = 0;
//		for (Mesh mesh : meshes) {
//			if (mesh.getSlice() >= size) {
//				size = mesh.getCell();
//			}
//		}
//		return size;
//	}

	/**
	 * Visit.
	 *
	 * @param sample
	 *            the sample
	 */
	@Override
	public void visit(Sample sample) {
		run(sample);
	}

	/**
	 * Visit.
	 *
	 * @param fieldOfView
	 *            the field of view
	 */
	@Override
	public void visit(FieldOfView fieldOfView) {
		run(fieldOfView);
	}

	/**
	 * Visit.
	 *
	 * @param fileNode
	 *            the file node
	 */
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

		for(String string : dialog.getImageTag()){
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

	/**
	 * Visit.
	 *
	 * @param operationNode
	 *            the operation node
	 */
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

	public static void Stack_Deflicker(ImagePlus imp) {
		int Fram = -1;
		/*
		 * Adapted from: Stack_Deflicker.java ImageJ plugin to remove flickering
		 * from movies by J. S. Pedersen. version 1 2008-12-30 Initial version
		 * based on Stack_Normalizer plugin by Joachim Walter
		 * 
		 * version 2 2010-09-09 Uses ImageJ functions to measure mean and
		 * multiply frames in stead of getPixelValue and putPixel These changes
		 * make the plugin about 4 times faster than version 1.
		 */
		/**
		 * The Stack_Deflicker calculates the average grey value for each frame
		 * and normalizes all frames so that they have same average grey level
		 * as a specified frame of the stack This plugin is very useful to remove
		 * flickering in movies caused by frame rates different from the
		 * frequency of 50/60Hz AC power used for the light-source that
		 * illuminate the scene. An input value of -1 corresponds to the
		 * brightest frame while an input value of zero corresponds to the
		 * faintest frame. If a ROI is selected the average frame intensity will
		 * be calculated from this region, but the whole scene will be
		 * normalized.
		 */

		ImageStack stack = imp.getStack();
		int size = stack.getSize();
		ImageProcessor ip = imp.getProcessor();
		Rectangle roi = ip.getRoi();

		// Find min and max

		double roiAvg[] = new double[size + 1];
		double fMin = Double.MAX_VALUE;
		double fMax = -Double.MAX_VALUE;
		int maxF = 1;
		int minF = 1;

		for (int slice = 1; slice <= size; slice++) {
			IJ.showStatus("Calculating: " + slice + "/" + size);
			IJ.showProgress((double) slice / size);
			ip = stack.getProcessor(slice);
			roiAvg[slice] = 0;

			ip.setRoi(roi);
			ImageStatistics is = ImageStatistics.getStatistics(ip,
					Measurements.MEAN, imp.getCalibration());

			roiAvg[slice] = is.mean;

			if (roiAvg[slice] > fMax) {
				maxF = slice;
				fMax = roiAvg[slice];
			}
			if (roiAvg[slice] < fMin) {
				minF = slice;
				fMin = roiAvg[slice];
			}

		}
		if (Fram < 0)
			Fram = maxF;
		else if (Fram < 1)
			Fram = minF;

		for (int slice = 1; slice <= size; slice++) {
			IJ.showStatus("Normalizing: " + slice + "/" + size);
			IJ.showProgress((double) slice / size);
			ip = stack.getProcessor(slice);
			ip.multiply(roiAvg[Fram] / roiAvg[slice]);
		}
	}
}