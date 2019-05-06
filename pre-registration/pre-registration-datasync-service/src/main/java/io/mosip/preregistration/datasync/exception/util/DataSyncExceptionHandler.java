package io.mosip.preregistration.datasync.exception.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.core.util.GenericUtil;
import io.mosip.preregistration.datasync.exception.DataSyncRecordNotFoundException;
import io.mosip.preregistration.datasync.exception.DemographicGetDetailsException;
import io.mosip.preregistration.datasync.exception.DocumentGetDetailsException;
import io.mosip.preregistration.datasync.exception.RecordNotFoundForDateRange;
import io.mosip.preregistration.datasync.exception.ReverseDataFailedToStoreException;
import io.mosip.preregistration.datasync.exception.ZipFileCreationException;
import io.mosip.preregistration.datasync.exception.system.SystemFileIOException;

/**
 * Exception Handler
 * 
 * @author M1046129
 *
 */
@RestControllerAdvice
public class DataSyncExceptionHandler {
	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * DataSyncRecordNotFoundException Handling
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(DataSyncRecordNotFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> dataSyncRecordNotFound(final DataSyncRecordNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainResponseDto().getId());
		errorRes.setVersion(e.getMainResponseDto().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * ReverseDataFailedToStoreException Handling
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(ReverseDataFailedToStoreException.class)
	public ResponseEntity<MainResponseDTO<?>> reverseDataSyncFailedToStore(final ReverseDataFailedToStoreException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainResponseDto().getId());
		errorRes.setVersion(e.getMainResponseDto().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * RecordNotFoundForDateRange hanlding
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(RecordNotFoundForDateRange.class)
	public ResponseEntity<MainResponseDTO<?>> databaseerror(final RecordNotFoundForDateRange e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainResponseDto().getId());
		errorRes.setVersion(e.getMainResponseDto().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * TablenotAccessibleException handling
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(TableNotAccessibleException.class)
	public ResponseEntity<MainResponseDTO<?>> databaseerror(final TableNotAccessibleException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		// errorRes.setId(e.getMainResponseDto().getId());
		// errorRes.setVersion(e.getMainResponseDto().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * ZipFileCreationException handling
	 * 
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(ZipFileCreationException.class)
	public ResponseEntity<MainResponseDTO<?>> zipNotCreated(final ZipFileCreationException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainResponseDto().getId());
		errorRes.setVersion(e.getMainResponseDto().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(DemographicGetDetailsException.class)
	public ResponseEntity<MainResponseDTO<?>> demogetDetails(final DemographicGetDetailsException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainResponseDto().getId());
		errorRes.setVersion(e.getMainResponseDto().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);

	}

	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(DocumentGetDetailsException.class)
	public ResponseEntity<MainResponseDTO<?>> docGetDetails(final DocumentGetDetailsException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainResponseDto().getId());
		errorRes.setVersion(e.getMainResponseDto().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);

	}

	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<MainResponseDTO<?>> invalidRequestParamCheck(final InvalidRequestParameterException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainResponseDto().getId());
		errorRes.setVersion(e.getMainResponseDto().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);

	}

	/**
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(SystemFileIOException.class)
	public ResponseEntity<MainResponseDTO<?>> fileIOException(final SystemFileIOException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setId(e.getMainResponseDto().getId());
		errorRes.setVersion(e.getMainResponseDto().getVersion());
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);

	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}
}
