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
package operations.microbeTrackerIO;

import gui.LogPanel;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import imageOperations.NodeToImageStack;

import java.util.ArrayList;

import utils.ImageUtils;
import model.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class MicrobeTrackerInputStack represents the image to be used as an
 * Input for cell segmentation in MatLab.
 */
public class MicrobeTrackerInputStack {

	private ImagePlus fullStack;
	private ImageStack currentStack;
	private ImageProcessor currentIp;
	private ArrayList<Node> fileNodes;
	private boolean isTimeLapse;
	private String tag = "MTinput";

	/**
	 * Instantiates a new microbe tracker input stack.
	 *
	 * @param filenodes
	 *            List of File Nodes
	 * @param isTimeLapse
	 *            Experiment type (default = False)
	 */
	public MicrobeTrackerInputStack(ArrayList<Node> filenodes,
			boolean isTimeLapse) {
		this.fileNodes = filenodes;
		this.isTimeLapse = isTimeLapse;
	}

	/**
	 * Instantiates a new microbe tracker input stack assuming @isTimeLapse to
	 * be False.
	 *
	 * @param filenodes
	 *            List of File Nodes
	 */
	public MicrobeTrackerInputStack(ArrayList<Node> filenodes) {
		this.fileNodes = filenodes;
		this.isTimeLapse = false;
	}

	/**
	 * Instantiates a new microbe tracker input stack.
	 *
	 * @param filenodes
	 *            the filenodes
	 * @param isTimeLapse
	 *            the is time lapse
	 * @param tag
	 *            the tag
	 */
	public MicrobeTrackerInputStack(ArrayList<Node> filenodes,
			boolean isTimeLapse, String tag) {
		this.fileNodes = filenodes;
		this.isTimeLapse = isTimeLapse;
		this.tag = tag;

	}

	/**
	 * Gets the image plus.
	 *
	 * @return the image plus
	 */
	public ImagePlus getImagePlus() {
		if (fullStack == null) {
			createStack();
		}
		return fullStack;
	}

	/**
	 * Creates the stack to be the MT input.
	 */
	private void createStack() {
			System.out.println("Time Lapse selected");
			this.fullStack = new NodeToImageStack(fileNodes, tag,isTimeLapse)
					.getImagePlus();
	}

	/**
	 * Creates the time lapse mt stack.
	 * 
	 * @param tag2
	 * @param fileNodes2
	 */
//	private ImagePlus createTimeLapseMTStack(ArrayList<Node> fileNodes2,
//			String tag2) {
//		/**
//		 * Has to loop through all filenodes and concatenate the images so
//		 * information can be retrieved later.
//		 * 
//		 * Name pattern for Timelapse MT Input will follow the rapid acquisition
//		 * pattern but has the the extra information of
//		 * (CurrentSlice/TotalSlices)
//		 */
//		// Create the output
//
//		ImagePlus templateImp = IJ.openImage(fileNodes.get(0).getPath());
//		String channel = fileNodes.get(0).getChannel();
//		String str = "[" + channel + "]" + tag;
//		int total = fileNodes.size();
//
//		// To loop, get the stack
////		ImageStack resultStack = templateImp.getStack();
//		// Loop and Store. The first image will be copied to the recent made
//		// stack.
//
//		ImagePlus currentImp = IJ.openImage(fileNodes.get(0).getPath());
//		ImageStack currentStack = currentImp.getStack();
//		ImageUtils.appendTitle(currentStack, fileNodes.get(0).getName());
//		ImageUtils.appendStackPositiontoTitle(currentStack);
//
//		// Now loop and add to resultsStack.
//
//		// Start from 1, since 0 is already done.
//		for (int i = 1; i < total; i++) {
//
//			currentImp = IJ.openImage(fileNodes.get(i).getPath());
//			currentStack = currentImp.getStack();
//
//			ImageUtils.appendTitle(currentStack, fileNodes.get(0).getName());
//			ImageUtils.appendStackPositiontoTitle(currentStack);
//			templateImp = appendImagePlus(templateImp, currentImp);
//
//		}
//
//		return currentImp;
//
//	}

	/**
	 * Sets the tag.
	 *
	 * @param tag
	 *            the new tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
//	public static void main(String[] args) {
//		// Open image 1
//
//		ImagePlus imp1 = IJ.openImage("D:\\RoiTest\\001 BF_flat.tif");
//		ImagePlus imp3 = IJ.openImage("D:\\RoiTest\\001 BF_flat.tif");
//
//		ImageStack stack1 = imp1.getStack();
//		ImageStack stack2 = imp1.getStack();
//
//		ImageUtils.appendTitle(stack1, "Title1");
//		ImageUtils.appendTitle(stack1, "Title2");
//
//		ImageUtils.appendStackPositiontoTitle(stack1);
//		ImageUtils.appendStackPositiontoTitle(stack2);
//
////		ImagePlus results = appendImagePlus(imp1, imp3);
////		results = appendImagePlus(results, imp3);
//		// ImagePlus results = appendImagePlus(stack1, stack2);
//		IJ.saveAsTiff(results, "D:\\RoiTest\\result3");
//		System.out.println("Done");
//	}

	/**
	 * Append image plus.
	 *
	 * @param imp1
	 *            the imp1
	 * @param imp2
	 *            the imp2
	 * @return
	 */
//	private static ImagePlus appendImagePlus(ImagePlus imp1, ImagePlus imp2) {
//		// Create Image
//		ImagePlus results = imp1;
//		// get Stacks
//
//		ImageStack resultsStack = results.getStack();
//		ImageStack stack2 = imp2.getStack();
//
//		int TotalSize = stack2.getSize();
//		for (int i = 1; i <= TotalSize; i++) {
//			ImageProcessor ip = stack2.getProcessor(i);
//			resultsStack
//					.addSlice(ImageUtils.getStackPosition(i, TotalSize), ip);
//		}
//
//		return results;
//	}

}
