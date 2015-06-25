package iSBatch;

import filters.NodeFilterInterface;
import gui.DatabaseDialog;
import gui.DatabaseTreeCellRenderer;
import gui.AboutPanel;
import gui.LogPanel;
import gui.OperationButton;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.tmatesoft.sqljet.core.SqlJetException;

import macros.MacroOperation;
import model.Database;
import model.DatabaseModel;
import model.Node;
import model.PropertiesTableGui;
import model.parameters.NodeType;
import context.ContextHandler;
import operations.AddNodeOperation;
import operations.ImportOperation;
import operations.Operation;
import operations.cellIntensity.CellIntensity;
import operations.cellOutlines.CellOutlines;
import operations.cellularConcentration.CellularConcentration;
import operations.changePoint.ChangePoint;
import operations.focusLifetime.FocusLifetimes;
import operations.tracking.Tracking;
import operations.flatImages.FlattenOperation;
import operations.flatImages.SetBackGround;
import operations.locationMaps.LocationMaps;
import operations.microbeTrackerIO.MicrobeTrackerIO;
import operations.peakFinder.FindPeaksOperation;
import operations.peakFitter.PeakFitter2;

public class ISBatch_ implements TreeSelectionListener, ActionListener {
	String version = "v0.3.2-beta";
	private static ISBatch_ instance;

	/** Links to Websites */
	
	private String databaseDownloadURL = "http://singlemolecule.nl/~vcaldas/iSBatch/";
	private String openIssueURL = "https://github.com/SingleMolecule/iSBatch/issues/new";
	private String sourceCodeURL = "https://github.com/SingleMolecule/iSBatch";
	private String helpPageURL = "https://github.com/SingleMolecule/iSBatch/wiki";

	private Database database;
	private DatabaseModel treeModel;
	private ContextHandler contextHandler = new ContextHandler();
	private JTree tree;
	private DefaultListModel<Node> listModel = new DefaultListModel<Node>();
	private JList<Node> list = new JList<Node>(listModel);
	private JFrame frame = new JFrame("iSBatch");
	private JPanel treeButtonspanel = new JPanel();
	protected TreePath currentSelected;
	protected Object oldSelectedPath;

	public static void main(String[] args) {
		getInstance();
	}

	protected Node selectedNode;

	public ISBatch_() throws SqlJetException {
		DatabaseDialog dialog = new DatabaseDialog(frame);
		database = dialog.getDatabase();
		if (database == null)
			return;
		startProcess();
	}

