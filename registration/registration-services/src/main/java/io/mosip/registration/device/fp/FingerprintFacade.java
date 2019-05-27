package io.mosip.registration.device.fp;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FINGERPRINT_FACADE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.machinezoo.sourceafis.FingerprintTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.mdm.dto.CaptureResponsBioDataDto;
import io.mosip.registration.mdm.dto.CaptureResponseBioDto;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
import io.mosip.registration.mdm.service.impl.MosipBioDeviceManager;

/**
 * It takes a decision based on the input provider name and initialize the
 * respective implementation class and perform the required operation.
 * 
 * @author SaravanaKumar G
 *
 */
@Component
public class FingerprintFacade {

	private static final Logger LOGGER = AppConfig.getLogger(FingerprintFacade.class);
	private List<MosipFingerprintProvider> fingerprintProviders;

	@Autowired
	private MosipFingerprintProvider fingerprintProvider;

	@Autowired
	private MosipBioDeviceManager mosipBioDeviceManager;

	private byte[] isoTemplate;

	public boolean setIsoTemplate() throws RegBaseCheckedException {
		CaptureResponseDto captureResponseDto = mosipBioDeviceManager.scan("FINGERPRINT_SINGLE");
		isoTemplate = mosipBioDeviceManager.extractSingleBiometricIsoTemplate(captureResponseDto);
		if (isoTemplate != null)
			return true;
		return false;
	}

	public byte[] getIsoTemplateFromMdm() {
		return isoTemplate;
	}

	/**
	 * provide the minutia of a finger.
	 *
	 * @return the minutia
	 */
	public String getMinutia() {
		return fingerprintProvider.getMinutia();
	}

	private String minitia;

	public String getMinitiaThroughMdm() {
		minitia = new FingerprintTemplate().convert(this.isoTemplate).serialize();
		return minitia;
	}

	/**
	 * Gets the iso template.
	 *
	 * @return the iso template
	 */
	public byte[] getIsoTemplate() {
		return fingerprintProvider.getIsoTemplate();
	}

	/**
	 * Gets the error message.
	 *
	 * @return the error message
	 */
	public String getErrorMessage() {
		return fingerprintProvider.getErrorMessage();

	}

