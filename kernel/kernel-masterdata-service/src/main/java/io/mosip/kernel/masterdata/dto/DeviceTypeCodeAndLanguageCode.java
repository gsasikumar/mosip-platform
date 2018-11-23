package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Response class for Device specification save 
 * 
 * @author Megha Tanga
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTypeCodeAndLanguageCode {
	
	private String id;
	private String deviceTypeCode;
	private String langCode;
	

}
