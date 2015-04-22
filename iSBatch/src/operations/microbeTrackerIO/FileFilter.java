/*
 * 
 */
package operations.microbeTrackerIO;

import java.util.Arrays;

import model.Node;
import filters.NodeFilterInterface;

// TODO: Auto-generated Javadoc
/**
 * The Class FileFilter.
 */
public class FileFilter {
	
	/** The image file node filter. */
	NodeFilterInterface imageFileNodeFilter;
	
	/** The channel. */
	private String channel;
	
	/** The type. */
	private String type;
	
	/** The custom. */
	private String custom;
	
	/** The has channel. */
	private boolean hasChannel;
	
	/** The has type. */
	private boolean hasType;
	
	/** The is custom. */
	private boolean isCustom;
	
	/** The channels. */
	String[] channels = { "Acquisition", "Bright Field", "Red", "Green",
			"Blue", };

	/**
	 * Instantiates a new file filter.
	 *
	 * @param channel the channel
	 * @param type the type
	 * @param custom the custom
	 */
	public FileFilter(String channel, String type, String custom) {
		this.channel = channel;
		this.type = type;
		this.custom = custom;

		setNodeFilter();
		createFilter();
	}

	/**
	 * Creates the filter.
	 */
	private void createFilter() {
		
	}

	/**
	 * Sets the node filter.
	 */
	private void setNodeFilter() {
		setCustomBoolean(custom);

		setChannelBoolean(channel, channels);

		setTypeBoolean(type);

	}

	/**
	 * Sets the type boolean.
	 *
	 * @param type the new type boolean
	 */
	private void setTypeBoolean(String type) {
		if (type.equalsIgnoreCase("") || type.equals(null)) {
			hasType = false;
		} else {
			hasType = true;
		}
	}

	/**
	 * Sets the channel boolean.
	 *
	 * @param channel the channel
	 * @param listOfChannels the list of channels
	 */
	private void setChannelBoolean(String channel, String[] listOfChannels) {
		if (Arrays.asList(listOfChannels).contains(channel)) {
			this.hasChannel = true;
		} else {
			this.hasChannel = false;
		}

	}

	/**
	 * Sets the custom boolean.
	 *
	 * @param custom2 the new custom boolean
	 */
	private void setCustomBoolean(String custom2) {
		if (custom.equalsIgnoreCase("") || custom.equals(null)) {
			this.isCustom = false;
		} else {
			this.isCustom = true;
		}

	}

	/**
	 * Filter.
	 *
	 * @return the node filter interface
	 */
	private NodeFilterInterface filter() {
		final String selectedChannel = channel;

		NodeFilterInterface imageFileNodeFilter = null;
		// Create Filters
		if (channel == null || channel.equals("")
				|| channel.equalsIgnoreCase("All")) {
			imageFileNodeFilter = new NodeFilterInterface() {

				@Override
				public boolean accept(Node node) {

					String path = node.getProperty("path");

					// check if this file is an image
					if (path == null
							|| !(path.toLowerCase().endsWith(".tiff") || path
									.toLowerCase().endsWith(".tif")))
						return false;

					// Get custom string and remove spaces in the begin and end.
					// Not in
					// the middle.

					return true;
				};
			};
		} else if (Arrays.asList(channels).contains(channel)) {
			imageFileNodeFilter = new NodeFilterInterface() {

				public boolean accept(Node node) {
					String ch = null;
					// try{
					ch = node.getProperty("channel");
					
					if (ch == null || !ch.equalsIgnoreCase(selectedChannel))
						return false;

					String path = node.getProperty("path");

					// check if this file is an image
					if (path == null
							|| !(path.toLowerCase().endsWith(".tiff") || path
									.toLowerCase().endsWith(".tif")))
						return false;

					// Get custom string and remove spaces in the begin and end.
					// Not in
					// the middle.

					return true;
				};
			};

		}

		return imageFileNodeFilter;

	}
}
