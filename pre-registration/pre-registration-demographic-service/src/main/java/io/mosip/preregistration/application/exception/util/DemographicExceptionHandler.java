/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception.util;

import java.sql.Timestamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.preregistration.application.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.application.dto.ResponseDTO;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.exception.OperationNotAllowedException;
import io.mosip.preregistration.application.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.system.JsonValidationException;
import io.mosip.preregistration.application.exception.system.SystemIllegalArgumentException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;

/**
 * Exception Handler for demographic service
 * 
 * @author Rajath KR
 * @author Sanober Noor
 * @author Tapaswini Behera
 * @author Jagadishwari S
 * @author Ravi C Balaji
 * @since 1.0.0
 */
@RestControllerAdvice
public class DemographicExceptionHandler {
	protected String falseStatus = "false";

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for TablenotAccessibleException
	 */
	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<ResponseDTO<?>> databaseerror(final TablenotAccessibleException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_APP_002.toString(),
				ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for JsonValidationException
	 */
	@ExceptionHandler(JsonValidationException.class)
	public ResponseEntity<ResponseDTO<?>> jsonValidationException(final JsonValidationException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_PAM_APP_007.toString(),
				e.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for RecordNotFoundException
	 */
	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<ResponseDTO<?>> recException(final RecordNotFoundException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getMessage());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for SystemIllegalArgumentException
	 */
	@ExceptionHandler(SystemIllegalArgumentException.class)
	public ResponseEntity<ResponseDTO<?>> illegalArgumentException(final SystemIllegalArgumentException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for DocumentFailedToDeleteException
	 */
	@ExceptionHandler(DocumentFailedToDeleteException.class)
	public ResponseEntity<ResponseDTO<?>> documentFailedToDeleteException(final DocumentFailedToDeleteException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for RecordFailedToDeleteException
	 */
	@ExceptionHandler(RecordFailedToDeleteException.class)
	public ResponseEntity<ResponseDTO<?>> recordFailedToDeleteException(final RecordFailedToDeleteException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for OperationNotAllowedException
	 */
	@ExceptionHandler(OperationNotAllowedException.class)
	public ResponseEntity<ResponseDTO<?>> operationNotAllowedException(final OperationNotAllowedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
	}

	/**
	 * @param e
	 *            pass the exception
	 * @param request
	 *            pass the request
	 * @return response for InvalidRequestParameterException
	 */
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<ResponseDTO<?>> invalidRequest(final InvalidRequestParameterException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		ResponseDTO<?> errorRes = new ResponseDTO<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(falseStatus);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
