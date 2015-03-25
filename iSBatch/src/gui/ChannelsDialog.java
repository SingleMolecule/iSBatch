package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.Importer;

public class ChannelsDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private File folder;
	
	private JTextField acquisitionTextField = new JTextField(Importer.acqRegEx);
	private JTextField brightFieldTextField = new JTextField(Importer.bfRegEx);
	private JTextField greenTextField = new JTextField(Importer.greenRegEx);
	private JTextField redTextField = new JTextField(Importer.redRegEx);
	private JTextField blueTextField = new JTextField(Importer.blueRegEx);
	
	private JTextArea foundFilesTextArea = new JTextArea(10, 40);
	private JButton searchButton = new JButton("Search");
	private JButton okButton = new JButton("Ok");
	private JButton cancelButton = new JButton("Cancel");

	public ChannelsDialog(JFrame parent, File folder) {
		super(parent, "Database", true);

		this.folder = folder;

		JPanel centerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		searchButton.addActionListener(this);

		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		centerPanel.add(new JLabel("Acquisition Regular Expression"), gbc);

		gbc.gridx = 1;

		centerPanel.add(acquisitionTextField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;

		centerPanel.add(new JLabel("Bright Field Regular Expression"), gbc);

		gbc.gridx = 1;

		centerPanel.add(brightFieldTextField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;

		centerPanel.add(new JLabel("Green Channel Regular Expression"), gbc);

		gbc.gridx = 1;

		centerPanel.add(greenTextField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;

		centerPanel.add(new JLabel("Red Channel Regular Expression"), gbc);

		gbc.gridx = 1;

		centerPanel.add(redTextField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;

		centerPanel.add(new JLabel("Blue Channel Regular Expression"), gbc);

		gbc.gridx = 1;

		centerPanel.add(blueTextField, gbc);

		if (folder != null) {

			gbc.gridx = 0;
			gbc.gridy = 6;

			centerPanel.add(searchButton, gbc);

			gbc.gridx = 0;
			gbc.gridy = 7;

			centerPanel.add(new JLabel("Matching files : "), gbc);

			gbc.gridx = 0;
			gbc.gridy = 8;
			gbc.gridwidth = 2;
			centerPanel.add(new JScrollPane(foundFilesTextArea), gbc);

		}

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

	public void getChannelFiles(File file, ArrayList<File> files,
			String... regularExpressions) {

		if (file.isDirectory()) {

			for (File f : file.listFiles())
				getChannelFiles(f, files, regularExpressions);

		} else {

			String name = file.getName();

			for (String regularExpression : regularExpressions) {

				if (name.matches(regularExpression) && !files.contains(file))
					files.add(file);

			}

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == okButton) {

			Importer.acqRegEx = acquisitionTextField.getText();
			Importer.bfRegEx = brightFieldTextField.getText();
			Importer.greenRegEx = greenTextField.getText();
			Importer.redRegEx = redTextField.getText();
			Importer.blueRegEx = blueTextField.getText();

			dispose();
		} else if (e.getSource() == cancelButton) {

			dispose();
		} else if (e.getSource() == searchButton) {

			ArrayList<File> matchingFiles = new ArrayList<File>();

			getChannelFiles(folder, matchingFiles,
					acquisitionTextField.getText(),
					brightFieldTextField.getText(), greenTextField.getText(),
					redTextField.getText(), blueTextField.getText());

			foundFilesTextArea.setText("");
			String text = "";

			for (File f : matchingFiles)
				text += f.getPath() + "\n";

			foundFilesTextArea.setText(text);

		}

	}

}
