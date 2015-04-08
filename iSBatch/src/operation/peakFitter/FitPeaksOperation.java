/**
 * 
 */
package operation.peakFitter;

import operations.Operation;
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
public class FitPeaksOperation implements Operation {
	private PeakFitterGui dialog;
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
	private double zScale;
	private double errorSigmaY;
	private double errorSigmaX;
	private double errorY;
	private double errorX;
	private double errorHeight;
	private double errorBaseline;
	private DatabaseModel model;

	public FitPeaksOperation(DatabaseModel treeModel) {
		this.model = treeModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see context.ContextElement#getContext()
	 */
	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#getName()
	 */
	@Override
	public String getName() {
		return "Fit Peaks";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#setup(model.Node)
	 */
	@Override
	public boolean setup(Node node) {
		// String to parse:

		dialog = new PeakFitterGui(node);
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

		this.zScale = dialog.getZScale();

		this.errorSigmaY = dialog.getErrorSigmaY();
		this.errorSigmaX = dialog.getErrorSigmaX();
		this.errorY = dialog.getErrorY();
		this.errorX = dialog.getErrorX();
		this.errorHeight = dialog.getErrorHeight();
		this.errorBaseline = dialog.getErrorBaseline();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#finalize(model.Node)
	 */
	@Override
	public void finalize(Node node) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
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

		System.out.println(zScale);
		System.out.println(errorSigmaY);
		System.out.println(errorSigmaX);
		System.out.println(errorY);
		System.out.println(errorX);
		System.out.println(errorHeight);
		System.out.println(errorBaseline);
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
