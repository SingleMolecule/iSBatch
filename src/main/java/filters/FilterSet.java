/*
 * 
 */
package filters;

import model.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class FilterSet.
 */
public class FilterSet  {
		
		/** The node. */
		Node node;
		
	/**
	 * Instantiates a new filter set.
	 *
	 * @param channel the channel
	 * @param type the type
	 * @param custom the custom
	 */
	public FilterSet(String channel, String type, String custom){
	}
	
	/**
	 * Instantiates a new filter set.
	 */
	public FilterSet(){
	
	}
	
	
	
	
	
	/**
	 * Instantiates a new filter set.
	 *
	 * @param node the node
	 */
	public FilterSet(Node node) {
		this.node = node;// TODO Auto-generated constructor stub
	}





	/** The image file node filter. */
	public NodeFilterInterface imageFileNodeFilter = new NodeFilterInterface() {

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
			}
			
		};
	};
