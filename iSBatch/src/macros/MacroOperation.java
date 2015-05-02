/*
 * 
 */
package macros;

import ij.IJ;
import ij.WindowManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import operations.Operation;
import test.TreeGenerator;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;

// TODO: Auto-generated Javadoc
/**
 * The Class MacroOperation.
 */
public class MacroOperation implements Operation {

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
	 * @return the string[]
	 */
	public String[] runMacro(String macro, Node node) {

		try {
			InputStream is = getClass().getResource("/macros/template.ijm")
					.openStream();

			int length;
			byte[] b = new byte[1024];
			String templateMacro = "";

			while ((length = is.read(b)) != -1)
				templateMacro += new String(b, 0, length);

			is.close();
			macro = templateMacro.replaceAll("%user_macro%", macro);

			IJ.runMacro(macro, getArgument(node));
			WindowManager.closeAllWindows();
			
			System.out.println("DEBUG : " + getArgument(node));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		DatabaseModel model = TreeGenerator.generate("e:\\test", "e:\\temp", 5);
		MacroOperation mo = new MacroOperation();
		String argument = mo.getArgument(model.getRoot());

		System.out.println(argument.replaceAll("\\\\", "\\\\\\\\"));

	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	@Override
	public String[] getContext() {
		return new String[]{ "All" };
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return "Run Macro";
	}

	/**
	 * Setup.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	@Override
	public boolean setup(Node node) {
		new MacroDialog(null, node);
		return false;
	}

	/**
	 * Finalize.
	 *
	 * @param node the node
	 */
	@Override
	public void finalize(Node node) {
	}

	/**
	 * Gets the created nodes.
	 *
	 * @return the created nodes
	 */
	@Override
	public Node[] getCreatedNodes() {
		return null;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	@Override
	public HashMap<String, String> getParameters() {
		return null;
	}

	/**
	 * Visit.
	 *
	 * @param root the root
	 */
	@Override
	public void visit(Root root) {
	}

	/**
	 * Visit.
	 *
	 * @param experiment the experiment
	 */
	@Override
	public void visit(Experiment experiment) {
	}

	/**
	 * Visit.
	 *
	 * @param sample the sample
	 */
	@Override
	public void visit(Sample sample) {
	}

	/**
	 * Visit.
	 *
	 * @param fieldOfView the field of view
	 */
	@Override
	public void visit(FieldOfView fieldOfView) {
	}

	/**
	 * Visit.
	 *
	 * @param fileNode the file node
	 */
	@Override
	public void visit(FileNode fileNode) {

	}

	/**
	 * Visit.
	 *
	 * @param operationNode the operation node
	 */
	@Override
	public void visit(OperationNode operationNode) {

	}

}
