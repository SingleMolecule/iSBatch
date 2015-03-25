package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import model.Node;
import operations.Operation;
import context.ContextElement;
import context.ContextListener;

public class OperationButton extends JButton implements ContextListener, ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private Operation operation;
	private ContextElement contextElement;
	
	public OperationButton(Operation operation) {
		super(operation.getName());
		this.operation = operation;
		
		addActionListener(this);
	}

	@Override
	public void contextChanged(ContextElement contextElement) {
		this.contextElement = contextElement;
		
		for (String operationContext: operation.getContext()) {
			for (String elementContext: contextElement.getContext()) {
				
				if (operationContext.equals(elementContext)) {
					setVisible(true);
					return;
				}
				
			}
		}
		
		setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Node node = (Node)contextElement;
		
		operation.setup(node);
		node.accept(operation);
		operation.finalize(node);
	}
	
}
