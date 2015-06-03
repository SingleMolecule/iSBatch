/************************************************************************
 * 				iSBatch  Copyright (C) 2015  							*
 *		Victor E. A. Caldas -  v.e.a.caldas at rug.nl					*
 *		C. Michiel Punter - c.m.punter at rug.nl						*
 *																		*
 *	This program is distributed in the hope that it will be useful,		*
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of		*
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the		*
 *	GNU General Public License for more details.						*
 *	You should have received a copy of the GNU General Public License	*
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************/
package model;

import iSBatch.iSBatchPreferences;

import java.util.ArrayList;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

// TODO: Auto-generated Javadoc
/**
 * The Class DatabaseModel.
 */
public class DatabaseModel implements TreeModel {

	/** The root. */
	private Node root;
	
	/** The listeners. */
	private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
	
	/** The preferences. */
	public iSBatchPreferences preferences = new iSBatchPreferences();

	/**
	 * Instantiates a new database model.
	 *
	 * @param root the root
	 */
	public DatabaseModel(Node root) {
		this.root = root;
	}

	/**
	 * Adds the tree model listener.
	 *
	 * @param l the l
	 */
	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	/**
	 * Gets the child.
	 *
	 * @param parent the parent
	 * @param index the index
	 * @return the child
	 */
	@Override
	public Node getChild(Object parent, int index) {
		return ((Node) parent).getChildren().get(index);
	}

	/**
	 * Gets the child count.
	 *
	 * @param parent the parent
	 * @return the child count
	 */
	@Override
	public int getChildCount(Object parent) {
		return ((Node) parent).getChildren().size();
	}

	/**
	 * Gets the index of child.
	 *
	 * @param parent the parent
	 * @param child the child
	 * @return the index of child
	 */
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((Node) parent).getChildren().indexOf(child);
	}

	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	@Override
	public Node getRoot() {
		return root;
	}

	/**
	 * Sets the root.
	 *
	 * @param root the new root
	 */
	public void setRoot(Node root) {
		this.root = root;
		
		// notify listeners that the root has been changed
		for (TreeModelListener l : listeners)
			l.treeStructureChanged(new TreeModelEvent(this, getPathToRoot(root)));
	}

	/**
	 * Checks if is leaf.
	 *
	 * @param node the node
	 * @return true, if is leaf
	 */
	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	/**
	 * Removes the tree model listener.
	 *
	 * @param l the l
	 */
	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	/**
	 * Value for path changed.
	 *
	 * @param path the path
	 * @param newValue the new value
	 */
	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		for (TreeModelListener l : listeners)
			l.treeNodesChanged(new TreeModelEvent(this, path));
	}

	/**
	 * Adds the node.
	 *
	 * @param parent the parent
	 * @param child the child
	 */
	public void addNode(Node parent, Node child) {
		parent.getChildren().add(child);

		for (TreeModelListener l : listeners) {
			l.treeNodesInserted(new TreeModelEvent(this, getPathToRoot(parent),
					new int[] { getIndexOfChild(parent, child) },
					new Object[] { child }));
		}

	}

	/**
	 * Removes the node.
	 *
	 * @param parent the parent
	 * @param child the child
	 */
	public void removeNode(Node parent, Node child) {
		parent.getChildren().remove(child);

		for (TreeModelListener l : listeners) {
			l.treeNodesRemoved(new TreeModelEvent(this, getPathToRoot(parent),
					new int[] { getIndexOfChild(parent, child) },
					new Object[] { child }));
		}

	}
	
	/**
	 * Gets the path to root.
	 *
	 * @param aNode the a node
	 * @return the path to root
	 */
	public Object[] getPathToRoot(Node aNode) {
		ArrayList<Object> nodes = new ArrayList<Object>();

		for (Node node = aNode; node != null; node = node.getParent())
			nodes.add(0, node);

		return nodes.toArray();
	}

	/**
	 * Node changed.
	 *
	 * @param lastPathComponent the last path component
	 */
	public void nodeChanged(TreeNode lastPathComponent) {
		// TODO Auto-generated method stub
	}
	
	

}
