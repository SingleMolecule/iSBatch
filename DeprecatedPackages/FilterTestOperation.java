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
package iSBatch;


import java.util.ArrayList;

import filters.GenericFilter;
import gui.LogPanel;

import java.util.HashMap;

import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import operations.Operation;

public class FilterTestOperation implements Operation {
	private FilterTestOperationGUI dialog;
	private String channel;
	private String customFilter;
	private Object imageType;
	private ArrayList<String> imageTag;
	
	public FilterTestOperation(DatabaseModel treeModel) {
		
	}

	public String[] getContext() {
		return new String[] { "All" };
	}

	@Override
	public String getName() {
		return "Filter Debug";
	}

	@Override
	public boolean setup(Node node) {
		dialog = new FilterTestOperationGUI(node);
		if (dialog.isCanceled())
			return false;

		// Get information from the dialog
		// From panel1
		this.channel = dialog.getChannel();
		this.imageType = dialog.getImageType();
		this.customFilter = dialog.getCustomFilter();
		this.imageTag = dialog.getImageTag();
		// From panel 2
		return true;
	}

	public void finalize(Node node) {
		System.out.println("Filter Ouput end.");
	}

	public void visit(Root root) {
	}

	public void visit(Experiment experiment) {
		System.out.println(experiment.getProperty("type"));
		run(experiment);
	}

	private void run(Node node) {


			getStackForMT(node);
		}



	private void getStackForMT(Node node) {
		System.out.println("--- Start ----");
		
//		ArrayList<Node> nodes = node.getDescendents(filter(channel));
		ArrayList<Node> filenodes = node.getDescendents(new GenericFilter(
				channel, imageTag, null, null));
		
		if(filenodes.size()==0){
			LogPanel.log("No files found.");
		}
		
		System.out.println("Filters to use");
		System.out.println("Channel: " + channel);
		System.out.println("Type: " + imageType);
		System.out.println("Custom filter" + customFilter);

		for(Node node2 : filenodes){
			LogPanel.log("Selected: " + node2.getPath());
			System.out.println("Selected: " + node2.getPath());
		}

	}

//	private int getStackSize(ArrayList<Mesh> meshes) {
//		int size = 0;
//		for (Mesh mesh : meshes) {
//			if (mesh.getSlice() >= size) {
//				size = mesh.getCell();
//			}
//		}
//		return size;
//	}


	@Override
	public void visit(Sample sample) {
		run(sample);
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		run(fieldOfView);
	}

	/**
	 * Visit.
	 *
	 * @param fileNode
	 *            the file node
	 */
	@Override
	public void visit(FileNode fileNode) {
		run(fileNode);
	}

	public static void main(String[] args) {

	
//		DatabaseModel model = TreeGenerator.generate("e:/test", "e:/test", 4);
//		FilterTestOperationGUI dialog = new FilterTestOperationGUI(model.getRoot());
//		System.out.println(dialog.getChannel());
//		
//		for(String string : dialog.getImageTag()){
//			System.out.println("Tag: " + string);
//		}
	}
	@Override
	public void visit(OperationNode operationNode) {
	}


	@Override
	public Node[] getCreatedNodes() {
		return null;
	}

	@Override
	public HashMap<String, String> getParameters() {
		return null;
	}

}
