package operations.microbeTrackerIO;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import gui.LogPanel;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.RoiManager;

/**
 * @author Victor Caldas Class to handle hierarchical relations between ROI.
 * 
 */
public class Lineage {
	// TODO: There is a bug to be handled. Rois get an extra -1 or -2 in the end
	// of their name due to repetitions. This thoews an error window that has to
	// be handled or modify this class to avoid that.
	/**
	 * 
	 */
	private JTree tree;
	private static RoiManager manager = new RoiManager(true);
	private ResultsTable lineageTable;
	private static ImagePlus imp;
	ArrayList<RoiManager> listOfManager = new ArrayList<RoiManager>();
	int parentColumn;
	int nameColumn;
	int childrenCountColumn;
	int maxLineageIndex = 0;
	DefaultMutableTreeNode root = new DefaultMutableTreeNode("Lineage");
	ArrayList<DefaultMutableTreeNode> leafs = new ArrayList<DefaultMutableTreeNode>();
	public File OUTPUT_FOLDER;
	private String MANAGER_PATH;

	public Lineage(String path, ImagePlus imp) {
		this.MANAGER_PATH = path;
		RoiManager manager = new RoiManager(true);
		manager.runCommand("Open", path);
		new Lineage(manager, imp);
	}

	public Lineage(RoiManager manager, ImagePlus imp) {
		Lineage.manager = manager;
		this.imp = imp;
		this.lineageTable = new ResultsTable();
	}

	public static void main(String[] args) {
		String pathToManager = "/home/vcaldas/ISBatchTutorial/MinimalDataset/TutorialDB_files/TimeLapse/DnaX_DnaX-M9Glycerol/001/cellRoi.zip";
		ImagePlus imp = IJ
				.openImage("/home/vcaldas/ISBatchTutorial/MinimalDataset/TutorialDB_files/TimeLapse/DnaX_DnaX-M9Glycerol/001/001 BF_flat.tif");
		String path = "/home/vcaldas/ISBatchTutorial/MinimalDataset/TutorialDB_files/TimeLapse/DnaX_DnaX-M9Glycerol/001/CellMeasurements.csv";

		Lineage lineage = new Lineage(pathToManager, imp);
		lineage.assingn();

	}

	public void assingn() {
		setOutputFolder();
		createLineageTable();
		splitLineage();
		saveTable();
		TreeExample();
		System.out.println(" Tree Example done");
		getROIManagers();

		
		saveSubManagers();
		System.out.println("get RoiManagers Done");
		// lineage.printTable();

		
	}

	private void setOutputFolder() {
		File f = new File(MANAGER_PATH);
		OUTPUT_FOLDER = new File(f.getParent() + File.separator + "lineage");
		OUTPUT_FOLDER.mkdirs();
	}

	private void saveSubManagers() {
		int i = 1;
		for (RoiManager rm : listOfManager) {
			rm.runCommand("Save", OUTPUT_FOLDER.getAbsolutePath()
					+ File.separator + "roiSet_" + i + ".zip");
			i++;
		}

	}

	public ImagePlus getImp() {
		return imp;
	}

	private void getROIManagers() {

		ArrayList<DefaultMutableTreeNode> leafs = getAllLeafs();
		System.out.println("There are " + leafs.size() + " leafs");

		for (DefaultMutableTreeNode leaf : leafs) {
			RoiManager currentManager = new RoiManager(true);

			for (TreeNode s : leaf.getPath()) {
				if (!s.equals(root)) {
					currentManager.addRoi(getRoi(s.toString()));

				}
			}
			listOfManager.add(currentManager);
		}

	}

	private ArrayList<DefaultMutableTreeNode> getAllLeafs() {

		System.out.println("Leafs " + root.getLeafCount());
		visitNode(root);

		return leafs;
	}

	public void visitNode(DefaultMutableTreeNode node) {
		if (!node.isLeaf()) {
			for (int i = 0; i < node.getChildCount(); i++) {
				visitNode((DefaultMutableTreeNode) node.getChildAt(i));
			}

		}
		if (node.isLeaf()) {
			leafs.add(node);
		}
	}

	private Roi getRoi(String roiName) {
		Roi roi = null;
		for (int i = 0; i < manager.getCount(); i++) {
			String roiInManagerName = manager.getRoi(i).getName();

			if (roiInManagerName.equalsIgnoreCase(roiName)) {
				roi = manager.getRoi(i);
			}

		}
		return roi;
	}

