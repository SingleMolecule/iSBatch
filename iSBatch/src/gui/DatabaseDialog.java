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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.tmatesoft.sqljet.core.SqlJetException;

import model.Database;

public class DatabaseDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Database database;
	private JTextField pathTextField = new JTextField(System.getProperty("user.home") + File.separator + "database", 20);
	//private JTextField pathTextField = new JTextField("D:\\CurrentAnalysis\\current", 20);

	private JButton chooseButton = new JButton("Choose database");
	private JButton okButton = new JButton("Ok");
	private JButton cancelButton = new JButton("Cancel");
	private boolean canceled = false;
	
	public DatabaseDialog(Frame parent) {
		super(parent, "Database", true);
		
		chooseButton.addActionListener(this);
		
		JPanel centerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		centerPanel.add(new JLabel("Specify database to use"));
		
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		centerPanel.add(pathTextField, gbc);
		
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.NONE;
		
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
			
			try {
				String path = pathTextField.getText();
				File file = new File(path);
				database = new Database(file);
			}
			catch (SqlJetException exception) {
				JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			
			dispose();
		}
		else if (e.getSource() == cancelButton) {
			database = null;
			canceled = true;
			dispose();
		}
		else if (e.getSource() == chooseButton) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				pathTextField.setText(fileChooser.getSelectedFile().getPath());
		}
		
	}
	
	public Database getDatabase() {
		return database;
	}
	
	public boolean isCanceled() {
		return canceled;
	}

	public static void main(String[] args) {
		new DatabaseDialog(null);
	}

}
