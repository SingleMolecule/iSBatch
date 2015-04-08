/**
 * 
 */
package operations;


import operation.gui.FindPeaksGui;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.Root;
import model.Sample;

/**
 * @author VictorCaldas
 *
 */
public class FindPeaksOperation implements Operation {
	private FindPeaksGui dialog;
	private String channel;
	private String method;
	private double innerRadius;
	private double outerRadius;
	private double threshold;
	private double SNRthreshold;
	private double minDistance;
	private double selectionRadius;
	private boolean useCells;
	private boolean useDiscoidal;
	public FindPeaksOperation(DatabaseModel treeModel) {
		// TODO Auto-generated constructor stub
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
		return "Find Peaks";
	}

	/* (non-Javadoc)
	 * @see operations.Operation#setup(model.Node)
	 */
	@Override
	public boolean setup(Node node) {
		// String to parse:

		 dialog = new FindPeaksGui(node);
		if (dialog.isCanceled())
			return false;
		getParameters();
		return true;
	}

	private void getParameters() {
		this.innerRadius = dialog.getInnerRadius();
		this.outerRadius = dialog.getOuterRadius();
		this.threshold = dialog.getThreshold();
		this.SNRthreshold = dialog.getSNRThreshold();
		this.minDistance = dialog.getMinDistance();
		this.selectionRadius = dialog.getSelectionRadius();
		this.useCells = dialog.useCells;
		this.useDiscoidal = dialog.useDiscoidal;
		this.channel = dialog.getChannel();
		this.method = dialog.getMethod();
		
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

	private void run(Node node) {
		System.out.println(innerRadius);
		System.out.println(outerRadius);
		System.out.println(threshold);
		System.out.println(SNRthreshold);
		System.out.println(minDistance);
		System.out.println(selectionRadius);
		System.out.println(useCells);
		System.out.println(useDiscoidal);
		System.out.println(channel);
		System.out.println(method);
		

	}

	@Override
	public void visit(Experiment experiment) {
		run(experiment);
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
