/*
 * 
 */
package test;

// TODO: Auto-generated Javadoc
/**
 * The Class SplitTest.
 */
public class SplitTest {

/**
 * The main method.
 *
 * @param args the arguments
 */
public static void main(String[] args) {
	
	
	String string = "[Bright Field]BF_raw.tif";
	
	String[] array = string.split("_|\\."); 
	for(int i=0; i<array.length; i++){
		System.out.println(array[i]);
	}
}
}
