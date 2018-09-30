package org.mosip.registration.util.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.io.File.separator;
import static org.mosip.registration.constants.RegConstants.IMAGE_TYPE;
import static org.mosip.registration.constants.RegConstants.JSON_FILE_EXTENSION;
import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_IO_EXCEPTION;

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.constants.RegProcessorExceptionCode;
import org.mosip.registration.dto.RegistrationDTO;
import org.mosip.registration.dto.biometric.BiometricInfoDTO;
import org.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import org.mosip.registration.dto.biometric.IrisDetailsDTO;
import org.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import org.mosip.registration.dto.demographic.DocumentDetailsDTO;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME; 
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

/**
 * API Class to generate the Enrollment Registration Structure for zip file
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class ZipCreationManager {

	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Returns the byte array of the packet zip file containing the Registration
	 * Details
	 * 
	 * @param registrationDTO
	 *            the Registration to be stored in zip file
	 * @return the byte array of packet zip file
	 * @throws RegBaseCheckedException
	 */
	public static byte[] createPacket(final RegistrationDTO registrationDTO, final Map<String, byte[]> jsonMap)
			throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Packet Zip had been called");

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
			// Create folder structure for Biometric
			if (checkNotNull(registrationDTO)) {
				registrationDTO.getBiometricDTO();
				if (checkNotNull(registrationDTO.getBiometricDTO())) {
					String folderName;
					// Biometric -> Applicant Folder
					if (checkNotNull(registrationDTO.getBiometricDTO().getApplicantBiometricDTO())) {
						folderName = "Biometric".concat(separator).concat("Applicant").concat(separator);
						addBiometricImages(registrationDTO.getBiometricDTO().getApplicantBiometricDTO(), folderName,
								zipOutputStream);
						LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", getPropertyValue(APPLICATION_NAME),
								getPropertyValue(APPLICATION_ID), "Applicant's biometric added");
					}

					// Add HOF Biometrics to packet zip
					if (checkNotNull(registrationDTO.getBiometricDTO().getHofBiometricDTO())) {
						folderName = "Biometric".concat(separator).concat("HOF").concat(separator);
						addBiometricImages(registrationDTO.getBiometricDTO().getHofBiometricDTO(), folderName,
								zipOutputStream);
						LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", getPropertyValue(APPLICATION_NAME),
								getPropertyValue(APPLICATION_ID), "HOF's biometric added");
					}

					// Add Introducer Biometrics to packet zip
					if (checkNotNull(registrationDTO.getBiometricDTO().getIntroducerBiometricDTO())) {
						folderName = "Biometric".concat(separator).concat("Introducer").concat(separator);
						addBiometricImages(registrationDTO.getBiometricDTO().getIntroducerBiometricDTO(), folderName,
								zipOutputStream);
						LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", getPropertyValue(APPLICATION_NAME),
								getPropertyValue(APPLICATION_ID), "Introcucer's biometric added");
					}
				}

				// Create folder structure for Demographic
				if (checkNotNull(registrationDTO.getDemographicDTO())) {
					if (checkNotNull(registrationDTO.getDemographicDTO().getApplicantDocumentDTO())) {
						String folderName = "Demographic".concat(separator).concat("Applicant").concat(separator);
						addDemogrpahicData(registrationDTO.getDemographicDTO().getApplicantDocumentDTO(), folderName,
								zipOutputStream);
						LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", getPropertyValue(APPLICATION_NAME),
								getPropertyValue(APPLICATION_ID), "Applicant's demographic added");
					}
					writeFileToZip(
							"Demographic".concat(separator).concat("DemographicInfo").concat(JSON_FILE_EXTENSION),
							jsonMap.get(RegConstants.DEMOGRPAHIC_JSON_NAME), zipOutputStream);
					LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", getPropertyValue(APPLICATION_NAME),
							getPropertyValue(APPLICATION_ID), "Demographic JSON added");
				}

				// Add the Enrollment ID
				writeFileToZip("RegistrationId.txt", registrationDTO.getRegistrationId().getBytes(), zipOutputStream);
				LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", getPropertyValue(APPLICATION_NAME),
						getPropertyValue(APPLICATION_ID), "Registration Id added");

				// Add the HMAC Info
				writeFileToZip("HMACFile.txt", jsonMap.get(RegConstants.HASHING_JSON_NAME), zipOutputStream);
				LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", getPropertyValue(APPLICATION_NAME),
						getPropertyValue(APPLICATION_ID), "HMAC added");

				if (checkNotNull(registrationDTO.getBiometricDTO())) {
					if (checkNotNull(registrationDTO.getBiometricDTO().getSupervisorBiometricDTO())) {
						addOfficerBiometric("EnrollmentSupervisorBioImage",
								registrationDTO.getBiometricDTO().getSupervisorBiometricDTO(), zipOutputStream);
					}

					if (checkNotNull(registrationDTO.getBiometricDTO().getOperatorBiometricDTO())) {
						addOfficerBiometric("EnrollmentOfficerBioImage",
								registrationDTO.getBiometricDTO().getOperatorBiometricDTO(), zipOutputStream);
					}
					LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", getPropertyValue(APPLICATION_NAME),
							getPropertyValue(APPLICATION_ID), "Supervisor's Biometric added");
				}

				// Add Registration Meta JSON
				writeFileToZip("PacketMetaInfo".concat(JSON_FILE_EXTENSION),
						jsonMap.get(RegConstants.PACKET_META_JSON_NAME), zipOutputStream);
				LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", getPropertyValue(APPLICATION_NAME),
						getPropertyValue(APPLICATION_ID), "Registration Packet Meta added");

				// Add Audits
				writeFileToZip(RegConstants.AUDIT_JSON_FILE.concat(JSON_FILE_EXTENSION),
						jsonMap.get(RegConstants.AUDIT_JSON_FILE), zipOutputStream);
				LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", getPropertyValue(APPLICATION_NAME),
						getPropertyValue(APPLICATION_ID), "Registration Audit Logs Meta added");

				zipOutputStream.flush();
				byteArrayOutputStream.flush();
				zipOutputStream.close();
				byteArrayOutputStream.close();
			}

			LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Packet zip had been ended");
			return byteArrayOutputStream.toByteArray();
		} catch (IOException exception) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), exception.getCause().getMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.PACKET_ZIP_CREATION,
					runtimeException.toString());
		}
	}

	private static void addOfficerBiometric(final String fileName, final BiometricInfoDTO supervisorBio,
			final ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		List<FingerprintDetailsDTO> fingerprintDetailsDTOs = supervisorBio.getFingerprintDetailsDTO();

		if (!fingerprintDetailsDTOs.isEmpty()) {
				writeFileToZip(fileName + IMAGE_TYPE, fingerprintDetailsDTOs.get(0).getFingerPrint(), zipOutputStream);
		}
	}

	private static void addDemogrpahicData(final ApplicantDocumentDTO applicantDocumentDTO, final String folderName,
			final ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		// Add Proofs
		if (checkNotNull(applicantDocumentDTO.getDocumentDetailsDTO())) {
			for (DocumentDetailsDTO documentDetailsDTO : applicantDocumentDTO.getDocumentDetailsDTO()) {
				writeFileToZip(
						folderName + RegConstants.DOCUMENT_TYPES_MAP
								.get(documentDetailsDTO.getDocumentCategory().toLowerCase()) + RegConstants.DOC_TYPE,
						documentDetailsDTO.getDocument(), zipOutputStream);
			}
		}

		addToZip(applicantDocumentDTO.getPhoto(), folderName + "ApplicantPhoto" + IMAGE_TYPE, zipOutputStream);
		addToZip(applicantDocumentDTO.getExceptionPhoto(), folderName + "ExceptionPhoto" + IMAGE_TYPE, zipOutputStream);
		addToZip(applicantDocumentDTO.getAcknowledgeReceipt(), folderName + "AcknowledgementReceipt." + RegConstants.IMAGE_FORMAT,
				zipOutputStream);
	}

	private static void addToZip(final byte[] content, final String fileNameWithPath,
			final ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		if (checkNotNull(content)) {
			writeFileToZip(fileNameWithPath, content, zipOutputStream);
		}
	}

	private static void addBiometricImages(final BiometricInfoDTO biometricDTO, String folderName,
			ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		// Biometric -> Applicant - Files
		// Add the Fingerprint images to zip folder structure
		if (checkNotNull(biometricDTO.getFingerprintDetailsDTO())) {
			for (FingerprintDetailsDTO fingerprintDetailsDTO : biometricDTO.getFingerprintDetailsDTO()) {
				writeFileToZip(
						folderName + RegConstants.FINGERPRINT_IMAGE_NAMES_MAP
								.get(fingerprintDetailsDTO.getFingerType().toLowerCase()) + IMAGE_TYPE,
						fingerprintDetailsDTO.getFingerPrint(), zipOutputStream);
			}
		}

		// Add Iris Images to zip folder structure
		if (checkNotNull(biometricDTO.getIrisDetailsDTO())) {
			for (IrisDetailsDTO irisDetailsDTO : biometricDTO.getIrisDetailsDTO()) {
				writeFileToZip(
						folderName + RegConstants.IRIS_IMAGE_NAMES_MAP.get(irisDetailsDTO.getIrisType().toLowerCase())
								+ IMAGE_TYPE,
						irisDetailsDTO.getIris(), zipOutputStream);
			}
		}
	}

	private static boolean checkNotNull(Object object) {
		return object != null;
	}

	private static void writeFileToZip(String fileName, byte[] file, ZipOutputStream zipOutputStream)
			throws RegBaseCheckedException {
		try {
			// TODO : To be replaced with core kernal util class.
			final ZipEntry zipEntry = new ZipEntry(fileName);
			zipOutputStream.putNextEntry(zipEntry);
			zipOutputStream.write(file);
			zipOutputStream.flush();
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), REG_IO_EXCEPTION.getErrorMessage());
		}
	}

}
