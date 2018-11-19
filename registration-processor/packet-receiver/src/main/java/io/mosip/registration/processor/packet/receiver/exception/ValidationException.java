package io.mosip.registration.processor.packet.receiver.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;

/**
 * ValidationException occurs when internal validation fails.
 *
 */
public class ValidationException extends BaseUncheckedException{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new validation exception.
	 */
	public ValidationException() {
		super();
	}

	/**
	 * Instantiates a new validation exception.
	 *
	 * @param message the message
	 */
	public ValidationException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_VALIDATION_ERROR, message);
	}

	/**
	 * Instantiates a new validation exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public ValidationException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_VALIDATION_ERROR + EMPTY_SPACE, message, cause);
	}
}
