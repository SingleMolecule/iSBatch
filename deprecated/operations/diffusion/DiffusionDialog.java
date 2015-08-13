package operations.diffusion;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import model.DatabaseModel;
import model.Node;
import utils.ModelUtils;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class DiffusionDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	String channel = "[Select Channel]";
	String filter = "";
	int lookAhead = 1;
	float maxStepSize = 6f;
	float timeInterval = 1f;
	float pixelSize = 100f;
	float fitUntil = 0f;
	String dimensionality = "1D";
	boolean canceled = false;

	DefaultComboBoxModel<String> channelComboBoxModel = new DefaultComboBoxModel<String>();
	JComboBox<String> channelComboBox = new JComboBox<String>(channelComboBoxModel);
	JTextField filterTextField = new JTextField(20);
	JTextField lookAheadTextField = new JTextField(10);
	JTextField maxStepSizeTextField = new JTextField(10);
	JTextField timeIntervalTextField = new JTextField(10);
	JTextField pixelSizeTextField = new JTextField(10);
	JTextField fitUntilTextField = new JTextField(10);
	DefaultComboBoxModel<String> dimensionalityComboBoxModel = new DefaultComboBoxModel<String>();
	JComboBox<String> dimensionalityComboBox = new JComboBox<String>(dimensionalityComboBoxModel);
	JButton runButton = new JButton("Run");
	JButton cancelButton = new JButton("Cancel");

	public DiffusionDialog(Node node) {

		// fill combo boxes
		channelComboBox.setModel(ModelUtils.getUniqueChannels(node));
		dimensionalityComboBoxModel.addElement("2D");
		dimensionalityComboBoxModel.addElement("3D");

		setTitle("Dialog");
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.gridx = gbc.gridy = 0;

		// dropdown for channel
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Channel"), gbc);
		gbc.gridx++;
		add(channelComboBox, gbc);

		// textfield for string filter
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Filename filter (name should contain)"), gbc);
		gbc.gridx++;
		filterTextField.setText(filter);
		add(filterTextField, gbc);

		// textfield for integer lookAhead
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Look ahead (number of slices)"), gbc);
		gbc.gridx++;
		lookAheadTextField.setText(Integer.toString(lookAhead));
		add(lookAheadTextField, gbc);

		// textfield for float maxStepSize
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Maximum step size"), gbc);
		gbc.gridx++;
		maxStepSizeTextField.setText(Float.toString(maxStepSize));
		add(maxStepSizeTextField, gbc);

		// textfield for float timeInterval
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Time interval (s)"), gbc);
		gbc.gridx++;
		timeIntervalTextField.setText(Float.toString(timeInterval));
		add(timeIntervalTextField, gbc);

		// textfield for float pixelSize
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Pixel size (um)"), gbc);
		gbc.gridx++;
		pixelSizeTextField.setText(Float.toString(pixelSize));
		add(pixelSizeTextField, gbc);

		// textfield for float fitUntil
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Fit until (0=full length)"), gbc);
		gbc.gridx++;
		fitUntilTextField.setText(Float.toString(fitUntil));
		add(fitUntilTextField, gbc);

		// dropdown for dimensionality
		gbc.gridx = 0;
		gbc.gridy++;
		add(new JLabel("Diffusion dimensionality"), gbc);
		gbc.gridx++;
		add(dimensionalityComboBox, gbc);

		runButton.addActionListener(this);
		cancelButton.addActionListener(this);

		JPanel panel = new JPanel();
		panel.add(runButton);
		panel.add(cancelButton);

		gbc.gridx = 1;
		gbc.gridy++;
		add(panel, gbc);

		pack();
		setModal(true);
		setVisible(true);
	}

	protected void parseValues() {
		channel = (String) channelComboBox.getSelectedItem();
		filter = filterTextField.getText();
		lookAhead = Integer.parseInt(lookAheadTextField.getText());
		maxStepSize = Float.parseFloat(maxStepSizeTextField.getText());
		timeInterval = Float.parseFloat(timeIntervalTextField.getText());
		pixelSize = Float.parseFloat(pixelSizeTextField.getText());
		fitUntil = Float.parseFloat(fitUntilTextField.getText());
		dimensionality = (String) dimensionalityComboBox.getSelectedItem();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == runButton) {
			parseValues();
			dispose();
		}
		if (e.getSource() == cancelButton) {
			canceled = true;
			dispose();
		}
	}

	public String getChannel() {
		return channel;
	}

	public String getFilter() {
		return filter;
	}

	public int getLookAhead() {
		return lookAhead;
	}

	public float getMaxStepSize() {
		return maxStepSize;
	}

	public float getTimeInterval() {
		return timeInterval;
	}

	public float getPixelSize() {
		return pixelSize;
	}

	public float getFitUntil() {
		return fitUntil;
	}

	public String getDimensionality() {
		return dimensionality;
	}

	public boolean wasCanceled() {
		return canceled;
	}

}
