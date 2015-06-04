package utils;

import ij.ImagePlus;
import ij.plugin.ZProjector;


public abstract class Projections {

	
	public static ImagePlus doAverageProjection(ImagePlus imp) {
		// Properly save and keep track of that file now.
		ZProjector projector = new ZProjector(imp);
		projector.setMethod(ZProjector.AVG_METHOD);
		projector.doProjection();
		return projector.getProjection();
	}
}
