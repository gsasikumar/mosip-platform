/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.service.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.application.code.RequestCodes;
import io.mosip.preregistration.application.dto.DemographicRequestDTO;
import io.mosip.preregistration.application.entity.DemographicEntity;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.MissingRequestParameterException;
import io.mosip.preregistration.application.exception.OperationNotAllowedException;
import io.mosip.preregistration.application.exception.system.DateParseException;
import io.mosip.preregistration.application.exception.system.JsonParseException;
import io.mosip.preregistration.application.exception.system.SystemUnsupportedEncodingException;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.CryptoUtil;
import io.mosip.preregistration.core.util.HashUtill;

/**
 * This class provides the utility methods for DemographicService
 * 
 * @author Ravi C Balaji
 * @author Sanober Noor
 * @since 1.0.0
 */
@Component
public class DemographicServiceUtil {

	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * Logger instance
	 */
	private Logger log = LoggerConfiguration.logConfig(DemographicServiceUtil.class);

	@Autowired
	CryptoUtil cryptoUtil;
	


	/**
	 * This setter method is used to assign the initial demographic entity values to
	 * the createDTO
	 * 
	 * @param demographicEntity
	 *            pass the demographicEntity
	 * @return createDTO with the values
	 */
	public DemographicResponseDTO setterForCreateDTO(DemographicEntity demographicEntity) {
		log.info("sessionId", "idType", "id", "In setterForCreateDTO method of pre-registration service util");
		JSONParser jsonParser = new JSONParser();
		DemographicResponseDTO createDto = new DemographicResponseDTO();
		try {
			createDto.setPreRegistrationId(demographicEntity.getPreRegistrationId());
			createDto.setDemographicDetails((JSONObject) jsonParser.parse(new String(cryptoUtil
					.decrypt(demographicEntity.getApplicantDetailJson(), demographicEntity.getEncryptedDateTime()))));
			createDto.setStatusCode(demographicEntity.getStatusCode());
			createDto.setLangCode(demographicEntity.getLangCode());
			createDto.setCreatedBy(demographicEntity.getCreatedBy());
			createDto.setCreatedDateTime(getLocalDateString(demographicEntity.getCreateDateTime()));
			createDto.setUpdatedBy(demographicEntity.getUpdatedBy());
			createDto.setUpdatedDateTime(getLocalDateString(demographicEntity.getUpdateDateTime()));
		} catch (ParseException ex) {
			log.error("sessionId", "idType", "id",
					"In setterForCreateDTO method of pre-registration service- " + ex.getMessage());
			throw new JsonParseException(ErrorCodes.PRG_PAM_APP_007.toString(),
					ErrorMessages.JSON_PARSING_FAILED.toString(), ex.getCause());
		}
		return createDto;
	}

