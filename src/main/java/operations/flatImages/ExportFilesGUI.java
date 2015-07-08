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
package operations.flatImages;

import ij.IJ;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JRadioButton;

public class ExportFilesGUI extends JDialog implements ActionListener {

	private JButton btnCancel;
	private JButton btnProcess;
	private static final long serialVersionUID = 1L;
	private static final String[] methods = { "[Method]", "Load Image",
			"Average Images" };

	private String[] types = new String[] { "[File Type]", "Raw", "Flat",
			"Discoidal" };
	public JTextField fileNameContains;
	private boolean canceled = false;
	static JFrame frame;
	private Node node;
	private String channel, method, imagePath;
	public File OUTPUT_FOLDER;

	public ExportFilesGUI(Node node) {
		setModal(true);
		setTitle("Export Files");
		frame = new JFrame("Export my Files");
		this.node = node;

		setup();
		display();

	}

	private void setup() {
	}

	private void display() {

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 60, 92, 60, 22, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 23, 0, 23, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0, 1.0, 1.0,
				Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
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

		JLabel lblOperation = null;
		lblOperation = setLabelName(lblOperation, node);

		GridBagConstraints gbc_lblOperation = new GridBagConstraints();
		gbc_lblOperation.gridwidth = 2;
		gbc_lblOperation.anchor = GridBagConstraints.SOUTH;
		gbc_lblOperation.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblOperation.insets = new Insets(0, 0, 5, 5);
		gbc_lblOperation.gridx = 2;
		gbc_lblOperation.gridy = 0;
		getContentPane().add(lblOperation, gbc_lblOperation);

		lblFileTypeTo = new JLabel("File type to Export");
		GridBagConstraints gbc_lblFileTypeTo = new GridBagConstraints();
		gbc_lblFileTypeTo.insets = new Insets(0, 0, 5, 5);
		gbc_lblFileTypeTo.gridx = 1;
		gbc_lblFileTypeTo.gridy = 1;
		getContentPane().add(lblFileTypeTo, gbc_lblFileTypeTo);

		rdbtnImage = new JRadioButton("Image");
		fileTypeChooser.add(rdbtnImage);
		GridBagConstraints gbc_rdbtnImage = new GridBagConstraints();
		gbc_rdbtnImage.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnImage.gridx = 2;
		gbc_rdbtnImage.gridy = 1;
		getContentPane().add(rdbtnImage, gbc_rdbtnImage);

		rdbtnTable = new JRadioButton("Table");
		fileTypeChooser.add(rdbtnTable);
		GridBagConstraints gbc_rdbtnTable = new GridBagConstraints();
		gbc_rdbtnTable.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnTable.gridx = 3;
		gbc_rdbtnTable.gridy = 1;
		getContentPane().add(rdbtnTable, gbc_rdbtnTable);

		JLabel lblFilenameContains = new JLabel("Filename contains:");
		GridBagConstraints gbc_lblFilenameContains = new GridBagConstraints();
		gbc_lblFilenameContains.insets = new Insets(0, 0, 5, 5);
		gbc_lblFilenameContains.anchor = GridBagConstraints.EAST;
		gbc_lblFilenameContains.gridx = 1;
		gbc_lblFilenameContains.gridy = 2;
		getContentPane().add(lblFilenameContains, gbc_lblFilenameContains);

		fileNameContains = new JTextField();
		fileNameContains.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				imagePath = fileNameContains.getText();
			}
		});

		GridBagConstraints gbc_fileNameContains = new GridBagConstraints();
		gbc_fileNameContains.gridwidth = 2;
		gbc_fileNameContains.insets = new Insets(0, 0, 5, 5);
		gbc_fileNameContains.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileNameContains.gridx = 2;
		gbc_fileNameContains.gridy = 2;
		getContentPane().add(fileNameContains, gbc_fileNameContains);
		fileNameContains.setColumns(1);

		lblExportAs = new JLabel("Export as ");
		GridBagConstraints gbc_lblExportAs = new GridBagConstraints();
		gbc_lblExportAs.insets = new Insets(0, 0, 5, 5);
		gbc_lblExportAs.gridx = 1;
		gbc_lblExportAs.gridy = 3;
		getContentPane().add(lblExportAs, gbc_lblExportAs);

		rdbtnSingle = new JRadioButton("Single file");
		exportTypeChooser.add(rdbtnSingle);
		GridBagConstraints gbc_rdbtnSingle = new GridBagConstraints();
		gbc_rdbtnSingle.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnSingle.gridx = 2;
		gbc_rdbtnSingle.gridy = 3;
		getContentPane().add(rdbtnSingle, gbc_rdbtnSingle);

		rdbtnSequen = new JRadioButton("Sequential");
		exportTypeChooser.add(rdbtnSequen);
		GridBagConstraints gbc_rdbtnSequen = new GridBagConstraints();
		gbc_rdbtnSequen.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnSequen.gridx = 3;
		gbc_rdbtnSequen.gridy = 3;
		getContentPane().add(rdbtnSequen, gbc_rdbtnSequen);

		chooseFolderButton = new JButton("Choose Folder");
		chooseFolderButton.addActionListener(this);

		lblNewLabel = new JLabel("Export name");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 4;
		getContentPane().add(lblNewLabel, gbc_lblNewLabel);

		exportName = new JTextField();
		GridBagConstraints gbc_exportName = new GridBagConstraints();
		gbc_exportName.gridwidth = 2;
		gbc_exportName.insets = new Insets(0, 0, 5, 5);
		gbc_exportName.fill = GridBagConstraints.HORIZONTAL;
		gbc_exportName.gridx = 2;
		gbc_exportName.gridy = 4;
		getContentPane().add(exportName, gbc_exportName);
		exportName.setColumns(10);
		GridBagConstraints gbc_chooseFolderButton = new GridBagConstraints();
		gbc_chooseFolderButton.insets = new Insets(0, 0, 5, 5);
		gbc_chooseFolderButton.gridx = 1;
		gbc_chooseFolderButton.gridy = 5;
		getContentPane().add(chooseFolderButton, gbc_chooseFolderButton);

		pathToFolder = new JTextField();
		GridBagConstraints gbc_pathToFolder = new GridBagConstraints();
		gbc_pathToFolder.gridwidth = 2;
		gbc_pathToFolder.insets = new Insets(0, 0, 5, 5);
		gbc_pathToFolder.fill = GridBagConstraints.HORIZONTAL;
		gbc_pathToFolder.gridx = 2;
		gbc_pathToFolder.gridy = 5;
		getContentPane().add(pathToFolder, gbc_pathToFolder);
		pathToFolder.setColumns(10);

		btnProcess = new JButton("Process");
		btnProcess.addActionListener(this);
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.insets = new Insets(0, 0, 0, 5);
		gbc_btnProcess.gridx = 2;
		gbc_btnProcess.gridy = 6;
		getContentPane().add(btnProcess, gbc_btnProcess);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 3;
		gbc_btnCancel.gridy = 6;
		getContentPane().add(btnCancel, gbc_btnCancel);

		pack();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);

	}

	private JLabel setLabelName(JLabel lblOperation, Node node2) {

		if (node == null) {
			lblOperation = new JLabel(" Debug - Null node");
		} else {
			lblOperation = new JLabel(node.getType() + ": " + node.toString());
		}

		return lblOperation;
	}

	private void run() {
	}

	public static void main(String[] args) {
		frame = new JFrame();

		new ExportFilesGUI(null);

	}

	void error(String msg) {
		IJ.error("Batch Processor", msg);
	}

	public boolean isCanceled() {
		return canceled;
	}

	private String imageType;
	private JRadioButton rdbtnImage;
	private JRadioButton rdbtnTable;
	private JLabel lblFileTypeTo;
	private JLabel lblExportAs;
	private JRadioButton rdbtnSingle;
	private JRadioButton rdbtnSequen;
	private JButton chooseFolderButton;
	public JTextField pathToFolder;
	private final ButtonGroup fileTypeChooser = new ButtonGroup();
	private final ButtonGroup exportTypeChooser = new ButtonGroup();
	public Object fileType;
	public Object saveType;
	private JLabel lblNewLabel;
	public JTextField exportName;
	public String exportNameFile;

	@Override
	public void actionPerformed(ActionEvent e) {
		Object click = e.getSource();
		if (click == btnCancel) {
			canceled = true;
			dispose();
		} else if (click == btnProcess) {
			OUTPUT_FOLDER = new File(pathToFolder.getText());
			fileType = getSelectedButtonText(fileTypeChooser);
			saveType = getSelectedButtonText(exportTypeChooser);
			exportNameFile = exportName.getText();
			run();
			dispose();
		} else if (click == chooseFolderButton) {
			selectAndSetFolder();
		}

	}

	private void selectAndSetFolder() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Choose folder to save");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int option = fileChooser.showOpenDialog(null);

		if (option == JFileChooser.APPROVE_OPTION)
			pathToFolder.setText(fileChooser.getSelectedFile()
					.getAbsolutePath());
	}


    public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }

        return null;
    }
    
	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	public String getMethod() {
		return method;
	}

	public String getImagePath() {
		return imagePath;
	}

	public ArrayList<String> getImageTag() {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(imageType);
		return temp;
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
}