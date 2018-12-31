package io.mosip.registration.processor.virus.scanner.job.stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.util.ZipUtils;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.virus.scanner.job.decrypter.Decryptor;
import io.mosip.registration.processor.virus.scanner.job.decrypter.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.virus.scanner.job.exceptions.VirusScanFailedException;
import io.mosip.registration.processor.virus.scanner.job.util.StatusMessage;
/**
 * The Class VirusScannerStage.
 */
@Service
public class VirusScannerStage extends MosipVerticleManager {

	/** The reg proc logger. */
	private static Logger regProcLogger = (Logger) RegProcessorLogger.getLogger(VirusScannerStage.class);


	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	@Autowired
	private Environment env;

	/** The extention. */
	@Value("${registration.processor.packet.ext}")
	private String extention;

	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The virus scanner service. */
	@Autowired
	VirusScanner<Boolean, String> virusScannerService;

	/** The file manager. */
	@Autowired
	FileManager<DirectoryPathDto, InputStream> fileManager;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The decryptor. */
    Decryptor decryptor;

	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	/** The Constant VIRUS_SCAN_FAILED. */
	private static final String VIRUS_SCAN_FAILED = "The Virus Scan for the Packet Failed";

	/** The description. */
	String description = "";

	/** The is transaction successful. */
	boolean isTransactionSuccessful = false;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.VIRUS_SCAN_BUS_IN, MessageBusAddress.PACKET_UPLOADER_IN);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {

		String registrationId = object.getRid();
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);
		String extension = env.getProperty("registration.processor.packet.ext");
		String encryptedPacketPath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_ENC.toString()) + File.separator
				+ registrationId + extension;
		File encryptedFile = new File(encryptedPacketPath);
		boolean isEncryptedFileCleaned;
		boolean isUnpackedFileCleaned;

		InputStream decryptedData = null;

		// To avoid sonar issue
		try (InputStream encryptedPacket = new FileInputStream(encryptedFile)) {

			isEncryptedFileCleaned = virusScannerService.scanFile(encryptedPacketPath);
			if (isEncryptedFileCleaned) {
				decryptedData = decryptor.decrypt(encryptedPacket, registrationId);

				fileManager.put(registrationId, decryptedData, DirectoryPathDto.VIRUS_SCAN_DEC);
				String decryptedPacketPath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString())
						+ File.separator + registrationId + extension;
				String unpackedPacketPath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_UNPACK.toString())
						+ File.separator + registrationId;

				ZipUtils.unZipDirectory(decryptedPacketPath, unpackedPacketPath);
				isUnpackedFileCleaned = virusScannerService.scanFolder(unpackedPacketPath);

				if (isUnpackedFileCleaned) {
					sendToPacketUploaderStage(registrationStatusDto);
				} else {
					fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_ENC, registrationId);
					fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_DEC, registrationId);
					// unpacked file doesn't contain extension
					fileManager.deleteFolder(DirectoryPathDto.VIRUS_SCAN_UNPACK, registrationId);
					processVirusScanFailure(registrationStatusDto);
				}

			} else {
				fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_ENC, registrationId);
				processVirusScanFailure(registrationStatusDto);

			}
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
		} catch (VirusScanFailedException | IOException | io.mosip.kernel.core.exception.IOException e) {
            regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), VIRUS_SCAN_FAILED + e.getMessage());
		} catch (PacketDecryptionFailureException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(), e.getErrorCode(), e.getErrorText(), e);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_DECRYPTION_FAILED.toString());
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_DECRYPTION_FAILURE);
			registrationStatusDto.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			isTransactionSuccessful = false;
			description = "Packet decryption failed for packet " + registrationId;
		} finally {

			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);

		}

		return object;
	}

	/**
	 * Process virus scan failure.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 */
	private void processVirusScanFailure(InternalRegistrationStatusDto registrationStatusDto) {
		String registrationId = registrationStatusDto.getRegistrationId();

		registrationStatusDto.setStatusCode(RegistrationStatusCode.VIRUS_SCAN_FAILED.toString());
		registrationStatusDto.setStatusComment(StatusMessage.PACKET_VIRUS_SCAN_FAILURE);
		registrationStatusDto.setUpdatedBy(USER);
		isTransactionSuccessful = false;
		description = registrationId + " packet is infected.";
		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), registrationStatusDto.getRegistrationId(), "File is infected.");

	}

	/**
	 * Send to packet uploader stage.
	 *
	 * @param entry
	 *            the entry
	 */
	private void sendToPacketUploaderStage(InternalRegistrationStatusDto entry) {
		String registrationId = entry.getRegistrationId();

		entry.setStatusCode(RegistrationStatusCode.VIRUS_SCAN_SUCCESSFUL.toString());
		entry.setStatusComment(StatusMessage.PACKET_VIRUS_SCAN_SUCCESS);
		entry.setUpdatedBy(USER);
		isTransactionSuccessful = true;
		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), entry.getRegistrationId(), "File is successfully scanned.");
		description = registrationId + " packet successfully  scanned for virus";

	}

}
