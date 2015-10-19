package iSBatch.controller.MenuItems;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class SourceMenuAction extends AbstractAction{

	private String url = "https://github.com/SingleMolecule/iSBatch";

	private OpenWebsiteAction website = new OpenWebsiteAction(url);
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
			return "Source code";
		} 
//		else if (Action.ACCELERATOR_KEY.equals(key)) {
//			return KeyStroke.getKeyStroke('L');
//		}
		return super.getValue(key);
	}
	
	
	public String getURL(){
		return url;
	}
	
	public void openWebsite(){
		website.openWebPage();
	}
	
}