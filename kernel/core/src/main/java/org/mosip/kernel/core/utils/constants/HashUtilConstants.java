package org.mosip.kernel.core.utils.constants;

public enum HashUtilConstants {
	MOSIP_ILLEGAL_ARGUMENT_INITIALODDNUMBER_ERROR_CODE("COK-UTL-HAS-001","Entered initialOddNumber is even"),
	MOSIP_ILLEGAL_ARGUMENT_MULTIPLIERODDNUMBER_ERROR_CODE("COK-UTL-HAS-002","Entered multiplierOddNumber is even");
	
	public final String errorCode;
	public final String errorMessage;

	HashUtilConstants(String string1,String string2) {
		this.errorCode = string1;
		this.errorMessage = string2;
	}

	public String getErrorCode() {
		return errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	
}
