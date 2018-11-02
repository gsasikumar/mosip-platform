package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.SyncJobDAO;
import io.mosip.registration.dao.SyncJobDAO.SyncJobInfo;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.GeoLocationCapture;
import io.mosip.registration.service.SyncStatusValidatorService;

/**
 * {@code SyncStatusValidatorServiceImpl} is the sync status validate service
 * class.
 *
 * @author Chukka Sreekar
 * @author Mahesh Kumar
 */
@Service

public class SyncStatusValidatorServiceImpl implements SyncStatusValidatorService {

	@Value("${MDS_J00001}")
	private int mdsJobId;
	@Value("${LCS_J00002}")
	private int lcsJobId;
	@Value("${PDS_J00003}")
	private int pdsJobId;
	@Value("${RSS_J00004}")
	private int rssJobId;
	@Value("${RCS_J00005}")
	private int rcsJobId;
	@Value("${RPS_J00006}")
	private int rpsJobId;
	@Value("${URS_J00007}")
	private int ursJobId;
	@Value("${POS_J00008}")
	private int posJobId;
	@Value("${LER_J00009}")
	private int lerJobId;
	@Value("${GEO_CAP_FREQ}")
	private String geoFrequnecyFlag;
	@Value("${REG_PAK_MAX_CNT_OFFLINE_FREQ}")
	private double packetMaxCount;
	@Value("${DIST_FRM_MACHN_TO_CENTER}")
	private double machnToCenterDistance;

	/** Object for SyncJobDAO class. */
	@Autowired
	private SyncJobDAO syncJObDao;

	/** Object for GeoLocationCapture class. */
	@Autowired
	private GeoLocationCapture geoLocationCapture;

	/** Object for Logger. */

	private static MosipLogger LOGGER;

	/**
	 * Initializing logger.
	 *
	 * @param mosipRollingFileAppender the mosip rolling file appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.SyncStatusValidatorService#validateSyncStatus()
	 */
	public ResponseDTO validateSyncStatus() {
		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Validating the sync status started");

		Map<String, Integer> map = new HashMap<>();

		map.put(RegistrationConstants.OPT_TO_REG_MDS_J00001, mdsJobId);
		map.put(RegistrationConstants.OPT_TO_REG_LCS_J00002, lcsJobId);
		map.put(RegistrationConstants.OPT_TO_REG_PDS_J00003, pdsJobId);
		map.put(RegistrationConstants.OPT_TO_REG_RSS_J00004, rssJobId);
		map.put(RegistrationConstants.OPT_TO_REG_RCS_J00005, rcsJobId);
		map.put(RegistrationConstants.OPT_TO_REG_RPS_J00006, rpsJobId);
		map.put(RegistrationConstants.OPT_TO_REG_URS_J00007, ursJobId);
		map.put(RegistrationConstants.OPT_TO_REG_POS_J00008, posJobId);
		map.put(RegistrationConstants.OPT_TO_REG_LER_J00009, lerJobId);

		List<ErrorResponseDTO> errorResponseDTOList = new ArrayList<>();

		int syncFailureCount = 0;

		try {
			SyncJobInfo syncJobInfo = syncJObDao.getSyncStatus();

			LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
					"Fetched SyncJobInfo containing the synccontrol list and yettoexportpacket count");

			if (syncJobInfo.getSyncControlList() != null && !syncJobInfo.getSyncControlList().isEmpty()) {

				for (SyncControl syncControl : syncJobInfo.getSyncControlList()) {

					// Comment:
					Date lastSyncDate = new Date(syncControl.getLastSyncDtimez().getTime());

					LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
							"Checking the actualdaysfrequency with the configured frequency [" + lastSyncDate + "]");

					if (map.get(syncControl.getsyncJobId().trim()) <= getActualDays(lastSyncDate)) {

						syncFailureCount++;

						if (RegistrationConstants.OPT_TO_REG_LER_J00009.equals(syncControl.getsyncJobId().trim())) {
							getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_002, RegistrationConstants.OPT_TO_REG_ICS‌_002_MSG,
									RegistrationConstants.OPT_TO_REG_INFOTYPE, errorResponseDTOList);

						}
					}
				}

