/*
 * 
 */
package test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.print.attribute.TextSyntax;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// TODO: Auto-generated Javadoc
/**
 * The Class EditComboBox.
 */
public class EditComboBox {
  
  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String args[]) {
    String labels[] = { "A", "B", "C", "D","E", "F", "G", "H","I", "J" };
    JFrame frame = new JFrame("Editable JComboBox");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container contentPane = frame.getContentPane();

    final JComboBox comboBox = new JComboBox(labels);
    comboBox.setMaximumRowCount(5);
    comboBox.setEditable(true);
    contentPane.add(comboBox, BorderLayout.NORTH);

    final JTextArea textArea = new JTextArea();
    JScrollPane scrollPane = new JScrollPane(textArea);
    contentPane.add(scrollPane, BorderLayout.CENTER);

    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        textArea.append("Selected: " + comboBox.getSelectedItem());
        textArea.append(", Position: " + comboBox.getSelectedIndex());
        textArea.append(System.getProperty("line.separator"));
        System.out.println(textArea);
      }
    };
    comboBox.addActionListener(actionListener);

    frame.setSize(300, 200);
    frame.setVisible(true);
  }
}

