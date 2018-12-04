package io.mosip.registration.processor.rest.client.regcentermachine.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCenterResponseDto {
	private List<RegistrationCenterDto> registrationCenters;
}
