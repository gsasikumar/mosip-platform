package org.mosip.kernel.core.utils.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Unchecked exception thrown to indicate a syntax error in a regular-expression
 * pattern
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class MosipPatternSyntaxException extends BaseUncheckedException {
	/** Serializable version Id. */
	private static final long serialVersionUID = 123456202110630628L;

	/**
	 * @param errorCode
	 *            The error code defined for the exception
	 * @param errorMessage
	 *            The error message defined for the exception
	 * @param rootCause
	 *            Traceback to the method throwing the error
	 */
	public MosipPatternSyntaxException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
