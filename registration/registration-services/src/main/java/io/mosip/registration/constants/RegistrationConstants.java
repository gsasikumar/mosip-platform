package io.mosip.registration.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class contains the constants used in Registration application
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegistrationConstants {

	/**
	 * private constructor
	 */
	private RegistrationConstants() {

	}

	/*********** UI Constants **********/
	// paths of FXML pages to be loaded

	public static final String ERROR_PAGE = "/fxml/ErrorPage.fxml";
	public static final String INITIAL_PAGE = "/fxml/RegistrationLogin.fxml";
	public static final String HOME_PAGE = "/fxml/RegistrationOfficerLayout.fxml";
	public static final String HEADER_PAGE = "/fxml/Header.fxml";
	public static final String UPDATE_PAGE = "/fxml/UpdateLayout.fxml";
	public static final String OFFICER_PACKET_PAGE = "/fxml/RegistrationOfficerPacketLayout.fxml";
	public static final String CREATE_PACKET_PAGE = "/fxml/Registration.fxml";
	public static final String ACK_RECEIPT_PATH = "/fxml/AckReceipt.fxml";
	public static final String APPROVAL_PAGE = "/fxml/RegistrationApproval.fxml";
	public static final String FTP_UPLOAD_PAGE = "/fxml/PacketUpload.fxml";
	public static final String USER_MACHINE_MAPPING = "/fxml/UserClientMachineMapping.fxml";
	public static final String SYNC_STATUS = "/fxml/RegPacketStatus.fxml";
	public static final String ONHOLD_PAGE = "/fxml/OnholdComment.fxml";
	public static final String REJECTION_PAGE = "/fxml/RejectionComment.fxml";
	public static final String DEVICE_ONBOARDING_PAGE = "/fxml/DeviceMachineMapping.fxml";
	public static final String USER_AUTHENTICATION = "/fxml/Authentication.fxml";
	public static final String DEMOGRAPHIC_PREVIEW = "/fxml/DemographicPreview.fxml";
	public static final String BIOMETRIC_PREVIEW = "/fxml/BiometricPreview.fxml";
	public static final String WEB_CAMERA_PAGE = "/fxml/WebCamera.fxml";
	public static final String PENDING_ACTION_PAGE = "/fxml/RegistrationPendingAction.fxml";
	public static final String PENDING_APPROVAL_PAGE = "/fxml/RegistrationPendingApproval.fxml";
	public static final String REREGISTRATION_PAGE = "/fxml/ReRegistration.fxml";
	public static final String SCAN_PAGE = "/fxml/Scan.fxml";
	public static final String UIN_UPDATE = "/fxml/UpdateUIN.fxml";
	public static final String SYNC_DATA = "/fxml/SyncDataProcess.fxml";
	public static final String USER_ONBOARD = "/fxml/Onboard.fxml";
	public static final String USER_ONBOARD_FP = "/fxml/UserOnboardFPCapture.fxml";
	public static final String USER_ONBOARD_IRIS = "/fxml/UserOnboardIrisCapture.fxml";
	public static final String USER_ONBOARD_WEBCAM = "/fxml/UserOnboardWebCamera.fxml";
	public static final String BIO_EXCEPTION_PAGE = "/fxml/BiometricException.fxml";
	public static final String SEND_NOTIFICATION_PAGE = "/fxml/SendNotification.fxml";

	// FXML Id
	public static final String DEMOGRAPHIC_DETAIL = "demographicDetail";
	public static final String DOCUMENT_SCAN = "documentScan";
	public static final String DOCUMENT_PANE = "documentPane";
	public static final String EXCEPTION_PANE = "exceptionPane";
	public static final String BIOMETRIC_EXCEPTION = "biometricException";
	public static final String FINGER_PANE = "fingerPane";
	public static final String IRIS_PANE = "irisPane";
	public static final String FINGERPRINT_CAPTURE = "fingerPrintCapture";
	public static final String IRIS_CAPTURE = "irisCapture";
	public static final String FACE_CAPTURE = "faceCapture";
	public static final String REGISTRATION_PREVIEW = "registrationPreview";
	public static final String OPERATOR_AUTHENTICATION = "operatorAuthenticationPane";
	public static final String ONBOARD_USER_PARENT = "onboardUser";
	public static final String ONBOARD_USER_SUCCESS = "userOnboardSuccess";

	// Page Flow
	public static final String VISIBILITY = "visibility";
	public static final String ONBOARD_LIST = "onboardPageList";
	public static final String ONBOARD_MAP = "onboardMap";
	public static final String REGISTRATION_MAP = "registrationMap";
	public static final String ONBOARD = "onboard";

	// CSS file
	public static final String CSS_FILE_PATH = "application.css";

	// Images path
	public static final String CLOSE_IMAGE_PATH = "/images/Close.png";
	public static final String DOC_STUB_PATH = "/images/PANStubbed.jpg";
	public static final String FP_IMG_PATH = "/images/fingerprint.jpg";
	public static final String IRIS_IMG_PATH = "/images/iris.jpg";
	public static final String FACE_IMG_PATH = "/images/face.jpg";
	public static final String LEFTPALM_IMG_PATH = "/images/leftHand.png";
	public static final String RIGHTPALM_IMG_PATH = "/images/rightHand.png";
	public static final String THUMB_IMG_PATH = "/images/thumbs.png";
	public static final String RIGHT_IRIS_IMG_PATH = "/images/Eye.png";
	public static final String LEFT_IRIS_IMG_PATH = "/images/Eye.png";
	public static final String VIEW = "/images/View.png";
	public static final String SCAN = "/images/scan.png";

	// Authentication
	public static final String SUPERVISOR_VERIFICATION = "Supervisor Login";
	public static final String SUPERVISOR_NAME = "Supervisor";
	public static final String SUPERVISOR_FINGERPRINT_LOGIN = "Supervisior Fingerprint Authentication";
	public static final String FINGER_PRINT_SINGLE = "single";
	public static final String FINGER_PRINT_MULTIPLE = "multiple";
	public static final String OTP_VALIDATION_SUCCESS = "success";
	public static final String SUCCESS = "Success";
	public static final String FAILURE = "Fail";
	public static final String SUPERVISOR = "SUPERVISOR";
	public static final String OFFICER = "OFFICER";
	public static final String IRIS_STUB = "leftIris";
	public static final String FACE_STUB = "face";

	// Authentication Methods
	public static final String PWORD = "PWD";
	public static final String OTP = "OTP";
	public static final String BIO = "BIO";
	public static final String FACE = "FACE";
	public static final String IRIS = "IRIS";

	public static final String LOGIN_OTP_PARAM = "otp";

	// Login
	public static final String BLOCKED = "BLOCKED";
	public static final String LOGIN_INITIAL_SCREEN = "initialMode";
	public static final String LOGIN_SEQUENCE = "sequence";
	public static final String ONBOARD_USER = "isOnboardUser";
	public static final String ONBOARD_USER_UPDATE = "updateOnboard";

	// FingerPrint
	public static final String FINGER_PRINT_CAPTURE = "Please place your finger on device.";
	public static final String FP_DEVICE = "Mantra";
	public static final String FINGER_TYPE_MINUTIA = "minutia";

	// Authorization Info
	public static final String ADMIN_ROLE = "SUPERADMIN";
	public static final String ROLES_EMPTY = "RolesEmpty";
	public static final String MACHINE_MAPPING = "MachineMapping";

	// Generic
	public static final String ERROR = "ERROR";
	public static final int PARAM_ONE = 1;
	public static final int PARAM_ZERO = 0;
	public static final String PREVIOUS = "PREVIOUS";
	public static final String NEXT = "NEXT";

	// UI Registration Validations
	public static final String ADDRESS_KEY = "PrevAddress";
	public static final String REGISTRATION_CONTROLLER = "REGISTRATION_CONTROLLER";
	public static final String DOCUMNET_SCAN_CONTROLLER = "DOCUMNET_SCAN_CONTROLLER";
	public static final String REGISTRATION_DATA = "registrationDTOContent";
	public static final String REGISTRATION_AGE_DATA = "ageDatePickerContent";
	public static final String REGISTRATION_PANE1_DATA = "demoGraphicPane1Content";
	public static final String REGISTRATION_PANE2_DATA = "demoGraphicPane2Content";
	public static final String REGISTRATION_ISEDIT = "isEditPage";
	public static final String IS_Child = "isChild";
	public static final String IS_CONSOLIDATED = "isConsolidated";
	public static final String ENABLE = "Y";
	public static final String DISABLE = "N";
	public static final String VALIDATION_SPLITTER = "\\s,";
	public static final String ON_TYPE = "_ontype";
	public static final String POR_DOCUMENTS = "porDocuments";
	public static final String POR_BOX = "porBox";
	public static final String VALIDATION_LOGGER = "VALIDATIONS";
	public static final String REG_LGN_001 = "REG_LGN_001";
	public static final String REG_DDC_002_1 = "REG_DDC_002_1";
	public static final String REG_DDC_002_2 = "REG_DDC_002_2";
	public static final String REG_DDC_003_1 = "REG_DDC_003_1";
	public static final String REG_DDC_003_2 = "REG_DDC_003_2";
	public static final String REG_DDC_004_1 = "REG_DDC_004_1";
	public static final String REG_DDC_004_2 = "REG_DDC_004_2";

	public static final String UIN_UPDATE_ISUINUPDATE = "isUINUpdate";
	public static final String LOCAL_LANGUAGE = "LocalLanguage";
	public static final String PRIMARY_LANGUAGE = "mosip.primary-language";
	public static final String SECONDARY_LANGUAGE = "mosip.secondary-language";
	public static final String LANGUAGE_ENGLISH = "english";
	public static final String LANGUAGE_ARABIC = "arabic";

	public static final String APPLICATION_LANGUAGE = "application_language";
	public static final String REGISTRATION_LOCAL_LANGUAGE = "local_language";
	public static final String PACKET_TYPE_NEW = "New";
	public static final String REGISTRATION_DTO = "registrationDto";
	public static final String ADDRESS_LINE1 = "addressLine1";
	public static final String ADDRESS_LINE2 = "addressLine2";
	public static final String ADDRESS_LINE3 = "addressLine3";
	public static final String FULL_NAME = "fullName";
	public static final String PARENT_NAME = "parentName";
	public static final String UIN_LENGTH = "mosip.kernel.uin.length";

	public static final String CHILD = "Child";
	public static final String ADULT = "Adult";
	public static final String AGE_DATEPICKER_CONTENT = "ageDatePickerContent";

	public static final String MACHINE_MAPPING_ACTIVE = "ACTIVE";
	public static final String MACHINE_MAPPING_IN_ACTIVE = "IN-ACTIVE";
	public static final String TOGGLE_BIO_METRIC_EXCEPTION = "toggleBiometricException";

	// update UIN
	public static final String UIN_NAV_LABEL = "/ UIN Update";
	public static final String UIN_LABEL = "UIN";
	public static final String FIRST_TOGGLE_LABEL = "toggleLabel1";
	public static final String SECOND_TOGGLE_LABEL = "toggleLabel2";
	public static final String PACKET_TYPE_UPDATE = "Update";
	public static final String DOB_TOGGLE = "toggleAgeOrDob";
	public static final String UIN_UPDATE_CONFIG_FLAG = "mosip.registration.uin_update_config_flag";
	public static final String UIN_UPDATE_CONFIG_FIELDS_FROM_DB = "mosip.registration.uin.update.configured.fields";
	public static final String UIN_UPDATE_NAME = "name";
	public static final String UIN_UPDATE_AGE = "age";
	public static final String UIN_UPDATE_ADDRESS = "address";
	public static final String UIN_UPDATE_CONTACT_DETAILS = "contactDetails";
	public static final String UIN_UPDATE_FOREIGNER = "foreigner";
	public static final String UIN_UPDATE_GENDER = "gender";
	public static final String UIN_UPDATE_PARENT_DETAILS = "parentOrGuardianDetails";
	public static final String UIN_UPDATE_CNIE_NUMBER = "cnieNumber";
	public static final String UIN_UPDATE_BIO_EXCEPTION = "biometricException";
	public static final String UIN_UPDATE_BIO_FP = "biometricFingerprint";
	public static final String UIN_UPDATE_BIO_IRIS = "biometricIris";

	// onBoard User
	public static final String ONBOARD_BIOMETRICS = "Biometrics - ";

	// RegistrationApproval
	public static final String PLACEHOLDER_LABEL = "No Packets for approval";
	public static final String REGISTRATIONID = "registrationID";
	public static final String STATUSCODE = "statusCode";
	public static final String STATUSCOMMENT = "statusComment";
	public static final String ONHOLD_COMMENTS = "ONHOLD_COMMENTS";
	public static final String REJECTION_COMMENTS = "REJECT_COMMENTS";
	public static final String EMPTY = "";
	public static final String CONSTANTS_FILE_NAME = "/constants.properties";
	public static final String APPROVED = "Marked for Approval";
	public static final String REJECTED = "Marked for Rejection";
	public static final String PENDING = "Pending";
	public static final String EOD_PROCESS_CONFIG_FLAG = "mosip.registration.eod_process_config_flag";

	// Packet Store Location Constants
	public static final String PACKET_STORE_LOCATION = "mosip.registration.registration_packet_store_location";
	public static final String PACKET_STORE_DATE_FORMAT = "mosip.registration.packet_store_date_format";

	// Packet Creation Constants
	public static final String ZIP_FILE_EXTENSION = ".zip";
	public static final String DEMOGRPAHIC_JSON_NAME = "ID.json";
	public static final String PACKET_META_JSON_NAME = "packet_meta_info.json";
	public static final String PACKET_DATA_HASH_FILE_NAME = "packet_data_hash.txt";
	public static final String PACKET_OSI_HASH_FILE_NAME = "packet_osi_hash.txt";
	public static final String AUDIT_JSON_FILE = "audit";
	public static final String JSON_FILE_EXTENSION = ".json";
	public static final String ACK_RECEIPT = "RegistrationAcknowledgement";
	public static final String APPLICANT_BIO_CBEFF_FILE_NAME = "applicant_bio_CBEFF.xml";
	public static final String OFFICER_BIO_CBEFF_FILE_NAME = "officer_bio_CBEFF.xml";
	public static final String SUPERVISOR_BIO_CBEFF_FILE_NAME = "supervisor_bio_CBEFF.xml";
	public static final String INTRODUCER_BIO_CBEFF_FILE_NAME = "introducer_bio_CBEFF.xml";
	public static final String INDIVIDUAL = "INDIVIDUAL";
	public static final String INTRODUCER = "INTRODUCER";
	public static final String CBEFF_BIR_UUIDS_MAP_NAME = "CBEFF_BIR_UUIDS";
	public static final String XML_FILE_FORMAT = ".xml";
	public static final String CBEFF_FILE_FORMAT = "cbeff";
	public static final String FACE_EXCEPTION = "ExceptionFace";

	// Validation Types
	public static final String VALIDATION_TYPE_FP = "Fingerprint";
	public static final String VALIDATION_TYPE_IRIS = "Iris";
	public static final String VALIDATION_TYPE_FACE = "Face";
	public static final String VALIDATION_TYPE_FP_SINGLE = "single";

	// Supervisor Authentication
	public static final String PWD_MATCH = "Username and Password Match";
	public static final String PWD_MISMATCH = "Username and Password Not Match";

	// RSA
	public static final String LOCALHOST = "localhost";

	// Constants for Registration Creation Zip

	/**
	 * Specifies the format for storing the Registration Acknowledgement
	 */
	public static final String ACKNOWLEDGEMENT_FORMAT = "html";

	public static final String IMAGE_FORMAT = "png";

	// Constants for Registration ID Generator - will be removed after Kernel
	// Integration
	public static final String AGENCY_CODE = "2018";
	public static final String STATION_NUMBER = "78213";
	public static final String RID_DATE_FORMAT = "ddMMyyyyHHmmss";

	// Logger - Constants
	public static final String APPLICATION_ID = "REG";
	public static final String APPLICATION_NAME = "REGISTRATION";

	// Audit - Constants
	public static final String AUDIT_DEFAULT_USER = "NA";

	// OnlineConnectivity check
	public static final String URL = "http://localhost:8080/getTokenId";

	// ALert related constants
	public static final String ALERT_INFORMATION = "INFORMATION";
	public static final String ALERT_WARNING = "WARNING";
	public static final String ALERT = "ALERT";

	// api related constant values
	public static final String HTTPMETHOD = "service.httpmethod";
	public static final String SERVICE_URL = "service.url";
	public static final String HEADERS = "service.headers";
	public static final String RESPONSE_TYPE = "service.responseType";
	public static final String REQUEST_TYPE = "service.requestType";
	public static final String AUTH_HEADER = "service.authheader";
	public static final String AUTH_REQUIRED = "service.authrequired";
	public static final String AUTH_TYPE = "BASIC";

	// OTP Related Details
	public static final String OTP_GENERATOR_SERVICE_NAME = "otp_generator";
	public static final String USERNAME_KEY = "key";
	public static final String OTP_GENERATED = "otp";
	public static final String OTP_VALIDATOR_SERVICE_NAME = "otp_validator";
	public static final String OTP_GENERATOR_RESPONSE_DTO = "otpGeneratorResponseDTO";
	public static final String OTP_VALIDATOR_RESPONSE_DTO = "otpValidatorResponseDTO";

	// Velocity Template Generator Constants
	public static final String TEMPLATE_ACKNOWLEDGEMENT = "AckReceipt";
	public static final String TEMPLATE_PREVIEW = "Preview";
	public static final String TEMPLATE_QRCODE_SOURCE = "QRCodeSource";
	public static final String TEMPLATE_UIN_UPDATE = "UINUpdate";
	public static final String TEMPLATE_HEADER_TABLE = "headerTable";
	public static final String TEMPLATE_UIN_HEADER_TABLE = "uinHeaderTable";
	public static final String TEMPLATE_UIN_USER_LANG_LABEL = "UINPrimLabel";
	public static final String TEMPLATE_UIN_LOCAL_LANG_LABEL = "UINSecLabel";
	public static final String TEMPLATE_UIN = "UIN";
	public static final String TEMPLATE_RID_USER_LANG_LABEL = "RIDPrimLabel";
	public static final String TEMPLATE_RID_LOCAL_LANG_LABEL = "RIDSecLabel";
	public static final String TEMPLATE_RID = "RID";
	public static final String TEMPLATE_DATE_USER_LANG_LABEL = "DatePrimLabel";
	public static final String TEMPLATE_DATE_LOCAL_LANG_LABEL = "DateSecLabel";
	public static final String TEMPLATE_DATE = "Date";
	public static final String TEMPLATE_PRE_REG_ID_USER_LANG_LABEL = "PreRegIDPrimLabel";
	public static final String TEMPLATE_PRE_REG_ID_LOCAL_LANG_LABEL = "PreRegIDSecLabel";
	public static final String TEMPLATE_PRE_REG_ID = "PreRegID";
	public static final String TEMPLATE_DEMO_INFO = "DemographicInfo";
	public static final String TEMPLATE_FULL_NAME_USER_LANG_LABEL = "FullNamePrimLabel";
	public static final String TEMPLATE_FULL_NAME_LOCAL_LANG_LABEL = "FullNameSecLabel";
	public static final String TEMPLATE_FULL_NAME = "FullName";
	public static final String TEMPLATE_FULL_NAME_LOCAL_LANG = "FullNameSec";
	public static final String TEMPLATE_GENDER_USER_LANG_LABEL = "GenderPrimLabel";
	public static final String TEMPLATE_GENDER_LOCAL_LANG_LABEL = "GenderSecLabel";
	public static final String TEMPLATE_GENDER = "Gender";
	public static final String TEMPLATE_GENDER_LOCAL_LANG = "GenderSec";
	public static final String TEMPLATE_DOB_USER_LANG_LABEL = "DOBPrimLabel";
	public static final String TEMPLATE_DOB_LOCAL_LANG_LABEL = "DOBSecLabel";
	public static final String TEMPLATE_DOB = "DOB";
	public static final String TEMPLATE_AGE_USER_LANG_LABEL = "AgePrimLabel";
	public static final String TEMPLATE_AGE_LOCAL_LANG_LABEL = "AgeSecLabel";
	public static final String TEMPLATE_AGE = "Age";
	public static final String TEMPLATE_YEARS_USER_LANG = "YearsPrim";
	public static final String TEMPLATE_YEARS_LOCAL_LANG = "YearsSec";
	public static final String TEMPLATE_FOREIGNER_USER_LANG_LABEL = "ForiegnerPrimLabel";
	public static final String TEMPLATE_FOREIGNER_LOCAL_LANG_LABEL = "ForiegnerSecLabel";
	public static final String TEMPLATE_RESIDENCE_STATUS = "ResidenceStatus";
	public static final String TEMPLATE_RESIDENCE_STATUS_LOCAL_LANG = "ResidenceStatusSec";
	public static final String TEMPLATE_ADDRESS_LINE1_USER_LANG_LABEL = "AddressLine1PrimLabel";
	public static final String TEMPLATE_ADDRESS_LINE1_LOCAL_LANG_LABEL = "AddressLine1SecLabel";
	public static final String TEMPLATE_ADDRESS_LINE1 = "AddressLine1";
	public static final String TEMPLATE_ADDRESS_LINE1_LOCAL_LANG = "AddressLine1Sec";
	public static final String TEMPLATE_ADDRESS_LINE2_USER_LANG_LABEL = "AddressLine2PrimLabel";
	public static final String TEMPLATE_ADDRESS_LINE2_LOCAL_LANG_LABEL = "AddressLine2SecLabel";
	public static final String TEMPLATE_ADDRESS_LINE2 = "AddressLine2";
	public static final String TEMPLATE_ADDRESS_LINE3 = "AddressLine3";
	public static final String TEMPLATE_ADDRESS_LINE2_LOCAL_LANG = "AddressLine2Sec";
	public static final String TEMPLATE_REGION_USER_LANG_LABEL = "RegionPrimLabel";
	public static final String TEMPLATE_REGION_LOCAL_LANG_LABEL = "RegionSecLabel";
	public static final String TEMPLATE_REGION = "Region";
	public static final String TEMPLATE_CITY = "City";
	public static final String TEMPLATE_REGION_LOCAL_LANG = "RegionSec";
	public static final String TEMPLATE_PROVINCE_USER_LANG_LABEL = "ProvincePrimLabel";
	public static final String TEMPLATE_PROVINCE_LOCAL_LANG_LABEL = "ProvinceSecLabel";
	public static final String TEMPLATE_PROVINCE = "Province";
	public static final String TEMPLATE_PROVINCE_LOCAL_LANG = "ProvinceSec";
	public static final String TEMPLATE_LOCAL_AUTHORITY_USER_LANG_LABEL = "LocalAuthorityPrimLabel";
	public static final String TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG_LABEL = "LocalAuthoritySecLabel";
	public static final String TEMPLATE_LOCAL_AUTHORITY = "LocalAuthority";
	public static final String TEMPLATE_LOCAL_AUTHORITY_LOCAL_LANG = "LocalAuthoritySec";
	public static final String TEMPLATE_MOBILE_USER_LANG_LABEL = "MobilePrimLabel";
	public static final String TEMPLATE_MOBILE_LOCAL_LANG_LABEL = "MobileSecLabel";
	public static final String TEMPLATE_MOBILE = "Mobile";
	public static final String TEMPLATE_POSTAL_CODE_USER_LANG_LABEL = "PostalCodePrimLabel";
	public static final String TEMPLATE_POSTAL_CODE_LOCAL_LANG_LABEL = "PostalCodeSecLabel";
	public static final String TEMPLATE_POSTAL_CODE = "PostalCode";
	public static final String TEMPLATE_EMAIL_USER_LANG_LABEL = "EmailPrimLabel";
	public static final String TEMPLATE_EMAIL_LOCAL_LANG_LABEL = "EmailSecLabel";
	public static final String TEMPLATE_EMAIL = "Email";
	public static final String TEMPLATE_CNIE_NUMBER_USER_LANG_LABEL = "CNIEPrimLabel";
	public static final String TEMPLATE_CNIE_LOCAL_LANG_LABEL = "CNIESecLabel";
	public static final String TEMPLATE_CNIE_NUMBER = "CNIE";
	public static final String TEMPLATE_WITH_PARENT = "WithParent";
	public static final String TEMPLATE_PARENT_NAME_USER_LANG_LABEL = "ParentNamePrimLabel";
	public static final String TEMPLATE_PARENT_NAME_LOCAL_LANG_LABEL = "ParentNameSecLabel";
	public static final String TEMPLATE_PARENT_NAME = "ParentName";
	public static final String TEMPLATE_PARENT_UIN_USER_LANG_LABEL = "ParentUINPrimLabel";
	public static final String TEMPLATE_PARENT_UIN_LOCAL_LANG_LABEL = "ParentUINSecLabel";
	public static final String TEMPLATE_PARENT_UIN = "ParentUIN";
	public static final String TEMPLATE_PARENT_NAME_LOCAL_LANG = "ParentNameSec";
	public static final String TEMPLATE_DOCUMENTS_USER_LANG_LABEL = "DocumentsPrimLabel";
	public static final String TEMPLATE_DOCUMENTS_LOCAL_LANG_LABEL = "DocumentsSecLabel";
	public static final String TEMPLATE_DOCUMENTS = "Documents";
	public static final String TEMPLATE_DOCUMENTS_LOCAL_LANG = "DocumentsSec";
	public static final String TEMPLATE_BIOMETRICS_USER_LANG_LABEL = "BiometricsPrimLabel";
	public static final String TEMPLATE_BIOMETRICS_LOCAL_LANG_LABEL = "BiometricsSecLabel";
	public static final String TEMPLATE_BIOMETRICS_CAPTURED_USER_LANG_LABEL = "BiometricsCapturedPrimLabel";
	public static final String TEMPLATE_BIOMETRICS_CAPTURED_LOCAL_LANG_LABEL = "BiometricsCapturedSecLabel";
	public static final String TEMPLATE_BIOMETRICS_CAPTURED = "Biometrics";
	public static final String TEMPLATE_BIOMETRICS_CAPTURED_LOCAL_LANG = "BiometricsSec";
	public static final String TEMPLATE_WITHOUT_EXCEPTION = "WithoutException";
	public static final String TEMPLATE_WITH_EXCEPTION = "WithException";
	public static final String TEMPLATE_EXCEPTION_PHOTO_USER_LANG_LABEL = "ExceptionPhotoPrimLabel";
	public static final String TEMPLATE_EXCEPTION_PHOTO_LOCAL_LANG_LABEL = "ExceptionPhotoSecLabel";
	public static final String TEMPLATE_LEFT_EYE_USER_LANG_LABEL = "LeftEyePrimLabel";
	public static final String TEMPLATE_LEFT_EYE_LOCAL_LANG_LABEL = "LeftEyeSecLabel";
	public static final String TEMPLATE_RIGHT_EYE_USER_LANG_LABEL = "RightEyePrimLabel";
	public static final String TEMPLATE_RIGHT_EYE_LOCAL_LANG_LABEL = "RightEyeSecLabel";
	public static final String TEMPLATE_EXCEPTION_IMAGE_SOURCE = "ExceptionImageSource";
	public static final String TEMPLATE_LEFT_EYE = "LeftEye";
	public static final String TEMPLATE_EYE_IMAGE_SOURCE = "EyeImageSource";
	public static final String TEMPLATE_RIGHT_EYE = "RightEye";
	public static final String TEMPLATE_CAPTURED_LEFT_EYE = "CapturedLeftEye";
	public static final String TEMPLATE_CAPTURED_RIGHT_EYE = "CapturedRightEye";
	public static final String TEMPLATE_LEFT_PALM_USER_LANG_LABEL = "LeftPalmPrimLabel";
	public static final String TEMPLATE_LEFT_PALM_LOCAL_LANG_LABEL = "LeftPalmSecLabel";
	public static final String TEMPLATE_RIGHT_PALM_USER_LANG_LABEL = "RightPalmPrimLabel";
	public static final String TEMPLATE_RIGHT_PALM_LOCAL_LANG_LABEL = "RightPalmSecLabel";
	public static final String TEMPLATE_THUMBS_USER_LANG_LABEL = "ThumbsPrimLabel";
	public static final String TEMPLATE_THUMBS_LOCAL_LANG_LABEL = "ThumbsSecLabel";
	public static final String TEMPLATE_LEFT_PALM_IMAGE_SOURCE = "LeftPalmImageSource";
	public static final String TEMPLATE_RIGHT_PALM_IMAGE_SOURCE = "RightPalmImageSource";
	public static final String TEMPLATE_THUMBS_IMAGE_SOURCE = "ThumbsImageSource";
	public static final String TEMPLATE_LEFT_LITTLE_FINGER = "leftLittle";
	public static final String TEMPLATE_LEFT_RING_FINGER = "leftRing";
	public static final String TEMPLATE_LEFT_MIDDLE_FINGER = "leftMiddle";
	public static final String TEMPLATE_LEFT_INDEX_FINGER = "leftIndex";
	public static final String TEMPLATE_LEFT_THUMB_FINGER = "leftThumb";
	public static final String TEMPLATE_RIGHT_LITTLE_FINGER = "rightLittle";
	public static final String TEMPLATE_RIGHT_RING_FINGER = "rightRing";
	public static final String TEMPLATE_RIGHT_MIDDLE_FINGER = "rightMiddle";
	public static final String TEMPLATE_RIGHT_INDEX_FINGER = "rightIndex";
	public static final String TEMPLATE_RIGHT_THUMB_FINGER = "rightThumb";
	public static final String TEMPLATE_CAPTURED_LEFT_SLAP = "CapturedLeftSlap";
	public static final String TEMPLATE_CAPTURED_RIGHT_SLAP = "CapturedRightSlap";
	public static final String TEMPLATE_CAPTURED_THUMBS = "CapturedThumbs";
	public static final String TEMPLATE_MISSING_LEFT_FINGERS = "MissingLeftFingers";
	public static final String TEMPLATE_LEFT_SLAP_EXCEPTION_USER_LANG = "LeftSlapExceptionPrim";
	public static final String TEMPLATE_LEFT_SLAP_EXCEPTION_LOCAL_LANG = "LeftSlapExceptionSec";
	public static final String TEMPLATE_MISSING_RIGHT_FINGERS = "MissingRightFingers";
	public static final String TEMPLATE_RIGHT_SLAP_EXCEPTION_USER_LANG = "RightSlapExceptionPrim";
	public static final String TEMPLATE_RIGHT_SLAP_EXCEPTION_LOCAL_LANG = "RightSlapExceptionSec";
	public static final String TEMPLATE_MISSING_THUMBS = "MissingThumbs";
	public static final String TEMPLATE_THUMBS_EXCEPTION_USER_LANG = "ThumbsExceptionPrim";
	public static final String TEMPLATE_THUMBS_EXCEPTION_LOCAL_LANG = "ThumbsExceptionSec";
	public static final String TEMPLATE_RO_IMAGE_SOURCE = "ROImageSource";
	public static final String TEMPLATE_RO_NAME_USER_LANG_LABEL = "RONamePrimLabel";
	public static final String TEMPLATE_RO_NAME_LOCAL_LANG_LABEL = "RONameSecLabel";
	public static final String TEMPLATE_RO_NAME = "ROName";
	public static final String TEMPLATE_RO_NAME_LOCAL_LANG = "RONameSec";
	public static final String TEMPLATE_REG_CENTER_USER_LANG_LABEL = "RegCenterPrimLabel";
	public static final String TEMPLATE_REG_CENTER_LOCAL_LANG_LABEL = "RegCenterSecLabel";
	public static final String TEMPLATE_REG_CENTER = "RegCenter";
	public static final String TEMPLATE_REG_CENTER_LOCAL_LANG = "RegCenterSec";
	public static final String TEMPLATE_PHOTO_USER_LANG = "PhotoPrim";
	public static final String TEMPLATE_PHOTO_LOCAL_LANG = "PhotoSec";
	public static final String TEMPLATE_APPLICANT_IMAGE_SOURCE = "ApplicantImageSource";
	public static final String TEMPLATE_DATE_FORMAT = "dd/MM/yyyy";
	public static final String TEMPLATE_JPG_IMAGE_ENCODING = "data:image/jpg;base64,";
	public static final String TEMPLATE_PNG_IMAGE_ENCODING = "data:image/png;base64,";
	public static final String TEMPLATE_CROSS_MARK = "&#10008;";
	public static final String TEMPLATE_EYE_IMAGE_PATH = "/images/Eye.png";
	public static final String TEMPLATE_LEFT_SLAP_IMAGE_PATH = "/images/leftHand.png";
	public static final String TEMPLATE_RIGHT_SLAP_IMAGE_PATH = "/images/rightHand.png";
	public static final String TEMPLATE_THUMBS_IMAGE_PATH = "/images/thumbs.png";
	public static final String TEMPLATE_STYLE_HIDE_PROPERTY = "style='display:none;'";
	public static final String TEMPLATE_RIGHT_MARK = "&#10003;";
	public static final String TEMPLATE_FINGERPRINTS_CAPTURED = "FingerprintsCaptured";
	public static final String TEMPLATE_IMPORTANT_GUIDELINES = "ImportantGuidelines";
	public static final String TEMPLATE_NAME = "Acknowledgement Template";
	public static final String TEMPLATE_RESIDENT_NAME = "ResidentName";
	public static final String TEMPLATE_RO_IMAGE = "ROImage";
	public static final String TEMPLATE_MODIFY_IMAGE_PATH = "/images/Modify.png";
	public static final String TEMPLATE_MODIFY_IMAGE_SOURCE = "ModifyImageSource";
	public static final String TEMPLATE_MODIFY = "Modify";
	public static final String TEMPLATE_ENCODING = "UTF-8";
	public static final String TEMPLATE_FACE_CAPTURE_ENABLED = "FaceCaptureEnabled";
	public static final String TEMPLATE_DOCUMENTS_ENABLED = "DocumentsEnabled";
	public static final String TEMPLATE_BIOMETRICS_ENABLED = "BiometricsEnabled";
	public static final String TEMPLATE_IRIS_ENABLED = "IrisEnabled";
	public static final String TEMPLATE_IRIS_DISABLED = "IrisDisabled";
	public static final String TEMPLATE_LEFT_EYE_CAPTURED = "leftEyeCaptured";
	public static final String TEMPLATE_RIGHT_EYE_CAPTURED = "rightEyeCaptured";
	public static final String TEMPLATE_LEFT_SLAP_CAPTURED = "leftSlapCaptured";
	public static final String TEMPLATE_RIGHT_SLAP_CAPTURED = "rightSlapCaptured";
	public static final String TEMPLATE_THUMBS_CAPTURED = "thumbsCaptured";

	public static final String MODIFY_DEMO_INFO = "modifyDemographicInfo";
	public static final String MODIFY_DOCUMENTS = "modifyDocuments";
	public static final String MODIFY_BIOMETRICS = "modifyBiometrics";
	public static final String CLICK = "click";

	// Web Camera Constants
	public static final String WEB_CAMERA_IMAGE_TYPE = "jpg";
	public static final String APPLICANT_PHOTOGRAPH_NAME = "Applicant Photograph.jpg";
	public static final String EXCEPTION_PHOTOGRAPH_NAME = "Exception Photograph.jpg";
	public static final String APPLICANT_IMAGE = "Applicant Image";
	public static final String EXCEPTION_IMAGE = "Exception Image";
	public static final String APPLICANT_PHOTO_PANE = "applicantPhoto";
	public static final String EXCEPTION_PHOTO_PANE = "exceptionPhoto";
	public static final String WEB_CAMERA_PAGE_TITLE = "Applicant Biometrics";

	// Acknowledement Form
	public static final String ACKNOWLEDGEMENT_FORM_TITLE = "Registration Acknowledgement";

	// logos for new registration
	public static final String DEMOGRAPHIC_DETAILS_LOGO = "file:src/main/resources/images/Pre-Registration.png";
	public static final String APPLICANT_BIOMETRICS_LOGO = "file:src/main/resources/images/ApplicantBiometrics.png";
	public static final String OPERATOR_AUTHENTICATION_LOGO = "file:src/main/resources/images/OperatorAuthentication.png";

	// Exception Code for Components
	public static final String PACKET_CREATION_EXP_CODE = "PCC-";
	public static final String PACKET_UPLOAD_EXP_CODE = "PAU-";
	public static final String REG_ACK_EXP_CODE = "ACK-";
	public static final String DEVICE_ONBOARD_EXP_CODE = "DVO-";
	public static final String SYNC_JOB_EXP_CODE = "SYN-";
	public static final String USER_REG_IRIS_CAPTURE_EXP_CODE = "IRC-";
	public static final String USER_REG_FINGERPRINT_CAPTURE_EXP_CODE = "FPC-";
	public static final String USER_REGISTRATION_EXP_CODE = "REG-";
	public static final String USER_REG_SCAN_EXP_CODE = "SCN-";

	// USER CLIENT MACHINE MAPPING
	public static final String MACHINE_MAPPING_CREATED = "created";
	public static final String MACHINE_MAPPING_UPDATED = "updated";
	public static final String MACHINE_MAPPING_LOGGER_TITLE = "REGISTRATION - USER CLIENT MACHINE MAPPING";
	public static final String DEVICE_MAPPING_LOGGER_TITLE = "REGISTRATION - CENTER MACHINE DEVICE MAPPING";
	public static final String SYNC_TRANSACTION_DAO_LOGGER_TITLE = "REGISTRATION-SYNC-TRANSACTION DAO";
	public static final String SYNC_JOB_CONTROL_DAO_LOGGER_TITLE = "REGISTRATION-SYNC-JOB_CONTROL DAO";

	// CENTER MACHINE DEVICE MAPPING
	public static final String DEVICE_MAPPING_SUCCESS_CODE = "REG-DVO‌-001";
	public static final String DEVICE_MAPPING_ERROR_CODE = "REG-DVO‌-002";

	// MAP ID
	public static final String USER_MACHINE_MAPID = "ListOfUserDTO";

	// ACTIVE INACTIVE USER
	public static final String USER_ACTIVE = "Active";
	public static final String USER_IN_ACTIVE = "In-Active";

	// Upload Packet

	public static final List<String> PACKET_UPLOAD_STATUS = Arrays.asList("SYNCED", "EXPORTED", "RESEND", "E");

	public static final String PACKET_UPLOAD = "packet_upload";
	public static final String PACKET_DUPLICATE = "duplicate";

	public static final Set<String> getRoles() {
		return new HashSet<>(Arrays.asList("*"));
	}

	// opt to register constants
	public static final String OPT_TO_REG_GEO_CAP_FREQ = "GEO_CAP_FREQ";
	public static final String ICS_CODE_ONE = "REG-ICS‌-001";
	public static final String ICS_CODE_TWO = "REG-ICS‌-002";
	public static final String ICS_CODE_THREE = "REG-ICS‌-003";
	public static final String ICS_CODE_FOUR = "REG-ICS‌-004";
	public static final String OPT_TO_REG_PAK_MAX_CNT_OFFLINE_FREQ = "REG_PAK_MAX_CNT_OFFLINE_FREQ";
	public static final double OPT_TO_REG_EARTH_RADIUS = 6371000;
	public static final double OPT_TO_REG_METER_CONVERSN = 1609.00;
	public static final String OPT_TO_REG_DIST_FRM_MACHN_TO_CENTER = "DIST_FRM_MACHN_TO_CENTER";
	public static final String ICS_CODE_FIVE = "REG-ICS‌-005";
	public static final String ICS_CODE_SIX = "REG-ICS‌-006";
	public static final String ICS_CODE_SEVEN = "REG-ICS‌-007";
	public static final String PAK_APPRVL_MAX_CNT = "REG-ICS‌-008";
	public static final String PAK_APPRVL_MAX_TIME = "REG-ICS‌-009";
	public static final String OPT_TO_REG_LAST_CAPTURED_TIME = "lastCapturedTime";
	public static final String LATITUDE = "latitude";
	public static final String OPT_TO_REG_MDS_J00001 = "MDS_J00001";
	public static final String OPT_TO_REG_LCS_J00002 = "LCS_J00002";
	public static final String OPT_TO_REG_PDS_J00003 = "PDS_J00003";
	public static final String OPT_TO_REG_RSS_J00004 = "RSS_J00004";
	public static final String OPT_TO_REG_RCS_J00005 = "RCS_J00005";
	public static final String OPT_TO_REG_RPS_J00006 = "RPS_J00006";
	public static final String OPT_TO_REG_URS_J00007 = "URS_J00007";
	public static final String OPT_TO_REG_POS_J00008 = "POS_J00008";
	public static final String OPT_TO_REG_LER_J00009 = "LER_J00009";
	public static final String OPT_TO_REG_RDJ_J00010 = "RDJ_J00010";
	public static final String OPT_TO_REG_RDJ_J00011 = "RDJ_J00011";
	public static final String OPT_TO_REG_ADJ_J00012 = "ADJ_J00012";
	public static final String OPT_TO_REG_DEL_001 = "DEL_001";
	public static final String OPT_TO_REG_UDM_J00012 = "UDM_J00012";
	public static final String GEO_CAP_FREQ = "mosip.registration.geo.capture.frequency";
	public static final String DIST_FRM_MACHN_TO_CENTER = "mosip.registration.distance.from.machine.to.center";
	public static final String REG_PAK_MAX_CNT_OFFLINE_FREQ = "mosip.registration.packet.maximum.count.offline.frequency";
	public static final String MOSIP_REGISTRATION = "mosip.registration.";
	public static final String FREQUENCY = "frequency";

	/** Packet Status Sync Constants */
	public static final String PACKET_STATUS_SYNC_RESPONSE_ENTITY = "registrations";
	public static final String PACKET_STATUS_SYNC_SERVICE_NAME = "packet_status";
	public static final String PACKET_STATUS_READER_URL_PARAMETER = "request";
	public static final String PACKET_STATUS_READER_RESPONSE = "response";
	public static final String PACKET_STATUS_READER_REGISTRATION_ID = "registrationId";
	public static final String PACKET_STATUS_READER_STATUS_CODE = "statusCode";
	public static final String PACKET_STATUS_CODE_PROCESSED = "processed";
	public static final String PACKET_STATUS_CODE_PROCESSING = "PROCESSING";
	public static final String PACKET_STATUS_CODE_REREGISTER = "RE-REGISTER";
	public static final List<String> PACKET_STATUS_CODES_FOR_REMAPDELETE = Arrays.asList(PACKET_STATUS_CODE_REREGISTER,
			PACKET_STATUS_CODE_PROCESSING, PACKET_STATUS_CODE_PROCESSED);
	public static final String MACHINE_CENTER_REMAP_FLAG = "mosip.registrtaion.machinecenterchanged";
	public static final String PACKET_STATUS_READER_ID = "mosip.registration.status";
	public static final String PACKET_SYNC_STATUS_ID = "mosip.registration.sync";
	public static final String PACKET_SYNC_VERSION = "1.0";

	public static final String BIOMETRIC_IMAGE = "Image";
	public static final String BIOMETRIC_TYPE = "Type";
	// Packet Upload
	public static final String PACKET_TYPE = "file";
	public static final String PACKET_STATUS_PRE_SYNC = "PRE_SYNC";
	public static final String PACKET_STATUS_SYNC_TYPE = "NEW";
	public static final String ACKNOWLEDGEMENT_FILE = "_Ack";
	public static final String PACKET_SYNC_ERROR = "Error";
	public static final String RE_REGISTRATION_STATUS = "Re-Register";
	public static final String PACKET_SYNC_REF_ID = "packetsync";
	public static final String PACKET_UPLOAD_REF_ID = "packetUpload";
	public static final String PACKET_UPLOAD_SUCCESS = "Uploaded";

	// Device On-boarding
	public static final String DEVICE_MANUFACTURER_NAME = "manufacturerName";
	public static final String DEVICE_MODEL_NAME = "modelName";
	public static final String DEVICE_SERIAL_NO = "serialNo";
	public static final String ONBOARD_AVAILABLE_DEVICES = "availableDevices";
	public static final String ONBOARD_MAPPED_DEVICES = "mappedDevices";
	public static final String ONBOARD_DEVICES_MAP = "onBoardDevicesMap";
	public static final String ONBOARD_DEVICES_REF_ID_TYPE = "UserID";
	public static final String MACHINE_ID = "machineId";
	public static final String ONBOARD_DEVICES_MAP_UPDATED = "updatedDevicesMap";
	public static final String DEVICE_TYPES_ALL_OPTION = "All";
	public static final String DEVICE_TYPE = "deviceType";
	public static final String DONGLE_SERIAL_NUMBER = "dongleSerialNumber";

	// Template Name
	public static final String ACKNOWLEDGEMENT_TEMPLATE = "Ack Template";
	public static final String ACKNOWLEDGEMENT_TEMPLATE_PART_1 = "reg-ack-template-part1";
	public static final String ACKNOWLEDGEMENT_TEMPLATE_PART_2 = "reg-ack-template-part2";
	public static final String ACKNOWLEDGEMENT_TEMPLATE_PART_3 = "reg-ack-template-part3";
	public static final String EMAIL_TEMPLATE = "reg-email-notification";
	public static final String SMS_TEMPLATE = "reg-sms-notification";

	// Notification Service
	public static final String EMAIL_SUBJECT = "MOSIP REGISTRATION NOTIFICATION";
	public static final String EMAIL_SERVICE = "email";
	public static final String SMS_SERVICE = "sms";
	public static final String NOTIFICATION_SERVICE = "REGISTRATION - NOTIFICATION SERVICE ";
	public static final String MODE_OF_COMMUNICATION = "mosip.registration.mode_of_communication";

	// Global configuration parameters
	public static final String REGISTARTION_CENTER = "REGISTARTION_CENTER";
	public static final String GLOBAL_CONFIG_TRUE_VALUE = "Y";
	public static final String REG_PAK_MAX_CNT_APPRV_LIMIT = "mosip.registration.reg_pak_max_cnt_apprv_limit";
	public static final String REG_PAK_MAX_TIME_APPRV_LIMIT = "mosip.registration.reg_pak_max_time_apprv_limit";

	// Spring Batch-Jobs
	public static final String JOB_TRIGGER_STARTED = "Trigger started";
	public static final String JOB_TRIGGER_COMPLETED = "Trigger completed";
	public static final String JOB_EXECUTION_STARTED = "Execution started";
	public static final String JOB_EXECUTION_COMPLETED = "Execution completed";
	public static final String JOB_EXECUTION_SUCCESS = "Executed with success";
	public static final String JOB_EXECUTION_FAILURE = "Executed with failure";
	public static final String JOB_TRIGGER_MIS_FIRED = "Trigger Mis-Fired";
	public static final String JOB_EXECUTION_REJECTED = "Execution Rejected";
	public static final String RETRIEVED_PRE_REG_ID = "Retrieved Pre Registration";
	public static final String UNABLE_TO_RETRIEVE_PRE_REG_ID = "Unable to retrieve pre registration";

	public static final String JOB_TRIGGER_POINT_SYSTEM = "System";
	public static final String JOB_TRIGGER_POINT_USER = "User";
	public static final String JOB_SYNC_TO_SERVER = "Server";
	public static final String JOB_DETAIL = "jobDetail";
	public static final String APPLICATION_CONTEXT = "applicationContext";
	public static final String SYNC_TRANSACTION = "syncTransaction";

	// GPS Device
	public static final String GPS_LOGGER = "GPS-Device-Information";
	public static final String LONGITUDE = "longitude";
	public static final String GPS_DISTANCE = "distance";
	public static final String GPS_CAPTURE_ERROR_MSG = "gpsErrorMessage";
	public static final String GPS_CAPTURE_SUCCESS = "gpsCaptureSuccess";
	public static final String GPS_CAPTURE_FAILURE = "gpsCaptureFailure";
	public static final String GPS_CAPTURE_FAILURE_MSG = "GPS signal is weak please capture again";
	public static final String GPS_CAPTURE_SUCCESS_MSG = "GPS signal Captured Sucessfullty";
	public static final String GPS_CAPTURE_PORT_FAILURE_MSG = "Please insert the GPS device in the Specified Port";
	public static final String GPS_DEVICE_CONNECTION_FAILURE = "Please connect the GPS Device";
	public static final String GPS_DEVICE_CONNECTION_FAILURE_ERRO_MSG = "GPS device not found. Please connect an on-boarded GPS device.";
	public static final String GPS_REG_LGE‌_002 = "REG-LGE‌-002";
	public static final String GPS_SERIAL_PORT = "COM4";
	public static final String GPS_PORT_TIMEOUT = "1000";
	public static final String GPS_DEVICE_MODEL = "BU343";
	public static final String GPS_ERROR_CODE = "errorCode";
	public static final String GPS_CAPTURING_EXCEPTION = "GPS_EXCEPTION";
	public static final String GPS_SIGNAL = "$GP";

	// Documents
	public static final String POA_DOCUMENT = "POA";
	public static final String POI_DOCUMENT = "POI";
	public static final String POR_DOCUMENT = "POR";
	public static final String DOB_DOCUMENT = "POB";

	public static final List<String> PACKET_STATUS = Arrays.asList("APPROVED", "REJECTED", "RE_REGISTER_APPROVED");

	public static final List<String> PACKET_EXPORT_STATUS = Arrays.asList("EXPORTED", "SYNCED");

	// Pre Registration
	public static final String PRE_REGISTRATION_ID = "pre_registration_id";
	public static final String GET_PRE_REGISTRATION_IDS = "get_pre_registration_Ids";
	public static final String GET_PRE_REGISTRATION = "get_pre_registration";
	public static final String REGISTRATION_CLIENT_ID = "10";
	public static final String PRE_REGISTRATION_DUMMY_ID = "mosip.pre-registration.datasync";
	public static final String VER = "1.0";
	public static final String PRE_REG_TO_GET_ID_ERROR = "Unable to get Pre registartion id's";
	public static final String PRE_REG_TO_GET_PACKET_ERROR = "The Pre-registration ID entered does not exist in the database. Please enter the correct Pre-registration ID.";
	public static final String PRE_REG_PACKET_NETWORK_ERROR = "You must be connected to the internet to sync data";
	public static final String PRE_REG_SUCCESS_MESSAGE = "Pre-Registartion Data Synced successfully";
	public static final String IS_PRE_REG_SYNC = "PreRegSync";
	public static final String PRE_REG_FILE_NAME = "fileName";
	public static final String PRE_REG_FILE_CONTENT = "fileContent";
	public static final String PRE_REG_APPOINMENT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	// UI Date Format
	public static final String DATE_FORMAT = "MM/dd/yyy hh:mm:ss";
	public static final String HH_MM_SS = "HH:mm:ss";

	// Biometric Exception style
	public static final String ADD_BORDER = "addBorderStyle";
	public static final String REMOVE_BORDER = "removeBorderStyle";
	public static final String OLD_BIOMETRIC_EXCEPTION = "oldBiometric";
	public static final String NEW_BIOMETRIC_EXCEPTION = "newBiometric";

	// Iris & Fingerprint Capture for Individual Registration
	public static final String IRIS_THRESHOLD = "mosip.registration.iris_threshold";
	public static final String IMAGE_FORMAT_KEY = "imageFormat";
	public static final String IMAGE_BYTE_ARRAY_KEY = "imageBytes";
	public static final String IMAGE_SCORE_KEY = "imageScore";
	public static final String LEFT = "Left";
	public static final String RIGHT = "Right";
	public static final String EYE = "Eye";
	public static final String DOT = ".";
	public static final String FINGERPRINT = "Fingerprint";
	public static final String LEFTPALM = "leftSlap";
	public static final String RIGHTPALM = "rightSlap";
	public static final String THUMBS = "thumbs";
	public static final String PERCENTAGE = "%";
	public static final String ISO_FILE_NAME = "ISOTemplate";
	public static final String ISO_IMAGE_FILE_NAME = "ISOImage";
	public static final String ISO_FILE = "ISOTemplate.iso";
	public static final String DUPLICATE_FINGER = "DuplicateFinger";
	public static final String ISO_IMAGE_FILE = "ISOImage.iso";
	public static final String LEFTHAND_SLAP_FINGERPRINT_PATH = "/fingerprints/leftSlap.jpg";
	public static final String RIGHTHAND_SLAP_FINGERPRINT_PATH = "/fingerprints/rightSlap.jpg";
	public static final String BOTH_THUMBS_FINGERPRINT_PATH = "/fingerprints/thumbs.jpg";
	public static final String LEFTSLAP_FINGERPRINT_THRESHOLD = "mosip.registration.leftslap_fingerprint_threshold";
	public static final String RIGHTSLAP_FINGERPRINT_THRESHOLD = "mosip.registration.rightslap_fingerprint_threshold";
	public static final String THUMBS_FINGERPRINT_THRESHOLD = "mosip.registration.thumbs_fingerprint_threshold";
	public static final String FINGERPRINT_RETRIES_COUNT = "mosip.registration.num_of_fingerprint_retries";
	public static final String IRIS_RETRY_COUNT = "mosip.registration.num_of_iris_retries";
	public static final String[] LEFTHAND_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/lefthand/leftIndex/",
			"/fingerprints/lefthand/leftLittle/", "/fingerprints/lefthand/leftMiddle/",
			"/fingerprints/lefthand/leftRing/" };
	public static final String[] RIGHTHAND_SEGMNTD_DUPLICATE_FILE_PATHS = new String[] {
			"/fingerprints/righthand/rightIndex/", "/fingerprints/righthand/rightLittle/",
			"/fingerprints/righthand/rightMiddle/", "/fingerprints/righthand/rightRing/" };
	public static final String[] RIGHTHAND_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/Srighthand/rightIndex/",
			"/fingerprints/Srighthand/rightLittle/", "/fingerprints/Srighthand/rightMiddle/",
			"/fingerprints/Srighthand/rightRing/" };
	public static final String[] THUMBS_SEGMNTD_FILE_PATHS = new String[] { "/fingerprints/thumb/leftThumb/",
			"/fingerprints/thumb/rightThumb/" };
	public static final String THUMB = "Thumb";
	public static final String LEFT_HAND = "Left hand ";
	public static final String RIGHT_HAND = "Right hand ";
	public static final String[] LEFTHAND_SEGMNTD_FILE_PATHS_USERONBOARD = new String[] {
			"/fingerprints/UserOnboard/leftHand/leftIndex/", "/fingerprints/UserOnboard/leftHand/leftLittle/",
			"/fingerprints/UserOnboard/leftHand/leftMiddle/", "/fingerprints/UserOnboard/leftHand/leftRing/" };
	public static final String[] RIGHTHAND_SEGMNTD_FILE_PATHS_USERONBOARD = new String[] {
			"/fingerprints/UserOnboard/rightHand/rightIndex/", "/fingerprints/UserOnboard/rightHand/rightLittle/",
			"/fingerprints/UserOnboard/rightHand/rightMiddle/", "/fingerprints/UserOnboard/rightHand/rightRing/" };
	public static final String[] THUMBS_SEGMNTD_FILE_PATHS_USERONBOARD = new String[] {
			"/fingerprints/UserOnboard/thumb/leftThumb/", "/fingerprints/UserOnboard/thumb/rightThumb/" };

	/** Exception codes **/
	private static final String REG_SERVICE_CODE = "REG-SER-";

	public static final String REG_FRAMEWORK_PACKET_HANDLING_EXCEPTION = REG_SERVICE_CODE + "PHA-201";
	public static final String PACKET_CREATION_EXCEPTION = REG_SERVICE_CODE + "PHA-202";
	public static final String PACKET_ZIP_CREATION = REG_SERVICE_CODE + "ZCM-203";
	public static final String ENROLLMENT_ZIP_CREATION = REG_SERVICE_CODE + "ZCM-204";
	public static final String PACKET_ENCRYPTION_MANAGER = REG_SERVICE_CODE + "PEM-205";
	public static final String AES_ENCRYPTION_MANAGER = REG_SERVICE_CODE + "AEM-206";
	public static final String AES_SEED_GENERATION = REG_SERVICE_CODE + "ASG-207";
	public static final String AES_KEY_MANAGER = REG_SERVICE_CODE + "EKM-208";
	public static final String AES_ENCRYPTION = REG_SERVICE_CODE + "AEI-209";
	public static final String CONCAT_ENCRYPTED_DATA = REG_SERVICE_CODE + "AEI-210";
	public static final String ENCRYPTED_PACKET_STORAGE = REG_SERVICE_CODE + "STM-211";
	public static final String PACKET_INSERTION = REG_SERVICE_CODE + "IPD-212";
	public static final String CREATE_PACKET_ENTITY = REG_SERVICE_CODE + "IPD-213";
	public static final String LOGIN_SERVICE = REG_SERVICE_CODE + "IPD-214";
	public static final String SERVICE_DELEGATE_UTIL = REG_SERVICE_CODE + "IPD-215";
	public static final String SERVICE_DATA_PROVIDER_UTIL = REG_SERVICE_CODE + "DPU-216";
	public static final String RSA_ENCRYPTION_MANAGER = REG_SERVICE_CODE + "REM-219";
	public static final String UPDATE_SYNC_AUDIT = REG_SERVICE_CODE + "ADI-220";
	public static final String FETCH_UNSYNC_AUDIT = REG_SERVICE_CODE + "ADI-221";
	public static final String READ_PROPERTY_FILE_ERROR = REG_SERVICE_CODE + "PFR-222";
	public static final String PACKET_UPDATE_STATUS = REG_SERVICE_CODE + "UPS-217";
	public static final String PACKET_RETRIVE_STATUS = REG_SERVICE_CODE + "RPS-218";
	public static final String MACHINE_MAPPING_RUN_TIME_EXCEPTION = REG_SERVICE_CODE + "RDI-219";
	public static final String MACHINE_MAPPING_STATIONID_RUN_TIME_EXCEPTION = REG_SERVICE_CODE + "UMM-220";
	public static final String MACHINE_MAPPING_CENTERID_RUN_TIME_EXCEPTION = REG_SERVICE_CODE + "UMM-221";
	public static final String MACHINE_MAPPING_USERLIST_RUN_TIME_EXCEPTION = REG_SERVICE_CODE + "UMM-222";
	public static final String SYNC_STATUS_VALIDATE = REG_SERVICE_CODE + "SSV-223";
	public static final String MACHINE_MASTER_RECORD_NOT_FOUND = REG_SERVICE_CODE + "MMD-224";
	public static final String PACKET_META_CONVERTOR = REG_SERVICE_CODE + "PMC-225";

	// #Exceptions SyncJobs
	public static final String SYNC_TRANSACTION_RUNTIME_EXCEPTION = REG_SERVICE_CODE + "RPS-BTM-226";
	public static final String SYNC_JOB_RUN_TIME_EXCEPTION = REG_SERVICE_CODE + "RPS-JTD-227";
	public static final String PACKET_SYNC__STATUS_READER_NULL_POINTER_EXCEPTION = REG_SERVICE_CODE + "RPS-PSJ-228";
	public static final String BASE_JOB_NO_SUCH_BEAN_DEFINITION_EXCEPTION = REG_SERVICE_CODE + "RPS-BJ-229";
	public static final String BASE_JOB_NULL_POINTER_EXCEPTION = REG_SERVICE_CODE + "RPS-BJ-229";

	// Device Onboarding Service
	private static final String DEVICE_ONBOARDING_SERVICE = REG_SERVICE_CODE + "DVO-";
	public static final String FETCH_DEVICE_TYPES_EXCEPTION = DEVICE_ONBOARDING_SERVICE + "MMS-230";
	public static final String FETCH_DEVICE_MAPPING_EXCEPTION = DEVICE_ONBOARDING_SERVICE + "MMS-231";
	public static final String UPDATE_DEVICE_MAPPING_EXCEPTION = DEVICE_ONBOARDING_SERVICE + "MMS-232";

	// Exceptions
	private static final String REG_UI_CODE = "REG-UI";

	public static final String REG_UI_LOGIN_LOADER_EXCEPTION = REG_UI_CODE + "RAI-001";
	public static final String REG_UI_LOGIN_SCREEN_LOADER_EXCEPTION = REG_UI_CODE + "LC-002";
	public static final String REG_UI_HOMEPAGE_LOADER_EXCEPTION = REG_UI_CODE + "ROC-003";
	public static final String REG_UI_BASE_CNTRLR_IO_EXCEPTION = REG_UI_CODE + "BAS-004";
	public static final String REG_UI_VIEW_ACK_FORM_IO_EXCEPTION = REG_UI_CODE + "VAF-005";

	// Exceptions for Device Onboarding
	public static final String DEVICE_ONBOARD_PAGE_NAVIGATION_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "ROD-001";
	public static final String DEVICE_ONBOARD_INITIALIZATION_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-001";
	public static final String DEVICE_ONBOARD_LOADING_DEVICES_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-002";
	public static final String DEVICE_ONBOARD_MAPPING_DEVICES_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-003";
	public static final String DEVICE_ONBOARD_UNMAPPING_DEVICES_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-004";
	public static final String DEVICE_ONBOARD_HOME_NAVIGATION_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-005";
	public static final String DEVICE_ONBOARD_DEVICE_GROUPING_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-006";
	public static final String DEVICE_ONBOARD_DEVICE_UPDATING_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-007";
	public static final String DEVICE_ONBOARD_DEVICE_FILTERING_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-008";
	public static final String DEVICE_ONBOARD_DEVICE_POPULATION_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-009";
	public static final String DEVICE_ONBOARD_DEVICE_FETCHING_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-010";
	public static final String DEVICE_ONBOARD_CLEAR_CONTEXT_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-011";
	public static final String DEVICE_ONBOARD_FILTER_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-012";
	public static final String DEVICE_ONBOARD_FILTER_LIST_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-013";
	public static final String DEVICE_ONBOARD_SEARCH_DEVICE_EXCEPTION = DEVICE_ONBOARD_EXP_CODE + "DOC-014";

	// Exceptions for User Registration - Iris & FingerPrint Capture
	public static final String USER_REG_IRIS_CAPTURE_PAGE_LOAD_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-001";
	public static final String USER_REG_IRIS_CAPTURE_NEXT_SECTION_LOAD_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-002";
	public static final String USER_REG_IRIS_CAPTURE_PREV_SECTION_LOAD_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-003";
	public static final String USER_REG_IRIS_CAPTURE_POPUP_LOAD_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-004";
	public static final String USER_REG_IRIS_VALIDATION_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-005";
	public static final String USER_REG_IRIS_SCORE_VALIDATION_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-006";
	public static final String USER_REG_IRIS_SCAN_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "IFC-001";
	public static final String USER_REG_FINGERPRINT_SCAN_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE + "FSC-003";
	public static final String USER_REG_FINGERPRINT_PAGE_LOAD_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE + "FCC-001";
	public static final String USER_REG_FINGERPRINT_CAPTURE_NEXT_SECTION_LOAD_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE
			+ "FCC-002";
	public static final String USER_REG_FINGERPRINT_CAPTURE_PREV_SECTION_LOAD_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE
			+ "FCC-003";
	public static final String USER_REG_FINGERPRINT_CAPTURE_POPUP_LOAD_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE
			+ "FCC-004";
	public static final String USER_REG_FINGERPRINT_VALIDATION_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE + "FCC-005";
	public static final String USER_REG_FINGERPRINT_SCORE_VALIDATION_EXP = USER_REG_FINGERPRINT_CAPTURE_EXP_CODE
			+ "FCC-006";
	public static final String USER_REG_IRIS_SAVE_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-008";
	public static final String USER_REG_GET_IRIS_QUALITY_SCORE_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-009";
	public static final String USER_REG_IRIS_STUB_IMAGE_EXP = USER_REG_IRIS_CAPTURE_EXP_CODE + "IFC-002";

	// Exception for Registration - Document Scan and Upload
	public static final String USER_REG_DOC_SCAN_UPLOAD_EXP = USER_REGISTRATION_EXP_CODE + "SCN-001";

	// Scan
	public static final String USER_REG_SCAN_EXP = USER_REG_SCAN_EXP_CODE + "DOC-001";

	// Regex Constants
	public static final String FULL_NAME_REGEX = "([A-z]+\\s?\\.?)+";
	public static final int FULL_NAME_LENGTH = 50;
	public static final String ADDRESS_LINE1_REGEX = "^.{1,50}$";
	public static final String MOBILE_NUMBER_REGEX = "\\d++";
	public static final int MOBILE_NUMBER_LENGTH = 9;
	public static final String EMAIL_ID_REGEX = "^([\\w\\-\\.]+)@((\\[([0-9]{1,3}\\.){3}[0-9]{1,3}\\])|(([\\w\\-]+\\.)+)([a-zA-Z]{2,4}))$";
	public static final String EMAIL_ID_REGEX_INITIAL = "([a-zA-Z]+\\.?\\-?\\@?(\\d+)?)+";
	public static final String CNI_OR_PIN_NUMBER_REGEX = "\\d{0,30}";
	public static final String AGE_REGEX = "\\d{1,2}";
	public static final String UIN_REGEX = "\\d{1,30}";
	public static final String POSTAL_CODE_REGEX = "\\d{5}";
	public static final String POSTAL_CODE_REGEX_INITIAL = "\\d{1,5}";

	// master sync
	public static final String MASTER_SYNC_SUCESS_MSG_CODE = "REG-MDS‌-001";
	public static final String MASTER_SYNC_OFFLINE_FAILURE_MSG_CODE = "REG-MDS‌-002";
	public static final String MASTER_SYNC_FAILURE_MSG_CODE = "REG-MDS‌-003";
	public static final String MASTER_SYNC_FAILURE_MSG_INFO = "Error in sync";
	public static final String MASTER_SYNC_FAILURE_MSG = "Sync failure";
	public static final String MASTER_SYNC_OFFLINE_FAILURE_MSG_INFO = "Client not online";
	public static final String MASTER_SYNC_OFFLINE_FAILURE_MSG = "You must be connected to the internet to sync data";
	public static final String MASTER_SYNC_EXCEPTION = "MASTER_SYNC_EXCEPTION";
	public static final String MASTER_SYNC_JOD_DETAILS = "MASTER_SYNC_JOB_DETAILS";
	public static final String MASTER_SYNC_SUCCESS = "Sync successful";
	public static final String MASTER_SYNC = "MASTER_SYNC";
	public static final String MASTER_VALIDATOR_SERVICE_NAME = "master_sync";
	public static final String MASTER_DATA_LASTUPDTAE = "lastUpdated";
	// POLICY SYNC
	public static final String POLICY_SYNC_SUCCESS_CODE = "REG-MDS‌-001 ";
	public static final String POLICY_SYNC_SUCCESS_MESSAGE = "Sync Successful";
	public static final String POLICY_SYNC_ERROR_CODE = "REG-MDS‌-003 ";
	public static final String POLICY_SYNC_ERROR_MESSAGE = "Sync failure";
	public static final String POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_CODE = "REG-MDS‌-002";
	public static final String POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_MESSAGE = "You must be connected to the internet to sync data";

	public static final String SYNCH_CONFIG_DATA_JOB_TITLE = "synch config data job";
	public static final String REG_USER_MAPPING_SYNC_JOB_TITLE = "registration user mapping sync job";

	// PRE-REG DELETE JOB
	public static final String PRE_REG_DELETE_SUCCESS = "Pre-Registration Records deleted";
	public static final String PRE_REG_DELETE_FAILURE = "Error While Deleting the records";

	// Connection Error
	public static final String CONNECTION_ERROR = "Unable to establish the connection";

	// Exceptions - Template Generator
	public static final String TEMPLATE_GENERATOR_ACK_RECEIPT_EXCEPTION = PACKET_CREATION_EXP_CODE + "TGE-002";
	public static final String TEMPLATE_GENERATOR_SMS_EXCEPTION = PACKET_CREATION_EXP_CODE + "TGE-002";

	// Jobs
	public static final String BATCH_JOB_START_SUCCESS_MESSAGE = "SYNC-DATA Process started successfully";
	public static final String START_SCHEDULER_ERROR_MESSAGE = "Unable to start SYNC-DATA process";
	public static final String BATCH_JOB_STOP_SUCCESS_MESSAGE = "Jobs stopped successfully";
	public static final String STOP_SCHEDULER_ERROR_MESSAGE = "Unable to stop SYNC-DATA Process";
	public static final String CURRENT_JOB_DETAILS_ERROR_MESSAGE = "Unable to fetch current running job details";
	public static final String EXECUTE_JOB_ERROR_MESSAGE = "Unable to execute job";
	public static final String SYNC_DATA_PROCESS_ALREADY_STARTED = "SYNC-DATA Process already running";
	public static final String SYNC_DATA_PROCESS_ALREADY_STOPPED = "SYNC-DATA Process not running to stop";
	public static final String SYNC_DATA_DTO = "SYNC-DATA DTO";
	public static final String JOB_RUNNING = "RUNNING";
	public static final String JOB_COMPLETED = "COMPLETED";
	public static final String NO_JOB_COMPLETED = "NO JOBS COMPLETED";
	public static final String NO_JOBS_TRANSACTION = "No Transaction History Available";
	public static final String NO_JOBS_RUNNING = "Currently No Jobs Running";
	public static final String JOB_UNKNOWN = "UNKNOWN";

	// Machine Mapping
	public static final String MACHINE_MAPPING_SUCCESS_MESSAGE = "User Mapped Successfully";
	public static final String MACHINE_MAPPING_ERROR_MESSAGE = "Unable to map user";
	public static final String MACHINE_MAPPING_ENTITY_SUCCESS_MESSAGE = "User Data Fetched Successfully";
	public static final String MACHINE_MAPPING_ENTITY_ERROR_NO_RECORDS = "No Records Found";
	public static final String DEVICE_MAPPING_SUCCESS_MESSAGE = "On-boarding successful";
	public static final String DEVICE_MAPPING_ERROR_MESSAGE = "Unable to map the device";

	// PACKET
	public static final String PACKET_STATUS_SYNC_ERROR_RESPONSE = "No Status Available";
	public static final String PACKET_STATUS_SYNC_SUCCESS_MESSAGE = "Packet Status Sync Successful";

	// OTP
	public static final String OTP_GENERATION_SUCCESS_MESSAGE = "Generated OTP is :";
	public static final String OTP_GENERATION_ERROR_MESSAGE = "Unable Generate OTP for entered username";
	public static final String OTP_VALIDATION_ERROR_MESSAGE = "Incorrect or expired OTP";

	// Packet Export
	public static final String FILE_EXPLORER_NAME = "File Explorer";
	public static final String INFO = "INFO";

	// Sync Status
	public static final String REG_PKT_APPRVL_CNT_EXCEED = "Maximum number of registration packets pending approval on client reached. Please approve or reject packets before proceeding with this registration";
	public static final String REG_PKT_APPRVL_TIME_EXCEED = "Maximum duration for registration packets pending approval on client reached.Please approve or reject packets before proceeding with this registration";
	public static final String OPT_TO_REG_TIME_EXPORT_EXCEED = "Time since last export of registration packets exceeded maximum limit. Please export or upload packets to server before proceeding with this registration";
	public static final String OPT_TO_REG_TIME_SYNC_EXCEED = "Time since last sync exceeded maximum limit. Please sync from server before proceeding with this registration";
	public static final String OPT_TO_REG_REACH_MAX_LIMIT = "Maximum limit for registration packets on client reached. Please export or upload packets to server before proceeding with this registration";
	public static final String OPT_TO_REG_OUTSIDE_LOCATION = "Your client machine location is outside the registration center. Please note that registration can be done only from within the registration centre";
	public static final String OPT_TO_REG_WEAK_GPS = "Unable to validate machine location due to weak GPS signal. Please try again";
	public static final String OPT_TO_REG_INSERT_GPS = "Unable to validate machine location. Please insert the GPS device and try again";
	public static final String OPT_TO_REG_GPS_PORT_MISMATCH = "Unable to validate machine location due to GPS port mismatch. Please insert into specific port and try again";

	public static final String POLICY_SYNC_SERVICE = "policysync";
	public static final String KEY_NAME = "mosip.registration.key_policy_sync_threshold_value";
	public static final String OPT_TO_REG_LAST_EXPORT_REG_PKTS_TIME = "mosip.registration.last_export_registration_config_time";

	// Reg Deletion
	public static final String REGISTRATION_DELETION_BATCH_JOBS_SUCCESS = "Registartion Packets Deletion Successful ";
	public static final String REGISTRATION_DELETION_BATCH_JOBS_FAILURE = "Registartion Packets Deletion Failure ";

	// Application Language
	public static final String APPLICATION_LANUAGE = "application_language";

	// Global-Config Constants
	public static final String GET_GLOBAL_CONFIG = "get_registration_center_config";
	public static final String REGISTRATION_CENTER_ID = "registrationcenterid";
	public static final String GLOBAL_CONFIG_ERROR_MSG = "please synch the data before starting the application";

	// user on boarding
	public static final String USER_ON_BOARDING_ERROR_RESPONSE = "failure";
	public static final String USER_ON_BOARDING_EXCEPTION = "USER_ON_BOARDING_EXCEPTION";
	public static final String USER_ON_BOARDING_EXCEPTION_MSG_CODE = "REG-URE‌-000";
	public static final String USER_ON_BOARDING_SUCCESS_CODE = "REG-URE‌-002";
	public static final String USER_ON_BOARDING_SUCCESS_MSG = "User on-boarded successfully.";
	public static final String USER_ON_BOARDING_THRESHOLD_NOT_MET_CODE = "REG-URE‌-008";
	public static final String USER_ON_BOARDING_THRESHOLD_NOT_MET_MSG = "Threshold for number of successful authentications not met.";
	public static final String USER_STATION_ID = "stationId";
	public static final String USER_CENTER_ID = "centerId";
	public static final String USER_MACHINE_VALIDATION_CODE = "REG-URE‌-009";
	public static final String USER_MACHINE_VALIDATION_MSG = "This computer is not mapped to the same Registration Centre as the dongle plugged in.";
	public static final String USER_ONBOARD_DATA = "UserOnboardBiometricData";
	public static final String USER_ON_BOARD_THRESHOLD_LIMIT = "USER_ON_BOARD_THRESHOLD_LIMIT";

	// Configuration Constants
	public static final String AUDIT_LOG_DELETION_CONFIGURED_DAYS = "mosip.registration.AUDIT_LOG_DELETION_CONFIGURED_DAYS";
	public static final String SYNC_TRANSACTION_NO_OF_DAYS_LIMIT = "mosip.registration.SYNC_TRANSACTION_NO_OF_DAYS_LIMIT";
	public static final String REG_DELETION_CONFIGURED_DAYS = "mosip.registration.REG_DELETION_CONFIGURED_DAYS";
	public static final String PRE_REG_DELETION_CONFIGURED_DAYS = "mosip.registration.PRE_REG_DELETION_CONFIGURED_DAYS";

	// Audit Constants
	public static final String AUDIT_LOGS_DELETION_SUCESS_MSG = "Audit Logs Deleted Successfully";
	public static final String AUDIT_LOGS_DELETION_FLR_MSG = "Audit Logs Deleted Failed";
	public static final String AUDIT_LOGS_DELETION_EMPTY_MSG = "No Audit Logs Found to be deleted";

	// Rest Authentication Constants

	public static final String USER_DTO = "userDTO";
	public static final String REST_OAUTH = "oauth";
	public static final String REST_OAUTH_USER_NAME = "userName";
	public static final String REST_OAUTH_USER_PSWD = "password";
	public static final String REST_OAUTH_ERROR_CODE = "REST-OAUTH-001";
	public static final String REST_OAUTH_ERROR_MSG = "Internal Server Error";
	public static final String REST_AUTHORIZATION = "authorization";
	public static final String REST_RESPONSE_BODY = "responseBody";
	public static final String REST_RESPONSE_HEADERS = "responseHeader";

	// Packet Sync
	public static final String PACKET_SYNC = "packet_sync";

	// Validations to ignore

	public static List<String> fieldsToExclude() {
		List<String> fieldToExclude = new ArrayList<>();
		fieldToExclude.add("preRegistrationId");
		fieldToExclude.add("virtualKeyboard");
		fieldToExclude.add("docPageNumber");
		fieldToExclude.add("residence");
		fieldToExclude.add("NFR");
		fieldToExclude.add("FR");
		fieldToExclude.add("residenceLocalLanguage");
		fieldToExclude.add("updateUinId");

		return fieldToExclude;

	}

	// Virus Scan
	public static final String VIRUS_SCAN_PACKET_NOT_FOUND = "FILE_NOT_PRESENT_FOR_SCAN";
	public static final String VIRUS_SCAN_INFECTED_FILES = "Infected Files";
	public static final String ANTIVIRUS_SERVICE_NOT_ACCESSIBLE = "Unable to access antivirus service";

	// concent of applicant
	public static final String CONCENT_OF_APPLICANT_SELECTED = "Yes";
	public static final String CONCENT_OF_APPLICANT_UNSELECTED = "No";

	// User Details
	public static final String USER_DETAILS_SERVICE_NAME = "user_details";
	public static final String MAPPER_UTILL = "MAPPER_UTILL";
	public static final String REG_ID = "regid";

	public static final String CONTENT_TYPE_EMAIL = "EMAIL";
	public static final String CONTENT_TYPE_MOBILE = "MOBILE";

	// Key-Policy Key validation
	public static final String VALID_KEY = "VALID KEY";
	public static final String INVALID_KEY = "Please sync to get latest key from the server before proceeding with this registration.";

	public static final String JOB_ID = "JOB_ID";

	public static final String SYNC_DATA_FREQ = "mosip.registration.SYNC_DATA_FREQ";

	public static final String LABEL = "Label";
	public static final Object UI_SYNC_DATA = "mosip.registration.UI_SYNC_DATA";
	public static final String MESSAGE = "Message";
	public static final String HASH = "#";
	public static final String DOB_MESSAGE = "dobMessage";
	public static final String DD = "dd";
	public static final String MM = "mm";
	public static final String YYYY = "yyyy";
	public static final String DOB = "dob";

	public static final String ATTR_INDIVIDUAL_TYPE = "individualTypeCode";
	public static final String ATTR_DATE_OF_BIRTH = "dateofbirth";
	public static final String ATTR_GENDER_TYPE = "genderCode";
	public static final String ATTR_NON_FORINGER = "NFR";
	public static final String ATTR_FORINGER = "FR";

}
