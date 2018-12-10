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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/** Rest Controller for Cryptographic Service
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@CrossOrigin
@RestController
@RequestMapping("/v1.0")
@Api(value="Operation related to Encryption and Decryption",tags = { "cryptomanager" })
public class CryptomanagerController {

	/**
	 * {@link CryptomanagerService} instance
	 */
	@Autowired
	CryptomanagerService cryptoService;

	/**
	 * Controller for Encrypt the data
	 * 
	 * @param cryptoRequestDto
	 *            {@link CryptomanagerRequestDto} request
	 * @return {@link CryptomanagerResponseDto} encrypted Data
	 */
	@ApiOperation(value = "Encrypt the data",response = CryptomanagerResponseDto.class)
	@PostMapping(value = "/encrypt",produces= "application/json")
	public CryptomanagerResponseDto encrypt(@ApiParam("Data to encrypt in base64 encoding with meta-data")@RequestBody @Valid CryptomanagerRequestDto cryptoRequestDto) {
		return cryptoService.encrypt(cryptoRequestDto);
	}

	
	/**
	 * Controller for Decrypt the data
	 * 
	 * @param cryptoRequestDto
	 *            {@link CryptomanagerRequestDto} request
	 * @return {@link CryptomanagerResponseDto} decrypted Data
	 */
	@ApiOperation(value = "Decrypt the data",response = CryptomanagerResponseDto.class)
	@PostMapping(value = "/decrypt",produces= "application/json")
	public CryptomanagerResponseDto decrypt(@ApiParam("Data to decrypt in base64 encoding with meta-data")@RequestBody @Valid CryptomanagerRequestDto cryptoRequestDto) {
		return cryptoService.decrypt(cryptoRequestDto);
	}
}
