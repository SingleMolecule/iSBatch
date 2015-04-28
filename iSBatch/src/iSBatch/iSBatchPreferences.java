package iSBatch;




// A class to store preferences
// Version beta

public class iSBatchPreferences  {

	public static String INNER_RADIUS = "1";
	public static String OUTER_RADIUS = "4";
	public static String SNR_THRESHOLD = "6";
	public static String INTENSITY_THRESHOLD = "0";
	public static String SELECTION_RADIUS = "4";
	public static String DISTANCE_BETWEEN_PEAKS = "8";
	public static String Z_SCALE = "1.25";
	public static String ERROR_BASELINE = "5000";
	public static String ERROR_SIGMA_Y = "1";
	public static String ERROR_SIGMA_X = "1";
	public static String ERROR_X = "1";
	public static String ERROR_Y = "1";
	public static String ERROR_HEIGHT = "5000";

	public static boolean insideCell = false;
	public static boolean useDiscoidalFiltering = false;
	
	public static double[] maxError = new double[] {
		Double.parseDouble(ERROR_BASELINE),
		Double.parseDouble(ERROR_HEIGHT),
		Double.parseDouble(ERROR_X),
		Double.parseDouble(ERROR_Y),
		Double.parseDouble(ERROR_SIGMA_X),
		Double.parseDouble(ERROR_SIGMA_Y),};
	
	
	public static String lastSelectedPath;
	
//	public String INNER_RADIUS = "1";
//	public String INNER_RADIUS = "1";
//	public String INNER_RADIUS = "1";
//	public String INNER_RADIUS = "1";
//	public String INNER_RADIUS = "1";
//	public String INNER_RADIUS = "1";
//	public String INNER_RADIUS = "1";
//	public String INNER_RADIUS = "1";
//	

}
