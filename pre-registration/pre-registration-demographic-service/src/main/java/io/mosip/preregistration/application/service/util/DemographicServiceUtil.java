/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.service.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.application.code.RequestCodes;
import io.mosip.preregistration.application.code.StatusCodes;
import io.mosip.preregistration.application.dto.CreateDemographicDTO;
import io.mosip.preregistration.application.dto.MainRequestDTO;
import io.mosip.preregistration.application.entity.DemographicEntity;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.MissingRequestParameterException;
import io.mosip.preregistration.application.exception.OperationNotAllowedException;
import io.mosip.preregistration.application.exception.system.DateParseException;
import io.mosip.preregistration.application.exception.system.JsonParseException;
import io.mosip.preregistration.application.exception.system.SystemUnsupportedEncodingException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;

/**
 * This class provides the utility methods for DemographicService
 *  
 * @author Ravi C Balaji
 * @since 1.0.0
 */
@Component
public class DemographicServiceUtil {

	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	/**
	 * This setter method is used to assign the initial demographic entity values to
	 * the createDTO
	 * 
	 * @param demographicEntity 
	 * 						pass the demographicEntity
	 * @return createDTO with the values
	 */
	public CreateDemographicDTO setterForCreateDTO(DemographicEntity demographicEntity) {
		JSONParser jsonParser = new JSONParser();
		CreateDemographicDTO createDto = new CreateDemographicDTO();
		try {
			createDto.setPreRegistrationId(demographicEntity.getPreRegistrationId());
			createDto.setDemographicDetails((JSONObject) jsonParser
					.parse(new String(demographicEntity.getApplicantDetailJson(), StandardCharsets.UTF_8)));
			createDto.setStatusCode(demographicEntity.getStatusCode());
			createDto.setLangCode(demographicEntity.getLangCode());
			createDto.setCreatedBy(demographicEntity.getCreatedBy());
			createDto.setCreatedDateTime(getLocalDateString(demographicEntity.getCreateDateTime()));
			createDto.setUpdatedBy(demographicEntity.getUpdatedBy());
			createDto.setUpdatedDateTime(getLocalDateString(demographicEntity.getUpdateDateTime()));
		} catch (ParseException e) {
			throw new JsonParseException(ErrorCodes.PRG_PAM_APP_007.toString(),
					ErrorMessages.JSON_PARSING_FAILED.toString(), e.getCause());
		}
		return createDto;
	}

	/**
	 * This method is used to set the values from the request to the
	 * demographicEntity entity fields.
	 * 
	 * @param demographicRequest 
	 * 					pass demographicRequest
	 * @param requestId
	 * 				pass requestId
	 * @param entityType
	 * 				pass entityType
	 * @return demographic entity with values
	 */
	public DemographicEntity prepareDemographicEntity(CreateDemographicDTO demographicRequest, String requestId,
			String entityType) {
		DemographicEntity demographicEntity = new DemographicEntity();
		demographicEntity.setPreRegistrationId(demographicRequest.getPreRegistrationId());
		demographicEntity.setGroupId("1234567890");
		demographicEntity.setApplicantDetailJson(
				demographicRequest.getDemographicDetails().toJSONString().getBytes(StandardCharsets.UTF_8));
		demographicEntity.setStatusCode(demographicRequest.getStatusCode());
		demographicEntity.setLangCode(demographicRequest.getLangCode());
		demographicEntity.setCrAppuserId(requestId);
		try {
			if (entityType.equals("save")) {
				if (!isNull(demographicRequest.getCreatedBy()) && !isNull(demographicRequest.getCreatedDateTime())
						&& isNull(demographicRequest.getUpdatedBy()) && isNull(demographicEntity.getUpdateDateTime())) {
					demographicEntity.setCreatedBy(demographicRequest.getCreatedBy());
					demographicEntity
							.setCreateDateTime(DateUtils.parseDateToLocalDateTime(getDateFromString(demographicRequest.getCreatedDateTime())));
					
					demographicEntity.setUpdatedBy(null);
					demographicEntity
							.setUpdateDateTime(DateUtils.parseDateToLocalDateTime(getDateFromString(demographicRequest.getCreatedDateTime())));
				} else {
					throw new InvalidRequestParameterException(ErrorCodes.PRG_PAM_APP_012.toString(),
							ErrorMessages.MISSING_REQUEST_PARAMETER.toString());
				}
			} else if (entityType.equals("update")) {
				if (!isNull(demographicRequest.getCreatedBy()) && !isNull(demographicRequest.getCreatedDateTime())
						&& !isNull(demographicRequest.getUpdatedBy())
						&& !isNull(demographicRequest.getUpdatedDateTime())) {
					demographicEntity.setCreatedBy(demographicRequest.getCreatedBy());
					demographicEntity
							.setCreateDateTime(DateUtils.parseDateToLocalDateTime(getDateFromString(demographicRequest.getCreatedDateTime())));
					demographicEntity.setUpdatedBy(demographicRequest.getUpdatedBy());
					demographicEntity
							.setUpdateDateTime(DateUtils.parseDateToLocalDateTime(getDateFromString(demographicRequest.getUpdatedDateTime())));
				} else {
					throw new InvalidRequestParameterException(ErrorCodes.PRG_PAM_APP_012.toString(),
							ErrorMessages.MISSING_REQUEST_PARAMETER.toString());
				}
			}
		} catch (NullPointerException e) {
			throw new MissingRequestParameterException(ErrorCodes.PRG_PAM_APP_012.toString(),
					ErrorMessages.MISSING_REQUEST_PARAMETER.toString());
		}
		return demographicEntity;
	}

