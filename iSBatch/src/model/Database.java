package model;

import java.io.File;
import java.util.Map.Entry;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class Database {
	
	private SqlJetDb database;
	
	public Database(File file) throws SqlJetException {
		
		if (!file.exists()) {
			database = SqlJetDb.open(file, true);
			createTables();
		} else
			database = SqlJetDb.open(file, true);
		
	}
	
	public void createTables() throws SqlJetException {
		
		database.getOptions().setAutovacuum(true);
		database.beginTransaction(SqlJetTransactionMode.WRITE);
		database.getOptions().setUserVersion(1);
		
		String sql = "create table nodes ( "
				+ "parent integer, "
				+ "type text)";
		
		database.createTable(sql);
		
		sql = "create index parent_index on nodes(parent)";
		
		database.createIndex(sql);
		
		sql = "create table node_properties ( "
				+ "node integer, "
				+ "name text, "
				+ "value text)";
		
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
	}
	
	public void write(Node node, long parentId) throws SqlJetException {
		
		ISqlJetTable table = database.getTable("nodes");
		
		String type = node.getType();
		long id = table.insert(parentId, type);
		
		table = database.getTable("node_properties");
		
		for (Entry<String, String> property: node.getProperties().entrySet())
			table.insert(id, property.getKey(), property.getValue());
		
		for (Node child: node.getChildren())
			write(child, id);
		
	}
	
	public Node read() throws SqlJetException {

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
	
	public Node createNode(Node parent, String type) {
		
		switch (type) {
		case Root.type:			return new Root(database.getFile().getParent());
		case Experiment.type:	return new Experiment((Root)parent);
		case Sample.type:		return new Sample((Experiment)parent);
		case FieldOfView.type:	return new FieldOfView((Sample)parent);
		case FileNode.type:		return new FileNode(parent);
		}
		
		return null;
	}
	
}
