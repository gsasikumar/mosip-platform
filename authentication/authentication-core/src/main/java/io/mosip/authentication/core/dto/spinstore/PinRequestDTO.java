package io.mosip.authentication.core.dto.spinstore;

import lombok.Data;

/**
 * This Class Provides the Values of StaticPin and StaticPinIdentityDTO
 * 
 * @author Prem Kumar
 *
 */
@Data
public class PinRequestDTO {
	
	/** The value StaticPinIdentityDTO */
	private StaticPinIdentityDTO identity;
	
	/** The value pinValue */
	private String staticPin;
}
