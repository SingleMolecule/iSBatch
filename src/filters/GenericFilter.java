/*
 * 
 */
package filters;

import java.util.ArrayList;

import model.FileNode;
import model.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericFilter.
 */
public class GenericFilter implements NodeFilterInterface {

	/** The channel. */
	private String channel;
	
	/** The tags. */
	private ArrayList<String> tags;
	
	/** The custom. */
	private String custom;

	/**
	 * Instantiates a new generic filter.
	 *
	 * @param channel the channel
	 * @param custom the custom
	 */
	public GenericFilter(String channel, String custom) {
		this.channel = channel;

		this.custom = custom;
		if (custom == null) {
			this.custom = "raw";
		}
	}

	/**
	 * Instantiates a new generic filter.
	 *
	 * @param channel the channel
	 * @param tags the tags
	 * @param extension the extension
	 * @param custom the custom
	 */
	public GenericFilter(String channel, ArrayList<String> tags,
			String extension, String custom) {
		this.channel = channel;
		this.tags = tags;
		this.custom = custom;

	}

	/**
	 * Accept.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	@Override
	public boolean accept(Node node) {
		System.out.println("Filter image: " + node.getPath());
		System.out.println(node.getChannel() + " " + node.getType());
		
		// Just for files
		if (!node.getType().equalsIgnoreCase(FileNode.type)) {
			System.out.println("Not the rigth type");
			return false;
		}
		
		boolean isChannel = false;
		if(channel==null || channel.equalsIgnoreCase("All")|| node.getChannel().equalsIgnoreCase(channel)){
			isChannel = true;
			System.out.println("Channel true");
		}


		boolean matchTag = false;
		System.out.println("Matchtag" + node.getTag());
		System.out.println("//////");
		System.out.println(tags.size());
		System.out.println("/////");
		if(tags.isEmpty() || tags==null || tags.size()==0){
			matchTag = true;
			System.out.println("tags null");
		}
		else{
			System.out.println("convert fileNode");
			
			FileNode fNode = (FileNode) node;
			System.out.println("convertion done");
//			System.out.println("Fnode Info: "+ fNode.getTag().get(0));
			if(fNode.getTag().size()==0 && tags.get(0).equalsIgnoreCase("Raw")){
				matchTag = true;
			}
			else {
				for (String currentTag : fNode.getTag()) {
					if (tags.contains(currentTag)) {
						matchTag = true;
					}
			}
			
			}
		}
		
//		if (!(extension == null)) {
//			// Check Extension
//
//			String path = node.getProperty("path");
//
//			// check if this file is an image
//			if (path == null
//					|| !(path.toLowerCase().endsWith(".tiff") || path
//							.toLowerCase().endsWith(".tif"))) {
//				return false;
//			}
//
//		}

		boolean containsCustomTag = false;
		if(custom==null || custom.equalsIgnoreCase("")){
			containsCustomTag = true;
		}
		if(!(custom == null)) {
			if(node.getName().contains(custom)){
				 containsCustomTag = true;
			
			}
		}
		System.out.println(isChannel +"|"+matchTag+ "|" + containsCustomTag);
		if (isChannel && matchTag && containsCustomTag) {
			return true;
		}
	
		return false;
	};
};
