/************************************************************************
 * 				iSBatch  Copyright (C) 2015  							*
 *		Victor E. A. Caldas -  v.e.a.caldas at rug.nl					*
 *		C. Michiel Punter - c.m.punter at rug.nl						*
 *																		*
 *	This program is distributed in the hope that it will be useful,		*
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of		*
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the		*
 *	GNU General Public License for more details.						*
 *	You should have received a copy of the GNU General Public License	*
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************/
package operations.peakFitter;


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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import model.Node;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;

import operations.peakFinder.FindPeaksGui;
import utils.ModelUtils;

public class PeakFitterGui extends JDialog implements ActionListener{
	
	NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale
			.getDefault());
	
	DecimalFormat decimalFormat = (DecimalFormat) numberFormat;

	private String imageType;
	private JPanel panel;
	private JCheckBox useDiscoidalFilterChk;
	private JTextField innerRadiusTxt;
	private JTextField outerRadiusTxt;
	private JLabel lblRadius;
	private JLabel lblInner;
	private JLabel lblOuter;
	private JPanel DiscoidalFilterPanel;
	private JPanel PeakFinderParameters;
	private JLabel lblThreshold;
	private JTextField SNRTxt;
	private JLabel lblSnr;
	private JTextField IntensityTxt;
	private JLabel lblThreshold_1;
	private JLabel lblIntensity;
	private JLabel lblSelectionRadius;
	private JTextField SelectionRadiusTxt;
	private JLabel lblMinDistance;
	private JTextField minDistanceTxt;
	private JLabel lblPx;
	private JLabel lblPx_1;
	private JCheckBox chckbxInsideCells;
	private String customSearch;
	private JButton btnCancel;
	private JButton btnProcess;
	private boolean exportRaw;
	public String innerRadius= null;

	public double getInnerRadius() {
		return parseDouble(innerRadius);
	}

	private double parseDouble(String str) throws NumberFormatException {
		double toReturn = 0;
		if(!str.equalsIgnoreCase("") || !str.equals(null))
		{	
		try {
			toReturn = Double.parseDouble(str);
		} catch (NumberFormatException ex) {
			System.out.println("---- Debug --- ");
			System.err.println("Ilegal input");
			System.out.println("Illegal string: " + str+".|||");
			toReturn = 0;
		}
		}
		
		return toReturn;
	}

	public String outerRadius= null;

	public double getOuterRadius() {
		return parseDouble(outerRadius);
	}

	public String SNRThreshold = null;
	public String threshold= null;
	public String minDistance= null;
	public String selectionRadius= null;
	private JComboBox<String> fileTypeComboBox;
	private JComboBox<String> channelComboBox;
	private static final long serialVersionUID = 1L;

