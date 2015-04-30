package operations;

import java.util.HashMap;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;

public class NewDatabaseOperation implements Operation {

	private DatabaseModel model;

	public NewDatabaseOperation(DatabaseModel model) {
		super();
		this.model = model;
	}

	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	@Override
	public String getName() {
		return "New Database";
	}

	@Override
	public boolean setup(Node node) {

		// ask the user to save the current database

		// ask the user to name the new database

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
