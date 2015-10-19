package iSBatch.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;

import iSBatch.controller.ClearListModelAction;
import iSBatch.controller.LoadDatabaseAction;
import iSBatch.controller.LoadPersonsAction;
import iSBatch.controller.NewDatabaseAction;
import iSBatch.controller.SaveDatabaseAction;
import iSBatch.controller.SwingWorkerProgressModel;
import iSBatch.controller.MenuItems.AboutMenuAction;
import iSBatch.controller.MenuItems.DownloadDatabaseMenuAction;
import iSBatch.controller.MenuItems.HelpMenuAction;
import iSBatch.controller.MenuItems.ReportBugMenuAction;
import iSBatch.controller.MenuItems.SourceMenuAction;
import iSBatch.data.Person;
import iSBatch.model.list.ListAdapterListModel;

public class MainFrame extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 4353611743416911021L;

	private ListAdapterListModel<Person> personListModel = new ListAdapterListModel<Person>();

	private SwingWorkerProgressModel swingWorkerProgressModel = new SwingWorkerProgressModel();
	private JProgressBar progressBar = new JProgressBar(swingWorkerProgressModel);

	private SwingWorkerBasedComponentVisibility swingWorkerBasedComponentVisibility = new SwingWorkerBasedComponentVisibility(
			progressBar);

	private OverviewPanel overviewPanel = new OverviewPanel();
	private LoadSpeedSimulationPanel loadSpeedSimulationPanel = new LoadSpeedSimulationPanel();

	private Component currentContent;

	public MainFrame() {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		progressBar.setStringPainted(true);
		overviewPanel.setPersonList(personListModel);

		setContent(overviewPanel);
		getContentPane().add(loadSpeedSimulationPanel, BorderLayout.NORTH);
		getContentPane().add(progressBar, BorderLayout.SOUTH);

		JMenuBar jMenuBar = new JMenuBar();
		setJMenuBar(jMenuBar);
		initMenu(jMenuBar);

		setSize(605, 660);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("iSBatch - 0.5");
		setLocationRelativeTo(null);
	}

	private void initMenu(JMenuBar jMenuBar) {
		initFileMenu(jMenuBar);
	}

	private void initFileMenu(JMenuBar jMenuBar) {
		/**
		 * 
		 * File menu Item - File New Save Save as Load Clear
		 * 
		 * 
		 */

		JMenu fileMenu = new JMenu("File");
		jMenuBar.add(fileMenu);

		LoadPersonsAction loadPersonsAction = new LoadPersonsAction(personListModel);
		loadPersonsAction.addSwingWorkerPropertyChangeListener(swingWorkerProgressModel);
		loadPersonsAction.addSwingWorkerPropertyChangeListener(swingWorkerBasedComponentVisibility);
		loadPersonsAction.setLoadSpeedModel(loadSpeedSimulationPanel.getPersonsLoadSpeedModel());

		JMenuItem loadMenuItem = new JMenuItem(loadPersonsAction);
		fileMenu.add(loadMenuItem);

		NewDatabaseAction newDatabaseAction = new NewDatabaseAction();
		JMenuItem newDatabaseMenuItem = new JMenuItem(newDatabaseAction);
		fileMenu.add(newDatabaseMenuItem);

		SaveDatabaseAction saveDatabaseAction = new SaveDatabaseAction();
		JMenuItem saveDatabaseMenuItem = new JMenuItem(saveDatabaseAction);
		fileMenu.add(saveDatabaseMenuItem);

		LoadDatabaseAction loadDatabaseAction = new LoadDatabaseAction();
		JMenuItem loadDatabaseMenuItem = new JMenuItem(loadDatabaseAction);
		fileMenu.add(loadDatabaseMenuItem);

		ClearListModelAction clearPersonsModelAction = new ClearListModelAction(personListModel);
		JMenuItem clearPersonsMenuItem = new JMenuItem(clearPersonsModelAction);
		fileMenu.add(clearPersonsMenuItem);
		
			

		/*
		 * Help Menu -Help Help About Source Report bug Download DB
		 */
		JMenu helpMenu = new JMenu("Help");
		jMenuBar.add(helpMenu);

		HelpMenuAction helpMenuAction = new HelpMenuAction();
		JMenuItem helpMenuItem = new JMenuItem(helpMenuAction);
		helpMenu.add(helpMenuItem);

		AboutMenuAction abouMenuAction = new AboutMenuAction();
		JMenuItem aboutMenuItem = new JMenuItem(abouMenuAction);
		helpMenu.add(aboutMenuItem);

		SourceMenuAction sourceMenuAction = new SourceMenuAction();
		JMenuItem sourceMenuItem = new JMenuItem(sourceMenuAction);
		helpMenu.add(sourceMenuItem);

		ReportBugMenuAction reportMenuAction = new ReportBugMenuAction();
		JMenuItem reportMenuItem = new JMenuItem(reportMenuAction);
		helpMenu.add(reportMenuItem);

		DownloadDatabaseMenuAction downloadMenuAction = new DownloadDatabaseMenuAction();
		JMenuItem downloadMenuItem = new JMenuItem(downloadMenuAction);
		helpMenu.add(downloadMenuItem);

	}

	public void setContent(Component component) {
		Container contentPane = getContentPane();
		if (currentContent != null) {
			contentPane.remove(currentContent);
		}
		contentPane.add(component, BorderLayout.CENTER);
		currentContent = component;
		contentPane.doLayout();
		repaint();
	}

}
