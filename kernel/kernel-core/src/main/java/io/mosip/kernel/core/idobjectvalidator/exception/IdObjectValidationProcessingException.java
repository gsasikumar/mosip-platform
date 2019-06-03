package io.mosip.kernel.core.idobjectvalidator.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant;

/**
 * Exception class when there is any interrupt in processing JSON validation.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class IdObjectValidationProcessingException extends BaseCheckedException {

	/**
	 * Generated serialization ID.
	 */
	private static final long serialVersionUID = -3849227719514230853L;

	/**
	 * Constructor for JsonValidationProcessingException class.
	 * 
	 * @param errorCode
	 *            the error code of the exception.
	 * @param errorMessage
	 *            the error message associated with the exception.
	 */
	public IdObjectValidationProcessingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * @param errorCode
	 *            the error code of the exception.
	 * @param errorMessage
	 *            the error message associated with the exception.
	 * @param rootCause
	 *            the root cause of the error.
	 */
	public IdObjectValidationProcessingException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new id object validation processing exception.
	 *
	 * @param errorConstant
	 *            the error constant
	 * @param errors
	 *            the errors
	 */
	public IdObjectValidationProcessingException(IdObjectValidatorErrorConstant errorConstant,
			List<ServiceError> errors) {
		errors.parallelStream().forEach(error -> super.addInfo(error.getErrorCode(), error.getMessage()));
	}

}
