package io.mosip.registration.processor.status.code;

/**
 * Valid Status codes for Registration status table.
 *
 * @author Girish Yarru
 *
 */
public enum RegistrationStatusCode {
	// If any new status is being added here then add it in
	// RegistrationStatusMapUtil also.
	/** Potential Match found in data base. */

	DEMO_DEDUPE_POTENTIAL_MATCH_FOUND,

	/** The packet uploaded to virus scan. */
	PACKET_UPLOADED_TO_VIRUS_SCAN,

	/** The virus scan failed. */
	VIRUS_SCAN_FAILED,

	/** The packet uploaded to filesystem. */
	PACKET_UPLOADED_TO_FILESYSTEM,

	/** The duplicate packet recieved. */
	DUPLICATE_PACKET_RECIEVED,

	/** The invalid packet format. */
	INVALID_PACKET_FORMAT,

	/** The packet not present in request. */
	PACKET_NOT_PRESENT_IN_REQUEST,

	/** The packet size greater than limit. */
	PACKET_SIZE_GREATER_THAN_LIMIT,

	/** The packet decryption successful. */
	PACKET_DECRYPTION_SUCCESS,

	/** The packet decryption failed. */
	PACKET_DECRYPTION_FAILED,

	/** The structure validation success. */
	STRUCTURE_VALIDATION_SUCCESS,

	/** The structure validation failed. */
	STRUCTURE_VALIDATION_FAILED,

	/** The packet not yet sync. */
	PACKET_NOT_YET_SYNC,

	/** The packet data store successful. */
	PACKET_DATA_STORE_SUCCESS,

	/** The packet data store failed. */
	PACKET_DATA_STORE_FAILED,

	/** The packet osi validation successful. */
	PACKET_OSI_VALIDATION_SUCCESS,

	/** The packet osi validation failed. */
	PACKET_OSI_VALIDATION_FAILED,

	/** The packet demo dedupe successful. */
	DEMO_DEDUPE_SUCCESS,

	/** The packet demo dedupe failed. */
	DEMO_DEDUPE_FAILED,

	/** The packet bio dedupe successful. */
	PACKET_BIO_DEDUPE_SUCCESS,

	/** The packet bio potential match. */
	PACKET_BIO_POTENTIAL_MATCH,

	/** The packet bio dedupe failed. */
	PACKET_BIO_DEDUPE_FAILED,

	/** The uin generated. */
	PACKET_UIN_GENERATION_SUCCESS,

	/** The manual adjudication success. */
	MANUAL_ADJUDICATION_SUCCESS,

	/** The manual adjudication failed. */
	MANUAL_ADJUDICATION_FAILED,

	/** The virus scan success. */
	VIRUS_SCAN_SUCCESSFUL,

	/** The packet uin updation success. */
	PACKET_UIN_UPDATION_SUCCESS,

	/** The packet uin updation failure. */
	PACKET_UIN_UPDATION_FAILURE,

	/** The document resent to camel queue */
	DOCUMENT_RESENT_TO_CAMEL_QUEUE,

	/** The packet sent for printing. */
	PACKET_SENT_FOR_PRINTING,

	/** The unable to sent for printing. */
	UNABLE_TO_SENT_FOR_PRINTING,

	/** Unable to upload packet to packet store. */
	PACKET_UPLOAD_TO_PACKET_STORE_FAILED,

	/** The notification sent to resident. */
	NOTIFICATION_SENT_TO_RESIDENT,

	/** The packet uin printed and posted success */
	PRINT_AND_POST_COMPLETED,

	/** The packet uin printed and posted failure */
	RESEND_UIN_CARD_FOR_PRINTING,

	EXTERNAL_STAGE_SUCCESS,

	EXTERNAL_STAGE_FAILURE,

	VIRUS_SCAN_PROCESSING,

	PACKET_UPLOAD_TO_PACKET_STORE_PROCESSING,

	STRUCTURE_VALIDATION_PROCESSING,

}
