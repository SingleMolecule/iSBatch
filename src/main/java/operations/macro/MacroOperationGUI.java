/*
 * 
 */
package operations.macro;

import filters.NodeFilterInterface;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Macro;
import ij.WindowManager;
import ij.macro.Interpreter;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.GridBagConstraints;

import javax.swing.JTextField;

import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import model.FileNode;
import model.Node;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MacroOperationGUI extends JDialog implements ItemListener {
	private static final long serialVersionUID = 1L;
	private static JTextField outputTextField;
	private String[] channels = new String[] {

	"[Select Channel]", "All", "Acquisition", "Bright Field", "Red", "Green",
			"Blue", };

	private JComboBox comboBox;
	private static final String[] code = { "[Select from list]", "Add Border",
			"Convert to RGB", "Crop", "Gaussian Blur", "Invert", "Label",
			"Timestamp", "Max Dimension", "Measure", "Print Index and Title",
			"Resize", "Scale", "Show File Info", "Unsharp Mask", };

	private JTextField CustomFilter;
	private JComboBox macroComboBox;
	private JTextArea MacroTextArea;
	private boolean canceled = false;
	private String macro = "";
	private JTextField outputTag;
	private ImagePlus outputImage;
	private JFrame frame;

	private Node node;

	private String Selectedchannel = "All";

	public MacroOperationGUI(Node node) {
		frame = new JFrame();
		this.node = node;

		setup();
		display();

	}

	private void setup() {
	}

	private void display() {

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 60, 60, 60, 60, 60, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 14, 23, 0, 0, 0, 23, 202, 0, 0,
				0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 1.0,
				0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JLabel lblBatch = new JLabel("Batch: ");
		lblBatch.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblBatch = new GridBagConstraints();
		gbc_lblBatch.anchor = GridBagConstraints.NORTH;
		gbc_lblBatch.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblBatch.insets = new Insets(0, 0, 5, 5);
		gbc_lblBatch.gridx = 1;
		gbc_lblBatch.gridy = 0;
		getContentPane().add(lblBatch, gbc_lblBatch);

		JButton btnOutput = new JButton("Output");
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
					outputTextField.setText(fileChooser.getSelectedFile()
							.getPath());
			}
		});

		JLabel lblOperation = new JLabel(node.getType() + ": "
				+ node.toString());

		GridBagConstraints gbc_lblOperation = new GridBagConstraints();
		gbc_lblOperation.anchor = GridBagConstraints.SOUTH;
		gbc_lblOperation.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblOperation.insets = new Insets(0, 0, 5, 5);
		gbc_lblOperation.gridx = 3;
		gbc_lblOperation.gridy = 0;
		getContentPane().add(lblOperation, gbc_lblOperation);
		GridBagConstraints gbc_btnOutput = new GridBagConstraints();
		gbc_btnOutput.gridwidth = 2;
		gbc_btnOutput.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnOutput.insets = new Insets(0, 0, 5, 5);
		gbc_btnOutput.gridx = 1;
		gbc_btnOutput.gridy = 1;
		getContentPane().add(btnOutput, gbc_btnOutput);

		outputTextField = new JTextField();
		outputTextField.setText("Database");
		outputTextField.setColumns(1);
		GridBagConstraints gbc_outputTextField = new GridBagConstraints();
		gbc_outputTextField.gridwidth = 3;
		gbc_outputTextField.insets = new Insets(0, 0, 5, 5);
		gbc_outputTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_outputTextField.gridx = 3;
		gbc_outputTextField.gridy = 1;
		getContentPane().add(outputTextField, gbc_outputTextField);

		comboBox = new JComboBox();
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {
					// Do any operations you need to do when an item is
					// selected.
					// The list of files to be accepted are in the importer.
					String item = String.valueOf(comboBox.getSelectedItem());
					String code = null;
					if (item.equals("[Select Channel]"))
						code = "All";
					else if (item.equals("All"))
						code = item;
					else if (item.equals("Acquisition"))
						code = "acquisition";
					else if (item.equals("Bright Field"))
						code = "bf";
					else if (item.equals("Red"))
						code = "red";
					else if (item.equals("Green"))
						code = "green";
					else if (item.equals("Blue"))
						code = "blue";
					if (code != null) {
						Selectedchannel = code;
					}
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					// Do any operations you need to do when an item is
					// de-selected.
				}

			}
		});
		comboBox.setModel(new DefaultComboBoxModel(channels));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 2;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 2;
		getContentPane().add(comboBox, gbc_comboBox);

		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(
				new String[] { "FileType" }));
		GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
		gbc_comboBox_1.gridwidth = 2;
		gbc_comboBox_1.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_1.gridx = 3;
		gbc_comboBox_1.gridy = 2;
		getContentPane().add(comboBox_1, gbc_comboBox_1);

		JLabel lblFilenameContains = new JLabel("Filename Contains: ");
		GridBagConstraints gbc_lblFilenameContains = new GridBagConstraints();
		gbc_lblFilenameContains.gridwidth = 2;
		gbc_lblFilenameContains.insets = new Insets(0, 0, 5, 5);
		gbc_lblFilenameContains.anchor = GridBagConstraints.EAST;
		gbc_lblFilenameContains.gridx = 1;
		gbc_lblFilenameContains.gridy = 3;
		getContentPane().add(lblFilenameContains, gbc_lblFilenameContains);

		CustomFilter = new JTextField();
		CustomFilter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});

		GridBagConstraints gbc_CustomFilter = new GridBagConstraints();
		gbc_CustomFilter.gridwidth = 2;
		gbc_CustomFilter.insets = new Insets(0, 0, 5, 5);
		gbc_CustomFilter.fill = GridBagConstraints.HORIZONTAL;
		gbc_CustomFilter.gridx = 3;
		gbc_CustomFilter.gridy = 3;
		getContentPane().add(CustomFilter, gbc_CustomFilter);
		CustomFilter.setColumns(1);

		JLabel lblOutputTag = new JLabel("Output Tag:");
		GridBagConstraints gbc_lblOutputTag = new GridBagConstraints();
		gbc_lblOutputTag.gridwidth = 2;
		gbc_lblOutputTag.insets = new Insets(0, 0, 5, 5);
		gbc_lblOutputTag.gridx = 1;
		gbc_lblOutputTag.gridy = 4;
		getContentPane().add(lblOutputTag, gbc_lblOutputTag);

		outputTag = new JTextField();
		outputTag.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		GridBagConstraints gbc_outputTag = new GridBagConstraints();
		gbc_outputTag.gridwidth = 2;
		gbc_outputTag.insets = new Insets(0, 0, 5, 5);
		gbc_outputTag.fill = GridBagConstraints.HORIZONTAL;
		gbc_outputTag.gridx = 3;
		gbc_outputTag.gridy = 4;
		getContentPane().add(outputTag, gbc_outputTag);
		outputTag.setColumns(10);

		JLabel lblMacro = new JLabel("Add macro");
		GridBagConstraints gbc_lblMacro = new GridBagConstraints();
		gbc_lblMacro.gridwidth = 2;
		gbc_lblMacro.anchor = GridBagConstraints.EAST;
		gbc_lblMacro.insets = new Insets(0, 0, 5, 5);
		gbc_lblMacro.gridx = 1;
		gbc_lblMacro.gridy = 5;
		getContentPane().add(lblMacro, gbc_lblMacro);

		macroComboBox = new JComboBox();
		macroComboBox.setModel(new DefaultComboBoxModel(code));
		macroComboBox.addItemListener(this);

		GridBagConstraints gbc_macroComboBox = new GridBagConstraints();
		gbc_macroComboBox.gridwidth = 2;
		gbc_macroComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_macroComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_macroComboBox.gridx = 3;
		gbc_macroComboBox.gridy = 5;
		getContentPane().add(macroComboBox, gbc_macroComboBox);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 5;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 6;
		getContentPane().add(scrollPane, gbc_scrollPane);

		MacroTextArea = new JTextArea();
		MacroTextArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		scrollPane.setViewportView(MacroTextArea);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}

		});
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.insets = new Insets(0, 0, 5, 5);
		gbc_btnSave.gridx = 1;
		gbc_btnSave.gridy = 7;
		getContentPane().add(btnSave, gbc_btnSave);

		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open();
			}

		});
		GridBagConstraints gbc_btnOpen = new GridBagConstraints();
		gbc_btnOpen.insets = new Insets(0, 0, 5, 5);
		gbc_btnOpen.gridx = 2;
		gbc_btnOpen.gridy = 7;
		getContentPane().add(btnOpen, gbc_btnOpen);

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MacroTextArea.setText("");
			}
		});
		GridBagConstraints gbc_btnClear = new GridBagConstraints();
		gbc_btnClear.insets = new Insets(0, 0, 5, 5);
		gbc_btnClear.gridx = 3;
		gbc_btnClear.gridy = 7;
		getContentPane().add(btnClear, gbc_btnClear);

		JButton btnProcess = new JButton("Process");
		btnProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				run();
			}

		});

		JButton btnTest = new JButton("Test");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				test();
			}
		});
		GridBagConstraints gbc_btnTest = new GridBagConstraints();
		gbc_btnTest.insets = new Insets(0, 0, 5, 5);
		gbc_btnTest.gridx = 1;
		gbc_btnTest.gridy = 8;
		getContentPane().add(btnTest, gbc_btnTest);
		GridBagConstraints gbc_btnProcess = new GridBagConstraints();
		gbc_btnProcess.insets = new Insets(0, 0, 5, 5);
		gbc_btnProcess.gridx = 4;
		gbc_btnProcess.gridy = 8;
		getContentPane().add(btnProcess, gbc_btnProcess);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				canceled = true;
				dispose();
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 5, 5);
		gbc_btnCancel.gridx = 5;
		gbc_btnCancel.gridy = 8;
		getContentPane().add(btnCancel, gbc_btnCancel);

		pack();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);

	}

	/**
	 * Run.
	 */
	private void run() {
		macro = MacroTextArea.getText();
		if (macro.equals("")) {
			error("There is no macro code in the text area");
			return;
		}
		// get array of Images
		ArrayList<Node> images = node.getDescendents(imageFileNodeFilter);

		for (Node image : images) {
			int countFiles = 1;
			if (runMacro(macro, image)) {
				// if true, macro returned an image;
				// save Image
				String path;
				if (outputTextField.getText().equalsIgnoreCase("database")) {
					// save in the tree structure - not yet.

					if (!outputTag.getText().equalsIgnoreCase("")) {
						path = image.getParent().getProperty("path")
								+ File.separator + image.getProperty("name")
								+ outputTag.getText();
					} else {
						path = image.getParent().getProperty("path")
								+ File.separator + image.getProperty("name")
								+ "- " + Integer.toString(countFiles);
						countFiles++;
					}

					IJ.save(outputImage, path);

				} else {

					if (!outputTag.getText().equalsIgnoreCase("")) {
						path = outputTextField.getText() + File.separator
								+ image.getProperty("name")
								+ outputTag.getText();
					} else {
						path = outputTextField.getText() + File.separator
								+ image.getProperty("name") + "- "
								+ Integer.toString(countFiles);
						countFiles++;
					}

					IJ.save(outputImage, path);

				}

			}

		}

	}

	/**
	 * Run macro.
	 *
	 * @param macro
	 *            the macro
	 * @param image
	 *            the image
	 * @return true, if successful
	 */
	private boolean runMacro(String macro, Node image) {
		ImagePlus imp = IJ.openImage(image.getProperty("path"));

		Interpreter interp = new Interpreter();
		try {
			outputImage = interp.runBatchMacro(macro, imp);
		} catch (Throwable e) {
			interp.abortMacro();
			String msg = e.getMessage();
			if (!(e instanceof RuntimeException && msg != null && e
					.getMessage().equals(Macro.MACRO_CANCELED)))
				IJ.handleException(e);
			return false;
		} finally {
			WindowManager.setTempCurrentImage(null);
		}
		return true;

	}

	/**
	 * Run macro.
	 *
	 * @param macro
	 *            the macro
	 * @param imp
	 *            the imp
	 * @return true, if successful
	 */
	private boolean runMacro(String macro, ImagePlus imp) {
		WindowManager.setTempCurrentImage(imp);
		Interpreter interp = new Interpreter();
		try {
			outputImage = interp.runBatchMacro(macro, imp);
		} catch (Throwable e) {
			interp.abortMacro();
			String msg = e.getMessage();
			if (!(e instanceof RuntimeException && msg != null && e
					.getMessage().equals(Macro.MACRO_CANCELED)))
				IJ.handleException(e);
			return false;
		} finally {
			WindowManager.setTempCurrentImage(null);
		}
		return true;
	}

	/**
	 * Test.
	 */
	private void test() {
		IJ.log("Print list of files to be analysed.");
		ArrayList<Node> allnodes = node.getDescendents(imageFileNodeFilter);
		for (Node node : allnodes) {

			IJ.log(node.getProperty("path"));
		}

	}

	/**
	 * Save.
	 */
	private void save() {
		macro = MacroTextArea.getText();
		if (!macro.equals(""))
			IJ.saveString(macro, "");

	}

	/**
	 * Open.
	 */
	private void open() {
		String text = IJ.openAsString("");
		if (text == null)
			return;
		if (text.startsWith("Error: ")) {
			error(text.substring(7));
		}

		else {
			if (text.length() > 30000) {
				error("File is too large");
			} else {
				MacroTextArea.setText(text);
			}
		}

	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();

		new MacroOperationGUI(null);

	}

	/**
	 * Channel combo box.
	 *
	 * @return the j combo box
	 */
	public JComboBox ChannelComboBox() {
		return comboBox;
	}

	/**
	 * Item state changed.
	 *
	 * @param e
	 *            the e
	 */
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			// Do any operations you need to do when an item is selected.

			String item = String.valueOf(macroComboBox.getSelectedItem());
			String code = null;
			if (item.equals("Convert to RGB"))
				code = "run(\"RGB Color\");\n";
			else if (item.equals("Measure"))
				code = "run(\"Measure\");\n";
			else if (item.equals("Resize"))
				code = "run(\"Size...\", \"width=512 height=512 interpolation=Bicubic\");\n";
			else if (item.equals("Scale"))
				code = "scale=1.5;\nw=getWidth*scale; h=getHeight*scale;\nrun(\"Size...\", \"width=w height=h interpolation=Bilinear\");\n";
			else if (item.equals("Label"))
				code = "setFont(\"SansSerif\", 18, \"antialiased\");\nsetColor(\"red\");\ndrawString(\"Hello\", 20, 30);\n";
			else if (item.equals("Timestamp"))
				code = openMacroFromJar("TimeStamp.ijm");
			else if (item.equals("Crop"))
				code = "makeRectangle(getWidth/4, getHeight/4, getWidth/2, getHeight/2);\nrun(\"Crop\");\n";
			else if (item.equals("Add Border"))
				code = "border=25;\nw=getWidth+border*2; h=getHeight+border*2;\nrun(\"Canvas Size...\", \"width=w height=h position=Center zero\");\n";
			else if (item.equals("Invert"))
				code = "run(\"Invert\");\n";
			else if (item.equals("Gaussian Blur"))
				code = "run(\"Gaussian Blur...\", \"sigma=2\");\n";
			else if (item.equals("Unsharp Mask"))
				code = "run(\"Unsharp Mask...\", \"radius=1 mask=0.60\");\n";
			else if (item.equals("Show File Info"))
				code = "path=File.directory+File.name;\ndate=File.dateLastModified(path);\nsize=File.length(path);\nprint(i+\", \"+getTitle+\", \"+date+\", \"+size);\n";
			else if (item.equals("Max Dimension"))
				code = "max=2048;\nw=getWidth; h=getHeight;\nsize=maxOf(w,h);\nif (size>max) {\n  scale = max/size;\n  w*=scale; h*=scale;\n  run(\"Size...\", \"width=w height=h interpolation=Bicubic average\");\n}";
			else if (item.equals("Print Index and Title"))
				code = "if (i==0) print(\"\\\\Clear\"); print(IJ.pad(i,4)+\": \"+getTitle());\n";
			if (code != null) {
				MacroTextArea.insert(code, MacroTextArea.getCaretPosition());
				if (IJ.isMacOSX())
					MacroTextArea.requestFocus();
				//
			}
		} else if (e.getStateChange() == ItemEvent.DESELECTED) {
			// Do any operations you need to do when an item is de-selected.
		}

	}

	/**
	 * Open macro from jar.
	 *
	 * @param name
	 *            the name
	 * @return the string
	 */
	private String openMacroFromJar(String name) {
		ImageJ ij = IJ.getInstance();
		Class c = ij != null ? ij.getClass() : (new ImageStack()).getClass();
		String macro = null;
		try {
			InputStream is = c.getResourceAsStream("/macros/" + name);
			if (is == null)
				return null;
			InputStreamReader isr = new InputStreamReader(is);
			StringBuffer sb = new StringBuffer();
			char[] b = new char[8192];
			int n;
			while ((n = isr.read(b)) > 0)
				sb.append(b, 0, n);
			macro = sb.toString();
		} catch (IOException e) {
			return null;
		}
		return macro;
	}

	void error(String msg) {
		IJ.error("Batch Processor", msg);
	}
	public JComboBox MacroComboBox() {
		return macroComboBox;
	}

	public boolean isCanceled() {
		return canceled;
	}

	/** The image file node filter. */
	private NodeFilterInterface imageFileNodeFilter = new NodeFilterInterface() {

		@Override
		public boolean accept(Node node) {
			// Filtering by types
			System.out.println(CustomFilter.getText());

			if (!node.getType().equals(FileNode.type))
				return false;

			String ch = node.getProperty("channel");
			System.out.println(ch);
			if (!Selectedchannel.equalsIgnoreCase("All")) {

				// check the channel of this file
				if (ch == null || !ch.equals(Selectedchannel))
					return false;
			}

			String path = node.getProperty("path");

			// check if this file is an image
			if (path == null
					|| !(path.toLowerCase().endsWith(".tiff") || path
							.toLowerCase().endsWith(".tif")))
				return false;

			// Get custom string and remove spaces in the begin and end. Not in
			// the middle.

			String customString = CustomFilter.getText();
			customString = customString.replaceAll("^\\s+|\\s+$", "");

			if (!customString.equalsIgnoreCase("")) {
				// Filtering by name
				String name = node.getProperty("name");

				if (!name.toLowerCase().contains(customString.toLowerCase())) {
					return false;
				}
				;

			}
			return true;
		};
	};
}
