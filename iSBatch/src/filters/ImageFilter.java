package filters;

import model.Node;

public class ImageFilter implements NodeFilterInterface{
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

	
}
