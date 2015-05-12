package model.parameters;

// TODO: Auto-generated Javadoc
/**
 *  Colors that can be used
 *  <li>{@link #GREEN}</li>
 *  <li>{@link #RED}</li>
 *  <li>{@link #BLUE}</li>
 *  <li>{@link #BRIGHT_FIELD}</li>
 *  <li>{@link #ACQUISITION}</li>.
 */
public enum Channel {

	/** Green channel. */
	GREEN,
	/** Blue channel. */
	BLUE, 
	/** Red channel. */
	RED, 
	/** Bright field channel. */
	BRIGHT_FIELD,
	/** Acquisition channel. */
	ACQUISITION;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		switch (this) {
		case GREEN:			return "Green";
		case BLUE:			return "Blue";
		case RED:			return "Red";
		case BRIGHT_FIELD: 	return "Bright Field";
		default:			return "Acquisition";
		}
	}

}
