package iSBatch.controller.MenuItems;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class DownloadDatabaseMenuAction extends AbstractAction{

	private String databaseDownloadURL = "http://singlemolecule.nl/~vcaldas/iSBatch/";
	private OpenWebsiteAction website = new OpenWebsiteAction(databaseDownloadURL);
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		website.openWebPage();
	}
	
	@Override
	public Object[] getKeys() {
		return new Object[] { Action.NAME };
	}

	@Override
	public Object getValue(String key) {
		if (Action.NAME.equals(key)) {
			return "Download DB";
		} 
		return super.getValue(key);
	}
}