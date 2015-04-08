/**
 * 
 */
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
import javax.swing.JTextField;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.Root;
import model.Sample;

/**
 * @author VictorCaldas
 *
 */
public class SetBackGround implements Operation,  ActionListener {
	private JButton cancelButton = new JButton("Cancel");
	private JButton loadButton = new JButton("Set");
	private JButton createButton = new JButton("Proceed");
	private JComboBox<String> channelComboBox;
	private JComboBox<String> methodComboBox;
	private JComboBox<String> imageTypeComboBox;
	private String[] methods = new String[] { "Average Images", "Load Image" };
	private String[] channels = new String[] {
			"acquisition",
			"bf",
			"red",
			"green",
			"blue",
	};
	
	private String[] types = new String[] {"Raw"};
	private String channel = channels[0];
	private String method = methods[0];
	
	private String imageType = types[0];
	private JTextField filePathField;
	private String filePath;
	public SetBackGround(DatabaseModel treeModel) {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see context.ContextElement#getContext()
	 */
	@Override
	public String[] getContext() {
		return new String[]{"All"};	
	}

	/* (non-Javadoc)
	 * @see operations.Operation#getName()
	 */
	@Override
	public String getName() {
		return "Set BackGround";
	}

	/* (non-Javadoc)
	 * @see operations.Operation#setup(model.Node)
	 */
	@Override
	public boolean setup(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see operations.Operation#finalize(model.Node)
	 */
	@Override
	public void finalize(Node node) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.Root)
	 */

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
		dialog.setTitle("Set BackGround");
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
		
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		dialog.getContentPane().add(new JLabel("ImageType"), gbc);
		
		
		imageTypeComboBox = new JComboBox<String>(types);
		imageTypeComboBox.setEditable(true);
		imageTypeComboBox.addActionListener (this);
		
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		
		dialog.getContentPane().add(imageTypeComboBox, gbc);
		
		
		
		
		
		
		// create third row
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		
		dialog.getContentPane().add(new JLabel("Path"), gbc);
		
		
		filePathField = new JTextField();
		filePathField.addActionListener(this);
		

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		
		dialog.getContentPane().add(filePathField, gbc);
		
		// fourth row
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		
		dialog.getContentPane().add(new JLabel("Method"), gbc);
		
		
		methodComboBox = new JComboBox<String>(methods);
		
		methodComboBox.addActionListener (this);

		gbc.gridx = 1;
		gbc.gridy = 2;
		
		dialog.getContentPane().add(methodComboBox, gbc);
		
		
		// buttons show be next to each other so we can use a JPanel with the standard flow layout
		
		JPanel buttonPanel = new JPanel();	// by default a JPanel has the flow layout
		
		cancelButton.addActionListener(this);
		loadButton.addActionListener(this);
		createButton.addActionListener(this);
		
		buttonPanel.add(cancelButton);
		buttonPanel.add(createButton);
		buttonPanel.add(loadButton);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
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
		else if (e.getSource() == createButton) {
			System.out.println("Parameters will be: " + channel + " , " + imageType +  " , " +  method + " , " + filePath);
			if(method.equalsIgnoreCase("Load Image")){
				System.out.println("Ill ask the image");
				filePath = "Just a path";
				filePathField.setText(filePath);
			}
			
			if(method.equalsIgnoreCase("Average Images")){
				
				System.out.println("I'll average all images and return the path");
				filePath = "Just a path of averaged Image";
				filePathField.setText(filePath);
				
				
			}
		}
		else if(e.getSource() == loadButton){
			if(filePath == null || filePath.equals("")){
				System.out.println("No file selecting. Choose one or click Cancel");
			}
			else{
				System.out.println("Settin path and exiting.");

			}
		}
		else if (e.getSource() == channelComboBox) {
			channel = channels[channelComboBox.getSelectedIndex()];
	    	System.out.println(channel);
		}
		else if (e.getSource() == methodComboBox){
			method = (String) methodComboBox.getSelectedItem();
		}
		else if(e.getSource()== imageTypeComboBox){
			imageType = (String) imageTypeComboBox.getSelectedItem();
			
		}
		else if( e.getSource() == filePathField){
			filePath = filePathField.getText();
		}
		
	}
	
	
	public static void main(String[] args) {
		
		SetBackGround gui = new SetBackGround(null);
		gui.showDialog();
		System.out.println(gui.channel + gui.method);
	}

}
