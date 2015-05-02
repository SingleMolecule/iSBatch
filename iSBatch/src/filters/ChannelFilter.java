/*
 * 
 */
package filters;

import model.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class ChannelFilter.
 */
public class ChannelFilter implements NodeFilterInterface{

	/** The channel. */
	private String channel;
	
	/**
	 * Instantiates a new channel filter.
	 *
	 * @param channel the channel
	 */
	public ChannelFilter(String channel) {
		this.channel = channel;
	}
	
	/**
	 * Accept.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	@Override
	public boolean accept(Node node) {

		String ch = node.getChannel();
		if (!channel.equalsIgnoreCase("All")) {
			// check the channel of this file
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
	
	
