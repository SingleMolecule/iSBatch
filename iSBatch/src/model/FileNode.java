package model;

import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import model.types.Channel;
import operations.Operation;

public class FileNode extends Node {
	String channel = null;
	public static final String type = "File";
	Channel channel1 = null;
	
	public FileNode(Node parent) {
		super(parent, type);
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}
}
