/*
 * 
 */
package operations;


import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import model.DatabaseModel;
import model.Node;
import model.OperationNode;

// TODO: Auto-generated Javadoc
/**
 * The Class OperationRunner.
 */
public class OperationRunner implements Runnable {

	/** The operation. */
	private DatabaseModel model;
	private Operation operation;
	
	/** The node. */
	private Node node;
	
	private JTree tree;
	
	/**
	 * Instantiates a new operation runner.
	 *
	 * @param model the model
	 * @param operation the operation
	 * @param node the node
	 * @param tree 
	 */
	public OperationRunner(DatabaseModel model, Operation operation, Node node, JTree tree) {
		super();
		this.model = model;
		this.operation = operation;
		this.node = node;
		this.tree = tree;
		new Thread(this).start();
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		
		JDialog dialog = new JDialog();
		dialog.add(new JLabel("Running " + operation.getName() + " on " + node));
		dialog.pack();
		dialog.setVisible(true);
		
		if (operation.setup(node)) {
			node.accept(operation);
			operation.finalize(node);
			tree.expandRow(0);
		}
		
		dialog.setVisible(false);
		dialog.dispose();
		
	}
	
	
	
}
