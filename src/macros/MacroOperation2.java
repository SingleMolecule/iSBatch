package macros;

import ij.IJ;
import model.FileNode;

public class MacroOperation2 {
	
	public static void runMacro(String macro) {
		IJ.runMacro(macro);
	}
	
	public static void runMacro(String macro, String outputPath) {
		outputPath = outputPath.replace("\\", "\\\\");
		macro = String.format("%ssave(\"%s\");close();", macro, outputPath);
		runMacro(macro);
	}
	
	public static void runMacro(FileNode fileNode, String macro, String outputPath) {
		String path = fileNode.getPath();
		path = path.replace("\\", "\\\\");
		macro = String.format("open(\"%s\");%s", path, macro);
		runMacro(macro, outputPath);
	}
	
	public static void main(String[] args) {
		
		FileNode fileNode = new FileNode(null);
		fileNode.setProperty("path", "C:\\Users\\p262597\\Desktop\\image.tif");
		
		String macro = "IJ.log(nImages);IJ.log(getTitle);";
		runMacro(fileNode, macro, "C:\\Users\\p262597\\Desktop\\image2.tif");
		
	}
	

}
