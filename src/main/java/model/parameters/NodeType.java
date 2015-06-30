package model.parameters;

/**
 *  Colors that can be used
 *  <li>{@link #GREEN}</li>
 *  <li>{@link #RED}</li>
 *  <li>{@link #BLUE}</li>
 *  <li>{@link #BRIGHT_FIELD}</li>
 *  <li>{@link #ACQUISITION}</li>.
 */
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