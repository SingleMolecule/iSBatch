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
		
		if (node.getType() == NodeType.ROOT.toString()) {
			setIcon(new ImageIcon(getClass().getResource("/icons/Root.png")));
		} else if (node.getType() == NodeType.EXPERIMENT.toString()) {
			setIcon(new ImageIcon(getClass().getResource("/icons/Experiment.png")));
		} else if (node.getType() == NodeType.SAMPLE.toString()) {
			setIcon(new ImageIcon(getClass().getResource("/icons/Sample.png")));
		} else if (node.getType() == NodeType.FOV.toString()) {
			setIcon(new ImageIcon(getClass().getResource("/icons/FieldOfView.png")));
		} else if (node.getType() == NodeType.FILE.toString()) {
			setIcon(new ImageIcon(getClass().getResource("/icons/File.png")));

			// TODO color text
			// setForeground(color);
		}
		
		return c;
	}
	

}
