package operations;

import java.util.HashMap;

import gui.MaintenanceDialog;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;

public class MaintenanceOperation implements Operation {
	private DatabaseModel model;

	public MaintenanceOperation(DatabaseModel model) {
		this.model = model;
	}

	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Maintenance";
	}

	@Override
	public boolean setup(Node node) {
		new MaintenanceDialog(null, node, model);
		return true;
	}

	@Override
	public void finalize(Node node) {
		// TODO Auto-generated method stub

	}

	@Override
	public Node[] getCreatedNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visit(Root root) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Experiment experiment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Sample sample) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FileNode fileNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OperationNode operationNode) {
		// TODO Auto-generated method stub

	}

}
