package io.mosip.kernel.keymanager.softhsm.constant;

/**
 * Error Code for Softhsm Keystore
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public enum SofthsmKeystoreErrorCode {
	NO_SUCH_SECURITY_PROVIDER("KER-KMS-001", "No such security provider"),

	KEYSTORE_PROCESSING_ERROR("KER-KMS-002", "Error occured in processing Keystore: "),

	NO_SUCH_ALIAS("KER-KMS-003", "No such alias");

	/**
	 * The error code
	 */
	private final String errorCode;
	/**
	 * The error message
	 */
	private final String errorMessage;

	/**
	 * Constructor to set error code and message
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	private SofthsmKeystoreErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Function to get error code
	 * 
	 * @return {@link #errorCode}
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Function to get the error message
	 * 
	 * @return {@link #errorMessage}
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
