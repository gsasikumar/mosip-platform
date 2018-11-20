package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DocumentCategoryRequestDto;
import io.mosip.kernel.masterdata.dto.DocumentCategoryResponseDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;

/**
 * @author Neha
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestController
public class DocumentCategoryController {

	@Autowired
	DocumentCategoryService documentCategoryService;

	/**
	 * API to fetch all Document categories details
	 * 
	 * @return All Document categories
	 */
	@GetMapping("/documentcategories")
	public DocumentCategoryResponseDto fetchAllDocumentCategory() {
		return documentCategoryService.getAllDocumentCategory();
	}

	/**
	 * API to fetch all Document categories details based on language code
	 * 
	 * @return All Document categories of a specific language
	 */
	@GetMapping("/documentcategories/{languagecode}")
	public DocumentCategoryResponseDto fetchAllDocumentCategoryUsingLangCode(
			@PathVariable("languagecode") String langCode) {
		return documentCategoryService.getAllDocumentCategoryByLaguageCode(langCode);
	}

	/**
	 * API to fetch A Document category details using id and language code
	 * 
	 * @return A Document category
	 */
	@GetMapping("/documentcategories/{id}/{languagecode}")
	public DocumentCategoryResponseDto fetchDocumentCategoryUsingCodeAndLangCode(@PathVariable("id") String code,
			@PathVariable("languagecode") String langCode) {
		return documentCategoryService.getDocumentCategoryByCodeAndLangCode(code, langCode);
	}

	@PostMapping("/documentcategories")
	public PostResponseDto addDocumentCategories(@RequestBody DocumentCategoryRequestDto category) {
		return documentCategoryService.addDocumentCategoriesData(category);
		
	}
}
