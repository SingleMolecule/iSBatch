package utils;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;

import java.util.ArrayList;

/**
 * @author Victor Caldas
 * 
 *         This class takes care of operations in ImageStacks. Operations
 *         related to strings itself should be writen in StringUtils.class. This
 *         class handles the management, iterations and other possible simple
 *         operations. If a image has to be returned, operations toget the
 *         ImagePlus or ImageStack/ImageProcessor should be done via ImageUtils.
 * 
 */
public abstract class ImageStackUtils {

	private static StringUtils strUtils = new StringUtils(false);

	/**
	 * @param imp
	 *            Image as input of type @ImagePlus
	 * @return ArrayList<String> with unique @FieldOfView names;
	 */

	public static ArrayList<String> getUniqueFOVNames(ImagePlus imp) {
				
		return getUniqueFOVNames(imp.getStack());
	}

	private static ArrayList<String> getUniqueFOVNames(ImageStack stack) {
		return strUtils.removeDuplicates(getFOVNames(stack));
	}

	private static ArrayList<String> getFOVNames(ImagePlus imp) {
		return getFOVNames(imp.getStack());
	}

	private static ArrayList<String> getFOVNames(ImageStack stack) {
		ArrayList<String> uniqueFOVNames = new ArrayList<String>();

		int size = stack.getSize();

		for (int i = 1; i <= size; i++) {
			uniqueFOVNames.add(strUtils.getFovNameFromTLTitle(stack
					.getShortSliceLabel(i)));
		}

		return uniqueFOVNames;
	}

	/**
	 * @param args
	 *            Main method for testing.
	 */
	public static void main(String[] args) {
		ImagePlus imp = IJ
				.openImage("/home/vcaldas/ISBatchTutorial/MinimalDataset/TutorialDB_files/TimeLapse/[Bright Field]MTInput.tif");
		ArrayList<String> arrStr = getFOVNames(imp);

		for (String s : arrStr) {
			System.out.println(s);
		}

		System.out.println(imp.getTitle());

		System.out.println("-------");
		arrStr = strUtils.removeDuplicates(getUniqueFOVNames(imp));

		for (String s : arrStr) {
			System.out.println(s);
		}

	}

}
