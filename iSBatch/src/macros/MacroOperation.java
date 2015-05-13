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

// TODO: Auto-generated Javadoc
/**
 * The Class MacroOperation.
 */
public class MacroOperation implements Operation {

	/** The model. */
	private DatabaseModel model;
	
	/** The importer. */
	private Importer importer;
	
	/**
	 * Instantiates a new macro operation.
	 *
	 * @param model the model
	 */
	public MacroOperation(DatabaseModel model) {
		this.model = model;
	 	this.importer = new Importer(model);
	}
	
	/**
	 * Escape.
	 *
	 * @param str the str
	 * @return the string
	 */
	public String escape(String str) {

		str = str.replaceAll("\\\\", "\\\\\\\\");
		str = str.replaceAll("\\(", "\\\\(");
		str = str.replaceAll("\\)", "\\\\)");
		str = str.replaceAll("=", "\\\\=");
		str = str.replaceAll(" ", "\\\\ ");

		return str;
	}

	/**
	 * Gets the argument.
	 *
	 * @param node the node
	 * @return the argument
	 */
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

	/**
	 * Run macro.
	 *
	 * @param macro the macro
	 * @param node the node
	 */
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

	/* (non-Javadoc)
	 * @see context.ContextElement#getContext()
	 */
	@Override
	public String[] getContext() {
		return new String[]{ "All" };
	}

	/* (non-Javadoc)
	 * @see operations.Operation#getName()
	 */
	@Override
	public String getName() {
		return "Run Macro";
	}

	/* (non-Javadoc)
	 * @see operations.Operation#setup(model.Node)
	 */
	@Override
	public boolean setup(Node node) {
		new MacroDialog(null, model, node);
		return false;
	}

	/* (non-Javadoc)
	 * @see operations.Operation#finalize(model.Node)
	 */
	@Override
	public void finalize(Node node) {
	}

	/* (non-Javadoc)
	 * @see operations.Operation#getCreatedNodes()
	 */
	@Override
	public Node[] getCreatedNodes() {
		return null;
	}

	/* (non-Javadoc)
	 * @see operations.Operation#getParameters()
	 */
	@Override
	public HashMap<String, String> getParameters() {
		return null;
	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.Root)
	 */
	@Override
	public void visit(Root root) {
	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.Experiment)
	 */
	@Override
	public void visit(Experiment experiment) {
	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.Sample)
	 */
	@Override
	public void visit(Sample sample) {
	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.FieldOfView)
	 */
	@Override
	public void visit(FieldOfView fieldOfView) {
	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.FileNode)
	 */
	@Override
	public void visit(FileNode fileNode) {

	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.OperationNode)
	 */
	@Override
	public void visit(OperationNode operationNode) {

	}

}
