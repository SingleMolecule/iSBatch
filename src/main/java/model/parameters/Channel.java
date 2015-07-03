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
package model.parameters;

public enum Channel {

	/** Green channel. */
	GREEN,
	/** Blue channel. */
	BLUE, 
	/** Red channel. */
	RED, 
	/** Bright field channel. */
	BRIGHT_FIELD,
	/** Acquisition channel. */
	ACQUISITION;

	public String toString() {
		switch (this) {
		case GREEN:			return "Green";
		case BLUE:			return "Blue";
		case RED:			return "Red";
		case BRIGHT_FIELD: 	return "Bright Field";
		default:			return "Acquisition";
		}
	}
	

}
