package io.mosip.kernel.syncdata.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.syncdata.constant.UserDetailsErrorCode;
import io.mosip.kernel.syncdata.dto.RegistrationCenterUserDto;
import io.mosip.kernel.syncdata.dto.SyncUserDetailDto;
import io.mosip.kernel.syncdata.dto.UserDetailMapDto;
import io.mosip.kernel.syncdata.dto.UserDetailRequestDto;
import io.mosip.kernel.syncdata.dto.response.RegistrationCenterUserResponseDto;
import io.mosip.kernel.syncdata.dto.response.UserDetailResponseDto;
import io.mosip.kernel.syncdata.exception.SyncServiceException;
import io.mosip.kernel.syncdata.exception.ParseResponseException;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;
import io.mosip.kernel.syncdata.service.RegistrationCenterUserService;
import io.mosip.kernel.syncdata.service.SyncUserDetailsService;
import io.mosip.kernel.syncdata.utils.MapperUtils;

/**
 * This class will fetch all user details from the LDAP server through
 * auth-service.
 *
 * @author Srinivasan
 * @author Megha Tanga
 * @since 1.0.0
 */
@RefreshScope
@Service
public class SyncUserDetailsServiceImpl implements SyncUserDetailsService {

	/** The rest template. */
	@Autowired
	RestTemplate restTemplate;

	/** The object mapper. */
	@Autowired
	ObjectMapper objectMapper;

	/** The registration center user service. */
	@Autowired
	RegistrationCenterUserService registrationCenterUserService;

	/** The auth user details base uri. */
	@Value("${mosip.kernel.syncdata.auth-manager-base-uri:https://dev.mosip.io/authmanager/v1.0}")
	private String authUserDetailsBaseUri;

	/** The auth user details uri. */
	@Value("${mosip.kernel.syncdata.auth-user-details:/userdetails}")
	private String authUserDetailsUri;

	/** The sync data request id. */
	@Value("${mosip.kernel.syncdata.syncdata-request-id:SYNCDATA.REQUEST}")
	private String syncDataRequestId;

	/** The sync data version id. */
	@Value("${mosip.kernel.syncdata.syncdata-version-id:v1.0}")
	private String syncDataVersionId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.syncdata.service.SyncUserDetailsService#getAllUserDetail(java
	 * .lang.String)
	 */
	@Override
	public SyncUserDetailDto getAllUserDetail(String regId) {
		StringBuilder userDetailsUri = new StringBuilder();
		userDetailsUri.append(authUserDetailsBaseUri).append(authUserDetailsUri);
		SyncUserDetailDto syncUserDetailDto = null;
		ResponseEntity<String> response = null;
		RegistrationCenterUserResponseDto registrationCenterResponseDto = registrationCenterUserService
				.getUsersBasedOnRegistrationCenterId(regId);
		List<RegistrationCenterUserDto> registrationCenterUserDtos = registrationCenterResponseDto
				.getRegistrationCenterUsers();
		List<String> userIds = registrationCenterUserDtos.stream().map(RegistrationCenterUserDto::getUserId)
				.collect(Collectors.toList());
		HttpEntity<RequestWrapper<?>> userDetailReqEntity = getHttpRequest(userIds);

		try {

			response = restTemplate.postForEntity(userDetailsUri.toString()+ "/registrationclient",
					userDetailReqEntity, String.class);
		} catch (HttpServerErrorException | HttpClientErrorException e) {

			throw new SyncDataServiceException(UserDetailsErrorCode.USER_DETAILS_FETCH_EXCEPTION.getErrorCode(),
					UserDetailsErrorCode.USER_DETAILS_FETCH_EXCEPTION.getErrorMessage(), e);
		}
		String responseBody = response.getBody();
		UserDetailResponseDto userDetailResponseDto = getUserDetailFromResponse(responseBody);
		if (userDetailResponseDto != null && userDetailResponseDto.getMosipUserDtoList() != null) {
			List<UserDetailMapDto> userDetails = MapperUtils
					.mapUserDetailsToUserDetailMap(userDetailResponseDto.getMosipUserDtoList());
			syncUserDetailDto = new SyncUserDetailDto();
			syncUserDetailDto.setUserDetails(userDetails);
		}
		return syncUserDetailDto;

	}

	/**
	 * Gets the http request.
	 *
	 * @param userIds
	 *            the user ids
	 * @return {@link HttpEntity}
	 */
	private HttpEntity<RequestWrapper<?>> getHttpRequest(List<String> userIds) {
		RequestWrapper<UserDetailRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setId(syncDataRequestId);
		requestWrapper.setVersion(syncDataVersionId);
		HttpHeaders syncDataRequestHeaders = new HttpHeaders();
		syncDataRequestHeaders.setContentType(MediaType.APPLICATION_JSON);
		UserDetailRequestDto userDetailsDto = new UserDetailRequestDto();
		userDetailsDto.setUserDetails(userIds);
		requestWrapper.setRequest(userDetailsDto);
		return new HttpEntity<>(requestWrapper, syncDataRequestHeaders);

	}

	/**
	 * Gets the user detail from response.
	 *
	 * @param responseBody
	 *            the response body
	 * @return {@link UserDetailResponseDto}
	 */
	private UserDetailResponseDto getUserDetailFromResponse(String responseBody) {
		List<ServiceError> validationErrorsList = null;
		validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
		UserDetailResponseDto userDetailResponseDto = null;
		if (!validationErrorsList.isEmpty()) {
			throw new SyncServiceException(validationErrorsList);
		}
		ResponseWrapper<UserDetailResponseDto> responseObject = null;
		try {
			
			responseObject = objectMapper.readValue(responseBody,
					new TypeReference<ResponseWrapper<UserDetailResponseDto>>() {
					});
			userDetailResponseDto = responseObject.getResponse();
		} catch (IOException | NullPointerException exception) {
			throw new ParseResponseException(UserDetailsErrorCode.USER_DETAILS_PARSE_ERROR.getErrorCode(),
					UserDetailsErrorCode.USER_DETAILS_PARSE_ERROR.getErrorMessage() + exception.getMessage(),
					exception);
		}

		return userDetailResponseDto;
	}

}
