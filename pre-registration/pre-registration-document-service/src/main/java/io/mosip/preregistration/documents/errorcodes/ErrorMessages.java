/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.errorcodes;

/**
 * 
 * This Enum provides the constant variables to define Error Messages.
 * 
 * @author Ravi C Balaji
 * @since 1.0.0
 *
 */
public enum ErrorMessages {

	/**
	 * ErrorMessage for PRG_PAM_DOC_001
	 */
	DOCUMENT_FAILED_IN_QUALITY_CHECK("Document failde in quality check"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_002
	 */
	DOCUMENT_FAILED_IN_ENCRYPTION("Document failed to encrypt"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_003
	 */
	DOCUMENT_FAILED_IN_DECRYPTION("Document failed to decrypt"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_004
	 */
	DOCUMENT_INVALID_FORMAT("Invalid document format supported"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_005
	 */
	DOCUMENT_NOT_PRESENT("Document not found for the source pre-registration Id"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_005
	 */
	DOCUMENT_FAILED_TO_FETCH("Failed to fetch from File System server"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_006
	 */
	DOCUMENT_FAILED_TO_DELETE("Documents failed to delete"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_007
	 */
	DOCUMENT_EXCEEDING_PREMITTED_SIZE("Document exceeding permited size"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_008
	 */
	DOCUMENT_TYPE_NOT_SUPPORTED("Document type not supported"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_009
	 */
	DOCUMENT_FAILED_TO_UPLOAD("Document upload failed"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_010
	 */
	DOCUMENT_FAILED_IN_VIRUS_SCAN("Document virus scan failed"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_011
	 */
	DOCUMENT_FAILED_TO_COPY("Document copy failed from source to destination"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_012
	 */
	DOCUMENT_TABLE_NOTACCESSIBLE(""),
	/**
	 * ErrorMessage for PRG_PAM_DOC_013
	 */
	DOCUMENT_IO_EXCEPTION(""),
	/**
	 * ErrorMessage for PRG_PAM_DOC_014
	 */
	MANDATORY_FIELD_NOT_FOUND(""),
	/**
	 * ErrorMessage for PRG_PAM_DOC_015
	 */
	JSON_EXCEPTION(""),
	/**
	 * ErrorMessage for PRG_PAM_DOC_016
	 */
	INVALID_CEPH_CONNECTION(""),
	/**
	 * ErrorMessage for PRG_PAM_DOC_017
	 */
	CONNECTION_UNAVAILABLE(""),
	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_REQUEST_PARAMETER(""),

	/**
	 * ErrorMessage for PRG_PAM_DOC_019
	 */
	INVALID_DOCUMENT_ID(""),
	/**
	 * ErrorMessage for PRG_PAM_DOC_019
	 */
	INVALID_DOCUMENT_CATEGORY_CODE(""),
	
	/**
	 * ErrorMessage for PRG_PAM_DOC_019
	 */
	DEMOGRAPHIC_DATA_NOT_FOUND(""),
	/**
	 * ErrorMessage for PRG_PAM_DOC_019
	 */
	DEMOGRAPHIC_GET_RECORD_FAILED(""),
	
	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_PRE_ID(""),
	
	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_DOC_CAT_CODE(""),
	
	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_DOC_TYPE_CODE(""),
	
	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_DOC_FILE_FORMAT(""),
	
	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_STATUS_CODE(""),
	
	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_UPLOAD_BY(""),
	
	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_LANG_CODE(""),
	
	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_UPLOAD_DATE_TIME(""),
	
	/**
	 * ErrorMessage for PRG_PAM_DOC_021
	 */
	DOCUMENT_ALREADY_PRESENT("");
	
	private ErrorMessages(String message) {
		this.message = message;
	}

	private final String message;

	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
}
