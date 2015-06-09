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
package operations.microbeTrackerIO;

import gui.LogPanel;
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

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JTabbedPane;

import utils.ModelUtils;

public class MicrobeTrackerIOGui extends JDialog implements ActionListener {
	private JButton btnCancel;
	private JButton btnProcess;
	private static final long serialVersionUID = 1L;
	private String[] types = new String[] { "[File Type]", "Raw", "Flat",
			"Discoidal" };
	private boolean canceled = false;
	static JFrame frame;
	private Node node;
	private String channel, method, imagePath;
	protected String matFilePath, BFFIleInputPath;
	protected String customFiter;

	/**
	 * Instantiates a new MicrobeTracker I/O Graphic interface.
	 *
	 * @param node
	 *            Entry node
	 */
	public MicrobeTrackerIOGui(Node node) {
		setModal(true);
		setTitle("MicrobeTracker I/O");
		frame = new JFrame("MicrobeTracker I/O");
		this.node = node;
		setup();
		display();
	}

	private void setup() {
	}

	private void display() {

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 107, 97, 60, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 23, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 1.0,
				Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 0.0,
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

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.gridwidth = 3;
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 1;
		getContentPane().add(tabbedPane, gbc_tabbedPane);

		createPanel = new JPanel();
		createPanel.setBorder(new TitledBorder(null, "Create image stack",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		tabbedPane.addTab("Create", null, createPanel, null);
		GridBagLayout gbl_createPanel = new GridBagLayout();
		gbl_createPanel.columnWidths = new int[] { 60, 60, 60, 60, 0 };
		gbl_createPanel.rowHeights = new int[] { 25, 0, 0 };
		gbl_createPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 0.0,
				Double.MIN_VALUE };
		gbl_createPanel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		createPanel.setLayout(gbl_createPanel);

		panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridwidth = 4;
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		createPanel.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		channelComboBox = new JComboBox<String>(
				ModelUtils.getUniqueChannels(node));
		channelComboBox.addActionListener(this);
		GridBagConstraints gbc_channelComboBox = new GridBagConstraints();
		gbc_channelComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_channelComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_channelComboBox.gridx = 0;
		gbc_channelComboBox.gridy = 0;
		panel_2.add(channelComboBox, gbc_channelComboBox);

		fileTypeComboBox = new JComboBox<String>(
				new DefaultComboBoxModel<String>(types));
		fileTypeComboBox.addActionListener(this);
		fileTypeComboBox.setEditable(true);
		GridBagConstraints gbc_fileTypeComboBox = new GridBagConstraints();
		gbc_fileTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileTypeComboBox.gridx = 1;
		gbc_fileTypeComboBox.gridy = 0;
		panel_2.add(fileTypeComboBox, gbc_fileTypeComboBox);

		label = new JLabel("CustomFilter");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(0, 0, 0, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 1;
		createPanel.add(label, gbc_label);

		customFilterTextField = new JTextField();
		customFilterTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				customFiter = customFilterTextField.getText();
			}
		});
		customFilterTextField.setColumns(10);
		GridBagConstraints gbc_customFilterTextField = new GridBagConstraints();
		gbc_customFilterTextField.insets = new Insets(0, 0, 0, 5);
		gbc_customFilterTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_customFilterTextField.gridwidth = 3;
		gbc_customFilterTextField.gridx = 1;
		gbc_customFilterTextField.gridy = 1;
		createPanel.add(customFilterTextField, gbc_customFilterTextField);

		loadPanel = new JPanel();
		loadPanel
				.setToolTipText("Warming! There is no error check! Make sure you are providing valid files!");
		loadPanel.setBorder(new TitledBorder(null, "Load .Mat File with cells",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		tabbedPane.addTab("Load", null, loadPanel, null);
		GridBagLayout gbl_loadPanel = new GridBagLayout();
		gbl_loadPanel.columnWidths = new int[] { 60, 60, 60, 60, 0 };
		gbl_loadPanel.rowHeights = new int[] { 24, 0 };
		gbl_loadPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 0.0,
				Double.MIN_VALUE };
		gbl_loadPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		loadPanel.setLayout(gbl_loadPanel);

		panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 0, 5);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridwidth = 4;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 0;
		loadPanel.add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_3.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_3.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_3.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_3.setLayout(gbl_panel_3);

