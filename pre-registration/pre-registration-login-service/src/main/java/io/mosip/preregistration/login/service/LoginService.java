package io.mosip.preregistration.login.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class provides different methods for login called by the controller 
 * 
 * @author M1050360
 * @since 1.0.0
 */

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.RequestWrapper;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.core.util.GenericUtil;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.login.dto.OtpRequestDTO;
import io.mosip.preregistration.login.dto.OtpUser;
import io.mosip.preregistration.login.dto.User;
import io.mosip.preregistration.login.dto.UserOtp;
import io.mosip.preregistration.login.errorcodes.ErrorCodes;
import io.mosip.preregistration.login.errorcodes.ErrorMessages;
import io.mosip.preregistration.login.exception.ConfigFileNotFoundException;
import io.mosip.preregistration.login.exception.InvalidOtpOrUseridException;
import io.mosip.preregistration.login.exception.LoginServiceException;
import io.mosip.preregistration.login.exception.NoAuthTokenException;
import io.mosip.preregistration.login.exception.util.LoginExceptionCatcher;
import io.mosip.preregistration.login.util.LoginCommonUtil;

@Service
public class LoginService {

	private Logger log = LoggerConfiguration.logConfig(LoginService.class);
	
	/**
	 * Autowired reference for {@link #authCommonUtil}
	 */
	@Autowired
	private LoginCommonUtil loginCommonUtil;
	
	@Value("${global.config.file}")
	private String globalFileName;

	@Value("${pre.reg.config.file}")
	private String preRegFileName;

	@Value("${ui.config.params}")
	private String uiConfigParams;
	
	@Value("${mosip.preregistration.sendotp.id}")
	private String sendOtpId;
	
	@Value("${mosip.preregistration.validateotp.id}")
	private String userIdOtpId;
	
	@Value("${mosip.preregistration.invalidatetoken.id}")
	private String invalidateTokenId;
	
	@Value("${mosip.preregistration.config.id}")
	private String configId;
	
	@Value("${mosip.preregistration.login.service.version}")
	private String version;
	/**
	 * Reference for ${sendOtp.resource.url} from property file
	 */
	@Value("${sendOtp.resource.url}")
	private String sendOtpResourceUrl;
	
	@Value("${validationStatus}")
	private String status;
	
	private List<String> otpChannel;
	
	@Value("${userIdType}")
	private String useridtype;
	
	@Value("${appId}")
	private String appId;
	
	@Value("${context}")
	private String context;
	
	@Autowired
	AuditLogUtil auditLogUtil;
	
	Map<String, String> requiredRequestMap = new HashMap<>();
	
