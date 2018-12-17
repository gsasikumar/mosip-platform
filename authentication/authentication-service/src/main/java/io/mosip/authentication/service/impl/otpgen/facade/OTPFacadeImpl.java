package io.mosip.authentication.service.impl.otpgen.facade;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.authentication.core.spi.otpgen.facade.OTPFacade;
import io.mosip.authentication.core.spi.otpgen.service.OTPService;
import io.mosip.authentication.core.util.MaskUtil;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.integration.NotificationManager;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * Facade implementation of OTPfacade to generate OTP.
 * 
 * @author Rakesh Roshan
 */
@Service
public class OTPFacadeImpl implements OTPFacade {

	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SessionID";

	/** The otp service. */
	@Autowired
	private OTPService otpService;

	/** The id auth service. */
	@Autowired
	private IdAuthService idAuthService;

	/** The autntxnrepository. */
	@Autowired
	private AutnTxnRepository autntxnrepository;

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private DateHelper dateHelper;

	@Autowired
	NotificationManager notificationManager;

	@Autowired
	private IdInfoHelper demoHelper;

	@Autowired
	IdRepoService idInfoService;

	@Autowired
	private NotificationService notificationService;
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OTPFacadeImpl.class);

	/**
	 * Generate OTP, store the OTP request details for success/failure. And send OTP
	 * notification by sms(on mobile)/mail(on email-id).
	 *
	 * @param otpRequestDto the otp request dto
	 * @return otpResponseDTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Override
	public OtpResponseDTO generateOtp(OtpRequestDTO otpRequestDto) throws IdAuthenticationBusinessException {
		String otpKey = null;
		String otp = null;
		String mobileNumber = null;
		String email = null;
		String comment = null;
		String status = null;

		Map<String, Object> idResDTO = idAuthService.processIdType(otpRequestDto.getIdvIdType(),
				otpRequestDto.getIdvId());
		String productid = env.getProperty("application.id");
		String txnID = otpRequestDto.getTxnID();

		String refId = String.valueOf(idResDTO.get("registrationId"));
		if (isOtpFlooded(otpRequestDto)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED);
		} else {
			otpKey = OTPUtil.generateKey(productid, refId, txnID, otpRequestDto.getMuaCode());
			try {
				otp = otpService.generateOtp(otpKey);
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error("", otpRequestDto.getIdvIdType(), e.getErrorCode(), "Error: " + e);
			}
		}
		mosipLogger.info(SESSION_ID, "NA", "generated OTP", otp);

		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		if (otp == null || otp.trim().isEmpty()) {
			status = "N";
			comment = "OTP_GENERATION_FAILED";
			saveAutnTxn(otpRequestDto, status, comment, refId);
			mosipLogger.error("SessionId", "NA", "NA", "OTP Generation failed");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
		} else {
			mosipLogger.info("NA", "NA", "NA", "generated OTP is: " + otp);
			otpResponseDTO.setStatus("Y");
			otpResponseDTO.setErr(Collections.emptyList());
			otpResponseDTO.setTxnId(txnID);
			status = "Y";
			comment = "OTP_GENERATED";


			Map<String, List<IdentityInfoDTO>> idInfo = idInfoService.getIdInfo(idResDTO);
			
			mobileNumber = getMobileNumber(idInfo);
			email = getEmail(idInfo);
			
			String responseTime = formatDate(new Date(), env.getProperty(DATETIME_PATTERN));
			otpResponseDTO.setResTime(responseTime);

			if(email != null && email.length() > 0) {
				otpResponseDTO.setMaskedEmail(MaskUtil.maskEmail(email));
			}
			
			if(mobileNumber != null && mobileNumber.length() > 0) {
				otpResponseDTO.setMaskedMobile(MaskUtil.maskMobile(mobileNumber));
			}
			
			// -- send otp notification --
			String uin = String.valueOf(idResDTO.get("uin"));
			notificationService.sendOtpNotification(otpRequestDto, otp,uin, email, mobileNumber, idInfo);
			saveAutnTxn(otpRequestDto, status, comment, refId);

		}
		return otpResponseDTO;

	}

	/**
	 * Validate the number of request for OTP generation. Limit for the number of
	 * request for OTP is should not exceed 3 in 60sec.
	 *
	 * @param otpRequestDto the otp request dto
	 * @return true, if is otp flooded
	 * @throws IDDataValidationException
	 * @throws IdAuthenticationBusinessException
	 */
	private boolean isOtpFlooded(OtpRequestDTO otpRequestDto) throws IDDataValidationException {
		boolean isOtpFlooded = false;
		String uniqueID = otpRequestDto.getIdvId();
		Date requestTime = dateHelper.convertStringToDate(otpRequestDto.getReqTime());
		Date addMinutesInOtpRequestDTime = addMinutes(requestTime, -1);

		if (autntxnrepository.countRequestDTime(requestTime, addMinutesInOtpRequestDTime, uniqueID) > 3) {
			isOtpFlooded = true;
		}

		return isOtpFlooded;
	}

	/**
	 * Adds a number of minutes(positive/negative) to a date returning a new Date
	 * object. Add positive, date increase in minutes. Add negative, date reduce in
	 * minutes.
	 *
	 * @param date   the date
	 * @param minute the minute
	 * @return the date
	 */
	private Date addMinutes(Date date, int minute) {
		return DateUtils.addMinutes(date, minute);
	}

	/**
	 * Formate date.
	 *
	 * @param date   the date
	 * @param format the formate
	 * @return the date
	 */
	private String formatDate(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * Save the input Request to trigger OTP.
	 *
	 * @param otpRequestDto the otp request dto
	 * @param refId
	 * @throws IDDataValidationException
	 */
	private void saveAutnTxn(OtpRequestDTO otpRequestDto, String status, String comment, String refId)
			throws IDDataValidationException {
		String txnID = otpRequestDto.getTxnID();

		AutnTxn autnTxn = new AutnTxn();
		autnTxn.setRefId(refId);
		autnTxn.setRefIdType(otpRequestDto.getIdvIdType());

		autnTxn.setId(String.valueOf(new Date().getTime())); // FIXME

		// TODO check
		autnTxn.setCrBy("OTP Generate Service");
		autnTxn.setCrDTimes(new Date());
		// FIXME utilize Instant
		autnTxn.setRequestDTtimes(dateHelper.convertStringToDate(otpRequestDto.getReqTime()));
		autnTxn.setResponseDTimes(new Date()); // TODO check this
		autnTxn.setAuthTypeCode(RequestType.OTP_REQUEST.getRequestType());
		autnTxn.setRequestTrnId(txnID);
		autnTxn.setStatusCode(status);
		autnTxn.setStatusComment(comment);
		// FIXME
		autnTxn.setLangCode(env.getProperty("mosip.primary.lang-code"));

		autntxnrepository.saveAndFlush(autnTxn);
	}

	private String getEmail(Map<String, List<IdentityInfoDTO>> idInfo) {
		return demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo);
	}

	private String getMobileNumber(Map<String, List<IdentityInfoDTO>> idInfo) {
		return demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo);
	}

}
