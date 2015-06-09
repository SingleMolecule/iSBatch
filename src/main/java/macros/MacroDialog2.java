package macros;

import ij.IJ;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import test.TreeGenerator;
import utils.ModelUtils;
import filters.NodeFilterInterface;
import gui.LogPanel;
import model.DatabaseModel;
import model.FileNode;
import model.Importer;
import model.Node;

public class MacroDialog2 extends JDialog implements ActionListener, Runnable {
	private static final long serialVersionUID = 1L;

	private Node node;
	private Importer importer;
	private NodeFilterInterface filter = new NodeFilterInterface() {
		
		@Override
		public boolean accept(Node node) {

			if (node.getType().equals(FileNode.type)) {
				
				FileNode fileNode = (FileNode)node;
				String extension = (String)extensionComboBox.getSelectedItem();
				String tag = (String)tagComboBox.getSelectedItem();
				String channel = (String)channelComboBox.getSelectedItem();
				String path = fileNode.getPath();
				String filename = new File(path).getName();
				String fileChannel = fileNode.getChannel();
				
				if (!customTagTextField.getText().isEmpty())
					tag = customTagTextField.getText();
				
				if (path == null || channel == null)
					return false;
				
				
				System.out.println("fileChannel : " + fileChannel + " == " + channel);
				System.out.println("filename : " + filename + " contains " + tag);
				System.out.println("filename : " + filename + " endsWith " + extension);
				System.out.println();
				
				return fileChannel.equalsIgnoreCase(channel) && filename.contains(tag) && filename.endsWith(extension);
			}
			
			return false;
			
		}
		
	};
	
	private JComboBox<String> channelComboBox = new JComboBox<String>();
	private JComboBox<String> tagComboBox = new JComboBox<String>();
	private JTextField customTagTextField = new JTextField(10);
	private JComboBox<String> extensionComboBox = new JComboBox<String>();
	private JTextArea macroTextPane = new JTextArea(20, 80);
	private JTextField macroPathTextField = new JTextField(20);
	private JButton chooseButton = new JButton("Choose");
	private JButton runButton = new JButton("Run");
	private JButton stopButton = new JButton("Stop");
	private JCheckBox addToDatabaseCheckBox = new JCheckBox("Add output to database");
	private JTextField tagTextField = new JTextField(20);
	private JCheckBox overrideTagCheckBox = new JCheckBox("Override other tags");
	private Thread thread;
	private boolean shouldRun = true;
	
