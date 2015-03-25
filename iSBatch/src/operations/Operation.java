package operations;

import context.ContextElement;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.Root;
import model.Sample;

public interface Operation extends ContextElement {
	
	public String getName();
	public boolean setup(Node node);
	public void finalize(Node node);
	
	public void visit(Root root);
	public void visit(Experiment experiment);
	public void visit(Sample sample);
	public void visit(FieldOfView fieldOfView);
	public void visit(FileNode fileNode);
}