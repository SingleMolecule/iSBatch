/*
 * 
 */
package process;

import java.awt.AWTEvent;

import util.SMBGenericDialog;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;

// TODO: Auto-generated Javadoc
/**
 * The Class BackgroundRemover.
 */
public class BackgroundRemover implements ExtendedPlugInFilter, DialogListener {

	/** The flags. */
	private int flags = DOES_8G | DOES_16 | DOES_32 | PARALLELIZE_STACKS | NO_CHANGES | FINAL_PROCESSING;
	
	/** The background ip. */
	private ImageProcessor backgroundIp;
	
	/** The normalize. */
	private boolean normalize = true;
	
	/**
	 * Run.
	 *
	 * @param ip the ip
	 */
	@Override
	public void run(ImageProcessor ip) {
		
		if (backgroundIp != null) {
			for (int y = 0; y < ip.getHeight(); y++) {
				for (int x = 0; x < ip.getWidth(); x++) {
				
					if (x < backgroundIp.getWidth() && y < backgroundIp.getHeight()) {
						float pixel = ip.getf(x, y) / backgroundIp.getf(x, y);
						ip.setf(x, y, pixel);
					}
					
				}
			}
		}
		
	}

	/**
	 * Setup.
	 *
	 * @param arg the arg
	 * @param imp the imp
	 * @return the int
	 */
	@Override
	public int setup(String arg, ImagePlus imp) {
		
		return flags;
	}

	/**
	 * Dialog item changed.
	 *
	 * @param dialog the dialog
	 * @param arg1 the arg1
	 * @return true, if successful
	 */
	@Override
	public boolean dialogItemChanged(GenericDialog dialog, AWTEvent arg1) {
		
		String imageTitle = dialog.getNextChoice();
		ImagePlus backgroundImage = WindowManager.getImage(imageTitle);
		
		if (backgroundImage == null)
			return false;
		
		backgroundIp = backgroundImage.getProcessor().duplicate();
		float min = backgroundIp.getf(0, 0);
		float max = min;
		
		if (normalize) {
			
			for (int y = 0; y < backgroundIp.getHeight(); y++) {
				for (int x = 0; x < backgroundIp.getWidth(); x++) {
					
					float pixel = backgroundIp.getf(x, y);
					
					if (pixel < min)
						min = pixel;
					
					if (pixel > max)
						max = pixel;
					
				}
			}
			
			for (int y = 0; y < backgroundIp.getHeight(); y++) {
				for (int x = 0; x < backgroundIp.getWidth(); x++) {
					
					float pixel = (backgroundIp.getf(x, y) - min) / (max - min);
					backgroundIp.setf(x, y, pixel);
					
				}
			}
			
		}
		
		return true;
	}

	/**
	 * Sets the n passes.
	 *
	 * @param arg0 the new n passes
	 */
	@Override
	public void setNPasses(int arg0) {
		
	}

	/**
	 * Show dialog.
	 *
	 * @param imp the imp
	 * @param arg the arg
	 * @param pfr the pfr
	 * @return the int
	 */
	@Override
	public int showDialog(ImagePlus imp, String arg, PlugInFilterRunner pfr) {
		
		int n = WindowManager.getImageCount();
		String[] imageTitles = new String[n];
		
		for (int i = 0; i < n; i++)
			imageTitles[i] = WindowManager.getImage(i + 1).getTitle();
		
		GenericDialog dialog = new SMBGenericDialog("Background Remover");
		dialog.addChoice("Background_image", imageTitles, imageTitles[0]);
		dialog.addCheckbox("Normalize_background", normalize);
		dialog.addDialogListener(this);
		dialog.addPreviewCheckbox(pfr);
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return DONE;
		
		return IJ.setupDialog(imp, flags);
	}
	
}
