/*
 * 				iSBatch  Copyright (C) 2015  
 *		Victor E. A. Caldas -  v.e.a.caldas at rug.nl
 *		C. Michiel Punter - c.m.punter at rug.nl
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package model;

import java.util.ArrayList;
import java.util.Map.Entry;

// TODO: Auto-generated Javadoc
/**
 * The Class MacroModel.
 */
public class MacroModel {

	/** The root. */
	public static Node root;

	/**
	 * Gets the type.
	 *
	 * @param hash the hash
	 * @return the type
	 */
	public static String getType(String hash) {
		Node node = findNode(root, Integer.parseInt(hash));
		
		if (node == null)
			return "";
		
		return node.getType();
	}
	
	/**
	 * Gets the properties.
	 *
	 * @param hash the hash
	 * @return the properties
	 */
	public static String getProperties(String hash) {
		
		String str = "";
		Node node = findNode(root, Integer.parseInt(hash));

		if (node == null) return "";
		
		for (Entry<String, String> entry : node.getProperties().entrySet()) {

			String key = entry.getKey();
			String value = entry.getValue();

			key = key.replaceAll("\n", " ");
			value = value.replaceAll("\n", " ");

			str += entry.getKey() + "=" + entry.getValue() + "\n";
		}

		return str;
	}

	/**
	 * Gets the children.
	 *
	 * @param hash the hash
	 * @return the children
	 */
	public static String getChildren(String hash) {
		String str = "";

		Node node = findNode(root, Integer.parseInt(hash));

		if (node != null) {
			
			ArrayList<Node> children = node.getChildren();
			
			if (children != null) {
				for (Node child : children)
					str += Integer.toString(child.hashCode()) + "\n";
			}
		}

		return str;
	}
	
	/**
	 * Gets the parent.
	 *
	 * @param hash the hash
	 * @return the parent
	 */
	public static String getParent(String hash) {
		Node node = findNode(root, Integer.parseInt(hash));
		return Integer.toString(node.getParent() == null ? root.hashCode() : node.getParent().hashCode());
	}

	/**
	 * Find node.
	 *
	 * @param node the node
	 * @param hashCode the hash code
	 * @return the node
	 */
	public static Node findNode(Node node, int hashCode) {
		
		if (node.hashCode() == hashCode)
			return node;
		else {
			for (Node child : node.getChildren()) {
				Node n = findNode(child, hashCode);
				
				if (n != null)
					return n;
			}
		}

		return null;
	}
	
}
