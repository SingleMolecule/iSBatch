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
package operations;

import java.util.HashMap;

import context.ContextElement;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
public interface Operation extends ContextElement {
	
	String name = null; 
	
	public String getName();
	
	public boolean setup(Node node);
	public void finalize(Node node);
	public Node[] getCreatedNodes();
	public HashMap<String, String> getParameters();
	public void visit(Root root);
	public void visit(Experiment experiment);
	public void visit(Sample sample);
	public void visit(FieldOfView fieldOfView);
	public void visit(FileNode fileNode);
	public void visit(OperationNode operationNode);
	
}
