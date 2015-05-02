/*
 * 
 */
package model;

import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.EventListenerList;


// TODO: Auto-generated Javadoc
/** Support for generic dynamically changing TreeModels.
*/

public abstract class AbstractTreeModel
    implements TreeModel
{
    
    /** The listeners. */
    protected EventListenerList listeners;
    

    /**
     * Instantiates a new abstract tree model.
     */
    protected AbstractTreeModel()
    {
        listeners = new EventListenerList();
    }


    /**
     * Gets the index of child.
     *
     * @param parent the parent
     * @param child the child
     * @return the index of child
     */
    public int getIndexOfChild(Object parent, Object child)
    {
        for (int count = getChildCount(parent), i = 0; i < count; i++)
            if (getChild(parent, i).equals(child))
                return i;

        return -1;
    }


    /** Call when there is a new root, which may be null, i.e. not existent. */
    protected void fireNewRoot()
    {
        Object[] pairs = listeners.getListenerList();

        Object root = getRoot();

        /* Undocumented. I think it is the only reasonable/possible solution
           to use use null as path if there is no root. TreeModels without
           root aren't important anyway, since JTree doesn't support them (yet).
        */
        TreePath path = (root != null) ? new TreePath(root) : null;
        
        TreeModelEvent e = null;
        
        for (int i = pairs.length - 2; i >= 0; i -= 2)
        {
            if (pairs[i] == TreeModelListener.class)
            {
                if (e == null)
                    e = new TreeModelEvent(this, path, null, null);
                
                ((TreeModelListener)pairs[i + 1]).treeStructureChanged(e);
            }
        }
    }
    
    /** Call when everything but the root has changed. Only may be called
        when the root is not null. Otherwise there isn't a structure to have
        changed.
    */
    protected void fireStructureChanged()
    {
        fireTreeStructureChanged(new TreePath(getRoot()));
    }
    
    /**
     *  Call when a node has changed its leaf state.
     *
     * @param path the path
     */
    protected void firePathLeafStateChanged(TreePath path)
    {
        fireTreeStructureChanged(path);
    }
    
    /**
     *  Call when the tree structure below the path has completely changed.
     *
     * @param parentPath the parent path
     */
    protected void fireTreeStructureChanged(TreePath parentPath)
    {
        Object[] pairs = listeners.getListenerList();
        
        TreeModelEvent e = null;
        
        for (int i = pairs.length - 2; i >= 0; i -= 2)
        {
            if (pairs[i] == TreeModelListener.class)
            {
                if (e == null)
                    e = new TreeModelEvent(this, parentPath, null, null);
                
                ((TreeModelListener)pairs[i + 1]).treeStructureChanged(e);
            }
        }
     }
    
    /**
     *  Call when the path itself has changed, but no structure changes
     *         have occurred.
     *
     * @param path the path
     */
    protected void firePathChanged(TreePath path)
    {
        Object node = path.getLastPathComponent();
        TreePath parentPath = path.getParentPath();
        
        if (parentPath == null)
            fireChildrenChanged(path, null, null);
        else
        {
            Object parent = parentPath.getLastPathComponent();
            
            fireChildChanged(parentPath, getIndexOfChild(parent, node), node);
        }
    }
    
    /**
     * Fire child added.
     *
     * @param parentPath the parent path
     * @param index the index
     * @param child the child
     */
    protected void fireChildAdded(TreePath parentPath, int index, Object child)
    {
        fireChildrenAdded(parentPath, new int[] {index}, new Object[] {child});
    }
    
    /**
     * Fire child changed.
     *
     * @param parentPath the parent path
     * @param index the index
     * @param child the child
     */
    protected void fireChildChanged(TreePath parentPath, int index, Object child)
    {
        fireChildrenChanged(parentPath, new int[] {index}, new Object[] {child});
    }
    
    /**
     * Fire child removed.
     *
     * @param parentPath the parent path
     * @param index the index
     * @param child the child
     */
    protected void fireChildRemoved(TreePath parentPath, int index, Object child)
    {
        fireChildrenRemoved(parentPath, new int[] {index}, new Object[] {child});
    }
    
    
    /**
     * Fire children added.
     *
     * @param parentPath the parent path
     * @param indices the indices
     * @param children the children
     */
    protected void fireChildrenAdded(TreePath parentPath, int[] indices, Object[] children)
    {
        Object[] pairs = listeners.getListenerList();
        
        TreeModelEvent e = null;
        
        for (int i = pairs.length - 2; i >= 0; i -= 2)
        {
            if (pairs[i] == TreeModelListener.class)
            {
                if (e == null)
                    e = new TreeModelEvent(this, parentPath, indices, children);
                
                ((TreeModelListener)pairs[i + 1]).treeNodesInserted(e);
            }
        }
    }

    /**
     * Fire children changed.
     *
     * @param parentPath the parent path
     * @param indices the indices
     * @param children the children
     */
    protected void fireChildrenChanged(TreePath parentPath, int[] indices, Object[] children)
    {
        Object[] pairs = listeners.getListenerList();

        TreeModelEvent e = null;

        for (int i = pairs.length - 2; i >= 0; i -= 2)
        {
            if (pairs[i] == TreeModelListener.class)
            {
                if (e == null)
                    e = new TreeModelEvent(this, parentPath, indices, children);
                
                ((TreeModelListener)pairs[i + 1]).treeNodesChanged(e);
            }
        }
    }

    /**
     * Fire children removed.
     *
     * @param parentPath the parent path
     * @param indices the indices
     * @param children the children
     */
    protected void fireChildrenRemoved(TreePath parentPath, int[] indices, Object[] children)
    {
        Object[] pairs = listeners.getListenerList();

        TreeModelEvent e = null;

        for (int i = pairs.length - 2; i >= 0; i -= 2)
        {
            if (pairs[i] == TreeModelListener.class)
            {
                if (e == null)
                    e = new TreeModelEvent(this, parentPath, indices, children);
                ((TreeModelListener)pairs[i + 1]).treeNodesRemoved(e);
            }
        }
    }


    /**
     * Clone.
     *
     * @return the object
     * @throws CloneNotSupportedException the clone not supported exception
     */
    protected Object clone()
        throws CloneNotSupportedException
    {
        AbstractTreeModel clone = (AbstractTreeModel)super.clone();

        clone.listeners = new EventListenerList();

        return clone;
    }
        


    /**
     * Adds the tree model listener.
     *
     * @param l the l
     */
    public void addTreeModelListener(TreeModelListener l)
    {
        listeners.add(TreeModelListener.class, l);
    }

    /**
     * Removes the tree model listener.
     *
     * @param l the l
     */
    public void removeTreeModelListener(TreeModelListener l)
    {
        listeners.remove(TreeModelListener.class, l);
    }
}