	/**
	 * Gets the finger print image as DTO.
	 *
	 * @param fpDetailsDTO
	 *            the fp details DTO
	 * @param fingerType
	 *            the finger type
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	public void getFingerPrintImageAsDTO(FingerprintDetailsDTO fpDetailsDTO, String fingerType)
			throws RegBaseCheckedException {

		if (isMdmEnabled())
			getFingerPrintImageAsDTOWithMdm(fpDetailsDTO, fingerType);
		else
			getFingerPrintImageAsDTONonMdm(fpDetailsDTO, fingerType);
	}

	public boolean isMdmEnabled() {
		return RegistrationConstants.ENABLE
				.equalsIgnoreCase(((String) ApplicationContext.map().get(RegistrationConstants.MDM_ENABLED)));
	}

	/**
	 * Gets the finger print image as DTO without MDM.
	 *
	 * @param fpDetailsDTO
	 *            the fp details DTO
	 * @param fingerType
	 *            the finger type
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private void getFingerPrintImageAsDTONonMdm(FingerprintDetailsDTO fpDetailsDTO, String fingerType)
			throws RegBaseCheckedException {
		Map<String, Object> fingerMap = null;

		try {
			// TODO : Currently stubbing the data. once we have the device, we
			// can remove
			// this.

			if (fingerType.equals(RegistrationConstants.LEFTPALM)) {
				fingerMap = getFingerPrintScannedImageWithStub(RegistrationConstants.LEFTHAND_SLAP_FINGERPRINT_PATH);
			} else if (fingerType.equals(RegistrationConstants.RIGHTPALM)) {
				fingerMap = getFingerPrintScannedImageWithStub(RegistrationConstants.RIGHTHAND_SLAP_FINGERPRINT_PATH);
			} else if (fingerType.equals(RegistrationConstants.THUMBS)) {
				fingerMap = getFingerPrintScannedImageWithStub(RegistrationConstants.BOTH_THUMBS_FINGERPRINT_PATH);
			}

			if ((fingerMap != null)
					&& ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER) || (fpDetailsDTO
							.getQualityScore() < (double) fingerMap.get(RegistrationConstants.IMAGE_SCORE_KEY)))) {
				fpDetailsDTO.setFingerPrint((byte[]) fingerMap.get(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY));
				fpDetailsDTO.setFingerprintImageName(fingerType.concat(RegistrationConstants.DOT)
						.concat((String) fingerMap.get(RegistrationConstants.IMAGE_FORMAT_KEY)));
				fpDetailsDTO.setFingerType(fingerType);
				fpDetailsDTO.setForceCaptured(false);
				if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
					fpDetailsDTO.setQualityScore((double) fingerMap.get(RegistrationConstants.IMAGE_SCORE_KEY));
				}
			}

		} finally {
			if (fingerMap != null && !fingerMap.isEmpty())
				fingerMap.clear();
		}
	}

	/**
	 * Gets the finger print image as DTO with MDM
	 *
	 * @param fpDetailsDTO
	 *            the fp details DTO
	 * @param fingerType
	 *            the finger type
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private void getFingerPrintImageAsDTOWithMdm(FingerprintDetailsDTO fpDetailsDTO, String fingerType)
			throws RegBaseCheckedException {
		String type = fingerType;
		fingerType = findFingerPrintType(fingerType);
		CaptureResponseDto captureResponseDto = mosipBioDeviceManager.scan(fingerType);
		if (captureResponseDto != null) {
			byte[] fingerPrintByte = captureResponseDto.getSlapImage();
			fpDetailsDTO.setFingerPrint(fingerPrintByte);
			fpDetailsDTO.setFingerType(type);
			fpDetailsDTO.setQualityScore(80);
		}
	}

	private String findFingerPrintType(String fingerType) {
		switch (fingerType) {
		case RegistrationConstants.LEFTPALM:
			fingerType = RegistrationConstants.FINGER_SLAP + RegistrationConstants.UNDER_SCORE
					+ RegistrationConstants.LEFT.toUpperCase();
			break;
		case RegistrationConstants.RIGHTPALM:
			fingerType = RegistrationConstants.FINGER_SLAP + RegistrationConstants.UNDER_SCORE
					+ RegistrationConstants.RIGHT.toUpperCase();
			break;
		case RegistrationConstants.THUMBS:
			fingerType = RegistrationConstants.FINGER_SLAP + RegistrationConstants.UNDER_SCORE
					+ RegistrationConstants.THUMB.toUpperCase();
			break;
		case RegistrationConstants.LEFTPALM + "_onboard":
			fingerType = RegistrationConstants.FINGER_SLAP + RegistrationConstants.UNDER_SCORE
					+ RegistrationConstants.LEFT.toUpperCase() + RegistrationConstants.UNDER_SCORE + "ONBOARD";
			break;
		case RegistrationConstants.RIGHTPALM + "_onboard":
			fingerType = RegistrationConstants.FINGER_SLAP + RegistrationConstants.UNDER_SCORE
					+ RegistrationConstants.RIGHT.toUpperCase() + RegistrationConstants.UNDER_SCORE + "ONBOARD";
			break;
		case RegistrationConstants.THUMBS + "_onboard":
			fingerType = RegistrationConstants.FINGER_SLAP + RegistrationConstants.UNDER_SCORE
					+ RegistrationConstants.THUMB.toUpperCase() + RegistrationConstants.UNDER_SCORE + "ONBOARD";
			break;
		default:
			break;
		}
		return fingerType;
	}

	/**
	 * Segment finger print image.
	 *
	 * @param fingerprintDetailsDTO
	 *            the fingerprint details DTO
	 * @param filePath
	 *            the file path
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	public void segmentFingerPrintImage(FingerprintDetailsDTO fingerprintDetailsDTO, String[] filePath,
			String fingerType) throws RegBaseCheckedException {

		readSegmentedFingerPrintsSTUB(fingerprintDetailsDTO, filePath, fingerType);

	}

	/**
	 * Assign all the Fingerprint providers which extends the
	 * MosipFingerprintProvider to the list.
	 *
	 * @param make
	 *            the make
	 * @return the fingerprint provider factory
	 */

	public MosipFingerprintProvider getFingerprintProviderFactory(String make) {
		for (MosipFingerprintProvider mosipFingerprintProvider : fingerprintProviders) {
			if (mosipFingerprintProvider.getClass().getName().toLowerCase().contains(make.toLowerCase())) {
				fingerprintProvider = mosipFingerprintProvider;
			}
		}
		return fingerprintProvider;
	}

	/**
	 * Sets the fingerprint providers.
	 *
	 * @param fingerprintProviders
	 *            the new fingerprint providers
	 */
	@Autowired
	public void setFingerprintProviders(List<MosipFingerprintProvider> fingerprintProviders) {
		this.fingerprintProviders = fingerprintProviders;
	}

