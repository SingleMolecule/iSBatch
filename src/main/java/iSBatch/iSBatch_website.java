package iSBatch;

import ij.IJ;
import ij.plugin.BrowserLauncher;
import ij.plugin.PlugIn;

public class iSBatch_website implements PlugIn {

	public void run(String arg) {

		try {
			BrowserLauncher.openURL("http://singlemolecule.github.io/iSBatch/");
		} catch (Throwable e) {
			error("Could not open default internet browser");
		}
	}

	static void error(final String message) {

		IJ.showMessage(name() + ": Error", message + ".");
		IJ.showProgress(1);
		IJ.showStatus("");
	}
	
	public static String name() {
		
		return "iSBatch";
	}
	

}
