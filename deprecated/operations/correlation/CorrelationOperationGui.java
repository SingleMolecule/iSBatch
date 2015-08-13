/*
 * 
 */
package operations.correlation;

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

import model.Node;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;


import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;

// TODO: Auto-generated Javadoc
/**
 * The Class CorrelationOperationGui.
 */
public class CorrelationOperationGui extends JDialog implements ActionListener {

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
	
	/** The filter1 text. */
	protected String filter1Text;
	
	/** The filter2 text. */
	protected String filter2Text;

	/*
	 * Filter variables
	 */

	/**
	 * Instantiates a new correlation operation gui.
	 *
	 * @param node the node
	 */
	public CorrelationOperationGui(Node node) {
		setModal(true);
		setTitle("Set Background Image");
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
		gridBagLayout.columnWidths = new int[] { 60, 60, 60, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 202, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0,
				1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0,
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
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 3;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Channel 1", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.insets = new Insets(0, 0, 5, 0);
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 0;
		panel.add(panel_4, gbc_panel_4);
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[]{0, 0};
		gbl_panel_4.rowHeights = new int[]{0, 0};
		gbl_panel_4.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_4.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel_4.setLayout(gbl_panel_4);
		
		panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		panel_4.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{120, 120, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		channel1ComboBox = new JComboBox<String>(new DefaultComboBoxModel<String>(channels));
		channel1ComboBox.addActionListener(this);
		GridBagConstraints gbc_channel1ComboBox = new GridBagConstraints();
		gbc_channel1ComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_channel1ComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_channel1ComboBox.gridx = 0;
		gbc_channel1ComboBox.gridy = 0;
		panel_1.add(channel1ComboBox, gbc_channel1ComboBox);
		
		channel1_TypComboBox = new JComboBox<String>(new DefaultComboBoxModel<String>(types));
		channel1_TypComboBox.addActionListener(this);
		GridBagConstraints gbc_channel1_TypComboBox = new GridBagConstraints();
		gbc_channel1_TypComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_channel1_TypComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_channel1_TypComboBox.gridx = 1;
		gbc_channel1_TypComboBox.gridy = 0;
		panel_1.add(channel1_TypComboBox, gbc_channel1_TypComboBox);
		
		lblCustom = new JLabel("Custom Filter");
		GridBagConstraints gbc_lblCustom = new GridBagConstraints();
		gbc_lblCustom.insets = new Insets(0, 0, 0, 5);
		gbc_lblCustom.anchor = GridBagConstraints.EAST;
		gbc_lblCustom.gridx = 0;
		gbc_lblCustom.gridy = 1;
		panel_1.add(lblCustom, gbc_lblCustom);
		
		filterCh1TextField = new JTextField();
		filterCh1TextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				filter1Text = filterCh1TextField.getText();
			}
		});
		GridBagConstraints gbc_filterCh1TextField = new GridBagConstraints();
		gbc_filterCh1TextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_filterCh1TextField.gridx = 1;
		gbc_filterCh1TextField.gridy = 1;
		panel_1.add(filterCh1TextField, gbc_filterCh1TextField);
		filterCh1TextField.setColumns(10);
		
		panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "Channel 2", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_5 = new GridBagConstraints();
		gbc_panel_5.fill = GridBagConstraints.BOTH;
		gbc_panel_5.insets = new Insets(0, 0, 5, 0);
		gbc_panel_5.gridx = 0;
		gbc_panel_5.gridy = 1;
		panel.add(panel_5, gbc_panel_5);
		GridBagLayout gbl_panel_5 = new GridBagLayout();
		gbl_panel_5.columnWidths = new int[]{0, 0};
		gbl_panel_5.rowHeights = new int[]{0, 0};
		gbl_panel_5.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_5.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel_5.setLayout(gbl_panel_5);
		
		panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		panel_5.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{120, 120, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		channel2ComboBox = new JComboBox<String>(new DefaultComboBoxModel<String>(channels));
		channel2ComboBox.addActionListener(this);
		GridBagConstraints gbc_channel2ComboBox = new GridBagConstraints();
		gbc_channel2ComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_channel2ComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_channel2ComboBox.gridx = 0;
		gbc_channel2ComboBox.gridy = 0;
		panel_2.add(channel2ComboBox, gbc_channel2ComboBox);
		
		ch2TypeComboBox = new JComboBox<String>(new DefaultComboBoxModel<String>(types));
		ch2TypeComboBox.addActionListener(this);
		GridBagConstraints gbc_ch2TypeComboBox = new GridBagConstraints();
		gbc_ch2TypeComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_ch2TypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_ch2TypeComboBox.gridx = 1;
		gbc_ch2TypeComboBox.gridy = 0;
		panel_2.add(ch2TypeComboBox, gbc_ch2TypeComboBox);
		
		lblCustom_1 = new JLabel("Custom Filter");
		GridBagConstraints gbc_lblCustom_1 = new GridBagConstraints();
		gbc_lblCustom_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblCustom_1.anchor = GridBagConstraints.EAST;
		gbc_lblCustom_1.gridx = 0;
		gbc_lblCustom_1.gridy = 1;
		panel_2.add(lblCustom_1, gbc_lblCustom_1);
		
		filterCh2TextField = new JTextField();
		filterCh2TextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				filter2Text = filterCh2TextField.getText();
			}
		});
		GridBagConstraints gbc_filterCh2TextField = new GridBagConstraints();
		gbc_filterCh2TextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_filterCh2TextField.gridx = 1;
		gbc_filterCh2TextField.gridy = 1;
		panel_2.add(filterCh2TextField, gbc_filterCh2TextField);
		filterCh2TextField.setColumns(10);
		
		panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 2;
		panel.add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_3.rowHeights = new int[]{0, 0};
		gbl_panel_3.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		xProjection = new JCheckBox("X Projection");
		xProjection.addActionListener(this);
		GridBagConstraints gbc_xProjection = new GridBagConstraints();
		gbc_xProjection.insets = new Insets(0, 0, 0, 5);
		gbc_xProjection.gridx = 0;
		gbc_xProjection.gridy = 0;
		panel_3.add(xProjection, gbc_xProjection);
		
		yProjection = new JCheckBox("Y Projection");
		yProjection.addActionListener(this);
		GridBagConstraints gbc_yProjection = new GridBagConstraints();
		gbc_yProjection.anchor = GridBagConstraints.EAST;
		gbc_yProjection.insets = new Insets(0, 0, 0, 5);
		gbc_yProjection.gridx = 2;
		gbc_yProjection.gridy = 0;
		panel_3.add(yProjection, gbc_yProjection);
		
		btnProcess = new JButton("Process");
		GridBagConstraints gbc_btnProccess = new GridBagConstraints();
		gbc_btnProccess.insets = new Insets(0, 0, 0, 5);
		gbc_btnProccess.gridx = 5;
		gbc_btnProccess.gridy = 0;
		panel_3.add(btnProcess, gbc_btnProccess);
		
		btnCancel = new JButton("Cancel");
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.gridx = 6;
		gbc_btnCancel.gridy = 0;
		panel_3.add(btnCancel, gbc_btnCancel);

		pack();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		frame = new JFrame();

		new CorrelationOperationGui(null);

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

	/** The panel. */
	private JPanel panel;
	
	/** The panel_1. */
	private JPanel panel_1;
	
	/** The panel_2. */
	private JPanel panel_2;
	
	/** The panel_3. */
	private JPanel panel_3;
	
	/** The panel_4. */
	private JPanel panel_4;
	
	/** The panel_5. */
	private JPanel panel_5;
	
	/** The channel1_ typ combo box. */
	private JComboBox<String> channel1_TypComboBox;
	
	/** The ch2 type combo box. */
	private JComboBox<String> ch2TypeComboBox;
	
	/** The channel1 combo box. */
	private JComboBox<String> channel1ComboBox;
	
	/** The channel2 combo box. */
	private JComboBox<String> channel2ComboBox;
	
	/** The filter ch1 text field. */
	private JTextField filterCh1TextField;
	
	/** The filter ch2 text field. */
	private JTextField filterCh2TextField;
	
	/** The lbl custom. */
	private JLabel lblCustom;
	
	/** The lbl custom_1. */
	private JLabel lblCustom_1;
	
	/** The btn process. */
	private JButton btnProcess;
	
	/** The btn cancel. */
	private JButton btnCancel;
	
	/** The type2. */
	private String type2;
	
	/** The type1. */
	private String type1;
	
	/** The channel1. */
	private String channel1;
	
	/** The channel2. */
	private String channel2;
	
	/** The filter1. */
	private String filter1;
	
	/** The filter2. */
	private String filter2;
	
	/** The x projection. */
	private JCheckBox xProjection;
	
	/** The y projection. */
	private JCheckBox yProjection;
	
	/** The project_on_ x. */
	private boolean project_on_X;
	
	/** The project_on_ y. */
	private boolean project_on_Y;

	/**
	 * Gets the channel1.
	 *
	 * @return the channel1
	 */
	public String getChannel1() {
		return channel1;
	}
	
	/**
	 * Gets the channel2.
	 *
	 * @return the channel2
	 */
	public String getChannel2() {
		return channel2;
	}
	
	/**
	 * Gets the type1.
	 *
	 * @return the type1
	 */
	public String gettype1() {
		return type1;
	}
	
	/**
	 * Gets the type2.
	 *
	 * @return the type2
	 */
	public String gettype2() {
		return type2;
	}
	
	/**
	 * Action performed.
	 *
	 * @param e the e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == channel1ComboBox){
			this.channel1 = (String) channel1ComboBox.getSelectedItem();
		}
		else if(e.getSource() == channel2ComboBox){
			this.channel2 = (String) channel2ComboBox.getSelectedItem();
		}
		else if(e.getSource() == channel1_TypComboBox){
			this.type1 = (String) channel1_TypComboBox.getSelectedItem();
		}
		else if(e.getSource()== ch2TypeComboBox){
			this.type2 = (String) ch2TypeComboBox.getSelectedItem();
		}
		else if (e.getSource() == btnProcess) {
			filter1 = filterCh1TextField.getText();
			filter2 = filterCh2TextField.getText();
			dispose();
		}
		else if(e.getSource() == btnCancel) {
			canceled = true;
			dispose();
		}
		else if (e.getSource() == xProjection) {
			boolean isSelected = xProjection.isSelected();

			if (isSelected) {
				this.project_on_X = true;

			} else {
				this.project_on_X = false;
			}
		}
		else if (e.getSource() == yProjection) {
			boolean isSelected = yProjection.isSelected();

			if (isSelected) {
				this.project_on_Y = true;

			} else {
				this.project_on_Y = false;
			}
		}
		
		
		
		
	}
	
	/**
	 * Require x projection.
	 *
	 * @return true, if successful
	 */
	public boolean requireXProjection(){
		return project_on_X;
	};
	
	/**
	 * Require y projection.
	 *
	 * @return true, if successful
	 */
	public boolean requireYProjection(){
		return project_on_Y;
	};
	
	
	/**
	 * Gets the filter1.
	 *
	 * @return the filter1
	 */
	public String getFilter1(){
		return filter1;
		
	}
	
	/**
	 * Gets the filter2.
	 *
	 * @return the filter2
	 */
	public String getFilter2(){
		return filter2;
		
	}

}
