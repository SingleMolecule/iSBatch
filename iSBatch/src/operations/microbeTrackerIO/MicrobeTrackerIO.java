/**
 * 
 */
package operations.microbeTrackerIO;


import java.awt.image.ImageFilter;
import java.util.ArrayList;
import java.util.Arrays;

import filters.NodeFilterInterface;
import java.util.HashMap;

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
		return new String[]{"All"};	
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
		this.channel = dialog.getChannel();
		this.method = dialog.getMethod();
		this.customFilter = dialog.getCustomFilter();
		this.matFilePath = dialog.getMatFilePath();
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
		run(experiment);
		
	}

	private void run(Node node) {
		System.out.println("Run class: " + channel + " using the method " + method);
		System.out.println(channel);
		System.out.println(method);
		System.out.println(customFilter);
		System.out.println(matFilePath);
		
		
		ArrayList<Node> nodes = node.getDescendents(filter(channel));
		
		
		for(Node thisNode : nodes){
			System.out.println(thisNode.getProperty("path"));
		}
		
		System.out.println("-------");
		
		
		
		
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
						 System.out.println("the channel is : " + ch);
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
	
	
	
	

	@Override
	public Node[] getCreatedNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

}