	/**
	 * This method is used to set the values from the request to the
	 * demographicEntity entity fields.
	 * 
	 * @param demographicRequest
	 *            pass demographicRequest
	 * @param requestId
	 *            pass requestId
	 * @param entityType
	 *            pass entityType
	 * @return demographic entity with values
	 */
	public DemographicEntity prepareDemographicEntity(DemographicRequestDTO demographicRequest, String requestId,
			String entityType, String statuscode) {
		log.info("sessionId", "idType", "id", "In prepareDemographicEntity method of pre-registration service util");
		DemographicEntity demographicEntity = new DemographicEntity();
		demographicEntity.setPreRegistrationId(demographicRequest.getPreRegistrationId());
		LocalDateTime encryptionDateTime = DateUtils.getUTCCurrentDateTime();
		byte[] encryptedDemographicDetails = cryptoUtil
				.encrypt(demographicRequest.getDemographicDetails().toJSONString().getBytes(), encryptionDateTime);
		demographicEntity.setApplicantDetailJson(encryptedDemographicDetails);
		demographicEntity.setLangCode(demographicRequest.getLangCode());
		demographicEntity.setCrAppuserId(requestId);
		demographicEntity.setCreatedBy(demographicRequest.getCreatedBy());
		demographicEntity.setCreateDateTime(DateUtils
				.parseDateToLocalDateTime(getDateFromString(demographicRequest.getCreatedDateTime())));
		demographicEntity.setStatusCode(statuscode);
		demographicEntity.setDemogDetailHash(new String(HashUtill.hashUtill(demographicEntity.getApplicantDetailJson())));
		try {
			if (entityType.equals(RequestCodes.SAVE.getCode())) {
				if (!isNull(demographicRequest.getCreatedBy()) && !isNull(demographicRequest.getCreatedDateTime())
						&& isNull(demographicRequest.getUpdatedBy()) && isNull(demographicEntity.getUpdateDateTime())) {
					
					demographicEntity.setUpdatedBy(null);
					demographicEntity.setUpdateDateTime(DateUtils
							.parseDateToLocalDateTime(getDateFromString(demographicRequest.getCreatedDateTime())));
					demographicEntity.setEncryptedDateTime(encryptionDateTime);
					
				} else {
					throw new InvalidRequestParameterException(ErrorCodes.PRG_PAM_APP_012.toString(),
							ErrorMessages.MISSING_REQUEST_PARAMETER.toString());
				}
			} else if (entityType.equals(RequestCodes.UPDATE.getCode())) {
				if (!isNull(demographicRequest.getCreatedBy()) && !isNull(demographicRequest.getCreatedDateTime())
						&& !isNull(demographicRequest.getUpdatedBy())
						&& !isNull(demographicRequest.getUpdatedDateTime())) {
					demographicEntity.setUpdatedBy(demographicRequest.getUpdatedBy());
					demographicEntity.setUpdateDateTime(DateUtils
							.parseDateToLocalDateTime(getDateFromString(demographicRequest.getUpdatedDateTime())));
					demographicEntity.setEncryptedDateTime(encryptionDateTime);
					
					
				} else {
					throw new InvalidRequestParameterException(ErrorCodes.PRG_PAM_APP_012.toString(),
							ErrorMessages.MISSING_REQUEST_PARAMETER.toString());
				}
			}
		} catch (NullPointerException ex) {
			log.error("sessionId", "idType", "id",
					"In prepareDemographicEntity method of pre-registration service- " + ex.getCause());
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
	 *            pass demographicRequestDTO
	 * @return a map for request input validation
	 */
	public Map<String, String> prepareRequestParamMap(MainRequestDTO<DemographicRequestDTO> demographicRequestDTO) {
		log.info("sessionId", "idType", "id", "In prepareRequestParamMap method of pre-registration service util");
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.ID.getCode(), demographicRequestDTO.getId());
		inputValidation.put(RequestCodes.VER.getCode(), demographicRequestDTO.getVer());
		inputValidation.put(RequestCodes.REQ_TIME.getCode(),
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(demographicRequestDTO.getReqTime()));
		inputValidation.put(RequestCodes.REQUEST.getCode(), demographicRequestDTO.getRequest().toString());
		return inputValidation;
	}

	/**
	 * This method is used to set the JSON values to RequestCodes constants.
	 * 
	 * @param demographicData
	 *            pass demographicData
	 * @param identityKey
	 *            pass identityKey
	 * @return values from JSON
	 * 
	 * @throws ParseException
	 *             On json Parsing Failed
	 * 
	 */
	public JSONArray getValueFromIdentity(byte[] demographicData, String identityKey) throws ParseException {
		log.info("sessionId", "idType", "id", "In getValueFromIdentity method of pre-registration service util ");
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObj = (JSONObject) jsonParser.parse(new String(demographicData));
		JSONObject identityObj = (JSONObject) jsonObj.get(RequestCodes.IDENTITY.getCode());
		return (JSONArray) identityObj.get(identityKey);
	}
	
	/**
	 * This method is used to set the JSON values to RequestCodes constants.
	 * 
	 * @param demographicData
	 *            pass demographicData
	 * @param identityKey
	 *            pass postalcode
	 * @return values from JSON
	 * 
	 * @throws ParseException
	 *             On json Parsing Failed
	 * 
	 */
	public String getPostalCode(byte[] demographicData, String postalcode) throws ParseException {
		log.info("sessionId", "idType", "id", "In getValueFromIdentity method of pre-registration service util ");
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObj = (JSONObject) jsonParser.parse(new String(demographicData));
		JSONObject identityObj = (JSONObject) jsonObj.get(RequestCodes.IDENTITY.getCode());
		return  (String) identityObj.get(postalcode);
	}

	/**
	 * This method is used as Null checker for different input keys.
	 *
	 * @param key
	 *            pass the key
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
	 * @param statusCode
	 *            pass statusCode
	 * @return true or false
	 */
	public boolean checkStatusForDeletion(String statusCode) {
		log.info("sessionId", "idType", "id", "In checkStatusForDeletion method of pre-registration service util ");
		if (statusCode.equals(StatusCodes.PENDING_APPOINTMENT.getCode())
				|| statusCode.equals(StatusCodes.BOOKED.getCode())) {
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
	 *            pass dateMap
	 * @param format
	 *            pass Date format
	 * @return map with formatted fromDate and toDate
	 */
	public Map<String, LocalDateTime> dateSetter(Map<String, String> dateMap, String format) {
		log.info("sessionId", "idType", "id", "In dateSetter method of pre-registration service util ");
		Map<String, LocalDateTime> localDateTimeMap = new HashMap<>();
		try {
			  
			Date fromDate = DateUtils
					.parseToDate(URLDecoder.decode(dateMap.get(RequestCodes.FROM_DATE.getCode()), "UTF-8"), format);
          
			Date toDate;
			if (dateMap.get(RequestCodes.TO_DATE.getCode()) == null
					|| isNull(dateMap.get(RequestCodes.TO_DATE.getCode()))) {
				toDate = fromDate;
				Calendar cal = Calendar.getInstance();
				cal.setTime(toDate);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				toDate = cal.getTime();
			} else {
				toDate = DateUtils.parseToDate(URLDecoder.decode(dateMap.get(RequestCodes.TO_DATE.getCode()), "UTF-8"),
						format);
			}
			localDateTimeMap.put(RequestCodes.FROM_DATE.getCode(), DateUtils.parseDateToLocalDateTime(fromDate));
			localDateTimeMap.put(RequestCodes.TO_DATE.getCode(), DateUtils.parseDateToLocalDateTime(toDate));

		} catch (java.text.ParseException | io.mosip.kernel.core.exception.ParseException ex) {
			log.error("sessionId", "idType", "id",
					"In dateSetter method of pre-registration service- " + ex.getCause());
			throw new DateParseException(ErrorCodes.PRG_PAM_APP_011.toString(),
					ErrorMessages.UNSUPPORTED_DATE_FORMAT.toString(), ex.getCause());
		} catch (UnsupportedEncodingException ex) {
			log.error("sessionId", "idType", "id",
					"In dateSetter method of pre-registration service- " + ex.getCause());
			throw new SystemUnsupportedEncodingException(ErrorCodes.PRG_PAM_APP_009.toString(),
					ErrorMessages.UNSUPPORTED_ENCODING_CHARSET.toString(), ex.getCause());
		}
		return localDateTimeMap;
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}

	public Date getDateFromString(String date) {
		log.info("sessionId", "idType", "id", "In getDateFromString method of pre-registration service util ");
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(date);
		} catch (java.text.ParseException ex) {
			log.error("sessionId", "idType", "id",
					"In getDateFromString method of pre-registration service- " + ex.getCause());
			throw new DateParseException(ErrorCodes.PRG_PAM_APP_011.toString(),
					ErrorMessages.UNSUPPORTED_DATE_FORMAT.toString(), ex.getCause());
		}
	}

	public String getLocalDateString(LocalDateTime date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
		return date.format(dateTimeFormatter);
	}

	public boolean isStatusValid(String status) {
		for (StatusCodes choice : StatusCodes.values())
			if (choice.getCode().equals(status))
				return true;
		return false;
	}


}
