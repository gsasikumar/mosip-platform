package io.mosip.authentication.service.impl.indauth.validator;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BaseAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.BioIdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.DataDTO;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.InternalAuthType;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.service.impl.indauth.service.bio.BioMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.DOBType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoAuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.pin.PinMatchType;
import io.mosip.authentication.service.integration.MasterDataManager;
import io.mosip.authentication.service.validator.IdAuthValidator;
import io.mosip.kernel.core.datavalidator.exception.InvalidPhoneNumberException;
import io.mosip.kernel.core.datavalidator.exception.InvalideEmailException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.datavalidator.email.impl.EmailValidatorImpl;
import io.mosip.kernel.datavalidator.phone.impl.PhoneValidatorImpl;

/**
 * The Class BaseAuthRequestValidator.
 *
 * @author Manoj SP
 * @author Prem Kumar
 * @author RakeshRoshan
 * 
 */
public class BaseAuthRequestValidator extends IdAuthValidator {

	private static final String OTP2 = "OTP";

	private static final String REQUEST_ADDITIONAL_FACTORS_STATIC_PIN = "request/additionalFactors/staticPin";

	private static final String REQUEST_ADDITIONAL_FACTORS_TOTP = "request/additionalFactors/totp";

	private static final String BIO_TYPE = "biotype";

	/** The Final Constant For PIN_VALUE */
	private static final String PIN_VALUE = "pinValue";

	/** The Final Constant For MODEL */
	private static final String DEVICE_PROVIDER_ID = "deviceProviderID";

	// TODO remove the below properties and the commented fields.
//	/** The Final Constant For FINGERPRINT_PROVIDER_ALL */
//	private static final String FINGERPRINT_PROVIDER_ALL = "fingerprint.provider.all";
//
//	/** The Final Constant For IRIS_PROVIDER_ALL */
//	private static final String IRIS_PROVIDER_ALL = "iris.provider.all";

	/** The Final Constant For deviceId */
	private static final String DEVICE_ID = "Device Id";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(BaseAuthRequestValidator.class);

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	/** The Constant VALIDATE. */
	private static final String VALIDATE = "VALIDATE";

	/** The Constant PRIMARY_LANG_CODE. */
	private static final String PRIMARY_LANG_CODE = "mosip.primary.lang-code";

	/** The Constant INVALID_INPUT_PARAMETER. */
	private static final String INVALID_INPUT_PARAMETER = "INVALID_INPUT_PARAMETER - ";

	/** The Constant VALIDATE_CHECK_OTP_AUTH. */
	private static final String VALIDATE_CHECK_OTP_AUTH = "validate -> checkOTPAuth";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant OTP_LENGTH. */
	private static final Integer OTP_LENGTH = 6;

	/** The Constant finger. */
	private static final String FINGER = "finger";

	/** The Constant iris. */
	private static final String IRIS = "iris";

	/** The Constant face. */
	private static final String FACE = "face";

	/** The Constant IdentityInfoDTO. */
	private static final String IDENTITY_INFO_DTO = "IdentityInfoDTO";

	/** The Constant PATTERN. */
	private static final Pattern STATIC_PIN_PATTERN = Pattern.compile("^[0-9]{6}");

	/** The Constant AUTH_TYPE. */
	private static final String AUTH_TYPE = "requestedAuth";


	/** email Validator */
	@Autowired
	EmailValidatorImpl emailValidatorImpl;

	/** phone Validator */
	@Autowired
	PhoneValidatorImpl phoneValidatorImpl;

	/** The id info helper. */
	@Autowired
	protected IdInfoHelper idInfoHelper;

	@Autowired
	private MasterDataManager masterDataManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return BaseAuthRequestDTO.class.isAssignableFrom(clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object req, Errors errors) {
		BaseAuthRequestDTO baseAuthRequestDTO = (BaseAuthRequestDTO) req;

		if (baseAuthRequestDTO != null) {
			validateId(baseAuthRequestDTO.getId(), errors);
		}
		
		
	}

