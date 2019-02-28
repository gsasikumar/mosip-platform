package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Language related errors.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
public enum IndividualTypeErrorCode {

	NO_INDIVIDUAL_TYPE_FOUND_EXCEPTION("KER-MSD-XXXX", "Individual Type not found"), INDIVIDUAL_TYPE_FETCH_EXCEPTION("KER-MSD-XXXX",
			"Error occured while fetching Individual Type");

	private final String errorCode;
	private final String errorMessage;

	private IndividualTypeErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
