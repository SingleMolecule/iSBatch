package iSBatch;
// Comment
import filters.NodeFilterInterface;
import gui.DatabaseDialog;
import gui.DatabaseTreeCellRenderer;
import gui.LogPanel;
import gui.OperationButton;

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

import model.Database;
import model.DatabaseModel;
import model.FileNode;
import model.Node;
import context.ContextHandler;
import operations.*;
import operations.correlation.CorrelationOperation;
import operations.flatImages.FlattenOperation;
import operations.flatImages.SetBackGround;
import operations.microbeTrackerIO.MicrobeTrackerIO;
import operations.peakFinder.FindPeaksOperation;
import operations.peakFitter.FitPeaksOperation;

public class ISBatch_ implements TreeSelectionListener {

	private static ISBatch_ instance;
	
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

	/** The menu bar. */
	private JMenuBar menuBar;

	/** The Newt menu item. */
	private JMenuItem NewtMenuItem;

	/** The Loat menu item. */
	private JMenuItem LoadMenuItem;

	/** The save menu item. */
	private JMenuItem saveMenuItem;

	/** The save as menu item. */
	private JMenuItem saveAsMenuItem;

	/** The prefs menu item. */
	private JMenuItem prefsMenuItem;

	/** The help menu item. */
	private JMenuItem helpMenuItem;

	/** The about menu item. */
	private JMenuItem aboutMenuItem;
	protected TreePath currentSelected;
	protected Object oldSelectedPath;
	private JMenuItem sourceMenuItem;
	
	public static void main(String[] args) {
		getInstance();
	}

	protected ISBatch_() throws SqlJetException {
		DatabaseDialog dialog = new DatabaseDialog(frame);
		database = dialog.getDatabase();

		if (database == null)
			return;

		setTree();

		display(tree);
		
	}
	
	public static ISBatch_ getInstance() {
		
		if (instance == null) {
			try {
				instance = new ISBatch_();
			}
			catch (SqlJetException e) {
				JOptionPane.showMessageDialog(null, "Could not open database : " + e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return instance;
	}

	private void reload(){
		DatabaseDialog dialog = new DatabaseDialog(frame);
		database = dialog.getDatabase();
		
		
		setTree();

		display(tree);
	}
	
	private void setTree() {
		try {
			treeModel = new DatabaseModel(database.getRoot());
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tree = new JTree(treeModel);
		
		tree.addTreeSelectionListener(this);

//		TreePopup popupMenu = new TreePopup(tree);
		
		tree.setComponentPopupMenu(getPopUpMenu());
		tree.addMouseListener(getMouseListener());
		tree.addMouseMotionListener(getMouseMotionAdapter());
		
		tree.setCellRenderer(new DatabaseTreeCellRenderer());
		
	}

	private void display(JTree tree) {
		JPanel treePanel = createTreePanel();
		// create operations panel

		JPanel operationsPanel = createOperationsPanel();
		JPanel listPanel = createListPanel();
		layoutPanels(treePanel,operationsPanel,listPanel);
		
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	setContext(treeModel.getRoot());
		    }
		});
		
	}
	

	private void layoutPanels(JPanel treePanel, JPanel operationsPanel,
			JPanel listPanel) {
		createMenus();
		frame.setLayout(new BorderLayout());
		frame.add(treePanel, BorderLayout.WEST);
		frame.add(new JScrollPane(operationsPanel), BorderLayout.EAST);
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
				System.out.println("New database");
				LogPanel.log("New database");
			}
		});

		LoadMenuItem = new JMenuItem("Load Database");
		menu.add(LoadMenuItem);
		LoadMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				LogPanel.log("Load database");
				reload();
			}
		});
		saveMenuItem = new JMenuItem("Save");
		menu.add(saveMenuItem);
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				LogPanel.log("Save database");
				new SaveDatabaseOperation(database, treeModel.getRoot());
				System.out.println("Saved.");
			}
		});
		saveAsMenuItem = new JMenuItem("Save as...");
		menu.add(saveAsMenuItem);
		saveAsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				LogPanel.log("Save as database");
			}
		});

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
		about.add(helpMenuItem);
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
		
		
		

		menu.add(exit);
		menuBar.add(menu);
		menuBar.add(preferences);
		menuBar.add(about);
		frame.setJMenuBar(menuBar);

	}

	protected void goToSourceCode() {
		  try 
	        {
	            Desktop.getDesktop().browse(new URL("http://vcaldas.github.io/iSBatch/").toURI());
	        }           
	        catch (Exception e) {}
		
	}

	protected void showHelp() {
        try 
        {
            Desktop.getDesktop().browse(new URL("http://www.google.com").toURI());
        }           
        catch (Exception e) {}
		
	}
	protected void showAbout() {
		JFrame AboutFrame = new JFrame("About iSBatch");
		//JPanel AboutPanel = new AboutPanel();
		
		AboutFrame.setLayout(new BorderLayout());
		//AboutFrame.add(AboutPanel, BorderLayout.WEST);
		

		
	}

	private JPanel createListPanel() {
		JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.add(new JScrollPane(list), BorderLayout.CENTER);
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

	private MouseMotionAdapter getMouseMotionAdapter(){
		return new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				
				if (path == null || selRow < 0 ) {
					tree.setCursor(Cursor.getDefaultCursor());
                   
				} else {
					if(selRow >0){
					path = 	tree.getPathForLocation(e.getX(), e.getY());
						
						
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
		return new Operation[] { 
				new AddNodeOperation(frame, treeModel),
				new SaveDatabaseOperation(database, treeModel.getRoot()), };
	}

	public Operation[] getOperations() {
		return new Operation[] {
				new MacroOperation(frame, treeModel),
				new MacroOperation2(frame, treeModel),
				
				new SetBackGround(treeModel),
				new FlattenOperation(treeModel),
				new FindPeaksOperation(treeModel),
				new FitPeaksOperation(treeModel),
				new MicrobeTrackerIO(treeModel),
				new CorrelationOperation(treeModel),
//				new LocationMapsOperation(treeModel),
//				new CellIntensityOperation(treeModel),
				new CellOutlines(treeModel),
				 };
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
				return node.getType().equals(FileNode.type);
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
//					System.out.println("Run macro on "
//							+ selectedNode.getContext().getClass());
					System.out.println("pressed on macro " + selectedNode);
				}
			}
		};
	}

}
