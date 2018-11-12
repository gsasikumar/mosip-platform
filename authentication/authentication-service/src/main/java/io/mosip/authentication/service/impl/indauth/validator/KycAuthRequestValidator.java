package io.mosip.authentication.service.impl.indauth.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.EkycAuthType;
import io.mosip.authentication.core.dto.indauth.KycAuthRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.spi.logger.MosipLogger;

/**
 * 
 * @author Prem Kumar
 * @author Dinesh Karuppiah.T
 *
 */

@Component
public class KycAuthRequestValidator extends BaseAuthRequestValidator {

	@Autowired
	private AuthRequestValidator authRequestValidator;

	/** The mosip logger. */
	private static MosipLogger mosipLogger = IdaLogger.getLogger(KycAuthRequestValidator.class);

	private static final String AUTH_REQUEST = "authRequest";

	/** The Constant INVALID_INPUT_PARAMETER. */
	private static final String INVALID_INPUT_PARAMETER = "INVALID_INPUT_PARAMETER - ";

	/** The Constant VALIDATE. */
	private static final String VALIDATE = "VALIDATE";

	/** The Constant ID_AUTH_VALIDATOR. */
	private static final String KYC_REQUEST_VALIDATOR = "AUTH_REQUEST_VALIDATOR";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	private static final String CONSENT_REQ = "consentReq";

	@Override
	public boolean supports(Class<?> clazz) {
		return KycAuthRequestDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		super.validate(target, errors);
		KycAuthRequestDTO kycAuthRequestDTO = (KycAuthRequestDTO) target;
		if (kycAuthRequestDTO != null) {
			validateConsentReq(kycAuthRequestDTO, errors);
			if (kycAuthRequestDTO.getAuthRequest() == null) {
				mosipLogger.error(SESSION_ID, KYC_REQUEST_VALIDATOR, VALIDATE, INVALID_INPUT_PARAMETER + AUTH_REQUEST);
				errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(),
								AUTH_REQUEST));
			} else {
				authRequestValidator.validate(kycAuthRequestDTO.getAuthRequest(), errors);
			}

			boolean isValidAuthtype = kycAuthRequestDTO.getEKycAuthType().chars().mapToObj(i -> (char) i)
					.map(String::valueOf)
					.allMatch(authTypeStr -> EkycAuthType.getEkycAuthType(authTypeStr).filter(eAuthType -> eAuthType
							.getAuthTypePredicate().test(kycAuthRequestDTO.getAuthRequest().getAuthType()))
							.isPresent());

			if (!isValidAuthtype) {
				mosipLogger.error(SESSION_ID, KYC_REQUEST_VALIDATOR, VALIDATE, INVALID_INPUT_PARAMETER + AUTH_REQUEST);
				errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								AUTH_REQUEST));
			}

		} else {
			mosipLogger.error(SESSION_ID, KYC_REQUEST_VALIDATOR, VALIDATE, INVALID_INPUT_PARAMETER + AUTH_REQUEST);
		}

	}

	public void validateConsentReq(KycAuthRequestDTO kycAuthRequestDTO, Errors errors) {
		if (!kycAuthRequestDTO.isConsentReq()) {
			errors.rejectValue(CONSENT_REQ, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
							CONSENT_REQ));
		}
	}

}
