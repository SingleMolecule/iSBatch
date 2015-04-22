package operations;


import javax.swing.JDialog;
import javax.swing.JLabel;

import model.DatabaseModel;
import model.Node;
import model.OperationNode;

public class OperationRunner implements Runnable {

	private DatabaseModel model;
	private Operation operation;
	private Node node;
	
	public OperationRunner(DatabaseModel model, Operation operation, Node node) {
		super();
		this.operation = operation;
		this.node = node;
		this.model = model;
		
		new Thread(this).start();
	}

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
