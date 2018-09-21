package org.mosip.registration.processor.service.packet.encryption;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;

import static java.io.File.separator;

import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.kernel.core.utils.file.FileUtil;
import org.mosip.registration.processor.exception.RegBaseCheckedException;
import org.mosip.registration.processor.exception.RegBaseUncheckedException;
import org.mosip.registration.processor.dao.RegistrationDAO;
import org.mosip.registration.processor.service.packet.encryption.aes.AESEncryptionManager;
import org.mosip.registration.processor.response.Response;
import org.mosip.registration.processor.dto.EnrollmentDTO;
import org.mosip.registration.processor.consts.RegConstants;
import org.mosip.registration.processor.consts.RegProcessorExceptionCode;
import org.mosip.registration.processor.util.store.StorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.mosip.registration.processor.util.reader.PropertyFileReader.getPropertyValue;
import static org.mosip.kernel.core.utils.datetime.DateUtil.formatDate;

@Component
public class PacketEncryptionManager {

	/**
	 * Class to encrypt the data using AES Algorithm
	 */
	@Autowired
	private AESEncryptionManager aesEncryptionManager;
	/**
	 * Class to insert the Registration Details into DB
	 */
	@Autowired
	private RegistrationDAO packetInsertion;
	
	private static MosipLogger LOGGER;
	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	/**
	 * Encrypts the input data using AES algorithm followed by RSA
	 * 
	 * @param packetZipData
	 *            the data to be encrypted
	 * @return encrypted data as byte array
	 * @throws RegBaseCheckedException
	 */
	public Response encrypt(final EnrollmentDTO enrollmentDTO, final byte[] packetZipData)
			throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - ENCRPTION", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Packet encryption had been started");
		try {
			// AES Encryption
			byte[] encryptedData = aesEncryptionManager.encrypt(packetZipData);
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - ENCRPTION", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Packet encrypted successfully");

			// Generate Zip File Name with absolute path
			String zipFileName = getPropertyValue(RegConstants.PACKET_STORE_LOCATION) + separator 
					+ formatDate(new Date(), getPropertyValue(RegConstants.PACKET_STORE_DATE_FORMAT)).concat(separator).concat(enrollmentDTO.getPacketDTO().getEnrollmentID());

			// Store the zip file to local disk
			StorageManager.storeToDisk(zipFileName, encryptedData);
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - ENCRPTION", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Encrypted packet saved");

			// Store the Acknowledgement Receipt in local disk
			FileUtil.copyToFile(new ByteArrayInputStream(enrollmentDTO.getPacketDTO().getDemographicDTO()
						.getApplicantDocumentDTO().getAcknowledgeReceipt()), new File(zipFileName + "_Ack.jpg"));
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - ENCRPTION", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Registration's Acknowledgement Receipt saved");

			// Insert the Registration Details into DB
			packetInsertion.save(zipFileName);
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - ENCRPTION", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Encrypted Packet persisted");

			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - ENCRPTION", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Packet encryption had been ended");
			// Return the Response Object
			Response response = new Response();
			response.setCode("0000");
			response.setMessage("Success");
			return response;
		} catch (MosipIOException e) {
			// TODO Auto-generated catch block
			throw new RegBaseCheckedException("", "");
		} catch (RegBaseUncheckedException uncheckedException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.PACKET_ENCRYPTION_MANAGER,
					uncheckedException.getMessage());
		}
	}
}
