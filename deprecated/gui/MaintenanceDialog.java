package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import filters.NodeFilterInterface;
import model.DatabaseModel;
import model.FileNode;
import model.Node;
import utils.FileUtils;

public class MaintenanceDialog extends JDialog implements ActionListener, NodeFilterInterface {
	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private Node node;
	private DatabaseModel model;

	private JTextField filterTextField = new JTextField(20);
	DefaultListModel<Node> nodeListModel = new DefaultListModel<Node>();
	private JList<Node> nodeList = new JList<Node>(nodeListModel);
	private JButton searchButton = new JButton("Search");
	private JButton deleteButton = new JButton("Delete");
	private JButton exportButton = new JButton("Export");

	public MaintenanceDialog(JFrame parent, Node node, DatabaseModel model) {
		super(parent, "Database", true);
		this.frame = parent;
		this.node = node;
		this.model = model;

		searchButton.addActionListener(this);
		deleteButton.addActionListener(this);
		exportButton.addActionListener(this);

		setModal(true);
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;

		add(new JLabel("Search for"), gbc);

		gbc.gridx++;

		add(filterTextField, gbc);

		gbc.gridx++;

		add(searchButton, gbc);

		gbc.gridx = 0;
		gbc.gridy++;

		gbc.gridwidth = 3;

		add(new JScrollPane(nodeList), gbc);

		gbc.gridx = 0;
		gbc.gridy++;

		JPanel panel = new JPanel();
		panel.add(deleteButton);
		panel.add(exportButton);
		add(panel, gbc);

		pack();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == searchButton) {

			nodeListModel.clear();

			for (Node n : node.getDescendents(this))
				nodeListModel.addElement(n);

		} else if (e.getSource() == deleteButton) {

			for (Node n : node.getDescendents(this)) {
				model.removeNode(n.getParent(), n);

				try {
					FileUtils.delete(new File(n.getOutputFolder()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}

		} else if (e.getSource() == exportButton) {

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {

				File selectedFile = fileChooser.getSelectedFile();

				if (!selectedFile.getAbsolutePath().endsWith(".zip"))
					selectedFile = new File(selectedFile.getAbsolutePath() + ".zip");

				try {
					ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(selectedFile));

					byte[] buffer = new byte[1024];

					HashSet<String> uniqueNames = new HashSet<String>();

					for (Node n : node.getDescendents(this)) {

						if (n.getType().equals(FileNode.type)) {

							String filename = new File(n.getProperty("path")).getName();
							String extension = "";

							if (filename.contains(".")) {
								extension = filename.substring(filename.lastIndexOf("."));
								filename = filename.substring(0, filename.lastIndexOf("."));
							}

							String uname = filename + extension;
							int number = 0;

							while (uniqueNames.contains(uname)) {
								number++;
								uname = filename + "-" + number + extension;
							}

							uniqueNames.add(uname);

							ZipEntry ze = new ZipEntry(uname);
							zos.putNextEntry(ze);

							FileInputStream in = new FileInputStream(n.getProperty("path"));

							int len;

							while ((len = in.read(buffer)) > 0) {
								zos.write(buffer, 0, len);
							}

							in.close();

						}

					}
					zos.close();

				} catch (Exception ex) {
					LogPanel.log(ex.getMessage());
				}

			}

		}

	}

	public static void main(String[] args) {

		//DatabaseModel model = TreeGenerator.generate("C:\\test", "C:\\test_temp", 10);

		//new MaintenanceDialog(null, model.getRoot(), model);
	}

	@Override
	public boolean accept(Node node) {
		return node.getName().toLowerCase().contains(filterTextField.getText().toLowerCase());
	}
}
