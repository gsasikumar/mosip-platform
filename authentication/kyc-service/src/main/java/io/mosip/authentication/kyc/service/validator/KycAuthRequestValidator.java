package io.mosip.authentication.kyc.service.validator;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.common.service.validator.BaseAuthRequestValidator;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.EkycAuthType;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class For KycAuthRequestValidator extending the
 * BaseAuthRequestValidator{@link BaseAuthRequestValidator}}
 *
 * @author Prem Kumar
 * @author Dinesh Karuppiah.T
 * 
 * 
 */

@Component
public class KycAuthRequestValidator extends BaseAuthRequestValidator {
	
	/** The Constant SECONDARY_LANG_CODE. */
	private static final String SECONDARY_LANG_CODE = "secondaryLangCode";
	
	

	/** The auth request validator. */
	@Autowired
	private AuthRequestValidator authRequestValidator;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(KycAuthRequestValidator.class);



	

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return KycAuthRequestDTO.class.equals(clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		super.validate(target, errors);
		KycAuthRequestDTO kycAuthRequestDTO = (KycAuthRequestDTO) target;
		if (kycAuthRequestDTO != null) {
			BeanPropertyBindingResult authErrors = new BeanPropertyBindingResult(kycAuthRequestDTO,
					errors.getObjectName());
			authRequestValidator.validate(kycAuthRequestDTO, authErrors);
			errors.addAllErrors(authErrors);

			if (!errors.hasErrors()) {
				validateConsentReq(kycAuthRequestDTO, errors);
			}

			if (!errors.hasErrors()) {
				validateAuthType(errors, kycAuthRequestDTO);
				validateLangCode(kycAuthRequestDTO.getSecondaryLangCode(), errors, SECONDARY_LANG_CODE);
			}

		} else {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), IdAuthCommonConstants.VALIDATE,
					IdAuthCommonConstants.INVALID_INPUT_PARAMETER + IdAuthCommonConstants.REQUESTEDAUTH);
			errors.rejectValue(IdAuthCommonConstants.REQUESTEDAUTH, IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), IdAuthCommonConstants.REQUESTEDAUTH));
		}

	}
	
	/**
	 * Validates the KycAuthrequest against the Authtype on the request.
	 *
	 * @param errors            the errors
	 * @param kycAuthRequestDTO the kyc auth request DTO
	 */
	private void validateAuthType(Errors errors, KycAuthRequestDTO kycAuthRequestDTO) {
		String values = env.getProperty(IdAuthConfigKeyConstants.EKYC_ALLOWED_AUTH_TYPE);
		List<String> allowedAuthTypesList = Arrays.stream(values.split(",")).collect(Collectors.toList());
		Map<Boolean, List<EkycAuthType>> authTypes = Stream.of(EkycAuthType.values()).collect(
				Collectors.partitioningBy(ekycAuthType -> allowedAuthTypesList.contains(ekycAuthType.getType())));
		List<EkycAuthType> allowedAuthTypes = authTypes.get(Boolean.TRUE);
		List<EkycAuthType> notAllowedAuthTypes = authTypes.get(Boolean.FALSE);

		boolean noNotAllowedAuthTypeEnabled = notAllowedAuthTypes.stream().noneMatch(
				ekycAuthType -> ekycAuthType.getAuthTypePredicate().test(kycAuthRequestDTO.getRequestedAuth()));
		boolean anyAllowedAuthTypeEnabled = allowedAuthTypes.stream().anyMatch(
				ekycAuthType -> ekycAuthType.getAuthTypePredicate().test(kycAuthRequestDTO.getRequestedAuth()));
		boolean isValidAuthtype = noNotAllowedAuthTypeEnabled && anyAllowedAuthTypeEnabled;
		if (!isValidAuthtype) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), IdAuthCommonConstants.VALIDATE,
					IdAuthCommonConstants.INVALID_INPUT_PARAMETER + IdAuthCommonConstants.REQUESTEDAUTH);
			String notAllowedAuthTypesStr = notAllowedAuthTypes.stream().map(EkycAuthType::getType).collect(Collectors.joining(","));
			errors.rejectValue(IdAuthCommonConstants.REQUESTEDAUTH, IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(), String
					.format(IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage(), notAllowedAuthTypesStr));
		}

	}

}
