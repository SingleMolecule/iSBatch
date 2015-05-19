package model.parameters;

/**
 *  Colors that can be used
 *  <li>{@link #TIME_LAPSE}</li>
 *  <li>{@link #RAPID_ACQUISITION}</li>

 */

public enum ExperimentType {

	/** Time lapse experiment. */
	TIME_LAPSE,
	/** Rapid Acquisition experiment. */
	RAPID_ACQUISITION, 
;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		switch (this) {
		case TIME_LAPSE:			return "Time lapse";
		case RAPID_ACQUISITION:		return "Rapid Acquisition";
		default:					return "Acquisition";
		}
	}
	

}
