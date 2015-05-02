/*
 * 
 */
package operations.locationMaps;


import ij.IJ;

import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.GridBagConstraints;

import javax.swing.JTextField;

import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import model.Node;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

// TODO: Auto-generated Javadoc
/**
 * The Class LocationMapsGui.
 */
public class LocationMapsGui extends JDialog implements ActionListener {
	
	/** The btn cancel. */

	// butons

	private JButton btnCancel;
	
	/** The btn process. */
	private JButton btnProcess;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The channels. */
	private String[] channels = new String[] {

	"[Select Channel]", "All", "Acquisition", "Bright Field", "Red", "Green",
			"Blue", };

	/** The types. */
	private String[] types = new String[] { "[File Type]", "Raw", "Flat",
			"Discoidal" };

	/** The canceled. */
	private boolean canceled = false;

	/** The frame. */
	static JFrame frame;
	
	/** The node. */
	private Node node;
	
	/** The image path. */
	private String channel, method, imagePath;
	
	/** The mat file path. */
	protected String matFilePath;
	
	/** The custom fiter. */
	protected String customFiter;

	/*
	 * Filter variables
	 */

	/**
	 * Instantiates a new location maps gui.
	 *
	 * @param node the node
	 */
	public LocationMapsGui(Node node) {
		setModal(true);
		setTitle("Location Maps");
		frame = new JFrame("Location Maps");
		this.node = node;

		setup();
		display();

	}

	/**
	 * Setup.
	 */
	private void setup() {
	}

