/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptomanager.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.cryptomanager.dto.CryptoEncryptRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptoEncryptResponseDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Rest Controller for Crypto-Manager-Service
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@CrossOrigin
@RestController
@Api(value = "Operation related to Encryption and Decryption", tags = { "cryptomanager" })
public class CryptomanagerController {

	/**
	 * {@link CryptomanagerService} instance
	 */
	@Autowired
	CryptomanagerService cryptomanagerService;

	/**
	 * Controller for Encrypt the data
	 * 
	 * @param cryptomanagerRequestDto
	 *            {@link CryptomanagerRequestDto} request
	 * @return {@link CryptomanagerResponseDto} encrypted Data
	 */
	//@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_PROCESSOR','ID_AUTHENTICATION','TEST')")
	@ResponseFilter
	@ApiOperation(value = "Encrypt the data", response = CryptomanagerResponseDto.class)
	@PostMapping(value = "/encrypt", produces = "application/json")
	public CryptomanagerResponseDto encrypt(
			@ApiParam("Data to encrypt in BASE64 encoding with meta-data") @RequestBody @Valid RequestWrapper<CryptomanagerRequestDto> cryptomanagerRequestDto) {
		return cryptomanagerService.encrypt(cryptomanagerRequestDto.getRequest());
	}
	/**
	 * 
	 * @param cryptomanagerRequestDto
	 * @return {@link CryptoEncryptResponseDto }
	 */
	@ResponseFilter
	@ApiOperation(value = "Encrypt the data", response = CryptomanagerResponseDto.class)
	@PostMapping(value = "/encrypt/private", produces = "application/json")
	public CryptoEncryptResponseDto encryptWithPrivate(
			@ApiParam("Data to encrypt in BASE64 encoding with meta-data") @RequestBody @Valid RequestWrapper<CryptoEncryptRequestDto> cryptomanagerRequestDto) {
		return cryptomanagerService.encryptWithPrivate(cryptomanagerRequestDto.getRequest());
	}

	/**
	 * Controller for Decrypt the data
	 * 
	 * @param cryptomanagerRequestDto
	 *            {@link CryptomanagerRequestDto} request
	 * @return {@link CryptomanagerResponseDto} decrypted Data
	 */
	//@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_PROCESSOR','ID_AUTHENTICATION','TEST')")
	@ResponseFilter
	@ApiOperation(value = "Decrypt the data", response = CryptomanagerResponseDto.class)
	@PostMapping(value = "/decrypt", produces = "application/json")
	public CryptomanagerResponseDto decrypt(
			@ApiParam("Data to decrypt in BASE64 encoding with meta-data") @RequestBody @Valid RequestWrapper<CryptomanagerRequestDto> cryptomanagerRequestDto) {
		return cryptomanagerService.decrypt(cryptomanagerRequestDto.getRequest());
	}
}
