package macros;

import gui.LogPanel;
import ij.IJ;
import ij.WindowManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import operations.Operation;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Importer;
import model.MacroModel;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;

public class MacroOperation implements Operation {

	private DatabaseModel model;
	private Importer importer;
	
	public MacroOperation(DatabaseModel model) {
		this.model = model;
	 	this.importer = new Importer(model);
	}
	
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

		for (Entry<String, String> entry : node.getProperties().entrySet()) {

			String key = escape(entry.getKey());
			String value = escape(entry.getValue());

			argument += key + "=" + value + " ";
		}

		for (Node childNode : node.getChildren()) {
			argument += getArgument(childNode);
		}

		return "(" + argument + ")";

	}

	public void runMacro(String macro, Node node) {

		if (node.getType() == FileNode.type) {
			String path = node.getProperty("path");
			
			if (path == null) {
				LogPanel.log("Cannot run macro for file node " + node + ". The node does not specify a path.");
				return;
			}
			
			File file = new File(node.getProperty("path"));
			
			if (!file.exists()) {
				LogPanel.log("Cannot run macro for file node " + node + ". The specified path does not exist (or is not accessible by the program).");
				return;
			}
			
		}
		
		try {
			InputStream is = getClass().getResource("/macros/template_new.ijm")
					.openStream();

			int length;
			byte[] b = new byte[1024];
			String templateMacro = "";

			while ((length = is.read(b)) != -1)
				templateMacro += new String(b, 0, length);

			is.close();
			
			macro = templateMacro.replaceAll("%user_macro%", macro);

			MacroModel.root = model.getRoot();
			String outputArg = IJ.runMacro(macro, Integer.toString(node.hashCode()));
			WindowManager.closeAllWindows();

			if (!outputArg.isEmpty()) {
			
				// put all output files in the tree
				String[] outputFiles = outputArg.split("\n");
				
				
				for (String outputFile: outputFiles) {
					importer.importFile(node, new File(outputFile));
	
					LogPanel.log("added " + outputFile +  " to the tree");
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String[] getContext() {
		return new String[]{ "All" };
	}

	@Override
	public String getName() {
		return "Run Macro";
	}

	@Override
	public boolean setup(Node node) {
		new MacroDialog(null, model, node);
		return false;
	}

	@Override
	public void finalize(Node node) {
	}

	@Override
	public Node[] getCreatedNodes() {
		return null;
	}

	@Override
	public HashMap<String, String> getParameters() {
		return null;
	}

	@Override
	public void visit(Root root) {
	}

	@Override
	public void visit(Experiment experiment) {
	}

	@Override
	public void visit(Sample sample) {
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
	}

	@Override
	public void visit(FileNode fileNode) {

	}

	@Override
	public void visit(OperationNode operationNode) {

	}

}
