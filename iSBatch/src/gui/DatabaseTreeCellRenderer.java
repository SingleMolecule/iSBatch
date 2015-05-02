/*
 * 
 */
package gui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.Root;
import model.Sample;

// TODO: Auto-generated Javadoc
/**
 * The Class DatabaseTreeCellRenderer.
 */
public class DatabaseTreeCellRenderer extends DefaultTreeCellRenderer {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Gets the tree cell renderer component.
	 *
	 * @param tree the tree
	 * @param value the value
	 * @param selected the selected
	 * @param expanded the expanded
	 * @param isLeaf the is leaf
	 * @param row the row
	 * @param focused the focused
	 * @return the tree cell renderer component
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean isLeaf, int row, boolean focused) {
		
		Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
		
		Node node = (Node)value;
		
		if (node.getType() == Root.type) {
			setIcon(new ImageIcon(getClass().getResource("/icons/Root.png")));
		} else if (node.getType() == Experiment.type) {
			setIcon(new ImageIcon(getClass().getResource("/icons/Experiment.png")));
		} else if (node.getType() == Sample.type) {
			setIcon(new ImageIcon(getClass().getResource("/icons/Sample.png")));
		} else if (node.getType() == FieldOfView.type) {
			setIcon(new ImageIcon(getClass().getResource("/icons/FieldOfView.png")));
		} else if (node.getType() == FileNode.type) {
			setIcon(new ImageIcon(getClass().getResource("/icons/File.png")));

			// TODO color text
			// setForeground(color);
		}
		
		return c;
	}
	

}
