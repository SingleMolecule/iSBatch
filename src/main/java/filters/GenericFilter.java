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
package filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import model.FileNode;
import model.Node;

public class GenericFilter implements NodeFilterInterface {

	private String channel;
	private ArrayList<String> tags;
	private String custom;

	public GenericFilter(String channel, String custom) {
		this.channel = channel;

		this.custom = custom;
		if (custom == null) {
			this.custom = "raw";
		}
	}

	public GenericFilter(String channel, ArrayList<String> tags,
			String extension, String custom) {
		this.channel = channel;
		this.tags = tags;
		this.custom = custom;

	}

	@Override
	public boolean accept(Node node) {
		// System.out.println("1 --- " + node.getType() + "|" +
		// node.getChannel()
		// + "|" + node.getName());
		if (!node.getType().equalsIgnoreCase(FileNode.type)) {
			// System.out.println("Not the rigth type");
			return false;
		}

		boolean isChannel = false;
		if (channel == null || channel.equalsIgnoreCase("All")
				|| node.getChannel().equalsIgnoreCase(channel)) {
			isChannel = true;
		}

		boolean matchTag = false;
		if (tags.isEmpty() || tags == null || tags.size() == 0) {
			System.out.println("Tag is empty!");
			matchTag = true;
		} else {

			FileNode fNode = (FileNode) node;
			// System.out.println("  |--- " + fNode.getType() + "|"
			// + fNode.getChannel() + "|" + fNode.getName() + "|"
			// + fNode.getTags().size() + "|" + "|" + tags.size());
			if (fNode.getTags().size() == 0
					&& tags.get(0).equalsIgnoreCase("Raw")) {
				// System.out.println("Tag size is 1. Tag is" + tags.get(0));
				matchTag = true;
			} else {
				if (equalLists(fNode.getTags(), tags)) {
					// System.out.println("String comparison.");
					matchTag = true;
				}
			}
		}

		boolean containsCustomTag = false;
		if (custom == null || custom.equalsIgnoreCase("")) {
			containsCustomTag = true;
		}
		if (!(custom == null)) {
			if (node.getName().contains(custom)) {
				containsCustomTag = true;

			}
		}
		if (isChannel && matchTag && containsCustomTag) {
			return true;
		}

		return false;
	};

	public static boolean equalLists(ArrayList<String> one,
			ArrayList<String> two) {

		HashSet<String> hOne = new HashSet<String>();
		HashSet<String> hTwo = new HashSet<String>();

		if (one == null && two == null) {
			return true;
		}

		for (String string : one) {
			hOne.add(string.toUpperCase());
		}
		for (String string : two) {
			hTwo.add(string.toUpperCase());
		}

		if ((hOne == null && hTwo != null) || hOne != null && hTwo == null
				|| hOne.size() != hTwo.size()) {
			return false;
		}
		System.out.println("Same size");

		one = new ArrayList<String>(hOne);
		two = new ArrayList<String>(hTwo);

		Collections.sort(one);
		Collections.sort(two);
		return one.equals(two);
	}
};
