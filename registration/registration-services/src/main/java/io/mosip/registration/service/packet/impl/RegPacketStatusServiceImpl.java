package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.util.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationTransactionType;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegPacketStatusDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegPacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.SyncRegistrationDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.packet.RegPacketStatusService;
import io.mosip.registration.service.sync.PacketSynchService;

/**
 * This class will update the packet status in the table after sync with the
 * server.
 * 
 * @author Himaja Dhanyamraju
 */
@Service
public class RegPacketStatusServiceImpl extends BaseService implements RegPacketStatusService {

	@Autowired
	private RegPacketStatusDAO regPacketStatusDAO;

	@Autowired
	private RegistrationDAO registrationDAO;

	@Autowired
	private PacketSynchService packetSynchService;

	private static final Logger LOGGER = AppConfig.getLogger(RegPacketStatusServiceImpl.class);

	/**
	 * Required no days to maintain registrations
	 */
	@Value("${REG_NO_OF_DAYS_LIMIT_TO_DELETE}")
	private int noOfDays;

	private HashMap<String, Registration> registrationMap = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.packet.RegPacketStatusService#
	 * deleteRegistrationPackets()
	 */
	@Override
	synchronized public ResponseDTO deleteRegistrationPackets() {

		LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Delete  Reg-packets started");

		ResponseDTO responseDTO = new ResponseDTO();

		Timestamp reqTime = new Timestamp(System.currentTimeMillis());

		try {
			/* Get Registrations to be deleted */
			List<Registration> registrations = registrationDAO
					.getRegistrationsToBeDeleted(getPacketDeletionLastDate(reqTime));

			deleteRegistrations(registrations);

			setSuccessResponse(responseDTO, RegistrationConstants.REGISTRATION_DELETION_BATCH_JOBS_SUCCESS, null);

		} catch (RuntimeException runtimeException) {

			LOGGER.error("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME,
					APPLICATION_ID, runtimeException.getMessage());

			setErrorResponse(responseDTO, RegistrationConstants.REGISTRATION_DELETION_BATCH_JOBS_FAILURE, null);
		}

		LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Delete  Reg-packets ended");

		return responseDTO;

	}

	private Timestamp getPacketDeletionLastDate(Timestamp reqTime) {

		/* Get Calendar instance */
		Calendar cal = Calendar.getInstance();
		cal.setTime(reqTime);
		cal.add(Calendar.DATE, -noOfDays);

		/* To-Date */
		return new Timestamp(cal.getTimeInMillis());
	}

	/**
	 * Get all registrationIDs for which the clientStatus is post-sync
	 *
	 * @return List<String> list of registrationId's required for packet status sync
	 *         with server
	 */
	private List<String> getPacketIds() {
		LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"getting packets by status post-sync has been ended");

		List<Registration> registrationList = regPacketStatusDAO.getPacketIdsByStatusUploaded();

