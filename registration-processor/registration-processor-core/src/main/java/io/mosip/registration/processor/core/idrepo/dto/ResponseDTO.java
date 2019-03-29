package io.mosip.registration.processor.core.idrepo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class ResponseDTO.
 *
 * @author M1048358 Alok
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class ResponseDTO extends BaseRequestResponseDTO{

	/** The entity. */
	private String entity;

}
