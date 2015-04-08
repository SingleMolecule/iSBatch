/**
 * 
 */
package operations;

import javax.swing.JDialog;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.Root;
import model.Sample;
import operation.gui.FlatOperationGui;

/**
 * @author VictorCaldas
 *
 */
public class SetBackGround implements Operation {
	FlatOperationGui dialog;
	
	private String channel;
	private String method;
	
	public SetBackGround(DatabaseModel treeModel) {
	}

	/* (non-Javadoc)
	 * @see context.ContextElement#getContext()
	 */
	@Override
	public String[] getContext() {
		return new String[]{"All"};	
	}

	/* (non-Javadoc)
	 * @see operations.Operation#getName()
	 */
	@Override
	public String getName() {
		return "Set BackGround";
	}

	/* (non-Javadoc)
	 * @see operations.Operation#setup(model.Node)
	 */
	@Override
	public boolean setup(Node node) {
		dialog = new FlatOperationGui(node);
		if (dialog.isCanceled())
			return false;
		channel = dialog.getChannel();
		method = dialog.getMethod();
		return true;
	}

	/* (non-Javadoc)
	 * @see operations.Operation#finalize(model.Node)
	 */
	@Override
	public void finalize(Node node) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.Root)
	 */

	@Override
	public void visit(Root root) {
		
		run(root);
		
	}

	@Override
	public void visit(Experiment experiment) {
		run(experiment);
		
		
	}

	private void getParameters() {
		this.channel = dialog.getChannel();
		this.method = dialog.getMethod();
		
	}

	private void run(Node node) {
		System.out.println(channel);
		
	}

	@Override
	public void visit(Sample sample) {
		run(sample);
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		run(fieldOfView);
	}

	@Override
	public void visit(FileNode fileNode) {
		run(fileNode);
	}
	
	public static void main(String[] args) {
		
	}

}
