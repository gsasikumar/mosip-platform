package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.entity.RegistrationUserDetail;

public abstract class AuthenticationValidatorImplementation {
	protected String fingerPrintType;
	
	protected RegistrationUserDetail registrationUserDetail;
	
	@Autowired
	protected FingerprintValidator fingerprintValidator;
	
	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {
		return fingerprintValidator.validate(authenticationValidatorDTO);
	}

	public String getFingerPrintType() {
		return fingerPrintType;
	}

	public void setFingerPrintType(String fingerPrintType) {
		this.fingerPrintType = fingerPrintType;
	}

	
}
