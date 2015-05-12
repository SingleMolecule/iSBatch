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

import operations.Operation;


// TODO: Auto-generated Javadoc
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

	/** The Constant type. */
	public static final String type = "Root";
	
	/**
	 * Instantiates a new root.
	 *
	 * @param outputFolder the output folder
	 */
	public Root(String outputFolder) {
		super(null, type);
		
		outputFolder += File.separator + "database_files";
		
		if (!new File(outputFolder).exists())
			new File(outputFolder).mkdirs();
		
		setProperty("name", "Database");
		setProperty("outputFolder", outputFolder);
	}

	/**
	 * Accept.
	 *
	 * @param operation the operation
	 */
	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}

	/**
	 * Gets the number of fo v.
	 *
	 * @return the number of fo v
	 */
	@Override
	public int getNumberOfFoV() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Gets the field of view.
	 *
	 * @return the field of view
	 */
	@Override
	public ArrayList<FieldOfView> getFieldOfView() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the samples.
	 *
	 * @return the samples
	 */
	@Override
	public ArrayList<Sample> getSamples() {
			// TODO Auto-generated method stub
		
		return null;
	}
	
	
}
