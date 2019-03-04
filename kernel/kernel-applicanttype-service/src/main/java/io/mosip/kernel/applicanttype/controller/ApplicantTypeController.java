package io.mosip.kernel.applicanttype.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.applicanttype.dto.KeyValues;
import io.mosip.kernel.applicanttype.dto.request.RequestDTO;
import io.mosip.kernel.applicanttype.dto.response.ResponseDTO;
import io.mosip.kernel.applicanttype.service.ApplicantTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Bal Vikash Sharma
 *
 */
@RestController
@RequestMapping(value = "/v1.0/applicanttype")
@Api(value = "This service provide operations on applicant type", tags = { "ApplicantType" })
public class ApplicantTypeController {

	@Autowired
	private ApplicantTypeService applicantTypeService;

	@GetMapping(value = "/getApplicantType")
	@ApiOperation(value = "Get applicant type for provided queries", notes = "Get applicant type for matching queries", response = String.class)
	public ResponseEntity<ResponseDTO> getApplicantType(@RequestParam("individualTypeCode") String individualTypeCode,
			@RequestParam("genderCode") String genderCode, @RequestParam("dateofbirth") String dateofbirth,
			@RequestParam(value = "biometricAvailable", required = false, defaultValue = "false") Boolean biometricAvailable) {
		RequestDTO dto = new RequestDTO();
		KeyValues request = new KeyValues();
		request.getRequest().put("individualTypeCode", individualTypeCode);
		request.getRequest().put("genderCode", genderCode);
		request.getRequest().put("dateofbirth", dateofbirth);
		request.getRequest().put("biometricAvailable", biometricAvailable.toString());
		dto.setRequest(request);
		return new ResponseEntity<>(applicantTypeService.getApplicantType(dto), HttpStatus.OK);
	}
}
