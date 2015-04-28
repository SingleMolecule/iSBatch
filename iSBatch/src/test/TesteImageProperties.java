package test;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class TesteImageProperties {
	public static void main(String[] args) {
		
		ImagePlus imp = IJ.openImage("D:\\TestFolderIsbatch\\BFTeste.tif");
		
		int size = imp.getStackSize();
		ImageStack stack = imp.getStack();
		
		for(int i =1; i<size; i++){
			imp.setSlice(i);
			
					
			System.out.println(stack.getShortSliceLabel(i));
			
			
			
			
		}
		
		
		
	}

}
