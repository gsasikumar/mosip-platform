package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data


public class MachineTypeRequestDto {
	
	private String id;
	private String ver;
	private String timestamp;
	private MachineTypeDtoData request;

}
