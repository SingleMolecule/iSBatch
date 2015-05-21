package macros;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import test.TreeGenerator;
import utils.ModelUtils;
import filters.NodeFilterInterface;
import gui.CodeTextPane;
import model.DatabaseModel;
import model.FileNode;
import model.Node;

public class MacroDialog2 extends JDialog implements ActionListener, Runnable {
	private static final long serialVersionUID = 1L;
	
	private Node node;
	private NodeFilterInterface filter = new NodeFilterInterface() {
		
		@Override
		public boolean accept(Node node) {

			if (node.getType().equals(FileNode.type)) {
				
				FileNode fileNode = (FileNode)node;
				String extension = (String)extensionComboBox.getSelectedItem();
				String tag = (String)tagComboBox.getSelectedItem();
				String channel = (String)channelComboBox.getSelectedItem();
				String path = fileNode.getPath();
				String fileChannel = fileNode.getChannel();
				
				if (path == null || channel == null)
					return false;
				
				return fileChannel.equalsIgnoreCase(channel) && path.contains(tag) && path.endsWith(extension);
			}
			
			return false;
			
		}
		
	};
	
	private JComboBox<String> channelComboBox = new JComboBox<String>();
	private JComboBox<String> tagComboBox = new JComboBox<String>();
	private JTextField customTagTextField = new JTextField(10);
	private JComboBox<String> extensionComboBox = new JComboBox<String>();
	private CodeTextPane macroTextPane = new CodeTextPane();
	private JTextField macroPathTextField = new JTextField(20);
	private JButton chooseButton = new JButton("Choose");
	private JButton runButton = new JButton("Run");
	private JButton stopButton = new JButton("Stop");
	private JCheckBox addToDatabaseCheckBox = new JCheckBox("Add output to database");
	private JTextField tagTextField = new JTextField(20);
	private JCheckBox overrideTagCheckBox = new JCheckBox("Override other tags");
	
	
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
	public MacroDialog2(Node node) {
		this.node = node;
		
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
		add(macroTextPane, gbc);

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
		runButton.addActionListener(this);
		stopButton.addActionListener(this);
		
		
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {

		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 2);
		new MacroDialog2(model.getRoot());
		

	}

	@Override
	public void run() {
		
		for (Node node: node.getDescendents(filter)) {
			
			
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == customTagTextField) {
			tagComboBox.setEnabled(customTagTextField.getText().isEmpty());
		}
		else if (e.getSource() == runButton) {
			
			
			
		}
		else if (e.getSource() == stopButton) {
			
		}
		
	}

}
