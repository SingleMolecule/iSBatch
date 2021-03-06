/*
 * 
 */
package table;

import java.io.File;
import java.io.IOException;

import ij.IJ;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;

// TODO: Auto-generated Javadoc
/**
 * The Class ResultsTableOpener.
 */
public class ResultsTableOpener implements PlugIn {

	/**
	 * Run.
	 *
	 * @param arg0 the arg0
	 */
	@Override
	public void run(String arg0) {
		
		try {
			
			String path = IJ.getFilePath("Open Results Table...");
			
			if (path == null)
				return;
			
			ResultsTable results = ResultsTable.open(path);
			String title = new File(path).getName();
			
			new ResultsTableView(results, title);
			
		} catch (IOException e) {
			IJ.log(e.getMessage());
		}

	}

}
