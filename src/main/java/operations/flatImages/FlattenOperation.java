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

import java.io.File;
import java.util.HashMap;

import javax.swing.JFileChooser;

import operations.Operation;
import filters.NodeFilterInterface;
import gui.LogPanel;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Importer;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;

public class FlattenOperation implements Operation {
	private Importer importer;
	private String[] channels = new String[] { "Acquisition", "Bright Field",
			"Red", "Green", "Blue", };

	private String channel;
	private double electronicOffset = 3300;
	private boolean useElectronicOffsetImage = false;
	private ImagePlus electronicOffsetImage;
	private ImagePlus backgroundImage;
	private NodeFilterInterface imageFileNodeFilter = new NodeFilterInterface() {

		@Override
		public boolean accept(Node node) {

			if (!node.getType().equals(FileNode.type))
				return false;

			String ch = node.getProperty("channel");

			// check the channel of this file
			if (ch == null || !ch.equalsIgnoreCase(channel))
				return false;

			String path = node.getProperty("path");

			// check if this file is an image
			if (path == null
					|| !(path.toLowerCase().endsWith(".tiff") || path
							.toLowerCase().endsWith(".tif")))
				return false;

			return true;
		}
	};

	public FlattenOperation(DatabaseModel model) {
		this.importer = new Importer(model);
	}

	@Override
	public String[] getContext() {
		return new String[] { "Experiment", "Sample", "FieldOfView" };
	}

	@Override
	public String getName() {
		return "Flatten";
	}

	@Override
	public boolean setup(Node node) {

		// electronic offset
		// beam profile image

		GenericDialog dialog = new GenericDialog("Flatten Operation");
		dialog.addChoice("Channel", channels, channels[0]);
		dialog.addNumericField("Electronic_Offset", electronicOffset, 2);
		dialog.addCheckbox("Use_Electronic_Offset_Image",
				useElectronicOffsetImage);
		dialog.showDialog();

		if (dialog.wasCanceled())
			return false;

		channel = dialog.getNextChoice();
		electronicOffset = dialog.getNextNumber();
		useElectronicOffsetImage = dialog.getNextBoolean();

		if (useElectronicOffsetImage) {

			JFileChooser fileChooser = new JFileChooser(
					node.getProperty("folder"));
			fileChooser.setDialogTitle("Select Electronic Offset Image");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int option = fileChooser.showOpenDialog(null);

			if (option == JFileChooser.APPROVE_OPTION)
				electronicOffsetImage = IJ.openImage(fileChooser
						.getSelectedFile().getPath());
			else
				return false;

		}

		backgroundImage = node.getBeamProfileAsImage(channel);

		if (backgroundImage == null) {
			return false;
		}

		// }

		return true;
	}

	@Override
	public void finalize(Node node) {

	}

	@Override
	public void visit(Root root) {
	}

	@Override
	public void visit(Experiment experiment) {

		// loop through all the samples
		for (Node node : experiment.getDescendents(imageFileNodeFilter))
			node.accept(this);

	}

	@Override
	public void visit(Sample sample) {

		// loop through all the field of views
		for (Node node : sample.getDescendents(imageFileNodeFilter))
			node.accept(this);

	}

	@Override
	public void visit(FieldOfView fieldOfView) {

		// loop through all files
		for (Node node : fieldOfView.getDescendents(imageFileNodeFilter))
			node.accept(this);

	}

	@Override
	public void visit(FileNode fileNode) {

		LogPanel.log("flattening " + fileNode);

		String channel = fileNode.getProperty("channel");

		if (channel == null || !channel.equals(this.channel))
			return;

		String path = fileNode.getProperty("path");

		// check if this file is an image
		if (path == null
				|| !(path.toLowerCase().endsWith(".tiff") || path.toLowerCase()
						.endsWith(".tif")))
			return;

		ImagePlus imp = IJ.openImage(path);
		ImageConverter converter = new ImageConverter(imp);
		converter.convertToGray32();

		ImageProcessor backgroundIp = backgroundImage.getProcessor();

		// determine maximum pixel value
		double maximumPixelValue = backgroundIp.getMax();

		ImageProcessor electronicOffsetIp = null;

		if (useElectronicOffsetImage)
			electronicOffsetIp = electronicOffsetImage.getProcessor();

		// go through the whole stack
		ImageStack stack = imp.getStack();

		for (int slice = 1; slice <= stack.getSize(); slice++) {

			ImageProcessor ip = stack.getProcessor(slice);

			// subtract electronic offset and
			// divide by background
			for (int y = 0; y < ip.getHeight(); y++) {
				for (int x = 0; x < ip.getWidth(); x++) {

					if (useElectronicOffsetImage)
						electronicOffset = electronicOffsetIp.getf(x, y);

					double backgroundValue = (backgroundIp.getf(x, y) - electronicOffset)
							/ (maximumPixelValue - electronicOffset);
					double value = ip.getf(x, y) - electronicOffset;

					ip.setf(x, y, (float) Math.abs(value / backgroundValue));
				}
			}
		}

		// filename without extension
		File file = new File(path);
		String filename = file.getName();
		String filenameWithoutExtension = filename.substring(0,
				filename.lastIndexOf('.'));
		File outputFile = new File(fileNode.getOutputFolder(),
				filenameWithoutExtension + "_flat.tif");


		LogPanel.log("saving flattened file as " + outputFile);
		IJ.saveAsTiff(imp, outputFile.getPath());
		LogPanel.log("adding to tree");

		importer.importFile(fileNode.getParent(), outputFile, channel,
				outputFile.getName());

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

}
