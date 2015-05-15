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

import java.io.File;
import java.util.ArrayList;

import model.parameters.NodeType;
import operations.Operation;


/**
 * The root class represents the root of all experiments.
 * A root node should have the following properties:
 * <dl>
 * <dt>path</dt><dd>the path where to store all output files</dd>
 * </dl>
 * 
 * @author C.M. Punter
 *
 */
public class Root extends Node {

	public static final String type = "Root";
	public static final NodeType nodeType = NodeType.ROOT;
	
	public Root(String outputFolder, String name) {
		super(null, type);
		
		outputFolder += File.separator + name + "_files";
		
		if (!new File(outputFolder).exists())
			new File(outputFolder).mkdirs();
		
		setProperty("name", "Database");
		setProperty("outputFolder", outputFolder);
	}
	
//	public Root(String outputFolder, String name) {
//		super(null, NodeType.ROOT);
//		
//		outputFolder += File.separator + name + "_files";
//		
//		if (!new File(outputFolder).exists())
//			new File(outputFolder).mkdirs();
//		
//		setProperty("name", "Database");
//		setProperty("outputFolder", outputFolder);
//	}
	

	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}

	@Override
	public int getNumberOfFoV() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<FieldOfView> getFieldOfView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Sample> getSamples() {
			// TODO Auto-generated method stub
		
		return null;
	}
	
	
}
