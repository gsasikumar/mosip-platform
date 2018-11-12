package io.mosip.kernel.masterdata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.service.BiometricTypeService;

/**
 * Controller APIs to get Biometric types details
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping("/biometrictypes")
public class BiometricTypeController {

	@Autowired
	private BiometricTypeService biometricTypeService;

	/**
	 * API to fetch all Biometric types details
	 * 
	 * @return All Biometric types
	 */
	@GetMapping("/all")
	public List<BiometricTypeDto> fetchAllBioMetricType() {
		return biometricTypeService.getAllBiometricTypes();
	}

	/**
	 * API to fetch all Biometric types details based on language code
	 * 
	 * @return All Biometric types of specific language
	 */
	@GetMapping("/all/{languagecode}")
	public List<BiometricTypeDto> fetchAllBiometricTypeUsingLangCode(@PathVariable("languagecode") String langCode) {
		return biometricTypeService.getAllBiometricTypesByLanguageCode(langCode);
	}

	/**
	 * API to fetch a Biometric type details using id and language code
	 * 
	 * @return A Biometric type
	 */
	@GetMapping("/{id}/{languagecode}")
	public BiometricTypeDto fetchBiometricTypeUsingCodeAndLangCode(@PathVariable("id") String code,
			@PathVariable("languagecode") String langCode) {
		return biometricTypeService.getBiometricTypeByCodeAndLangCode(code, langCode);
	}
}
