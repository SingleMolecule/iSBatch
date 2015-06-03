/*
 * 
 */
package plugins_ij;

import java.awt.Component;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import tools.iSBOps;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.io.RoiDecoder;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

// TODO: Auto-generated Javadoc
/**
 * The Class Cell_Fluorescence.
 */
public class Cell_Fluorescence implements PlugIn {
	static ResultsTable table;
	static String[][] CSVContent = null;
	static File file;
	static String roiFolder;
	static File ROIFile;
	static String StatsFolder;

	public static void main(String[] args) {
		new Cell_Fluorescence().run("");

		System.out.println("Done detecting peaks");
	}

	public void run(String arg0) {

		// Open BFMAt
		// Open ConfigFile
		// Open ROI
		// Get file

		String csvFilename = IJ.getFilePath("Provide ControlFile.CSV");
		if (csvFilename == null)
			return;
		// Load table
		loadTable(csvFilename);

		String roiFolder = IJ
				.getDirectory("Provide the directory where ROIs are stored");
		if (roiFolder == null)
			return;

		File controlFile = new File(csvFilename);
		ROIFile = new File(roiFolder);

		StatsFolder = iSBOps
				.checkCreateSubDir(controlFile.getParent(), "Stats");
		// Getting option to choose channels

		// Create labels for the checkbox control
		List<String> uniques = getUniqueTags("Channel", table);

		String[] labels = iSBOps.CheckBoxLabes(uniques);

		try {
			decision_tree(labels);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("---------Done!-------------");
		IJ.showMessage("Done - Detecting peaks");
		java.awt.Toolkit.getDefaultToolkit().beep();

	}

	private List<String> getUniqueTags(String collum, ResultsTable table) {
		List<String> list = getAllTags(collum, table);
		HashSet<String> repeated = new HashSet<String>();
		repeated.addAll(list);
		list.clear();
		list.addAll(repeated);
		return list;

	}

	private List<String> getAllTags(String collum, ResultsTable table)
	{
		
		List<String> list = new ArrayList<>();
		
		
		for (int i=0; i<table.getCounter(); i++){
			String tag = table.getStringValue(collum, i);
			list.add(tag);
			}
			
				
		return list;
		
	}

	private void loadTable(String csvFilename) {
		try {
			table = ResultsTable.open(csvFilename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void decision_tree(String[] labels) throws IOException {
		int choice = ConfirmExecution(labels);
		System.out.println(choice);

		ResultsTable SUM = new ResultsTable();
		ResultsTable AVERAGE = new ResultsTable();
		ResultsTable AREA = new ResultsTable();
		ResultsTable STDDEV = new ResultsTable();
		ResultsTable MAX = new ResultsTable();
		ResultsTable MIN = new ResultsTable();

		if (choice == -1) {
			System.out.println("Action cancelled");
		}

		else {
			System.out.println("Channel: " + labels[choice]);
			System.out.println("Detecting peaks");
			// Ask to continue

			// Ask threshold values
			// The channel was selected. Now, get a list of all images that will
			// be analysed
			// List<String> listchannel =
			// tools.iSBOps.getSubFileList(CSVContent, labels[choice]);
			List<String> listchannel = getSubFileList(table, "Channel",
					labels[choice], "SaveToPath");
			for (int i = 1; i < listchannel.size(); i++) {

				// Figure out the Folder Index based on the fileName

				int Folder = (int) table.getValue("FolderIndex", i);
				// System.out.println(listchannel.get(i) +
				// "-------------Folder: "+ Folder);

				List<File> listRois = tools.iSBOps.getListofFiles(ROIFile);
				// Get list of Specific ROIs for a given file
				List<File> subSetROIS = getROIs(listRois, Folder);

				ImagePlus imp = new ImagePlus(listchannel.get(i));

				RoiManager manager = RoiManager.getInstance();
				if (manager == null)
					manager = new RoiManager();

				int subSetROISize = subSetROIS.size();

				for (int j = 0; j < subSetROISize; j++) {

					String file = subSetROIS.get(j).getAbsolutePath();
					RoiDecoder roiD = new RoiDecoder(file);
					ImageProcessor mask = imp.getMask();
					boolean hasmask = (mask != null);

					if (hasmask) {
						(new ImagePlus("The mask", mask)).show();
					}

					manager.addRoi(roiD.getRoi());
				}

				/**
				 * 
				 * Creating the tables to store all the next calculations
				 * 
				 * 
				 */

				if (manager != null) {

					ImageStack stack = imp.getStack();
					int stackSize = stack.getSize(); //

					// table of Rois
					@SuppressWarnings("unchecked")
					Hashtable<String, Roi> table = (Hashtable<String, Roi>) manager
							.getROIs();
					// for each ROI
					for (String label : table.keySet()) {
						Roi roi = table.get(label);
						// System.out.println(label);
						for (int k = 1; k <= stackSize; k++) {
							ImageProcessor ip = stack.getProcessor(k);
							ImageProcessor mask = roi != null ? roi.getMask()
									: null;
							Rectangle r = roi != null ? roi.getBounds()
									: new Rectangle(0, 0, ip.getWidth(),
											ip.getHeight());
							// PeakFitter fitter = new PeakFitter();
							// fitter.run(ip);
							double[] average = getAverage(ip, mask, r);
							// System.out.println(k + "-- "+ average[0] +
							// "---- " + average[1] + "---- " + average[2] +
							// "---- ");
							// SaveStats in Files
							/**
							 * Tables will have the format.\ Stats.568.MAX
							 * Stats.568.AVG..etc
							 * 
							 * Frame\Cell 1-1 1-2 2-1 ... 1 501 2111 5152 2 445
							 * 1800 4521 . . . . . . . . . . . .
							 * 
							 */
							SUM.setValue(label, k - 1, average[0]);
							AVERAGE.setValue(label, k - 1, average[1]);
							STDDEV.setValue(label, k - 1, average[2]);
							AREA.setValue(label, k - 1, average[3]);
							MIN.setValue(label, k - 1, average[4]);
							MAX.setValue(label, k - 1, average[5]);
						}
					}
				}

				/**
				 * 
				 * Saving the tables * StatFolder table2.saveAs(tableFilename +
				 * ".result.txt");
				 * 
				 */

				manager.close();
				imp.close();
				imp.flush();

			}
			SUM.saveAs(StatsFolder + File.separator + labels[choice]
					+ ".Cell.SUM.txt");
			AVERAGE.saveAs(StatsFolder + File.separator + labels[choice]
					+ ".Cell.AVERAGE.txt");
			AREA.saveAs(StatsFolder + File.separator + labels[choice]
					+ ".Cell.AREA.txt");
			STDDEV.saveAs(StatsFolder + File.separator + labels[choice]
					+ ".Cell.STDDEV.txt");
			MAX.saveAs(StatsFolder + File.separator + labels[choice]
					+ ".Cell.MAX.txt");
			MIN.saveAs(StatsFolder + File.separator + labels[choice]
					+ ".Cell.MIN.txt");
		}
		int choice2 = askToContinue();
		if (choice2 == 0) {
			decision_tree(labels);

		}
	}

	private static List<String> getSubFileList(ResultsTable resultstable, String colNameToCheckMatch, String stringToMatch, String colNameGetValuesFrom) {
		List<String> list = new ArrayList<>();
		
		for (int row = 0; row<resultstable.getCounter(); row++){
			if(resultstable.getStringValue(colNameToCheckMatch, row).equalsIgnoreCase(stringToMatch)){
				list.add(resultstable.getStringValue(colNameGetValuesFrom, row));
			}
		}
		return list;
	}

	private static List<File> getROIs(List<File> listRois, int folder) {
		List<File> results = new ArrayList<>();
		
		for (int i=0; i<listRois.size();i++){
			//Split the ROI name to get the usefull index
			String ROINAME = listRois.get(i).getName();
		// Split based on the "-"" 
			int intROIindex = Integer.parseInt(ROINAME.split("-")[0]);
			if (folder == intROIindex){
				results.add(listRois.get(i));
			}
		}
		return results;
	}

	private static double[] getAverage(ImageProcessor ip, ImageProcessor mask,
			Rectangle r) {
		double[] results = new double[7];
		results[0] = 0; // sum of all pixels
		results[1] = 0; // average
		results[2] = 0; // std deviation
		results[3] = 0; // number of pixels
		results[4] = 0; // min
		results[5] = 0; // max
		results[6] = 0; // will store slice value

		List<Double> ListOfValues = new ArrayList<Double>();

		for (int y = 0; y < r.height; y++) {
			for (int x = 0; x < r.width; x++) {
				if (mask == null || mask.getPixel(x, y) != 0) {

					double pixelValue = ip.getPixelValue(x + r.x, y + r.y);
					ListOfValues.add(pixelValue);
					results[0] += pixelValue;

					if (pixelValue > results[5]) {
						results[5] = pixelValue;
					}
					if (pixelValue < results[4]) {
						results[4] = pixelValue;
					}
					results[3]++;
				}
			}
		}
		results[1] = results[0] - results[3];
		results[2] = getSTDDEV(ListOfValues);

		return results;
	}

	private static double getSTDDEV(List<Double> listOfValues) {
		int size = listOfValues.size();
		double sum = 0;

		List<Double> ListOfSquares = new ArrayList<Double>();

		for (int i = 0; i < size; i++) {
			double current = listOfValues.get(i);
			sum += current;
			ListOfSquares.add(current * current);

		}

		double average = sum / size;
		double sumSquareDiff = 0;

		for (int i = 0; i < size; i++) {
			double diference = listOfValues.get(i) - average;

			sumSquareDiff += diference * diference;
		}

		double s2 = sumSquareDiff / (size - 1);

		return s2;
	}

	private static int ConfirmExecution(String[] labels) {

		Object[] options2 = labels;

		Component frame2 = null;
		int DKSelection = JOptionPane.showOptionDialog(frame2,
				"Detect peaks of channel: ", "Choose Channel",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do
				options2, // the titles of buttons
				options2[0]); // default button title
		return DKSelection;// TODO Auto-generated method stub

	}

	private static int askToContinue() {
		Object[] options2 = { "Yes", "No" };

		Component frame2 = null;
		int DKSelection = JOptionPane.showOptionDialog(frame2,
				"Do you wish to continue with another channel?: ",
				"Dark Count Correction", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
				options2, // the titles of buttons
				options2[0]); // default button title
		return DKSelection;// TODO Auto-generated method stub

	}
}
