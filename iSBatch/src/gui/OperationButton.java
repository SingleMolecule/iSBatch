/*
 * 
 */
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

// TODO: Auto-generated Javadoc
/**
 * The Class OperationButton.
 */
public class OperationButton extends JButton implements ContextListener, ActionListener {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The model. */
	private DatabaseModel model;
	
	/** The operation. */
	private Operation operation;
	
	/** The context element. */
	private ContextElement contextElement;
	
	/**
	 * Instantiates a new operation button.
	 *
	 * @param model the model
	 * @param operation the operation
	 */
	public OperationButton(DatabaseModel model, Operation operation) {
		super(operation.getName());
		this.model = model;
		this.operation = operation;
		
		addActionListener(this);
	}

	/**
	 * Context changed.
	 *
	 * @param contextElement the context element
	 */
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

	/**
	 * Action performed.
	 *
	 * @param e the e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		new OperationRunner(model, operation, (Node)contextElement);
	}
	
}
