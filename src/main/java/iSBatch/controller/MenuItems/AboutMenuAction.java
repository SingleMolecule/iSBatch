package iSBatch.controller.MenuItems;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import gui.AboutPanel;

public class AboutMenuAction extends AbstractAction{

	String version = "v0.3.5";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		new AboutPanel(version);
	}
	
	@Override
	public Object[] getKeys() {
		return new Object[] { Action.NAME };
	}

	@Override
	public Object getValue(String key) {
		if (Action.NAME.equals(key)) {
			return "About";
		}
		return super.getValue(key);
	}
}