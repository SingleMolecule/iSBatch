package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class LogPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private static LogPanel instance;
	
	private DefaultListModel<String> listModel = new DefaultListModel<>();
	private JList<String> list = new JList<>(listModel);
	
	private JButton clearButton = new JButton("Clear");
	private JButton saveButton = new JButton("Save");
	
	protected LogPanel() {
		super(new BorderLayout());
		
		listModel.addElement("Log Panel");
		
		clearButton.addActionListener(this);
		saveButton.addActionListener(this);
		
		JPanel panel = new JPanel();
		panel.add(clearButton);
		panel.add(saveButton);
		
		add(panel, BorderLayout.SOUTH);
		add(new JScrollPane(list), BorderLayout.CENTER);
	}
	
	public static LogPanel getInstance() {
		
		if (instance == null)
			instance = new LogPanel();
		
		return instance;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == clearButton)
			listModel.clear();
		else if (e.getSource() == saveButton) {
			
			JFileChooser fileChooser = new JFileChooser();
			
			if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				
				// make sure the log file ends with .txt
				File file = fileChooser.getSelectedFile();
				
				if (!file.getName().endsWith(".txt"))
					file = new File(file.getPath() + ".txt");
				
				save(file);
				
			}
			
		}
		
	}
	
	public static void log(String str) {
		LogPanel panel = LogPanel.getInstance();
		panel.listModel.add(0, str);
	}
	
	public void save(File file) {
		
		try {
			FileOutputStream os = new FileOutputStream(file);
			
			String lineSeparator = System.getProperty("line.separator");
			
			for (int i = 0; i < listModel.getSize(); i++)
				os.write(new String(listModel.get(i) + lineSeparator).getBytes());
			
			os.close();
			
			JOptionPane.showMessageDialog(null, "File saved as " + file.getPath());
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
	}
	
}