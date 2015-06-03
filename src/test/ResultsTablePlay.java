package test;

import gui.LogPanel;
import ij.IJ;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;

public class ResultsTablePlay {
public static void main(String[] args) {
	
	LogPanel.log("There are " + WindowManager.getWindowCount()
			+ " windows open.");
	String pluginName = "Particle Tracker";
	String arguments = "";
	double minDistance = 2.0;
	int[] IDS = WindowManager.getIDList();

	IJ.open("D:\\ImageTest\\green.csv");

	ResultsTable green = Analyzer.getResultsTable();
	green.open2("D:\\ImageTest\\green.csv");

	System.out.println(green.getCounter());
	
	for (int i = 0; i < green.getCounter(); i++) {
		System.out.println(green.getValue("slice", i));
//		double x = green.getValue("x", i);
//		double y = green.getValue("y", i);
//		double slice = green.getValue("slice", i);
//		operations.microbeTrackerIO.Point p = new operations.microbeTrackerIO.Point(
//				x, y);
//
//		for (int j = 0; j < red.getCounter(); j++) {
//			double dslice = red.getValue("slice", j);
//
//			if (dslice > slice) {
//
//				break;
//			}
//			if (dslice == slice) {
//				double dx = red.getValue("x", j);
//				double dy = red.getValue("y", j);
//				operations.microbeTrackerIO.Point p2 = new operations.microbeTrackerIO.Point(
//						x, y);
//
//				double distance = p.distanceTo(p2);
//				if (distance <= minDistance) {
//					green.setValue("distance", i, distance);
//					green.setValue("x2", i, dx);
//					green.setValue("y2", i, dy);
//				} else {
//
//					green.setValue("distance", i, -1);
//					green.setValue("x2", i, 0);
//					green.setValue("y2", i, 0);
//				}
//			}
//		}
	}

	green.save("D:\\ImageTest\\greenColoS.csv");

}

}
