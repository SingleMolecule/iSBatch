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

import java.util.ArrayList;

import operations.Operation;

public class Sample extends Node {

	public static final String type = "Sample";
	
	public Sample(Experiment parent) {
		super(parent, type);
	}

	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}
	
	public String getExperimentType(){
		return this.getParent().getProperty("type");
	}

	public  ArrayList<FieldOfView> getFieldOfView() {
		ArrayList<Node> nodes = this.getChildren();
		
		//convert to sample array
		ArrayList<FieldOfView> fov = new ArrayList<FieldOfView>();
		for(Node node : nodes){
			fov.add((FieldOfView) node);
			}
		return fov;
	}

	@Override
	public int getNumberOfFoV() {
		return this.getChildren().size();
	}
	@SuppressWarnings("null")
	@Override
	public ArrayList<Sample> getSamples() {
		ArrayList<Sample> samples = null;
		samples.add(this);
		return samples;
	}

	public String getBeamProfile(String channel){
		if(this.getProperty(channel+"_BeamProfile")== null){
			return this.getParent().getBeamProfile(channel);
		}
		return this.getProperty(channel+"_BeamProfile");
	}

	public String getExperimentName(){
		return this.getParent().getName();
	}

	public String getSampleName(){
		return this.getName();
	}
	
}
