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
 *  Categories of Regions of interest (ROI).
 *  <li>{@link #CELL}</li>
 *  <li>{@link #BACKGROUND}</li>
 *  <li>{@link #FRAP_REGION}</li>
 */
public enum RoiType {

	/** Cell ROI. */
	CELL,
	/** Background ROI. */
	BACKGROUND, 
	/** Red channel. */
	FRAP_REGION;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		switch (this) {
		case CELL:			return "Cell";
		case BACKGROUND:	return "Background";
		case FRAP_REGION:		return "Frap region";
		default:			return "Unspecified";
		}
	}

}
