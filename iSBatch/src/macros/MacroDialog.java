package macros;

import ij.IJ;
import ij.Macro;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import test.TreeGenerator;
import filters.NodeFilterInterface;
import gui.CodeTextPane;
import gui.LogPanel;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.Root;
import model.Sample;

// TODO: Auto-generated Javadoc
/**
 * The Class MacroDialog.
 */
public class MacroDialog extends JDialog implements ActionListener, Runnable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The node types. */
	private String[] nodeTypes = new String[] {
		Root.type,
		Experiment.type,
		Sample.type,
		FieldOfView.type,
		FileNode.type,
	};
	
	/** The model. */
	private DatabaseModel model;
	
	/** The node. */
	private Node node;

	/** The node combo box. */
	private JComboBox<String> nodeComboBox = new JComboBox<String>();
	
	/** The channel combo box. */
	private JComboBox<String> channelComboBox = new JComboBox<String>();
	
	/** The type combo box. */
	private JComboBox<String> typeComboBox = new JComboBox<String>();
	
	/** The choose button. */
	private JButton chooseButton = new JButton("Choose");
	
	/** The run button. */
	private JButton runButton = new JButton("Run");
	
	/** The stop button. */
	private JButton stopButton = new JButton("Stop");
	
	/** The filename text field. */
	private JTextField filenameTextField = new JTextField(20);
	
	/** The filter text field. */
	private JTextField filterTextField = new JTextField(".*", 10);
	
	/** The text pane. */
	private CodeTextPane textPane = new CodeTextPane();
	
	/** The macro thread. */
	private Thread macroThread;
	
	/** The macro should run. */
	private boolean macroShouldRun = false;
	
	/**
	 * Instantiates a new macro dialog.
	 *
	 * @param frame the frame
	 * @param model the model
	 * @param node the node
	 */
	public MacroDialog(JFrame frame, DatabaseModel model, Node node) {
		super(frame, "Run Macro");
		
		this.model = model;
		this.node = node;
		
		nodeComboBox.addActionListener(this);
		chooseButton.addActionListener(this);
		runButton.addActionListener(this);
		stopButton.addActionListener(this);
		
		
		// fill each combo box
		for (String nodeType: nodeTypes)
			nodeComboBox.addItem(nodeType);
		
		nodeComboBox.setSelectedItem(node.getType());
		
		for (String channel: getChannels())
			channelComboBox.addItem(channel);
		
		for (String type: getTags())
			typeComboBox.addItem(type);
		
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = gbc.gridy = 0;
		add(new JLabel("Run_for_each"), gbc);
		
		gbc.gridx++;
		add(nodeComboBox, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		
		add(new JLabel("Channel"), gbc);
		
		gbc.gridx++;
		
		add(channelComboBox, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		
		add(new JLabel("Type"), gbc);
		
		gbc.gridx++;
		
		add(typeComboBox, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;

		add(new JLabel("Filter (regular expression)"), gbc);
		
		gbc.gridx++;
		
		add(filterTextField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		
		add(new JLabel("Tag"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		
		add(new JLabel("Filename"), gbc);
		
		gbc.gridx++;
		
		JPanel filePanel = new JPanel();
		filePanel.add(filenameTextField);
		filePanel.add(chooseButton);
		
		add(filePanel, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		
		add(new JLabel("code : "), gbc);
		
		gbc.gridy++;
		gbc.gridwidth = 3;
		
		add(textPane, gbc);
		
		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy++;
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(runButton);
		buttonPanel.add(stopButton);
		
		add(buttonPanel, gbc);
		
		pack();
		setVisible(true);
	}
	
	/**
	 * Gets the channels.
	 *
	 * @return the channels
	 */
	public String[] getChannels() {
		
		if (node == null)
			return new String[]{"all"};
		
		ArrayList<String> channels = new ArrayList<String>();
		channels.add("all");
				
		ArrayList<Node> fileNodes = node.getDescendents(new NodeFilterInterface() {
			
			@Override
			public boolean accept(Node node) {
				return node.getType() == FileNode.type;
			}
			
		});
		
		for (Node n: fileNodes) {
			String channel = n.getChannel();
			
			if (!channels.contains(channel))
				channels.add(n.getChannel());
		}
		
		return channels.toArray(new String[channels.size()]);
	}
	
	/**
	 * Gets the tag.
	 *
	 * @param filename the filename
	 * @return the tag
	 */
	public String getTag(String filename) {
		if (filename.matches("[^\\[]*\\[[^\\]]*\\].*"))
			return filename.substring(filename.indexOf("[") + 1, filename.indexOf("]"));
		else
			return "";
	}
	
	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	public String[] getTags() {
		
		if (node == null)
			return new String[]{"all"};
		
		ArrayList<Node> fileNodes = node.getDescendents(new NodeFilterInterface() {
			
			@Override
			public boolean accept(Node node) {
				return node.getType() == FileNode.type;
			}
			
		});
		
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("all");
		
		for (Node n: fileNodes) {
			
			String path = n.getProperty("path");
			String tag = getTag(path);
			
			if (!tag.isEmpty()) {
				if (!tags.contains(tag))
					tags.add(tag);
			}
			
		}
		
		return tags.toArray(new String[tags.size()]);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == nodeComboBox) {
			
			boolean filter = nodeComboBox.getSelectedItem() == FileNode.type;
			channelComboBox.setEnabled(filter);
			typeComboBox.setEnabled(filter);
			filterTextField.setEnabled(filter);
			
		}
		else if (e.getSource() == chooseButton) {
			
			String filename = IJ.getFilePath("Choose macro file");
			
			if (filename == null)
				return;
			
			String macro = IJ.openAsString(filename);
			filenameTextField.setText(filename);
			textPane.setText(macro);
			
		}
		else if (e.getSource() == runButton) {
			macroThread = new Thread(this);
			macroThread.start();
			macroShouldRun = true;
		}
		else if (e.getSource() == stopButton) {
			Macro.abort();
			runButton.setEnabled(true);
			macroThread = null;
			macroShouldRun = false;
			
			LogPanel.log("Macro was stopped by user");
		}
		
		
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		DatabaseModel model = TreeGenerator.generate("e:\\test", "e:\\temp", 2);
		new MacroDialog(null, model, model.getRoot());
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		runButton.setEnabled(false);
		
		final String type = (String)nodeComboBox.getSelectedItem();
		
		NodeFilterInterface nodeFilter = new NodeFilterInterface() {
			
			@Override
			public boolean accept(Node node) {
				
				if (node.getType().equalsIgnoreCase(FileNode.type)) {
					
					String filter = filterTextField.getText();
					String channel = (String)channelComboBox.getSelectedItem();
					String tag = (String)typeComboBox.getSelectedItem();
					
					
					if (!channel.equalsIgnoreCase("all")) {
						if (!node.getProperty("channel").equalsIgnoreCase(channel))
							return false;
					}
					
					if (!tag.equalsIgnoreCase("all")) {
						String path = node.getProperty("path");
						
						if (!getTag(path).equalsIgnoreCase(tag))
							return false;
					}
					
					if (!node.getProperty("path").matches(filter))
						return false;
					
				}
				
				return node.getType().equalsIgnoreCase(type);
			}
			
		};
		
		ArrayList<Node> nodes = node.getDescendents(nodeFilter);
		
		if (nodeFilter.accept(node))
			nodes.add(node);
		
		String macro = textPane.getText();
		
		for (Node n: nodes) {
			
			MacroOperation op = new MacroOperation(model);
			op.runMacro(macro, n);
			
			if (!macroShouldRun) break;
		}
		
		runButton.setEnabled(true);
		macroShouldRun = false;
	}
	
}
 