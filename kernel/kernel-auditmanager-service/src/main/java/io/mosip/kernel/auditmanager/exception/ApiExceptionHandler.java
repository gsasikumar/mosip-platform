package io.mosip.kernel.auditmanager.exception;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.auditmanager.constant.AuditErrorCode;
import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;

/**
 * Class for handling API exceptions
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	private static final String WHITESPACE = " ";

	/**
	 * This method handle MethodArgumentNotValidException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> methodArgumentNotValidException(
			final MethodArgumentNotValidException e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		BindingResult bindingResult = e.getBindingResult();
		final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(AuditErrorCode.HANDLEREXCEPTION.getErrorCode(),
					Character.toUpperCase(x.getField().charAt(0)) + x.getField().substring(1) + WHITESPACE
							+ x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		errorResponse.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);

	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse<ServiceError>> onHttpMessageNotReadable(
			final HttpMessageNotReadableException e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		ServiceError error = new ServiceError(AuditErrorCode.INVALIDFORMAT.getErrorCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ErrorResponse<ServiceError>> defaultErrorHandler(HttpServletRequest request, Exception e) {
		ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
		ServiceError error = new ServiceError(AuditErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}