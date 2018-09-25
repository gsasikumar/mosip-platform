package org.mosip.kernel.core.utils.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when a number is too large.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public class MosipNumberIsTooLargeException extends BaseUncheckedException {

	/** Serializable version Id. */
	private static final long serialVersionUID = 424322202100630614L;

	/**
	 * @param arg0
	 *            Error Code Corresponds to Particular Exception
	 * @param arg1
	 *            Message providing the specific context of the error.
	 * @param arg2
	 *            Cause of exception
	 */
	public MosipNumberIsTooLargeException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
	}

}
