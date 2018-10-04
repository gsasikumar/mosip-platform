package io.mosip.authentication.service.impl.otpgen.validator;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;

/**
 * {@code OTPRequestValidator} do constraint validate of {@link OtpRequestDTO}
 * and enum atribute "idType" validation.
 * 
 * @author Rakesh Roshan
 */
@Component
@PropertySource("classpath:application-local.properties")
public class OTPRequestValidator implements Validator {

	@Autowired
	private SpringValidatorAdapter validator;

	@Autowired
	Environment env;

	@Override
	public boolean supports(Class<?> clazz) {
		return OtpRequestDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		OtpRequestDTO otpRequestDto = (OtpRequestDTO) target;

		validator.validate(otpRequestDto, errors);
		
		if (!isTimestampValid(otpRequestDto.getReqTime())) {
			errors.rejectValue("requestTime", IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorMessage());
		}

		if (!IdType.UIN.getType().equals(IdType.UIN.getType())
				|| !IdType.VID.getType().equals(IdType.VID.getType())) {

			errors.rejectValue("idType", IdAuthenticationErrorConstants.INVALID_IDTYPE.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_IDTYPE.getErrorMessage());
		}

	}

	private boolean isTimestampValid(Date timestamp) {

		Date reqTime = timestamp;
		Instant reqTimeInstance = reqTime.toInstant();
		Instant now = Instant.now();

		return Duration.between(reqTimeInstance, now).toMinutes() < env.getProperty("requestdate.received.in.max.time",
				Integer.class);

	}
}
