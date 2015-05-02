/*
 * 
 */
package test;

import ij.IJ;
import ij.Macro;
import ij.gui.GenericDialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map.Entry;

import gui.CodeTextPane;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import operations.Operation;
import macros.MacroOperation;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.Sample;

// TODO: Auto-generated Javadoc
/**
 * The Class MacroOperationTest.
 */
public class MacroOperationTest implements ActionListener, Runnable {

	/** The frame. */
	private JFrame frame = new JFrame("Macro Operation Test");
	
	/** The model. */
	private DatabaseModel model;
	
	/** The tree. */
	private JTree tree;
	
	/** The text pane. */
	private CodeTextPane textPane = new CodeTextPane(); 
	
	/** The add node button. */
	private JButton addNodeButton = new JButton("Add");
	
	/** The remove node button. */
	private JButton removeNodeButton = new JButton("Remove");
	
	/** The edit button. */
	private JButton editButton = new JButton("Edit");
	
	/** The load button. */
	private JButton loadButton = new JButton("Load");
	
	/** The save button. */
	private JButton saveButton = new JButton("Save");
	
	/** The run button. */
	private JButton runButton = new JButton("Run");
	
	/** The stop button. */
	private JButton stopButton = new JButton("Stop");
	
	/** The macro thread. */
	private Thread macroThread;
	
	/**
	 * Instantiates a new macro operation test.
	 *
	 * @param model the model
	 */
	public MacroOperationTest(DatabaseModel model) {
		this.model = model;
		this.tree = new JTree(model);
		
		addNodeButton.addActionListener(this);
		removeNodeButton.addActionListener(this);
		editButton.addActionListener(this);
		loadButton.addActionListener(this);
		saveButton.addActionListener(this);
		runButton.addActionListener(this);
		stopButton.addActionListener(this);
				
		JPanel treeButtonPanel = new JPanel();
		treeButtonPanel.add(addNodeButton);
		treeButtonPanel.add(removeNodeButton);
		treeButtonPanel.add(editButton);
		
		JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.add(treeButtonPanel, BorderLayout.SOUTH);
		treePanel.add(new JScrollPane(tree), BorderLayout.CENTER);
		
		JPanel codeButtonPanel = new JPanel();
		codeButtonPanel.add(loadButton);
		codeButtonPanel.add(saveButton);
		codeButtonPanel.add(runButton);
		codeButtonPanel.add(stopButton);
		
		JPanel codePanel = new JPanel(new BorderLayout());
		codePanel.add(codeButtonPanel, BorderLayout.SOUTH);
		codePanel.add(textPane, BorderLayout.CENTER);
		
		frame.setLayout(new BorderLayout());
		frame.add(treePanel, BorderLayout.WEST);
		frame.add(codePanel, BorderLayout.CENTER);
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		textPane.setText(readTestMacro());
	}
	
