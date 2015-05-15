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

import gui.ExperimentDialog;
import gui.FieldOfViewDialog;
import gui.FileDialog;
import gui.LogPanel;
import gui.SampleDialog;

import java.io.File;
import java.util.HashMap;

import javax.swing.JFrame;
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

// TODO: Auto-generated Javadoc
/**
 * The Class AddNodeOperation.
 */
public class AddNodeOperation implements Operation {

	/** The frame. */
	private JFrame frame;

	/** The model. */
	private DatabaseModel model;

	/** The importer. */
	private Importer importer;

	/**
	 * Instantiates a new adds the node operation.
	 *
	 * @param frame
	 *            the frame
	 * @param model
	 *            the model
	 */
	public AddNodeOperation(JFrame frame, DatabaseModel model) {
		super();
		this.frame = frame;
		this.model = model;
		importer = new Importer(model);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return "Add";
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	/**
	 * Visit.
	 *
	 * @param root
	 *            the root
	 */
	@Override
	public void visit(Root root) {

		Experiment experiment = new Experiment(root);
		experiment.setProperty("name", "experiment");
		experiment.setProperty("folder", "");
		experiment.setProperty("type", "Rapid Acquisition");
		ExperimentDialog dialog = new ExperimentDialog(frame, experiment);

		if (dialog.isCanceled())
			return;

		model.addNode(root, experiment);

		int option = JOptionPane.showConfirmDialog(frame,
				"Import all underlying samples?", "Import",
				JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION) {
			File folder = new File(experiment.getProperty("folder"));

			if (!folder.exists()) {
				JOptionPane
						.showMessageDialog(
								frame,
								"Cannot import samples because the experiment folder does not exist!",
								"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			importer.importSamples(experiment);

		}
	}

	/**
	 * Visit.
	 *
	 * @param experiment
	 *            the experiment
	 */
	@Override
	public void visit(Experiment experiment) {

		Sample sample = new Sample(experiment);
		sample.setProperty("name", "sample");
		sample.setProperty("folder", "");
		SampleDialog dialog = new SampleDialog(frame, sample);

		if (dialog.isCanceled())
			return;

		model.addNode(experiment, sample);

		int option = JOptionPane.showConfirmDialog(frame,
				"Import all underlying field of views?", "Import",
				JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION) {
			File folder = new File(sample.getProperty("folder"));

			if (!folder.exists()) {
				JOptionPane
						.showMessageDialog(
								frame,
								"Cannot import field of views because the sample folder does not exist!",
								"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			importer.importFieldOfViews(sample);

		}

	}

	/**
	 * Visit.
	 *
	 * @param sample
	 *            the sample
	 */
	@Override
	public void visit(Sample sample) {

		FieldOfView fieldOfView = new FieldOfView(sample);
		fieldOfView.setProperty("name", "field of view");
		fieldOfView.setProperty("folder", "");
		FieldOfViewDialog dialog = new FieldOfViewDialog(frame, fieldOfView);

		if (dialog.isCanceled())
			return;

		model.addNode(sample, fieldOfView);

		int option = JOptionPane.showConfirmDialog(frame,
				"Import all underlying files?", "Import",
				JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION) {
			File folder = new File(fieldOfView.getProperty("folder"));

			if (!folder.exists()) {
				JOptionPane
						.showMessageDialog(
								frame,
								"Cannot import field of views because the sample folder does not exist!",
								"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			importer.importFiles(fieldOfView);

		}
	}

	/**
	 * Visit.
	 *
	 * @param fieldOfView
	 *            the field of view
	 */
	@Override
	public void visit(FieldOfView fieldOfView) {

		FileNode fileNode = new FileNode(fieldOfView);
		FileDialog dialog = new FileDialog(frame, fileNode);

		if (!dialog.isCanceled())
			model.addNode(fieldOfView, fileNode);
	}

	/**
	 * Visit.
	 *
	 * @param fileNode
	 *            the file node
	 */
	@Override
	public void visit(FileNode fileNode) {
	}

	/**
	 * Setup.
	 *
	 * @param node
	 *            the node
	 * @return true, if successful
	 */
	@Override
	public boolean setup(Node node) {

		return true;
	}

	/**
	 * Finalize.
	 *
	 * @param node
	 *            the node
	 */
	@Override
	public void finalize(Node node) {
		LogPanel.log("Finish importing data. Double click on the node to expand it.");
	}

	/**
	 * Visit.
	 *
	 * @param operationNode
	 *            the operation node
	 */
	@Override
	public void visit(OperationNode operationNode) {
		// TODO Auto-generated method stub

	}

	/**
	 * Gets the created nodes.
	 *
	 * @return the created nodes
	 */
	@Override
	public Node[] getCreatedNodes() {
		return null;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	@Override
	public HashMap<String, String> getParameters() {
		return null;
	}

}
