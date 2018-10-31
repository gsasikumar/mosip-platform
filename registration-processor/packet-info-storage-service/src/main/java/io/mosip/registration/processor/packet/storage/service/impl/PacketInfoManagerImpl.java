package io.mosip.registration.processor.packet.storage.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.builder.CoreAuditRequestBuilder;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.packet.dto.BiometericData;
import io.mosip.registration.processor.core.packet.dto.DemographicInfo;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.DocumentDetail;
import io.mosip.registration.processor.core.packet.dto.ExceptionFingerprint;
import io.mosip.registration.processor.core.packet.dto.ExceptionIris;
import io.mosip.registration.processor.core.packet.dto.Fingerprint;
import io.mosip.registration.processor.core.packet.dto.FingerprintData;
import io.mosip.registration.processor.core.packet.dto.Iris;
import io.mosip.registration.processor.core.packet.dto.IrisData;
import io.mosip.registration.processor.core.packet.dto.MetaData;
import io.mosip.registration.processor.core.packet.dto.OsiData;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantFingerprintEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantIrisEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.BiometricExceptionEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.exception.TablenotAccessibleException;
import io.mosip.registration.processor.packet.storage.mapper.PacketInfoMapper;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

/**
 * The Class PacketInfoManagerImpl.
 *
 * @author Horteppa M1048399
 * @author Girish Yarru
 * 
 */
