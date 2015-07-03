package test;

import ij.plugin.frame.RoiManager;

/**
 * @author Victor Caldas
 * Class to handle hierarchical relations between ROI.
 * 
 */
public class Lineage {

	private RoiManager manager;
	
	/**
	 * @param manager RoiManager 
	 */
	public Lineage(RoiManager manager) {
		this.manager = manager;
	}
	
	/**
	 * Main function for testing purposes.
	 * @param args no args
	 */
	public static void main(String[] args) {
		RoiManager manager = new RoiManager(true);
		manager.runCommand("Open", "/home/vcaldas/ISBatchTutorial/MinimalDataset/TutorialDB_files/TimeLapse/DnaX_DnaX-M9Glycerol/001/cellRoi.zip");

		System.out.println("Roi manager contains " + manager.getCount() + " rois.");
		
		
	}
	

}
