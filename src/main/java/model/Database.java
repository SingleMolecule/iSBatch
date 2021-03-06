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
import java.util.Map.Entry;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import gui.LogPanel;

public class Database {

	private SqlJetDb database;
	private File file;

	public Database(File file) throws SqlJetException {
		this.file = file;

		if (!file.exists()) {
			database = SqlJetDb.open(file, true);
			// createTablesFromFile();
			createTables();

		}

		database = SqlJetDb.open(file, true);

	}

	/**
	 * Creates the tables from file.
	 *
	 * @throws SqlJetException
	 *             the sql jet exception
	 */
	// private void createTablesFromFile() throws SqlJetException {
	//
	// database.getOptions().setAutovacuum(true);
	// database.beginTransaction(SqlJetTransactionMode.WRITE);
	// database.getOptions().setUserVersion(1);
	// File test = new File("src//model//template");
	//
	// SQLReader reader = new SQLReader();
	// ArrayList<String> listOfQueries = reader.createQueries(test
	// .getAbsolutePath());
	//
	// for (String string : listOfQueries) {
	//
	// if (string.contains("CREATE TABLE")) {
	// System.out.println("Creating table");
	// database.createTable(string);
	//
	// }
	// if (string.contains("CREATE INDEX")) {
	// System.out.println("Creating index");
	// database.createIndex(string);
	//
	// } else {
	// System.out.println(string);
	// }
	//
	// }
	// database.commit();
	//
	// }

	public static void main(String[] args) {

	}

	public void createTables() throws SqlJetException {

		database.getOptions().setAutovacuum(true);
		database.beginTransaction(SqlJetTransactionMode.WRITE);
		database.getOptions().setUserVersion(1);

		String sql = "create table nodes ( " + "parent integer, " + "type text)";

		database.createTable(sql);

		sql = "create index parent_index on nodes(parent)";

		database.createIndex(sql);

		sql = "create table node_properties ( " + "node integer, " + "name text, " + "value text)";

		database.createTable(sql);

		sql = "create index node_index on node_properties(node)";

		database.createIndex(sql);
		database.commit();

	}

	public void write(Node root) throws SqlJetException {
		database.beginTransaction(SqlJetTransactionMode.WRITE);

		database.getTable("nodes").clear();
		database.getTable("node_properties").clear();
		write(root, -1);
		database.commit();
		LogPanel.log("Database saved.");
	}

	public void write(Node node, long parentId) throws SqlJetException {

		ISqlJetTable table = database.getTable("nodes");

		String type = node.getType();
		long id = table.insert(parentId, type);

		table = database.getTable("node_properties");

		for (Entry<String, String> property : node.getProperties().entrySet())
			table.insert(id, property.getKey(), property.getValue());

		for (Node child : node.getChildren())
			write(child, id);

	}

	public Node getRoot() throws SqlJetException {

		database.beginTransaction(SqlJetTransactionMode.READ_ONLY);

		ISqlJetTable table = database.getTable("nodes");
		ISqlJetCursor cursor = table.lookup("parent_index", -1);

		if (cursor.getRowCount() == 0)
			return createNode(null, Root.type);

		long id = cursor.getRowId();
		String type = cursor.getString("type");

		Node node = createNode(null, type);
		read(node, id);

		return node;
	}

	public void read(Node parentNode, long parentId) throws SqlJetException {

		ISqlJetTable table = database.getTable("nodes");
		ISqlJetCursor cursor = table.lookup("parent_index", parentId);

		while (!cursor.eof()) {

			// read node
			long id = cursor.getRowId();
			String type = cursor.getString("type");
			Node node = createNode(parentNode, type);
			node.setProperty("type", type);

			// read properties
			table = database.getTable("node_properties");
			ISqlJetCursor propertyCursor = table.lookup("node_index", id);

			while (!propertyCursor.eof()) {
				String name = propertyCursor.getString("name");
				String value = propertyCursor.getString("value");
				node.getProperties().put(name, value);
				propertyCursor.next();
			}

			// read children
			read(node, id);

			parentNode.getChildren().add(node);
			cursor.next();
		}

	}

	// Java 1.7+ version
	// public Node createNode(Node parent, String type) {
	//
	// switch (type) {
	// case Root.type:
	// return new Root(database.getFile().getParent());
	// case Experiment.type:
	// return new Experiment((Root) parent);
	// case Sample.type:
	// return new Sample((Experiment) parent);
	// case FieldOfView.type:
	// return new FieldOfView((Sample) parent);
	// case FileNode.type:
	// return new FileNode(parent);
	// }
	//
	// return null;
	// }
	//

	public Node createNode(Node parent, String type) {
		if (type.equalsIgnoreCase(Root.type)) {
			return new Root(database.getFile().getParent(), file.getName());
		}

		else if (type.equalsIgnoreCase(Experiment.type)) {
			return new Experiment((Root) parent);
		}

		else if (type.equalsIgnoreCase(Sample.type)) {
			return new Sample((Experiment) parent);
		}

		else if (type.equalsIgnoreCase(FieldOfView.type)) {
			return new FieldOfView((Sample) parent);
		} else if (type.equalsIgnoreCase(FileNode.type)) {
			return new FileNode(parent);
		} else if (type.equalsIgnoreCase(OperationNode.type)) {
			return new OperationNode(parent);
		}

		return null;
	}

	// public Node createNode(Node parent, NodeType nodeType) {
	// if(nodeType == NodeType.ROOT) {
	// return new Root(database.getFile().getParent(),file.getName());}
	//
	// else if(nodeType == NodeType.EXPERIMENT) {
	// return new Experiment((Root) parent);}
	//
	// else if(nodeType == NodeType.SAMPLE) {
	// return new Sample((Experiment) parent);}
	//
	// else if(nodeType == NodeType.FOV) {
	// return new FieldOfView((Sample) parent);}
	// else if(nodeType == NodeType.FILE) {
	// return new FileNode(parent);
	// }
	//// else if (nodeType == OperationNode.type) {
	//// return new OperationNode(parent);
	//// }
	//
	//
	// return null;
	// }

}
