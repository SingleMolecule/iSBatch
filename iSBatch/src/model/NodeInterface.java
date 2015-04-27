package model;

import java.util.ArrayList;

public interface NodeInterface {
	public int getNumberOfFoV();
	public ArrayList<FieldOfView> getFieldOfView();
	public ArrayList<Sample> getSamples();
}
