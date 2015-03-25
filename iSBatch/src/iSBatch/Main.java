package iSBatch;

import gui.DatabaseDialog;
import gui.DatabaseTreeCellRenderer;
import gui.LogPanel;
import gui.OperationButton;

import java.awt.BorderLayout;
import java.awt.Cursor;
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

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
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
import model.NodeFilter;
import context.ContextHandler;
import operations.AddOperation;
import operations.FlattenOperation;
import operations.MacroOperation;
import operations.MacroOperation2;
import operations.Operation;
import operations.SaveDatabaseOperation;

public class Main implements TreeSelectionListener {

	private Database database;
	private DatabaseModel treeModel;
	private ContextHandler contextHandler = new ContextHandler();

	private Node selectedNode;

	private JTree tree;
	private DefaultListModel<Node> listModel = new DefaultListModel<Node>();
	private JList<Node> list = new JList<Node>(listModel);
	private JFrame frame = new JFrame("iSBTools");
	private JPanel treeButtonspanel = new JPanel();
	
	public static void main(String[] args) {

		try {
			new Main();
		} catch (SqlJetException e) {
			e.printStackTrace();
		}
	}

	public Main() throws SqlJetException {
		System.out.println("Im here.");
		DatabaseDialog dialog = new DatabaseDialog(frame);
		database = dialog.getDatabase();

		if (database == null)
			return;

		treeModel = new DatabaseModel(database.read());
		tree = new JTree(treeModel);
		tree.addTreeSelectionListener(this);

//		TreePopup popupMenu = new TreePopup(tree);
		
		tree.setComponentPopupMenu(getPopUpMenu());
		tree.addMouseListener(getMouseListener());

		tree.setCellRenderer(new DatabaseTreeCellRenderer());

		// add hand selection . Not intentioned, but who care.s

		tree.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int x = (int) e.getPoint().getX();
				int y = (int) e.getPoint().getY();
				TreePath path = tree.getPathForLocation(x, y);
				if (path == null) {
					tree.setCursor(Cursor.getDefaultCursor());
				} else {
					tree.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
			}
		});

		// create tree panel

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
		frame.setLayout(new BorderLayout());
		frame.add(treePanel, BorderLayout.WEST);
		frame.add(new JScrollPane(operationsPanel), BorderLayout.EAST);
		frame.add(LogPanel.getInstance(), BorderLayout.SOUTH);
		frame.add(listPanel, BorderLayout.CENTER);
		frame.setSize(800, 500);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		
	}

	private JPanel createListPanel() {
		JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.add(new JScrollPane(list), BorderLayout.CENTER);
		return listPanel;
	}

	private JPanel createTreePanel() {
		JPanel treePanel = new JPanel(new BorderLayout());
		for (Operation operation : getTreeOperations()) {
			OperationButton button = new OperationButton(operation);
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

			OperationButton opButton = new OperationButton(operation);
			contextHandler.getListeners().add(opButton);

			gbc.gridy++;
			operationsPanel.add(opButton, gbc);

		}
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

	private Operation[] getTreeOperations() {
		return new Operation[] { 
				new AddOperation(frame, treeModel),
				new SaveDatabaseOperation(database, treeModel.getRoot()), };
	}

	private Operation[] getOperations() {
		return new Operation[] {
				new MacroOperation(frame, treeModel),
				new MacroOperation2(frame, treeModel),
				new FlattenOperation(treeModel),
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

		NodeFilter fileNodesFilter = new NodeFilter() {

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
