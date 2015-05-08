/*
 * 
 */
package model;

import ij.IJ;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import utils.SQLReader;

// TODO: Auto-generated Javadoc
/**
 * The Class Database.
 */
public class Database {

	/** The database. */
	private SqlJetDb database;

	// private static File defaultdb = new File("src//model//template.db");
	/**
	 * Instantiates a new database.
	 *
	 * @param file the file
	 * @throws SqlJetException the sql jet exception
	 */
	public Database(File file) throws SqlJetException {
		
		if (!file.exists()) {
	
			database = SqlJetDb.open(file, true);
			IJ.showMessage("Open File");
			//createTablesFromFile();
			
			createTables();

		}
		else{
			database = SqlJetDb.open(file, true);
		}

	}

	/**
	 * Creates the tables from file.
	 *
	 * @throws SqlJetException the sql jet exception
	 */
	private void createTablesFromFile() throws SqlJetException {
		database.getOptions().setAutovacuum(true);
		database.beginTransaction(SqlJetTransactionMode.WRITE);
		database.getOptions().setUserVersion(1);
		
//		File test = new File("src//model//template");
		
//		InputStream is = this.getClass().getResourceAsStream("src//model//template");
//		File test = new File(is);
		
		InputStream is = getClass().getResourceAsStream("/template.txt");
		IJ.showMessage("Try stream3");
//		URL urlToDictionary = this.getClass().getResource("/template.txt");
//		IJ.showMessage(urlToDictionary.getFile());

	
		
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		
//		IJ.showMessage(test.getAbsolutePath());
		SQLReader reader = new SQLReader();
		ArrayList<String> listOfQueries = reader.createQueries(br);

		for (String string : listOfQueries) {
			IJ.showMessage(string);

			if (string.contains("CREATE TABLE")) {
				
				
				database.createTable(string);

			}
			if (string.contains("CREATE INDEX")) {
				System.out.println("Creating index");
				database.createIndex(string);

			} else {
				System.out.println(string);
			}

		}
		database.commit();

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

	}

	/**
	 * Creates the tables.
	 *
	 * @throws SqlJetException the sql jet exception
	 */
	public void createTables() throws SqlJetException {

		database.getOptions().setAutovacuum(true);
		database.beginTransaction(SqlJetTransactionMode.WRITE);
		database.getOptions().setUserVersion(1);

		String sql = "create table nodes ( " + "parent integer, "
				+ "type text)";

		database.createTable(sql);

		sql = "create index parent_index on nodes(parent)";

		database.createIndex(sql);

		sql = "create table node_properties ( " + "node integer, "
				+ "name text, " + "value text)";

		database.createTable(sql);

		sql = "create index node_index on node_properties(node)";

		database.createIndex(sql);
		database.commit();

	}

	/**
	 * Write.
	 *
	 * @param root the root
	 * @throws SqlJetException the sql jet exception
	 */
	public void write(Node root) throws SqlJetException {
		database.beginTransaction(SqlJetTransactionMode.WRITE);

		database.getTable("nodes").clear();
		database.getTable("node_properties").clear();

		write(root, -1);

		database.commit();
	}

	/**
	 * Write.
	 *
	 * @param node the node
	 * @param parentId the parent id
	 * @throws SqlJetException the sql jet exception
	 */
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

	/**
	 * Read.
	 *
	 * @return the node
	 * @throws SqlJetException the sql jet exception
	 */
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

	/**
	 * Read.
	 *
	 * @param parentNode the parent node
	 * @param parentId the parent id
	 * @throws SqlJetException the sql jet exception
	 */
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

	/**
	 * Creates the node.
	 *
	 * @param parent the parent
	 * @param type the type
	 * @return the node
	 */
//Java 1.7+ version
//	public Node createNode(Node parent, String type) {
//
//		switch (type) {
//		case Root.type:
//			return new Root(database.getFile().getParent());
//		case Experiment.type:
//			return new Experiment((Root) parent);
//		case Sample.type:
//			return new Sample((Experiment) parent);
//		case FieldOfView.type:
//			return new FieldOfView((Sample) parent);
//		case FileNode.type:
//			return new FileNode(parent);
//		}
//
//		return null;
//	}
//	
	
	public Node createNode(Node parent, String type) {
		if(type.equalsIgnoreCase(Root.type)) {
			return new Root(database.getFile().getParent());}
		
		else if(type.equalsIgnoreCase(Experiment.type)) {
			return new Experiment((Root) parent);}
		
		else if(type.equalsIgnoreCase(Sample.type)) {
			return new Sample((Experiment) parent);}
		
		else if(type.equalsIgnoreCase(FieldOfView.type))	{
			return new FieldOfView((Sample) parent);}
		else if(type.equalsIgnoreCase(FileNode.type))	{
			return new FileNode(parent);
		}
		else if (type.equalsIgnoreCase(OperationNode.type)) {
			return new OperationNode(parent);
		}
		

		return null;
	}
	

}
