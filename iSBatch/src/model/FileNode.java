package model;

import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import model.types.Channel;
import operations.Operation;

public class FileNode extends Node implements FileInterface{
	String channel = null;
	public static final String type = "File";
	Channel channel1 = null;
	private ArrayList<String> tag = new ArrayList<String>();
	private String extension;
	
	
	
	
	
	
	public FileNode(Node parent) {
		super(parent, type);
	}

	public void addTag(String tag){
		this.tag.add(tag);
		
	}
	
	public void setExtension(String extention){
		this.extension = extention;
	}
	
	public String getExtension(){
		return extension;
	}

	
	

	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}

	@Override
	public String toString() {

		String channel = getProperty("channel");

		if (channel == null || channel.isEmpty())
			return getProperty("name");
		else
			return String.format("[%s] %s", channel, getProperty("name"));
	}

	@Override
	public String getOutputFolder() {
		return getParent().getOutputFolder();
	}

	public String getChannel() {
		if (channel == null) {
			this.channel = getProperty("channel");
			this.channel1 = new Channel(getProperty("channel"));
		}
		
		return channel;
	}

	public String getFoVName(){
		return this.getParent().getName();
		
	}

	@Override
	public int getNumberOfFoV() {
		return 0;
	}

	public ImagePlus getImage() {
		return IJ.openImage(this.getPath());
	}


	@Override
	public ArrayList<FieldOfView> getFieldOfView() {
				return null;
	}

	@Override
	public ArrayList<Sample> getSamples() {
		return null;
	}

	public ArrayList<String> getTag() {
		
		if(this.tag.isEmpty()){
			this.tag = new ArrayList<String>();
			this.tag.add("Raw");
		}
		if(this.getName().contains("flat")){
			this.tag.add("Flat");
		}
		//check if there are underscores

		return this.tag;
	}
	public static int countOccurrences(String haystack, char needle)
	{
	    int count = 0;
	    for (int i=0; i < haystack.length(); i++)
	    {
	        if (haystack.charAt(i) == needle)
	        {
	             count++;
	        }
	    }
	    return count;
	}
	
	
	public String getCellROIPath() {
		if(this.getParent().getType() == FieldOfView.type){
			return this.getParent().getProperty("CellRoi");
		}
		return null;
	}
	

	
	
}
