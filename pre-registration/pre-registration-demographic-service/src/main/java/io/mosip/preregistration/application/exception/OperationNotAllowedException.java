/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the OperationNotAllowedException
 * 
 * @author Rajath KR
 * @since 1.0.0
 * 
 */
public class OperationNotAllowedException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public OperationNotAllowedException(String errorMessage) {
		super("", errorMessage);
	}

	public OperationNotAllowedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public OperationNotAllowedException(String errorMessage, Throwable rootCause) {
		super("", errorMessage, rootCause);
	}

	public OperationNotAllowedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
