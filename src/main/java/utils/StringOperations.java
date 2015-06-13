package utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class StringOperations {

	public static void main(String[] args) {

		// String name = "file.name";
		// System.out.println(getFileExtension(name));
		String string = "13:27:21 | selectWindow(\"514.TIF\");run(\"Peak Fitter\", \"use_discoidal_averaging_filter inner_radius=1 outer_radius=3 threshold=6 threshold_value=0 minimum_distance=8 fit_radius=4 max_error_baseline=5000 max_error_height=5000 max_error_x=1 max_error_y=1 max_error_sigma_x=1 max_error_sigma_y=1 stack\")";
		String arguments = getArguments("Peak Fitter", string);
		System.out.println(arguments);
	}

	public static String getArguments(String pluginName, String fullCommand) {
		// pluginName = "\""+ pluginName + "\"";
		String arguments = "";
		// LogPanel.log("Command: " + fullCommand);

		String[] array = fullCommand.split(";");
		ArrayList<String> allMatches = new ArrayList<String>();

		// LogPanel.log("Array size: " + array.length);

		for (int i = 0; i < array.length; i++) {
			String current = array[i];
			// LogPanel.log("Current on loop: " + current);
			if (current.startsWith("IJ.run")) {
				// This tag a good candidate.
				// LogPanel.log(current);

				Pattern p = Pattern.compile("\"([^\"]*)\"");
				Matcher m = p.matcher(current);
				while (m.find()) {
					// get with quotes
					allMatches.add(m.group(1));
				}
			}
		}

		// LogPanel.log(allMatches.size());

		if (allMatches.size() > 0) {
			if (allMatches.get(0).equalsIgnoreCase(pluginName)) {
				arguments = allMatches.get(1);
			}
		}

		return arguments;
	}

}
