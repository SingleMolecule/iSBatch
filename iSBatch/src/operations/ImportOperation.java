package operations;

import gui.ChannelsDialog;
import ij.IJ;

import java.io.File;
import java.util.HashMap;

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

public class ImportOperation implements Operation {

	
	private Importer importer;
	private File file;
	
	public ImportOperation(DatabaseModel model) {
		importer = new Importer(model);
	}
	
	@Override
	public String[] getContext() {
		return new String[]{ "All" };
	}

	@Override
	public String getName() {
		return "Import";
	}

	@Override
	public boolean setup(Node node) {
		
		String directory = IJ.getDirectory("Choose directory to import from");
		if (directory == null) return false;
		file = new File(directory);
		ChannelsDialog dialog = new ChannelsDialog(null, file);
		return !dialog.isCanceled();
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
		
		String[] options = new String[]{"Time Sampling" , "Time Lapse"};
		int option = JOptionPane.showOptionDialog(null, "Experiment type", "Import Experiment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		
		if (option == JOptionPane.CANCEL_OPTION)
			return;
		else
			importer.importExperiment(file, option == 0);
	}

	@Override
	public void visit(Experiment experiment) {
		importer.importSamples(experiment);
	}

	@Override
	public void visit(Sample sample) {
		importer.importFieldOfViews(sample);
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		importer.importFiles(fieldOfView);
	}

	@Override
	public void visit(FileNode fileNode) {
	}

	@Override
	public void visit(OperationNode operationNode) {
	}

}
