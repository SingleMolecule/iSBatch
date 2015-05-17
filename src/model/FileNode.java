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
import model.parameters.Channel;
import operations.Operation;

// TODO: Auto-generated Javadoc
/**
 * The Class FileNode.
 */
public class FileNode extends Node implements FileInterface{
	
	private String channel = null;
	public static final String type = "File";
	private Channel channel1 = null;
	private ArrayList<String> tags = new ArrayList<String>();
	
	/**
	 * Instantiates a new file node.
	 *
	 * @param parent the parent
	 */
	public FileNode(Node parent) {
		super(parent, type);
	}

	
	/**
	 * Accept.
	 *
	 * @param operation the operation
	 */
	@Override
	public void accept(Operation operation) {
		operation.visit(this);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {

		String channel = getProperty("channel");

		if (channel == null || channel.isEmpty())
			return getProperty("name");
		else
			return String.format("[%s] %s", channel, getProperty("name"));
	}

	/**
	 * Gets the output folder.
	 *
	 * @return the output folder
	 */
	@Override
	public String getOutputFolder() {
		return getParent().getOutputFolder();
	}

	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public String getChannel() {
		String channel = getProperty("channel");
		return channel != null ? channel : "";
	}

	/**
	 * Gets the Field of View name.
	 *
	 * @return the Field of View name.
	 */
	public String getFoVName(){
		return this.getParent().getName();
		
	}

	/**
	 * Gets the number of fo v.
	 *
	 * @return the number of fo v
	 */
	@Override
	public int getNumberOfFoV() {
		return 0;
	}

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public ImagePlus getImage() {
		return IJ.openImage(this.getPath());
	}


	/**
	 * Gets the field of view.
	 *
	 * @return the field of view
	 */
	@Override
	public ArrayList<FieldOfView> getFieldOfView() {
				return null;
	}

	/**
	 * Gets the samples.
	 *
	 * @return the samples
	 */
	@Override
	public ArrayList<Sample> getSamples() {
		return null;
	}

	/**
	 * Count occurrences.
	 *
	 * @param haystack the haystack
	 * @param needle the needle
	 * @return the int
	 */
	public static int countOccurrences(String haystack, char needle)
	{
	    int count = 0;
	    for (int i=0; i < haystack.length(); i++)
	    {
	        if (haystack.charAt(i) == needle)
	        {
	             count++;
	        }
	    }
	    return count;
	}
	
	
	/**
	 * Gets the cell roi path.
	 *
	 * @return the cell roi path
	 */
	public String getCellROIPath() {
		if(this.getParent().getType() == FieldOfView.type){
			return this.getParent().getProperty("CellRoi");
		}
		return null;
	}
	
	/**
	 * Extracts the filename from the file path.
	 * 
	 * @return The filename of the file to which this file node refers.
	 */
	public String getFilename() {
		String path = getProperty("path");
		return path != null ? new File(path).getName() : "";
	}
	
	/**
	 * Extracts the file extension from the file path.
	 * 
	 * @return The extension of the file to which this file node refers.
	 */
	public String getExtension() {
		String filename = getFilename();
		return filename.contains(".") ? filename.substring(filename.indexOf(".")) : "";
	}
	
	/**
	 * Get all the tags that are assigned to this file node. A tag is defined as '_tagname'.
	 * A file node can have multiple tags, e.g. file_tag1_tag2_tag3.tif.
	 * 
	 * @return Tags that are assigned to this file node (which is specified by the filename)
	 */
	public ArrayList<String> getTags() {
	
//			
//		String filename = getFilename();
//			
//		Pattern pattern = Pattern.compile("(?<=_)[a-zA-Z0-9]+");
//		Matcher matcher = pattern.matcher(filename);
//	
//		while (matcher.find())
//			tags.add(matcher.group());
			
		return tags;
		
	}


	
}
