package gui;


import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import model.Node;
import model.parameters.NodeType;

public class DatabaseTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean isLeaf, int row, boolean focused) {
		
		Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
		
		Node node = (Node)value;
		
		if (node.getType().equalsIgnoreCase(NodeType.ROOT.toString())) {
			setIcon(new ImageIcon(DatabaseTreeCellRenderer.class.getResource("/gui/DatabaseTreeCellRenderer/treeIcons/Root.png")));
		} else if (node.getType().equalsIgnoreCase(NodeType.EXPERIMENT.toString())) {
			setIcon(new ImageIcon(DatabaseTreeCellRenderer.class.getResource("/gui/DatabaseTreeCellRenderer/treeIcons/Experiment.png")));
		} else if (node.getType().equalsIgnoreCase(NodeType.SAMPLE.toString())) {
			setIcon(new ImageIcon(DatabaseTreeCellRenderer.class.getResource("/gui/DatabaseTreeCellRenderer/treeIcons/Sample.png")));
		} else if (node.getType().equalsIgnoreCase(NodeType.FOV.toString())) {
			setIcon(new ImageIcon(DatabaseTreeCellRenderer.class.getResource("/gui/DatabaseTreeCellRenderer/treeIcons/FieldOfView.png")));
		} else if (node.getType().equalsIgnoreCase(NodeType.FILE.toString())) {
			setIcon(new ImageIcon(DatabaseTreeCellRenderer.class.getResource("/gui/DatabaseTreeCellRenderer/treeIcons/File.png")));

			// TODO color text
			// setForeground(color);
		}
		
		return c;
	}
	

}
