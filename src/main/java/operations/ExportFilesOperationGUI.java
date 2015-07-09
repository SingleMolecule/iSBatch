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
package operations;

import ij.IJ;

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
import java.io.File;

import model.Node;





import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class ExportFilesOperationGUI extends JDialog implements ActionListener {

	private JButton btnCancel;
	private JButton btnProcess;
	private static final long serialVersionUID = 1L;

	private boolean canceled = false;
	static JFrame frame;
	private Node node;

	public ExportFilesOperationGUI(Node node) {
		setModal(true);
		setTitle("Set Background Image");
		frame = new JFrame("Set Backgroung Image");
		this.node = node;

		setup();
		display();

	}

	private void setup() {
	}

	private void display() {

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 60, 60, 95, 60, 60, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 23, 0, 23, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 1.0,
				0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
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
		gbc_lblOperation.gridwidth = 2;
		gbc_lblOperation.anchor = GridBagConstraints.SOUTH;
		gbc_lblOperation.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblOperation.insets = new Insets(0, 0, 5, 5);
		gbc_lblOperation.gridx = 3;
		gbc_lblOperation.gridy = 0;
		getContentPane().add(lblOperation, gbc_lblOperation);
		
		lblFileType = new JLabel("File type");
		GridBagConstraints gbc_lblFileType = new GridBagConstraints();
		gbc_lblFileType.gridwidth = 2;
		gbc_lblFileType.insets = new Insets(0, 0, 5, 5);
		gbc_lblFileType.gridx = 1;
		gbc_lblFileType.gridy = 1;
		getContentPane().add(lblFileType, gbc_lblFileType);
		
		rdbtnImage = new JRadioButton("Image");
		buttonGroup.add(rdbtnImage);
		GridBagConstraints gbc_rdbtnImage = new GridBagConstraints();
		gbc_rdbtnImage.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnImage.gridx = 3;
		gbc_rdbtnImage.gridy = 1;
		getContentPane().add(rdbtnImage, gbc_rdbtnImage);
		
		rdbtnTable = new JRadioButton("Table");
		buttonGroup.add(rdbtnTable);
		GridBagConstraints gbc_rdbtnTable = new GridBagConstraints();
		gbc_rdbtnTable.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnTable.gridx = 4;
		gbc_rdbtnTable.gridy = 1;
		getContentPane().add(rdbtnTable, gbc_rdbtnTable);
		
		btnExportToFolder = new JButton("Export to folder");
		btnExportToFolder.addActionListener(this);
		GridBagConstraints gbc_btnExportToFolder = new GridBagConstraints();
		gbc_btnExportToFolder.gridwidth = 2;
		gbc_btnExportToFolder.insets = new Insets(0, 0, 5, 5);
		gbc_btnExportToFolder.gridx = 1;
		gbc_btnExportToFolder.gridy = 2;
		getContentPane().add(btnExportToFolder, gbc_btnExportToFolder);
		
		tfOutput = new JTextField();
		GridBagConstraints gbc_tfOutput = new GridBagConstraints();
		gbc_tfOutput.gridwidth = 3;
		gbc_tfOutput.insets = new Insets(0, 0, 5, 5);
		gbc_tfOutput.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfOutput.gridx = 3;
		gbc_tfOutput.gridy = 2;
		getContentPane().add(tfOutput, gbc_tfOutput);
		tfOutput.setColumns(10);

		btnProcess = new JButton("Process");
		btnProcess.addActionListener(this);
		
		lblSaveAs = new JLabel("Save as: ");
		GridBagConstraints gbc_lblSaveAs = new GridBagConstraints();
		gbc_lblSaveAs.gridwidth = 2;
		gbc_lblSaveAs.insets = new Insets(0, 0, 5, 5);
		gbc_lblSaveAs.gridx = 1;
		gbc_lblSaveAs.gridy = 3;
		getContentPane().add(lblSaveAs, gbc_lblSaveAs);
		
		rdbtnSequential = new JRadioButton("Sequential");
		buttonGroup_1.add(rdbtnSequential);
		GridBagConstraints gbc_rdbtnSequential = new GridBagConstraints();
		gbc_rdbtnSequential.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnSequential.gridx = 3;
		gbc_rdbtnSequential.gridy = 3;
		getContentPane().add(rdbtnSequential, gbc_rdbtnSequential);
		
		rdbtnSingle = new JRadioButton("Single");
		buttonGroup_1.add(rdbtnSingle);
		rdbtnSingle.setToolTipText("This option may create huge files.");
		GridBagConstraints gbc_rdbtnSingle = new GridBagConstraints();
		gbc_rdbtnSingle.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnSingle.gridx = 4;
		gbc_rdbtnSingle.gridy = 3;
		getContentPane().add(rdbtnSingle, gbc_rdbtnSingle);
		
		lblWithName = new JLabel("with name");
		GridBagConstraints gbc_lblWithName = new GridBagConstraints();
		gbc_lblWithName.insets = new Insets(0, 0, 5, 5);
		gbc_lblWithName.gridx = 2;
		gbc_lblWithName.gridy = 4;
		getContentPane().add(lblWithName, gbc_lblWithName);
		
		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.gridwidth = 2;
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 3;
		gbc_textField_1.gridy = 4;
		getContentPane().add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.insets = new Insets(0, 0, 0, 5);
		gbc_btnProcess.gridx = 4;
		gbc_btnProcess.gridy = 5;
		getContentPane().add(btnProcess, gbc_btnProcess);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 5;
		gbc_btnCancel.gridy = 5;
		getContentPane().add(btnCancel, gbc_btnCancel);

		pack();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);

	}

	private void run() {
	}
	
	public static void main(String[] args) {
		frame = new JFrame();

		new ExportFilesOperationGUI(null);

	}

	void error(String msg) {
		IJ.error("Batch Processor", msg);
	}

	public boolean isCanceled() {
		return canceled;
	}

	private String imageType;
	private JLabel lblFileType;
	private JRadioButton rdbtnImage;
	private JRadioButton rdbtnTable;
	private JTextField tfOutput;
	private JLabel lblSaveAs;
	private JButton btnExportToFolder;
	private JRadioButton rdbtnSequential;
	private JRadioButton rdbtnSingle;
	private JLabel lblWithName;
	private JTextField textField_1;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();

	public File OUTPUT_FOLDER;
	public String outFolderPath;
	public String method;
	public String fileType;
	public String matchingString;
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel) {
			canceled = true;
			dispose();
		} else if (e.getSource() == btnProcess) {
			
			outFolderPath = tfOutput.getText();
			method = buttonGroup_1.getSelection().toString();
			fileType = buttonGroup.getSelection().toString();
			matchingString= textField_1.getText();
			
			
			
			
			
			
			run();
			dispose();
		}
		else if(e.getSource() == btnExportToFolder){
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Select output folder");
			  chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			  //
			    // disable the "All files" option.
			    //
			    chooser.setAcceptAllFileFilterUsed(false);
			    //    
			    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
			    	OUTPUT_FOLDER = chooser.getSelectedFile();
			    	tfOutput.setText(OUTPUT_FOLDER.getAbsolutePath());
			      }
		}
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