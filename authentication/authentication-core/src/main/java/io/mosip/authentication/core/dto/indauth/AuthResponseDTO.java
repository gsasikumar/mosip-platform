package io.mosip.authentication.core.dto.indauth;

import lombok.Data;
import lombok.EqualsAndHashCode;

// TODO: Auto-generated Javadoc
/**
 * {@code AuthResponseDTO} is used for collect response from
 * core-kernel.Core-kernel get request from {@code AuthRequestDTO} and perform
 * operation.In result send
 * {@link AuthResponseDTO#info}
 * 
 * 
 * 
 * @author Rakesh Roshan
 */

/**
 * Instantiates a new auth response DTO.
 */
@Data

/* (non-Javadoc)
 * @see io.mosip.authentication.core.dto.indauth.BaseAuthResponseDTO#hashCode()
 */
@EqualsAndHashCode(callSuper = true)
public class AuthResponseDTO extends BaseAuthResponseDTO {

	//private AuthResponseInfo info;
	
	/**  The txnID value. */
	private String transactionID;
	
	/** Version. */
	private String version;
	

	/** Static token. */
	private String staticToken;
	
	/** The id. */
	private String id;


}
