package macros;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import filters.NodeFilterInterface;
import gui.LogPanel;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import model.DatabaseModel;
import model.FileNode;
import model.Importer;
import model.Node;

public class MacroRunner implements Runnable {

	private String channel = "";
	private String tag = "";
	private String customTag = "";
	private String extension = ".tif";
	private boolean saveInDatabase = true;
	
	private String outputTag;
	private boolean overridetags = false;
	
	private NodeFilterInterface filter = new NodeFilterInterface() {

		@Override
		public boolean accept(Node node) {

			return node.getType() == FileNode.type
					&& node.getChannel().equalsIgnoreCase(channel)
					&& (node.getTags().contains(tag) || node.getProperty("path").contains(customTag))
					&& node.getProperty("path").endsWith(extension);
			
		}

	};

	private Node node;
	private String macro;
	private Importer importer;
	private Thread thread;
	private boolean shouldRun = false;
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
	
	
	public MacroRunner(DatabaseModel model) {
		importer = new Importer(model);
	}
	
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getCustomTag() {
		return customTag;
	}

	public void setCustomTag(String customTag) {
		this.customTag = customTag;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public boolean isSaveInDatabase() {
		return saveInDatabase;
	}

	public void setSaveInDatabase(boolean saveInDatabase) {
		this.saveInDatabase = saveInDatabase;
	}
	
	
	private String getUniquePath(String filename, String extension) {
		
		File f = new File(filename + extension);
		int i = 0;
		
		while (f.exists()) {
			i++;
			f = new File(filename + i + extension);
		}
		
		return f.getPath();
		
	}

	private void runMacro(FileNode node, String macro) {
		
		RoiManager roiManager = new RoiManager();
		roiManager.setVisible(false);
		
		String path = node.getProperty("path");
		path = path.replace("\\", "\\\\"); // necessary since it will be included in the macro code
		macro = "open(\"" + path + "\");" + macro;
		IJ.runMacro(macro);
		
		if (saveInDatabase) {
			
			String basename = new File(path).getName();
			
			if (basename.contains(FileNode.tagDivider))
				basename = basename.substring(0, basename.indexOf(FileNode.tagDivider));
			else if (basename.contains("."))
				basename = basename.substring(0, basename.indexOf("."));
			
			if (!overridetags) {
				for (String tag: node.getTags())
					basename += FileNode.tagDivider + tag;
			}
			
			basename += FileNode.tagDivider + outputTag;
			String filename = new File(node.getOutputFolder(), basename).getPath();
			
			// save generated or changed images
			for (String title : WindowManager.getImageTitles()) {
			    
				ImagePlus imp = WindowManager.getImage(title);
			    
			    if (imp.changes) {
			    	String tifFile = getUniquePath(filename, ".tif");
			    	IJ.saveAsTiff(imp, tifFile);
			    	File file = new File(tifFile);
			    	importer.importFile(node, file, node.getChannel(), file.getName());
			    	
			    }
			    
			}
			
			// save roi's in the roimanager
			if (roiManager.getCount() == 1) {
				String roiFile = getUniquePath(filename, ".roi");
				roiManager.runCommand("save", roiFile);
				File file = new File(roiFile);
				importer.importFile(node, file, node.getChannel(), file.getName());
			}
			else if (roiManager.getCount() > 1) {
				String zipFile = getUniquePath(filename, ".zip");
				roiManager.runCommand("save", zipFile);
				File file = new File(zipFile);
				importer.importFile(node, file, node.getChannel(), file.getName());
			}
			
			// save results table
			ResultsTable table = ResultsTable.getResultsTable();
			
			if (table.getCounter() > 0) {
				String csvFile = getUniquePath(filename, ".csv");
				table.save(csvFile);
				File file = new File(csvFile);
				importer.importFile(node, file, node.getChannel(), file.getName());
			}
		
		}
		
		roiManager.close();
	}

	public void runMacro(Node node, String macro) {
		this.node = node;
		this.shouldRun = true;
		this.macro = macro;
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop() {
		shouldRun = false;
		thread = null;
	}
	
	@Override
	public void run() {
		
		for (Node n: node.getDescendents(filter)) {
			
			if (!shouldRun)
				return;
			
			LogPanel.log("run macro on " + n);
			System.out.println("run macro on " + n);
			runMacro((FileNode)n, macro);
		}
		
		for (ActionListener listener: listeners)
			listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_LAST, "MacroRunner"));
		
	}
	
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}
	

}
