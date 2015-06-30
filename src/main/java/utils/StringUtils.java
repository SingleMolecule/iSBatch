package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	
	/**
	 * This class contain methods to deal with simple string operations and to
	 * handle creation of tags.
	 * 
	 * @param string
	 * @return
	 */

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

	public static boolean checkStackAssignment(String currentStacklabel) {
		
		System.out.println("Checking Stack assignment in " + currentStacklabel);
		System.out.println("This will return " + getStackAssigment(currentStacklabel));
		if(getStackAssigment(currentStacklabel) == "" || getStackAssigment(currentStacklabel) == null ){
		
			System.out.println("Return false");
			return false;
		}
		System.out.println("Return true " + getStackAssigment(currentStacklabel));
		return true;
	}
	

		

	public static String getStackAssigment(String currentStacklabel) {
		String stackAssignmentPattern = "(S\\(\\d{1,15}\\|\\d{1,15}\\))";
		Pattern MY_PATTERN = Pattern.compile(stackAssignmentPattern);
		Matcher  matcher = MY_PATTERN.matcher(currentStacklabel);
	
		 String matching = "";
		
		 while (matcher.find()) {
			 	matching = matcher.group(1);
			 	System.out.println(matching);
		}
		 return matching;
	}

	public static int getCurrentStackfromAssigment(String currentStacklabel) {
		int i = 0;
		return i;
	}

	public static int getStackSizefromAssigment(String currentStacklabel) {
		int i = 0;
		return i;
	}

	public static void removeStackAssigment(String currentStacklabel) {
		int i = 0;
	}

	public static void main(String[] args) {
		String[] str = new String[8];
		str[0] = "S(1|23)";
		str[1] = "S(1|23)";
		str[2] = "S(123|233)";
		str[3] = "S(1|3)";
		str[4] = "S(1|23232323232)";
		str[5] = "x(1|23232323232)";
		str[6] = "s(1|23232323232)";
		str[7] = "Sstack(1|23232323232)";


		for (int i = 0; i < str.length; i++) {
			System.out.println("Input: " + str[i]);
			System.out.println("Found: " + getStackAssigment(str[i]));
		}

	}
}
