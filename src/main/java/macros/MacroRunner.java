package macros;

import java.io.File;

import filters.NodeFilterInterface;
import gui.LogPanel;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import model.FileNode;
import model.Node;

public class MacroRunner {

	private String channel;
	private String tag;
	private String customName;
	private boolean saveInDatabase = true;

	private String outputTag;
	private boolean overridetags = false;
	private NodeFilterInterface filter = new NodeFilterInterface() {

		@Override
		public boolean accept(Node node) {

			return node.getType() == FileNode.type
					&& node.getChannel().equalsIgnoreCase(channel)
					&& (node.getTags().contains(tag) || node.getProperty("path").matches(customName));
		}

	};

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

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
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
			    }
			    
			}
			
			// save roi's in the roimanager
			if (roiManager.getCount() == 1) {
				String roiFile = getUniquePath(filename, ".roi");
				roiManager.runCommand("save", roiFile);
			}
			else if (roiManager.getCount() > 1) {
				String zipFile = getUniquePath(filename, ".zip");
				roiManager.runCommand("save", zipFile);
			}
			
			// save results table
			ResultsTable table = ResultsTable.getResultsTable();
			
			if (table.getCounter() > 0) {
				String csvFile = getUniquePath(filename, ".csv");
				table.save(csvFile);
			}
		
		}
		
		roiManager.close();
	}

	public void runMacro(Node node, String macro) {
		
		for (Node n: node.getDescendents(filter)) {
			LogPanel.log("run macro on " + n);
			runMacro((FileNode)n, macro);
		}
		
	}
	
	
	public static void main(String[] args) {
		
		RoiManager roiManager = new RoiManager();
		roiManager.setVisible(false);
		
		String macro = "newImage(\"Untitled\", \"32-bit black\", 16, 16, 1); ";
		macro += "run(\"Select All\"); ";
		macro += "run(\"Fill\", \"slice\"); ";
		macro += "run(\"Set Measurements...\", \"area redirect=None decimal=3\"); ";
		macro += "run(\"Measure\"); ";
		macro += "roiManager(\"Add\"); ";
		IJ.runMacro(macro);
		
		for (String title : WindowManager.getImageTitles()) {
		    
			ImagePlus imp = WindowManager.getImage(title);
		    
		    if (imp.changes) {
		    	System.out.println("imp : " + imp);
		    }
		}
		
		ResultsTable table = ResultsTable.getResultsTable();
		
		System.out.println("results table count : " + table.getCounter());
		
		System.out.println("roi manager count : " + roiManager.getCount());
		
		roiManager.close();
		
		
	}
	
	
	

}
