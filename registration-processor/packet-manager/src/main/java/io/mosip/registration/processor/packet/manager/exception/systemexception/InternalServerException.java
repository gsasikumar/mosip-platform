package io.mosip.registration.processor.packet.manager.exception.systemexception;


import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorCodes;

/**
 * InternalServerException occurs for any internal server issue.
 *
 */
public class InternalServerException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new internal server exception.
	 */
	public InternalServerException() {
		super();
	}

	/**
	 * Instantiates a new internal server exception.
	 *
	 * @param message the message
	 */
	public InternalServerException(String message) {
		super(PlatformErrorCodes.RPR_PKM_SERVER_ERROR, message);
	}

	/**
	 * Instantiates a new internal server exception.
	 *
	 * @param msg the msg
	 * @param cause the cause
	 */
	public InternalServerException(String msg, Throwable cause) {
		super(PlatformErrorCodes.RPR_PKM_SERVER_ERROR + EMPTY_SPACE, msg, cause);
	}
}
