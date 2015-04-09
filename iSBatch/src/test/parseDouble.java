package test;

public class parseDouble {

	public static void main(String[] args) {
		String str = "1";
		Float number = parseDouble(str);// TODO Auto-generated method stub
		System.out.println(number);
	}

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
