package io.mosip.registration.processor.core.token.validation.dto;

import java.util.Arrays;

import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InvalidTokenResponseDTO {
	Errors[] errors;
	
	public Errors[] getErrors() {
		if(errors!=null)
			return Arrays.copyOf(errors, errors.length);
		return null;
	}

	public void setErrors(Errors[] errors) {
		this.errors = errors!=null?errors:null;
	}
	
	String timestamp;
	int status;
}