package io.mosip.authentication.core.dto.otpgen;

import lombok.Data;

/**
 * 
 * 
 * @author Dinesh Karuppiah.T
 * 
 */

@Data
public class MaskedResponseDTO {

	/**
	 * masked mobile(i.e XXXXXXX123) number where send OTP
	 */
	private String maskedMobile;

	/**
	 * masked email id(raXXXXXXXXXan@xyz.com) where send OTP
	 */
	private String maskedEmail;

}
