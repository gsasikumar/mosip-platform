package io.mosip.authentication.core.dto.vid;

import java.util.List;

import io.mosip.authentication.core.dto.indauth.AuthError;
import lombok.Data;

// TODO: Auto-generated Javadoc
// 
/**
 * This class is a response for the creation of VID.
 *
 * @author Arun Bose S
 */

/**
 * Instantiates a new VID response DTO.
 */
@Data
public class VIDResponseDTO {
	
	/** The id. */
	private String id;
	
	/** The version. */
	private String version;
	
	/** The response time. */
	private String responseTime;
	
	/** The vid. */
	private String vid;
	
	/** The error. */
	private List<AuthError> error;

}
