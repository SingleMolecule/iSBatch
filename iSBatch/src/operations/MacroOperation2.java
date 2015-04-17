package operations;

import java.util.HashMap;

import filters.NodeFilterInterface;
import gui.newDialog;

import javax.swing.JFrame;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;

public class MacroOperation2 implements Operation {
	
	private String macroFile = "";
	private NodeFilterInterface filter;
	private JFrame frame;
	private DatabaseModel model;
	
	public MacroOperation2(JFrame frame, DatabaseModel model) {
		this.frame = frame;
		this.model = model;
	}
	
	@Override
	public String getName() {
		return "Run Macro2";
	}

	@Override
	public String[] getContext() {
		return new String[]{"All"};	
	}

	@Override
	public void visit(Root root) {
		for (Node childNode: root.getChildren())
			childNode.accept(this);
	}

	@Override
	public void visit(Experiment experiment) {
		for (Node childNode: experiment.getChildren())
			childNode.accept(this);
	}

	@Override
	public void visit(Sample sample) {
		for (Node childNode: sample.getChildren())
			childNode.accept(this);
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		runMacro(fieldOfView);
	}

	@Override
	public void visit(FileNode fileNode) {
		runMacro(fileNode);
	}
	
	public void runMacro(Node node) {
		
//		String arg = node.getType();
//		
//		arg += "," + node.getOutputFolder();
//		
//		for (Node child: node.getDescendents(filter))
//			arg += "," + child.getProperty("path");
//		
//		String resultArg = IJ.runMacroFile(macroFile, arg);
//		
//		// resultArg is a comma separated list of output files that need to be added to this node
//		String[] filenames = resultArg.split(",");	// resultArg.split("[^\\]+,");
//		
//		for (String filename: filenames) {
//			
//			File file = new File(filename);
//			FileNode fileNode = new FileNode(node);
//			fileNode.setProperty("name", file.getName());
//			fileNode.setProperty("path", file.getName());
//			
//			model.addNode(node, fileNode);
//			
//		}
		
	}
	
	@Override
	public boolean setup(Node node) {
		// String to parse:
		
		final newDialog dialog = new newDialog(node);
		
		if (dialog.isCanceled())
			return false;
//		
//		final String channel = dialog.getChannel();
//		
//		filter = new NodeFilter() {
//			
//			@Override
//			public boolean accept(Node node) {
//				
//				return node.getType().equals(FileNode.type) &&
//						node.getProperty("channel").equals(channel);
//			}
//		};
//		
//		macroFile = dialog.getMacroFilename();
		
		return true;
	}

	@Override
	public void finalize(Node node) {
		macroFile = null;
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
