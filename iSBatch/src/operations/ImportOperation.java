/*
 * 
 */
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

// TODO: Auto-generated Javadoc
/**
 * The Class ImportOperation.
 */
public class ImportOperation implements Operation {

	
	/** The importer. */
	private Importer importer;
	
	/** The file. */
	private File file;
	
	/**
	 * Instantiates a new import operation.
	 *
	 * @param model the model
	 */
	public ImportOperation(DatabaseModel model) {
		importer = new Importer(model);
	}
	
	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	@Override
	public String[] getContext() {
		return new String[]{ "All" };
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return "Import";
	}

	/**
	 * Setup.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	@Override
	public boolean setup(Node node) {
		
		String directory = IJ.getDirectory("Choose directory to import from");
		if (directory == null) return false;
		file = new File(directory);
		ChannelsDialog dialog = new ChannelsDialog(null, file);
		return !dialog.isCanceled();
	}

	/**
	 * Finalize.
	 *
	 * @param node the node
	 */
	@Override
	public void finalize(Node node) {
	}

	/**
	 * Gets the created nodes.
	 *
	 * @return the created nodes
	 */
	@Override
	public Node[] getCreatedNodes() {
		return null;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	@Override
	public HashMap<String, String> getParameters() {
		return null;
	}

	/**
	 * Visit.
	 *
	 * @param root the root
	 */
	@Override
	public void visit(Root root) {
		
		String[] options = new String[]{"Time Sampling" , "Time Lapse"};
		int option = JOptionPane.showOptionDialog(null, "Experiment type", "Import Experiment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		
		if (option == JOptionPane.CANCEL_OPTION)
			return;
		else
			importer.importExperiment(file, option == 0);
	}

	/**
	 * Visit.
	 *
	 * @param experiment the experiment
	 */
	@Override
	public void visit(Experiment experiment) {
		importer.importSamples(experiment);
	}

	/**
	 * Visit.
	 *
	 * @param sample the sample
	 */
	@Override
	public void visit(Sample sample) {
		importer.importFieldOfViews(sample);
	}

	/**
	 * Visit.
	 *
	 * @param fieldOfView the field of view
	 */
	@Override
	public void visit(FieldOfView fieldOfView) {
		importer.importFiles(fieldOfView);
	}

	/**
	 * Visit.
	 *
	 * @param fileNode the file node
	 */
	@Override
	public void visit(FileNode fileNode) {
	}

	/**
	 * Visit.
	 *
	 * @param operationNode the operation node
	 */
	@Override
	public void visit(OperationNode operationNode) {
	}

}
