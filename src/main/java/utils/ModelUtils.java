package utils;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import filters.NodeFilterInterface;
import model.FileNode;
import model.Node;

public abstract class ModelUtils {

	
	
	HashSet<String> channels = new HashSet<String>();

	public static ComboBoxModel<String> getUniques(ArrayList<Node> fileNodes, String propertyName) {
		String[] uniques = null;
		
		HashSet<String> properties = new HashSet<String>();

		for (Node n: fileNodes) {
		FileNode fn = (FileNode)n;
		properties.add(fn.getProperty(propertyName));
		
	}
		uniques =  properties.toArray(new String[properties.size()]);
		return new DefaultComboBoxModel<String>(uniques);
	}
	
	public static ComboBoxModel<String> getUniqueChannels(ArrayList<Node> fileNodes) {
		return getUniques(fileNodes, "channel");
	}
	
	public static ComboBoxModel<String> getUniquesExtension(ArrayList<Node> fileNodes) {
		return getUniques(fileNodes, "extension");
	}
	public static ComboBoxModel<String> getUniqueTags(ArrayList<Node> fileNodes) {
	String[] uniques = null;
		
		HashSet<String> tags = new HashSet<String>();

		for (Node n: fileNodes) {
		FileNode fn = (FileNode)n;
		tags.addAll(fn.getTags());
		
		}
		uniques =  tags.toArray(new String[tags.size()]);
		return new DefaultComboBoxModel<String>(uniques);
	}
	
//	public static void main(String[] args) {
//		HashSet<String> tags = new HashSet<String>();
//	}

	public static ComboBoxModel<String> getUniqueChannels(Node node) {
		ArrayList<Node> fileNodes = getAllFileNodes(node);
		return getUniqueChannels(fileNodes);
	}
	
	
	private static ArrayList<Node> getAllFileNodes(Node node){
		ArrayList<Node> fileNodes = node.getDescendents(new NodeFilterInterface() {
			@Override
			public boolean accept(Node node) {
				return node.getType().equalsIgnoreCase(FileNode.type);
			}
		});
		return fileNodes;
	}

	//Applying Clean code strategies. 
	public static ComboBoxModel<String> getUniqueChannelsFromDatabase(Node node) {
		//This function getUniqueChannels will be deprecated in the next iteration.
		return getUniqueChannels(node);
	}
	
	
	
}
