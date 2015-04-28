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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.FieldOfView;

public class FieldOfViewDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	private FieldOfView fieldOfView;
	private JButton okButton = new JButton("Ok");
	private JButton cancelButton = new JButton("Cancel");
	private JTextField nameTextField = new JTextField(20);
	private JTextField folderTextField = new JTextField(20);
	private JButton chooseButton = new JButton("Choose");
	private boolean canceled = false;
	
	public FieldOfViewDialog(Frame parent, FieldOfView fieldOfView) {
		super(parent, "Field of View", true);

		this.fieldOfView = fieldOfView;
		nameTextField.setText(fieldOfView.getProperty("name"));
		folderTextField.setText(fieldOfView.getProperty("folder"));

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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			fieldOfView.setProperty("name", nameTextField.getText());
			fieldOfView.setProperty("folder", folderTextField.getText());

			dispose();
		} else if (e.getSource() == cancelButton) {
			canceled = true;
			dispose();
		} else if (e.getSource() == chooseButton) {

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setCurrentDirectory(iSBatchPreferences.lastSelectedPath);
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				folderTextField.setText(fileChooser.getSelectedFile().getPath());

		}
	}

	public boolean isCanceled() {
		return canceled;
	}
	
	
}