	/**
	 * Display.
	 */
	private void display() {

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 60, 60, 60, 60, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 60, 47, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0,
				0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 1.0, 0.0,
				Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JLabel lblBatch = new JLabel("Batch: ");
		lblBatch.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblBatch = new GridBagConstraints();
		gbc_lblBatch.anchor = GridBagConstraints.NORTH;
		gbc_lblBatch.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblBatch.insets = new Insets(0, 0, 5, 5);
		gbc_lblBatch.gridx = 0;
		gbc_lblBatch.gridy = 0;
		getContentPane().add(lblBatch, gbc_lblBatch);

		JLabel lblOperation = new JLabel(node.getType() + ": "
				+ node.toString());

		GridBagConstraints gbc_lblOperation = new GridBagConstraints();
		gbc_lblOperation.anchor = GridBagConstraints.SOUTH;
		gbc_lblOperation.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblOperation.insets = new Insets(0, 0, 5, 5);
		gbc_lblOperation.gridx = 1;
		gbc_lblOperation.gridy = 0;
		getContentPane().add(lblOperation, gbc_lblOperation);

		btnProcess = new JButton("Process");
		btnProcess.addActionListener(this);
		
		CreateInputPanel = new JPanel();
		CreateInputPanel.setBorder(new TitledBorder(null, "Create image stack", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_CreateInputPanel = new GridBagConstraints();
		gbc_CreateInputPanel.fill = GridBagConstraints.BOTH;
		gbc_CreateInputPanel.gridwidth = 4;
		gbc_CreateInputPanel.insets = new Insets(0, 0, 5, 5);
		gbc_CreateInputPanel.gridx = 0;
		gbc_CreateInputPanel.gridy = 1;
		getContentPane().add(CreateInputPanel, gbc_CreateInputPanel);
		GridBagLayout gbl_CreateInputPanel = new GridBagLayout();
		gbl_CreateInputPanel.columnWidths = new int[]{60, 60, 60, 60, 0};
		gbl_CreateInputPanel.rowHeights = new int[]{25, 0, 0};
		gbl_CreateInputPanel.columnWeights = new double[]{1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_CreateInputPanel.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		CreateInputPanel.setLayout(gbl_CreateInputPanel);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridwidth = 4;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		CreateInputPanel.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		channelComboBox = new JComboBox<String>(new DefaultComboBoxModel<String>(channels));
		GridBagConstraints gbc_channelComboBox = new GridBagConstraints();
		gbc_channelComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_channelComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_channelComboBox.gridx = 0;
		gbc_channelComboBox.gridy = 0;
		panel.add(channelComboBox, gbc_channelComboBox);
		
		fileTypeComboBox = new JComboBox<String>(new DefaultComboBoxModel<String>(types));
		GridBagConstraints gbc_fileTypeComboBox = new GridBagConstraints();
		gbc_fileTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileTypeComboBox.gridx = 1;
		gbc_fileTypeComboBox.gridy = 0;
		panel.add(fileTypeComboBox, gbc_fileTypeComboBox);
		
		lblCustomfilter = new JLabel("CustomFilter");
		GridBagConstraints gbc_lblCustomfilter = new GridBagConstraints();
		gbc_lblCustomfilter.anchor = GridBagConstraints.EAST;
		gbc_lblCustomfilter.insets = new Insets(0, 0, 0, 5);
		gbc_lblCustomfilter.gridx = 0;
		gbc_lblCustomfilter.gridy = 1;
		CreateInputPanel.add(lblCustomfilter, gbc_lblCustomfilter);
		
		customFilterTextField = new JTextField();
		customFilterTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				customFiter = customFilterTextField.getText();
			}
		});
		GridBagConstraints gbc_customFilterTextField = new GridBagConstraints();
		gbc_customFilterTextField.gridwidth = 3;
		gbc_customFilterTextField.insets = new Insets(0, 0, 0, 5);
		gbc_customFilterTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_customFilterTextField.gridx = 1;
		gbc_customFilterTextField.gridy = 1;
		CreateInputPanel.add(customFilterTextField, gbc_customFilterTextField);
		customFilterTextField.setColumns(10);
		
		ImportPanel = new JPanel();
		ImportPanel.setBorder(new TitledBorder(null, "Load .Mat File with cells", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_ImportPanel = new GridBagConstraints();
		gbc_ImportPanel.fill = GridBagConstraints.BOTH;
		gbc_ImportPanel.gridwidth = 4;
		gbc_ImportPanel.insets = new Insets(0, 0, 5, 0);
		gbc_ImportPanel.gridx = 0;
		gbc_ImportPanel.gridy = 2;
		getContentPane().add(ImportPanel, gbc_ImportPanel);
		GridBagLayout gbl_ImportPanel = new GridBagLayout();
		gbl_ImportPanel.columnWidths = new int[]{60, 60, 60, 60, 0};
		gbl_ImportPanel.rowHeights = new int[]{24, 0};
		gbl_ImportPanel.columnWeights = new double[]{1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_ImportPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		ImportPanel.setLayout(gbl_ImportPanel);
		
		panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridwidth = 4;
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		ImportPanel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		btnLoadmat = new JButton("Load .mat");
		btnLoadmat.addActionListener(this);
		GridBagConstraints gbc_btnLoadmat = new GridBagConstraints();
		gbc_btnLoadmat.insets = new Insets(0, 0, 0, 5);
		gbc_btnLoadmat.gridx = 0;
		gbc_btnLoadmat.gridy = 0;
		panel_1.add(btnLoadmat, gbc_btnLoadmat);
		
		mathPathTextField = new JTextField();
		mathPathTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				matFilePath = mathPathTextField.getText();
			}
		});
		GridBagConstraints gbc_mathPathTextField = new GridBagConstraints();
		gbc_mathPathTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_mathPathTextField.gridx = 1;
		gbc_mathPathTextField.gridy = 0;
		panel_1.add(mathPathTextField, gbc_mathPathTextField);
		mathPathTextField.setColumns(10);
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.insets = new Insets(0, 0, 0, 5);
		gbc_btnProcess.gridx = 2;
		gbc_btnProcess.gridy = 3;
		getContentPane().add(btnProcess, gbc_btnProcess);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.gridx = 3;
		gbc_btnCancel.gridy = 3;
		getContentPane().add(btnCancel, gbc_btnCancel);

		pack();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);

	}

	/**
	 * Run.
	 */
	private void run() {
		//
		// // get array of Images
		// ArrayList<Node> images = node.getDescendents(imageFileNodeFilter);

		System.out.println("Run this baby");
		System.out.println("Parameters will be: " + channel + " , " + imagePath
				+ " , " + method);
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		frame = new JFrame();

		new LocationMapsGui(null);

	}

	/**
	 * Error.
	 *
	 * @param msg the msg
	 */
	void error(String msg) {
		IJ.error("Batch Processor", msg);
	}

	/**
	 * Checks if is canceled.
	 *
	 * @return true, if is canceled
	 */
	public boolean isCanceled() {
		return canceled;
	}

	/** The image type. */
	private String imageType;
	
	/** The panel. */
	private JPanel panel;
	
	/** The panel_1. */
	private JPanel panel_1;
	
	/** The Import panel. */
	private JPanel ImportPanel;
	
	/** The Create input panel. */
	private JPanel CreateInputPanel;
	
	/** The file type combo box. */
	private JComboBox<String> fileTypeComboBox;
	
	/** The channel combo box. */
	private JComboBox<String> channelComboBox;
	
	/** The lbl customfilter. */
	private JLabel lblCustomfilter;
	
	/** The custom filter text field. */
	private JTextField customFilterTextField;
	
	/** The btn loadmat. */
	private JButton btnLoadmat;
	
	/** The math path text field. */
	private JTextField mathPathTextField;

	/**
	 * Action performed.
	 *
	 * @param e the e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel) {
			canceled = true;
			dispose();
		} else if (e.getSource() == btnProcess) {
			matFilePath = mathPathTextField.getText();
			customFiter = customFilterTextField.getText();
			run();
			dispose();
		} else if (e.getSource() == channelComboBox) {
			this.channel = channels[channelComboBox.getSelectedIndex()];
			System.out.println(channel);
//		} 
//		else if (e.getSource() == methodComboBox) {
//			this.method = (String) methodComboBox.getSelectedItem();
//			System.out.println(method);
		} else if (e.getSource() == fileTypeComboBox) {
			this.imageType = (String) fileTypeComboBox.getSelectedItem();
			System.out.println(imageType);

		}
		else if(e.getSource() == btnLoadmat){
			System.out.println("Open dialog to get file");
			matFilePath = "";
			}
		}

	
	
	
	
	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * Gets the method.
	 *
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Gets the image path.
	 *
	 * @return the image path
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * Gets the custom filter.
	 *
	 * @return the custom filter
	 */
	public String getCustomFilter() {
		return customFiter;
	}

	/**
	 * Gets the mat file path.
	 *
	 * @return the mat file path
	 */
	public String getMatFilePath() {
		return matFilePath;
	}
}
