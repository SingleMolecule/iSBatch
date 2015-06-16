package macros;

import ij.IJ;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
import model.DatabaseModel;
import model.FileNode;
import model.Node;

public class MacroDialog2 extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	private Node node;
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
	private MacroRunner runner;
	
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
		runner = new MacroRunner(model);
		
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
		runner.addActionListener(this);
		
		pack();
		setVisible(true);
	}
	
	public void runMacro(FileNode node, String macro) {
		runner.runMacro(node, macro);
		runButton.setEnabled(false);
		stopButton.setEnabled(true);
	}
	
	public static void main(String[] args) {
		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 2);
		new MacroDialog2(model.getRoot(), model);
		
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
			runner.runMacro(node, macroTextPane.getText());
		}
		else if (e.getSource() == stopButton) {
			runner.stop();
		}
		else if (e.getSource() == runner) {
			runButton.setEnabled(true);
			stopButton.setEnabled(false);
		}
		
	}

}
