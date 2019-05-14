package io.mosip.admin.accountmgmt.constant;

public enum AccountManagementErrorCode {
   INTERNAL_SERVER_ERROR("ADM-ACM-100","Runtime exception"),
   REST_SERVICE_EXCEPTION("ADM-ACM-101","Error occured while fetching username"),
   PARSE_EXCEPTION("ADM-ACM-102","Error occured while parsing username");
	
	private final String errorCode;
	private final String errorMessage;

	private AccountManagementErrorCode(String errorCode, String errorMessage) {
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
