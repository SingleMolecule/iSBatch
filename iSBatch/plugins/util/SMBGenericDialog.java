package util;

import ij.Prefs;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

public class SMBGenericDialog extends ij.gui.GenericDialog {
	private static final long serialVersionUID = 1L;
	
	public SMBGenericDialog(String title) {
		super(title);
	}
	
	private ArrayList<Button> buttons = new ArrayList<Button>();
	
	@Override
	protected void setup() {
		int count = getComponentCount();
		Panel buttonPanel = (Panel) getComponent(count - 1);
		
		for (Button button: buttons)
			buttonPanel.add(button);
		
		Button saveButton = new Button("Save Values");
		
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				savePreferences();
			}
			
		});
		
		buttonPanel.add(saveButton);
		
		pack();	// make sure all buttons are visible
		
		super.setup();
		
		// set the preferences for all numerical fields
		loadPreferences();
	}
	
	@SuppressWarnings("rawtypes")
	public void loadPreferences() {
		
		String title = getTitle().replace(' ', '_');
		
		Vector numericFields = getNumericFields();
		Vector stringFields = getStringFields();
		Vector checkboxes = getCheckboxes();
		
		int s1 = numericFields == null ? 0 : numericFields.size();
		int s2 = stringFields == null ? 0 : stringFields.size();
		int s3 = checkboxes == null ? 0 : checkboxes.size();
		
		for (int i = 0; i < s1 && i < numericFields.size(); i++) {
			TextField tf = (TextField) numericFields.get(i);
			tf.setText(Prefs.get(title + ".numericField" + i, tf.getText()));
		}
		
		for (int i = 0; i < s2 && i < stringFields.size(); i++) {
			TextField tf = (TextField) stringFields.get(i);
			tf.setText(Prefs.getString(title + ".stringField" + i, tf.getText()));
		}
		
		for (int i = 0 ; i < s3; i++) {
			Checkbox cb = (Checkbox) checkboxes.get(i);
			cb.setState(Prefs.getBoolean(title + ".checkbox" + i, cb.getState()));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void savePreferences() {
		
		String title = getTitle().replace(' ', '_');
		
		Vector numericFields = getNumericFields();
		Vector stringFields = getStringFields();
		Vector checkboxes = getCheckboxes();
		
		int s1 = numericFields == null ? 0 : numericFields.size();
		int s2 = stringFields == null ? 0 : stringFields.size();
		int s3 = checkboxes == null ? 0 : checkboxes.size();
		
		Prefs.set(title + ".numericFieldsSize", s1);
		Prefs.set(title + ".stringFieldsSize", s2);
		Prefs.set(title + ".checkboxesSize", s3);
		
		for (int i = 0; i < s1; i++) {
			TextField tf = (TextField) numericFields.get(i);
			Prefs.set(title + ".numericField" + i, tf.getText());
		}
		
		for (int i = 0; i < s2; i++) {
			TextField tf = (TextField) stringFields.get(i);
			Prefs.set(title + ".stringField" + i, tf.getText());
		}
		
		for (int i = 0 ; i < s3; i++) {
			Checkbox cb = (Checkbox) checkboxes.get(i);
			Prefs.set(title + ".checkbox" + i, cb.getState());
		}
		
		Prefs.savePreferences();
	}
	
}
