package model;

import java.util.ArrayList;
import java.util.Map.Entry;

public class MacroModel {

	public static Node root;

	public static String getType(String hash) {
		Node node = findNode(root, Integer.parseInt(hash));
		
		if (node == null)
			return "";
		
		return node.getType();
	}
	
	public static String getProperties(String hash) {
		
		String str = "";
		Node node = findNode(root, Integer.parseInt(hash));

		if (node == null) return "";
		
		for (Entry<String, String> entry : node.getProperties().entrySet()) {

			String key = entry.getKey();
			String value = entry.getValue();

			key = key.replaceAll("\n", " ");
			value = value.replaceAll("\n", " ");

			str += entry.getKey() + "=" + entry.getValue() + "\n";
		}

		return str;
	}

	public static String getChildren(String hash) {
		String str = "";

		Node node = findNode(root, Integer.parseInt(hash));

		if (node != null) {
			
			ArrayList<Node> children = node.getChildren();
			
			if (children != null) {
				for (Node child : children)
					str += Integer.toString(child.hashCode()) + "\n";
			}
		}

		return str;
	}
	
	public static String getParent(String hash) {
		Node node = findNode(root, Integer.parseInt(hash));
		return Integer.toString(node.getParent() == null ? root.hashCode() : node.getParent().hashCode());
	}

	public static Node findNode(Node node, int hashCode) {
		
		if (node.hashCode() == hashCode)
			return node;
		else {
			for (Node child : node.getChildren()) {
				Node n = findNode(child, hashCode);
				
				if (n != null)
					return n;
			}
		}

		return null;
	}
	
}
