/*
 * 
 */
package gui;

import iSBatch.iSBatchPreferences;

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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Sample;

// TODO: Auto-generated Javadoc
/**
 * The Class SampleDialog.
 */
public class SampleDialog extends JDialog implements ActionListener {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The sample. */
	private Sample sample;
	
	/** The ok button. */
	private JButton okButton = new JButton("Ok");
	
	/** The cancel button. */
	private JButton cancelButton = new JButton("Cancel");
	
	/** The name text field. */
	private JTextField nameTextField = new JTextField(20);
	
	/** The folder text field. */
	private JTextField folderTextField = new JTextField(20);
	
	/** The choose button. */
	private JButton chooseButton = new JButton("Choose");
	
	/** The canceled. */
	private boolean canceled = false;
	
	/**
	 * Instantiates a new sample dialog.
	 *
	 * @param parent the parent
	 * @param sample the sample
	 */
	public SampleDialog(Frame parent, Sample sample) {
		super(parent, "Sample", true);

		this.sample = sample;
		nameTextField.setText(sample.getProperty("name"));
		folderTextField.setText(sample.getProperty("folder"));

		JPanel centerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;

		centerPanel.add(new JLabel("Name"), gbc);

		gbc.gridx++;

		centerPanel.add(nameTextField, gbc);

		gbc.gridx = 0;
		gbc.gridy++;

		centerPanel.add(new JLabel("Folder"), gbc);

		gbc.gridx++;

		centerPanel.add(folderTextField, gbc);

		gbc.gridx++;

		chooseButton.addActionListener(this);

		centerPanel.add(chooseButton, gbc);

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
			sample.setProperty("name", nameTextField.getText());
			sample.setProperty("folder", folderTextField.getText());

			dispose();
		} else if (e.getSource() == cancelButton) {
			canceled = true;
			dispose();
		} else if (e.getSource() == chooseButton) {

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setCurrentDirectory(new File(iSBatchPreferences.lastSelectedPath));
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				folderTextField
						.setText(fileChooser.getSelectedFile().getPath());

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
