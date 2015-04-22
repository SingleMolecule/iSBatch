package model;

import java.util.ArrayList;

import filters.ChannelFilter;
import filters.NodeFilter;
import operations.Operation;

public class FieldOfView extends Node {

	public static final String type = "FieldOfView";
	
	public FieldOfView(Sample parent) {
		super(parent, type);
	}
	
	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}
	
	public String getExperimentType(){
		return this.getParent().getParent().getProperty("type");
	}

	public  ArrayList<FileNode> getImages() {
		ArrayList<Node> nodes = this.getChildren(new NodeFilter(FieldOfView.type));
		
		//convert to sample array
		ArrayList<FileNode> filesNodes = new ArrayList<FileNode>();
		for(Node node : nodes){
			filesNodes.add((FileNode) node);
					}
		return filesNodes;		
		
	}

	public  ArrayList<FileNode> getImages(String channel) {
		ArrayList<Node> nodes = this.getChildren(new ChannelFilter(channel));
		System.out.println(nodes.size());
		//convert to sample array
		ArrayList<FileNode> filesNodes = new ArrayList<FileNode>();
		for(Node node : nodes){
			FileNode thisNode = (FileNode)node;
//			if(thisNode.getChannel().equalsIgnoreCase(channel))
//			{
				filesNodes.add(thisNode);
//			}
		}
		return filesNodes;		
		
	}

	@Override
	public int getNumberOfFoV() {
		return 1;
	}

	public String getCellularROIs() {
	
		return null;
	}
	
	
	
}
