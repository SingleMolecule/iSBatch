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

import model.DatabaseModel;
import model.Node;
import utils.ModelUtils;

public class MacroDialog4 extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private JComboBox<String> channelComboBox = new JComboBox<String>();
	private JTextField containsTextField = new JTextField("", 20);
	
	private JCheckBox replaceCheckBox = new JCheckBox("Replace original file", false);
	private JTextField filenameTextField = new JTextField(20);
	private JTextField outputFolderTextField = new JTextField(20);
	
	private JTextArea macroTextPane = new JTextArea(20, 80);
	
	private JButton chooseButton = new JButton("Choose Macro File");
	private JButton chooseOutputFolderButton = new JButton("Choose Output Folder");
	private JButton runButton = new JButton("Run");
	private JButton stopButton = new JButton("Stop");
	
	private Node node;
	private MacroRunner2 runner;
	
	public MacroDialog4(DatabaseModel model, Node node) {
		
		setTitle("Run Macro");
		macroTextPane.setFont(new Font("Monospaced", Font.PLAIN, 15));
		
		this.node = node;
		this.runner = new MacroRunner2(model);
		
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
		add(new JLabel("Filename contains (space separated list)"), gbc);
		
		gbc.gridx++;
		add(containsTextField, gbc);
		
		gbc.gridx = 1;
		gbc.gridy++;
		gbc.gridwidth = 2;
		add(replaceCheckBox, gbc);
		
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Output filename"), gbc);
		
		gbc.gridx++;
		add(filenameTextField, gbc);
		
		
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Output folder"), gbc);
		gbc.gridx++;
		add(outputFolderTextField, gbc);
		gbc.gridx++;		
		add(chooseOutputFolderButton, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 3;
		add(new JLabel("When an output folder is specified the output files will be stored in that folder."), gbc);
		gbc.gridy++;
		add(new JLabel("The output files will not be added to the database tree!"), gbc);
		gbc.gridwidth = 1;
		
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 3;
		
		add(new JScrollPane(macroTextPane), gbc);
		
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy++;
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(chooseButton);
		buttonPanel.add(runButton);
		buttonPanel.add(stopButton);
		
		add(buttonPanel, gbc);
		
		channelComboBox.addActionListener(this);
		
		// set all action listeners
		chooseButton.addActionListener(this);
		runButton.addActionListener(this);
		stopButton.addActionListener(this);
		chooseOutputFolderButton.addActionListener(this);
		
		ArrayList<Node> fileNodes = ModelUtils.getAllFileNodes(node);
		channelComboBox.setModel((ModelUtils.getUniqueChannels(fileNodes)));
		setAllFields();
		
		pack();
		setVisible(true);
	}
	
	private void setAllFields() {
		filenameTextField.setText("[" + channelComboBox.getSelectedItem() + "]" + node.getName());
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
		else if (e.getSource() == runButton) {
			runner.setNode(node);
			runner.setFilename(filenameTextField.getText());
			runner.setChannel((String)channelComboBox.getSelectedItem());
			runner.setContains(containsTextField.getText());
			runner.setMacro(macroTextPane.getText());
			runner.setOutputFolder(new File(outputFolderTextField.getText()));
			runner.start();
		}
		else if (e.getSource() == stopButton) {
			runner.stop();
		}
		else if (e.getSource() == channelComboBox) {
			setAllFields();
		}
		else if (e.getSource() == chooseOutputFolderButton) {
			
			// open file
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int option = fileChooser.showOpenDialog(null);
			
			if (option == JFileChooser.APPROVE_OPTION) {
				
				File file = fileChooser.getSelectedFile();
				outputFolderTextField.setText(file.getPath());
				
			}
			
		}
	}
	
}
