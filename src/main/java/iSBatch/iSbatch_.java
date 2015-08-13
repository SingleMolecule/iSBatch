package iSBatch;

import ij.IJ;
import ij.plugin.PlugIn;

public class iSbatch_ implements PlugIn {

	public static void main(final String... args) {
		new ij.ImageJ();
		new iSbatch_().run("");

	}

	@Override
	public void run(String arg0) {
		
		IJ.showMessage("This is a fresh start");
		
	
		
	}

}
