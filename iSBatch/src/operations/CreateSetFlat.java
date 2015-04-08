package operations;

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

import test.TestDialog;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.Root;
import model.Sample;

public class CreateSetFlat implements Operation, ActionListener {
	private JButton cancelButton = new JButton("Cancel");
	private JButton loadButton = new JButton("Load");
	private JButton createButton = new JButton("Create");
	private String channel, method;
	private JComboBox<String> channelComboBox;
	private JComboBox<String> methodComboBox;
	private String[] methods = new String[] { "Average Images", "Flatten Images" };
	private String[] channels = new String[] { "Red", "Green", "Blue" };
	public CreateSetFlat(DatabaseModel treeModel) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String[] getContext() {
		return new String[]{"All"};	
	}

	@Override
	public String getName() {
		return "Create/Set Bg";
	}

	@Override
	public boolean setup(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void finalize(Node node) {
		// TODO Auto-generated method stub

	}


	@Override
	public void visit(Root root) {
		showDialog();
	}

	@Override
	public void visit(Experiment experiment) {
		showDialog();
	}

	@Override
	public void visit(Sample sample) {
		showDialog();
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		showDialog();
	}

	
	@Override
	public void visit(FileNode fileNode) {
		showDialog();
	}

	public void showDialog() {
		
		JDialog dialog = new JDialog();
		dialog.setTitle("Test Dialog");
		dialog.getContentPane().setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		// create first row
		
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		dialog.getContentPane().add(new JLabel("Channel"), gbc);
		
		
		channelComboBox = new JComboBox<String>(channels);
		channelComboBox.addActionListener (this);
		
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		
		dialog.getContentPane().add(channelComboBox, gbc);
		
		// create second row
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		
		dialog.getContentPane().add(new JLabel("Method"), gbc);
		
		
		methodComboBox = new JComboBox<String>(methods);
		methodComboBox.addActionListener (this);

		gbc.gridx = 1;
		gbc.gridy = 1;
		
		dialog.getContentPane().add(methodComboBox, gbc);
		
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

		dialog.getContentPane().add(buttonPanel, gbc);
		
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
		else if (e.getSource() == channelComboBox) {
			channel = channels[channelComboBox.getSelectedIndex()];
	    	System.out.println(channel);
		}
		else if (e.getSource() == methodComboBox){
			method = methods[methodComboBox.getSelectedIndex()];
	    	System.out.println(method);
		}
		
	}
	
	
	public static void main(String[] args) {
		
		TestDialog gui = new TestDialog();
		gui.showDialog();
		System.out.println(gui.getChannel() + gui.getMethod());
	}

}
