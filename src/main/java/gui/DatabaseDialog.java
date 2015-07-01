package gui;

import iSBatch.iSBatchPreferences;
import ij.Prefs;

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
	private JTextField pathTextField;
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
		
		centerPanel.add(new JLabel("Specify database to use (or choose a filename for a new database)"));
		
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pathTextField = getPathText();
		
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
	

	public DatabaseDialog(Frame parent, String pathToDatabase) {
		super(parent, "Database", true);
		File file = new File(pathToDatabase);
		iSBatchPreferences.lastSelectedPath = file.getPath();
		Prefs.set("isbatch.lastSelectet.DBdir",pathToDatabase);
		try {
			database = new Database(file);
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	private JTextField getPathText() {
		return new JTextField(Prefs.get("isbatch.lastSelectet.DBdir",System.getProperty("user.home") + File.separator + "database"), 20);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == okButton) {
			
			try {
				String path = pathTextField.getText();
				System.out.println();
				File file = new File(path);
				iSBatchPreferences.lastSelectedPath = file.getPath();
				Prefs.set("isbatch.lastSelectet.DBdir",path);
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
	
	/**
	 * Gets the database.
	 *
	 * @return the database
	 */
	public Database getDatabase() {
		return database;
	}
	
	/**
	 * Checks if is canceled.
	 *
	 * @return true, if is canceled
	 */
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		new DatabaseDialog(null);
	}

}
