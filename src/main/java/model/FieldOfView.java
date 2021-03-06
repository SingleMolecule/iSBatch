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
package model;

import ij.IJ;

import java.util.ArrayList;

import filters.ChannelFilter;
import filters.NodeFilter;
import operations.Operation;

public class FieldOfView extends Node {

	public static final String type = "FieldOfView";
	public FieldOfView(Sample parent) {
		super(parent, type);
	}
	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}
	
	public String getExperimentType(){
		return this.getParent().getParent().getProperty("type");
	}

	public  ArrayList<FileNode> getImages() {
		ArrayList<Node> nodes = this.getChildren(new NodeFilter(FieldOfView.type));
		
		//convert to sample array
		ArrayList<FileNode> filesNodes = new ArrayList<FileNode>();
		for(Node node : nodes){
			filesNodes.add((FileNode) node);
					}
		return filesNodes;		
		
	}

	public  ArrayList<FileNode> getImages(String channel) {
		ArrayList<Node> nodes = this.getChildren(new ChannelFilter(channel));
		System.out.println(nodes.size());
		//convert to sample array
		ArrayList<FileNode> filesNodes = new ArrayList<FileNode>();
		for(Node node : nodes){
			FileNode thisNode = (FileNode)node;
//			if(thisNode.getChannel().equalsIgnoreCase(channel))
//			{
				filesNodes.add(thisNode);
//			}
		}
		return filesNodes;		
		
	}

	@Override
	public int getNumberOfFoV() {
		return 1;
	}
	
	

//	public String getCellularROIs() {
//		if(cellRoiPath!=null){
//			return cellRoiPath;
//		}
//		else {
//			//Try to find it on the disk
//			File temp = new File(this.getPath()+ File.separator + "cellRois.zip");
//			if(temp.exists()){
//				this.cellRoiPath = temp.getAbsolutePath();
//				return cellRoiPath;
//			}
//		}
//		return null;
//	}

@SuppressWarnings("null")
	@Override
	public ArrayList<FieldOfView> getFieldOfView() {
		ArrayList<FieldOfView> fovs = null;
		fovs.add(this);
		return fovs;
	}

	@Override
	public ArrayList<Sample> getSamples() {
		 ArrayList<Sample> sample  = new ArrayList<Sample>();
		 sample.add((Sample) this.getParent());
		 IJ.log("You are asking a Sample from a FoV. Optmize the code and use node.getParent()");
		return sample;
	}
	
	
	public String getBeamProfile(String channel){
		if(this.getProperty(channel+"_BeamProfile")== null){
			return this.getParent().getBeamProfile(channel);
		}
		return this.getProperty(channel+"_BeamProfile");
	}

	public String getExperimentName(){
		return this.getParent().getParent().getName();
	}
	
	public String getSampleName(){
		return this.getParent().getName();
	}
	
	public String getFieldOfViewName(){
		return this.getName();
	}
	
	
}
