/*
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.FileNode;

// TODO: Auto-generated Javadoc
/**
 * The Class FileDialog.
 */
public class FileDialog extends JDialog implements ActionListener {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The file node. */
	private FileNode fileNode;
	
	/** The ok button. */
	private JButton okButton = new JButton("Ok");
	
	/** The cancel button. */
	private JButton cancelButton = new JButton("Cancel");
	
	/** The path text field. */
	private JTextField pathTextField = new JTextField(20);
	
	/** The choose button. */
	private JButton chooseButton = new JButton("Choose");
	
	/** The type combo box. */
	private JComboBox<String> typeComboBox = new JComboBox<String>(
			new String[] { "Aqcuisition", "Bright Field", "Red Channel", "Green Channel", "Blue Channel" });
	
	/** The canceled. */
	private boolean canceled = false;
	
	/**
	 * Instantiates a new file dialog.
	 *
	 * @param parent the parent
	 * @param file the file
	 */
	public FileDialog(Frame parent, FileNode file) {
		super(parent, "Field of View File", true);
		
		this.fileNode = file;
		pathTextField.setText(file.getProperty("path"));
		typeComboBox.setSelectedItem(file.getProperty("type"));
		
		JPanel centerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;

		centerPanel.add(new JLabel("File"), gbc);

		gbc.gridx++;

		centerPanel.add(pathTextField, gbc);

		gbc.gridx++;

		chooseButton.addActionListener(this);

		centerPanel.add(chooseButton, gbc);

		gbc.gridx = 0;
		gbc.gridy++;

		centerPanel.add(new JLabel("Type"), gbc);

		gbc.gridx++;
		centerPanel.add(typeComboBox, gbc);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		bottomPanel.add(okButton);
		bottomPanel.add(cancelButton);

		setLayout(new BorderLayout());
		add(bottomPanel, BorderLayout.SOUTH);
		add(centerPanel, BorderLayout.CENTER);
		pack();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Action performed.
	 *
	 * @param e the e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == okButton) {
			fileNode.setProperty("name", new File(pathTextField.getText()).getName());
			fileNode.setProperty("path", pathTextField.getText());
			fileNode.setProperty("type", (String)typeComboBox.getSelectedItem());

			dispose();
		} else if (e.getSource() == cancelButton) {
			canceled = true;
			dispose();
		} else if (e.getSource() == chooseButton) {

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				pathTextField.setText(fileChooser.getSelectedFile().getPath());
			
		}

	}
	
	/**
	 * Checks if is canceled.
	 *
	 * @return true, if is canceled
	 */
	public boolean isCanceled() {
		return canceled;
	}
	
	
}
