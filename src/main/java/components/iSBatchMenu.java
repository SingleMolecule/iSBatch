package components;

import gui.AboutPanel;
import gui.LogPanel;
import iSBatch.ISBatch_;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class iSBatchMenu implements ActionListener {

	//TODO: The MenuBar should be separated from the rest of the software. Better MCV practices should be implemented before this step.
	

	/** Website links */

	// Issues page
	String issuesPageURL = "https://github.com/SingleMolecule/iSBatch/issues/new";
	String sourceCodeURL = "https://github.com/SingleMolecule/iSBatch";
	String helpPageURL = "https://github.com/SingleMolecule/iSBatch/wiki";
	String downloadFilesURL = "https://github.com/SingleMolecule/iSBatch/wiki";

	private JMenuBar menuBar = new JMenuBar();
	private JMenu menu = new JMenu("Menu");
	private JMenu preferences = new JMenu("Preferences");
	private JMenu about = new JMenu("About");

	private JMenuItem NewtMenuItem = new JMenuItem("New Database");
	private JMenuItem LoadMenuItem = new JMenuItem("Load Database");
	private JMenuItem saveMenuItem = new JMenuItem("Save");;
	private JMenuItem prefsMenuItem = new JMenuItem("Preferences");;
	private JMenuItem helpMenuItem = new JMenuItem("Help");;
	private JMenuItem aboutMenuItem = new JMenuItem("About");
	private JMenuItem exit = new JMenuItem("Quit");
	private JMenuItem bugReport = new JMenuItem("Report bug");
	private JMenuItem sourceMenuItem = new JMenuItem("Source Code");

	private String version;

	private ISBatch_ isbatch;

	public iSBatchMenu(ISBatch_ instance) {
		this.isbatch = instance;
		NewtMenuItem.addActionListener(this);
		LoadMenuItem.addActionListener(this);
		saveMenuItem.addActionListener(this);
		prefsMenuItem.addActionListener(this);
		helpMenuItem.addActionListener(this);
		aboutMenuItem.addActionListener(this);
		exit.addActionListener(this);
		sourceMenuItem.addActionListener(this);
		bugReport.addActionListener(this);

		menu.add(NewtMenuItem);
		menu.add(LoadMenuItem);
		menu.add(saveMenuItem);
		menu.add(exit);

		preferences.add(prefsMenuItem);
		about.add(aboutMenuItem);
		about.add(sourceMenuItem);

		menuBar.add(menu);
		menuBar.add(about);
		menuBar.add(bugReport);
	}

	public void actionPerformed(ActionEvent e) {
		Object selectedSource = e.getSource();
		if (selectedSource == NewtMenuItem) {
			isbatch.newDatabase();
		} else if (selectedSource == saveMenuItem) {
			isbatch.saveDatabase();
		} else if (selectedSource == exit) {
			System.exit(0);
		} else if (selectedSource == about) {
			goToSourceCode();
		} else if (selectedSource == bugReport) {
			openIssuePages();
		} else if (selectedSource == aboutMenuItem) {
			showAbout();
		} else if (selectedSource == helpMenuItem) {
			showHelp();
		} else if (selectedSource == prefsMenuItem) {
			LogPanel.log("Set Preferences");
		} else if (selectedSource == LoadMenuItem) {
//			isbatch.reLoad();

		}
	}

	public JMenuBar getISBachMenuBar() {
		return menuBar;
	}

	protected void openIssuePages() {
		try {
			Desktop.getDesktop().browse(new URL(issuesPageURL).toURI());
		} catch (Exception e) {
			LogPanel.log(e.getMessage());
		}
	}

	protected void goToSourceCode() {
		try {
			Desktop.getDesktop().browse(new URL(sourceCodeURL).toURI());
		} catch (Exception e) {
			LogPanel.log(e.getMessage());
		}
	}

	protected void showHelp() {
		try {
			Desktop.getDesktop().browse(new URL(helpPageURL).toURI());
		} catch (Exception e) {
			LogPanel.log(e.getMessage());
		}
	}
	
	protected void showAbout() {
		new AboutPanel(version);

	}

	public void setVersion(String version) {
		this.version = version;
	}

}
