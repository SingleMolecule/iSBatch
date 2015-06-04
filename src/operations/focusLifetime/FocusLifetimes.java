package operations.focusLifetime;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.frame.Recorder;
import ij.plugin.frame.RoiManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import gui.FileSelectionDialog;
import gui.LogPanel;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operations.Operation;
import utils.IJUtils;
import utils.Projections;
import utils.StringOperations;
//import operations.peakFitter.PeakFitterGui;
import process.DiscoidalAveragingFilter;

public class FocusLifetimes implements Operation, PlugIn {
	private FileSelectionDialog dialog;
	private ArrayList<Node> filenodes;

	public FocusLifetimes(DatabaseModel treeModel) {
	}

	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}
	

	@Override
	public String getName() {
		return "Focus Lifetimes";
	}

	@Override
	public boolean setup(Node node) {
		dialog = new FileSelectionDialog(node);
		if (dialog.isCanceled())
			return false;
		this.filenodes = dialog.getFileNodes();
		return true;
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
		System.out.println("Does not apply to root");
	}

	@Override
	public void visit(Experiment experiment) {
		runNode(experiment);
	}

	@Override
	public void visit(Sample sample) {
		runNode(sample);
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		runNode(fieldOfView);
	}

	@Override
	public void visit(FileNode fileNode) {
		runNode(fileNode);
	}

	private void runNode(Node node) {
		run(null);
	}

	@Override
	public void visit(OperationNode operationNode) {
	}

	@Override
	public void run(String arg0) {
		IJUtils.emptyAll();
		RoiManager manager = new RoiManager(true);
		ResultsTable table;
		String pluginName = "Peak Finder";
		String arguments = "";
		int size = filenodes.size();

		for (int i = 0; i < size; i++) {
			int currentcount = i+1;
			LogPanel.log("Measuring on file " + currentcount + " of " + size);
			Node currentNode = filenodes.get(i);
			ImagePlus imp = IJ.openImage(currentNode.getPath());
			//Get Average Projection
			ImagePlus projection  = Projections.doAverageProjection(imp);
			
			File f = new File(currentNode.getOutputFolder() + File.separator
					+ "Tracking");
			f.mkdirs();
			if (i == 0) {
				
				projection.show();
				Recorder recorder = new Recorder(false);
				Recorder.record = true;
				Recorder.recordInMacros = true;
				IJ.run(projection, pluginName, arguments);
				String command = recorder.getText();
				recorder.close();
				arguments = StringOperations.getArguments(pluginName, command);
				projection.close();
			}
			else{
				IJ.run(projection, pluginName, arguments);
			}
			
			
			manager = RoiManager.getInstance();
			manager.runCommand("Save", f.getAbsolutePath() + File.separator + "traceSeeds.zip");
			
			IJ.run(imp, "Select All", "");
			table = manager.multiMeasure(imp);
			
			String path = f.getAbsolutePath() + File.separator
					+ currentNode.getChannel() + currentNode.getName()
					+ ".traces.csv";
			table.save(path);
			table.reset();
			manager.close();
			
			IJUtils.emptyAll();
		}
		manager.close();
		
	}

}
