/*
 * 
 */
package filters;

import model.Node;

/**
 * The Class NodeFilter.
 */
public class NodeFilter implements NodeFilterInterface {

	private String type;
	
	public NodeFilter(String type) {
		this.type = type;
	}

	@Override
	public boolean accept(Node node) {
		return node.getType().equalsIgnoreCase(type);
	}
}
