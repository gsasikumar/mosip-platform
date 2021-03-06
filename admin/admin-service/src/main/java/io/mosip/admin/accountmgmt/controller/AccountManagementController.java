package io.mosip.admin.accountmgmt.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.accountmgmt.dto.PasswordDto;
import io.mosip.admin.accountmgmt.dto.ResetPasswordDto;
import io.mosip.admin.accountmgmt.dto.StatusResponseDto;
import io.mosip.admin.accountmgmt.dto.UserDetailRestClientDto;
import io.mosip.admin.accountmgmt.dto.UserDetailsDto;
import io.mosip.admin.accountmgmt.dto.UserNameDto;
import io.mosip.admin.accountmgmt.dto.ValidationResponseDto;
import io.mosip.admin.accountmgmt.service.AccountManagementService;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.swagger.annotations.Api;
import springfox.documentation.annotations.ApiIgnore;

/**
 * AccountManagementController.
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/accountmanagement")
@Api(tags = { "AccountManagement" })
public class AccountManagementController {

	/** The account management service. */
	@Autowired
	AccountManagementService accountManagementService;

	/**
	 * Change password.
	 *
	 * @param passwordDto
	 *            the password dto
	 * @param otpChannel
	 *            the otp channel
	 * @return the string
	 */
	@ResponseFilter
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','CENTRAL_ADMIN')")
	@PostMapping("/changepassword")
	public ResponseWrapper<StatusResponseDto> changePassword(
			@RequestBody @Valid RequestWrapper<PasswordDto> passwordDto) {
		ResponseWrapper<StatusResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(accountManagementService.changePassword(passwordDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * Reset password.
	 *
	 * @param passwordDto
	 *            the password dto
	 * @param otpChannel
	 *            the otp channel
	 */
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','CENTRAL_ADMIN')")
	@PostMapping("/resetpassword")
	public ResponseWrapper<StatusResponseDto> resetPassword(@RequestBody RequestWrapper<ResetPasswordDto> passwordDto) {
		ResponseWrapper<StatusResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(accountManagementService.resetPassword(passwordDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * Forgot username.
	 *
	 * @param userId
	 *            the user id
	 * @return the user name dto
	 */
	@ApiIgnore
	@ResponseFilter
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','CENTRAL_ADMIN')")
	@GetMapping("/forgotusername")
	public UserNameDto forgotUsername(String userId) {
		return accountManagementService.getUserName(userId);
	}

	/**
	 * Un block account.
	 *
	 * @param userId
	 *            the user id
	 */
	@ResponseFilter
	@GetMapping("/unblockaccount/{userid}")
	public ResponseWrapper<StatusResponseDto> unBlockAccount(@PathVariable("userid") String userId) {
		ResponseWrapper<StatusResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(accountManagementService.unBlockUserName(userId));
		return responseWrapper;
	}

	@ResponseFilter
	@GetMapping("/username/{mobilenumber}")
	public ResponseWrapper<UserNameDto> getUserName(@PathVariable("mobilenumber") String mobile) {
		ResponseWrapper<UserNameDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(accountManagementService.getUserNameBasedOnMobileNumber(mobile));
		return responseWrapper;
	}

	/**
	 * Gets the user detail.
	 *
	 * @param mobile the mobile
	 * @return the user detail
	 */
	@ResponseFilter
	@GetMapping("/userdetail/{mobilenumber}")
	public ResponseWrapper<UserDetailsDto> getUserDetail(@PathVariable("mobilenumber") String mobile)  {
		ResponseWrapper<UserDetailsDto> responseWrapper= new ResponseWrapper<>();
		responseWrapper.setResponse(accountManagementService.getUserDetailBasedOnMobileNumber(mobile));
		return responseWrapper ;
	}
	
	@ResponseFilter
	@GetMapping("/userdetails/{regid}")
	public ResponseWrapper<UserDetailRestClientDto> getUserDetailBasedOnUid(@PathVariable("regid") String regId)  {
		ResponseWrapper<UserDetailRestClientDto> responseWrapper= new ResponseWrapper<>();
		responseWrapper.setResponse(accountManagementService.getUserDetailBasedOnRegId(regId));
		return responseWrapper ;
	}

	@ResponseFilter
	@GetMapping(value = "/validate/{userid}")
	public ResponseWrapper<ValidationResponseDto> validateUserName(@PathVariable("userid") String userId) {
		ValidationResponseDto validationResponseDto = accountManagementService.validateUserName(userId);
		ResponseWrapper<ValidationResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(validationResponseDto);
		return responseWrapper;
	}


}
