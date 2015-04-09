/*
 * 
 */
package operations.flatImages;

import java.io.File;

import javax.swing.JFileChooser;

import operations.Operation;
import gui.LogPanel;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Importer;
import model.Node;
import model.NodeFilter;
import model.Root;
import model.Sample;

// TODO: Auto-generated Javadoc
/**
 * The Class FlattenOperation.
 */
public class FlattenOperation implements Operation {

	/** The importer. */
	private Importer importer;
	
	/** The channels. */
	private String[] channels = new String[] {
			"acquisition",
			"bf",
			"red",
			"green",
			"blue",
	};
			
	/** The channel. */
	private String channel;
	
	/** The electronic offset. */
	private double electronicOffset = 3300;
	
	/** The use electronic offset image. */
	private boolean useElectronicOffsetImage = false;
	
	/** The electronic offset image. */
	private ImagePlus electronicOffsetImage;
	
	/** The background image. */
	private ImagePlus backgroundImage;
	
	/** The image file node filter. */
	private NodeFilter imageFileNodeFilter = new NodeFilter() {
		
		@Override
		public boolean accept(Node node) {

			if (!node.getType().equals(FileNode.type))
				return false;
			
			String ch = node.getProperty("channel");
			
			// check the channel of this file
			if (ch == null || !ch.equals(channel))
				return false;
			
			String path = node.getProperty("path");
			
			// check if this file is an image
			if (path == null || !(path.toLowerCase().endsWith(".tiff") || path.toLowerCase().endsWith(".tif")))
				return false;
			
			return true;
		}
	};
	
	
	/**
	 * Instantiates a new flatten operation.
	 *
	 * @param model the model
	 */
	public FlattenOperation(DatabaseModel model) {
		this.importer = new Importer(model);
	}
	
	/* (non-Javadoc)
	 * @see context.ContextElement#getContext()
	 */
	@Override
	public String[] getContext() {
		return new String[]{"Experiment", "Sample", "FieldOfView"};
	}

	/* (non-Javadoc)
	 * @see operations.Operation#getName()
	 */
	@Override
	public String getName() {
		return "Flatten";
	}

	/* (non-Javadoc)
	 * @see operations.Operation#setup(model.Node)
	 */
	@Override
	public boolean setup(Node node) {
		
		// electronic offset
		// beam profile image
		
		GenericDialog dialog = new GenericDialog("Flatten Operation");
		dialog.addChoice("Channel", channels, channels[0]);
		dialog.addNumericField("Electronic_Offset", electronicOffset, 2);
		dialog.addCheckbox("Use_Electronic_Offset_Image", useElectronicOffsetImage);
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return false;
		
		channel = dialog.getNextChoice();
		electronicOffset = dialog.getNextNumber();
		useElectronicOffsetImage = dialog.getNextBoolean();
		
		if (useElectronicOffsetImage) {
			
			JFileChooser fileChooser = new JFileChooser(node.getProperty("folder"));
			fileChooser.setDialogTitle("Select Electronic Offset Image");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int option = fileChooser.showOpenDialog(null);
					
			if (option == JFileChooser.APPROVE_OPTION)
				electronicOffsetImage = IJ.openImage(fileChooser.getSelectedFile().getPath());
			else
				return false;
					
		}
		
		JFileChooser fileChooser = new JFileChooser(node.getProperty("folder"));
		fileChooser.setDialogTitle("Select Background Image");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int option = fileChooser.showOpenDialog(null);
				
		if (option == JFileChooser.APPROVE_OPTION) {
			
			backgroundImage = IJ.openImage(fileChooser.getSelectedFile().getPath());
			
			return true;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see operations.Operation#finalize(model.Node)
	 */
	@Override
	public void finalize(Node node) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.Root)
	 */
	@Override
	public void visit(Root root) {
	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.Experiment)
	 */
	@Override
	public void visit(Experiment experiment) {
		
		// loop through all the samples
		for (Node node: experiment.getDescendents(imageFileNodeFilter))
			node.accept(this);
		
	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.Sample)
	 */
	@Override
	public void visit(Sample sample) {
		
		// loop through all the field of views
		for (Node node: sample.getDescendents(imageFileNodeFilter))
			node.accept(this);
		
	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.FieldOfView)
	 */
	@Override
	public void visit(FieldOfView fieldOfView) {
		
		// loop through all files
		for (Node node: fieldOfView.getDescendents(imageFileNodeFilter))
			node.accept(this);
		
	}

	/* (non-Javadoc)
	 * @see operations.Operation#visit(model.FileNode)
	 */
	@Override
	public void visit(FileNode fileNode) {
		
		LogPanel.log("flattening " + fileNode);
		
		
		String channel = fileNode.getProperty("channel");
		
		// check the channel of this file
		if (channel == null || !channel.equals(this.channel))
			return;
		
		String path = fileNode.getProperty("path");
		
		// check if this file is an image
		if (path == null || !(path.toLowerCase().endsWith(".tiff") || path.toLowerCase().endsWith(".tif")))
			return;
		
		ImagePlus imp = IJ.openImage(path);
		ImageConverter converter = new ImageConverter(imp);
		converter.convertToGray32();
		
		ImageProcessor backgroundIp =  backgroundImage.getProcessor();
		
		// determine maximum pixel value
		double maximumPixelValue = backgroundIp.getMax(); 
		
		
		ImageProcessor electronicOffsetIp = null;
		
		if (useElectronicOffsetImage)
			electronicOffsetIp = electronicOffsetImage.getProcessor();
		
		// go through the whole stack
		ImageStack stack = imp.getStack();
		
		for (int slice = 1; slice <= stack.getSize(); slice++) {
			
			ImageProcessor ip = stack.getProcessor(slice);

			// subtract electronic offset and
			// divide by background
			for (int y = 0; y < ip.getHeight(); y++) {
				for (int x = 0; x < ip.getWidth(); x++) {
					
					if (useElectronicOffsetImage)
						electronicOffset = electronicOffsetIp.getf(x, y);
					
					double backgroundValue = (backgroundIp.getf(x, y) - electronicOffset) / (maximumPixelValue - electronicOffset);
					double value = ip.getf(x, y) - electronicOffset;
				
					ip.setf(x, y, (float)Math.abs(value / backgroundValue));
				}
				
			}
			
		}
		
		// filename without extension
		File file = new File(path);
		String filename = file.getName();
		String filenameWithoutExtension = filename.substring(0, filename.lastIndexOf('.'));
		File outputFile = new File(fileNode.getOutputFolder(), filenameWithoutExtension + "-flat.tif");
		
		LogPanel.log("saving flattened file as " + outputFile);
		IJ.saveAsTiff(imp, outputFile.getPath());
		
		// add it to the tree
		LogPanel.log("adding to tree");
		importer.importFile(fileNode.getParent(), outputFile);
	}

	private double getMaxPixelValue(ImageProcessor backgroundIp) {
		double maxValue = backgroundIp.getf(0, 0);
		
		for (int y = 0; y < backgroundIp.getHeight(); y++) {
			for (int x = 0; x < backgroundIp.getWidth(); x++) {
				
				double value = backgroundIp.getf(x, y);
				
				if (value > maxValue)
					maxValue = value;
			}
		}
		return maxValue;
	}

}
