/**
 * 
 */
package operations.microbeTrackerIO;


import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import filters.NodeFilterInterface;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operations.Operation;

/**
 * @author VictorCaldas
 *
 */
public class MicrobeTrackerIO implements Operation {
	MicrobeTrackerIOGui dialog;
	
	private String channel;
	private String method;
	private String customFilter;
	private String matFilePath;
	
	
	public MicrobeTrackerIO(DatabaseModel treeModel) {
	}

	/* (non-Javadoc)
	 * @see context.ContextElement#getContext()
	 */
	@Override
	public String[] getContext() {
		return new String[]{"Experiment", "Sample", "FieldOfView"};	
	}

	/* (non-Javadoc)
	 * @see operations.Operation#getName()
	 */
	@Override
	public String getName() {
		return "MicrobeTracker I/O";
	}

	/* (non-Javadoc)
	 * @see operations.Operation#setup(model.Node)
	 */
	@Override
	public boolean setup(Node node) {
		dialog = new MicrobeTrackerIOGui(node);
		if (dialog.isCanceled())
			return false;
		getParameters();
		return true;
	}

	/* (non-Javadoc)
	 * @see operations.Operation#finalize(model.Node)
	 */
	@Override
	public void finalize(Node node) {
		System.out.println("Operation finalized");

	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.Root)
	 */

	@Override
	public void visit(Root root) {
			
	}

	@Override
	public void visit(Experiment experiment) {
		System.out.println(experiment.getProperty("type"));
		run(experiment);
		
	}

	private void getParameters() {
		this.channel = dialog.getChannel();
		this.method = dialog.getMethod();
		this.customFilter = dialog.getCustomFilter();
		this.matFilePath = dialog.getMatFilePath();
	}

	private void run(Node node) {
//		System.out.println("Run class: " + channel + " using the method " + method);
//		System.out.println(channel);
//		System.out.println(method);
//		System.out.println(customFilter);
//		System.out.println(matFilePath);
		System.out.println("--- Start ----");
		
		ArrayList<Node> nodes = node.getDescendents(filter(channel));
		
		ImagePlus imp = getStack(nodes);
		
		//save Image
		System.out.println(node.getFolder()+ File.separator + imp.getTitle());
		IJ.saveAsTiff(imp, node.getFolder()+ File.separator + imp.getTitle());
		
		//Now, finally get this list of files and create a combined
		System.out.println("--- End ----");
		
		
		
		
	}

	private NodeFilterInterface filter(String channel) {
		final String selectedChannel = channel;
		String[] channels = {"Acquisition", "Bright Field", "Red", "Green",
				"Blue", };
		NodeFilterInterface imageFileNodeFilter = null;
		// Create Filters
		if(channel == null || channel.equals("") || channel.equalsIgnoreCase("All")){
			 imageFileNodeFilter = new NodeFilterInterface() {

					@Override
					public boolean accept(Node node) {

						String path = node.getProperty("path");

						// check if this file is an image
						if (path == null
								|| !(path.toLowerCase().endsWith(".tiff") || path
										.toLowerCase().endsWith(".tif")))
							return false;

						// Get custom string and remove spaces in the begin and end. Not in
						// the middle.

						return true;
					};
				};
		}
		else if(Arrays.asList(channels).contains(channel)) {
			imageFileNodeFilter = new NodeFilterInterface() {

				public boolean accept(Node node) {
					String ch = null;
//					try{
						 ch = node.getProperty("channel");
//					} 
//						 System.out.println("the channel is : " + ch);
//					catch(NullPointerException e){
//						System.out.println(e.getMessage());
//						System.out.println("This is not a File node");
//						return false;
//					}
//					
//					System.out.println(ch);
						// check the channel of this file
						if (ch == null || !ch.equalsIgnoreCase(selectedChannel))
							return false;

					
					String path = node.getProperty("path");

					// check if this file is an image
					if (path == null
							|| !(path.toLowerCase().endsWith(".tiff") || path
									.toLowerCase().endsWith(".tif")))
						return false;

					// Get custom string and remove spaces in the begin and end. Not in
					// the middle.

					
					return true;
				};
			};
			
			
		}
		
		return imageFileNodeFilter;
		
		
	}

	@Override
	public void visit(Sample sample) {
		run(sample);
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		run(fieldOfView);
	}

	@Override
	public void visit(FileNode fileNode) {
		run(fileNode);
	}
	
	public static void main(String[] args) {
		
	}

	@Override
	public void visit(OperationNode operationNode) {
		// TODO Auto-generated method stub
		
	}
	
	private ImagePlus getStack(ArrayList<Node> nodes) {
		System.out.println(nodes.get(0).getPath());
		ImagePlus ip = IJ.openImage(nodes.get(0).getPath());
		String str = "[" +this.getChannel() +"]MTinput" ;
		
		
		ImagePlus imp2 = IJ.createImage(str, ip.getWidth(), ip.getHeight(), nodes.size(), 16);
		ImageStack stack = imp2.getStack();
		
		for (int i=0; i<nodes.size(); i++){
			System.out.println(nodes.get(i));
			ImagePlus imp = IJ.openImage(nodes.get(i).getPath());
			ImageProcessor ip2 = imp.getProcessor();
			String ImageName = nodes.get(i).getParent().getName();
			stack.setProcessor(ip2, i+1);			
			stack.setSliceLabel(ImageName, i+1);
		}
		return imp2;
		
		
	}

	private String getChannel() {
		
		return channel;
	}	

	
	
	
	

}
