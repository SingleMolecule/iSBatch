package operations;

import model.Experiment;
import model.FieldOfView;
import model.FileNode;
import model.Node;
import model.NodeFilter;
import model.Root;
import model.Sample;

public class CreateMTInputOperation implements Operation {

	private String channel;

	@Override
	public String[] getContext() {
		return new String[]{"Experiment", "Sample", "FieldOfView"};
	}

	@Override
	public String getName() {
		return "MTracker Input";
	}

	@Override
	public boolean setup(Node node) {
		return true;
	}

	@Override
	public void finalize(Node node) {
	}

	@Override
	public void visit(Root root) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Experiment experiment) {
		//if experiment = Rapid Acquisition/TimeSampling
		//get array of all FoV
		if(experiment.getType().equalsIgnoreCase("Time Lapse")){
			System.out.println("Time lapse experiment");
			
			for (Node node: experiment.getDescendents(imageFileNodeFilter))
				node.accept(this);
		}
		
		else{
			System.out.println("Rapid Acquisition");
			
		}
		
		//if not
		// loop through all the samples
		
		
		
		
	}
	
	
private NodeFilter imageFileNodeFilter = new NodeFilter() {
		
		@Override
		public boolean accept(Node node) {

			if (!node.getType().equals(FileNode.type))
				return false;
			
			String ch = node.getProperty("channel");
			
			// check the channel of this file
			if (ch == null || !ch.equals(channel))
				return false;
			
			String path = node.getProperty("path");
			
			// check if this file is an image
			if (path == null || !(path.toLowerCase().endsWith(".tiff") || path.toLowerCase().endsWith(".tif")))
				return false;
			
			return true;
		}
	};

	@Override
	public void visit(Sample sample) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FieldOfView fieldOfView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FileNode fileNode) {
		// TODO Auto-generated method stub
		
	}

}
