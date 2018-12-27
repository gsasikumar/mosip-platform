/*
 * 
 */
package io.mosip.authentication.service.impl.indauth.facade;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.BioType;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.KycInfo;
import io.mosip.authentication.core.dto.indauth.KycResponseDTO;
import io.mosip.authentication.core.dto.indauth.KycType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.authentication.service.helper.AuditHelper;
import io.mosip.authentication.service.impl.indauth.builder.AuthResponseBuilder;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * This class provides the implementation of AuthFacade.
 *
 * @author Arun Bose
 * 
 * @author Prem Kumar
 */
@Service
public class AuthFacadeImpl implements AuthFacade {

	private static final String DEMO_AUTHENTICATION_REQUESTED = "Demo Authentication requested";

	private static final String OTP_AUTHENTICATION_REQUESTED = "OTP Authentication requested";

	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The Constant STATUS_SUCCESS. */
	private static final String STATUS_SUCCESS = "y";
	/** The Constant IDA. */
	private static final String IDA = "IDA";

	/** The Constant AUTH_FACADE. */
	private static final String AUTH_FACADE = "AuthFacade";

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";
	
	private static final String VER = "1.0";

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(AuthFacadeImpl.class);

	/** The otp service. */
	@Autowired
	private OTPAuthService otpService;

	/** The id auth service. */
	@Autowired
	private IdAuthService idAuthService;

	/** The Kyc Service */
	@Autowired
	private KycService kycService;
	/** The Environment */
	@Autowired
	private Environment env;
	/** The Id Info Service */
	@Autowired
	private IdRepoService idInfoService;
	/** The Demo Auth Service */
	@Autowired
	private DemoAuthService demoAuthService;

	@Autowired
	private AuditHelper auditHelper;

	@Autowired
	private BioAuthService bioAuthService;

	@Autowired
	private NotificationService notificationService;

