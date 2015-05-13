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

// TODO: Auto-generated Javadoc
/**
 *  Types of Results Table
 *  <li>{@link #TRACK}</li>
 *  <li>{@link #TRACE}</li>
 *  <li>{@link #PEAK}</li>
 *  <li>{@link #BRIGHT_FIELD}</li>.
 */
public enum TableType {

	/**  Track table. */
	TRACK,
	/** TRACE table. */
	TRACE, 
	/** PEAK table. */
	PEAK;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		switch (this) {
		case TRACK:	return "Track";
		case TRACE:	return "Trace";
		case PEAK:	return "Peak";
		default:	return "Generic";
		}
	}

}
