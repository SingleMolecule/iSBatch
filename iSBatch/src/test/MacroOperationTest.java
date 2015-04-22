package test;

import ij.IJ;
import ij.Macro;
import ij.gui.GenericDialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
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

public class MacroOperationTest implements ActionListener, Runnable {

	private JFrame frame = new JFrame("Macro Operation Test");
	private DatabaseModel model;
	private JTree tree;
	private CodeTextPane textPane = new CodeTextPane(); 
	private JButton addNodeButton = new JButton("Add");
	private JButton removeNodeButton = new JButton("Remove");
	private JButton editButton = new JButton("Edit");
	private JButton loadButton = new JButton("Load");
	private JButton saveButton = new JButton("Save");
	private JButton runButton = new JButton("Run");
	private JButton stopButton = new JButton("Stop");
	private Thread macroThread;
	
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
	
	public static void main(String[] args) {
		DatabaseModel model = TreeGenerator.generate("e:\\test", "e:\\temp", 2);
		new MacroOperationTest(model);
	}


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
	
	public void removeNode() {
		Node node = (Node)tree.getLastSelectedPathComponent();
		
		if (node == null) {
			IJ.showMessage("No node selected");
			return;
		}
		
		model.removeNode(node.getParent(), node);
	}
	
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
	
	public void loadMacro() {
		
		String macro = IJ.openAsString(null);
		
		if (macro != null)
			textPane.setText(macro);
		
	}
	
	public void saveMacro() {
		IJ.saveString(textPane.getText(), null);
	}
	
	public void runMacro() {
		macroThread = new Thread(this);
		macroThread.start();
	}
	
	public void stopMacro() {
		Macro.abort();
		runButton.setEnabled(true);
		macroThread = null;
	}
	

}
