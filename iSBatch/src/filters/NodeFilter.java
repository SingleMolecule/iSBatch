/*
 * 
 */
package filters;

import model.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class NodeFilter.
 */
public class NodeFilter implements NodeFilterInterface {

	/** The type. */
	private String type;
	
	/**
	 * Instantiates a new node filter.
	 *
	 * @param type the type
	 */
	public NodeFilter(String type) {
		this.type = type;
	}

	/**
	 * Accept.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	@Override
	public boolean accept(Node node) {
		return node.getType().equalsIgnoreCase(type);
	}
}
