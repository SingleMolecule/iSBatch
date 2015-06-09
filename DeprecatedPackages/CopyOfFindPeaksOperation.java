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

import gui.FileSelectionDialog;
import gui.LogPanel;
import iSBatch.iSBatchPreferences;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.io.RoiEncoder;
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
import utils.FileNames;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;

public class CopyOfFindPeaksOperation implements Operation {
	private FileSelectionDialog dialog;
	private String channel;
	private boolean useDiscoidal;
	// private DatabaseModel model;
	PeakFinder peakFinder;
	RoiManager roiManager;
	int NUMBER_OF_OPERATIONS;
	int currentCount;
	String operationName = "Peak Finder";

	public CopyOfFindPeaksOperation() {
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
		this.channel = dialog.getChannel();
		if (channel.equalsIgnoreCase(null)) {
			LogPanel.log("No channel selected. Operation cancelled.");
			return false;
		}
		NUMBER_OF_OPERATIONS = node.getNumberOfFoV();
		currentCount = 1;
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

		ImagePlus imp = IJ.openImage(node.getPath());
		peakFinder = new PeakFinder(useDiscoidal, new DiscoidalAveragingFilter(
				imp.getWidth(), dialog.getINNER_RADIUS(),
				dialog.getOUTER_RADIUS()),
				 Double.parseDouble(dialog.getSNR_THRESHOLD()),
				Double.parseDouble(dialog.getINTENSITY_THRESHOLD()),
				Integer.parseInt(dialog.getDISTANCE_BETWEEN_PEAKS()));

		ArrayList<Roi> rois = findPeaks(peakFinder, imp);

		String nameToSave = FileNames
				.getOutputFilename(node, "PeakROI", ".zip");
		LogPanel.log("Saving peak Rois @ " + node.getOutputFolder()
				+ File.separator + nameToSave);

		try {
			System.out.println("Detected " + rois.size() + " raw peaks.");
			saveListOfRoisAsZip(rois, node.getOutputFolder() + File.separator
					+ nameToSave);
			node.getProperties().put(channel + "_AllPeaks",
					node.getOutputFolder() + File.separator + nameToSave);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (iSBatchPreferences.insideCell == true) {
			LogPanel.log("Detect within cells");

			RoiManager allPeaksManager = new RoiManager(true);
			allPeaksManager.runCommand("Open", node.getOutputFolder()
					+ File.separator + nameToSave);

			RoiManager cellsManager = new RoiManager(true);
			cellsManager.runCommand("Open", node.getParent().getCellROIPath());

			RoiManager peaksInsideCells = getPeaksInsideCells(allPeaksManager,
					cellsManager);

			System.out.println(peaksInsideCells.getCount());
			nameToSave = node.getName().replace(".TIF", "")
					+ "_PeakROIsFiltered.zip";

			peaksInsideCells.runCommand("Save", node.getOutputFolder()
					+ File.separator + nameToSave);

			node.getProperties().put(channel + "_PeaksFiltered",
					node.getOutputFolder() + File.separator + nameToSave);
		}
		currentCount++;
	}

	private RoiManager getPeaksInsideCells(RoiManager allPeaksManager,
			RoiManager cellsManager) {

		RoiManager peaksInsideCells = new RoiManager(true);

		@SuppressWarnings("unchecked")
		Hashtable<String, Roi> listOfCells = (Hashtable<String, Roi>) cellsManager
				.getROIs();
		@SuppressWarnings("unchecked")
		Hashtable<String, Roi> listOfPeaks = (Hashtable<String, Roi>) allPeaksManager
				.getROIs();

		for (String label : listOfCells.keySet()) {
			ShapeRoi currentCell = new ShapeRoi(listOfCells.get(label));

			for (String peakLabel : listOfPeaks.keySet()) {
				Roi currentPeak = listOfPeaks.get(peakLabel);

				if (currentCell.contains(currentPeak.getBounds().x,
						currentPeak.getBounds().y)) {
					peaksInsideCells.addRoi(currentPeak);
				}
			}
		}
		return peaksInsideCells;
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
		run(fileNode);

	}

	public static void main(String[] args) {
		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 2);
		FindPeaksGui dialog = new FindPeaksGui(model.getRoot());
		System.out.println(dialog.getSelectedChannel());
		System.out.println(dialog.getMethod());
		System.out.println("Discoidal: " + dialog.performDiscoidalFiltering());
		System.out.println(dialog.getImageType());
		System.out.println("Inside cells: " + dialog.performInsideCells());
		System.out.println(dialog.getINNER_RADIUS());
		System.out.println(dialog.getOUTER_RADIUS());
		System.out.println(dialog.getSNR_THRESHOLD());
		System.out.println(dialog.getINTENSITY_THRESHOLD());
		System.out.println(dialog.getDISTANCE_BETWEEN_PEAKS());
		System.out.println("--------");
		System.out.println(Double.parseDouble(dialog.getSNR_THRESHOLD()));
		
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

	public static void saveListOfRoisAsZip(ArrayList<Roi> rois, String filename)
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
