package macros;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;

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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.DatabaseModel;
import model.FileNode;
import model.Importer;
import model.Node;
import test.TreeGenerator;
import utils.ModelUtils;

public class MacroDialog3 extends JDialog implements ActionListener, Runnable {
	private static final long serialVersionUID = 1L;
	
	private JComboBox<String> channelComboBox = new JComboBox<String>();
	private JTextField filterTextField = new JTextField(".*", 20);
	
	private JCheckBox replaceCheckBox = new JCheckBox("Replace original file", false);
	private JTextField filenameTextField = new JTextField(20);
	private JTextField tifOutputFilename = new JTextField(20);
	private JTextField roiOutputFilename = new JTextField(20);
	private JTextField csvOutputFilename = new JTextField(20);
	
	private JTextArea macroTextPane = new JTextArea(20, 80);
	
	private JButton chooseButton = new JButton("Choose");
	private JButton runButton = new JButton("Run");
	private JButton stopButton = new JButton("Stop");
	
	private Thread macroThread;
	private Node node;
	private Importer importer;
	
	
	private boolean shouldRun = false;
	
	public MacroDialog3(JFrame parent, DatabaseModel model, Node node) {
		super(parent);
		
		setTitle("Run Macro");
		macroTextPane.setFont(new Font("Monospaced", Font.PLAIN, 15));
		
		this.node = node;
		importer = new Importer(model);
		
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
		add(new JLabel("Filter"), gbc);
		
		gbc.gridx++;
		add(filterTextField, gbc);
		
		gbc.gridx = 1;
		gbc.gridy++;
		gbc.gridwidth = 2;
		add(replaceCheckBox, gbc);
		
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Filename"), gbc);
		
		gbc.gridx++;
		add(filenameTextField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Tif output filename"), gbc);
		
		gbc.gridx++;
		add(tifOutputFilename, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Roi output filename"), gbc);
		
		gbc.gridx++;
		add(roiOutputFilename, gbc);
		
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Csv output filename"), gbc);
		
		gbc.gridx++;
		add(csvOutputFilename, gbc);
		
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
		
		ArrayList<Node> fileNodes = ModelUtils.getAllFileNodes(node);
		channelComboBox.setModel((ModelUtils.getUniqueChannels(fileNodes)));
		setAllFields();
		
		pack();
		setVisible(true);
	}
	
	private void setAllFields() {
		
		if (filenameTextField.getText().isEmpty())
			filenameTextField.setText(node.getName());
		
		tifOutputFilename.setText("[" + channelComboBox.getSelectedItem() + "]" + filenameTextField.getText() + ".tif");
		roiOutputFilename.setText("[" + channelComboBox.getSelectedItem() + "]" + filenameTextField.getText() + ".zip");
		csvOutputFilename.setText("[" + channelComboBox.getSelectedItem() + "]" + filenameTextField.getText() + ".csv");
		
	}
	
	private ArrayList<FileNode> getFileNodes() {
		
		ArrayList<FileNode> fileNodes = new ArrayList<FileNode>();
		
		for (Node node: ModelUtils.getAllFileNodes(node)) {
			
			if (node.getChannel().equalsIgnoreCase((String)channelComboBox.getSelectedItem())
					&& node.getName().matches(filterTextField.getText())) {
				fileNodes.add((FileNode)node);
			}
			
		}
		
		return fileNodes;
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
			macroThread = new Thread(this);
			macroThread.start();
			
			shouldRun = true;
		}
		else if (e.getSource() == stopButton) {
			
			shouldRun = false;
		}
		else if (e.getSource() == channelComboBox) {
			setAllFields();
		}
		
	}
	
	@Override
	public void run() {
		
		ArrayList<FileNode> fileNodes = getFileNodes();
		
		for (FileNode fileNode: fileNodes) {
			
			if (!shouldRun)
				return;
			
			RoiManager roiManager = new RoiManager();
			roiManager.setVisible(false);
			
			String path = fileNode.getProperty("path");
			path = path.replace("\\", "\\\\"); // necessary since it will be included in the macro code
			String macro = macroTextPane.getText();
			
			macro = "open(\"" + path + "\");" + macro;
			IJ.runMacro(macro);
			
			
			if (replaceCheckBox.isSelected()) {
				IJ.save(fileNode.getProperty("path"));
			}
			else {
				
				// save generated or changed images
				for (String title : WindowManager.getImageTitles()) {
				    
					ImagePlus imp = WindowManager.getImage(title);
				    
				    if (imp.changes) {
				    	String tifFile = tifOutputFilename.getText();
				    	IJ.saveAsTiff(imp, tifFile);
				    	File file = new File(tifFile);
				    	importer.importFile(node, file, node.getChannel(), file.getName());
				    }
				    
				}
				
				// save roi's in the roimanager
				if (roiManager.getCount() >= 1) {
					String roiFile = roiOutputFilename.getText();
					roiManager.runCommand("save", roiFile);
					File file = new File(roiFile);
					importer.importFile(node, file, node.getChannel(), file.getName());
				}
				
				// save results table
				ResultsTable table = ResultsTable.getResultsTable();
				
				if (table.getCounter() > 0) {
					String csvFile = csvOutputFilename.getText();
					table.save(csvFile);
					File file = new File(csvFile);
					importer.importFile(node, file, node.getChannel(), file.getName());
				}
				
			}
			
			IJ.runMacro("close(\"*\");");
			roiManager.close();
		}
		
		
	}
	
	public static void main(String[] args) {
		
		DatabaseModel model = TreeGenerator.generate("c:/output/", "C:/temp/", 4);
		new MacroDialog3(null, model, model.getRoot().getChildren().get(2));
		
	}
	
}
