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

public enum RoiType {

	/** Cell ROI. */
	CELL,
	/** Background ROI. */
	BACKGROUND, 
	/** Red channel. */
	FRAP_REGION;
	public String toString() {
		switch (this) {
		case CELL:			return "Cell";
		case BACKGROUND:	return "Background";
		case FRAP_REGION:		return "Frap region";
		default:			return "Unspecified";
		}
	}

}
