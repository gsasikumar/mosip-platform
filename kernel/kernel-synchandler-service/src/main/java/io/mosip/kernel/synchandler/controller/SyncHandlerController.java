package io.mosip.kernel.synchandler.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.synchandler.constant.MasterDataErrorCode;
import io.mosip.kernel.synchandler.dto.response.MasterDataResponseDto;
import io.mosip.kernel.synchandler.exception.DateParsingException;
import io.mosip.kernel.synchandler.service.MasterDataService;
import io.mosip.kernel.synchandler.service.SyncConfigDetailsService;
import net.minidev.json.JSONObject;

/**
 * Sync Handler Controller
 * 
 * @author Abhishek Kumar
 * @author Srinivasan
 * @since 29-11-2018
 */
@RestController
public class SyncHandlerController {
	@Autowired
	private MasterDataService masterDataService;

	/**
	 * Service instance {@link SyncConfigDetailsService}
	 */
	@Autowired
	SyncConfigDetailsService syncConfigDetailsService;

	/**
	 * This API method would fetch all synced global config details from server
	 * 
	 * @return JSONObject - global config response
	 */
	@GetMapping(value = "/globalconfigs")
	public JSONObject getGlobalConfigDetails() {
		return syncConfigDetailsService.getGlobalConfigDetails();
	}

	/**
	 * * This API method would fetch all synced registration center config details
	 * from server
	 * 
	 * 
	 * @param regId - registration Id
	 * @return JSONObject
	 */
	@GetMapping(value = "/registrationcenterconfig/{registrationcenterid}")
	public JSONObject getRegistrationCentreConfig(@PathVariable(value = "registrationcenterid") String regId) {
		return syncConfigDetailsService.getRegistrationCenterConfigDetails(regId);
	}

	@GetMapping("/syncmasterdata/{machineId}")
	public MasterDataResponseDto syncMasterData(@PathVariable("machineId") String machineId,
			@RequestParam(value = "lastUpdated", required = false) String lastUpdated) {
		LocalDateTime timestamp = null;
		if (lastUpdated != null) {
			try {
				timestamp = LocalDateTime.parse(lastUpdated);
			} catch (Exception e) {
				throw new DateParsingException(MasterDataErrorCode.LAST_UPDATED_PARSE_EXCEPTION.getErrorCode(),
						e.getMessage());
			}
		}
		return masterDataService.syncData(machineId, timestamp);
	}

}
