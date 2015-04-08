package operations;

import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.Root;
import model.Sample;

public abstract class OperationTemplate implements Operation {
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	String OperationName = "Set Operation Name";
	
	@Override
	public String[] getContext() {
		return null;
	}
	@Override
	public String getName() {
		return OperationName;
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

}
