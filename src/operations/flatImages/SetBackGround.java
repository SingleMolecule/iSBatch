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
import ij.ImagePlus;
import ij.plugin.ZProjector;
import imageOperations.NodeToImageStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import filters.GenericFilter;
import gui.LogPanel;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operations.Operation;
import test.TreeGenerator;

public class SetBackGround implements Operation {
	SetBackgroundGui dialog;
	private String channel;
	private String method;
	private ArrayList<String> imageTag;
	private String imagePath = "";

	/**
	 * Instantiates a new sets the back ground.
	 *
	 * @param treeModel
	 *            the tree model
	 */
	public SetBackGround(DatabaseModel treeModel) {
	}

	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	@Override
	public String getName() {
		return "Set BackGround";
	}

	@Override
	public boolean setup(Node node) {
		dialog = new SetBackgroundGui(node);
		if (dialog.isCanceled())
			return false;
		this.channel = dialog.getChannel();
		if(channel.equalsIgnoreCase(null)){
			LogPanel.log("No channel selected. Operation cancelled.");
			return false;
		}
			
			
		this.imageTag = dialog.getImageTag();
		this.method = dialog.getMethod();
		this.imagePath = dialog.getImagePath();

		return true;
	}

	@Override
	public void finalize(Node node) {
		System.out.println("Operation finalized");
		LogPanel.log("Background image created and stored.");
	}

	@Override
	public void visit(Root root) {
	}

	@Override
	public void visit(Experiment experiment) {
		run(experiment);

	}

	private void run(Node node) {
		LogPanel.log("Setting background on channel " + channel + " using "
				+ method);
		// Get all images with the same characteristics

		if (method.equalsIgnoreCase("Load Image")) {
			LogPanel.log("Debug info: LoadImage");

			File f = new File(imagePath);

			if (f.exists() && channel != null) {
				// Test if the file exist
				ImagePlus imp = IJ.openImage(imagePath);
				averageImage(node, imp);
				imp.close();
				LogPanel.log("Background set to channel " + channel);
			}
		} else if (method.equalsIgnoreCase("Average Images")) {
			System.out.println("Debug info: Average Images");

			ArrayList<Node> filenodes = node.getDescendents(new GenericFilter(
					channel, imageTag, null, null));

			if (filenodes.size() == 0) {
				LogPanel.log("No image found for averaging");
			}

			NodeToImageStack temp = new NodeToImageStack(filenodes, channel,
					"BeamProfile");

			ImagePlus imp = temp.getImagePlus();

			// GetProjection
			ZProjector projector = new ZProjector(imp);
			projector.setMethod(ZProjector.AVG_METHOD);
			projector.doProjection();

			// Properly save and keep track of that file now.
			File folder = new File(node.getOutputFolder());
			folder.mkdirs();
			File f = new File(node.getOutputFolder() + File.separator
					+ imp.getTitle() + ".tif");

			IJ.saveAsTiff(projector.getProjection(), f.getAbsolutePath());
			node.getProperties().put(channel + "_BeamProfile",
					f.getAbsolutePath());
			//
			// importer.importFile(node, f, channel,
			// f.getName(), f.getAbsolutePath());
		} else {
			IJ.log("Operation failed");
		}
	}

	private void averageImage(Node node, ImagePlus imp) {
		// Properly save and keep track of that file now.
		ZProjector projector = new ZProjector(imp);
		projector.setMethod(ZProjector.AVG_METHOD);
		projector.doProjection();
		File folder = new File(node.getOutputFolder());
		folder.mkdirs();
		File f = new File(node.getOutputFolder() + File.separator
				+ imp.getTitle());

		IJ.saveAsTiff(projector.getProjection(), f.getAbsolutePath());
		node.getProperties().put(channel + "_BeamProfile", f.getAbsolutePath());

	}

	@Override
	public void visit(Sample sample) {
		run(sample);

	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		System.out.println("Call load image or ignore");
	}

	@Override
	public void visit(FileNode fileNode) {
		run(fileNode);
	}

	@Override
	public void visit(OperationNode operationNode) {
	}

	@Override
	public Node[] getCreatedNodes() {
		return null;
	}

	@Override
	public HashMap<String, String> getParameters() {
		return null;
	}

	public static void main(String[] args) {
		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 2);
		SetBackgroundGui dialog = new SetBackgroundGui(model.getRoot());

		System.out.println(dialog.getChannel());

	}
}
