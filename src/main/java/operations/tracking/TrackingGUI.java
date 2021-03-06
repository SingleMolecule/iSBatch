package operations.tracking;

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

public class TrackingGUI extends JDialog implements ActionListener {
	private String lookAhead;
	private JButton btnCancel;
	private JButton btnProcess;
	private JComboBox<String> fileTypeComboBox;
	private JComboBox<String> channelComboBox;
	private JComboBox<String> methodComboBox;
	private static final long serialVersionUID = 1L;
	private String[] channels = new String[] {

	"[Select Channel]", "All", "Acquisition", "Bright Field", "Red", "Green",
			"Blue", };

	private static final String[] methods = { "[Method]", "Average Images" };
	private String[] types = new String[] { "[File Type]", "Raw", "Flat"};
	private JTextField customSearchTxtField;
	private boolean canceled = false;
	static JFrame frame;
	private Node node;
	private String channel, method, imagePath;
	protected String maxSteps;
	public TrackingGUI(Node node) {
		setModal(true);
		setTitle("Focus Lifetimes");
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
		gridBagLayout.rowHeights = new int[] { 14, 23, 0, 23, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 1.0,
				0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
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
		
		lblLookAhead = new JLabel("Look ahead");
		GridBagConstraints gbc_lblLookAhead = new GridBagConstraints();
		gbc_lblLookAhead.gridwidth = 2;
		gbc_lblLookAhead.anchor = GridBagConstraints.EAST;
		gbc_lblLookAhead.insets = new Insets(0, 0, 5, 5);
		gbc_lblLookAhead.gridx = 1;
		gbc_lblLookAhead.gridy = 4;
		getContentPane().add(lblLookAhead, gbc_lblLookAhead);
		
		LookAheadtextField = new JTextField();
		GridBagConstraints gbc_LookAheadtextField = new GridBagConstraints();
		gbc_LookAheadtextField.insets = new Insets(0, 0, 5, 5);
		gbc_LookAheadtextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_LookAheadtextField.gridx = 3;
		gbc_LookAheadtextField.gridy = 4;
		getContentPane().add(LookAheadtextField, gbc_LookAheadtextField);
		LookAheadtextField.setColumns(10);
		LookAheadtextField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				lookAhead = LookAheadtextField.getText();
			}
		});
		
		lblSlices = new JLabel("Slices");
		GridBagConstraints gbc_lblSlices = new GridBagConstraints();
		gbc_lblSlices.insets = new Insets(0, 0, 5, 5);
		gbc_lblSlices.gridx = 4;
		gbc_lblSlices.gridy = 4;
		getContentPane().add(lblSlices, gbc_lblSlices);
		
		lblMaxStepSize = new JLabel("Max step size: ");
		GridBagConstraints gbc_lblMaxStepSize = new GridBagConstraints();
		gbc_lblMaxStepSize.anchor = GridBagConstraints.EAST;
		gbc_lblMaxStepSize.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxStepSize.gridx = 2;
		gbc_lblMaxStepSize.gridy = 5;
		getContentPane().add(lblMaxStepSize, gbc_lblMaxStepSize);
		
		maxStepTextField = new JTextField();
		GridBagConstraints gbc_maxStepTextField = new GridBagConstraints();
		gbc_maxStepTextField.insets = new Insets(0, 0, 5, 5);
		gbc_maxStepTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_maxStepTextField.gridx = 3;
		gbc_maxStepTextField.gridy = 5;
		getContentPane().add(maxStepTextField, gbc_maxStepTextField);
		maxStepTextField.setColumns(10);
		maxStepTextField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				maxSteps = maxStepTextField.getText();
			}
		});
		
		lblPixels = new JLabel("pixels");
		GridBagConstraints gbc_lblPixels = new GridBagConstraints();
		gbc_lblPixels.insets = new Insets(0, 0, 5, 5);
		gbc_lblPixels.gridx = 4;
		gbc_lblPixels.gridy = 5;
		getContentPane().add(lblPixels, gbc_lblPixels);
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.insets = new Insets(0, 0, 0, 5);
		gbc_btnProcess.gridx = 4;
		gbc_btnProcess.gridy = 6;
		getContentPane().add(btnProcess, gbc_btnProcess);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 5;
		gbc_btnCancel.gridy = 6;
		getContentPane().add(btnCancel, gbc_btnCancel);

		pack();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);

	}
	private void run() {
	}

	public static void main(String[] args) {
		frame = new JFrame();
		new TrackingGUI(null);
	}

	void error(String msg) {
		IJ.error("Batch Processor", msg);
	}
	
	public boolean isCanceled() {
		return canceled;
	}

	private String imageType = null;
	private String customSearch;
	private JLabel lblLookAhead;
	private JTextField LookAheadtextField;
	private JLabel lblSlices;
	private JLabel lblMaxStepSize;
	private JTextField maxStepTextField;
	private JLabel lblPixels;

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
			maxSteps = maxStepTextField.getText();
			lookAhead = LookAheadtextField.getText();
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

	public String getChannel() {
		return channel;
	}
	public String getMethod() {
		return method;
	}
	public String getImagePath() {
		return imagePath;
	}

	public String getImageTag() {
		return imageType;
	}

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

	public String getExtention() {
		return method;
	}

	public String getCustom() {
		return imagePath;
	}

	public String getCustomSearch() {
		return this.customSearch;
	}
	
	public int getLookAhead(){
		return Integer.parseInt(lookAhead);
	}
	
	public int getMaxStepSize(){
		return Integer.parseInt(maxSteps);
	}
}
