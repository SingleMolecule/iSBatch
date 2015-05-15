/*
 * 
 */
package gui;

import filters.NodeFilterInterface;
import ij.IJ;
import ij.Macro;
import ij.macro.Interpreter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import test.TreeGenerator;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.Root;
import model.Sample;

// TODO: Auto-generated Javadoc
/**
 * The Class RunMacroDialog.
 */
public class RunMacroDialog extends JDialog implements ActionListener, Runnable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The channels. */
	private String[] channels = new String[] {
			"acquisition",
			"bf",
			"red",
			"green",
			"blue",
	};
	
	/** The levels. */
	private String[] levels = new String[] {
			Root.type,
			Experiment.type,
			Sample.type,
			FieldOfView.type,
			FileNode.type
	};
	
	/** The macro filename text field. */
	private JTextField macroFilenameTextField = new JTextField(20);
	
	/** The choose button. */
	private JButton chooseButton = new JButton("Choose");
	
	/** The macro text pane. */
	private CodeTextPane macroTextPane = new CodeTextPane();
	
	/** The channel combo box. */
	private JComboBox<String> channelComboBox = new JComboBox<String>(channels);
	
	/** The run for each combo box. */
	private JComboBox<String> runForEachComboBox = new JComboBox<String>(levels);
	
	/** The filename contains text field. */
	private JTextField filenameContainsTextField = new JTextField(20);
	
	/** The save button. */
	private JButton saveButton = new JButton("Save");
	
	/** The run button. */
	private JButton runButton = new JButton("Run");
	
	/** The stop button. */
	private JButton stopButton = new JButton("Stop");
	
	/** The selected file. */
	private File selectedFile;
	
	/** The node. */
	private Node node;
	
	/** The model. */
	private DatabaseModel model;
	
	/** The is running. */
	private boolean isRunning = false;
	
	/**
	 * Instantiates a new run macro dialog.
	 *
	 * @param parent the parent
	 * @param model the model
	 * @param node the node
	 */
	public RunMacroDialog(JFrame parent, DatabaseModel model, Node node) {
		super(parent, "Run Macro", true);
		
		this.model = model;
		this.node = node;
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		add(new JLabel("Macro filename"), gbc);
		
		gbc.gridx++;
		
		add(macroFilenameTextField, gbc);
		
		gbc.gridx++;
		
		add(chooseButton, gbc);
		chooseButton.addActionListener(this);
		
		gbc.gridx = 0;
		gbc.gridy++;
		
		add(new JLabel("Macro content"), gbc);
		
		gbc.gridy++;
		gbc.gridwidth = 3;
		
		
		add(macroTextPane, gbc);
		
		gbc.gridy++;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		add(new JLabel("Run for channel"), gbc);
		
		gbc.gridx++;
		add(channelComboBox, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 1;
		
		add(new JLabel("Run for each"), gbc);
		
		gbc.gridx++;
		
		add(runForEachComboBox, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		
		add(new JLabel("Input file should contain"), gbc);
		
		gbc.gridx++;
		
		add(filenameContainsTextField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		
		JPanel panel = new JPanel();
		panel.add(saveButton);
		panel.add(runButton);
		panel.add(stopButton);
		
		saveButton.addActionListener(this);
		runButton.addActionListener(this);
		stopButton.addActionListener(this);
		
		add(panel, gbc);
		
		pack();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		DatabaseModel model = TreeGenerator.generate("e:\\test", "e:\\temp", 10);
		
		new RunMacroDialog(null, model, model.getRoot().getChildren().get(4));
	}
	
	/**
	 * Action performed.
	 *
	 * @param e the e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == chooseButton) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				selectedFile = fileChooser.getSelectedFile();
				macroFilenameTextField.setText(selectedFile.getPath());
				String content = IJ.openAsString(macroFilenameTextField.getText());
				macroTextPane.setText(content);
			}
			
		}
		else if (e.getSource() == saveButton) {
			
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			if (selectedFile != null)
				fileChooser.setSelectedFile(selectedFile);
			
			if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				selectedFile = fileChooser.getSelectedFile();
				macroFilenameTextField.setText(selectedFile.getPath());
				IJ.saveString(macroTextPane.getText(), selectedFile.getPath());
			}
			
			
		}
		else if (e.getSource() == runButton) {
			isRunning = true;
			new Thread(this).start();
			runButton.setEnabled(false);
		}
		else if (e.getSource() == stopButton) {
			Macro.abort();
			isRunning = false;
			runButton.setEnabled(true);
		}
		
	}
	
	/**
	 * Run.
	 */
	@Override
	public void run() {
		
		System.out.println("run macro");
		
		String userMacro = macroTextPane.getText();
		
		NodeFilterInterface filter = new NodeFilterInterface() {
			
			@Override
			public boolean accept(Node node) {
				String level = (String)runForEachComboBox.getSelectedItem();
				return node.getType().equals(level);
			}
			
		};
		
		NodeFilterInterface fileFilter = new NodeFilterInterface() {
			
			@Override
			public boolean accept(Node node) {
				String channel = (String)channelComboBox.getSelectedItem();
				return node.getType().equals(FileNode.type) && node.getProperty("channel").equals(channel);
			}
		};
		
		for (Node descendentNode: node.getDescendents(filter)) {
			
			System.out.println("run for node " + descendentNode);
			
			if (!isRunning)
				return;
			
			String arg = descendentNode.getType();
			
			arg += "," + descendentNode.getOutputFolder();
			
			for (Node fileNode: descendentNode.getDescendents(fileFilter))
				arg += "," + fileNode.getProperty("path");
			
			Interpreter interpreter = new Interpreter();
			
			String macro = userMacro;
			
			
			try {
				
				InputStream is = getClass().getResourceAsStream("/macros/template.ijm");
				
				byte[] b = new byte[1024];
				int length;
				
				macro = "";
				
				while ((length = is.read(b)) != -1)
					macro += new String(b, 0, length);
				
				macro = macro.replaceAll("%user_macro%", userMacro);
				
				is.close();
				
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			String outputArg = interpreter.run(macro, arg);
			
			//String outputArg = IJ.runMacro(macro, arg);
			
			if (outputArg != null) {
				
				for (String path: outputArg.split(",")) {
					
					FileNode fileNode = new FileNode(descendentNode);
					fileNode.setProperty("name", path);
					fileNode.setProperty("path", path);
					
					// set channel ??
					
					model.addNode(descendentNode, fileNode);
				}
				
			}
			
		}
		
		isRunning = false;
		runButton.setEnabled(true);
	}

}
