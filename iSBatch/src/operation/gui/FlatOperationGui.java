package operation.gui;

import ij.IJ;
import ij.ImagePlus;

import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.Frame;
import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.GridBagConstraints;

import javax.swing.JTextField;

import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import model.FileNode;
import model.Node;
import model.NodeFilter;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.util.ArrayList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class FlatOperationGui extends JDialog implements ActionListener {
	/**
	 * 
	 */
	
	//butons
	
	private JButton btnCancel;
	private JButton btnProcess;
	
	
	//ComboBox
	private JComboBox<String> fileTypeComboBox;
	private JComboBox<String> channelComboBox;
	private JComboBox<String> methodComboBox;
	
	private static final long serialVersionUID = 1L;
	private String[] channels = new String[] {

	"[Select Channel]", "All", "Acquisition", "Bright Field", "Red", "Green",
			"Blue", };

	private static final String[] methods = { "[Method]", "Load Image", "Average Images" };
	private String[] types = new String[] {"[File Type]","Raw", "Flat", "Discoidal"};

	private JTextField pathToImage;
	
	private boolean canceled = false;
	private ImagePlus outputImage;

	static JFrame frame;
	private Node node;
	private String channel, method, imageType;

	/*
	 * Filter variables
	 */
	private String Selectedchannel = "All";

	public FlatOperationGui(Node node) {
		setTitle("Set Background Image");
		frame = new JFrame("Set Backgroung Image");
		this.node = node;

		setup();
		display();

	}

	private void setup() {
	}

	private void display() {

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 60, 60, 60, 60, 60, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 23, 0, 23, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 1.0,
				0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JLabel lblBatch = new JLabel("Batch: ");
		lblBatch.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblBatch = new GridBagConstraints();
		gbc_lblBatch.anchor = GridBagConstraints.NORTH;
		gbc_lblBatch.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblBatch.insets = new Insets(0, 0, 5, 5);
		gbc_lblBatch.gridx = 1;
		gbc_lblBatch.gridy = 0;
		getContentPane().add(lblBatch, gbc_lblBatch);

		JLabel lblOperation = new JLabel(node.getType() + ": "
				+ node.toString());

		GridBagConstraints gbc_lblOperation = new GridBagConstraints();
		gbc_lblOperation.anchor = GridBagConstraints.SOUTH;
		gbc_lblOperation.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblOperation.insets = new Insets(0, 0, 5, 5);
		gbc_lblOperation.gridx = 3;
		gbc_lblOperation.gridy = 0;
		getContentPane().add(lblOperation, gbc_lblOperation);
		
		channelComboBox = new JComboBox<String>();
		channelComboBox.addActionListener(this); 

				channelComboBox.setModel(new DefaultComboBoxModel<String>(channels));
				GridBagConstraints gbc_channelComboBox = new GridBagConstraints();
				gbc_channelComboBox.gridwidth = 2;
				gbc_channelComboBox.insets = new Insets(0, 0, 5, 5);
				gbc_channelComboBox.fill = GridBagConstraints.HORIZONTAL;
				gbc_channelComboBox.gridx = 1;
				gbc_channelComboBox.gridy = 1;
				getContentPane().add(channelComboBox, gbc_channelComboBox);
		
				fileTypeComboBox = new JComboBox<String>();
				fileTypeComboBox.addActionListener(this);
				fileTypeComboBox.setEditable(true);
				fileTypeComboBox.setModel(new DefaultComboBoxModel<String>(types));
				GridBagConstraints gbc_fileTypeComboBox = new GridBagConstraints();
				gbc_fileTypeComboBox.gridwidth = 2;
				gbc_fileTypeComboBox.insets = new Insets(0, 0, 5, 5);
				gbc_fileTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
				gbc_fileTypeComboBox.gridx = 3;
				gbc_fileTypeComboBox.gridy = 1;
				getContentPane().add(fileTypeComboBox, gbc_fileTypeComboBox);
		
				JLabel lblMethod = new JLabel("Select method: ");
				GridBagConstraints gbc_lblMethod = new GridBagConstraints();
				gbc_lblMethod.gridwidth = 2;
				gbc_lblMethod.anchor = GridBagConstraints.EAST;
				gbc_lblMethod.insets = new Insets(0, 0, 5, 5);
				gbc_lblMethod.gridx = 1;
				gbc_lblMethod.gridy = 2;
				getContentPane().add(lblMethod, gbc_lblMethod);
				
						methodComboBox = new JComboBox<String>();
						methodComboBox.addActionListener(this);
						
						methodComboBox.setModel(new DefaultComboBoxModel<String>(methods));
						
								GridBagConstraints gbc_methodComboBox = new GridBagConstraints();
								gbc_methodComboBox.gridwidth = 2;
								gbc_methodComboBox.insets = new Insets(0, 0, 5, 5);
								gbc_methodComboBox.fill = GridBagConstraints.HORIZONTAL;
								gbc_methodComboBox.gridx = 3;
								gbc_methodComboBox.gridy = 2;
								getContentPane().add(methodComboBox, gbc_methodComboBox);
		
				JLabel lblFilenameContains = new JLabel("File path:");
				GridBagConstraints gbc_lblFilenameContains = new GridBagConstraints();
				gbc_lblFilenameContains.gridwidth = 2;
				gbc_lblFilenameContains.insets = new Insets(0, 0, 5, 5);
				gbc_lblFilenameContains.anchor = GridBagConstraints.EAST;
				gbc_lblFilenameContains.gridx = 1;
				gbc_lblFilenameContains.gridy = 3;
				getContentPane().add(lblFilenameContains, gbc_lblFilenameContains);

		btnProcess = new JButton("Process");
		btnProcess.addActionListener(this);
		
				pathToImage = new JTextField();
				pathToImage.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
					imageType = pathToImage.getText();	
					}
				});
				
						GridBagConstraints gbc_pathToImage = new GridBagConstraints();
						gbc_pathToImage.gridwidth = 2;
						gbc_pathToImage.insets = new Insets(0, 0, 5, 5);
						gbc_pathToImage.fill = GridBagConstraints.HORIZONTAL;
						gbc_pathToImage.gridx = 3;
						gbc_pathToImage.gridy = 3;
						getContentPane().add(pathToImage, gbc_pathToImage);
						pathToImage.setColumns(1);
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.insets = new Insets(0, 0, 0, 5);
		gbc_btnProcess.gridx = 4;
		gbc_btnProcess.gridy = 4;
		getContentPane().add(btnProcess, gbc_btnProcess);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 5;
		gbc_btnCancel.gridy = 4;
		getContentPane().add(btnCancel, gbc_btnCancel);

		pack();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);

	}

	private void run() {
	
		// get array of Images
		ArrayList<Node> images = node.getDescendents(imageFileNodeFilter);

		System.out.println("Run this baby");
		System.out.println("Parameters will be: " + channel + " , " + imageType +  " , " +  method);
	}

	
	public static void main(String[] args) {
		frame = new JFrame();

		new FlatOperationGui(null);

	}




	void error(String msg) {
		IJ.error("Batch Processor", msg);
	}


	public boolean isCanceled() {
		return canceled;
	}

	
	
	private NodeFilter imageFileNodeFilter = new NodeFilter() {

		@Override
		public boolean accept(Node node) {
			// Filtering by types
			System.out.println(pathToImage.getText());

			if (!node.getType().equals(FileNode.type))
				return false;

			String ch = node.getProperty("channel");
			System.out.println(ch);
			if (!Selectedchannel.equalsIgnoreCase("All")) {

				// check the channel of this file
				if (ch == null || !ch.equals(Selectedchannel))
					return false;
			}

			String path = node.getProperty("path");

			// check if this file is an image
			if (path == null
					|| !(path.toLowerCase().endsWith(".tiff") || path
							.toLowerCase().endsWith(".tif")))
				return false;

			// Get custom string and remove spaces in the begin and end. Not in
			// the middle.

			String customString = pathToImage.getText();
			customString = customString.replaceAll("^\\s+|\\s+$", "");

			if (!customString.equalsIgnoreCase("")) {
				// Filtering by name
				String name = node.getProperty("name");

				if (!name.toLowerCase().contains(customString.toLowerCase())) {
					return false;
				}
				;

			}
			return true;
		};
	};

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnCancel){
			canceled = true;
			dispose();
		}
		else if (e.getSource()== btnProcess){
			run();
			dispose();
		}
		else if (e.getSource() == channelComboBox) {
			this.channel = channels[channelComboBox.getSelectedIndex()];
	    	System.out.println(channel);
		}
		else if (e.getSource() == methodComboBox){
			this.method = (String) methodComboBox.getSelectedItem();
			System.out.println(method);
		}
		else if(e.getSource()== fileTypeComboBox){
			this.imageType = (String) fileTypeComboBox.getSelectedItem();
			System.out.println(imageType);
			
		}
	}

	public String getChannel() {
		return channel;
	}

	public String getMethod() {
		return method;
	}}
