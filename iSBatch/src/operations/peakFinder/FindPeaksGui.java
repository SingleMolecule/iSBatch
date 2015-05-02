/*
 * 
 */
package operations.peakFinder;

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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import model.Node;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;

// TODO: Auto-generated Javadoc
/**
 * The Class FindPeaksGui.
 */
public class FindPeaksGui extends JDialog implements ActionListener {
	
	/** The number format. */
	NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale
			.getDefault());
	
	/** The decimal format. */
	DecimalFormat decimalFormat = (DecimalFormat) numberFormat;

	/** The panel. */
	private JPanel panel;
	
	/** The use discoidal filter chk. */
	private JCheckBox useDiscoidalFilterChk;
	
	/** The inner radius txt. */
	private JTextField innerRadiusTxt;
	
	/** The outer radius txt. */
	private JTextField outerRadiusTxt;
	
	/** The lbl radius. */
	private JLabel lblRadius;
	
	/** The lbl inner. */
	private JLabel lblInner;
	
	/** The lbl outer. */
	private JLabel lblOuter;
	
	/** The Discoidal filter panel. */
	private JPanel DiscoidalFilterPanel;
	
	/** The Peak finder parameters. */
	private JPanel PeakFinderParameters;
	
	/** The lbl threshold. */
	private JLabel lblThreshold;
	
	/** The SNR txt. */
	private JTextField SNRTxt;
	
	/** The lbl snr. */
	private JLabel lblSnr;
	
	/** The Intensity txt. */
	private JTextField IntensityTxt;
	
	/** The lbl threshold_1. */
	private JLabel lblThreshold_1;
	
	/** The lbl intensity. */
	private JLabel lblIntensity;
	
	/** The lbl selection radius. */
	private JLabel lblSelectionRadius;
	
	/** The Selection radius txt. */
	private JTextField SelectionRadiusTxt;
	
	/** The lbl min distance. */
	private JLabel lblMinDistance;
	
	/** The min distance txt. */
	private JTextField minDistanceTxt;
	
	/** The lbl px. */
	private JLabel lblPx;
	
	/** The lbl px_1. */
	private JLabel lblPx_1;
	
	/** The chckbx inside cells. */
	private JCheckBox chckbxInsideCells;

	// butons

	/** The btn cancel. */
	private JButton btnCancel;
	
	/** The btn process. */
	private JButton btnProcess;

	// Parameters
	/** The inner radius. */
	public String innerRadius = null;

	/**
	 * Gets the inner radius.
	 *
	 * @return the inner radius
	 */
	public double getInnerRadius() {
		return parseDouble(innerRadius);
	}

	/**
	 * Parses the double.
	 *
	 * @param str the str
	 * @return the double
	 * @throws NumberFormatException the number format exception
	 */
	private double parseDouble(String str) throws NumberFormatException {
		double toReturn = 0;
		// System.out.println(str);
		if (!str.equalsIgnoreCase("") || !str.equals(null)) {
			try {
				toReturn = Double.parseDouble(str);
				// System.out.println("Value parsed :" + toReturn);
			} catch (NumberFormatException ex) {
				System.err.println("Ilegal input");
				toReturn = 0;
				// Discard input or request new input ...
				// clean up if necessary
			}
		}

		return toReturn;
	}

	/** The outer radius. */
	public String outerRadius = null;

	/**
	 * Gets the outer radius.
	 *
	 * @return the outer radius
	 */
	public double getOuterRadius() {
		return parseDouble(outerRadius);
	}

	/** The SNR threshold. */
	public String SNRThreshold = null;
	
	/** The threshold. */
	public String threshold = null;
	
	/** The min distance. */
	public String minDistance = null;
	
	/** The selection radius. */
	public String selectionRadius = null;

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
	private static final String[] methods = { "[Method]", "Load Image",
			"Average Images" };
	
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
	
	/** The use discoidal. */
	public boolean useDiscoidal;
	
	/** The use cells. */
	public boolean useCells;
	
	/** The image type. */
	@SuppressWarnings("unused")
	private String imageType;

	/*
	 * Filter variables
	 */

	/**
	 * Instantiates a new find peaks gui.
	 *
	 * @param node the node
	 * @param preferences the preferences
	 */
	public FindPeaksGui(Node node, iSBatchPreferences preferences) {
		setModal(true);
		setTitle("Find Peaks");
		frame = new JFrame("Find Peaks");
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
		decimalFormat.setGroupingUsed(false);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 60, 115, 60, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 23, 0, 0, 103, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0,
				Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 1.0, 0.0,
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

		channelComboBox = new JComboBox<String>();
		channelComboBox.addActionListener(this);

		channelComboBox.setModel(new DefaultComboBoxModel<String>(channels));
		GridBagConstraints gbc_channelComboBox = new GridBagConstraints();
		gbc_channelComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_channelComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_channelComboBox.gridx = 0;
		gbc_channelComboBox.gridy = 1;
		getContentPane().add(channelComboBox, gbc_channelComboBox);

		fileTypeComboBox = new JComboBox<String>();
		fileTypeComboBox.addActionListener(this);
		fileTypeComboBox.setEditable(true);
		fileTypeComboBox.setModel(new DefaultComboBoxModel<String>(types));
		GridBagConstraints gbc_fileTypeComboBox = new GridBagConstraints();
		gbc_fileTypeComboBox.gridwidth = 2;
		gbc_fileTypeComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_fileTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileTypeComboBox.gridx = 1;
		gbc_fileTypeComboBox.gridy = 1;
		getContentPane().add(fileTypeComboBox, gbc_fileTypeComboBox);

		JLabel lblMethod = new JLabel("Select method: ");
		GridBagConstraints gbc_lblMethod = new GridBagConstraints();
		gbc_lblMethod.anchor = GridBagConstraints.EAST;
		gbc_lblMethod.insets = new Insets(0, 0, 5, 5);
		gbc_lblMethod.gridx = 0;
		gbc_lblMethod.gridy = 2;
		getContentPane().add(lblMethod, gbc_lblMethod);

		methodComboBox = new JComboBox<String>();
		methodComboBox.addActionListener(this);

		methodComboBox.setModel(new DefaultComboBoxModel<String>(methods));

		GridBagConstraints gbc_methodComboBox = new GridBagConstraints();
		gbc_methodComboBox.gridwidth = 2;
		gbc_methodComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_methodComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_methodComboBox.gridx = 1;
		gbc_methodComboBox.gridy = 2;
		getContentPane().add(methodComboBox, gbc_methodComboBox);

		DiscoidalFilterPanel = new JPanel();
		DiscoidalFilterPanel.setBorder(new TitledBorder(null,
				"Discoidal Filter", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		GridBagConstraints gbc_DiscoidalFilterPanel = new GridBagConstraints();
		gbc_DiscoidalFilterPanel.fill = GridBagConstraints.BOTH;
		gbc_DiscoidalFilterPanel.gridwidth = 3;
		gbc_DiscoidalFilterPanel.insets = new Insets(0, 0, 5, 0);
		gbc_DiscoidalFilterPanel.gridx = 0;
		gbc_DiscoidalFilterPanel.gridy = 3;
		getContentPane().add(DiscoidalFilterPanel, gbc_DiscoidalFilterPanel);
		GridBagLayout gbl_DiscoidalFilterPanel = new GridBagLayout();
		gbl_DiscoidalFilterPanel.columnWidths = new int[] { 60, 60, 60, 60, 0 };
		gbl_DiscoidalFilterPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_DiscoidalFilterPanel.columnWeights = new double[] { 1.0, 0.0, 1.0,
				1.0, Double.MIN_VALUE };
		gbl_DiscoidalFilterPanel.rowWeights = new double[] { 1.0, 0.0, 0.0,
				Double.MIN_VALUE };
		DiscoidalFilterPanel.setLayout(gbl_DiscoidalFilterPanel);

		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridheight = 3;
		gbc_panel.gridwidth = 4;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		DiscoidalFilterPanel.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 145, 86, 86, 0 };
		gbl_panel.rowHeights = new int[] { 23, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		useDiscoidalFilterChk = new JCheckBox("Use");
		useDiscoidalFilterChk.addActionListener(this);
		GridBagConstraints gbc_useDiscoidalFilterChk = new GridBagConstraints();
		gbc_useDiscoidalFilterChk.anchor = GridBagConstraints.NORTHWEST;
		gbc_useDiscoidalFilterChk.insets = new Insets(0, 0, 5, 5);
		gbc_useDiscoidalFilterChk.gridx = 0;
		gbc_useDiscoidalFilterChk.gridy = 0;
		panel.add(useDiscoidalFilterChk, gbc_useDiscoidalFilterChk);

		innerRadiusTxt = new JFormattedTextField(decimalFormat);
		innerRadiusTxt.setText(iSBatchPreferences.INNER_RADIUS);
		innerRadiusTxt.setColumns(15);
		innerRadiusTxt.addActionListener(this);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				innerRadius = innerRadiusTxt.getText();
				// System.out.println(innerRadius);
			}
		});

		GridBagConstraints gbc_innerRadiusTxt = new GridBagConstraints();
		gbc_innerRadiusTxt.anchor = GridBagConstraints.WEST;
		gbc_innerRadiusTxt.insets = new Insets(0, 0, 5, 5);
		gbc_innerRadiusTxt.gridx = 1;
		gbc_innerRadiusTxt.gridy = 0;
		panel.add(innerRadiusTxt, gbc_innerRadiusTxt);

		innerRadiusTxt.setColumns(10);

		outerRadiusTxt = new JTextField();
		outerRadiusTxt.setText(iSBatchPreferences.OUTER_RADIUS);
		GridBagConstraints gbc_outerRadiusTxt = new GridBagConstraints();
		gbc_outerRadiusTxt.insets = new Insets(0, 0, 5, 0);
		gbc_outerRadiusTxt.anchor = GridBagConstraints.WEST;
		gbc_outerRadiusTxt.gridx = 2;
		gbc_outerRadiusTxt.gridy = 0;
		panel.add(outerRadiusTxt, gbc_outerRadiusTxt);
		outerRadiusTxt.setColumns(10);
		outerRadiusTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {

				outerRadius = outerRadiusTxt.getText();
			}
		});

		lblRadius = new JLabel("Radius");
		GridBagConstraints gbc_lblRadius = new GridBagConstraints();
		gbc_lblRadius.insets = new Insets(0, 0, 0, 5);
		gbc_lblRadius.gridx = 0;
		gbc_lblRadius.gridy = 1;
		panel.add(lblRadius, gbc_lblRadius);

		lblInner = new JLabel("Inner");
		GridBagConstraints gbc_lblInner = new GridBagConstraints();
		gbc_lblInner.insets = new Insets(0, 0, 0, 5);
		gbc_lblInner.gridx = 1;
		gbc_lblInner.gridy = 1;
		panel.add(lblInner, gbc_lblInner);

		lblOuter = new JLabel("Outer");
		GridBagConstraints gbc_lblOuter = new GridBagConstraints();
		gbc_lblOuter.gridx = 2;
		gbc_lblOuter.gridy = 1;
		panel.add(lblOuter, gbc_lblOuter);

		btnProcess = new JButton("Process");
		btnProcess.addActionListener(this);

		PeakFinderParameters = new JPanel();
		PeakFinderParameters.setBorder(new TitledBorder(null,
				"Peak Finder Parameters", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		GridBagConstraints gbc_PeakFinderParameters = new GridBagConstraints();
		gbc_PeakFinderParameters.fill = GridBagConstraints.BOTH;
		gbc_PeakFinderParameters.gridwidth = 3;
		gbc_PeakFinderParameters.insets = new Insets(0, 0, 5, 0);
		gbc_PeakFinderParameters.gridx = 0;
		gbc_PeakFinderParameters.gridy = 4;
		getContentPane().add(PeakFinderParameters, gbc_PeakFinderParameters);
		GridBagLayout gbl_PeakFinderParameters = new GridBagLayout();
		gbl_PeakFinderParameters.columnWidths = new int[] { 60, 60, 119, 0 };
		gbl_PeakFinderParameters.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_PeakFinderParameters.columnWeights = new double[] { 1.0, 1.0, 1.0,
				Double.MIN_VALUE };
		gbl_PeakFinderParameters.rowWeights = new double[] { 0.0, 0.0, 0.0,
				0.0, Double.MIN_VALUE };
		PeakFinderParameters.setLayout(gbl_PeakFinderParameters);

		lblThreshold = new JLabel("Threshold");
		GridBagConstraints gbc_lblThreshold = new GridBagConstraints();
		gbc_lblThreshold.anchor = GridBagConstraints.EAST;
		gbc_lblThreshold.insets = new Insets(0, 0, 5, 5);
		gbc_lblThreshold.gridx = 0;
		gbc_lblThreshold.gridy = 0;
		PeakFinderParameters.add(lblThreshold, gbc_lblThreshold);

		SNRTxt = new JTextField();
		SNRTxt.setText(iSBatchPreferences.SNR_THRESHOLD);
		GridBagConstraints gbc_SNRTxt = new GridBagConstraints();
		gbc_SNRTxt.insets = new Insets(0, 0, 5, 5);
		gbc_SNRTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_SNRTxt.gridx = 1;
		gbc_SNRTxt.gridy = 0;
		PeakFinderParameters.add(SNRTxt, gbc_SNRTxt);
		SNRTxt.setColumns(10);

		SNRTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SNRThreshold = SNRTxt.getText();
			}
		});

		lblSnr = new JLabel("SNR");
		GridBagConstraints gbc_lblSnr = new GridBagConstraints();
		gbc_lblSnr.anchor = GridBagConstraints.WEST;
		gbc_lblSnr.insets = new Insets(0, 0, 5, 0);
		gbc_lblSnr.gridx = 2;
		gbc_lblSnr.gridy = 0;
		PeakFinderParameters.add(lblSnr, gbc_lblSnr);

		lblThreshold_1 = new JLabel("Threshold");
		GridBagConstraints gbc_lblThreshold_1 = new GridBagConstraints();
		gbc_lblThreshold_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblThreshold_1.anchor = GridBagConstraints.EAST;
		gbc_lblThreshold_1.gridx = 0;
		gbc_lblThreshold_1.gridy = 1;
		PeakFinderParameters.add(lblThreshold_1, gbc_lblThreshold_1);

		IntensityTxt = new JTextField();
		IntensityTxt.setText(iSBatchPreferences.INTENSITY_THRESHOLD);
		GridBagConstraints gbc_IntensityTxt = new GridBagConstraints();
		gbc_IntensityTxt.insets = new Insets(0, 0, 5, 5);
		gbc_IntensityTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_IntensityTxt.gridx = 1;
		gbc_IntensityTxt.gridy = 1;
		PeakFinderParameters.add(IntensityTxt, gbc_IntensityTxt);
		IntensityTxt.setColumns(10);
		IntensityTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				threshold = IntensityTxt.getText();
			}
		});

		lblIntensity = new JLabel("Intensity");
		GridBagConstraints gbc_lblIntensity = new GridBagConstraints();
		gbc_lblIntensity.anchor = GridBagConstraints.WEST;
		gbc_lblIntensity.insets = new Insets(0, 0, 5, 0);
		gbc_lblIntensity.gridx = 2;
		gbc_lblIntensity.gridy = 1;
		PeakFinderParameters.add(lblIntensity, gbc_lblIntensity);

		lblSelectionRadius = new JLabel("Selection Radius");
		GridBagConstraints gbc_lblSelectionRadius = new GridBagConstraints();
		gbc_lblSelectionRadius.anchor = GridBagConstraints.EAST;
		gbc_lblSelectionRadius.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectionRadius.gridx = 0;
		gbc_lblSelectionRadius.gridy = 2;
		PeakFinderParameters.add(lblSelectionRadius, gbc_lblSelectionRadius);

		SelectionRadiusTxt = new JTextField();
		SelectionRadiusTxt.setText(iSBatchPreferences.SELECTION_RADIUS);
		GridBagConstraints gbc_SelectionRadiusTxt = new GridBagConstraints();
		gbc_SelectionRadiusTxt.insets = new Insets(0, 0, 5, 5);
		gbc_SelectionRadiusTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_SelectionRadiusTxt.gridx = 1;
		gbc_SelectionRadiusTxt.gridy = 2;
		PeakFinderParameters.add(SelectionRadiusTxt, gbc_SelectionRadiusTxt);
		SelectionRadiusTxt.setColumns(10);

		SelectionRadiusTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				selectionRadius = SelectionRadiusTxt.getText();
			}
		});
		lblPx = new JLabel("px");
		GridBagConstraints gbc_lblPx = new GridBagConstraints();
		gbc_lblPx.anchor = GridBagConstraints.WEST;
		gbc_lblPx.insets = new Insets(0, 0, 5, 0);
		gbc_lblPx.gridx = 2;
		gbc_lblPx.gridy = 2;
		PeakFinderParameters.add(lblPx, gbc_lblPx);

		lblMinDistance = new JLabel("Min Distance");
		GridBagConstraints gbc_lblMinDistance = new GridBagConstraints();
		gbc_lblMinDistance.anchor = GridBagConstraints.EAST;
		gbc_lblMinDistance.insets = new Insets(0, 0, 0, 5);
		gbc_lblMinDistance.gridx = 0;
		gbc_lblMinDistance.gridy = 3;
		PeakFinderParameters.add(lblMinDistance, gbc_lblMinDistance);

		minDistanceTxt = new JTextField();
		minDistanceTxt.setText(iSBatchPreferences.DISTANCE_BETWEEN_PEAKS);
		GridBagConstraints gbc_minDistanceTxt = new GridBagConstraints();
		gbc_minDistanceTxt.insets = new Insets(0, 0, 0, 5);
		gbc_minDistanceTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_minDistanceTxt.gridx = 1;
		gbc_minDistanceTxt.gridy = 3;
		PeakFinderParameters.add(minDistanceTxt, gbc_minDistanceTxt);
		minDistanceTxt.setColumns(10);

		minDistanceTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				minDistance = minDistanceTxt.getText();
			}
		});

		lblPx_1 = new JLabel("px");
		GridBagConstraints gbc_lblPx_1 = new GridBagConstraints();
		gbc_lblPx_1.anchor = GridBagConstraints.WEST;
		gbc_lblPx_1.gridx = 2;
		gbc_lblPx_1.gridy = 3;
		PeakFinderParameters.add(lblPx_1, gbc_lblPx_1);

		chckbxInsideCells = new JCheckBox("Inside Cells");
		chckbxInsideCells.addActionListener(this);
		GridBagConstraints gbc_chckbxInsideCells = new GridBagConstraints();
		gbc_chckbxInsideCells.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxInsideCells.gridx = 0;
		gbc_chckbxInsideCells.gridy = 5;
		getContentPane().add(chckbxInsideCells, gbc_chckbxInsideCells);
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.insets = new Insets(0, 0, 0, 5);
		gbc_btnProcess.gridx = 1;
		gbc_btnProcess.gridy = 5;
		getContentPane().add(btnProcess, gbc_btnProcess);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.gridx = 2;
		gbc_btnCancel.gridy = 5;
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

		// System.out.println("Run this baby");
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

		new FindPeaksGui(null, new iSBatchPreferences());

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
			iSBatchPreferences.INNER_RADIUS = innerRadiusTxt.getText();
			iSBatchPreferences.DISTANCE_BETWEEN_PEAKS = minDistanceTxt.getText();
			iSBatchPreferences.SELECTION_RADIUS = SelectionRadiusTxt.getText();
			iSBatchPreferences.INTENSITY_THRESHOLD = IntensityTxt.getText();
			iSBatchPreferences.SNR_THRESHOLD = SNRTxt.getText();
			iSBatchPreferences.OUTER_RADIUS = outerRadiusTxt.getText();
			run();
			dispose();
		} else if (e.getSource() == channelComboBox) {
			this.channel = channels[channelComboBox.getSelectedIndex()];
			// System.out.println(channel);
		} else if (e.getSource() == methodComboBox) {
			this.method = (String) methodComboBox.getSelectedItem();
			// System.out.println(method);
		} else if (e.getSource() == fileTypeComboBox) {
			this.imageType = (String) fileTypeComboBox.getSelectedItem();
			// System.out.println(imageType);

		} else if (e.getSource() == useDiscoidalFilterChk) {
			boolean isSelected = useDiscoidalFilterChk.isSelected();

			if (isSelected) {
				this.useDiscoidal = true;

			} else {
				this.useDiscoidal = false;
			}
		} else if (e.getSource() == chckbxInsideCells) {
			boolean isSelected = chckbxInsideCells.isSelected();

			if (isSelected) {
				this.useCells = true;
				iSBatchPreferences.insideCell = true;

			} else {
				this.useCells = false;
				iSBatchPreferences.insideCell = false;
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
	 * Gets the threshold.
	 *
	 * @return the threshold
	 */
	public double getThreshold() {
		return parseDouble(iSBatchPreferences.INTENSITY_THRESHOLD);
	}

	/**
	 * Gets the SNR threshold.
	 *
	 * @return the SNR threshold
	 */
	public double getSNRThreshold() {
		return parseDouble(iSBatchPreferences.SNR_THRESHOLD);
	}

	/**
	 * Gets the min distance.
	 *
	 * @return the min distance
	 */
	public double getMinDistance() {
		return parseDouble(iSBatchPreferences.DISTANCE_BETWEEN_PEAKS);
	}

	/**
	 * Gets the selection radius.
	 *
	 * @return the selection radius
	 */
	public double getSelectionRadius() {
		return parseDouble(iSBatchPreferences.SELECTION_RADIUS);
	}
}
