package io.mosip.registration.service;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegConstants.MACHINE_MAPPING_ENTITY_ERROR_NO_RECORDS;
import static io.mosip.registration.constants.RegConstants.MACHINE_MAPPING_ENTITY_SUCCESS_MESSAGE;
import static io.mosip.registration.constants.RegConstants.MACHINE_MAPPING_LOGGER_TITLE;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.UserMachineMappingDTO;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.UserMachineMappingID;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

/**
 * User Client Machine Mapping Service
 * 
 * @author Yaswanth S
 * @since 1.0.0
 *
 */
@Service
public class MapMachineServiceImpl implements MapMachineService {

	/**
	 * LOGGER for logging
	 */
	private static MosipLogger LOGGER;

	/**
	 * intializing logger
	 * 
	 * @param mosipRollingFileAppender appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	@Autowired
	private MachineMappingDAO machineMappingDAO;


	/* (non-Javadoc)
	 * @see io.mosip.registration.service.MapMachineService#saveOrUpdate(io.mosip.registration.dto.UserMachineMappingDTO)
	 */
	@Override
	public ResponseDTO saveOrUpdate(UserMachineMappingDTO userMachineMappingDTO) {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME,
				APPLICATION_ID, "Service saveOrUpdate method called");

		//find user
		UserMachineMappingID userID = new UserMachineMappingID();
		userID.setUserID(userMachineMappingDTO.getUserID());
		userID.setCentreID(userMachineMappingDTO.getCentreID());
		userID.setMachineID(userMachineMappingDTO.getMachineID());
		
		boolean isActive=false;
		if(userMachineMappingDTO.getStatus().equalsIgnoreCase(RegConstants.MACHINE_MAPPING_ACTIVE)) {
			isActive=true;
		} else {
			isActive=false;
		}
	
		/* create response*/
		ResponseDTO responseDTO = new ResponseDTO();

		/* Interacting with DAO layer*/

