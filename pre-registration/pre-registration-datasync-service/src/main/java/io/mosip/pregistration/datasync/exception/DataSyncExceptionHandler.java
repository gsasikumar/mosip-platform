package io.mosip.pregistration.datasync.exception;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.pregistration.datasync.code.StatusCodes;
import io.mosip.pregistration.datasync.dto.ExceptionJSONInfo;
import io.mosip.pregistration.datasync.dto.ResponseDTO;
import io.mosip.pregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;

/**
 * Exception Handler
 * 
 * @author M1046129
 *
 */
@RestControllerAdvice
public class DataSyncExceptionHandler {

	// @SuppressWarnings({ "rawtypes", "unchecked" })
	// @ExceptionHandler(DocumentNotFoundException.class)
	// public ResponseEntity<ResponseDTO> documentNotFound(final
	// DocumentNotFoundException e, WebRequest request) {
	// ExceptionJSONInfo errorDetails = new
	// ExceptionJSONInfo(ErrorCodes.PRG_DATA_SYNC_006.toString(),
	// StatusCodes.DOCUMENT_IS_MISSING.toString());
	// ResponseDTO responseDto = new ResponseDTO();
	//
	// List<ExceptionJSONInfo> err = new ArrayList<>();
	// responseDto.setStatus("false");
	// err.add(errorDetails);
	// responseDto.setErr(err);
	// responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
	// System.out.println("responseDto::" + responseDto);
	// return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	// }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(DataSyncRecordNotFoundException.class)
	public ResponseEntity<ResponseDTO> dataSyncRecordNotFound(final DataSyncRecordNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_DATA_SYNC_004.toString(),
				StatusCodes.RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID.toString());

		ResponseDTO responseDto = new ResponseDTO();

		List<ExceptionJSONInfo> err = new ArrayList<>();
		responseDto.setStatus("false");
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(ReverseDataSyncRecordNotFoundException.class)
	public ResponseEntity<ResponseDTO> reverseDataSyncRecordNotFound(final ReverseDataSyncRecordNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_REVESE_DATA_SYNC_001.toString(),
				StatusCodes.FAILED_TO_STORE_PRE_REGISTRATION_IDS.toString());

		ResponseDTO responseDto = new ResponseDTO();

		List<ExceptionJSONInfo> err = new ArrayList<>();
		responseDto.setStatus("false");
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(RecordNotFoundForDateRange.class)
	public ResponseEntity<ResponseDTO> databaseerror(final RecordNotFoundForDateRange e, WebRequest request) {
		ArrayList<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_DATA_SYNC_001.toString(),
				StatusCodes.RECORDS_NOT_FOUND_FOR_DATE_RANGE.toString());
		err.add(errorDetails);
		ResponseDTO errorRes = new ResponseDTO();
		errorRes.setErr(err);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<ResponseDTO> databaseerror(final TablenotAccessibleException e, WebRequest request) {
		ArrayList<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_PAM_APP_002.toString(),
				StatusCodes.REGISTRATION_TABLE_NOT_ACCESSIBLE.toString());
		err.add(errorDetails);
		ResponseDTO errorRes = new ResponseDTO();
		errorRes.setErr(err);
		errorRes.setStatus("false");
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
