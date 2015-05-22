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
public class NodeToImageStack {
	
	private ImagePlus imp;
	
	public NodeToImageStack(ArrayList<Node> nodes, String channel, String tag) {
//		System.out.println(nodes.get(0).getPath());
		ImagePlus ip = IJ.openImage(nodes.get(0).getPath());
		String str = "[" +channel +"]"+tag ;
		int total = nodes.size();
		ImagePlus imp2 = IJ.createImage(str, ip.getWidth(), ip.getHeight(), total, ip.getBitDepth());
		ImageStack stack = imp2.getStack();
				
		
		for (int i=0; i<total; i++){
			LogPanel.log("Averaging files:" + Integer.toString(i+1)+ " of " + total);
			System.out.println(nodes.get(i));
			ImagePlus imp = IJ.openImage(nodes.get(i).getPath());
			
			ImageProcessor ip2 = getSlice(imp);
			String ImageName = nodes.get(i).getParent().getName();
			stack.setProcessor(ip2, i+1);			
			stack.setSliceLabel(ImageName, i+1);
		}
		this.imp = imp2;
	}
	public ImagePlus getImagePlus() {
		return imp;
	}
	
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