				if (syncFailureCount > 0) {
					getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_001, RegistrationConstants.OPT_TO_REG_ICS‌_001_MSG,
							RegistrationConstants.OPT_TO_REG_INFOTYPE, errorResponseDTOList);
				}
			}

			if (syncJobInfo.getYetToExportCount() >= packetMaxCount) {

				LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
						"Checking the yet to export packets frequency with the configured limit count");

				auditFactory.audit(AuditEvent.SYNC_PKT_COUNT_VALIDATE, AppModule.SYNC_VALIDATE,
						"Validating yet to export packets frequency with the configured limit count", "refId",
						"refIdType");

				getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_003, RegistrationConstants.OPT_TO_REG_ICS‌_003_MSG,
						RegistrationConstants.OPT_TO_REG_INFOTYPE, errorResponseDTOList);
			}

			if (RegistrationConstants.OPT_TO_REG_GEO_FLAG_SINGLETIME.equals(geoFrequnecyFlag)) {
				if (!isCapturedForTheDay()) {
					captureGeoLocation(errorResponseDTOList);
				}
			} else if (RegistrationConstants.OPT_TO_REG_GEO_FLAG_MULTIPLETIME.equals(geoFrequnecyFlag)) {
				captureGeoLocation(errorResponseDTOList);
			}

			LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
					"Validating the sync status ended");

			auditFactory.audit(AuditEvent.SYNC_INFO_VALIDATE, AppModule.SYNC_VALIDATE,
					"Validating the sync status ended successfully", "refId", "refIdType");

		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.SYNC_STATUS_VALIDATE,
					runtimeException.toString());
		}

		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setErrorResponseDTOs(errorResponseDTOList);

		return responseDTO;

	}

	/**
	 * {@code isCapturedForTheDay} is to capture time for first time login for the day.
	 * 
	 * @return boolean
	 */
	private boolean isCapturedForTheDay() {

		// TODO : lastCapturedTime - get this from application context....
		Map<String, Object> map = SessionContext.getInstance().getMapObject();
		Instant lastCapturedTime = (Instant) map.get(RegistrationConstants.OPT_TO_REG_LAST_CAPTURED_TIME);

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Capturing the login time Firsttime login");

		return lastCapturedTime != null && Duration.between(lastCapturedTime, Instant.now()).toHours() < 24;
	}

	/**
	 * {@code captureGeoLocation} is to capture the Geo location and calculate and
	 * validate the distance between the registration center and machine.
	 *
	 * @param errorResponseDTOList the error response DTO list
	 */
	private void captureGeoLocation(List<ErrorResponseDTO> errorResponseDTOList) {

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Validating the geo location of machine w.r.t registration center started");

		Map<String, Object> map = geoLocationCapture.getLatLongDtls();

		double centerLatitude = Double.parseDouble(SessionContext.getInstance().getUserContext()
				.getRegistrationCenterDetailDTO().getRegistrationCenterLatitude());

		double centerLongitude = Double.parseDouble(SessionContext.getInstance().getUserContext()
				.getRegistrationCenterDetailDTO().getRegistrationCenterLongitude());

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Getting the center latitude and longitudes from session conext");

		double distance = actualDistance(centerLatitude, centerLongitude, map);

		if (RegistrationConstants.OPT_TO_REG_SUCCESS.equals(map.get(RegistrationConstants.OPT_TO_REG_ERROR_MESSAGE))) {

			if (machnToCenterDistance <= distance) {

				getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_004, RegistrationConstants.OPT_TO_REG_ICS‌_004_MSG,
						RegistrationConstants.OPT_TO_REG_INFOTYPE, errorResponseDTOList);
			} else {

				SessionContext.getInstance().getMapObject().put(RegistrationConstants.OPT_TO_REG_LAST_CAPTURED_TIME, Instant.now());
			}
		} else {
			getErrorResponse(RegistrationConstants.OPT_TO_REG_ICS‌_005, RegistrationConstants.OPT_TO_REG_ICS‌_005_MSG,
					RegistrationConstants.OPT_TO_REG_INFOTYPE, errorResponseDTOList);
		}

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Validating the geo location of machine w.r.t registration center ended");

		auditFactory.audit(AuditEvent.SYNC_GEO_VALIDATE, AppModule.SYNC_VALIDATE,
				"Validating the geo information ended successfully", "refId", "refIdType");

	}

	/**
	 * {@code actualDistance} is to calculate the distance between the given
	 * latitudes and longitudes.
	 *
	 * @param fromlat from latitude
	 * @param fromlng from longitude
	 * @param tolat   to latitude
	 * @param tolng   to longitude
	 * @return double
	 */
	private double actualDistance(double fromlat, double fromlng, Map<String, Object> map) {

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Calculation of distance between the geo location of machine and registration center started");

		double earthRadius = RegistrationConstants.OPT_TO_REG_EARTH_RADIUS;
		double distanceLat = Math.toRadians((double) map.get(RegistrationConstants.OPT_TO_REG_LATITUDE) - fromlat);
		double distanceLng = Math.toRadians((double) map.get(RegistrationConstants.OPT_TO_REG_LONGITUDE) - fromlng);

		double tempDist = Math.sin(distanceLat / 2) * Math.sin(distanceLat / 2)
				+ Math.cos(Math.toRadians(fromlat)) * Math.cos(Math.toRadians((double) map.get(RegistrationConstants.OPT_TO_REG_LATITUDE)))
						* Math.sin(distanceLng / 2) * Math.sin(distanceLng / 2);

		double radius = 2 * Math.atan2(Math.sqrt(tempDist), Math.sqrt(1 - tempDist));

		double rounding = earthRadius * radius * RegistrationConstants.OPT_TO_REG_METER_CONVERSN / 1000;

		LOGGER.debug(RegistrationConstants.OPT_TO_REG_LOGGER_SESSION_ID, APPLICATION_NAME, APPLICATION_ID,
				"Calculation of distance between the geo location of machine and registration center started");

		return Math.round(rounding * 10000.0) / 10000.0;

	}

	/**
	 *{@code getActualDays} will calculate the difference of days between the given date and present date.
	 *
	 * @param lastSyncDate 
	 * 					date
	 * @return the number of days
	 */
	private int getActualDays(Date lastSyncDate) {
		/* */
		return (int) (lastSyncDate != null
				? ((new Date().getTime() - lastSyncDate.getTime()) / (24 * 60 * 60 * 1000) + 1)
				: 0);
	}

	/**
	 * Gets the error response.
	 *
	 * @param code                 the code
	 * @param message              the message
	 * @param infoType             the info type
	 * @param errorResponseDTOList the error response DTO list
	 * @return the error response
	 */
	private void getErrorResponse(String code, String message, String infoType,
			List<ErrorResponseDTO> errorResponseDTOList) {

		ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
		errorResponseDTO.setCode(code);
		errorResponseDTO.setMessage(message);
		errorResponseDTO.setInfoType(infoType);
		errorResponseDTOList.add(errorResponseDTO);
	}
}
