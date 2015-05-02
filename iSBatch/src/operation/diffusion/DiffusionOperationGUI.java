/*
 * 
 */
package operation.diffusion;

import iSBatch.iSBatchPreferences;
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

import javax.swing.JCheckBox;

// TODO: Auto-generated Javadoc
/**
 * The Class DiffusionOperationGUI.
 */
public class DiffusionOperationGUI extends JDialog implements ActionListener {
	
	/** The look ahead. */
	private String lookAhead;
	
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
	
	/** The Constant dimensions. */
	private static final String[] dimensions = { "1D", "2D" };
	
	/** The types. */
	private String[] types = new String[] { "[File Type]", "Raw", "Flat" };

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
	
	/** The max steps. */
	private String maxSteps;
	
	/** The time interval. */
	private String timeInterval;
	
	/** The pixel size. */
	private String pixelSize;
	
	/** The number of points. */
	private String numberOfPoints;
	
	/** The fit until. */
	private String fitUntil;

	/*
	 * Filter variables
	 */

	/**
	 * Instantiates a new diffusion operation gui.
	 *
	 * @param node the node
	 */
	public DiffusionOperationGUI(Node node) {
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
		gridBagLayout.rowHeights = new int[] { 14, 23, 0, 23, 0, 0, 0, 0, 0, 0,
				0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 1.0,
				0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
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

		lblLookAhead = new JLabel("Time interval:");
		GridBagConstraints gbc_lblLookAhead = new GridBagConstraints();
		gbc_lblLookAhead.gridwidth = 2;
		gbc_lblLookAhead.anchor = GridBagConstraints.EAST;
		gbc_lblLookAhead.insets = new Insets(0, 0, 5, 5);
		gbc_lblLookAhead.gridx = 1;
		gbc_lblLookAhead.gridy = 4;
		getContentPane().add(lblLookAhead, gbc_lblLookAhead);

		timeIntervaltextField = new JTextField();
		GridBagConstraints gbc_timeIntervaltextField = new GridBagConstraints();
		gbc_timeIntervaltextField.insets = new Insets(0, 0, 5, 5);
		gbc_timeIntervaltextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeIntervaltextField.gridx = 3;
		gbc_timeIntervaltextField.gridy = 4;
		getContentPane().add(timeIntervaltextField, gbc_timeIntervaltextField);
		timeIntervaltextField.setColumns(10);
		timeIntervaltextField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				timeInterval = timeIntervaltextField.getText();
			}
		});

		lblSlices = new JLabel("s");
		GridBagConstraints gbc_lblSlices = new GridBagConstraints();
		gbc_lblSlices.insets = new Insets(0, 0, 5, 5);
		gbc_lblSlices.gridx = 4;
		gbc_lblSlices.gridy = 4;
		getContentPane().add(lblSlices, gbc_lblSlices);

		lblMaxStepSize = new JLabel("Pixel size:");
		GridBagConstraints gbc_lblMaxStepSize = new GridBagConstraints();
		gbc_lblMaxStepSize.anchor = GridBagConstraints.EAST;
		gbc_lblMaxStepSize.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxStepSize.gridx = 2;
		gbc_lblMaxStepSize.gridy = 5;
		getContentPane().add(lblMaxStepSize, gbc_lblMaxStepSize);

		PixelSizeTextField = new JTextField();
		GridBagConstraints gbc_PixelSizeTextField = new GridBagConstraints();
		gbc_PixelSizeTextField.insets = new Insets(0, 0, 5, 5);
		gbc_PixelSizeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_PixelSizeTextField.gridx = 3;
		gbc_PixelSizeTextField.gridy = 5;
		getContentPane().add(PixelSizeTextField, gbc_PixelSizeTextField);
		PixelSizeTextField.setColumns(10);
		PixelSizeTextField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				pixelSize = PixelSizeTextField.getText();
			}
		});

		lblPixels = new JLabel("um");
		GridBagConstraints gbc_lblPixels = new GridBagConstraints();
		gbc_lblPixels.insets = new Insets(0, 0, 5, 5);
		gbc_lblPixels.gridx = 4;
		gbc_lblPixels.gridy = 5;
		getContentPane().add(lblPixels, gbc_lblPixels);

		lblMinimumNumberOf = new JLabel("Minimum number of points:");
		GridBagConstraints gbc_lblMinimumNumberOf = new GridBagConstraints();
		gbc_lblMinimumNumberOf.gridwidth = 2;
		gbc_lblMinimumNumberOf.insets = new Insets(0, 0, 5, 5);
		gbc_lblMinimumNumberOf.gridx = 1;
		gbc_lblMinimumNumberOf.gridy = 6;
		getContentPane().add(lblMinimumNumberOf, gbc_lblMinimumNumberOf);

		NumberOfPointstextField = new JTextField();
		NumberOfPointstextField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				numberOfPoints = NumberOfPointstextField.getText();
			}
		});
		
		GridBagConstraints gbc_NumberOfPointstextField = new GridBagConstraints();
		gbc_NumberOfPointstextField.insets = new Insets(0, 0, 5, 5);
		gbc_NumberOfPointstextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_NumberOfPointstextField.gridx = 3;
		gbc_NumberOfPointstextField.gridy = 6;
		getContentPane().add(NumberOfPointstextField,
				gbc_NumberOfPointstextField);
		NumberOfPointstextField.setColumns(10);

		lblFitUntilall = new JLabel("Fit until (0=all)");
		GridBagConstraints gbc_lblFitUntilall = new GridBagConstraints();
		gbc_lblFitUntilall.insets = new Insets(0, 0, 5, 5);
		gbc_lblFitUntilall.anchor = GridBagConstraints.EAST;
		gbc_lblFitUntilall.gridx = 2;
		gbc_lblFitUntilall.gridy = 7;
		getContentPane().add(lblFitUntilall, gbc_lblFitUntilall);

		FItLimittextField_1 = new JTextField();
		
		FItLimittextField_1.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				fitUntil = FItLimittextField_1.getText();
			}
		});
		
		
		GridBagConstraints gbc_FItLimittextField_1 = new GridBagConstraints();
		gbc_FItLimittextField_1.insets = new Insets(0, 0, 5, 5);
		gbc_FItLimittextField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_FItLimittextField_1.gridx = 3;
		gbc_FItLimittextField_1.gridy = 7;
		getContentPane().add(FItLimittextField_1, gbc_FItLimittextField_1);
		FItLimittextField_1.setColumns(10);

		lblS = new JLabel("s");
		GridBagConstraints gbc_lblS = new GridBagConstraints();
		gbc_lblS.insets = new Insets(0, 0, 5, 5);
		gbc_lblS.gridx = 4;
		gbc_lblS.gridy = 7;
		getContentPane().add(lblS, gbc_lblS);

		lblDiffusionDimensionality = new JLabel("Diffusion dimensionality");
		GridBagConstraints gbc_lblDiffusionDimensionality = new GridBagConstraints();
		gbc_lblDiffusionDimensionality.insets = new Insets(0, 0, 5, 5);
		gbc_lblDiffusionDimensionality.anchor = GridBagConstraints.EAST;
		gbc_lblDiffusionDimensionality.gridx = 2;
		gbc_lblDiffusionDimensionality.gridy = 8;
		getContentPane().add(lblDiffusionDimensionality,
				gbc_lblDiffusionDimensionality);

		dimensionComboBox = new JComboBox<String>();
		dimensionComboBox.addActionListener(this);
		dimensionComboBox
				.setModel(new DefaultComboBoxModel<String>(dimensions));
		GridBagConstraints gbc_dimensionComboBox = new GridBagConstraints();
		gbc_dimensionComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_dimensionComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_dimensionComboBox.gridx = 3;
		gbc_dimensionComboBox.gridy = 8;
		getContentPane().add(dimensionComboBox, gbc_dimensionComboBox);

		chckbxAverageAllTrajectories = new JCheckBox("Average all trajectories");
		chckbxAverageAllTrajectories.addActionListener(this);
		GridBagConstraints gbc_chckbxAverageAllTrajectories = new GridBagConstraints();
		gbc_chckbxAverageAllTrajectories.gridwidth = 2;
		gbc_chckbxAverageAllTrajectories.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxAverageAllTrajectories.gridx = 2;
		gbc_chckbxAverageAllTrajectories.gridy = 9;
		getContentPane().add(chckbxAverageAllTrajectories,
				gbc_chckbxAverageAllTrajectories);
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.insets = new Insets(0, 0, 0, 5);
		gbc_btnProcess.gridx = 4;
		gbc_btnProcess.gridy = 9;
		getContentPane().add(btnProcess, gbc_btnProcess);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 5;
		gbc_btnCancel.gridy = 9;
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

		new DiffusionOperationGUI(null);

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
	
	/** The lbl look ahead. */
	private JLabel lblLookAhead;
	
	/** The time intervaltext field. */
	private JTextField timeIntervaltextField;
	
	/** The lbl slices. */
	private JLabel lblSlices;
	
	/** The lbl max step size. */
	private JLabel lblMaxStepSize;
	
	/** The Pixel size text field. */
	private JTextField PixelSizeTextField;
	
	/** The lbl pixels. */
	private JLabel lblPixels;
	
	/** The lbl minimum number of. */
	private JLabel lblMinimumNumberOf;
	
	/** The Number of pointstext field. */
	private JTextField NumberOfPointstextField;
	
	/** The F it limittext field_1. */
	private JTextField FItLimittextField_1;
	
	/** The lbl fit untilall. */
	private JLabel lblFitUntilall;
	
	/** The lbl s. */
	private JLabel lblS;
	
	/** The lbl diffusion dimensionality. */
	private JLabel lblDiffusionDimensionality;
	
	/** The dimension combo box. */
	private JComboBox dimensionComboBox;
	
	/** The chckbx average all trajectories. */
	private JCheckBox chckbxAverageAllTrajectories;
	
	/** The do average. */
	private boolean doAverage;

	
	

	
	
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
			pixelSize = PixelSizeTextField.getText();
			timeInterval = timeIntervaltextField.getText();
			numberOfPoints = NumberOfPointstextField.getText();
			fitUntil = FItLimittextField_1.getText();
			
			
		
			
			
			run();
			dispose();
		} else if (e.getSource() == channelComboBox) {
			this.channel = channels[channelComboBox.getSelectedIndex()];
		} else if (e.getSource() == methodComboBox) {
			this.method = (String) methodComboBox.getSelectedItem();
		} else if (e.getSource() == fileTypeComboBox) {
			this.imageType = (String) fileTypeComboBox.getSelectedItem();

		}
		
		else if(e.getSource() == chckbxAverageAllTrajectories){
			boolean isSelected = chckbxAverageAllTrajectories.isSelected();		
			
			
			
				if (isSelected) {
					this.doAverage = true;

				} else {
					this.doAverage = false;
				}
			
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


	/**
	 * Gets the max step size.
	 *
	 * @return the max step size
	 */
	public int getMaxStepSize() {
		return Integer.parseInt(maxSteps);
	}
	
	/**
	 * Gets the pixel size.
	 *
	 * @return the pixel size
	 */
	public double getPixelSize(){
		return Double.parseDouble(pixelSize);
	}
	
	/**
	 * Gets the time interval.
	 *
	 * @return the time interval
	 */
	public double gettimeInterval(){
		return Double.parseDouble(timeInterval);
	}
	
	/**
	 * Gets the time interval.
	 *
	 * @return the time interval
	 */
	public double getTimeInterval(){
		return Double.parseDouble(timeInterval);
	}
	
	/**
	 * Gets the number of points.
	 *
	 * @return the number of points
	 */
	public int getNumberOfPoints(){
		return Integer.parseInt(numberOfPoints);
	}
	
	/**
	 * Gets the fit until.
	 *
	 * @return the fit until
	 */
	public double getFitUntil(){
		return Double.parseDouble(fitUntil);
	}

	/**
	 * Gets the do average.
	 *
	 * @return the do average
	 */
	public boolean getDoAverage() {
	
		return doAverage;
	}
	
	
	
	
	
	
	
	
	
	

}
