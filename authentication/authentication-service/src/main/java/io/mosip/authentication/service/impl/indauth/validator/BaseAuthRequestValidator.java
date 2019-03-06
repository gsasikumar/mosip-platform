package io.mosip.authentication.service.impl.indauth.validator;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BaseAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.BioType;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.InternalAuthType;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.dto.indauth.PinType;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DOBType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoAuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.pin.PinAuthType;
import io.mosip.authentication.service.integration.MasterDataManager;
import io.mosip.authentication.service.validator.IdAuthValidator;
import io.mosip.kernel.core.datavalidator.exception.InvalidPhoneNumberException;
import io.mosip.kernel.core.datavalidator.exception.InvalideEmailException;
import io.mosip.kernel.core.exception.ExceptionUtils;
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

	private static final String BIO_TYPE = "biotype";

	private static final String MAKE_FOR_0_BIO_TYPE = "make for {0} bioType";

	/** The Final Constant For PIN_VALUE */
	private static final String PIN_VALUE = "pinValue";

	/** The Final Constant For PIN_TYPE */
	private static final String PIN_TYPE = "pinType";

	/** The Final Constant For MODEL */
	private static final String MODEL = "model";

	/** The Final Constant For FINGERPRINT_PROVIDER_ALL */
	private static final String FINGERPRINT_PROVIDER_ALL = "fingerprint.provider.all";

	/** The Final Constant For IRIS_PROVIDER_ALL */
	private static final String IRIS_PROVIDER_ALL = "iris.provider.all";

	/** The Final Constant For make */
	private static final String MAKE = "Make";

	/** The Final Constant For deviceId */
	private static final String DEVICE_ID = "Device Id";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(BaseAuthRequestValidator.class);

	/** The Constant ID_AUTH_VALIDATOR. */
	private static final String ID_AUTH_VALIDATOR = "ID_AUTH_VALIDATOR";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	/** The Constant VALIDATE. */
	private static final String VALIDATE = "VALIDATE";

	/** The Constant ID_AUTH_VALIDATOR. */
	private static final String AUTH_REQUEST_VALIDATOR = "AUTH_REQUEST_VALIDATOR";

	/** The Constant PRIMARY_LANG_CODE. */
	private static final String PRIMARY_LANG_CODE = "mosip.primary.lang-code";

	/** The Constant INVALID_INPUT_PARAMETER. */
	private static final String INVALID_INPUT_PARAMETER = "INVALID_INPUT_PARAMETER - ";

	/** The Constant VALIDATE_CHECK_OTP_AUTH. */
	private static final String VALIDATE_CHECK_OTP_AUTH = "validate -> checkOTPAuth";

	/** The Constant PIN_INFO. */
	private static final String PIN_INFO = "pinInfo";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant OTP_LENGTH. */
	private static final Integer OTP_LENGTH = 6;

	/** The Constant finger. */
	private static final String FINGER = "finger";

	/** The Constant iris. */
	private static final String IRIS = "iris";

	/** The Constant fullAddress. */
	private static final String FULLADDRESS = "fullAddress";

	/** The Constant Address. */
	private static final String ADDRESS = "Address";

	/** The Constant personalIdentity. */
	private static final String PERSONALIDENTITY = "personalIdentity";

	/** The Constant face. */
	private static final String FACE = "face";

	/** The Constant IdentityInfoDTO. */
	private static final String IDENTITY_INFO_DTO = "IdentityInfoDTO";

	/** The Constant PATTERN. */
	private static final Pattern STATIC_PIN_PATTERN = Pattern.compile("^[0-9]{6}");
	
	/** The Constant AUTH_TYPE. */
	private static final String AUTH_TYPE = "authType";
	
	/** The Final Constant For allowed Internal auth  type*/
	private static final String INTERNAL_ALLOWED_AUTH_TYPE = "internal.allowed.auth.type";

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
	protected void validatePinDetails(AuthRequestDTO authRequestDTO, Errors errors) {
		AuthTypeDTO authTypeDTO = authRequestDTO.getAuthType();

		if ((authTypeDTO != null && authTypeDTO.isPin())) {

			List<PinInfo> pinInfo = authRequestDTO.getPinInfo();

			if (pinInfo != null && !pinInfo.isEmpty()) {

				validatePinInfo(pinInfo, errors);

			} else {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Missing pinval in the request");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_PINDATA.getErrorCode(),
						new Object[] { PIN_INFO }, IdAuthenticationErrorConstants.MISSING_PINDATA.getErrorMessage());
			}
		}
	}

	/**
	 * validate The Pin Info list from the request.
	 * 
	 * @param pinInfo
	 * @param errors
	 */
	private void validatePinInfo(List<PinInfo> pinInfo, Errors errors) {
		if (!isPinTypeEmptyOrNull(pinInfo)) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "missing Pin Type Info request");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { PIN_TYPE },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
		if (!errors.hasErrors()) {
			checkPinType(pinInfo, errors);
		}
		if (!isPinValueEmptyOrNull(pinInfo)) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "missing Pin Value Info request");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { PIN_VALUE },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
		if (!errors.hasErrors()) {
			checkPinValue(pinInfo, errors);
		}

	}

	/**
	 * checks the static Pin value.
	 * 
	 * @param pinInfo
	 * @param errors
	 */
	private void checkPinValue(List<PinInfo> pinInfo, Errors errors) {
		for (PinInfo pinInfos : pinInfo) {
			if (!STATIC_PIN_PATTERN.matcher(pinInfos.getValue()).matches()) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Invalid Input Static pin Value");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { PIN_VALUE },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		}
	}

	/**
	 * checks the static Pin Type.
	 * 
	 * @param pinInfo
	 * @param errors
	 */
	private void checkPinType(List<PinInfo> pinInfo, Errors errors) {
		for (PinInfo pinInfos : pinInfo) {
			if (!Stream.of(PinAuthType.values())
					.anyMatch(pinType -> pinInfos.getType().equalsIgnoreCase(pinType.getType()))) {
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { PIN_TYPE },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		}
	}

	/**
	 * checks pin value is null or empty
	 * 
	 * @param pinInfo
	 * @param errors
	 * @return
	 */
	private boolean isPinTypeEmptyOrNull(List<PinInfo> pinInfo) {
		return pinInfo.parallelStream().allMatch(info -> info.getType() != null && !info.getType().isEmpty());
	}

	/**
	 * checks pin Type is null or empty.
	 * 
	 * @param pinInfo
	 * @return
	 */
	private boolean isPinValueEmptyOrNull(List<PinInfo> pinInfo) {
		return pinInfo.parallelStream().allMatch(info -> info.getValue() != null && !info.getValue().isEmpty());
	}

	/**
	 * Validate Biometric details i.e validating fingers,iris,face and device
	 * information.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	protected void validateBioDetails(AuthRequestDTO authRequestDTO, Errors errors) {

		AuthTypeDTO authTypeDTO = authRequestDTO.getAuthType();

		if ((authTypeDTO != null && authTypeDTO.isBio())) {

			List<BioInfo> bioInfo = authRequestDTO.getBioInfo();

			if (bioInfo != null && !bioInfo.isEmpty() && isContainDeviceInfo(bioInfo)) {

				validateDeviceInfo(bioInfo, errors);

				validateBioType(bioInfo, errors);

				validateFinger(authRequestDTO, bioInfo, errors);

				validateIris(authRequestDTO, bioInfo, errors);

				validateFace(authRequestDTO, bioInfo, errors);

			} else if (bioInfo == null || bioInfo.isEmpty()) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "missing biometric request");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_BIOMETRICDATA.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.MISSING_BIOMETRICDATA.getErrorMessage(), REQUEST));
			}
		}

	}

	/**
	 * Validates the BioType value
	 * 
	 * @param bioInfo
	 * @param errors
	 */
	private void validateBioType(List<BioInfo> bioInfo, Errors errors) {
		AuthType[] authTypes = BioAuthType.values();
		Set<String> availableAuthTypeInfos = new HashSet<>();
		for (AuthType authType : authTypes) {
			availableAuthTypeInfos.add(authType.getType());
		}
		for (BioInfo bioInfos : bioInfo) {
			if (!availableAuthTypeInfos.contains(bioInfos.getBioType())) {
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { BIO_TYPE },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			}
		}

	}

	/**
	 * validates the DeviceInfo
	 * 
	 * @param bioInfos
	 * @param errors
	 */
	private void validateDeviceInfo(List<BioInfo> bioInfos, Errors errors) {
		if (!isContainDeviceId(bioInfos)) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "missing biometric Device id Info request");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { DEVICE_ID },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
		if (!isModelNullOrEmpty(bioInfos)) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "missing biometric model Info request");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { MODEL }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
		if (!isDeviceMakeNullOrEmpty(bioInfos)) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
					"missing biometric Device Make Info request");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { MAKE }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
		validateMake(bioInfos, errors);

	}

	/**
	 * check model attribute is empty or null
	 * 
	 * @param bioInfos
	 * @return
	 */
	private boolean isModelNullOrEmpty(List<BioInfo> bioInfos) {
		return bioInfos.parallelStream().allMatch(deviceInfo -> deviceInfo.getDeviceInfo().getModel() != null
				&& !deviceInfo.getDeviceInfo().getModel().isEmpty());
	}

	/**
	 * checks for proper Make value present in the request.
	 * 
	 * @param bioInfo
	 * @return
	 */

	private void validateMake(List<BioInfo> bioInfo, Errors errors) {
		String deviceNameList = null;
		if (isAvailableBioType(bioInfo, BioType.IRISIMG)) {
			deviceNameList = env.getProperty(IRIS_PROVIDER_ALL);
		} else if (isAvailableBioType(bioInfo, BioType.FGRMIN) || isAvailableBioType(bioInfo, BioType.FGRIMG)) {
			deviceNameList = env.getProperty(FINGERPRINT_PROVIDER_ALL);
		}
		if (deviceNameList != null) {
			String[] deviceName = deviceNameList.split(",");
			List<String> wordList = Arrays.asList(deviceName);
			bioInfo.stream().map(info -> info).filter(make -> !wordList.contains(make.getDeviceInfo().getMake()))
					.forEach(make -> {
						errors.rejectValue(REQUEST,
								IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
								// TODO
								new Object[] { MessageFormat.format(MAKE_FOR_0_BIO_TYPE, make.getBioType()) },
								IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
					});
		}

	}

	/**
	 * check the value of Make present or not from the list
	 * 
	 * @param deviceInfoList
	 * @return
	 */
	private boolean isDeviceMakeNullOrEmpty(List<BioInfo> deviceInfoList) {
		return deviceInfoList.parallelStream().allMatch(deviceInfo -> deviceInfo.getDeviceInfo().getMake() != null
				&& !deviceInfo.getDeviceInfo().getMake().isEmpty());
	}

	/**
	 * check the deviceId value present in the request is null or empty
	 * 
	 * @param deviceInfoList
	 * @return
	 */
	private boolean isContainDeviceId(List<BioInfo> deviceInfoList) {
		return deviceInfoList.parallelStream().allMatch(deviceInfo -> deviceInfo.getDeviceInfo().getDeviceId() != null
				&& !deviceInfo.getDeviceInfo().getDeviceId().isEmpty());
	}

	/**
	 * Validate fingers.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param bioInfo        the bio info
	 * @param errors         the errors
	 */
	private void validateFinger(AuthRequestDTO authRequestDTO, List<BioInfo> bioInfo, Errors errors) {
		if ((isAvailableBioType(bioInfo, BioType.FGRMIN) && isDuplicateBioType(authRequestDTO, BioType.FGRMIN))
				|| (isAvailableBioType(bioInfo, BioType.FGRIMG)
						&& isDuplicateBioType(authRequestDTO, BioType.FGRIMG))) {
			checkAtleastOneFingerRequestAvailable(authRequestDTO, errors);
			if (!errors.hasErrors()) {
				validateFingerRequestCount(authRequestDTO, errors);
				validateMultiFingersValue(authRequestDTO, errors);
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
	private void validateIris(AuthRequestDTO authRequestDTO, List<BioInfo> bioInfo, Errors errors) {
		if (isAvailableBioType(bioInfo, BioType.IRISIMG) && isDuplicateBioType(authRequestDTO, BioType.IRISIMG)) {

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
		IdentityDTO identity = authRequestDTO.getRequest().getIdentity();
		List<Supplier<List<IdentityInfoDTO>>> listOfIris = Stream
				.<Supplier<List<IdentityInfoDTO>>>of(identity::getLeftEye, identity::getRightEye)
				.collect(Collectors.toList());

		List<IdentityInfoDTO> idendityInfoList = listOfIris.stream().map(Supplier::get).filter(Objects::nonNull)
				.flatMap(list -> list.stream()).collect(Collectors.toList());

		boolean isDuplicateIrisValue = checkIsDuplicate(idendityInfoList);

		if (isDuplicateIrisValue) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Duplicate IRIS in request");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.DUPLICATE_IRIS.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.DUPLICATE_IRIS.getErrorMessage(), REQUEST));
		}

	}

	/**
	 * Validate Face.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param bioInfo        the bio info
	 * @param errors         the errors
	 */
	private void validateFace(AuthRequestDTO authRequestDTO, List<BioInfo> bioInfo, Errors errors) {

		if (isAvailableBioType(bioInfo, BioType.FACEIMG) && isDuplicateBioType(authRequestDTO, BioType.FACEIMG)) {

			checkAtleastOneFaceRequestAvailable(authRequestDTO, errors);
		}
	}

	/**
	 * validate atleast one finger request should be available for Bio.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void checkAtleastOneFingerRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {

		@SuppressWarnings("unchecked")
		boolean isAtleastOneFingerRequestAvailable = checkAnyIdInfoAvailable(authRequestDTO, IdentityDTO::getLeftThumb,
				IdentityDTO::getLeftIndex, IdentityDTO::getLeftMiddle, IdentityDTO::getLeftRing,
				IdentityDTO::getLeftLittle, IdentityDTO::getRightThumb, IdentityDTO::getRightIndex,
				IdentityDTO::getRightMiddle, IdentityDTO::getRightRing, IdentityDTO::getRightLittle);
		if (!isAtleastOneFingerRequestAvailable) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "finger request is not available");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { FINGER }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}

	}

	/**
	 * validate atleast one Iris request should be available for Bio.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void checkAtleastOneIrisRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {
		@SuppressWarnings("unchecked")
		boolean isIrisRequestAvailable = checkAnyIdInfoAvailable(authRequestDTO, IdentityDTO::getLeftEye,
				IdentityDTO::getRightEye);
		if (!isIrisRequestAvailable) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "iris request is not available");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IRIS }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * validate atleast one Face request should be available for Bio.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void checkAtleastOneFaceRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {
		boolean isFaceRequestAvailable = authRequestDTO.getRequest() != null
				&& authRequestDTO.getRequest().getIdentity() != null
				&& authRequestDTO.getRequest().getIdentity().getFace() != null;
		if (!isFaceRequestAvailable) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "face request is not available");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { FACE }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
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
				.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity).map(func)
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
	private boolean isAvailableBioType(List<BioInfo> bioInfoList, BioType bioType) {
		return bioInfoList.parallelStream().filter(bio -> bio.getBioType() != null && !bio.getBioType().isEmpty())
				.anyMatch(bio -> bio.getBioType().equals(bioType.getType()));
	}

	/**
	 * If DemoAuthType is Bio, then validate device information is available or not.
	 *
	 * @param deviceInfoList the device info list
	 * @return true, if is contain device info
	 */
	private boolean isContainDeviceInfo(List<BioInfo> deviceInfoList) {

		return deviceInfoList.parallelStream().allMatch(deviceInfo -> deviceInfo.getDeviceInfo() != null);
	}

	/**
	 * If DemoAuthType is Bio, then check same bio request type should not be
	 * requested again.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param bioType        the bio type
	 * @return true, if is duplicate bio type
	 */
	private boolean isDuplicateBioType(AuthRequestDTO authRequestDTO, BioType bioType) {
		List<BioInfo> bioInfo = authRequestDTO.getBioInfo();
		Long bioTypeCount = Optional.ofNullable(bioInfo).map(List::parallelStream)
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
	private void validateFingerRequestCount(AuthRequestDTO authRequestDTO, Errors errors) {
		IdentityDTO identity = authRequestDTO.getRequest().getIdentity();

		List<Supplier<List<IdentityInfoDTO>>> listOfIndInfoSupplier = Stream.<Supplier<List<IdentityInfoDTO>>>of(
				identity::getLeftThumb, identity::getLeftIndex, identity::getLeftMiddle, identity::getLeftRing,
				identity::getLeftLittle, identity::getRightThumb, identity::getRightIndex, identity::getRightMiddle,
				identity::getRightRing, identity::getRightLittle).collect(Collectors.toList());

		boolean anyInfoIsMoreThanOne = listOfIndInfoSupplier.stream().anyMatch(s -> getIdInfoCount(s.get()) > 1);
		if (anyInfoIsMoreThanOne) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, "Duplicate fingers ");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorMessage(), REQUEST));
		}

		Long fingerCountExceeding = listOfIndInfoSupplier.stream().map(s -> getIdInfoCount(s.get())).mapToLong(l -> l)
				.sum();
		if (fingerCountExceeding > 2) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "finger count is exceeding to 2");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorMessage(), REQUEST));
		}
	}

	/**
	 * Validate multi fingers value.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void validateMultiFingersValue(AuthRequestDTO authRequestDTO, Errors errors) {
		IdentityDTO identity = authRequestDTO.getRequest().getIdentity();
		List<Supplier<List<IdentityInfoDTO>>> listOfFingerprint = Stream.<Supplier<List<IdentityInfoDTO>>>of(
				identity::getLeftThumb, identity::getLeftIndex, identity::getLeftMiddle, identity::getLeftRing,
				identity::getLeftLittle, identity::getRightThumb, identity::getRightIndex, identity::getRightMiddle,
				identity::getRightRing, identity::getRightLittle).collect(Collectors.toList());

		List<IdentityInfoDTO> idendityInfoList = listOfFingerprint.stream().map(Supplier::get).filter(Objects::nonNull)
				.flatMap(list -> list.stream()).collect(Collectors.toList());

		boolean isDuplicateFingerValue = checkIsDuplicate(idendityInfoList);

		if (isDuplicateFingerValue) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Duplicate fingers in request");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorMessage(), REQUEST));
		}
	}

	/**
	 * Gets the id info count.
	 *
	 * @param list the list
	 * @return the id info count
	 */
	private Long getIdInfoCount(List<IdentityInfoDTO> list) {
		return Optional.ofNullable(list).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> lt.getValue() != null && !lt.getValue().isEmpty()).count())
				.orElse((long) 0);
	}

	/**
	 * Check is duplicate.
	 *
	 * @param list the list
	 * @return true, if successful
	 */
	private boolean checkIsDuplicate(List<IdentityInfoDTO> list) {
		return Optional.ofNullable(list).map(List::parallelStream).map(stream -> stream.filter((IdentityInfoDTO lt) -> {
			return lt.getValue() != null && !lt.getValue().isEmpty();
		}).collect(Collectors.groupingBy(IdentityInfoDTO::getValue, Collectors.counting())))
				.map((Map<String, Long> valueCountMap) -> valueCountMap.values().stream().anyMatch(count -> count > 1))
				.orElse(false);
	}

	/**
	 * validate Iris request count. left and right eye should not exceed 1 and total
	 * iris should not exceed 2.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void validateIrisRequestCount(AuthRequestDTO authRequestDTO, Errors errors) {
		IdentityDTO identity = authRequestDTO.getRequest().getIdentity();

		List<IdentityInfoDTO> leftEye = identity.getLeftEye();
		Long leftEyeCount = Optional.ofNullable(leftEye).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> rightEye = identity.getRightEye();
		Long rightEyeCount = Optional.ofNullable(rightEye).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		if (leftEyeCount > 1 || rightEyeCount > 1) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
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
					List<IdentityInfoDTO> identityInfos = matchType
							.getIdentityInfoList(authRequest.getRequest().getIdentity());
					if (identityInfos != null && !identityInfos.isEmpty()) {
						availableAuthTypeInfos.add(authType.getType());
						hasMatch = true;
						checkIdentityInfoValue(identityInfos, errors);
						checkLangaugeDetails(matchType, identityInfos, errors);
					}
				}
			}
		}

		checkAvaliableAuthInfo(authRequest, errors, authTypes, availableAuthTypeInfos);

		if (!hasMatch) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Missing IdentityInfoDTO");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDENTITY_INFO_DTO },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		} else {
			checkOtherValues(authRequest, errors, availableAuthTypeInfos);
		}
	}

	/**
	 * Check avaliable auth info.
	 *
	 * @param authRequest            the auth request
	 * @param errors                 the errors
	 * @param authTypes              the auth types
	 * @param availableAuthTypeInfos the available auth type infos
	 */
	private void checkAvaliableAuthInfo(AuthRequestDTO authRequest, Errors errors, AuthType[] authTypes,
			Set<String> availableAuthTypeInfos) {
		Set<String> allowedLang = extractAllowedLang();
		for (AuthType authType : authTypes) {
			if (authType.isAuthTypeEnabled(authRequest, idInfoHelper)) {
				addMissingAuthTypeError(errors, availableAuthTypeInfos, authType);

				for (String lang : allowedLang) {
					checkAvailableMatchingStrategy(authRequest, errors, authType, lang);
					checkAvailableMatchingThreshold(authRequest, errors, authType, lang);
				}
			}
		}
	}

	/**
	 * Check available matching threshold.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 * @param authType    the auth type
	 * @param lang
	 */
	private void checkAvailableMatchingThreshold(AuthRequestDTO authRequest, Errors errors, AuthType authType,
			String lang) {
		Optional<Integer> matchingThreshold = authType.getMatchingThreshold(authRequest, lang, env);
		if (matchingThreshold.isPresent()) {
			Integer integer = matchingThreshold.get();
			if (integer <= 0 || integer >= 100) {
				if (authType.equals(DemoAuthType.FULL_ADDRESS)) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
							"Full Address Matching Strategy is Missing");
					errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.INVALID_MATCHINGTHRESHOLD_FAD_PRI.getErrorCode(),
							new Object[] { PERSONALIDENTITY },
							IdAuthenticationErrorConstants.INVALID_MATCHINGTHRESHOLD_FAD_PRI.getErrorMessage());
				} else if (authType.equals(DemoAuthType.PERSONAL_IDENTITY)) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
							"Personal Identity Matching Threshold is Invalid");
					errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.INVALID_MATCHINGTHRESHOLD_PI_PRI.getErrorCode(),
							new Object[] { PERSONALIDENTITY },
							IdAuthenticationErrorConstants.INVALID_MATCHINGTHRESHOLD_PI_PRI.getErrorMessage());
				}
			}
		}
	}

	/**
	 * Check available matching strategy.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 * @param authType    the auth type
	 * @param lang
	 */
	private void checkAvailableMatchingStrategy(AuthRequestDTO authRequest, Errors errors, AuthType authType,
			String lang) {
		Optional<String> matchingStrategy = authType.getMatchingStrategy(authRequest, lang);
		if (matchingStrategy.isPresent()) {
			if (!MatchingStrategyType.getMatchStrategyType(matchingStrategy.get()).isPresent()) {
				if (authType.equals(DemoAuthType.FULL_ADDRESS)) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
							"fullAddress Matching Strategy is Missing");
					errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.INVALID_MATCHINGSTRATEGY_FAD_PRI.getErrorCode(),
							new Object[] { FULLADDRESS },
							IdAuthenticationErrorConstants.INVALID_MATCHINGSTRATEGY_FAD_PRI.getErrorMessage());
				} else if (authType.equals(DemoAuthType.PERSONAL_IDENTITY)) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
							"personalIdentity Matching Strategy is Missing");
					errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.INVALID_MATCHINGSTRATEGY_PI_PRI.getErrorCode(),
							new Object[] { PERSONALIDENTITY },
							IdAuthenticationErrorConstants.INVALID_MATCHINGSTRATEGY_PI_PRI.getErrorMessage());
				}
			}
		}
	}

	/**
	 * Check available auth type.
	 *
	 * @param errors                 the errors
	 * @param availableAuthTypeInfos the available auth type infos
	 * @param authType               the auth type
	 */
	private void addMissingAuthTypeError(Errors errors, Set<String> availableAuthTypeInfos, AuthType authType) {
		if (!availableAuthTypeInfos.contains(authType.getType())) {
			if ((authType.equals(DemoAuthType.FULL_ADDRESS))) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
						"Full Address is Missing");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_FAD.getErrorCode(),
						new Object[] { FULLADDRESS }, IdAuthenticationErrorConstants.MISSING_FAD.getErrorMessage());
			} else if ((authType.equals(DemoAuthType.ADDRESS))) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER, "Address is Missing");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_AD.getErrorCode(),
						new Object[] { ADDRESS }, IdAuthenticationErrorConstants.MISSING_AD.getErrorMessage());
			} else if ((authType.equals(DemoAuthType.PERSONAL_IDENTITY))) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
						"personalIdentity is Missing");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_PI.getErrorCode(),
						new Object[] { PERSONALIDENTITY }, IdAuthenticationErrorConstants.MISSING_PI.getErrorMessage());
			}
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
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "IdentityInfoDTO is invalid");
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
		checkDOB(authRequest, errors);
		checkDOBType(authRequest, errors);
		checkAge(authRequest, errors);
		checkGender(authRequest, errors);
		validateEmail(authRequest, errors);
		validatePhone(authRequest, errors);
		validateAdAndFullAd(availableAuthTypeInfos, errors);
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
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Ad and FAD are enabled");
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
		List<IdentityInfoDTO> genderList = DemoMatchType.GENDER
				.getIdentityInfoList(authRequest.getRequest().getIdentity());
		if (genderList != null && !genderList.isEmpty()) {
			Map<String, List<String>> fetchGenderType = null;
			try {
				fetchGenderType = masterDataManager.fetchGenderType();
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
						"Master Data util failed to load - Gender Type");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.UNKNOWN_ERROR.getErrorCode(),
						new Object[] { "gender" }, IdAuthenticationErrorConstants.UNKNOWN_ERROR.getErrorMessage());
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
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
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
		List<IdentityInfoDTO> dobTypeList = DemoMatchType.DOBTYPE
				.getIdentityInfoList(authRequest.getRequest().getIdentity());
		if (dobTypeList != null) {
			for (IdentityInfoDTO identityInfoDTO : dobTypeList) {
				if (!DOBType.isTypePresent(identityInfoDTO.getValue())) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
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
		List<IdentityInfoDTO> ageList = DemoMatchType.AGE.getIdentityInfoList(authRequest.getRequest().getIdentity());
		if (ageList != null) {
			for (IdentityInfoDTO identityInfoDTO : ageList) {
				try {
					Integer.parseInt(identityInfoDTO.getValue());
				} catch (NumberFormatException e) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
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
		List<IdentityInfoDTO> dobList = DemoMatchType.DOB.getIdentityInfoList(authRequest.getRequest().getIdentity());
		if (dobList != null) {
			for (IdentityInfoDTO identityInfoDTO : dobList) {
				try {
					DateUtils.parseToDate(identityInfoDTO.getValue(), env.getProperty("dob.req.date.pattern"));
				} catch (io.mosip.kernel.core.exception.ParseException e) {
					// FIXME change to DOB - Invalid -DOB - Please enter DOB in specified date
					// format or Age in the acceptable range

					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
							"Demographic data – DOB(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "dob" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				} catch (ParseException e) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
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
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
						"Invalid or Multiple language code");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { "LanguageCode" },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		}

		if (langCount.keySet().size() > 1 && !demoMatchType.isMultiLanguage()) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
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
		Optional<String> otp = getOtpValue(authRequest);
		if (!otp.isPresent()) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH,
					"INVALID_OTP - pinType is not OTP");
			errors.rejectValue(PIN_INFO, IdAuthenticationErrorConstants.OTP_NOT_PRESENT.getErrorCode(),
					IdAuthenticationErrorConstants.OTP_NOT_PRESENT.getErrorMessage());
		} else if (OTP_LENGTH != otp.orElse("").length()) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH,
					"INVALID_OTP - pinType is not OTP");
			errors.rejectValue(PIN_INFO, IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_OTP.getErrorMessage());
		}
	}

	/**
	 * Gets the otp value.
	 *
	 * @param authreqdto the authreqdto
	 * @return the otp value
	 */
	private Optional<String> getOtpValue(AuthRequestDTO authreqdto) {
		return Optional.ofNullable(authreqdto.getPinInfo())
				.flatMap(pinInfos -> pinInfos.stream()
						.filter(pinInfo -> pinInfo.getType() != null
								&& pinInfo.getType().equalsIgnoreCase(PinType.OTP.getType()))
						.findAny())
				.map(PinInfo::getValue);
	}

	/**
	 * validate email id.
	 *
	 * @param authRequest authRequest
	 * @param errors      the errors
	 */
	private void validateEmail(AuthRequestDTO authRequest, Errors errors) {
		try {
			List<IdentityInfoDTO> emailId = DemoMatchType.EMAIL
					.getIdentityInfoList(authRequest.getRequest().getIdentity());
			if (emailId != null) {
				for (IdentityInfoDTO email : emailId) {
					emailValidatorImpl.validateEmail(email.getValue());
				}
			}
		} catch (InvalideEmailException e) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
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
			List<IdentityInfoDTO> phoneNumber = DemoMatchType.PHONE
					.getIdentityInfoList(authRequest.getRequest().getIdentity());
			if (phoneNumber != null) {
				for (IdentityInfoDTO phone : phoneNumber) {
					phoneValidatorImpl.validatePhone(phone.getValue());
				}
			}
		} catch (InvalidPhoneNumberException e) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
					"Invalid email \n" + ExceptionUtils.getStackTrace(e));
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "phoneNumber" },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Extract allowed lang.
	 *
	 * @return the sets the
	 */
	private Set<String> extractAllowedLang() {
		Set<String> allowedLang;
		String languages = env.getProperty("mosip.supported-languages");
		if (null != languages && languages.contains(",")) {
			allowedLang = Arrays.stream(languages.split(",")).collect(Collectors.toSet());
		} else {
			allowedLang = new HashSet<>();
			allowedLang.add(languages);
		}
		return allowedLang;
	}
	
	/**
	 * Validates the AuthType
	 * 
	 * @param authType
	 * @param errors
	 */
	protected void validateAuthType(AuthTypeDTO authType, Errors errors) {
		if (!(authType.isAddress() || authType.isBio() || authType.isFullAddress() || authType.isOtp()
				|| authType.isPersonalIdentity() || authType.isPin())) {
			errors.rejectValue(AUTH_TYPE,
					IdAuthenticationErrorConstants.NO_AUTHENTICATION_TYPE_SELECTED_IN_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.NO_AUTHENTICATION_TYPE_SELECTED_IN_REQUEST.getErrorMessage());
		}
	}
	protected void validateRequest(AuthRequestDTO requestDTO, Errors errors) {
		validateAllowedAuthTypes(requestDTO, errors,INTERNAL_ALLOWED_AUTH_TYPE) ;
	}
	/**
	 * Method to validate auth type
	 * 
	 * @param requestDTO
	 * @param errors
	 */
	protected void validateAllowedAuthTypes(AuthRequestDTO requestDTO, Errors errors,String configKey) {
		AuthTypeDTO authTypeDTO = requestDTO.getAuthType();
		if (authTypeDTO != null) {
			Set<String> allowedAuthType = getAllowedAuthTypes(configKey);			
			validateAuthType(requestDTO, errors, authTypeDTO, allowedAuthType);
		}else {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
		}
		
		
	}

	/**
	 * Validate auth type.
	 *
	 * @param requestDTO the request DTO
	 * @param errors the errors
	 * @param authTypeDTO the auth type DTO
	 * @param allowedAuthType the allowed auth type
	 */
	private void validateAuthType(AuthRequestDTO requestDTO, Errors errors, AuthTypeDTO authTypeDTO,
			Set<String> allowedAuthType) {
		checkAllowedAuthType(requestDTO, errors, authTypeDTO, allowedAuthType);
		
		if(authTypeDTO.isBio()) {
			if(allowedAuthType.contains(InternalAuthType.BIO.getType())) {
				validateBioDetails(requestDTO, errors);
			} else {
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						new Object[]{AUTH_TYPE} , IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
			}
			
		}
	}

	/**
	 * Check allowed auth type.
	 *
	 * @param requestDTO the request DTO
	 * @param errors the errors
	 * @param authTypeDTO the auth type DTO
	 * @param allowedAuthType the allowed auth type
	 */
	private void checkAllowedAuthType(AuthRequestDTO requestDTO, Errors errors, AuthTypeDTO authTypeDTO,
			Set<String> allowedAuthType) {
		if((authTypeDTO.isPersonalIdentity() || authTypeDTO.isFullAddress() || authTypeDTO.isAddress())) {
			if(allowedAuthType.contains(InternalAuthType.DEMO.getType())) {
				checkDemoAuth(requestDTO, errors);
			} else {
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						new Object[]{AUTH_TYPE} , IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
			}
		} 
		
		if(authTypeDTO.isOtp()) {
			if(allowedAuthType.contains(InternalAuthType.OTP.getType())) {
				checkOTPAuth(requestDTO, errors);
			} else {
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						new Object[]{AUTH_TYPE} , IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
			}
		}
		
		if(authTypeDTO.isPin()) {
			if(allowedAuthType.contains(InternalAuthType.SPIN.getType())) {
				validatePinDetails(requestDTO, errors);
			} else {
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						new Object[]{AUTH_TYPE} , IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
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
		return Stream.of(intAllowedAuthType.split(","))
				.filter(str -> !str.isEmpty())
				.collect(Collectors.toSet());
	}
}