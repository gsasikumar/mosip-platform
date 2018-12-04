package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.MachineResponseDto;
import io.mosip.kernel.masterdata.dto.MachineResponseIdDto;
import io.mosip.kernel.masterdata.dto.MachineSpecIdAndId;
import io.mosip.kernel.masterdata.dto.MachineTypeCodeAndLanguageCodeAndId;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.service.MachineService;

/**

 * This controller class provides Machine details based on user provided data.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@RestController
public class MachineController {

	/**
	 * Reference to MachineService.
	 */
	@Autowired
	private MachineService machineService;

	/**
	 * 
	 * Function to fetch machine detail based on given Machine ID and Language code.
	 * 
	 * @param machineId
	 * @param langcode
	 * @return machine detail based on given Machine ID and Language code
	 */
	@GetMapping(value = "/machines/{id}/{langcode}")
	public MachineResponseIdDto getMachineIdLangcode(@PathVariable("id") String machineId,
			@PathVariable("langcode") String langCode) {
		return machineService.getMachineIdLangcode(machineId, langCode);

	}

	/**
	 * 
	 * Function to fetch machine detail based on given Language code
	 * 
	 * @param langcode
	 * @return machine detail based on given Language code
	 */

	@GetMapping(value = "/machines/{langcode}")
	public MachineResponseDto getMachineLangcode(@PathVariable("langcode") String langCode) {
		return machineService.getMachineLangcode(langCode);

	}

	/**
	 * Function to fetch a all machines details
	 * 
	 * @return all machines details
	 */
	@GetMapping(value = "/machines")
	public MachineResponseDto getMachineAll() {
		return machineService.getMachineAll();

	}
	
	/**
	 * Save machine  details to the database table
	 * 
	 * @param machine
	 *            input from user Machine  DTO
	 * @return {@link MachineTypeCodeAndLanguageCodeAndId}
	 */
	@PostMapping("/machines")
	public ResponseEntity<MachineSpecIdAndId> saveMachine(
			@Valid  @RequestBody RequestDto<MachineDto> machine) {

		return new ResponseEntity<>(machineService.createMachine(machine.getRequest()), HttpStatus.CREATED);
	}

}
