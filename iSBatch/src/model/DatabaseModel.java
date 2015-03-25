package model;

import java.util.ArrayList;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class DatabaseModel implements TreeModel {
	
	private Node root;
	private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
	
	public DatabaseModel(Node root) {
		this.root = root;
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public Node getChild(Object parent, int index) {
		return ((Node)parent).getChildren().get(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return ((Node)parent).getChildren().size();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((Node)parent).getChildren().indexOf(child);
	}

	@Override
	public Node getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		for (TreeModelListener l: listeners)
			l.treeNodesChanged(new TreeModelEvent(this, path));
	}
	
	public void addNode(Node parent, Node child) {
		parent.getChildren().add(child);
		
		for (TreeModelListener l: listeners) {
			l.treeNodesInserted(new TreeModelEvent(this, getPathToRoot(parent),
					new int[] { getIndexOfChild(parent, child) },
					new Object[] { child }));
		}
		
	}
	
	public void removeNode(Node parent, Node child) {
		parent.getChildren().add(child);
		
		for (TreeModelListener l: listeners) {
			l.treeNodesRemoved(new TreeModelEvent(this, getPathToRoot(parent),
					new int[] { getIndexOfChild(parent, child) },
					new Object[] { child }));
		}
		
	}
	
	public Object[] getPathToRoot(Node aNode) {
		ArrayList<Object> nodes = new ArrayList<Object>();
		
		for (Node node = aNode; node != null; node = node.getParent())
			nodes.add(0, node);
		
		return nodes.toArray();
	}

}
