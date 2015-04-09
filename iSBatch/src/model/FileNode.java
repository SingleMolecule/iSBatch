package model;

import operations.Operation;

public class FileNode extends Node {
	String channel = null;
	public static final String type = "File";

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
		}
		return channel;
	}

	public String getFoVName(){
		return this.getParent().getName();
		
	}
}
