package io.mosip.registration.constants;

/**
 * Class contains the constants used in Registration application
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegConstants {

	/**
	 * private constructor
	 */
	private RegConstants() {

	}

	public static final String EMPTY = "";
	public static final String CONSTANTS_FILE_NAME = "constants.properties";

	// AES Encryption Constants
	public static final String AES_KEY_MANAGER_ALG = "aes.algorithm";
	public static final String AES_KEY_SEED_LENGTH = "aes.keyLengthInBytes";
	public static final String AES_SESSION_KEY_LENGTH = "aes.keyLengthInBits";
	public static final String AES_KEY_CIPHER_SPLITTER = "aes.keySplitter";
	
	// Packet Store Location Constants
	public static final String PACKET_STORE_LOCATION = "packet.storageLocation";
	public static final String PACKET_STORE_DATE_FORMAT = "packet.location.dateFormat";
	
	// Packet Creation Constants 
	public static final String ZIP_FILE_EXTENSION = ".zip";
	public static final String DEMOGRPAHIC_JSON_NAME = "demographicJson";
	public static final String PACKET_META_JSON_NAME = "packetMetaJson";
	public static final String ENROLLMENT_META_JSON_NAME = "enrollmentMetaJson";
	public static final String HASHING_JSON_NAME = "hash";
	public static final String AUDIT_JSON_FILE = "audit";
	public static final String JSON_FILE_EXTENSION = ".json";

	// RSA
	public static final String RSA_ALG = "RSA";
	public static final String RSA_PUBLIC_KEY_FILE = "../Key_Store/public.key";
	public static final String RSA_PRIVATE_KEY_FILE = "../Key_Store/private.key";
	public static final String LOCALHOST = "localhost";

	// Constants for Registration Creation Zip
	/**
	 * Specifies the Image type for storing the images in zip file
	 */
	public static final String IMAGE_TYPE = ".jpg";

	/**
	 * Specifies the Document type for storing the documents in zip file
	 */
	public static final String DOC_TYPE = ".jpg";

	/**
	 * Specifies the format for storing the Registration Acknowledgement
	 */
	public static final String IMAGE_FORMAT = "png";
	/**
	 * Specifies the path for storing the template in vm file
	 */
	public static final String TEMPLATE_PATH = "src/main/resources/templates/acktemplate.vm";

	// Constants for Registration ID Generator - will be removed after Kernel
	// Integration
	public static final String AGENCY_CODE = "2018";
	public static final String STATION_NUMBER = "78213";
	public static final String RID_DATE_FORMAT = "ddMMyyyyHHmmss";

	// Logger - Constants
	public static final String APPLICATION_ID = "applicationId";
	public static final String APPLICATION_NAME = "applicationName";

	// Audit - Constants
	public static final String AUDIT_APPLICATION_ID = "audit.applicationId";
	public static final String AUDIT_APPLICATION_NAME = "audit.applicationName";
	
	// Default Host IP Address and Name for Audit Logs
	public static final String HOST_IP = "audit.hostIP";
	public static final String HOST_NAME = "audit.hostName";

	// OnlineConnectivity check
	public static final String URL = "http://localhost:8080/getTokenId";

	public static final String OTP_GENERATOR_SERVICE_NAME = "otp_generator";
	public static final String USERNAME_KEY = "key";
	public static final String OTP_GENERATED = "otp";
	public static final String OTP_VALIDATOR_SERVICE_NAME = "otp_validator";
	public static final String OTP_GENERATION_SUCCESS_MESSAGE = "Generated OTP is : ";
	public static final String OTP_VALIDATION_SUCCESS_MESSAGE = "OTP validation Successful";
	public static final String OTP_GENERATION_ERROR_MESSAGE = "Please Enter Valid Username ";
	public static final String OTP_VALIDATION_ERROR_MESSAGE = "Please Enter Valid OTP";
	public static final String OTP_GENERATOR_RESPONSE_DTO = "otpGeneratorResponseDTO";
	public static final String OTP_VALIDATOR_RESPONSE_DTO = "otpValidatorResponseDTO";
	public static final String OTP_INFO_MESSAGE = "OTP Login Information";

	public static final String ALERT_INFORMATION = "INFORMATION";
	public static final String ALERT_ERROR = "ERROR";
	public static final String ALERT_WARNING = "WARNING";

	// api related constant values
	public static final String HTTPMETHOD = "service.httpmethod";
	public static final String SERVICE_URL = "service.url";
	public static final String HEADERS = "service.headers";
	public static final String RESPONSE_TYPE = "service.responseType";
	public static final String REQUEST_TYPE = "service.requestType";
	public static final String AUTH_HEADER = "service.authheader";
	public static final String AUTH_REQUIRED = "service.authrequired";
	public static final String AUTH_TYPE = "BASIC";

	// Alert Related Details
	public static final String LOGIN_ALERT_TITLE = "LOGIN ALERT";
	public static final String LOGIN_INVALID_USERNAME = "Unable To Login";
	public static final String LOGIN_INVALID_OTP = "Unable To Login";
	public static final String GENERATED_OTP = "Generated OTP : ";
	
	/** Velocity Template Generator Constants */
	public static final String RESOURCE_LOADER = "file";
	public static final String FILE_RESOURCE_LOADER_CLASS = "file.resource.loader.class";
	public static final String FILE_RESOURCE_LOADER_PATH = "file.resource.loader.path";
	public static final String TEMPLATE_REGISTRATION_ID = "RegId";
	public static final String TEMPLATE_DATE_FORMAT = "dd/MM/yyyy";
	public static final String TEMPLATE_DATE = "Date";
	public static final String TEMPLATE_FULL_NAME = "FullName";
	public static final String TEMPLATE_DOB = "DOB";
	public static final String TEMPLATE_GENDER = "Gender";
	public static final String TEMPLATE_ADDRESS_LINE1 = "AddressLine1";
	public static final String TEMPLATE_ADDRESS_LINE2 = "AddressLine2";
	public static final String TEMPLATE_CITY = "City";
	public static final String TEMPLATE_STATE = "State";
	public static final String TEMPLATE_COUNTRY = "Country";
	public static final String TEMPLATE_MOBILE = "Mobile";
	public static final String TEMPLATE_EMAIL = "Email";
	public static final String TEMPLATE_DOCUMENTS = "Documents";
	public static final String TEMPLATE_OPERATOR_NAME = "OperatorName";
	public static final String TEMPLATE_IMAGE_SOURCE = "imagesource";
	public static final String TEMPLATE_IMAGE_ENCODING = "data:image/jpg;base64,";
	public static final String TEMPLATE_MISSING_FINGER = "&#10008;";
	public static final String TEMPLATE_BIOMETRICS_CAPTURED = "BiometricsCaptured";
	public static final String TEMPLATE_HANDS_IMAGE_PATH = "src/main/resources/images/hands.jpg";

	// Exception Code for Components
	public static final String PACKET_CREATION_EXP_CODE = "PCC-";
	public static final String PACKET_UPLOAD_EXP_CODE = "PAU-";
	public static final String REG_ACK_EXP_CODE = "ACK-";
	
	// Constants for Audits
	public static final String INTERNAL_SERVER_ERROR = "Internal error while creating packet";
	public static final String REGISTRATION_ID = "RID";
	
	//Login Sequence
	public static final String LOGIN_SEQUENCE = "sequence";
	public static final int INITIAL_LOGIN_SEQUENCE = 1;

	// USER CLIENT MACHINE MAPPING
	public static final String MACHINE_MAPPING_CODE = "USER CLIENT MACHINE MAPPING ALERT";
	public static final String MACHINE_MAPPING_SUCCESS_MESSAGE = "User Mapped Successfully";
	public static final String MACHINE_MAPPING_ERROR_MESSAGE = "Unable to map user";
	public static final String MACHINE_MAPPING_CREATED = "created";
	public static final String MACHINE_MAPPING_UPDATED = "updated";
	public static final String MACHINE_MAPPING_ACTIVE = "ACTIVE";
	public static final String MACHINE_MAPPING_IN_ACTIVE = "IN-ACTIVE";
	public static final String MACHINE_MAPPING_ENTITY_SUCCESS_MESSAGE = "User Data Fetched Successfully";
	public static final String MACHINE_MAPPING_ENTITY_ERROR_NO_RECORDS = "No Records Found";
	public static final String MACHINE_MAPPING_LOGGER_TITLE = "REGISTRATION - USER CLIENT MACHINE MAPPING";
	// BLOCKLISTED USER
	public static final String BLACKLISTED = "BLOCKED";

	// MAP ID
	public static final String USER_MACHINE_MAPID = "ListOfUserDTO";

	
	// ACTIVE INACTIVE USER
	public static final String USER_ACTIVE = "Active";
	public static final String USER_IN_ACTIVE = "In-Active";

	//opt to register constants
	public static final String OPT_TO_REG_GEO_CAP_FREQ = "GEO_CAP_FREQ";
	public static final String OPT_TO_REG_LER_J00009 = "LER_J00009"; 
	public static final String OPT_TO_REG_ICS‌_001 = "REG-ICS‌-001";
	public static final String OPT_TO_REG_ICS‌_001_MSG = "Time since last sync exceeded maximum limit. Please sync from server before proceeding with this registration";
	public static final String OPT_TO_REG_INFOTYPE = "ERROR";
	public static final String OPT_TO_REG_ICS‌_002 = "REG-ICS‌-002";
	public static final String OPT_TO_REG_ICS‌_002_MSG = "Time since last export of registration packets exceeded maximum limit. Please export or upload packets to server before proceeding with this registration";
	public static final String OPT_TO_REG_ICS‌_003 = "REG-ICS‌-003";
	public static final String OPT_TO_REG_ICS‌_003_MSG = "Maximum limit for registration packets on client reached. Please export or upload packets to server before proceeding with this registration";
	public static final String OPT_TO_REG_ICS‌_004 = "REG-ICS‌-004";
	public static final String OPT_TO_REG_ICS‌_004_MSG = "Your client machine’s location is outside the registration centre. Please note that registration can be done only from within the registration centre";
	public static final String OPT_TO_REG_PAK_MAX_CNT_OFFLINE_FREQ = "REG_PAK_MAX_CNT_OFFLINE_FREQ";
	public static final double OPT_TO_REG_EARTH_RADIUS = 6371000;
	public static final double OPT_TO_REG_METER_CONVERSN = 1609.00;
	public static final String OPT_TO_REG_LOGGER_SESSION_ID = "REGISTRATION - SYNC - VALIDATION";
	public static final String OPT_TO_REG_DIST_FRM_MACHN_TO_CENTER = "DIST_FRM_MACHN_TO_CENTER";
	public static final String OPT_TO_REG_GEO_FLAG_SINGLETIME = "Y";
	public static final String OPT_TO_REG_GEO_FLAG_MULTIPLETIME = "N";
	public static final String OPT_TO_REG_ICS‌_005 = "REG-ICS‌-005";
	public static final String OPT_TO_REG_ICS‌_005_MSG = "Unable to validate machine location. Please insert the GPS device and try again";
	public static final String OPT_TO_REG_SUCCESS = "success";

	/** Packet Status Sync Constants */
	public static final String PACKET_STATUS_SYNC_SUCCESS_MESSAGE = "Packet Status Sync Successful";
	public static final String PACKET_STATUS_SYNC_RESPONSE_ENTITY = "registrations";
	public static final String PACKET_STATUS_SYNC_SERVICE_NAME = "packet_status";
	public static final String PACKET_STATUS_SYNC_URL_PARAMETER = "registrationIds";
	public static final String PACKET_STATUS_SYNC_REGISTRATION_ID = "registrationId";
	public static final String PACKET_STATUS_SYNC_STATUS_CODE = "statusCode";
	public static final String PACKET_STATUS_SYNC_ERROR_RESPONSE = "No Status Available";
	
}
