/*
 * 
 */
package model;

import java.io.File;
import java.util.ArrayList;

import filters.ChannelFilter;
import filters.NodeFilter;
import operations.Operation;

// TODO: Auto-generated Javadoc
/**
 * The Class FieldOfView.
 */
public class FieldOfView extends Node {

	/** The Constant type. */
	public static final String type = "FieldOfView";
	
	/** The cell roi path. */
	private String cellRoiPath;
	
	/**
	 * Instantiates a new field of view.
	 *
	 * @param parent the parent
	 */
	public FieldOfView(Sample parent) {
		super(parent, type);
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
	 * Gets the experiment type.
	 *
	 * @return the experiment type
	 */
	public String getExperimentType(){
		return this.getParent().getParent().getProperty("type");
	}

	/**
	 * Gets the images.
	 *
	 * @return the images
	 */
	public  ArrayList<FileNode> getImages() {
		ArrayList<Node> nodes = this.getChildren(new NodeFilter(FieldOfView.type));
		
		//convert to sample array
		ArrayList<FileNode> filesNodes = new ArrayList<FileNode>();
		for(Node node : nodes){
			filesNodes.add((FileNode) node);
					}
		return filesNodes;		
		
	}

	/**
	 * Gets the images.
	 *
	 * @param channel the channel
	 * @return the images
	 */
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

	/**
	 * Gets the number of fo v.
	 *
	 * @return the number of fo v
	 */
	@Override
	public int getNumberOfFoV() {
		return 1;
	}
	
	

//	public String getCellularROIs() {
//		if(cellRoiPath!=null){
//			return cellRoiPath;
//		}
//		else {
//			//Try to find it on the disk
//			File temp = new File(this.getPath()+ File.separator + "cellRois.zip");
//			if(temp.exists()){
//				this.cellRoiPath = temp.getAbsolutePath();
//				return cellRoiPath;
//			}
//		}
//		return null;
//	}

	/**
 * Gets the field of view.
 *
 * @return the field of view
 */
@SuppressWarnings("null")
	@Override
	public ArrayList<FieldOfView> getFieldOfView() {
		ArrayList<FieldOfView> fovs = null;
		fovs.add(this);
		return fovs;
	}

	/**
	 * Gets the samples.
	 *
	 * @return the samples
	 */
	@Override
	public ArrayList<Sample> getSamples() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Gets the beam profile.
	 *
	 * @param channel the channel
	 * @return the beam profile
	 */
	public String getBeamProfile(String channel){
		if(this.getProperty(channel+"_BeamProfile")== null){
			return this.getParent().getBeamProfile(channel);
		}
		return this.getProperty(channel+"_BeamProfile");
	}

	/**
	 * Gets the experiment name.
	 *
	 * @return the experiment name
	 */
	public String getExperimentName(){
		return this.getParent().getParent().getName();
	}
	
	/**
	 * Gets the sample name.
	 *
	 * @return the sample name
	 */
	public String getSampleName(){
		return this.getParent().getName();
	}
	
	/**
	 * Gets the field of view name.
	 *
	 * @return the field of view name
	 */
	public String getFieldOfViewName(){
		return this.getName();
	}
	
	
}
