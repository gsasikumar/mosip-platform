package io.mosip.preregistration.datasync.errorcodes;

/**
 * @author M1046129
 *
 */
public enum ErrorMessages {
	DOCUMENT_NOT_PRESENT_REQUEST, 
	DOCUMENT_IS_MISSING, 
	DOCUMENT_TABLE_NOTACCESSIBLE, 
	FAILED_TO_CREATE_A_ZIP_FILE, 
	RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID, 
	PRE_REGISTRATION_IDS_STORED_SUCESSFULLY, 
	FAILED_TO_STORE_PRE_REGISTRATION_IDS,
	RECORDS_NOT_FOUND_FOR_DATE_RANGE,
	REGISTRATION_TABLE_NOT_ACCESSIBLE,
	RECORDS_NOT_FOUND_FOR_REGISTRATION_CENTER,
	INVALID_USER_ID,
	INVALID_CREATED_USER_ID,
	INVALID_UPDATE_USER_ID,
	DEMOGRAPHIC_GET_RECORD_FAILED,
	DOCUMENT_GET_RECORD_FAILED,
	INVALID_REGISTRATION_CENTER_ID,
	INVALID_REQUESTED_DATE,
	INVALID_REQUESTED_CREATED_DATE,
	INVALID_REQUESTED_UPDATED_DATE,
	INVALID_REQUESTED_PRE_REG_ID_LIST,
	INVALID_REQUESTED_LANG_CODE,
	 FAILED_TO_GET_PRE_REG_ID_BY_REG_CLIENT_ID,
	 FAILED_TO_FETCH_DOCUMENT,
		/**
		 * ErrorMessage for PRG_PAM_APP_009
		 */
		FILE_IO_EXCEPTION,

		/**
		 * ErrorMessage for PRG_PAM_APP_009
		 */
		UNSUPPORTED_ENCODING_CHARSET,
		FILE_NOT_FOUND,
		/**
		 * ErrorMessage for PRG_DATA_SYNC_016
		 */
		BOOKING_NOT_FOUND 
		;
}
