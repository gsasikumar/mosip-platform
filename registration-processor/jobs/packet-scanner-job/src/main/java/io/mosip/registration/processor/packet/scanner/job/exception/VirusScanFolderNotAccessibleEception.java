package io.mosip.registration.processor.packet.scanner.job.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorConstants;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class VirusScanFolderNotAccessibleEception.
 */
public class VirusScanFolderNotAccessibleEception extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new virus scan folder not accessible eception.
	 *
	 * @param errorMessage the error message
	 */
	public VirusScanFolderNotAccessibleEception(String errorMessage) {
		super(PlatformErrorMessages.RPR_PSJ_VIRUS_SCAN_FOLDER_NOT_ACCESSIBLE.getCode(), errorMessage);
	}

	/**
	 * Instantiates a new virus scan folder not accessible eception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public VirusScanFolderNotAccessibleEception(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PSJ_VIRUS_SCAN_FOLDER_NOT_ACCESSIBLE.getCode() + EMPTY_SPACE, message, cause);
	}
}
