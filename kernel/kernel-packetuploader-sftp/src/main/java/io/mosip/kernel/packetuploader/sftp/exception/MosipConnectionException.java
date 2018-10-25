package io.mosip.kernel.packetuploader.sftp.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderConstant;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderExceptionConstant;

/**
 * Exception to be thrown when Connection is not made with server
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipConnectionException extends BaseCheckedException {

	/**
	 * Constant id for serialization
	 */
	private static final long serialVersionUID = 3585613514626311385L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 * @param cause
	 *            cause of exception
	 */
	public MosipConnectionException(PacketUploaderExceptionConstant exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage()
				+ PacketUploaderConstant.EXCEPTTION_BREAKER.getValue() + cause.getMessage());
	}

}
