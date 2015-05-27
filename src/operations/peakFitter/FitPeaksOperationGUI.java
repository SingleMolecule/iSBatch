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
import java.util.Locale;

import model.Node;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;

import operations.peakFinder.FindPeaksGui;
import utils.FileNames;
import utils.ModelUtils;

public class FitPeaksOperationGUI extends JDialog implements ActionListener {

	NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale
			.getDefault());

	DecimalFormat decimalFormat = (DecimalFormat) numberFormat;

	private String imageType;
	private JCheckBox chckbxInsideCells;
	private String customSearch;
	private JButton btnCancel;
	private JButton btnProcess;
	private boolean exportRaw;
	public String innerRadius = null;

	public double getInnerRadius() {
		return parseDouble(innerRadius);
	}

	private double parseDouble(String str) throws NumberFormatException {
		double toReturn = 0;
		if (!str.equalsIgnoreCase("") || !str.equals(null)) {
			try {
				toReturn = Double.parseDouble(str);
			} catch (NumberFormatException ex) {
				System.out.println("---- Debug --- ");
				System.err.println("Ilegal input");
				System.out.println("Illegal string: " + str + ".|||");
				toReturn = 0;
			}
		}

		return toReturn;
	}

	public String outerRadius = null;

	public double getOuterRadius() {
		return parseDouble(outerRadius);
	}

	public String SNRThreshold = null;
	public String threshold = null;
	public String minDistance = null;
	public String selectionRadius = null;
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
	protected String zScale;

	public double getZScale() {
		return parseDouble(zScale);
	}

	protected String errorSigmaY;

	public double getErrorSigmaY() {
		return parseDouble(errorSigmaY);
	}

	protected String errorSigmaX;

	public double getErrorSigmaX() {
		return parseDouble(errorSigmaX);
	}

	protected String errorY;

	public double getErrorY() {
		return parseDouble(errorY);
	}

	protected String errorX;

	public double getErrorX() {
		return parseDouble(errorX);
	}

	protected String errorHeight;

	public double getErrorHeight() {
		return parseDouble(errorHeight);
	}

	protected String errorBaseline;
	private JTextField customSearchTxtField;

	public double getErrorBaseline() {
		return parseDouble(errorBaseline);
	}

	public FitPeaksOperationGUI(Node node) {
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
		gridBagLayout.rowHeights = new int[] { 14, 23, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0,
				Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
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
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});

		btnProcess = new JButton("Process");
		btnProcess.addActionListener(this);

		chckbxInsideCells = new JCheckBox("Inside Cells");
		chckbxInsideCells
				.setToolTipText("\"If the plugin requires cell Rois, check this Box to load the ROI manager with cellular rois\"");
		chckbxInsideCells.addActionListener(this);
		GridBagConstraints gbc_chckbxInsideCells = new GridBagConstraints();
		gbc_chckbxInsideCells.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxInsideCells.gridx = 0;
		gbc_chckbxInsideCells.gridy = 3;
		getContentPane().add(chckbxInsideCells, gbc_chckbxInsideCells);
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.insets = new Insets(0, 0, 0, 5);
		gbc_btnProcess.gridx = 1;
		gbc_btnProcess.gridy = 3;
		getContentPane().add(btnProcess, gbc_btnProcess);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.gridx = 2;
		gbc_btnCancel.gridy = 3;
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
	 * @param msg
	 *            the msg
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
			channel = (String) channelComboBox.getSelectedItem();
			customSearch = customSearchTxtField.getText();
			// Fix this duplication later.
			this.useCells = chckbxInsideCells.isSelected();

			run();
			dispose();

		} else if (e.getSource() == fileTypeComboBox) {
			String foo = (String) fileTypeComboBox.getSelectedItem();
			if (foo.equalsIgnoreCase("[File Type]")) {

				this.imageType = null;
			} else {
				this.imageType = (String) fileTypeComboBox.getSelectedItem();
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





	public String getCustomSearch() {
		return this.customSearch;
	}

	public ArrayList<String> getTags() {
		return FileNames.getTags(imageType);

	}


	public boolean useCells() {
		return useCells;
	}

	public boolean exportRaw() {
		return exportRaw;
	}

}
