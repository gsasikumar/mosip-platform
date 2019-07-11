package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DeviceTypeExtnDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This interface has abstract methods to save a Device Type Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface DeviceTypeService {
	/**
	 * Abstract method to save Device Type Details to the Database
	 * 
	 * @param deviceTypes
	 *            input from user
	 * @return CodeAndLanguageCodeID returning code and LanguageCode
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving Device Type
	 * 
	 */
	public CodeAndLanguageCodeID createDeviceType(DeviceTypeDto deviceTypes);

	/**
	 * Method to get all device types.
	 * 
	 * @param pageNumber
	 *            the page number
	 * @param pageSize
	 *            the size of each page
	 * @param sortBy
	 *            the attributes by which it should be ordered
	 * @param orderBy
	 *            the order to be used
	 * 
	 * @return the response i.e. pages containing the device types
	 */
	public PageDto<DeviceTypeExtnDto> getAllDeviceTypes(int pageNumber, int pageSize, String sortBy, String orderBy);

}
