package operations;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JOptionPane;

import gui.LogPanel;
import model.DatabaseModel;
import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.OperationNode;
import model.Root;
import model.Sample;
import utils.FileUtils;

/**
 * This operations removes a node from the database tree. Output files will be deleted.
 * 
 * @author C.M. Punter
 *
 */
public class RemoveNodeOperation implements Operation {

	private DatabaseModel model;
	
	public RemoveNodeOperation(DatabaseModel model) {
		this.model = model;		
	}
		
	@Override
	public String[] getContext() {
		return new String[]{"All"};
	}

	@Override
	public String getName() {
		
		return "Remove";
	}

	@Override
	public boolean setup(Node node) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void finalize(Node node) {
		// TODO Auto-generated method stub

	}

	@Override
	public Node[] getCreatedNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visit(Root root) {
		// cannot be deleted
	}

	@Override
	public void visit(Experiment experiment) {
		
		if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete node " + experiment + "?") == JOptionPane.YES_OPTION) {
			model.removeNode(experiment.getParent(), experiment);
			try {
				FileUtils.delete(new File(experiment.getOutputFolder()));
			} catch (IOException e) {
				LogPanel.log(e.getMessage());
			}
		}
	}

	@Override
	public void visit(Sample sample) {
		if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete node " + sample + "?") == JOptionPane.YES_OPTION) {
			model.removeNode(sample.getParent(), sample);
			try {
				FileUtils.delete(new File(sample.getOutputFolder()));
			} catch (IOException e) {
				LogPanel.log(e.getMessage());
			}
		}
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete node " + fieldOfView + "?") == JOptionPane.YES_OPTION) {
			model.removeNode(fieldOfView.getParent(), fieldOfView);
			try {
				FileUtils.delete(new File(fieldOfView.getOutputFolder()));
			} catch (IOException e) {
				LogPanel.log(e.getMessage());
			}
		}
	}

	@Override
	public void visit(FileNode fileNode) {
		if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete node " + fileNode + "?") == JOptionPane.YES_OPTION) {
			model.removeNode(fileNode.getParent(), fileNode);
			try {
				FileUtils.delete(new File(fileNode.getOutputFolder()));
			} catch (IOException e) {
				LogPanel.log(e.getMessage());
			}
		}
	}

	@Override
	public void visit(OperationNode operationNode) {

	}

}
