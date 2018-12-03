

package io.mosip.kernel.masterdata.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Megha Tanga
 * @version 1.0.0
 */
@Data


public class DeviceSpecificationRequestDto {
	private String id;
	private String ver;
	private String timestamp;
	private DeviceSpecificationListDto request;
	
}

