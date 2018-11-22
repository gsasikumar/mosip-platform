package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Thrown while performing operation return of the operation assume some data
 * but got no data.
 * 
 * @see io.mosip.kernel.core.exception.BaseUncheckedException
 * 
 * @author Bal Vikash Sharma
 * 
 * @since 1.0.0
 *
 */
public class DataNotFoundException extends BaseUncheckedException {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 2785372588639412708L;

	/**
	 * Constructor to initialize handler exception
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public DataNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
