package io.mosip.authentication.service.integration;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;

@Component
public class MasterDataManager {

	private static final String LANGUAGE_TYPE_REQUIRED = "secondary";

	private static final String NOTIFICATION_LANGUAGE_SUPPORT = "notification.language.support";

	/** The template manager. */
	private TemplateManager templateManager;

	/**
	 * The Template Manager Builder
	 */
	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;

	/**
	 * Environment
	 */
	@Autowired
	private Environment environment;

	/**
	 * The Rest Helper
	 */
	@Autowired
	private RestHelper restHelper;

	/**
	 * The Rest request factory
	 */
	@Autowired
	private RestRequestFactory restFactory;

	private static final String SESSION_ID = "sessionId";

	/**
	 * IdTemplate Manager Logger
	 */
	private static Logger logger = IdaLogger.getLogger(MasterDataManager.class);

	private Map<String, Map<String, String>> fetchMasterData(RestServicesConstants type, Map<String, String> params,
			String masterDataListName, String keyAttribute, String valueAttribute)
			throws IdAuthenticationBusinessException {
		try {
			RestRequestDTO buildRequest = restFactory.buildRequest(type, null, Map.class);
			if (params != null && !params.isEmpty()) {
				buildRequest.setPathVariables(params);
			}
			Map<String, List<Map<String, Object>>> response = restHelper.requestSync(buildRequest);
			List<Map<String, Object>> masterDataList = response.get(masterDataListName);
			Map<String, Map<String, String>> masterDataMap = new HashMap<>();
			for (Map<String, Object> map : masterDataList) {
				String langCode = String.valueOf(map.get("langCode"));
				String key = String.valueOf(map.get(keyAttribute));
				String value = String.valueOf(map.get(valueAttribute));
				Object isActiveObj = map.get("isActive");
				if (isActiveObj instanceof Boolean && (Boolean) isActiveObj) {
					Map<String, String> valueMap = masterDataMap.computeIfAbsent(langCode,
							k -> new LinkedHashMap<String, String>());
					valueMap.put(key, value);
				}
			}
			return masterDataMap;
		} catch (IDDataValidationException | RestServiceException e) {
			logger.error(SESSION_ID, this.getClass().getName(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}

	}

	public String fetchTemplate(String langCode, String templateName) throws IdAuthenticationBusinessException {
		Map<String, String> params = new HashMap<>();
		params.put("langcode", langCode);
		params.put("templatetypecode", templateName);
		Map<String, Map<String, String>> masterData = fetchMasterData(
				RestServicesConstants.ID_MASTERDATA_TEMPLATE_SERVICE, params, "templates", "templateTypeCode",
				"fileText");
		return Optional.ofNullable(masterData.get(langCode)).map(map -> map.get(templateName)).orElse("");
	}

	public Map<String, List<String>> fetchTitles() throws IdAuthenticationBusinessException {
		return  fetchMasterdataList(RestServicesConstants.TITLE_SERVICE,
				"titleList", "code", "titleName");
	}

	public Map<String, List<String>> fetchGenderType() throws IdAuthenticationBusinessException {
		return fetchMasterdataList(RestServicesConstants.GENDER_TYPE_SERVICE,
				"genderType", "code", "genderName");
	}

	private Map<String, List<String>> fetchMasterdataList(RestServicesConstants type, String masterDataName,
			String keyAttribute, String valueAttribute) throws IdAuthenticationBusinessException {
		Map<String, Map<String, String>> fetchMasterData = fetchMasterData(type, null,
				masterDataName, keyAttribute, valueAttribute);
		if (fetchMasterData != null && !fetchMasterData.isEmpty()) {
			return fetchMasterData.entrySet().stream()
					.collect(Collectors.toMap(Entry<String, Map<String, String>>::getKey,
							(Entry<String, Map<String, String>> entry) -> entry.getValue().values().stream()
									.collect(Collectors.toList())));
		}
		return Collections.emptyMap();
	}

}
