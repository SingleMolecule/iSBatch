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

public class MacroDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private String[] channels = new String[] {
		"acquisition",
		"bf",
		"red",
		"green",
		"blue",
	};
	
	private JTextField macroFileTextField = new JTextField(20);
	private JButton chooseButton = new JButton("Choose");
	private JButton okButton = new JButton("Ok");
	private JButton cancelButton = new JButton("Cancel");
	private JComboBox<String> channelComboBox = new JComboBox<String>(channels);
	private boolean canceled = false;
	
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
	
	public String getMacroFilename() {
		return macroFileTextField.getText();
	}
	
	public String getChannel() {
		return (String)channelComboBox.getSelectedItem();
	}
	
	public boolean isCanceled() {
		return canceled;
	}

}