	public void TreeExample() {

		for (int row = 0; row < lineageTable.getCounter(); row++) {
			if (!hasParent(row)) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(
						getRoiNameFromTable(row));
				root.add(node);
				addChildrenRecursivelly(node);
			}
		}

		// add the child nodes to the root node

		// create the tree by passing in the root node
		this.tree = new JTree(root);
		// add(tree);
		//
		// this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// this.setTitle("JTree Example");
		// this.pack();
		// this.setVisible(true);
	}

	private void addChildrenRecursivelly(DefaultMutableTreeNode node) {
		String name = node.toString();
		ArrayList<String> childrens = getChildrenRois(name);

		if (childrens.size() != 0) {
			for (String s : childrens) {
				DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(s);
				node.add(newChild);
				addChildrenRecursivelly(newChild);
			}

		}
	}

	private ArrayList<String> getChildrenRois(String parentName) {
		ArrayList<String> listChildren = new ArrayList<String>();
		for (int row = 0; row < lineageTable.getCounter(); row++) {
			if (getParentName(row).equalsIgnoreCase(parentName)) {
				listChildren.add(lineageTable.getStringValue("Name", row));
			}
			;
		}

		return listChildren;
	}

	private String getRoiNameFromTable(int row) {
		return lineageTable.getStringValue("Name", row);
	}

	private void printTable() {
		String headers = null;
		for (String s : lineageTable.getHeadings()) {

			headers += s;
			headers += "\t";
		}
		System.out.println(headers);
		for (int i = 0; i < lineageTable.getCounter(); i++) {
			System.out.println(lineageTable.getRowAsString(i));
		}

	}

	private void splitLineage() {
		addChildrenCountToTable();

	}


	private String getParentLineageIndex(int row) {
		String parentName = getParentName(row);

		for (int i = 0; i < lineageTable.getCounter(); i++) {
			if (getParentName(i).equalsIgnoreCase(parentName)) {
				return lineageTable.getStringValue("LineageIndex", i);
			}
		}

		return "NaN";
	}

	private int getMaxLineageAndIncrementCounter() {
		maxLineageIndex++;
		return maxLineageIndex - 1;
	}

	private boolean hasSiblings(int row) {
		if (getNumberOfChildrens(getParentName(row)) > 0) {
			return true;
		}
		return false;
	}

	private int getNumberOfChildrens(String parentName) {
		for (int i = 0; i < lineageTable.getCounter(); i++) {
			if (getParentName(i).equalsIgnoreCase(parentName)) {
				return i;
			}
		}
		return 0;
	}

	private String getParentName(int row) {// TODO Auto-generated method stub
		return lineageTable.getStringValue("ParentName", row);
	}

	private boolean hasParent(int row) {
		if (getParentName(row).startsWith("0.00")) {
			return false;

		}
		return true;
	}

	private boolean hasChildren(int row) {
		if (getChildrenCountFromTable(row) > 0) {
			return true;
		}
		return false;
	}

	private int getChildrenCountFromTable(int row) {
		return (int) lineageTable.getValue("ChildrenCount", row);
	}

	private void addChildrenCountToTable() {
		for (int i = 0; i < lineageTable.getCounter(); i++) {
			lineageTable.setValue("ChildrenCount", i, getChildrenCount(i));
		}
	}

	private int getChildrenCount(int row) {
		String roiName = lineageTable.getStringValue("Name", row);

		int slice = (int) lineageTable.getValue("Slice", row);

		int childrenCount = 0;
		int nextSlice = slice + 1;

		for (int count = 0; count < lineageTable.getCounter(); count++) {

			if (lineageTable.getValue("Slice", count) == nextSlice) {
				if (lineageTable.getStringValue("ParentName", count)
						.equalsIgnoreCase(roiName)) {
					childrenCount++;
				}
			}
		}

		return childrenCount;
	}

	private ArrayList<RoiManager> individualROIManagers(
			ArrayList<Roi> listOfRois) {

		ArrayList<RoiManager> arrList = new ArrayList<RoiManager>();
		for (Roi roi : listOfRois) {
			RoiManager rm = new RoiManager(true);
			rm.addRoi(roi);
			arrList.add(rm);
		}

		return arrList;
	}

	private ArrayList<Roi> getROIsOnLastSlice() {
		return getROIsOnSlice(imp.getStackSize());
	}

	private ArrayList<Roi> getROIsOnSlice(int stackPosition) {
		ArrayList<Roi> currentList = new ArrayList<Roi>();
		int managerCount = manager.getCount();
		Roi roi;

		for (int i = 0; i < managerCount; i++) {
			roi = manager.getRoi(i);
			if (roi.getPosition() == stackPosition) {
				currentList.add(roi);
			}
		}

		return currentList;
	}

	private static int getROICount() {
		return manager.getCount();
	}

	private void setRefereceImp(ImagePlus imp) {
		this.imp = imp;
	}

	private void saveTable() {
		if (lineageTable == null) {
			this.lineageTable = new ResultsTable();
			LogPanel.log("Table is empty");
		}
		lineageTable.save(OUTPUT_FOLDER + File.separator
				+ "CellMeasurements.csv");
	}

	private void createLineageTable() {

		lineageTable = new ResultsTable();

		Roi roi;

		for (int i = 0; i < manager.getCount(); i++) {
			lineageTable.incrementCounter();
			roi = manager.getRoi(i);
			lineageTable.addValue("Name", roi.getName());
			lineageTable.addValue("Slice", roi.getPosition());
			Roi parent = getParent(roi, manager);
			if (parent == null) {
				lineageTable.addValue("Parent", 0);
				lineageTable.addValue("ParentName", 0);
			} else {
				lineageTable.addValue("Parent", parent.getPosition());
				lineageTable.addValue("ParentName", parent.getName());
			}
		}
	}

	private static Roi getParent(Roi roi, RoiManager manager) {
		if (roi.getPosition() == 1) {
			return null;
		}
		// get Rois in the Previous slide in reference to that particular Roi
		RoiManager subManager = getSubManager(manager, roi.getPosition() - 1);
		// check ROI most likelly to be the parent ROI

		return biggestROIOverlap(roi, subManager);

	}

	private static Roi biggestROIOverlap(Roi roi, RoiManager subManager) {
		Roi intersectROI = null;

		double area = 0;
		for (int i = 0; i < subManager.getCount(); i++) {
			Roi otherRoi = subManager.getRoi(i);

			// getOverlap Area
			if (calculateOverlapArea2(roi, otherRoi) > area) {
				intersectROI = otherRoi;
			}
			;
		}

		return intersectROI;
	}

	private static double calculateOverlapArea2(Roi roi, Roi otherRoi) {
		// check if the ROis touch at all
		double area = 0;
		// This way to usemore IJ functions.
		if (roi.getBounds().intersects(otherRoi.getBounds())) {

			int width = getLargerROIWidth(roi, otherRoi);
			int height = getLargerROIHeight(roi, otherRoi);

			ImagePlus imp = IJ.createImage("123", "8-bit white", width, height,
					1);

			RoiManager rm = new RoiManager(true);
			rm.addRoi(otherRoi);
			rm.addRoi(roi);
			rm.setSelectedIndexes(new int[] { 1, 2 });
			rm.runCommand(imp, "AND");
			rm.select(3);
			rm.getRoi(3);
			rm.runCommand(imp, "Measure");
			ResultsTable table = Analyzer.getResultsTable();
			area = table.getValue("Area", 0);

		}
		return area;
	}

	private static int getLargerROIHeight(Roi roi, Roi otherRoi) {
		int height = roi.getBounds().height;
		if (otherRoi.getBounds().height > height) {
			height = otherRoi.getBounds().height;
		}
		return height;
	}

	private static int getLargerROIWidth(Roi roi, Roi otherRoi) {
		int width = roi.getBounds().width;
		if (otherRoi.getBounds().width > width) {
			width = otherRoi.getBounds().width;
		}
		return width;
	}

	public static RoiManager getSubManager(RoiManager manager, int stackPosition) {
		Roi roi;
		RoiManager subManager = new RoiManager(true);
		for (int i = 0; i < manager.getCount(); i++) {
			roi = manager.getRoi(i);
			if (roi.getPosition() == stackPosition) {
				subManager.addRoi(roi);
			}
		}
		return subManager;
	}

}
