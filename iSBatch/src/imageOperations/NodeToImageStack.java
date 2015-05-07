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

import model.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class NodeToImageStack.
 */
public class NodeToImageStack {
	
	/** The imp. */
	ImagePlus imp;
	
	/**
	 * Instantiates a new node to image stack.
	 *
	 * @param nodes the nodes
	 * @param channel the channel
	 * @param tag the tag
	 */
	public NodeToImageStack(ArrayList<Node> nodes, String channel, String tag) {
//		System.out.println(nodes.get(0).getPath());
		ImagePlus ip = IJ.openImage(nodes.get(0).getPath());
		String str = "[" +channel +"]"+tag ;
		
		
		ImagePlus imp2 = IJ.createImage(str, ip.getWidth(), ip.getHeight(), nodes.size(), 16);
		ImageStack stack = imp2.getStack();
		
		int total = nodes.size();
		
		for (int i=0; i<total; i++){
//			IJ.showProgress(i + 1, total);
			LogPanel.log("Averaging files:" + Integer.toString(i)+ " of " + total);
			System.out.println(nodes.get(i));
			ImagePlus imp = IJ.openImage(nodes.get(i).getPath());
			
			
			
			ImageProcessor ip2 = getSlice(imp);
			String ImageName = nodes.get(i).getParent().getName();
			stack.setProcessor(ip2, i+1);			
			stack.setSliceLabel(ImageName, i+1);
		}
		this.imp = imp2;
	}

	/**
	 * Gets the image plus.
	 *
	 * @return the image plus
	 */
	public ImagePlus getImagePlus() {
		return imp;
	}
	
	/**
	 * Gets the slice.
	 *
	 * @param imp the imp
	 * @return the slice
	 */
	private ImageProcessor getSlice(ImagePlus imp) {
		ImageProcessor ip = imp.getProcessor();
		if(imp.getStack().getSize()!=1){
			
			ZProjector projector = new ZProjector(imp);
			projector.setMethod(ZProjector.AVG_METHOD);
			projector.doProjection();
			
			ip = projector.getProjection().getProcessor();
				
		}
		
		
			return ip;
	}
}
