package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;

public abstract class AuthenticationValidatorImplementation {
	protected String fingerPrintType;

	protected RegistrationUserDetail registrationUserDetail;

	@Autowired
	protected FingerprintValidator fingerprintValidator;

	public abstract boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) throws RegBaseCheckedException;

	public String getFingerPrintType() {
		return fingerPrintType;
	}

	public void setFingerPrintType(String fingerPrintType) {
		this.fingerPrintType = fingerPrintType;
	}

}
