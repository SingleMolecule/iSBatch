/*
 * 
 */
package operations;


import javax.swing.JDialog;
import javax.swing.JLabel;

import model.DatabaseModel;
import model.Node;
import model.OperationNode;

// TODO: Auto-generated Javadoc
/**
 * The Class OperationRunner.
 */
public class OperationRunner implements Runnable {

	/** The operation. */
	private Operation operation;
	
	/** The node. */
	private Node node;
	
	/**
	 * Instantiates a new operation runner.
	 *
	 * @param model the model
	 * @param operation the operation
	 * @param node the node
	 */
	public OperationRunner(DatabaseModel model, Operation operation, Node node) {
		super();
		this.operation = operation;
		this.node = node;
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
		}
		
		// create an operation node
		OperationNode operationNode = new OperationNode(node);
		operationNode.setProperty("name", operation.getName());
		System.out.println(operation.getName());
//		System.out.println(operation.getParameters().toString());
//		for (Entry<String, String> entry: operation.getParameters().entrySet())
//			operationNode.setProperty(entry.getKey(), entry.getValue());
		
//		model.addNode(node, operationNode);
//		
//		for (Node child: operation.getCreatedNodes())
//			model.addNode(operationNode, child);
		
		dialog.setVisible(false);
		dialog.dispose();
		
	}
	
	
	
}
