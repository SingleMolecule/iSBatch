package filters;


import model.Node;

public class GenericFilter implements NodeFilterInterface{

	private String channel;
	private String customTag;
	
	public GenericFilter(String channel, String customTag) {
		this.channel = channel;
		
		this.customTag =customTag;
		if(customTag==null){
			this.customTag="raw";
		}
	}
	
	@Override
	public boolean accept(Node node) {
		
		String ch = node.getChannel();
		String name = node.getTag();
//		System.out.println(ch + " to match with " + channel + "|| " + name + " to match with " + customTag);
		// Filter by channel
		if (!channel.equalsIgnoreCase("All")) {
			// check the channel of this file
			if (ch == null || !ch.equalsIgnoreCase(channel))
				return false;
		}
		
		// Filter by imageTag (e.g. raw, flat, etc or CUSTOM
		// File names follow the pattern CHANNEL_TYPE.FILE_EXTENSION
		// For now, getting the term between the dot ant the last _ should suffice.
		
		//Split the name in several parts
		String[] array = name.split("_|\\."); 
		for(int i=0; i<array.length; i++){
			boolean checkType = false;
			if(customTag.equalsIgnoreCase(array[i])){
				checkType = true;
			}
			if(checkType ==false){
				return false;
			}
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
	
	
