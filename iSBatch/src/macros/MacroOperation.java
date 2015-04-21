package macros;

import ij.IJ;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import test.TreeGenerator;
import model.DatabaseModel;
import model.Node;


public class MacroOperation {
	
	public String escape(String str) {
		
		str = str.replaceAll("\\\\", "\\\\\\\\");
		str = str.replaceAll("\\(", "\\\\(");
		str = str.replaceAll("\\)", "\\\\)");
		str = str.replaceAll("=", "\\\\=");
		str = str.replaceAll(" ", "\\\\ ");
		
		
		return str;
	}
	

	public String getArgument(Node node) {
	
		String argument = "";
		
		for (Entry<String, String> entry: node.getProperties().entrySet()) {
			
			String key = escape(entry.getKey());
			String value = escape(entry.getValue());
			
			argument += key + "=" + value + " ";
		}
		
		for (Node childNode: node.getChildren()) {
			argument += getArgument(childNode);
		}
		
		return "(" + argument + ")";
		
	}
	
	public String[] runMacro(String macro, Node node) {
		
		try {
			InputStream is = getClass().getResource("/macros/template.ijm").openStream();
			
			int length;
			byte[] b = new byte[1024];
			String templateMacro = "";
			
			while ((length = is.read(b)) != -1)
				templateMacro += new String(b, 0, length);
			
			is.close();
			macro = templateMacro.replaceAll("%user_macro%", macro);
			
			IJ.runMacro(macro, getArgument(node));
			
			System.out.println("DEBUG : " + getArgument(node));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		
		DatabaseModel model = TreeGenerator.generate("e:\\test", "e:\\temp", 5);
		MacroOperation mo = new MacroOperation();
		String argument = mo.getArgument(model.getRoot());
		
		System.out.println(argument.replaceAll("\\\\", "\\\\\\\\"));
		
	}
	
}