@Service
public class PacketInfoManagerImpl implements PacketInfoManager<PacketInfo, DemographicInfo, MetaData> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketInfoManagerImpl.class);

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The Constant DEMOGRAPHIC_APPLICANT. */
	public static final String DEMOGRAPHIC_APPLICANT = PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR
			+ PacketFiles.APPLICANT.name() + FILE_SEPARATOR;
	
	/** The applicant document repository. */
	@Autowired
	private BasePacketRepository<ApplicantDocumentEntity, String> applicantDocumentRepository;

	/** The biometric exception repository. */
	@Autowired
	private BasePacketRepository<BiometricExceptionEntity, String> biometricExceptionRepository;

	/** The applicant fingerprint repository. */
	@Autowired
	private BasePacketRepository<ApplicantFingerprintEntity, String> applicantFingerprintRepository;

	/** The applicant iris repository. */
	@Autowired
	private BasePacketRepository<ApplicantIrisEntity, String> applicantIrisRepository;

	/** The applicant photograph repository. */
	@Autowired
	private BasePacketRepository<ApplicantPhotographEntity, String> applicantPhotographRepository;

	/** The reg osi repository. */
	@Autowired
	private BasePacketRepository<RegOsiEntity, String> regOsiRepository;

	/** The applicant demographic repository. */
	@Autowired
	private BasePacketRepository<ApplicantDemographicEntity, String> applicantDemographicRepository;

	/** The reg center machine repository. */
	@Autowired
	private BasePacketRepository<RegCenterMachineEntity, String> regCenterMachineRepository;

	/** The event id. */
	private String eventId = "";
	
	/** The event name. */
	private String eventName = "";
	
	/** The event type. */
	private String eventType = "";
	
	/** The description. */
	String description = "";
	
	/** The core audit request builder. */
	@Autowired
	CoreAuditRequestBuilder coreAuditRequestBuilder;

	
	/** The file system adapter. */
	private FileSystemAdapter<InputStream, Boolean> fileSystemAdapter = new FilesystemCephAdapterImpl();

	/** The meta data. */
	private MetaData metaData;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager
	 * #savePacketData(java.lang.Object)
	 */
	@Override
	public void savePacketData(PacketInfo packetInfo) {

		boolean isTransactionSuccessful = false;

		BiometericData biometricData = packetInfo.getBiometericData();
		Document documentDto = packetInfo.getDocument();
		OsiData osiData = packetInfo.getOsiData();

		Photograph photoGraphData = packetInfo.getPhotograph();
		metaData = packetInfo.getMetaData();

		try {
			saveDocuments(documentDto);
			saveBioMetricData(biometricData);
			savePhotoGraph(photoGraphData);
			saveOsiData(osiData);
			saveRegCenterData(metaData);

			isTransactionSuccessful = true;
			//Event constants for audit log
			eventId = EventId.RPR_402.toString();
			eventName = EventName.UPDATE.toString();
			eventType = EventType.BUSINESS.toString();
			
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException("Table Not Accessible", e);
		} finally {
			description = isTransactionSuccessful ? "packet-meta-data saved Success"
					: "packet-metadata Failure";
			
			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());

					}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager
	 * #saveDemographicData(java.lang.Object)
	 */
	@Override
	public void saveDemographicData(DemographicInfo demographicInfo, MetaData metaData) {

		boolean isTransactionSuccessful = false;
		try {
			List<ApplicantDemographicEntity> applicantDemographicEntities = PacketInfoMapper
					.convertDemographicDtoToEntity(demographicInfo, metaData);
			for (ApplicantDemographicEntity applicantDemographicEntity : applicantDemographicEntities) {
				applicantDemographicRepository.save(applicantDemographicEntity);
				LOGGER.info(applicantDemographicEntity.getId().getRegId() + " --> Demographic  DATA SAVED");
			}
			//Event constants for audit log
			eventId = EventId.RPR_407.toString();
			eventName = EventName.SAVE.toString();
			eventType = EventType.BUSINESS.toString();
			isTransactionSuccessful = true;
		} catch (DataAccessLayerException e) {
			eventId = EventId.RPR_405.toString();
			eventName = EventName.EXCEPTION.toString();
			eventType = EventType.BUSINESS.toString();
			throw new TablenotAccessibleException("Table Not Accessible", e);
		} finally {
			description = isTransactionSuccessful ? "Demographic-data saved Success"
					: "Demographic Failed to save";
			
			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());

		}

	}

	/**
	 * Save bio metric data.
	 *
	 * @param bioMetricData
	 *            the bio metric data
	 */
	private void saveBioMetricData(BiometericData bioMetricData) {
		saveFingerPrint(bioMetricData.getFingerprintData());
		saveIris(bioMetricData.getIrisData());
	}

	/**
	 * Save iris.
	 *
	 * @param irisData
	 *            the iris data
	 */
	private void saveIris(IrisData irisData) {
		List<Iris> irisList = irisData.getIris();
		List<ExceptionIris> exceptionIrisList = irisData.getExceptionIris();

		irisList.forEach(iris -> {
			ApplicantIrisEntity applicantIrisEntity = PacketInfoMapper.convertIrisDtoToEntity(iris, metaData);
			applicantIrisRepository.save(applicantIrisEntity);
			LOGGER.info(applicantIrisEntity.getId().getRegId() + " --> Applicant Iris DATA SAVED");
		});

		exceptionIrisList.forEach(exceptionIris -> {
			BiometricExceptionEntity biometricIrisExceptionEntity = PacketInfoMapper
					.convertBiometricExcDtoToEntity(exceptionIris, metaData);
			biometricExceptionRepository.save(biometricIrisExceptionEntity);
			LOGGER.info(biometricIrisExceptionEntity.getId().getRegId() + " --> Applicant Iris DATA SAVED");
		});
	}

	/**
	 * Save finger print.
	 *
	 * @param fingerprintData
	 *            the fingerprint data
	 */
	private void saveFingerPrint(FingerprintData fingerprintData) {
		List<Fingerprint> fingerprints = fingerprintData.getFingerprints();
		List<ExceptionFingerprint> exceptionFingerprints = fingerprintData.getExceptionFingerprints();

		fingerprints.forEach(fingerprint -> {
			ApplicantFingerprintEntity fingerprintEntity = PacketInfoMapper.convertFingerprintDtoToEntity(fingerprint,
					metaData);
			applicantFingerprintRepository.save(fingerprintEntity);
			LOGGER.info(fingerprintEntity.getId().getRegId() + " --> Fingerprint DATA SAVED");

		});

		exceptionFingerprints.forEach(exceptionFingerprint -> {
			BiometricExceptionEntity biometricExceptionEntity = PacketInfoMapper
					.convertBiometricExceptioDtoToEntity(exceptionFingerprint, metaData);
			biometricExceptionRepository.save(biometricExceptionEntity);
			LOGGER.info(biometricExceptionEntity.getId().getRegId() + " --> Biometric Exception DATA SAVED");
		});
	}

	/**
	 * Save documents.
	 *
	 * @param documentDto
	 *            the document dto
	 */
	private void saveDocuments(Document documentDto) {

		List<DocumentDetail> documentDetails = documentDto.getDocumentDetails();
		for (DocumentDetail documentDetail : documentDetails) {
			saveDocument(documentDetail);
		}
	}

	/**
	 * Save document data.
	 *
	 * @param documentDetail
	 *            the document detail
	 */
	public void saveDocument(DocumentDetail documentDetail) {
		ApplicantDocumentEntity applicantDocumentEntity = PacketInfoMapper.convertAppDocDtoToEntity(documentDetail,
				metaData);

		String fileName = "";
		if (PacketFiles.APPLICANTPHOTO.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.APPLICANTPHOTO.name();
		} else if (PacketFiles.REGISTRATIONACKNOWLEDGEMENT.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.REGISTRATIONACKNOWLEDGEMENT.name();
		} else if (PacketFiles.DEMOGRAPHICINFO.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name();
		} else if (PacketFiles.PROOFOFADDRESS.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.PROOFOFADDRESS.name();
		} else if (PacketFiles.EXCEPTIONPHOTO.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.EXCEPTIONPHOTO.name();
		} else if (PacketFiles.PROOFOFIDENTITY.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.PROOFOFIDENTITY.name();
		}

		applicantDocumentEntity.setDocStore(getDocumentAsByteArray(metaData.getRegistrationId(), fileName));
		applicantDocumentRepository.save(applicantDocumentEntity);
		LOGGER.info(applicantDocumentEntity.getId().getRegId() + " --> Document Demographic DATA SAVED");
	}

	/**
	 * Save osi data.
	 *
	 * @param osiData
	 *            the osi data
	 */
	private void saveOsiData(OsiData osiData) {
		RegOsiEntity regOsiEntity = PacketInfoMapper.convertOsiDataToEntity(osiData, metaData);
		regOsiRepository.save(regOsiEntity);
		LOGGER.info(regOsiEntity.getId() + " --> Applicant OSI DATA SAVED");
	}

	/**
	 * Save photo graph.
	 *
	 * @param photoGraphData
	 *            the photo graph data
	 */
	private void savePhotoGraph(Photograph photoGraphData) {
		ApplicantPhotographEntity applicantPhotographEntity = PacketInfoMapper
				.convertPhotoGraphDtoToEntity(photoGraphData, metaData);
		applicantPhotographRepository.save(applicantPhotographEntity);
		LOGGER.info(applicantPhotographEntity.getId().getRegId() + " --> Applicant Photograph DATA SAVED");
	}

	/**
	 * Save reg center data.
	 *
	 * @param metaData
	 *            the meta data
	 */
	private void saveRegCenterData(MetaData metaData) {
		RegCenterMachineEntity regCenterMachineEntity = PacketInfoMapper.convertRegCenterMachineToEntity(metaData);
		regCenterMachineRepository.save(regCenterMachineEntity);
		LOGGER.info(regCenterMachineEntity.getId() + " --> Registration Center Machine DATA SAVED");

	}

	/**
	 * Gets the document as byte array.
	 *
	 * @param registrationId
	 *            the registration id
	 * @param documentName
	 *            the document name
	 * @return the document as byte array
	 */
	private byte[] getDocumentAsByteArray(String registrationId, String documentName) {

		InputStream in = fileSystemAdapter.getFile(registrationId, documentName);
		byte[] buffer = new byte[1024];
		int len;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			while ((len = in.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
		} catch (IOException e) {
			LOGGER.error("Error While reading  inputstream file", e);
		}
		return os.toByteArray();
	}

}
