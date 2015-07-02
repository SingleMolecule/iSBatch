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
import ij.IJ;

import java.io.File;
import java.util.HashMap;

import javax.swing.JOptionPane;

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

/**
 * The Class ImportOperation.
 */
public class ImportOperation implements Operation {
	private Importer importer;
	private File file;
	
	/**
	 * Instantiates a new import operation.
	 *
	 * @param model the model
	 */
	public ImportOperation(DatabaseModel model) {
		importer = new Importer(model);
	}
	
	public String[] getContext() {
		return new String[]{ "All" };
	}

	public String getName() {
		return "Import";
	}

	public boolean setup(Node node) {
		
		String directory = IJ.getDirectory("Choose directory to import from");
		if (directory == null) return false;
		file = new File(directory);
		ChannelsDialog dialog = new ChannelsDialog(null, file);
		return !dialog.isCanceled();
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
		
		String[] options = new String[]{ExperimentType.RAPID_ACQUISITION.toString() , ExperimentType.TIME_LAPSE.toString()};
		int option = JOptionPane.showOptionDialog(null, "Experiment type", "Import Experiment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		
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
