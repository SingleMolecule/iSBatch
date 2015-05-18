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

// TODO: Auto-generated Javadoc
/**
 * The Class SetBackGround.
 *
 * @author VictorCaldas
 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see context.ContextElement#getContext()
	 */
	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#getName()
	 */
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return "Set BackGround";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#setup(model.Node)
	 */
	/**
	 * Setup.
	 *
	 * @param node
	 *            the node
	 * @return true, if successful
	 */
	@Override
	public boolean setup(Node node) {
		dialog = new SetBackgroundGui(node);
		if (dialog.isCanceled())
			return false;
		this.channel = dialog.getChannel();
		this.imageTag = dialog.getImageTag();
		this.method = dialog.getMethod();
		this.imagePath = dialog.getImagePath();

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see operations.Operation#finalize(model.Node)
	 */
	/**
	 * Finalize.
	 *
	 * @param node
	 *            the node
	 */
	@Override
	public void finalize(Node node) {
		System.out.println("Operation finalized");
		LogPanel.log("Background image created and stored.");
	}

	@Override
	public void visit(Root root) {
		// Does not apply to ROOT.
	}
	/**
	 * Visit.
	 *
	 * @param experiment
	 *            the experiment
	 */
	@Override
	public void visit(Experiment experiment) {
		run(experiment);

	}

	/**
	 * Run.
	 *
	 * @param node
	 *            the node
	 */
	private void run(Node node) {
		LogPanel.log("Run class: " + channel + " using the method " + method);
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
			if(filenodes.size() == 0){
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
			File f = new File(node.getOutputFolder()
					+ File.separator + imp.getTitle()+".tif");
			
			
				
			IJ.saveAsTiff(projector.getProjection(), f.getAbsolutePath());
			node.getProperties().put(channel + "_BeamProfile",
					f.getAbsolutePath());

			
			
			
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
				File f = new File(node.getOutputFolder()
						+ File.separator + imp.getTitle());
				
				IJ.saveAsTiff(projector.getProjection(), f.getAbsolutePath());
				node.getProperties().put(channel + "_BeamProfile",
						f.getAbsolutePath());

	}

	/**
	 * Save.
	 *
	 * @param node
	 *            the node
	 * @param imp
	 *            the imp
	 */

	/**
	 * Visit.
	 *
	 * @param sample
	 *            the sample
	 */
	@Override
	public void visit(Sample sample) {
		run(sample);

	}

	/**
	 * Visit.
	 *
	 * @param fieldOfView
	 *            the field of view
	 */
	@Override
	public void visit(FieldOfView fieldOfView) {
		System.out.println("Call load image or ignore");
	}

	/**
	 * Visit.
	 *
	 * @param fileNode
	 *            the file node
	 */
	@Override
	public void visit(FileNode fileNode) {
		run(fileNode);
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
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	@Override
	public HashMap<String, String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

}
