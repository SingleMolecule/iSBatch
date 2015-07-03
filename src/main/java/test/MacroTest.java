package test;

import ij.IJ;
import ij.plugin.frame.RoiManager;

public class MacroTest {

	public static void main(String[] args) {
		
		RoiManager roiManager = new RoiManager(true);
		
		String macro = "newImage(\"Untitled\", \"16-bit black\", 400, 400, 1);";
			macro += "makeRectangle(76,25,55,43);";
			macro += "roiManager(\"Add\");";

		IJ.runMacro(macro);
		
		System.out.println(RoiManager.getInstance2());
	}

}
