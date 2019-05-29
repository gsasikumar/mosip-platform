package io.mosip.preregistration.core.errorcodes;

/**
 * Error codes
 * 
 * @author M1037717
 *
 */
public enum ErrorCodes {
	PRG_CORE_REQ_001("PRG_PAM_CORE_001"), 
	PRG_CORE_REQ_002("PRG_PAM_CORE_002"), 
	PRG_CORE_REQ_003("PRG_PAM_CORE_003"),
	PRG_CORE_REQ_004("PRG_PAM_CORE_004"),
	PRG_CORE_REQ_005("PRG_PAM_CORE_005"),
	PRG_CORE_REQ_006("PRG_PAM_CORE_006"), 
	PRG_CORE_REQ_007("PRG_PAM_CORE_007"),
	PRG_CORE_REQ_008("PRG_PAM_CORE_008"),
	PRG_CORE_REQ_009("PRG_PAM_CORE_009"),
	PRG_CORE_REQ_010("PRG_PAM_CORE_010"),
	PRG_CORE_REQ_011("PRG_PAM_CORE_011"),
	PRG_CORE_REQ_012("PRG_PAM_CORE_012"),
	PRG_CORE_REQ_013("PRG_CORE_REQ_013"),
	PRG_CORE_REQ_014("PRG_CORE_REQ_014"),
	PRG_CORE_REQ_015("PRG_CORE_REQ_015"),
	PRG_CORE_REQ_016("PRG_CORE_REQ_016");


	private ErrorCodes(String code) {
		this.code = code;
	}

	private final String code;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
}
