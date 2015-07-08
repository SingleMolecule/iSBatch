package macros;

import java.util.HashMap;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operations.Operation;

public class MacroOperation implements Operation {

	private DatabaseModel model;
	
	public MacroOperation(DatabaseModel model) {
		this.model = model;
	}
	
	@Override
	public String[] getContext() {
		return new String[]{"All"};
	}

	@Override
	public String getName() {
		return "Run Macro";
	}

	@Override
	public boolean setup(Node node) {
		new MacroDialog4(model, node);

		return true;
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
