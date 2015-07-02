/*
 * 
 */
package filters;

import model.Node;

public class ChannelFilter implements NodeFilterInterface{

	private String channel;
	public ChannelFilter(String channel) {
		this.channel = channel;
	}
	
	public boolean accept(Node node) {

		String ch = node.getChannel();
		if (!channel.equalsIgnoreCase("All")) {
			if (ch == null || !ch.equalsIgnoreCase(channel))
				return false;
		}
		
		String path = node.getProperty("path");

		// check if this file is an image
		if (path == null
				|| !(path.toLowerCase().endsWith(".tiff") || path
						.toLowerCase().endsWith(".tif"))){
			return false;
		}
			
		return true;
	};
};
	
	
