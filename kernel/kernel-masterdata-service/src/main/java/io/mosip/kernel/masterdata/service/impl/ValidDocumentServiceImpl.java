package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.ApplicationErrorCode;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ValidDocumentDto;
import io.mosip.kernel.masterdata.entity.ValidDocument;
import io.mosip.kernel.masterdata.entity.id.ValidDocumentID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.ValidDocumentRepository;
import io.mosip.kernel.masterdata.service.ValidDocumentService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * This service class contains methods that create valid document in table.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Service
public class ValidDocumentServiceImpl implements ValidDocumentService {

	/**
	 * Reference to ValidDocumentRepository.
	 */
	@Autowired
	private ValidDocumentRepository documentRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.ValidDocumentService#
	 * insertDocumentCategory(io.mosip.kernel.masterdata.dto.
	 * ValidDocumentRequestDto)
	 */
	@Override
	public ValidDocumentID createValidDocument(RequestDto<ValidDocumentDto> document) {

		ValidDocument validDocument = MetaDataUtils.setCreateMetaData(document.getRequest(), ValidDocument.class);
		try {
			validDocument = documentRepository.create(validDocument);
		} catch (DataAccessLayerException  | DataAccessException   e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}

		ValidDocumentID validDocumentId = new ValidDocumentID();
		validDocumentId.setDocCategoryCode(validDocument.getDocCategoryCode());
		validDocumentId.setDocTypeCode(validDocument.getDocTypeCode());
		return validDocumentId;
	}

}
