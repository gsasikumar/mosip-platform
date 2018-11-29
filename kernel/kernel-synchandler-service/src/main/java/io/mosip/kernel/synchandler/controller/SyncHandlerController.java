package io.mosip.kernel.synchandler.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.synchandler.dto.MasterDataResponseDto;
import io.mosip.kernel.synchandler.service.MasterDataService;

/**
 * Sync Handler Controller
 * 
 * @author Abhishek Kumar
 * @since 29-11-2018
 */
@RestController
public class SyncHandlerController {
	@Autowired
	private MasterDataService masterDataService;

	@GetMapping("/syncmasterdata/{machineId}")
	public MasterDataResponseDto syncMasterData(@PathVariable("machineId") String machineId,
			@RequestParam(value = "lastUpdated", required = false) LocalDateTime lastUpdated) {
		return masterDataService.syncData(machineId, lastUpdated);
	}

}
