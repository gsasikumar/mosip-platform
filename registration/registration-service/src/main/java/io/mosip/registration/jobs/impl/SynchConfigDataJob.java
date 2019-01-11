package io.mosip.registration.jobs.impl;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.service.config.GlobalParamService;

@Component("synchConfigDataJob")
public class SynchConfigDataJob extends BaseJob {
	@Autowired
	private GlobalParamService globalParamService;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(SynchConfigDataJob.class);

	@Override
	public ResponseDTO executeJob(String triggerPoint, String jobId) {
		LOGGER.debug(RegistrationConstants.SYNCH_CONFIG_DATA_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");
		SessionContext sessionContext = SessionContext.getInstance();
		String centerId = sessionContext.getUserContext().getRegistrationCenterDetailDTO().getRegistrationCenterId();

		this.responseDTO = globalParamService.synchConfigData(centerId);
		syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		LOGGER.debug(RegistrationConstants.SYNCH_CONFIG_DATA_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		return responseDTO;
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		LOGGER.debug(RegistrationConstants.SYNCH_CONFIG_DATA_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal started");
		this.responseDTO = new ResponseDTO();

		try {

			this.jobId = loadContext(context);
			// policySyncService = applicationContext.getBean(PolicySyncService.class);

			// Run the Parent JOB always first

			String centerId = SessionContext.getInstance().getUserContext().getRegistrationCenterDetailDTO()
					.getRegistrationCenterId();

			// Run the Parent JOB always first
			this.responseDTO = globalParamService.synchConfigData(centerId);

			// To run the child jobs after the parent job Success
			if (responseDTO.getSuccessResponseDTO() != null) {
				executeChildJob(jobId, jobMap);
			}

			syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error(RegistrationConstants.SYNCH_CONFIG_DATA_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, baseUncheckedException.getMessage());
			throw baseUncheckedException;
		}

		LOGGER.debug(RegistrationConstants.SYNCH_CONFIG_DATA_JOB_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal Ended");

	}

}
