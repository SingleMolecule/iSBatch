package iSBatch;

import ij.Prefs;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import operations.Operation;
import model.FieldOfView;
import model.Node;
import model.Root;
import model.Sample;

// TODO: Auto-generated Javadoc
/**
 * This class is a placeholder for all the preferences. It also contains methods for storing and retrieving
 * preferences into a (root)node. This root node can be stored in the database to make the preferences persistent.
 * Preferences should be loaded from the (root)node before the program is running (somewhere in the main class).
 * 
 * To add extra preferences it is sufficient to add a public static string field. Currently only string values
 * are supported!
 * 
 * 30/4/2015 - initial version
 * 1/5/2015 - added methods to save and load preferences (using reflection)
 * 
 * @author V.A.E. Caldas
 * @author C.M. Punter
 *
 */

public class iSBatchPreferences  {
	public static String INNER_RADIUS = "1";
	public static String OUTER_RADIUS = "3";
	public static String SNR_THRESHOLD = "4";
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
	public static String lastSelectedPath = "";
	public static boolean insideCell = false;
	public static boolean useDiscoidalFiltering = false;
	public static double[] maxError = new double[] {
		Double.parseDouble(ERROR_BASELINE),
		Double.parseDouble(ERROR_HEIGHT),
		Double.parseDouble(ERROR_X),
		Double.parseDouble(ERROR_Y),
		Double.parseDouble(ERROR_SIGMA_X),
		Double.parseDouble(ERROR_SIGMA_Y),};
	
	/**
	 * Loads all preferences from a node object using reflection. Only public static string fields are loaded!
	 * 
	 * @param root The node in which all the preferences are stored as properties (usually the root node)
	 */

	public static void loadPreferences(Node root) {
		
		Field[] fields = iSBatchPreferences.class.getDeclaredFields();
		
		for (Field field: fields) {
			
			int modifiers = field.getModifiers();
			
			if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && field.getType() == String.class) {
				
				String value = root.getProperty(field.getName());
				
				if (value != null) {
					try {
						field.set(null, value);
					} catch (Exception e) {
						
					}
				}
				
			}
			
		}
		
		maxError = new double[] {
			Double.parseDouble(ERROR_BASELINE),
			Double.parseDouble(ERROR_HEIGHT),
			Double.parseDouble(ERROR_X),
			Double.parseDouble(ERROR_Y),
			Double.parseDouble(ERROR_SIGMA_X),
			Double.parseDouble(ERROR_SIGMA_Y)
		};
		
	}
	
	/**
	 * Stores all preferences into a node object using reflection. Only public static string fields are stored!
	 * 
	 * @param root The node in which all the preferences are stored as properties (usually the root node)
	 */
	public static void savePreferences(Node root) {
		
		ERROR_BASELINE = Double.toString(maxError[0]);
		ERROR_HEIGHT = Double.toString(maxError[1]);
		ERROR_X = Double.toString(maxError[2]);
		ERROR_Y = Double.toString(maxError[3]);
		ERROR_SIGMA_X = Double.toString(maxError[4]);
		ERROR_SIGMA_Y = Double.toString(maxError[5]);
		
		Field[] fields = iSBatchPreferences.class.getDeclaredFields();
		
		for (Field field: fields) {
			
			int modifiers = field.getModifiers();
			
			if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && field.getType() == String.class) {
				
				try {
					String value = (String)field.get(null);
					root.setProperty(field.getName(), value);
				} catch (Exception e) {
					
				}
				
			}
			
		}
		
	}

	// some test code
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Node testNode = new Node(null, Root.type) {
			@Override
			public void accept(Operation operation) {
			}
			@Override
			public int getNumberOfFoV() {
				return 0;
			}
			@Override
			public ArrayList<FieldOfView> getFieldOfView() {
				return null;
			}
			@Override
			public ArrayList<Sample> getSamples() {
				return null;
			} 
		};
		testNode.setProperty("OUTER_RADIUS", "123");
		loadPreferences(testNode);
		System.out.println(OUTER_RADIUS);
		
		OUTER_RADIUS = "456";
		savePreferences(testNode);
		System.out.println(testNode.getProperty("OUTER_RADIUS"));
	}
		
}
