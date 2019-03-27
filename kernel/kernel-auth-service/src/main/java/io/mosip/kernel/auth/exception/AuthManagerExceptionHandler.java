/**
 * 
 */
package io.mosip.kernel.auth.exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;

/**
 * @author M1049825
 *
 */
@RestControllerAdvice
public class AuthManagerExceptionHandler {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(HttpServletRequest request, Exception e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(request);
		ServiceError error = new ServiceError("500", e.getMessage());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private ResponseWrapper<ServiceError> setErrors(HttpServletRequest httpServletRequest) throws IOException {
		RequestWrapper<?> requestWrapper = null;
		ResponseWrapper<ServiceError> responseWrapper = new ResponseWrapper<>();
		String requestBody = null;
		if (httpServletRequest instanceof ContentCachingRequestWrapper) {
			requestBody = new String(((ContentCachingRequestWrapper) httpServletRequest).getContentAsByteArray());
		}
		if (requestBody == null) {
			return responseWrapper;
		}
		objectMapper.registerModule(new JavaTimeModule());
		requestWrapper = objectMapper.readValue(requestBody, RequestWrapper.class);
		responseWrapper.setId(requestWrapper.getId());
		responseWrapper.setVersion(requestWrapper.getVersion());
		responseWrapper.setResponsetime(LocalDateTime.now(ZoneId.of("UTC")));
		return responseWrapper;
	}
	
	@ExceptionHandler(value = { AuthManagerException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> customErrorMessage(HttpServletRequest request, AuthManagerException e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(request);
		ServiceError error = new ServiceError(e.getErrorCode(), e.getMessage());
		responseWrapper.getErrors().add(error);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	@ExceptionHandler(value = { AuthManagerServiceException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> customErrorMessageList(HttpServletRequest request, AuthManagerServiceException e) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(request);
		responseWrapper.getErrors().addAll(e.getList());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

}
