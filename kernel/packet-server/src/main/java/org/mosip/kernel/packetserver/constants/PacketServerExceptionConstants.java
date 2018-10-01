package org.mosip.kernel.packetserver.constants;

/**
 * Exception constants for this Application
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum PacketServerExceptionConstants {

	/**
	 * {@link #MOSIP_PUBLIC_KEY_EXCEPTION} exception constant
	 */
	MOSIP_PUBLIC_KEY_EXCEPTION("KER-FTM-FTP-001", "cannot read public key"),
	/**
	 * {@link #MOSIP_INVALID_SPEC_EXCEPTION} exception constant
	 */
	MOSIP_INVALID_SPEC_EXCEPTION("KER-FTM-FTP-002",
			"public key is does not have valid spec"),
	/**
	 * {@link #MOSIP_ILLEGAL_STATE_EXCEPTION} exception constant
	 */
	MOSIP_ILLEGAL_STATE_EXCEPTION("KER-FTM-FTP-003",
			"server went into illegal state");

	/**
	 * Constructor for this {@link Enum}
	 */
	private PacketServerExceptionConstants() {
	}

	/**
	 * Constructor for this {@link Enum}
	 * 
	 * @param errorCode
	 *            errorCode for exception
	 * @param errorMessage
	 *            errorMessage for exception
	 */
	PacketServerExceptionConstants(String errorCode, String errorMessage) {
		this.setErrorCode(errorCode);
		this.setErrorMessage(errorMessage);
	}

	/**
	 * Getter for {@link #errorCode}
	 * 
	 * @return {@link #errorCode}
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Setter for {@link #errorCode}
	 * 
	 * @param errorCode
	 *            {@link #errorCode}
	 */
	private void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Getter for {@link #errorMessage}
	 * 
	 * @return {@link #errorMessage}
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Setter for {@link #errorMessage}
	 * 
	 * @param errorMessage
	 */
	private void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Error Code for Exception
	 */
	String errorCode;
	/**
	 * Error Message for Exception
	 */
	String errorMessage;
}
