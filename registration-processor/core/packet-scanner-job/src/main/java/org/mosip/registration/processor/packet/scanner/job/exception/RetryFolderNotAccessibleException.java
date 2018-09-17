package org.mosip.registration.processor.packet.scanner.job.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.registration.processor.packet.scanner.job.exception.utils.PacketScannerErrorCodes;

public class RetryFolderNotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public RetryFolderNotAccessibleException(String errorMessage) {
		super(PacketScannerErrorCodes.IIS_EPP_EPV_RETRY_FOLDER_NOT_ACCESSIBLE, errorMessage);
	}

	public RetryFolderNotAccessibleException(String message, Throwable cause) {
		super(PacketScannerErrorCodes.IIS_EPP_EPV_RETRY_FOLDER_NOT_ACCESSIBLE+ EMPTY_SPACE, message, cause);
	}

}
