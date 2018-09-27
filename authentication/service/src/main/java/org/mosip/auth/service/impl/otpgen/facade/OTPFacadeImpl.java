package org.mosip.auth.service.impl.otpgen.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.constant.RequestType;
import org.mosip.auth.core.dto.indauth.AuthError;
import org.mosip.auth.core.dto.indauth.IdType;
import org.mosip.auth.core.dto.otpgen.OtpRequestDTO;
import org.mosip.auth.core.dto.otpgen.OtpResponseDTO;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.spi.idauth.service.IdAuthService;
import org.mosip.auth.core.spi.otpgen.facade.OTPFacade;
import org.mosip.auth.core.spi.otpgen.service.OTPService;
import org.mosip.auth.core.util.OTPUtil;
import org.mosip.auth.service.dao.AutnTxnRepository;
import org.mosip.auth.service.entity.AutnTxn;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.core.utils.DateUtil;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Facade implementation of OTPfacade to generate OTP.
 * 
 * @author Rakesh Roshan
 */
@Service
@PropertySource("classpath:application-local.properties")
public class OTPFacadeImpl implements OTPFacade {

	@Autowired
	OTPService otpService;

	@Autowired
	IdAuthService idAuthService;

	@Autowired
	AutnTxnRepository autntxnrepository;

	@Autowired
	private Environment env;

	private MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());

	}

	/**
	 * Obtained OTP.
	 * 
	 * @param otpRequestDto
	 * @return otpResponseDTO
	 * @throws IdAuthenticationBusinessException
	 */
	@Override
	public OtpResponseDTO generateOtp(OtpRequestDTO otpRequestDto) throws IdAuthenticationBusinessException {

		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		String otpKey = null;
		String otp = null;

		IdType idType = otpRequestDto.getIdType();
		String refId = getRefId(otpRequestDto);
		String productid = env.getProperty("application.id");
		String txnID = otpRequestDto.getTxnID();
		String auaCode = otpRequestDto.getAuaCode();

		if (isOtpFlooded(otpRequestDto)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_FLOODING_ERROR);
		} else {
			otpKey = OTPUtil.generateKey(productid, refId, txnID, auaCode);

			try {
				otp = otpService.generateOtp(otpKey);
			} catch (IdAuthenticationBusinessException e) {
				LOGGER.error("", idType.getType(), e.getErrorCode(), e.getErrorText());
			}
			if (otp == null || otp.trim().isEmpty()) {
				LOGGER.error("NA", "NA", "NA", "generated OTP is: " + otp);
				otpResponseDTO.setStatus("N");
				otpResponseDTO.setErrorCode(getAuthErrors());
				otpResponseDTO.setTxnID(txnID);
				otpResponseDTO.setResponseTime(new Date());
			} else {
				LOGGER.info("NA", "NA", "NA", "generated OTP is: " + otp);
				otpResponseDTO.setStatus("Y");
				otpResponseDTO.setTxnID(txnID);
				otpResponseDTO.setResponseTime(new Date());
				saveAutnTxn(otpRequestDto);
			}
		}
		return otpResponseDTO;
	}

	/**
	 * Validate the number of request for OTP generation. Limit for the number of
	 * request for OTP is should not exceed 3 in 60sec.
	 * 
	 * @param otpRequestDto
	 * @return
	 */
	private boolean isOtpFlooded(OtpRequestDTO otpRequestDto) {

		boolean isOtpFlooded = false;
		String uniqueID = otpRequestDto.getUniqueID();
		Date requestTime = otpRequestDto.getReqTime();
		Date addMinutesInOtpRequestDTime = addMinites(requestTime, -1);

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
	 * @param date
	 * @param minute
	 * @return
	 */
	private Date addMinites(Date date, int minute) {
		 return DateUtil.addMinutes(date, minute);
	}

	/**
	 * Save the input Request to trigger OTP.
	 * 
	 * @param otpRequestDto
	 */
	private void saveAutnTxn(OtpRequestDTO otpRequestDto) {
		String uniqueID = otpRequestDto.getUniqueID();
		String txnID = otpRequestDto.getTxnID();

		AutnTxn autnTxn = new AutnTxn();
		autnTxn.setUin(uniqueID);
		autnTxn.setId(String.valueOf(new Date().getTime())); // TODO check this

		// TODO check
		autnTxn.setCrBy("OTP Generate Service");
		autnTxn.setCrDTimes(new Date());
		autnTxn.setRequestDTtimes(otpRequestDto.getReqTime());
		autnTxn.setResponseDTimes(new Date()); // TODO check this
		autnTxn.setAuthTypeCode(RequestType.OTP_REQUEST.getRequestType());
		autnTxn.setRequestTxnId(txnID);
		autnTxn.setIsActive("y");
		autnTxn.setStatusCode("OTP_GENERATED"); // TODO
		autnTxn.setStatusComment("OTP_GENERATED"); // TODO

		autntxnrepository.saveAndFlush(autnTxn);
	}

	/**
	 * Obtain the reference id for IDType.
	 * 
	 * @param otpRequestDto
	 * @return
	 */
	private String getRefId(OtpRequestDTO otpRequestDto) {
		String refId = null;
		IdType idType = otpRequestDto.getIdType();
		String uniqueID = otpRequestDto.getUniqueID();
		try {
			if (idType.equals(IdType.UIN)) {
				refId = idAuthService.validateUIN(uniqueID);
			} else if (otpRequestDto.getIdType().equals(IdType.VID)) {
				refId = idAuthService.validateVID(uniqueID);
			}
			LOGGER.info("NA", idType.getType(), "NA", " reference id of ID Type " + idType.getType() + refId);
		} catch (IdAuthenticationBusinessException e) {
			LOGGER.error("", idType.getType(), e.getErrorCode(), e.getErrorText());
		}
		return refId;
	}

	/**
	 * Authentication Errors.
	 * 
	 * @return List<AuthError>
	 */
	private List<AuthError> getAuthErrors() {
		List<AuthError> authErrors = new ArrayList<AuthError>();
		AuthError authError = new AuthError();
		authError.setErrorCode(IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED.getErrorCode());
		authError.setErrorMessage(IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED.getErrorMessage());
		authErrors.add(authError);

		return authErrors;
	}
}
