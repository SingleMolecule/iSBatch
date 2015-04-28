package test;

public class SplitTest {
public static void main(String[] args) {
	
	
	String string = "[Bright Field]BF_raw.tif";
	
	String[] array = string.split("_|\\."); 
	for(int i=0; i<array.length; i++){
		System.out.println(array[i]);
	}
}
}
