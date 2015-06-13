package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Node;

public final class FileNames {

	public static String getOutputFilename(Node node, String tagName,
			String extension) {
		String outputName;
		outputName = node.getName().replace(getFileExtension(node.getName()), "") + "_" + tagName + "."
				+ extension;

		return outputName;
	}

	static String getFileExtension(String path) {
		return path.substring(path.lastIndexOf("."));
	}

	static String getFileExtension(File file) {
		return getFileExtension(file.getAbsolutePath());
	}

	public static void main(String[] args) {
		
		
//		String name = "file.name";
//		System.out.println(getFileExtension(name));

	}
	
	
	

	public static ArrayList<String> getTags(String imageType) {
		ArrayList<String> container = new ArrayList<String>();
		if (imageType != null) {
			
			List<String> list = Arrays.asList(imageType.split("\\s*,\\s*"));
			for (String foo : list) {
				container.add(foo);
			}
			return container;
		}
		else
			return container;

	}
	
	
	
	
	
}
