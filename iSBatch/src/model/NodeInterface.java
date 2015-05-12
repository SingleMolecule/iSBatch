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

// TODO: Auto-generated Javadoc
/**
 * The Interface NodeInterface.
 */
public interface NodeInterface {
	
	/**
	 * Gets the number of fo v.
	 *
	 * @return the number of fo v
	 */
	public int getNumberOfFoV();
	
	/**
	 * Gets the field of view.
	 *
	 * @return the field of view
	 */
	public ArrayList<FieldOfView> getFieldOfView();
	
	/**
	 * Gets the samples.
	 *
	 * @return the samples
	 */
	public ArrayList<Sample> getSamples();
}
