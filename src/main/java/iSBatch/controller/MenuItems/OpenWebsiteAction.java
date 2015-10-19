package iSBatch.controller.MenuItems;

import java.awt.Desktop;
import java.net.URL;

import gui.LogPanel;

public class OpenWebsiteAction {

	private String url;

	public OpenWebsiteAction(String url) {
		this.url = url;
	}

	public void openWebPage() {
		try {
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (Exception e) {
			LogPanel.log(e.getMessage());
		}
	}

	public void openWebPage(String url) {
		try {
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (Exception e) {
			LogPanel.log(e.getMessage());
		}
	}

}