	/**
	 * validates the Static Pin Details
	 * 
	 * @param authRequestDTO
	 * @param errors
	 */
	protected void validateAdditionalFactorsDetails(AuthRequestDTO authRequestDTO, Errors errors) {
		AuthTypeDTO authTypeDTO = authRequestDTO.getRequestedAuth();

		if ((authTypeDTO != null && authTypeDTO.isPin() && isMatchtypeEnabled(PinMatchType.SPIN))) {

			Optional<String> pinOpt = Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getStaticPin);

			if (!pinOpt.isPresent()) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
						"Missing pinval in the request");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
						new Object[] { REQUEST_ADDITIONAL_FACTORS_STATIC_PIN },
						IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
			} else {
				checkAdditionalFactorsValue(pinOpt, PIN_VALUE, errors);
			}
		} else if ((authTypeDTO != null && authTypeDTO.isOtp() && isMatchtypeEnabled(PinMatchType.OTP))) {
			Optional<String> otp = Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getOtp);

			if (!otp.isPresent()) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
						"Missing OTP value in the request");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { REQUEST_ADDITIONAL_FACTORS_TOTP },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			} else {
				checkAdditionalFactorsValue(otp, OTP2, errors);
			}
		}
	}

	/**
	 * checks the static Pin value.
	 * 
	 * @param pinInfo
	 * @param errors
	 */
	private void checkAdditionalFactorsValue(Optional<String> info, String type, Errors errors) {
		if (!STATIC_PIN_PATTERN.matcher(info.get()).matches()) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, "Invalid Input Static pin Value");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { type }, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validate Biometric details i.e validating fingers,iris,face and device
	 * information.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	protected void validateBioMetadataDetails(AuthRequestDTO authRequestDTO, Errors errors) {

		AuthTypeDTO authTypeDTO = authRequestDTO.getRequestedAuth();

		if ((authTypeDTO != null && authTypeDTO.isBio())) {

			List<BioIdentityInfoDTO> bioInfo = authRequestDTO.getRequest().getBiometrics();

			if (bioInfo == null || bioInfo.isEmpty() || bioInfo.stream().anyMatch(bioDto -> bioDto.getData() == null)) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, "missing biometric request");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_BIOMETRICDATA.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.MISSING_BIOMETRICDATA.getErrorMessage(), REQUEST));
			} else {
				
				List<DataDTO> bioData = bioInfo.stream().map(BioIdentityInfoDTO::getData).collect(Collectors.toList());

				validateDeviceInfo(bioData, errors);

				validateBioType(bioData, errors);

				if (isAuthtypeEnabled(BioAuthType.FGR_MIN, BioAuthType.FGR_IMG, BioAuthType.FGR_MIN_MULTI)) {
					validateFinger(authRequestDTO, bioData, errors);
				}
				if (isAuthtypeEnabled(BioAuthType.IRIS_IMG, BioAuthType.IRIS_COMP_IMG)) {
					validateIris(authRequestDTO, bioData, errors);
				}
				if (isMatchtypeEnabled(BioMatchType.FACE)) {
					validateFace(authRequestDTO, bioData, errors);
				}

			}
		}

	}

	/**
	 * Validates the BioType value
	 * 
	 * @param bioInfo
	 * @param errors
	 */
	private void validateBioType(List<DataDTO> bioInfo, Errors errors) {
		AuthType[] authTypes = BioAuthType.values();
		Set<String> availableAuthTypeInfos = new HashSet<>();
		for (AuthType authType : authTypes) {
			availableAuthTypeInfos.add(authType.getType());
		}
		for (DataDTO bioInfos : bioInfo) {
			if (bioInfos.getBioType() == null || bioInfos.getBioType().isEmpty()) {
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { BIO_TYPE },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			} else if (!availableAuthTypeInfos.contains(bioInfos.getBioType())) {
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_BIOTYPE.getErrorCode(),
						new Object[] { bioInfos.getBioType() },
						IdAuthenticationErrorConstants.INVALID_BIOTYPE.getErrorMessage());
			}

		}

	}

	/**
	 * validates the DeviceInfo
	 * 
	 * @param bioInfos
	 * @param errors
	 */
	private void validateDeviceInfo(List<DataDTO> bioInfos, Errors errors) {
		if (!isContaindeviceProviderID(bioInfos)) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					"missing biometric deviceProviderID Info request");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { DEVICE_PROVIDER_ID },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * check model attribute is empty or null
	 * 
	 * @param bioInfos
	 * @return
	 */
	private boolean isContaindeviceProviderID(List<DataDTO> bioInfos) {
		return bioInfos.parallelStream().allMatch(
				deviceInfo -> deviceInfo.getDeviceProviderID() != null && !deviceInfo.getDeviceProviderID().isEmpty());
	}

	/**
	 * Validate fingers.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param bioInfo        the bio info
	 * @param errors         the errors
	 */
	private void validateFinger(AuthRequestDTO authRequestDTO, List<DataDTO> bioInfo, Errors errors) {
		if ((isAvailableBioType(bioInfo, BioAuthType.FGR_MIN)
				&& isDuplicateBioType(authRequestDTO, BioAuthType.FGR_MIN))) {
			checkAtleastOneFingerRequestAvailable(authRequestDTO, errors, BioAuthType.FGR_MIN.getType());
			if (!errors.hasErrors()) {
				validateFingerRequestCount(authRequestDTO, errors, BioAuthType.FGR_MIN.getType());
			}
		}
		if ((isAvailableBioType(bioInfo, BioAuthType.FGR_IMG)
				&& isDuplicateBioType(authRequestDTO, BioAuthType.FGR_IMG))) {
			checkAtleastOneFingerRequestAvailable(authRequestDTO, errors, BioAuthType.FGR_IMG.getType());
			if (!errors.hasErrors()) {
				validateFingerRequestCount(authRequestDTO, errors, BioAuthType.FGR_IMG.getType());
			}
		}
	}

	/**
	 * Validates the Iris parameters present in thr request.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param bioInfo        the bio info
	 * @param errors         the errors
	 */
	private void validateIris(AuthRequestDTO authRequestDTO, List<DataDTO> bioInfo, Errors errors) {
		if (isAvailableBioType(bioInfo, BioAuthType.IRIS_IMG)
				&& isDuplicateBioType(authRequestDTO, BioAuthType.IRIS_IMG)) {

			checkAtleastOneIrisRequestAvailable(authRequestDTO, errors);
			if (!errors.hasErrors()) {
				validateIrisRequestCount(authRequestDTO, errors);
				validateMultiIrisValue(authRequestDTO, errors);
			}

		}
	}

	/**
	 * Validation for MultiIris Values present in the request
	 * 
	 * @param authRequestDTO
	 * @param errors
	 */
	private void validateMultiIrisValue(AuthRequestDTO authRequestDTO, Errors errors) {
		if (isDuplicateBioValue(authRequestDTO, BioAuthType.IRIS_IMG.getType())) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, "Duplicate IRIS in request");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.DUPLICATE_IRIS.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.DUPLICATE_IRIS.getErrorMessage(), REQUEST));
		}
	}

	private boolean isDuplicateBioValue(AuthRequestDTO authRequestDTO, String type) {
		Map<String, Long> countsMap = getBioValueCounts(authRequestDTO, type);
		return hasDuplicate(countsMap);
	}

	private boolean hasDuplicate(Map<String, Long> countsMap) {
		return countsMap.entrySet().stream().anyMatch(entry -> entry.getValue() > 1);
	}

	private Map<String, Long> getBioSubtypeCounts(AuthRequestDTO authRequestDTO, String type) {
		return getBioSubtypeCount(getBioIds(authRequestDTO, type));
	}

	private Map<String, Long> getBioValueCounts(AuthRequestDTO authRequestDTO, String type) {
		return getBioValuesCount(getBioIds(authRequestDTO, type));
	}

	private List<BioIdentityInfoDTO> getBioIds(AuthRequestDTO authRequestDTO, String type) {
		List<BioIdentityInfoDTO> identity = Optional.ofNullable(authRequestDTO.getRequest())
				.map(RequestDTO::getBiometrics).orElseGet(Collections::emptyList);
		if (!identity.isEmpty()) {
			List<BioIdentityInfoDTO> idendityInfoList = identity.stream().filter(Objects::nonNull)
					.filter(bioId -> bioId.getData().getBioType().equalsIgnoreCase(type)).collect(Collectors.toList());
			return idendityInfoList;
		}
		return Collections.emptyList();
	}

	/**
	 * Validate Face.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param bioInfo        the bio info
	 * @param errors         the errors
	 */
	private void validateFace(AuthRequestDTO authRequestDTO, List<DataDTO> bioInfo, Errors errors) {

		if (isAvailableBioType(bioInfo, BioAuthType.FACE_IMG)
				&& isDuplicateBioType(authRequestDTO, BioAuthType.FACE_IMG)) {

			checkAtleastOneFaceRequestAvailable(authRequestDTO, errors);
		}
	}

	/**
	 * validate atleast one finger request should be available for Bio.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void checkAtleastOneFingerRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors,
			String bioAuthType) {

		boolean isAtleastOneFingerRequestAvailable = checkAnyBioIdAvailable(authRequestDTO, bioAuthType);
		if (!isAtleastOneFingerRequestAvailable) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, "finger request is not available");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
					new Object[] { FINGER }, IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
		}

	}

	private boolean checkAnyBioIdAvailable(AuthRequestDTO authRequestDTO, String type) {
		return Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getBiometrics)
				.filter(list -> list.stream().anyMatch(bioId -> bioId.getData().getBioType().equalsIgnoreCase(type))).isPresent();
	}

	/**
	 * validate atleast one Iris request should be available for Bio.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void checkAtleastOneIrisRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {
		boolean isIrisRequestAvailable = checkAnyBioIdAvailable(authRequestDTO, BioAuthType.IRIS_IMG.getType());
		if (!isIrisRequestAvailable) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, "iris request is not available");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
					new Object[] { IRIS }, IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
		}
	}

	/**
	 * validate atleast one Face request should be available for Bio.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void checkAtleastOneFaceRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {

		boolean isFaceRequestAvailable = checkAnyBioIdAvailable(authRequestDTO, BioAuthType.FACE_IMG.getType());
		if (!isFaceRequestAvailable) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, "face request is not available");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
					new Object[] { FACE }, IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
		}
	}

	/**
	 * check any IdentityInfoDto data available or not.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param functions      the functions
	 * @return true, if successful
	 */
	@SuppressWarnings("unchecked")
	boolean checkAnyIdInfoAvailable(AuthRequestDTO authRequestDTO,
			Function<IdentityDTO, List<IdentityInfoDTO>>... functions) {
		return Stream.<Function<IdentityDTO, List<IdentityInfoDTO>>>of(functions).anyMatch(func -> Optional
				.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getDemographics).map(func)
				.filter(list -> list != null && !list.isEmpty()
						&& list.stream().allMatch(idDto -> idDto.getValue() != null && !idDto.getValue().isEmpty()))
				.isPresent());
	}

	/**
	 * If DemoAuthType is Bio, then validate bioinfo is available or not.
	 *
	 * @param bioInfoList the bio info list
	 * @param bioType     the bio type
	 * @return true, if is available bio type
	 */
	private boolean isAvailableBioType(List<DataDTO> bioInfoList, BioAuthType bioType) {
		return bioInfoList.parallelStream().filter(bio -> bio.getBioType() != null && !bio.getBioType().isEmpty())
				.anyMatch(bio -> bio.getBioType().equals(bioType.getType()));
	}

	/**
	 * If DemoAuthType is Bio, then check same bio request type should not be
	 * requested again.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param bioType        the bio type
	 * @return true, if is duplicate bio type
	 */
	private boolean isDuplicateBioType(AuthRequestDTO authRequestDTO, BioAuthType bioType) {
		List<BioIdentityInfoDTO> bioInfo = authRequestDTO.getRequest().getBiometrics();
		List<DataDTO> bioData = bioInfo.stream().map(BioIdentityInfoDTO::getData).collect(Collectors.toList());;
		Long bioTypeCount = Optional.ofNullable(bioData).map(List::parallelStream)
				.map(stream -> stream
						.filter(bio -> bio.getBioType().isEmpty() && bio.getBioType().equals(bioType.getType()))
						.count())
				.orElse((long) 0);

		return bioTypeCount <= 1;

	}

	/**
	 * If DemoAuthType is Bio, Then check duplicate request of finger and number
	 * finger of request should not exceed to 10.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void validateFingerRequestCount(AuthRequestDTO authRequestDTO, Errors errors, String bioType) {
		Map<String, Long> fingerSubtypesCountsMap = getBioSubtypeCounts(authRequestDTO, bioType);
		boolean anyInfoIsMoreThanOne = hasDuplicate(fingerSubtypesCountsMap);
		Map<String, Long> fingerValuesCountsMap = getBioValueCounts(authRequestDTO, bioType);
		boolean anyValueIsMoreThanOne = hasDuplicate(fingerValuesCountsMap);

		if (anyInfoIsMoreThanOne || anyValueIsMoreThanOne) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, "Duplicate fingers ");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorMessage(), REQUEST));
		}

		Long fingerCountExceeding = fingerSubtypesCountsMap.values().stream().mapToLong(l -> l).sum();
		if (fingerCountExceeding > 2) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, "finger count is exceeding to 2");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorMessage(), REQUEST));
		}
	}

	private Map<String, Long> getBioSubtypeCount(List<BioIdentityInfoDTO> idendityInfoList) {
		Map<String, Long> countsMap = idendityInfoList.stream()
				.map(BioIdentityInfoDTO :: getData)
				.collect(Collectors.groupingBy(DataDTO::getBioSubType, Collectors.counting()));
		return countsMap;

	}

	private Map<String, Long> getBioValuesCount(List<BioIdentityInfoDTO> idendityInfoList) {
		Map<String, Long> countsMap = idendityInfoList.stream()
				.map(BioIdentityInfoDTO :: getData)
				.collect(Collectors.groupingBy(DataDTO::getBioValue, Collectors.counting()));
		return countsMap;

	}

	/**
	 * validate Iris request count. left and right eye should not exceed 1 and total
	 * iris should not exceed 2.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void validateIrisRequestCount(AuthRequestDTO authRequestDTO, Errors errors) {
		Map<String, Long> irisSubtypeCounts = getBioSubtypeCounts(authRequestDTO,BioAuthType.IRIS_IMG.getType());
		if (irisSubtypeCounts.values().stream().anyMatch(count -> count > 1)) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					"Iris : either left eye or right eye count is more than 1.");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.IRIS_EXCEEDING.getErrorCode(),
					new Object[] { IRIS }, IdAuthenticationErrorConstants.IRIS_EXCEEDING.getErrorMessage());
		}

	}

	/**
	 * Check demo auth.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	protected void checkDemoAuth(AuthRequestDTO authRequest, Errors errors) {
		AuthType[] authTypes = DemoAuthType.values();
		Set<String> availableAuthTypeInfos = new HashSet<>();
		boolean hasMatch = false;
		for (AuthType authType : authTypes) {
			if (authType.isAuthTypeEnabled(authRequest, idInfoHelper)) {
				Set<MatchType> associatedMatchTypes = authType.getAssociatedMatchTypes();
				for (MatchType matchType : associatedMatchTypes) {
					if (isMatchtypeEnabled(matchType)) {
						List<IdentityInfoDTO> identityInfos = matchType.getIdentityInfoList(authRequest.getRequest());
						if (identityInfos != null && !identityInfos.isEmpty()) {
							availableAuthTypeInfos.add(authType.getType());
							hasMatch = true;
							checkIdentityInfoValue(identityInfos, errors);
							checkLangaugeDetails(matchType, identityInfos, errors);
						}
					}

				}
			}
		}

		if (!hasMatch) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, "Missing IdentityInfoDTO");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDENTITY_INFO_DTO },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		} else {
			checkOtherValues(authRequest, errors, availableAuthTypeInfos);
		}
	}

	/**
	 * Check identity info value.
	 *
	 * @param identityInfos the identity infos
	 * @param errors        the errors
	 */
	private void checkIdentityInfoValue(List<IdentityInfoDTO> identityInfos, Errors errors) {
		for (IdentityInfoDTO identityInfoDTO : identityInfos) {
			if (Objects.isNull(identityInfoDTO.getValue())) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, "IdentityInfoDTO is invalid");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { IDENTITY_INFO_DTO },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}

		}

	}

	/**
	 * Check other values.
	 *
	 * @param authRequest            the auth request
	 * @param errors                 the errors
	 * @param availableAuthTypeInfos
	 * @param hasMatch               the has match
	 */
	private void checkOtherValues(AuthRequestDTO authRequest, Errors errors, Set<String> availableAuthTypeInfos) {
		if (isMatchtypeEnabled(DemoMatchType.DOB)) {
			checkDOB(authRequest, errors);
		}

		if (isMatchtypeEnabled(DemoMatchType.DOBTYPE)) {
			checkDOBType(authRequest, errors);
		}

		if (isMatchtypeEnabled(DemoMatchType.AGE)) {
			checkAge(authRequest, errors);
		}

		if (isMatchtypeEnabled(DemoMatchType.GENDER)) {
			checkGender(authRequest, errors);
		}

		if (isMatchtypeEnabled(DemoMatchType.EMAIL)) {
			validateEmail(authRequest, errors);
		}

		if (isMatchtypeEnabled(DemoMatchType.PHONE)) {
			validatePhone(authRequest, errors);
		}

		if (isAuthtypeEnabled(DemoAuthType.ADDRESS, DemoAuthType.FULL_ADDRESS)) {
			validateAdAndFullAd(availableAuthTypeInfos, errors);
		}

	}

	private boolean isMatchtypeEnabled(MatchType matchType) {
		return idInfoHelper.isMatchtypeEnabled(matchType);
	}

	private boolean isAuthtypeEnabled(AuthType... authTypes) {
		return Stream.of(authTypes).anyMatch(
				authType -> authType.getAssociatedMatchTypes().stream().anyMatch(idInfoHelper::isMatchtypeEnabled));
	}

	/**
	 * Validate ad and full ad.
	 *
	 * @param availableAuthTypeInfos the available auth type infos
	 * @param errors                 the errors
	 */
	private void validateAdAndFullAd(Set<String> availableAuthTypeInfos, Errors errors) {
		if (availableAuthTypeInfos.contains(DemoAuthType.ADDRESS.getType())
				&& availableAuthTypeInfos.contains(DemoAuthType.FULL_ADDRESS.getType())) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, "Ad and FAD are enabled");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDENTITY_INFO_DTO },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Check gender.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkGender(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> genderList = DemoMatchType.GENDER.getIdentityInfoList(authRequest.getRequest());
		if (genderList != null && !genderList.isEmpty()) {
			Map<String, List<String>> fetchGenderType = null;
			try {
				fetchGenderType = masterDataManager.fetchGenderType();
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
						"Master Data util failed to load - Gender Type");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { "gender" },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			}
			if (null != fetchGenderType) {
				checkGender(errors, genderList, fetchGenderType);
			}
		}
	}

	private void checkGender(Errors errors, List<IdentityInfoDTO> genderList,
			Map<String, List<String>> fetchGenderType) {
		for (IdentityInfoDTO identityInfoDTO : genderList) {
			String language = identityInfoDTO.getLanguage() != null ? identityInfoDTO.getLanguage()
					: env.getProperty(PRIMARY_LANG_CODE);
			List<String> genderTypeList = fetchGenderType.get(language);
			if (null != genderTypeList && !genderTypeList.contains(identityInfoDTO.getValue())) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
						"Demographic data – Gender(pi) did not match");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { "gender" },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}

		}
	}

	/**
	 * Check DOB type.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkDOBType(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> dobTypeList = DemoMatchType.DOBTYPE.getIdentityInfoList(authRequest.getRequest());
		if (dobTypeList != null) {
			for (IdentityInfoDTO identityInfoDTO : dobTypeList) {
				if (!DOBType.isTypePresent(identityInfoDTO.getValue())) {
					mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
							"Demographic data – DOBType(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "DOBType" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}

	}

	/**
	 * Check age.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkAge(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> ageList = DemoMatchType.AGE.getIdentityInfoList(authRequest.getRequest());
		if (ageList != null) {
			for (IdentityInfoDTO identityInfoDTO : ageList) {
				try {
					Integer.parseInt(identityInfoDTO.getValue());
				} catch (NumberFormatException e) {
					mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
							"Demographic data – Age(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "age" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}
	}

	/**
	 * Check DOB.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkDOB(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> dobList = DemoMatchType.DOB.getIdentityInfoList(authRequest.getRequest());
		if (dobList != null) {
			for (IdentityInfoDTO identityInfoDTO : dobList) {
				try {
					DateUtils.parseToDate(identityInfoDTO.getValue(), env.getProperty("dob.req.date.pattern"));
				} catch (ParseException e) {
					// FIXME change to DOB - Invalid -DOB - Please enter DOB in specified date
					// format or Age in the acceptable range

					mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
							"Demographic data – DOB(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "dob" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}
	}

	/**
	 * Check langauge details.
	 *
	 * @param demoMatchType the demo match type
	 * @param identityInfos the identity infos
	 * @param errors        the errors
	 */
	private void checkLangaugeDetails(MatchType demoMatchType, List<IdentityInfoDTO> identityInfos, Errors errors) {
		String priLangCode = env.getProperty(PRIMARY_LANG_CODE);

		Map<String, Long> langCount = identityInfos.stream().map((IdentityInfoDTO idInfo) -> {
			String language = idInfo.getLanguage();
			if (language == null) {
				language = priLangCode;
			}
			return new SimpleEntry<>(language, idInfo);
		}).collect(Collectors.groupingBy(Entry::getKey, Collectors.counting()));

		for (long value : langCount.values()) {
			if (value > 1) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), INVALID_INPUT_PARAMETER,
						"Invalid or Multiple language code");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { "LanguageCode" },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		}

		if (langCount.keySet().size() > 1 && !demoMatchType.isMultiLanguage()) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), INVALID_INPUT_PARAMETER,
					"Invalid or Multiple language code");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "LanguageCode" },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Check OTP auth.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	protected void checkOTPAuth(AuthRequestDTO authRequest, Errors errors) {
		if (isMatchtypeEnabled(PinMatchType.OTP)) {
			Optional<String> otp = getOtpValue(authRequest);
			if (!otp.isPresent()) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE_CHECK_OTP_AUTH,
						"INVALID_OTP - pinType is not OTP");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
						new Object[] { "OTP" }, IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
			} else if (OTP_LENGTH != otp.orElse("").length()) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE_CHECK_OTP_AUTH,
						"INVALID_OTP - pinType is not OTP");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_OTP.getErrorMessage());
			}
		}
	}

	/**
	 * Gets the otp value.
	 *
	 * @param authreqdto the authreqdto
	 * @return the otp value
	 */
	private Optional<String> getOtpValue(AuthRequestDTO authreqdto) {
		return Optional.ofNullable(authreqdto.getRequest()).map(RequestDTO::getOtp);

	}

	/**
	 * validate email id.
	 *
	 * @param authRequest authRequest
	 * @param errors      the errors
	 */
	private void validateEmail(AuthRequestDTO authRequest, Errors errors) {
		try {
			List<IdentityInfoDTO> emailId = DemoMatchType.EMAIL.getIdentityInfoList(authRequest.getRequest());
			if (emailId != null) {
				for (IdentityInfoDTO email : emailId) {
					emailValidatorImpl.validateEmail(email.getValue());
				}
			}
		} catch (InvalideEmailException e) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), INVALID_INPUT_PARAMETER,
					"Invalid email \n" + ExceptionUtils.getStackTrace(e));
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "emailId" },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * validate phone number.
	 *
	 * @param authRequest authRequest
	 * @param errors      the errors
	 */
	private void validatePhone(AuthRequestDTO authRequest, Errors errors) {
		try {
			List<IdentityInfoDTO> phoneNumber = DemoMatchType.PHONE.getIdentityInfoList(authRequest.getRequest());
			if (phoneNumber != null) {
				for (IdentityInfoDTO phone : phoneNumber) {
					phoneValidatorImpl.validatePhone(phone.getValue());
				}
			}
		} catch (InvalidPhoneNumberException e) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), INVALID_INPUT_PARAMETER,
					"Invalid email \n" + ExceptionUtils.getStackTrace(e));
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "phoneNumber" },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}

	}

	/**
	 * Validates the AuthType
	 * 
	 * @param authType
	 * @param errors
	 */
	protected void validateAuthType(AuthTypeDTO authType, Errors errors) {
		if (!(authType.isDemo() || authType.isBio() || authType.isOtp() || authType.isPin())) {
			errors.rejectValue(AUTH_TYPE,
					IdAuthenticationErrorConstants.NO_AUTHENTICATION_TYPE_SELECTED_IN_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.NO_AUTHENTICATION_TYPE_SELECTED_IN_REQUEST.getErrorMessage());
		}
	}

	/**
	 * Method to validate auth type
	 * 
	 * @param requestDTO
	 * @param errors
	 */
	protected void validateAllowedAuthTypes(AuthRequestDTO requestDTO, Errors errors, String configKey) {
		AuthTypeDTO authTypeDTO = requestDTO.getRequestedAuth();
		if (authTypeDTO != null) {
			Set<String> allowedAuthType = getAllowedAuthTypes(configKey);
			validateAuthType(requestDTO, errors, authTypeDTO, allowedAuthType);
		} else {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage(), REQUEST));
		}

	}

	/**
	 * Validate auth type.
	 *
	 * @param requestDTO      the request DTO
	 * @param errors          the errors
	 * @param authTypeDTO     the auth type DTO
	 * @param allowedAuthType the allowed auth type
	 */
	private void validateAuthType(AuthRequestDTO requestDTO, Errors errors, AuthTypeDTO authTypeDTO,
			Set<String> allowedAuthType) {
		checkAllowedAuthType(requestDTO, errors, authTypeDTO, allowedAuthType);

		if (authTypeDTO.isBio()) {
			if (allowedAuthType.contains(InternalAuthType.BIO.getType())) {
				validateBioMetadataDetails(requestDTO, errors);
			} else {
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
						new Object[] { InternalAuthType.BIO.getType() },
						IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage());
			}

		}
	}

	/**
	 * Check allowed auth type.
	 *
	 * @param requestDTO      the request DTO
	 * @param errors          the errors
	 * @param authTypeDTO     the auth type DTO
	 * @param allowedAuthType the allowed auth type
	 */
	private void checkAllowedAuthType(AuthRequestDTO requestDTO, Errors errors, AuthTypeDTO authTypeDTO,
			Set<String> allowedAuthType) {
		if (authTypeDTO.isDemo()) {
			if (allowedAuthType.contains(InternalAuthType.DEMO.getType())) {
				checkDemoAuth(requestDTO, errors);
			} else {
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
						new Object[] { InternalAuthType.DEMO.getType() },
						IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage());
			}
		}

		if (authTypeDTO.isOtp()) {
			if (allowedAuthType.contains(InternalAuthType.OTP.getType())) {
				checkOTPAuth(requestDTO, errors);
			} else {
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
						new Object[] { InternalAuthType.OTP.getType() },
						IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage());
			}
		}

		if (authTypeDTO.isPin()) {
			if (allowedAuthType.contains(InternalAuthType.SPIN.getType())) {
				validateAdditionalFactorsDetails(requestDTO, errors);
			} else {
				errors.rejectValue(InternalAuthType.SPIN.getType(),
						IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorCode(),
						new Object[] { InternalAuthType.SPIN.getType() },
						IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED.getErrorMessage());
			}
		}
	}

	/**
	 * Extract auth info.
	 *
	 * @return the sets the
	 */
	private Set<String> getAllowedAuthTypes(String configKey) {
		String intAllowedAuthType = env.getProperty(configKey);
		return Stream.of(intAllowedAuthType.split(",")).filter(str -> !str.isEmpty()).collect(Collectors.toSet());
	}
}