	/**
	 * This method is used to add the initial request values into a map for input
	 * validations.
	 * 
	 * @param demographicRequestDTO 
	 * 						pass demographicRequestDTO
	 * @return a map for request input validation
	 */
	public Map<String, String> prepareRequestParamMap(
			MainRequestDTO<CreateDemographicDTO> demographicRequestDTO) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.id.toString(), demographicRequestDTO.getId());
		inputValidation.put(RequestCodes.ver.toString(), demographicRequestDTO.getVer());
		inputValidation.put(RequestCodes.reqTime.toString(),
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(demographicRequestDTO.getReqTime()));
		inputValidation.put(RequestCodes.request.toString(), demographicRequestDTO.getRequest().toString());
		return inputValidation;
	}

	/**
	 * This method is used to set the JSON values to RequestCodes constants.
	 * 
	 * @param demographicData
	 * 					pass demographicData
	 * @param identityKey
	 * 					pass identityKey
	 * @return values from JSON
	 * 					
	 * @throws ParseException On json Parsing Failed
	 * 					
	 */
	public String getValueFromIdentity(byte[] demographicData, String identityKey) throws ParseException {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObj = (JSONObject) jsonParser.parse(new String(demographicData, StandardCharsets.UTF_8));
		JSONObject identityObj = (JSONObject) jsonObj.get(RequestCodes.identity.toString());
		JSONArray keyArr = (JSONArray) identityObj.get(identityKey);
		JSONObject valueObj = (JSONObject) keyArr.get(0);
		return valueObj.get(RequestCodes.value.toString()).toString();
	}

	/**
	 * This method is used as Null checker for different input keys.
	 *
	 * @param key 
	 * 			pass the key
	 * @return true if key not null and return false if key is null.
	 */
	public boolean isNull(Object key) {
		if (key instanceof String) {
			if (key.equals(""))
				return true;
		} else if (key instanceof List<?>) {
			if (((List<?>) key).isEmpty())
				return true;
		} else {
			if (key == null)
				return true;
		}
		return false;

	}

	/**
	 * This method is used to validate Pending_Appointment and Booked status codes.
	 * 
	 * @param statusCode pass statusCode
	 * @return true or false
	 */
	public boolean checkStatusForDeletion(String statusCode) {
		if (statusCode.equals(StatusCodes.Pending_Appointment.name()) || statusCode.equals(StatusCodes.Booked.name())) {
			return true;
		} else {
			throw new OperationNotAllowedException(ErrorCodes.PRG_PAM_APP_003.name(),
					ErrorMessages.DELETE_OPERATION_NOT_ALLOWED.name());
		}
	}

	/**
	 * This method is used for parsing and formatting the fromDate and toDate
	 * 
	 * @param dateMap
	 * 				pass dateMap
	 * @param format
	 * 				pass Date format
	 * @return map with formatted fromDate and toDate
	 */
	public Map<String, LocalDateTime> dateSetter(Map<String, String> dateMap, String format) {
		Map<String, LocalDateTime> localDateTimeMap = new HashMap<>();
		try {

			Date fromDate = DateUtils.parseToDate(URLDecoder.decode(dateMap.get(RequestCodes.fromDate.toString()), "UTF-8"),
					format);

			Date toDate = null;
			if (dateMap.get(RequestCodes.toDate.toString()) == null
					|| isNull(dateMap.get(RequestCodes.toDate.toString()))) {
				toDate = fromDate;
				Calendar cal = Calendar.getInstance();
				cal.setTime(toDate);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				toDate = cal.getTime();
			} else {
				toDate = DateUtils.parseToDate(URLDecoder.decode(dateMap.get(RequestCodes.toDate.toString()), "UTF-8"),
						format);
			}
			localDateTimeMap.put(RequestCodes.fromDate.toString(), DateUtils.parseDateToLocalDateTime(fromDate));
			localDateTimeMap.put(RequestCodes.toDate.toString(), DateUtils.parseDateToLocalDateTime(toDate));

		} catch (java.text.ParseException e) {
			throw new DateParseException(ErrorCodes.PRG_PAM_APP_011.toString(),
					ErrorMessages.UNSUPPORTED_DATE_FORMAT.toString(), e.getCause());
		} catch (UnsupportedEncodingException e) {
			throw new SystemUnsupportedEncodingException(ErrorCodes.PRG_PAM_APP_009.toString(),
					ErrorMessages.UNSUPPORTED_ENCODING_CHARSET.toString(), e.getCause());
		}
		return localDateTimeMap;
	}
	
	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}
	
	public Date getDateFromString(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(date);
		} catch (java.text.ParseException e) {
			throw new DateParseException(ErrorCodes.PRG_PAM_APP_011.toString(),
					ErrorMessages.UNSUPPORTED_DATE_FORMAT.toString(), e.getCause());
		}
	}
	
	public String getLocalDateString(LocalDateTime date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
		return date.format(dateTimeFormatter);
	}
}
