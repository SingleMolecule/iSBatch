package iSBatch.controller.MenuItems;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class WebsiteMenuAction extends AbstractAction {

	private final String url = "http://singlemolecule.github.io/iSBatch/";
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
			return "Website";
		} 
		return super.getValue(key);
	}
	
	public String getURL(){
		return url;
	}
	
	public void openWebsite(){
		website.openWebPage();
	}
	
}