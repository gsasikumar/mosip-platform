package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Customized exception for template not found
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
public class TemplateNotFoundException extends BaseUncheckedException {

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
	public TemplateNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