		List<String> packetIds = new ArrayList<>();
		for (Registration registration : registrationList) {
			String registrationId = registration.getId();

			registrationMap.put(registrationId, registration);
			packetIds.add(registrationId);
		}
		LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"getting packets by status post-sync has been ended");
		return packetIds;
	}

	/**
	 * update status for all packets that are synced with server
	 *
	 * @param registrations
	 *            list of registration entities which are represented as
	 *            LinkedHashMap which maps the attributes of registration entity to
	 *            their respective values that are obtained after sync with server
	 */
	private void updatePacketIdsByServerStatus(List<LinkedHashMap<String, String>> registrations)
			throws RegBaseUncheckedException {
		LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"packets status sync from server has been started");

		List<RegPacketStatusDTO> packetStatusDTO = new ArrayList<>();
		for (Map<String, String> registration : registrations) {
			packetStatusDTO.add(
					new RegPacketStatusDTO(registration.get(RegistrationConstants.PACKET_STATUS_SYNC_REGISTRATION_ID),
							registration.get(RegistrationConstants.PACKET_STATUS_SYNC_STATUS_CODE)));
		}

		try {
			for (RegPacketStatusDTO regPacketStatusDTO : packetStatusDTO) {

				Registration registration = registrationMap.get(regPacketStatusDTO.getPacketId());
				registration.setServerStatusCode(regPacketStatusDTO.getStatus());
				registration.setServerStatusTimestamp(new Timestamp(System.currentTimeMillis()));

				delete(registration, regPacketStatusDTO.getStatus(), false);

			}
			LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME,
					APPLICATION_ID, "packets status sync from server has been ended");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME,
					APPLICATION_ID, runtimeException.getMessage());
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_UPDATE_STATUS,
					runtimeException.toString());

		}

	}

	@SuppressWarnings("unchecked")
	synchronized public ResponseDTO packetSyncStatus() {

		LOGGER.info("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
				"packet status sync called");

		List<LinkedHashMap<String, String>> registrations = new ArrayList<>();
		List<String> packetIds = getPacketIds();
		LOGGER.info("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
				"PacketIds for sync with server have been retrieved");

		/* Create Response to Return to UI layer */
		ResponseDTO response = new ResponseDTO();
		SuccessResponseDTO successResponse;

		/* Validator response service API creation */
		final String SERVICE_NAME = RegistrationConstants.PACKET_STATUS_SYNC_SERVICE_NAME;

		/* prepare request params to pass through URI */
		Map<String, String> requestParamMap = new HashMap<>();
		String packetIdList = packetIds.stream().map(Object::toString).collect(Collectors.joining(","));
		requestParamMap.put(RegistrationConstants.PACKET_STATUS_SYNC_URL_PARAMETER, packetIdList);

		try {
			/* Obtain RegistrationStatusDTO from service delegate util */
			registrations = (List<LinkedHashMap<String, String>>) serviceDelegateUtil.get(SERVICE_NAME, requestParamMap,
					false);
			if (!registrations.isEmpty()) {
				/* update the status of packets after sync with server */
				try {
					updatePacketIdsByServerStatus(registrations);
				} catch (RegBaseUncheckedException regBaseUncheckedException) {
					LOGGER.error("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
							regBaseUncheckedException.getMessage());

					setErrorResponse(response, RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE, null);
					return response;
				}
				/* Create Success response */
				successResponse = new SuccessResponseDTO();
				successResponse.setCode(RegistrationConstants.ALERT_INFORMATION);
				successResponse.setMessage(RegistrationConstants.PACKET_STATUS_SYNC_SUCCESS_MESSAGE);
				Map<String, Object> otherAttributes = new HashMap<>();
				otherAttributes.put(RegistrationConstants.PACKET_STATUS_SYNC_RESPONSE_ENTITY, registrations);
				successResponse.setOtherAttributes(otherAttributes);
				response.setSuccessResponseDTO(successResponse);
				LOGGER.info("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
						"Success Response Created");
			} else {
				/* Create Error response */
				setErrorResponse(response, RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE, null);
				return response;
			}

		} catch (SocketTimeoutException | RegBaseCheckedException | IllegalArgumentException | HttpClientErrorException
				| HttpServerErrorException | ResourceAccessException exception) {
			LOGGER.error("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage());

			setErrorResponse(response, RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE, null);
			return response;
		}
		LOGGER.info("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
				"Packet Status Sync ended");

		return response;
	}

	private Registration delete(final Registration registration, final String clientStatus, boolean isToBeDeleted) {

		LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Delete Registration Packet started");
		Registration updatedRegistration = null;

		isToBeDeleted = (clientStatus.equalsIgnoreCase(RegistrationConstants.PACKET_STATUS_CODE_PROCESSED)
				|| isToBeDeleted);

		if (isToBeDeleted) {
			File ackFile = null;
			File zipFile = null;
			String ackPath = registration.getAckFilename();
			ackFile = new File(ackPath);
			String zipPath = ackPath.replace("_Ack.png", RegistrationConstants.ZIP_FILE_EXTENSION);
			zipFile = new File(zipPath);

			if (ackFile != null) {

				Files.delete(ackFile);
				Files.delete(zipFile);

				/* Delete row from DB */
				regPacketStatusDAO.delete(registration);

				return registration;

			}
		}

		else {
			/* Get Registration Transaction List for each transaction */
			List<RegistrationTransaction> transactionList = registration.getRegistrationTransaction();
			if (isNull(transactionList)) {
				transactionList = new LinkedList<>();
			}
			/* Prepare Registration Transaction */
			RegistrationTransaction registrationTxn = new RegistrationTransaction();

			registrationTxn.setRegId(registration.getId());
			registrationTxn.setTrnTypeCode(RegistrationTransactionType.CREATED.getCode());
			registrationTxn.setLangCode("ENG");
			registrationTxn.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
			registrationTxn.setCrDtime(new Timestamp(System.currentTimeMillis()));

			registrationTxn.setStatusCode(registration.getClientStatusCode());

			transactionList.add(registrationTxn);
			registration.setRegistrationTransaction(transactionList);

			updatedRegistration = regPacketStatusDAO.update(registration);

		}
		LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Delete Registration Packet ended");

		return updatedRegistration;

	}

	@Override
	public void deleteRegistrations(List<Registration> registrations) {
		for (Registration registration : registrations) {
			/* Delete each registration */
			delete(registration, registration.getStatusCode(), true);
		}

	}

public ResponseDTO syncPacket() {

		LOGGER.debug("REGISTRATION - SYNCH_PACKETS_TO_SERVER - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Sync the packets to the server");
		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
		List<ErrorResponseDTO> errorList = new ArrayList<>();
		try {

			List<Registration> packetsToBeSynched = registrationDAO
					.getPacketsToBeSynched(RegistrationConstants.PACKET_STATUS);
			List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
			Object response = null;
			if (!packetsToBeSynched.isEmpty()) {
				for (Registration packetToBeSynch : packetsToBeSynched) {
					SyncRegistrationDTO syncDto = new SyncRegistrationDTO();
					syncDto.setLangCode("ENG");
					syncDto.setStatusComment(packetToBeSynch.getClientStatusCode() + " " + "-" + " "
							+ packetToBeSynch.getClientStatusComments());
					syncDto.setRegistrationId(packetToBeSynch.getId());
					syncDto.setSyncStatus(RegistrationConstants.PACKET_STATUS_PRE_SYNC);
					syncDto.setSyncType(RegistrationConstants.PACKET_STATUS_SYNC_TYPE);
					syncDtoList.add(syncDto);
				}
				response = packetSynchService.syncPacketsToServer(syncDtoList);
			}
			if (response != null) {
				packetSynchService.updateSyncStatus(packetsToBeSynched);
				successResponseDTO.setMessage(RegistrationConstants.SUCCESS);
				responseDTO.setSuccessResponseDTO(successResponseDTO);
			}
		} catch (RegBaseUncheckedException | RegBaseCheckedException | JsonProcessingException | URISyntaxException e) {
			LOGGER.error("REGISTRATION - SYNCH_PACKETS_TO_SERVER - REG_PACKET_STATUS_SYNC", APPLICATION_NAME,
					APPLICATION_ID, "Error in Synching packets to the server");
			ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
			errorResponseDTO.setMessage(e.getMessage());
			errorList.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorList);
		}
		return responseDTO;
	}

}
