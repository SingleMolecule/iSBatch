/**
 * 
 */
package operation.correlation;


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
public class CorrelationOperation implements Operation {
	CorrelationOperationGui dialog;
	

	private String channel1,filter1;
	private String channel2,filter2;


	private String type1;


	private String type2;


	private boolean projectX;


	private boolean projectY;
	
	public CorrelationOperation(DatabaseModel treeModel) {
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
		dialog = new CorrelationOperationGui(node);
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
		this.channel1 = dialog.getChannel1();
		this.channel2 = dialog.getChannel2();
		this.filter1 = dialog.getFilter1();
		this.filter2 = dialog.getFilter2();
		this.type1 = dialog.gettype1();
		this.type2 = dialog.gettype2();
		this.projectX = dialog.requireXProjection();
		this.projectY = dialog.requireYProjection();
		
		
		
	}

	private void run(Node node) {
		System.out.println(channel1);
		System.out.println(channel2);
		System.out.println(filter1);
		System.out.println(filter2);
		System.out.println(type1);
		System.out.println(type2);
		System.out.println( "Done");
		System.out.println(projectX);
		System.out.println(projectY);
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
