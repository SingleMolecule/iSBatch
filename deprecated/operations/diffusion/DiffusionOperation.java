package operations.diffusion;

import java.util.HashMap;

import ij.IJ;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.text.TextWindow;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operations.Operation;

public class DiffusionOperation implements Operation {

	private DiffusionDialog dialog;
	
	@Override
	public String[] getContext() {
		return new String[]{"All"};
	}

	@Override
	public String getName() {
		return "Diffusion";
	}

	@Override
	public boolean setup(Node node) {
		
		// show dialog
		dialog = new DiffusionDialog(node);
		
		return !dialog.wasCanceled();
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
		for (Node child: root.getChildren())
			child.accept(this);
	}

	@Override
	public void visit(Experiment experiment) {
		for (Node child: experiment.getChildren())
			child.accept(this);
	}

	@Override
	public void visit(Sample sample) {
		for (Node child: sample.getChildren())
			child.accept(this);
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		for (Node child: fieldOfView.getChildren())
			child.accept(this);
	}

	@Override
	public void visit(FileNode fileNode) {
		
		String peakTable = fileNode.getProperty("PeakTable");
		
		if (peakTable != null) {
			
			
			ResultsTable table = ResultsTable.open2(peakTable);
			String arguments = String.format("slice_to_look_ahead=%d max_step_size=%d", dialog.getLookAhead(), dialog.getMaxStepSize());
			IJ.run("Particle Tracker", arguments);
			
			arguments = String.format("time_interval=%f pixel_size=%f minimum_number_of_points=1 fit_until=%f diffusion_dimensionality=%s", dialog.getTimeInterval(), dialog.getPixelSize(), dialog.fitUntil, dialog.getDimensionality());
			IJ.run("Mean Square Displacement", arguments);
			
			// there are now three results tables that we have to consider
			// Diffusion Coefficients
			// mean square displacements 
			// square displacements
			
			TextWindow window1 = (TextWindow)WindowManager.getWindow("Diffusion Coefficients");
			TextWindow window2 = (TextWindow)WindowManager.getWindow("mean square displacements");
			TextWindow window3 = (TextWindow)WindowManager.getWindow("square displacements");
			ResultsTable table1 = window1.getTextPanel().getResultsTable();
			ResultsTable table2 = window2.getTextPanel().getResultsTable();
			ResultsTable table3 = window3.getTextPanel().getResultsTable();
			
			table1.save("");
			table2.save("");
			table3.save("");			
			
		}
		
	}

	@Override
	public void visit(OperationNode operationNode) {
	}

}
