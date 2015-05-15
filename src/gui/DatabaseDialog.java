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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.tmatesoft.sqljet.core.SqlJetException;

import model.Database;

// TODO: Auto-generated Javadoc
/**
 * The Class DatabaseDialog.
 */
public class DatabaseDialog extends JDialog implements ActionListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The database. */
	private Database database;
	
	/** The path text field. */
	private JTextField pathTextField = new JTextField(System.getProperty("user.home") + File.separator + "database", 20);
	//private JTextField pathTextField = new JTextField("D:\\CurrentAnalysis\\current", 20);

	/** The choose button. */
	private JButton chooseButton = new JButton("Choose database");
	
	/** The ok button. */
	private JButton okButton = new JButton("Ok");
	
	/** The cancel button. */
	private JButton cancelButton = new JButton("Cancel");
	
	/** The canceled. */
	private boolean canceled = false;
	
	/**
	 * Instantiates a new database dialog.
	 *
	 * @param parent the parent
	 */
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

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == okButton) {
			
			try {
				String path = pathTextField.getText();
				
				File file = new File(path);
				iSBatchPreferences.lastSelectedPath = file.getPath();
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
