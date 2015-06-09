package test;

import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

public class RoiOperations {
	public static void main(String[] args) {
		RoiManager manager = new RoiManager(true);
		manager.runCommand("Open", "D:\\ImageTest\\cellRoi3.zip");

		ImagePlus imp2 = IJ.openImage("D:\\ImageTest\\514_flat-1.tif");
		ImagePlus imp = IJ.createImage("Template", "8-bit white",
				imp2.getWidth(), imp2.getHeight(), 1);

		// int size = manager.getCount();
		System.out.println("Contains " + manager.getCount() + " rois");

		int[] selection = getSelection(manager.getCount());

		// manager.setSelectedIndexes(selection);
		IJ.run(imp, "Select All", "");
		manager.runCommand(imp, "Combine"); // combine all ROis in one
		manager.addRoi(imp.getRoi());

		// Remove original Rois
		manager.setSelectedIndexes(selection);
		manager.runCommand(imp, "Delete");

		// Enlarge ROIS

		manager.select(0);
		IJ.run(imp, "Enlarge...", "enlarge=3");
		manager.addRoi(imp.getRoi());
		// The ROI manager now must have 2 rois.

		manager.setSelectedIndexes(new int[] { 0, 1 });
		manager.runCommand(imp, "XOR");
		manager.addRoi(imp.getRoi());

		manager.select(0);
		manager.runCommand("Rename", "AllCels");
		manager.select(1);
		manager.runCommand("Rename", "Expand");
		manager.select(2);
		manager.runCommand("Rename", "Band");

		IJ.saveAsTiff(imp, "D:\\ImageTest\\514_flatteste.tif");
		manager.runCommand("Save", "D:\\ImageTest\\cellRoi2.zip");
		imp.flush();
	}

	public static int[] convertIntegers(ArrayList<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

	public static int[] getSelection(int n) {
		ArrayList<Integer> integers = new ArrayList<Integer>();
		for (int i = 0; i < n; i++) {
			integers.add(i);
		}
		return convertIntegers(integers);
	}
}
