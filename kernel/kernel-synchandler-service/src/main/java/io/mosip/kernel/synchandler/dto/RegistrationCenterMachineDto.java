package io.mosip.kernel.synchandler.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationCenterMachineDto {

	@NotNull
	@Size(min = 1, max = 36)
	private String regCenterId;

	@NotNull
	@Size(min = 1, max = 36)
	private String machineId;

	@NotNull
	private Boolean isActive;
}
