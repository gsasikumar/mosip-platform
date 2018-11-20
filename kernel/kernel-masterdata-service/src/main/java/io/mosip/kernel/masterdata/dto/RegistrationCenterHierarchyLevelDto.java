package io.mosip.kernel.masterdata.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCenterHierarchyLevelDto {
	private String id;
	
	private String name;
	
	private String addressLine1;

	private String addressLine2;

	private String addressLine3;

	private String latitude;

	private String longitude;
	
	private String centerTypeCode;
	
	private Boolean isActive;
	
	private String workingHours;
	
	private String contactPhone;
	
	private Short numberOfKiosks;
	
	private LocalTime perKioskProcessTime;
	
	private LocalTime processStartTime;
	
	private LocalTime processEndTime;
}