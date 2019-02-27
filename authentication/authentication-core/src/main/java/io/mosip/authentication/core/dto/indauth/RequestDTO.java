package io.mosip.authentication.core.dto.indauth;

import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
@Data
public class RequestDTO {

	/** variable to hold identity value */
	private IdentityDTO identity;
	
	/** variable to hold OTP and Static pin values */
	private AdditionalFactorsDTO additionalFactors;

}
