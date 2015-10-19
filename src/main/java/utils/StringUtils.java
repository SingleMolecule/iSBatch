package utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	private boolean verbose = false;
	
	public StringUtils(boolean verbosive){
		System.out.println("Set verbose to: " + verbosive);
		this.verbose = verbosive;
	}
	

	public String flankString(String string) {
		return "[" + string + "]";
	}

	public String flankString(String string, String flankingString) {
		return flankingString + string + flankingString;
	}

	public String flankString(String string, String frontFlankString,
			String backFlankString) {
		return frontFlankString + string + backFlankString;
	}

	public String flankAndTag(String string, String tag) {
		return flankString(string) + tag;
	}

	public boolean checkStackAssignment(String currentStacklabel) {

		// System.out.println("Checking Stack assignment in " +
		// currentStacklabel);
		// System.out.println("This will return " +
		// getStackAssigment(currentStacklabel));
		if (getStackAssigment(currentStacklabel).equalsIgnoreCase("")
				|| getStackAssigment(currentStacklabel) == null) {

//			System.out.println("Return false");
			return false;
		}
//		System.out.println("Return true "
//				+ getStackAssigment(currentStacklabel));
		return true;
	}

	public String getStackAssigment(String currentStacklabel) {
		String stackAssignmentPattern = "(S\\(\\d{1,15}\\|\\d{1,15}\\))";
		Pattern MY_PATTERN = Pattern.compile(stackAssignmentPattern);
		Matcher matcher = MY_PATTERN.matcher(currentStacklabel);

		String matching = "";

		while (matcher.find()) {
			matching = matcher.group(1);
			if(this.verbose){
				System.out.println("Match stack assigment " + matching);
			}
			
		} 
		return matching;
	}

	public  int getCurrentStackfromAssigment(String currentStacklabel) {
		String stackCounter = getStackAssigment(currentStacklabel);
		stackCounter = stackCounter.replace("S(", "");
		stackCounter = stackCounter.replace(")", "");
		
		String[] parts = stackCounter.split("\\|");
		System.out.println(currentStacklabel);
		return Integer.parseInt(parts[0]);
	}

	public  int getStackSizefromAssigment(String currentStacklabel) {
		int i = 0;
		return i;
	}

	public  String removeStackAssigment(String currentStacklabel) {
		String assigment = getStackAssigment(currentStacklabel);
		currentStacklabel = currentStacklabel.replace(assigment, "");
		return currentStacklabel = currentStacklabel.replace("|", "");
	}

	public static void main(String[] args) {
		String[] str = new String[8];
		str[0] = "S(1|23)";
		str[1] = "S(14|23)";
		str[2] = "S(123|233)";
		str[3] = "S(4|3)";
		str[4] = "S(1|23232323232)";
		str[5] = "x(4|23232323232)";
		str[6] = "s(1|23232323232)";
		str[7] = "Sstack(1|23232323232)";

		StringUtils utils = new StringUtils(false);
		for (int i = 0; i < str.length; i++) {
			System.out.println("Input: " + str[i]);
			System.out.println("Found: " + utils.getStackAssigment(str[i]));
			System.out.println("Slice " + utils.getCurrentStackfromAssigment(str[i]));
		}

	}

	public String getFovNameFromTLTitle(String currentStacklabel) {
		String str = currentStacklabel;
		
		return 	removeStackAssigment(str);
	}
	
	
	
	
	
	/**
	 * 
	 * Remove the duplicated entries in an ArrayList<Strings> and keeps its ordering
	 * 
	 * @param arrStr ArrayList<String> 
	 * @return {@link ArrayList} without duplicate entries.
	 * 
	 * This solution will be improved later to avoid unnecessary return values.
	 * 
	 */
	public  ArrayList<String> removeDuplicates(ArrayList<String> arrStr){
		return new ArrayList<String>(new LinkedHashSet<String>(arrStr));
		
	}
	
	
}
