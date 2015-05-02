/**
 * <p>
 *This class can be used to read sql files into an array of Strings, each
 * representing a single query terminated by ";"
 * Comments are filtered out..
 * </p>
 *
 * @since 1.0
 * @author Victor Caldas
 * @version 1.0
 */

package iSBatch.src.utils;

import java.io.File;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


// TODO: Auto-generated Javadoc
/*
 * ATTENTION: SQL file must not contain column names, etc. including comment 
 * signs (#, --, /* etc.) like e.g. a.'#rows' etc. because every characters 
 * after # or -- in a line are filtered out of the query string the same is 
 * true for every characters surrounded by /* and */
/**/

/**
 * The Class SQLReader.
 */
public class SQLReader {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String[] args) {
		File test = new File("src//model//template");
		System.out.println(test.exists());
		SQLReader reader = new SQLReader();
		listOfQueries = reader.createQueries(test.getAbsolutePath());

		for (String string : listOfQueries) {
			System.out.println(string);

			if (string.contains("CREATE TABLE")) {
				System.out.println("Detect Table");
			}
		}

	}

	/** The list of queries. */
	private static ArrayList<String> listOfQueries = null;

	/*
	 * @param path Path to the SQL file
	 * 
	 * @return List of query strings
	 */
	/**
	 * Creates the queries.
	 *
	 * @param path Path to the file
	 * @return the ArrayList of SQL Commands
	 */
	public ArrayList<String> createQueries(String path) {
		String queryLine = new String();
		StringBuffer sBuffer = new StringBuffer();
		listOfQueries = new ArrayList<String>();

		try {
			FileReader fr = new FileReader(new File(path));
			BufferedReader br = new BufferedReader(fr);

			// read the SQL file line by line
			while ((queryLine = br.readLine()) != null) {
				// ignore comments beginning with #
				int indexOfCommentSign = queryLine.indexOf('#');
				if (indexOfCommentSign != -1) {
					if (queryLine.startsWith("#")) {
						queryLine = new String("");
					} else
						queryLine = new String(queryLine.substring(0,
								indexOfCommentSign - 1));
				}
				// ignore comments beginning with --
				indexOfCommentSign = queryLine.indexOf("--");
				if (indexOfCommentSign != -1) {
					if (queryLine.startsWith("--")) {
						queryLine = new String("");
					} else
						queryLine = new String(queryLine.substring(0,
								indexOfCommentSign - 1));
				}
				// ignore comments surrounded by /* */
				indexOfCommentSign = queryLine.indexOf("/*");
				if (indexOfCommentSign != -1) {
					if (queryLine.startsWith("#")) {
						queryLine = new String("");
					} else
						queryLine = new String(queryLine.substring(0,
								indexOfCommentSign - 1));

					sBuffer.append(queryLine + " ");
					// ignore all characters within the comment
					do {
						queryLine = br.readLine();
					} while (queryLine != null && !queryLine.contains("*/"));
					indexOfCommentSign = queryLine.indexOf("*/");
					if (indexOfCommentSign != -1) {
						if (queryLine.endsWith("*/")) {
							queryLine = new String("");
						} else {
							queryLine = new String(queryLine.substring(
									indexOfCommentSign + 2,
									queryLine.length() - 1));
						}
					}
				}

				// the + " " is necessary, because otherwise the content before
				// and after a line break are concatenated
				// like e.g. a.xyz FROM becomes a.xyzFROM otherwise and can not
				// be executed
				if (queryLine != null) {
					sBuffer.append(queryLine + " ");
				}
			}
			br.close();

			// here is our splitter ! We use ";" as a delimiter for each request
			String[] splittedQueries = sBuffer.toString().split(";");

			// filter out empty statements
			for (int i = 0; i < splittedQueries.length; i++) {
				if (!splittedQueries[i].trim().equals("")
						&& !splittedQueries[i].trim().equals("\t")) {
					String inputString = splittedQueries[i];
					// remove spurious stuff
					inputString = cleanString(inputString);
					

					listOfQueries.add(new String(inputString));
				}
			}
		} catch (Exception e) {
			System.out.println("*** Error : " + e.toString());
			System.out.println("*** ");
			System.out.println("*** Error : ");
			e.printStackTrace();
			System.out
					.println("##############################################");
			System.out.println(sBuffer.toString());
		}
		return listOfQueries;
	}

	/**
	 * Clean string.
	 *
	 * @param inputString String with 
	 * @return Clean String
	 */
	private String cleanString(final String inputString) {
		String string = inputString;
		string = string.replaceAll("\t", "");
		string = string.replaceAll("\n", "");
		string = string.replaceAll("  ", "");
		return string;
	}
}