	/**
	 * This is the constructor for the macro dialog. It will show the user a dialog with the following options:
	 * <ul>
	 * <li>a combo box for selecting the channel on which the macro will be run</li>
	 * <li>a combo box for selecting a file tag</li>
	 * <li>a text field for specifying a custom tag</li>
	 * <li>a text area for specifying the macro</li>
	 * <li>a text field for specifying the output tag</li>
	 * </ul>
	 * 
	 * @param node The node on which a macro will be run.
	 */
	public MacroDialog2(Node node, DatabaseModel model) {
		this.node = node;
		this.importer = new Importer(model);
		
		macroTextPane.setFont(new Font("Monospaced", Font.PLAIN, 15));
		setTitle("Run Macro");
		
		// fill all combo boxes
		ArrayList<Node> fileNodes = node.getDescendents(new NodeFilterInterface() {
			
			@Override
			public boolean accept(Node node) {
				return node.getType().equalsIgnoreCase(FileNode.type);
			}
			
		});
		
		HashSet<String> tags = new HashSet<String>();
		HashSet<String> extensions = new HashSet<String>();
		
		for (Node n: fileNodes) {
			FileNode fn = (FileNode)n;
			tags.addAll(fn.getTags());
			extensions.add(fn.getExtension());
		}
		
		channelComboBox.setModel((ModelUtils.getUniqueChannels(node)));
		
		tagComboBox.addItem("");
		for (String tag: tags)
			tagComboBox.addItem(tag);

		for (String extension: extensions)
			extensionComboBox.addItem(extension);

		
		// add all components to this dialog
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		gbc.gridx = gbc.gridy = 0;
		add(new JLabel("Channel"), gbc);
		
		gbc.gridx++;
		add(channelComboBox, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Extension"), gbc);
		
		gbc.gridx++;
		add(extensionComboBox, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Tags"), gbc);

		gbc.gridx++;
		add(tagComboBox, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Or custom tag"), gbc);
		
		gbc.gridx++;
		add(customTagTextField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Macro path"), gbc);
		
		gbc.gridx++;
		add(macroPathTextField, gbc);
		
		gbc.gridx++;
		add(chooseButton, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 4;
		gbc.ipady = 100;
		add(new JScrollPane(macroTextPane), gbc);
		gbc.ipady = 0;

		gbc.gridx = 0;
		gbc.gridy++;
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(runButton);
		buttonPanel.add(stopButton);
		
		add(buttonPanel, gbc);
		
		gbc.gridwidth = 2;		
		gbc.gridx = 0;
		gbc.gridy++;
		add(addToDatabaseCheckBox, gbc);
		
		gbc.gridwidth = 1;
		gbc.gridy++;
		add(new JLabel("Output tag"), gbc);
		
		gbc.gridx++;
		add(tagTextField, gbc);
		
		gbc.gridx++;
		add(overrideTagCheckBox, gbc);
		
		// set all action listeners
		chooseButton.addActionListener(this);
		runButton.addActionListener(this);
		stopButton.addActionListener(this);
		
		
		pack();
		setVisible(true);
	}
	
	public void runMacro(FileNode node, String macro) {
		
		System.out.println("RUN MACRO ON " + node);
		try {
			
			URL url = MacroDialog2.class.getResource("/macros/template.ijm");
			InputStream is = url.openStream();
			byte[] b = new byte[1024];
			int len = 0;
			String template = "";
			
			File file = new File(node.getProperty("path"));
			String name = file.getName();
			
			if (name.contains("."))
				name = name.substring(0, name.indexOf('.'));
			
			String tags = "";
			
			if (!overrideTagCheckBox.isSelected()) {
				if (name.contains("_"))
					tags = name.substring(name.indexOf('_'));
			}
			
			tags += "_" + tagTextField.getText();
			
			while ((len = is.read(b)) != -1)
				template += new String(b, 0, len);
			
			
			String escapedOutputPath = (node.getOutputFolder() + File.separator).replaceAll("\\\\", "\\\\\\\\");
			String escapedInputPath = node.getProperty("path").replaceAll("\\\\", "\\\\\\\\");
			
			System.out.println(escapedInputPath);
			
			template = template.replace("%output_path%", escapedOutputPath)
					.replace("%input_path%", escapedInputPath)
					.replace("%tags%", tags)
					.replace("%macro%", macro);
			
			LogPanel.log("Run macro on " + node);
			String paths = IJ.runMacro(template);
			
			if (!paths.isEmpty()) {
				
				for (String path: paths.split("\n")) {
					LogPanel.log("Adding " + path + " to " + node);
					File outputFile = new File(path);
					importer.importFile(node, outputFile, (String)channelComboBox.getSelectedItem(), outputFile.getName(), outputFile.getPath());
				}
				
			}
			
		}
		catch (IOException e) {
			LogPanel.log("Could not run macro on node " + node + " : " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {

		
		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 2);
		new MacroDialog2(model.getRoot(), model);
		
	}

	@Override
	public void run() {
		
		String macro = macroPathTextField.getText();
		
		for (Node node: this.node.getDescendents(filter)) {
			
			if (!shouldRun)
				return;
			
			runMacro((FileNode)node, macro);
		}
		
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {

		
		if (e.getSource() == chooseButton) {
			
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int option = fileChooser.showOpenDialog(null);
			
			if (option == JFileChooser.APPROVE_OPTION) {
				
				File file = fileChooser.getSelectedFile();
				String macro = IJ.openAsString(file.getPath());
				
				System.out.println(macro);
				
				macroTextPane.setText(macro);
				
			}
			
		}
		else if (e.getSource() == customTagTextField) {
			tagComboBox.setEnabled(customTagTextField.getText().isEmpty());
		}
		else if (e.getSource() == runButton) {
			shouldRun = true;
			thread = new Thread(this);
			thread.start();
		}
		else if (e.getSource() == stopButton) {
			shouldRun = false;
		}
		
	}

}
