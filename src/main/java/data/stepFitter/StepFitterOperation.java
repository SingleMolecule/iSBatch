package data.stepFitter;

import java.io.File;

import java.util.HashMap;

import ij.IJ;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Importer;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operations.Operation;

public class StepFitterOperation implements Operation {

	StepFitter3 fitter = new StepFitter3();
	Importer importer;
	
	public StepFitterOperation(DatabaseModel model) {
		importer = new Importer(model);
	}
	
	@Override
	public String[] getContext() {
		return new String[]{"All"};
	}

	@Override
	public String getName() {
		return "Step Fitter Operation";
	}

	@Override
	public boolean setup(Node node) {
		
		String path = node.getProperty("PeakTable");
		
		if (path == null) {
			IJ.showMessage("This plugin work only on result tables");
			return false;
		}
		
		ResultsTable table = Analyzer.getResultsTable();
		ResultsTable.open2(path);
		
		File f = new File(node.getOutputFolder() + File.separator);
		IJ.run("Step Fitter");
		
		String channel = node.getChannel();
		table = Analyzer.getResultsTable();
		
		File outputFile = new File(f.getAbsolutePath() + "["+channel+"]Steps.csv"); 
		table.save(outputFile.getPath());
		
		importer.importFile(node, outputFile);
		
		return true;
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
