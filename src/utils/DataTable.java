package utils;

import ij.measure.ResultsTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

public class DataTable {
	private CSVReader reader;
	private String[] headers;
	private int cols = 0, rows = 0;

	public static void main(String[] args) throws FileNotFoundException {
		File f = new File("D:\\ImageTest\\Results2.csv");
		if (!f.exists()) {
			System.out.println("Fix path");
		}

		DataTable table = new DataTable(f);
		table.getHeader();
		System.out.println();
		for (String string : table.getHeader()) {
			System.out.println(string);
		}

		ResultsTable table3 = table.getResultsTable();
		table3.save("D:\\ImageTest\\Output.csv");
	}

	public DataTable(String path) {
		try {
			this.reader = new CSVReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DataTable(File file) {
		try {
			this.reader = new CSVReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String[] getHeader() {
		if (headers == null) {
			try {
				this.headers = reader.readNext();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.headers;
	}

	public int getHeaderIndex(String columName) {
		String[] headers = getHeader();

		for (int i = 0; i < headers.length; i++) {
			if (columName.equalsIgnoreCase(headers[i])) {
				return i;
			}
		}
		return 0;
	}

	public ResultsTable getResultsTable() {
		String[] nextLine;
		ResultsTable table = new ResultsTable();
		try {
			while ((nextLine = reader.readNext()) != null) {
				for (String string : nextLine) {

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String[][] getTableMatrix() throws IOException {

		String[][] matrix = new String[getNumberOfRows()][getNumberOfCols()];
		// add header
		for (int i = 0; i < this.cols; i++) {
			matrix[i][0] = headers[i];
		}

		for (int j = 1; j < cols; j++) {
			String[] currentRow = reader.readNext();
			for (int i = 0; i < this.cols; i++) {
				matrix[i][j] = currentRow[i];
			}
		}
		return matrix;
	}

	public int getNumberOfRows() {
		if (rows == 0) {
			int counter = 0;
			try {
				while ((reader.readNext()) != null) {
					counter++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.rows = counter;
		}
		return this.rows;
	}

	public int getNumberOfCols() {
		return getHeader().length;
	}
}
