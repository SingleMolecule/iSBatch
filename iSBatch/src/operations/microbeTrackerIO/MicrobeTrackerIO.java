/**
 * 
 */
package operations.microbeTrackerIO;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.measure.Measurements;
import ij.plugin.ZProjector;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import imageOperations.NodeToImageStack;

import java.awt.Rectangle;
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
	public static void Stack_Deflicker(ImagePlus imp){
		int Fram=-1;
		/* Adapted from: Stack_Deflicker.java
		 * ImageJ plugin to remove flickering from movies by J. S. Pedersen. 
		 * version 1 2008-12-30 Initial version based on Stack_Normalizer plugin by Joachim Walter
		 *
		 * version 2 2010-09-09 Uses ImageJ functions to measure mean and multiply frames in stead of getPixelValue and putPixel
		 *                      These changes make the plugin about 4 times faster than version 1.
		*/
		/** The Stack_Deflicker calculates the average grey value for each frame and normalizes all frames so that they have same average grey level as a specified frame of the stack
		 * This plugin is very useful to remove flickering in movies caused by frame rates different from the frequency of 50/60Hz AC power used for the light-source that illuminate the scene.
		 * An input value of -1 corresponds to the brightest frame while an input value of zero corresponds to the faintest frame.
		 * If a ROI is selected the average frame intensity will be calculated from this region, but the whole scene will be normalized.
		 */


		ImageStack stack = imp.getStack();
		int size = stack.getSize();
		ImageProcessor ip = imp.getProcessor();
		Rectangle roi = ip.getRoi();

		// Find min and max

		double roiAvg[] = new double[size+1];
		double fMin = Double.MAX_VALUE;
		double fMax = -Double.MAX_VALUE;
		int maxF = 1;
		int minF = 1; 
						
		for (int slice=1; slice<=size; slice++) {
			IJ.showStatus("Calculating: "+slice+"/"+size);
			IJ.showProgress((double)slice/size);
			ip = stack.getProcessor(slice);
		roiAvg[slice]=0;
		
		ip.setRoi(roi);
		ImageStatistics is = ImageStatistics.getStatistics(ip, Measurements.MEAN, imp.getCalibration());
		
		roiAvg[slice]=is.mean; 
		
		if (roiAvg[slice]>fMax) {
		maxF= slice ;
		fMax=roiAvg[slice];
		}
		if (roiAvg[slice]<fMin) {
		minF = slice;
		fMin=roiAvg[slice];
		}
			
		}
		if (Fram<0) Fram=maxF;
		else if (Fram<1) Fram=minF;

		
		for (int slice=1; slice<=size; slice++) {
			IJ.showStatus("Normalizing: "+slice+"/"+size);
			IJ.showProgress((double)slice/size);
			ip = stack.getProcessor(slice);
			ip.multiply(roiAvg[Fram]/roiAvg[slice]);  
		}
		
		
		
		
		
	}


}
// Test
