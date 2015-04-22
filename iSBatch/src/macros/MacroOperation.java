package macros;

import ij.IJ;

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

public class MacroOperation implements Operation {

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
		new MacroDialog(null, node);
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