	/**
	 * Read test macro.
	 *
	 * @return the string
	 */
	public String readTestMacro() {
		
		String macro = "";
		
		// set test code
		try {
			InputStream is = getClass().getResource("/test/testMacro.ijm").openStream();

			int length;
			byte[] b = new byte[1024];
			
			while ((length = is.read(b)) != -1) {
				macro += new String(b, 0, length);
				System.out.println(length);
			}

			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(macro);
		return macro;
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		DatabaseModel model = TreeGenerator.generate("e:\\test", "e:\\temp", 2);
		new MacroOperationTest(model);
	}


	/**
	 * Run.
	 */
	@Override
	public void run() {
		runButton.setEnabled(false);
		
		Node node = (Node)tree.getLastSelectedPathComponent();
		
		if (node == null)
			node = model.getRoot();
		
		String macro = textPane.getText();
		MacroOperation op = new MacroOperation();
		op.runMacro(macro, node);
		
		runButton.setEnabled(true);
	}


	/**
	 * Action performed.
	 *
	 * @param e the e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == addNodeButton)
			addNode();
		else if (e.getSource() == removeNodeButton)
			removeNode();
		else if (e.getSource() == editButton)
			editNode();
		else if (e.getSource() == loadButton)
			loadMacro();
		else if (e.getSource() == saveButton)
			saveMacro();
		else if (e.getSource() == runButton)
			runMacro();
		else if (e.getSource() == stopButton)
			stopMacro();
		
	}
	
	/**
	 * Adds the node.
	 */
	public void addNode() {
		
		Node parent = (Node)tree.getLastSelectedPathComponent();
		
		if (parent == null) {
			IJ.showMessage("No node selected");
			return;
		}
		
		String[] nodeTypes = new String[] {
			Experiment.type,
			Sample.type,
			FieldOfView.type,
			FileNode.type
		};
		
		int properties = 5;
		
		GenericDialog dialog = new GenericDialog("add node");
		dialog.addChoice("node_type", nodeTypes, nodeTypes[0]);
		dialog.addStringField("name", "default name");
		
		for (int i = 0; i < properties; i++) {
			dialog.addStringField("property_" + i + "_name", "");
			dialog.addStringField("property_" + i + "_value", "");
		}
		
		dialog.showDialog();
		
		if (!dialog.wasCanceled()) {
			String type = dialog.getNextChoice();
			String name = dialog.getNextString();
			
			Node node = new Node(parent, type) {
				@Override
				public void accept(Operation operation) {
				}

				@Override
				public int getNumberOfFoV() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public ArrayList<FieldOfView> getFieldOfView() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public ArrayList<Sample> getSamples() {
					// TODO Auto-generated method stub
					return null;
				}
			};
			
			node.setProperty("name", name);
			
			for (int i = 0; i < properties; i++) {
				String key = dialog.getNextString();
				String value = dialog.getNextString();
				
				if (!key.isEmpty()) {
					node.setProperty(key, value);
				}
				
			}
			
			model.addNode(parent, node);
		}
		
	}
	
	/**
	 * Removes the node.
	 */
	public void removeNode() {
		Node node = (Node)tree.getLastSelectedPathComponent();
		
		if (node == null) {
			IJ.showMessage("No node selected");
			return;
		}
		
		model.removeNode(node.getParent(), node);
	}
	
	/**
	 * Edits the node.
	 */
	public void editNode() {
		
		Node node = (Node)tree.getLastSelectedPathComponent();
		
		String[] nodeTypes = new String[] {
			Experiment.type,
			Sample.type,
			FieldOfView.type,
			FileNode.type
		};
		
		int properties = 2;
		
		GenericDialog dialog = new GenericDialog("add node");
		dialog.addChoice("node_type", nodeTypes, node.getType());
		
		for (Entry<String, String> entry: node.getProperties().entrySet()) {
			dialog.addStringField(entry.getKey(), entry.getValue());
		}
		
		for (int i = 0; i < properties; i++) {
			dialog.addStringField("property_" + i + "_name", "");
			dialog.addStringField("property_" + i + "_value", "");
		}
		
		dialog.showDialog();
		
		if (!dialog.wasCanceled()) {
			String type = dialog.getNextChoice();
			
			node.setType(type);
			
			for (String name: node.getProperties().keySet()) {
				String value = dialog.getNextString();
				node.setProperty(name, value);
			}
			
			for (int i = 0; i < properties; i++) {
				String key = dialog.getNextString();
				String value = dialog.getNextString();
				
				if (!key.isEmpty()) {
					node.setProperty(key, value);
				}
			}
			
		}
		
	}
	
	/**
	 * Load macro.
	 */
	public void loadMacro() {
		
		String macro = IJ.openAsString(null);
		
		if (macro != null)
			textPane.setText(macro);
		
	}
	
	/**
	 * Save macro.
	 */
	public void saveMacro() {
		IJ.saveString(textPane.getText(), null);
	}
	
	/**
	 * Run macro.
	 */
	public void runMacro() {
		macroThread = new Thread(this);
		macroThread.start();
	}
	
	/**
	 * Stop macro.
	 */
	public void stopMacro() {
		Macro.abort();
		runButton.setEnabled(true);
		macroThread = null;
	}
	

}
