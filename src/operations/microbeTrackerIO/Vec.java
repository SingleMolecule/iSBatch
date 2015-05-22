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
package operations.microbeTrackerIO;

// TODO: Auto-generated Javadoc
/**
 * The Class Vec.
 */
public class Vec {

	/** The u. */
	public double u;
	
	/** The v. */
	public double v;

	/**
	 * Instantiates a new vec.
	 *
	 * @param u the u
	 * @param v the v
	 */
	public Vec(double u, double v) {
		this.u = u;
		this.v = v;
	}
	
	/**
	 * Length.
	 *
	 * @return the double
	 */
	public double length() {
		return Math.sqrt(u * u + v * v);
	}
	
	/**
	 * Normal.
	 *
	 * @return the vec
	 */
	public Vec normal() {
		return new Vec(v, -u);
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return String.format("[%f, %f]", u, v);
	}
	
}
