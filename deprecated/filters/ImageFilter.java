/*
 * 
 */
package filters;

import model.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageFilter.
 */
public class ImageFilter implements NodeFilterInterface{
	
	/**
	 * Accept.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
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


	
}
