package io.mosip.registration.processor.landingzone.scanner.job.stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@Service
public class LandingzoneScannerStage extends MosipVerticleManager {


	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(LandingzoneScannerStage.class);

	private static final String USER = "MOSIP_SYSTEM";

	private static final String LOGDISPLAY = "{} - {}";

	// @Value("${landingzone.scanner.stage.time.interval}")
	private long secs = 30;

	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	@Autowired
	protected RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	private static final String VIRUS_SCAN_NOT_ACCESSIBLE = "The Virus Scan Path set by the System is not accessible";
	private static final String ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE = "The Enrolment Status table is not accessible";

	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		mosipEventBus.getEventbus().setPeriodic(secs * 1000, msg -> {
			process(new MessageDTO());
			this.send(mosipEventBus, MessageBusAddress.LANDING_ZONE_BUS_OUT, new MessageDTO());
		}

		);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		try {

			List<InternalRegistrationStatusDto> getEnrols = this.registrationStatusService
					.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString());

			if (!(getEnrols.isEmpty())) {
				getEnrols.forEach(dto -> {
					String description = "";
					boolean isTransactionSuccessful = false;
					String registrationId = dto.getRegistrationId();
					try {

						this.filemanager.copy(dto.getRegistrationId(), DirectoryPathDto.LANDING_ZONE,
								DirectoryPathDto.VIRUS_SCAN);
						if (this.filemanager.checkIfFileExists(DirectoryPathDto.VIRUS_SCAN, dto.getRegistrationId())) {

							dto.setStatusCode(RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN.toString());
							dto.setStatusComment("Packet successfully uploaded to Landing Zone");
							dto.setUpdatedBy(USER);
							this.registrationStatusService.updateRegistrationStatus(dto);

							this.filemanager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN,
									dto.getRegistrationId());

							isTransactionSuccessful = true;
							description = registrationId + "moved successfully to virus scan.";
							regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),dto.getRegistrationId(),"moved successfully to virus scan.");
						}
					} catch (TablenotAccessibleException e) {
						regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),dto.getRegistrationId(),ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE+e.getMessage());
						description = "Registration status table not accessible for packet " + registrationId;
					} catch (IOException | FileNotFoundInDestinationException e) {
						regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),dto.getRegistrationId(),VIRUS_SCAN_NOT_ACCESSIBLE+e.getMessage());
						description = "Virus scan path set by the system is not accessible for packet "
								+ registrationId;
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

				});
			} else if (getEnrols.isEmpty()) {
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),"NOFILESTOBEMOVED","There are currently no files to be moved");
			}
		} catch (TablenotAccessibleException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE,e.getMessage());


		}
		return object;
	}

}
