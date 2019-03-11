package io.mosip.registration.processor.packet.service.dto;

import java.util.List;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestResponseDTO;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PacketGeneratorResponseDto extends BaseRestResponseDTO {

	/** The response. */
	private PackerGeneratorResDto response;

	/** The error. */
	private List<ErrorDTO> errors;
}
