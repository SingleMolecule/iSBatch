package iSBatch.controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

public class SaveDatabaseAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Save the database.");
	}
	
	@Override
	public Object[] getKeys() {
		return new Object[] { Action.NAME };
	}

	@Override
	public Object getValue(String key) {
		if (Action.NAME.equals(key)) {
			return "Save";
		} else if (Action.ACCELERATOR_KEY.equals(key)) {
			return KeyStroke.getKeyStroke('S');
		}
		return super.getValue(key);
	}

}
