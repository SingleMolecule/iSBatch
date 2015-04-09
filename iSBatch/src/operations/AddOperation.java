package operations;

import gui.ExperimentDialog;
import gui.FieldOfViewDialog;
import gui.FileDialog;
import gui.SampleDialog;

import java.io.File;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Importer;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;

public class AddOperation implements Operation {

	private JFrame frame;
	private DatabaseModel model;
	private Importer importer;
	
	public AddOperation(JFrame frame, DatabaseModel model) {
		super();
		this.frame = frame;
		this.model = model;
		importer = new Importer(model);
	}

	@Override
	public String getName() {
		return "Add";
	}

	@Override
	public String[] getContext() {
		return new String[]{"All"};
	}

	@Override
	public void visit(Root root) {
		
		Experiment experiment = new Experiment(root);
		experiment.setProperty("name", "experiment");
		experiment.setProperty("folder", "");
		experiment.setProperty("type", "Time Sampling");
		ExperimentDialog dialog = new ExperimentDialog(frame, experiment);

		if (dialog.isCanceled())
			return;
			
		model.addNode(root, experiment);
		
		int option = JOptionPane.showConfirmDialog(frame, "Import all underlying samples?", "Import", JOptionPane.YES_NO_OPTION);
			
		if (option == JOptionPane.YES_OPTION) {
			File folder = new File(experiment.getProperty("folder"));
				
			if (!folder.exists()) {
				JOptionPane.showMessageDialog(frame, "Cannot import samples because the experiment folder does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			importer.importSamples(experiment);
			
		}
	}

	@Override
	public void visit(Experiment experiment) {
		
		Sample sample = new Sample(experiment);
		sample.setProperty("name", "sample");
		sample.setProperty("folder", "");
		SampleDialog dialog = new SampleDialog(frame, sample);

		if (dialog.isCanceled())
			return;
			
		model.addNode(experiment, sample);
			
		int option = JOptionPane.showConfirmDialog(frame, "Import all underlying field of views?", "Import", JOptionPane.YES_NO_OPTION);
			
		if (option == JOptionPane.YES_OPTION) {
			File folder = new File(sample.getProperty("folder"));
				
			if (!folder.exists()) {
				JOptionPane.showMessageDialog(frame, "Cannot import field of views because the sample folder does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			importer.importFieldOfViews(sample);

		}
		
	}

	@Override
	public void visit(Sample sample) {
		
		FieldOfView fieldOfView = new FieldOfView(sample);
		fieldOfView.setProperty("name", "field of view");
		fieldOfView.setProperty("folder", "");
		FieldOfViewDialog dialog = new FieldOfViewDialog(frame, fieldOfView);

		if (dialog.isCanceled())
			return;
			
		model.addNode(sample, fieldOfView);
			
		int option = JOptionPane.showConfirmDialog(frame, "Import all underlying files?", "Import", JOptionPane.YES_NO_OPTION);
			
		if (option == JOptionPane.YES_OPTION) {
			File folder = new File(fieldOfView.getProperty("folder"));
				
			if (!folder.exists()) {
				JOptionPane.showMessageDialog(frame, "Cannot import field of views because the sample folder does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			importer.importFiles(fieldOfView);
			
		}
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		
		FileNode fileNode = new FileNode(fieldOfView);
		FileDialog dialog = new FileDialog(frame, fileNode);

		if (!dialog.isCanceled())
			model.addNode(fieldOfView, fileNode);
	}

	@Override
	public void visit(FileNode fileNode) {
	}

	@Override
	public boolean setup(Node node) {

		return true;
	}

	@Override
	public void finalize(Node node) {
		
	}

	@Override
	public void visit(OperationNode operationNode) {
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
	
	
}
