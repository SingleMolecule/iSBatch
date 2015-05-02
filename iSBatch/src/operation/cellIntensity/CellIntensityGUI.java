/*
 * 
 */
package operation.cellIntensity;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class CellIntensityGUI.
 */
public class CellIntensityGUI extends JDialog implements ActionListener {

	/** The btn cancel. */
	private JButton btnCancel;
	
	/** The btn process. */
	private JButton btnProcess;

	// ComboBox
	/** The file type combo box. */
	private JComboBox<String> fileTypeComboBox;
	
	/** The channel combo box. */
	private JComboBox<String> channelComboBox;
	
	/** The method combo box. */
	private JComboBox<String> methodComboBox;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The channels. */
	private String[] channels = new String[] {

	"[Select Channel]", "All", "Acquisition", "Bright Field", "Red", "Green",
			"Blue", };

	/** The Constant methods. */
	private static final String[] methods = { "[Method]", "Average Images" };
	
	/** The types. */
	private String[] types = new String[] { "[File Type]", "Raw", "Flat"};

	/** The custom search txt field. */
	private JTextField customSearchTxtField;

	/** The canceled. */
	private boolean canceled = false;

	/** The frame. */
	static JFrame frame;
	
	/** The node. */
	private Node node;
	
	/** The image path. */
	private String channel, method, imagePath;

	/*
	 * Filter variables
	 */

	/**
	 * Instantiates a new cell intensity gui.
	 *
	 * @param node the node
	 */
	public CellIntensityGUI(Node node) {
		setModal(true);
		setTitle("Focus Lifetimes");
		frame = new JFrame("Set Backgroung Image");
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
		gridBagLayout.columnWidths = new int[] { 0, 60, 60, 60, 60, 60, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 23, 0, 23, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 1.0,
				0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
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

		JLabel lblMethod = new JLabel("Extension: ");
		GridBagConstraints gbc_lblMethod = new GridBagConstraints();
		gbc_lblMethod.gridwidth = 2;
		gbc_lblMethod.anchor = GridBagConstraints.EAST;
		gbc_lblMethod.insets = new Insets(0, 0, 5, 5);
		gbc_lblMethod.gridx = 1;
		gbc_lblMethod.gridy = 2;
		getContentPane().add(lblMethod, gbc_lblMethod);

		methodComboBox = new JComboBox<String>();
		methodComboBox.addActionListener(this);
		methodComboBox.setEditable(true);
		methodComboBox.setModel(new DefaultComboBoxModel<String>(methods));

		GridBagConstraints gbc_methodComboBox = new GridBagConstraints();
		gbc_methodComboBox.gridwidth = 2;
		gbc_methodComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_methodComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_methodComboBox.gridx = 3;
		gbc_methodComboBox.gridy = 2;
		getContentPane().add(methodComboBox, gbc_methodComboBox);

		JLabel lblFilenameContains = new JLabel("Custom search: ");
		GridBagConstraints gbc_lblFilenameContains = new GridBagConstraints();
		gbc_lblFilenameContains.gridwidth = 2;
		gbc_lblFilenameContains.insets = new Insets(0, 0, 5, 5);
		gbc_lblFilenameContains.anchor = GridBagConstraints.EAST;
		gbc_lblFilenameContains.gridx = 1;
		gbc_lblFilenameContains.gridy = 3;
		getContentPane().add(lblFilenameContains, gbc_lblFilenameContains);

		btnProcess = new JButton("Process");
		btnProcess.addActionListener(this);

		customSearchTxtField = new JTextField();
		customSearchTxtField.addKeyListener(new KeyAdapter() {
			

			@Override
			public void keyTyped(KeyEvent e) {
				customSearch = customSearchTxtField.getText();
			}
		});


		GridBagConstraints gbc_customSearchTxtField = new GridBagConstraints();
		gbc_customSearchTxtField.gridwidth = 2;
		gbc_customSearchTxtField.insets = new Insets(0, 0, 5, 5);
		gbc_customSearchTxtField.fill = GridBagConstraints.HORIZONTAL;
		gbc_customSearchTxtField.gridx = 3;
		gbc_customSearchTxtField.gridy = 3;
		getContentPane().add(customSearchTxtField, gbc_customSearchTxtField);
		customSearchTxtField.setColumns(1);
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

	/**
	 * Run.
	 */
	private void run() {
		//
		// // get array of Images
		// ArrayList<Node> images = node.getDescendents(imageFileNodeFilter);

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		frame = new JFrame();

		new CellIntensityGUI(null);

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
	private String imageType = null;
	
	/** The custom search. */
	private String customSearch;

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
			String foo = (String) fileTypeComboBox.getSelectedItem();
			if (foo.equalsIgnoreCase("[File Type]")) {
				
				this.imageType = null;
			} else {
				this.imageType = (String) fileTypeComboBox.getSelectedItem();
			}

			foo = (String) channelComboBox.getSelectedItem();
			if (foo.equalsIgnoreCase("[Select Channel]")) {
				this.channel = "All";
			} else {
				this.channel = channels[channelComboBox.getSelectedIndex()];
			}
			
			foo = (String) methodComboBox.getSelectedItem();
			if (foo.equalsIgnoreCase("[Method]")) {
				this.method = ".tiff";
			} else {
				this.method = (String) methodComboBox.getSelectedItem();
			}
			customSearch = customSearchTxtField.getText();
			run();
			dispose();
		} else if (e.getSource() == channelComboBox) {
			this.channel = channels[channelComboBox.getSelectedIndex()];
		} else if (e.getSource() == methodComboBox) {
			this.method = (String) methodComboBox.getSelectedItem();
		} else if (e.getSource() == fileTypeComboBox) {
			this.imageType = (String) fileTypeComboBox.getSelectedItem();

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
	 * Gets the image tag.
	 *
	 * @return the image tag
	 */
	public String getImageTag() {
		return imageType;
	}

	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	public ArrayList<String> getTags() {
		ArrayList<String> container = new ArrayList<String>();
		if (imageType != null) {
			

			List<String> list = Arrays.asList(imageType.split("\\s*,\\s*"));
			for (String foo : list) {
				container.add(foo);
			}
			return container;
		}

		else
			
			return container;

	}

	/**
	 * Gets the extention.
	 *
	 * @return the extention
	 */
	public String getExtention() {
		return method;
	}

	/**
	 * Gets the custom.
	 *
	 * @return the custom
	 */
	public String getCustom() {
		return imagePath;
	}
	
	/**
	 * Gets the custom search.
	 *
	 * @return the custom search
	 */
	public String getCustomSearch() {
		return this.customSearch;
	}
	
}
