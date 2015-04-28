/**
 * 
 */
package operations.microbeTrackerIO;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.ZProjector;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import imageOperations.NodeToImageStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import filters.NodeFilterInterface;

import java.util.HashMap;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operations.Operation;

/**
 * @author VictorCaldas
 *
 */
public class MicrobeTrackerIO implements Operation {
	MicrobeTrackerIOGui dialog;
	private RoiManager manager;
	private String channel;
	private String method;
	private String customFilter;
	private String matFilePath;
	private String BFFIleInputPath;
	private Object imageType;

	public MicrobeTrackerIO(DatabaseModel treeModel) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see context.ContextElement#getContext()
	 */
	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#getName()
	 */
	@Override
	public String getName() {
		return "MicrobeTracker I/O";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#setup(model.Node)
	 */
	@Override
	public boolean setup(Node node) {
		dialog = new MicrobeTrackerIOGui(node);
		if (dialog.isCanceled())
			return false;
		this.channel = dialog.getChannel();
		this.method = dialog.getMethod();
		this.customFilter = dialog.getCustomFilter();
		this.matFilePath = dialog.getMatFilePath();
		this.imageType = dialog.getImageType();
		this.BFFIleInputPath = dialog.BFFIleInputPath;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#finalize(model.Node)
	 */
	@Override
	public void finalize(Node node) {
		System.out.println("Operation finalized");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#visit(model.Root)
	 */

	@Override
	public void visit(Root root) {

	}

	@Override
	public void visit(Experiment experiment) {
		System.out.println(experiment.getProperty("type"));
		run(experiment);

	}

	private void run(Node node) {
		File matFile = new File(matFilePath);

		if (!matFile.exists()) {
			getStackForMT(node);
		} else {
			importFiles(node, matFile);
		}

	}

	private void importFiles(Node node, File matFile) {
		// Get the MicrobeTracker Reference Image
		ImagePlus referenceImp = IJ.openImage(BFFIleInputPath);
		manager = new RoiManager(true);
		ArrayList<FieldOfView> nodes = node.getFieldOfView();

		

		// By the way that it acts, get the parent is the folder to Save the
		// ROI. The list of nodes
		// contain imagePaths based on the filter tag. Sure this has to be
		// improved later, but provides enough control now
		// TODO: Improve the search for file and provide bug free record keep.
		// This is a temporary solution.
		FieldOfView currentFov = null;
		try {
			ArrayList<Mesh> meshes = MatlabMeshes.getMeshes(matFile);

			for (int i = 1; i <= referenceImp.getStackSize(); i++) {
				// get the matching FOV
				for(Node node1 : nodes){
					if(node1.getName().equalsIgnoreCase(referenceImp.getStack().getSliceLabel(i))){
						currentFov = (FieldOfView)node1;
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
				node.setCellROIPath(currentFov.getOutputFolder() + File.separator
						+ "cellRoi.zip");
				node.getProperties().put("CellRoi", node.getCellROIPath());
				currentManager.runCommand("Save", currentFov.getOutputFolder()
						+ File.separator + "cellRoi.zip");

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ImagePlus getReference(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	private void getStackForMT(Node node) {
		System.out.println("--- Start ----");
		ArrayList<Node> nodes = node.getDescendents(filter(channel));

		// Create BF File
		NodeToImageStack temp = new NodeToImageStack(nodes, channel, "MTinput");
		ImagePlus imp = temp.getImagePlus();
		System.out.println("Filters to use");
		System.out.println("Channel: " + channel);
		System.out.println("Type: " + imageType);
		System.out.println("Custom filter" + customFilter);

		// save Image
		System.out.println(node.getOutputFolder() + File.separator
				+ imp.getTitle());
		IJ.saveAsTiff(imp,
				node.getOutputFolder() + File.separator + imp.getTitle());

		// Now, finally get this list of files and create a combined

		System.out.println("--- End ----");

	}

	private int getStackSize(ArrayList<Mesh> meshes) {
		int size = 0;
		for (Mesh mesh : meshes) {
			if (mesh.getSlice() >= size) {
				size = mesh.getCell();
			}
		}
		return size;
	}

	private NodeFilterInterface filter(String channel) {
		final String selectedChannel = channel;
		String[] channels = { "Acquisition", "Bright Field", "Red", "Green",
				"Blue", };
		NodeFilterInterface imageFileNodeFilter = null;
		// Create Filters
		if (channel == null || channel.equals("")
				|| channel.equalsIgnoreCase("All")) {
			imageFileNodeFilter = new NodeFilterInterface() {

				@Override
				public boolean accept(Node node) {

					String path = node.getProperty("path");

					// check if this file is an image
					if (path == null
							|| !(path.toLowerCase().endsWith(".tiff") || path
									.toLowerCase().endsWith(".tif")))
						return false;

					// Get custom string and remove spaces in the begin and end.
					// Not in
					// the middle.

					return true;
				};
			};
		} else if (Arrays.asList(channels).contains(channel)) {
			imageFileNodeFilter = new NodeFilterInterface() {

				public boolean accept(Node node) {
					String ch = null;
					// try{
					ch = node.getChannel();
					if (ch == null || !ch.equalsIgnoreCase(selectedChannel))
						return false;

					String path = node.getPath();

					// check if this file is an image
					if (path == null
							|| !(path.toLowerCase().endsWith(".tiff") || path
									.toLowerCase().endsWith(".tif")))
						return false;

					return true;
				};
			};

		}

		return imageFileNodeFilter;

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

	}

	@Override
	public void visit(OperationNode operationNode) {
		// TODO Auto-generated method stub

	}

	private String getChannel() {

		return channel;
	}

	@Override
	public Node[] getCreatedNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

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
// Test
