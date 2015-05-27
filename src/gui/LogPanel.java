/*
 * 
 */
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



import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class LogPanel.
 */
public class LogPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static LogPanel instance;
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> list = new JList<String>(listModel);
	private JButton clearButton = new JButton("Clear");
	private JButton saveButton = new JButton("Save");
	
	/**
	 * Instantiates a new log panel.
	 */
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
	
	/**
	 * Gets the single instance of LogPanel.
	 *
	 * @return single instance of LogPanel
	 */
	public static LogPanel getInstance() {
		
		if (instance == null)
			instance = new LogPanel();
		
		return instance;
	}

	/**
	 * Action performed.
	 *
	 * @param e the e
	 */
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
	
	/**
	 * Log.
	 *
	 * @param str the str
	 */
	public static void log(String str) {
		String ts = new SimpleDateFormat("HH:mm:ss").format(new Date());
		LogPanel panel = LogPanel.getInstance();
		panel.listModel.add(0, ts + " | "+ str);
	}
	

	public static void log(int integer) {
		log(Integer.toString(integer));
	}
	
	
	/**
	 * Save.
	 *
	 * @param file the file
	 */
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