	/**
	 * Process the authorization type and authorization response is returned.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param isAuth         boolean i.e is auth type request.
	 * @return AuthResponseDTO the auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception.
	 */
	@Override
	public AuthResponseDTO authenticateApplicant(AuthRequestDTO authRequestDTO, boolean isAuth)
			throws IdAuthenticationBusinessException {

		Map<String, Object> idResDTO = idAuthService.processIdType(authRequestDTO.getIdvIdType(),
				authRequestDTO.getIdvId());

		AuthResponseDTO authResponseDTO;
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder.newInstance(env.getProperty(DATETIME_PATTERN));
		Map<String, List<IdentityInfoDTO>> idInfo = null;
		//String refId = String.valueOf(idResDTO.get("registrationId"));
		String uin = String.valueOf(idResDTO.get("uin"));
		try {
			idInfo = getIdEntity(idResDTO);

			authResponseBuilder.setTxnID(authRequestDTO.getTxnID()).setIdType(authRequestDTO.getIdvIdType())
					.setReqTime(authRequestDTO.getReqTime()).setVersion(VER);

			List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, idInfo, uin, isAuth);
			authStatusList.forEach(authResponseBuilder::addAuthStatusInfo);
		} finally {
			authResponseDTO = authResponseBuilder.build();
			logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE,
					"authenticateApplicant status : " + authResponseDTO.getStatus());
			//String uin = String.valueOf(idResDTO.get("uin"));
			if (idInfo != null && uin != null) {
				notificationService.sendAuthNotification(authRequestDTO, uin, authResponseDTO, idInfo, isAuth);
			}
			
		}

		return authResponseDTO;

	}

	/**
	 * Process the authorisation type and corresponding authorisation service is
	 * called according to authorisation type. reference Id is returned in
	 * AuthRequestDTO.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param idInfo         list of identityInfoDto request
	 * @param refId          the ref id
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, String uin, boolean isAuth)
			throws IdAuthenticationBusinessException {
		
		String idvId = authRequestDTO.getIdvId();
		String idvIdType = authRequestDTO.getIdvIdType();
		String reqTime = authRequestDTO.getReqTime();
		String txnId = authRequestDTO.getTxnID();
		String status = null;
		String comment = null;
		
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		AuthStatusInfo statusInfo = null;
		IdType idType = null;

		if (idvIdType.equals(IdType.UIN.getType())) {
			idType = IdType.UIN;
		} else {
			idType = IdType.VID;
		}
		if (authRequestDTO.getAuthType().isOtp()) {
			AuthStatusInfo otpValidationStatus;
			try {

				otpValidationStatus = otpService.validateOtp(authRequestDTO, uin);
				authStatusList.add(otpValidationStatus);
				statusInfo = otpValidationStatus;
			} finally {
				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				status = isStatus ? "Y" : "N";
				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "OTP Authentication status : " + statusInfo);
				auditHelper.audit(AuditModules.OTP_AUTH, getAuditEvent(isAuth), idvIdType, idType,
						OTP_AUTHENTICATION_REQUESTED);
				
				comment = isStatus ? "OTP Authenticated Success" : "OTP Authenticated Failed";
				idAuthService.saveAutnTxn(idvId, idvIdType, reqTime, txnId, status, comment, RequestType.OTP_AUTH);
			}

		}

		if (authRequestDTO.getAuthType().isPersonalIdentity() || authRequestDTO.getAuthType().isAddress()
				|| authRequestDTO.getAuthType().isFullAddress()) {
			AuthStatusInfo demoValidationStatus;
			try {
				demoValidationStatus = demoAuthService.getDemoStatus(authRequestDTO, uin, idInfo);
				authStatusList.add(demoValidationStatus);
				statusInfo = demoValidationStatus;
			} finally {
				
				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				status = isStatus ? "Y" : "N";
				
				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "Demographic Authentication status : " + statusInfo);
				auditHelper.audit(AuditModules.DEMO_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(), idType,
						DEMO_AUTHENTICATION_REQUESTED);
				
				comment = isStatus ? "Demo  Authenticated Success" : "Demo  Authenticated Failed";
				idAuthService.saveAutnTxn(idvId, idvIdType, reqTime, txnId, status, comment, RequestType.DEMO_AUTH);
			}

		}
		if (authRequestDTO.getAuthType().isBio()) {
			AuthStatusInfo bioValidationStatus;
			try {

				bioValidationStatus = bioAuthService.validateBioDetails(authRequestDTO, idInfo);
				authStatusList.add(bioValidationStatus);
				statusInfo = bioValidationStatus;
			} finally {
				
				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				status = isStatus ? "Y" : "N";
				
				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "BioMetric Authentication status :" + statusInfo);
				processBioAuthType(authRequestDTO, isAuth, idType);
				
				comment = isStatus ? "Bio  Authenticated Success" : "Bio  Authenticated Failed";
				idAuthService.saveAutnTxn(idvId, idvIdType, reqTime, txnId, status, comment, RequestType.BIO_AUTH);
			}
		}

		return authStatusList;
	}

	/**
	 * Processed to authentic bio type request.
	 * 
	 * @param authRequestDTO authRequestDTO
	 * @param isAuth         boolean value for verify is auth type request or not.
	 * @param idType         idtype
	 * @throws IDDataValidationException exception
	 */
	private void processBioAuthType(AuthRequestDTO authRequestDTO, boolean isAuth, IdType idType)
			throws IDDataValidationException {
		String desc;
		if (authRequestDTO.getBioInfo().stream()
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioType.FGRMIN.getType())
						|| bioInfo.getBioType().equals(BioType.FGRIMG.getType()))) {
			desc = "Fingerprint Authentication requested";
			auditHelper.audit(AuditModules.BIO_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(), idType, desc);
		}
		if (authRequestDTO.getBioInfo().stream()
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioType.IRISIMG.getType()))) {
			desc = "Iris Authentication requested";
			auditHelper.audit(AuditModules.BIO_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(), idType, desc);
		}
		if (authRequestDTO.getBioInfo().stream()
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioType.FACEIMG.getType()))) {
			desc = "Face Authentication requested";
			auditHelper.audit(AuditModules.BIO_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(), idType, desc);
		}
	}

	private AuditEvents getAuditEvent(boolean isAuth) {
		return isAuth ? AuditEvents.AUTH_REQUEST_RESPONSE : AuditEvents.INTERNAL_REQUEST_RESPONSE;
	}

	@Override
	public AuthResponseDTO authenticateTsp(AuthRequestDTO authRequestDTO) {

		String dateTimePattern = env.getProperty(DATETIME_PATTERN);

		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);

		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(authRequestDTO.getReqTime(), isoPattern);
		ZoneId zone = zonedDateTime2.getZone();
		String resTime = DateUtils.formatDate(new Date(), dateTimePattern, TimeZone.getTimeZone(zone));
		AuthResponseDTO authResponseTspDto = new AuthResponseDTO();
		authResponseTspDto.setStatus(STATUS_SUCCESS);
		authResponseTspDto.setErr(Collections.emptyList());
		authResponseTspDto.setResTime(resTime);
		authResponseTspDto.setTxnID(authRequestDTO.getTxnID());
		return authResponseTspDto;
	}

	/**
	 * 
	 * Process the KycAuthRequestDTO to integrate with KycService
	 * 
	 * @param kycAuthRequestDTO is DTO of KycAuthRequestDTO
	 * 
	 */
	@Override
	public KycAuthResponseDTO processKycAuth(KycAuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO)
			throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequest = kycAuthRequestDTO.getAuthRequest();
		Map<String, Object> idResDTO = null;
		String key = null;
		String resTime = null;
		IdType idType = null;
		if (authRequest != null) {
			idResDTO = idAuthService.processIdType(authRequest.getIdvIdType(), authRequest.getIdvId());
			key = "ekyc.mua.accesslevel." + kycAuthRequestDTO.getAuthRequest().getMuaCode();

			if (kycAuthRequestDTO.getAuthRequest().getIdvIdType().equals(IdType.UIN.getType())) {
				idType = IdType.UIN;
			} else {
				idType = IdType.VID;
			}
			String dateTimePattern = env.getProperty(DATETIME_PATTERN);

			DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);

			ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(kycAuthRequestDTO.getAuthRequest().getReqTime(),
					isoPattern);
			ZoneId zone = zonedDateTime2.getZone();
			resTime = DateUtils.formatDate(new Date(), dateTimePattern, TimeZone.getTimeZone(zone));
			auditHelper.audit(AuditModules.EKYC_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
					kycAuthRequestDTO.getAuthRequest().getIdvId(), idType, "eKYC Authentication requested");
		}
		Map<String, List<IdentityInfoDTO>> idInfo = getIdEntity(idResDTO);
		KycInfo info = null;
		if (idResDTO != null) {
			info = kycService.retrieveKycInfo(String.valueOf(idResDTO.get("uin")),
					KycType.getEkycAuthType(env.getProperty(key)), kycAuthRequestDTO.isEPrintReq(),
					kycAuthRequestDTO.isSecLangReq(), idInfo);

		}

		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();

		KycResponseDTO response = new KycResponseDTO();
		response.setAuth(authResponseDTO);
		kycAuthResponseDTO.setResponse(response);
		kycAuthResponseDTO.getResponse().setKyc(info);
		kycAuthResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));

		kycAuthResponseDTO.setStatus(authResponseDTO.getStatus());

		kycAuthResponseDTO.setResTime(resTime);

		return kycAuthResponseDTO;
	}

	/**
	 * Gets the demo entity.
	 *
	 * @param uniqueId the unique id
	 * @return the demo entity
	 * @throws IdAuthenticationBusinessException
	 * @throws IdAuthenticationDaoException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public Map<String, List<IdentityInfoDTO>> getIdEntity(Map<String, Object> idResponseDTO)
			throws IdAuthenticationBusinessException {
		return idInfoService.getIdInfo(idResponseDTO);
	}

}