	@PostConstruct
	public void setupLoginService() {
		requiredRequestMap.put("version", version);

	}
	/**
	 * Reference for ${mosip.prereg.app-id} from property file
	 */
//	@Value("${mosip.prereg.app-id}")
//	private String appId;
	
	
	/**
	 * It will fetch otp from Kernel auth service  and send to the userId provided
	 * 
	 * @param userOtpRequest
	 * @return MainResponseDTO<AuthNResponse>
	 */
	public MainResponseDTO<AuthNResponse> sendOTP(MainRequestDTO<OtpRequestDTO> userOtpRequest) {
		log.info("sessionId", "idType", "id",
				"In callsendOtp method of login service ");
		MainResponseDTO<AuthNResponse> response  = null;
		OtpRequestDTO otp=userOtpRequest.getRequest();
		requiredRequestMap.put("id",sendOtpId);
		response  =	(MainResponseDTO<AuthNResponse>) loginCommonUtil.getMainResponseDto(userOtpRequest);
		
		try {
			if(ValidationUtil.requestValidator(loginCommonUtil.createRequestMap(userOtpRequest),requiredRequestMap)/*authCommonUtil.validateRequest(userOtpRequest)*/) {
				
				
				otpChannel=loginCommonUtil.validateUserId(otp.getUserId());
				OtpUser user=new OtpUser(otp.getUserId(),otpChannel, appId, useridtype,null,context);
				RequestWrapper<OtpUser> requestSendOtpKernel=new RequestWrapper<>();
				requestSendOtpKernel.setRequest(user);
				requestSendOtpKernel.setRequesttime(LocalDateTime.now());
				String url=sendOtpResourceUrl+"/authenticate/sendotp";
				ResponseEntity<String> responseEntity=(ResponseEntity<String>) loginCommonUtil.callAuthService(url,HttpMethod.POST,MediaType.APPLICATION_JSON,requestSendOtpKernel,null,String.class);
				log.info("sessionId", "idType","id","Kernel request body:\n " +requestSendOtpKernel.getRequest().toString()+"\n Kernel response: \n"+responseEntity.getBody());
				List<ServiceError> validationErrorList=ExceptionUtils.getServiceErrorList(responseEntity.getBody());
				if(!validationErrorList.isEmpty()) {
					throw new LoginServiceException(validationErrorList,response);
				}
				
				ResponseWrapper<?> responseKernel=loginCommonUtil.requestBodyExchange(responseEntity.getBody());
				AuthNResponse responseBody=(AuthNResponse) loginCommonUtil.requestBodyExchangeObject(loginCommonUtil.responseToString(responseKernel.getResponse()),AuthNResponse.class);
				response.setResponse(responseBody);
				}
			
		}
		catch(Exception ex) {
			log.error("sessionId", "idType", "id",
					"In callsendOtp method of login service- " + ex.getMessage());
			new LoginExceptionCatcher().handle(ex,"sendOtp",response);	
		}
		finally {
			response.setResponsetime(GenericUtil.getCurrentResponseTime());
		}
		return response;
	}
	
	
	/**
	 * It will validate userId & otp and provide with a access token 
	 * 
	 * @param userIdOtpRequest
	 * @return MainResponseDTO<AuthNResponse>
	 */
	public MainResponseDTO<ResponseEntity<String>> validateWithUserIdOtp(MainRequestDTO<User> userIdOtpRequest){
		log.info("sessionId", "idType", "id",
				"In calluserIdOtp method of login service ");
		MainResponseDTO<ResponseEntity<String>> response  = null;
		response  =	(MainResponseDTO<ResponseEntity<String>>) loginCommonUtil.getMainResponseDto(userIdOtpRequest);
		requiredRequestMap.put("id",userIdOtpId);
		String userid=null;
		boolean isSuccess = false;
		try {
			if(ValidationUtil.requestValidator(loginCommonUtil.createRequestMap(userIdOtpRequest), requiredRequestMap)/*authCommonUtil.validateRequest(userIdOtpRequest)*/) {
				User user=userIdOtpRequest.getRequest();
				userid=user.getUserId();
				loginCommonUtil.validateOtpAndUserid(user);
				UserOtp userOtp=new UserOtp(user.getUserId(), user.getOtp(), appId);
				RequestWrapper<UserOtp> requestSendOtpKernel=new RequestWrapper<>();
				requestSendOtpKernel.setRequest(userOtp);
				requestSendOtpKernel.setRequesttime(LocalDateTime.now());
				
				ResponseEntity<String> responseEntity = null;
				String url=sendOtpResourceUrl+"/authenticate/useridOTP";
				responseEntity=(ResponseEntity<String>) loginCommonUtil.callAuthService(url,HttpMethod.POST,MediaType.APPLICATION_JSON_UTF8,requestSendOtpKernel,null,String.class);
				log.info("sessionId", "idType","id","Kernel request body:\n " +requestSendOtpKernel.getRequest().toString()+"\n Kernel response: \n"+responseEntity.getBody());
				List<ServiceError> validationErrorList=null;
				validationErrorList=ExceptionUtils.getServiceErrorList(responseEntity.getBody());
				if(!validationErrorList.isEmpty()) {
					throw new LoginServiceException(validationErrorList,response);
				}
				ResponseWrapper<?> responseKernel=loginCommonUtil.requestBodyExchange(responseEntity.getBody());
				AuthNResponse responseBody=(AuthNResponse) loginCommonUtil.requestBodyExchangeObject(loginCommonUtil.responseToString(responseKernel.getResponse()),AuthNResponse.class);
				if(!responseBody.getStatus().equals(status)) {
					throw new InvalidOtpOrUseridException(ErrorCodes.PRG_AUTH_013.getCode(),responseBody.getMessage(), response);
				}
				if(responseEntity.getHeaders().get("Set-Cookie").isEmpty()) {
					throw new NoAuthTokenException(ErrorCodes.PRG_AUTH_014.getCode(), ErrorMessages.TOKEN_NOT_PRESENT.getMessage(), null);
				}
				
				response.setResponse(responseEntity);
			}
			isSuccess = true;
		}
		catch(Exception ex) {
			log.error("sessionId", "idType", "id",
					"In calluserIdOtp method of login service- " + ex.getMessage());
			new LoginExceptionCatcher().handle(ex,"userIdOtp",response);	
		}
		finally {
			response.setResponsetime(GenericUtil.getCurrentResponseTime());
		
				if (isSuccess) {
					setAuditValues(EventId.PRE_410.toString(), EventName.AUTHENTICATION.toString(), EventType.SYSTEM.toString(),
							" User sucessfully logedin    ",
							AuditLogVariables.NO_ID.toString(), userid,
							userid);
				} else {
					setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
							" User failed to logedin ", AuditLogVariables.NO_ID.toString(),
							userid, userid);
		}
			
		}
		return response;
	}
	
	/**
	 * This method will invalidate the access token
	 * 
	 * @param authHeader
	 * @return AuthNResponse
	 */

	public MainResponseDTO<AuthNResponse> invalidateToken(String authHeader){
		log.info("sessionId", "idType", "id",
				"In calluserIdOtp method of login service ");
		ResponseEntity<String> responseEntity = null;
		AuthNResponse authNResponse = null;
		MainResponseDTO<AuthNResponse> response  = new MainResponseDTO<>();
		response.setId(invalidateTokenId);
		response.setVersion(version);
		boolean isSuccess = false;
		String userId=null;
		try {
			
			Map<String,String> headersMap=new HashMap<>();
			headersMap.put("Cookie",authHeader);
			String url=sendOtpResourceUrl+"/authorize/invalidateToken";
			userId=loginCommonUtil.getUserDetailsFromToken(headersMap);
			responseEntity=(ResponseEntity<String>) loginCommonUtil.callAuthService(url,HttpMethod.POST,MediaType.APPLICATION_JSON,null,headersMap,String.class);
			log.info("sessionId", "idType","id","Kernel response: \n"+responseEntity.getBody());
			List<ServiceError> validationErrorList=null;
			validationErrorList=ExceptionUtils.getServiceErrorList(responseEntity.getBody());
			if(!validationErrorList.isEmpty()) {
				throw new LoginServiceException(validationErrorList,response);
			}
			ResponseWrapper<?> responseKernel=loginCommonUtil.requestBodyExchange(responseEntity.getBody());
			authNResponse = (AuthNResponse) loginCommonUtil.requestBodyExchangeObject(loginCommonUtil.responseToString(responseKernel.getResponse()), AuthNResponse.class);
			response.setResponse(authNResponse);
			isSuccess = true;
		}
		catch(Exception ex) {	
			log.error("sessionId", "idType", "id",
					"In call invalidateToken method of login service- " + ex.getMessage());
			new LoginExceptionCatcher().handle(ex,"invalidateToken",response);	
		}
		finally {
			response.setResponsetime(GenericUtil.getCurrentResponseTime());
			if (isSuccess) {
				setAuditValues(EventId.PRE_410.toString(), EventName.AUTHENTICATION.toString(), EventType.SYSTEM.toString(),
						"User sucessfully logedin ",
						AuditLogVariables.NO_ID.toString(),userId,
						userId);
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"User failed to logedin", AuditLogVariables.NO_ID.toString(),
						userId, userId);
	}
		
		}
		return response;
	}
	
	/**
	 * This method is used to audit all the Authentication events
	 * 
	 * @param eventId
	 * @param eventName
	 * @param eventType
	 * @param description
	 * @param idType
	 */
	public void setAuditValues(String eventId, String eventName, String eventType, String description, String idType,String userId,String userName) {
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setEventId(eventId);
		auditRequestDto.setEventName(eventName);
		auditRequestDto.setEventType(eventType);
		auditRequestDto.setDescription(description);
		auditRequestDto.setId(idType);
		auditRequestDto.setSessionUserId(userId);
		auditRequestDto.setSessionUserName(userName);
		auditRequestDto.setModuleId(AuditLogVariables.AUTHENTICATION.toString());
		auditRequestDto.setModuleName(AuditLogVariables.AUTHENTICATION_SERVICE.toString());
		auditLogUtil.saveAuditDetails(auditRequestDto);
	}
	
	/**
	 * This will return UI related configurations return
	 */
	public MainResponseDTO<Map<String, String>> getConfig() {
		log.info("sessionId", "idType", "id",
				"In login service of getConfig ");
		MainResponseDTO<Map<String, String>> res = new MainResponseDTO<>();
		res.setId(configId);
		res.setVersion(version);
		List<String> reqParams = new ArrayList<>();
		Map<String, String> configParams = new HashMap<>();
		try {
			String[] uiParams = uiConfigParams.split(",");
			for (int i = 0; i < uiParams.length; i++) {
				reqParams.add(uiParams[i]);
			}
			if (globalFileName != null && preRegFileName != null) {
				String globalParam = loginCommonUtil.getConfig(globalFileName);
				String preregParam = loginCommonUtil.getConfig(preRegFileName);
				Properties prop1 = loginCommonUtil.parsePropertiesString(globalParam);
				Properties prop2 = loginCommonUtil.parsePropertiesString(preregParam);
				loginCommonUtil.getConfigParams(prop1,configParams,reqParams);
				loginCommonUtil.getConfigParams(prop2,configParams,reqParams);
		
			} else {
				throw new ConfigFileNotFoundException(ErrorCodes.PRG_AUTH_012.getCode(),
						ErrorMessages.CONFIG_FILE_NOT_FOUND_EXCEPTION.getMessage(),res);
			}
			
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In login service of getConfig "+ex.getMessage());
			new LoginExceptionCatcher().handle(ex,"config",res);
		}
		res.setResponse(configParams);
		res.setResponsetime(GenericUtil.getCurrentResponseTime());
		return res;
	}
}
