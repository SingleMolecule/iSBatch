/*
 * 
 */
package operations;

import java.io.File;
import java.util.HashMap;

import filters.NodeFilterInterface;
import gui.MacroDialog;
import ij.IJ;

import javax.swing.JFrame;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;

import model.OperationNode;
import model.Root;
import model.Sample;

// TODO: Auto-generated Javadoc
/**
 * The Class CopyOfMacroOperation.
 */
public class CopyOfMacroOperation implements Operation {

	/** The macro file. */
	private String macroFile;
	
	/** The filter. */
	private NodeFilterInterface filter;
	
	/** The frame. */
	private JFrame frame;
	
	/** The model. */
	private DatabaseModel model;
	
	/**
	 * Instantiates a new copy of macro operation.
	 *
	 * @param frame the frame
	 * @param model the model
	 */
	public CopyOfMacroOperation(JFrame frame, DatabaseModel model) {
		this.frame = frame;
		this.model = model;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return "Run Macro";
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	@Override
	public String[] getContext() {
		return new String[]{"All"};	
	}

	/**
	 * Visit.
	 *
	 * @param root the root
	 */
	@Override
	public void visit(Root root) {
		for (Node childNode: root.getChildren())
			childNode.accept(this);
	}

	/**
	 * Visit.
	 *
	 * @param experiment the experiment
	 */
	@Override
	public void visit(Experiment experiment) {
		for (Node childNode: experiment.getChildren())
			childNode.accept(this);
	}

	/**
	 * Visit.
	 *
	 * @param sample the sample
	 */
	@Override
	public void visit(Sample sample) {
		for (Node childNode: sample.getChildren())
			childNode.accept(this);
	}

	/**
	 * Visit.
	 *
	 * @param fieldOfView the field of view
	 */
	@Override
	public void visit(FieldOfView fieldOfView) {
		runMacro(fieldOfView);
	}

	/**
	 * Visit.
	 *
	 * @param fileNode the file node
	 */
	@Override
	public void visit(FileNode fileNode) {
		runMacro(fileNode);
	}
	
	/**
	 * Run macro.
	 *
	 * @param node the node
	 */
	public void runMacro(Node node) {
		
		String arg = node.getType();
		
		arg += "," + node.getOutputFolder();
		
		for (Node child: node.getDescendents(filter))
			arg += "," + child.getProperty("path");
		
		String resultArg = IJ.runMacroFile(macroFile, arg);
		
		// resultArg is a comma separated list of output files that need to be added to this node
		String[] filenames = resultArg.split(",");	// resultArg.split("[^\\]+,");
		
		for (String filename: filenames) {
			
			File file = new File(filename);
			FileNode fileNode = new FileNode(node);
			fileNode.setProperty("name", file.getName());
			fileNode.setProperty("path", file.getName());
			
			model.addNode(node, fileNode);
			
		}
		
	}
	
	/**
	 * Setup.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	@Override
	public boolean setup(Node node) {
		
		final MacroDialog dialog = new MacroDialog(frame);
		
		if (dialog.isCanceled())
			return false;
		
		final String channel = dialog.getChannel();
		
		filter = new NodeFilterInterface() {
			
			@Override
			public boolean accept(Node node) {
				
				return node.getType().equals(FileNode.type) &&
						node.getProperty("channel").equals(channel);
			}
		};
		
		macroFile = dialog.getMacroFilename();
		
		return true;
	}

	/**
	 * Finalize.
	 *
	 * @param node the node
	 */
	@Override
	public void finalize(Node node) {
		macroFile = null;
	}

	/**
	 * Visit.
	 *
	 * @param operationNode the operation node
	 */
	@Override
	public void visit(OperationNode operationNode) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Gets the created nodes.
	 *
	 * @return the created nodes
	 */
	@Override
	public Node[] getCreatedNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	@Override
	public HashMap<String, String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}
}
