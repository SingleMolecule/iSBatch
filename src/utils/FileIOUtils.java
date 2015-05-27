package utils;

import ij.gui.Roi;
import ij.io.RoiEncoder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class FileIOUtils {
	
	public static void saveListOfRoisAsZip(ArrayList<Roi> rois, String filename)
			throws IOException {
		ZipOutputStream zos = new ZipOutputStream(
				new FileOutputStream(filename));

		int i = 0;

		for (Roi roi : rois) {
			byte[] b = RoiEncoder.saveAsByteArray(roi);
			zos.putNextEntry(new ZipEntry(i + ".roi"));
			zos.write(b, 0, b.length);
			i++;
		}
		zos.close();
	}
	

}
