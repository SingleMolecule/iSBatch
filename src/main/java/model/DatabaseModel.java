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

public class DatabaseModel implements TreeModel {

	private Node root;

	private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

	public iSBatchPreferences preferences = new iSBatchPreferences();

	public DatabaseModel(Node root) {
		this.root = root;
	}

	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	public Node getChild(Object parent, int index) {
		return ((Node) parent).getChildren().get(index);
	}

	public int getChildCount(Object parent) {
		return ((Node) parent).getChildren().size();
	}

	public int getIndexOfChild(Object parent, Object child) {
		return ((Node) parent).getChildren().indexOf(child);
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;

		// notify listeners that the root has been changed
		for (TreeModelListener l : listeners)
			l.treeStructureChanged(new TreeModelEvent(this, getPathToRoot(root)));
	}

	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		for (TreeModelListener l : listeners)
			l.treeNodesChanged(new TreeModelEvent(this, path));
	}

	public void addNode(Node parent, Node child) {
		parent.getChildren().add(child);

		for (TreeModelListener l : listeners) {
			l.treeNodesInserted(new TreeModelEvent(this, getPathToRoot(parent),
					new int[] { getIndexOfChild(parent, child) },
					new Object[] { child }));
		}

	}

	public void removeNode(Node parent, Node child) {
		parent.getChildren().remove(child);

		for (TreeModelListener l : listeners) {
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

	public void nodeChanged(TreeNode lastPathComponent) {
		// TODO Auto-generated method stub
	}

}
