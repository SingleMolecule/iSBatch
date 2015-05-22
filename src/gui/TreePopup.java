package gui;

import ij.plugin.BatchProcessor;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;

public class TreePopup {
    
    JTree tree = new JTree();
    JPopupMenu popup;
 
    public TreePopup(JTree tree) {
    	this.tree = tree;
        popup = new JPopupMenu();
        popup.setInvoker(tree);
        PopupHandler handler = new PopupHandler(tree, popup);
        popup.add(getMenuItem("add child",   handler));
        popup.add(getMenuItem("add sibling", handler));
    }
 
    public TreePopup() {
        popup = new JPopupMenu();
        popup.setInvoker(tree);
        PopupHandler handler = new PopupHandler(tree, popup);
        popup.add(getMenuItem("add child",   handler));
        popup.add(getMenuItem("add sibling", handler));
        popup.add(getMenuItem("Run macro...",  handler));
        
    }
 
    private JMenuItem getMenuItem(String s, ActionListener al) {
        JMenuItem menuItem = new JMenuItem(s);
        menuItem.setActionCommand(s.toUpperCase());
        System.out.println(s.toUpperCase());
        menuItem.addActionListener(al);
        return menuItem;
    }
    private JScrollPane getTreeComponent() {
        tree.add(popup);
        expand(new TreePath(tree.getModel().getRoot()));
        return new JScrollPane(tree);
    }
 
    private void expand(TreePath path) {
        TreeNode node = (TreeNode)path.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            java.util.Enumeration e = node.children();
            while(e.hasMoreElements()) {
                TreeNode n = (TreeNode)e.nextElement();
                expand(path.pathByAddingChild(n));
            }
        }
        tree.expandPath(path);
    }
 
    public static void main(String[] args) {
    	System.out.println("Start");
        TreePopup test = new TreePopup();
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(test.getTreeComponent());
        f.setSize(400,400);
        f.setLocation(200,200);
        f.setVisible(true);
    }
}
 
class PopupHandler implements ActionListener {
    JTree tree;
    JPopupMenu popup;
    Point loc;
 
    public PopupHandler(JTree tree, JPopupMenu popup) {
        this.tree = tree;
        this.popup = popup;
        tree.addMouseListener(ma);
    }
 
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        TreePath path  = tree.getPathForLocation(loc.x, loc.y);
        //System.out.println("path = " + path);
        //System.out.printf("loc = [%d, %d]%n", loc.x, loc.y);
        if(ac.equals("ADD CHILD"))
            addChild(path);
        if(ac.equals("ADD SIBLING"))
            addSibling(path);
        if(ac.equals("RUN MACRO..."))
        	runMacroPanel();

    }
 
    private void runMacroPanel() {
		BatchProcessor batch = new BatchProcessor();
		batch.run("");
		
	}

	private void addChild(TreePath path) {
        DefaultMutableTreeNode parent =
            (DefaultMutableTreeNode)path.getLastPathComponent();
        int count = parent.getChildCount();
        DefaultMutableTreeNode child =
            new DefaultMutableTreeNode("child " + count);
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        model.insertNodeInto(child, parent, count);
    }
 
    private void addSibling(TreePath path) {
        DefaultMutableTreeNode node =
            (DefaultMutableTreeNode)path.getLastPathComponent();
        DefaultMutableTreeNode parent =
            (DefaultMutableTreeNode)node.getParent();
        int count = parent.getChildCount();
        DefaultMutableTreeNode child =
            new DefaultMutableTreeNode("child " + count);
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        model.insertNodeInto(child, parent, count);
    }
 
    private MouseListener ma = new MouseAdapter() {
        private void checkForPopup(MouseEvent e) {
            if(e.isPopupTrigger()) {
                loc = e.getPoint();
                popup.show(tree, loc.x, loc.y);
            }
        }
 
        public void mousePressed(MouseEvent e)  { checkForPopup(e); }
        public void mouseReleased(MouseEvent e) { checkForPopup(e); }
        public void mouseClicked(MouseEvent e)  { checkForPopup(e); }
    };
}