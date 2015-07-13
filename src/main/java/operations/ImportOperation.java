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

import gui.ChannelsDialog;
import ij.Prefs;

import java.io.File;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Importer;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import model.parameters.ExperimentType;

public class ImportOperation implements Operation {
	private Importer importer;
	private File file;

	public ImportOperation(DatabaseModel model) {
		importer = new Importer(model);
	}

	public String[] getContext() {
		return new String[] { "All" };
	}

	public String getName() {
		return "Import";
	}

	public boolean setup(Node node) {

		file = getDirectory();

		if (file == null)
			return false;

		ChannelsDialog dialog = new ChannelsDialog(null, file);
		return !dialog.isCanceled();
	}

	private File getDirectory() {
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setCurrentDirectory(getStoredDirectory());
		int result = fileChooser.showOpenDialog(null);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			Prefs.set("isbatch.lastSelectet.ImportDir",
					selectedFile.getAbsolutePath());
			System.out.println("Selected file: "
					+ selectedFile.getAbsolutePath());
			return selectedFile;
		}
		return null;
	}

	private File getStoredDirectory() {
		JTextField jtf = new JTextField(Prefs.get(
				"isbatch.lastSelectet.ImportDir",
				System.getProperty("user.home") + File.separator + "database"),
				20);
		return new File(jtf.getText());
	}

	public void finalize(Node node) {
	}

	public Node[] getCreatedNodes() {
		return null;
	}

	public HashMap<String, String> getParameters() {
		return null;
	}

	public void visit(Root root) {

		String[] options = new String[] {
				ExperimentType.RAPID_ACQUISITION.toString(),
				ExperimentType.TIME_LAPSE.toString() };
		int option = JOptionPane.showOptionDialog(null, "Experiment type",
				"Import Experiment", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		if (option == JOptionPane.CANCEL_OPTION)
			return;
		else
			importer.importExperiment(file, option == 0);
	}

	public void visit(Experiment experiment) {
		importer.importSamples(experiment);
	}

	public void visit(Sample sample) {
		importer.importFieldOfViews(sample);
	}

	public void visit(FieldOfView fieldOfView) {
		importer.importFiles(fieldOfView);
	}

	public void visit(FileNode fileNode) {
	}

	public void visit(OperationNode operationNode) {
	}

}
