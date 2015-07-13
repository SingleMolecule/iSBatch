package macros;

import ij.IJ;
import ij.io.Opener;

import java.io.File;
import java.util.ArrayList;

import org.tmatesoft.sqljet.core.SqlJetException;

import gui.LogPanel;
import utils.ModelUtils;
import model.Database;
import model.DatabaseModel;
import model.Importer;
import model.Node;

public class MacroRunner2 implements Runnable {

	private DatabaseModel model;
	private boolean overrideOriginal = false;
	private String channel = "";
	private String contains = "";
	private String filename = "";
	private String macro = "";
	private File outputFolder;
	private Node node;
	private Thread thread;
	private boolean shouldRun = false;
	
	public MacroRunner2(DatabaseModel model) {
		super();
		this.model = model;
	}

	public boolean isOverrideOriginal() {
		return overrideOriginal;
	}

	public void setOverrideOriginal(boolean overrideOriginal) {
		this.overrideOriginal = overrideOriginal;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getContains() {
		return contains;
	}

	public void setContains(String contains) {
		this.contains = contains;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMacro() {
		return macro;
	}

	public void setMacro(String macro) {
		this.macro = macro;
	}
	
	public File getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(File outputFolder) {
		this.outputFolder = outputFolder;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	protected void runMacro(Node node) {

		// make a list of all the file nodes underneath node
		ArrayList<Node> fileNodes = ModelUtils.getAllFileNodes(node);
		String[] containsItems = contains.split("\\s+");
		Importer importer = new Importer(model);

		ArrayList<Node> filteredFileNodes = new ArrayList<Node>();
		
		for (Node fileNode : fileNodes) {
			if (fileNode.getChannel().equalsIgnoreCase(channel)) {
				
				String path = fileNode.getProperty("path");
				
				if (Opener.getFileFormat(path).equals(Opener.types[Opener.UNKNOWN])) {
					System.out.println("unsupported format");
					continue;
				}
				
				String name = new File(path).getName();
				
				for (String item : containsItems) {
					if (!name.contains(item))
						continue;
				}

				
				filteredFileNodes.add(fileNode);
				
			}
		}
		
		for (Node fileNode : filteredFileNodes) {

			if (!shouldRun) return;
			
			String path = fileNode.getProperty("path");

			LogPanel.log("running macro on " + path);

			String outputDirectory = fileNode.getOutputFolder();
			String outputPath = outputDirectory + File.separator + filename;
	
			if (overrideOriginal)
				outputPath = fileNode.getProperty("path");
			
			if (outputFolder != null && outputFolder.exists()) {
				outputPath = outputFolder.getPath() + File.separator + filename;
	
				int number = 0;
				
				while (new File(outputPath).exists()) {
					
					String name = filename;
					String extension = "";
					
					if (name.contains(".")) {
						name = name.substring(0, name.lastIndexOf("."));
						extension = name.substring(name.lastIndexOf("."));
					}
					
					number++;
					outputPath = outputFolder.getPath() + File.separator + name + "-" + number + extension;
					
				}
				
			}
			
			// escape characters
			path = path.replace("\\", "\\\\");
			outputPath = outputPath.replace("\\", "\\\\");
	
			String prefix = "open(\"" + path + "\");";
			String suffix = "";
	
			if (overrideOriginal)
				suffix += "if (is(\"changes\")) save(\"" + outputPath
						+ "\");";
			else {
				suffix += "function getFilename(path, extension) {\n";
				suffix += "tmp = path + extension;\n";
				suffix += "count = 1;\n";
				suffix += "while (File.exists(tmp)) {\n";
				suffix += "tmp = path + \"-\" + count + extension;\n";
				suffix += "count++;\n";
				suffix += "}\n";
				suffix += "return tmp;\n";
				suffix += "}\n";
				suffix += "outputs = \"\";\n";
				suffix += "for (i = 1; i <= nImages; i++) {\n";
				suffix += "selectImage(i);\n";
				suffix += "if (is(\"changes\")) {\n";
				suffix += "filename = getFilename(\"" + outputPath
						+ "\", \".tif\");\n";
				suffix += "saveAs(\"tiff\", filename);\n";
				suffix += "outputs += filename + \";\";\n";
				suffix += "}\n";
				suffix += "}\n";
				suffix += "if (roiManager(\"count\") > 0) {\n";
				suffix += "filename = getFilename(\"" + outputPath
						+ "\", \".zip\");\n";
				suffix += "roiManager(\"save\", filename);\n";
				suffix += "outputs += filename + \";\";\n";
				suffix += "}\n";
				suffix += "if (nResults > 0) {\n";
				suffix += "filename = getFilename(\"" + outputPath
						+ "\", \".csv\");\n";
				suffix += "saveAs(\"results\", filename);\n";
				suffix += "outputs += filename + \";\";\n";
				suffix += "}\n";
				suffix += "close(\"*\");\n";
				suffix += "roiManager(\"reset\");\n";
				suffix += "run(\"Clear Results\");\n";
				suffix += "return outputs;\n";
	
			}
	
			String outputs = IJ.runMacro(prefix + macro + suffix);
	
			// add everything to the database
			for (String output : outputs.split(";"))
				importer.importFile(fileNode.getParent(), new File(output),
						channel, new File(output).getName());
		}

	}

	@Override
	public void run() {

		runMacro(node);
	}

	public void start() {
		shouldRun = true;
		thread = new Thread(this);
		thread.start();
	}

	public void waitUntilDone() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public void stop() {
		shouldRun = false;
	}

	public static void main(String[] args) {

		try {
			
			System.setProperty("plugins.dir", "C:/Program Files/ImageJ/plugins");
			
			File file = new File("E:/test/database");

			boolean shouldImport = !file.exists();
			Database database = new Database(file);
			DatabaseModel model = new DatabaseModel(database.getRoot());

			if (shouldImport) {
				Importer importer = new Importer(model);
				importer.importExperiment(
						new File(
								"Z:/Victor/ExampleDB/MinimalDataset/RapidAcquisition/DnaX"),
						true);
				importer.importExperiment(
						new File(
								"Z:/Victor/ExampleDB/MinimalDataset/RapidAcquisition/E-DnaQ"),
						true);
				importer.importExperiment(
						new File(
								"Z:/Victor/ExampleDB/MinimalDataset/TimeLapse/DnaX_DnaX-M9Glycerol"),
						false);
				database.write(model.getRoot());
			}

			// run something on the Green channel

			String macro1 = "run(\"Delete Slice\"); IJ.log(\"nImages : \" + nImages); IJ.log(\"memory free : \" + IJ.freeMemory()); ";
			
			String macro2 = "run(\"Set Measurements...\", \"area mean standard fit stack display redirect=None decimal=5\");";
			macro2 += "makeOval(141, 144, 222, 237);";
			macro2 += "run(\"Measure\");";
			
			String macro3 = "run(\"Peak Finder\", \"use_discoidal_averaging_filter inner_radius=1 outer_radius=3 threshold=6 threshold_value=0 selection_radius=4 minimum_distance=8 stack\");";
			macro3 += "run(\"Set Measurements...\", \"area mean standard fit stack display redirect=None decimal=5\");";
			macro3 += "roiManager(\"Multi Measure\");";
			
			MacroRunner2 runner = new MacroRunner2(model);
			runner.setChannel("Green");
			runner.setNode(model.getRoot());
			runner.setContains(".TIF"); // all TIF files
			runner.setOverrideOriginal(false);
			
			runner.setFilename("macro_test1");
			runner.setMacro(macro1);
			runner.start();
			runner.waitUntilDone();
			
			runner.setFilename("macro_test2");
			runner.setMacro(macro2);
			runner.start();
			runner.waitUntilDone();
			
			runner.setFilename("macro_test3");
			runner.setMacro(macro3);
			runner.start();
			runner.waitUntilDone();
			
			database.write(model.getRoot());


		} catch (SqlJetException e) {

		}

	}

}
