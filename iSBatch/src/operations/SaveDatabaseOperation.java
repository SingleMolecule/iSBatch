package operations;


import gui.LogPanel;

import javax.swing.JOptionPane;

import org.tmatesoft.sqljet.core.SqlJetException;

import model.Database;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.Root;
import model.Sample;

public class SaveDatabaseOperation implements Operation {

	private Database database;
	private Node root;
	
	public SaveDatabaseOperation(Database database, Node root) {
		this.database = database;
		this.root = root;
	}
	
	@Override
	public String getName() {
		return "Save";
	}

	@Override
	public String[] getContext() {
		return new String[] { "All" };
	}

	@Override
	public void visit(Root root) {
		saveDatabase();
	}

	@Override
	public void visit(Experiment experiment) {
		saveDatabase();
	}

	@Override
	public void visit(Sample sample) {
		saveDatabase();
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		saveDatabase();
	}

	@Override
	public void visit(FileNode fileNode) {
		saveDatabase();
	}
	
	private void saveDatabase() {
		
		try {
			database.write(root);
		} catch (SqlJetException e) {
			JOptionPane.showMessageDialog(null, "Could not save the database!");
			LogPanel.log(e.getMessage());
		}
		
	}

	@Override
	public boolean setup(Node node) {
		return true;
	}

	@Override
	public void finalize(Node node) {
		
	}

}