package io.mosip.registration.processor.packet.storage.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bouncycastle.asn1.cms.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.registration.processor.core.packet.dto.BiometricData;
import io.mosip.registration.processor.core.packet.dto.BiometricException;

import io.mosip.registration.processor.core.packet.dto.Document;

import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Introducer;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicPKEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentPKEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantFingerprintEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantFingerprintPKEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantIrisEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantIrisPKEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographPKEntity;
import io.mosip.registration.processor.packet.storage.entity.BiometricExceptionEntity;
import io.mosip.registration.processor.packet.storage.entity.BiometricExceptionPKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachinePKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiPkEntity;

/**
 * The Class PacketInfoMapper.
 */
public class PacketInfoMapper {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketInfoMapper.class);

	private static final String REGISTRATION_ID="registrationId";
	private static final String PRE_REGISTRATION_ID="preRegistrationId";
	/**
	 * Instantiates a new packet info mapper.
	 */
	private PacketInfoMapper() {
		super();
	}

	/**
	 * Convert app doc dto to app doc entity.
	 *
	 * @param documentDto            the document dto
	 * @param metaData the meta data
	 * @return the applicant document entity
	 */
	public static ApplicantDocumentEntity convertAppDocDtoToEntity(Document documentDto, List<FieldValue> metaData) {

		Optional<FieldValue> regId=metaData.stream().filter(m -> m.getLabel().equals(REGISTRATION_ID)).findFirst();
		String registrationId="";
		if(regId.isPresent())registrationId=regId.get().getValue();
		
		Optional<FieldValue> preregId=metaData.stream().filter(m -> m.getLabel().equals(PRE_REGISTRATION_ID)).findFirst();
		String preregistrationId="";
		if(preregId.isPresent())preregistrationId=preregId.get().getValue();
		
		ApplicantDocumentEntity applicantDocumentEntity = new ApplicantDocumentEntity();
		ApplicantDocumentPKEntity applicantDocumentPKEntity = new ApplicantDocumentPKEntity();
		applicantDocumentPKEntity.setDocCatCode(documentDto.getDocumentCategory());
		applicantDocumentPKEntity.setDocTypCode(documentDto.getDocumentType());
		applicantDocumentPKEntity.setRegId(registrationId);

		applicantDocumentEntity.setId(applicantDocumentPKEntity);
		applicantDocumentEntity.setPreRegId(preregistrationId);
		applicantDocumentEntity.setDocOwner(documentDto.getDocumentOwner());
		applicantDocumentEntity.setDocName(documentDto.getDocumentName());
		applicantDocumentEntity.setDocOwner(documentDto.getDocumentOwner());
		applicantDocumentEntity.setDocFileFormat(".zip");
		applicantDocumentEntity.setActive(true);

		return applicantDocumentEntity;
	}

	

	/**
	 * Convert iris to iris entity.
	 *
	 * @param iris            the iris
	 * @param metaData the meta data
	 * @return the applicant iris entity
	 */
	public static ApplicantIrisEntity convertIrisDtoToEntity(BiometricData iris, List<FieldValue>  metaData) {
		Optional<FieldValue> regId=metaData.stream().filter(m -> m.getLabel().equals(REGISTRATION_ID)).findFirst();
		String registrationId="";
		if(regId.isPresent())registrationId=regId.get().getValue();
		
		Optional<FieldValue> preregId=metaData.stream().filter(m -> m.getLabel().equals(PRE_REGISTRATION_ID)).findFirst();
		String preregistrationId="";
		if(preregId.isPresent())preregistrationId=preregId.get().getValue();
		
		ApplicantIrisEntity applicantIrisEntity = new ApplicantIrisEntity();
		ApplicantIrisPKEntity applicantIrisPKEntity = new ApplicantIrisPKEntity();

		applicantIrisPKEntity.setRegId(registrationId);
		applicantIrisPKEntity.setTyp(iris.getType());

		applicantIrisEntity.setId(applicantIrisPKEntity);
		applicantIrisEntity.setNoOfRetry(iris.getNumRetry());
		applicantIrisEntity.setImageName(iris.getImageName());
		applicantIrisEntity.setPreRegId(preregistrationId);
		applicantIrisEntity.setQualityScore(BigDecimal.valueOf(iris.getQualityScore()));
		applicantIrisEntity.setActive(true);

		return applicantIrisEntity;
	}

	/**
	 * Convert fingerprint to fingerprint entity.
	 *
	 * @param fingerprint            the fingerprint
	 * @param metaData the meta data
	 * @return the applicant fingerprint entity
	 */
	public static ApplicantFingerprintEntity convertFingerprintDtoToEntity(BiometricData fingerprint, List<FieldValue> metaData) {
		Optional<FieldValue> regId=metaData.stream().filter(m -> m.getLabel().equals(REGISTRATION_ID)).findFirst();
		String registrationId="";
		if(regId.isPresent())registrationId=regId.get().getValue();
		
		Optional<FieldValue> preregId=metaData.stream().filter(m -> m.getLabel().equals(PRE_REGISTRATION_ID)).findFirst();
		String preregistrationId="";
		if(preregId.isPresent())preregistrationId=preregId.get().getValue();
		ApplicantFingerprintEntity applicantFingerprintEntity = new ApplicantFingerprintEntity();
		ApplicantFingerprintPKEntity applicantFingerprintPKEntity = new ApplicantFingerprintPKEntity();

		applicantFingerprintPKEntity.setRegId(registrationId);
		applicantFingerprintPKEntity.setTyp(fingerprint.getType());

		applicantFingerprintEntity.setId(applicantFingerprintPKEntity);
		applicantFingerprintEntity.setNoOfRetry(fingerprint.getNumRetry());
		applicantFingerprintEntity.setImageName(fingerprint.getImageName());
		applicantFingerprintEntity.setNoOfRetry(fingerprint.getNumRetry());
		applicantFingerprintEntity.setPreRegId(preregistrationId);
		applicantFingerprintEntity.setQualityScore(BigDecimal.valueOf(fingerprint.getQualityScore()));
		applicantFingerprintEntity.setActive(true);

		return applicantFingerprintEntity;

	}

	/**
	 * Convert biometric exc to biometric exc entity.
	 *
	 * @param exceptionFingerprint            the exception fingerprint
	 * @param metaData the meta data
	 * @return the biometric exception entity
	 */
	public static BiometricExceptionEntity convertBiometricExceptioDtoToEntity(
			BiometricException exception, List<FieldValue> metaData) {
		Optional<FieldValue> regId=metaData.stream().filter(m -> m.getLabel().equals(REGISTRATION_ID)).findFirst();
		String registrationId="";
		if(regId.isPresent())registrationId=regId.get().getValue();
		
		Optional<FieldValue> preregId=metaData.stream().filter(m -> m.getLabel().equals(PRE_REGISTRATION_ID)).findFirst();
		String preregistrationId="";
		if(preregId.isPresent())preregistrationId=preregId.get().getValue();
		BiometricExceptionEntity bioMetricExceptionEntity = new BiometricExceptionEntity();
		BiometricExceptionPKEntity biometricExceptionPKEntity = new BiometricExceptionPKEntity();
		biometricExceptionPKEntity.setRegId(registrationId);
		biometricExceptionPKEntity.setMissingBio(exception.getMissingBiometric());
		biometricExceptionPKEntity.setLangCode("en");

		bioMetricExceptionEntity.setId(biometricExceptionPKEntity);
		bioMetricExceptionEntity.setPreregId(preregistrationId);
		bioMetricExceptionEntity.setBioTyp(exception.getType());
		bioMetricExceptionEntity.setExcpDescr(exception.getExceptionDescription());
		bioMetricExceptionEntity.setExcpTyp(exception.getExceptionType());
		bioMetricExceptionEntity.setIsDeleted(false);

		return bioMetricExceptionEntity;
	}

	/**
	 * Convert photo graph data to photo graph entity.
	 *
	 * @param photoGraphData            the photo graph data
	 * @param exceptionPhotographData 
	 * @param metaData the meta data
	 * @return the applicant photograph entity
	 */
	public static ApplicantPhotographEntity convertPhotoGraphDtoToEntity(Photograph photoGraphData, Photograph exceptionPhotographData, List<FieldValue> metaData) {
		Optional<FieldValue> regId=metaData.stream().filter(m -> m.getLabel().equals(REGISTRATION_ID)).findFirst();
		String registrationId="";
		if(regId.isPresent())registrationId=regId.get().getValue();
		
		Optional<FieldValue> preregId=metaData.stream().filter(m -> m.getLabel().equals(PRE_REGISTRATION_ID)).findFirst();
		String preregistrationId="";
		if(preregId.isPresent())preregistrationId=preregId.get().getValue();
		
		ApplicantPhotographEntity applicantPhotographEntity = new ApplicantPhotographEntity();
		
		Boolean isHasExceptionPhoto=false;
		if(!(exceptionPhotographData.getPhotographName().isEmpty())) {
			isHasExceptionPhoto=true;
			applicantPhotographEntity.setExcpPhotoName(exceptionPhotographData.getPhotographName());
		}
		
		

		ApplicantPhotographPKEntity applicantPhotographPKEntity = new ApplicantPhotographPKEntity();
		applicantPhotographPKEntity.setRegId(registrationId);

		applicantPhotographEntity.setId(applicantPhotographPKEntity);
		applicantPhotographEntity.setPreRegId(preregistrationId);
		
		applicantPhotographEntity.setImageName(photoGraphData.getPhotographName());
		applicantPhotographEntity.setHasExcpPhotograph(isHasExceptionPhoto);
		applicantPhotographEntity.setQualityScore(BigDecimal.valueOf(photoGraphData.getQualityScore()));
		applicantPhotographEntity.setActive(true);

		return applicantPhotographEntity;
	}

	/**
	 * Convert osi data to osi entity.
	 *
	 * @param osiData            the osi data
	 * @param introducer the meta data
	 * @param metaData 
	 * @return the reg osi entity
	 */
	public static RegOsiEntity convertOsiDataToEntity(List<FieldValue> osiData, Introducer introducer, List<FieldValue> metaData) {
		RegOsiEntity regOsiEntity = new RegOsiEntity();
		
		RegOsiPkEntity regOsiPkEntity = new RegOsiPkEntity();
		
		for(FieldValue field: metaData) {
			if(field.getLabel().matches(REGISTRATION_ID)) {
				regOsiPkEntity.setRegId(field.getValue());
			}
			else if(field.getLabel().matches(PRE_REGISTRATION_ID)) {
				regOsiEntity.setPreregId(field.getValue());
			}
			else if(field.getLabel().matches("introducerRID")) {
				regOsiEntity.setIntroducerId(field.getValue());
			}
			else if(field.getLabel().matches("introducerUIN")) {
				regOsiEntity.setIntroducerRegId(field.getValue());
				regOsiEntity.setIntroducerUin(field.getValue());
			}
			else if(field.getLabel().matches("introducerType")) {
				regOsiEntity.setIntroducerTyp(field.getValue());
			}
			
		}
		
		for(FieldValue field: osiData) {
			if(field.getLabel().matches("officerFingerprintImage")) {
				regOsiEntity.setOfficerFingerpImageName(field.getValue());
			}
			else if(field.getLabel().matches("officerId")) {
				regOsiEntity.setOfficerId(field.getValue());
			}
			else if(field.getLabel().matches("officerIrisImage")) {
				regOsiEntity.setOfficerIrisImageName(field.getValue());
			}
			else if(field.getLabel().matches("supervisorFingerprintImage")) {
				regOsiEntity.setSupervisorFingerpImageName(field.getValue());
			}
			else if(field.getLabel().matches("supervisorIrisImage")) {
				regOsiEntity.setSupervisorIrisImageName(field.getValue());
			}
			else if(field.getLabel().matches("supervisiorId")) {
				regOsiEntity.setSupervisorId(field.getValue());
			}
		}

		regOsiEntity.setIntroducerFingerpImageName(introducer.getIntroducerFingerprint().getImageName());
		
		regOsiEntity.setIntroducerIrisImageName(introducer.getIntroducerIris().getImageName());
		
		regOsiEntity.setId(regOsiPkEntity);
		
		regOsiEntity.setIsActive(true);

		return regOsiEntity;
	}

	/**
	 * Convert demographic info to app demographic info entity.
	 *
	 * @param demographicInfo            the demographic info
	 * @param metaData the meta data
	 * @return the list
	 *//*
	public static List<ApplicantDemographicEntity> convertDemographicDtoToEntity(Demographic demographicInfo,
			MetaData metaData) {

		DemographicInfo demoInLocalLang = demographicInfo.getDemoInLocalLang();
		DemographicInfo demoInUserLang = demographicInfo.getDemoInUserLang();
		List<ApplicantDemographicEntity> applicantDemographicEntities = new ArrayList<>();

		ApplicantDemographicEntity applicantDemographicEntity = new ApplicantDemographicEntity();

		// adding Local Language Demographic data
		ApplicantDemographicPKEntity applicantDemographicPKEntity = new ApplicantDemographicPKEntity();
		applicantDemographicPKEntity.setLangCode(demoInLocalLang.getLanguageCode());
		applicantDemographicPKEntity.setRegId(metaData.getRegistrationId());

		applicantDemographicEntity.setId(applicantDemographicPKEntity);
		applicantDemographicEntity.setPreRegId(metaData.getPreRegistrationId());
		applicantDemographicEntity.setAddrLine1(demoInLocalLang.getAddressDTO().getLine1());
		applicantDemographicEntity.setAddrLine2(demoInLocalLang.getAddressDTO().getLine2());
		applicantDemographicEntity.setAddrLine3(demoInLocalLang.getAddressDTO().getLine3());
		int age = demoInLocalLang.getAge() != null ? Integer.parseInt(demoInLocalLang.getAge()) : 0;
		applicantDemographicEntity.setAge(age);
		applicantDemographicEntity.setApplicantType(metaData.getApplicationType());
		Long dobTime = demoInLocalLang.getDateOfBirth() != null ? Long.parseLong(demoInLocalLang.getDateOfBirth())
				: null;
		applicantDemographicEntity.setDob(dobTime != null ? new Date(dobTime) : null);
		applicantDemographicEntity.setEmail(demoInLocalLang.getEmailId());

		applicantDemographicEntity.setFirstName(demoInLocalLang.getFirstName());

		applicantDemographicEntity.setFullName(demoInLocalLang.getFullName());
		applicantDemographicEntity.setGenderCode(demoInLocalLang.getGender());

		applicantDemographicEntity.setLastName(demoInLocalLang.getLastName());
		applicantDemographicEntity.setMiddleName(demoInLocalLang.getMiddleName());
		applicantDemographicEntity.setMobile(demoInLocalLang.getMobile());

		applicantDemographicEntity.setIsActive(true);

		applicantDemographicEntity.setLocationCode("Location Code");
		applicantDemographicEntity.setNationalId("National Id");
		applicantDemographicEntity.setParentFullName("Parent Full Name");
		applicantDemographicEntity.setParentRefId("ParentRefId");
		applicantDemographicEntity.setParentRefIdType("ParentRefIdType");

		applicantDemographicEntities.add(applicantDemographicEntity);

		// adding User Language Demographic data

		applicantDemographicEntity = new ApplicantDemographicEntity();

		ApplicantDemographicPKEntity applicantDemographicPKEntity1 = new ApplicantDemographicPKEntity();
		applicantDemographicPKEntity1.setLangCode(demoInUserLang.getLanguageCode());
		applicantDemographicPKEntity1.setRegId(metaData.getRegistrationId());

		applicantDemographicEntity.setId(applicantDemographicPKEntity1);
		applicantDemographicEntity.setPreRegId(metaData.getPreRegistrationId());
		applicantDemographicEntity.setAddrLine1(demoInUserLang.getAddressDTO().getLine1());
		applicantDemographicEntity.setAddrLine2(demoInUserLang.getAddressDTO().getLine2());
		applicantDemographicEntity.setAddrLine3(demoInUserLang.getAddressDTO().getLine3());
		int userAge = demoInUserLang.getAge() != null ? Integer.parseInt(demoInUserLang.getAge()) : 0;
		applicantDemographicEntity.setAge(userAge);
		applicantDemographicEntity.setApplicantType(metaData.getApplicationType());
		Long dobUserTime = demoInLocalLang.getDateOfBirth() != null ? Long.parseLong(demoInUserLang.getDateOfBirth())
				: null;
		applicantDemographicEntity.setDob(dobTime != null ? new Date(dobUserTime) : null);
		applicantDemographicEntity.setEmail(demoInUserLang.getEmailId());
		applicantDemographicEntity.setFirstName(demoInUserLang.getFirstName());
		applicantDemographicEntity.setFullName(demoInUserLang.getFullName());
		applicantDemographicEntity.setGenderCode(demoInUserLang.getGender());
		applicantDemographicEntity.setLastName(demoInUserLang.getLastName());
		applicantDemographicEntity.setIsActive(true);
		applicantDemographicEntity.setMiddleName(demoInUserLang.getMiddleName());
		applicantDemographicEntity.setMobile(demoInUserLang.getMobile());
		applicantDemographicEntity.setLocationCode("Location Code");
		applicantDemographicEntity.setNationalId("National Id");
		applicantDemographicEntity.setParentFullName("Parent Full Name");
		applicantDemographicEntity.setParentRefId("ParentRefId");
		applicantDemographicEntity.setParentRefIdType("ParentRefIdType");

		applicantDemographicEntities.add(applicantDemographicEntity);

		return applicantDemographicEntities;
	}
*/
	public static RegCenterMachineEntity convertRegCenterMachineToEntity(List<FieldValue> metaData) {
		
		RegCenterMachinePKEntity regCenterMachinePKEntity = new RegCenterMachinePKEntity();
		RegCenterMachineEntity regCenterMachineEntity = new RegCenterMachineEntity();
		
		for(FieldValue field: metaData) {
			if(field.getLabel().matches(REGISTRATION_ID)) {
				regCenterMachinePKEntity.setRegId(field.getValue());
			}
			else if(field.getLabel().matches(PRE_REGISTRATION_ID)) {
				regCenterMachineEntity.setPreregId(field.getValue());
			}
			else if(field.getLabel().matches("geoLocLatitude")) {
				regCenterMachineEntity.setLatitude(field.getValue());
			}
			else if(field.getLabel().matches("geoLoclongitude")) {
				regCenterMachineEntity.setLongitude(field.getValue());
				
			}
				
		}
			
		regCenterMachineEntity.setCntrId("Center 1");
		regCenterMachineEntity.setMachineId("Machine 1");
		regCenterMachineEntity.setId(regCenterMachinePKEntity);
		regCenterMachineEntity.setIsActive(true);
		
		return regCenterMachineEntity;
	}

}
