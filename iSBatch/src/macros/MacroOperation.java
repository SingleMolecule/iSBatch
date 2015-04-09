package macros;

import java.util.Map.Entry;

import test.TreeGenerator;
import model.DatabaseModel;
import model.Node;


public class MacroOperation {
	
	public String escape(String str) {
		
		str = str.replaceAll("\\(", "\\\\(");
		str = str.replaceAll("\\)", "\\\\)");
		str = str.replaceAll("=", "\\\\=");
		str = str.replaceAll(",", "\\\\,");
		
		return str;
	}
	

	public String getArgument(Node node) {
	
		String argument = "";
		String separator = "";
		
		for (Entry<String, String> entry: node.getProperties().entrySet()) {
			
			String key = escape(entry.getKey());
			String value = escape(entry.getValue());
			
			argument += separator + key + "=" + value;
			separator = ",";
		}
		
		for (Node childNode: node.getChildren()) {
			argument += separator + getArgument(childNode);
			separator = ",";
		}
		
		return "(" + argument + ")";
		
	}
	
	public static void main(String[] args) {
		
		DatabaseModel model = TreeGenerator.generate("e:\\test", "e:\\temp", 5);
		MacroOperation mo = new MacroOperation();
		String argument = mo.getArgument(model.getRoot());
		
		System.out.println(argument.replaceAll("\\\\", "\\\\\\\\"));
		
	}
	
}
