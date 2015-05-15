/*
 * 
 */
import ij.IJ;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

// TODO: Auto-generated Javadoc
/**
 * The Class CombineExcelFile.
 */
public class CombineExcelFile extends JDialog{
	
	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;





	/**
	 * Instantiates a new combine excel file.
	 */
	public CombineExcelFile() {

		
	}
	
	/** The folder. */
	static File fFolder;
	
	/** The Data summary. */
	static String[][] DataSummary; 
	
	/** The writer. */
	static CSVWriter writer;
	
	/** The data. */
	static String[] data = new String[5];
	
	/** The txt bgintensityavgcsv. */
	private static JTextField txtBgintensityavgcsv;
	
	/** The txt cellintensitybguncorravgcsv. */
	private static JTextField txtCellintensitybguncorravgcsv;
	
	/** The txt resultspeakfitpercelltxt. */
	private static JTextField txtResultspeakfitpercelltxt;
	
	/** The text field_3. */
	private JTextField textField_3;
	
	/** The txt output. */
	private JTextField txtOutput;
	
	
	
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		CombineExcelFile cada = new CombineExcelFile();
		cada.display();
		
		
		
	

	}
	
	/**
	 * Display.
	 */
	private  void display() {
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JButton btnNewButton = new JButton("Folder");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fFolder = new File(IJ.getDirectory("Indicate directory"));
				textField_3.setText(fFolder.getAbsolutePath());
			}
		});
		getContentPane().add(btnNewButton, "4, 4");
		
		textField_3 = new JTextField();
		getContentPane().add(textField_3, "8, 4, 13, 1, fill, default");
		textField_3.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("BG Intensity");
		getContentPane().add(lblNewLabel, "4, 6");
		
		txtBgintensityavgcsv = new JTextField();
		txtBgintensityavgcsv.setText("BGintensity[avg].csv");
		getContentPane().add(txtBgintensityavgcsv, "8, 6, 13, 1, fill, default");
		txtBgintensityavgcsv.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Cell Intensity");
		getContentPane().add(lblNewLabel_1, "4, 8");
		
		txtCellintensitybguncorravgcsv = new JTextField();
		txtCellintensitybguncorravgcsv.setText("Cellintensity[bguncorr][avg].csv");
		getContentPane().add(txtCellintensitybguncorravgcsv, "8, 8, 13, 1, fill, default");
		txtCellintensitybguncorravgcsv.setColumns(10);
		
		JButton btnNewButton_3 = new JButton("Proceed");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					run();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			
		});
		
		JLabel lblNewLabel_2 = new JLabel("Peaks");
		getContentPane().add(lblNewLabel_2, "4, 10");
		
		txtResultspeakfitpercelltxt = new JTextField();
		txtResultspeakfitpercelltxt.setText("Results[PeakFit][5][perCell].txt");
		getContentPane().add(txtResultspeakfitpercelltxt, "8, 10, 13, 1, fill, default");
		txtResultspeakfitpercelltxt.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("Output");
		getContentPane().add(lblNewLabel_3, "4, 12");
		
		txtOutput = new JTextField();
		txtOutput.setText("Statistics.csv");
		getContentPane().add(txtOutput, "8, 12, fill, default");
		txtOutput.setColumns(10);
		getContentPane().add(btnNewButton_3, "22, 14");
		
		pack();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Run.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void run() throws IOException {
		File[] directories = fFolder.listFiles(File::isDirectory);
		
		File output = new File(fFolder + File.separator + txtOutput.getText());
		output.delete();
		
		try {
			writer = new CSVWriter(new FileWriter(output), ',');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//Stupid but will work
		//Create a 2D matrix to Store the results
		
		//First, I have to calculate how many cells 
		/*
		 * Count Cells.
		 * 
		 */
		System.out.println(fFolder.getAbsolutePath());
		int nCells = countCells(fFolder);
		DataSummary = new String[5][nCells];
		
		//Define Headers
		
		DataSummary[0][0] = "Cell"; 
		DataSummary[1][0] = "BgMean";
		DataSummary[2][0] = "Intensity";
		DataSummary[3][0] = "Corrected";
		DataSummary[4][0] = "nPeaks";
	
		//Fill Lines of the Data Summary
		
		//For every folder
		
		List<File> BGintensityFiles = getExcelFiles(txtBgintensityavgcsv.getText());
		ArrayList<Double> backgroundAverages = getAverageBG(BGintensityFiles);
		System.out.println("Average Values for Background acquired.");

		// get list of excell files to combine. Not read yet.
		List<File> CellIntensityFiles = getExcelFiles(txtCellintensitybguncorravgcsv.getText());

		List<File> cellPeakFiles = getExcelFiles(txtResultspeakfitpercelltxt.getText());
		
		data[0] = "cellName";
		data[1] = "Intensity;";
		data[2] = "num of peaks";
		data[3] = "background";
		data[4] = "corrected";
		 
			
		writer.writeNext(data);
		
		for (int i = 0; i < directories.length; i++) {
			System.out.println("Processing " + directories[i].getPath());
			System.out.println("Background value: " + backgroundAverages.get(i));
			//Get list of cells names and average intensities
			getCellData(CellIntensityFiles.get(i), cellPeakFiles.get(i), backgroundAverages.get(i));
			
		}

		for (int i = 0; i < DataSummary[0].length; i++) {
			// System.out.println(DataSummary[0][i] + " | " + DataSummary[1][i]
			// + " | " + DataSummary[2][i] + " | "+ DataSummary[3][i] + " | " +
			// DataSummary[4][i]);

		}
		writer.close();

		
	}
	
	
	

	/**
	 * Gets the cell data.
	 *
	 * @param CellIntensity the cell intensity
	 * @param CellPeak the cell peak
	 * @param backGround the back ground
	 * @return the cell data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static List<String[]> getCellData(File CellIntensity, File CellPeak, Double backGround) throws IOException {
		List<String[]> currentFolderData = new ArrayList<String[]>();
		String[][] cellData = null;
		String[][] peakata = null;
		
		//Transform files in tables
		if( CellIntensity.getName().endsWith("csv")){
			cellData = convertToArray(CellIntensity, "");
			
		}
		if( CellIntensity.getName().endsWith("txt")){
			cellData = convertToArray(CellIntensity, "\t");
			
		}
		if( CellPeak.getName().endsWith("csv")){
			peakata = convertToArray(CellIntensity, "");
			
		}
		if( CellPeak.getName().endsWith("txt")){
			peakata = convertToArray(CellIntensity, "\t");
			
		}
		
	
		
		//Constructing Array to be added
		
		for (int j = 1; j<cellData[0].length; j++){
			//The first collum does not contain anything
			data[0] = removeCrapString(cellData[0][j]);
			data[1] = cellData[1][j];
			int collum = getCollum(data[0],peakata[0]);
			data[2] = Integer.toString(countPeaks(peakata, collum));
			data[3] = String.valueOf(backGround);
			data[4] = String.valueOf(Double.parseDouble(cellData[1][j])-backGround);
			
			System.out.println(data[0] + " : " + data[1] + " : " + data[2] + " : " + data[3]+ " : " + data[4]);
			
			writer.writeNext(data);
			
		}
		
		
		
		
		
		
		
		
		
		
		return currentFolderData;
	}

	/**
	 * Count cells.
	 *
	 * @param folder the folder
	 * @return the int
	 */
	private static int countCells(File folder) {
		String RoiFolder = "BFcellROI";
		int totalCells = 0;

		File[] list = fFolder.listFiles();

		for (File file : list) {
			if (file.isDirectory()) {
				File subDir = new File(file.getAbsolutePath() + File.separator
						+ RoiFolder);
				File[] listCells = subDir.listFiles();
				totalCells = totalCells + listCells.length;
			}
		}
		return totalCells;
	}

	/**
	 * Count peaks.
	 *
	 * @param peakata the peakata
	 * @param collum the collum
	 * @return the int
	 */
	private static int countPeaks(String[][] peakata, int collum) {
		int totalPeaks = 0;
		
		for(int i = 1; i<peakata.length; i++){
			totalPeaks =totalPeaks +  Integer.parseInt(peakata[i][collum]);
		}
		return totalPeaks;
	}

	/**
	 * Gets the collum.
	 *
	 * @param toMatch the to match
	 * @param header the header
	 * @return the collum
	 */
	private static int getCollum(String toMatch, String[] header) {
		int collum = 0;
		for (int i = 0; i < header.length; i++) {
			if (header[i].equalsIgnoreCase(toMatch)) {
				collum = i;
			}
		}
		return collum;
	}

	/**
	 * Removes the crap string.
	 *
	 * @param string the string
	 * @return the string
	 */
	private static String removeCrapString(String string) {
		string = string.replace("Mean(", "");
		string =string.replace(")", "");
		return string;
	}


	/**
	 * Convert to array.
	 *
	 * @param file the file
	 * @param string the string
	 * @return the string[][]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("resource")
	private static String[][] convertToArray(File file, String string) throws IOException {
		CSVReader reader;
		
		if(string.equalsIgnoreCase("\t")) {
			reader = new CSVReader(new FileReader(file), '\t');
		}
		else{
			reader = new CSVReader(new FileReader(file));
		}
			List<String[]> list = reader.readAll();
		
		// Convert to 2D array
		String[][] cellinformation = new String[list.size()][];
		cellinformation = list.toArray(cellinformation);
		//Now I have all the data from a cell Intensity file. 
		
		return cellinformation;
	}
	
	/**
	 * Gets the average bg.
	 *
	 * @param Files the files
	 * @return the average bg
	 */
	private static ArrayList<Double> getAverageBG(List<File> Files) {
		// Get list of Values

		ArrayList<Double> values = new ArrayList<Double>();

		// Loop through all files
		for (File file : Files) {
			// Get a list with all values from that CSV file
			ArrayList<Double> current = getListOfValues(file);
			Double average = average(current);
			values.add(average);
		}

		return values;

	}

	/**
	 * Average.
	 *
	 * @param list the list
	 * @return the double
	 */
	public static double average(ArrayList<Double> list) {  
	    double average = sum(list)/list.size();
	    return average;
	}

	/**
	 * Sum.
	 *
	 * @param list the list
	 * @return the double
	 */
	public static double sum(ArrayList<Double> list) {
	    double sum = 0;        
	    for(int i=0; i<list.size(); i++ ){
	        sum = sum + list.get(i) ;
	    }
	    return sum;
	}
	
	/**
	 * Variance.
	 *
	 * @param list the list
	 * @return the double
	 */
	public static double variance(ArrayList<Double> list) {
		   double sumDiffsSquared = 0.0;
		   double avg = average(list);
		   for (double value : list)
		   {
		       double diff = value - avg;
		       diff *= diff;
		       sumDiffsSquared += diff;
		   }
		   return sumDiffsSquared  / (list.size()-1);
		}

	/**
	 * Gets the list of values.
	 *
	 * @param file the file
	 * @return the list of values
	 */
	private static ArrayList<Double> getListOfValues(File file) {
		// Get list of Values

		ArrayList<Double> values = new ArrayList<Double>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	 
		try {
	 
			br = new BufferedReader(new FileReader(file));
			
			//Read ignoring headers
			while ((line = br.readLine()) != null) {
				  while ((line = br.readLine()) != null) {
					  String[] country = line.split(cvsSplitBy);
					  
					  //convert Array to list
					  
					  for (String string : country){
						  values.add(Double.parseDouble(string));
					  }
				  }
				}
			   
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		
		values.remove(0);
		
		
		return values;
	}

	/**
	 * Gets the excel files.
	 *
	 * @param string the string
	 * @return the excel files
	 */
	private static List<File> getExcelFiles(String string) {
		// LIst containing the directories
		File[] list = fFolder.listFiles();
		List<File> myList = new ArrayList<File>();

		// Loop in every directory

		for (File directory : list) {
			if (directory.isDirectory()) {
				File[] filesInDirectory = directory.listFiles();

				for (File file : filesInDirectory) {

					if (file.getName().equalsIgnoreCase(string)) {
						// System.out.println(file.getName());
						// System.out.println(string);

						myList.add(file);
					}

				}

			}

		}

		return myList;
	}

}
