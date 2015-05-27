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
import iSBatch.iSBatchPreferences;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.io.RoiEncoder;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import analysis.PeakFinder;
import analysis.PeakFitter;
import operations.Operation;
import process.DiscoidalAveragingFilter;
import test.TreeGenerator;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
public class FitPeaksOperation implements Operation {
	private FitPeaksOperationGUI dialog;
	private String channel;
	PeakFinder peakFinder;
	RoiManager roiManager;
	int NUMBER_OF_OPERATIONS;
	int currentCount;
	private String customSearch;
	ArrayList<String> tags = new ArrayList<String>();
	private Node currentNode;
	RoiManager cellRoiManager;
	RoiManager peaksManager;
	private boolean exportRaw;
	private int radius;
	
	public FitPeaksOperation(DatabaseModel treeModel) {
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
		if(channel.equalsIgnoreCase(null)){
			LogPanel.log("No channel selected. Operation cancelled.");
			return false;
		}
		this.customSearch = dialog.getCustomSearch();
		this.tags = dialog.getTags();
		this.radius = Integer.parseInt(iSBatchPreferences.SELECTION_RADIUS);
		NUMBER_OF_OPERATIONS = node.getNumberOfFoV();
		currentCount = 1;
		this.exportRaw = dialog.exportRaw();

		return true;
	}

	@Override
	public void finalize(Node node) {
	}

	@Override
	public void visit(Root root) {
		System.out.println("Not applicable to root. ");
	}

