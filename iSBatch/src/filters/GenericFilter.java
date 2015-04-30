package filters;

import java.util.ArrayList;

import model.FileNode;
import model.Node;

public class GenericFilter implements NodeFilterInterface {

	private String channel;
	private ArrayList<String> tags;
	private String extension;
	private String custom;

	public GenericFilter(String channel, String custom) {
		this.channel = channel;

		this.custom = custom;
		if (custom == null) {
			this.custom = "raw";
		}
	}

	public GenericFilter(String channel, ArrayList<String> tags,
			String extension, String custom) {
		this.channel = channel;
		this.tags = tags;
		this.extension = extension;
		this.custom = custom;

	}

	@Override
	public boolean accept(Node node) {
		
		// Just for files
		if (!node.getType().equalsIgnoreCase(FileNode.type)) {
			return false;
		}
		
		boolean isChannel = false;
		if(channel==null || channel.equalsIgnoreCase("All")|| node.getChannel().equalsIgnoreCase(channel)){
			isChannel = true;
		}


		boolean matchTag = false;
		if(tags.isEmpty() || tags==null || tags.size()==0){
			matchTag = true;
//			System.out.println("tags null");
		}
		else{
			FileNode fNode = (FileNode) node;
			for (String currentTag : fNode.getTag()) {
				if (tags.contains(currentTag)) {
					matchTag = true;
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
//		System.out.println(isChannel +"|"+matchTag+ "|" + containsCustomTag);
		if (isChannel && matchTag && containsCustomTag) {
			return true;
		}
	
		return false;
	};
};
