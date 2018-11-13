
package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.DeviceErrorCode;
import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceResponseDto;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.exception.DeviceFetchException;
import io.mosip.kernel.masterdata.exception.DeviceMappingException;
import io.mosip.kernel.masterdata.exception.DeviceNotFoundException;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.service.DeviceService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

/**
 * This class have methods to fetch a Device Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Service
public class DeviceServiceImpl implements DeviceService {

	/**
	 * Field to hold Device Repository object
	 */
	@Autowired
	DeviceRepository deviceRepository;

	/**
	 * Field to hold ObjectMapperUtil object
	 */
	@Autowired
	ObjectMapperUtil objectMapperUtil;
	


	/**
	 * Method used for fetch all Device details based on given language code
	 * 
	 * @return DeviceDto returning Device Details based on given language code
	 * 
	 * @param langCode pass language as string
	 * 
	 * @throws DeviceFetchException
	 *             While Fetching Device Detail If fails to fetch required Device
	 *             Detail
	 * 
	 * @throws DeviceMappingException
	 *             If not able to map Device detail entity with Device Detail Dto
	 * 
	 * @throws DeviceNotFoundException
	 *             If given required Device ID and language not found
	 * 
	 */
	
	@Override
	public DeviceResponseDto getDeviceLangCode(String langCode) {
		List<Device> deviceList = null;
		List<DeviceDto> deviceDtoList = null;
		DeviceResponseDto deviceResponseDto = new DeviceResponseDto();
		try {
			deviceList = deviceRepository.findByLangCode(langCode);
		} catch (DataAccessException dataAccessLayerException) {
			throw new DeviceFetchException(DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (deviceList != null && !deviceList.isEmpty()) {
			try {
				deviceDtoList = objectMapperUtil.mapAll(deviceList,DeviceDto.class);
			}catch (IllegalArgumentException | ConfigurationException | MappingException exception) {
				throw new DeviceMappingException(
						DeviceErrorCode.DEVICE_MAPPING_EXCEPTION.getErrorCode(),
						DeviceErrorCode.DEVICE_MAPPING_EXCEPTION.getErrorMessage());
			}
		} else {
			throw new DeviceNotFoundException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		deviceResponseDto.setDevices(deviceDtoList);
		return deviceResponseDto;
	}
	
	
	/**
	 * Method used for fetch all Device details based on given language code and Device type
	 * 
	 * @return DeviceDto returning all Device Detail based on language code and Device type
	 * 
	 * @param langCode 
	 * 					pass language code as String
	 * @param dtypeCode 
	 * 					pass Device type as String
	 * 
	 * @throws DeviceFetchException
	 *             While Fetching Device Detail If fails to fetch required Device
	 *             Detail
	 * @throws DeviceMappingException
	 *             If not able to map Device detail entity with Device Detail Dto
	 * @throws DeviceNotFoundException
	 *             If given required Device ID and language not found
	 * 
	 */
	
	@Override
	public DeviceLangCodeResponseDto getDeviceLangCodeAndDeviceType(String langCode, String dtypeCode) {

		List<Object[]> objectList = null;
		List<DeviceLangCodeDtypeDto> deviceLangCodeDtypeDtoList = null;
		DeviceLangCodeResponseDto deviceLangCodeResponseDto = new DeviceLangCodeResponseDto();
		try {
			objectList = deviceRepository.findByLangCodeAndDtypeCode(langCode, dtypeCode);
		} catch (DataAccessException dataAccessLayerException) {
			throw new DeviceFetchException(DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (objectList != null && !objectList.isEmpty()) {
			try {
				deviceLangCodeDtypeDtoList = objectMapperUtil.mapDeviceDto(objectList);
			}catch (IllegalArgumentException | ConfigurationException | MappingException exception) {
				throw new DeviceMappingException(
						DeviceErrorCode.DEVICE_MAPPING_EXCEPTION.getErrorCode(),
						DeviceErrorCode.DEVICE_MAPPING_EXCEPTION.getErrorMessage());
			}
		} else {
			throw new DeviceNotFoundException(DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorCode(),
					DeviceErrorCode.DEVICE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		deviceLangCodeResponseDto.setDevices(deviceLangCodeDtypeDtoList);
		return deviceLangCodeResponseDto;
	}

}
