/*
 * 
 */
package model;

import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import model.parameters.Channel;
import operations.Operation;

// TODO: Auto-generated Javadoc
/**
 * The Class FileNode.
 */
public class FileNode extends Node implements FileInterface{
	
	/** The channel. */
	String channel = null;
	
	/** The Constant type. */
	public static final String type = "File";
	
	/** The channel1. */
	Channel channel1 = null;
	
	/** The tag. */
	private ArrayList<String> tag = new ArrayList<String>();
	
	/** The extension. */
	private String extension;
	
	
	
	
	
	
	/**
	 * Instantiates a new file node.
	 *
	 * @param parent the parent
	 */
	public FileNode(Node parent) {
		super(parent, type);
	}

	/**
	 * Adds the tag.
	 *
	 * @param tag the tag
	 */
	public void addTag(String tag){
		this.tag.add(tag);
		
	}
	
	/**
	 * Sets the extension.
	 *
	 * @param extention the new extension
	 */
	public void setExtension(String extention){
		this.extension = extention;
	}
	
	/**
	 * Gets the extension.
	 *
	 * @return the extension
	 */
	public String getExtension(){
		return extension;
	}

	
	

	/**
	 * Accept.
	 *
	 * @param operation the operation
	 */
	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {

		String channel = getProperty("channel");

		if (channel == null || channel.isEmpty())
			return getProperty("name");
		else
			return String.format("[%s] %s", channel, getProperty("name"));
	}

	/**
	 * Gets the output folder.
	 *
	 * @return the output folder
	 */
	@Override
	public String getOutputFolder() {
		return getParent().getOutputFolder();
	}

	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public String getChannel() {
		if (channel == null) {
			this.channel = getProperty("channel");
//			this.channel1 = new Channel(getProperty("channel"));
		}
		
		return channel;
	}

	/**
	 * Gets the fo v name.
	 *
	 * @return the fo v name
	 */
	public String getFoVName(){
		return this.getParent().getName();
		
	}

	/**
	 * Gets the number of fo v.
	 *
	 * @return the number of fo v
	 */
	@Override
	public int getNumberOfFoV() {
		return 0;
	}

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public ImagePlus getImage() {
		return IJ.openImage(this.getPath());
	}


	/**
	 * Gets the field of view.
	 *
	 * @return the field of view
	 */
	@Override
	public ArrayList<FieldOfView> getFieldOfView() {
				return null;
	}

	/**
	 * Gets the samples.
	 *
	 * @return the samples
	 */
	@Override
	public ArrayList<Sample> getSamples() {
		return null;
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
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
	
	/**
	 * Count occurrences.
	 *
	 * @param haystack the haystack
	 * @param needle the needle
	 * @return the int
	 */
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
	
	
	/**
	 * Gets the cell roi path.
	 *
	 * @return the cell roi path
	 */
	public String getCellROIPath() {
		if(this.getParent().getType() == FieldOfView.type){
			return this.getParent().getProperty("CellRoi");
		}
		return null;
	}
	

	
	
}
