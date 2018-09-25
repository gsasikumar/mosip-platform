package org.mosip.registration.processor.status.controller;

import java.util.List;

import org.mosip.registration.processor.status.code.RegistrationStatusCode;
import org.mosip.registration.processor.status.dto.RegistrationStatusDto;
import org.mosip.registration.processor.status.service.RegistrationStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v0.1/registration-processor/registration-status")
@Api(tags = "Status Handler")
public class RegistrationStatusController {

	@Autowired
	RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;

	@GetMapping(path = "/registrationstatus", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the registration entity", response = RegistrationStatusCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Registration Entity successfully fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch the Registration Entity") })
	public ResponseEntity<List<RegistrationStatusDto>> search(
			@RequestParam(value = "registrationIds", required = true) String registrationIds) {
		List<RegistrationStatusDto>registrations = registrationStatusService.getByIds(registrationIds);
		return ResponseEntity.status(HttpStatus.OK).body(registrations);
	}
}