private String[] types = new String[] { "[File Type]", "Raw", "Flat",
			"Discoidal" };
	private boolean canceled = false;
	static JFrame frame;
	private Node node;
	private String channel, method, imagePath;
	public boolean useDiscoidal;
	public boolean useCells;
	private JPanel PeakFitterPanel;
	private JTextField ErrorBaselinetextField;
	private JTextField errorHeightextField;
	private JTextField errorXtextField;
	private JTextField errorYtextField;
	private JTextField errorSigmaXtextField;
	private JTextField errorSigmaYtextField;
	private JTextField ZscaletxtField;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JLabel lblNewLabel_4;
	private JLabel lblNewLabel_5;
	private JLabel lblNewLabel_6;
	protected String zScale;

	public double getZScale(){
		return parseDouble(zScale);
	}
	protected String errorSigmaY;

	public double getErrorSigmaY(){
		return parseDouble(errorSigmaY);
	}
	protected String errorSigmaX;
	
	public double getErrorSigmaX(){
		return parseDouble(errorSigmaX);
	}
	protected String errorY;
	
	public double getErrorY(){
		return parseDouble(errorY);
	}
	
	protected String errorX;
	
	public double getErrorX(){
		return parseDouble(errorX);
	}
	
	protected String errorHeight;
	
	public double getErrorHeight(){
		return parseDouble(errorHeight);
	}
	
	protected String errorBaseline;
	private JTextField customSearchTxtField;
	private JLabel lblSelectMethod;
	private JComboBox<String> comboBox;
	private JCheckBox chckbxExportRaw;
	
	public double getErrorBaseline(){
		return parseDouble(errorBaseline);
	}

	public PeakFitterGui(Node node) {
		setModal(true);
		setTitle("Fit Peaks");
		frame = new JFrame("Find Peaks");
		this.node = node;

		setup();
		display();

	}


	private void setup() {
	}

	private void display() {
		decimalFormat.setGroupingUsed(false);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 60, 115, 60, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 23, 0, 0, 103, 0, 203, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0,
				Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0,
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

		channelComboBox.setModel(ModelUtils.getUniqueChannels(node));
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

		JLabel lblMethod = new JLabel("Custom Search: ");
		GridBagConstraints gbc_lblMethod = new GridBagConstraints();
		gbc_lblMethod.anchor = GridBagConstraints.EAST;
		gbc_lblMethod.insets = new Insets(0, 0, 5, 5);
		gbc_lblMethod.gridx = 0;
		gbc_lblMethod.gridy = 2;
		getContentPane().add(lblMethod, gbc_lblMethod);
		
		customSearchTxtField = new JTextField();
		GridBagConstraints gbc_customSearchTxtField = new GridBagConstraints();
		gbc_customSearchTxtField.gridwidth = 2;
		gbc_customSearchTxtField.insets = new Insets(0, 0, 5, 0);
		gbc_customSearchTxtField.fill = GridBagConstraints.HORIZONTAL;
		gbc_customSearchTxtField.gridx = 1;
		gbc_customSearchTxtField.gridy = 2;
		getContentPane().add(customSearchTxtField, gbc_customSearchTxtField);
		customSearchTxtField.setColumns(10);
		customSearchTxtField.addKeyListener(new KeyAdapter() {
			

			@Override
			public void keyTyped(KeyEvent e) {
				customSearch = customSearchTxtField.getText();
			}
		});

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
				iSBatchPreferences.INNER_RADIUS = innerRadiusTxt.getText();
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

				iSBatchPreferences.OUTER_RADIUS = outerRadiusTxt.getText();
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
				iSBatchPreferences.SNR_THRESHOLD = SNRTxt.getText();
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
				iSBatchPreferences.INTENSITY_THRESHOLD = IntensityTxt.getText();
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
		SelectionRadiusTxt.setToolTipText("Must be integer");
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
				iSBatchPreferences.SELECTION_RADIUS = SelectionRadiusTxt.getText();
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
				iSBatchPreferences.DISTANCE_BETWEEN_PEAKS = minDistanceTxt.getText();
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
		
		lblSelectMethod = new JLabel("Select method: ");
		GridBagConstraints gbc_lblSelectMethod = new GridBagConstraints();
		gbc_lblSelectMethod.anchor = GridBagConstraints.EAST;
		gbc_lblSelectMethod.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectMethod.gridx = 0;
		gbc_lblSelectMethod.gridy = 5;
		getContentPane().add(lblSelectMethod, gbc_lblSelectMethod);
		
		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Gaussian fit", "----------"}));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 5;
		getContentPane().add(comboBox, gbc_comboBox);
		
		chckbxExportRaw = new JCheckBox("Export raw");
		chckbxExportRaw.addActionListener(this);
		chckbxExportRaw.setToolTipText("Save raw peak intensities in the folder \\Peaks.");
		GridBagConstraints gbc_chckbxExportRaw = new GridBagConstraints();
		gbc_chckbxExportRaw.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxExportRaw.gridx = 2;
		gbc_chckbxExportRaw.gridy = 5;
		getContentPane().add(chckbxExportRaw, gbc_chckbxExportRaw);
		
		PeakFitterPanel = new JPanel();
		PeakFitterPanel.setBorder(new TitledBorder(null, "Peak Fitter Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_PeakFitterPanel = new GridBagConstraints();
		gbc_PeakFitterPanel.fill = GridBagConstraints.BOTH;
		gbc_PeakFitterPanel.gridwidth = 3;
		gbc_PeakFitterPanel.insets = new Insets(0, 0, 5, 0);
		gbc_PeakFitterPanel.gridx = 0;
		gbc_PeakFitterPanel.gridy = 6;
		getContentPane().add(PeakFitterPanel, gbc_PeakFitterPanel);
		GridBagLayout gbl_PeakFitterPanel = new GridBagLayout();
		gbl_PeakFitterPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_PeakFitterPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_PeakFitterPanel.columnWeights = new double[]{1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_PeakFitterPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		PeakFitterPanel.setLayout(gbl_PeakFitterPanel);
		
		lblNewLabel = new JLabel("Max Error Baseline");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		PeakFitterPanel.add(lblNewLabel, gbc_lblNewLabel);
		
		ErrorBaselinetextField = new JTextField();
		ErrorBaselinetextField.setText(iSBatchPreferences.ERROR_BASELINE);
		GridBagConstraints gbc_ErrorBaselinetextField = new GridBagConstraints();
		gbc_ErrorBaselinetextField.insets = new Insets(0, 0, 5, 5);
		gbc_ErrorBaselinetextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_ErrorBaselinetextField.gridx = 1;
		gbc_ErrorBaselinetextField.gridy = 0;
		PeakFitterPanel.add(ErrorBaselinetextField, gbc_ErrorBaselinetextField);
		ErrorBaselinetextField.setColumns(10);
		ErrorBaselinetextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				iSBatchPreferences.ERROR_BASELINE = ErrorBaselinetextField.getText();
			}
		});
		
		
		lblNewLabel_6 = new JLabel("Max Error height");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 1;
		PeakFitterPanel.add(lblNewLabel_6, gbc_lblNewLabel_6);
		
		errorHeightextField = new JTextField();
		errorHeightextField.setText(iSBatchPreferences.ERROR_HEIGHT);
		GridBagConstraints gbc_errorHeightextField = new GridBagConstraints();
		gbc_errorHeightextField.insets = new Insets(0, 0, 5, 5);
		gbc_errorHeightextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_errorHeightextField.gridx = 1;
		gbc_errorHeightextField.gridy = 1;
		PeakFitterPanel.add(errorHeightextField, gbc_errorHeightextField);
		errorHeightextField.setColumns(10);
		errorHeightextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				iSBatchPreferences.ERROR_HEIGHT = errorHeightextField.getText();
			}
		});
		
		lblNewLabel_1 = new JLabel("Max Error X");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		PeakFitterPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		errorXtextField = new JTextField();
		errorXtextField.setText(iSBatchPreferences.ERROR_X);
		GridBagConstraints gbc_errorXtextField = new GridBagConstraints();
		gbc_errorXtextField.insets = new Insets(0, 0, 5, 5);
		gbc_errorXtextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_errorXtextField.gridx = 1;
		gbc_errorXtextField.gridy = 2;
		PeakFitterPanel.add(errorXtextField, gbc_errorXtextField);
		errorXtextField.setColumns(10);
		errorXtextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				iSBatchPreferences.ERROR_X = errorXtextField.getText();
			}
		});
		
		lblNewLabel_2 = new JLabel("Max Error Y");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 3;
		PeakFitterPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		errorYtextField = new JTextField();
		errorYtextField.setText(iSBatchPreferences.ERROR_X);
		errorYtextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				iSBatchPreferences.ERROR_X = errorYtextField.getText();
			}
		});
		GridBagConstraints gbc_errorYtextField = new GridBagConstraints();
		gbc_errorYtextField.insets = new Insets(0, 0, 5, 5);
		gbc_errorYtextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_errorYtextField.gridx = 1;
		gbc_errorYtextField.gridy = 3;
		PeakFitterPanel.add(errorYtextField, gbc_errorYtextField);
		errorYtextField.setColumns(10);
		errorYtextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				iSBatchPreferences.ERROR_SIGMA_Y = errorYtextField.getText();
			}
		});
		
		lblNewLabel_3 = new JLabel("Max error sigma X");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 4;
		PeakFitterPanel.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		errorSigmaXtextField = new JTextField();
		errorSigmaXtextField.setText(iSBatchPreferences.ERROR_SIGMA_X);
		errorSigmaXtextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				iSBatchPreferences.ERROR_SIGMA_X =  errorSigmaXtextField.getText();
			}
		});
		GridBagConstraints gbc_errorSigmaXtextField = new GridBagConstraints();
		gbc_errorSigmaXtextField.insets = new Insets(0, 0, 5, 5);
		gbc_errorSigmaXtextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_errorSigmaXtextField.gridx = 1;
		gbc_errorSigmaXtextField.gridy = 4;
		PeakFitterPanel.add(errorSigmaXtextField, gbc_errorSigmaXtextField);
		errorSigmaXtextField.setColumns(10);
		
		lblNewLabel_4 = new JLabel("Max Error sigma Y");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 5;
		PeakFitterPanel.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		errorSigmaYtextField = new JTextField();
		errorSigmaYtextField.setText(iSBatchPreferences.ERROR_SIGMA_Y);
		errorSigmaYtextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				iSBatchPreferences.ERROR_SIGMA_Y = errorSigmaYtextField.getText();
			}
		});
		GridBagConstraints gbc_errorSigmaYtextField = new GridBagConstraints();
		gbc_errorSigmaYtextField.insets = new Insets(0, 0, 5, 5);
		gbc_errorSigmaYtextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_errorSigmaYtextField.gridx = 1;
		gbc_errorSigmaYtextField.gridy = 5;
		PeakFitterPanel.add(errorSigmaYtextField, gbc_errorSigmaYtextField);
		errorSigmaYtextField.setColumns(10);
		
		lblNewLabel_5 = new JLabel("Z scale (nm)");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 6;
		PeakFitterPanel.add(lblNewLabel_5, gbc_lblNewLabel_5);
		
		ZscaletxtField = new JTextField();
		ZscaletxtField.setText(iSBatchPreferences.Z_SCALE);
		ZscaletxtField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				iSBatchPreferences.Z_SCALE = ZscaletxtField.getText();
			}
		});
	
		GridBagConstraints gbc_ZscaletxtField = new GridBagConstraints();
		gbc_ZscaletxtField.insets = new Insets(0, 0, 0, 5);
		gbc_ZscaletxtField.fill = GridBagConstraints.HORIZONTAL;
		gbc_ZscaletxtField.gridx = 1;
		gbc_ZscaletxtField.gridy = 6;
		PeakFitterPanel.add(ZscaletxtField, gbc_ZscaletxtField);
		ZscaletxtField.setColumns(10);
		GridBagConstraints gbc_chckbxInsideCells = new GridBagConstraints();
		gbc_chckbxInsideCells.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxInsideCells.gridx = 0;
		gbc_chckbxInsideCells.gridy = 7;
		getContentPane().add(chckbxInsideCells, gbc_chckbxInsideCells);
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.insets = new Insets(0, 0, 0, 5);
		gbc_btnProcess.gridx = 1;
		gbc_btnProcess.gridy = 7;
		getContentPane().add(btnProcess, gbc_btnProcess);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.gridx = 2;
		gbc_btnCancel.gridy = 7;
		getContentPane().add(btnCancel, gbc_btnCancel);

		pack();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);

	}

	private void run() {
	}

	public static void main(String[] args) {
		frame = new JFrame();

		new FindPeaksGui(null);

	}

	/**
	 * Error.
	 *
	 * @param msg the msg
	 */
	void error(String msg) {
		IJ.error("Batch Processor", msg);
	}

	public boolean isCanceled() {
		return canceled;
	}

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
			iSBatchPreferences.Z_SCALE = ZscaletxtField.getText();
			
			iSBatchPreferences.ERROR_SIGMA_Y = errorSigmaYtextField.getText();
			iSBatchPreferences.ERROR_SIGMA_X = errorSigmaXtextField.getText();
			iSBatchPreferences.ERROR_X = errorYtextField.getText();
			iSBatchPreferences.ERROR_Y = errorXtextField.getText();
			iSBatchPreferences.ERROR_HEIGHT = errorHeightextField.getText();
			iSBatchPreferences.ERROR_BASELINE = ErrorBaselinetextField.getText();
			channel = (String) channelComboBox.getSelectedItem();
			customSearch = customSearchTxtField.getText();
			run();
			dispose();
			
		} else if (e.getSource() == fileTypeComboBox) {
			String foo = (String) fileTypeComboBox.getSelectedItem();
			if (foo.equalsIgnoreCase("[File Type]")) {
				
				this.imageType = null;
			} else {
				this.imageType = (String) fileTypeComboBox.getSelectedItem();
			}

		} else if (e.getSource() == useDiscoidalFilterChk) {
			boolean isSelected = useDiscoidalFilterChk.isSelected();

			if (isSelected) {
				this.useDiscoidal = true;
				iSBatchPreferences.useDiscoidalFiltering = true;

			} else {
				this.useDiscoidal = false;
				iSBatchPreferences.useDiscoidalFiltering = true;
			}
		}  else if (e.getSource() == chckbxExportRaw) {
			boolean isSelected = chckbxExportRaw.isSelected();

			if (isSelected) {
				this.exportRaw = true;

			} else {
				this.exportRaw = false;
			}
		}
		else if (e.getSource() == chckbxInsideCells) {
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

	public String getChannel() {
		return channel;
	}

	public String getMethod() {
		return method;
	}

public String getImagePath() {
		return imagePath;
	}

	public double getThreshold() {
		return parseDouble(threshold);
	}

	public double getSNRThreshold() {
		return parseDouble(SNRThreshold);
	}

	public double getMinDistance() {
		return parseDouble(minDistance);
	}

	public double getSelectionRadius() {
		return parseDouble(selectionRadius);
	}

	public String getCustomSearch() {
		return this.customSearch;
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
	
	public boolean useDiscoidal(){
		return useDiscoidal;
	}
	
	public boolean useCells(){
		return useCells;
	}

	public boolean exportRaw(){
		return exportRaw;
	}


}

		