	private void startProcess() {
		setTree();

		JPanel treePanel = createTreePanel();
		// create operations panel

		JPanel operationsPanel = createOperationsPanel();
		JPanel listPanel = createListPanel();
		layoutPanels(treePanel, operationsPanel, listPanel);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setContext(treeModel.getRoot());
			}
		});
		iSBatchPreferences.loadPreferences(treeModel.getRoot());
	}

	public static ISBatch_ getInstance() {

		if (instance == null) {
			try {
				instance = new ISBatch_();
			} catch (SqlJetException e) {
				JOptionPane.showMessageDialog(null,
						"Could not open database : " + e.getMessage(),
						"Database error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return instance;
	}

	private void setTree() {

		try {
			treeModel = new DatabaseModel(database.getRoot());
		} catch (SqlJetException e) {
			e.printStackTrace();
		}
		tree = new JTree(treeModel);
		tree.removeTreeSelectionListener(this);
		tree.addTreeSelectionListener(this);
		tree.setComponentPopupMenu(getPopUpMenu());
		tree.addMouseListener(getMouseListener());
		tree.addMouseMotionListener(getMouseMotionAdapter());
		tree.setCellRenderer(new DatabaseTreeCellRenderer());
	}

	private void layoutPanels(JPanel treePanel, JPanel operationsPanel,
			JPanel listPanel) {
		createMenus();
		frame.setLayout(new BorderLayout());
		frame.add(treePanel, BorderLayout.WEST);
		frame.add(new JScrollPane(operationsPanel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.EAST);
		frame.add(LogPanel.getInstance(), BorderLayout.SOUTH);
		frame.add(listPanel, BorderLayout.CENTER);
		frame.setSize(800, 500);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * 
	 * Defining components of Menu Bar
	 * This section will be moved when proper MVC design takes place.
	 * 
	 */
	
	private JMenuBar menuBar;
//	private JMenu preferences = new JMenu("Preferences");
	private JMenu menu;
	private JMenuItem NewtMenuItem;
	private JMenuItem LoadMenuItem;
	private JMenuItem exit;
	private JMenuItem saveMenuItem;
	
	private JMenu helpMenuBar;
	private JMenuItem prefsMenuItem;
	private JMenuItem helpMenuItem;
	private JMenuItem aboutMenuItem;
	private JMenuItem sourceMenuItem;
	private JMenuItem bugReport;
	private JMenuItem downloadDBItem;
	
	private void createMenus() {
		// iSBatchMenu iSBatchMenu = new iSBatchMenu(instance);
		// iSBatchMenu.setVersion(version);
		// frame.setJMenuBar(iSBatchMenu.getISBachMenuBar());

		// Main top MenuBar
		
		// Add Items to the main top Menu bar.
		 menuBar = new JMenuBar();
		/**
		 * Menu
		 * 	Save database
		 * 	Load database
		 * 	New Database
		 * 	Quit
		 */
		menu = new JMenu("Database");
		menuBar.add(menu); 			//Add to the top MenuBar
		
		NewtMenuItem = new JMenuItem("New Database");
		LoadMenuItem = new JMenuItem("Load Database");
		saveMenuItem = new JMenuItem("Save");
		exit = new JMenuItem("Quit");
		
		menu.add(NewtMenuItem);
		menu.add(LoadMenuItem);
		menu.add(saveMenuItem);
		menu.add(exit);
		
		NewtMenuItem.addActionListener(this);
		LoadMenuItem.addActionListener(this);
		saveMenuItem.addActionListener(this);
		exit.addActionListener(this);
		
		/**
		 * Help
		 * 	About
		 * 	Source Code
		 * 	Website
		 * 	Report issues
		 * 	Contact
		 * 	Get Datasets
		 */
		
		helpMenuBar = new JMenu("Help");
		menuBar.add(helpMenuBar);
		
		prefsMenuItem = new JMenuItem("Preferences");
		helpMenuItem = new JMenuItem("Help");
		aboutMenuItem = new JMenuItem("About");
		sourceMenuItem = new JMenuItem("Source Code");
		bugReport = new JMenuItem("Report bug");
		downloadDBItem = new JMenuItem("Download Database");
		
		helpMenuBar.add(helpMenuItem);
		helpMenuBar.add(aboutMenuItem);
		helpMenuBar.add(sourceMenuItem);
		helpMenuBar.add(bugReport);
		helpMenuBar.add(downloadDBItem);
		
		prefsMenuItem.addActionListener(this);
		helpMenuItem.addActionListener(this);
		aboutMenuItem.addActionListener(this);
		bugReport.addActionListener(this);
		sourceMenuItem.addActionListener(this);
		downloadDBItem.addActionListener(this);
		
		// preferences.add(prefsMenuItem);
		// menuBar.add(preferences);
		
		frame.setJMenuBar(menuBar);

	}

	public void openWebPage(String url) {
		try {
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (Exception e) {
			LogPanel.log(e.getMessage());
		}
	}

	private JPanel createListPanel() {
		JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.add(new JScrollPane(list), BorderLayout.CENTER);

		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() != 2)
					return;

				int index = list.locationToIndex(e.getPoint());

				if (index == -1)
					return;

				Node node = list.getModel().getElementAt(index);
				String path = node.getProperty("path");

				if (path == null)
					return;

				if (path.toLowerCase().matches(".+\\.(tif|tiff)")) {
					LogPanel.log("open " + path);
					ImagePlus imp = IJ.openImage(path);
					imp.show();
				} else if (path.toLowerCase().matches(".+\\.(csv|txt)")) {

					try {
						LogPanel.log("open " + path);
						ResultsTable table = ResultsTable.open(path);
						table.show("Results");
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(
								frame,
								"Could not open results table : "
										+ ex.getMessage());
					}
				}
			}
		});

		return listPanel;
	}

	private JPanel createTreePanel() {
		JPanel treePanel = new JPanel(new BorderLayout());
		for (Operation operation : getTreeOperations()) {
			OperationButton button = new OperationButton(treeModel, operation);
			contextHandler.getListeners().add(button);
			treeButtonspanel.add(button);
		}

		treePanel.add(treeButtonspanel, BorderLayout.SOUTH);
		treePanel.add(new JScrollPane(tree), BorderLayout.CENTER);
		treePanel.setPreferredSize(new Dimension(300, 300));

		return treePanel;
	}

	private JPanel createOperationsPanel() {
		JPanel operationsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		for (Operation operation : getOperations()) {

			OperationButton opButton = new OperationButton(treeModel, operation);
			contextHandler.getListeners().add(opButton);

			gbc.gridy++;
			operationsPanel.add(opButton, gbc);

		}
		return operationsPanel;
	}

	private MouseListener getMouseListener() {

		return new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath pathForLocation = tree.getPathForLocation(
						e.getPoint().x, e.getPoint().y);
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (pathForLocation != null) {
						selectedNode = (Node) pathForLocation
								.getLastPathComponent();

					} else {
						selectedNode = null;
					}
				}

				else if (e.getButton() == MouseEvent.BUTTON1) {
					if (e.getClickCount() == 1) {
						// System.out.println(pathForLocation.getLastPathComponent());
					} else if (e.getClickCount() == 2) {
						myDoubleClick(selRow, pathForLocation);
					}
				}
				super.mousePressed(e);
			}

			private void myDoubleClick(int selRow, TreePath pathForLocation) {
				selectedNode = (Node) pathForLocation.getLastPathComponent();
				if (selectedNode.getType().equalsIgnoreCase("File")) {
					LogPanel.log("Opening image: " + selectedNode.getPath());

					IJ.open(selectedNode.getPath());
				}
			}
		};
	}

	private MouseMotionAdapter getMouseMotionAdapter() {
		return new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());

				if (path == null || selRow < 0) {
					tree.setCursor(Cursor.getDefaultCursor());

				} else {
					if (selRow > 0) {
						path = tree.getPathForLocation(e.getX(), e.getY());
					}
				}
				tree.repaint();
			}
		};

	}

	private JPopupMenu getPopUpMenu() {

		JPopupMenu menu = new JPopupMenu();
		JMenuItem item = new JMenuItem("Open cell roi");
		item.addActionListener(getEditActionListener());
		menu.add(item);

		JMenuItem item3 = new JMenuItem("Open support Rois");
		item3.addActionListener(getEditActionListener2());
		menu.add(item3);

		JMenuItem item2 = new JMenuItem("Run Macro ...");
		item2.addActionListener(getRunMacroActionListener());
		menu.add(item2);

		JMenuItem item4 = new JMenuItem("Properties");
		item4.addActionListener(displayProperties());
		menu.add(item4);

		return menu;
	}

	private ActionListener displayProperties() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new PropertiesTableGui(selectedNode);
			}
		};
	}

	private ActionListener getEditActionListener2() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (selectedNode.getType().equalsIgnoreCase("File")) {
					String path = selectedNode.getProperty("supportRoi");
					if (path != null) {
						RoiManager manager = RoiManager.getInstance();
						if (manager == null) {
							manager = new RoiManager();
						}
						manager.runCommand("Open",
								selectedNode.getProperty("supportRoi"));
					}
				}
			}
		};
	}

	public Operation[] getTreeOperations() {
		return new Operation[] { new AddNodeOperation(frame, treeModel),
				new ImportOperation(treeModel) };
	}

	public Operation[] getOperations() {
		return new Operation[] {
				// new MacroOperation2(frame, treeModel),
				new DebugProperties(treeModel), new SetBackGround(treeModel),
				new FlattenOperation(treeModel),
				new MicrobeTrackerIO(treeModel), new CellOutlines(treeModel),
				new FindPeaksOperation(), new PeakFitter2(),
				new MacroOperation(treeModel),
				new CellularConcentration(treeModel),
				new CellIntensity(treeModel), new FocusLifetimes(treeModel),
				new Tracking(treeModel), new LocationMaps(treeModel),
				new ChangePoint(treeModel), };
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {

		Node node = (Node) tree.getLastSelectedPathComponent();

		if (node == null)
			node = (Node) tree.getModel().getRoot();

		setContext(node);

	}

	public void setContext(Node node) {

		contextHandler.setContext(node);
		listModel.clear();

		NodeFilterInterface fileNodesFilter = new NodeFilterInterface() {

			@Override
			public boolean accept(Node node) {
				return node.getType().equals(NodeType.FILE);
			}

		};

		for (Node fileNode : node.getDescendents(fileNodesFilter))
			listModel.addElement(fileNode);

	}

	private ActionListener getEditActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (selectedNode.getType().equalsIgnoreCase("File")) {
					RoiManager manager = RoiManager.getInstance();
					if (manager == null) {
						manager = new RoiManager();

					}
					manager.runCommand("Open", selectedNode.getOutputFolder()
							+ File.separator + "cellRoi.zip");

					System.out.println("pressed " + selectedNode);
				}
			}
		};
	}

	private ActionListener getRunMacroActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (selectedNode != null) {

					System.out.println("pressed on macro " + selectedNode);
				}
			}
		};
	}

	public void saveDatabase() {

		// save preferences
		iSBatchPreferences.savePreferences(treeModel.getRoot());

		try {
			database.write(treeModel.getRoot());
		} catch (SqlJetException e) {
			JOptionPane.showMessageDialog(frame, "Could not save database : "
					+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

	}

	public void newDatabase() {

		int option = JOptionPane.showConfirmDialog(frame,
				"Do you want to save the current database?");

		if (option == JOptionPane.CANCEL_OPTION)
			return;

		if (option == JOptionPane.YES_OPTION)
			saveDatabase();

		// new database or load existing database
		DatabaseDialog dialog = new DatabaseDialog(frame);
		database = dialog.getDatabase();

		if (database == null)
			return;

		try {

			treeModel.setRoot(database.getRoot());
			tree.invalidate();

		} catch (SqlJetException e) {
			JOptionPane.showMessageDialog(frame,
					"Database error : " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}

		iSBatchPreferences.loadPreferences(treeModel.getRoot());

	}

	public void loadDatabase(Database database) throws SqlJetException {

		if (treeModel == null)
			treeModel = new DatabaseModel(database.getRoot());
		else
			treeModel.setRoot(database.getRoot());

	}

	public void reLoad() {

		DatabaseDialog dialog = new DatabaseDialog(frame);
		database = dialog.getDatabase();
		if (database == null)
			return;
		else {
			try {
				loadDatabase(database);
			} catch (SqlJetException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object selectedSource = e.getSource();
		if (selectedSource == NewtMenuItem) {
			newDatabase();
		} else if (selectedSource == saveMenuItem) {
			saveDatabase();
		} else if (selectedSource == exit) {
			System.exit(0);
		} else if (selectedSource == helpMenuItem) {
			openWebPage(sourceCodeURL);
		} else if (selectedSource == bugReport) {
			openWebPage(openIssueURL);
		} else if (selectedSource == aboutMenuItem) {
			new AboutPanel(version);
		} else if (selectedSource == helpMenuItem) {
			openWebPage(helpPageURL);
		} else if (selectedSource == prefsMenuItem) {
			LogPanel.log("Set Preferences");
		} else if (selectedSource == LoadMenuItem) {
			reLoad();
		}
		else if(selectedSource == downloadDBItem){
			openWebPage(databaseDownloadURL);
		}
	}

}
