package io.mosip.registration.processor.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.RPRPlatformErrorCodes;

/**
 * The Class UnsupportedEncodingException.
 */
public class UnsupportedEncodingException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new unsupported encoding exception.
	 */
	public UnsupportedEncodingException() {
		super();
	}

	/**
	 * Instantiates a new unsupported encoding exception.
	 *
	 * @param message
	 *            the message
	 */
	public UnsupportedEncodingException(String message) {
		super(RPRPlatformErrorCodes.RPR_CMB_UNSUPPORTED_ENCODING, message);
	}

	/**
	 * Instantiates a new unsupported encoding exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public UnsupportedEncodingException(String message, Throwable cause) {
		super(RPRPlatformErrorCodes.RPR_CMB_UNSUPPORTED_ENCODING + EMPTY_SPACE, message, cause);
	}

}
