/*
 * 
 */
package test;

// TODO: Auto-generated Javadoc
/**
 * The Class parseDouble.
 */
public class parseDouble {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		String str = "1";
		Float number = parseDouble(str);// TODO Auto-generated method stub
		System.out.println(number);
	}

	/**
	 * Parses the double.
	 *
	 * @param str the str
	 * @return the float
	 * @throws NumberFormatException the number format exception
	 */
	private static Float parseDouble(String str) throws NumberFormatException{
		Float toRetun = null ;
		try{
			toRetun = Float.parseFloat(str); 
			System.out.println("Value parsed :"+toRetun);
		}catch(NumberFormatException ex){
			System.err.println("Ilegal input");
			// Discard input or request new input ...
			// clean up if necessary
		}
		return toRetun;	
	}

}
