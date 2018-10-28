/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.crypto.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * {@link Exception} to be thrown when algorithm is invalid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipNoSuchAlgorithmException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 24357407021151065L;

	/**
	 * Constructor with errorCode and errorMessage
	 * @param errorCode
	 * @param errorMessage
	 */
	public MosipNoSuchAlgorithmException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
