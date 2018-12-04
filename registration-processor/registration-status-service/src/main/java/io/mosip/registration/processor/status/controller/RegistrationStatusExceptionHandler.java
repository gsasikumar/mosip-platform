package io.mosip.registration.processor.status.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.registration.processor.status.dto.ExceptionJSONInfo;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;

/**
 * The Class RegistrationStatusExceptionHandler.
 */
@RestControllerAdvice
public class RegistrationStatusExceptionHandler {
	
	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(RegistrationStatusExceptionHandler.class);

	/**
	 * Duplicateentry.
	 *
	 * @param Exception as e
	 * @param WebRequest as request
	 * @return the response entity
	 */
	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<ExceptionJSONInfo> duplicateentry(final TablenotAccessibleException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		log.error(e.getErrorCode(), e.getCause());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ExceptionJSONInfo> dataExceptionHandler(final DataIntegrityViolationException e, WebRequest request) {
		ExceptionJSONInfo exe = new ExceptionJSONInfo( "RPR-DBE-001","Data Integrity Violation Exception");
		log.error( "RPR-DBE-001 Data integrity violation exception",e.getMessage());
		return new ResponseEntity<>(exe, HttpStatus.BAD_REQUEST);
	}
}
