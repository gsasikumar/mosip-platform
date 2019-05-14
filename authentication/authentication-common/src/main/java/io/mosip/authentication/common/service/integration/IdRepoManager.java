package io.mosip.authentication.common.service.integration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;

/**
 * 
 * @author Dinesh Karuppiah.T
 * @author Rakesh Roshan
 */

@Component
public class IdRepoManager {

	private static final String ERROR_CODE = "errorCode";

	private static final String ERRORS = "errors";

	private static final String USER_ID_NOTEXIST_ERRORCODE = "KER-ATH-003";

	private static final String USER_ID_NOTEXIST_ERRORMSG = "User Detail doesn't exist";

	private static final String REG_ID = "rid";

	/**
	 * The Constant Id Repo Errors
	 */
	private static final List<String> ID_REPO_ERRORS_INVALID_UIN = Arrays.asList(
			IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode(),
			IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode());

	/**
	 * The Rest Helper
	 */
	@Autowired
	private RestHelper restHelper;

	/**
	 * The Restrequest Factory
	 */
	@Autowired
	private RestRequestFactory restRequestFactory;

	/**
	 * The Environment
	 */
	@Autowired
	private Environment environment;

	/**
	 * Fetch data from Id Repo based on Individual's UIN / VID value and all UIN
	 * 
	 * @param uin
	 * @param isBio
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getIdenity(String uin, boolean isBio) throws IdAuthenticationBusinessException {

		RestRequestDTO buildRequest;
		Map<String, Object> response = null;

		try {
			Map<String, String> params = new HashMap<>();
			params.put("uin", uin);
			if (isBio) {
				buildRequest = restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE, null, Map.class);
				params.put("type", "bio");
			} else {
				buildRequest = restRequestFactory.buildRequest(RestServicesConstants.ID_REPO_SERVICE_WITHOUT_TYPE, null,
						Map.class);
			}
			buildRequest.setPathVariables(params);
			response = restHelper.requestSync(buildRequest);
			if (environment.getProperty(IdRepoConstants.ACTIVE_STATUS.getValue()).equalsIgnoreCase(
					(String) ((Map<String, Object>) response.get("response")).get(IdAuthCommonConstants.STATUS))) {
				response.put("uin", uin);
			} else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UIN_DEACTIVATED);
			}

		} catch (RestServiceException e) {
			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				Map<String, Object> idrepoMap = (Map<String, Object>) responseBody.get();
				if (idrepoMap.containsKey(ERRORS)) {
					List<Map<String, Object>> idRepoerrorList = (List<Map<String, Object>>) idrepoMap.get(ERRORS);
					if (!idRepoerrorList.isEmpty()
							&& idRepoerrorList.stream().anyMatch(map -> map.containsKey("errCode")
									&& ID_REPO_ERRORS_INVALID_UIN.contains(map.get("errCode")))) {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN, e);
					} else {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,
								e);
					}
				}
			}
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} catch (IDDataValidationException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	public String getRIDByUID(String idvId) throws IdAuthenticationBusinessException {
		RestRequestDTO buildRequest = null;
		String rid = null;
		try {
			Map<String, String> params = new HashMap<>();
			params.put("appId", environment.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
			params.put("rid", idvId);
			buildRequest = restRequestFactory.buildRequest(RestServicesConstants.USERID_RID, null, Map.class);

			buildRequest.setPathVariables(params);
			Map<String, Object> ridMap = restHelper.requestSync(buildRequest);
			rid = (String) ((Map<String, Object>) ridMap.get("response")).get("rid");
		} catch (RestServiceException e) {
			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				Map<String, Object> idrepoMap = (Map<String, Object>) responseBody.get();
				if (idrepoMap.containsKey(ERRORS)) {
					List<Map<String, Object>> idRepoerrorList = (List<Map<String, Object>>) idrepoMap.get(ERRORS);
					if (!idRepoerrorList.isEmpty()
							&& idRepoerrorList.stream().anyMatch(map -> map.containsKey(ERROR_CODE)
									&& USER_ID_NOTEXIST_ERRORCODE.equalsIgnoreCase((String) map.get(ERROR_CODE)))) {
						throw new IdAuthenticationBusinessException(USER_ID_NOTEXIST_ERRORCODE,
								USER_ID_NOTEXIST_ERRORMSG);
					} else {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,
								e);
					}
				}
			}
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}

		catch (IDDataValidationException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		}
		return rid;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getUINByRID(String regID) throws IdAuthenticationBusinessException {
		RestRequestDTO buildRequest = null;
		Map<String, Object> uinMap = null;
		try {
			Map<String, String> params = new HashMap<>();
			params.put("rid", regID);
			buildRequest = restRequestFactory.buildRequest(RestServicesConstants.RID_UIN, null, Map.class);
			buildRequest.setPathVariables(params);
			uinMap = restHelper.requestSync(buildRequest);
		} catch (RestServiceException e) {
			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				Map<String, Object> idrepoMap = (Map<String, Object>) responseBody.get();
				if (idrepoMap.containsKey(ERRORS)) {
					List<Map<String, Object>> idRepoerrorList = (List<Map<String, Object>>) idrepoMap.get(ERRORS);

					if (!idRepoerrorList.isEmpty() && idRepoerrorList.stream()
							.anyMatch(map -> map.containsKey(ERROR_CODE) && IdRepoErrorConstants.INVALID_INPUT_PARAMETER
									.getErrorCode().equalsIgnoreCase((String) map.get(ERROR_CODE)))) {
						throw new IdAuthenticationBusinessException(
								IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
								String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
										REG_ID));
					} else {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,
								e);
					}
				}
			}
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} catch (IDDataValidationException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		}
		return uinMap;
	}

}