	/**
	 * Stub method to get the finger print scanned image from local hard disk.
	 * Once SDK and device avilable then we can remove it.
	 *
	 * @param path
	 *            the path
	 * @return the finger print scanned image
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private Map<String, Object> getFingerPrintScannedImageWithStub(String path) throws RegBaseCheckedException {
		try {
			LOGGER.info(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of fingerprints details for user registration");

			BufferedImage bufferedImage = ImageIO.read(this.getClass().getResourceAsStream(path));

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "jpeg", byteArrayOutputStream);

			byte[] scannedFingerPrintBytes = byteArrayOutputStream.toByteArray();

			// Add image format, image and quality score in bytes array to map
			Map<String, Object> scannedFingerPrints = new WeakHashMap<>();
			scannedFingerPrints.put(RegistrationConstants.IMAGE_FORMAT_KEY, "jpg");
			scannedFingerPrints.put(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY, scannedFingerPrintBytes);
			if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
				if (path.contains(RegistrationConstants.THUMBS)) {
					scannedFingerPrints.put(RegistrationConstants.IMAGE_SCORE_KEY, 90.0);
				} else if (path.contains(RegistrationConstants.LEFTPALM)) {
					scannedFingerPrints.put(RegistrationConstants.IMAGE_SCORE_KEY, 85.0);
				} else if (path.contains(RegistrationConstants.RIGHTPALM)) {
					scannedFingerPrints.put(RegistrationConstants.IMAGE_SCORE_KEY, 90.0);
				}
			}

			LOGGER.info(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of fingerprints details for user registration completed");

			return scannedFingerPrints;
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"Exception while scanning fingerprints details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_SCAN_EXP,
					String.format(
							"Exception while scanning fingerprints details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	/**
	 * {@code readFingerPrints} is to read the scanned fingerprints.
	 *
	 * @param fingerprintDetailsDTO
	 *            the fingerprint details DTO
	 * @param path
	 *            the path
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private void readSegmentedFingerPrintsSTUB(FingerprintDetailsDTO fingerprintDetailsDTO, String[] path,
			String fingerType) throws RegBaseCheckedException {
		LOGGER.info(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID, "Reading scanned Finger has started");

		try {

			List<BiometricExceptionDTO> biometricExceptionDTOs;

			if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
				biometricExceptionDTOs = ((BiometricDTO) SessionContext.map()
						.get(RegistrationConstants.USER_ONBOARD_DATA)).getOperatorBiometricDTO()
								.getBiometricExceptionDTO();
			} else if (((RegistrationDTO) SessionContext.map().get(RegistrationConstants.REGISTRATION_DATA))
					.isUpdateUINChild() || (boolean) SessionContext.map().get(RegistrationConstants.IS_Child)) {
				biometricExceptionDTOs = ((RegistrationDTO) SessionContext.map()
						.get(RegistrationConstants.REGISTRATION_DATA)).getBiometricDTO().getIntroducerBiometricDTO()
								.getBiometricExceptionDTO();
			} else {
				biometricExceptionDTOs = ((RegistrationDTO) SessionContext.map()
						.get(RegistrationConstants.REGISTRATION_DATA)).getBiometricDTO().getApplicantBiometricDTO()
								.getBiometricExceptionDTO();
			}

			if (isMdmEnabled()) {

				prepareSegmentedBiometricsFromMdm(fingerprintDetailsDTO, fingerType);
			}

			else {

				prepareSegmentedBiometrics(fingerprintDetailsDTO, path, biometricExceptionDTOs);
			}

		} catch (IOException ioException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID, String.format(
					"Exception while reading scanned fingerprints details for user registration: %s caused by %s",
					runtimeException.getMessage(), runtimeException.getCause()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_SCAN_EXP, String.format(
					"Exception while reading scanned fingerprints details for user registration: %s caused by %s",
					runtimeException.getMessage(), runtimeException.getCause()));
		}
		LOGGER.info(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID, "Reading scanned Finger has ended");
	}

	protected void prepareSegmentedBiometricsFromMdm(FingerprintDetailsDTO fingerprintDetailsDTO, String fingerType)
			throws RegBaseCheckedException {
		CaptureResponseDto biometricData = mosipBioDeviceManager.scan(findFingerPrintType(fingerType));

		if (null != biometricData && null != biometricData.getMosipBioDeviceDataResponses()
				&& !biometricData.getMosipBioDeviceDataResponses().isEmpty()) {
		}
		for (CaptureResponseBioDto captureResponseBioDto : biometricData.getMosipBioDeviceDataResponses()) {

			CaptureResponsBioDataDto bioData = captureResponseBioDto.getCaptureResponseData();
			FingerprintDetailsDTO segmentedDetailsDTO = new FingerprintDetailsDTO();

			byte[] isoTemplateBytes = bioData.getBioExtract();
			segmentedDetailsDTO.setFingerPrint(isoTemplateBytes);

			byte[] isoImageBytes = bioData.getBioValue();
			segmentedDetailsDTO.setFingerPrintISOImage(isoImageBytes);

			segmentedDetailsDTO.setFingerType(bioData.getBioSegmentedType());
			segmentedDetailsDTO.setFingerprintImageName(bioData.getBioSegmentedType());
			segmentedDetailsDTO.setNumRetry(fingerprintDetailsDTO.getNumRetry());
			segmentedDetailsDTO.setForceCaptured(false);
			segmentedDetailsDTO.setQualityScore(90);

			if (fingerprintDetailsDTO.getSegmentedFingerprints() == null) {
				List<FingerprintDetailsDTO> segmentedFingerprints = new ArrayList<>(5);
				fingerprintDetailsDTO.setSegmentedFingerprints(segmentedFingerprints);
			}
			fingerprintDetailsDTO.getSegmentedFingerprints().add(segmentedDetailsDTO);
		}
	}

	protected void prepareSegmentedBiometrics(FingerprintDetailsDTO fingerprintDetailsDTO, String[] path,
			List<BiometricExceptionDTO> biometricExceptionDTOs) throws IOException {
		List<String> filePaths = Arrays.asList(path);

		boolean isExceptionFinger = false;

		for (String folderPath : filePaths) {
			isExceptionFinger = false;
			String[] imageFileName = folderPath.split("/");

			for (BiometricExceptionDTO exceptionDTO : biometricExceptionDTOs) {

				if (imageFileName[3].equals(exceptionDTO.getMissingBiometric())) {
					isExceptionFinger = true;
					break;
				}
			}
			if (!isExceptionFinger) {
				FingerprintDetailsDTO segmentedDetailsDTO = new FingerprintDetailsDTO();

				byte[] isoTemplateBytes = IOUtils
						.resourceToByteArray(folderPath.concat(RegistrationConstants.ISO_FILE));
				segmentedDetailsDTO.setFingerPrint(isoTemplateBytes);

				byte[] isoImageBytes = IOUtils
						.resourceToByteArray(folderPath.concat(RegistrationConstants.ISO_IMAGE_FILE));
				segmentedDetailsDTO.setFingerPrintISOImage(isoImageBytes);

				segmentedDetailsDTO.setFingerType(imageFileName[3]);
				segmentedDetailsDTO.setFingerprintImageName(imageFileName[3]);
				segmentedDetailsDTO.setNumRetry(fingerprintDetailsDTO.getNumRetry());
				segmentedDetailsDTO.setForceCaptured(false);
				segmentedDetailsDTO.setQualityScore(90);

				if (fingerprintDetailsDTO.getSegmentedFingerprints() == null) {
					List<FingerprintDetailsDTO> segmentedFingerprints = new ArrayList<>(5);
					fingerprintDetailsDTO.setSegmentedFingerprints(segmentedFingerprints);
				}
				fingerprintDetailsDTO.getSegmentedFingerprints().add(segmentedDetailsDTO);
			}
		}
	}

	/**
	 * Validate the Input Finger with the finger that is fetched from the
	 * Database.
	 *
	 * @param fingerprintDetailsDTO
	 *            the fingerprint details DTO
	 * @param userFingerprintDetails
	 *            the user fingerprint details
	 * @return true, if successful
	 */
	public boolean validateFP(FingerprintDetailsDTO fingerprintDetailsDTO, List<UserBiometric> userFingerprintDetails) {
		FingerprintTemplate fingerprintTemplate = new FingerprintTemplate()
				.convert(fingerprintDetailsDTO.getFingerPrint());
		String minutiae = fingerprintTemplate.serialize();
		int fingerPrintScore = Integer
				.parseInt(String.valueOf(ApplicationContext.map().get(RegistrationConstants.FINGER_PRINT_SCORE)));
		userFingerprintDetails.forEach(fingerPrintTemplateEach -> {
			if (fingerprintProvider.scoreCalculator(minutiae,
					fingerPrintTemplateEach.getBioMinutia()) > fingerPrintScore) {
				fingerprintDetailsDTO.setFingerType(fingerPrintTemplateEach.getUserBiometricId().getBioAttributeCode());
			}
		});
		return userFingerprintDetails.stream()
				.anyMatch(bio -> fingerprintProvider.scoreCalculator(minutiae, bio.getBioMinutia()) > fingerPrintScore);
	}

}
