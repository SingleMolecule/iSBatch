package model.parameters;

public enum NodeType {

	/** Root file. */
	ROOT,
	/** Experiment File type file. */
	EXPERIMENT, 
	/** Sample type file. */
	SAMPLE, 
	/** Field of View type file. */
	FOV,
	/** File type file. */
	FILE;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		switch (this) {
		case ROOT:			return "Root";
		case EXPERIMENT:			return "Experiment";
		case SAMPLE:			return "Sample";
		case FOV: 	return "FieldOfView";
		case FILE: 	return "File";
		default:			return "";
		}
	}

}