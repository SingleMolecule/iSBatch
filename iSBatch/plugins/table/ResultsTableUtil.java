package table;

import java.awt.Frame;

import java.util.Arrays;

import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.text.TextPanel;
import ij.text.TextWindow;

public class ResultsTableUtil {

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
	
	public static ResultsTable getResultsTable(String title) {
		
		Frame frame = WindowManager.getFrame(title);
		
		if (frame instanceof TextWindow) {
			
			TextWindow textWindow = (TextWindow)frame;
			TextPanel textPanel = textWindow.getTextPanel();
			return textPanel.getResultsTable();
			
		}
		
		return null;
	}
	
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
