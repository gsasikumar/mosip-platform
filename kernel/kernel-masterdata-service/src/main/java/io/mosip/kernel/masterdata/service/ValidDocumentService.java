package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.ValidDocCategoryAndDocTypeResponseDto;
import io.mosip.kernel.masterdata.dto.ValidDocumentDto;
import io.mosip.kernel.masterdata.dto.postresponse.DocCategoryAndTypeResponseDto;
import io.mosip.kernel.masterdata.entity.id.ValidDocumentID;

/**
 * This interface contains methods to create valid document.
 * 
 * @author Ritesh Sinha
 * @author Neha Sinha
 * 
 * @since 1.0.0
 *
 */
public interface ValidDocumentService {
	/**
	 * This method create valid document in table.
	 * 
	 * @param document the dto.
	 * @return {@link ValidDocumentID}
	 */
	public ValidDocumentID createValidDocument(ValidDocumentDto document);

	/**
	 * This method delete valid document.
	 * 
	 * @param docCatCode  the document category code.
	 * @param docTypeCode the docuemnt type code.
	 * @return {@link DocCategoryAndTypeResponseDto}.
	 */
	public DocCategoryAndTypeResponseDto deleteValidDocuemnt(String docCatCode, String docTypeCode);

	/**
	 * This method to get all the valid document category along with doc type
	 * 
	 * @param langCode
	 *            the language code
	 * @return {@link ValidDocCategoryAndDocTypeResponseDto}
	 */
	public ValidDocCategoryAndDocTypeResponseDto getValidDocumentByLangCode(String langCode);
}
