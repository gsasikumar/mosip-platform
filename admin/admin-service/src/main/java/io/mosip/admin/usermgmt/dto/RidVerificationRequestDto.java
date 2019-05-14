package io.mosip.admin.usermgmt.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RidVerificationRequestDto {
	@NotBlank
	private String rid;
	@NotBlank
	private String userName;
}
