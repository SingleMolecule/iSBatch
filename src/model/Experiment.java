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

import filters.NodeFilter;
import operations.Operation;

/**
 * The experiment class represents an experiment which contains samples, field
 * of views and files. An experiment can have samples, channels and file nodes
 * as children. An experiment node should have the following properties:
 * <dl>
 * <dt>path</dt>
 * <dd>the root path of the experiment (this is optional). This is the folder
 * where all the raw measurement files come from.</dd>
 * <dt>name</dt>
 * <dd>the descriptive name for this experiment (usually the name of the root
 * folder)</dd>
 * </dl>
 * 
 * @author C.M. Punter
 *
 */
public class Experiment extends Node {

	public static final String type = "Experiment";

	public Experiment(Root parent) {
		super(parent, type);
	}

	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}

	public ArrayList<Sample> getSamples() {
		ArrayList<Node> nodes = this.getChildren(new NodeFilter(Sample.type));

		// convert to sample array
		ArrayList<Sample> samples = new ArrayList<Sample>();
		for (Node node : nodes) {
			samples.add((Sample) node);
		}
		return samples;
	}

	@Override
	public int getNumberOfFoV() {
		int total = 0;
		for (Sample sample : this.getSamples()) {
			total += sample.getNumberOfFoV();
		}
		return total;
	}

	@Override
	public ArrayList<FieldOfView> getFieldOfView() {
		ArrayList<FieldOfView> fovs = new ArrayList<FieldOfView>();
		for (Sample sample : this.getSamples()) {
			fovs.addAll(sample.getFieldOfView());
		}
		return fovs;
	}

	public String getBeamProfile(String channel) {
		return this.getProperty(channel + "_BeamProfile");
	}

	public String getExperimentName() {
		return this.getName();
	}

}
