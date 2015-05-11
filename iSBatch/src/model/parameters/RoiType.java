package model.parameters;

/**
 *  Categories of Regions of interest (ROI).
 *  <li>{@link #CELL}</li>
 *  <li>{@link #BACKGROUND}</li>
 *  <li>{@link #FRAP_SPOT}</li>
 */
public enum RoiType {

	/** Cell ROI. */
	CELL,
	/** Background ROI. */
	BACKGROUND, 
	/** Red channel. */
	FRAP_SPOT;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		switch (this) {
		case CELL:
			return "Cell";
		case BACKGROUND:
			return "Background";
		case FRAP_SPOT:
			return "Frap spot";
		default:
			return "Unspecified";
		}
	}

}
