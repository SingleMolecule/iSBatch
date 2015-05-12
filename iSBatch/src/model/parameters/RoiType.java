package model.parameters;

// TODO: Auto-generated Javadoc
/**
 *  Categories of Regions of interest (ROI).
 *  <li>{@link #CELL}</li>
 *  <li>{@link #BACKGROUND}</li>
 *  <li>{@link #FRAP_REGION}</li>
 */
public enum RoiType {

	/** Cell ROI. */
	CELL,
	/** Background ROI. */
	BACKGROUND, 
	/** Red channel. */
	FRAP_REGION;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		switch (this) {
		case CELL:			return "Cell";
		case BACKGROUND:	return "Background";
		case FRAP_REGION:		return "Frap region";
		default:			return "Unspecified";
		}
	}

}
