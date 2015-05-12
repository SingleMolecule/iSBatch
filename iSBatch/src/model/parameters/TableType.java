package model.parameters;

// TODO: Auto-generated Javadoc
/**
 *  Types of Results Table
 *  <li>{@link #TRACK}</li>
 *  <li>{@link #TRACE}</li>
 *  <li>{@link #PEAK}</li>
 *  <li>{@link #BRIGHT_FIELD}</li>.
 */
public enum TableType {

	/**  Track table. */
	TRACK,
	/** TRACE table. */
	TRACE, 
	/** PEAK table. */
	PEAK;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		switch (this) {
		case TRACK:	return "Track";
		case TRACE:	return "Trace";
		case PEAK:	return "Peak";
		default:	return "Generic";
		}
	}

}
