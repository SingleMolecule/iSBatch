package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import model.DatabaseModel;
import model.Node;
import operations.Operation;
import operations.OperationRunner;
import context.ContextElement;
import context.ContextListener;

public class OperationButton extends JButton implements ContextListener, ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private DatabaseModel model;
	private Operation operation;
	private ContextElement contextElement;
	
	public OperationButton(DatabaseModel model, Operation operation) {
		super(operation.getName());
		this.model = model;
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
		new OperationRunner(model, operation, (Node)contextElement);
	}
	
}
