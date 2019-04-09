package io.mosip.kernel.syncdata.constant;

public enum MasterDataErrorCode {
	LAST_UPDATED_PARSE_EXCEPTION("KER_SYNC-100","Erro occurred while parsing lastUpdated timesatamp"),
	APPLICATION_FETCH_EXCEPTION("KER-SYNC-101","Error occurred while fetching Applications"), 
	MACHINE_DETAIL_FETCH_EXCEPTION("KER-SYNC-102","Error occurred while fetching Machine Details"), 
	MACHINE_REG_CENTER_FETCH_EXCEPTION("KER-SYNC-103","Error occurred while fetching Machine Registration Center"), 
	REG_CENTER_FETCH_EXCEPTION("KER-SYNC-104","Error occurred while fetching Registration Center"), 
	REG_CENTER_TYPE_FETCH_EXCEPTION("KER-SYNC-105", "Error occurred while fetching Registration Center Type"),
	TEMPLATE_FETCH_EXCEPTION("KER-SYNC-106", "Error occurred while fetching Templates"),
	TEMPLATE_TYPE_FETCH_EXCEPTION("KER-SYNC-107", "Error occurred while fetching Template Types"),
	REASON_CATEGORY_FETCH_EXCEPTION("KER-SYNC-108", "Error occurred while fetching Reason Category"),
	HOLIDAY_FETCH_EXCEPTION("KER-SYNC-109", "Error occurred while fetching Holidays"),
	BLACKLISTED_WORDS_FETCH_EXCEPTION("KER-SYNC-110", "Error occurred while fetching Blacklisted Words"),
	BIOMETRIC_TYPE_FETCH_EXCEPTION("KER-SYNC-111", "Error occurred while fetching Biometric types"),
	BIOMETRIC_ATTR_TYPE_FETCH_EXCEPTION("KER-SYNC-112", "Error occurred while fetching Biometric Attribute types"),
	TITLE_FETCH_EXCEPTION("KER-SYNC-113", "Error occurred while fetching Titles"),
	LANGUAGE_FETCH_EXCEPTION("KER-SYNC-114", "Error occurred while fetching Languages"),
	GENDER_FETCH_EXCEPTION("KER-SYNC-115", "Error occurred while fetching Genders"),
	REGISTARTION_CENTER_DEVICES_FETCH_EXCEPTION("KER-SYNC-116", "Error occurred while fetching Registration Center Devices"),
	DEVICES_FETCH_EXCEPTION("KER-SYNC-117", "Error occurred while fetching Devices"),
	DOCUMENT_CATEGORY_FETCH_EXCEPTION("KER-SYNC-118", "Error occurred while fetching Document Category"),
	DOCUMENT_TYPE_FETCH_EXCEPTION("KER-SYNC-119", "Error occurred while fetching Document Type"),
	ID_TYPE_FETCH_EXCEPTION("KER-SYNC-120", "Error occurred while fetching Id Type"),
	DEVICE_SPECIFICATION_FETCH_EXCEPTION("KER-SYNC-121", "Error occurred while fetching Device Specification"),
	MACHINE_SPECIFICATION_FETCH_EXCEPTION("KER-SYNC-122", "Error occurred while fetching Machine Specification"),
	MACHINE_TYPE_FETCH_EXCEPTION("KER-SYNC-123", "Error occurred while fetching Machine Type"),
	LOCATION_FETCH_EXCEPTION("KER-SYNC-124", "Error occurred while fetching Location"),
	DEVICE_TYPE_FETCH_EXCEPTION("KER-SYNC-125", "Error occurred while fetching Device Type"),
	VALID_DOCUMENT_FETCH_EXCEPTION("KER-SYNC-126", "Error occurred while fetching Valid Document Type"),
	REASON_LIST_FETCH_EXCEPTION("KER-SYNC-127", "Error occurred while fetching Valid Document Type"),
	THREAD_INTERRUPTED_WHILE_FETCH_EXCEPTION("KER-SYNC-128", "Error occurred while fetching data"),
	REQUEST_DATA_NOT_VALID("KER-SYNC-999","Request Data not valid"),
	REG_CENTER_MACHINE_FETCH_EXCEPTION("KER-SNC-129","Error occurred while fetching Registration Center Machine"),
	REG_CENTER_DEVICE_FETCH_EXCEPTION("KER-SNC-130","Error occurred while fetching Registration Center Device"),
	REG_CENTER_MACHINE_DEVICE_FETCH_EXCEPTION("KER-SNC-131","Error occurred while fetching Registration Center Machine Device"),
	REG_CENTER_USER_MACHINE_DEVICE_FETCH_EXCEPTION("KER-SNC-132","Error occurred while fetching Registration Center Machine Device"), 
	REG_CENTER_USER_FETCH_EXCEPTION("KER-SYNC-133","Error occurred while fetching Registration Center User"),
	MACHINE_ID_NOT_FOUND_EXCEPTION("KER-SNC-134","Machine id not found"),
	INTERNAL_SERVER_ERROR("KER-SYNC-500","Internal server error"),
	INVALID_TIMESTAMP_EXCEPTION("KER-SNC-135","Timestamp cannot be future date"),
	REG_CENTER_USER_HISTORY_FETCH_EXCEPTION("KER-SNC-136","Error occurred while fetching Registration Center User History"),
	REG_CENTER_MACHINE_USER_HISTORY_FETCH_EXCEPTION("KER-SYNC-137","Error occurred while fetching Registration Center Machine User History"),
	REG_CENTER_DEVICE_HISTORY_FETCH_EXCEPTION("KER-SYNC-138","Error occurred while fetching Registration Center Device History"),
	REG_CENTER_MACHINE_HISTORY_FETCH_EXCEPTION("KER-SYNC-139","Error occurred while fetching Registration Center Machine History"),
	EMPTY_MAC_OR_SERIAL_NUMBER("KER-SNC-140","Mac-Address and Serial Number cannot be empty"),
	REGISTRATION_CENTER_NOT_FOUND("KER-SNC-141","Registration center not found"),
	APP_AUTHORIZATION_METHOD_FETCH_EXCEPTION("KER-SNC-142","Error occurred while fetching app authorization methods"),
	APP_DETAIL_FETCH_EXCEPTION("KER-SNC-143","Error occurred while fetching app details"),
	APP_ROLE_PRIORITY_FETCH_EXCEPTION("KER-SNC-144","Error occurred while fetching app role priorities"),
	PROCESS_LIST_FETCH_EXCEPTION("KER-SNC-145","Error occurred while fetching processList"),
	SCREEN_AUTHORIZATION_FETCH_EXCEPTION("KER-SNC-146","Error occurred while fetching screen authorizations"),
	INDIVIDUAL_TYPE_FETCH_EXCEPTION("KER-SNC-147","Error occurred while fetching Individual types"),
	INVALID_MAC_OR_SERIAL_NUMBER("KER-SNC-148","Mac-Address and/or Serial Number does not exist	"),
	REG_CENTER_UPDATED("KER-SNC-149","Registration Center has been updated for the received Machine ID"),
	SCREEN_DETAIL_FETCH_EXCEPTION("KER-SNC-150","Error occured while fetching screen detail"),
	APPLICANT_VALID_DOCUMENT_FETCH_EXCEPTION("KER-SNC-151","Error occurred while fetching ApplicantValidDocument"),
	REG_CENTER_MACHINE_DEVICE_HISTORY_FETCH_EXCEPTION("KER-SNC-152","Error occurred while fetching Registration Center Machine Device History"),
	SYNC_JOB_DEF_FETCH_EXCEPTION("KER-SNC-153","Error occured while fetching sync job definitons"),
	SYNC_JOB_DEF_PARSE_EXCEPTION("KER-SNC-154","Error occured while parsing the response");
	
	private final String errorCode;
	private final String errorMessage;

	private MasterDataErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}
