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

import iSBatch.iSBatchPreferences;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.io.RoiEncoder;
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

// TODO: Auto-generated Javadoc
/**
 * The Class FindPeaksOperation.
 *
 * @author VictorCaldas
 */
public class FindPeaksOperation implements Operation {
	private FindPeaksGui dialog;
	private String channel;
	private boolean useDiscoidal;
	private DatabaseModel model;
	iSBatchPreferences preferences;
	PeakFinder peakFinder;
	RoiManager roiManager;
	int NUMBER_OF_OPERATIONS;
	int currentCount;

	public FindPeaksOperation(DatabaseModel treeModel) {
		this.model = treeModel;
	}
	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	@Override
	public String getName() {
		return "Peak Finder";
	}
	@Override
	public boolean setup(Node node) {
		// String to parse:

		preferences = model.preferences;
		dialog = new FindPeaksGui(node);
		if (dialog.isCanceled())
			return false;
		this.useDiscoidal = dialog.useDiscoidal;
		this.channel = dialog.getChannel();
		NUMBER_OF_OPERATIONS = node.getNumberOfFoV();
		currentCount = 1;
		return true;
	}

	@Override
	public void finalize(Node node) {
		// TODO Auto-generated method stub

	}
	@Override
	public void visit(Root root) {
		System.out.println("Not applicable to root. ");
	}
	private void run(Node node) {
		ImagePlus imp = IJ.openImage(node.getPath());
		peakFinder = new PeakFinder(useDiscoidal, new DiscoidalAveragingFilter(
				imp.getWidth(), iSBatchPreferences.INNER_RADIUS,
				iSBatchPreferences.OUTER_RADIUS),
				parseDouble(iSBatchPreferences.SNR_THRESHOLD),
				parseDouble(iSBatchPreferences.INTENSITY_THRESHOLD),
				Integer.parseInt(iSBatchPreferences.DISTANCE_BETWEEN_PEAKS));

		ArrayList<Roi> rois = findPeaks(peakFinder, imp);

		String nameToSave = node.getName().replace(".TIF", "")
				+ "_PeakROIs.zip";
		System.out.println("Saving peak Rois @ " + node.getOutputFolder()
				+ File.separator + nameToSave);

		try {
			saveRoisAsZip(rois, node.getOutputFolder() + File.separator
					+ nameToSave);
			node.getProperties().put(channel + "_AllPeaks",
					node.getOutputFolder() + File.separator + nameToSave);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (iSBatchPreferences.insideCell == true) {
			System.out.println("Detect within cells");
			// Filter peaks
			// Load Roi Peaks, and Load ROI Cells
			RoiManager allPeaksManager = new RoiManager(true);
			allPeaksManager.runCommand("Open", node.getOutputFolder() + File.separator
					+ nameToSave);
			System.out.println(allPeaksManager.getCount());
			RoiManager cellsManager = new RoiManager(true);
			System.out.println(node.getParent().getCellROIPath());
			
			cellsManager.runCommand("Open", node.getParent().getCellROIPath());
			System.out.println(cellsManager.getCount());
			
			RoiManager filteredPeaks = PeaksInsideCells(cellsManager,
					allPeaksManager);

			System.out.println(filteredPeaks.getCount());
			nameToSave = node.getName().replace(".TIF", "")
					+ "_PeakROIsFiltered.zip";
			
				filteredPeaks.runCommand("Save", node.getOutputFolder() + File.separator+ nameToSave);
				
				node.getProperties().put(channel + "_PeaksFiltered",
						node.getOutputFolder() + File.separator + nameToSave);
		}

		currentCount++;

	}

	/**
	 * Peaks inside cells.
	 *
	 * @param cellsManager the cells manager
	 * @param allPeaksManager the all peaks manager
	 * @return the roi manager
	 */
	private RoiManager PeaksInsideCells(RoiManager cellsManager,
			RoiManager allPeaksManager) {
		RoiManager filtered = new RoiManager(true);
		// check if peaks are inside cells
		@SuppressWarnings("unchecked")
		Hashtable<String, Roi> listOfCells = (Hashtable<String, Roi>) cellsManager
				.getROIs();
		@SuppressWarnings("unchecked")
		Hashtable<String, Roi> listOfPeaks = (Hashtable<String, Roi>) allPeaksManager
				.getROIs();

		// for some reason, RoiManager.getRoi does not work.
		for (String label : listOfCells.keySet()) {
			ShapeRoi currentCell = new ShapeRoi(listOfCells.get(label));

			for (String peakLabel : listOfPeaks.keySet()) {
				Roi currentPeak = listOfPeaks.get(peakLabel);

				if (currentCell.contains(currentPeak.getBounds().x,
						currentPeak.getBounds().y)) {
					filtered.addRoi(currentPeak);
				}
			}

		}
		return filtered;
	}

	@Override
	public void visit(Experiment experiment) {
		for (Sample sample : experiment.getSamples()) {
			visit(sample);

		}
	}

	@Override
	public void visit(Sample sample) {
		for (FieldOfView fov : sample.getFieldOfView()) {
			visit(fov);
		}
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		for (FileNode fileNode : fieldOfView.getImages(channel)) {
			visit(fileNode);
		}
	}

	@Override
	public void visit(FileNode fileNode) {
		System.out.println("Peak Find: " + currentCount + " of "
				+ NUMBER_OF_OPERATIONS);
		if (currentCount == NUMBER_OF_OPERATIONS) {
			System.out.println("Peak Finder finished.");
		}
		IJ.showProgress(currentCount, NUMBER_OF_OPERATIONS);
		run(fileNode);

	}

	public static void main(String[] args) {
		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 2);
		FindPeaksGui dialog = new FindPeaksGui(model.getRoot());
		System.out.println(dialog.getChannel());
		System.out.println(dialog.getMethod());
		System.out.println(dialog.useDiscoidal);
		System.out.println(dialog.getImageType());
		System.out.println(dialog.getInsindeCells());
		
	}

	@Override
	public void visit(OperationNode operationNode) {
		// TODO Auto-generated method stub

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
		return null;
	}

	/**
	 * Parses the double.
	 *
	 * @param str the str
	 * @return the double
	 * @throws NumberFormatException the number format exception
	 */
	private double parseDouble(String str) throws NumberFormatException {
		double toReturn = 0;
		// System.out.println(str);
		if (!str.equalsIgnoreCase("") || !str.equals(null)) {
			try {
				toReturn = Double.parseDouble(str);
				// System.out.println("Value parsed :" + toReturn);
			} catch (NumberFormatException ex) {
				System.err.println("Ilegal input");
				toReturn = 0;
				// Discard input or request new input ...
				// clean up if necessary
			}
		}

		return toReturn;
	}

	/**
	 * Run plug in filter.
	 *
	 * @param filter the filter
	 * @param imp the imp
	 */
	public static void runPlugInFilter(PlugInFilter filter, ImagePlus imp) {

		ImageStack stack = imp.getImageStack();

		for (int slice = 1; slice <= stack.getSize(); slice++)
			runPlugInFilter(filter, stack.getProcessor(slice));
	}

	/**
	 * Run plug in filter.
	 *
	 * @param filter the filter
	 * @param ip the ip
	 */
	public static void runPlugInFilter(PlugInFilter filter, ImageProcessor ip) {
		filter.run(ip);
	}

	/**
	 * Find peaks.
	 *
	 * @param finder the finder
	 * @param imp the imp
	 * @return the array list
	 */
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

	/**
	 * Save rois as zip.
	 *
	 * @param rois the rois
	 * @param filename the filename
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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

	

}