	private void run(Node node) {
		String extention = null;
		// Run Peak Finder
		//
		ResultsTable fittedPeaks = null;
		ArrayList<Node> filenodes = node.getDescendents(new GenericFilter(
				channel, tags, extention, customSearch));
		
		for (Node currentNode : filenodes) {
			System.out.println(currentNode.getName());
			fittedPeaks = getFittedPeaks(currentNode);
			
			

		// Save raw table
		String nameToSave = currentNode.getName().replace(".tif", "")
				+ "_PeakTable.csv";

		try {
			System.out.println("Saving peak Rois @ " + currentNode.getOutputFolder()
					+ File.separator + nameToSave);
			fittedPeaks.saveAs(currentNode.getOutputFolder() + File.separator
					+ nameToSave);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fittedPeaks.reset();
		}
		

	}

	private ResultsTable getFittedPeaks(Node node) {
		FileNode fnode = (FileNode) node;
		this.currentNode = node;
		
		ImagePlus imp = fnode.getImage();
		System.out.println(fnode.getPath());
		// Check if calculation will be done inside cells and load the manager
		if (iSBatchPreferences.insideCell) {
			cellRoiManager = new RoiManager(true);
			cellRoiManager.runCommand("Open", node.getCellROIPath());
			return getPeaks(imp);

		}

		return getPeaks(imp);
	}

	private ResultsTable getPeaks(ImagePlus imp) {
		ResultsTable PeakData = new ResultsTable();
		ImageStack stack = imp.getStack();
		int stackSize = stack.getSize();
		
		
		
		for (int stackPosition = 1; stackPosition <= stackSize; stackPosition++) {
			ImageProcessor ip = stack.getProcessor(stackPosition);
			@SuppressWarnings("unchecked")
			Hashtable<String, Roi> table = (Hashtable<String, Roi>) cellRoiManager.getROIs();
			for (String label : table.keySet()) {

				Roi roi = table.get(label);
				// ImageProcessor ip2 = imp.getProcessor();
				ip.setRoi(roi);

				DiscoidalAveragingFilter filter1 = new DiscoidalAveragingFilter(
						ip.getWidth(), iSBatchPreferences.INNER_RADIUS,
						iSBatchPreferences.OUTER_RADIUS);
				PeakFinder finder = new PeakFinder(
						iSBatchPreferences.useDiscoidalFiltering, filter1,
						iSBatchPreferences.SNR_THRESHOLD,
						iSBatchPreferences.INTENSITY_THRESHOLD,
						iSBatchPreferences.DISTANCE_BETWEEN_PEAKS);
				
				
				
				ArrayList<Point> positions = finder.findPeaks(ip);
				
				for (int j = 0; j < positions.size(); j++) {
					// fit peak
					
					double[] parameters= new double[6];
					double[] errors = new double[6];
					
					for (int k = 0; k < parameters.length; k++)
						parameters[k] = Double.NaN;
					
					int x = positions.get(j).x;
					int y = positions.get(j).y;
					
					parameters[2] = x;
					parameters[3] = y;

					ip.setRoi(x - (radius-1) , y - (radius-1), (2*radius-1), 2*radius-1);
					/**
					 * 
					 * 
					 * Selection radius
					 * 
					 * 
					 */
					
					
					if(exportRaw){
						//Create FolderS
						ImagePlus imp2 =new ImagePlus();
						
						//Format
						// frame-cell-x-y
						String fileName = "CellNumber-"+ Integer.toString(stackPosition)+ "-"+ Integer.toString(x) + "-" + Integer.toString(y);
						File outputDir = new File(currentNode.getOutputFolder()+ File.separator + "RawPeaks" + File.separator + "peakImage");
						outputDir.mkdirs();
						imp2.setProcessor(ip.crop());
						IJ.saveAsTiff(imp2, outputDir.getAbsolutePath() + File.separator + fileName );
						System.out.println(outputDir.getAbsolutePath() + File.separator + fileName);
						////
						outputDir = new File(currentNode.getOutputFolder()+ File.separator + "RawPeaks" + File.separator + "peakValues");
						outputDir.mkdirs();
						IJ.saveAs(imp2, "Text Image", outputDir.getAbsolutePath() + File.separator + fileName );
						System.out.println(outputDir.getAbsolutePath() + File.separator + fileName + ".csv");
					}
					PeakFitter.fitPeak(ip, parameters, errors);

					// Filtering conditions

					for (int k = 0; k < parameters.length; k++) {

						if (Double.isNaN(parameters[k])
								|| Double.isNaN(errors[k])
								|| Math.abs(errors[k]) > iSBatchPreferences.maxError[k])
							continue;

					}
					double position_x = parameters[2];
					
					if ( position_x<1 ||  position_x>(ip.getWidth()-1) || Double.isNaN(position_x))
						continue;
					
					double  position_y = parameters[3];
					 
					if ( position_y<1 ||  position_x>(ip.getHeight()-1) || Double.isNaN(position_y))
					continue;
					
					double fwhmx = parameters[4] * SIGMA_TO_FWHM;
					
						if (fwhmx<1 || fwhmx>6  || Double.isNaN(fwhmx))
							continue;
						
					double fwhmy = parameters[5] * SIGMA_TO_FWHM;
						if (fwhmy<1 || fwhmy>6 || Double.isNaN(fwhmy))
							continue;
						
						
						//Add values to table
						PeakData.incrementCounter();

//						addDataToTable(PeakData);
						PeakData.addValue("Experiment", currentNode.getExperimentName());
						PeakData.addValue("Sample", currentNode.getSampleName());
						PeakData.addValue("FoV", currentNode.getFieldOfViewName());
						PeakData.addValue("Cell", label);
						PeakData.addValue("slice", stackPosition);
						// PeakData.addValue("ExperimentIndex",
						// ExperimentIndexNumber);
						PeakData.addValue("baseline", parameters[0]);
						PeakData.addValue("height", parameters[1]);
						PeakData.addValue("x", parameters[2]);
						PeakData.addValue("y", parameters[3]);
						PeakData.addValue("sigma_x", parameters[4]);
						PeakData.addValue("sigma_y", parameters[5]);

						PeakData.addValue("fwhm_x", fwhmx);
						PeakData.addValue("fwhm_y", fwhmy);
						PeakData.addValue("fwhm", (fwhmx + fwhmy) / 2);

						PeakData.addValue("error_baseline", errors[0]);
						PeakData.addValue("error_height", errors[1]);
						PeakData.addValue("error_x", errors[2]);
						PeakData.addValue("error_y", errors[3]);
						PeakData.addValue("error_sigma_x", errors[4]);
						PeakData.addValue("error_sigma_y", errors[5]);

						double errorFwhmx = errors[4] * SIGMA_TO_FWHM;
						double errorFwhmy = errors[5] * SIGMA_TO_FWHM;

						PeakData.addValue("error_fwhm_x", errorFwhmx);
						PeakData.addValue("error_fwhm_y", errorFwhmy);
						PeakData.addValue(
								"error_fwhm",
								Math.sqrt(errorFwhmx * errorFwhmx + errorFwhmy
										* errorFwhmy) / 2);

						// table.addValue("z", zScale * (fwhmx - fwhmy));
						// table.addValue("error_z", zScale * Math.sqrt(errorFwhmx *
						// errorFwhmx + errorFwhmy * errorFwhmy));

						PeakData.addValue("slice", stackPosition);
					
					
				}
			}

		}
		return PeakData;
	}

	@Override
	public void visit(Experiment experiment) {
		run(experiment);
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
		// System.out.println("Peak Find: " + currentCount + " of "
		// + NUMBER_OF_OPERATIONS);
		// if (currentCount == NUMBER_OF_OPERATIONS) {
		// System.out.println("Peak Fitter finished.");
		// }
		// IJ.showProgress(currentCount, NUMBER_OF_OPERATIONS);
		run(fileNode);

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
		// TODO Auto-generated method stub
		return null;
	}

	public static void runPlugInFilter(PlugInFilter filter, ImagePlus imp) {

		ImageStack stack = imp.getImageStack();

		for (int slice = 1; slice <= stack.getSize(); slice++)
			runPlugInFilter(filter, stack.getProcessor(slice));
	}

	public static void runPlugInFilter(PlugInFilter filter, ImageProcessor ip) {
		filter.run(ip);
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

	public static void saveRoisAsZip(ArrayList<Roi> rois, String filename)
			throws IOException {
		ZipOutputStream zos = new ZipOutputStream(
				new FileOutputStream(filename));

		int i = 0;

		for (Roi roi : rois) {
			byte[] b = RoiEncoder.saveAsByteArray(roi);
			zos.putNextEntry(new ZipEntry(i + ".roi"));
			zos.write(b, 0, b.length);
			i++;
		}

		zos.close();

	}
	public static void main(String[] args) {
		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 2);
		FitPeaksOperationGUI dialog = new FitPeaksOperationGUI(model.getRoot());
		
		System.out.println("Channel: "+ dialog.getChannel());
		
		System.out.println("Custom search: "+ dialog.getCustomSearch());
		
		for(String string : dialog.getTags()){
			System.out.println("Tag: "+ string);
		}
		
		System.out.println("Discoidal: " + dialog.useDiscoidal);
		System.out.println("Cells: " + dialog.useCells);
		System.out.println("Export raw: " + dialog.exportRaw());
		
		
		
		
		
		
		
	}

}
