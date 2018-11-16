package io.mosip.registration.processor.quality.check.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorCodes;

public class InvalidQcUserIdException extends BaseUncheckedException {

	/**
	 * 
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidQcUserIdException() {
		super();
	}

	public InvalidQcUserIdException(String message) {
		super(PlatformErrorCodes.RPR_QCR_INVALID_QC_USER_ID, message);
	}

	public InvalidQcUserIdException(String message, Throwable cause) {
		super(PlatformErrorCodes.RPR_QCR_INVALID_QC_USER_ID + EMPTY_SPACE, message, cause);
	}
}