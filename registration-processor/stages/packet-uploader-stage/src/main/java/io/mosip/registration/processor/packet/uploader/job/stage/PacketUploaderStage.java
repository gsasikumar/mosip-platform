package io.mosip.registration.processor.packet.uploader.job.stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.archiver.util.exception.PacketNotFoundException;
import io.mosip.registration.processor.packet.archiver.util.exception.UnableToAccessPathException;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FilePathNotAccessibleException;
import io.mosip.registration.processor.packet.uploader.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.uploader.job.exception.DFSNotAccessibleException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;


/**
 * The Class PacketUploaderStage.
 * @author M1049387
 */
@Component
public class PacketUploaderStage extends MosipVerticleManager {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketUploaderStage.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {} - {}";

	/** The cluster address. */
	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	/** The secs. */
	// @Value("${landingzone.scanner.stage.time.interval}")
	private long secs = 30;

	/** The mosip event bus. */
	MosipEventBus mosipEventBus = null;

	/** The localhost. */
	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The adapter. */
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	
	/** The audit log request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The packet archiver. */
	@Autowired
	private PacketArchiver packetArchiver;

	private static final String UNABLE_TO_DELETE = "unable to delete after sending to DFS.";

	/** The env. */
	@Autowired
	private Environment env;
	
	/** The Constant DFS_NOT_ACCESSIBLE. */
	private static final String DFS_NOT_ACCESSIBLE = "The DFS Path set by the System is not accessible";

	/** The Constant REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE. */
	private static final String REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = "The Registration Status table "
			+ "is not accessible";

	/** The description. */
	private String description = "";
	
	/** The is transaction successful. */
	private boolean isTransactionSuccessful = false;
	
	/** The registration id. */
	private String registrationId = "";
	
	@Autowired
	FileManager<DirectoryPathDto, InputStream> fileManager;

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		try {
					this.registrationId = object.getRid();
					InternalRegistrationStatusDto dto=registrationStatusService.getRegistrationStatus(registrationId);
					try {
						uploadpacket(dto);

					} catch (TablenotAccessibleException e) {

						LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e.getMessage(), e);

						this.isTransactionSuccessful = false;
						this.description = "Registration status table is not accessible for packet "
								+ this.registrationId;
					} catch (IOException e) {

						LOGGER.error(LOGDISPLAY, DFS_NOT_ACCESSIBLE, e.getMessage(), e);
						this.isTransactionSuccessful = false;
						this.description = "File System is not accessible for packet " + this.registrationId;
					} finally {

						String eventId = "";
						String eventName = "";
						String eventType = "";
						eventId = this.isTransactionSuccessful ? EventId.RPR_402.toString()
								: EventId.RPR_405.toString();
						eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
								: EventName.EXCEPTION.toString();
						eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
								: EventType.SYSTEM.toString();

						auditLogRequestBuilder.createAuditRequestBuilder(this.description, eventId, eventName,
								eventType, this.registrationId);

					}
				
			
		} catch (TablenotAccessibleException e) {

			LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);
		}

		return null;
	}

	
	/**
	 * Uploadpacket.
	 *
	 * @param dto the dto
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void uploadpacket(InternalRegistrationStatusDto dto) throws IOException{
		try {
			packetArchiver.archivePacket(dto.getRegistrationId());
		} catch (UnableToAccessPathException e) {
			LOGGER.error(LOGDISPLAY, e.getErrorCode(), e.getMessage(), e.getCause());
		} catch (PacketNotFoundException ex) {
			LOGGER.error(LOGDISPLAY, ex.getErrorCode(), ex.getMessage(), ex.getCause());
		}
		String filepath = env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString()) + File.separator	+ dto.getRegistrationId()+".zip";
		File file = new File(filepath);	
		InputStream decryptedData = new FileInputStream(file);
		sendToDFS(dto,decryptedData);
		}

	
	
	
	/**
	 * Send to DFS.
	 *
	 * @param file
	 *            the file
	 * @param entry
	 *            the entry
	 */
	private void sendToDFS(InternalRegistrationStatusDto entry,InputStream decryptedData) {
		
		registrationId=entry.getRegistrationId();
		try {
			
			adapter.storePacket(registrationId, decryptedData);
			adapter.unpackPacket(registrationId);

			if (adapter.isPacketPresent(registrationId)) {
				fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_DEC,registrationId);
				fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_ENC, registrationId);
				fileManager.deleteFolder(DirectoryPathDto.VIRUS_SCAN_UNPACK,registrationId);

				LOGGER.info(LOGDISPLAY, registrationId, "File is Already exists in DFS location " + " And its now Deleted from Virus scanner job ");
			} else {
				LOGGER.info(LOGDISPLAY, registrationId,
						"File has been sent to DFS.");
			}
			entry.setStatusCode(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM.toString());
			entry.setStatusComment("Packet is uploaded in file system.");
			entry.setUpdatedBy(USER);
			
			registrationStatusService.updateRegistrationStatus(entry);
			isTransactionSuccessful = true;
			description =  registrationId + " packet successfully has been send to DFS";
		} catch (DFSNotAccessibleException e) {
			LOGGER.error(LOGDISPLAY, DFS_NOT_ACCESSIBLE, e);
			description =  "FileSytem is not accessible for packet " +registrationId ;
		} catch (IOException e) {
			LOGGER.error(LOGDISPLAY, entry.getRegistrationId() + UNABLE_TO_DELETE, e);
			description =  "Virus scan path is not accessible for packet " +registrationId ;
		}
		catch (TablenotAccessibleException e) {
			LOGGER.error(LOGDISPLAY, REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);
			description =  "The Registration Status table is not accessible for packet " +registrationId ;
		}finally {

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

	}

	
	
	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		this.consume(mosipEventBus, MessageBusAddress.PACKET_UPLOADER_IN);
	
	}

	/**
	 * Send message.
	 *
	 * @param mosipEventBus the mosip event bus
	 * @param message the message
	 */
	private void sendMessage(MosipEventBus mosipEventBus, MessageDTO message) {
		this.send(mosipEventBus, MessageBusAddress.BATCH_BUS, message);
	}

}
