package io.mosip.kernel.idgenerator.uin.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.idgenerator.uin.constant.UinGeneratorErrorCode;
import io.mosip.kernel.idgenerator.uin.exception.UinNotFoundException;

/**
 * Class for handling API exceptions
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	/**
	 * This method handle MethodArgumentNotValidException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(UinNotFoundException.class)
	public ResponseEntity<ErrorItem> handle(MethodArgumentNotValidException e) {

		ErrorItem error = new ErrorItem();
		error.setMessage(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());
		error.setCode(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

	}

}