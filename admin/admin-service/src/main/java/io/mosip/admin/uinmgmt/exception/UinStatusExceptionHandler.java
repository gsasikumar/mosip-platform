package io.mosip.admin.uinmgmt.exception;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
/**
 * 
 * Rest Controller Advice for Exception Handler
 * 
 * @author Megha Tanga
 *
 */
@RestControllerAdvice
public class UinStatusExceptionHandler {

	@ExceptionHandler(UinStatusException.class)
	public ResponseEntity<ResponseWrapper<?>> handlerError(UinStatusException e) {
		ResponseWrapper<ServiceError> errorResponse = new ResponseWrapper<>();
		ServiceError error = new ServiceError(e.getErrorCode(), e.getErrorText());
		errorResponse.setErrors(Arrays.asList(error));
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}
}
