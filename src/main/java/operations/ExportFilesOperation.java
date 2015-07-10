/************************************************************************
 * 				iSBatch  Copyright (C) 2015  							*
 *		Victor E. A. Caldas -  v.e.a.caldas at rug.nl					*
 *		C. Michiel Punter - c.m.punter at rug.nl						*
 *																		*
 *	This program is distributed in the hope that it will be useful,		*
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of		*
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the		*
 *	GNU General Public License for more details.						*
 *	You should have received a copy of the GNU General Public License	*
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************/
package operations;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.process.ImageProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import gui.LogPanel;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operations.Operation;
import test.TreeGenerator;

public class ExportFilesOperation implements Operation {
	ExportFilesOperationGUI dialog;
	private String method;
	private String fileType;
	private String stringToMatch;
	private String output;
	private ImagePlus imp;
	private ResultsTable table;
	private Node currentNode;
	private int currentCount = 0;
	private String sequenceName;
	private ImageStack stack = null;

	/**
	 * Instantiates a new sets the back ground.
	 *
	 * @param treeModel
	 *            the tree model
	 */
	public ExportFilesOperation(DatabaseModel treeModel) {
	}

	public String[] getContext() {
		return new String[] { "All" };
	}

	public String getName() {
		return "Export files to folder";
	}

	public boolean setup(Node node) {
		dialog = new ExportFilesOperationGUI(node);
		if (dialog.isCanceled())
			return false;
		if (dialog.outFolderPath.equalsIgnoreCase("")) {
			System.out.println("Select a folder");
			return false;
		}

		this.method = dialog.method;
		this.fileType = dialog.fileType;
		this.stringToMatch = dialog.matchingString;
		this.output = dialog.outFolderPath;
		this.sequenceName = dialog.sequenceName;
		return true;
	}

	public void finalize(Node node) {
		
		if(imp !=null){
			IJ.saveAsTiff(imp, output + File.separator + "ImageStack");
		}
		System.out
				.println("Operation fi// TODO Auto-generated method stubnalized");
		LogPanel.log("Files saved at : " + output);
	}

	public void visit(Root root) {
	}

	public void visit(Experiment experiment) {
		for (Sample sample : experiment.getSamples()) {
			visit(sample);
		}
	}

	@SuppressWarnings("static-access")
	private void run(Node node) {
		currentNode = node;

		File currentDir = new File(currentNode.getPath());
		boolean isImage = fileType.equalsIgnoreCase("image");
		File f = getMatchingFile(isImage, currentDir);

		if (method.equalsIgnoreCase("sequential")) {
			if (f != null && isImage) {
				currentCount++;
				String name = sequenceName + "_" + currentCount;
				IJ.saveAsTiff(IJ.openImage(f.getAbsolutePath()), output
						+ File.separator + name);
			} else if (f != null && !isImage) {
				if (f != null && isImage) {
					currentCount++;
					String name = sequenceName + "_" + currentCount;
					ResultsTable currentTable = Analyzer.getResultsTable();
					currentTable.open2(f.getAbsolutePath());
					currentTable.save(output + File.separator + name);
				}
			}
		} else {

			if (f != null && isImage) {
				currentCount++;
				String name = sequenceName + "_" + currentCount;
				ImagePlus currentImage = IJ.openImage(f.getAbsolutePath());
				currentImage.setTitle(currentNode.getSampleName() + "_"
						+ currentNode.getFieldOfViewName());
				appendImage(currentImage);

//			} 
			}
//			else if (f != null && !isImage) {
//				if (f != null && isImage) {
//					currentCount++;
//					ResultsTable currentTable = Analyzer.getResultsTable();
//					currentTable.open2(f.getAbsolutePath());
//					currentTable.save(output + File.separator + name);
//					addColums(currentTable);
//					appendResults(currentTable); // TODO Auto-generated method
//													// stub
//
//				}
			}
		}

	private void appendResults(ResultsTable currentTable) {

	}

	private void addColums(ResultsTable currentTable) {
		for (int i = 0; i < currentTable.getCounter(); i++) {
			currentTable.setValue("Experiment", i,
					currentNode.getExperimentName());
			currentTable.setValue("Sample", i, currentNode.getSampleName());
			currentTable.setValue("FieldOfView", i,
					currentNode.getFieldOfViewName());
		}

	}

	private void appendImage(ImagePlus currentImage) {
		ImageStack currentStack = currentImage.getStack();

		if (stack == null) {
			stack = currentStack;
			for (int i = 1; i <= stack.getSize(); i++) {
				stack.setSliceLabel(currentImage.getTitle() + "_" + i, i);
			}
		} else {
			for (int i = 1; i <= currentStack.getSize(); i++) {
				currentStack
						.setSliceLabel(currentImage.getTitle() + "_" + i, i);
				ImageProcessor ip = currentStack.getProcessor(i);
				stack.addSlice(ip);
			}
		}

	}

	private File getMatchingFile(boolean isImage, File currentDir) {
		ArrayList<File> matchingFiles = new ArrayList<File>();
		for (File file : currentDir.listFiles()) {
			if (file.isFile() || file.getName().contains(stringToMatch)) {
				matchingFiles.add(file);
			}
		}

		// check extension
		for (File file : matchingFiles) {
			String extension = getExtension(file);
			if (isImage && extension.equalsIgnoreCase("tif")
					|| extension.equalsIgnoreCase("tiff")) {
				return file;
			}

			if (!isImage && extension.equalsIgnoreCase("csv")) {

				return file;
			}

		}
		return null;
	}

	private String getExtension(File file) {
		String fileName = file.getAbsolutePath();
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
		return extension;
	}

	public static void showFiles(File[] files) {
		for (File file : files) {
			if (file.isDirectory()) {
				System.out.println("Directory: " + file.getName());
				showFiles(file.listFiles()); // Calls same method again.
			} else {
				System.out.println("File: " + file.getName());
			}
		}
	}

	public void visit(Sample sample) {
		for (FieldOfView fov : sample.getFieldOfView()) {
			visit(fov);
		}

	}

	public void visit(FieldOfView fieldOfView) {
		run(fieldOfView);
	}

	public void visit(FileNode fileNode) {
		run(fileNode);
	}

	public void visit(OperationNode operationNode) {
	}

	public Node[] getCreatedNodes() {
		return null;
	}

	public HashMap<String, String> getParameters() {
		return null;
	}

	public static void main(String[] args) {
		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 2);
		ExportFilesOperationGUI dialog = new ExportFilesOperationGUI(
				model.getRoot());

	}
}
