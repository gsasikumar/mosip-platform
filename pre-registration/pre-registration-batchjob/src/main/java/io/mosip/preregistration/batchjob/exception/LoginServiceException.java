/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjob.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;


/**
 * 
 * This class defines the LoginServiceException
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
public class LoginServiceException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ServiceError> validationErrorList;
	
	private MainResponseDTO<?> mainResposneDTO;

	public List<ServiceError> getValidationErrorList() {
		return validationErrorList;
	}

	public LoginServiceException(List<ServiceError> validationErrorList,MainResponseDTO<?> response) {
		this.validationErrorList = validationErrorList;
		this.mainResposneDTO=response;
	}

	public MainResponseDTO<?> getMainResposneDTO() {
		return mainResposneDTO;
	}	
	
}
