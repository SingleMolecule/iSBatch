/*
 * 
 */
package imageOperations;

import gui.LogPanel;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.ZProjector;
import ij.process.ImageProcessor;

import java.util.ArrayList;

import utils.ImageUtils;
import model.Node;

public class NodeToImageStack {

	private ImagePlus imp;

	public NodeToImageStack(ArrayList<Node> nodes, String channel, boolean isTimeLapse) {
		if(isTimeLapse){
			/**
			 * Has to loop through all filenodes and concatenate the images so
			 * information can be retrieved later.
			 * 
			 * Name pattern for Timelapse MT Input will follow the rapid acquisition
			 * pattern but has the the extra information of
			 * (CurrentSlice/TotalSlices)
			 */
			// Create the output

			ImagePlus templateImp = IJ.openImage(fileNodes.get(0).getPath());
			String channel = fileNodes.get(0).getChannel();
			String str = "[" + channel + "]" + tag;
			int total = fileNodes.size();

			// To loop, get the stack
//			ImageStack resultStack = templateImp.getStack();
			// Loop and Store. The first image will be copied to the recent made
			// stack.

			ImagePlus currentImp = IJ.openImage(fileNodes.get(0).getPath());
			ImageStack currentStack = currentImp.getStack();
			ImageUtils.appendTitle(currentStack, fileNodes.get(0).getName());
			ImageUtils.appendStackPositiontoTitle(currentStack);

			// Now loop and add to resultsStack.

			// Start from 1, since 0 is already done.
			for (int i = 1; i < total; i++) {

				currentImp = IJ.openImage(fileNodes.get(i).getPath());
				currentStack = currentImp.getStack();

				ImageUtils.appendTitle(currentStack, fileNodes.get(0).getName());
				ImageUtils.appendStackPositiontoTitle(currentStack);
				templateImp = appendImagePlus(templateImp, currentImp);

			}

			return currentImp;

		}
			
			
		}
		else{
			// System.out.println(nodes.get(0).getPath());
			ImagePlus ip = IJ.openImage(nodes.get(0).getPath());
			String str = "[" + channel + "]" + isTimeLapse;
			int total = nodes.size();
			ImagePlus imp2 = IJ.createImage(str, ip.getWidth(), ip.getHeight(),
					total, ip.getBitDepth());
			ImageStack stack = imp2.getStack();

			for (int i = 0; i < total; i++) {
				LogPanel.log("Averaging files:" + Integer.toString(i + 1) + " of "
						+ total);
				System.out.println(nodes.get(i));
				ImagePlus imp = IJ.openImage(nodes.get(i).getPath());

				ImageProcessor ip2 = getSlice(imp);
				String ImageName = nodes.get(i).getParent().getName();
				stack.setProcessor(ip2, i + 1);
				stack.setSliceLabel(ImageName, i + 1);
			}
			this.imp = imp2;
		}
		
		
		
	}

	public NodeToImageStack(ArrayList<Node> nodes, String tag) {
		ImagePlus ip = IJ.openImage(nodes.get(0).getPath());
		String channel = nodes.get(0).getChannel();
		String str = "[" + channel + "]" + tag;
		int total = nodes.size();
		ImagePlus imp2 = IJ.createImage(str, ip.getWidth(), ip.getHeight(),
				total, ip.getBitDepth());
		ImageStack stack = imp2.getStack();

		for (int i = 0; i < total; i++) {
			LogPanel.log("Averaging files:" + Integer.toString(i + 1) + " of "
					+ total);
			System.out.println(nodes.get(i));
			ImagePlus imp = IJ.openImage(nodes.get(i).getPath());

			ImageProcessor ip2 = getSlice(imp);
			String ImageName = nodes.get(i).getParent().getName();
			stack.setProcessor(ip2, i + 1);
			stack.setSliceLabel(ImageName, i + 1);
		}
		this.imp = imp2;

	}

	public ImagePlus getImagePlus() {
		return imp;
	}

	private ImageProcessor getSlice(ImagePlus imp) {
		ImageProcessor ip = imp.getProcessor();
		if (imp.getStack().getSize() != 1) {

			ZProjector projector = new ZProjector(imp);
			projector.setMethod(ZProjector.AVG_METHOD);
			projector.doProjection();

			ip = projector.getProjection().getProcessor();

		}
		return ip;
	}
}