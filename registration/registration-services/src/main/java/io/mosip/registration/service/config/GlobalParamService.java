package io.mosip.registration.service.config;

import java.util.Map;

import io.mosip.registration.dto.ResponseDTO;

/**
 * Service Class for GlobalContextParameters
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface GlobalParamService {

	/**
	 * Fetching Global parameters of application
	 * 
	 * @return map
	 */
	Map<String, Object> getGlobalParams();

	/**
	 * Get Global params details from server
	 * 
	 * @param isJob whaeather it is triggerred by job or not
	 *            
	 * @return response
	 */
	ResponseDTO synchConfigData(boolean isJob);
	
	/**
	 * Update software update status.
	 *
	 * @return the response DTO
	 */
	ResponseDTO updateSoftwareUpdateStatus(boolean isUpdateAvailable);
	

}