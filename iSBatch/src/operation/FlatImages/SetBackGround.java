/**
 * 
 */
package operation.FlatImages;


import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.Root;
import model.Sample;
import operations.Operation;

/**
 * @author VictorCaldas
 *
 */
public class SetBackGround implements Operation {
	SetBackgroundGui dialog;
	
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
		dialog = new SetBackgroundGui(node);
		if (dialog.isCanceled())
			return false;
		getParameters();
		return true;
	}

	/* (non-Javadoc)
	 * @see operations.Operation#finalize(model.Node)
	 */
	@Override
	public void finalize(Node node) {
		System.out.println("Operation finalized");

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
		System.out.println("Run class: " + channel + " using the method " + method);
		
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
