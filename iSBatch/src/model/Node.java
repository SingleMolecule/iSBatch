package model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import context.ContextElement;
import filters.NodeFilterInterface;
import operations.OperationElement;

public abstract class Node implements OperationElement, ContextElement, NodeInterface {
	
	private Node parent;
	private String type;
	private HashMap<String, String> properties = new HashMap<String, String>();
	private ArrayList<Node> children = new ArrayList<Node>();
	private File file;
	
	
	
	
	public String getCellROIPath() {
		return this.getProperty("CellRoi");
	}

	public void setCellROIPath(String cellROIPath) {
		this.getProperties().put("CellRoi", cellROIPath);
	}

	public Node(Node parent, String type) {
		super();
		this.parent = parent;
		this.type = type;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}
	
	public String getProperty(String name) {
		return properties.get(name);
	}
	
	public void setProperty(String name, String value) {
		properties.put(name, value);
	}
	
	public ArrayList<Node> getChildren() {
		return children;
	}

	@Override
	public String[] getContext() {
		return new String[]{ type, "All" };
	}
	
	public ArrayList<Node> getChildren(NodeFilterInterface filter) {
		
		ArrayList<Node> filteredChildren = new ArrayList<Node>();
		
		for (Node child: getChildren()) {
			try{
				if (filter.accept(child))	
					filteredChildren.add(child);
			}
			catch(NullPointerException e){
//				Do nothing
//				System.out.println("Node ignored");
			}
				
		}
		
		return filteredChildren;
		
	}
	
	public ArrayList<Node> getDescendents(NodeFilterInterface filter) {
		
		ArrayList<Node> descendents = getChildren(filter);
		
		for (Node child: getChildren())
			descendents.addAll(child.getDescendents(filter));
		
		return descendents;
		
	}
	
	
	
	
	
	
	public String getOutputFolder() {
		
		String outputFolder = getProperty("outputFolder");
		
		if (outputFolder == null) {
			
			String parentOutputFolder = parent.getOutputFolder();
			outputFolder = parentOutputFolder + File.separator + getProperty("name");
			
			if (new File(outputFolder).exists()) {
				
				int i = 2;
				
				while (new File(outputFolder + i).exists())
					i++;
			}
			
			new File(outputFolder).mkdir();
			
			setProperty("outputFolder", outputFolder);
		}
		
		return outputFolder;
		
	}
	
	@Override
	public String toString() {
		return getProperty("name");
	}

	
//
//	public String getPath() {
//		String path = null;
//		if(!this.getClass().toString().equalsIgnoreCase("model.FileNode")){
//			path =  this.getProperty("path");
//			System.out.println("this is a file Node");
//			System.out.println(path);
//		}
//		else {
//			path = this.getProperty("folder");
//			System.out.println("This will return folder");
//			System.out.println(path);
//		}
//		return path;
//	}
//	
	public String getParentFolder(){
		Node parent = this.getParent();
		return parent.getFolder();
	}
	
	public String getFolder(){
		return this.getProperty("folder");
	}
	public String getPath(){
		return this.getProperty("path");
	}
	public String getName(){
		return this.getProperty("name");
	}
	public String getChannel(){
		return this.getProperty("channel");
	}
	
	public String getBeamProfile(String channel){
		return this.getProperty(channel+"_BeamProfile");
	}
	

	public File getFile(){
		if(file == null){
			this.file = new File(getPath());
		}
		return file;
	}
	public ArrayList<String> getTag() {
		System.out.println("does not apply to this level.");
		return null;
	}

	public String getExperimentName() {

		return this.getParent().getExperimentName();
	}

	public String getSampleName() {
		
		return this.getParent().getSampleName();
	}

	public String getFieldOfViewName() {
		
		return this.getParent().getFieldOfViewName();
	}



	
	
}
