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
import java.util.HashMap;

import context.ContextElement;
import filters.NodeFilterInterface;
import operations.OperationElement;

// TODO: Auto-generated Javadoc
/**
 * The Class Node.
 */
public abstract class Node implements OperationElement, ContextElement,
		NodeInterface {

	/** The metadata. */
	private Metadata metadata;
	
	/** The parent. */
	private Node parent;

	/** The type. */
	private String type;

	/** The properties. */
	private HashMap<String, String> properties = new HashMap<String, String>();

	/** The children. */
	private ArrayList<Node> children = new ArrayList<Node>();

	/** The file. */
	private File file;

	/**
	 * Gets the cell roi path.
	 *
	 * @return the cell roi path
	 */
	public String getCellROIPath() {
		return this.getProperty("CellRoi");
	}

	/**
	 * Sets the cell roi path.
	 *
	 * @param cellROIPath
	 *            the new cell roi path
	 */
	public void setCellROIPath(String cellROIPath) {
		this.getProperties().put("CellRoi", cellROIPath);
	}

	/**
	 * Instantiates a new node.
	 *
	 * @param parent
	 *            the parent
	 * @param type
	 *            the type
	 */
	public Node(Node parent, String type) {
		super();
		this.parent = parent;
		this.type = type;
	}
	
	/**
	 * Instantiates a new node.
	 *
	 * @param parent the parent
	 * @param type the type
	 * @param metadata the metadata
	 */
	public Node(Node parent, String type, Metadata metadata){
		super();
		this.parent = parent;
		this.type = type;
		this.metadata = metadata;
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * Sets the parent.
	 *
	 * @param parent
	 *            the new parent
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {

		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type
	 *            the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public HashMap<String, String> getProperties() {
		return properties;
	}

	/**
	 * Gets the property.
	 *
	 * @param name
	 *            the name
	 * @return the property
	 */
	public String getProperty(String name) {
		return properties.get(name);
	}

	/**
	 * Sets the property.
	 *
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public void setProperty(String name, String value) {
		properties.put(name, value);
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<Node> getChildren() {
		return children;
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	@Override
	public String[] getContext() {
		return new String[] { type, "All" };
	}

	/**
	 * Gets the children.
	 *
	 * @param filter
	 *            the filter
	 * @return the children
	 */
	public ArrayList<Node> getChildren(NodeFilterInterface filter) {

		ArrayList<Node> filteredChildren = new ArrayList<Node>();

		for (Node child : getChildren()) {
			try {
				if (filter.accept(child))
					filteredChildren.add(child);
			} catch (NullPointerException e) {
				// Do nothing
				// System.out.println("Node ignored");
			}

		}

		return filteredChildren;

	}

	/**
	 * Gets the descendents.
	 *
	 * @param filter
	 *            the filter
	 * @return the descendents
	 */
	public ArrayList<Node> getDescendents(NodeFilterInterface filter) {

		ArrayList<Node> descendents = getChildren(filter);

		for (Node child : getChildren())
			descendents.addAll(child.getDescendents(filter));

		return descendents;

	}

	/**
	 * Gets the output folder.
	 *
	 * @return the output folder
	 */
	public String getOutputFolder() {

		String outputFolder = getProperty("outputFolder");

		if (outputFolder == null) {

			String parentOutputFolder = parent.getOutputFolder();
			outputFolder = parentOutputFolder + File.separator
					+ getProperty("name");

			if (new File(outputFolder).exists()) {

				int i = 2;

				while (new File(outputFolder + i).exists())
					i++;
			}

			new File(outputFolder).mkdir();

			setProperty("outputFolder", outputFolder);
		}

		return outputFolder;

	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return getProperty("name");
	}

	//
	// public String getPath() {
	// String path = null;
	// if(!this.getClass().toString().equalsIgnoreCase("model.FileNode")){
	// path = this.getProperty("path");
	// System.out.println("this is a file Node");
	// System.out.println(path);
	// }
	// else {
	// path = this.getProperty("folder");
	// System.out.println("This will return folder");
	// System.out.println(path);
	// }
	// return path;
	// }
	//
	/**
	 * Gets the parent folder.
	 *
	 * @return the parent folder
	 */
	public String getParentFolder() {
		Node parent = this.getParent();
		return parent.getFolder();
	}

	/**
	 * Gets the folder.
	 *
	 * @return the folder
	 */
	public String getFolder() {
		return this.getProperty("folder");
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return this.getProperty("path");
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.getProperty("name");
	}

	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public String getChannel() {
		return this.getProperty("channel");
	}

	/**
	 * Gets the beam profile.
	 *
	 * @param channel
	 *            the channel
	 * @return the beam profile
	 */
	public String getBeamProfile(String channel) {
		return this.getProperty(channel + "_BeamProfile");
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		if (file == null) {
			this.file = new File(getPath());
		}
		return file;
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	public ArrayList<String> getTag() {
		System.out.println("does not apply to this level.");
		return null;
	}

	/**
	 * Gets the experiment name.
	 *
	 * @return the experiment name
	 */
	public String getExperimentName() {

		return this.getParent().getExperimentName();
	}

	/**
	 * Gets the sample name.
	 *
	 * @return the sample name
	 */
	public String getSampleName() {

		return this.getParent().getSampleName();
	}

	/**
	 * Gets the field of view name.
	 *
	 * @return the field of view name
	 */
	public String getFieldOfViewName() {

		return this.getParent().getFieldOfViewName();
	}

	/**
	 * Gets the metadata.
	 *
	 * @return the metadata
	 */
	public Metadata getMetadata(){
		return metadata;
	}
}
