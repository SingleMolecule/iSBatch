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

public class ExportFilesOperation implements Operation {

	public ExportFilesOperation(DatabaseModel treeModel) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String[] getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setup(Node node) {
		// TODO Auto-generated method stub
		return false;
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
