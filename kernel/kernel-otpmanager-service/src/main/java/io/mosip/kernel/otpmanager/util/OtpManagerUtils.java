package io.mosip.kernel.otpmanager.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;

import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.otpmanager.constant.OtpErrorConstants;
import io.mosip.kernel.otpmanager.constant.OtpPropertyConstants;
import io.mosip.kernel.otpmanager.exception.Errors;
import io.mosip.kernel.otpmanager.exception.OtpInvalidArgumentExceptionHandler;

/**
 * This utility class defines some of the utility methods used in OTP
 * validation.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public class OtpManagerUtils {

	/**
	 * Private constructor for avoiding instantiation as this is an utility class.
	 */
	private OtpManagerUtils() {
	}

	/**
	 * This method returns the difference between two LocalDateTime objects in
	 * seconds.
	 * 
	 * @param fromDateTime
	 *            The time from which the difference needs to be calculated.
	 * @param toDateTime
	 *            The time till which the difference needs to be calculated.
	 * @return The difference in seconds.
	 */
	public static int timeDifferenceInSeconds(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
		return (int) fromDateTime.until(toDateTime, ChronoUnit.SECONDS);
	}

	/**
	 * This method returns the current LocalDateTime.
	 * 
	 * @return The current local date and time.
	 */
	public static LocalDateTime getCurrentLocalDateTime() {
		return LocalDateTime.now();
	}
	
	/**
	 * This method validates the input arguments provided for validation.
	 * 
	 * @param key The key.
	 * @param otp The OTP to be validated against the given key.
	 */
	public static void validateOtpRequestArguments(String key, String otp) {
		List<Errors> validationErrorsList = new ArrayList<>();
		if (key == null || key.isEmpty()) {
			validationErrorsList.add(new Errors(OtpErrorConstants.OTP_VAL_INVALID_KEY_INPUT.getErrorCode(),
					OtpErrorConstants.OTP_VAL_INVALID_KEY_INPUT.getErrorMessage()));
		} else {
			if ((key.length() < Integer.parseInt(OtpPropertyConstants.KEY_MIN_LENGTH.getProperty()))
					|| (key.length() > Integer.parseInt(OtpPropertyConstants.KEY_MAX_LENGTH.getProperty()))) {
				validationErrorsList.add(new Errors(OtpErrorConstants.OTP_VAL_ILLEGAL_KEY_INPUT.getErrorCode(),
						OtpErrorConstants.OTP_VAL_ILLEGAL_KEY_INPUT.getErrorMessage()));
			}
		}
		if (otp == null || otp.isEmpty()) {
			validationErrorsList.add(new Errors(OtpErrorConstants.OTP_VAL_INVALID_OTP_INPUT.getErrorCode(),
					OtpErrorConstants.OTP_VAL_INVALID_OTP_INPUT.getErrorMessage()));
		}
		if ((otp != null) && (!StringUtils.isNumeric(otp))) {
			validationErrorsList.add(new Errors(OtpErrorConstants.OTP_VAL_ILLEGAL_OTP_INPUT.getErrorCode(),
					OtpErrorConstants.OTP_VAL_ILLEGAL_OTP_INPUT.getErrorMessage()));
		}
		if (!validationErrorsList.isEmpty()) {
			throw new OtpInvalidArgumentExceptionHandler(validationErrorsList);
		}
	}
}

