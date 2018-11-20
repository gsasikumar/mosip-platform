package io.mosip.registration.util.acktemplate;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
/**
 * Generates Velocity Template for the creation of acknowledgement
 * 
 * @author Himaja Dhanyamraju
 *
 */
public class VelocityPDFGenerator {

	/**
	 * @param templateText
	 *            - string which contains the data of template that is used to generate acknowledgement
	 * @param registration
	 *            - RegistrationDTO to display required fields on the template
	 * @return writer - After mapping all the fields into the template, it is
	 *         written into a StringWriter and returned
	 */
	public Writer generateTemplate(String templateText, RegistrationDTO registration) {
		
		Reader templateReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(templateText.getBytes())));

		VelocityContext velocityContext = new VelocityContext();

		velocityContext.put(RegistrationConstants.TEMPLATE_REGISTRATION_ID, registration.getRegistrationId());

		SimpleDateFormat sdf = new SimpleDateFormat(RegistrationConstants.TEMPLATE_DATE_FORMAT);
		String currentDate = sdf.format(new Date());

		// map the respective fields with the values in the enrolmentDTO
		velocityContext.put(RegistrationConstants.TEMPLATE_DATE, currentDate);
		velocityContext.put(RegistrationConstants.TEMPLATE_FULL_NAME, registration.getDemographicDTO().getDemoInUserLang().getFullName());
		Date dob = registration.getDemographicDTO().getDemoInUserLang().getDateOfBirth();
		if(dob == null) {
			velocityContext.put(RegistrationConstants.TEMPLATE_DOB, registration.getDemographicDTO().getDemoInUserLang().getAge());
		} else {
			velocityContext.put(RegistrationConstants.TEMPLATE_DOB, 
					DateUtils.formatDate(dob, "dd-MM-YYYY"));
		}
		velocityContext.put(RegistrationConstants.TEMPLATE_GENDER, registration.getDemographicDTO().getDemoInUserLang().getGender());
		velocityContext.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1,
				registration.getDemographicDTO().getDemoInUserLang().getAddressDTO().getAddressLine1());
		velocityContext.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2,
				registration.getDemographicDTO().getDemoInUserLang().getAddressDTO().getAddressLine2());
		velocityContext.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE3,
				registration.getDemographicDTO().getDemoInUserLang().getAddressDTO().getAddressLine3());
		velocityContext.put(RegistrationConstants.TEMPLATE_CITY, registration.getDemographicDTO().getDemoInUserLang().getAddressDTO().getLocationDTO().getCity());
		velocityContext.put(RegistrationConstants.TEMPLATE_STATE, registration.getDemographicDTO().getDemoInUserLang().getAddressDTO().getLocationDTO().getProvince());
		velocityContext.put(RegistrationConstants.TEMPLATE_COUNTRY,
				registration.getDemographicDTO().getDemoInUserLang().getAddressDTO().getLocationDTO().getRegion());
		velocityContext.put(RegistrationConstants.TEMPLATE_POSTAL_CODE,
				registration.getDemographicDTO().getDemoInUserLang().getAddressDTO().getLocationDTO().getPostalCode());
		velocityContext.put(RegistrationConstants.TEMPLATE_MOBILE, registration.getDemographicDTO().getDemoInUserLang().getMobile());
		String email = registration.getDemographicDTO().getDemoInUserLang().getEmailId();
		if (email == null || email == RegistrationConstants.EMPTY) {
			velocityContext.put(RegistrationConstants.TEMPLATE_EMAIL, RegistrationConstants.EMPTY);
		} else {
			velocityContext.put(RegistrationConstants.TEMPLATE_EMAIL, email);
		}

		List<DocumentDetailsDTO> documents = registration.getDemographicDTO().getApplicantDocumentDTO()
				.getDocumentDetailsDTO();
		List<String> documentNames = new ArrayList<>();
		for (DocumentDetailsDTO document : documents) {
			documentNames.add(document.getDocumentName());
		}

		String documentsList = documentNames.stream().map(Object::toString).collect(Collectors.joining(", "));
		velocityContext.put(RegistrationConstants.TEMPLATE_DOCUMENTS, documentsList);
		velocityContext.put(RegistrationConstants.TEMPLATE_OPERATOR_NAME, registration.getOsiDataDTO().getOperatorID());

		byte[] applicantImageBytes = registration.getDemographicDTO().getApplicantDocumentDTO().getPhoto();
		String applicantImageEncodedBytes = StringUtils.newStringUtf8(Base64.encodeBase64(applicantImageBytes, false));
		velocityContext.put(RegistrationConstants.TEMPLATE_IMAGE_SOURCE, RegistrationConstants.TEMPLATE_IMAGE_ENCODING + applicantImageEncodedBytes);

		if(registration.getDemographicDTO().getApplicantDocumentDTO().isHasExceptionPhoto()) {
			velocityContext.put(RegistrationConstants.TEMPLATE_WITH_EXCEPTION_IMAGE, null);
			velocityContext.put(RegistrationConstants.TEMPLATE_WITHOUT_EXCEPTION_IMAGE, RegistrationConstants.TEMPLATE_STYLE_PROPERTY);
			byte[] exceptionImageBytes = registration.getDemographicDTO().getApplicantDocumentDTO().getExceptionPhoto();
			String exceptionImageEncodedBytes = StringUtils.newStringUtf8(Base64.encodeBase64(exceptionImageBytes, false));
			velocityContext.put(RegistrationConstants.TEMPLATE_EXCEPTION_IMAGE_SOURCE, RegistrationConstants.TEMPLATE_IMAGE_ENCODING + exceptionImageEncodedBytes);
		}
		else {
			velocityContext.put(RegistrationConstants.TEMPLATE_WITHOUT_EXCEPTION_IMAGE, null);
			velocityContext.put(RegistrationConstants.TEMPLATE_WITH_EXCEPTION_IMAGE, RegistrationConstants.TEMPLATE_STYLE_PROPERTY);
		}
		/* get the quality ranking for fingerprints of the applicant
		HashMap<String, Integer> fingersQuality = getFingerPrintQualityRanking(registration);
		int count=1;
		for (Map.Entry<String, Integer> entry : fingersQuality.entrySet()) {
			if (entry.getValue() != 0) {
				// display rank of quality for the captured fingerprints
				velocityContext.put(entry.getKey(), count++);
			} else {
				// display cross mark for missing fingerprints
				velocityContext.put(entry.getKey(), RegistrationConstants.TEMPLATE_MISSING_FINGER);
			}
		}*/
		File imageFile = new File(RegistrationConstants.TEMPLATE_HANDS_IMAGE_PATH);
		velocityContext.put("handsImageSource", "file:/"+ imageFile.getAbsolutePath().replace("\\", "/"));

		velocityContext.put("rightIndexFinger", "1");
		velocityContext.put("rightMiddleFinger", "4");
		velocityContext.put("rightRingFinger", "2");
		velocityContext.put("rightLittleFinger", "5");
		velocityContext.put("rightThumb", "3");
		velocityContext.put("leftIndexFinger", "6");
		velocityContext.put("leftMiddleFinger", "2");
		velocityContext.put("leftRingFinger", "2");
		velocityContext.put("leftLittleFinger", "4");
		velocityContext.put("leftThumb", "5");
		// get the total count of fingerprints captured and irises captured
		List<FingerprintDetailsDTO> capturedFingers = registration.getBiometricDTO()
				.getApplicantBiometricDTO().getFingerprintDetailsDTO();

		List<IrisDetailsDTO> capturedIris = registration.getBiometricDTO().getApplicantBiometricDTO()
				.getIrisDetailsDTO();
		
		int[] fingersAndIrises = { capturedFingers.size(), capturedIris.size() };
		
		velocityContext.put(RegistrationConstants.TEMPLATE_BIOMETRICS_CAPTURED,
				fingersAndIrises[0] + " fingers and " + fingersAndIrises[1] + " Iris(es)");

		Writer writer = new StringWriter();
		Velocity.evaluate(velocityContext, writer, RegistrationConstants.TEMPLATE_LOGTAG, templateReader);
		return writer;
	}
	
	/**
	 * @param templateText
	 *            - string which contains the data of template that is used to generate notification
	 * @param registration
	 *            - RegistrationDTO to display required fields on the template
	 * @return writer - After mapping all the fields into the template, it is
	 *         written into a StringWriter and returned
	 */
	public Writer generateNotificationTemplate(String templateText, RegistrationDTO registration) {
		
		Reader templateReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(templateText.getBytes())));

		VelocityContext velocityContext = new VelocityContext();
		
		velocityContext.put(RegistrationConstants.TEMPLATE_RESIDENT_NAME, registration.getDemographicDTO().getDemoInUserLang().getFullName());
		velocityContext.put(RegistrationConstants.TEMPLATE_REGISTRATION_ID, registration.getRegistrationId());

		SimpleDateFormat sdf = new SimpleDateFormat(RegistrationConstants.TEMPLATE_DATE_FORMAT);
		String currentDate = sdf.format(new Date());

		// map the respective fields with the values in the enrolmentDTO
		velocityContext.put(RegistrationConstants.TEMPLATE_DATE, currentDate);
		velocityContext.put(RegistrationConstants.TEMPLATE_FULL_NAME, registration.getDemographicDTO().getDemoInUserLang().getFullName());
		Date dob = registration.getDemographicDTO().getDemoInUserLang().getDateOfBirth();
		if(dob == null) {
			velocityContext.put(RegistrationConstants.TEMPLATE_DOB, registration.getDemographicDTO().getDemoInUserLang().getAge());
		} else {
			velocityContext.put(RegistrationConstants.TEMPLATE_DOB, 
					DateUtils.formatDate(dob, "dd-MM-YYYY"));
		}
		velocityContext.put(RegistrationConstants.TEMPLATE_GENDER, registration.getDemographicDTO().getDemoInUserLang().getGender());
		velocityContext.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE1,
				registration.getDemographicDTO().getDemoInUserLang().getAddressDTO().getAddressLine1());
		velocityContext.put(RegistrationConstants.TEMPLATE_ADDRESS_LINE2,
				registration.getDemographicDTO().getDemoInUserLang().getAddressDTO().getAddressLine2());
		velocityContext.put(RegistrationConstants.TEMPLATE_CITY, registration.getDemographicDTO().getDemoInUserLang().getAddressDTO().getLocationDTO().getCity());
		velocityContext.put(RegistrationConstants.TEMPLATE_STATE, registration.getDemographicDTO().getDemoInUserLang().getAddressDTO().getLocationDTO().getProvince());
		velocityContext.put(RegistrationConstants.TEMPLATE_COUNTRY,
				registration.getDemographicDTO().getDemoInUserLang().getAddressDTO().getLocationDTO().getRegion());
		velocityContext.put(RegistrationConstants.TEMPLATE_POSTAL_CODE,
				registration.getDemographicDTO().getDemoInUserLang().getAddressDTO().getLocationDTO().getPostalCode());
		velocityContext.put(RegistrationConstants.TEMPLATE_MOBILE, registration.getDemographicDTO().getDemoInUserLang().getMobile());
		String email = registration.getDemographicDTO().getDemoInUserLang().getEmailId();
		if (email == null || email == RegistrationConstants.EMPTY) {
			velocityContext.put(RegistrationConstants.TEMPLATE_EMAIL, RegistrationConstants.EMPTY);
		} else {
			velocityContext.put(RegistrationConstants.TEMPLATE_EMAIL, email);
		}		

		Writer writer = new StringWriter();
		Velocity.evaluate(velocityContext, writer, RegistrationConstants.NOTIFICATION_TEMPLATE, templateReader);
		return writer;
	}

	/**
	 * @param enrolment
	 *            - EnrolmentDTO to get the biometric details
	 * @return hash map which gives the set of fingerprints captured and their
	 *         respective rankings based on quality score
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, Integer> getFingerPrintQualityRanking(RegistrationDTO registration) {
		// for storing the fingerprints captured and their respective quality scores
		HashMap<String, Double> fingersQuality = new HashMap<>();

		// list of missing fingers
		List<BiometricExceptionDTO> exceptionFingers = registration.getBiometricDTO()
				.getApplicantBiometricDTO().getFingerPrintBiometricExceptionDTO();
		//
		if (exceptionFingers != null) {
			for (BiometricExceptionDTO exceptionFinger : exceptionFingers) {
				fingersQuality.put(exceptionFinger.getMissingBiometric(), (double) 0);
			}
		}
		List<FingerprintDetailsDTO> availableFingers = registration.getBiometricDTO().getApplicantBiometricDTO()
				.getFingerprintDetailsDTO();
		for (FingerprintDetailsDTO availableFinger : availableFingers) {
			fingersQuality.put(availableFinger.getFingerType(), availableFinger.getQualityScore());
		}

		Object[] fingerQualitykeys = fingersQuality.entrySet().toArray();
		Arrays.sort(fingerQualitykeys, new Comparator<Object>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			public int compare(Object fingetPrintQuality1, Object fingetPrintQuality2) {
				return ((Map.Entry<String, Double>) fingetPrintQuality2).getValue()
						.compareTo(((Map.Entry<String, Double>) fingetPrintQuality1).getValue());
			}
		});
		
		
		LinkedHashMap<String, Double> fingersQualitySorted = new LinkedHashMap<>();
		for (Object fingerPrintQualityKey : fingerQualitykeys) {
			String finger = ((Map.Entry<String, Double>) fingerPrintQualityKey).getKey();
			double quality = ((Map.Entry<String, Double>) fingerPrintQualityKey).getValue();
			fingersQualitySorted.put(finger, quality);
		}
		
		
		HashMap<String, Integer> fingersQualityRanking = new HashMap<>();
		int rank = 1;
		double prev = 1.0;
		for (Map.Entry<String, Double> entry : fingersQualitySorted.entrySet()) {
			if (entry.getValue() != 0) {
				if (Double.compare(entry.getValue(), prev) ==  0 || Double.compare(prev, 1.0) == 0) {
					fingersQualityRanking.put(entry.getKey(), rank);
				} else {
					fingersQualityRanking.put(entry.getKey(), ++rank);
				}
				prev = entry.getValue();
			} else {
				fingersQualityRanking.put(entry.getKey(), entry.getValue().intValue());
			}
		}

		return fingersQualityRanking;
	}
}