		btnLoadmat = new JButton("Load .mat");
		btnLoadmat
				.setToolTipText("Warming! There is no error check! Make sure you are providing valid  .mat file!");
		btnLoadmat.addActionListener(this);
		GridBagConstraints gbc_btnLoadmat = new GridBagConstraints();
		gbc_btnLoadmat.insets = new Insets(0, 0, 5, 5);
		gbc_btnLoadmat.gridx = 0;
		gbc_btnLoadmat.gridy = 0;
		panel_3.add(btnLoadmat, gbc_btnLoadmat);

		mathPathTextField = new JTextField();
		mathPathTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				matFilePath = mathPathTextField.getText();
			}
		});
		mathPathTextField.setColumns(10);
		GridBagConstraints gbc_mathPathTextField = new GridBagConstraints();
		gbc_mathPathTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_mathPathTextField.insets = new Insets(0, 0, 5, 0);
		gbc_mathPathTextField.gridx = 1;
		gbc_mathPathTextField.gridy = 0;
		panel_3.add(mathPathTextField, gbc_mathPathTextField);

		btnBfInput = new JButton("BF Input");
		btnBfInput
				.setToolTipText("Warming! There is no error check! Make sure you are providing valid  .TIF file! The file should have a tag MTInput");
		btnBfInput.addActionListener(this);
		GridBagConstraints gbc_btnBfInput = new GridBagConstraints();
		gbc_btnBfInput.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnBfInput.insets = new Insets(0, 0, 0, 5);
		gbc_btnBfInput.gridx = 0;
		gbc_btnBfInput.gridy = 1;
		panel_3.add(btnBfInput, gbc_btnBfInput);

		BFInput = new JTextField();
		BFInput.setColumns(10);
		GridBagConstraints gbc_BFInput = new GridBagConstraints();
		gbc_BFInput.fill = GridBagConstraints.HORIZONTAL;
		gbc_BFInput.gridx = 1;
		gbc_BFInput.gridy = 1;
		panel_3.add(BFInput, gbc_BFInput);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		btnProcess = new JButton("Process");
		btnProcess.setHorizontalAlignment(SwingConstants.LEFT);
		btnProcess.addActionListener(this);
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.anchor = GridBagConstraints.EAST;
		gbc_btnProcess.insets = new Insets(0, 0, 0, 5);
		gbc_btnProcess.gridx = 1;
		gbc_btnProcess.gridy = 2;
		getContentPane().add(btnProcess, gbc_btnProcess);

		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.gridx = 2;
		gbc_btnCancel.gridy = 2;
		getContentPane().add(btnCancel, gbc_btnCancel);

		pack();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);

	}

	/**
	 * Error.
	 *
	 * @param msg
	 *            the msg
	 */
	void error(String msg) {
		LogPanel.log("MicrobeTracker IO error: " + msg);
	}

	public boolean isCanceled() {
		return canceled;
	}

	private String imageType;
	private JTabbedPane tabbedPane;
	private JPanel createPanel;
	private JPanel panel_2;
	private JComboBox<String> channelComboBox;
	private JComboBox<String> fileTypeComboBox;
	private JLabel label;
	private JTextField customFilterTextField;
	private JPanel loadPanel;
	private JPanel panel_3;
	private JButton btnLoadmat;
	private JTextField mathPathTextField;
	private JButton btnBfInput;
	private JTextField BFInput;

	/**
	 * Action performed.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel) {
			canceled = true;
			dispose();
		} else if (e.getSource() == btnProcess) {

			channel = channelComboBox.getSelectedItem().toString();
			imageType = fileTypeComboBox.getSelectedItem().toString();
			BFFIleInputPath = BFInput.getText();
			customFiter = customFilterTextField.getText();
			matFilePath = mathPathTextField.getText();
			customFiter = customFilterTextField.getText();
			dispose();
		} else if (e.getSource() == btnLoadmat) {
			matFilePath = IJ.getFilePath(".MAT file");
			mathPathTextField.setText(matFilePath);
		} else if (e.getSource() == fileTypeComboBox) {
			String foo = (String) fileTypeComboBox.getSelectedItem();
			if (foo.equalsIgnoreCase("[File Type]")) {
				this.imageType = null;
			} else {
				this.imageType = (String) fileTypeComboBox.getSelectedItem();
			}
		} else if (e.getSource() == btnBfInput) {
			BFFIleInputPath = IJ.getFilePath("MT input image");
			BFInput.setText(BFFIleInputPath);
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

	public String getCustomFilter() {
		return customFiter;
	}

	public String getMatFilePath() {
		return matFilePath;
	}

	public String getImageType() {
		return imageType;
	}

	public ArrayList<String> getImageTag() {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(imageType);
		return temp;
	}
}
