package io.mosip.registration.processor.status.code;

/**
 * Valid Status codes for Registration status table.
 * @author Girish Yarru
 *
 */
public enum RegistrationStatusCode {

	PACKET_UPLOADED_TO_LANDING_ZONE, PACKET_UPLOADED_TO_VIRUS_SCAN, VIRUS_SCAN_FAILED, VIRUS_SCAN_SUCCESSFUL, PACKET_UPLOADED_TO_FILESYSTEM, DUPLICATE_PACKET_RECIEVED, INVALID_PACKET_FORMAT, PACKET_NOT_PRESENT_IN_REQUEST, PACKET_SIZE_GREATER_THAN_LIMIT, PACKET_DECRYPTION_SUCCESSFUL, PACKET_DECRYPTION_FAILED, PACKET_STRUCTURAL_VALIDATION_SUCCESSFULL, PACKET_STRUCTURAL_VALIDATION_FAILED, PACKET_NOT_YET_SYNC, PACKET_DATA_STORE_SUCCESSFUL, PACKET_DATA_STORE_FAILED, PACKET_OSI_VALIDATION_SUCCESSFUL, PACKET_OSI_VALIDATION_FAILED, PACKET_DEMO_DEDUPE_SUCCESSFUL, PACKET_DEMO_POTENTIAL_MATCH, PACKET_DEMO_DEDUPE_FAILED, PACKET_BIO_DEDUPE_SUCCESSFUL, PACKET_BIO_POTENTIAL_MATCH, PACKET_BIO_DEDUPE_FAILED, UIN_GENERATED

}
