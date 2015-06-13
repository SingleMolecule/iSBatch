/*
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// TODO: Auto-generated Javadoc
/**
 * The Class MacroDialog.
 */
public class MacroDialog extends JDialog implements ActionListener {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The channels. */
	private String[] channels = new String[] {
		"acquisition",
		"Bright Field",
		"red",
		"green",
		"blue",
	};
	
	/** The macro file text field. */
	private JTextField macroFileTextField = new JTextField(20);
	
	/** The choose button. */
	private JButton chooseButton = new JButton("Choose");
	
	/** The ok button. */
	private JButton okButton = new JButton("Ok");
	
	/** The cancel button. */
	private JButton cancelButton = new JButton("Cancel");
	
	/** The channel combo box. */
	private JComboBox<String> channelComboBox = new JComboBox<String>(channels);
	
	/** The canceled. */
	private boolean canceled = false;
	
	/**
	 * Instantiates a new macro dialog.
	 *
	 * @param parent the parent
	 */
	public MacroDialog(JFrame parent) {
		super(parent, "Run Macro", true);
		
		JPanel centerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;

		centerPanel.add(new JLabel("Macro file"), gbc);

		gbc.gridx++;

		centerPanel.add(macroFileTextField, gbc);

		gbc.gridx++;

		centerPanel.add(chooseButton, gbc);
		chooseButton.addActionListener(this);
		
		gbc.gridx = 0;
		gbc.gridy++;
		
		centerPanel.add(new JLabel("Channel"), gbc);
		
		gbc.gridx++;
		
		centerPanel.add(channelComboBox, gbc);

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
			dispose();
		} else if (e.getSource() == cancelButton) {
			canceled = true;
			dispose();
		} else if (e.getSource() == chooseButton) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				macroFileTextField.setText(fileChooser.getSelectedFile().getPath());
			
		}
	}
	
	/**
	 * Gets the macro filename.
	 *
	 * @return the macro filename
	 */
	public String getMacroFilename() {
		return macroFileTextField.getText();
	}
	
	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public String getChannel() {
		return (String)channelComboBox.getSelectedItem();
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