		try {
			/*find user*/
			UserMachineMapping user=machineMappingDAO.findByID(userID);
			
			if(user!=null) {
				/*if user already exists*/
				user.setUpdBy(SessionContext.getInstance().getUserContext().getUserId());
				user.setUpdDtimes(OffsetDateTime.now());
				user.setIsActive(isActive);
				
				
				user.setIsActive(isActive);
				machineMappingDAO.update(user);
			} else {
				/*if user didn't exists*/
				 user = new UserMachineMapping();
				user.setUserMachineMappingId(userID);
				user.setIsActive(isActive);
				user.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
				user.setCrDtime(OffsetDateTime.now());
				user.setUpdBy(SessionContext.getInstance().getUserContext().getUserId());
				user.setUpdDtimes(OffsetDateTime.now());

				machineMappingDAO.save(user);
			}
			/* create success response*/
			SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
			successResponseDTO.setCode(RegConstants.MACHINE_MAPPING_CODE);
			successResponseDTO.setInfoType(RegConstants.ALERT_INFORMATION);
			successResponseDTO.setMessage(RegConstants.MACHINE_MAPPING_SUCCESS_MESSAGE);
			responseDTO.setSuccessResponseDTO(successResponseDTO);
			LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME,
					APPLICATION_ID, "Success Response created");
		} catch (RegBaseUncheckedException exception) {
			responseDTO = getErrorResponse(responseDTO, RegConstants.MACHINE_MAPPING_ERROR_MESSAGE);
			LOGGER.error(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME,
					APPLICATION_ID, "Error Response created");

		}
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME,
				APPLICATION_ID, "Service saveOrUpdate method ended");

		return responseDTO;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.MapMachineService#view()
	 */
	@Override
	public ResponseDTO view() {
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME,
				APPLICATION_ID, "View Method called");

		ResponseDTO responseDTO = new ResponseDTO();

		try {
			/* get mac address*/			
			String machineID = RegistrationSystemPropertiesChecker.getMachineId();
			/* get station ID*/
			String stationID = machineMappingDAO.getStationID(machineID);
			/* get center id*/
			String centerID = machineMappingDAO.getCenterID(stationID);
			/* get user list*/
			List<RegistrationUserDetail> registrationUserDetails = machineMappingDAO.getUsers(centerID);
			
			if(registrationUserDetails != null && !registrationUserDetails.isEmpty()) {
			/* create success response*/
			SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
			successResponseDTO.setCode(RegConstants.MACHINE_MAPPING_CODE);
			successResponseDTO.setMessage(MACHINE_MAPPING_ENTITY_SUCCESS_MESSAGE);
			successResponseDTO
					.setOtherAttributes(constructDTO(machineID, stationID, centerID, registrationUserDetails));

			responseDTO.setSuccessResponseDTO(successResponseDTO);
			LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME,
					APPLICATION_ID, "View Method Success Response created");
			}else {
				 getErrorResponse(responseDTO, MACHINE_MAPPING_ENTITY_ERROR_NO_RECORDS);
			}
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			responseDTO = getErrorResponse(responseDTO, regBaseUncheckedException.getMessage());
			LOGGER.error(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME,
					APPLICATION_ID, "View() Method Error "+regBaseUncheckedException.getMessage());
		} catch (RegBaseCheckedException regBaseCheckedException) {
			responseDTO = getErrorResponse(responseDTO, regBaseCheckedException.getMessage());
			LOGGER.error(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME,
					APPLICATION_ID, "Exception Method Response created"+regBaseCheckedException.getMessage());
		}

		return responseDTO;
	}

	/**
	 * To prepare List of {@link UserMachineMappingDTO}
	 * 
	 * @param machineID
	 * @param stationID
	 * @param centreID
	 * @param registrationUserDetails
	 * @return
	 */
	private Map<String, Object> constructDTO(String machineID, String stationID, String centreID,
			List<RegistrationUserDetail> registrationUserDetails) {		
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME,
				APPLICATION_ID, "constructDTO() method called");
		Map<String, Object> userDetailMap = new HashMap<>();
		try {
			List<UserMachineMappingDTO> userMachineMappingDTOs = registrationUserDetails.stream()
					.map(registrationUserDetail -> {
						UserMachineMappingDTO userMachineMappingDTO = null;
						if (registrationUserDetail != null) {
							String userID = registrationUserDetail.getId();
							String userName = registrationUserDetail.getName();
							StringBuilder role = new StringBuilder();
							String roleCode = "";
							String status = RegConstants.USER_IN_ACTIVE;
							if (!registrationUserDetail.getUserRole().isEmpty()) {
								/*
								 * List of roles
								 * 
								 * @see role code
								 */
								registrationUserDetail.getUserRole().forEach(registrationUserRole -> {
									role.append(registrationUserRole.getRegistrationUserRoleID().getRoleCode() + ",");
								});

								if (role.length() > 0) {
									roleCode = role.substring(0, role.lastIndexOf(","));
								}
							}
							if (!registrationUserDetail.getUserMachineMapping().isEmpty()) {
								
								for (UserMachineMapping userMachineMapping : registrationUserDetail.getUserMachineMapping()) {	
										if (userMachineMapping.getUserMachineMappingId()
												.getMachineID().equals(machineID)) {
											status = userMachineMapping.getIsActive() ? RegConstants.USER_ACTIVE
													: RegConstants.USER_IN_ACTIVE;	
									}
								}
							}

							userMachineMappingDTO = new UserMachineMappingDTO(userID, userName, roleCode, status,
									centreID, stationID, machineID);
						}
						return userMachineMappingDTO;

					}).collect(Collectors.toList());
			userDetailMap.put(RegConstants.USER_MACHINE_MAPID, userMachineMappingDTOs);
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME,
					APPLICATION_ID, "Exception in preparing DTO "+regBaseUncheckedException.getMessage());
		}
		return userDetailMap;
	}

	/**
	 * Common method to prepare error response
	 * 
	 * @param response
	 * @param message
	 * @return
	 */
	private ResponseDTO getErrorResponse(ResponseDTO response, final String message) {
		/* Create list of Error Response */
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		/* Error response */
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegConstants.MACHINE_MAPPING_CODE);
		errorResponse.setInfoType(RegConstants.ALERT_ERROR);
		errorResponse.setMessage(message);

		errorResponses.add(errorResponse);

		/* Assing list of error responses to response */
		response.setErrorResponseDTOs(errorResponses);
		return response;

	}
}
