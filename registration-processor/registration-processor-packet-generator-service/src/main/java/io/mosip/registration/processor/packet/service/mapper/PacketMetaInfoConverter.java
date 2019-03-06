package io.mosip.registration.processor.packet.service.mapper;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import io.mosip.kernel.core.util.DateUtils;

import io.mosip.registration.processor.packet.service.constants.RegistrationConstants;

import io.mosip.registration.processor.packet.service.dto.BaseDTO;
import io.mosip.registration.processor.packet.service.dto.RegistrationDTO;
import io.mosip.registration.processor.packet.service.dto.RegistrationMetaDataDTO;
import io.mosip.registration.processor.packet.service.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.processor.packet.service.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.processor.packet.service.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.processor.packet.service.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.processor.packet.service.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.processor.packet.service.dto.demographic.DemographicDTO;
import io.mosip.registration.processor.packet.service.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.processor.packet.service.dto.json.metadata.Applicant;
import io.mosip.registration.processor.packet.service.dto.json.metadata.Biometric;
import io.mosip.registration.processor.packet.service.dto.json.metadata.BiometricDetails;
import io.mosip.registration.processor.packet.service.dto.json.metadata.BiometricException;
import io.mosip.registration.processor.packet.service.dto.json.metadata.Document;
import io.mosip.registration.processor.packet.service.dto.json.metadata.FieldValue;
import io.mosip.registration.processor.packet.service.dto.json.metadata.Identity;
import io.mosip.registration.processor.packet.service.dto.json.metadata.Introducer;
import io.mosip.registration.processor.packet.service.dto.json.metadata.PacketMetaInfo;
import io.mosip.registration.processor.packet.service.dto.json.metadata.Photograph;
import io.mosip.registration.processor.packet.service.exception.RegBaseUncheckedException;
import io.mosip.registration.processor.packet.service.util.checksum.CheckSumUtil;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * The custom Orika Mapper converter class for converting the
 * {@link RegistrationDTO} object to {@link PacketMetaInfo}
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class PacketMetaInfoConverter extends CustomConverter<RegistrationDTO, PacketMetaInfo> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.glasnost.orika.Converter#convert(java.lang.Object,
	 * ma.glasnost.orika.metadata.Type)
	 */
	@Override
	public PacketMetaInfo convert(RegistrationDTO source, Type<? extends PacketMetaInfo> destinationType) {
		// Instantiate PacketMetaInfo object
		PacketMetaInfo packetMetaInfo = new PacketMetaInfo();
		try {
			// Initialize PacketMetaInfo object
			Identity identity = new Identity();
			packetMetaInfo.setIdentity(identity);
			List<BiometricException> exceptionBiometrics = new LinkedList<>();
			identity.setExceptionBiometrics(exceptionBiometrics);
			Biometric biometric = new Biometric();
			identity.setBiometric(biometric);
			Applicant applicant = new Applicant();
			biometric.setApplicant(applicant);
			Introducer introducer = new Introducer();
			biometric.setIntroducer(introducer);

			// Load from ApplicationContext
			String language = "eng";

			ApplicantDocumentDTO documentDTO = source.getDemographicDTO().getApplicantDocumentDTO();

			// Set Photograph
			identity.setApplicantPhotograph(buildPhotograph("label", language, documentDTO.getNumRetry(),
					documentDTO.getPhotographName(), documentDTO.getQualityScore()));

			// Set Exception Photograph
			identity.setExceptionPhotograph(
					buildPhotograph("label", language, 0, documentDTO.getExceptionPhotoName(), 0));

			// Set Documents
			identity.setDocuments(buildDocuments(source.getDemographicDTO()));

			// Add Biometric Details
			BiometricInfoDTO biometricInfoDTO = source.getBiometricDTO().getApplicantBiometricDTO();

			// Get the captured fingerprints
			List<FingerprintDetailsDTO> fingerprintDetailsDTOs = biometricInfoDTO.getFingerprintDetailsDTO();

			// Put the finger-prints to map
			Map<String, FingerprintDetailsDTO> fingerprintMap = new WeakHashMap<>();
			if (fingerprintDetailsDTOs != null) {
				for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
					for (FingerprintDetailsDTO segmentedFingerprint : fingerprintDetailsDTO
							.getSegmentedFingerprints()) {
						fingerprintMap.put(segmentedFingerprint.getFingerType().toUpperCase(), segmentedFingerprint);
					}
				}
			}

			// Set Left Index Finger
			String biometricType = RegistrationConstants.FINGERPRINT.toLowerCase();
			applicant.setLeftIndex(getBiometric(fingerprintMap.get("LEFTINDEX"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Middle Finger
			applicant.setLeftMiddle(getBiometric(fingerprintMap.get("LEFTMIDDLE"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Ring Finger
			applicant.setLeftRing(getBiometric(fingerprintMap.get("LEFTRING"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Little Finger
			applicant.setLeftLittle(getBiometric(fingerprintMap.get("LEFTLITTLE"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Thumb Finger
			applicant.setLeftThumb(getBiometric(fingerprintMap.get("LEFTTHUMB"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Right Index Finger
			applicant.setRightIndex(getBiometric(fingerprintMap.get("RIGHTINDEX"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Middle Finger
			applicant.setRightMiddle(getBiometric(fingerprintMap.get("RIGHTMIDDLE"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Ring Finger
			applicant.setRightRing(getBiometric(fingerprintMap.get("RIGHTRING"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Little Finger
			applicant.setRightLittle(getBiometric(fingerprintMap.get("RIGHTLITTLE"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Thumb Finger
			applicant.setRightThumb(getBiometric(fingerprintMap.get("RIGHTTHUMB"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Get captured Iris Details
			List<IrisDetailsDTO> irisDetailsDTOs = biometricInfoDTO.getIrisDetailsDTO();

			// Put Iris to map
			Map<String, IrisDetailsDTO> irisMap = new WeakHashMap<>();
			if (irisDetailsDTOs != null) {
				for (IrisDetailsDTO irisDetailsDTO : irisDetailsDTOs) {
					irisMap.put(irisDetailsDTO.getIrisType().toUpperCase(), irisDetailsDTO);
				}
			}

			// Set Left Eye
			biometricType = RegistrationConstants.IRIS.toLowerCase();
			applicant.setLeftEye(
					getBiometric(irisMap.get("LEFTEYE"), language, biometricType, RegistrationConstants.INDIVIDUAL));

			// Set Right Eye
			applicant.setRightEye(
					getBiometric(irisMap.get("RIGHTEYE"), language, biometricType, RegistrationConstants.INDIVIDUAL));

			// Add captured biometric exceptions
			identity.getExceptionBiometrics()
					.addAll(getExceptionBiometrics(biometricInfoDTO.getBiometricExceptionDTO(), language));

			// Set Parent Finger-print Image
			getIntroducerBiometrics(source, introducer, language);

			// Set MetaData
			identity.setMetaData(getMetaData(source));

			// Set OSIData
			identity.setOsiData(getOSIData(source));

			// Set Checksum
			List<FieldValue> checkSums = new LinkedList<>();
			Map<String, String> checkSumMap = CheckSumUtil.getCheckSumMap();
			checkSumMap.forEach((key, value) -> checkSums.add(buildFieldValue(key, value)));
			identity.setCheckSum(checkSums);

			setuinUpdatedFields(source, identity);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_META_CONVERTOR,
					runtimeException.toString());
		}
		return packetMetaInfo;
	}

	/**
	 * Set uin updated fields.
	 *
	 * @param source the source
	 * @param identity the identity
	 */
	private void setuinUpdatedFields(RegistrationDTO source, Identity identity) {
		// uinUpdatedFields
		if (source.getSelectionListDTO() != null) {
			List<String> uinUpdateFields = new ArrayList<>();
			BeanWrapper beanWrapper = new BeanWrapperImpl(source.getSelectionListDTO());
			PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				Object beanWrapperValue = beanWrapper.getPropertyValue(pd.getName());
				if (beanWrapperValue instanceof Boolean && (Boolean) beanWrapperValue)
					uinUpdateFields.add(pd.getName());
			}
			identity.setUinUpdatedFields(uinUpdateFields);
		}
	}

	private void getIntroducerBiometrics(RegistrationDTO source, Introducer introducer, String language) {
		BiometricInfoDTO biometricInfoDTO;
		String biometricType;
		biometricInfoDTO = source.getBiometricDTO().getIntroducerBiometricDTO();
		if (biometricInfoDTO != null) {
			List<FingerprintDetailsDTO> fingerprints = biometricInfoDTO.getFingerprintDetailsDTO();
			if (fingerprints != null && !fingerprints.isEmpty()) {
				biometricType = RegistrationConstants.FINGERPRINT.toLowerCase();
				introducer.setIntroducerFingerprint(
						getBiometric(fingerprints.get(0), language, biometricType, RegistrationConstants.INTRODUCER));
			}

			List<IrisDetailsDTO> parentIris = biometricInfoDTO.getIrisDetailsDTO();
			if (parentIris != null && !parentIris.isEmpty()) {
				biometricType = RegistrationConstants.IRIS.toLowerCase();
				introducer.setIntroducerIris(
						getBiometric(parentIris.get(0), language, biometricType, RegistrationConstants.INTRODUCER));
			}
		}
	}

	private Photograph buildPhotograph(String label, String language, int numRetry, String photographName,
			double qualityScore) {
		Photograph photograph = null;
		if (photographName != null) {
			photograph = new Photograph();
			photograph.setLabel(label);
			photograph.setLanguage(language);
			photograph.setNumRetry(numRetry);
			photograph.setPhotographName(removeFileExt(photographName));
			photograph.setQualityScore(qualityScore);
		}

		return photograph;
	}

	private List<Document> buildDocuments(DemographicDTO demographicDTO) {
		List<Document> documents = new ArrayList<>();

		Map<String, DocumentDetailsDTO> documentDetailsDTOs = demographicDTO.getApplicantDocumentDTO().getDocuments();
		
		for (Entry<String, DocumentDetailsDTO> documentCategory : documentDetailsDTOs.entrySet()) {
			DocumentDetailsDTO document = documentCategory.getValue();
			documents.add(getDocument(removeFileExt(document.getValue()), documentCategory.getKey(), document.getType(),
					document.getOwner()));
		}

		if (demographicDTO.getApplicantDocumentDTO().getAcknowledgeReceipt() != null) {
			// Add the Acknowledgement Receipt
			documents.add(
					getDocument(removeFileExt(demographicDTO.getApplicantDocumentDTO().getAcknowledgeReceiptName()),
							RegistrationConstants.ACK_RECEIPT, RegistrationConstants.ACK_RECEIPT, "Self"));
		}

		return documents;
	}

	private Document getDocument(String documentName, String documentType, String documentCategory,
			String documentOwner) {
		Document document = new Document();
		document.setDocumentName(documentName);
		document.setDocumentType(documentType);
		document.setDocumentCategory(documentCategory);
		document.setDocumentOwner(documentOwner);

		return document;
	}

	private BiometricDetails getBiometric(BaseDTO biometricDTO, String language, String biometricType,
			String personType) {
		BiometricDetails biometricDetails = null;
		/*if (biometricDTO != null) {
			if (biometricDTO instanceof FingerprintDetailsDTO) {
				FingerprintDetailsDTO fingerprint = (FingerprintDetailsDTO) biometricDTO;
				biometricDetails = buildBiometric("label", language, biometricType,
						getBIRUUID(personType, fingerprint.getFingerType()), fingerprint.getQualityScore(),
						fingerprint.getNumRetry(), fingerprint.isForceCaptured());
			} else if (biometricDTO instanceof IrisDetailsDTO) {
				IrisDetailsDTO iris = (IrisDetailsDTO) biometricDTO;
				biometricDetails = buildBiometric("label", language, biometricType,
						getBIRUUID(personType, iris.getIrisType()), iris.getQualityScore(), iris.getNumOfIrisRetry(),
						iris.isForceCaptured());
			}
		}*/
		return biometricDetails;
	}

	private BiometricDetails buildBiometric(String label, String language, String type, String birIndex,
			double qualityScore, int numRetry, boolean forceCaptured) {
		BiometricDetails biometricDetails = new BiometricDetails();
		biometricDetails.setLabel(label);
		biometricDetails.setLanguage(language);
		biometricDetails.setType(type);
		biometricDetails.setImageName(birIndex);
		biometricDetails.setQualityScore(qualityScore);
		biometricDetails.setNumRetry(numRetry);
		biometricDetails.setForceCaptured(forceCaptured);

		return biometricDetails;
	}

	private List<BiometricException> getExceptionBiometrics(List<BiometricExceptionDTO> biometricExceptionDTOs,
			String language) {
		List<BiometricException> exceptionBiometrics = new LinkedList<>();

		// Add finger-print biometric exceptions
		if (biometricExceptionDTOs != null) {
			for (BiometricExceptionDTO biometricExceptionDTO : biometricExceptionDTOs) {
				exceptionBiometrics.add(buildExceptionBiometric(language, biometricExceptionDTO.getBiometricType(),
						biometricExceptionDTO.getMissingBiometric(), biometricExceptionDTO.getExceptionType(),
						biometricExceptionDTO.getExceptionDescription()));
			}
		}

		return exceptionBiometrics;
	}

	private BiometricException buildExceptionBiometric(String language, String type, String missingBiometric,
			String exceptionType, String exceptionDescription) {
		BiometricException exceptionBiometric = new BiometricException();
		exceptionBiometric.setLanguage(language);
		exceptionBiometric.setType(type);
		exceptionBiometric.setMissingBiometric(missingBiometric);
		exceptionBiometric.setExceptionType(exceptionType);
		exceptionBiometric.setExceptionDescription(exceptionDescription);

		return exceptionBiometric;
	}

	private List<FieldValue> getMetaData(RegistrationDTO registrationDTO) {
		List<FieldValue> metaData = new LinkedList<>();

		// Get RegistrationMetaDataDTO
		RegistrationMetaDataDTO metaDataDTO = registrationDTO.getRegistrationMetaDataDTO();

		// Add Geo-location Latitude
		metaData.add(buildFieldValue("geoLocLatitude", String.valueOf(metaDataDTO.getGeoLatitudeLoc())));
		// Add Geo-location Longitude
		metaData.add(buildFieldValue("geoLoclongitude", String.valueOf(metaDataDTO.getGeoLongitudeLoc())));
		// Add Registration Type
		metaData.add(buildFieldValue("registrationType", metaDataDTO.getRegistrationCategory()));
		// Add Applicant Type
		metaData.add(buildFieldValue("applicantType", metaDataDTO.getApplicationType()));
		// Add Pre-Registration ID
		metaData.add(buildFieldValue("preRegistrationId", registrationDTO.getPreRegistrationId()));
		// Add Registration ID
		metaData.add(buildFieldValue("registrationId", registrationDTO.getRegistrationId()));
		// Add Machine ID
		metaData.add(buildFieldValue("machineId", metaDataDTO.getMachineId()));
		// Add Dongle ID
		metaData.add(buildFieldValue("dongleId", metaDataDTO.getDeviceId()));
		// Add MAC ID
		//metaData.add(buildFieldValue("macId", RegistrationSystemPropertiesChecker.getMachineId()));
		// Add Center ID
		metaData.add(buildFieldValue("centerId", metaDataDTO.getCenterId()));
		// Add UIN
		metaData.add(buildFieldValue("uin", metaDataDTO.getUin()));
		// Add Previous Registration ID
		metaData.add(buildFieldValue("previousRID", metaDataDTO.getPreviousRID()));
		// Add Introducer Type
		metaData.add(buildFieldValue("introducerType", registrationDTO.getOsiDataDTO().getIntroducerType()));
		// Add consentOfApplicant
		metaData.add(buildFieldValue("consentOfApplicant",
				registrationDTO.getRegistrationMetaDataDTO().getConsentOfApplicant()));

		// Validate whether Introducer has provided UIN or RID
		String introducerRID = null;
		String introducerUIN = null;
		String introducerRIDorUIN = registrationDTO.getRegistrationMetaDataDTO().getParentOrGuardianUINOrRID();
		/*if (introducerRIDorUIN != null) {
			if (introducerRIDorUIN.length() == Integer
					.parseInt(AppConfig.getApplicationProperty("uin_length"))) {
				introducerUIN = introducerRIDorUIN;
			} else {
				introducerRID = introducerRIDorUIN;
			}
		}*/

		// Add Introducer RID
		metaData.add(buildFieldValue("introducerRID", introducerRID));
		// Add Introducer UIN
		metaData.add(buildFieldValue("introducerUIN", introducerUIN));
		// Add Officer Biometrics
		metaData.addAll(getOfficerBiometric(registrationDTO.getBiometricDTO().getOperatorBiometricDTO(),
				RegistrationConstants.OFFICER.toLowerCase(), RegistrationConstants.BIOMETRIC_TYPE));
		// Add Supervisor Biometrics
		metaData.addAll(getOfficerBiometric(registrationDTO.getBiometricDTO().getOperatorBiometricDTO(),
				RegistrationConstants.SUPERVISOR.toLowerCase(), RegistrationConstants.BIOMETRIC_TYPE));
		// Add Introducer Biometrics
		metaData.addAll(getOfficerBiometric(registrationDTO.getBiometricDTO().getIntroducerBiometricDTO(),
				RegistrationConstants.INTRODUCER.toLowerCase(), RegistrationConstants.BIOMETRIC_TYPE));
		// Add Registration Creation Date
		metaData.add(buildFieldValue("creationDate", DateUtils.formatToISOString(LocalDateTime.now())));

		metaData.add(buildFieldValue("applicantTypeCode", metaDataDTO.getApplicantTypeCode()));
		
		return metaData;
	}

	@SuppressWarnings("unchecked")
	private List<FieldValue> getOSIData(RegistrationDTO registrationDTO) {
		List<FieldValue> osiData = new LinkedList<>();
		// Add Operator ID
		osiData.add(buildFieldValue("officerId", registrationDTO.getOsiDataDTO().getOperatorID()));
		
		// Add Supervisor ID
		osiData.add(buildFieldValue("supervisorId", registrationDTO.getOsiDataDTO().getSupervisorID()));
		

		// Add Supervisor Password
		osiData.add(buildFieldValue("supervisorPassword",
				String.valueOf(registrationDTO.getOsiDataDTO().isSuperviorAuthenticatedByPassword())));
		// Add Officer Password
		osiData.add(buildFieldValue("officerPassword",
				String.valueOf(registrationDTO.getOsiDataDTO().isOperatorAuthenticatedByPassword())));

		// Add Supervisor PIN
		osiData.add(buildFieldValue("supervisorPIN", null));
		// Add Officer PIN
		osiData.add(buildFieldValue("officerPIN", null));

		// Add Supervisor Face Image
		osiData.add(buildFieldValue("supervisorFaceImage", null));
		// Add Officer Face Image
		osiData.add(buildFieldValue("officerFaceImage", null));

		// Add Supervisor OTP Authentication Image
		osiData.add(buildFieldValue("supervisorOTPAuthentication",
				String.valueOf(registrationDTO.getOsiDataDTO().isSuperviorAuthenticatedByPIN())));
		// Add Officer Face Image
		osiData.add(buildFieldValue("officerOTPAuthentication",
				String.valueOf(registrationDTO.getOsiDataDTO().isOperatorAuthenticatedByPIN())));

		return osiData;
	}

	private List<FieldValue> getOfficerBiometric(BiometricInfoDTO officerBiometric, String officerType, String field) {
		List<FieldValue> officer = new LinkedList<>();
		String fingerprintImageName = null;
		String irisImageName = null;

		/*if (officerBiometric != null) {
			FingerprintDetailsDTO fingerprint = (FingerprintDetailsDTO) getObjectAt(
					officerBiometric.getFingerprintDetailsDTO(), 0);
			if (fingerprint != null) {
				fingerprintImageName = fingerprint.getFingerType();
				if (field.equals(RegistrationConstants.BIOMETRIC_IMAGE)) {
					fingerprintImageName = getBIRUUID(officerType, fingerprintImageName);
				}
			}

			IrisDetailsDTO iris = (IrisDetailsDTO) getObjectAt(officerBiometric.getIrisDetailsDTO(), 0);
			if (iris != null) {
				irisImageName = iris.getIrisType();
				if (field.equals(RegistrationConstants.BIOMETRIC_IMAGE)) {
					irisImageName = getBIRUUID(officerType, irisImageName);
				}
			}
		}
*/
		officer.add(buildFieldValue(officerType + "Fingerprint" + field, fingerprintImageName));
		officer.add(buildFieldValue(officerType + "Iris" + field, irisImageName));

		return officer;
	}

	private FieldValue buildFieldValue(String label, String value) {
		FieldValue fieldValue = new FieldValue();
		fieldValue.setLabel(label);
		fieldValue.setValue(value);
		return fieldValue;
	}

	private boolean checkNull(List<?> list) {
		boolean isNull = false;
		if (list == null || list.isEmpty()) {
			isNull = true;
		}
		return isNull;
	}

	private Object getObjectAt(List<?> list, int index) {
		Object object = null;
		if (!checkNull(list) && (index < list.size())) {
			object = list.get(index);
		}
		return object;
	}

	private String removeFileExt(String fileName) {
		if (fileName.contains(".")) {
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		}
		return fileName;
	}

	
}
