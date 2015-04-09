package filters;

import model.Node;

public class ChannelFilter implements NodeFilterInterface{

	private String channel;
	
	public ChannelFilter(String channel) {
		this.channel = channel;
	}
	
	@Override
	public boolean accept(Node node) {

		String ch = node.getProperty("channel");
		if (!channel.equalsIgnoreCase("All")) {
			// check the channel of this file
			if (ch == null || !ch.equals(channel))
				return false;
		}
		
		String path = node.getProperty("path");

		// check if this file is an image
		if (path == null
				|| !(path.toLowerCase().endsWith(".tiff") || path
						.toLowerCase().endsWith(".tif")))
			return false;
		return true;
	};
};
	
	
