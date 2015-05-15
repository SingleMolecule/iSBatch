/*
 * 
 */
package test;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Root;
import model.Sample;

// TODO: Auto-generated Javadoc
/**
 * The Class TreeGenerator.
 */
public class TreeGenerator {

	/**
	 * Generate.
	 *
	 * @param outputFolder the output folder
	 * @param tempFolder the temp folder
	 * @param size the size
	 * @return the database model
	 */
	public static DatabaseModel generate(String outputFolder, String tempFolder, int size) {
		
		Root root = new Root(outputFolder, "test");
		DatabaseModel model = new DatabaseModel(root);
		
		Random random = new Random();
		String[] channels = new String[] {"acquisition", "bf", "red", "green", "blue"};
		
		for (int i = 0; i < size; i++) {
			System.out.println("generating experiment node " + i);
			
			String experimentPath = new File(tempFolder, "experiment" + i).getPath();
			Experiment experiment = new Experiment(root);
			experiment.setProperty("name", "experiment" + i);
			experiment.setProperty("path", experimentPath);
			experiment.setProperty("type", Math.random() < 0.5 ? "Time Sampling" : "Time Lapse");
			model.addNode(root, experiment);
			
			for (int j = 0; j < size; j++) {
				System.out.println("generating sample node " + j + " for experiment " + i);
				
				String samplePath = new File(experimentPath, "sample" + j).getPath();
				Sample sample = new Sample(experiment);
				sample.setProperty("name", "experiment" + i + "-sample" + j);
				sample.setProperty("path", samplePath);
				model.addNode(experiment, sample);
				
				for (int k = 0; k < size; k++) {
					System.out.println("generating field of view node " + k + " for sample " + j);
					
					String fovPath = new File(samplePath, "fov" + k).getPath();
					FieldOfView fov = new  FieldOfView(sample);
					fov.setProperty("name", "experiment" + i + "-sample" + j + "-fov" + k);
					fov.setProperty("path", fovPath);
					model.addNode(sample, fov);
					
					for (int l = 0; l < 3; l++) {
						System.out.println("generating file node " + l + " for fiel of view node " + k);
						
						FileNode fileNode = new FileNode(fov);
						
						int ch = random.nextInt(channels.length);
						String channel = channels[ch];
						fileNode.setProperty("channel", channel);
						
						String filePath = new File(fovPath, "file" + l + "-" + channel + ".tif").getPath();
						fileNode.setProperty("path", filePath);
						fileNode.setProperty("name", filePath);
						
						model.addNode(fov, fileNode);
					}
					
				}
				
			}
			
		}
		
		return model;
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		DatabaseModel model = TreeGenerator.generate("e:\\test", "e:\\temp", 10);
		JTree tree = new JTree(model);
		JFrame frame = new JFrame("tree test");
		frame.setLayout(new BorderLayout());
		frame.add(new JScrollPane(tree), BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
	}
	
}
