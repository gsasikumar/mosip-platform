package org.mosip.kernel.otpmanagerservice.exceptionhandler;

import java.util.List;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class to handle exceptions for invalid OTP validation inputs.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 * 
 */
public class MosipOtpInvalidArgumentExceptionHandler extends BaseUncheckedException {
	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = 8152409863253682472L;

	/**
	 * This variable holds the MosipErrors list.
	 */
	private final List<MosipErrors> list;

	/**
	 * @param list
	 *            The error list.
	 */
	public MosipOtpInvalidArgumentExceptionHandler(List<MosipErrors> list) {
		this.list = list;
	}

	/**
	 * Getter for error list.
	 * 
	 * @return The error list.
	 */
	public List<MosipErrors> getList() {
		return list;
	}
}
