package test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStamp {
	 
	    public static void main( String[] args )
	    {
	    	
		 System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()));
	    }
	}
