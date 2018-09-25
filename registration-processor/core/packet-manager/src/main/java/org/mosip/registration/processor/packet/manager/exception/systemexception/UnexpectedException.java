package org.mosip.registration.processor.packet.manager.exception.systemexception;



import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;

/**
 * This is unexpected exception.
 *
 */
public class UnexpectedException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public UnexpectedException() {
		super();
	}

	public UnexpectedException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_UNEXCEPTED_ERROR, message);
	}

	public UnexpectedException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_UNEXCEPTED_ERROR + EMPTY_SPACE, message, cause);
	}
}
