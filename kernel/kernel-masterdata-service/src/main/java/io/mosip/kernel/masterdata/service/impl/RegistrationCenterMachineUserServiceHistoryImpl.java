package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.RegistrationCenterErrorCode;
import io.mosip.kernel.masterdata.constant.RegistrationCenterUserMappingHistoryErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingHistoryDto;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterUserMachineMappingHistoryResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistory;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterUserMachineHistoryRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineUserHistoryService;
import io.mosip.kernel.masterdata.utils.MapperUtils;

/**
 * Implementation class for user machine mapping service
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
public class RegistrationCenterMachineUserServiceHistoryImpl implements RegistrationCenterMachineUserHistoryService {

	/**
	 * {@link RegistrationCenterUserMachineHistoryRepository} instance
	 */
	@Autowired
	RegistrationCenterUserMachineHistoryRepository registrationCenterUserMachineHistoryRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.
	 * RegistrationCenterMachineUserHistoryService#
	 * getRegistrationCentersMachineUserMapping(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public RegistrationCenterUserMachineMappingHistoryResponseDto getRegistrationCentersMachineUserMapping(
			String effectiveTimestamp, String registrationCenterId, String machineId, String userId) {
		List<RegistrationCenterUserMachineHistory> registrationCenterUserMachines = null;
		LocalDateTime lDateAndTime = null;
		try {
			lDateAndTime = MapperUtils.parseToLocalDateTime(effectiveTimestamp);
		} catch (Exception e) {
			throw new RequestException(RegistrationCenterErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorCode(),
					RegistrationCenterErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorMessage());
		}
		try {
			registrationCenterUserMachines = registrationCenterUserMachineHistoryRepository
					.findByIdAndEffectivetimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
							new RegistrationCenterMachineUserID(registrationCenterId, userId, machineId), lDateAndTime);
		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new MasterDataServiceException(
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_FETCH_EXCEPTION
							.getErrorCode(),
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_FETCH_EXCEPTION
							.getErrorMessage());
		}
		if (registrationCenterUserMachines != null && registrationCenterUserMachines.isEmpty()) {
			throw new DataNotFoundException(
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_NOT_FOUND
							.getErrorCode(),
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_NOT_FOUND
							.getErrorMessage());
		}

		List<RegistrationCenterUserMachineMappingHistoryDto> registrationCenters = null;
		registrationCenters = MapperUtils.mapAll(registrationCenterUserMachines,
				RegistrationCenterUserMachineMappingHistoryDto.class);
		RegistrationCenterUserMachineMappingHistoryResponseDto centerUserMachineMappingResponseDto = new RegistrationCenterUserMachineMappingHistoryResponseDto();
		centerUserMachineMappingResponseDto.setRegistrationCenters(registrationCenters);
		return centerUserMachineMappingResponseDto;
	}

}
