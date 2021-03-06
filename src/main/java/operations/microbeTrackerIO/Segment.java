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
 * The Class Segment.
 */
public class Segment extends Line {

	/** The length offset. */
	private double lengthOffset = 0;
	
	/**
	 * Instantiates a new segment.
	 *
	 * @param l1 the l1
	 * @param l2 the l2
	 */
	public Segment(Line l1, Line l2) {
		super(l1.center(), l2.center());
	}
	
	/**
	 * Distance to.
	 *
	 * @param p the p
	 * @return the double
	 */
	public double distanceTo(Point p) {
		return center().distanceTo(p);
	}
	
	/**
	 * Projection of.
	 *
	 * @param p the p
	 * @return the point
	 */
	public Point projectionOf(Point p) {
		Point i = intersection(new Line(p, this.normal()));
		double orientation = p.isLeftOf(this) ? 1 : -1;
		
		return new Point(i.distanceTo(orig) + lengthOffset, i.distanceTo(p) * orientation);
	}
	
	/**
	 * Sets the length offset.
	 *
	 * @param lengthOffset the new length offset
	 */
	public void setLengthOffset(double lengthOffset) {
		this.lengthOffset = lengthOffset;
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Segment s = new Segment(new Line(new Point(0,0), new Point(0, 10)), new Line(new Point(10, 10), new Point(10, 20)));
		Point p = new Point(5, 15);
		
		System.out.println(s.projectionOf(p));
	}
}
