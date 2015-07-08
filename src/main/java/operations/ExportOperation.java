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
import ij.plugin.ZProjector;
import ij.plugin.filter.Analyzer;
import ij.process.ImageProcessor;
import imageOperations.NodeToImageStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTextField;

import filters.GenericFilter;
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
import operations.flatImages.ExportFilesGUI;
import test.TreeGenerator;

public class ExportOperation implements Operation {
	ExportFilesGUI dialog;
	private Object method;
	private String imagePath = "";
	private String OUTPUT_FOLDER;
	private Object fileType;
	private String searchText;
	private String exportName;
	private ImagePlus imp;
	private ResultsTable table;
	private ImageStack stack;
	int savingIndex =1;
	public ExportOperation(DatabaseModel treeModel) {
		
	}


	public String[] getContext() {
		return new String[] { "All" };
	}

	
	public String getName() {
		return "Export data";
	}

	
	public boolean setup(Node node) {
		dialog = new ExportFilesGUI(node);
		if (dialog.isCanceled())
			return false;
			
		this.method = dialog.saveType;
		this.fileType = dialog.fileType;
		this.searchText = dialog.fileNameContains.getText();
		if(dialog.pathToFolder.getText().equalsIgnoreCase("")){
			System.out.println("No output folder selected");
			return false;
		}
		this.OUTPUT_FOLDER = dialog.pathToFolder.getText();
		this.exportName = dialog.exportNameFile;
		
		printParameters();
		
		return true;
	}

	
	private void printParameters() {
		System.out.println("Method : " + method.toString()); 
		System.out.println("File type : "+ fileType.toString());
		System.out.println("Search text " + searchText);
		System.out.println("Ouput " + OUTPUT_FOLDER);
		System.out.println("Export name : " + exportName);
		
	}


	public void finalize(Node node) {
		LogPanel.log("All files saved at : "+ OUTPUT_FOLDER+ "");
	}

	
	public void visit(Root root) {
	}

	
	public void visit(Experiment experiment) {
		for(Sample sample : experiment.getSamples()){
			visit(sample);
		}

	}

	private void run(Node node) {
		//check what file type and get the select files
		
		if(fileType.toString().equalsIgnoreCase("Image")){
			ImagePlus currentImp = getMatchingImagePlus(node,searchText );
			currentImp.setTitle(node.getSampleName() +"_"+ node.getName());
			
			if(method.toString().equalsIgnoreCase("Sequential")){
				//getFileName
				String fileName = exportName+ "_"+savingIndex+".tif";
				savingIndex++;
				IJ.saveAsTiff(currentImp,OUTPUT_FOLDER+File.separator+fileName);
				
			}
			else{
				IJ.log("This operation is too slow. Function available in a future release.");
				IJ.log("If this is crucial for your work, please report as a issue to speed development");
//				appendToImage(currentImp);
				
			}
			
		}
		
		if(fileType.toString().equalsIgnoreCase("Table")){
			ResultsTable currentTable = getMatchingResultsTable(node, searchText);
			addExtraColumns(currentTable,node);
		
			if(method.toString().equalsIgnoreCase("Sequential")){
				String fileName = exportName+ "_"+savingIndex+".csv";
				savingIndex++;
				try {
					currentTable.saveAs(OUTPUT_FOLDER+File.separator+fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				//
				IJ.log("This operation is too slow. Function available in a future release.");
				IJ.log("If this is crucial for your work, please report as a issue to speed development");
//				appendToTable(currentTable);
				
			}
			
			
		}
		
		
	}


	
	private void appendToTable(ResultsTable currentTable) {
		for(int i=0; i<currentTable.getCounter(); i++){
		}
		
	}


	private void appendToImage(ImagePlus currentImp) {
		ImageStack currentStack = currentImp.getStack();
		stack = imp.getImageStack();
	
		for(int i = 1; i<=currentStack.getSize(); i++){
			stack.addSlice(currentStack.getProcessor(i));
		}
		
		
	}


	private void addExtraColumns(ResultsTable currentTable, Node node) {
		for(int i =0; i<currentTable.getCounter(); i++){
			currentTable.setValue("Experiment", i, node.getExperimentName());
			currentTable.setValue("Sample", i, node.getSampleName());
			currentTable.setValue("FoV", i, node.getFieldOfViewName());
		}
		
	}



	private ImagePlus getMatchingImagePlus(Node node, String searchText2) {
		File f = getMatchingFile(node, searchText2, "tif");
		return IJ.openImage(f.getAbsolutePath());
	}


	private File getMatchingFile(Node node, String searchText2, String string) {
		File folder = new File(node.getPath());
		File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		    	  if(getExtension(listOfFiles[i]).equalsIgnoreCase(string)){
		    		  
		    		  if(listOfFiles[i].getName().contains(searchText2)){
		    			  return listOfFiles[i];
		    		  }
		    		  
		    	  }
		      } 
		    }
		return null;
	}


	private String getExtension(File file) {
		String str = file.getAbsolutePath();
		String[] bits = str.split("-");
		String lastOne = bits[bits.length-1];
		return lastOne;
	}


	private ResultsTable getMatchingResultsTable(Node node, String searchText2) {
		File f = getMatchingFile(node, searchText2, "csv");
		return ResultsTable.open2(f.getAbsolutePath());
	}
	

	private void exportTable() {
		// TODO Auto-generated method stub
		
	}


	private void exportImage() {
		// TODO Auto-generated method stub
		
	}


	public void visit(Sample sample) {
		for(FieldOfView fov : sample.getFieldOfView()){
			visit(fov);
		}

	}

	
	public void visit(FieldOfView fieldOfView) {
			run(fieldOfView);
	}

	
	public void visit(FileNode fileNode) {
		
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
		
		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 1);
		ExportOperation export = new ExportOperation(model);
		export.setup(null);
		//ExportFilesGUI dialog = new ExportFilesGUI(model.getRoot());
		
		

	}
}
