package filters;

import model.Node;

public class ImageFilter implements NodeFilterInterface{

	@Override
	public boolean accept(Node node) {
		String path = node.getProperty("path");
		if (path == null
				|| !(path.toLowerCase().endsWith(".tiff") || path
						.toLowerCase().endsWith(".tif")))
			return false;
		return true;
	}

	
}
