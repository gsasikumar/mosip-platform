package io.mosip.registration.processor.packet.service.external.impl;

import static io.mosip.registration.processor.packet.service.exception.RegistrationExceptionConstants.REG_IO_EXCEPTION;
import static java.io.File.separator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.service.constants.RegistrationConstants;
import io.mosip.registration.processor.packet.service.dto.RegistrationDTO;
import io.mosip.registration.processor.packet.service.dto.demographic.DemographicDTO;
import io.mosip.registration.processor.packet.service.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.processor.packet.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.packet.service.exception.RegBaseUncheckedException;
import io.mosip.registration.processor.packet.service.external.ZipCreationService;
import io.mosip.registration.processor.packet.service.impl.PacketCreationServiceImpl;

/**
 * API Class to generate the in-memory zip file for Registration Packet.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Service
public class ZipCreationServiceImpl implements ZipCreationService {

	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketCreationServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.ZipCreationService#createPacket(io.mosip.
	 * registration.dto.RegistrationDTO, java.util.Map)
	 */
	@Override
	public byte[] createPacket(final RegistrationDTO registrationDTO, final Map<String, byte[]> filesGeneratedForPacket)
			throws RegBaseCheckedException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationDTO.getRegistrationId(), "ZipCreationServiceImpl ::createPacket()::entry");

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

			// Create folder structure for Demographic
			if (checkNotNull(registrationDTO.getDemographicDTO())) {

				writeFileToZip("Demographic".concat(separator).concat(RegistrationConstants.DEMOGRPAHIC_JSON_NAME),
						filesGeneratedForPacket.get(RegistrationConstants.DEMOGRPAHIC_JSON_NAME), zipOutputStream);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationDTO.getRegistrationId(),
						"Demographic JSON added");

			}

			// Add the HMAC Info
			writeFileToZip(RegistrationConstants.PACKET_DATA_HASH_FILE_NAME,
					filesGeneratedForPacket.get(RegistrationConstants.PACKET_DATA_HASH_FILE_NAME), zipOutputStream);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationDTO.getRegistrationId(), "HMAC added");

			// Add Registration Meta JSON
			writeFileToZip(RegistrationConstants.PACKET_META_JSON_NAME,
					filesGeneratedForPacket.get(RegistrationConstants.PACKET_META_JSON_NAME), zipOutputStream);

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationDTO.getRegistrationId(), "Registration Packet Meta added");

			// Add Audits
			writeFileToZip(RegistrationConstants.AUDIT_JSON_FILE.concat(RegistrationConstants.JSON_FILE_EXTENSION),
					filesGeneratedForPacket.get(RegistrationConstants.AUDIT_JSON_FILE), zipOutputStream);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationDTO.getRegistrationId(), "Audit Logs Meta added");

			// Add Packet_OSI_HASH
			writeFileToZip(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME,
					filesGeneratedForPacket.get(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME), zipOutputStream);

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationDTO.getRegistrationId(), "Registration packet_osi_hash added");

			zipOutputStream.flush();
			byteArrayOutputStream.flush();
			zipOutputStream.close();
			byteArrayOutputStream.close();

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationDTO.getRegistrationId(), "ZipCreationServiceImpl ::createPacket()::end()");

			return byteArrayOutputStream.toByteArray();
		} catch (IOException exception) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationDTO.getRegistrationId(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage() + ExceptionUtils.getStackTrace(exception));
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
		} catch (RuntimeException runtimeException) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationDTO.getRegistrationId(), PlatformErrorMessages.RPR_SYS_SERVER_ERROR.getMessage()
							+ ExceptionUtils.getStackTrace(runtimeException));
			throw new RegBaseUncheckedException(PlatformErrorMessages.RPR_SYS_SERVER_ERROR.getCode(),
					PlatformErrorMessages.RPR_SYS_SERVER_ERROR.getMessage());
		}
	}

	/**
	 * Adds the demogrpahic data.
	 *
	 * @param demographicDTO
	 *            the applicant document DTO
	 * @param folderName
	 *            the folder name
	 * @param zipOutputStream
	 *            the zip output stream
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private static void addDemogrpahicData(final DemographicDTO demographicDTO, final String folderName,
			final ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		// Add Proofs
		Map<String, DocumentDetailsDTO> documents = demographicDTO.getApplicantDocumentDTO().getDocuments();

		for (Entry<String, DocumentDetailsDTO> documentCategory : documents.entrySet()) {
			writeFileToZip(folderName + getFileNameWithExt(documentCategory.getValue()),
					documentCategory.getValue().getDocument(), zipOutputStream);
		}

		addToZip(demographicDTO.getApplicantDocumentDTO().getPhoto(),
				folderName + demographicDTO.getApplicantDocumentDTO().getPhotographName(), zipOutputStream);
		addToZip(demographicDTO.getApplicantDocumentDTO().getExceptionPhoto(),
				folderName + demographicDTO.getApplicantDocumentDTO().getExceptionPhotoName(), zipOutputStream);
		if (demographicDTO.getApplicantDocumentDTO().getAcknowledgeReceipt() != null) {
			addToZip(demographicDTO.getApplicantDocumentDTO().getAcknowledgeReceipt(),
					folderName + demographicDTO.getApplicantDocumentDTO().getAcknowledgeReceiptName(), zipOutputStream);
		}
	}

	/**
	 * Adds the to zip.
	 *
	 * @param content
	 *            the content
	 * @param fileNameWithPath
	 *            the file name with path
	 * @param zipOutputStream
	 *            the zip output stream
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private static void addToZip(final byte[] content, final String fileNameWithPath,
			final ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		if (checkNotNull(content)) {
			writeFileToZip(fileNameWithPath, content, zipOutputStream);
		}
	}

	/**
	 * Check not null.
	 *
	 * @param object
	 *            the object
	 * @return true, if successful
	 */
	private static boolean checkNotNull(Object object) {
		return object != null;
	}

	/**
	 * Write file to zip.
	 *
	 * @param fileName
	 *            the file name
	 * @param file
	 *            the file
	 * @param zipOutputStream
	 *            the zip output stream
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private static void writeFileToZip(String fileName, byte[] file, ZipOutputStream zipOutputStream)
			throws RegBaseCheckedException {
		try {
			final ZipEntry zipEntry = new ZipEntry(fileName);
			zipOutputStream.putNextEntry(zipEntry);
			zipOutputStream.write(file);
			zipOutputStream.flush();
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), REG_IO_EXCEPTION.getErrorMessage());
		}
	}

	private static String getFileNameWithExt(DocumentDetailsDTO documentDetailsDTO) {
		return documentDetailsDTO.getValue().concat(".").concat(documentDetailsDTO.getFormat());
	}

}
