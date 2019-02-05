package io.mosip.registration.jobs.impl;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.service.packet.RegPacketStatusService;
import io.mosip.registration.service.sync.PacketSynchService;

/**
 * This is a job to sync the packet status
 * 
 * @author SARAVANAKUMAR G
 * @since 1.0.0
 *
 */
@Component(value = "registrationPacketSyncJob")
public class RegistrationPacketSyncJob extends BaseJob {

	/**
	 * The RegPacketStatusServiceImpl
	 */
	@Autowired
	private RegPacketStatusService packetStatusService;
	
	@Autowired
	private PacketSynchService packetSynchService;
	
	@Autowired
	private RegPacketStatusService regPacketStatusService;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationPacketSyncJob.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.
	 * quartz.JobExecutionContext)
	 */
	@Async
	@Override
	public void executeInternal(JobExecutionContext context) {
		LOGGER.debug(LoggerConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal started");
		this.responseDTO = new ResponseDTO();

		try {
			
			this.jobId = loadContext(context);
			packetSynchService = applicationContext.getBean(PacketSynchService.class);

			// Run the Parent JOB always first
			this.responseDTO = regPacketStatusService.syncPacket();

			// To run the child jobs after the parent job Success
			if (responseDTO.getSuccessResponseDTO() != null && context != null) {
				executeChildJob(jobId, jobMap);
			}

			syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error(LoggerConstants.PRE_REG_DATA_SYNC_JOB_LOGGER_TITLE,
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					baseUncheckedException.getMessage());
			throw baseUncheckedException;
		}

		LOGGER.debug(LoggerConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal Ended");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.jobs.BaseJob#executeJob(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ResponseDTO executeJob(String triggerPoint, String jobId) {

		LOGGER.debug(LoggerConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");

		this.responseDTO = packetStatusService.packetSyncStatus();
		syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		LOGGER.debug(LoggerConstants.PACKET_SYNC_STATUS_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		return responseDTO;

	}

}
