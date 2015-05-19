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
import java.util.Arrays;

import filters.NodeFilterInterface;

import java.util.HashMap;



import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import model.parameters.NodeType;
import operations.Operation;
public class MicrobeTrackerIO implements Operation {
	private MicrobeTrackerIOGui dialog;
	private RoiManager manager;
	private String channel;
	private String method;
	private String customFilter;
	private String matFilePath;
	private String BFFIleInputPath;
	private Object imageType;

	/**
	 * Instantiates a new microbe tracker io.
	 *
	 * @param treeModel the tree model
	 */
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
	 * @param node the node
	 * @return true, if successful
	 */
	@Override
	public boolean setup(Node node) {
		dialog = new MicrobeTrackerIOGui(node);
		if (dialog.isCanceled())
			return false;
		
		//Get information from the dialog
		//From panel1
		this.channel = dialog.getChannel();
		this.imageType = dialog.getImageType();
		this.customFilter = dialog.getCustomFilter();
		
		// From panel 2
		this.matFilePath = dialog.getMatFilePath();
		this.BFFIleInputPath = dialog.BFFIleInputPath;
		return true;
	}

	/**
	 * Finalize.
	 *
	 * @param node the node
	 */
	@Override
	public void finalize(Node node) {
		System.out.println("MicrobeTracker IO Operation finalized");
	}


	/**
	 * Visit.
	 *
	 * @param root the root
	 */
	@Override
	public void visit(Root root) {
	}

	/**
	 * Visit.
	 *
	 * @param experiment the experiment
	 */
	@Override
	public void visit(Experiment experiment) {
		System.out.println(experiment.getProperty("type"));
		run(experiment);
	}

	/**
	 * Run.
	 *
	 * @param node the node
	 */
	private void run(Node node) {
		//Decide path to take.
		// Relevant informations: Experiment Type : Rapid Acquisition or Time Lapse
		
//		String experimentType = node.getExperimentType();
		
		
		File matFile = new File(matFilePath);

		if (!matFile.exists()) {
			getStackForMT(node);
		} else {
			importFiles(node, matFile);
		}

	}

	/**
	 * Import files.
	 *
	 * @param node the node
	 * @param matFile the mat file
	 */
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
				System.out.println(currentFov.getOutputFolder() + File.separator
						+ "cellRoi.zip");
				currentFov.setCellROIPath(currentFov.getOutputFolder() + File.separator
						+ "cellRoi.zip");
				currentFov.getProperties().put("CellRoi", currentFov.getCellROIPath());
				currentManager.runCommand("Save", currentFov.getOutputFolder()
						+ File.separator + "cellRoi.zip");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ImagePlus getReference(Node node) {
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

	/**
	 * Visit.
	 *
	 * @param sample the sample
	 */
	@Override
	public void visit(Sample sample) {
		run(sample);
	}

	/**
	 * Visit.
	 *
	 * @param fieldOfView the field of view
	 */
	@Override
	public void visit(FieldOfView fieldOfView) {
		run(fieldOfView);
	}

	/**
	 * Visit.
	 *
	 * @param fileNode the file node
	 */
	@Override
	public void visit(FileNode fileNode) {
		run(fileNode);
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

	}

	/**
	 * Visit.
	 *
	 * @param operationNode the operation node
	 */
	@Override
	public void visit(OperationNode operationNode) {
		// TODO Auto-generated method stub

	}

	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	private String getChannel() {

		return channel;
	}

	/**
	 * Gets the created nodes.
	 *
	 * @return the created nodes
	 */
	@Override
	public Node[] getCreatedNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	@Override
	public HashMap<String, String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the roi.
	 *
	 * @param m the m
	 * @return the roi
	 */
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
	
	/**
	 * Stack_ deflicker.
	 *
	 * @param imp the imp
	 */
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
