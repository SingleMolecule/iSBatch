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

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ij.IJ;
import ij.ImagePlus;
import operations.Operation;

public class FileNode extends Node implements FileInterface {

	public static final String type = "File";
	private ArrayList<String> tags = new ArrayList<String>();

	public static final String tagDivider = "_";
	
	public FileNode(Node parent) {
		super(parent, type);
	}

	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}
	@Override
	public String toString() {

		String channel = getProperty("channel");

		if (channel == null || channel.isEmpty())
			return getProperty("name");
		else
			return String.format("[%s] %s", channel, getProperty("name"));
	}

	@Override
	public String getOutputFolder() {
		return getParent().getOutputFolder();
	}

	public String getChannel() {
		String channel = getProperty("channel");
		return channel != null ? channel : "";
	}

	public String getFoVName() {
		return this.getParent().getName();
	}

	@Override
	public int getNumberOfFoV() {
		return 0;
	}

	public ImagePlus getImage() {
		return IJ.openImage(this.getPath());
	}

	@Override
	public ArrayList<FieldOfView> getFieldOfView() {
		return null;
	}

	@Override
	public ArrayList<Sample> getSamples() {
		return null;
	}

	public static int countOccurrences(String haystack, char needle) {
		int count = 0;
		for (int i = 0; i < haystack.length(); i++) {
			if (haystack.charAt(i) == needle) {
				count++;
			}
		}
		return count;
	}

	public String getCellROIPath() {
		if (this.getParent().getType() == FieldOfView.type) {
			return this.getParent().getProperty("CellRoi");
		}
		return null;
	}

	public String getFilename() {
		String path = getProperty("path");
		return path != null ? new File(path).getName() : "";
	}

	public String getExtension() {
		String filename = getFilename();
		return filename.contains(".") ? filename.substring(filename
				.indexOf(".")) : "";
	}

	/**
	 * Get all the tags that are assigned to this file node. A tag is defined as
	 * '_tagname'. A file node can have multiple tags, e.g.
	 * file_tag1_tag2_tag3.tif.
	 * 
	 * @return Tags that are assigned to this file node (which is specified by
	 *         the filename)
	 */
	public ArrayList<String> getTags() {
		
		 String filename = getFilename();
		
		 Pattern pattern = Pattern.compile("(?<=" + tagDivider + ")[a-zA-Z0-9]+");
		 Matcher matcher = pattern.matcher(filename);
		 while (matcher.find())
		 tags.add(matcher.group());
		return tags;

	}
	
	public static void main(String[] args) {
		String path = "c:/teste/514_flat.tif";
		String filename =debug(path); 
		System.out.println(filename);
		
		ArrayList<String> mytag = new ArrayList<String>();
		
		Pattern pattern = Pattern.compile("(?<=" + tagDivider + ")[a-zA-Z0-9]+");
		Matcher matcher = pattern.matcher(filename);
		
		System.out.println("Find");
		 while (matcher.find())
			mytag.add(matcher.group());
		System.out.println(mytag.size());
		 for(String string : mytag){
			 System.out.println(string);
		 }
	}
	public static String debug(String path) {
		return path != null ? new File(path).getName() : "";
	}
}
