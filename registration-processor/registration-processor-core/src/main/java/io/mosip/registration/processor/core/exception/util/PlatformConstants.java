package io.mosip.registration.processor.core.exception.util;

/**
 * The Enum RPRPlatformErrorMessages.
 *
 * @author M1047487
 */
public final class PlatformConstants {

	/**
	 * RPR platform error codes.
	 */
	private static final String RPR_REGISTRATION_PROCESSOR_PREFIX = "RPR-";

	/** The constant RPR_PACKET_GENERATOR_MODULE. */

	public static final String RPR_PACKET_GENERATOR_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PGS-";

	/** The Constant RPR_PACKET_RECEIVER_MODULE. */
	public static final String RPR_PACKET_RECEIVER_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PKR-";

	/** The Constant RPR_REGISTRATION_STATUS_MODULE. */
	public static final String RPR_REGISTRATION_STATUS_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "RGS-";

	/** The Constant RPR_PACKET_INFO_STORAGE_MODULE. */
	public static final String RPR_PACKET_INFO_STORAGE_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PIS-";

	/** The Constant RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE. */
	public static final String RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "FAC-";

	/** The Constant RPR_PACKET_MANAGER_MODULE. */
	public static final String RPR_PACKET_MANAGER_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PKM-";

	/** The Constant RPR_OSI_VALIDATOR_MODULE. */
	public static final String RPR_OSI_VALIDATOR_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "OSI-";

	/** The Constant RPR_DEMO_DEDUPE_MODULE. */
	public static final String RPR_DEMO_DEDUPE_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "DDD-";

	/** The Constant RPR_CAMEL_BRIDGE_MODULE. */
	public static final String RPR_CAMEL_BRIDGE_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "CMB-";

	/** The Constant RPR_QUALITY_CHECKER_MODULE. */
	public static final String RPR_QUALITY_CHECKER_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "QCR-";

	/** The Constant RPR_PACKET_SCANNER_JOB_MODULE. */
	public static final String RPR_PACKET_SCANNER_JOB_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PSS-";

	/** The Constant RPR_PACKET_DECRYPTION_JOB_MODULE. */
	public static final String RPR_PACKET_DECRYPTION_JOB_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PDS-";

	/** The Constant RPR_MANUAL_VERIFICATION_MODULE. */
	public static final String RPR_MANUAL_VERIFICATION_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "MVS-";

	/** The Constant RPR_REST_CLIENT_MODULE. */
	public static final String RPR_REST_CLIENT_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "RCT-";

	/** The Constant RPR_SYSTEM_EXCEPTION. */
	public static final String RPR_SYSTEM_EXCEPTION = RPR_REGISTRATION_PROCESSOR_PREFIX + "SYS-";

	/** The Constant RPR_MESSAGE_SENDER_TEMPLATE. */
	public static final String RPR_MESSAGE_SENDER_TEMPLATE = RPR_REGISTRATION_PROCESSOR_PREFIX + "TEM-";

	/** The Constant RPR_PACKET_UPLOADER_MODULE. */
	public static final String RPR_PACKET_UPLOADER_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PUM-";

	/** The Constant RPR_BIO_DEDUPE_SERVICE_MODULE. */
	public static final String RPR_BIO_DEDUPE_SERVICE_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "BDD-";

	/** The Constant RPR_PRINTING_MODULE. */
	public static final String RPR_PRINTING_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PRT-";

	/** The Constant RPR_MOSIP_QUEUE_MODULE. */
	public static final String RPR_MOSIP_QUEUE_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "MQI-";

	/** The Constant RPR_PACKET_VALIDATOR_MODULE. */
	public static final String RPR_PACKET_VALIDATOR_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PVM-";

	/** The Constant RPR_UIN_GENERATOR_STAGE. */
	public static final String RPR_UIN_GENERATOR_STAGE = RPR_REGISTRATION_PROCESSOR_PREFIX + "UGS-";

	/** The Constant RPR_UIN_GENERATOR_STAGE. */
	public static final String RPR_AUTHENTICATION = RPR_REGISTRATION_PROCESSOR_PREFIX + "AUT-";

	public static final String RPR_REPROCESSOR_STAGE = RPR_REGISTRATION_PROCESSOR_PREFIX + "RS-";

	public static final String RPR_EXTERNAL_STAGE = RPR_REGISTRATION_PROCESSOR_PREFIX + "EX-";

	public static final String RPR_UTIL = RPR_REGISTRATION_PROCESSOR_PREFIX + "UTL-";

	/**
	 * Instantiates a new RPR platform error codes.
	 */
	private PlatformConstants() {
		throw new IllegalStateException("Utility class");
	}

}
