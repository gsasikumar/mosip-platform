package io.mosip.kernel.idrepo.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.ServletException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idrepo.constant.IdRepoConstants;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppUncheckedException;
import io.mosip.kernel.core.idrepo.exception.IdRepoUnknownException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.idrepo.config.IdRepoLogger;
import io.mosip.kernel.idrepo.dto.ErrorDTO;
import io.mosip.kernel.idrepo.dto.IdResponseDTO;

/**
 * The Class IdRepoExceptionHandler.
 *
 * @author Manoj SP
 */
@RestControllerAdvice
public class IdRepoExceptionHandler extends ResponseEntityExceptionHandler {

	/** The Constant ID_REPO_EXCEPTION_HANDLER. */
	private static final String ID_REPO_EXCEPTION_HANDLER = "IdRepoExceptionHandler";

	/** The Constant ID_REPO. */
	private static final String ID_REPO = "IdRepo";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";
	
	/** The Constant READ. */
	private static final String READ = "read";

	/** The Constant CREATE. */
	private static final String CREATE = "create";

	/** The Constant UPDATE. */
	private static final String UPDATE = "update";

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(IdRepoExceptionHandler.class);

	/** The env. */
	@Autowired
	private Environment env;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;
	
	@Resource
	private Map<String, String> id;

	/**
	 * Handle all exceptions.
	 *
	 * @param ex
	 *            the ex
	 * @param request
	 *            the request
	 * @return the response entity
	 */
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		mosipLogger.error(IdRepoLogger.getUin(), ID_REPO, ID_REPO_EXCEPTION_HANDLER,
				"handleAllExceptions - \n" + ExceptionUtils.getStackTrace(ex));
		IdRepoUnknownException e = new IdRepoUnknownException(IdRepoErrorConstants.UNKNOWN_ERROR);
		return new ResponseEntity<>(
				buildExceptionResponse((BaseCheckedException) e, ((ServletWebRequest) request).getHttpMethod()),
				HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.method.annotation.
	 * ResponseEntityExceptionHandler#handleExceptionInternal(java.lang.Exception,
	 * java.lang.Object, org.springframework.http.HttpHeaders,
	 * org.springframework.http.HttpStatus,
	 * org.springframework.web.context.request.WebRequest)
	 */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object errorMessage,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		mosipLogger.error(IdRepoLogger.getUin(), ID_REPO, ID_REPO_EXCEPTION_HANDLER,
				"handleExceptionInternal - \n" + ExceptionUtils.getStackTrace(ex));
		if (ex instanceof ServletException || ex instanceof BeansException
				|| ex instanceof HttpMessageConversionException) {
			ex = new IdRepoAppException(IdRepoErrorConstants.INVALID_REQUEST.getErrorCode(),
					IdRepoErrorConstants.INVALID_REQUEST.getErrorMessage());

			return new ResponseEntity<>(buildExceptionResponse(ex, ((ServletWebRequest) request).getHttpMethod()),
					HttpStatus.OK);
		} else {
			return handleAllExceptions(ex, request);
		}
	}

	/**
	 * Handle id app exception.
	 *
	 * @param ex
	 *            the ex
	 * @param request
	 *            the request
	 * @return the response entity
	 */
	@ExceptionHandler(IdRepoAppException.class)
	protected ResponseEntity<Object> handleIdAppException(IdRepoAppException ex, WebRequest request) {

		mosipLogger.error(IdRepoLogger.getUin(), ID_REPO, ID_REPO_EXCEPTION_HANDLER,
				"handleIdAppException - \n" + ExceptionUtils.getStackTrace(ex));

		return new ResponseEntity<>(
				buildExceptionResponse((Exception) ex, ((ServletWebRequest) request).getHttpMethod()), HttpStatus.OK);
	}

	/**
	 * Handle id app unchecked exception.
	 *
	 * @param ex
	 *            the ex
	 * @param request
	 *            the request
	 * @return the response entity
	 */
	@ExceptionHandler(IdRepoAppUncheckedException.class)
	protected ResponseEntity<Object> handleIdAppUncheckedException(IdRepoAppUncheckedException ex, WebRequest request) {

		mosipLogger.error(IdRepoLogger.getUin(), ID_REPO, ID_REPO_EXCEPTION_HANDLER,
				"handleIdAppUncheckedException - \n" + ExceptionUtils.getStackTrace(ex));

		return new ResponseEntity<>(
				buildExceptionResponse((Exception) ex, ((ServletWebRequest) request).getHttpMethod()), HttpStatus.OK);
	}

	/**
	 * Constructs exception response body for all exceptions.
	 *
	 * @param ex
	 *            the exception occurred
	 * @param httpMethod 
	 * @return Object .
	 */
	private Object buildExceptionResponse(Exception ex, HttpMethod httpMethod) {

		IdResponseDTO response = new IdResponseDTO();

		Throwable e = getRootCause(ex);
		
		if (httpMethod.compareTo(HttpMethod.GET) == 0) {
			response.setId(id.get(READ));
		} else if (httpMethod.compareTo(HttpMethod.POST) == 0) {
			response.setId(id.get(CREATE));
		} else if (httpMethod.compareTo(HttpMethod.PATCH) == 0) {
			response.setId(id.get(UPDATE));
		}

		if (e instanceof BaseCheckedException) {
			List<String> errorCodes = ((BaseCheckedException) e).getCodes();
			List<String> errorTexts = ((BaseCheckedException) e).getErrorTexts();

			List<ErrorDTO> errors = errorTexts.parallelStream()
					.map(errMsg -> new ErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setErrors(errors);
		}

		if (e instanceof BaseUncheckedException) {
			List<String> errorCodes = ((BaseUncheckedException) e).getCodes();
			List<String> errorTexts = ((BaseUncheckedException) e).getErrorTexts();

			List<ErrorDTO> errors = errorTexts.parallelStream()
					.map(errMsg -> new ErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setErrors(errors);
		}

		response.setTimestamp(
				DateUtils.getUTCCurrentDateTimeString(env.getProperty(IdRepoConstants.DATETIME_PATTERN.getValue())));

		response.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));

		mapper.setFilterProvider(new SimpleFilterProvider().addFilter("responseFilter",
				SimpleBeanPropertyFilter.serializeAllExcept("registrationId", "status", "response")));

		return response;
	}

	/**
	 * Gets the root cause.
	 *
	 * @param ex the ex
	 * @param response the response
	 * @return the root cause
	 */
	private Throwable getRootCause(Exception ex) {
		Throwable e = ex;
		while (e != null) {
			if (Objects.nonNull(e.getCause()) && (e.getCause() instanceof IdRepoAppException
					|| e.getCause() instanceof IdRepoAppUncheckedException)) {
				e = e.getCause();
			} else {
				break;
			}
		}
		return e;
	}
}
