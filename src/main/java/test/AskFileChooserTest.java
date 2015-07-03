/*
 * 
 */
package test;

import java.io.File;

// TODO: Auto-generated Javadoc
/**
 * The Class AskFileChooserTest.
 */
public class AskFileChooserTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		File directory = new File("C:\\Users\\VictorCaldas\\Documents\\Thesis\\Chapter 02 - iSBToolsPaper");
		File file = new File("C:\\Users\\VictorCaldas\\Documents\\Thesis\\Chapter 02 - iSBToolsPaper\\26-Lamprecht_Biotech_2007.pdf");

		
		System.out.println(directory.getAbsolutePath());
		if(directory.isDirectory()){
			System.out.println("Directory");
		}
		
		System.out.println(file.getAbsolutePath());
		if(file.isDirectory()){
			System.out.println("Directory");
		}
		else if (file.isFile()){
			System.out.println("this is a file");
		}
		
		String str = file.getName();
	
		
		System.out.println(str.substring(0, str.lastIndexOf('.')));
		str = str.substring(0, str.lastIndexOf('.'));
		
		System.out.println(str.substring(str.lastIndexOf('_') + 1));
		
	}

}
