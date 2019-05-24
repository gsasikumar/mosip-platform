package io.mosip.registration.service.login.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.AppAuthenticationDAO;
import io.mosip.registration.dao.RegistrationCenterDAO;
import io.mosip.registration.dao.ScreenAuthorizationDAO;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.operator.UserDetailService;
import io.mosip.registration.service.operator.UserSaltDetailsService;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.service.sync.PublicKeySync;

/**
 * Class for implementing login service
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Service
public class LoginServiceImpl implements LoginService {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(LoginServiceImpl.class);

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditManagerService auditFactory;

	/**
	 * Class to retrieve the Login Details from DB
	 */
	@Autowired
	private AppAuthenticationDAO appAuthenticationDAO;

	/**
	 * Class to retrieve the Officer Details from DB
	 */
	@Autowired
	private UserDetailDAO userDetailDAO;

	/**
	 * Class to retrieve the Registration Center details from DB
	 */
	@Autowired
	private RegistrationCenterDAO registrationCenterDAO;

	/**
	 * Class to retrieve the Screen authorization from DB
	 */
	@Autowired
	private ScreenAuthorizationDAO screenAuthorizationDAO;

	@Autowired
	private PublicKeySync publicKeySyncImpl;

	@Autowired
	private GlobalParamService globalParamService;

	@Autowired
	private MasterSyncService masterSyncService;

	@Autowired
	private UserDetailService userDetailService;

	@Autowired
	private UserSaltDetailsService userSaltDetailsService;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.login.LoginService#getModesOfLogin()
	 */
	@Override
	public List<String> getModesOfLogin(String authType, Set<String> roleList) {
		// Retrieve Login information

		LOGGER.info("REGISTRATION - LOGINMODES - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Fetching list of login modes");

		auditFactory.audit(AuditEvent.LOGIN_MODES_FETCH, Components.LOGIN_MODES, RegistrationConstants.APPLICATION_NAME,
				AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

		return appAuthenticationDAO.getModesOfLogin(authType, roleList);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.service.login.LoginService#getUserDetail(java.lang.
	 * String)
	 */
	@Override
	public UserDetail getUserDetail(String userId) {
		// Retrieving Officer details
		LOGGER.info("REGISTRATION - USERDETAIL - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Fetching User details");

		auditFactory.audit(AuditEvent.FETCH_USR_DET, Components.USER_DETAIL, RegistrationConstants.APPLICATION_NAME,
				AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

		return userDetailDAO.getUserDetail(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.login.LoginService#
	 * getRegistrationCenterDetails(java.lang.String)
	 */
	@Override
	public RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId,String langCode) {
		// Retrieving Registration Center details

		LOGGER.info("REGISTRATION - CENTERDETAILS - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Fetching Center details");

		auditFactory.audit(AuditEvent.FETCH_CNTR_DET, Components.CENTER_DETAIL, RegistrationConstants.APPLICATION_NAME,
				AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

		return registrationCenterDAO.getRegistrationCenterDetails(centerId,langCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.login.LoginService#
	 * getScreenAuthorizationDetails(java.lang.String)
	 */
	@Override
	public AuthorizationDTO getScreenAuthorizationDetails(List<String> roleCode) {
		// Fetching screen authorization details

		LOGGER.info("REGISTRATION - SCREENAUTHORIZATION - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Fetching list of Screens to be Authorized");

		auditFactory.audit(AuditEvent.FETCH_SCR_AUTH, Components.SCREEN_AUTH, RegistrationConstants.APPLICATION_NAME,
				AuditReferenceIdTypes.APPLICATION_ID.getReferenceTypeId());

		return screenAuthorizationDAO.getScreenAuthorizationDetails(roleCode);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.LoginService#updateLoginParams(io.mosip.
	 * registration.entity.UserDetail)
	 */
	public void updateLoginParams(UserDetail userDetail) {

		LOGGER.info("REGISTRATION - UPDATELOGINPARAMS - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Updating Login Params");

		userDetailDAO.updateLoginParams(userDetail);
		
		LOGGER.info("REGISTRATION - UPDATELOGINPARAMS - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Updated Login Params");

	}

	@Override
	public List<String> initialSync() {
		LOGGER.info("REGISTRATION  - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Started Initial sync");
		List<String> val = new LinkedList<>();
		ResponseDTO publicKeySyncResponse = publicKeySyncImpl
				.getPublicKey(RegistrationConstants.JOB_TRIGGER_POINT_USER);
		ResponseDTO responseDTO = globalParamService.synchConfigData(false);
		SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
		if (successResponseDTO != null && successResponseDTO.getOtherAttributes() != null) {
			val.add(RegistrationConstants.RESTART);
		}
		ResponseDTO masterResponseDTO = masterSyncService.getMasterSync(
				RegistrationConstants.OPT_TO_REG_MDS_J00001,
				RegistrationConstants.JOB_TRIGGER_POINT_USER);
		
		if (RegistrationConstants.ENABLE
				.equalsIgnoreCase(masterResponseDTO.getSuccessResponseDTO().getMessage())) {
		}

		ResponseDTO userResponseDTO = userDetailService
				.save(RegistrationConstants.JOB_TRIGGER_POINT_USER);

		ResponseDTO userSaltResponse = userSaltDetailsService
				.getUserSaltDetails(RegistrationConstants.JOB_TRIGGER_POINT_USER);

		if (((masterResponseDTO.getErrorResponseDTOs() != null
				|| userResponseDTO.getErrorResponseDTOs() != null
				|| userSaltResponse.getErrorResponseDTOs() != null)
				|| responseDTO.getErrorResponseDTOs() != null
				|| publicKeySyncResponse.getErrorResponseDTOs() != null)) {
			val.add(RegistrationConstants.FAILURE);
		} else {
			val.add(RegistrationConstants.SUCCESS);
		}
		
		LOGGER.info("REGISTRATION  - LOGINSERVICE", APPLICATION_NAME, APPLICATION_ID,
				"completed Initial sync");
	
		return val;

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.LoginService#validateInvalidLogin(io.mosip.
	 * registration.entity.UserDetail,java.lang.String,integer,integer)
	 */
	public String validateInvalidLogin(UserDetail userDetail, String errorMessage, int invalidLoginCount, int invalidLoginTime) {
		
		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "validating invalid login params");

		int loginCount = userDetail.getUnsuccessfulLoginCount() != null
				? userDetail.getUnsuccessfulLoginCount().intValue()
				: RegistrationConstants.PARAM_ZERO;

		Timestamp loginTime = userDetail.getUserlockTillDtimes();

		if (validateLoginTime(loginCount, invalidLoginCount, loginTime, invalidLoginTime)) {

			loginCount = RegistrationConstants.PARAM_ZERO;
			userDetail.setUnsuccessfulLoginCount(RegistrationConstants.PARAM_ZERO);

			updateLoginParams(userDetail);

		}

		if (loginCount >= invalidLoginCount) {

			LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
					"validating login count and time ");

			if (TimeUnit.MILLISECONDS.toMinutes(loginTime.getTime() - System.currentTimeMillis()) > invalidLoginTime) {

				userDetail.setUnsuccessfulLoginCount(RegistrationConstants.PARAM_ONE);

				updateLoginParams(userDetail);

			} else {
				return RegistrationConstants.ERROR;
			}
			return "false";

		} else {
			if (!errorMessage.isEmpty()) {

				LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
						"updating login count and time for invalid login attempts");
				loginCount = loginCount + RegistrationConstants.PARAM_ONE;
				userDetail.setUserlockTillDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
				userDetail.setUnsuccessfulLoginCount(loginCount);

				updateLoginParams(userDetail);

				if (loginCount >= invalidLoginCount) {					
					return RegistrationConstants.ERROR;
				} else {
					return errorMessage;
				}
			}
			return "true";
		}
	}

	/**
	 * Validating login time and count
	 * 
	 * @param loginCount
	 *            number of invalid attempts
	 * @param invalidLoginCount
	 *            count from global param
	 * @param loginTime
	 *            login time from table
	 * @param invalidLoginTime
	 *            login time from global param
	 * @return boolean
	 */
	private boolean validateLoginTime(int loginCount, int invalidLoginCount, Timestamp loginTime,
			int invalidLoginTime) {

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
				"Comparing timestamps in case of invalid login attempts");

		return (loginCount >= invalidLoginCount
				&& TimeUnit.MILLISECONDS.toMinutes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()).getTime()
						- loginTime.getTime()) > invalidLoginTime);
	}
}
