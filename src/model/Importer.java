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
package model;

import java.io.File;
import java.util.ArrayList;

import model.parameters.Channel;

/**
 * The importer class takes care of importing measurement data. Depending on the
 * directory structure and filename it deduces what is an experiment, a sample,
 * a field of view or a file.
 * 
 * The level of a directory determines if it refers to an experiment, sample or
 * field of view. For each file the channel is determined by its filename.
 * 
 * Channels are (by default) determined by the following conditions:
 * <dl>
 * <dt>acquisition</dt>
 * <dd>filename ends in 'acquisition.tif'</dd>
 * <dt>bright field</dt>
 * <dd>filename ends in 'bf.tif'</dd>
 * <dt>red channel</dt>
 * <dd>filename ends in '568.tif'</dd>
 * <dt>green channel</dt>
 * <dd>filename ends '514.tif'</dd>
 * <dt>blue channel</dt>
 * <dd>filename ends '488.tif'</dd>
 * </dl>
 * 
 * Note that after 'bf', '568' and the other channels there might be an extra
 * character. For example: '001 5682.tif'. The '2' indicates that it is the
 * seconds part of the 568 image file.
 * 
 * @author C.M. Punter
 *
 */
public class Importer {

	// channel regular expressions
	/** The Acquisition regex. */
	public static String acqRegEx = ".*acquisition.?\\.tif";
	
	/** The Bright Field regex. */
	public static String bfRegEx = ".*bf.?\\.tif";
	
	/** The Red channel regex. */
	public static String redRegEx = ".*568.?\\.tif";
	
	/** The Green channel regex. */
	public static String greenRegEx = ".*514.?\\.tif";
	
	/** The Blue channel regex. */
	public static String blueRegEx = ".*488.?\\.tif";
	
	private DatabaseModel model;

	/**
	 * Instantiates a new importer.
	 *
	 * @param model the model
	 */
	public Importer(DatabaseModel model) {
		this.model = model;
	
		Node root = model.getRoot();
		String acqRegExProperty = root.getProperty("acqRegEx");
		String bfRegExProperty = root.getProperty("bfRegEx");
		String redRegExProperty = root.getProperty("redRegEx");
		String greenRegExProperty = root.getProperty("greenRegEx");
		String blueRegExProperty = root.getProperty("blueRegEx");
		
		if (acqRegExProperty != null) acqRegEx = acqRegExProperty;
		if (bfRegExProperty != null) bfRegEx = bfRegExProperty;
		if (redRegExProperty != null) redRegEx = redRegExProperty;
		if (greenRegExProperty != null) greenRegEx = greenRegExProperty;
		if (blueRegExProperty != null) blueRegEx = blueRegExProperty;
	}

	/**
	 * For a given root (node) and folder this method will import experiments. You also need to specify if this experiment is
	 * time lapse or time sampling. 
	 *
	 * @param folder the folder from which all samples are imported
	 * @param isTimeSampling determines if this experiment is time lapse or not
	 */
	public void importExperiment(File folder, boolean isTimeSampling) {

		Experiment experiment = new Experiment((Root)model.getRoot());
		experiment.setProperty("name", folder.getName());
		experiment.setProperty("folder", folder.getPath());
		experiment.setProperty("type", isTimeSampling ? "Rapid Acquisition"
				: "Time Lapse");
		model.addNode((Root)model.getRoot(), experiment);

		importSamples(experiment);
	}

	/**
	 * Import samples.
	 *
	 * @param experiment the experiment
	 */
	public void importSamples(Experiment experiment) {

		File folder = new File(experiment.getProperty("folder"));

		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {

				Sample sample = new Sample(experiment);
				sample.setProperty("name", f.getName());
				sample.setProperty("folder", f.getPath());
				model.addNode(experiment, sample);

				importFieldOfViews(sample);

			}
		}

	}

	/**
	 * Import field of views.
	 *
	 * @param sample the sample
	 */
	public void importFieldOfViews(Sample sample) {

		File folder = new File(sample.getProperty("folder"));
		String type = sample.getParent().getProperty("type");

		if (type.equals("Time Lapse")) {

			// determine all prefixes
			ArrayList<String> prefixes = new ArrayList<String>();

			for (File f : folder.listFiles()) {

				String name = f.getName();

				if (f.isFile() && name.matches("[0-9][0-9][0-9] .*")) {
					String prefix = name.substring(0, 4);

					if (!prefixes.contains(prefix))
						prefixes.add(prefix);

				}

			}

			// for each prefix we create a field of view
			for (String prefix : prefixes) {

				FieldOfView fieldOfView = new FieldOfView(sample);
				fieldOfView.setProperty("name", prefix.trim());
				fieldOfView.setProperty("folder", folder.getPath());
				fieldOfView.setProperty("prefix", prefix);
				model.addNode(sample, fieldOfView);

				importFiles(fieldOfView);

			}

		} else {

			for (File f : folder.listFiles()) {
				if (f.isDirectory()) {

					FieldOfView fieldOfView = new FieldOfView(sample);
					fieldOfView.setProperty("name", f.getName());
					fieldOfView.setProperty("folder", f.getPath());
					fieldOfView.setProperty("prefix", "");
					model.addNode(sample, fieldOfView);

					importFiles(fieldOfView);

				}
			}

		}

	}

	/**
	 * Import files.
	 *
	 * @param fieldOfView the field of view
	 */
	public void importFiles(Node fieldOfView) {

		File folder = new File(fieldOfView.getProperty("folder"));
		String prefix = fieldOfView.getProperty("prefix");

		for (File f : folder.listFiles()) {
			if (f.isFile() && f.getName().startsWith(prefix)) {
				importFile(fieldOfView, f);				
			}

		}

	}
	

	public void importFile(Node node, File file) {
		
		FileNode fileNode = new FileNode(node);
		Channel channelType = null;
		String channel = "";
		String name = file.getName().toLowerCase();

		if (name.matches(acqRegEx)){
			channel = "Acquisition"; 
			channelType = Channel.ACQUISITION;
		}
			
		else if (name.matches(bfRegEx)){
			channel = "Bright Field";
			channelType = Channel.BRIGHT_FIELD;
		}
		else if (name.matches(redRegEx) || name.contains("red")){
			channel = "Red";
			channelType = Channel.RED;
		}
		else if (name.matches(greenRegEx)){
			channel = "Green";
			channelType = Channel.GREEN;
		}
		else if (name.matches(blueRegEx)){
			channel = "Blue";
			channelType = Channel.BLUE;
		}
		
		fileNode.setProperty("channel", channel);
		fileNode.setProperty("name", file.getName());
		fileNode.setProperty("path", file.getPath());
		fileNode.addProperty("channelType", channelType);
		model.addNode(node, fileNode);
	}
	
public void importFile(Node node, File file, String channel, String name, String path) {
		FileNode fileNode = new FileNode(node);
		Channel channelType = null;

		
		fileNode.setProperty("channel", channel);
		fileNode.setProperty("name", file.getName());
		fileNode.setProperty("path", file.getPath());
		fileNode.addProperty("channelType", channelType);
		model.addNode(node, fileNode);
	}
	
}
