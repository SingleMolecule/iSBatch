package iSBatch;

import filters.NodeFilterInterface;
import gui.AboutPanel;
import gui.DatabaseDialog;
import gui.DatabaseTreeCellRenderer;
import gui.LogPanel;
import gui.OperationButton;
import iSBatch.iSBatchPreferences;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;

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
import model.parameters.NodeType;
import context.ContextHandler;
import operations.AddNodeOperation;
import operations.ImportOperation;
import operations.Operation;
import operations.cellIntensity.CellIntensity;
import operations.cellOutlines.CellOutlines;
import operations.cellularConcentration.CellularConcentration;
import operations.changePoint.ChangePoint;
import operations.diffusion.DiffusioOperation;
import operations.focusLifetime.FocusLifetimes;
import operations.tracking.Tracking;
import operations.flatImages.FlattenOperation;
import operations.flatImages.SetBackGround;
import operations.locationMaps.LocationMaps;
import operations.microbeTrackerIO.MicrobeTrackerIO;
import operations.peakFinder.FindPeaksOperation;
import operations.peakFitter.FitPeaksOperation;

public class ISBatch implements TreeSelectionListener {

	private static ISBatch instance;

	private Database database;
	private DatabaseModel treeModel;
	private ContextHandler contextHandler = new ContextHandler();
	private Node selectedNode;
	private JTree tree;
	private DefaultListModel<Node> listModel = new DefaultListModel<Node>();
	private JList<Node> list = new JList<Node>(listModel);
	private JFrame frame = new JFrame("iSBatch");
	private JPanel treeButtonspanel = new JPanel();
	private JMenu menu, preferences, about;
	private JMenuBar menuBar;
	private JMenuItem NewtMenuItem;
	private JMenuItem LoadMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem prefsMenuItem;
	private JMenuItem helpMenuItem;
	private JMenuItem aboutMenuItem;
	protected TreePath currentSelected;
	protected Object oldSelectedPath;
	private JMenuItem sourceMenuItem;

	public static void main(String[] args) {
		getInstance();
	}

	/*
	 * Current version:
	 */
	String version = "v0.3.1-beta";

	public ISBatch() throws SqlJetException {
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

		// load preferences
		iSBatchPreferences.loadPreferences(treeModel.getRoot());
		
	}

	public static ISBatch getInstance() {

		if (instance == null) {
			try {
				instance = new ISBatch();
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
		
//		tree.setComponentPopupMenu(getPopUpMenu());
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

	private void createMenus() {
		menuBar = new JMenuBar();

		// Add menus to "Menu"
		menu = new JMenu("Menu");
		preferences = new JMenu("Preferences");
		about = new JMenu("About");

		NewtMenuItem = new JMenuItem("New Database");
		menu.add(NewtMenuItem);
		NewtMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				LogPanel.log("New database");
				newDatabase();
			}
		});

		LoadMenuItem = new JMenuItem("Load Database");
		menu.add(LoadMenuItem);
		LoadMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				LogPanel.log("Load database");
				
				DatabaseDialog dialog = new DatabaseDialog(frame);
				database = dialog.getDatabase();
				if (database == null)
					return;
				else{
					
					startProcess();
				}
			}
		});
		saveMenuItem = new JMenuItem("Save");
		menu.add(saveMenuItem);
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				LogPanel.log("Save database");
				saveDatabase();
			}
		});
		// saveAsMenuItem = new JMenuItem("Save as...");
		// menu.add(saveAsMenuItem);
		// saveAsMenuItem.addActionListener(new ActionListener() {
		// public void actionPerformed(final ActionEvent event) {
		// LogPanel.log("Save as database");
		// }
		// });

		JMenuItem exit = new JMenuItem("Quit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				System.exit(0);
			}
		});

		// add Menu Preferences
		prefsMenuItem = new JMenuItem("Preferences");
		preferences.add(prefsMenuItem);
		prefsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				LogPanel.log("Set Preferences");
			}
		});

		// add Menu about
		helpMenuItem = new JMenuItem("Help");
		// about.add(helpMenuItem);
		helpMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				showHelp();
			}

		});

		aboutMenuItem = new JMenuItem("About");
		about.add(aboutMenuItem);
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				showAbout();
			}

		});

		sourceMenuItem = new JMenuItem("Source Code");
		about.add(sourceMenuItem);
		sourceMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				goToSourceCode();
			}

		});

		JMenuItem bugReport = new JMenuItem("Report bug");
		bugReport.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				openIssuePages();
			}

		});

		menu.add(exit);
		menuBar.add(menu);
		// menuBar.add(preferences);
		menuBar.add(about);
		menuBar.add(bugReport);
		frame.setJMenuBar(menuBar);

	}

	protected void openIssuePages() {
		try {
			Desktop.getDesktop()
					.browse(new URL(
							"https://github.com/SingleMolecule/iSBatch/issues/new")
							.toURI());
		} catch (Exception e) {
			LogPanel.log(e.getMessage());
		}

	}

	protected void goToSourceCode() {
		try {
			Desktop.getDesktop().browse(
					new URL("https://github.com/SingleMolecule/iSBatch")
							.toURI());
		} catch (Exception e) {
			LogPanel.log(e.getMessage());
		}

	}

	protected void showHelp() {
		try {
			Desktop.getDesktop().browse(
					new URL("https://github.com/SingleMolecule/iSBatch/wiki")
							.toURI());
		} catch (Exception e) {
			LogPanel.log(e.getMessage());
		}

	}

	protected void showAbout() {
		new AboutPanel(version);

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

		// add all the operations that are specified as macros
		//

		return operationsPanel;

	}

	private MouseListener getMouseListener() {

		return new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON3) {
					TreePath pathForLocation = tree.getPathForLocation(
							arg0.getPoint().x, arg0.getPoint().y);
					if (pathForLocation != null) {
						selectedNode = (Node) pathForLocation
								.getLastPathComponent();
					} else {
						selectedNode = null;
					}

				}
				super.mousePressed(arg0);
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
		JMenuItem item = new JMenuItem("edit");
		item.addActionListener(getEditActionListener());
		menu.add(item);

		JMenuItem item2 = new JMenuItem("Run Macro ...");
		item2.addActionListener(getRunMacroActionListener());
		menu.add(item2);

		return menu;
	}

	public Operation[] getTreeOperations() {
		return new Operation[] { new AddNodeOperation(frame, treeModel),
				new ImportOperation(treeModel) };
	}

	public Operation[] getOperations() {
		return new Operation[] {
				// new MacroOperation2(frame, treeModel),

				new SetBackGround(treeModel), new FlattenOperation(treeModel),
				new MicrobeTrackerIO(treeModel), new CellOutlines(treeModel),
				new FindPeaksOperation(treeModel),
				new FitPeaksOperation(treeModel),
				new MacroOperation(treeModel),
				new CellularConcentration(treeModel),
				new CellIntensity(treeModel), new FocusLifetimes(treeModel),
				new Tracking(treeModel), new DiffusioOperation(treeModel),
				new LocationMaps(treeModel), new ChangePoint(treeModel),
				// new DebugProperties(treeModel),
				new FilterTestOperation(treeModel), };
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
				if (selectedNode != null) {
					// edit here
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
					// System.out.println("Run macro on "
					// + selectedNode.getContext().getClass());
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

		// load preferences
		iSBatchPreferences.loadPreferences(treeModel.getRoot());

	}

}
