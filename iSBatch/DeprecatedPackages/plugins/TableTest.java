package plugins;

import ij.measure.ResultsTable;

import java.io.IOException;

import pluginsIJ.Detect_Peaks_iSB;

public class TableTest {

	public static void main(String[] args) throws IOException{
		String tableFilename = "D:\\20140521-UmuC\\Stats\\568.Peaks.LocalizedFiltered[500].txt";
		
		
		
		ResultsTable PeakData = ResultsTable.open(tableFilename);
		ResultsTable table2 = new ResultsTable();
		
		int last = PeakData.getLastColumn();
		
		String[] labels = PeakData.getHeadings();
		for(String  label : labels){
			
				System.out.println(label);
			
		}
		
				
		for (int row = 1; row< PeakData.getCounter() ; row++) {
			
			int cell = (int) PeakData.getValue("cell", row);
			System.out.println(row);
			if (cell == 0){
				
				for(String  label : labels){
					String value = PeakData.getStringValue(label, row);
					table2.setValue(label, row, value);
				
			}
				
				
				
				
			}
			
			
			
			
			
				
			}
			
			
			
			
		System.out.println("---------Done!-------------");
		
		java.awt.Toolkit.getDefaultToolkit().beep();
			
			
			
		}
		
		
		
		
	}
	
	
	
	
	
	

