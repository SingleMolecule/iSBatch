package model.parameters;

public enum ExperimentType {

	/** Time lapse experiment. */
	TIME_LAPSE,
	/** Rapid Acquisition experiment. */
	RAPID_ACQUISITION, 
;

	public String toString() {
		switch (this) {
		case TIME_LAPSE:			return "Time lapse";
		case RAPID_ACQUISITION:		return "Rapid Acquisition";
		default:					return "Acquisition";
		}
	}
	

}
