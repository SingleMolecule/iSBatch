/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package model.cell;

/*
 * A 1.4 example that uses the following files:
 *    GenealogyModel.java
 *    Person.java
 *
 * Based on an example provided by tutorial reader Olivier Berlanger.
 */
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GenealogyExample extends JPanel 
                              implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	GenealogyTree tree;
    private static String SHOW_ANCESTOR_CMD = "showAncestor";

    public GenealogyExample() {
        super(new BorderLayout());
        
        //Construct the panel with the toggle buttons.
        JRadioButton showDescendant = 
                new JRadioButton("Show descendants", true);
        final JRadioButton showAncestor = 
                new JRadioButton("Show ancestors");
        ButtonGroup bGroup = new ButtonGroup();
        bGroup.add(showDescendant);
        bGroup.add(showAncestor);
        showDescendant.addActionListener(this);
        showAncestor.addActionListener(this);
        showAncestor.setActionCommand(SHOW_ANCESTOR_CMD);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(showDescendant);
        buttonPanel.add(showAncestor);

        //Construct the tree.
        tree = new GenealogyTree(getGenealogyGraph());
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setPreferredSize(new Dimension(200, 200));

        //Add everything to this panel.
        add(buttonPanel, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
    }

    /** 
     * Required by the ActionListener interface.
     * Handle events on the showDescendant and
     * showAncestore buttons. 
     */
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand() == SHOW_ANCESTOR_CMD) {
            tree.showAncestor(true);
        } else {
            tree.showAncestor(false);
        }
    }
    
    /**
     *  Constructs the genealogy graph used by the model.
     */
    public Cell getGenealogyGraph() {
        //the greatgrandparent generation
        Cell a1 = new Cell("Jack (great-granddaddy)");
        Cell a3 = new Cell("Albert (great-granddaddy)");
        Cell a5 = new Cell("Paul (great-granddaddy)");

        //the grandparent generation
        Cell b1 = new Cell("Peter (grandpa)");
        Cell b2 = new Cell("Zoe (grandma)");
        Cell b3 = new Cell("Simon (grandpa)");
        Cell b4 = new Cell("James (grandpa)");
        Cell b5 = new Cell("Bertha (grandma)");
        Cell b6 = new Cell("Veronica (grandma)");
        Cell b7 = new Cell("Anne (grandma)");
        Cell b8 = new Cell("Renee (grandma)");
        Cell b9 = new Cell("Joseph (grandpa)");

        //the parent generation
        Cell c1 = new Cell("Isabelle (mom)");
        Cell c2 = new Cell("Frank (dad)");
        Cell c3 = new Cell("Louis (dad)");
        Cell c4 = new Cell("Laurence (dad)");
        Cell c5 = new Cell("Valerie (mom)");
        Cell c6 = new Cell("Marie (mom)");
        Cell c7 = new Cell("Helen (mom)");
        Cell c8 = new Cell("Mark (dad)");
        Cell c9 = new Cell("Oliver (dad)");

        //the youngest generation
        Cell d1 = new Cell("Clement (boy)");
        Cell d2 = new Cell("Colin (boy)");

        Cell.linkCells(a1,new Cell[] {b1,b2,b3,b4});
        Cell.linkCells(a3,new Cell[] {b5,b6,b7});
        Cell.linkCells(a5,new Cell[] {b8,b9});
        Cell.linkCells(b3,new Cell[] {c1,c2,c3});
        Cell.linkCells(b4,new Cell[] {c4,c5,c6});
        Cell.linkCells(b8,new Cell[] {c7,c8,c9});
        Cell.linkCells(c4,new Cell[] {d1,d2});

        return a1;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("GenealogyExample");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        GenealogyExample newContentPane = new GenealogyExample();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
