/*
 * 
 */
package table;

import java.awt.Frame;

import java.util.Arrays;

import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.text.TextPanel;
import ij.text.TextWindow;

// TODO: Auto-generated Javadoc
/**
 * The Class ResultsTableUtil.
 */
public class ResultsTableUtil {

	/**
	 * Gets the results table titles.
	 *
	 * @return the results table titles
	 */
	public static String[] getResultsTableTitles() {
		
		Frame[] nonImageWindows = WindowManager.getNonImageWindows();
		String[] titles = new String[nonImageWindows.length];
		
		int n = 0;
		
		for (Frame frame: nonImageWindows) {
			
			if (frame instanceof TextWindow) {
				
				TextWindow textWindow = (TextWindow)frame;
				TextPanel textPanel = textWindow.getTextPanel();
				ResultsTable table = textPanel.getResultsTable();
				
				if (table != null)
					titles[n++] = frame.getTitle();
			}
			
		}
		
		return Arrays.copyOf(titles, n);
	}
	
	/**
	 * Gets the results table.
	 *
	 * @param title the title
	 * @return the results table
	 */
	public static ResultsTable getResultsTable(String title) {
		
		Frame frame = WindowManager.getFrame(title);
		
		if (frame instanceof TextWindow) {
			
			TextWindow textWindow = (TextWindow)frame;
			TextPanel textPanel = textWindow.getTextPanel();
			return textPanel.getResultsTable();
			
		}
		
		return null;
	}
	
	/**
	 * Delete.
	 *
	 * @param table the table
	 * @param rows the rows
	 */
	public static void delete(ResultsTable table, int[] rows) {
		
		if (rows.length == 0)
			return;
		
		ResultsTableList list = new ResultsTableList(table);
		
		int to = rows[rows.length - 1] + 1;
		
		for (int i = rows.length - 1; i > 0; i--) {
			
			if (rows[i] != rows[i - 1] + 1) {
				list.removeRange(rows[i], to);
				to = rows[i - 1] + 1;
			}
			
		}
		
		list.removeRange(rows[0], to);
	}
	
}
