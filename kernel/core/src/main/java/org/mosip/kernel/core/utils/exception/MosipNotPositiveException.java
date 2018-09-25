package org.mosip.kernel.core.utils.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when the argument is negative.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public class MosipNotPositiveException extends BaseUncheckedException {

	/** Serializable version Id. */
	private static final long serialVersionUID = 764722202100630634L;

	/**
	 * @param arg0
	 *            Error Code Corresponds to Particular Exception
	 * @param arg1
	 *            Message providing the specific context of the error.
	 * @param arg2
	 *            Cause of exception
	 */
	public MosipNotPositiveException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);

	}

}
