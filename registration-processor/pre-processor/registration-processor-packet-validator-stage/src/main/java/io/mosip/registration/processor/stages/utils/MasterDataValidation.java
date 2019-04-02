package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.web.client.HttpClientErrorException;

import com.google.gson.Gson;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.core.packet.dto.masterdata.StatusResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.ErrorDTO;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;

/**
 * The Class MasterDataValidation.
 * 
 * @author Nagalakshmi
 * 
 */

public class MasterDataValidation {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MasterDataValidation.class);

	/** The registration processor rest service. */
	RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	/** The env. */
	Environment env;

	/** The Constant VALID. */
	private static final String VALID = "Valid";

	/** The demographic identity. */
	JSONObject demographicIdentity = null;

	/** The utility. */
	private Utilities utility;

	/** The Constant VALUE. */
	private static final String VALUE = "value";

	private static final String PRIMARY_LANGUAGE = "primary.language";

	private static final String SECONDARY_LANGUAGE = "secondary.language";

	private static final String ATTRIBUTES = "registration.processor.masterdata.validation.attributes";

	/**
	 * Instantiates a new master data validation.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 * @param env
	 *            the env
	 * @param registrationProcessorRestService
	 *            the registration processor rest service
	 * @param utility
	 *            the utility
	 */
	public MasterDataValidation(Environment env,
			RegistrationProcessorRestClientService<Object> registrationProcessorRestService, Utilities utility) {
		this.env = env;
		this.registrationProcessorRestService = registrationProcessorRestService;
		this.utility = utility;

	}

	/**
	 * Validate master data.
	 *
	 * @param jsonString
	 *            the json string
	 * @return the boolean
	 */
	public Boolean validateMasterData(String jsonString) {
		boolean isValid = false;
		String primaryLanguage = env.getProperty(PRIMARY_LANGUAGE);
		String secondaryLanguage = env.getProperty(SECONDARY_LANGUAGE);
		try {

			demographicIdentity = getDemographicJson(jsonString);

			String[] attributes = env.getProperty(ATTRIBUTES).split(",");
			List<String> list = new ArrayList<>(Arrays.asList(attributes));

			Iterator<String> it = list.iterator();

			while (it.hasNext()) {
				String key = it.next().trim();

				if (env.getProperty(ApiName.valueOf(key.toUpperCase()).name()) != null) {

					String engValue = null;
					String araValue = null;

					Object object = JsonUtil.getJSONValue(demographicIdentity, key);
					if (object instanceof ArrayList) {
						JSONArray node = JsonUtil.getJSONArray(demographicIdentity, key);
						JsonValue[] jsonValues = JsonUtil.mapJsonNodeToJavaObject(JsonValue.class, node);
						engValue = getParameter(jsonValues, primaryLanguage);
						araValue = getParameter(jsonValues, secondaryLanguage);
					} else if (object instanceof LinkedHashMap) {
						JSONObject json = JsonUtil.getJSONObject(demographicIdentity, key);
						engValue = (String) json.get(VALUE);
					} else {
						engValue = (String) object;
					}

					if (validateIdentityValues(key, engValue) && validateIdentityValues(key, araValue)) {
						isValid = true;
					} else {
						isValid = false;
						regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), "",
								PlatformErrorMessages.RPR_PVM_IDENTITY_INVALID.getMessage() + " " + key
										+ "and for values are" + engValue + " " + araValue);

						break;
					}
				} else {
					isValid = false;
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), "",
							PlatformErrorMessages.RPR_PVM_RESOURCE_NOT_FOUND.getMessage() + " " + key);

					break;

				}
			}

		} catch (IdentityNotFoundException | IOException e) {
			isValid = false;
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", PlatformErrorMessages.RPR_PVM_IDENTITY_NOT_FOUND.getMessage() + e.getMessage());

		}

		catch (Exception e) {
			isValid = false;
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage() + e.getMessage());

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"MasterDataValidation::validateMasterData::exit");
		return isValid;

	}

	/**
	 * Validate identity values.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @return true, if successful
	 */
	private boolean validateIdentityValues(String key, String value) {
		StatusResponseDto statusResponseDto;
		boolean isvalidateIdentity = false;
		if (value != null) {
			try {

				List<String> pathsegmentsEng = new ArrayList<>();

				pathsegmentsEng.add(value);

				statusResponseDto = (StatusResponseDto) registrationProcessorRestService
						.getApi(ApiName.valueOf(key.toUpperCase()), pathsegmentsEng, "", "", StatusResponseDto.class);

				if (statusResponseDto.getStatus().equalsIgnoreCase(VALID))
					isvalidateIdentity = true;
			} catch (ApisResourceAccessException ex) {
				if (ex.getCause() instanceof HttpClientErrorException) {
					HttpClientErrorException httpClientException = (HttpClientErrorException) ex.getCause();
					String result = httpClientException.getResponseBodyAsString();
					Gson gsonObj = new Gson();
					statusResponseDto = gsonObj.fromJson(result, StatusResponseDto.class);
					ErrorDTO error = statusResponseDto.getErrors().get(0);
					isvalidateIdentity = false;
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), "",
							PlatformErrorMessages.RPR_PVM_API_RESOUCE_ACCESS_FAILED.getMessage() + ex.getMessage());

				}
			}
		} else {
			isvalidateIdentity = true;
		}
		return isvalidateIdentity;

	}

	/**
	 * Gets the demographic json.
	 *
	 * @param jsonString
	 *            the json string
	 * @return the demographic json
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private JSONObject getDemographicJson(String jsonString) throws IOException {

		JSONObject demographicJson = JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
		demographicIdentity = JsonUtil.getJSONObject(demographicJson, utility.getGetRegProcessorDemographicIdentity());

		if (demographicIdentity == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PVM_IDENTITY_NOT_FOUND.getMessage());

		return demographicIdentity;
	}

	/**
	 * Gets the parameter.
	 *
	 * @param jsonValues
	 *            the json values
	 * @param langCode
	 *            the lang code
	 * @return the parameter
	 */
	private String getParameter(JsonValue[] jsonValues, String langCode) {

		String parameter = null;
		if (jsonValues != null) {
			for (int count = 0; count < jsonValues.length; count++) {
				String lang = jsonValues[count].getLanguage();
				if (langCode.contains(lang)) {
					parameter = jsonValues[count].getValue();
					break;
				}
			}
		}
		return parameter;
	}

}