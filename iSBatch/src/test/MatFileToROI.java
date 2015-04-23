package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import operations.microbeTrackerIO.MatlabMeshes;
import operations.microbeTrackerIO.Mesh;

public class MatFileToROI {
	
	public static void main(String[] args) throws IOException {
		
		File mat = new File("D:\\TestFolderIsbatch\\DnaQ.mat");
		ArrayList<Mesh> meshes = MatlabMeshes.getMeshes(mat.getAbsolutePath());
		
		
		System.out.println(meshes.size());
		
	}

}
