package test;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TestDialog implements ActionListener {

	private JButton cancelButton = new JButton("Cancel");
	private JButton loadButton = new JButton("Load");
	private JButton createButton = new JButton("Create");
	
	public void showDialog() {
		
		JDialog dialog = new JDialog();
		dialog.setTitle("Test Dialog");
		dialog.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		// create first row
		
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		dialog.add(new JLabel("Channel"), gbc);
		
		String[] channels = new String[] { "Red", "Green", "Blue" };
		JComboBox<String> channelComboBox = new JComboBox<String>(channels);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		
		dialog.add(channelComboBox, gbc);
		
		// create second row
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		
		dialog.add(new JLabel("Method"), gbc);
		
		String[] methods = new String[] { "Average Images", "Flatten Images" };
		JComboBox<String> methodComboBox = new JComboBox<String>(methods);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		
		dialog.add(methodComboBox, gbc);
		
		// third row
		
		// buttons show be next to each other so we can use a JPanel with the standard flow layout
		
		JPanel buttonPanel = new JPanel();	// by default a JPanel has the flow layout
		
		cancelButton.addActionListener(this);
		loadButton.addActionListener(this);
		createButton.addActionListener(this);
		
		buttonPanel.add(cancelButton);
		buttonPanel.add(loadButton);
		buttonPanel.add(createButton);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;	// two columns wide

		dialog.add(buttonPanel, gbc);
		
		dialog.pack();	// make sure the dialog is big enough so that all components are visible
		dialog.setVisible(true);
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == cancelButton) {
			JOptionPane.showMessageDialog(null, "Cancel");
		}
		else if (e.getSource() == loadButton) {
			JOptionPane.showMessageDialog(null, "Load");
		}
		else if (e.getSource() == createButton) {
			JOptionPane.showMessageDialog(null, "Create");
		}
		
	}
	
	
	public static void main(String[] args) {
		
		new TestDialog().showDialog();

	}

}
